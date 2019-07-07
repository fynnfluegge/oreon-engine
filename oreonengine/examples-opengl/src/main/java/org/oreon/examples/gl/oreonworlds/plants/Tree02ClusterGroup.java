package org.oreon.examples.gl.oreonworlds.plants;

import java.nio.FloatBuffer;
import java.util.List;

import org.oreon.core.gl.instanced.GLInstancedObject;
import org.oreon.core.gl.memory.GLMeshVBO;
import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.util.GLAssimpModelLoader;
import org.oreon.core.gl.wrapper.parameter.CullFaceDisable;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.model.Vertex;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.IntegerReference;
import org.oreon.core.util.Util;
import org.oreon.examples.gl.oreonworlds.shaders.InstancedWireframeShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeBillboardShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeBillboardShadowShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeLeavesShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeShadowShader;
import org.oreon.examples.gl.oreonworlds.shaders.plants.TreeTrunkShader;
import org.oreon.gl.components.terrain.TerrainHelper;

public class Tree02ClusterGroup extends GLInstancedObject{
	
	public Tree02ClusterGroup(){
		
		setInstanceCount(3);
		Vec3f[] positions = { new Vec3f(-457.16794f,-37.229347f,937.7439f), new Vec3f(-696,0,932), new Vec3f(-765,0,976) };
		setPositions(positions);
		setHighPolyRange(400);
		
		List<Model> models = GLAssimpModelLoader.loadModel("oreonworlds/assets/plants/Tree_02","tree02.obj");
		
		List<Model> billboards = GLAssimpModelLoader.loadModel("oreonworlds/assets/plants/Tree_02","billboardmodel.obj");
		
		setHighPolyInstanceCount(new IntegerReference(0));
		setLowPolyInstanceCount(new IntegerReference(getInstanceCount()));
		
		for (Model model : models){
			
			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			if (model.equals(models.get(0))){
				model.getMesh().setTangentSpace(true);
				Util.generateTangentsBitangents(model.getMesh());
			}
			else
				model.getMesh().setTangentSpace(false);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPosition().setX(vertex.getPosition().getX()*1.2f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1.2f);
			}
			
			meshBuffer.addData(model.getMesh());
			meshBuffer.setDrawInstanced(true);
			meshBuffer.setInstances(getHighPolyInstanceCount());

			GLRenderInfo renderInfo;
			GLRenderInfo shadowRenderInfo;
			GLRenderInfo wireframeRenderInfo = new GLRenderInfo(InstancedWireframeShader.getInstance(), new CullFaceDisable(), meshBuffer);
			
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
			object.addComponent(NodeComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
			object.addComponent(NodeComponentType.MATERIAL0, model.getMaterial());
			addChild(object);
			getHighPolyObjects().add(object);
		}
		
		for (Model billboard : billboards){	
			
			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			billboard.getMesh().setTangentSpace(false);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPosition(vertex.getPosition().mul(2.4f));
				vertex.getPosition().setX(vertex.getPosition().getX()*1f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
			meshBuffer.setDrawInstanced(true);
			meshBuffer.setInstances(getLowPolyInstanceCount());
	
			GLRenderInfo renderInfo = new GLRenderInfo(TreeBillboardShader.getInstance(), new CullFaceDisable(), meshBuffer);
			GLRenderInfo shadowRenderInfo = new GLRenderInfo(TreeBillboardShadowShader.getInstance(), new CullFaceDisable(), meshBuffer);
			GLRenderInfo wireframeRenderInfo = new GLRenderInfo(InstancedWireframeShader.getInstance(), new CullFaceDisable(), meshBuffer);
			
			Renderable object = new Renderable();
			object.addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
			object.addComponent(NodeComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
			object.addComponent(NodeComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
			object.addComponent(NodeComponentType.MATERIAL0, billboard.getMaterial());
			addChild(object);
			getLowPolyObjects().add(object);
		}
		
		
		for (int i=0; i<getInstanceCount(); i++){
			
			float s = (float)(Math.random()*6 + 26);
			Vec3f translation = positions[i];
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,(float) Math.random()*360f,0);
			
			float terrainHeight = TerrainHelper.getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 1;
			translation.setY(terrainHeight);
			
			Matrix4f translationMatrix = new Matrix4f().Translation(translation);
			Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
			Matrix4f scalingMatrix = new Matrix4f().Scaling(scaling);
			
			getWorldMatrices().add(translationMatrix.mul(scalingMatrix.mul(rotationMatrix)));
			getModelMatrices().add(rotationMatrix);
			getLowPolyIndices().add(i);
		}
		
		int buffersize = Float.BYTES * 16 * getInstanceCount();
		
		setModelMatricesBuffer(new GLUniformBuffer());
		getModelMatricesBuffer().allocate(buffersize);
		
		setWorldMatricesBuffer(new GLUniformBuffer());
		getWorldMatricesBuffer().allocate(buffersize);	
		
		/**
		 * init matrices UBO's
		 */
		int size = Float.BYTES * 16 * getInstanceCount();
		
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
	}
}
