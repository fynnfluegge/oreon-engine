package org.oreon.common.ui;

import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.Renderable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UIElement extends Renderable{

	protected Transform orthoTransform;
	protected Matrix4f orthographicMatrix;
	protected Mesh panel;
	protected Vec2f[] uv;
	
	public abstract void render();
	
	public void update(){}
	
	public void init(){}

}
