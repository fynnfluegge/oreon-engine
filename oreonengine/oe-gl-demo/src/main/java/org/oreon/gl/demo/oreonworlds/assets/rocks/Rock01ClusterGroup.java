package org.oreon.gl.demo.oreonworlds.assets.rocks;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.gl.buffer.GLMeshVBO;
import org.oreon.core.gl.parameter.Default;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Util;
import org.oreon.gl.demo.oreonworlds.shaders.assets.rocks.RockHighPolyShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.rocks.RockShadowShader;

public class Rock01ClusterGroup extends InstancedObject{
	
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
			object.addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(NodeComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(NodeComponentType.MATERIAL0, model.getMaterial());
			objects.add(object);
		}
		
		addChild(new Rock01Cluster(8,new Vec3f(-698,0,772),objects));
	}

	public void run() {}
}
