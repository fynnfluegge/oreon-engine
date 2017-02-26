package apps.oreonworlds.assets.plants;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import apps.oreonworlds.shaders.plants.Tree01Shader;
import engine.buffers.MeshVAO;
import engine.buffers.UBO;
import engine.configs.AlphaTest;
import engine.configs.AlphaTestCullFaceDisable;
import engine.configs.CullFaceDisable;
import engine.core.Camera;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.scenegraph.components.TransformsInstanced;
import engine.utils.BufferAllocation;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;
import modules.terrain.Terrain;

public class Tree01Instanced extends Node{

	private List<TransformsInstanced> transforms = new ArrayList<TransformsInstanced>();
	private List<TransformsInstanced> highPolyModelTransforms = new ArrayList<TransformsInstanced>();
	private List<TransformsInstanced> billboardTransforms = new ArrayList<TransformsInstanced>();
	
	private UBO highPolyModelMatricesBuffer;
	private UBO highPolyWorldMatricesBuffer;
	private UBO billboardModelMatricesBuffer;
	private UBO billboardWorldMatricesBuffer;

	private final int instances = 1;
	private final int buffersize = Float.BYTES * 16 * instances;
	private Vec3f center;
	
	private int highpolyModelMatBinding;
	private int highpolyWorldMatBinding;
	private int billboardModelMatBinding;
	private int billboardWorldMatBinding;

	public Tree01Instanced(Vec3f pos, int highpolyModelMatBinding, int highpolyWorldMatBinding, int billboardModelMatBinding, int billboardWorldMatBinding){
		
		center = pos;
		this.highpolyModelMatBinding = highpolyModelMatBinding;
		this.highpolyWorldMatBinding = highpolyWorldMatBinding;
		this.billboardModelMatBinding = billboardModelMatBinding;
		this.billboardWorldMatBinding = billboardWorldMatBinding;
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_01","tree.obj","tree02.mtl");
//		Model[] billboards = new OBJLoader().load("./res/oreonworlds/assets/plants/Tree_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (int i=0; i<instances; i++){
			Vec3f translation = new Vec3f((float)(Math.random()*500)-250 + center.getX(), 0, (float)(Math.random()*500)-250 + center.getZ());
			float terrainHeight = Terrain.getInstance().getTerrainHeight(translation.getX(),translation.getZ());
			terrainHeight -= 1;
			translation.setY(terrainHeight);
			float s = (float)(Math.random()*2 + 8);
			Vec3f scaling = new Vec3f(s,s,s);
			Vec3f rotation = new Vec3f(0,0,0);
			
			TransformsInstanced transform = new TransformsInstanced();
			transform.setTranslation(translation);
			transform.setScaling(scaling);
			transform.setRotation(rotation);
			transform.setLocalRotation(rotation);
			transform.initMatrices();
			transforms.add(transform);
		}
		
		highPolyModelMatricesBuffer = new UBO();
		highPolyModelMatricesBuffer.setBinding_point_index(highpolyModelMatBinding);
		highPolyModelMatricesBuffer.bindBufferBase();
		highPolyModelMatricesBuffer.allocate(buffersize);
		
		highPolyWorldMatricesBuffer = new UBO();
		highPolyWorldMatricesBuffer.setBinding_point_index(highpolyWorldMatBinding);
		highPolyWorldMatricesBuffer.bindBufferBase();
		highPolyWorldMatricesBuffer.allocate(buffersize);
		
		billboardModelMatricesBuffer = new UBO();
		billboardModelMatricesBuffer.setBinding_point_index(billboardModelMatBinding);
		billboardModelMatricesBuffer.bindBufferBase();
		billboardModelMatricesBuffer.allocate(buffersize);
		
		billboardWorldMatricesBuffer = new UBO();
		billboardWorldMatricesBuffer.setBinding_point_index(billboardWorldMatBinding);
		billboardWorldMatricesBuffer.bindBufferBase();
		billboardWorldMatricesBuffer.allocate(buffersize);
		
		
		for (Model model : models){
			
			GameObject object = new GameObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			model.getMesh().setInstances(instances);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new AlphaTest(0.6f), Tree01Shader.getInstance()));
			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);

			object.addComponent("Material", model.getMaterial());
			object.addComponent("Renderer", renderer);
			addChild(object);
		}
//		for (Model billboard : billboards){	
//			GameObject object = new GameObject();
//			MeshVAO meshBuffer = new MeshVAO();
//			billboard.getMesh().setTangentSpace(false);
//			billboard.getMesh().setInstanced(true);
//			billboard.getMesh().setInstances(0);
//			
//			for (Vertex vertex : billboard.getMesh().getVertices()){
//				vertex.setPos(vertex.getPos().mul(135));
//				vertex.getPos().setX(vertex.getPos().getX()*1.1f);
//				vertex.getPos().setZ(vertex.getPos().getZ()*1.1f);
//			}
//			
//			meshBuffer.addData(billboard.getMesh());
//	
//			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.4f), PalmBillboardShader.getInstance(), PalmBillboardShadowShader.getInstance()));
//			Renderer renderer = new Renderer(object.getRenderInfo().getShader(), meshBuffer);
//			
//			object.addComponent("Material", billboard.getMaterial());
//			object.addComponent("Renderer", renderer);
//			addChild(object);
//		}
		
		updateUBOs();
	}

	public void update()
	{	
		if (center.sub(Camera.getInstance().getPosition()).length() < 1550){
			
			updateUBOs();
			
//			System.out.println(center.sub(Camera.getInstance().getPosition()).length());
//			System.out.println(highPolyModelTransforms.size());
		}
	}
	
	public void updateUBOs(){
		
		highPolyModelTransforms.clear();
		billboardTransforms.clear();
		
		for (TransformsInstanced transform : transforms){
			if (transform.getTranslation().sub(Camera.getInstance().getPosition()).length() > 1000){
				billboardTransforms.add(transform);
			}
			else{
				highPolyModelTransforms.add(transform);
			}
		}
		
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(0)).getComponent("Renderer")).getVao()).setInstances(highPolyModelTransforms.size());
		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(1)).getComponent("Renderer")).getVao()).setInstances(highPolyModelTransforms.size());
		
//		((MeshVAO) ((Renderer) ((GameObject) getChildren().get(4)).getComponent("Renderer")).getVao()).setInstances(billboardTransforms.size());
		
		/**
		 * update matrices UBO's for high poly models
		 */
		int size = Float.BYTES * 16 * highPolyModelTransforms.size();
		
		FloatBuffer worldMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		FloatBuffer modelMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		
		for(TransformsInstanced matrix : highPolyModelTransforms){
			worldMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getWorldMatrix()));
			modelMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getModelMatrix()));
		}
		
		highPolyWorldMatricesBuffer.updateData(worldMatricesFloatBuffer, size);
		highPolyModelMatricesBuffer.updateData(modelMatricesFloatBuffer, size);
		
		/**
		 * update matrices UBO's fora billboard models
		 */
		size = Float.BYTES * 16 * billboardTransforms.size();
		worldMatricesFloatBuffer.clear();
		modelMatricesFloatBuffer.clear();
		worldMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		modelMatricesFloatBuffer = BufferAllocation.createFloatBuffer(size);
		
		for(TransformsInstanced matrix : billboardTransforms){
			worldMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getWorldMatrix()));
			modelMatricesFloatBuffer.put(BufferAllocation.createFlippedBuffer(matrix.getModelMatrix()));
		}
		
		billboardWorldMatricesBuffer.updateData(worldMatricesFloatBuffer, size);
		billboardModelMatricesBuffer.updateData(modelMatricesFloatBuffer, size);
	}

	public int getHighpolyModelMatBinding() {
		return highpolyModelMatBinding;
	}

	public void setHighpolyModelMatBinding(int highpolyModelMatBinding) {
		this.highpolyModelMatBinding = highpolyModelMatBinding;
	}

	public int getHighpolyWorldMatBinding() {
		return highpolyWorldMatBinding;
	}

	public void setHighpolyWorldMatBinding(int highpolyWorldMatBinding) {
		this.highpolyWorldMatBinding = highpolyWorldMatBinding;
	}

	public int getBillboardModelMatBinding() {
		return billboardModelMatBinding;
	}

	public void setBillboardModelMatBinding(int billboardModelMatBinding) {
		this.billboardModelMatBinding = billboardModelMatBinding;
	}

	public int getBillboardWorldMatBinding() {
		return billboardWorldMatBinding;
	}

	public void setBillboardWorldMatBinding(int billboardWorldMatBinding) {
		this.billboardWorldMatBinding = billboardWorldMatBinding;
	}
}
