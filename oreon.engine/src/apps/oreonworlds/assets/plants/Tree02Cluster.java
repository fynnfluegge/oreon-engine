package apps.oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.List;

import apps.oreonworlds.shaders.InstancingGridShader;
import apps.oreonworlds.shaders.plants.TreeBillboardShader;
import apps.oreonworlds.shaders.plants.TreeLeavesShader;
import apps.oreonworlds.shaders.plants.TreeTrunkShader;
import engine.buffers.MeshVBO;
import engine.buffers.UBO;
import engine.components.renderer.Renderer;
import engine.core.Camera;
import engine.core.RenderingEngine;
import engine.math.Matrix4f;
import engine.math.Vec3f;
import engine.scene.GameObject;
import engine.scene.Node;
import engine.utils.BufferUtil;
import engine.utils.Constants;
import engine.utils.IntegerReference;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingCluster;
import modules.terrain.Terrain;

public class Tree02Cluster extends InstancingCluster{

	public Tree02Cluster(int instances, Vec3f pos, List<InstancedDataObject> objects){
		
		setCenter(pos);
		setHighPolyInstances(new IntegerReference(0));
		setLowPolyInstances(new IntegerReference(instances));
		int buffersize = Float.BYTES * 16 * instances;
		
		for (int i=0; i<instances; i++){
			
			float s = (float)(Math.random()*6 + 26);
			Vec3f translation = new Vec3f((float)(Math.random()*100)-50 + getCenter().getX(), 0, (float)(Math.random()*100)-50 + getCenter().getZ());
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 1;
			translation.setY(terrainHeight);
			
			Matrix4f translationMatrix = new Matrix4f().Translation(translation);
			Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
			Matrix4f scalingMatrix = new Matrix4f().Scaling(scaling);
			
			getWorldMatrices().add(translationMatrix.mul(scalingMatrix.mul(rotationMatrix)));
			getModelMatrices().add(rotationMatrix);
			getLowPolyIndices().add(i);
		}
		
		setModelMatricesBuffer(new UBO());
		getModelMatricesBuffer().allocate(buffersize);
		
		setWorldMatricesBuffer(new UBO());
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
		for(Matrix4f matrix : getModelMatrices()){
			modelMatricesFloatBuffer.put(BufferUtil.createFlippedBuffer(matrix));
		}
		
		getWorldMatricesBuffer().updateData(worldMatricesFloatBuffer, size);
		getModelMatricesBuffer().updateData(modelMatricesFloatBuffer, size);
		
		for (InstancedDataObject dataObject : objects){
			GameObject object = new GameObject();
			MeshVBO vao = new MeshVBO((MeshVBO) dataObject.getVao());
			vao.setInstances(new IntegerReference(instances));
			
			Renderer renderer = new Renderer(vao);
			renderer.setRenderInfo(dataObject.getRenderInfo());
			
			Renderer shadowRenderer = new Renderer(vao);
			shadowRenderer.setRenderInfo(dataObject.getShadowRenderInfo());
			
			object.addComponent("Material", dataObject.getMaterial());
			object.addComponent(Constants.RENDERER, renderer);
			object.addComponent(Constants.SHADOW_RENDERER, shadowRenderer);

			addChild(object);
		}
		
		((MeshVBO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVbo()).setInstances(getHighPolyInstances());
		((MeshVBO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVbo()).setInstances(getHighPolyInstances());
		
		((MeshVBO) ((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getVbo()).setInstances(getLowPolyInstances());
	}
	
	@Override
	public void updateUBOs(){
		
		getHighPolyIndices().clear();
		
		int index = 0;
		
		for (Matrix4f transform : getWorldMatrices()){
			if (transform.getTranslation().sub(Camera.getInstance().getPosition()).length() < 220){
				getHighPolyIndices().add(index);
			}

			index++;
		}
		getHighPolyInstances().setValue(getHighPolyIndices().size());
	}

	public void update()
	{	
		super.update();
		
		if (RenderingEngine.isGrid()){
			for (Node child : getChildren()){
				((Renderer) ((GameObject) child).getComponent("Renderer")).getRenderInfo().setShader(InstancingGridShader.getInstance());
			}
		}
		else{
			((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getRenderInfo().setShader(TreeTrunkShader.getInstance());
			((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getRenderInfo().setShader(TreeLeavesShader.getInstance());
			((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getRenderInfo().setShader(TreeBillboardShader.getInstance());
		}
	}
	
	public void renderShadows(){
		
		getHighPolyInstances().setValue(0);
	
		super.renderShadows();
	
		getHighPolyInstances().setValue(getHighPolyIndices().size());
	}
	
	public void render(){
		
//		if (RenderingEngine.isWaterReflection()){
//			((MeshVAO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVao()).setInstances(0);
//			((MeshVAO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVao()).setInstances(0);
//		
//			((MeshVAO) ((Renderer) ((GameObject) getChildren().get(2)).getComponent("Renderer")).getVao()).setInstances(getLowPolyIndices().size());
//		
//			super.render();
//		
//			((MeshVAO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVao()).setInstances(getHighPolyIndices().size());
//			((MeshVAO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVao()).setInstances(getHighPolyIndices().size());
//		}
//		else
			super.render();
	}
}
