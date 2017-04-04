package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.TreeBillboardShader;
import apps.oreonworlds.shaders.plants.TreeBillboardShadowShader;
import apps.oreonworlds.shaders.plants.TreeLeavesShader;
import apps.oreonworlds.shaders.plants.TreeShadowShader;
import apps.oreonworlds.shaders.plants.TreeTrunkShader;
import engine.buffers.MeshVAO;
import engine.configs.AlphaTest;
import engine.configs.AlphaTestCullFaceDisable;
import engine.configs.Default;
import engine.geometry.Vertex;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.Util;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Tree02ClusterGroup extends InstancingObject{

	public Tree02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_02","tree02.obj","tree02.mtl");
		Model[] billboards = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_02","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			
			if (model.equals(models[0])){
				model.getMesh().setTangentSpace(true);
				Util.generateTangentsBitangents(model.getMesh());
			}
			else
				model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			model.getMesh().setInstances(8);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPos().setX(vertex.getPos().getX()*1.2f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1.2f);
			}
			
			meshBuffer.addData(model.getMesh());

			if (model.equals(models[0]))
				object.setRenderInfo(new RenderInfo(new Default(), TreeTrunkShader.getInstance(), TreeShadowShader.getInstance()));
			else
				object.setRenderInfo(new RenderInfo(new AlphaTest(0.6f), TreeLeavesShader.getInstance(), TreeShadowShader.getInstance()));
				
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			
			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			getObjects().add(object);
		}
		
		for (Model billboard : billboards){	
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			billboard.getMesh().setInstances(0);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPos(vertex.getPos().mul(2.4f));
				vertex.getPos().setX(vertex.getPos().getX()*1f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.4f), TreeBillboardShader.getInstance(), TreeBillboardShadowShader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
			
			object.addComponent("Material", billboard.getMaterial());
			object.addComponent("Renderer", renderer);
			getObjects().add(object);
		}
	
		addChild(new Tree02Cluster(6,new Vec3f(1627,0,-1805),getObjects()));
		addChild(new Tree02Cluster(6,new Vec3f(1878,0,-1907),getObjects()));
		addChild(new Tree02Cluster(8,new Vec3f( 270,0, 1136),getObjects()));
	}

}
