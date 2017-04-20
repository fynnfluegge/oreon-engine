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

public class Grass01ClusterGroup extends InstancingObject{

	public Grass01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Grass_01","grassmodel.obj","grassmodel.mtl");
	
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
		
		addChild(new Grass01Cluster(200,new Vec3f(1098,0,437),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1130,0,485),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1181,0,456),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1174,0,400),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1096,0,380),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1144,0,440),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1070,0,499),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1128,0,406),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1053,0,443),getObjectData()));
		addChild(new Grass01Cluster(300,new Vec3f(1092,0,472),getObjectData()));
	}
}
