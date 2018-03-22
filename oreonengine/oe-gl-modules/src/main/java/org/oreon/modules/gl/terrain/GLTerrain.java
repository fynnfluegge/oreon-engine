package org.oreon.modules.gl.terrain;

import java.util.HashMap;

import org.oreon.core.gl.buffers.GLPatchVBO;
import org.oreon.core.gl.parameter.Default;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.scenegraph.Component;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.util.MeshGenerator;

import lombok.Getter;

public class GLTerrain extends Node{
	
	@Getter
	private TerrainQuadtree quadtree;
		
	public GLTerrain(GLShader shader, GLShader wireframe, GLShader shadow)
	{
		GLTerrainContext.initialize();
		
		GLPatchVBO buffer  = new GLPatchVBO();
		buffer.addData(MeshGenerator.TerrainChunkMesh(),16);
		
		GLRenderInfo renderInfo = new GLRenderInfo(shader,
				   new Default(),
				   buffer);

		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(wireframe,
						    new Default(),
						    buffer);
		HashMap<ComponentType, Component> components = new HashMap<ComponentType, Component>();
		
		components.put(ComponentType.MAIN_RENDERINFO, renderInfo);
		components.put(ComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
		
		quadtree = new TerrainQuadtree(components);
		
		addChild(quadtree);
		
		quadtree.start();
	}

}
