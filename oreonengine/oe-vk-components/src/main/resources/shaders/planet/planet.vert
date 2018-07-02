#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inPosition;

layout (location = 0 )out vec2 outUV;

out gl_PerVertex {
	vec4 gl_Position;	
};

layout(set = 0, binding = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (push_constant, std430, row_major) uniform Constants{
	mat4 localMatrix;
	mat4 worldMatrix;
	float verticalScaling;
	float horizontalScaling;
	int lod;
	float gap;
	vec2 location;
	vec2 index;
	int lod_morph_area[8];
	int tessFactor;
	float tessSlope;
	float tessShift;
	float uvScaling;
	int highDetailRange;
} constants;

float morphLatitude(vec2 position)
{
	vec2 frac = position - constants.location;
	
	if (constants.index == vec2(0,0)){
		float morph = frac.x - frac.y;
		if (morph > 0)
			return morph;
	}
	if (constants.index == vec2(1,0)){
		float morph = constants.gap - frac.x - frac.y;
		if (morph > 0)
			return morph;
	}
	if (constants.index == vec2(0,1)){
		float morph = frac.x + frac.y - constants.gap;
		if (morph > 0)
			return -morph;
	}
	if (constants.index == vec2(1,1)){
		float morph = frac.y - frac.x;
		if (morph > 0)
			return -morph;
	}
	return 0;
}

float morphLongitude(vec2 position)
{
	vec2 frac = position - constants.location;
	
	if (constants.index == vec2(0,0)){
		float morph = frac.y - frac.x;
		if (morph > 0)
			return -morph;
	}
	else if (constants.index == vec2(1,0)){
		float morph = frac.y - (constants.gap - frac.x);
		if (morph > 0)
			return morph;
	}
	else if (constants.index == vec2(0,1)){
		float morph = constants.gap - frac.y - frac.x;
		if (morph > 0)
			return -morph;
	}
	else if (constants.index == vec2(1,1)){
		float morph = frac.x - frac.y;
		if (morph > 0)
			return morph;
	}
	return 0;
}

vec2 morph(vec2 localPosition, float height, int morph_area){
	
	vec2 morphing = vec2(0,0);
	
	vec2 fixPointLatitude = vec2(0,0);
	vec2 fixPointLongitude = vec2(0,0);
	float distLatitude;
	float distLongitude;
	
	if (constants.index == vec2(0,0)) {
		fixPointLatitude = constants.location + vec2(constants.gap,0);
		fixPointLongitude = constants.location + vec2(0,constants.gap);
	}
	else if (constants.index == vec2(1,0)) {
		fixPointLatitude = constants.location;
		fixPointLongitude = constants.location + vec2(constants.gap,constants.gap);
	}
	else if (constants.index == vec2(0,1)) {
		fixPointLatitude = constants.location + vec2(constants.gap,constants.gap);
		fixPointLongitude = constants.location;
	}
	else if (constants.index == vec2(1,1)) {
		fixPointLatitude = constants.location + vec2(0,constants.gap);
		fixPointLongitude = constants.location + vec2(constants.gap,0);
	}
		
	distLatitude = length(eyePosition - (constants.worldMatrix * 
					vec4(fixPointLatitude.x,height,fixPointLatitude.y,1)).xyz);
	distLongitude = length(eyePosition - (constants.worldMatrix * 
					vec4(fixPointLongitude.x,height,fixPointLongitude.y,1)).xyz);
					
	if (distLatitude > morph_area)
		morphing.x = morphLatitude(localPosition.xy);
	if (distLongitude > morph_area)
		morphing.y = morphLongitude(localPosition.xy);
		
	return morphing;
}

void main()
{
	vec2 localPosition = (constants.localMatrix * vec4(inPosition.x,0,inPosition.y,1)).xz;
	
	float height = 0;
	
	if (constants.lod > 0){
		localPosition += morph(localPosition,height,constants.lod_morph_area[constants.lod-1]);
	}
	
	outUV = localPosition;
					
	vec4 position = constants.worldMatrix * vec4(localPosition.x,height,localPosition.y,1);
	position = normalize(position) * constants.horizontalScaling;
	
	gl_Position = vec4(position.xyz,1);
}