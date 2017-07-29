#version 430

layout (location = 0) in vec3 position;                                          
    
uniform mat4 m_MVP;
 
void main()                                                                         
{      
    gl_PointSize = 300;
	gl_Position = m_MVP * vec4(position,1.0);                                             
}