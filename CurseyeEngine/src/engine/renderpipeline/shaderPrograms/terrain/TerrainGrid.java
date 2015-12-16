package engine.renderpipeline.shaderPrograms.terrain;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.gameObject.GameObject;
import engine.main.RenderingEngine;
import engine.math.Matrix4f;
import engine.math.Vec3f;
import engine.models.data.Material;
import engine.renderer.terrain.Terrain;
import engine.renderpipeline.Shader;

public class TerrainGrid extends Shader{
	
	private static TerrainGrid instance = null;
	
	public static TerrainGrid getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainGrid();
	    }
	      return instance;
	}
	
	protected TerrainGrid()
	{
		super();

		addVertexShader(ResourceLoader.loadShader("terrain/grid/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("terrain/grid/Tessellation COntrol.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("terrain/grid/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("terrain/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("terrain/grid/Fragment.glsl"));
		compileShader();
		
		
		addUniform("projectionViewMatrix");
		addUniform("worldMatrix");
		addUniform("scaleY");
		addUniform("scaleXZ");
		addUniform("eyePosition");
		addUniform("color");
		
		addUniform("bezier");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("detailRange");
		addUniform("texDetail");
		
		addUniform("heightmap");
		addUniform("normalmap");
		addUniform("splatmap");
		addUniform("rockgrass.displacemap");
		addUniform("rockgrass.displaceScale");
		addUniform("rock.displacemap");
		addUniform("rock.displaceScale");
		addUniform("snow.displacemap");
		addUniform("snow.displaceScale");
		
		addUniform("clipplane");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionViewMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("projectionViewMatrix", projectionViewMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("eyePosition", Camera.getInstance().getPosition());
		
		setUniform("clipplane", RenderingEngine.getClipplane());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
	}
	
	public void sendUniforms(Material material1, Material material2, Material material3)
	{
		glActiveTexture(GL_TEXTURE3);
		material1.getDisplacemap().bind();
		setUniformi("rockgrass.displacemap", 3);
		setUniformf("rockgrass.displaceScale", material1.getDisplaceScale());

		glActiveTexture(GL_TEXTURE4);
		material2.getDisplacemap().bind();
		setUniformi("rock.displacemap", 4);
		setUniformf("rock.displaceScale", material2.getDisplaceScale());
		
		glActiveTexture(GL_TEXTURE5);
		material3.getDisplacemap().bind();
		setUniformi("snow.displacemap", 5);
		setUniformf("snow.displaceScale", material3.getDisplaceScale());
	}
	
	public void sendUniforms(Vec3f gridColor)
	{
		setUniform("color", gridColor);
	}
	
	public void sendUniforms(GameObject object)
	{
		Terrain terrain = (Terrain) object;
		
		glActiveTexture(GL_TEXTURE0);
		terrain.getHeightmap().bind();
		setUniformi("heightmap", 0);
		glActiveTexture(GL_TEXTURE1);
		terrain.getNormalmap().bind();
		setUniformi("normalmap", 1);
		glActiveTexture(GL_TEXTURE2);
		terrain.getSplatmap().bind();
		setUniformi("splatmap", 2);
		setUniformf("scaleY", terrain.getScaleY());
		setUniformf("scaleXZ", terrain.getScaleXZ());
		setUniformi("bezier", terrain.getBezíer());
		setUniformi("tessFactor", terrain.getTessellationFactor());
		setUniformf("tessSlope", terrain.getTessellationSlope());
		setUniformf("tessShift", terrain.getTessellationShift());
		setUniformi("detailRange", terrain.getDetailRange());
		setUniformf("texDetail", terrain.getTexDetail());
		setUniform("color", terrain.getGridColor());
		
		glActiveTexture(GL_TEXTURE3);
		terrain.getMaterial1().getDisplacemap().bind();
		setUniformi("rockgrass.displacemap", 3);
		setUniformf("rockgrass.displaceScale", terrain.getMaterial1().getDisplaceScale());

		glActiveTexture(GL_TEXTURE4);
		terrain.getMaterial2().getDisplacemap().bind();
		setUniformi("rock.displacemap", 4);
		setUniformf("rock.displaceScale", terrain.getMaterial2().getDisplaceScale());
		
		glActiveTexture(GL_TEXTURE5);
		terrain.getMaterial3().getDisplacemap().bind();
		setUniformi("snow.displacemap", 5);
		setUniformf("snow.displaceScale", terrain.getMaterial3().getDisplaceScale());
	}
}


