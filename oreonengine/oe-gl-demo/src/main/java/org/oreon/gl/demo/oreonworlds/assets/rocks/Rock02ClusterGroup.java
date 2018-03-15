package org.oreon.gl.demo.oreonworlds.assets.rocks;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.scene.GLRenderInfo;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Util;
import org.oreon.gl.demo.oreonworlds.shaders.assets.rocks.RockHighPolyShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.rocks.RockShadowShader;

public class Rock02ClusterGroup extends InstancedObject{

	public Rock02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/rocks/Rock_02","rock02.obj","rock02.mtl");
		
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
			object.addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(ComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(ComponentType.MATERIAL0, model.getMaterial());
			objects.add(object);
		}
		
		addChild(new Rock02Cluster(10,new Vec3f(954,0,-30),objects));
	}

	public void run() {}
}
