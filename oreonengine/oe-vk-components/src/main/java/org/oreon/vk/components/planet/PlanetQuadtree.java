package org.oreon.vk.components.planet;

import java.util.Map;

import org.oreon.common.planet.SphericalCubeQuadtree;
import org.oreon.common.quadtree.QuadtreeCache;
import org.oreon.common.quadtree.QuadtreeChunk;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.vk.components.terrain.TerrainChunk;

public class PlanetQuadtree extends SphericalCubeQuadtree{

	public PlanetQuadtree(Map<NodeComponentType, NodeComponent> components,
			int rootChunkCount, float horizontalScaling) {
		
		super(components, rootChunkCount, horizontalScaling);
	}

	@Override
	public QuadtreeChunk createChildChunk(Map<NodeComponentType, NodeComponent> components, QuadtreeCache quadtreeCache,
			Transform worldTransform, Vec2f location, int levelOfDetail, Vec2f index) {

		return new TerrainChunk(components, quadtreeCache, worldTransform, location, levelOfDetail, index);
	}

}
