package org.oreon.modules.gl.water;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterConfiguration {

	private float motion;
	private float displacementScale;
	private float choppiness;
	private int tessellationFactor;
	private float tessellationShift;
	private float tessellationSlope;
	private int largeDetailRange;
	private int texDetail;
	private float shininess;
	private float emission;
	private float kReflection;
	private float kRefraction;
	private float distortion;
	private float waveMotion;
	private float normalStrength;
	private float delta_T;
	private boolean choppy;
	
	public void loadFile(String file)
	{
		Properties properties = new Properties();
		try {
			InputStream stream = WaterConfiguration.class.getClassLoader().getResourceAsStream(file);
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		displacementScale = Float.valueOf(properties.getProperty("displacementScale"));
		choppiness = Float.valueOf(properties.getProperty("choppiness"));
		distortion = Float.valueOf(properties.getProperty("distortion"));
		waveMotion = Float.valueOf(properties.getProperty("wavemotion"));
		texDetail = Integer.valueOf(properties.getProperty("texDetail"));
		tessellationFactor = Integer.valueOf(properties.getProperty("tessellationFactor"));
		tessellationSlope = Float.valueOf(properties.getProperty("tessellationSlope"));
		tessellationShift = Float.valueOf(properties.getProperty("tessellationShift"));
		shininess = Float.valueOf(properties.getProperty("shininess"));
		emission = Float.valueOf(properties.getProperty("emission"));
		kReflection = Float.valueOf(properties.getProperty("kReflection"));
		kRefraction = Float.valueOf(properties.getProperty("kRefraction"));
		normalStrength = Float.valueOf(properties.getProperty("normalStrength"));
		largeDetailRange = Integer.valueOf(properties.getProperty("detailRange"));
		delta_T = Float.valueOf(properties.getProperty("delta_T"));
		choppy = Boolean.valueOf(properties.getProperty("choppy"));
	}
}
