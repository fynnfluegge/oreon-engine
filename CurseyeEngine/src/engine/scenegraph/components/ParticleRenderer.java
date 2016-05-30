package engine.scenegraph.components;

import engine.buffers.ParticleSystemVAO;
import engine.configs.RenderingConfig;
import engine.core.Camera;
import engine.modeling.Particle;
import engine.shaders.Shader;
import engine.shaders.particles.ParticleShader;

public class ParticleRenderer extends Renderer{

	private ParticleSystemVAO vao;
	private int delete;
	private int countdown;
	private long timeMillis;
	
	// TODO full rework
	
	public ParticleRenderer(Shader shader, RenderingConfig config, Material material)
	{
		super(config, shader);
		this.vao = new ParticleSystemVAO();
	}
	
	public void init(Particle[] particles)
	{
		vao.init(particles);
	}

	public void render()
	{
		getShader().execute();
		getShader().sendUniforms(getTransform().getWorldMatrix(), Camera.getInstance().getViewProjectionMatrix(), getTransform().getModelViewProjectionMatrix());
		getShader().sendUniforms((Material) getParent().getComponents().get("Material"));
		getConfig().enable();
		vao.draw();
		getConfig().disable();
	}
	
	public void update()
	{
		ParticleShader.getInstance().execute();
		
		if (delete == 1 && (System.currentTimeMillis() - timeMillis) > countdown)
		{
			vao.shutdown();
			this.getParent().getComponents().remove("Renderer");
		}
		else
		{
			ParticleShader.getInstance().sendUniforms(this.delete);
			vao.updateParticles();
		}
	}
	

	public int getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public int getCountdown() {
		return countdown;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}
}
