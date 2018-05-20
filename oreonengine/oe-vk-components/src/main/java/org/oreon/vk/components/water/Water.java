package org.oreon.vk.components.water;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_ALL_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_GEOMETRY_BIT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.common.water.WaterConfiguration;
import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Quaternion;
import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.RenderList;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.scenegraph.Scenegraph;
import org.oreon.core.target.Attachment;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.MeshGenerator;
import org.oreon.core.vk.buffer.VkBuffer;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;
import org.oreon.core.vk.scenegraph.VkMeshData;
import org.oreon.core.vk.scenegraph.VkRenderInfo;
import org.oreon.core.vk.synchronization.Fence;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.command.PrimaryCmdBuffer;
import org.oreon.core.vk.wrapper.command.SecondaryDrawCmdBuffer;
import org.oreon.core.vk.wrapper.image.Image2DLocal;
import org.oreon.core.vk.wrapper.image.VkImageHelper;
import org.oreon.core.vk.wrapper.pipeline.GraphicsTessellationPipeline;
import org.oreon.vk.components.fft.FFT;
import org.oreon.vk.components.util.NormalRenderer;

import lombok.Getter;

public class Water extends Renderable{

	private VkBuffer vertexBufferObject;
	private VkPipeline graphicsPipeline;
	private NormalRenderer normalRenderer;
	private VkUniformBuffer uniformBuffer;
	
	@Getter
	private WaterConfiguration waterConfiguration;

	private FFT fft;
	private Quaternion clipplane;
	private float clip_offset;
	private float motion;
	private float distortion;
	private VkImage image_dudv;
	private VkImageView imageView_dudv;
	
	private VkSampler dxSampler;
	private VkSampler dySampler;
	private VkSampler dzSampler;
	private VkSampler dudvSampler;
	private VkSampler normalSampler;
	private VkSampler reflectionSampler;
	private VkSampler refractionSampler;
	private Fence fence;
	
	// Reflection/Refraction Resources
	private VkFrameBufferObject offScreenReflecRefracFbo;
	
	// Reflection Resources
	private RenderList offScreenReflectionRenderList;
	private LinkedHashMap<String, CommandBuffer> reflectionSecondaryCmdBuffers;
	private PrimaryCmdBuffer offscreenReflectionCmdBuffer;
	private SubmitInfo offScreenReflectionSubmitInfo;
	private VkPipeline deferredReflectionPipeline;
	private CommandBuffer deferredReflectionCmdBuffer;
	private SubmitInfo deferredReflectionSubmitInfo;
	private VkImage deferredReflectionImage;
	private VkImageView deferredReflectionImageView;
	
	// Refraction Resources
	private RenderList offScreenRefractionRenderList;
	private LinkedHashMap<String, CommandBuffer> refractionSecondaryCmdBuffers;
	private PrimaryCmdBuffer offscreenRefractionCmdBuffer;
	private SubmitInfo offScreenRefractionSubmitInfo;
	private VkPipeline deferredRefractionPipeline;
	private CommandBuffer deferredRefractionCmdBuffer;
	private SubmitInfo deferredRefractionSubmitInfo;
	private VkImage deferredRefractionImage;
	private VkImageView deferredRefractionImageView;
	
	public Water() {
		
		VkDevice device = VkContext.getLogicalDevice().getHandle();
		VkPhysicalDeviceMemoryProperties memoryProperties =
				VkContext.getPhysicalDevice().getMemoryProperties();
		offScreenReflectionRenderList = new RenderList();
		offScreenRefractionRenderList = new RenderList();
		reflectionSecondaryCmdBuffers = new LinkedHashMap<String, CommandBuffer>();
		refractionSecondaryCmdBuffers = new LinkedHashMap<String, CommandBuffer>();
		offScreenReflecRefracFbo = VkContext.getRenderState().getOffScreenReflectionFbo();
		
		getWorldTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getWorldTransform().setTranslation(-Constants.ZFAR/2,0,-Constants.ZFAR/2);
		
		clip_offset = 4;
		clipplane = new Quaternion(0,-1,0,getWorldTransform().getTranslation().getY() + clip_offset);
		
		waterConfiguration = new WaterConfiguration();
		waterConfiguration.loadFile("water-config.properties");
		
		image_dudv = VkImageHelper.createSampledImageFromFile(
				device,
				memoryProperties,
				VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
				VkContext.getLogicalDevice().getTransferQueue(),
				"textures/water/dudv/dudv1.jpg");
		
		imageView_dudv = new VkImageView(device,
				VK_FORMAT_R8G8B8A8_UNORM, image_dudv.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		deferredReflectionImage = new Image2DLocal(device, memoryProperties,
				offScreenReflecRefracFbo.getWidth(), offScreenReflecRefracFbo.getHeight(),
				VK_FORMAT_R8G8B8A8_UNORM,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		deferredReflectionImageView = new VkImageView(device,
				VK_FORMAT_R8G8B8A8_UNORM, deferredReflectionImage.getHandle(),
				VK_IMAGE_ASPECT_COLOR_BIT);
		
		deferredRefractionImage = new Image2DLocal(device, memoryProperties,
				offScreenReflecRefracFbo.getWidth(), offScreenReflecRefracFbo.getHeight(),
				VK_FORMAT_R8G8B8A8_UNORM,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		deferredRefractionImageView = new VkImageView(device,
				VK_FORMAT_R8G8B8A8_UNORM, deferredRefractionImage.getHandle(),
				VK_IMAGE_ASPECT_COLOR_BIT);
		
		dxSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    dySampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    dzSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    dudvSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    normalSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    reflectionSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    refractionSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
		
		fft = new FFT(device,
				memoryProperties,
				waterConfiguration.getN(), waterConfiguration.getL(),
				waterConfiguration.getAmplitude(), waterConfiguration.getWindDirection(),
				waterConfiguration.getWindSpeed(), waterConfiguration.getCapillarWavesSupression());
		
		normalRenderer = new NormalRenderer(device,
				memoryProperties,
				waterConfiguration.getN(), waterConfiguration.getNormalStrength(),
				fft.getDyImageView(), dySampler);
		
		ShaderPipeline shaderPipeline = new ShaderPipeline(device);
	    shaderPipeline.createVertexShader("shaders/water/water.vert.spv");
	    shaderPipeline.createTessellationControlShader("shaders/water/water.tesc.spv");
	    shaderPipeline.createTessellationEvaluationShader("shaders/water/water.tese.spv");
	    shaderPipeline.createGeometryShader("shaders/water/water.geom.spv");
	    shaderPipeline.createFragmentShader("shaders/water/water.frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    ByteBuffer ubo = memAlloc(Float.BYTES * 2);
		ubo.putFloat(0);
		ubo.putFloat(0);
		ubo.flip();
	    
	    uniformBuffer = new VkUniformBuffer(device, memoryProperties, ubo);
	    
	    DescriptorSetLayout descriptorSetLayout = new DescriptorSetLayout(device,8);
	    descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_GEOMETRY_BIT);
	    descriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_GEOMETRY_BIT);
	    descriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_GEOMETRY_BIT);
	    descriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorSetLayout.addLayoutBinding(4, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorSetLayout.addLayoutBinding(5, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorSetLayout.addLayoutBinding(6, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorSetLayout.addLayoutBinding(7, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
				VK_SHADER_STAGE_FRAGMENT_BIT | VK_SHADER_STAGE_GEOMETRY_BIT);
	    descriptorSetLayout.create();
	    
	    DescriptorSet descriptorSet = new DescriptorSet(device,
	    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		descriptorSetLayout.getHandlePointer());
	    descriptorSet.updateDescriptorImageBuffer(
	    		fft.getDyImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dySampler.getHandle(), 0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
	    		fft.getDxImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dxSampler.getHandle(), 1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
	    		fft.getDzImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dzSampler.getHandle(), 2, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
	    		imageView_dudv.getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dudvSampler.getHandle(), 3, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
	    		normalRenderer.getNormalImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		normalSampler.getHandle(), 4, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
	    		deferredReflectionImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		reflectionSampler.getHandle(), 5, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
	    		deferredRefractionImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		refractionSampler.getHandle(), 6, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorBuffer(uniformBuffer.getHandle(),
	    		ubo.limit(), 0, 7, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
	    
	    List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(VkContext.getCamera().getDescriptor().getSet());
		descriptorSets.add(descriptorSet);
		descriptorSetLayouts.add(VkContext.getCamera().getDescriptor().getLayout());
		descriptorSetLayouts.add(descriptorSetLayout);
	    
	    VkVertexInput vertexInput = new VkVertexInput(VertexLayout.POS2D);
		
	    Vec2f[] vertices = MeshGenerator.generatePatch2D4x4(64);
	    
		ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(vertices);
		
		vertexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
				device, memoryProperties,
				VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
				VkContext.getLogicalDevice().getTransferQueue(),
				vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
		
		int pushConstantsRange = Float.BYTES * 26 + Integer.BYTES * 5;
		
		ByteBuffer pushConstants = memAlloc(pushConstantsRange);
		pushConstants.put(BufferUtil.createByteBuffer(getWorldTransform().getWorldMatrix()));
		pushConstants.putFloat(waterConfiguration.getWindDirection().getX());
		pushConstants.putFloat(waterConfiguration.getWindDirection().getY());
		pushConstants.putFloat(waterConfiguration.getTessellationSlope());
		pushConstants.putFloat(waterConfiguration.getTessellationShift());
		pushConstants.putInt(waterConfiguration.getTessellationFactor());
		pushConstants.putInt(waterConfiguration.getUvScale());
		pushConstants.putFloat(waterConfiguration.getDisplacementScale());
		pushConstants.putFloat(waterConfiguration.getChoppiness());
		pushConstants.putInt(waterConfiguration.getHighDetailRange());
		pushConstants.putFloat(waterConfiguration.getKReflection());
		pushConstants.putFloat(waterConfiguration.getKRefraction());
		pushConstants.putInt(EngineContext.getConfig().getWindowWidth());
		pushConstants.putInt(EngineContext.getConfig().getWindowHeight());
		pushConstants.putFloat(waterConfiguration.getEmission());
		pushConstants.putFloat(waterConfiguration.getSpecular());
		pushConstants.flip();
		
		graphicsPipeline = new GraphicsTessellationPipeline(device,
				shaderPipeline, vertexInput, VkUtil.createLongBuffer(descriptorSetLayouts),
				EngineContext.getConfig().getX_ScreenResolution(),
				EngineContext.getConfig().getY_ScreenResolution(),
				VkContext.getRenderState().getOffScreenFbo().getRenderPass().getHandle(),
				pushConstantsRange, VK_SHADER_STAGE_ALL_GRAPHICS,
				16);
		
		CommandBuffer commandBuffer = new SecondaryDrawCmdBuffer(
	    		device,
	    		VkContext.getLogicalDevice().getGraphicsCommandPool().getHandle(), 
	    		graphicsPipeline.getHandle(), graphicsPipeline.getLayoutHandle(),
	    		VkContext.getRenderState().getOffScreenFbo().getFrameBuffer().getHandle(),
	    		VkContext.getRenderState().getOffScreenFbo().getRenderPass().getHandle(),
	    		0,
	    		VkUtil.createLongArray(descriptorSets),
	    		vertexBufferObject.getHandle(),
	    		vertices.length,
	    		pushConstants, VK_SHADER_STAGE_ALL_GRAPHICS);
	    
	    VkMeshData meshData = new VkMeshData(vertexBufferObject, vertexBuffer,
	    		null, null);
	    VkRenderInfo mainRenderInfo = new VkRenderInfo(commandBuffer);
	    
	    addComponent(NodeComponentType.MESH_DATA, meshData);
	    addComponent(NodeComponentType.MAIN_RENDERINFO, mainRenderInfo);
	    
	    createReflectionRefractionResources(device, memoryProperties);
	}
	
	public void createReflectionRefractionResources(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties){
		
		List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		DescriptorSetLayout descriptorSetLayout = new DescriptorSetLayout(device,3);
		descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
				VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
				VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
				VK_SHADER_STAGE_COMPUTE_BIT);
	    descriptorSetLayout.create();
	    
	    DescriptorSet descriptorSetReflection = new DescriptorSet(device,
	    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		descriptorSetLayout.getHandlePointer());
	    descriptorSetReflection.updateDescriptorImageBuffer(
	    		deferredReflectionImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
	    descriptorSetReflection.updateDescriptorImageBuffer(
	    		offScreenReflecRefracFbo.getAttachmentImageView(Attachment.ALBEDO).getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
	    descriptorSetReflection.updateDescriptorImageBuffer(
	    		offScreenReflecRefracFbo.getAttachmentImageView(Attachment.NORMAL).getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets.add(VkContext.getCamera().getDescriptor().getSet());
		descriptorSets.add(descriptorSetReflection);
		descriptorSetLayouts.add(VkContext.getCamera().getDescriptor().getLayout());
		descriptorSetLayouts.add(descriptorSetLayout);
		
		DescriptorSet descriptorSetRefraction = new DescriptorSet(device,
	    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		descriptorSetLayout.getHandlePointer());
		descriptorSetRefraction.updateDescriptorImageBuffer(
				deferredRefractionImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		descriptorSetRefraction.updateDescriptorImageBuffer(
				offScreenReflecRefracFbo.getAttachmentImageView(Attachment.ALBEDO).getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		descriptorSetRefraction.updateDescriptorImageBuffer(
				offScreenReflecRefracFbo.getAttachmentImageView(Attachment.NORMAL).getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);

		deferredReflectionPipeline = new VkPipeline(device);
		deferredReflectionPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		deferredReflectionPipeline.createComputePipeline(
				new ShaderModule(device, "shaders/water/waterDeferredReflecRefrac.comp.spv",
						VK_SHADER_STAGE_COMPUTE_BIT));
		
		deferredReflectionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				deferredReflectionPipeline.getHandle(),
				deferredReflectionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets),
				offScreenReflecRefracFbo.getWidth()/16, offScreenReflecRefracFbo.getHeight()/16, 1);
		
		deferredRefractionPipeline = new VkPipeline(device);
		descriptorSets.set(1, descriptorSetRefraction);
		deferredRefractionPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		deferredRefractionPipeline.createComputePipeline(
				new ShaderModule(device, "shaders/water/waterDeferredReflecRefrac.comp.spv",
						VK_SHADER_STAGE_COMPUTE_BIT));
		
		descriptorSets.set(1, descriptorSetRefraction);
		
		deferredRefractionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				deferredRefractionPipeline.getHandle(),
				deferredRefractionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets),
				offScreenReflecRefracFbo.getWidth()/16, offScreenReflecRefracFbo.getHeight()/16, 1);
		
		offscreenReflectionCmdBuffer = new PrimaryCmdBuffer(device, 
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle());
		fence = new Fence(device);
		
		offscreenRefractionCmdBuffer = new PrimaryCmdBuffer(device, 
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle());
		fence = new Fence(device);
		
		offScreenReflectionSubmitInfo = new SubmitInfo();
		offScreenReflectionSubmitInfo.setCommandBuffers(
				offscreenReflectionCmdBuffer.getHandlePointer());
		offScreenReflectionSubmitInfo.setFence(fence);
		
		offScreenRefractionSubmitInfo = new SubmitInfo();
		offScreenRefractionSubmitInfo.setCommandBuffers(
				offscreenRefractionCmdBuffer.getHandlePointer());
		offScreenRefractionSubmitInfo.setFence(fence);
		
		deferredReflectionSubmitInfo = new SubmitInfo();
		deferredReflectionSubmitInfo.setCommandBuffers(deferredReflectionCmdBuffer.getHandlePointer());
		deferredReflectionSubmitInfo.setFence(fence);
		
		deferredRefractionSubmitInfo = new SubmitInfo();
		deferredRefractionSubmitInfo.setCommandBuffers(deferredRefractionCmdBuffer.getHandlePointer());
		deferredRefractionSubmitInfo.setFence(fence);
	}
	
	public void render(){
		
		fft.render();
		normalRenderer.render();
		
		// render reflection
		EngineContext.getRenderState().setClipplane(clipplane);
		
		// mirror scene to clipplane
		Scenegraph sceneGraph = getParentObject();
		sceneGraph.getWorldTransform().setScaling(1,-1,1);
		sceneGraph.update();
		sceneGraph.getRoot().record(offScreenReflectionRenderList);
		
		for (String key : offScreenReflectionRenderList.getKeySet()) {
			
			if(!reflectionSecondaryCmdBuffers.containsKey(key)){
				if (offScreenReflectionRenderList.get(key).getComponents()
						.containsKey(NodeComponentType.REFLECTION_RENDERINFO)){
					VkRenderInfo renderInfo = offScreenReflectionRenderList.get(key)
							.getComponent(NodeComponentType.REFLECTION_RENDERINFO);
					reflectionSecondaryCmdBuffers.put(key,renderInfo.getCommandBuffer());
				}
			}
		}
		
		// render reflection scene
		if (!offScreenReflectionRenderList.getObjectList().isEmpty()){
			offscreenReflectionCmdBuffer.reset();
			offscreenReflectionCmdBuffer.record(
					offScreenReflecRefracFbo.getRenderPass().getHandle(),
					offScreenReflecRefracFbo.getFrameBuffer().getHandle(),
					offScreenReflecRefracFbo.getWidth(),
					offScreenReflecRefracFbo.getHeight(),
					offScreenReflecRefracFbo.getAttachmentCount(),
					offScreenReflecRefracFbo.isDepthAttachment(),
					VkUtil.createPointerBuffer(reflectionSecondaryCmdBuffers.values()));
			offScreenReflectionSubmitInfo.submit(
					VkContext.getLogicalDevice().getGraphicsQueue());
		}
		
		fence.waitForFence();
		deferredReflectionSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
		fence.waitForFence();
		
		// antimirror scene to clipplane
		sceneGraph.getWorldTransform().setScaling(1,1,1);
		sceneGraph.update();
		sceneGraph.getRoot().record(offScreenRefractionRenderList);
		
		for (String key : offScreenRefractionRenderList.getKeySet()) {
			
			if(!refractionSecondaryCmdBuffers.containsKey(key)){
				if (offScreenRefractionRenderList.get(key).getComponents()
						.containsKey(NodeComponentType.REFLECTION_RENDERINFO)){
					VkRenderInfo renderInfo = offScreenRefractionRenderList.get(key)
							.getComponent(NodeComponentType.REFLECTION_RENDERINFO);
					refractionSecondaryCmdBuffers.put(key,renderInfo.getCommandBuffer());
				}
			}
		}
		
		// render refraction scene
		if (!offScreenRefractionRenderList.getObjectList().isEmpty()){
			offscreenRefractionCmdBuffer.reset();
			offscreenRefractionCmdBuffer.record(
					offScreenReflecRefracFbo.getRenderPass().getHandle(),
					offScreenReflecRefracFbo.getFrameBuffer().getHandle(),
					offScreenReflecRefracFbo.getWidth(),
					offScreenReflecRefracFbo.getHeight(),
					offScreenReflecRefracFbo.getAttachmentCount(),
					offScreenReflecRefracFbo.isDepthAttachment(),
					Constants.DEEPOCEAN_COLOR2,
					VkUtil.createPointerBuffer(refractionSecondaryCmdBuffers.values()));
			offScreenRefractionSubmitInfo.submit(
					VkContext.getLogicalDevice().getGraphicsQueue());
		}
		
		fence.waitForFence();
		deferredRefractionSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
		fence.waitForFence();
		
		float[] v = {motion += waterConfiguration.getWaveMotion(),
				distortion += waterConfiguration.getDistortion()};
		uniformBuffer.mapMemory(BufferUtil.createByteBuffer(v));
	}
}
