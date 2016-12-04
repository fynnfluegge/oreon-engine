package oreonworlds.plants;

import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import oreonworlds.shaders.PalmBushShader;
import oreonworlds.shaders.PalmBushShadwoShader;

public class PalmBush extends Node{
	
	
	public PalmBush(Vec3f translation){
		
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(1f,1f,1f);
		getTransform().setLocalTranslation(translation);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/oreonworlds/plants/PalmBush","Palm_01.obj","Palm_01.mtl");

		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmBushShader.getInstance(), PalmBushShadwoShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
}
