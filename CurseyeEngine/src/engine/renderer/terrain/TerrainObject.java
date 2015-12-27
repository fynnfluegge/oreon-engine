package engine.renderer.terrain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import engine.buffers.PatchVAO;
import engine.configs.AlphaBlending;
import engine.core.Texture;
import engine.core.Util;
import engine.core.Vertex;
import engine.gameObject.GameObject;
import engine.gameObject.components.Model;
import engine.gameObject.components.PatchRenderer;
import engine.gameObject.components.Renderer;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import engine.models.data.Material;
import engine.models.data.Patch;
import engine.shaderprograms.terrain.TerrainGrid;
import engine.shaderprograms.terrain.TerrainTessellation;


public class TerrainObject extends GameObject{
	
	private float scaleY;
	private float scaleXZ;
	private int bezíer;
	private float sightRangeFactor;
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
	
	private static final Object lock = new Object();
		
	public TerrainObject(int patches)
	{
		PatchVAO meshBuffer = new PatchVAO();
		Model model = new Model(new Patch(generatePatchs4x4(patches)));
		PatchRenderer renderer = new PatchRenderer(meshBuffer, TerrainTessellation.getInstance(), new AlphaBlending(0.0f));
		meshBuffer.addData(model.getPatch(),16);
		addComponent("Renderer", renderer);
		addComponent("Model", model);
	}
	
	public void loadSettingsFile(String file)
	{
		BufferedReader reader = null;
		
		try{
			if(new File(file).exists()){
				reader = new BufferedReader(new FileReader(file));
				String line;
				
				while((line = reader.readLine()) != null){
					
					String[] tokens = line.split(" ");
					tokens = Util.removeEmptyStrings(tokens);
					
					if(tokens.length == 0)
						continue;
					if(tokens[0].equals("scaleY")){
						scaleY = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("scaleXZ")){
						scaleXZ = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("heightmap")){
						heightmap = new Texture(tokens[1]);
						heightmap.bind();
						heightmap.mipmap();
					}
					if(tokens[0].equals("normalmap")){
						normalmap = new Texture(tokens[1]);
						normalmap.bind();
						normalmap.mipmap();
					}
					if(tokens[0].equals("ambientmap")){
						ambientmap = new Texture(tokens[1]);
						ambientmap.bind();
						ambientmap.mipmap();
					}
					if(tokens[0].equals("splatmap")){
						splatmap = new Texture(tokens[1]);
						splatmap.bind();
						splatmap.mipmap();
					}
					if(tokens[0].equals("material1_DIF")){
						setMaterial1(new Material());
						getMaterial1().setDiffusemap(new Texture(tokens[1]));
						getMaterial1().getDiffusemap().bind();
						getMaterial1().getDiffusemap().mipmap();
					}
					if(tokens[0].equals("material1_NRM")){
						getMaterial1().setNormalmap(new Texture(tokens[1]));
						getMaterial1().getNormalmap().bind();
						getMaterial1().getNormalmap().mipmap();
					}
					if(tokens[0].equals("material1_DISP")){
						getMaterial1().setDisplacemap(new Texture(tokens[1]));
						getMaterial1().getDisplacemap().bind();
						getMaterial1().getDisplacemap().mipmap();
					}
					if(tokens[0].equals("material1_displaceScale")){
						getMaterial1().setDisplaceScale(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material1_emission")){
						getMaterial1().setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material1_shininess")){
						getMaterial1().setShininess(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material2_DIF")){
						setMaterial2(new Material());
						getMaterial2().setDiffusemap(new Texture(tokens[1]));
						getMaterial2().getDiffusemap().bind();
						getMaterial2().getDiffusemap().mipmap();
					}
					if(tokens[0].equals("material2_NRM")){
						getMaterial2().setNormalmap(new Texture(tokens[1]));
						getMaterial2().getNormalmap().bind();
						getMaterial2().getNormalmap().mipmap();
					}
					if(tokens[0].equals("material2_DISP")){
						getMaterial2().setDisplacemap(new Texture(tokens[1]));
						getMaterial2().getDisplacemap().bind();
						getMaterial2().getDisplacemap().mipmap();
					}
					if(tokens[0].equals("material2_displaceScale")){
						getMaterial2().setDisplaceScale(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material2_emission")){
						getMaterial2().setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material2_shininess")){
						getMaterial2().setShininess(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material3_DIF")){
						setMaterial3(new Material());
						getMaterial3().setDiffusemap(new Texture(tokens[1]));
						getMaterial3().getDiffusemap().bind();
						getMaterial3().getDiffusemap().mipmap();
					}
					if(tokens[0].equals("material3_NRM")){
						getMaterial3().setNormalmap(new Texture(tokens[1]));
						getMaterial3().getNormalmap().bind();
						getMaterial3().getNormalmap().mipmap();
					}
					if(tokens[0].equals("material3_DISP")){
						getMaterial3().setDisplacemap(new Texture(tokens[1]));
						getMaterial3().getDisplacemap().bind();
						getMaterial3().getDisplacemap().mipmap();
					}
					if(tokens[0].equals("material3_displaceScale")){
						getMaterial3().setDisplaceScale(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material3_emission")){
						getMaterial3().setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material3_shininess")){
						getMaterial3().setShininess(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("tessellationFactor")){
						tessellationFactor = Integer.valueOf(tokens[1]);
					}
					if(tokens[0].equals("tessellationSlope")){
						tessellationSlope = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("tessellationShift")){
						tessellationShift = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("texDetail")){
						texDetail = Float.valueOf(tokens[1]);
					}
					if(tokens[0].equals("bezíer")){
						bezíer = Integer.valueOf(tokens[1]);
					}
					if(tokens[0].equals("detailRange")){
						detailRange = Integer.valueOf(tokens[1]);
					}
				}
				reader.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	public Vertex[] generatePatchs4x4(int patches)
	{
		
		int amountx = patches; 
		int amounty = patches;
		
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
			
			if (RenderingEngine.isGrid())
			{
				((Renderer) getComponents().get("Renderer")).setShader(TerrainGrid.getInstance());
			}
			else if (!RenderingEngine.isGrid())
			{
				((Renderer) getComponents().get("Renderer")).setShader(TerrainTessellation.getInstance());
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

	public Texture getAmbientmap() {
		return ambientmap;
	}

	public void setAmbientmap(Texture ambientmap) {
		this.ambientmap = ambientmap;
	}

	public static Object getLock() {
		return lock;
	}
}
