package org.oreon.engine.apps.oreonworlds.assets.rocks;

import org.oreon.engine.apps.oreonworlds.shaders.rocks.RockHighPolyShader;
import org.oreon.engine.apps.oreonworlds.shaders.rocks.RockShadowShader;
import org.oreon.engine.engine.buffers.MeshVAO;
import org.oreon.engine.engine.configs.Default;
import org.oreon.engine.engine.math.Vec3f;
import org.oreon.engine.engine.scenegraph.components.RenderInfo;
import org.oreon.engine.engine.utils.Util;
import org.oreon.engine.modules.instancing.InstancedDataObject;
import org.oreon.engine.modules.instancing.InstancingObject;
import org.oreon.engine.modules.modelLoader.obj.Model;
import org.oreon.engine.modules.modelLoader.obj.OBJLoader;

public class Rock02ClusterGroup extends InstancingObject{

	public Rock02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/rocks/Rock_02","rock02.obj","rock02.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(true);
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new Default(),RockHighPolyShader.getInstance(), RockShadowShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Rock02Cluster(10,new Vec3f(954,0,-30),getObjectData()));
		addChild(new Rock02Cluster(10,new Vec3f(1240,0,-292),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(1062,0,446),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(1123,0,494),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(1759,0,-1225),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(765,0,162),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(654,0,907),getObjectData()));
		addChild(new Rock02Cluster(8,new Vec3f(960,0,-840),getObjectData()));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
