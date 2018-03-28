package org.oreon.modules.gl.postprocessfilter.lensflare;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.light.Light;
import org.oreon.core.light.LightHandler;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec2f;

public class LensFlare {
	
	private List<LensFlareTexturePanel> lensFlareTexturePanels = new ArrayList<LensFlareTexturePanel>();
	private Vec2f windowMidPos = new Vec2f(EngineContext.getWindow().getWidth()/2f, EngineContext.getWindow().getHeight()/2f);
	
	private float occlusionThreshold = 80000f;
	
	public LensFlare(){
		
		LensFlareTexturePanel texturePanel0 = new LensFlareTexturePanel();
		texturePanel0.setTexture(new Texture2D("textures/lens_flare/tex4.png"));
		texturePanel0.getTexture().bind();
		texturePanel0.getTexture().bilinearFilter();
		texturePanel0.getOrthoTransform().setScaling(200,200,0);
		texturePanel0.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel0.getOrthoTransform().getWorldMatrix()));
		texturePanel0.setSpacing(0.06f);
		texturePanel0.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel0);
		
		LensFlareTexturePanel texturePanel1 = new LensFlareTexturePanel();
		texturePanel1.setTexture(new Texture2D("textures/lens_flare/tex01.png"));
		texturePanel1.getTexture().bind();
		texturePanel1.getTexture().bilinearFilter();
		texturePanel1.getOrthoTransform().setScaling(400,400,0);
		texturePanel1.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel1.getOrthoTransform().getWorldMatrix()));
		texturePanel1.setSpacing(0.0f);
		texturePanel1.setBrightness(0.6f);
		lensFlareTexturePanels.add(texturePanel1);
		
		LensFlareTexturePanel texturePanel2 = new LensFlareTexturePanel();
		texturePanel2.setTexture(new Texture2D("textures/lens_flare/tex2.png"));
		texturePanel2.getTexture().bind();
		texturePanel2.getTexture().bilinearFilter();
		texturePanel2.getOrthoTransform().setScaling(250,250,0);
		texturePanel2.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel2.getOrthoTransform().getWorldMatrix()));
		texturePanel2.setSpacing(0.2f);
		texturePanel2.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel2);
		
		LensFlareTexturePanel texturePanel3 = new LensFlareTexturePanel();
		texturePanel3.setTexture(new Texture2D("textures/lens_flare/tex7.png"));
		texturePanel3.getTexture().bind();
		texturePanel3.getTexture().bilinearFilter();
		texturePanel3.getOrthoTransform().setScaling(200,200,0);
		texturePanel3.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel3.getOrthoTransform().getWorldMatrix()));
		texturePanel3.setSpacing(0.4f);
		texturePanel3.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel3);
		
		LensFlareTexturePanel texturePanel4 = new LensFlareTexturePanel();
		texturePanel4.setTexture(new Texture2D("textures/lens_flare/tex5.png"));
		texturePanel4.getTexture().bind();
		texturePanel4.getTexture().bilinearFilter();
		texturePanel4.getOrthoTransform().setScaling(100,100,0);
		texturePanel4.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel4.getOrthoTransform().getWorldMatrix()));
		texturePanel4.setSpacing(0.6f);
		texturePanel4.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel4);
		
		LensFlareTexturePanel texturePanel5 = new LensFlareTexturePanel();
		texturePanel5.setTexture(new Texture2D("textures/lens_flare/tex3.png"));
		texturePanel5.getTexture().bind();
		texturePanel5.getTexture().bilinearFilter();
		texturePanel5.getOrthoTransform().setScaling(100,100,0);
		texturePanel5.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel5.getOrthoTransform().getWorldMatrix()));
		texturePanel5.setSpacing(0.8f);
		texturePanel5.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel5);
		
		LensFlareTexturePanel texturePanel6 = new LensFlareTexturePanel();
		texturePanel6.setTexture(new Texture2D("textures/lens_flare/tex9.png"));
		texturePanel6.getTexture().bind();
		texturePanel6.getTexture().bilinearFilter();
		texturePanel6.getOrthoTransform().setScaling(100,100,0);
		texturePanel6.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel6.getOrthoTransform().getWorldMatrix()));
		texturePanel6.setSpacing(1.1f);
		texturePanel6.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel6);
		
		LensFlareTexturePanel texturePanel7 = new LensFlareTexturePanel();
		texturePanel7.setTexture(new Texture2D("textures/lens_flare/tex1.png"));
		texturePanel7.getTexture().bind();
		texturePanel7.getTexture().bilinearFilter();
		texturePanel7.getOrthoTransform().setScaling(100,100,0);
		texturePanel7.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel7.getOrthoTransform().getWorldMatrix()));
		texturePanel7.setSpacing(1.3f);
		texturePanel7.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel7);
		
		LensFlareTexturePanel texturePanel8 = new LensFlareTexturePanel();
		texturePanel8.setTexture(new Texture2D("textures/lens_flare/tex4.png"));
		texturePanel8.getTexture().bind();
		texturePanel8.getTexture().bilinearFilter();
		texturePanel8.getOrthoTransform().setScaling(300,300,0);
		texturePanel8.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel8.getOrthoTransform().getWorldMatrix()));
		texturePanel8.setSpacing(1.7f);
		texturePanel8.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel8);
		
		LensFlareTexturePanel texturePanel9 = new LensFlareTexturePanel();
		texturePanel9.setTexture(new Texture2D("textures/lens_flare/tex8.png"));
		texturePanel9.getTexture().bind();
		texturePanel9.getTexture().bilinearFilter();
		texturePanel9.getOrthoTransform().setScaling(400,400,0);
		texturePanel9.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel9.getOrthoTransform().getWorldMatrix()));
		texturePanel9.setSpacing(2.0f);
		texturePanel9.setBrightness(0.2f);
		lensFlareTexturePanels.add(texturePanel9);
	}
	
	public void render(){
		
		if (EngineContext.getConfig().isWireframe())
			return;
		
		for (Light light : LightHandler.getLights()){
			
			Vec2f lightScreenSpacePos = light.getScreenSpacePosition();
			
			if (lightScreenSpacePos == null){
				return;
			}
			
			Vec2f sunToWindowCenter = windowMidPos.sub(lightScreenSpacePos);
			
			float brightness = 1 - sunToWindowCenter.div(new Vec2f(EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight())).length();
			
			for (LensFlareTexturePanel lensFlareTexture : lensFlareTexturePanels){
				
				lensFlareTexture.getOrthoTransform().getTranslation().setX(
						light.getScreenSpacePosition().getX() + (sunToWindowCenter.getX() * lensFlareTexture.getSpacing()) 
						- lensFlareTexture.getOrthoTransform().getScaling().getX()/2f);
				lensFlareTexture.getOrthoTransform().getTranslation().setY(
						light.getScreenSpacePosition().getY() + (sunToWindowCenter.getY() * lensFlareTexture.getSpacing())
						- lensFlareTexture.getOrthoTransform().getScaling().getY()/2f);
				lensFlareTexture.setOrthographicMatrix(
					new Matrix4f().Orthographic2D().mul(lensFlareTexture.getOrthoTransform().getWorldMatrix()));
			
				
				lensFlareTexture.setTransparency((light.getOcclusionQuery().getOcclusionFactor()/occlusionThreshold) * brightness);
				lensFlareTexture.render();
			}
		}
	}
}
