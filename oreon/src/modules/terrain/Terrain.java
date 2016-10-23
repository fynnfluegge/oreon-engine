package modules.terrain;

import engine.scenegraph.Node;
import engine.shaders.Shader;


public class Terrain extends Node{
	
	private TerrainConfiguration terrainConfiguration;
	private static final Object lock = new Object();
		
	public Terrain(String file, Shader shader, Shader grid, Shader shadow)
	{
		terrainConfiguration = new TerrainConfiguration();
		terrainConfiguration.loadFile(file);
		terrainConfiguration.setGridShader(grid);
		terrainConfiguration.setShader(shader);
		terrainConfiguration.setShadowShader(shadow);
		addChild(new TerrainQuadtree(terrainConfiguration));
	}
	
	// TODO rework
	public float getTerrainHeight(float x, float z)
	{
		float h = 0;
		
		// heightmap sampling
//		int xWidth = (int) ((location.getX() + gap/2f) * (float) (terrConfig.getHeightmapSampler().getWidth()));
//		int xHeight = (int) ((1f - (location.getY() + gap/2f)) * (float) (terrConfig.getHeightmapSampler().getHeight()));
//		if (xWidth == terrConfig.getHeightmapSampler().getWidth()) xWidth--;
//		if (xHeight == terrConfig.getHeightmapSampler().getWidth()) xHeight--;
//		byte[] b = ByteBuffer.allocate(4).putInt(terrConfig.getHeightmapSampler().getRGB(xWidth, xHeight)).array();
//		float height = ((float) (b[1]+128)/255f) * terrConfig.getScaleY();
		
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


	public static Object getLock() {
		return lock;
	}

	public TerrainConfiguration getTerrainConfiguration() {
		return terrainConfiguration;
	}

	public void setTerrainConfiguration(TerrainConfiguration terrainConfiguration) {
		this.terrainConfiguration = terrainConfiguration;
	}
}
