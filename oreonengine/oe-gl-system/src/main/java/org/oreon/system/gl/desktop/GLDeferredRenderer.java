package org.oreon.system.gl.desktop;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;

import java.nio.IntBuffer;

import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.deferred.GBuffer;
import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.shadow.ShadowMaps;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderingEngine;
import org.oreon.core.system.Window;
import org.oreon.core.texture.Texture;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.gui.elements.TexturePanel;
import org.oreon.modules.gl.terrain.Terrain;

public class GLDeferredRenderer implements RenderingEngine{

	private Window window;
	private TexturePanel fullScreenTexture;
	
	private GLFramebuffer fbo;
	private GLFramebuffer multisampledFbo;
	private GBuffer gbuffer;
	
	private static ShadowMaps shadowMaps;
	
	
	@Override
	public void init() {
		Default.init();
		window = CoreSystem.getInstance().getWindow();
		fullScreenTexture = new TexturePanel();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(4);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.flip();
		
		multisampledFbo = new GLFramebuffer();
		multisampledFbo.bind();
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 0);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 1);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 2);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 3);
		multisampledFbo.createDepthBufferMultisampleAttachment(Constants.MULTISAMPLES);
		multisampledFbo.setDrawBuffers(drawBuffers);
		multisampledFbo.checkStatus();
		multisampledFbo.unbind();
		
		System.out.print("mfbo");
		
		gbuffer = new GBuffer(window.getWidth(), window.getHeight());
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(gbuffer.getAlbedoTexture().getId(),0);
		fbo.createColorTextureAttachment(gbuffer.getWorldPositionTexture().getId(),1);
		fbo.createColorTextureAttachment(gbuffer.getNormalTexture().getId(),2);
		fbo.createColorTextureAttachment(gbuffer.getSpecularEmissionTexture().getId(),3);
		fbo.createDepthTextureAttachment(gbuffer.getSceneDepthmap().getId());
		fbo.checkStatus();
		fbo.unbind();
		
		System.out.print("fbo");
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
		// blit albedoTexture
		multisampledFbo.blitFrameBuffer(0,0,fbo.getId());
		// blit light Scattering SceneTexture
		multisampledFbo.blitFrameBuffer(1,1,fbo.getId());
		
		fullScreenTexture.render();
		
		// draw into OpenGL window
		window.draw();
	}
	@Override
	public void update() {
		CoreSystem.getInstance().getScenegraph().update();		
	}
	@Override
	public void shutdown() {
		CoreSystem.getInstance().getScenegraph().shutdown();
	}
	@Override
	public boolean isGrid() {
		// TODO Auto-generated method stub
		return false;
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
	public Quaternion getClipplane() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public float getSightRangeFactor() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setClipplane(Quaternion plane) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setGrid(boolean flag) {
		// TODO Auto-generated method stub
		
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
}
