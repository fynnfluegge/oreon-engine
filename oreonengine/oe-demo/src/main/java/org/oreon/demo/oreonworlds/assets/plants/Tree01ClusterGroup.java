package org.oreon.demo.oreonworlds.assets.plants;

import org.oreon.core.buffers.MeshVBO;
import org.oreon.core.configs.CullFaceDisable;
import org.oreon.core.configs.Default;
import org.oreon.core.instancing.InstancedDataObject;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.instancing.InstancingObject;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.model.Vertex;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.Util;
import org.oreon.core.utils.modelLoader.obj.OBJLoader;
import org.oreon.demo.oreonworlds.shaders.plants.TreeBillboardShader;
import org.oreon.demo.oreonworlds.shaders.plants.TreeBillboardShadowShader;
import org.oreon.demo.oreonworlds.shaders.plants.TreeLeavesShader;
import org.oreon.demo.oreonworlds.shaders.plants.TreeShadowShader;
import org.oreon.demo.oreonworlds.shaders.plants.TreeTrunkShader;

public class Tree01ClusterGroup extends InstancingObject{
	
	public Tree01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Tree_01","tree01.obj","tree01.mtl");
		Model[] billboards = new OBJLoader().load("oreonworlds/assets/plants/Tree_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVBO meshBuffer = new MeshVBO();
			
			if (model.equals(models[0])){
				model.getMesh().setTangentSpace(true);
				Util.generateTangentsBitangents(model.getMesh());
			}
			else
				model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPos().setX(vertex.getPos().getX()*1.2f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1.2f);
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
			MeshVBO meshBuffer = new MeshVBO();
			
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPos(vertex.getPos().mul(7.4f));
				vertex.getPos().setX(vertex.getPos().getX()*1f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), TreeBillboardShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), TreeBillboardShadowShader.getInstance()));
			
			object.setMaterial(billboard.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
	
		addCluster(new Tree01Cluster(10,new Vec3f(-1002,0,1550),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1085,0,1536),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1121,0,1473),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1114,0,1423),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1074,0,1378),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1138,0,1345),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1039,0,1129),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1011,0,1042),getObjectData()));
		addCluster(new Tree01Cluster(6,new Vec3f(-1181,0,1346),getObjectData()));
		addCluster(new Tree01Cluster(6,new Vec3f(-1210,0,1348),getObjectData()));
		addCluster(new Tree01Cluster(6,new Vec3f(-1211,0,1392),getObjectData()));
		
		setThread(new Thread(this));
		getThread().start();
	}

	public void run() {
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
				
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() < 2000){
						cluster.updateUBOs();
						addChild(cluster);
					}
				}
				
			}
		}
	}
}
