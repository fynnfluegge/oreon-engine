package org.oreon.core.vk.core.pipeline;

import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_NONE;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_SCISSOR;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_VIEWPORT;
import static org.lwjgl.vulkan.VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE;
import static org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_FILL;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT;
import static org.lwjgl.vulkan.VK10.VK_STENCIL_OP_KEEP;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateGraphicsPipelines;
import static org.lwjgl.vulkan.VK10.vkCreatePipelineLayout;
import static org.lwjgl.vulkan.VK10.vkDestroyPipeline;
import static org.lwjgl.vulkan.VK10.vkDestroyPipelineLayout;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_R_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_ALWAYS;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_G_BIT;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_B_BIT;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_A_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;
import org.oreon.core.vk.core.util.VkUtil;

import lombok.Getter;

public class Pipeline {
	
	private VkPipelineVertexInputStateCreateInfo vertexInputState;
	private VkPipelineInputAssemblyStateCreateInfo inputAssembly;
	private VkPipelineViewportStateCreateInfo viewportAndScissorState;
	private VkPipelineRasterizationStateCreateInfo rasterizer;
	private VkPipelineMultisampleStateCreateInfo multisampling;
	private VkPipelineColorBlendStateCreateInfo colorBlending;
	private VkPipelineDepthStencilStateCreateInfo depthStencil;
	private VkPipelineDynamicStateCreateInfo dynamicState;
	private VkViewport.Buffer viewport;
	private VkRect2D.Buffer scissor;
	private IntBuffer pDynamicStates;
	
	@Getter
	private long handle;
	@Getter
	private long layoutHandle;
	
	private RenderPass renderPass;
	
	
	public void createPipeline(VkDevice device, ShaderPipeline shaderPipeline, RenderPass renderPass){
		
		this.renderPass = renderPass;
		
		VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo = VkGraphicsPipelineCreateInfo.calloc(1)
				.sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
				.pStages(shaderPipeline.getShaderPipeline())
				.pVertexInputState(vertexInputState)
				.pInputAssemblyState(inputAssembly)
				.pViewportState(viewportAndScissorState)
				.pRasterizationState(rasterizer)
				.pMultisampleState(multisampling)
				.pDepthStencilState(null)
				.pColorBlendState(colorBlending)
				.pDynamicState(null)
				.layout(layoutHandle)
				.renderPass(renderPass.getHandle())
				.subpass(0)
				.basePipelineHandle(VK_NULL_HANDLE)
				.basePipelineIndex(-1);
		
		LongBuffer pPipelines = memAllocLong(1);
		int err = vkCreateGraphicsPipelines(device, VK_NULL_HANDLE, pipelineCreateInfo, null, pPipelines);
		
		handle = pPipelines.get(0);
		
		vertexInputState.free();
		inputAssembly.free();
		viewportAndScissorState.free();
		rasterizer.free();
		multisampling.free();
		colorBlending.free();
		depthStencil.free();
		dynamicState.free();
		viewport.free();
		scissor.free();
		memFree(pDynamicStates);
		
		shaderPipeline.destroy(device);
		
		if (err != VK_SUCCESS) {
			throw new AssertionError("Failed to create pipeline: " + VkUtil.translateVulkanResult(err));
		}
	}
	
	public void setLayout(VkDevice device, LongBuffer pLayouts){
		
		VkPipelineLayoutCreateInfo pipelineLayout = VkPipelineLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pNext(0)
                .pSetLayouts(pLayouts);
		
		LongBuffer pPipelineLayout = memAllocLong(1);
        int err = vkCreatePipelineLayout(device, pipelineLayout, null, pPipelineLayout);
        
        layoutHandle = pPipelineLayout.get(0);
        
        memFree(pPipelineLayout);
        pipelineLayout.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create pipeline layout: " + VkUtil.translateVulkanResult(err));
        }
	}
	
	public void setVertexInput(VertexInputInfo vertexInput){
		
		vertexInputState = VkPipelineVertexInputStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
				.pNext(0)
				.pVertexBindingDescriptions(vertexInput.getBindingDescription())
				.pVertexAttributeDescriptions(vertexInput.getAttributeDescriptions());
	}
	
	public void setInputAssembly(){
		
		inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc()
		        .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
		        .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
		        .primitiveRestartEnable(false);
	}
	
	public void setViewportAndScissor(VkExtent2D extent){
		
		viewport = VkViewport.calloc(1)
				.x(0)
				.y(0)
				.height(extent.height())
		        .width(extent.width())
		        .minDepth(0.0f)
		        .maxDepth(1.0f);
		 
		scissor = VkRect2D.calloc(1);
		scissor.extent().set(extent.width(), extent.height());
		scissor.offset().set(0, 0);
		
		viewportAndScissorState = VkPipelineViewportStateCreateInfo.calloc()
		        .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
		        .viewportCount(1)
		        .pViewports(viewport)
		        .scissorCount(1)
		        .pScissors(scissor);
	}
	
	public void setRasterizer(){
		
		rasterizer = VkPipelineRasterizationStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
				.polygonMode(VK_POLYGON_MODE_FILL)
				.cullMode(VK_CULL_MODE_NONE)
				.frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
				.rasterizerDiscardEnable(false)
				.lineWidth(1.0f)
				.depthClampEnable(false)
				.depthBiasEnable(false)
				.depthBiasConstantFactor(0)
				.depthBiasSlopeFactor(0)
				.depthBiasClamp(0);
	}
	
	public void setMultisampling(){
		
		multisampling = VkPipelineMultisampleStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .sampleShadingEnable(false)
                .pSampleMask(null)
                .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
                .minSampleShading(1)
                .alphaToCoverageEnable(false)
                .alphaToOneEnable(false);
	}
	
	public void setColorBlending(){
	
		VkPipelineColorBlendAttachmentState.Buffer colorWriteMask = VkPipelineColorBlendAttachmentState.calloc(1)
                .blendEnable(false)
                .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT
                				| VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
        colorBlending = VkPipelineColorBlendStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .logicOpEnable(false)
                .pAttachments(colorWriteMask);
	}
	
	public void setDepthAndStencilTest(){
		
		depthStencil = VkPipelineDepthStencilStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                .depthTestEnable(false)
                .depthWriteEnable(false)
                .depthCompareOp(VK_COMPARE_OP_ALWAYS)
                .depthBoundsTestEnable(false)
                .stencilTestEnable(false);
        depthStencil.back()
                .failOp(VK_STENCIL_OP_KEEP)
                .passOp(VK_STENCIL_OP_KEEP)
                .compareOp(VK_COMPARE_OP_ALWAYS);
        depthStencil.front(depthStencil.back());
	}
	
	public void setDynamicState(){
		
		pDynamicStates = memAllocInt(2);
        pDynamicStates.put(VK_DYNAMIC_STATE_VIEWPORT);
        pDynamicStates.put(VK_DYNAMIC_STATE_SCISSOR);
        pDynamicStates.flip();
        
        dynamicState = VkPipelineDynamicStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(pDynamicStates);
	}
	
	public void destroy(VkDevice device){
		
		vkDestroyPipelineLayout(device, layoutHandle, null);
		renderPass.destroy(device);
		vkDestroyPipeline(device, handle, null);
	}

}
