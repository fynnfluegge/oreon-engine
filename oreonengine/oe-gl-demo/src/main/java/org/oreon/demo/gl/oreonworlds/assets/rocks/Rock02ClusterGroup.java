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
import org.oreon.demo.gl.oreonworlds.shaders.assets.rocks.RockHighPolyShader;
import org.oreon.demo.gl.oreonworlds.shaders.assets.rocks.RockShadowShader;

public class Rock02ClusterGroup extends InstancingObject{

	public Rock02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/rocks/Rock_02","rock02.obj","rock02.mtl");
		
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
			object.setVbo(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Rock02Cluster(10,new Vec3f(954,0,-30),getObjectData()));
	}

	public void run() {}
}
