package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.GrassShader;
import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.core.Camera;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingCluster;
import modules.instancing.InstancingObject;
import modules.instancing.InstancingObjectHandler;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Grass01ClusterGroup extends InstancingObject{
	
	public Grass01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Grass_01","grassmodel.obj","grassmodel.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), GrassShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addCluster(new Grass01Cluster(200,new Vec3f(-2039,0,2845),getObjectData()));
		addCluster(new Grass01Cluster(200,new Vec3f(-2054,0,2872),getObjectData()));
		addCluster(new Grass01Cluster(200,new Vec3f(-2160,0,2827),getObjectData()));
		addCluster(new Grass01Cluster(200,new Vec3f(-2134,0,2817),getObjectData()));
		addCluster(new Grass01Cluster(200,new Vec3f(-2172,0,2800),getObjectData()));
		addCluster(new Grass01Cluster(200,new Vec3f(-2191,0,2804),getObjectData()));
		addCluster(new Grass01Cluster(200,new Vec3f(-2194,0,2753),getObjectData()));
		addCluster(new Grass01Cluster(200,new Vec3f(-2176,0,2727),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1979,0,2635),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1926,0,2626),getObjectData()));
	
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
		
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(Camera.getInstance().getPosition()).length() < 600){
						addChild(cluster);
					}
				}
			}
		}
	}
}
