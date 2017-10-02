package org.oreon.demo.gl.oreonworlds.assets.rocks;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instancing.InstancedDataObject;
import org.oreon.core.instancing.InstancingObject;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.util.Util;
import org.oreon.demo.gl.oreonworlds.shaders.rocks.RockHighPolyShader;
import org.oreon.demo.gl.oreonworlds.shaders.rocks.RockShadowShader;

public class Rock01ClusterGroup extends InstancingObject{
	
	public Rock01ClusterGroup(){

		Model[] models = new OBJLoader().load("oreonworlds/assets/rocks/Rock_01","rock01.obj","rock01.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			GLMeshVBO meshBuffer = new GLMeshVBO();
			model.getMesh().setTangentSpace(true);
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new Default(),RockHighPolyShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new Default(), RockShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Rock01Cluster(8,new Vec3f(-2183,0,2895),getObjectData()));
		addChild(new Rock01Cluster(4,new Vec3f(-2042,0,2717),getObjectData()));
		addChild(new Rock01Cluster(6,new Vec3f(-1464,0,2617),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-1295,0,1605),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-1321,0,1662),getObjectData()));
		addChild(new Rock01Cluster(8,new Vec3f(-909,0,1602),getObjectData()));
		addChild(new Rock01Cluster(4,new Vec3f(-1464,0,2512),getObjectData()));
	}

	public void run() {}
}
