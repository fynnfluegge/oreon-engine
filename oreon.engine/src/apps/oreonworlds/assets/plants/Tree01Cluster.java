package apps.oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.List;

import apps.oreonworlds.shaders.InstancingGridShader;
import apps.oreonworlds.shaders.plants.TreeBillboardShader;
import apps.oreonworlds.shaders.plants.TreeLeavesShader;
import apps.oreonworlds.shaders.plants.TreeTrunkShader;
import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.core.Camera;
import engine.core.RenderingEngine;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.Renderer;
import engine.scenegraph.components.TransformsInstanced;
import engine.utils.BufferAllocation;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingCluster;
import modules.terrain.Terrain;

public class Tree01Cluster extends InstancingCluster{
	
	public Tree01Cluster(int instances, Vec3f pos, List<InstancedDataObject> objects){
		
		setCenter(pos);
		int buffersize = Float.BYTES * 16 * instances;
		
		for (int i=0; i<instances; i++){
			Vec3f translation = new Vec3f((float)(Math.random()*100)-50 + getCenter().getX(), 0, (float)(Math.random()*100)-50 + getCenter().getZ());
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 1;
			translation.setY(terrainHeight);
			float s = (float)(Math.random()*2 + 4);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			TransformsInstanced transform = new TransformsInstanced();
			transform.setTranslation(translation);
			transform.setScaling(scaling);
			transform.setRotation(rotation);
			transform.setLocalRotation(rotation);
			transform.initMatrices();
			getInstancingTransforms().add(transform);
		}
		
		setModelMatricesBuffer(new UBO());
		getModelMatricesBuffer().allocate(buffersize);
		
		setWorldMatricesBuffer(new UBO());
		getWorldMatricesBuffer().allocate(buffersize);	
		
		/**
		 * init matrices UBO's
		 */
		int size = Float.BYTES * 16 * instances;
		
		FloatBuffer worldMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		FloatBuffer modelMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		
		for(TransformsInstanced matrix : getInstancingTransforms()){
			worldMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getWorldMatrix()));
			modelMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getModelMatrix()));
		}
		
		getWorldMatricesBuffer().updateData(worldMatricesFloatBuffer, size);
		getModelMatricesBuffer().updateData(modelMatricesFloatBuffer, size);
		
		for (InstancedDataObject dataObject : objects){
			GameObject object = new GameObject();
			MeshVAO vao = new MeshVAO((MeshVAO) dataObject.getVao());
			vao.setInstances(instances);
			Renderer renderer = new Renderer(dataObject.getRenderInfo().getShader(), vao);
			object.setRenderInfo(dataObject.getRenderInfo());
			object.addComponent("Material", dataObject.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
		
		updateUBOs();
	}

	public void update()
	{	
		super.update();
		
		if (getCenter().sub(Camera.getInstance().getPosition()).length() < 800){
			
			updateUBOs();
		}
		else if(getHighPolyIndices().size() > 0){
			System.out.println(getCenter().sub(Camera.getInstance().getPosition()).length());
			System.out.println(getHighPolyIndices().size());
		}
		
		if (RenderingEngine.isGrid()){
			for (Node child : getChildren()){
				((GameObject) child).getRenderInfo().setShader(InstancingGridShader.getInstance());
			}
		}
		else{
			((GameObject) getChildren().get(0)).getRenderInfo().setShader(TreeTrunkShader.getInstance());
			((GameObject) getChildren().get(1)).getRenderInfo().setShader(TreeLeavesShader.getInstance());
			((GameObject) getChildren().get(2)).getRenderInfo().setShader(TreeBillboardShader.getInstance());
		}
	}
	
	public void updateUBOs(){
		
		getHighPolyIndices().clear();
		getLowPolyIndices().clear();
		
		int index = 0;
		
		for (TransformsInstanced transform : getInstancingTransforms()){
			if (transform.getTranslation().sub(Camera.getInstance().getPosition()).length() < 400){
				getHighPolyIndices().add(index);
			}
			if (transform.getTranslation().sub(Camera.getInstance().getPosition()).length() > 100){
				getLowPolyIndices().add(index);
			}

			index++;
		}
		
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVao()).setInstances(getHighPolyIndices().size());
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVao()).setInstances(getHighPolyIndices().size());
		
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getVao()).setInstances(getLowPolyIndices().size());
	}
}