package org.oreon.gl.demo.oreonworlds.assets.rocks;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.scene.GLRenderInfo;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instancing.InstancingObject;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.scene.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.Util;
import org.oreon.gl.demo.oreonworlds.shaders.assets.rocks.RockHighPolyShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.rocks.RockShadowShader;

public class Rock01ClusterGroup extends InstancingObject{
	
	public Rock01ClusterGroup(){

		Model[] models = new OBJLoader().load("oreonworlds/assets/rocks/Rock_01","rock01.obj","rock01.mtl");
	
		List<Renderable> objects = new ArrayList<>();
		
		for (Model model : models){
			
			GLMeshVBO meshBuffer = new GLMeshVBO();
			model.getMesh().setTangentSpace(true);
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());
			
			GLRenderInfo renderInfo = new GLRenderInfo(RockHighPolyShader.getInstance(), new Default(), meshBuffer);
			GLRenderInfo shadowRenderInfo = new GLRenderInfo(RockShadowShader.getInstance(), new Default(), meshBuffer);
	
			Renderable object = new Renderable();
			object.addComponent(Constants.MAIN_RENDERINFO, renderInfo);
			object.addComponent(Constants.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(Constants.MATERIAL, model.getMaterial());
			objects.add(object);
		}
		
		addChild(new Rock01Cluster(8,new Vec3f(-698,0,772),objects));
	}

	public void run() {}
}
