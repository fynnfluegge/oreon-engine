package engine.shader;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

import java.util.HashMap;

import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.textures.Texture2D;
import engine.utils.BufferAllocation;

public abstract class Shader {

	private int program;
	private HashMap<String, Integer> uniforms;
	
	public Shader()
	{
		program = glCreateProgram();
		uniforms = new HashMap<String, Integer>();
		
		if (program == 0)
		{
			System.err.println("Shader creation failed");
			System.exit(1);
		}	
	}
	
	public void bind()
	{
		glUseProgram(program);
	}
	
	public void updateUniforms(Matrix4f matrix0, Matrix4f matrix1, Matrix4f matrix2){};
	
	public void updateUniforms(Texture2D texture, int i, float j){};
	
	public void updateUniforms(Matrix4f matrix0, Matrix4f matrix1){};
	
	public void updateUniforms(Matrix4f matrix){};
	
	public void updateUniforms(int value){};
	
	public void updateUniforms(float value){};
	
	public void updateUniforms(GameObject object){};
	
	public void updateUniforms(int l, int n, float t){};
	
	public void updateUniforms(int l, int n, int t){};
	
	public void updateUniforms(int n, int l, float a, Vec2f w, float l2) {}
	
	public void updateUniforms(int n, int l, float a, Vec2f w, float v, float l2) {}
	
	public void updateUniforms(int i, int j, int k, int l) {}
	
	public void updateUniforms(int n, int pingpong) {}
	
	public void updateUniforms(int i, float j) {}
	
	
	public void addUniform(String uniform)
	{
		int uniformLocation = glGetUniformLocation(program, uniform);
		
		if (uniformLocation == 0xFFFFFFFF)
		{
			System.err.println(this.getClass().getName() + " Error: Could not find uniform: " + uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		uniforms.put(uniform, uniformLocation);
	}
	
	public void addUniformBlock(String uniform)
	{
		int uniformLocation =  glGetUniformBlockIndex(program, uniform);		
		if (uniformLocation == 0xFFFFFFFF)
		{
			System.err.println(this.getClass().getName() + " Error: Could not find uniform: " + uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		uniforms.put(uniform, uniformLocation);
	}
	
	public void addVertexShader(String text)
	{
		addProgram(text, GL_VERTEX_SHADER);
	}
	
	public void addGeometryShader(String text)
	{
		addProgram(text, GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShader(String text)
	{
		addProgram(text, GL_FRAGMENT_SHADER);
	}
	
	public void addTessellationControlShader(String text)
	{
		addProgram(text, GL_TESS_CONTROL_SHADER);
	}
	
	public void addTessellationEvaluationShader(String text)
	{
		addProgram(text, GL_TESS_EVALUATION_SHADER);
	}
	
	public void addComputeShader(String text)
	{
		addProgram(text, GL_COMPUTE_SHADER);
	}
	
	public void compileShader()
	{
		glLinkProgram(program);

		if(glGetProgrami(program, GL_LINK_STATUS) == 0)
		{
			System.out.println(this.getClass().getName() + " " + glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}
		
		glValidateProgram(program);
		
		if(glGetProgrami(program, GL_VALIDATE_STATUS) == 0)
		{
			System.err.println(this.getClass().getName() +  " " + glGetProgramInfoLog(program, 1024));
			System.exit(1);
		}
	}
	
	private void addProgram(String text, int type)
	{
		int shader = glCreateShader(type);
		
		if (shader == 0)
		{
			System.err.println(this.getClass().getName() + " Shader creation failed");
			System.exit(1);
		}	
		
		glShaderSource(shader, text);
		glCompileShader(shader);
		
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0)
		{
			System.err.println(this.getClass().getName() + " " + glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}
		
		glAttachShader(program, shader);
	}
	
	public void setUniformi(String uniformName, int value)
	{
		glUniform1i(uniforms.get(uniformName), value);
	}
	public void setUniformf(String uniformName, float value)
	{
		glUniform1f(uniforms.get(uniformName), value);
	}
	public void setUniform(String uniformName, Vec2f value)
	{
		glUniform2f(uniforms.get(uniformName), value.getX(), value.getY());
	}
	public void setUniform(String uniformName, Vec3f value)
	{
		glUniform3f(uniforms.get(uniformName), value.getX(), value.getY(), value.getZ());
	}
	public void setUniform(String uniformName, Quaternion value)
	{
		glUniform4f(uniforms.get(uniformName), value.getX(), value.getY(), value.getZ(), value.getW());
	}
	public void setUniform(String uniformName, Matrix4f value)
	{
		glUniformMatrix4(uniforms.get(uniformName), true, BufferAllocation.createFlippedBuffer(value));
	}
	
	public void bindUniformBlock(String uniformBlockName, int uniformBlockBinding )
	{
		glUniformBlockBinding(program, uniforms.get(uniformBlockName), uniformBlockBinding);
	}
	
	public int getProgram()
	{
		return this.program;
	}
}
