package engine.renderer.terrain;

import engine.core.Texture;
import engine.core.Vertex;
import engine.gameObject.GameObject;
import engine.gameObject.components.Model;
import engine.gameObject.components.PatchRenderer;
import engine.gameObject.components.Renderer;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import engine.models.data.Material;
import engine.models.data.Patch;
import engine.renderpipeline.configs.AlphaBlending;
import engine.renderpipeline.data.PatchVAO;
import engine.renderpipeline.shaderPrograms.terrain.TerrainGrid;
import engine.renderpipeline.shaderPrograms.terrain.TerrainPhongBumpy;


public class Terrain extends GameObject{
	
	private String hm_file;
	private float scaleY;
	private float scaleXZ;
	private int bezíer;
	private int patchAmount = 64;
	private float sightRangeFactor = 4f;
	private float texDetail;
	private int tessellationFactor;
	private float tessellationSlope;
	private float tessellationShift;
	private int detailRange;
	private Texture heightmap;
	private Texture normalmap;
	private Texture ambientmap;
	private Texture splatmap;
	private Material material1;
	private Material material2;
	private Material material3;
	private Vec3f gridColor;
	
	private boolean initialized = false;
		
	public Terrain(String file)
	{
		getTransform().setLocalScaling(this.scaleXZ, this.scaleY, this.scaleXZ);
		getTransform().setLocalTranslation(-(this.scaleXZ/2),0,-(this.scaleXZ/2));	
		
		this.initialized = true;
		this.heightmap = new Texture("./res/textures/maps/terrain/height/"+ file);
		this.normalmap =  new Texture("./res/textures/maps/terrain/normal/" + file);
		this.setAmbientmap(new Texture("./res/textures/maps/terrain/occlusion/" + file));
		this.setSplatmap(new Texture("./res/textures/maps/terrain/splat/" + file));
		
		PatchVAO meshBuffer = new PatchVAO();
		Model model = new Model(new Patch(generatePatchs4x4()));
		PatchRenderer renderer = new PatchRenderer(meshBuffer, TerrainPhongBumpy.getInstance(), new AlphaBlending(0.0f));
		meshBuffer.addData(model.getPatch(),16);
		
		material1 = new Material();
		material1.setEmission(0);
		material1.setShininess(20);
		material1.setDiffusemap(new Texture("./res/textures/grass/grass1.jpg"));
		material1.getDiffusemap().bind();
		material1.getDiffusemap().mipmap();
		material1.setNormalmap(new Texture("./res/textures/maps/normal/grass/grass1.jpg"));
		material1.getNormalmap().bind();
		material1.getNormalmap().mipmap();
		material1.setDisplacemap(new Texture("./res/textures/maps/displacement/grass/grass1.jpg"));
		material1.getDisplacemap().bind();
		material1.getDisplacemap().mipmap();
		
		material2 = new Material();
		material2.setEmission(0);
		material2.setShininess(20);
		material2.setDiffusemap(new Texture("./res/textures/rock/rock4.jpg"));
		material2.getDiffusemap().bind();
		material2.getDiffusemap().mipmap();
		material2.setNormalmap(new Texture("./res/textures/maps/normal/rock/rock4.jpg"));
		material2.getNormalmap().bind();
		material2.getNormalmap().mipmap();
		material2.setDisplacemap(new Texture("./res/textures/maps/displacement/rock/rock4.jpg"));
		material2.getDisplacemap().bind();
		material2.getDisplacemap().mipmap();
		
		material3 = new Material();
		material3.setEmission(0);
		material3.setShininess(20);
		material3.setDiffusemap(new Texture("./res/textures/rock/rock4.jpg"));
		material3.getDiffusemap().bind();
		material3.getDiffusemap().mipmap();
		material3.setNormalmap(new Texture("./res/textures/maps/normal/rock/rock4.jpg"));
		material3.getNormalmap().bind();
		material3.getNormalmap().mipmap();
		material3.setDisplacemap(new Texture("./res/textures/maps/displacement/rock/rock4.jpg"));
		material3.getDisplacemap().bind();
		material3.getDisplacemap().mipmap();
		
		gridColor = new Vec3f(0.1f,0.1f,0.1f);
		
		addComponent("Model", model);
		addComponent("Renderer", renderer);
	}
	
	public void texturing()
	{	
//		((Surface) getComponent("Surface")).getMaterials().get(0).loadTexture();
//		((Surface) getComponent("Surface")).getMaterials().get(0).loadNormalmap();
//		((Surface) getComponent("Surface")).getMaterials().get(0).loadDisplacemap();
//		
//		((Surface) getComponent("Surface")).getMaterials().get(1).loadTexture();
//		((Surface) getComponent("Surface")).getMaterials().get(1).loadNormalmap();
//		((Surface) getComponent("Surface")).getMaterials().get(1).loadDisplacemap();
//		
//		((Surface) getComponent("Surface")).getMaterials().get(2).loadTexture();
//		((Surface) getComponent("Surface")).getMaterials().get(2).loadNormalmap();
//		((Surface) getComponent("Surface")).getMaterials().get(2).loadDisplacemap();
//		
//		((Surface) getComponent("Surface")).getMaterials().get(3).loadTexture();
//		((Surface) getComponent("Surface")).getMaterials().get(3).loadNormalmap();
//		((Surface) getComponent("Surface")).getMaterials().get(3).loadDisplacemap();
	}
	
	
	public Vertex[] generatePatchs4x4()
	{
		
		int amountx = patchAmount; 
		int amounty = patchAmount;
		
		// 16 vertices for each patch
		Vertex[] vertices = new Vertex[amountx * amounty * 16];
		
		int index = 0;
		float dx = 1f/(float)amountx;
		float dy = 1f/(float)amounty;
		
		for (float i=0; i<1; i+=dx)
		{
			for (float j=0; j<1; j+=dy)
			{
				vertices[index++] = new Vertex(new Vec3f(i,0,j));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy*0.33f));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy*0.33f));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy*0.66f));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy*0.66f));
				
				vertices[index++] = new Vertex(new Vec3f(i,0,j+dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.33f,0,j+dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx*0.66f,0,j+dy));
				vertices[index++] = new Vertex(new Vec3f(i+dx,0,j+dy));
			}
		}
		
		return vertices;
	}
	
	public void update()
	{		
			getTransform().setLocalScaling(scaleXZ, scaleY, scaleXZ);
			getTransform().getLocalTranslation().setX(-scaleXZ/2);
			getTransform().getLocalTranslation().setZ(-scaleXZ/2);
			
			getTransform().setScaling(getTransform().getLocalScaling());
			getTransform().setTranslation(getTransform().getLocalTranslation());
			
			if (RenderingEngine.isGrid() && initialized)
			{
				((Renderer) getComponents().get("Renderer")).setShader(TerrainGrid.getInstance());
			}
			else if (!RenderingEngine.isGrid() && initialized)
			{
				((Renderer) getComponents().get("Renderer")).setShader(TerrainPhongBumpy.getInstance());
			}
	}
	
	// TODO rework
	public float getTerrainHeight(float x, float z)
	{
		float h = 0;
		
		/*Vec3f vertexIndices = new Vec3f((x/this.scaleXZ)*this.hm_width, 0, (z/this.scaleXZ)*this.hm_height); 

		int x0 = (int) Math.floor(vertexIndices.getX());
		int x1 = x0 + 1;
		int z0 = (int) Math.floor(vertexIndices.getZ());
		int z1 = z0 + 1;
		
		Vec3f v0 = vertices[(z0 * hm_height) + x0].getPos(); //bottom left
		Vec3f v1 = vertices[(z0 * hm_height) + x1].getPos(); //bottom right
		Vec3f v2 = vertices[(z1 * hm_height) + x0].getPos(); //top left
		Vec3f v3 = vertices[(z1 * hm_height) + x1].getPos(); //top right
		
		
		float percentU = vertexIndices.getX() - x0;
        float percentV = vertexIndices.getZ() - z0;
        
        Vec3f dU, dV;
        if (percentU > percentV)
        {   // bottom triangle
            dU = v1.sub(v0);
            dV = v3.sub(v1);
        }
        else
        {   // top triangle
            dU = v3.sub(v2);
            dV = v2.sub(v0);
        }
        
        Vec3f heightPos = v0.add(( dU.mul(percentU).add(dV.mul(percentV ))));
        h = heightPos.getY();*/
   		
		return h;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getScaleXZ() {
		return scaleXZ;
	}

	public void setScaleXZ(float scaleXZ) {
		this.scaleXZ = scaleXZ;
	}

	public String gethm_file() {
		return hm_file;
	}


	public void sethm_file(String hm_file) {
		this.hm_file = hm_file;
	}


	public float getTexDetail() {
		return texDetail;
	}


	public void setTexDetail(int texDetail) {
		this.texDetail = texDetail;
	}


	public Material getMaterial1() {
		return material1;
	}


	public void setMaterial1(Material material) {
		this.material1 = material;
	}


	public Material getMaterial2() {
		return material2;
	}


	public void setMaterial2(Material material) {
		this.material2 = material;
	}


	public Material getMaterial3() {
		return material3;
	}


	public void setMaterial3(Material material) {
		this.material3 = material;
	}


	public int getPatchAmount() {
		return patchAmount;
	}


	public void setPatchAmount(int patchAmount) {
		this.patchAmount = patchAmount;
	}


	public float getSightRangeFactor() {
		return sightRangeFactor;
	}


	public void setSightRangeFactor(float sightRangeFactor) {
		this.sightRangeFactor = sightRangeFactor;
	}


	public int getBezíer() {
		return bezíer;
	}

	public void setBezíer(int bezíer) {
		this.bezíer = bezíer;
	}

	public int getTessellationFactor() {
		return tessellationFactor;
	}

	public void setTessellationFactor(int tessellationFactor) {
		this.tessellationFactor = tessellationFactor;
	}

	public float getTessellationSlope() {
		return tessellationSlope;
	}

	public void setTessellationSlope(float tessellationSlope) {
		this.tessellationSlope = tessellationSlope;
	}

	public int getDetailRange() {
		return detailRange;
	}

	public void setDetailRange(int detailRange) {
		this.detailRange = detailRange;
	}

	public Texture getHeightmap() {
		return heightmap;
	}

	public void setHeightmap(Texture heightmap) {
		this.heightmap = heightmap;
	}

	public Texture getNormalmap() {
		return normalmap;
	}

	public void setNormalmap(Texture normalmap) {
		this.normalmap = normalmap;
	}

	public Texture getSplatmap() {
		return splatmap;
	}

	public void setSplatmap(Texture splatmap) {
		this.splatmap = splatmap;
	}

	public float getTessellationShift() {
		return tessellationShift;
	}

	public void setTessellationShift(float tessellationShift) {
		this.tessellationShift = tessellationShift;
	}

	public Vec3f getGridColor() {
		return gridColor;
	}

	public void setGridColor(Vec3f color) {
		this.gridColor = color;
	}

	public Texture getAmbientmap() {
		return ambientmap;
	}

	public void setAmbientmap(Texture ambientmap) {
		this.ambientmap = ambientmap;
	}
}
