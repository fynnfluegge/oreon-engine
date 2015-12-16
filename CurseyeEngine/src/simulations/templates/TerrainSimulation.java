package simulations.templates;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.GL_COMPARE_REF_TO_TEXTURE;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import java.nio.ByteBuffer;

import engine.core.Constants;
import engine.core.Texture;
import engine.core.Window;
import engine.main.RenderingEngine;
import engine.renderer.terrain.SkySphere;
import engine.renderer.terrain.Terrain;
import engine.renderer.water.Ocean;
import engine.renderpipeline.RenderingConfig;

public class TerrainSimulation extends Simulation{

	private SkySphere skySphere;
	private Terrain terrain;
	private Ocean water;
	
	public void init()
	{
		super.init();
		
		setSceneTexture(new Texture());
		getSceneTexture().generate();
		getSceneTexture().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		setSceneDepthmap(new Texture());
		getSceneDepthmap().generate();
		getSceneDepthmap().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, Window.getWidth(), Window.getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		getSceneFBO().bind();
		getSceneFBO().setDrawBuffer(0);
		getSceneFBO().colorTextureAttachment(getSceneTexture().getId(), 0);
		getSceneFBO().depthbufferAttachment(Window.getWidth(), Window.getHeight());
		getSceneFBO().depthTextureAttachment(getSceneDepthmap().getId());
		getSceneFBO().checkStatus();
		getSceneFBO().unbind();
		
		setSkySphere(new SkySphere());
	}

	public void update()
	{
		getRoot().input();
		getRoot().update();
		skySphere.update();
		if (water != null)
			water.update();
		if (terrain != null)
			terrain.update();
	}
	
	public void render()
	{
		if (getWater() != null)
		{
			//water.renderFFT();
	
			RenderingEngine.setClipplane(getWater().getClipplane());
			
			//mirror scene to clipplane
			
			getRoot().getTransform().setScaling(1,-1,1);
			// prevent refelction distortion overlap
			skySphere.getTransform().setScaling(1.1f,-1,1.1f);
			skySphere.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() - (skySphere.getTransform().getTranslation().getY() - RenderingEngine.getClipplane().getW()));
			if (terrain != null){
				terrain.setScaleY(getTerrain().getScaleY() * -1f);
				terrain.getTransform().getLocalTranslation().setY(RenderingEngine.getClipplane().getW() - (getTerrain().getTransform().getLocalTranslation().getY() - RenderingEngine.getClipplane().getW()));
			}
			update();
			
			
			//render reflection to texture

			glViewport(0,0,Window.getWidth()/2, Window.getHeight()/2);
			
			water.getReflectionFBO().bind();
			RenderingConfig.clearScreen();
			glFrontFace(GL_CCW);
			getRoot().render();
			skySphere.render();
			if (terrain != null){
				terrain.render();
			}
			glFinish(); //important, prevent conflicts with following compute shaders
			glFrontFace(GL_CW);
			water.getReflectionFBO().unbind();
			
			// antimirror scene to clipplane
		
			getRoot().getTransform().setScaling(1,1,1);
			skySphere.getTransform().setScaling(1,1,1);
			skySphere.getTransform().getTranslation().setY(RenderingEngine.getClipplane().getW() + (RenderingEngine.getClipplane().getW() - skySphere.getTransform().getTranslation().getY()));
			if (terrain != null){
				terrain.setScaleY(getTerrain().getScaleY()/ -1f);
				terrain.getTransform().getLocalTranslation().setY(RenderingEngine.getClipplane().getW() + (RenderingEngine.getClipplane().getW() - getTerrain().getTransform().getLocalTranslation().getY()));
			}
			update();
			
			// render to refraction texture
			
			water.getRefractionFBO().bind();
			RenderingConfig.clearScreenDeepOceanRefraction();
			getRoot().render();
			if (terrain != null){
				terrain.render();
			}
			glFinish(); //important, prevent conflicts with following compute shaders
			water.getRefractionFBO().unbind();
			
			RenderingEngine.setClipplane(Constants.PLANE0);
		}
	
		glViewport(0,0,Window.getWidth(),Window.getHeight());
		getSceneFBO().bind();
		RenderingConfig.clearScreen();	
		if (water != null){
			water.render();
		}
		if (terrain != null) 
			terrain.render();
		if (!RenderingEngine.isGrid())
			skySphere.render();
		getRoot().render();
		glFinish(); //important, prevent conflicts with following compute shaders
		getSceneFBO().unbind();
	}

	public Ocean getWater() {
		return water;
	}

	public void setWater(Ocean water) {
		this.water = water;
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}

	public SkySphere getSkySphere() {
		return skySphere;
	}

	public void setSkySphere(SkySphere skyShape) {
		this.skySphere = skyShape;
	}

}
