package engine.geometry;

public class ParticleSystem {

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
