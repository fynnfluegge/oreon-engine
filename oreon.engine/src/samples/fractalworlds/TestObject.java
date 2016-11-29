package samples.fractalworlds;

import engine.buffers.MeshVAO;
import engine.configs.Default;
import engine.core.Util;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;

public class TestObject extends Node{

	
public TestObject() {
	getTransform().setLocalRotation(0, 0, 0);
	getTransform().setLocalScaling(10f,10f,10f);
	getTransform().setLocalTranslation(100,Terrain.getInstance().getTerrainHeight(100, 100),100);
	OBJLoader loader = new OBJLoader();
	Model[] models = loader.load("nanosuit");
	int size = 0;
	for (Model model : models){
		size += model.getMesh().getVertices().length;
		GameObject object = new GameObject();
		MeshVAO meshBuffer = new MeshVAO();
//		Util.generateNormalsCW(model.getMesh().getVertices(), model.getMesh().getIndices());
		Util.generateTangentsBitangents(model.getMesh());
		model.getMesh().setTangentSpace(true);
		meshBuffer.addData(model.getMesh());
		Renderer renderer = null;
		
		object.setRenderInfo(new RenderInfo(new Default(), 
											engine.shaders.blinnphong.TBN.getInstance(),
											TestObjectShadowShader.getInstance()));
		renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

		object.addComponent("Material", model.getMaterial());
		object.addComponent("Renderer", renderer);
		addChild(object);
	}
	System.out.println((size * 32.0f)/1000000f + " mb");
}
	
}
