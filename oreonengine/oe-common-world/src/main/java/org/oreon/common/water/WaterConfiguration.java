package org.oreon.common.water;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.oreon.core.math.Vec2f;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterConfiguration {

	private int N;
	private int L;
	private float amplitude;
	private Vec2f windDirection;
	private float windSpeed;
	private float alignment;
	private float capillarWavesSupression;
	private float motion;
	private float displacementScale;
	private float choppiness;
	private int tessellationFactor;
	private float tessellationShift;
	private float tessellationSlope;
	private int highDetailRange;
	private int uvScale;
	private float specular;
	private float emission;
	private float kReflection;
	private float kRefraction;
	private float distortion;
	private float waveMotion;
	private float normalStrength;
	private float t_delta;
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
		
		N = Integer.valueOf(properties.getProperty("fft.resolution"));
		L = Integer.valueOf(properties.getProperty("fft.L"));
		amplitude = Float.valueOf(properties.getProperty("fft.amplitude"));
		windDirection = new Vec2f(Float.valueOf(properties.getProperty("wind.x")),
				Float.valueOf(properties.getProperty("wind.y"))).normalize();
		windSpeed = Float.valueOf(properties.getProperty("wind.speed"));
		alignment = Float.valueOf(properties.getProperty("alignment"));
		capillarWavesSupression = Float.valueOf(properties.getProperty("fft.capillarwavesSuppression"));
		displacementScale = Float.valueOf(properties.getProperty("displacementScale"));
		choppiness = Float.valueOf(properties.getProperty("choppiness"));
		distortion = Float.valueOf(properties.getProperty("distortion"));
		waveMotion = Float.valueOf(properties.getProperty("wavemotion"));
		uvScale = Integer.valueOf(properties.getProperty("uvScale"));
		tessellationFactor = Integer.valueOf(properties.getProperty("tessellationFactor"));
		tessellationSlope = Float.valueOf(properties.getProperty("tessellationSlope"));
		tessellationShift = Float.valueOf(properties.getProperty("tessellationShift"));
		specular = Float.valueOf(properties.getProperty("specular.factor"));
		emission = Float.valueOf(properties.getProperty("emission"));
		kReflection = Float.valueOf(properties.getProperty("kReflection"));
		kRefraction = Float.valueOf(properties.getProperty("kRefraction"));
		normalStrength = Float.valueOf(properties.getProperty("normalStrength"));
		highDetailRange = Integer.valueOf(properties.getProperty("highDetailRange"));
		t_delta = Float.valueOf(properties.getProperty("t_delta"));
		choppy = Boolean.valueOf(properties.getProperty("choppy"));
	}
}
