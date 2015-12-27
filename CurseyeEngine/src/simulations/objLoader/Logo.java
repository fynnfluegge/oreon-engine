package simulations.objLoader;

import org.lwjgl.input.Keyboard;

import engine.buffers.PatchVAO;
import engine.configs.AlphaBlending;
import engine.core.Input;
import engine.core.Texture;
import engine.core.Vertex;
import engine.gameObject.GameObject;
import engine.gameObject.components.Model;
import engine.gameObject.components.PatchRenderer;
import engine.main.RenderingEngine;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.models.data.Material;
import engine.models.data.Patch;

public class Logo extends GameObject{
	
	private boolean startLightShow = false;
	
	public Logo(){
		
		getTransform().setLocalScaling(500f,200f,1f);
		getTransform().setLocalRotation(-20, 0, 0);
		
		PatchVAO meshBuffer = new PatchVAO();
		Model model = new Model(new Patch(generatePatchs4x4()));
		PatchRenderer renderer = new PatchRenderer(meshBuffer, engine.shaderprograms.phong.Tessellation.getInstance(), new AlphaBlending(0));
		meshBuffer.addData(model.getPatch(),16);
		
		Material material = new Material();
		material.setDiffusemap(new Texture("./res/textures/text.png"));
		material.getDiffusemap().bind();
		material.getDiffusemap().mipmap();
		material.setNormalmap(new Texture("./res/textures/text_NRM.jpg"));
		material.getNormalmap().bind();
		material.getNormalmap().mipmap();
		material.setDisplacemap(new Texture("./res/textures/text_DISP.jpg"));
		material.getDisplacemap().bind();
		material.getDisplacemap().mipmap();
		material.setSpecularmap(new Texture("./res/textures/text_SPEC.jpg"));
		material.getSpecularmap().bind();
		material.getSpecularmap().mipmap();
		material.setDisplaceScale(5);
		material.setEmission(3);
		material.setShininess(20);
		model.setMaterial(material);
		
		addComponent("Model", model);
		addComponent("Renderer", renderer);
	}
	
	public Vertex[] generatePatchs4x4()
	{
		
		int amountx = 4; 
		int amounty = 1;
		
		// 16 vertices for each patch
		Vertex[] vertices = new Vertex[amountx * amounty * 16];
		
		int index = 0;
		float dx = 1f/amountx;
		float dy = 1f/amounty;
		
		for (float i=0; i<1; i+=dx)
		{
			for (float j=0; j<1; j+=dy)
			{	
				vertices[index++] = new Vertex(new Vec3f(i,0,j), new Vec2f(i,1-j));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j), new Vec2f(i+dx,1-j));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy*0.33f));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy*0.66f));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy),  new Vec2f(i,1-j-dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy), new Vec2f(i+dx,1-j-dy));

			}
		}
		
		return vertices;
	}
	
	public void update(){
		super.update();
		
		if(Input.getHoldingKey(Keyboard.KEY_E))
			startLightShow = true;
			
		if (startLightShow){
			if (RenderingEngine.getDirectionalLight().getDirection().getX() > -1)
				RenderingEngine.getDirectionalLight().getDirection().setX(RenderingEngine.getDirectionalLight().getDirection().getX() - 0.01f);
			else if (RenderingEngine.getDirectionalLight().getDirection().getX() < 1 )
				RenderingEngine.getDirectionalLight().getDirection().setX(RenderingEngine.getDirectionalLight().getDirection().getX() + 0.01f);
			if (RenderingEngine.getDirectionalLight().getIntensity() < 0.5f)
				RenderingEngine.getDirectionalLight().setIntensity(RenderingEngine.getDirectionalLight().getIntensity() + 0.0025f);
		}
	}
}
