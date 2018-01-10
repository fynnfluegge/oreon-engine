package org.oreon.core.renderer;

import org.oreon.core.scene.GameObject;

public interface Shader {

	public void bind();
	public void updateUniforms(GameObject object);
}
