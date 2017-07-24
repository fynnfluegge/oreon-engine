package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.TreeBillboardShader;
import apps.oreonworlds.shaders.plants.TreeBillboardShadowShader;
import apps.oreonworlds.shaders.plants.TreeLeavesShader;
import apps.oreonworlds.shaders.plants.TreeShadowShader;
import apps.oreonworlds.shaders.plants.TreeTrunkShader;
import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.configs.Default;
import engine.core.Camera;
import engine.geometry.Vertex;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import engine.utils.Util;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingCluster;
import modules.instancing.InstancingObject;
import modules.instancing.InstancingObjectHandler;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Tree02ClusterGroup extends InstancingObject{
	
	public Tree02ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_02","tree02.obj","tree02.mtl");
		Model[] billboards = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_02","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			
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

			if (model.equals(models[0]))
				object.setRenderInfo(new RenderInfo(new Default(), TreeTrunkShader.getInstance(), TreeShadowShader.getInstance()));
			else
				object.setRenderInfo(new RenderInfo(new Default(), TreeLeavesShader.getInstance(), TreeShadowShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		for (Model billboard : billboards){	
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			billboard.getMesh().setInstances(0);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPos(vertex.getPos().mul(2.4f));
				vertex.getPos().setX(vertex.getPos().getX()*1f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), TreeBillboardShader.getInstance(), TreeBillboardShadowShader.getInstance()));
			
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
			
			getChildren().clear();
			
			synchronized (getChildren()) {
				
//				long time = System.currentTimeMillis();
				
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(Camera.getInstance().getPosition()).length() < 2000){
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
