package org.oreon.common.terrain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.oreon.core.image.Image;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerrainConfiguration extends NodeComponent{

	private float verticalScaling;
	private float horizontalScaling;
	private int rootChunkCount;
	private int waterReflectionShift;
	private float uvScaling;
	private int tessellationFactor;
	private float tessellationSlope;
	private float tessellationShift;
	private int highDetailRange;
	private Image heightmap;
	private Image normalmap;
	private Image ambientmap;
	private Image splatmap;
	private FloatBuffer heightmapDataBuffer;
	private List<Material<Image>> materials = new ArrayList<>();
	private int fractalMapResolution;
	private int lodCount;
	private int[] lod_range = new int[8];
	private int[] lod_morphing_area = new int[8];
	
//	private List<FractalMap> fractals = new ArrayList<>();
	
	public TerrainConfiguration() {
		
		Properties properties = new Properties();
		try {
			InputStream stream = TerrainConfiguration.class.getClassLoader()
					.getResourceAsStream("terrain-config.properties");
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		verticalScaling = Float.valueOf(properties.getProperty("verticalScaling"));
		horizontalScaling = Float.valueOf(properties.getProperty("horizontalScaling"));
		rootChunkCount = Integer.valueOf(properties.getProperty("rootChunkCount"));
		tessellationFactor = Integer.valueOf(properties.getProperty("tessellationFactor"));
		tessellationSlope = Float.valueOf(properties.getProperty("tessellationSlope"));
		tessellationShift = Float.valueOf(properties.getProperty("tessellationShift"));
		uvScaling = Float.valueOf(properties.getProperty("uvScaling"));
		highDetailRange = Integer.valueOf(properties.getProperty("highDetailRange"));
		lodCount = Integer.valueOf(properties.getProperty("lod.count"));
		
		for (int i=0; i<lodCount; i++){
			
			if (Integer.valueOf(properties.getProperty("lodRanges.lod" + i)) == 0){
				lod_range[i] = 0;
				lod_morphing_area[i] = 0;
			}
			else {
				setLodRange(i, Integer.valueOf(properties.getProperty("lodRanges.lod" + i)));
			}
		}
	}

	public void setLodRange(int index, int lod_range) {
		this.lod_range[index] = lod_range;
		lod_morphing_area[index] = lod_range - getMorphingArea4Lod(index+1);
	}
	
	private int getMorphingArea4Lod(int lod){
		return (int) ((horizontalScaling/rootChunkCount) / (Math.pow(2, lod)));
	}
	
}
