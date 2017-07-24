package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.PalmBillboardShader;
import apps.oreonworlds.shaders.plants.PalmBillboardShadowShader;
import apps.oreonworlds.shaders.plants.PalmShader;
import apps.oreonworlds.shaders.plants.PalmShadowShader;
import engine.buffers.MeshVAO;
import engine.configs.CullFaceDisable;
import engine.core.Camera;
import engine.geometry.Vertex;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingCluster;
import modules.instancing.InstancingObject;
import modules.instancing.InstancingObjectHandler;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Palm01ClusterGroup extends InstancingObject{
	
	public Palm01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Palm_01","Palma 001.obj","Palma 001.mtl");
		Model[] billboards = new OBJLoader().load("./res/oreonworlds/assets/plants/Palm_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmShader.getInstance(), PalmShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		for (Model billboard : billboards){	
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPos(vertex.getPos().mul(135));
				vertex.getPos().setX(vertex.getPos().getX()*1.1f);
				vertex.getPos().setZ(vertex.getPos().getZ()*1.1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmBillboardShader.getInstance(), PalmBillboardShadowShader.getInstance()));
			
			object.setMaterial(billboard.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
	
		addCluster(new Palm01Cluster(8,new Vec3f(-2086,0,2729),getObjectData()));
		addCluster(new Palm01Cluster(10,new Vec3f(-720,0,-395),getObjectData()));
		addCluster(new Palm01Cluster(10,new Vec3f(-577,0,-454),getObjectData()));
		addCluster(new Palm01Cluster(10,new Vec3f(-401,0,-571),getObjectData()));
		addCluster(new Palm01Cluster(10,new Vec3f(-334,0,-667),getObjectData()));
		addCluster(new Palm01Cluster(6,new Vec3f(-2049,0,2883),getObjectData()));
		addCluster(new Palm01Cluster(4,new Vec3f(-355,0,-259),getObjectData()));
		addCluster(new Palm01Cluster(4,new Vec3f(-160,0,-318),getObjectData()));
		
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
			
			getChildren().clear();
			
			synchronized (getChildren()) {
				
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(Camera.getInstance().getPosition()).length() < 2000){
						cluster.updateUBOs();
						addChild(cluster);
					}
				}
			}
		}
	}
}
