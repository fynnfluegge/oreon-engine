package apps.oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.List;

import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.core.Camera;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Renderer;
import engine.scenegraph.components.TransformsInstanced;
import engine.utils.BufferAllocation;
import modules.instancing.InstancingCluster;
import modules.terrain.Terrain;

public class Tree02Cluster extends InstancingCluster{

	public Tree02Cluster(int instances, Vec3f pos, List<GameObject> objects){
		
		setCenter(pos);
		int buffersize = Float.BYTES * 16 * instances;
		
		
		for (int i=0; i<instances; i++){
			Vec3f translation = new Vec3f((float)(Math.random()*200)-100 + getCenter().getX(), 0, (float)(Math.random()*200)-100 + getCenter().getZ());
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 1;
			translation.setY(terrainHeight);
			float s = (float)(Math.random()*6 + 26);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			TransformsInstanced transform = new TransformsInstanced();
			transform.setTranslation(translation);
			transform.setScaling(scaling);
			transform.setRotation(rotation);
			transform.setLocalRotation(rotation);
			transform.initMatrices();
			getTransforms().add(transform);
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
		
		for(TransformsInstanced matrix : getTransforms()){
			worldMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getWorldMatrix()));
			modelMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getModelMatrix()));
		}
		
		getWorldMatricesBuffer().updateData(worldMatricesFloatBuffer, size);
		getModelMatricesBuffer().updateData(modelMatricesFloatBuffer, size);
		
		for (GameObject object : objects){
			addChild(object);
		}
		
		updateUBOs();
	}

	public void update()
	{	
		super.update();
		
		if (getCenter().sub(Camera.getInstance().getPosition()).length() < 1000){
			
			updateUBOs();
		}
		else if(getHighPolyIndices().size() > 0){
			System.out.println(getCenter().sub(Camera.getInstance().getPosition()).length());
			System.out.println(getHighPolyIndices().size());
		}
	}
	
	public void updateUBOs(){
		
		getHighPolyIndices().clear();
		getLowPolyIndices().clear();
		
		int index = 0;
		
		for (TransformsInstanced transform : getTransforms()){
			if (transform.getTranslation().sub(Camera.getInstance().getPosition()).length() < 400){
				getHighPolyIndices().add(index);
			}
			getLowPolyIndices().add(index);

			index++;
		}
		
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVao()).setInstances(getHighPolyIndices().size());
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVao()).setInstances(getHighPolyIndices().size());
		
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getVao()).setInstances(getLowPolyIndices().size());
	}
}
