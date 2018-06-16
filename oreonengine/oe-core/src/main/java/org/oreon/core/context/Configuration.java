package org.oreon.core.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	
	// static render settings
	private float sightRange;
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
	
	private final Properties properties;
	
	private Configuration(){
		
		properties = new Properties();
		try {
			InputStream stream = Configuration.class.getClassLoader().getResourceAsStream("oe-config.properties");
			properties.load(stream);
			stream.close();
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
		
		renderWireframe = false;
		renderUnderwater = false;
		renderReflection = false;
		renderRefraction = false;
		clipplane = Constants.ZEROPLANE;
		
	}
}
