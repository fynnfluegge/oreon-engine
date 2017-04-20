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
import engine.scenegraph.components.RenderInfo;
import engine.utils.Util;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Tree01ClusterGroup extends InstancingObject{
	
	public Tree01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_01","tree01.obj","tree01.mtl");
		Model[] billboards = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			
			if (model.equals(models[0])){
				model.getMesh().setTangentSpace(true);
				Util.generateTangentsBitangents(model.getMesh());
			}
			else
				model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPos().setX(vertex.getPos().getX()*1.2f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1.2f);
			}
			
			meshBuffer.addData(model.getMesh());

			if (model.equals(models[0]))
				object.setRenderInfo(new RenderInfo(new Default(), TreeTrunkShader.getInstance(), TreeShadowShader.getInstance()));
			else
				object.setRenderInfo(new RenderInfo(new AlphaTest(0.1f), TreeLeavesShader.getInstance(), TreeShadowShader.getInstance()));
							
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		for (Model billboard : billboards){	
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPos(vertex.getPos().mul(7.4f));
				vertex.getPos().setX(vertex.getPos().getX()*1f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.9f), TreeBillboardShader.getInstance(), TreeBillboardShadowShader.getInstance()));
			
			object.setMaterial(billboard.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
	
		addChild(new Tree01Cluster(10,new Vec3f(1116,0,-1119),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(930, 0,-1041),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(1012,0,-1154),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(812,0,-1084),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(909,0,-1187),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(1463,0,1589),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(-662,0,-11),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(1240,0,1664),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(-702,0,306),getObjectData()));
		addChild(new Tree01Cluster(10,new Vec3f(1375,0,1609),getObjectData()));
	}
}
