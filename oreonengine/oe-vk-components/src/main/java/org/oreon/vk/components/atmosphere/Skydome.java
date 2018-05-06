package org.oreon.vk.components.atmosphere;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.context.EngineContext;
import org.oreon.core.model.Mesh;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.scenegraph.NodeComponentKey;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ProceduralTexturing;
import org.oreon.core.vk.core.buffer.VkBuffer;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.core.pipeline.ShaderPipeline;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.core.pipeline.VkVertexInput;
import org.oreon.core.vk.core.platform.VkCamera;
import org.oreon.core.vk.core.scenegraph.VkMeshData;
import org.oreon.core.vk.core.util.VkAssimpModelLoader;
import org.oreon.core.vk.core.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.command.SecondaryDrawCmdBuffer;
import org.oreon.core.vk.wrapper.pipeline.GraphicsPipeline;

public class Skydome extends Renderable{
	
	private VkPipeline graphicsPipeline;
	
	public Skydome() {
		
		VkDevice device = VkContext.getLogicalDevice().getHandle();
		
		getWorldTransform().setScaling(Constants.ZFAR*0.5f, Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
		
		Mesh mesh = VkAssimpModelLoader.loadModel("models/obj/dome", "dome.obj").get(0).getMesh();
		ProceduralTexturing.dome(mesh);
		
		ShaderPipeline shaderPipeline = new ShaderPipeline(device);
	    shaderPipeline.createVertexShader("shaders/atmosphere/atmosphere.vert.spv");
	    shaderPipeline.createFragmentShader("shaders/atmosphere/atmosphere.frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(VkCamera.getDescriptor().getSet());
		descriptorSetLayouts.add(VkCamera.getDescriptor().getLayout());
		
		VkVertexInput vertexInput = new VkVertexInput(VertexLayout.POS);
		
		ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(mesh.getVertices(), VertexLayout.POS);
		ByteBuffer indexBuffer = BufferUtil.createByteBuffer(mesh.getIndices());
		
		int pushConstantRange = Float.BYTES * 16;
		
		ByteBuffer pushConstants = memAlloc(pushConstantRange);
		FloatBuffer floatBuffer = pushConstants.asFloatBuffer();
		floatBuffer.put(BufferUtil.createFlippedBuffer(getWorldTransform().getWorldMatrix()));
		
		graphicsPipeline = new GraphicsPipeline(device,
				shaderPipeline, vertexInput, VkUtil.createLongBuffer(descriptorSetLayouts),
				EngineContext.getConfig().getX_ScreenResolution(),
				EngineContext.getConfig().getY_ScreenResolution(),
				VkContext.getRenderContext().getOffScreenRenderPass().getHandle(),
				pushConstantRange, VK_SHADER_STAGE_VERTEX_BIT);
		
		VkBuffer vertexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
				VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(),
				VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
				VkContext.getLogicalDevice().getTransferQueue(),
				vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

        VkBuffer indexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
        		VkContext.getLogicalDevice().getHandle(),
        		VkContext.getPhysicalDevice().getMemoryProperties(),
        		VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
        		VkContext.getLogicalDevice().getTransferQueue(),
        		indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
        
        CommandBuffer commandBuffer = new SecondaryDrawCmdBuffer(
	    		VkContext.getLogicalDevice().getHandle(),
	    		VkContext.getLogicalDevice().getGraphicsCommandPool().getHandle(), 
	    		graphicsPipeline.getHandle(), graphicsPipeline.getLayoutHandle(),
	    		VkContext.getRenderContext().getOffScreenFrameBuffer().getHandle(),
	    		VkContext.getRenderContext().getOffScreenRenderPass().getHandle(),
	    		0,
	    		VkUtil.createLongArray(descriptorSets),
	    		vertexBufferObject.getHandle(),
	    		indexBufferObject.getHandle(),
	    		mesh.getIndices().length,
	    		pushConstants, VK_SHADER_STAGE_VERTEX_BIT);
	    
	    VkContext.getRenderContext().getOffScreenSecondaryCmdBuffers().add(commandBuffer);
	    
	    VkMeshData meshData = new VkMeshData(vertexBufferObject, vertexBuffer,
	    		indexBufferObject, indexBuffer);
	    
	    addComponent(NodeComponentKey.MESH_DATA, meshData);
	}

}
