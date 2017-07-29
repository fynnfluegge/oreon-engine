package modules.modelLoader.obj;

import java.util.ArrayList;
import java.util.HashMap;

public class PolygonGroup {

	private ArrayList<Polygon> polygons;
	private HashMap<Integer,SmoothingGroup> smoothingGroups = new HashMap<Integer,SmoothingGroup>();
	private String name = "";
	
	public PolygonGroup(){
		smoothingGroups = new HashMap<Integer,SmoothingGroup>();
		polygons = new ArrayList<Polygon>();
	}

	public ArrayList<Polygon> getPolygons() {
		return polygons;
	}

	public void setPolygons(ArrayList<Polygon> polygons) {
		this.polygons = polygons;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Integer,SmoothingGroup> getSmoothingGroups() {
		return smoothingGroups;
	}

	public void setSmoothingGroups(HashMap<Integer,SmoothingGroup> smoothingGroups) {
		this.smoothingGroups = smoothingGroups;
	}	
}
