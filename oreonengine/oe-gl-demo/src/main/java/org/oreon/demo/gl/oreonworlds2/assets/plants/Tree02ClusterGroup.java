package org.oreon.demo.gl.oreonworlds2.assets.plants;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.CullFaceDisable;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instancing.InstancedDataObject;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.instancing.InstancingObject;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.model.Vertex;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Util;
import org.oreon.demo.gl.oreonworlds2.shaders.assets.plants.TreeBillboardShader;
import org.oreon.demo.gl.oreonworlds2.shaders.assets.plants.TreeBillboardShadowShader;
import org.oreon.demo.gl.oreonworlds2.shaders.assets.plants.TreeLeavesShader;
import org.oreon.demo.gl.oreonworlds2.shaders.assets.plants.TreeShadowShader;
import org.oreon.demo.gl.oreonworlds2.shaders.assets.plants.TreeTrunkShader;

public class Tree02ClusterGroup extends InstancingObject{
	
	public Tree02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Tree_02","tree02.obj","tree02.mtl");
		Model[] billboards = new OBJLoader().load("oreonworlds/assets/plants/Tree_02","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
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

			if (model.equals(models[0])){
				object.setRenderInfo(new RenderInfo(new Default(), TreeTrunkShader.getInstance()));
				object.setShadowRenderInfo(new RenderInfo(new Default(), TreeShadowShader.getInstance()));
			}
			else{
				object.setRenderInfo(new RenderInfo(new Default(), TreeLeavesShader.getInstance()));
				object.setShadowRenderInfo(new RenderInfo(new Default(), TreeShadowShader.getInstance()));
			}
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		for (Model billboard : billboards){	
			InstancedDataObject object = new InstancedDataObject();
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
	
			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), TreeBillboardShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), TreeBillboardShadowShader.getInstance()));
			
			object.setMaterial(billboard.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
	
		addCluster(new Tree02Cluster(6,new Vec3f(-1441,0,2678),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1499,0,2719),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1331,0,2649),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1462,0,2502),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1462,0,2502),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1154,0,2606),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1167,0,1198),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1119,0,1189),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(-1293,0,1159),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-528,0,874),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-696,0,932),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-765,0,976),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-820,0,1035),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-595,0,624),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-462,0,597),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1275,0,2549),getObjectData()));
		addCluster(new Tree02Cluster(6,new Vec3f(-1397,0,1198),getObjectData()));
		
		setThread(new Thread(this));
		getThread().start();
	}
	
	public void run(){
		
		while(isRunning()){
		
			InstancingObjectHandler.getInstance().getLock().lock();
			try {
				InstancingObjectHandler.getInstance().getCondition().await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				InstancingObjectHandler.getInstance().getLock().unlock();
			}
			
			synchronized (getChildren()) {
				
				getChildren().clear();
				
//				long time = System.currentTimeMillis();
				
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() < 2000){
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
