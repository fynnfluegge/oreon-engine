package org.oreon.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Mesh;
import org.oreon.core.model.Vertex;
import org.oreon.core.util.Util;

public abstract class UITextPanel extends UIElement{
	
	private int numFonts;
	
	public UITextPanel(String text, int xPos, int yPos, int xScaling, int yScaling) {
		
		numFonts = text.length();
		panel = generatePanelVertices();
		uv = generateFontMapUvCoords(text);
		setOrthoTransform(new Transform());
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		getOrthoTransform().setTranslation(xPos, yPos, 0);
		getOrthoTransform().setScaling(xScaling, yScaling, 0);
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
	}

	public Mesh generatePanelVertices(){
		
		List<Vertex> vertexList = new ArrayList<Vertex>();
		List<Integer> indexList = new ArrayList<Integer>();
		
		for (int i=0; i<numFonts; i++){
			 
			Mesh mesh = UIPanelLoader.load("gui/basicPanel.gui");
			
			for (int j=0; j<mesh.getVertices().length; j++){
				vertexList.add(mesh.getVertices()[j]);
			}
			for (int k=0; k<mesh.getIndices().length; k++){
				indexList.add(mesh.getIndices()[k]);
			}
		}
		
		int offset = 0;
		// shift vertices x-dimesnion
		for (int i=0; i<vertexList.size(); i++){
			
			if (i > 0 && (i % 2 == 0)){
				offset++;
			}
			vertexList.get(i).getPosition().setX(
					vertexList.get(i).getPosition().getX()+offset);
		}
		
		Vertex[] vertices = (Vertex[]) vertexList.toArray();
		int[] indices = new int[indexList.size()];
		
		for (int j=0; j<indexList.size(); j++){
			indices[j] = indexList.get(j);
		}
		
		return new Mesh(vertices, indices);
	}
	
	public Vec2f[] generateFontMapUvCoords(String text){
		
		List<Vec2f> uv = new ArrayList<Vec2f>();
		
		for (int i=0; i<text.length(); i++){
			Vec2f[] fontMapUv = new Vec2f[4];
			fontMapUv = Util.texCoordsFromFontMap(text.charAt(i));
			uv.add(fontMapUv[0]);
			uv.add(fontMapUv[1]);
			uv.add(fontMapUv[2]);
			uv.add(fontMapUv[3]);
		}
		
		return (Vec2f[]) uv.toArray();
	}
}
