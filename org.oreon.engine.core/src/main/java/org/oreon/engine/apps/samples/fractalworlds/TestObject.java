package org.oreon.engine.apps.samples.fractalworlds;

import org.oreon.engine.engine.buffers.MeshVAO;
import org.oreon.engine.engine.configs.Default;
import org.oreon.engine.engine.math.Vec3f;
import org.oreon.engine.engine.scenegraph.GameObject;
import org.oreon.engine.engine.scenegraph.Node;
import org.oreon.engine.engine.scenegraph.components.Material;
import org.oreon.engine.engine.scenegraph.components.RenderInfo;
import org.oreon.engine.engine.scenegraph.components.Renderer;
import org.oreon.engine.engine.utils.Util;
import org.oreon.engine.modules.modelLoader.obj.Model;
import org.oreon.engine.modules.modelLoader.obj.OBJLoader;
import org.oreon.engine.modules.terrain.Terrain;

public class TestObject extends Node{

	
	public TestObject() {
	
		getTransform().setLocalRotation(0, 0, 0);
		getTransform().setLocalScaling(1f,1f,1f);
		getTransform().setLocalTranslation(0,Terrain.getInstance().getTerrainHeight(0,0),0);
		OBJLoader loader = new OBJLoader();
		Model[] models = loader.load("./res/models/obj/nanosuit","nanosuit.obj","nanosuit.mtl");

		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			Util.generateNormalsCW(model.getMesh().getVertices(), model.getMesh().getIndices());
			Util.generateTangentsBitangents(model.getMesh());
			model.getMesh().setTangentSpace(true);
			meshBuffer.addData(model.getMesh());
			Renderer renderer = null;
			if(model.getMaterial() == null){
				Material material = new Material();
				material.setColor(new Vec3f(0.2f,0.2f,0.2f));
				material.setName("zero");
				model.setMaterial(material);
			}

			object.setRenderInfo(new RenderInfo(new Default(), org.oreon.engine.engine.shaders.blinnphong.BlinnPhongBumpShader.getInstance(), TestObjectShadowShader.getInstance()));
			renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
	}
	
}
