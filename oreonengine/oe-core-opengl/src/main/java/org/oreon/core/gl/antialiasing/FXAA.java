package org.oreon.core.gl.antialiasing;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import org.oreon.core.gl.shaders.FXAAShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.system.CoreSystem;

public class FXAA {

	private FXAAShader shader;
	private Texture2D fxaaSceneTexture;
	
	public FXAA(){
	
		shader = FXAAShader.getInstance();
		
		fxaaSceneTexture = new Texture2D();
		fxaaSceneTexture.generate();
		fxaaSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						CoreSystem.getInstance().getWindow().getWidth(),
						CoreSystem.getInstance().getWindow().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		fxaaSceneTexture.noFilter();
	}
	
	public void render(Texture2D sceneTexture){
		
		shader.bind();
		glBindImageTexture(0, fxaaSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, sceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glDispatchCompute(CoreSystem.getInstance().getWindow().getWidth()/16, CoreSystem.getInstance().getWindow().getHeight()/16, 1);	
	}

	public Texture2D getFxaaSceneTexture() {
		return fxaaSceneTexture;
	}

	public void setFxaaSceneTexture(Texture2D fxaaSceneTexture) {
		this.fxaaSceneTexture = fxaaSceneTexture;
	}
}
