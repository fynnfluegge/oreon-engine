package org.oreon.core.vk.pipeline;

import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_NONE;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_SCISSOR;
import static org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_VIEWPORT;
import static org.lwjgl.vulkan.VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE;
import static org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_FILL;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT;
import static org.lwjgl.vulkan.VK10.VK_STENCIL_OP_KEEP;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO;

import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_R_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_ALWAYS;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_G_BIT;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_B_BIT;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_A_BIT;

import org.lwjgl.vulkan.VkExtent2D;
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

public class Pipeline {
	
	private VkPipelineVertexInputStateCreateInfo vertexInput;
	private VkPipelineInputAssemblyStateCreateInfo inputAssembly;
	private VkPipelineViewportStateCreateInfo viewportAndScissor;
	private VkPipelineRasterizationStateCreateInfo rasterization;
	private VkPipelineMultisampleStateCreateInfo multisampling;
	private VkPipelineColorBlendStateCreateInfo colorBlending;
	private VkPipelineDepthStencilStateCreateInfo depthStencil;
	private VkPipelineDynamicStateCreateInfo dynamicState;
	private VkPipelineLayoutCreateInfo pipelineLayout;
	
	public void createPipeline(){
		
	}
	
	public void specifyVertexInput(){
		
		vertexInput = VkPipelineVertexInputStateCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
				.pNext(0)
				.pVertexBindingDescriptions(null)
				.pVertexAttributeDescriptions(null);
	}
	
	public void specifyInputAssembly(){
		
		inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc()
		        .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
		        .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
		        .primitiveRestartEnable(false);
	}
	
	public void specifyViewportAndScissor(VkExtent2D extent){
		
		VkViewport.Buffer viewport = VkViewport.calloc(1)
				.x(0)
				.y(0)
				.height(extent.height())
		        .width(extent.width())
		        .minDepth(0.0f)
		        .maxDepth(1.0f);
		 
		VkRect2D.Buffer scissor = VkRect2D.calloc(1);
		scissor.extent().set(extent.width(), extent.height());
		scissor.offset().set(0, 0);
		
		viewportAndScissor = VkPipelineViewportStateCreateInfo.calloc()
		        .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
		        .viewportCount(1)
		        .pViewports(viewport)
		        .scissorCount(1)
		        .pScissors(scissor);
	}
	
	public void specifyRasterizer(){
		
		rasterization = VkPipelineRasterizationStateCreateInfo.calloc()
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
	
	public void specifyMultisampling(){
		
		multisampling = VkPipelineMultisampleStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .sampleShadingEnable(false)
                .pSampleMask(null)
                .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
                .minSampleShading(1)
                .alphaToCoverageEnable(false)
                .alphaToOneEnable(false);
	}
	
	public void specifyColorBlending(){
	
		VkPipelineColorBlendAttachmentState.Buffer colorWriteMask = VkPipelineColorBlendAttachmentState.calloc(1)
                .blendEnable(false)
                .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT
                				| VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
        colorBlending = VkPipelineColorBlendStateCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .logicOpEnable(false)
                .pAttachments(colorWriteMask);
	}
	
	public void specifyDepthAndStencilTest(){
		
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
	
	public void specifyDynamicState(){
		
//		IntBuffer pDynamicStates = memAllocInt(2);
//        pDynamicStates.put(VK_DYNAMIC_STATE_VIEWPORT).put(VK_DYNAMIC_STATE_SCISSOR).flip();
//        
//        dynamicState = VkPipelineDynamicStateCreateInfo.calloc()
//                .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
//                .pDynamicStates(pDynamicStates);
	}
	
	public void specifyPipelineLayout(){
		
		pipelineLayout = VkPipelineLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pNext(0)
                .pSetLayouts(null);
	}
}
