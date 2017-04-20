package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.PalmBillboardShader;
import apps.oreonworlds.shaders.plants.PalmBillboardShadowShader;
import apps.oreonworlds.shaders.plants.PalmShader;
import apps.oreonworlds.shaders.plants.PalmShadowShader;
import engine.buffers.MeshVAO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.configs.CullFaceDisable;
import engine.geometry.Vertex;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Palm01ClusterGroup extends InstancingObject{
	
	public Palm01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Palm_01","Palma 001.obj","Palma 001.mtl");
		Model[] billboards = new OBJLoader().load("./res/oreonworlds/assets/plants/Palm_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmShader.getInstance(), PalmShadowShader.getInstance()));

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
				vertex.setPos(vertex.getPos().mul(135));
				vertex.getPos().setX(vertex.getPos().getX()*1.1f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1.1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.4f), PalmBillboardShader.getInstance(), PalmBillboardShadowShader.getInstance()));
			
			object.setMaterial(billboard.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
	
		addChild(new Palm01Cluster(10,new Vec3f(817,0,84),getObjectData()));
		addChild(new Palm01Cluster(10,new Vec3f(1003,0,-78),getObjectData()));
		addChild(new Palm01Cluster(10,new Vec3f(1218,0,-211),getObjectData()));
		addChild(new Palm01Cluster(10,new Vec3f(1322,0,-222),getObjectData()));
		addChild(new Palm01Cluster(10,new Vec3f(1292,0,455),getObjectData()));
//		addChild(new Palm01Cluster(10,new Vec3f(1151,0,654),getObjectData()));
		addChild(new Palm01Cluster(10,new Vec3f(1073,0,787),getObjectData()));
//		addChild(new Palm01Cluster(10,new Vec3f(1273,0,-1141),getObjectData()));
//		addChild(new Palm01Cluster(10,new Vec3f(-1380,0,-79),getObjectData()));
//		addChild(new Palm01Cluster(10,new Vec3f(-1738,0,-172),getObjectData()));
//		addChild(new Palm01Cluster(10,new Vec3f(-1591,0,214),getObjectData()));

	}
}
