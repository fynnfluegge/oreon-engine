package org.oreon.core.query;
import static org.lwjgl.opengl.GL15.glDeleteQueries;
import java.nio.IntBuffer;

import org.oreon.core.light.Light;
import org.oreon.core.scene.GameObject;

public abstract class OcclusionQuery {

	private int id;
	private IntBuffer buffer;
	private int occlusionFactor;
	
	public abstract void doQuery(GameObject object);
	
	public abstract void doQuery(Light light);
	
	public void delete() {
		glDeleteQueries(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOcclusionFactor() {
		return occlusionFactor;
	}

	public void setOcclusionFactor(int occlusionFactor) {
		this.occlusionFactor = occlusionFactor;
	}

	public IntBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(IntBuffer buffer) {
		this.buffer = buffer;
	}
}
