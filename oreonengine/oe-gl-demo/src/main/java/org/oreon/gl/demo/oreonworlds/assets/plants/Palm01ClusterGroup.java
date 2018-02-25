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
import org.oreon.core.model.Vertex;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.system.CoreSystem;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.PalmBillboardShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.PalmBillboardShadowShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.PalmShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.PalmShadowShader;

public class Palm01ClusterGroup extends InstancingObject{
	
	public Palm01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Palm_01","Palma 001.obj","Palma 001.mtl");
		Model[] billboards = new OBJLoader().load("oreonworlds/assets/plants/Palm_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			GLMeshVBO meshBuffer = new GLMeshVBO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), PalmShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVbo(meshBuffer);
			getObjectData().add(object);
		}
		for (Model billboard : billboards){	
			InstancedDataObject object = new InstancedDataObject();
			GLMeshVBO meshBuffer = new GLMeshVBO();
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPosition(vertex.getPosition().mul(135));
				vertex.getPosition().setX(vertex.getPosition().getX()*1.1f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1.1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), PalmBillboardShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), PalmBillboardShadowShader.getInstance()));
			
			object.setMaterial(billboard.getMaterial());
			object.setVbo(meshBuffer);
			getObjectData().add(object);
		}
	
		addCluster(new Palm01Cluster(8,new Vec3f(-166,0,-826),getObjectData()));
//		addCluster(new Palm01Cluster(8,new Vec3f(-290,0,-666),getObjectData()));
//		addCluster(new Palm01Cluster(8,new Vec3f(-185,0,-850),getObjectData()));
//		addCluster(new Palm01Cluster(8,new Vec3f(-314,0,-566),getObjectData()));
//		addCluster(new Palm01Cluster(10,new Vec3f(-334,0,-667),getObjectData()));
//		addCluster(new Palm01Cluster(6,new Vec3f(-2049,0,2883),getObjectData()));
//		addCluster(new Palm01Cluster(4,new Vec3f(-355,0,-259),getObjectData()));
//		addCluster(new Palm01Cluster(4,new Vec3f(-160,0,-318),getObjectData()));
		
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
