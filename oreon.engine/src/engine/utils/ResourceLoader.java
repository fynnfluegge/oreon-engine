package engine.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.opengl.TextureLoader;

import engine.math.Matrix4f;
import engine.math.Vec3f;

public class ResourceLoader {
	
	public static int loadTexture(String fileName)
	{
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length -1 ];
		
		try
		{
			File image = new File(fileName);
			FileInputStream stream = new FileInputStream(image);
			int id = TextureLoader.getTexture(ext, stream).getTextureID();
			
			// always power of two formats
//			System.out.println(TextureLoader.getTexture(ext, stream).getImageWidth());
//			System.out.println(TextureLoader.getTexture(ext, stream).getTextureWidth());
			
			return id;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}
	


	public static String loadShader(String fileName)
	{
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader shaderReader = null;
		
		try
		{
			shaderReader = new BufferedReader(new FileReader("./res/" + fileName));
			String line;
			while((line = shaderReader.readLine()) != null)
			{
				shaderSource.append(line).append("\n");
			}
			
			shaderReader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return shaderSource.toString();
	}
	
	public static List<Matrix4f> loadObjectTransformsModelMatrix(String fileName){
		
		List<Matrix4f> matrices = new ArrayList<Matrix4f>();

		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(fileName));
			String line;
			int linecounter = 0;
			while((line = reader.readLine()) != null)
			{
				String[] tokens = line.split(" ");
				tokens = Util.removeEmptyStrings(tokens);
				
				if(tokens.length == 0 || tokens[0].equals("#"))
					continue;
				
				Vec3f rotation = null;

				if(tokens[10].equals("["))
				{
					rotation = new Vec3f(Float.valueOf(tokens[11]),
							  			 Float.valueOf(tokens[12]),
							  			 Float.valueOf(tokens[13]));
					
					if(!tokens[14].equals("]"))
						System.err.println("parsing error of file " + fileName + " at line " + linecounter);
				}
				linecounter++;
				
				try{
					Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
					
					matrices.add(rotationMatrix);
				}
				catch (Exception e)
				{
					System.err.println("error when try to create modelMatrix of line " + linecounter);
					e.printStackTrace();
					System.exit(1);
				}
			}
			
			reader.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return matrices;
	}
	
	public static List<Matrix4f> loadObjectTransforms(String fileName){
		
		List<Matrix4f> matrices = new ArrayList<Matrix4f>();

		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(fileName));
			String line;
			int linecounter = 0;
			while((line = reader.readLine()) != null)
			{
				String[] tokens = line.split(" ");
				tokens = Util.removeEmptyStrings(tokens);
				
				if(tokens.length == 0 || tokens[0].equals("#"))
					continue;
				
				Vec3f translation = null;
				Vec3f scaling = null;
				Vec3f rotation = null;

				if(tokens[0].equals("["))
				{
						translation = new Vec3f(Float.valueOf(tokens[1]),
												Float.valueOf(tokens[2]),
												Float.valueOf(tokens[3]));
						
						if(!tokens[4].equals("]"))
							System.err.println("parsing error of file " + fileName + " at line " + linecounter);
				}
				if(tokens[5].equals("["))
				{
						scaling = new Vec3f(Float.valueOf(tokens[6]),
							  				Float.valueOf(tokens[7]),
							  				Float.valueOf(tokens[8]));
					
					if(!tokens[9].equals("]"))
						System.err.println("parsing error of file " + fileName + " at line " + linecounter);
				}
				if(tokens[10].equals("["))
				{
					rotation = new Vec3f(Float.valueOf(tokens[11]),
							  			 Float.valueOf(tokens[12]),
							  			 Float.valueOf(tokens[13]));
					
					if(!tokens[14].equals("]"))
						System.err.println("parsing error of file " + fileName + " at line " + linecounter);
				}
				linecounter++;
				
				try{
					Matrix4f translationMatrix = new Matrix4f().Translation(translation);
					Matrix4f rotationMatrix = new Matrix4f().Rotation(rotation);
					Matrix4f scalingMatrix = new Matrix4f().Scaling(scaling);
					
					matrices.add(translationMatrix.mul(scalingMatrix.mul(rotationMatrix)));
				}
				catch (Exception e)
				{
					System.err.println("error when try to create worldMatrix of line " + linecounter);
					e.printStackTrace();
					System.exit(1);
				}
			}
			
			reader.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return matrices;
	}
	
	
	public static Vec3f[] loadArrangement(String fileName)
	{
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length -1 ];
		
		if (!ext.equals("arr"))
		{
			System.err.println("Error: wrong file format for mesh data: " + ext);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		ArrayList<Vec3f> vertices = new ArrayList<Vec3f>();
		BufferedReader meshReader = null;

		try
		{
			meshReader = new BufferedReader(new FileReader("./res/arrangements/" + fileName));
			String line;
			while((line = meshReader.readLine()) != null)
			{
				String[] tokens = line.split(" ");
				tokens = Util.removeEmptyStrings(tokens);
				
				if(tokens.length == 0 || tokens[0].equals("#"))
					continue;
					else if(tokens[0].equals("v"))
					{
						vertices.add(new Vec3f(Float.valueOf(tokens[1]),
												 Float.valueOf(tokens[2]),
												 Float.valueOf(tokens[3])));
					}
			}
			
			meshReader.close();
			
			Vec3f[] vertexData = new Vec3f[vertices.size()];
			vertices.toArray(vertexData);	
		return vertexData;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
}
