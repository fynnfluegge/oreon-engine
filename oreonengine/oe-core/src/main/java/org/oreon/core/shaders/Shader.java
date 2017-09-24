package org.oreon.core.shaders;

import org.oreon.core.scene.GameObject;

public interface Shader {

	public void bind();
	public void updateUniforms(GameObject object);
}
