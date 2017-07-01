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
	
		addCluster(new Tree02Cluster(4,new Vec3f(1363,0,-1179),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(599,0,-114),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(735,0,-187),getObjectData()));
//		addCluster(new Tree02Cluster(4,new Vec3f(1472,0,-1227),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(1614,0,-1270),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(1768,0,-1254),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(1737,0,-1161),getObjectData()));
//		addCluster(new Tree02Cluster(4,new Vec3f(1902,0,7),getObjectData()));
//		addCluster(new Tree02Cluster(5,new Vec3f(1780,0,301),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(92,0,1676),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(218,0,1671),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(315,0,1648),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(516,0,1306),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(474,0,1432),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(-43,0,1677),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(-167,0,1716),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(-482,0,1702),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(-657,0,1675),getObjectData()));
//		addCluster(new Tree02Cluster(4,new Vec3f(-1901,0,1100),getObjectData()));
//		addCluster(new Tree02Cluster(4,new Vec3f(-1834,0,140),getObjectData()));
//		addCluster(new Tree02Cluster(4,new Vec3f(-1834,0,140),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(829,0,-6),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(889,0,-48),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(979,0,-95),getObjectData()));
		addCluster(new Tree02Cluster(4,new Vec3f(1107,0,-121),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(1200,0,-139),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(752,0,-113),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(1295,0,-186),getObjectData()));
		addCluster(new Tree02Cluster(5,new Vec3f(813,0,84),getObjectData()));
		
		setThread(new Thread(this));
		getThread().start();
	}
	
	public void run(){
		
		while(true){
		
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
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(Camera.getInstance().getPosition()).length() < 1000){
						cluster.updateUBOs();
						addChild(cluster);
					}
				}
			}
		}
	}
	
	public void render(){
			super.render();
	}
	
	public void update(){
		super.update();
	}
}
