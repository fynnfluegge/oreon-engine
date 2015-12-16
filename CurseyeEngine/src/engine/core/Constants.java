package engine.core;

import engine.math.Quaternion;

public class Constants {
	
	public static long NANOSECOND = 1000000000;
	public static float ZFAR = 10000.0f;
	public static float ZNEAR = 0.1f;
	public static Quaternion PLANE0 = new Quaternion(0,0,0,0);
	public static int CLIPOFFSET;
	
	/**
	 * terrain Textures
	 */
	
	public static String GRASS_TEXTURE1 = "grass/grass1.jpg";
	public static String GRASS_TEXTURE2 = "grass/grass2.jpg";
	public static String GRASS_TEXTURE3 = "grass/grass3.jpg";
	public static String GRASS_TEXTURE4 = "grass/grass4.jpg";
	public static String GRASS_TEXTURE5 = "grass/grass5.jpg";
	public static String GRASS_TEXTURE6 = "grass/grass6.jpg";
	public static String GRASS_TEXTURE7 = "grass/grass7.jpg";
	public static String GRASS_TEXTURE8 = "grass/grass8.jpg";
	
	public static String ROCK_TEXTURE1  = "rock/rock1.jpg";
	public static String ROCK_TEXTURE2  = "rock/rock2.jpg";
	public static String ROCK_TEXTURE3  = "rock/rock3.jpg";
	public static String ROCK_TEXTURE4  = "rock/rock4.jpg";
	public static String ROCK_TEXTURE5  = "rock/rock5.jpg";
	public static String ROCK_TEXTURE6  = "rock/rock6.jpg";
	public static String ROCK_TEXTURE7  = "rock/rock7.jpg";
	public static String ROCK_TEXTURE8  = "rock/rock8.jpg";
	
	
	
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
