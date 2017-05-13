package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.BushShader;
import apps.oreonworlds.shaders.plants.BushShadowShader;
import engine.buffers.MeshVAO;
import engine.configs.AlphaTestCullFaceDisable;
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

			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.1f), BushShader.getInstance(), BushShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Bush01Cluster(10,new Vec3f(1218,0,-503),getObjectData()));
		addChild(new Bush01Cluster(10,new Vec3f(925,0,-1022),getObjectData()));
	}
}
