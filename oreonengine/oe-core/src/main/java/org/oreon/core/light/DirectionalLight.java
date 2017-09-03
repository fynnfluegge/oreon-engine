package org.oreon.core.light;

import java.nio.FloatBuffer;

import org.oreon.core.buffers.UBO;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.shadow.PSSMCamera;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.BufferUtil;
import org.oreon.core.utils.Constants;

public class DirectionalLight extends Light{

	private static DirectionalLight instance = null;
	
	private Vec3f direction;
	private Vec3f ambient;
	private Matrix4f m_View;
	private Vec3f right;
	private Vec3f up;
	private PSSMCamera[] lightCameras;
	
	private UBO ubo_light;
	private UBO ubo_matrices;
	private FloatBuffer floatBufferLight;
	private FloatBuffer floatBufferMatrices;
	private final int lightBufferSize = Float.BYTES * 12;
	private final int matricesBufferSize = Float.BYTES * 96
										 + Float.BYTES * 24;

	
	public static DirectionalLight getInstance(){
		if(instance == null) 
	    {
	    	instance = new DirectionalLight();
	    }
	      return instance;
	}
	
	protected DirectionalLight(){
		this(new Vec3f(1,-1,1).normalize(),new Vec3f(0.12f,0.12f,0.12f),new Vec3f(1,0.95f,0.87f),1f);
	}
	
	private DirectionalLight(Vec3f direction, Vec3f ambient, Vec3f color, float intensity) {
		
		super(color, intensity);
		this.direction = direction;
		this.setAmbient(ambient);
		up = new Vec3f(1,2,1).normalize();
		
		if (direction.dot(up) != 0) 
			System.err.println("DirectionalLight vector up " + up + " and direction " +  direction + " not orthogonal");
		right = up.cross(getDirection()).normalize();
		m_View = new Matrix4f().View(getDirection(), up);	
		
		ubo_light = new UBO();
		ubo_light.setBinding_point_index(Constants.DirectionalLightUniformBlockBinding);
		ubo_light.bindBufferBase();
		ubo_light.allocate(lightBufferSize);
		floatBufferLight = BufferUtil.createFloatBuffer(lightBufferSize);
		floatBufferLight.put(BufferUtil.createFlippedBuffer(direction));
		floatBufferLight.put(intensity);
		floatBufferLight.put(BufferUtil.createFlippedBuffer(ambient));
		floatBufferLight.put(0);
		floatBufferLight.put(BufferUtil.createFlippedBuffer(color));
		floatBufferLight.put(0);
		ubo_light.updateData(floatBufferLight, lightBufferSize);
		
		ubo_matrices = new UBO();
		ubo_matrices.setBinding_point_index(Constants.LightMatricesUniformBlockBinding);
		ubo_matrices.bindBufferBase();
		ubo_matrices.allocate(matricesBufferSize);
		floatBufferMatrices = BufferUtil.createFloatBuffer(matricesBufferSize);
		
		lightCameras = new PSSMCamera[Constants.PSSM_SPLITS];
		
		for (int i = 0; i<Constants.PSSM_SPLITS*2; i += 2){
			lightCameras[i/2] = new PSSMCamera(Constants.PSSM_SPLIT_SHEME[i]*Constants.ZFAR,
											 Constants.PSSM_SPLIT_SHEME[i+1]*Constants.ZFAR);
			lightCameras[i/2].update(m_View, up, right);
			floatBufferMatrices.put(BufferUtil.createFlippedBuffer(lightCameras[i/2].getM_orthographicViewProjection()));
		}
		for (int i = 1; i<Constants.PSSM_SPLITS*2; i += 2){
			floatBufferMatrices.put(Constants.PSSM_SPLIT_SHEME[i]);
			floatBufferMatrices.put(0);
			floatBufferMatrices.put(0);
			floatBufferMatrices.put(0);
		}
		ubo_matrices.updateData(floatBufferMatrices, matricesBufferSize);
	}
	
	public void update(){
		
		if (CoreSystem.getInstance().getScenegraph().getCamera().isCameraRotated() || 
				CoreSystem.getInstance().getScenegraph().getCamera().isCameraMoved()){
			floatBufferMatrices.clear();
			for (PSSMCamera lightCamera : lightCameras){
				lightCamera.update(m_View, up, right);
				floatBufferMatrices.put(BufferUtil.createFlippedBuffer(lightCamera.getM_orthographicViewProjection()));
			}
			ubo_matrices.updateData(floatBufferMatrices, matricesBufferSize);
		}
	}
	
	public Vec3f getDirection() {
		return direction;
	}
	
	public void setDirection(Vec3f direction) {
		this.direction = direction;
		// TODO update up, right, m_View;
	}

	public Vec3f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vec3f ambient) {
		this.ambient = ambient;
	}

	public Matrix4f getM_View() {
		return m_View;
	}

	public void setM_View(Matrix4f m_view) {
		this.m_View = m_view;
	}

	public PSSMCamera[] getSplitLightCameras() {
		return lightCameras;
	}
}
