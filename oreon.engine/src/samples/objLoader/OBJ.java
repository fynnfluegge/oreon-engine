package samples.objLoader;
import modules.glass.GlassRenderer;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

import org.lwjgl.input.Keyboard;

import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.core.Input;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.Util;

public class OBJ extends Node{

	public OBJ(){
		
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(10f,10f,10f);
		getTransform().setLocalTranslation(0,0,0);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/models/obj/nanosuit","nanosuit.obj","nanosuit.mtl");
		int size = 0;
		for (Model model : models){
			size += model.getMesh().getVertices().length;
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			Util.generateNormalsCW(model.getMesh().getVertices(), model.getMesh().getIndices());
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
				object.setRenderInfo(new RenderInfo(new CullFaceDisable(), engine.shader.blinnphong.GlassShader.getInstance()));
				renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			}
			else if (model.getMaterial().getNormalmap() != null){
				object.setRenderInfo(new RenderInfo(new CullFaceDisable(), engine.shader.blinnphong.BlinnPhongBumpShader.getInstance()));
				renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			}
			else if (model.getMaterial().getDiffusemap() != null){
				object.setRenderInfo(new RenderInfo(new CullFaceDisable(), engine.shader.blinnphong.BlinnPhongTexturedShader.getInstance()));
				renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			}
			else{
				object.setRenderInfo(new RenderInfo(new CullFaceDisable(), engine.shader.blinnphong.BlinnPhongRGBAShader.getInstance()));
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
				((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shader.basic.BasicGridShader.getInstance());
			}
		}
		else {
			for(Node gameobject : this.getChildren()){
				if((Material) ((GameObject) gameobject).getComponent("Material") != null){
					if (((Material) ((GameObject) gameobject).getComponent("Material")).getName().equals("glass"))
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shader.blinnphong.GlassShader.getInstance());	
					else if (((Material) ((GameObject) gameobject).getComponent("Material")).getNormalmap() != null)
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shader.blinnphong.BlinnPhongBumpShader.getInstance());
					else if (((Material) ((GameObject) gameobject).getComponent("Material")).getDiffusemap() != null)
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shader.blinnphong.BlinnPhongTexturedShader.getInstance());
					else
						((Renderer) ((GameObject) gameobject).getComponent("Renderer")).setShader(engine.shader.blinnphong.BlinnPhongRGBAShader.getInstance());	
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