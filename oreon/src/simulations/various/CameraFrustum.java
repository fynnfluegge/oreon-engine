package simulations.various;

import engine.buffers.MeshVAO;
import engine.configs.CCW;
import engine.geometrics.Mesh;
import engine.geometrics.Vertex;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.Material;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;

public class CameraFrustum extends GameObject{

	private Vec3f position = new Vec3f(0,0,1);
	private Vec3f forward = new Vec3f(0,0,1).normalize();
	private Vec3f up = new Vec3f(0,1,0).normalize();
	private Vec3f right = up.cross(forward);
	private float zNear = 1;
	private float zFar = 10;
	private float fovY = 50;
	private float width = 1600;
	private float height = 900;
	private Vec3f[] frustumCorners = new Vec3f[8];
	
	
	public CameraFrustum(){
		
		 Mesh mesh = getMesh(); 
		 MeshVAO meshBuffer = new MeshVAO();
		 meshBuffer.addData(mesh);
		 setRenderInfo(new RenderInfo(new CCW(), engine.shaders.basic.Grid.getInstance()));
		 Renderer renderer = new Renderer(getRenderInfo().getShader(), meshBuffer);
		 Material material = new Material();
		 material.setColor(new Vec3f(0.1f,0.9f,0.1f));
		 addComponent("Renderer", renderer);
		 addComponent("Material", material);
	}
	
	private Mesh getMesh(){
		
		float tanFOV = (float) Math.tan(Math.toRadians(fovY/2));
		float aspectRatio = width/height;
		
		//width and height of near plane
		float heightNear = 2 * tanFOV * zNear;
		float widthNear = heightNear * aspectRatio;
		
		//width and height of far plane
		float heightFar = 2 * tanFOV * zFar;
		float widthFar = heightFar * aspectRatio;
		
		//center of planes
		Vec3f centerNear = position.add(forward.mul(zNear));
		Vec3f centerFar = position.add(forward.mul(zFar));
		
		Vec3f NearTopLeft = centerNear.add(up.mul(heightNear/2f)).sub(right.mul(widthNear/2f));
		Vec3f NearTopRight = centerNear.add(up.mul(heightNear/2f)).add(right.mul(widthNear/2f));
		Vec3f NearBottomLeft = centerNear.sub(up.mul(heightNear/2f)).sub(right.mul(widthNear/2f));
		Vec3f NearBottomRight = centerNear.sub(up.mul(heightNear/2f)).add(right.mul(widthNear/2f));
		
		Vec3f FarTopLeft = centerFar.add(up.mul(heightFar/2f)).sub(right.mul(widthFar/2f));
		Vec3f FarTopRight = centerFar.add(up.mul(heightFar/2f)).add(right.mul(widthFar/2f));
		Vec3f FarBottomLeft = centerFar.sub(up.mul(heightFar/2f)).sub(right.mul(widthFar/2f));
		Vec3f FarBottomRight = centerFar.sub(up.mul(heightFar/2f)).add(right.mul(widthFar/2f));
		
		frustumCorners[0] = NearTopLeft;
		frustumCorners[1] = NearTopRight;
		frustumCorners[2] = NearBottomLeft;
		frustumCorners[3] = NearBottomRight;
		frustumCorners[4] = FarTopLeft;
		frustumCorners[5] = FarTopRight;
		frustumCorners[6] = FarBottomLeft;
		frustumCorners[7] = FarBottomRight;
		
//		System.out.println(arg0);
		
		Vertex[] vertices = new Vertex[8];
		
		vertices[0] = new Vertex(NearTopLeft);
		vertices[1] = new Vertex(NearTopRight);
		vertices[2] = new Vertex(NearBottomLeft);
		vertices[3] = new Vertex(NearBottomRight);
		vertices[4] = new Vertex(FarTopLeft);
		vertices[5] = new Vertex(FarTopRight);
		vertices[6] = new Vertex(FarBottomLeft);
		vertices[7] = new Vertex(FarBottomRight);
		
		int[] indices = {0,2,0,0,1,0,1,3,1,2,3,2,0,4,0,4,5,4,5,1,5,4,6,4,5,7,5,6,7,6,6,2,6,3,7,3};
		
		return new Mesh(vertices,indices);
	}
	
	public void update(){
		
	}

	public Vec3f[] getFrustumCorners() {
		return frustumCorners;
	}
	
	public Vec3f getPostion(){
		return position;
	}
	
	public Vec3f getForward(){
		return forward;
	}
	
	public float getZFar(){
		return zFar;
	}
	
	public float getZNear(){
		return zNear;
	}
	
	public Vec3f getUp(){
		return up;
	}
	
}


