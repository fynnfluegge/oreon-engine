package apps.samples.fractalworlds;

import engine.buffers.MeshVAO;
import engine.configs.Default;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.Util;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;

public class TestObject extends Node{

	
	public TestObject() {
	
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(1f,1f,1f);
		getTransform().setLocalTranslation(0,Terrain.getInstance().getTerrainHeight(0,0),0);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/models/obj/nanosuit","nanosuit.obj","nanosuit.mtl");

		for (Model model : models){
			
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

			object.setRenderInfo(new RenderInfo(new Default(), engine.shader.blinnphong.BlinnPhongBumpShader.getInstance(), TestObjectShadowShader.getInstance()));
			renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
	
}
