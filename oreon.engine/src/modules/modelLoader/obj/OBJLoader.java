package modules.modelLoader.obj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import engine.geometry.Mesh;
import engine.geometry.Vertex;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.scenegraph.components.Material;
import engine.textures.Texture2D;
import engine.utils.Util;

public class OBJLoader {

	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Vec3f> normals = new ArrayList<Vec3f>();
	private ArrayList<Vec2f> texCoords = new ArrayList<Vec2f>();

	private Deque<Object> objects = new ArrayDeque<Object>();
	private HashMap<String,Material> materials = new HashMap<String,Material>();
	private int currentSmoothingGroup;
	private String materialname = null;
	
	private Frontface frontface = Frontface.CCW;
	private boolean generateNormals = true;
	
	private enum Frontface {
		CW,CCW
	}
	
	public Model[] load(String path, String objFile, String mtlFile)
	{
		long time = System.currentTimeMillis();
		
			BufferedReader meshReader = null;
			BufferedReader mtlReader = null;
			
			// load .mtl
			if (mtlFile != null){
				try{
						mtlReader = new BufferedReader(new FileReader(path + "/" +  mtlFile));
						String line;
						String currentMtl = "";
						
						while((line = mtlReader.readLine()) != null){
							
							String[] tokens = line.split(" ");
							tokens = Util.removeEmptyStrings(tokens);
							
							if(tokens.length == 0)
								continue;
							if(tokens[0].equals("newmtl")){
								Material material = new Material();
								material.setName(tokens[1]);
								materials.put(tokens[1], material);
								currentMtl = tokens[1];
							}
							if(tokens[0].equals("Kd")){
								if (tokens.length > 1){
									Vec3f color = new Vec3f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]), Float.valueOf(tokens[3]));
									materials.get(currentMtl).setColor(color);
								}
							}
							if(tokens[0].equals("map_Kd")){
								if (tokens.length > 1){
									materials.get(currentMtl).setDiffusemap(new Texture2D(path + "/" + tokens[1]));
									materials.get(currentMtl).getDiffusemap().bind();
									materials.get(currentMtl).getDiffusemap().trilinearFilter();
								}
							}
							if(tokens[0].equals("map_Ks")){
								if (tokens.length > 1){
									materials.get(currentMtl).setSpecularmap(new Texture2D(path + "/" + tokens[1]));
									materials.get(currentMtl).getSpecularmap().bind();
									materials.get(currentMtl).getSpecularmap().trilinearFilter();
								}
							}
							if(tokens[0].equals("map_bump")){
								if (tokens.length > 1){
									materials.get(currentMtl).setNormalmap(new Texture2D(path + "/" + tokens[1]));
									materials.get(currentMtl).getNormalmap().bind();
									materials.get(currentMtl).getNormalmap().trilinearFilter();
								}
							}
							if(tokens[0].equals("illum")){
								if (tokens.length > 1)
									materials.get(currentMtl).setEmission(Float.valueOf(tokens[1]));
							}
							if(tokens[0].equals("Ns")){
								if (tokens.length > 1)
									materials.get(currentMtl).setShininess(Float.valueOf(tokens[1]));
							}
						}
						mtlReader.close();
					}
				catch(Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
				
			// load .obj
			try{
				meshReader = new BufferedReader(new FileReader(path + "/" + objFile));
				String line;
				while((line = meshReader.readLine()) != null)
				{
					String[] tokens = line.split(" ");
					tokens = Util.removeEmptyStrings(tokens);
					if(tokens.length == 0 || tokens[0].equals("#"))
						continue;
					
					if(tokens[0].equals("v"))
					{
						vertices.add(new Vertex(new Vec3f(Float.valueOf(tokens[1]),
														  Float.valueOf(tokens[2]),
														  Float.valueOf(tokens[3]))));
					}
					if(tokens[0].equals("vn"))
					{
						normals.add(new Vec3f(Float.valueOf(tokens[1]),
											  Float.valueOf(tokens[2]),
											  Float.valueOf(tokens[3])));
					}
					if(tokens[0].equals("vt"))
					{
						texCoords.add(new Vec2f(Float.valueOf(tokens[1]), Float.valueOf(tokens[2])));
					}
					if(tokens[0].equals("o"))
					{
						Object object = new Object();
						object.setName(tokens[1]);
						objects.add(new Object());
					}
					if(tokens[0].equals("g"))
					{
						PolygonGroup polygonGroup = new PolygonGroup();	
						if (tokens.length > 1)
							polygonGroup.setName(tokens[1]);
						if (objects.isEmpty()) objects.add(new Object());
						objects.peekLast().getPolygonGroups().add(polygonGroup);
					}
					if(tokens[0].equals("usemtl"))
					{
						Polygon polygon = new Polygon();
						materialname = tokens[1];
						polygon.setMaterial(tokens[1]);
						if(objects.peekLast().getPolygonGroups().isEmpty())
							objects.peekLast().getPolygonGroups().add(new PolygonGroup());
						objects.peekLast().getPolygonGroups().peekLast().getPolygons().add(polygon);
					}
					if(tokens[0].equals("s"))
					{					
						if(tokens[1].equals("off") || tokens[1].equals("0")){
							currentSmoothingGroup = 0;
							if (!objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().containsKey(0)){
								objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().put(currentSmoothingGroup, new SmoothingGroup());
							}
						}
						else{
							currentSmoothingGroup = Integer.valueOf(tokens[1]);
							if (!objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().containsKey(currentSmoothingGroup)){
								objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().put(currentSmoothingGroup, new SmoothingGroup());
							}
						}
					}
					if(tokens[0].equals("f"))
					{
						if(objects.peekLast().getPolygonGroups().isEmpty())
							objects.peekLast().getPolygonGroups().add(new PolygonGroup());
						
						if(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().isEmpty()){
							currentSmoothingGroup = 1;
							objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().put(currentSmoothingGroup, new SmoothingGroup());
						}
						
						if(objects.peekLast().getPolygonGroups().peekLast().getPolygons().isEmpty())
							objects.peekLast().getPolygonGroups().peekLast().getPolygons().add(new Polygon());
						
						if (tokens.length == 4)
							parseTriangleFace(tokens);
						if (tokens.length == 5)
							parseQuadFace(tokens);
					}
				}
				meshReader.close();
					
				if (normals.isEmpty() && generateNormals){
					for (Object object : objects){
						for (PolygonGroup polygonGroup : object.getPolygonGroups()){
							for (Integer key : polygonGroup.getSmoothingGroups().keySet()){
								if (frontface == Frontface.CW)
									Util.generateNormalsCW(polygonGroup.getSmoothingGroups().get(key));
								else
									Util.generateNormalsCCW(polygonGroup.getSmoothingGroups().get(key));
							}
						}
					}
				}					
					
				ArrayList<Model> models = new ArrayList<Model>();
				
				for(Object object : objects){
					for (PolygonGroup polygonGroup : object.getPolygonGroups()){
						for (Polygon polygon : polygonGroup.getPolygons()){
							
							generatePolygon(polygonGroup.getSmoothingGroups(), polygon);
							Model model = convert(polygon);
					
							if(polygon.getMaterial() != null){
								model.setMaterial(materials.get(polygon.getMaterial()));
							}
							else
								System.out.println("no material in polygon of: " + 
													object.getName() + " " +  polygonGroup.getName());
						
							models.add(model);
						}
					}
				}
			
				System.out.println("obj loading time : " + (System.currentTimeMillis() - time) + "ms");
				
				Model[] modelArray = new Model[models.size()];
				
				return models.toArray(modelArray);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		
		return null;
	}
	
	public void parseTriangleFace(String[] tokens)
	{
		// vertex//normal
		if (tokens[1].contains("//")){
			
			int[] vertexIndices = {Integer.parseInt(tokens[1].split("//")[0]) - 1,
					      		   Integer.parseInt(tokens[2].split("//")[0]) - 1,
					      		   Integer.parseInt(tokens[3].split("//")[0]) - 1};
			int[] normalIndices = {Integer.parseInt(tokens[1].split("//")[1]) - 1,
								   Integer.parseInt(tokens[2].split("//")[1]) - 1,
								   Integer.parseInt(tokens[3].split("//")[1]) - 1};
			
			Vertex v0 = vertices.get(vertexIndices[0]);
			Vertex v1 = vertices.get(vertexIndices[1]);
			Vertex v2 = vertices.get(vertexIndices[2]);
			v0.setNormal(normals.get(normalIndices[0]));
			v1.setNormal(normals.get(normalIndices[1]));
			v2.setNormal(normals.get(normalIndices[2]));
			
			addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2);
		}
		
		else if (tokens[1].contains("/")){	
			
			// vertex/textureCoord/normal
			if (tokens[1].split("/").length == 3){

				int[] vertexIndices = {Integer.parseInt(tokens[1].split("/")[0]) - 1,
						      		   Integer.parseInt(tokens[2].split("/")[0]) - 1,
						      		   Integer.parseInt(tokens[3].split("/")[0]) - 1};
				int[] texCoordIndices =  {Integer.parseInt(tokens[1].split("/")[1]) - 1,
						              	  Integer.parseInt(tokens[2].split("/")[1]) - 1,
						              	  Integer.parseInt(tokens[3].split("/")[1]) - 1};
				int[] normalIndices = {Integer.parseInt(tokens[1].split("/")[2]) - 1,
									   Integer.parseInt(tokens[2].split("/")[2]) - 1,
									   Integer.parseInt(tokens[3].split("/")[2]) - 1};
				
				Vertex v0 = vertices.get(vertexIndices[0]);
				Vertex v1 = vertices.get(vertexIndices[1]);
				Vertex v2 = vertices.get(vertexIndices[2]);
				v0.setNormal(normals.get(normalIndices[0]));
				v1.setNormal(normals.get(normalIndices[1]));
				v2.setNormal(normals.get(normalIndices[2]));
				v0.setTextureCoord(texCoords.get(texCoordIndices[0]));
				v1.setTextureCoord(texCoords.get(texCoordIndices[1]));
				v2.setTextureCoord(texCoords.get(texCoordIndices[2]));
				
				addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2);
			}
			
			// vertex/textureCoord
			else{

				int[] vertexIndices = {Integer.parseInt(tokens[1].split("/")[0]) - 1,
							  	 	   Integer.parseInt(tokens[2].split("/")[0]) - 1,
							  	 	   Integer.parseInt(tokens[3].split("/")[0]) - 1};
				int[] texCoordIndices =  {Integer.parseInt(tokens[1].split("/")[1]) - 1,
										  Integer.parseInt(tokens[2].split("/")[1]) - 1,
										  Integer.parseInt(tokens[3].split("/")[1]) - 1};
				
				Vertex v0 = vertices.get(vertexIndices[0]);
				Vertex v1 = vertices.get(vertexIndices[1]);
				Vertex v2 = vertices.get(vertexIndices[2]);
				v0.setTextureCoord(texCoords.get(texCoordIndices[0]));
				v1.setTextureCoord(texCoords.get(texCoordIndices[1]));
				v2.setTextureCoord(texCoords.get(texCoordIndices[2]));
				
				addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2);
			}		
		}
	
		// vertex
		else{
			
			int[] vertexIndices = {Integer.parseInt(tokens[1]) - 1,
					      Integer.parseInt(tokens[2]) - 1,
					      Integer.parseInt(tokens[3]) - 1};
			
			Vertex v0 = vertices.get(vertexIndices[0]);
			Vertex v1 = vertices.get(vertexIndices[1]);
			Vertex v2 = vertices.get(vertexIndices[2]);
			
			addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2);
		}
	}
	
	public void parseQuadFace(String[] tokens)
	{
		// vertex//normal
		if (tokens[1].contains("//")){
			
			int[] vertexIndices = {Integer.parseInt(tokens[1].split("//")[0]) - 1,
						  		   Integer.parseInt(tokens[2].split("//")[0]) - 1,
						  		   Integer.parseInt(tokens[3].split("//")[0]) - 1,
						  		   Integer.parseInt(tokens[4].split("//")[0]) - 1};
			int[] normalIndices = {Integer.parseInt(tokens[1].split("//")[1]) - 1,
								   Integer.parseInt(tokens[2].split("//")[1]) - 1,
								   Integer.parseInt(tokens[3].split("//")[1]) - 1,
								   Integer.parseInt(tokens[4].split("//")[1]) - 1};
			
			Vertex v0 = vertices.get(vertexIndices[0]);
			Vertex v1 = vertices.get(vertexIndices[1]);
			Vertex v2 = vertices.get(vertexIndices[2]);
			Vertex v3 = vertices.get(vertexIndices[3]);
			v0.setNormal(normals.get(normalIndices[0]));
			v1.setNormal(normals.get(normalIndices[1]));
			v2.setNormal(normals.get(normalIndices[2]));
			v3.setNormal(normals.get(normalIndices[3]));
			
			addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2,v3);
		}
		
		else if (tokens[1].contains("/")){	
			
			// vertex/textureCoord/normal
			if (tokens[1].split("/").length == 3){

				int[] vertexIndices = {Integer.parseInt(tokens[1].split("/")[0]) - 1,
									   Integer.parseInt(tokens[2].split("/")[0]) - 1,
									   Integer.parseInt(tokens[3].split("/")[0]) - 1,
									   Integer.parseInt(tokens[4].split("/")[0]) - 1};
				int[] texCoordIndices =  {Integer.parseInt(tokens[1].split("/")[1]) - 1,
										  Integer.parseInt(tokens[2].split("/")[1]) - 1,
										  Integer.parseInt(tokens[3].split("/")[1]) - 1,
										  Integer.parseInt(tokens[4].split("/")[1]) - 1};
				int[] normalIndices = {Integer.parseInt(tokens[1].split("/")[2]) - 1,
						               Integer.parseInt(tokens[2].split("/")[2]) - 1,
						               Integer.parseInt(tokens[3].split("/")[2]) - 1,
						               Integer.parseInt(tokens[4].split("/")[2]) - 1};
				
				Vertex v0 = vertices.get(vertexIndices[0]);
				Vertex v1 = vertices.get(vertexIndices[1]);
				Vertex v2 = vertices.get(vertexIndices[2]);
				Vertex v3 = vertices.get(vertexIndices[3]);
				v0.setNormal(normals.get(normalIndices[0]));
				v1.setNormal(normals.get(normalIndices[1]));
				v2.setNormal(normals.get(normalIndices[2]));
				v3.setNormal(normals.get(normalIndices[3]));
				v0.setTextureCoord(texCoords.get(texCoordIndices[0]));
				v1.setTextureCoord(texCoords.get(texCoordIndices[1]));
				v2.setTextureCoord(texCoords.get(texCoordIndices[2]));
				v3.setTextureCoord(texCoords.get(texCoordIndices[3]));
				
				addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2,v3);
			}
			
			// vertex/textureCoord
			else{

				int[] vertexIndices = {Integer.parseInt(tokens[1].split("/")[0]) - 1,
						      		   Integer.parseInt(tokens[2].split("/")[0]) - 1,
						      		   Integer.parseInt(tokens[3].split("/")[0]) - 1,
						      		   Integer.parseInt(tokens[4].split("/")[0]) - 1};
				int[] texCoordIndices =  {Integer.parseInt(tokens[1].split("/")[1]) - 1,
										  Integer.parseInt(tokens[2].split("/")[1]) - 1,
										  Integer.parseInt(tokens[3].split("/")[1]) - 1,
										  Integer.parseInt(tokens[4].split("/")[1]) - 1};
				
				Vertex v0 = vertices.get(vertexIndices[0]);
				Vertex v1 = vertices.get(vertexIndices[1]);
				Vertex v2 = vertices.get(vertexIndices[2]);
				Vertex v3 = vertices.get(vertexIndices[3]);
				v0.setTextureCoord(texCoords.get(texCoordIndices[0]));
				v1.setTextureCoord(texCoords.get(texCoordIndices[1]));
				v2.setTextureCoord(texCoords.get(texCoordIndices[2]));
				v3.setTextureCoord(texCoords.get(texCoordIndices[3]));
				
				addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2,v3);
			}		
		}
	
		// vertex
		else{
			
			int[] vertexIndices = {Integer.parseInt(tokens[1]) - 1,
					      		   Integer.parseInt(tokens[2]) - 1,
					      		   Integer.parseInt(tokens[3]) - 1,
					      		   Integer.parseInt(tokens[4]) - 1};
			
			Vertex v0 = vertices.get(vertexIndices[0]);
			Vertex v1 = vertices.get(vertexIndices[1]);
			Vertex v2 = vertices.get(vertexIndices[2]);
			Vertex v3 = vertices.get(vertexIndices[3]);
			
			addToSmoothingGroup(objects.peekLast().getPolygonGroups().peekLast().getSmoothingGroups().get(currentSmoothingGroup),v0,v1,v2,v3);
		}
	}
	
	public void addToSmoothingGroup(SmoothingGroup smoothingGroup, Vertex v0, Vertex v1, Vertex v2){
		
		if (!smoothingGroup.getVertices().contains(v0)) 
			smoothingGroup.getVertices().add(v0);
		if (!smoothingGroup.getVertices().contains(v1)) 
			smoothingGroup.getVertices().add(v1);
		if (!smoothingGroup.getVertices().contains(v2)) 
			smoothingGroup.getVertices().add(v2);
		
		int index0 = smoothingGroup.getVertices().indexOf(v0);
		int index1 = smoothingGroup.getVertices().indexOf(v1);
		int index2 = smoothingGroup.getVertices().indexOf(v2);
		
		Face face = new Face();
		face.getIndices()[0] = index0;
		face.getIndices()[1] = index1;
		face.getIndices()[2] = index2;
		face.setMaterial(materialname);
		
		smoothingGroup.getFaces().add(face);
	}
	
	public void addToSmoothingGroup(SmoothingGroup smoothingGroup, Vertex v0, Vertex v1, Vertex v2, Vertex v3){
		
		if (!smoothingGroup.getVertices().contains(v0)) 
			smoothingGroup.getVertices().add(v0);
		if (!smoothingGroup.getVertices().contains(v1)) 
			smoothingGroup.getVertices().add(v1);
		if (!smoothingGroup.getVertices().contains(v2)) 
			smoothingGroup.getVertices().add(v2);
		if (!smoothingGroup.getVertices().contains(v3)) 
			smoothingGroup.getVertices().add(v3);
		
		int index0 = smoothingGroup.getVertices().indexOf(v0);
		int index1 = smoothingGroup.getVertices().indexOf(v1);
		int index2 = smoothingGroup.getVertices().indexOf(v2);
		int index3 = smoothingGroup.getVertices().indexOf(v3);
		
		Face face0 = new Face();
		face0.getIndices()[0] = index0;
		face0.getIndices()[1] = index1;
		face0.getIndices()[2] = index2;
		face0.setMaterial(materialname);
		
		Face face1 = new Face();
		face1.getIndices()[0] = index0;
		face1.getIndices()[1] = index2;
		face1.getIndices()[2] = index3;
		face1.setMaterial(materialname);
		
		smoothingGroup.getFaces().add(face0);
		smoothingGroup.getFaces().add(face1);
	}
	
	public void generatePolygon(HashMap<Integer, SmoothingGroup> smoothingGroups, Polygon polygon){
		
		for (Integer key : smoothingGroups.keySet()) {
			for(Face face : smoothingGroups.get(key).getFaces()){
				if (face.getMaterial() == polygon.getMaterial()){
					if (!polygon.getVertices().contains(smoothingGroups.get(key).getVertices().get(face.getIndices()[0])))
						polygon.getVertices().add(smoothingGroups.get(key).getVertices().get(face.getIndices()[0]));
					if (!polygon.getVertices().contains(smoothingGroups.get(key).getVertices().get(face.getIndices()[1])))
						polygon.getVertices().add(smoothingGroups.get(key).getVertices().get(face.getIndices()[1]));
					if (!polygon.getVertices().contains(smoothingGroups.get(key).getVertices().get(face.getIndices()[2])))
						polygon.getVertices().add(smoothingGroups.get(key).getVertices().get(face.getIndices()[2]));
					
					polygon.getIndices().add(polygon.getVertices().indexOf(smoothingGroups.get(key).getVertices().get(face.getIndices()[0])));
					polygon.getIndices().add(polygon.getVertices().indexOf(smoothingGroups.get(key).getVertices().get(face.getIndices()[1])));
					polygon.getIndices().add(polygon.getVertices().indexOf(smoothingGroups.get(key).getVertices().get(face.getIndices()[2])));
				}
			}
		}
	}
	
	public Model convert(Polygon polygon){
		
		Vertex[] vertexData = new Vertex[polygon.getVertices().size()];
		polygon.getVertices().toArray(vertexData);
	
		Integer[] objectArray = new Integer[polygon.getIndices().size()];
		polygon.getIndices().toArray(objectArray);
		int[] indexData = Util.toIntArray(objectArray);
	
		Model model = new Model(new Mesh(vertexData, indexData));
		model.setMaterial(materials.get(polygon.getMaterial()));
		
		return model;
	}
}
