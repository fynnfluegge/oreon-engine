package org.oreon.gl.demo.oreonworlds.assets.plants;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.CullFaceDisable;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.system.CoreSystem;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.GrassShader;
import org.oreon.gl.demo.oreonworlds.shaders.assets.plants.GrassShadowShader;

public class Grass01ClusterGroup extends InstancedObject{
	
	public Grass01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Grass_01","grassmodel.obj","grassmodel.mtl");
	
		List<Renderable> objects = new ArrayList<>();
		
		for (Model model : models){
			
			GLMeshVBO meshBuffer = new GLMeshVBO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			meshBuffer.addData(model.getMesh());

			GLRenderInfo renderInfo = new GLRenderInfo(GrassShader.getInstance(), new CullFaceDisable(), meshBuffer);
			GLRenderInfo shadowRenderInfo = new GLRenderInfo(GrassShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			
			Renderable object = new Renderable();
			object.addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(ComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(ComponentType.MATERIAL0, model.getMaterial());
			objects.add(object);
		}
		
		addCluster(new Grass01Cluster(200,new Vec3f(-2039,0,2845),objects));
		addCluster(new Grass01Cluster(200,new Vec3f(-2054,0,2872),objects));
		addCluster(new Grass01Cluster(200,new Vec3f(-2160,0,2827),objects));
		addCluster(new Grass01Cluster(200,new Vec3f(-2134,0,2817),objects));
		addCluster(new Grass01Cluster(200,new Vec3f(-2172,0,2800),objects));
		addCluster(new Grass01Cluster(200,new Vec3f(-2191,0,2804),objects));
		addCluster(new Grass01Cluster(200,new Vec3f(-2194,0,2753),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-2176,0,2727),objects));
		addCluster(new Grass01Cluster(250,new Vec3f(-1447,0,2655),objects));
		addCluster(new Grass01Cluster(250,new Vec3f(-1479,0,2665),objects));
		addCluster(new Grass01Cluster(250,new Vec3f(-1435,0,2627),objects));
		addCluster(new Grass01Cluster(250,new Vec3f(-1418,0,2537),objects));
		addCluster(new Grass01Cluster(250,new Vec3f(-1398,0,2662),objects));
		addCluster(new Grass01Cluster(250,new Vec3f(-1441,0,2568),objects));
		addCluster(new Grass01Cluster(250,new Vec3f(-1445,0,2617),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1151,0,1508),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1141,0,1467),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1119,0,1427),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1099,0,1419),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1144,0,1560),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1158,0,1604),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1181,0,1631),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1221,0,1651),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1177,0,1663),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1221,0,1682),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1243,0,1718),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-2147,0,2765),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-2229,0,2787),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1207,0,1428),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1222,0,1390),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1241,0,1288),objects));
		addCluster(new Grass01Cluster(400,new Vec3f(-1229,0,1337),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-200,0,-77),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-271,0,-64),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-235,0,-85),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-329,0,-123),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-240,0,-149),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-199,0,-148),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-274,0,-136),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-332,0,-60),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-210,0,-128),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-386,0,-141),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-290,0,-147),objects));
		addCluster(new Grass01Cluster(100,new Vec3f(-288,0,-118),objects));
	
		setThread(new Thread(this));
		getThread().start();
	}
	
	public void run(){
		while(isRunning()){
			
			InstancedHandler.getInstance().getLock().lock();
			try {
				InstancedHandler.getInstance().getCondition().await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				InstancedHandler.getInstance().getLock().unlock();
			}

			synchronized (getChildren()) {
				
				getChildren().clear();
		
				for (InstancedCluster cluster : getClusters()){
					if (cluster.getCenter().sub(CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() < 600){
						addChild(cluster);
					}
				}
			}
		}
	}
}
