package apps.oreonworlds.assets.rocks;

import java.nio.FloatBuffer;
import java.util.List;

import apps.oreonworlds.shaders.InstancingGridShader;
import apps.oreonworlds.shaders.rocks.RockHighPolyShader;
import engine.buffers.MeshVAO;
import engine.buffers.UBO;
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

public class Rock02Cluster extends InstancingCluster{
	
	public Rock02Cluster(int instances, Vec3f pos, List<InstancedDataObject> objects) {
		
		setCenter(pos);
		int buffersize = Float.BYTES * 16 * instances;
		
		for (int i=0; i<instances; i++){
			Vec3f translation = new Vec3f((float)(Math.random()*100)-50 + getCenter().getX(), 0, (float)(Math.random()*100)-50 + getCenter().getZ());
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 2;
			translation.setY(terrainHeight);
			float s = (float)(Math.random()*2 + 2);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			TransformsInstanced transform = new TransformsInstanced();
			transform.setTranslation(translation);
			transform.setScaling(scaling);
			transform.setRotation(rotation);
			transform.setLocalRotation(rotation);
			transform.initMatrices();
			getInstancingTransforms().add(transform);
			getHighPolyIndices().add(i);
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
	}
	
	public void update()
	{	
		if (RenderingEngine.isGrid()){
			for (Node child : getChildren()){
				((GameObject) child).getRenderInfo().setShader(InstancingGridShader.getInstance());
			}
		}
		else{
			((GameObject) getChildren().get(0)).getRenderInfo().setShader(RockHighPolyShader.getInstance());
		}
	}
}
