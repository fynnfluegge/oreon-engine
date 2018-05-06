#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 inPosition;

layout (location = 0) out vec2 outUV;

layout (push_constant) uniform Constants{
	mat4 m_World;
};

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
		gl_Position = m_World * vec4(inPosition.x,0,inPosition.y,1);
		outUV = inPosition.xy;
}
