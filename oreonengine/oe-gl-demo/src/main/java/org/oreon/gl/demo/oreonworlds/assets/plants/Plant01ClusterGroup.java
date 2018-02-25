package org.oreon.gl.demo.oreonworlds.assets.plants;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.CullFaceDisable;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instancing.InstancedDataObject;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.instancing.InstancingObject;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.system.CoreSystem;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.GrassShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.GrassShadowShader;

public class Plant01ClusterGroup extends InstancingObject{
	
	public Plant01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Plant_01","billboardmodel.obj","billboardmodel.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			GLMeshVBO meshBuffer = new GLMeshVBO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), GrassShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), GrassShadowShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVbo(meshBuffer);
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
					if (cluster.getCenter().sub(CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() < 600){
						addChild(cluster);
					}
				}
			}
		}
	}
}
