package org.oreon.core.gl.texture;

import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;

import org.oreon.core.texture.Texture;

public class Texture2DMultisample extends Texture{

	private int id;
	
	public Texture2DMultisample(){
	}
	
	public void generate(){
		
		id = glGenTextures();
	}
	
	public void delete(){
		
		glDeleteTextures(id);
	}
	
	public void bind(){
		
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, id);
	}
	
	public void unbind(){
		
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
