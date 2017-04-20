package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.GrassShader;
import engine.buffers.MeshVAO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Plant01ClusterGroup extends InstancingObject{
	
	public Plant01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Plant_01","billboardmodel.obj","billboardmodel.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.6f), GrassShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Plant01Cluster(50,new Vec3f(1703,0,-270),getObjectData()));
		addChild(new Plant01Cluster(40,new Vec3f(1022,0,-1320),getObjectData()));
		addChild(new Plant01Cluster(40,new Vec3f(930,0,-1349),getObjectData()));
		addChild(new Plant01Cluster(40,new Vec3f(1816,0,-91),getObjectData()));
		addChild(new Plant01Cluster(40,new Vec3f(1068,0,527),getObjectData()));
		addChild(new Plant01Cluster(40,new Vec3f(1508,0,1400),getObjectData()));
	}
}
