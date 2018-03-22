package org.oreon.core.scenegraph;

import org.oreon.core.math.Transform;
import org.oreon.core.platform.Camera;

public class Scenegraph extends Node{
	
	private Camera camera;
	private Node rootObject;
	private Node terrain;
	private Node water;
	private Node transparentObjects;
	
	private boolean renderTerrain = false;
	
	public Scenegraph(){
		
		setWorldTransform(new Transform());
		rootObject = new Node();
		terrain = new Node();
		water = new Node();
		transparentObjects = new Node();
		rootObject.setParent(this);
		terrain.setParent(this);
		water.setParent(this);
		transparentObjects.setParent(this);
	}
	
	public void render(){
		
		rootObject.render();
		terrain.render();
		water.render();
	}
	
	public void renderTransparentObejcts(){
		
		transparentObjects.render();
	}
	
	public void renderShadows(){
		
		rootObject.renderShadows();
		terrain.renderShadows();
		water.renderShadows();
	}
	
	public void update(){

		rootObject.update();
		terrain.update();
		water.update();
		transparentObjects.update();
	}
	
	public void input(){
		rootObject.input();
		terrain.input();
		water.input();
	}
	
	public void shutdown()
	{
		rootObject.shutdown();
		terrain.shutdown();
		water.shutdown();
	}

	public Node getRoot() {
		return rootObject;
	}
	
	public void addObject(Node object){
		rootObject.addChild(object);
	}
	
	public void addTransparentObject(Node object){
		transparentObjects.addChild(object);
	}

	public void setTerrain(Node terrain) {
		terrain.setParent(this);
		renderTerrain = true;
		this.terrain = terrain;
	}
	
	public Node getTerrain() {
		return terrain;
	}


	public Node getWater() {
		return water;
	}

	public void setWater(Node water) {
		water.setParent(this);
		this.water = water;
	}

	public boolean isRenderTerrain() {
		return renderTerrain;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Node getTransparentObjects() {
		return transparentObjects;
	}

	public void setTransparentObjects(Node transparentObjects) {
		this.transparentObjects = transparentObjects;
	}

}
