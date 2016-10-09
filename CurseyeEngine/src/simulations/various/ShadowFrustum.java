package simulations.various;

import engine.buffers.MeshVAO;
import engine.configs.CCW;
import engine.geometrics.Mesh;
import engine.geometrics.Vertex;
import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;

public class ShadowFrustum extends GameObject{

	private Vec3f lightDirection = new Vec3f(-1,-1,-1).normalize();
	private Vec3f right;
	private Vec3f up;
	private Vec3f[] frustumCorners = new Vec3f[8];
	private Matrix4f lightViewMatrix;
	
	public ShadowFrustum(CameraFrustum cameraFrustum){
	
		Mesh mesh = getMesh(cameraFrustum.getFrustumCorners(), cameraFrustum.getPostion(), cameraFrustum.getForward(),
							cameraFrustum.getUp(),
							cameraFrustum.getZNear(), cameraFrustum.getZFar()); 
		MeshVAO meshBuffer = new MeshVAO();
		meshBuffer.addData(mesh);
		setRenderInfo(new RenderInfo(new CCW(), engine.shaders.basic.Grid.getInstance()));
		Renderer renderer = new Renderer(getRenderInfo().getShader(), meshBuffer);
		Material material = new Material();
		material.setColor(new Vec3f(0.9f,0.1f,0.1f));
		addComponent("Renderer", renderer);
		addComponent("Material", material);
	}
	
	private Mesh getMesh(Vec3f[] cameraFrustumCorners, Vec3f cameraPosition, Vec3f cameraForward, Vec3f cameraUp, float zNear, float zFar){		
		
		right = new Vec3f(lightDirection.getZ(),0,-lightDirection.getX()).normalize();
		up = lightDirection.cross(right).normalize();
		
		lightViewMatrix = new Matrix4f().View(lightDirection, up);		
		
		frustumCorners[0] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[0],1)).xyz();
		frustumCorners[1] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[1],1)).xyz();
		frustumCorners[2] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[2],1)).xyz();
		frustumCorners[3] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[3],1)).xyz();
		frustumCorners[4] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[4],1)).xyz();
		frustumCorners[5] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[5],1)).xyz();
		frustumCorners[6] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[6],1)).xyz();
		frustumCorners[7] = lightViewMatrix.mul(new Quaternion(cameraFrustumCorners[7],1)).xyz();
		
//		// Z-dimension
//		float zMin,zMax;
//		Vec3f zMinV,zMaxV;
//		// initialize
//		zMin = frustumCorners[0].dot(lightDirection);
//		zMax = zMin;
//		zMinV = frustumCorners[0];
//		zMaxV = zMinV;
//				
//		for(Vec3f corner : frustumCorners){
//			float dot = corner.dot(lightDirection);
//			if (dot <= zMin){
//				zMin = dot;
//				zMinV = corner;
//			}
//			if (dot >= zMax){
//				zMax = dot;
//				zMaxV = corner;
//			}
//		}
//		
//		// Y-dimension
//		float yMin,yMax;
//		Vec3f yMinV,yMaxV;
//		// initialize
//		yMin = frustumCorners[0].dot(up);
//		yMax = yMin;
//		yMinV = frustumCorners[0];
//		yMaxV = yMinV;
//				
//		for(Vec3f corner : frustumCorners){
//			float dot = corner.dot(up);
//			if (dot <= yMin){
//				yMin = dot;
//				yMinV = corner;
//			}
//			if (dot >= yMax){
//				yMax = dot;
//				yMaxV = corner;
//			}
//		}
//				
//		// X-dimension
//		float xMin,xMax;
//		Vec3f xMinV,xMaxV;
//		// initialize
//		xMin = frustumCorners[0].dot(right);
//		xMax = xMin;
//		xMinV = frustumCorners[0];
//		xMaxV = xMinV;
//			
//		for(Vec3f corner : frustumCorners){
//			float dot = corner.dot(right);
//			if (dot <= xMin){
//				xMin = dot;
//				xMinV = corner;
//			}
//			if (dot >= xMax){
//				xMax = dot;
//				xMaxV = corner;
//			}
//		}
//				
//		float depth = zMax - zMin;
//		float width = xMax - xMin;
//		float height = yMax - yMin;	
		
		Vertex[] vertices = new Vertex[10];
		
		vertices[0] = new Vertex(frustumCorners[0]);
		vertices[1] = new Vertex(frustumCorners[1]);
		vertices[2] = new Vertex(frustumCorners[2]);
		vertices[3] = new Vertex(frustumCorners[3]);
		vertices[4] = new Vertex(frustumCorners[4]);
		vertices[5] = new Vertex(frustumCorners[5]);
		vertices[6] = new Vertex(frustumCorners[6]);
		vertices[7] = new Vertex(frustumCorners[7]);
		vertices[8] = new Vertex(lightViewMatrix.mul(new Quaternion(0,0,1,0)).xyz());
		vertices[9] = new Vertex(lightViewMatrix.mul(new Quaternion(0,0,20,0)).xyz());
		
		int[] indices = {0,1,0,0,2,0,1,3,1,2,3,2,0,4,0,1,5,1,2,6,2,3,7,3,4,5,4,4,6,4,6,7,6,5,7,5,8,9,8};
		
		return new Mesh(vertices,indices);
		
	}
}
