package org.oreon.engine.apps.oreonworlds.assets.plants;

import org.oreon.engine.apps.oreonworlds.shaders.plants.PalmBillboardShader;
import org.oreon.engine.apps.oreonworlds.shaders.plants.PalmBillboardShadowShader;
import org.oreon.engine.apps.oreonworlds.shaders.plants.PalmShader;
import org.oreon.engine.apps.oreonworlds.shaders.plants.PalmShadowShader;
import org.oreon.engine.engine.buffers.MeshVAO;
import org.oreon.engine.engine.configs.AlphaTestCullFaceDisable;
import org.oreon.engine.engine.configs.CullFaceDisable;
import org.oreon.engine.engine.geometry.Vertex;
import org.oreon.engine.engine.math.Vec3f;
import org.oreon.engine.engine.scenegraph.components.RenderInfo;
import org.oreon.engine.modules.instancing.InstancedDataObject;
import org.oreon.engine.modules.instancing.InstancingObject;
import org.oreon.engine.modules.modelLoader.obj.Model;
import org.oreon.engine.modules.modelLoader.obj.OBJLoader;

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
	
		addChild(new Palm01Cluster(4,new Vec3f(1272,0,409),getObjectData()));
		addChild(new Palm01Cluster(4,new Vec3f(961,0,503),getObjectData()));
		addChild(new Palm01Cluster(4,new Vec3f(1189,0,530),getObjectData()));
		addChild(new Palm01Cluster(4,new Vec3f(1111,0,561),getObjectData()));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
