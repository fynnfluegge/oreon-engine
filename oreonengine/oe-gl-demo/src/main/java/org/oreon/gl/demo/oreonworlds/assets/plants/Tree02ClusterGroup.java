package org.oreon.gl.demo.oreonworlds.assets.plants;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffer.GLMeshVBO;
import org.oreon.core.gl.parameter.CullFaceDisable;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.model.Vertex;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Util;
import org.oreon.gl.demo.oreonworlds.shaders.InstancedWireframeShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.TreeBillboardShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.TreeBillboardShadowShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.TreeLeavesShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.TreeShadowShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.TreeTrunkShader;

public class Tree02ClusterGroup extends InstancedObject{
	
	public Tree02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Tree_02","tree02.obj","tree02.mtl");
		Model[] billboards = new OBJLoader().load("oreonworlds/assets/plants/Tree_02","billboardmodel.obj","billboardmodel.mtl");
		
		List<Renderable> objects = new ArrayList<>();
		
		for (Model model : models){
			
			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			if (model.equals(models[0])){
				model.getMesh().setTangentSpace(true);
				Util.generateTangentsBitangents(model.getMesh());
			}
			else
				model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPosition().setX(vertex.getPosition().getX()*1.2f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1.2f);
			}
			
			meshBuffer.addData(model.getMesh());

			GLRenderInfo renderInfo;
			GLRenderInfo shadowRenderInfo;
			GLRenderInfo wireframeRenderInfo = new GLRenderInfo(InstancedWireframeShader.getInstance(), new CullFaceDisable(), meshBuffer);
			
			if (model.equals(models[0])){
				renderInfo = new GLRenderInfo(TreeTrunkShader.getInstance(), new CullFaceDisable(), meshBuffer);
				shadowRenderInfo = new GLRenderInfo(TreeShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			}
			else{
				renderInfo = new GLRenderInfo(TreeLeavesShader.getInstance(), new CullFaceDisable(), meshBuffer);
				shadowRenderInfo = new GLRenderInfo(TreeShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			}
			
			Renderable object = new Renderable();
			object.addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(ComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(ComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
			object.addComponent(ComponentType.MATERIAL0, model.getMaterial());
			objects.add(object);
		}
		
		for (Model billboard : billboards){	

			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			billboard.getMesh().setInstances(0);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPosition(vertex.getPosition().mul(2.4f));
				vertex.getPosition().setX(vertex.getPosition().getX()*1f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			GLRenderInfo renderInfo = new GLRenderInfo(TreeBillboardShader.getInstance(), new CullFaceDisable(), meshBuffer);
			GLRenderInfo shadowRenderInfo = new GLRenderInfo(TreeBillboardShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			GLRenderInfo wireframeRenderInfo = new GLRenderInfo(InstancedWireframeShader.getInstance(), new CullFaceDisable(), meshBuffer);
			
			Renderable object = new Renderable();
			object.addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(ComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(ComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
			object.addComponent(ComponentType.MATERIAL0, billboard.getMaterial());
			objects.add(object);
		}
	
		addCluster(new Tree02Cluster(6,new Vec3f(-528,0,874),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-696,0,932),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-765,0,976),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-820,0,1035),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-595,0,624),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-462,0,597),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-525,0,704),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-552,0,788),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-608,0,712),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-568,0,894),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-593,0,954),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-663,0,665),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-728,0,654),objects));
		addCluster(new Tree02Cluster(6,new Vec3f(-706,0,1103),objects));
		
		setThread(new Thread(this));
		getThread().start();
	}
	
	public void run(){
		
		while(isRunning()){
		
			InstancedHandler.getInstance().getLock().lock();
			try {
				InstancedHandler.getInstance().getCondition().await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				InstancedHandler.getInstance().getLock().unlock();
			}
			
			synchronized (getChildren()) {
				
				getChildren().clear();
				
//				long time = System.currentTimeMillis();
				
				for (InstancedCluster cluster : getClusters()){
					if (cluster.getCenter().sub(EngineContext.getCamera().getPosition()).length() < 2000){
						cluster.updateUBOs();
						addChild(cluster);
					}
				}
				
//				System.out.println("############## " + (System.currentTimeMillis() - time));
			}
		}
	}
	
	public void render() {
		
		super.render();
	}
}
