package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.BushShader;
import apps.oreonworlds.shaders.plants.BushShadowShader;
import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Bush01ClusterGroup extends InstancingObject{

	public Bush01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Bush_01","Bush_01.obj","Bush_01.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), BushShader.getInstance(), BushShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Bush01Cluster(20,new Vec3f(-2082,0,2881),getObjectData()));
		addChild(new Bush01Cluster(40,new Vec3f(-2125,0,2891),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(-2188,0,2894),getObjectData()));
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}
