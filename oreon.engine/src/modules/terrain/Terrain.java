package modules.terrain;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import engine.core.Camera;
import engine.math.Vec2f;
import engine.scenegraph.Node;
import engine.shaders.Shader;
import engine.utils.Constants;


public class Terrain extends Node implements Runnable{
	
	private static Terrain instance = null;
	private TerrainConfiguration configuration;
	private TerrainConfiguration lowPolyConfiguration;
	private int updateQuadtreeCounter = 0;	
	private Thread thread;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public static Terrain getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Terrain();
	    }
	      return instance;
	}
		
	public void init (String config, String lowPolyConfig, Shader shader, Shader grid, Shader shadow)
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
		while(true){
			lock.lock();
			try{
				condition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				lock.unlock();
			}
			
			updateQuadtree();
		}
	}
	
	public void updateQuadtree(){
		if (Camera.getInstance().isCameraMoved()){
			updateQuadtreeCounter++;
		}
		if (updateQuadtreeCounter == 1){
			
			((TerrainQuadtree) getChildren().get(0)).updateQuadtree();
			((TerrainQuadtree) getChildren().get(1)).updateQuadtree();
			
			updateQuadtreeCounter = 0;
		}
	}
	
	@Override
	public void render() {
		// render only high poly terrain
		getChildren().get(0).render();
	}
	
	public void renderLowPoly() {
		getChildren().get(1).render();
	}
	
	public float getTerrainHeight(float x, float z)
	{
		float h = 0;
		for (int i =0; i<7; i++){
			float fractalHeight = 0;
			Vec2f pos = new Vec2f();
			pos.setX(x);
			pos.setY(z);
			pos = pos.add(configuration.getScaleXZ()/2f);
			pos = pos.div(configuration.getScaleXZ());
			pos = pos.mul(configuration.getFractals().get(i).getScaling());
			Vec2f floor = new Vec2f((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()));
			pos = pos.sub(floor);
			pos = pos.mul(Constants.TERRAIN_FRACTALS_RESOLUTION-1);
			int x0 = (int) Math.floor(pos.getX());
			int x1 = x0 + 1;
			int z0 = (int) Math.floor(pos.getY());
			int z1 = z0 + 1;
			
			float h0 =  configuration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x0);
			float h1 =  configuration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x1);
			float h2 =  configuration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x0);
			float h3 =  configuration.getFractals().get(i).getHeightDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x1);
			
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
	        fractalHeight *= configuration.getScaleY()*configuration.getFractals().get(i).getStrength();
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
			pos = pos.add(configuration.getScaleXZ()/2f);
			pos = pos.div(configuration.getScaleXZ());
			pos = pos.mul(configuration.getFractals().get(i).getScaling());
			Vec2f floor = new Vec2f((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()));
			pos = pos.sub(floor);
			pos = pos.mul(Constants.TERRAIN_FRACTALS_RESOLUTION-1);
			int x0 = (int) Math.floor(pos.getX());
			int x1 = x0 + 1;
			int z0 = (int) Math.floor(pos.getY());
			int z1 = z0 + 1;
			
			float h0 =  configuration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x0);
			float h1 =  configuration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z0 + x1);
			float h2 =  configuration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x0);
			float h3 =  configuration.getFractals().get(i).getSlopeDataBuffer().get(Constants.TERRAIN_FRACTALS_RESOLUTION * z1 + x1);
			
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
	        fractalHeight *= configuration.getScaleY()*configuration.getFractals().get(i).getStrength();
			h += fractalHeight;
		}

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
	
	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}
	
	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}
}
