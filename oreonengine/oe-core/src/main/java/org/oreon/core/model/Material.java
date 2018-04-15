package org.oreon.core.model;

import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.NodeComponent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Material<T> extends NodeComponent{
	
	private String name;
	private T diffusemap;
	private T normalmap;
	private T heightmap;
	private T ambientmap;
	private T specularmap;
	private T alphamap;
	private Vec3f color;
	private float heightScaling;
	private float horizontalScaling;
	private float emission;
	private float shininess;
	
}