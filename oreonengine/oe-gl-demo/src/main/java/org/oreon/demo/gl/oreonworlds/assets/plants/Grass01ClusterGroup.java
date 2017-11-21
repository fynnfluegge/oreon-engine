package org.oreon.demo.gl.oreonworlds.assets.plants;

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
import org.oreon.demo.gl.oreonworlds.shaders.assets.plants.GrassShader;
import org.oreon.demo.gl.oreonworlds.shaders.assets.plants.GrassShadowShader;

public class Grass01ClusterGroup extends InstancingObject{
	
	public Grass01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Grass_01","grassmodel.obj","grassmodel.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			GLMeshVBO meshBuffer = new GLMeshVBO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), GrassShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), GrassShadowShader.getInstance()));	
			
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
		addCluster(new Grass01Cluster(400,new Vec3f(-2176,0,2727),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1447,0,2655),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1479,0,2665),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1435,0,2627),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1418,0,2537),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1398,0,2662),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1441,0,2568),getObjectData()));
		addCluster(new Grass01Cluster(250,new Vec3f(-1445,0,2617),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1151,0,1508),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1141,0,1467),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1119,0,1427),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1099,0,1419),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1144,0,1560),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1158,0,1604),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1181,0,1631),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1221,0,1651),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1177,0,1663),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1221,0,1682),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1243,0,1718),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-2147,0,2765),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-2229,0,2787),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1207,0,1428),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1222,0,1390),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1241,0,1288),getObjectData()));
		addCluster(new Grass01Cluster(400,new Vec3f(-1229,0,1337),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-200,0,-77),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-271,0,-64),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-235,0,-85),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-329,0,-123),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-240,0,-149),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-199,0,-148),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-274,0,-136),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-332,0,-60),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-210,0,-128),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-386,0,-141),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-290,0,-147),getObjectData()));
		addCluster(new Grass01Cluster(100,new Vec3f(-288,0,-118),getObjectData()));
	
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
		
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() < 600){
						addChild(cluster);
					}
				}
			}
		}
	}
}
