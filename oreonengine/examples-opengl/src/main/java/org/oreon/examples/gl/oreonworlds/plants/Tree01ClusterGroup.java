package org.oreon.examples.gl.oreonworlds.plants;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.memory.GLMeshVBO;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.util.GLAssimpModelLoader;
import org.oreon.core.gl.wrapper.parameter.CullFaceDisable;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.instanced.InstancedHandler;
import org.oreon.core.instanced.InstancedObject;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.model.Vertex;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Util;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeBillboardShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeBillboardShadowShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeLeavesShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeShadowShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeTrunkShader;

public class Tree01ClusterGroup extends InstancedObject{
	
	public Tree01ClusterGroup(){
		
		List<Model<GLTexture>> models = GLAssimpModelLoader.loadModel("oreonworlds/assets/plants/Tree_01","tree01.obj");
		List<Model<GLTexture>> billboards = GLAssimpModelLoader.loadModel("oreonworlds/assets/plants/Tree_01","billboardmodel.obj");
		
		List<Renderable> objects = new ArrayList<>();
		
		for (Model<GLTexture> model : models){
			
			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			if (model.equals(models.get(0))){
				model.getMesh().setTangentSpace(true);
				Util.generateTangentsBitangents(model.getMesh());
			}
			else
				model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPosition().setX(vertex.getPosition().getX()*1.2f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1.2f);
			}
			
			meshBuffer.addData(model.getMesh());

			GLRenderInfo renderInfo;
			GLRenderInfo shadowRenderInfo;
			
			if (model.equals(models.get(0))){
				renderInfo = new GLRenderInfo(TreeTrunkShader.getInstance(), new CullFaceDisable(), meshBuffer);
				shadowRenderInfo = new GLRenderInfo(TreeShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			}
			else{
				renderInfo = new GLRenderInfo(TreeLeavesShader.getInstance(), new CullFaceDisable(), meshBuffer);
				shadowRenderInfo = new GLRenderInfo(TreeShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			}
			
			Renderable object = new Renderable();
			object.addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(NodeComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(NodeComponentType.MATERIAL0, model.getMaterial());
			objects.add(object);
		}
		
		for (Model<GLTexture> billboard : billboards){
			
			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPosition(vertex.getPosition().mul(7.4f));
				vertex.getPosition().setX(vertex.getPosition().getX()*1f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			GLRenderInfo renderInfo = new GLRenderInfo(TreeBillboardShader.getInstance(), new CullFaceDisable(), meshBuffer);
			GLRenderInfo shadowRenderInfo = new GLRenderInfo(TreeBillboardShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			
			Renderable object = new Renderable();
			object.addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(NodeComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(NodeComponentType.MATERIAL0, billboard.getMaterial());
			objects.add(object);
		}
	
		addCluster(new Tree01Cluster(10,new Vec3f(-1002,0,1550),objects));
		addCluster(new Tree01Cluster(10,new Vec3f(-1085,0,1536),objects));
		addCluster(new Tree01Cluster(10,new Vec3f(-1121,0,1473),objects));
		addCluster(new Tree01Cluster(10,new Vec3f(-1114,0,1423),objects));
		addCluster(new Tree01Cluster(10,new Vec3f(-1074,0,1378),objects));
		addCluster(new Tree01Cluster(10,new Vec3f(-1138,0,1345),objects));
		addCluster(new Tree01Cluster(10,new Vec3f(-1039,0,1129),objects));
		addCluster(new Tree01Cluster(10,new Vec3f(-1011,0,1042),objects));
		addCluster(new Tree01Cluster(6,new Vec3f(-1181,0,1346),objects));
		addCluster(new Tree01Cluster(6,new Vec3f(-1210,0,1348),objects));
		addCluster(new Tree01Cluster(6,new Vec3f(-1211,0,1392),objects));
		
		setThread(new Thread(this));
		getThread().start();
	}

	public void run() {
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
					if (cluster.getCenter().sub(EngineContext.getCamera().getPosition()).length() < 2000){
						cluster.updateUBOs();
						addChild(cluster);
					}
				}
				
			}
		}
	}
}
