package org.oreon.gl.demo.oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.List;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.buffers.GLUBO;
import org.oreon.core.gl.scene.GLRenderInfo;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scene.Renderable;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.IntegerReference;

public class Palm01Cluster extends InstancingCluster{

	public Palm01Cluster(int instances, Vec3f pos,  List<Renderable> objects){
		
		setCenter(pos);
		setHighPolyInstances(new IntegerReference(0));
		setLowPolyInstances(new IntegerReference(instances));
		
		int buffersize = Float.BYTES * 16 * instances;
				
		for (int i=0; i<instances; i++){
			
			float s = (float)(Math.random()*0.1 + 0.3);
			Vec3f translation = new Vec3f((float)(Math.random()*100)-50 + getCenter().getX(), 0, (float)(Math.random()*100)-50 + getCenter().getZ());
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			float terrainHeight = CoreSystem.getInstance().getScenegraph().getTerrain().getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 3;
			translation.setY(terrainHeight);
			
			Matrix4f translationMatrix = new Matrix4f().Translation(translation);
			Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
			Matrix4f scalingMatrix = new Matrix4f().Scaling(scaling);
			
			getWorldMatrices().add(translationMatrix.mul(scalingMatrix.mul(rotationMatrix)));
			getModelMatrices().add(rotationMatrix);
			getLowPolyIndices().add(i);
		}
		
		setModelMatricesBuffer(new GLUBO());
		getModelMatricesBuffer().allocate(buffersize);
		
		setWorldMatricesBuffer(new GLUBO());
		getWorldMatricesBuffer().allocate(buffersize);	
		
		/**
		 * init matrices UBO's
		 */
		int size = Float.BYTES * 16 * instances;
		
		FloatBuffer worldMatricesFloatBuffer = BufferUtil.createFloatBuffer(size);
		FloatBuffer modelMatricesFloatBuffer = BufferUtil.createFloatBuffer(size);
		
		for(Matrix4f matrix : getWorldMatrices()){
			worldMatricesFloatBuffer.put(BufferUtil.createFlippedBuffer(matrix));
		}
		for(Matrix4f matrix: getModelMatrices()){
			modelMatricesFloatBuffer.put(BufferUtil.createFlippedBuffer(matrix));
		}
		
		getWorldMatricesBuffer().updateData(worldMatricesFloatBuffer, size);
		getModelMatricesBuffer().updateData(modelMatricesFloatBuffer, size);
		
		for (Renderable object : objects){
			addChild(object);
		}
		
		((GLMeshVBO) ((GLRenderInfo) ((Renderable) getChildren().get(0)).getComponent(Constants.MAIN_RENDERINFO)).getVbo()).setInstances(getHighPolyInstances());
		((GLMeshVBO) ((GLRenderInfo) ((Renderable) getChildren().get(1)).getComponent(Constants.MAIN_RENDERINFO)).getVbo()).setInstances(getHighPolyInstances());
		((GLMeshVBO) ((GLRenderInfo) ((Renderable) getChildren().get(2)).getComponent(Constants.MAIN_RENDERINFO)).getVbo()).setInstances(getHighPolyInstances());
		((GLMeshVBO) ((GLRenderInfo) ((Renderable) getChildren().get(3)).getComponent(Constants.MAIN_RENDERINFO)).getVbo()).setInstances(getHighPolyInstances());
		((GLMeshVBO) ((GLRenderInfo) ((Renderable) getChildren().get(4)).getComponent(Constants.MAIN_RENDERINFO)).getVbo()).setInstances(getLowPolyInstances());
	}
	
	public void updateUBOs(){
		
		getHighPolyIndices().clear();
		
		int index = 0;
		
		for (Matrix4f transform : getWorldMatrices()){
			if (transform.getTranslation().sub(CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() < 500){
				getHighPolyIndices().add(index);
			}

			index++;
		}
		getHighPolyInstances().setValue(getHighPolyIndices().size());
	}
	
	public void render(){
		if (!CoreSystem.getInstance().getRenderEngine().isWaterReflection() && 
					!CoreSystem.getInstance().getRenderEngine().isWaterRefraction()){
			super.render();
		}
	}
}
