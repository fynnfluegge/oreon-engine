#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform writeonly image2D splatmap;

uniform sampler2D normalmap;
uniform int N;

void main(void)
{
	ivec2 x = ivec2(gl_GlobalInvocationID.xy);
	vec2 texCoord = gl_GlobalInvocationID.xy/float(N);
	
	vec3 normal = normalize(texture(normalmap, texCoord).rgb);
	
	float slopeFactor = normal.z;
	
	vec4 blendValues = vec4(0,0,0,0);
	
	if (slopeFactor > 0.8){
		blendValues.x = 1.0;
	}
	else if (slopeFactor > 0.6){
		blendValues.y = 1.0;
	}
	else if (slopeFactor > 0.4) {
		blendValues.z = 1.0;
	}
	else {
		blendValues.a = 1.0;
	}
	
	imageStore(splatmap, x, blendValues);
}