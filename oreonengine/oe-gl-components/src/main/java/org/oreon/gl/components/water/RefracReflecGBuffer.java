package org.oreon.gl.components.water;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import java.nio.ByteBuffer;
import org.oreon.core.gl.texture.Texture2D;

public class RefracReflecGBuffer {
	
	private Texture2D albedoTexture;
	private Texture2D normalTexture;
	
	public RefracReflecGBuffer(int width, int height) {
		
		albedoTexture = new Texture2D();
		albedoTexture.generate();
		albedoTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		albedoTexture.noFilter();
		
		normalTexture = new Texture2D();
		normalTexture.generate();
		normalTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		normalTexture.noFilter();
	}

	public Texture2D getAlbedoTexture() {
		return albedoTexture;
	}

	public void setAlbedoTexture(Texture2D albedoTexture) {
		this.albedoTexture = albedoTexture;
	}

	public Texture2D getNormalTexture() {
		return normalTexture;
	}

	public void setNormalTexture(Texture2D normalTexture) {
		this.normalTexture = normalTexture;
	}
}
