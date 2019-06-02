package org.oreon.examples.gl.oreonworlds.shaders;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;
import org.oreon.gl.components.terrain.TerrainConfiguration;
import org.oreon.gl.components.terrain.TerrainNode;

public class TerrainShader extends GLShaderProgram {

	private static TerrainShader instance = null;

	public static TerrainShader getInstance() {
		if (instance == null) {
			instance = new TerrainShader();
		}
		return instance;
	}

	protected TerrainShader() {
		super();

		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/terrain.vert"));
		addTessellationControlShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/terrain.tesc"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/terrain.tese"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/terrain.geom"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/terrain/terrain.frag"));
		compileShader();

		addUniform("localMatrix");
		addUniform("worldMatrix");
		addUniform("scaleXZ");
		addUniform("scaleY");
		addUniform("diamond_square");

		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("largeDetailRange");
		addUniform("index");
		addUniform("gap");
		addUniform("lod");
		addUniform("location");
		addUniform("texDetail");
		addUniform("reflectionOffset");
		addUniform("isRefraction");
		addUniform("isReflection");
		addUniform("isCameraUnderWater");

		addUniform("caustics");
		addUniform("dudvCaustics");
		addUniform("distortionCaustics");
		addUniform("underwaterBlurFactor");

		addUniform("heightmap");
		addUniform("normalmap");
		addUniform("splatmap");

		for (int i = 0; i < 8; i++) {
			addUniform("lod_morph_area[" + i + "]");
		}

		for (int i = 0; i < 3; i++) {
			addUniform("materials[" + i + "].diffusemap");
			addUniform("materials[" + i + "].normalmap");
			addUniform("materials[" + i + "].heightmap");
			addUniform("materials[" + i + "].heightScaling");
			addUniform("materials[" + i + "].uvScaling");
		}

		addUniform("clipplane");
		addUniform("sightRangeFactor");
		addUniform("fogColor");

		addUniformBlock("Camera");
		addUniformBlock("DirectionalLight");
	}

	@Override
	public void updateUniforms(Renderable object)
	{	
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
		bindUniformBlock("DirectionalLight", Constants.DirectionalLightUniformBlockBinding);
		
		setUniformf("sightRangeFactor", BaseContext.getConfig().getSightRange());
		setUniform("fogColor", BaseContext.getConfig().getFogColor());
		
		setUniform("clipplane", BaseContext.getConfig().getClipplane());
		setUniformi("isRefraction", BaseContext.getConfig().isRenderRefraction() ? 1 : 0);
		setUniformi("isReflection", BaseContext.getConfig().isRenderReflection() ? 1 : 0);
		setUniformi("isCameraUnderWater", BaseContext.getConfig().isRenderUnderwater() ? 1 : 0);		
		
		TerrainConfiguration terrConfig = object.getComponent(NodeComponentType.CONFIGURATION);
		
		int lod = ((TerrainNode) object).getLod();
		Vec2f index = ((TerrainNode) object).getIndex();
		float gap = ((TerrainNode) object).getGap();
		Vec2f location = ((TerrainNode) object).getLocation();
		
		setUniform("localMatrix", object.getLocalTransform().getWorldMatrix());
		setUniform("worldMatrix", object.getWorldTransform().getWorldMatrix());
			
		glActiveTexture(GL_TEXTURE0);
		terrConfig.getHeightmap().bind();
		setUniformi("heightmap", 0);
		
		glActiveTexture(GL_TEXTURE1);
		terrConfig.getNormalmap().bind();
		setUniformi("normalmap", 1);
		
		glActiveTexture(GL_TEXTURE2);
		terrConfig.getSplatmap().bind();
		setUniformi("splatmap", 2);
		
		setUniformf("scaleXZ", terrConfig.getScaleXZ());
		setUniformf("scaleY", terrConfig.getScaleY());
		setUniformi("bezier", terrConfig.getBezier());
		setUniformi("tessFactor", terrConfig.getTessellationFactor());
		setUniformf("tessSlope", terrConfig.getTessellationSlope());
		setUniformf("tessShift", terrConfig.getTessellationShift());
		setUniformi("largeDetailRange", terrConfig.getDetailRange());
		setUniformf("texDetail", terrConfig.getTexDetail());
		setUniformi("lod", lod);
		setUniform("index", index);
		setUniformf("gap", gap);
		setUniform("location", location);
		setUniformi("reflectionOffset", terrConfig.getReflectionOffset());
		setUniformi("diamond_square", terrConfig.isDiamond_square() ? 1 : 0);
		
		glActiveTexture(GL_TEXTURE3);
		GLContext.getResources().getUnderwaterCausticsMap().bind();
		setUniformi("caustics", 3);
		glActiveTexture(GL_TEXTURE4);
		GLContext.getResources().getUnderwaterDudvMap().bind();
		setUniformi("dudvCaustics", 4);
		setUniformf("distortionCaustics", GLContext.getResources().getWaterConfig().getUnderwaterDistortion());
		setUniformf("underwaterBlurFactor", GLContext.getResources().getWaterConfig().getUnderwaterBlur());
		
		for (int i=0; i<8; i++){
			setUniformi("lod_morph_area[" + i + "]", terrConfig.getLod_morphing_area()[i]);
		}
		
		int texUnit = 5;
		for (int i=0; i<3; i++){
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getDiffusemap().bind();
			setUniformi("materials[" + i + "].diffusemap", texUnit);
			texUnit++;
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getHeightmap().bind();
			setUniformi("materials[" + i + "].heightmap", texUnit);
			texUnit++;
			
			glActiveTexture(GL_TEXTURE0 + texUnit);
			terrConfig.getMaterials().get(i).getNormalmap().bind();
			setUniformi("materials[" + i + "].normalmap", texUnit);
			texUnit++;
			
			setUniformf("materials[" + i + "].heightScaling", terrConfig.getMaterials().get(i).getHeightScaling());
			setUniformf("materials[" + i + "].uvScaling", terrConfig.getMaterials().get(i).getHorizontalScaling());
		}
	}
}
