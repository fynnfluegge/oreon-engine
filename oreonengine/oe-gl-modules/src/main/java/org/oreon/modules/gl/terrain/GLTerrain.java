package org.oreon.modules.gl.terrain;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.terrain.Terrain;

public class GLTerrain extends Terrain{
	
	private TerrainConfiguration configuration;
	private TerrainConfiguration lowPolyConfiguration;
	private int updateQuadtreeCounter = 0;	
		
	public void init (String config, String lowPolyConfig, GLShader shader, GLShader grid, GLShader shadow)
	{
		configuration = new TerrainConfiguration();
		configuration.loadFile(config);
		configuration.setGridShader(grid);
		configuration.setShader(shader);
		configuration.setShadowShader(shadow);
		
		lowPolyConfiguration = new TerrainConfiguration();
		lowPolyConfiguration.loadFile(lowPolyConfig);
		lowPolyConfiguration.setGridShader(grid);
		lowPolyConfiguration.setShader(shader);
		lowPolyConfiguration.setShadowShader(shadow);
		
		addChild(new TerrainQuadtree(configuration));
		addChild(new TerrainQuadtree(lowPolyConfiguration));
		
		setThread(new Thread(this));
		getThread().start();
	}
	
	@Override
	public void run() {
		while(isRunning()){
			
			getLock().lock();
			try{
				getCondition().await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				getLock().unlock();
			}
			
			updateQuadtree();
		}
	}
	
	public void updateQuadtree(){
		
		updateQuadtreeCounter++;
		
		if (updateQuadtreeCounter == 1){
			
			((TerrainQuadtree) getChildren().get(0)).updateQuadtree();
			((TerrainQuadtree) getChildren().get(1)).updateQuadtree();
			
			updateQuadtreeCounter = 0;
		}
	}
	
	@Override
	public void render() {
		// render high poly terrain (first child)
		getChildren().get(0).render();
	}
	
	public void renderLowPoly() {
		// render low poly terrain (second child)
		getChildren().get(1).render();
	}
	
	@Override
	public float getTerrainHeight(float x, float z){
		
		float h = 0;
		
		Vec2f pos = new Vec2f();
		pos.setX(x);
		pos.setY(z);
		pos = pos.add(configuration.getScaleXZ()/2f);
		pos = pos.div(configuration.getScaleXZ());
		Vec2f floor = new Vec2f((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()));
		pos = pos.sub(floor);
		pos = pos.mul(configuration.getHeightmap().getWidth());
		int x0 = (int) Math.floor(pos.getX());
		int x1 = x0 + 1;
		int z0 = (int) Math.floor(pos.getY());
		int z1 = z0 + 1;
		
		float h0 =  configuration.getHeightmapDataBuffer().get(configuration.getHeightmap().getWidth() * z0 + x0);
		float h1 =  configuration.getHeightmapDataBuffer().get(configuration.getHeightmap().getWidth() * z0 + x1);
		float h2 =  configuration.getHeightmapDataBuffer().get(configuration.getHeightmap().getWidth() * z1 + x0);
		float h3 =  configuration.getHeightmapDataBuffer().get(configuration.getHeightmap().getWidth() * z1 + x1);
		
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
        
        h = h0 + (dU * percentU) + (dV * percentV );
        h *= configuration.getScaleY();
		
		return h;
	}
	
	public TerrainConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(TerrainConfiguration configuration) {
		this.configuration = configuration;
	}

	public TerrainConfiguration getLowPolyConfiguration() {
		return lowPolyConfiguration;
	}

	public void setLowPolyConfiguration(TerrainConfiguration lowPolyConfiguration) {
		this.lowPolyConfiguration = lowPolyConfiguration;
	}
}
