package modules.postProcessingEffects.lensFlare;

import java.util.ArrayList;
import java.util.List;

import engine.core.Window;
import engine.math.Matrix4f;
import engine.math.Vec2f;
import engine.textures.Texture2D;
import modules.lighting.Light;
import modules.lighting.LightHandler;

public class LensFlare {
	
	private List<LensFlareTexturePanel> lensFlareTexturePanels = new ArrayList<LensFlareTexturePanel>();
	private Vec2f windowMidPos = new Vec2f(Window.getInstance().getWidth()/2f, Window.getInstance().getHeight()/2f);
	
	public LensFlare(){
		LensFlareTexturePanel texturePanel0 = new LensFlareTexturePanel();
		texturePanel0.setTexture(new Texture2D("./res/textures/lens_flare/tex4.png"));
		texturePanel0.getOrthoTransform().setScaling(200,200,0);
		texturePanel0.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel0.getOrthoTransform().getWorldMatrix()));
		texturePanel0.setSpacing(0.06f);
		lensFlareTexturePanels.add(texturePanel0);
		
		LensFlareTexturePanel texturePanel1 = new LensFlareTexturePanel();
		texturePanel1.setTexture(new Texture2D("./res/textures/lens_flare/tex01.png"));
		texturePanel1.getOrthoTransform().setScaling(400,400,0);
		texturePanel1.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel1.getOrthoTransform().getWorldMatrix()));
		texturePanel1.setSpacing(0.0f);
		lensFlareTexturePanels.add(texturePanel1);
		
		LensFlareTexturePanel texturePanel2 = new LensFlareTexturePanel();
		texturePanel2.setTexture(new Texture2D("./res/textures/lens_flare/tex2.png"));
		texturePanel2.getOrthoTransform().setScaling(250,250,0);
		texturePanel2.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel2.getOrthoTransform().getWorldMatrix()));
		texturePanel2.setSpacing(0.2f);
		lensFlareTexturePanels.add(texturePanel2);
		
		LensFlareTexturePanel texturePanel3 = new LensFlareTexturePanel();
		texturePanel3.setTexture(new Texture2D("./res/textures/lens_flare/tex7.png"));
		texturePanel3.getOrthoTransform().setScaling(200,200,0);
		texturePanel3.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel3.getOrthoTransform().getWorldMatrix()));
		texturePanel3.setSpacing(0.4f);
		lensFlareTexturePanels.add(texturePanel3);
		
		LensFlareTexturePanel texturePanel4 = new LensFlareTexturePanel();
		texturePanel4.setTexture(new Texture2D("./res/textures/lens_flare/tex5.png"));
		texturePanel4.getOrthoTransform().setScaling(100,100,0);
		texturePanel4.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel4.getOrthoTransform().getWorldMatrix()));
		texturePanel4.setSpacing(0.6f);
		lensFlareTexturePanels.add(texturePanel4);
		
		LensFlareTexturePanel texturePanel5 = new LensFlareTexturePanel();
		texturePanel5.setTexture(new Texture2D("./res/textures/lens_flare/tex3.png"));
		texturePanel5.getOrthoTransform().setScaling(100,100,0);
		texturePanel5.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel5.getOrthoTransform().getWorldMatrix()));
		texturePanel5.setSpacing(0.8f);
		lensFlareTexturePanels.add(texturePanel5);
		
		LensFlareTexturePanel texturePanel6 = new LensFlareTexturePanel();
		texturePanel6.setTexture(new Texture2D("./res/textures/lens_flare/tex9.png"));
		texturePanel6.getOrthoTransform().setScaling(100,100,0);
		texturePanel6.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel6.getOrthoTransform().getWorldMatrix()));
		texturePanel6.setSpacing(1.1f);
		lensFlareTexturePanels.add(texturePanel6);
		
		LensFlareTexturePanel texturePanel7 = new LensFlareTexturePanel();
		texturePanel7.setTexture(new Texture2D("./res/textures/lens_flare/tex1.png"));
		texturePanel7.getOrthoTransform().setScaling(100,100,0);
		texturePanel7.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel7.getOrthoTransform().getWorldMatrix()));
		texturePanel7.setSpacing(1.3f);
		lensFlareTexturePanels.add(texturePanel7);
		
		LensFlareTexturePanel texturePanel8 = new LensFlareTexturePanel();
		texturePanel8.setTexture(new Texture2D("./res/textures/lens_flare/tex4.png"));
		texturePanel8.getOrthoTransform().setScaling(300,300,0);
		texturePanel8.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel8.getOrthoTransform().getWorldMatrix()));
		texturePanel8.setSpacing(1.7f);
		lensFlareTexturePanels.add(texturePanel8);
		
		LensFlareTexturePanel texturePanel9 = new LensFlareTexturePanel();
		texturePanel9.setTexture(new Texture2D("./res/textures/lens_flare/tex8.png"));
		texturePanel9.getOrthoTransform().setScaling(400,400,0);
		texturePanel9.setOrthographicMatrix(new Matrix4f().Orthographic2D().mul(texturePanel9.getOrthoTransform().getWorldMatrix()));
		texturePanel9.setSpacing(2.0f);
		lensFlareTexturePanels.add(texturePanel9);
	}
	
	public void render(){
		for (Light light : LightHandler.getLights()){
			
			Vec2f lightScreenSpacePos = light.getScreenSpacePosition();
			
			if (lightScreenSpacePos == null){
				return;
			}
			
			Vec2f sunToWindowCenter = windowMidPos.sub(lightScreenSpacePos);
			
			float brightness = 1 - sunToWindowCenter.div(new Vec2f(Window.getInstance().getWidth(), Window.getInstance().getHeight())).length();
			brightness *= 0.5f;
			
			for (LensFlareTexturePanel lensFlareTexture : lensFlareTexturePanels){
				lensFlareTexture.getOrthoTransform().getTranslation().setX(
						light.getScreenSpacePosition().getX() + (sunToWindowCenter.getX() * lensFlareTexture.getSpacing()) 
						- lensFlareTexture.getOrthoTransform().getScaling().getX()/2f);
				lensFlareTexture.getOrthoTransform().getTranslation().setY(
						light.getScreenSpacePosition().getY() + (sunToWindowCenter.getY() * lensFlareTexture.getSpacing())
						- lensFlareTexture.getOrthoTransform().getScaling().getY()/2f);
				lensFlareTexture.setOrthographicMatrix(
					new Matrix4f().Orthographic2D().mul(lensFlareTexture.getOrthoTransform().getWorldMatrix()));
			
				
				lensFlareTexture.setTransparency((light.getOcclusionQuery().getOcclusionFactor()/80000f) * brightness);
				lensFlareTexture.render();
			}
		}
	}
}
