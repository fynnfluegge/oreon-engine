package org.oreon.system.gl.desktop;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.deferred.DeferredRenderer;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.shadow.ParallelSplitShadowMaps;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderingEngine;
import org.oreon.core.system.Window;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.gui.GUI;
import org.oreon.modules.gl.gui.GUIs.VoidGUI;
import org.oreon.modules.gl.gui.elements.TexturePanel;
import org.oreon.modules.gl.terrain.Terrain;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL11.GL_RGBA8;

public class GLDeferredRenderingEngine implements RenderingEngine{

	private Window window;
	private TexturePanel fullScreenTexture;
	
	private GLFramebuffer fbo;
	private GLFramebuffer multisampledFbo;
	private DeferredRenderer deferredRenderer;
	private GUI gui;
	
	private boolean grid;
	
	private Quaternion clipplane;
	private static ParallelSplitShadowMaps shadowMaps;
	
	@Override
	public void init() {
		
		Default.init();
		window = CoreSystem.getInstance().getWindow();
		
		if (gui != null){
			gui.init();
		}
		else {
			gui = new VoidGUI();
		}
		
		
		fullScreenTexture = new TexturePanel();
		shadowMaps = new ParallelSplitShadowMaps();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(4);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.flip();
		
		multisampledFbo = new GLFramebuffer();
		multisampledFbo.bind();
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 0, window.getWidth(), window.getHeight(), GL_RGBA8);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 1, window.getWidth(), window.getHeight(), GL_RGBA32F);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 2, window.getWidth(), window.getHeight(), GL_RGBA32F);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 3, window.getWidth(), window.getHeight(), GL_RGBA8);
		multisampledFbo.createDepthBufferMultisampleAttachment(Constants.MULTISAMPLES, window.getWidth(), window.getHeight());
		multisampledFbo.setDrawBuffers(drawBuffers);
		multisampledFbo.checkStatus();
		multisampledFbo.unbind();
		
		deferredRenderer = new DeferredRenderer(window.getWidth(), window.getHeight());
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(deferredRenderer.getGbuffer().getAlbedoTexture().getId(),0);
		fbo.createColorTextureAttachment(deferredRenderer.getGbuffer().getWorldPositionTexture().getId(),1);
		fbo.createColorTextureAttachment(deferredRenderer.getGbuffer().getNormalTexture().getId(),2);
		fbo.createColorTextureAttachment(deferredRenderer.getGbuffer().getSpecularEmissionTexture().getId(),3);
		fbo.createDepthTextureAttachment(deferredRenderer.getGbuffer().getSceneDepthmap().getId());
		fbo.checkStatus();
		fbo.unbind();
	}
	@Override
	public void render() {

		GLDirectionalLight.getInstance().update();
		if (CoreSystem.getInstance().getScenegraph().getCamera().isCameraMoved()){
			if (CoreSystem.getInstance().getScenegraph().terrainExists()){
				((Terrain) CoreSystem.getInstance().getScenegraph().getTerrain()).updateQuadtree();
			}
		}
		
		setClipplane(Constants.PLANE0);
		Default.clearScreen();
		
		//render shadow maps
		shadowMaps.getFBO().bind();
		shadowMaps.getConfig().enable();
		glClear(GL_DEPTH_BUFFER_BIT);
		CoreSystem.getInstance().getScenegraph().renderShadows();
		shadowMaps.getConfig().disable();
		shadowMaps.getFBO().unbind();
		
		// render scene/deferred maps
		multisampledFbo.bind();
		Default.clearScreen();
		CoreSystem.getInstance().getScenegraph().render();
		multisampledFbo.unbind();
		
		fbo.bind();
		Default.clearScreen();
		fbo.unbind();

		multisampledFbo.blitFrameBuffer(0,0,fbo.getId(), window.getWidth(), window.getHeight());
		multisampledFbo.blitFrameBuffer(1,1,fbo.getId(), window.getWidth(), window.getHeight());
		multisampledFbo.blitFrameBuffer(2,2,fbo.getId(), window.getWidth(), window.getHeight());
		multisampledFbo.blitFrameBuffer(3,3,fbo.getId(), window.getWidth(), window.getHeight());
		
		deferredRenderer.render();
		
		fullScreenTexture.setTexture(deferredRenderer.getGbuffer().getNormalTexture());
		
		fullScreenTexture.render();
		
		gui.render();
		
		// draw into OpenGL window
		window.draw();
	}
	@Override
	public void update() {
		
		if (CoreSystem.getInstance().getInput().isKeyPushed(GLFW.GLFW_KEY_G)){
			if (isGrid())
				setGrid(false);
			else
				setGrid(true);
		}
		
		CoreSystem.getInstance().getScenegraph().update();		
	}
	@Override
	public void shutdown() {
		CoreSystem.getInstance().getScenegraph().shutdown();
	}
	@Override
	public boolean isGrid() {
		
		return grid;
	}
	@Override
	public boolean isCameraUnderWater() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isWaterReflection() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isWaterRefraction() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isBloomEnabled() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Framebuffer getMultisampledFbo() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Texture getSceneDepthmap() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public float getSightRangeFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setGrid(boolean flag) {

		grid = flag;
	}
	@Override
	public void setWaterRefraction(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setWaterReflection(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setCameraUnderWater(boolean flag) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setSightRangeFactor(float range) {
		// TODO Auto-generated method stub
		
	}
	public static ParallelSplitShadowMaps getShadowMaps() {
		return shadowMaps;
	}
	public static void setShadowMaps(ParallelSplitShadowMaps shadowMaps) {
		GLDeferredRenderingEngine.shadowMaps = shadowMaps;
	}
	
	public Quaternion getClipplane() {
		return clipplane;
	}

	public void setClipplane(Quaternion clipplane) {
		this.clipplane = clipplane;
	}
	public GUI getGui() {
		return gui;
	}
	public void setGui(GUI gui) {
		this.gui = gui;
	} 
}
