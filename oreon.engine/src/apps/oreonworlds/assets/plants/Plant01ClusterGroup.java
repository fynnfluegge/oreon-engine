package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.GrassShader;
import apps.oreonworlds.shaders.plants.GrassShadowShader;
import engine.buffers.MeshVBO;
import engine.components.model.Model;
import engine.components.renderer.RenderInfo;
import engine.configs.CullFaceDisable;
import engine.core.Camera;
import engine.math.Vec3f;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingCluster;
import modules.instancing.InstancingObject;
import modules.instancing.InstancingObjectHandler;
import modules.modelLoader.obj.OBJLoader;

public class Plant01ClusterGroup extends InstancingObject{
	
	public Plant01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Plant_01","billboardmodel.obj","billboardmodel.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVBO meshBuffer = new MeshVBO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), GrassShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), GrassShadowShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addCluster(new Plant01Cluster(40,new Vec3f(-2171,0,2776),getObjectData()));
		addCluster(new Plant01Cluster(20,new Vec3f(-1125,0,1448),getObjectData()));
		addCluster(new Plant01Cluster(20,new Vec3f(-1174,0,1612),getObjectData()));
		
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
					if (cluster.getCenter().sub(Camera.getInstance().getPosition()).length() < 600){
						addChild(cluster);
					}
				}
			}
		}
	}
}
