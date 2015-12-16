package engine.gameObject.components;

import engine.models.data.Particle;

public class ParticleSystem extends Component{

private Particle[] particles;
	
	public ParticleSystem(int n)
	{
		particles = new Particle[n];
	}

	public Particle[] getParticles() {
		return particles;
	}

	public void setParticles(Particle[] particles) {
		this.particles = particles;
	}
}
