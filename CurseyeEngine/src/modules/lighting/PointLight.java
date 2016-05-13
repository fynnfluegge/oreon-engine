package modules.lighting;

import engine.main.RenderingEngine;
import engine.math.Vec3f;

public class PointLight extends Light{

	private int isEnabled;
	private int isSpot;
	private Vec3f position;
	private float ConstantAttenuation;
	private float LinearAttenuation;
	private float QuadraticAttenuation;
	private Vec3f ConeDirection;
	private float SpotCosCutoff;
	private float SpotExponent;
	
	public PointLight(Vec3f position, Vec3f ambient, Vec3f color, float intensity) {
		super(ambient, color, intensity);
		this.position = position;
		getLocalTransform().setTranslation(new Vec3f(position.getX(), position.getY(), position.getZ()));
		RenderingEngine.addLight(this);
	}
	
	public void update()
	{
		setPosition(getLocalTransform().getTranslation().add(getTransform().getTranslation()));
	}

	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;
	}

	public int isEnabled() {
		return isEnabled;
	}

	public void setEnabled(int isEnabled) {
		this.isEnabled = isEnabled;
	}

	public int isSpot() {
		return isSpot;
	}

	public void setSpot(int isSpot) {
		this.isSpot = isSpot;
	}

	public float getConstantAttenuation() {
		return ConstantAttenuation;
	}

	public void setConstantAttenuation(float constantAttenuation) {
		ConstantAttenuation = constantAttenuation;
	}

	public float getLinearAttenuation() {
		return LinearAttenuation;
	}

	public void setLinearAttenuation(float linearAttenuation) {
		LinearAttenuation = linearAttenuation;
	}

	public float getQuadraticAttenuation() {
		return QuadraticAttenuation;
	}

	public void setQuadraticAttenuation(float quadraticAttenuation) {
		QuadraticAttenuation = quadraticAttenuation;
	}

	public Vec3f getConeDirection() {
		return ConeDirection;
	}

	public void setConeDirection(Vec3f coneDirection) {
		ConeDirection = coneDirection;
	}

	public float getSpotCosCutoff() {
		return SpotCosCutoff;
	}

	public void setSpotCosCutoff(float spotCosCutoff) {
		SpotCosCutoff = spotCosCutoff;
	}

	public float getSpotExponent() {
		return SpotExponent;
	}

	public void setSpotExponent(float spotExponent) {
		SpotExponent = spotExponent;
	}
}
