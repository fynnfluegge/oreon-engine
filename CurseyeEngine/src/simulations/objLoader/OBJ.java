package simulations.objLoader;
import modules.glass.GlassRenderer;

import org.lwjgl.input.Keyboard;

import engine.buffers.MeshVAO;
import engine.configs.AlphaBlending;
import engine.configs.CullFaceDisable;
import engine.core.Input;
import engine.gameObject.GameObject;
import engine.gameObject.components.Material;
import engine.gameObject.components.MeshRenderer;
import engine.gameObject.components.Renderer;
import engine.math.Vec3f;
import engine.modeling.obj.Model;
import engine.modeling.obj.OBJLoader;

public class OBJ extends GameObject{

	public OBJ(){
		
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(10f,10f,10f);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("M60");
		int size = 0;
		for (Model model : models){
			size += model.getMesh().getVertices().length;
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			//Util.generateNormalsCW(model.getMesh().getVertices(), model.getMesh().getIndices());
			meshBuffer.addData(model.getMesh());
			MeshRenderer renderer = null;
			if(model.getMaterial() == null){
				Material material = new Material();
				material.setColor(new Vec3f(0.2f,0.2f,0.2f));
				material.setName("zero");
				model.setMaterial(material);
			}

			if (model.getMaterial().getName().equals("glass"))
				renderer = new MeshRenderer(meshBuffer, engine.shaders.phong.Glass.getInstance(), new AlphaBlending(0));
			else if (model.getMaterial().getNormalmap() != null)
				renderer = new MeshRenderer(meshBuffer, engine.shaders.phong.Bumpy.getInstance(), new CullFaceDisable());
			else if (model.getMaterial().getDiffusemap() != null)
				renderer = new MeshRenderer(meshBuffer, engine.shaders.phong.Textured.getInstance(), new CullFaceDisable());	
			else
				renderer = new MeshRenderer(meshBuffer, engine.shaders.phong.RGBA.getInstance(), new CullFaceDisable());	

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
		System.out.println((size * 32.0f)/1000000f + " mb");
	}
	
	public void update(){
		super.update();
		
		if (Input.getHoldingKeys().contains(Keyboard.KEY_G))
		{
			for(GameObject gameobject : this.getChildren()){
				((Renderer) gameobject.getComponent("Renderer")).setShader(engine.shaders.basic.Grid.getInstance());
			}
		}
		else {
			for(GameObject gameobject : this.getChildren()){
				if((Material) gameobject.getComponent("Material") != null){
					if (((Material) gameobject.getComponent("Material")).getName().equals("glass"))
						((Renderer) gameobject.getComponent("Renderer")).setShader(engine.shaders.phong.Glass.getInstance());	
					else if (((Material) gameobject.getComponent("Material")).getNormalmap() != null)
						((Renderer) gameobject.getComponent("Renderer")).setShader(engine.shaders.phong.Bumpy.getInstance());
					else if (((Material) gameobject.getComponent("Material")).getDiffusemap() != null)
						((Renderer) gameobject.getComponent("Renderer")).setShader(engine.shaders.phong.Textured.getInstance());
					else
						((Renderer) gameobject.getComponent("Renderer")).setShader(engine.shaders.phong.RGBA.getInstance());	
				}
			}
		}
		
		for(GameObject child: getChildren()){
			if (((Material) child.getComponent("Material")).getName().equals("glass")){
				GlassRenderer.getInstance().addChild(child);
			}
		}
		
	}
	
	public void render(){
		for(GameObject child: getChildren()){
			if (!((Material) child.getComponent("Material")).getName().equals("glass"))
				child.render();
		}
	}
	
}