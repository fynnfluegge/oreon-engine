package org.oreon.core.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.oreon.core.math.Vec3f;
import org.oreon.core.math.Vec4f;
import org.oreon.core.util.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configuration {
	
	// screen settings
	private int x_ScreenResolution;
	private int y_ScreenResolution;

	// window settings
	private String displayTitle;
	private int windowWidth;
	private int windowHeight;
	
	// anitaliasing
	private final int multisamples;
	private boolean fxaaEnabled;
	
	// post processing effects
	private boolean ssaoEnabled;
	private boolean bloomEnabled;
	private boolean depthOfFieldBlurEnabled;
	private boolean motionBlurEnabled;
	private boolean lightScatteringEnabled;
	private boolean lensFlareEnabled;
	
	// dynamic render settings
	private boolean renderWireframe;
	private boolean renderUnderwater;
	private boolean renderReflection;
	private boolean renderRefraction;
	private Vec4f clipplane;
	private float sightRange;
	
	// Vulkan Validation
	private boolean vkValidation;
	
	// Atmosphere parameter
	private float sunRadius;
	private Vec3f sunPosition;
	private Vec3f sunColor;
	private float sunIntensity;
	private float ambient;
	private boolean AtmosphericScatteringApproximation;
	
	public Configuration(){
		
		Properties properties = new Properties();
		try {
			InputStream vInputStream = Configuration.class.getClassLoader().getResourceAsStream("oe-config.properties");
			properties.load(vInputStream);
			vInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		windowWidth = Integer.valueOf(properties.getProperty("display.width"));
		windowHeight = Integer.valueOf(properties.getProperty("display.height"));
		displayTitle = properties.getProperty("display.title");
		x_ScreenResolution = Integer.valueOf(properties.getProperty("screen.resolution.x"));
		y_ScreenResolution = Integer.valueOf(properties.getProperty("screen.resolution.y"));
		multisamples = Integer.valueOf(properties.getProperty("multisamples"));
		fxaaEnabled = Integer.valueOf(properties.getProperty("fxaa.enable")) == 1 ? true : false;
		sightRange = Float.valueOf(properties.getProperty("sightRange"));
		
		bloomEnabled = Integer.valueOf(properties.getProperty("bloom.enable")) == 1 ? true : false;
		ssaoEnabled = Integer.valueOf(properties.getProperty("ssao.enable")) == 1 ? true : false;
		motionBlurEnabled = Integer.valueOf(properties.getProperty("motionBlur.enable")) == 1 ? true : false;
		lightScatteringEnabled = Integer.valueOf(properties.getProperty("lightScattering.enable")) == 1 ? true : false;
		depthOfFieldBlurEnabled = Integer.valueOf(properties.getProperty("depthOfFieldBlur.enable")) == 1 ? true : false;
		lensFlareEnabled = Integer.valueOf(properties.getProperty("lensFlare.enable")) == 1 ? true : false;
		
		if (properties.getProperty("validation.enable") != null){
			vkValidation = Integer.valueOf(properties.getProperty("validation.enable")) == 1 ? true : false;
		}
		
		renderWireframe = false;
		renderUnderwater = false;
		renderReflection = false;
		renderRefraction = false;
		clipplane = Constants.ZEROPLANE;
		
		
		try {
			InputStream vInputStream = Configuration.class.getClassLoader().getResourceAsStream("atmosphere-config.properties");
			if (vInputStream != null){
				properties.load(vInputStream);
				vInputStream.close();
				
				sunRadius = Float.valueOf(properties.getProperty("sun.radius"));
				sunPosition = new Vec3f(
						Float.valueOf(properties.getProperty("sun.position.x")),
						Float.valueOf(properties.getProperty("sun.position.y")),
						Float.valueOf(properties.getProperty("sun.position.z")));
				sunColor = new Vec3f(
						Float.valueOf(properties.getProperty("sun.color.r")),
						Float.valueOf(properties.getProperty("sun.color.g")),
						Float.valueOf(properties.getProperty("sun.color.b")));
				sunIntensity = Float.valueOf(properties.getProperty("sun.intensity"));
				ambient = Float.valueOf(properties.getProperty("ambient"));
				AtmosphericScatteringApproximation = Integer.valueOf(properties.getProperty("atmosphere.scattering.approximation")) == 1 ? true : false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
