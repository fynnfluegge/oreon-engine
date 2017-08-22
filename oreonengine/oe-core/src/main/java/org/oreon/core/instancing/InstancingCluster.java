package org.oreon.core.instancing;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.buffers.UBO;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scene.Node;
import org.oreon.core.utils.IntegerReference;

public abstract class InstancingCluster extends Node{
	
	private List<Matrix4f> worldMatrices = new ArrayList<Matrix4f>();
	private List<Matrix4f> modelMatrices = new ArrayList<Matrix4f>();
	
	private UBO modelMatricesBuffer;
	private UBO worldMatricesBuffer;
	
	private List<Integer> highPolyIndices = new ArrayList<Integer>();
	private List<Integer> lowPolyIndices = new ArrayList<Integer>();
	
	private IntegerReference highPolyInstances;
	private IntegerReference lowPolyInstances;
	
	private Vec3f center;
	
	public void updateUBOs(){};
	
	public void placeObject(){};
	
	
	public List<Integer> getHighPolyIndices(){
		return highPolyIndices;
	}
	
	public List<Integer> getLowPolyIndices(){
		return lowPolyIndices;
	}

	public void setHighPolyIndices(List<Integer> highPolyIndices) {
		this.highPolyIndices = highPolyIndices;
	}

	public void setLowPolyIndices(List<Integer> lowPolyIndices) {
		this.lowPolyIndices = lowPolyIndices;
	}

	public List<Matrix4f> getWorldMatrices() {
		return worldMatrices;
	}

	public void setWorldMatrices(List<Matrix4f> matrices) {
		this.worldMatrices = matrices;
	}

	public UBO getModelMatricesBuffer() {
		return modelMatricesBuffer;
	}

	public void setModelMatricesBuffer(UBO modelMatricesBuffer) {
		this.modelMatricesBuffer = modelMatricesBuffer;
	}

	public UBO getWorldMatricesBuffer() {
		return worldMatricesBuffer;
	}

	public void setWorldMatricesBuffer(UBO worldMatricesBuffer) {
		this.worldMatricesBuffer = worldMatricesBuffer;
	}

	public Vec3f getCenter() {
		return center;
	}

	public void setCenter(Vec3f center) {
		this.center = center;
	}

	public IntegerReference getHighPolyInstances() {
		return highPolyInstances;
	}

	public void setHighPolyInstances(IntegerReference instances) {
		this.highPolyInstances = instances;
	}

	public IntegerReference getLowPolyInstances() {
		return lowPolyInstances;
	}

	public void setLowPolyInstances(IntegerReference lowPolyInstances) {
		this.lowPolyInstances = lowPolyInstances;
	}

	public List<Matrix4f> getModelMatrices() {
		return modelMatrices;
	}

	public void setModelMatrices(List<Matrix4f> modelMatrices) {
		this.modelMatrices = modelMatrices;
	}
}
