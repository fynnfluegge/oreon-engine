package org.oreon.core.util;

import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Vertex;
import org.oreon.core.util.modelLoader.obj.Face;
import org.oreon.core.util.modelLoader.obj.SmoothingGroup;

public class GLUtil {

	public static void generateNormalsCW(SmoothingGroup smoothingGroup)
	{
	    for (Face face : smoothingGroup.getFaces())
	    {
	    	Vec3f v0 = smoothingGroup.getVertices().get(face.getIndices()[0]).getPosition();
	    	Vec3f v1 = smoothingGroup.getVertices().get(face.getIndices()[1]).getPosition();
	    	Vec3f v2 = smoothingGroup.getVertices().get(face.getIndices()[2]).getPosition();
	        
	    	Vec3f normal = v1.sub(v0).cross(v2.sub(v0)).normalize();
	        
	    	smoothingGroup.getVertices().get(face.getIndices()[0]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[0]).getNormal().add(normal));
	    	smoothingGroup.getVertices().get(face.getIndices()[1]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[1]).getNormal().add(normal));
	    	smoothingGroup.getVertices().get(face.getIndices()[2]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[2]).getNormal().add(normal));
	    }

	    for (Vertex vertex : smoothingGroup.getVertices())
	    {	
	    	vertex.setNormal(vertex.getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCCW(SmoothingGroup smoothingGroup)
	{
		  for (Face face : smoothingGroup.getFaces())
		    {
		    	Vec3f v0 = smoothingGroup.getVertices().get(face.getIndices()[0]).getPosition();
		    	Vec3f v1 = smoothingGroup.getVertices().get(face.getIndices()[1]).getPosition();
		    	Vec3f v2 = smoothingGroup.getVertices().get(face.getIndices()[2]).getPosition();
		        
		    	Vec3f normal = v2.sub(v0).cross(v1.sub(v0)).normalize();
		        
		    	smoothingGroup.getVertices().get(face.getIndices()[0]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[0]).getNormal().add(normal));
		    	smoothingGroup.getVertices().get(face.getIndices()[1]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[1]).getNormal().add(normal));
		    	smoothingGroup.getVertices().get(face.getIndices()[2]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[2]).getNormal().add(normal));
		    }

		    for (Vertex vertex : smoothingGroup.getVertices())
		    {	
		    	vertex.setNormal(vertex.getNormal().normalize());
		    }     
	}
}
