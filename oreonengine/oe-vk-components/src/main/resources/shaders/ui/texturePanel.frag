#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec2 inUV;

layout(location = 0) out vec4 outColor;

layout (set = 0, binding = 0) uniform sampler2D textureSampler;

void main() {

	vec4 rgba = texture(textureSampler, inUV);

    outColor = rgba;
}