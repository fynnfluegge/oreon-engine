package xamples.objLoader;
import modules.glass.GlassRenderer;

import org.lwjgl.input.Keyboard;

import engine.buffers.MeshVAO;
import engine.configs.AlphaBlending;
import engine.configs.CullFaceDisable;
import engine.configs.Default;
import engine.core.Input;
import engine.core.Util;
import engine.geometrics.obj.Model;
import engine.geometrics.obj.OBJLoader;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;

public class OBJ extends Node{

	public OBJ(){
		
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(100f,100f,100f);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("nanosuit");
		int size = 0;
		for (Model model : models){
			size += model.getMesh().getVertices().length;
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
//			Util.generateNormalsCW(model.getMesh().getVertices(), model.getMesh().getIndices());
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setTangentSpace(true);
			meshBuffer.addData(model.getMesh());
			Renderer renderer = null;
			if(model.getMaterial() == null){
				Material material = new Material();
				material.setColor(new Vec3f(0.2f,0.2f,0.2f));
				material.setName("zero");
				model.setMaterial(material);
			}

			if (model.getMaterial().getName().equals("glass")){
				object.setRenderInfo(new RenderInfo(new AlphaBlending(0), engine.shaders.phong.Glass.getInstance()));
				renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			}
			else if (model.getMaterial().getNormalmap() != null){
				object.setRenderInfo(new RenderInfo(new Default(), engine.shaders.phong.Bumpy.getInstance()));
				renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			}
			else if (model.getMaterial().getDiffusemap() != null){
				object.setRenderInfo(new RenderInfo(new Default(), engine.shaders.phong.Textured.getInstance()));
				renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			}
			else{
				object.setRenderInfo(new RenderInfo(new CullFaceDisable(), engine.shaders.phong.RGBA.getInstance()));
				renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			}

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
			for(Node gameobject : this.getChildren()){
				((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shaders.basic.Grid.getInstance());
			}
		}
		else {
			for(Node gameobject : this.getChildren()){
				if((Material) ((GameObject) gameobject).getComponent("Material") != null){
					if (((Material) ((GameObject) gameobject).getComponent("Material")).getName().equals("glass"))
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shaders.phong.Glass.getInstance());	
					else if (((Material) ((GameObject) gameobject).getComponent("Material")).getNormalmap() != null)
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shaders.phong.Bumpy.getInstance());
					else if (((Material) ((GameObject) gameobject).getComponent("Material")).getDiffusemap() != null)
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shaders.phong.Textured.getInstance());
					else
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shaders.phong.RGBA.getInstance());	
				}
			}
		}
		
		for(Node child: getChildren()){
			if (((Material) ((GameObject) child).getComponent("Material")).getName().equals("glass")){
				GlassRenderer.getInstance().addChild(child);
			}
		}
		
	}
	
	public void render(){
		for(Node child: getChildren()){
			if (!((Material) ((GameObject) child).getComponent("Material")).getName().equals("glass"))
				child.render();
		}
	}
	
}