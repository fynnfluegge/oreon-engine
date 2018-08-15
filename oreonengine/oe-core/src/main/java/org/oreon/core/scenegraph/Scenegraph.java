package org.oreon.core.scenegraph;

import org.oreon.core.math.Transform;

public class Scenegraph extends Node{
	
	private Node rootObject;
	private Node terrain;
	private Node water;
	private Node transparentObjects;
	
	private boolean hasTerrain = false;
	
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
	
	public void renderWireframe(){
		
		// TODO
	}
	
	public void renderTransparentObejcts(){
		
		transparentObjects.render();
	}
	
	public void renderShadows(){
		
		rootObject.renderShadows();
		terrain.renderShadows();
		water.renderShadows();
		transparentObjects.shutdown();
	}
	
	public void record(RenderList renderList){

		rootObject.record(renderList);
		terrain.record(renderList);
		water.record(renderList);
	}
	
	public void recordTransparentObjects(RenderList renderList){

		transparentObjects.record(renderList);
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
		transparentObjects.shutdown();
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
		hasTerrain = true;
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

	public boolean hasTerrain() {
		return hasTerrain;
	}

	public Node getTransparentObjects() {
		return transparentObjects;
	}

	public void setTransparentObjects(Node transparentObjects) {
		this.transparentObjects = transparentObjects;
	}

}
