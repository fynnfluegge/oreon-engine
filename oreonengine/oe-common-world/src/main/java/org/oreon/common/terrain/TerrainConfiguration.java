package org.oreon.common.terrain;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.oreon.common.quadtree.Quadtree;
import org.oreon.core.image.Image;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerrainConfiguration extends NodeComponent{

	private float scaleY;
	private float scaleXZ;
	private int bezier;
	private int waterReflectionShift;
	private float texDetail;
	private int tessellationFactor;
	private float tessellationSlope;
	private float tessellationShift;
	private int detailRange;
	private Image heightmap;
	private Image normalmap;
	private Image ambientmap;
	private Image splatmap;
	private FloatBuffer heightmapDataBuffer;
	private List<Material<Image>> materials = new ArrayList<>();
	private int fractalMapResolution;
//	private List<FractalMap> fractals = new ArrayList<>();
	
	private int[] lod_range = new int[8];
	private int[] lod_morphing_area = new int[8];
	
	private int updateMorphingArea(int lod){
		return (int) ((scaleXZ/Quadtree.getRootPatches()) / (Math.pow(2, lod)));
	}

	public void setLodRange(int index, int lod_range) {
		this.lod_range[index] = lod_range;
		lod_morphing_area[index] = lod_range - updateMorphingArea(index+1);
	}
	
}
