package org.oreon.vk.components.water;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
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
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.common.water.WaterConfiguration;
import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Quaternion;
import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.scenegraph.Scenegraph;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.MeshGenerator;
import org.oreon.core.vk.buffer.VkBuffer;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;
import org.oreon.core.vk.scenegraph.VkMeshData;
import org.oreon.core.vk.scenegraph.VkRenderInfo;
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
	
	// Reflection/Refraction Resources
	private PrimaryCmdBuffer offscreenRefracReflecCmdBuffer;
	private SubmitInfo offScreenRefracReflecSubmitInfo;
	private ReflectionFbo offScreenRefracReflecFbo;
	
	private VkPipeline deferredReflectionPipeline;
	private CommandBuffer deferredReflectionCmdBuffer;
	private SubmitInfo deferredReflectionSubmitInfo;
	private VkImage deferredReflectionImage;
	private VkImageView deferredReflectionImageView;
	
	private VkPipeline deferredRefractionPipeline;
	private CommandBuffer deferredRefractionCmdBuffer;
	private SubmitInfo deferredRefractionSubmitInfo;
	private VkImage deferredRefractionImage;
	private VkImageView deferredRefractionImageView;
	
	public Water() {
		
		VkDevice device = VkContext.getLogicalDevice().getHandle();
		VkPhysicalDeviceMemoryProperties memoryProperties =
				VkContext.getPhysicalDevice().getMemoryProperties();
		
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
		
		dxSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    dySampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    dzSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    dudvSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
	    normalSampler = new VkSampler(device, VK_FILTER_LINEAR, true);
		
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
	    
	    DescriptorSetLayout descriptorSetLayout = new DescriptorSetLayout(device,6);
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
	    descriptorSetLayout.addLayoutBinding(5, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
				VK_SHADER_STAGE_FRAGMENT_BIT | VK_SHADER_STAGE_GEOMETRY_BIT);
	    descriptorSetLayout.create();
	    
	    DescriptorSet descriptorSet = new DescriptorSet(device,
	    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		descriptorSetLayout.getHandlePointer());
	    descriptorSet.updateDescriptorImageBuffer(fft.getDyImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dySampler.getHandle(), 0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(fft.getDxImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dxSampler.getHandle(), 1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(fft.getDzImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dzSampler.getHandle(), 2, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(imageView_dudv.getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		dudvSampler.getHandle(), 3, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(normalRenderer.getNormalImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		normalSampler.getHandle(), 4, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorBuffer(uniformBuffer.getHandle(),
	    		ubo.limit(), 0, 5, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
	    
	    List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(VkContext.getCamera().getDescriptor().getSet());
		descriptorSets.add(descriptorSet);
		descriptorSetLayouts.add(VkContext.getCamera().getDescriptor().getLayout());
		descriptorSetLayouts.add(descriptorSetLayout);
	    
	    VkVertexInput vertexInput = new VkVertexInput(VertexLayout.POS2D);
		
	    Vec2f[] vertices = MeshGenerator.generatePatch2D4x4(32);
	    
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
		
		offScreenRefracReflecFbo = new ReflectionFbo(device, memoryProperties);
		
		deferredReflectionImage = new Image2DLocal(device, memoryProperties,
				offScreenRefracReflecFbo.getWidth(), offScreenRefracReflecFbo.getHeight(),
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		deferredReflectionImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, deferredReflectionImage.getHandle(),
				VK_IMAGE_ASPECT_COLOR_BIT);
		
		deferredRefractionImage = new Image2DLocal(device, memoryProperties,
				offScreenRefracReflecFbo.getWidth(), offScreenRefracReflecFbo.getHeight(),
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		deferredRefractionImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, deferredRefractionImage.getHandle(),
				VK_IMAGE_ASPECT_COLOR_BIT);
		
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
	    		offScreenRefracReflecFbo.getAlbedoBuffer().getImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
	    descriptorSetReflection.updateDescriptorImageBuffer(
	    		offScreenRefracReflecFbo.getNormalBuffer().getImageView().getHandle(),
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
				offScreenRefracReflecFbo.getAlbedoBuffer().getImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		descriptorSetRefraction.updateDescriptorImageBuffer(
				offScreenRefracReflecFbo.getNormalBuffer().getImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);

		deferredReflectionPipeline = new VkPipeline(device);
		deferredReflectionPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		deferredReflectionPipeline.createComputePipeline(
				new ShaderModule(device, "shaders/water/waterDeferredRefracReflec.comp.spv",
						VK_SHADER_STAGE_COMPUTE_BIT));
		deferredReflectionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				deferredReflectionPipeline.getHandle(),
				deferredReflectionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets),
				offScreenRefracReflecFbo.getHeight()/16, offScreenRefracReflecFbo.getHeight()/16, 1);
		
		deferredRefractionPipeline = new VkPipeline(device);
		descriptorSets.set(1, descriptorSetRefraction);
		deferredRefractionPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		deferredRefractionPipeline.createComputePipeline(
				new ShaderModule(device, "shaders/water/waterDeferredRefracReflec.comp.spv",
						VK_SHADER_STAGE_COMPUTE_BIT));
		deferredRefractionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				deferredRefractionPipeline.getHandle(),
				deferredRefractionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets),
				offScreenRefracReflecFbo.getHeight()/16, offScreenRefracReflecFbo.getHeight()/16, 1);
		
		deferredReflectionSubmitInfo = new SubmitInfo();
		deferredReflectionSubmitInfo.setCommandBuffers(deferredReflectionCmdBuffer.getHandlePointer());
		
		deferredRefractionSubmitInfo = new SubmitInfo();
		deferredRefractionSubmitInfo.setCommandBuffers(deferredRefractionCmdBuffer.getHandlePointer());
		
		offscreenRefracReflecCmdBuffer = new PrimaryCmdBuffer(device, 
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle());
		offScreenRefracReflecSubmitInfo = new SubmitInfo();
		offScreenRefracReflecSubmitInfo.setCommandBuffers(
				offscreenRefracReflecCmdBuffer.getHandlePointer());
	}
	
	public void render(){
		
		fft.render();
		normalRenderer.render();
		
		//TODO render refraction/reflection
		EngineContext.getRenderState().setClipplane(clipplane);
		
		//mirror scene to clipplane
		Scenegraph scenegraph = getParentObject();
		scenegraph.getWorldTransform().setScaling(1,-1,1);
		scenegraph.update();
		// render reflection scene
		offscreenRefracReflecCmdBuffer.reset();
//		offscreenRefracReflecCmdBuffer.record(
//				deferredReflectionFbo.getRenderPass().getHandle(),
//				deferredReflectionFbo.getFrameBuffer().getHandle(),
//				deferredReflectionFbo.getWidth(),
//				deferredReflectionFbo.getHeight(),
//				deferredReflectionFbo.getAttachmentCount(),
//				deferredReflectionFbo.isDepthAttachment(),
//				//TODO
//				VkUtil.createPointerBuffer(VkContext.getRenderState().getOffScreenSecondaryCmdBuffers()));
//		offScreenRefracReflecSubmitInfo.submit(
//				VkContext.getLogicalDevice().getGraphicsQueue());
		
		// antimirror scene to clipplane
		scenegraph.getWorldTransform().setScaling(1,1,1);
		scenegraph.update();
		
		float[] v = {motion += waterConfiguration.getWaveMotion(),
				distortion += waterConfiguration.getDistortion()};
		uniformBuffer.mapMemory(BufferUtil.createByteBuffer(v));
	}
}
