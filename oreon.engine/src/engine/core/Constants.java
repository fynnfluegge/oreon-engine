package engine.core;


import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import engine.main.CoreEngine;
import engine.math.Quaternion;


public class Constants {
	
	public static final long NANOSECOND = 1000000000;
	public static final float ZFAR = 10000.0f;
	public static final float ZNEAR = 0.2f;
	public static Quaternion PLANE0 = new Quaternion(0,0,0,0);
	public static int CLIPOFFSET;
	public static final int PSSM_SPLITS = 4;
	public static final float[] PSSM_SPLIT_SHEME= {0,0.02f,0.1f,0.25f,1};
	
	// Uniform Block Bindings
	public static final int CameraUniformBlockBinding = 1;
	public static final int DirectionalLightUniformBlockBinding = 2;
	public static final int LightMatricesUniformBlockBinding = 3;
	

	

	// context sharing
	@SuppressWarnings("unused")
	private void loadTerrain() throws LWJGLException {                                                     
    	
    	CoreEngine.setShareGLContext(true);
    	
    	CoreEngine.getGLContextLock().lock();
    	try{
    		while(!CoreEngine.isGlContextfree())
    		{
    			try {
    				CoreEngine.getHoldGLContext().await();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	finally{
    		CoreEngine.getGLContextLock().unlock();
    	}
  
    	Display.makeCurrent();
    	
//        simulation.setTerrain(new Terrain());
//        simulation.getTerrain().setScaleXZ(this.scaleXZSlider.getValue());
//        simulation.getTerrain().setScaleY(this.scaleYSlider.getValue());
//        simulation.getTerrain().setBezíer(0);
//        simulation.getTerrain().setTessellationFactor(this.tessFactorTSlider.getValue());
//        simulation.getTerrain().setTessellationSlope(this.tessSlopeTSlider.getValue()/10f);
//        simulation.getTerrain().setTessellationShift(this.tessShiftTSlider.getValue()/100f);
//        simulation.getTerrain().setDetailRange(this.detailRangeSlider.getValue());
//        simulation.getTerrain().setTexDetail(this.texDetailTSlider.getValue());
//        this.shininess1Spinner.setValue(simulation.getTerrain().getMaterial1().getShininess());
//        this.shininess2Spinner.setValue(simulation.getTerrain().getMaterial2().getShininess());
//        this.shininess3Spinner.setValue(simulation.getTerrain().getMaterial3().getShininess());

        
        CoreEngine.getGLContextLock().lock();
        try{
        	try {
        		Display.releaseContext();
        		CoreEngine.setGlContextfree(false);
        		CoreEngine.getHoldGLContext().signal();
        	} catch (LWJGLException e1) {
        		e1.printStackTrace();
        	}
        }
        finally{
        	CoreEngine.getGLContextLock().unlock();
        }
	}
	
	
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
