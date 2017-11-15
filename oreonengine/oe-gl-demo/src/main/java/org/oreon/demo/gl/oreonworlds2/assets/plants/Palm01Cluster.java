package org.oreon.demo.gl.oreonworlds2.assets.plants;

import java.nio.FloatBuffer;
import java.util.List;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.buffers.GLUBO;
import org.oreon.core.instancing.InstancedDataObject;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.renderer.Renderer;
import org.oreon.core.scene.GameObject;
import org.oreon.core.scene.Node;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.IntegerReference;
import org.oreon.demo.gl.oreonworlds.shaders.InstancingGridShader;
import org.oreon.demo.gl.oreonworlds2.shaders.assets.plants.PalmBillboardShader;
import org.oreon.demo.gl.oreonworlds2.shaders.assets.plants.PalmShader;
import org.oreon.modules.gl.terrain.Terrain;

public class Palm01Cluster extends InstancingCluster{

	public Palm01Cluster(int instances, Vec3f pos,  List<InstancedDataObject> objects){
		
		setCenter(pos);
		setHighPolyInstances(new IntegerReference(0));
		setLowPolyInstances(new IntegerReference(instances));
		int buffersize = Float.BYTES * 16 * instances;
				
		for (int i=0; i<instances; i++){
			
			float s = (float)(Math.random()*0.15 + 0.2);
			Vec3f translation = new Vec3f((float)(Math.random()*100)-50 + getCenter().getX(), 0, (float)(Math.random()*100)-50 + getCenter().getZ());
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
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
		
		for (InstancedDataObject dataObject : objects){
			GameObject object = new GameObject();
			GLMeshVBO vao = new GLMeshVBO((GLMeshVBO) dataObject.getVao());
			
			Renderer renderer = new Renderer(vao);
			renderer.setRenderInfo(dataObject.getRenderInfo());
			
			Renderer shadowRenderer = new Renderer(vao);
			shadowRenderer.setRenderInfo(dataObject.getShadowRenderInfo());
			
			object.addComponent("Material", dataObject.getMaterial());
			object.addComponent(Constants.RENDERER_COMPONENT, renderer);
			object.addComponent(Constants.SHADOW_RENDERER_COMPONENT, shadowRenderer);
			addChild(object);
		}
		
		((GLMeshVBO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVbo()).setInstances(getHighPolyInstances());
		((GLMeshVBO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVbo()).setInstances(getHighPolyInstances());
		((GLMeshVBO) ((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getVbo()).setInstances(getHighPolyInstances());
		((GLMeshVBO) ((Renderer) ((GameObject) getChildren().get(3)).getComponent("Renderer")).getVbo()).setInstances(getHighPolyInstances());
		
		((GLMeshVBO) ((Renderer) ((GameObject) getChildren().get(4)).getComponent("Renderer")).getVbo()).setInstances(getLowPolyInstances());
	}

	public void update()
	{	
		super.update();
		
		if (CoreSystem.getInstance().getRenderingEngine().isGrid()){
			for (Node child : getChildren()){
				((Renderer) ((GameObject) child).getComponent("Renderer")).getRenderInfo().setShader(InstancingGridShader.getInstance());
			}
		}
		else{
			((Renderer) ((GameObject) getChildren().get(0)).getComponent(Constants.RENDERER_COMPONENT)).getRenderInfo().setShader(PalmShader.getInstance());
			((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getRenderInfo().setShader(PalmShader.getInstance());
			((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getRenderInfo().setShader(PalmShader.getInstance());
			((Renderer) ((GameObject) getChildren().get(3)).getComponent("Renderer")).getRenderInfo().setShader(PalmShader.getInstance());
			((Renderer) ((GameObject) getChildren().get(4)).getComponent("Renderer")).getRenderInfo().setShader(PalmBillboardShader.getInstance());
		}
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
		if (!CoreSystem.getInstance().getRenderingEngine().isWaterReflection() && 
					!CoreSystem.getInstance().getRenderingEngine().isWaterRefraction()){
			super.render();
		}
	}
}
