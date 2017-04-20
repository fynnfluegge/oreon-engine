package modules.terrain;

import engine.core.Camera;
import engine.math.Vec2f;
import engine.scenegraph.Node;
import engine.shader.Shader;
import engine.utils.Constants;


public class Terrain extends Node{
	
	private static Terrain instance = null;
	private TerrainConfiguration terrainConfiguration;
	private static final Object lock = new Object();
	private int updateQuadtreeCounter = 0;
	
	public static Terrain getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Terrain();
	    }
	      return instance;
	}
		
	public void init (String file, Shader shader, Shader grid, Shader shadow)
	{
		terrainConfiguration = new TerrainConfiguration();
		
		terrainConfiguration.loadFile(file);
		terrainConfiguration.setGridShader(grid);
		terrainConfiguration.setShader(shader);
		terrainConfiguration.setShadowShader(shadow);
		
		addChild(new TerrainQuadtree(terrainConfiguration));
	}
	
	public void updateQuadtree(){
		if (Camera.getInstance().isCameraMoved()){
			updateQuadtreeCounter++;
		}
		if (updateQuadtreeCounter == 1){
			((TerrainQuadtree) getChildren().get(0)).updateQuadtree();
			updateQuadtreeCounter = 0;
		}
	}
	
	public float getTerrainHeight(float x, float z)
	{
		float h = 0;
		for (int i =0; i<7; i++){
			float fractalHeight = 0;
			Vec2f pos = new Vec2f();
			pos.setX(x);
			pos.setY(z);
			pos = pos.add(terrainConfiguration.getScaleXZ()/2f);
			pos = pos.div(terrainConfiguration.getScaleXZ());
			pos = pos.mul(terrainConfiguration.getFractals().get(i).getScaling());
			Vec2f floor = new Vec2f((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()));
			pos = pos.sub(floor);
			pos = pos.mul(Constants.TERRAIN_FRACTALS_RESOLUTION-1);
			int x0 = (int) Math.floor(pos.getX());
			int x1 = x0 + 1;
			int z0 = (int) Math.floor(pos.getY());
			int z1 = z0 + 1;
			
			float h0 =  terrainConfiguration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x0);
			float h1 =  terrainConfiguration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x1);
			float h2 =  terrainConfiguration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x0);
			float h3 =  terrainConfiguration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x1);
			
			float percentU = pos.getX() - x0;
	        float percentV = pos.getY() - z0;
	        
	        float dU, dV;
	        if (percentU > percentV)
	        {   // bottom triangle
	            dU = h1 - h0;
	            dV = h3 - h1;
	        }
	        else
	        {   // top triangle
	            dU = h3 - h2;
	            dV = h2 - h0;
	        }
	        
	        fractalHeight = h0 + (dU * percentU) + (dV * percentV );
	        fractalHeight *= terrainConfiguration.getScaleY()*terrainConfiguration.getFractals().get(i).getStrength();
			h += fractalHeight;
		}

		return h;
	}
	
	public float getTerrainSlope(float x, float z)
	{
		float h = 0;
		for (int i =0; i<7; i++){
			float fractalHeight = 0;
			Vec2f pos = new Vec2f();
			pos.setX(x);
			pos.setY(z);
			pos = pos.add(terrainConfiguration.getScaleXZ()/2f);
			pos = pos.div(terrainConfiguration.getScaleXZ());
			pos = pos.mul(terrainConfiguration.getFractals().get(i).getScaling());
			Vec2f floor = new Vec2f((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()));
			pos = pos.sub(floor);
			pos = pos.mul(Constants.TERRAIN_FRACTALS_RESOLUTION-1);
			int x0 = (int) Math.floor(pos.getX());
			int x1 = x0 + 1;
			int z0 = (int) Math.floor(pos.getY());
			int z1 = z0 + 1;
			
			float h0 =  terrainConfiguration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x0);
			float h1 =  terrainConfiguration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x1);
			float h2 =  terrainConfiguration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x0);
			float h3 =  terrainConfiguration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x1);
			
			float percentU = pos.getX() - x0;
	        float percentV = pos.getY() - z0;
	        
	        float dU, dV;
	        if (percentU > percentV)
	        {   // bottom triangle
	            dU = h1 - h0;
	            dV = h3 - h1;
	        }
	        else
	        {   // top triangle
	            dU = h3 - h2;
	            dV = h2 - h0;
	        }
	        
	        fractalHeight = h0 + (dU * percentU) + (dV * percentV );
	        fractalHeight *= terrainConfiguration.getScaleY()*terrainConfiguration.getFractals().get(i).getStrength();
			h += fractalHeight;
		}

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
