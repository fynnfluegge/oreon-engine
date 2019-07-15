package org.oreon.gl.components.terrain;

import java.util.HashMap;
import java.util.Map;

import org.oreon.common.quadtree.QuadtreeCache;
import org.oreon.common.quadtree.QuadtreeConfig;
import org.oreon.common.quadtree.QuadtreeNode;
import org.oreon.common.terrain.TerrainQuadtree;
import org.oreon.core.gl.memory.GLPatchVBO;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.wrapper.parameter.DefaultRenderParams;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.util.MeshGenerator;

import lombok.Getter;

public class GLTerrain extends Node{
	
	@Getter
	private static QuadtreeConfig config;
	@Getter
	private GLTerrainQuadtree quadtree;
		
	public GLTerrain(GLShaderProgram shader, GLShaderProgram wireframe, GLShaderProgram shadow)
	{
		config = new GLTerrainConfig();
		
		GLPatchVBO buffer  = new GLPatchVBO();
		buffer.addData(MeshGenerator.TerrainChunkMesh(),16);
		
		GLRenderInfo renderInfo = new GLRenderInfo(shader,
				   new DefaultRenderParams(),
				   buffer);

		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(wireframe,
						    new DefaultRenderParams(),
						    buffer);
		
		HashMap<NodeComponentType, NodeComponent> components = new HashMap<NodeComponentType, NodeComponent>();
		
		components.put(NodeComponentType.MAIN_RENDERINFO, renderInfo);
		components.put(NodeComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
		components.put(NodeComponentType.CONFIGURATION, config);
		
		if (shadow != null){
			GLRenderInfo shadowRenderInfo = new GLRenderInfo(shadow,
				    new DefaultRenderParams(),
				    buffer);
			
			components.put(NodeComponentType.SHADOW_RENDERINFO, shadowRenderInfo);
		}
		
		quadtree = new GLTerrainQuadtree(components, config.getRootChunkCount(), config.getHorizontalScaling());
		
		addChild(quadtree);
		
		quadtree.start();
	}

	
	public class GLTerrainQuadtree extends TerrainQuadtree{

		public GLTerrainQuadtree(Map<NodeComponentType, NodeComponent> components, int rootChunkCount,
				float horizontalScaling) {
			super(components, rootChunkCount, horizontalScaling);
		}

		@Override
		public QuadtreeNode createChildChunk(Map<NodeComponentType, NodeComponent> components,
				QuadtreeCache quadtreeCache,
				Transform worldTransform, Vec2f location, int levelOfDetail, Vec2f index) {
			return new TerrainChunk(components, quadtreeCache, worldTransform, location, levelOfDetail, index);
		}
	}
	
	public class TerrainChunk extends QuadtreeNode{

		public TerrainChunk(Map<NodeComponentType, NodeComponent> components, QuadtreeCache quadtreeCache,
				Transform worldTransform, Vec2f location, int lod, Vec2f index) {
			
			super(components, quadtreeCache, worldTransform, location, lod, index);
		}

		@Override
		public QuadtreeNode createChildChunk(Map<NodeComponentType, NodeComponent> components, QuadtreeCache quadtreeCache,
				Transform worldTransform, Vec2f location, int levelOfDetail, Vec2f index) {
			return new TerrainChunk(components, quadtreeCache,
					worldTransform, location, levelOfDetail, index);
		}
	}
}
