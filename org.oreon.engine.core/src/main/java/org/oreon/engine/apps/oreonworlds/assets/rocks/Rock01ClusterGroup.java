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

public class Rock01ClusterGroup extends InstancingObject{
	
	public Rock01ClusterGroup(){

		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/rocks/Rock_01","rock01.obj","rock01.mtl");
	
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
		
		addChild(new Rock01Cluster(8,new Vec3f(1018,0,-67),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1303,0,-206),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1265,0,-1144),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(555,0,1231),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(1285,0,488),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(819,0,82),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1504,0,1494),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(507,0,1325),getObjectData()));
		addChild(new Rock01Cluster(5,new Vec3f(925,0,-1024),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(778,0,737),getObjectData()));
		addChild(new Rock01Cluster(5,new Vec3f(949,0,-971),getObjectData()));
		addChild(new Rock01Cluster(5,new Vec3f(861,0,-1035),getObjectData()));
		addChild(new Rock01Cluster(5,new Vec3f(797,0,-1048),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1183,0,-493),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(1027,0,-1122),getObjectData()));
		addChild(new Rock01Cluster(12,new Vec3f(1549,0,-1264),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(788,0,189),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(1180,0,-1131),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(1120,0,-449),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(1114,0,-738),getObjectData()));
		addChild(new Rock01Cluster(10,new Vec3f(627,0,1016),getObjectData()));
		addChild(new Rock01Cluster(6,new Vec3f(902,0,-844),getObjectData()));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
