package org.oreon.core.util;

import org.oreon.core.math.Vec4f;
import org.oreon.core.math.Vec3f;

public class Constants {
	
	public static final long NANOSECOND = 1000000000;
	public static final float ZFAR = 10000.0f;
	public static final float ZNEAR = 0.1f;
	public static Vec4f PLANE0 = new Vec4f(0,0,0,0);
	public static Vec3f DEEPOCEAN_COLOR = new Vec3f(0.1f,0.125f,0.24f);
	public static Vec3f DEEPOCEAN_COLOR2 = new Vec3f(0.1f,0.125f,0.24f);
	public static int CLIPOFFSET;
	public static final int PSSM_SPLITS = 6;
	public static final float[] PSSM_SPLIT_SHEME= { -0.005f,0.005f,
													-0.005f,0.01f, 
													0.0f,0.02f,
													0.01f,0.04f,
													0.02f,0.06f,
													0.05f,0.16f };
	
	public static final int PSSM_SHADOWMAP_RESOLUTION = 4096;
	
	// Global Uniform Block Bindings
	public static final int CameraUniformBlockBinding = 51;
	public static final int DirectionalLightUniformBlockBinding = 52;
	public static final int LightMatricesUniformBlockBinding = 53;
	
	//example spotlight
	/*PointLight light = new PointLight(getTransform().getTranslation(), new Vec3f(1,1,1), new Vec3f(1.0f,1.0f,0.8f), 1f);
	light.getLocalTransform().getTranslation().setY(-10);
	if (i == 1)
		light.setEnabled(1);
	light.setSpot(1);
	light.setConstantAttenuation(0.01f);
	light.setLinearAttenuation(0.005f);
	light.setQuadraticAttenuation(0.00005f);
	light.setConeDirection(new Vec3f(0,-1,0));
	light.setSpotCosCutoff(0.8f);
	light.setSpotExponent(20);
	drone.addComponent("Light", light);*/
}
