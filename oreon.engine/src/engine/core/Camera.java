package engine.core;

import java.nio.FloatBuffer;

import engine.buffers.UBO;
import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.utils.BufferUtil;
import engine.utils.Constants;
import engine.utils.Util;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;

public class Camera {
	
	private static Camera instance = null;

	private final Vec3f yAxis = new Vec3f(0,1,0);
	
	private Vec3f position;
	private Vec3f previousPosition;
	private Vec3f forward;
	private Vec3f previousForward;
	private Vec3f up;
	private int scaleFactor;
	private float movAmt;
	private float rotAmt;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;
	private Matrix4f viewProjectionMatrix;
	private Matrix4f previousViewMatrix;
	private Matrix4f previousViewProjectionMatrix;
	private boolean cameraMoved;
	private boolean cameraRotated;
	
	private UBO ubo;
	private FloatBuffer floatBuffer;
	private final int bufferSize = Float.BYTES * (4+16+(6*4));
	
	private float width;
	private float height;
	private float fovY;
	
	private Vec2f lockedMousePosition;
	private Vec2f currentMousePosition;
	private float rotYstride;
	private float rotYamt;
	private float rotYcounter;
	private boolean rotYInitiated = false;
	private float rotXstride;
	private float rotXamt;
	private float rotXcounter;
	private boolean rotXInitiated = false;
	private float mouseSensitivity = 0.8f;
	
	private Quaternion[] frustumPlanes = new Quaternion[6];
	private Vec3f[] frustumCorners = new Vec3f[8];
	
	@SuppressWarnings("unused")
	private GLFWKeyCallback keyCallback;
	 
	@SuppressWarnings("unused")
	private GLFWCursorPosCallback cursorPosCallback;
	
	@SuppressWarnings("unused")
	private GLFWMouseButtonCallback mouseButtonCallback;
	
	private boolean KEY_W_DOWN;
	private boolean KEY_A_DOWN;
	private boolean KEY_S_DOWN;
	private boolean KEY_D_DOWN;
	private boolean KEY_UP_DOWN;
	private boolean KEY_LEFT_DOWN;
	private boolean KEY_DOWN_DOWN;
	private boolean KEY_RIGHT_DOWN;
	private boolean MOUSE_BUTTON2_DOWN;
	  
	public static Camera getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Camera();
	    }
	      return instance;
	}
	
	protected Camera()
	{
		this(new Vec3f(1060,20,-830), new Vec3f(0,0,1), new Vec3f(0,1,0));
		setProjection(70, Window.getInstance().getWidth(), Window.getInstance().getHeight());
		setViewMatrix(new Matrix4f().View(this.getForward(), this.getUp()).mul(
				new Matrix4f().Translation(this.getPosition().mul(-1))));
		initfrustumPlanes();
		currentMousePosition = new Vec2f();
		previousViewMatrix = new Matrix4f().Zero();
		viewProjectionMatrix = new Matrix4f().Zero();
		previousViewProjectionMatrix = new Matrix4f().Zero();
		ubo = new UBO();
		ubo.setBinding_point_index(Constants.CameraUniformBlockBinding);
		ubo.bindBufferBase();
		ubo.allocate(bufferSize);
		floatBuffer = BufferUtil.createFloatBuffer(bufferSize);
		
		glfwSetKeyCallback(Window.getInstance().getWindow(), (keyCallback = new GLFWKeyCallback() {

            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
            	 if(key == GLFW_KEY_W && action == GLFW_PRESS) {
            		 setKEY_W_DOWN(true);
            	 }
            	 if(key == GLFW_KEY_A && action == GLFW_PRESS) {
            		 setKEY_A_DOWN(true);
            	 }
            	 if(key == GLFW_KEY_S && action == GLFW_PRESS) {
            		 setKEY_S_DOWN(true);
            	 }
            	 if(key == GLFW_KEY_D && action == GLFW_PRESS) {
            		 setKEY_D_DOWN(true);
            	 }
            	 
            	 if(key == GLFW_KEY_W && action == GLFW_RELEASE) {
            		 setKEY_W_DOWN(false);
            	 }
            	 if(key == GLFW_KEY_A && action == GLFW_RELEASE) {
            		 setKEY_A_DOWN(false);
            	 }
            	 if(key == GLFW_KEY_S && action == GLFW_RELEASE) {
            		 setKEY_S_DOWN(false);
            	 }
            	 if(key == GLFW_KEY_D && action == GLFW_RELEASE) {
            		 setKEY_D_DOWN(false);
            	 }
            	 
            	 if(key == GLFW_KEY_UP && action == GLFW_PRESS) {
            		 setKEY_UP_DOWN(true);
            	 }
            	 if(key == GLFW_KEY_LEFT && action == GLFW_PRESS) {
            		 setKEY_LEFT_DOWN(true);
            	 }
            	 if(key == GLFW_KEY_DOWN && action == GLFW_PRESS) {
            		 setKEY_DOWN_DOWN(true);
            	 }
            	 if(key == GLFW_KEY_RIGHT && action == GLFW_PRESS) {
            		 setKEY_RIGHT_DOWN(true);
            	 }
            	 
            	 if(key == GLFW_KEY_UP && action == GLFW_RELEASE) {
            		 setKEY_UP_DOWN(false);
            	 }
            	 if(key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
            		 setKEY_LEFT_DOWN(false);
            	 }
            	 if(key == GLFW_KEY_DOWN && action == GLFW_RELEASE) {
            		 setKEY_DOWN_DOWN(false);
            	 }
            	 if(key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
            		 setKEY_RIGHT_DOWN(false);
            	 }
            }
        }));
		
		glfwSetMouseButtonCallback(Window.getInstance().getWindow(), (mouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                if(button == 2 && action == GLFW_PRESS) {
                	setMOUSE_BUTTON2_DOWN(true);
                	lockedMousePosition = new Vec2f(currentMousePosition);
                	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                }

                if(button == 2 && action == GLFW_RELEASE) {
                	setMOUSE_BUTTON2_DOWN(false);
                	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                }
            }
		}));
		
		glfwSetCursorPosCallback(Window.getInstance().getWindow(), (cursorPosCallback = new GLFWCursorPosCallback() {

            @Override
            public void invoke(long window, double xpos, double ypos) {
                currentMousePosition.setX((float) xpos);
                currentMousePosition.setY((float) ypos);
            }

		}));
		
	}
	
	private Camera(Vec3f position, Vec3f forward, Vec3f up)
	{
		setPosition(position);
		setForward(forward);
		setUp(up);
		up.normalize();
		forward.normalize();
	}
	
	public void update()
	{
		glfwPollEvents();
		
		setPreviousPosition(new Vec3f(position));
		setPreviousForward(new Vec3f(forward));
		cameraMoved = false;
		cameraRotated = false;
		
		setScaleFactor(400);
		movAmt = scaleFactor * 0.001f;
		rotAmt = 8 * scaleFactor * 0.001f; 
		
		if(isKEY_W_DOWN())
			move(getForward(), movAmt);
		if(isKEY_S_DOWN())
			move(getForward(), -movAmt);
		if(isKEY_A_DOWN())
			move(getLeft(), movAmt);
		if(isKEY_D_DOWN())
			move(getRight(), movAmt);
				
		if(isKEY_UP_DOWN())
			rotateX(-rotAmt/8f);
		if(isKEY_DOWN_DOWN())
			rotateX(rotAmt/8f);
		if(isKEY_LEFT_DOWN())
			rotateY(-rotAmt/8f);
		if(isKEY_RIGHT_DOWN())
			rotateY(rotAmt/8f);
		
		// free mouse rotation
		if(isMOUSE_BUTTON2_DOWN())
		{
			float dy = lockedMousePosition.getY() - currentMousePosition.getY();
			float dx = lockedMousePosition.getX() - currentMousePosition.getX();
			
			// y-axxis rotation
			
			if (dy != 0){
				rotYstride = Math.abs(dy * 0.04f);
				rotYamt = -dy;
				rotYcounter = 0;
				rotYInitiated = true;
			}
			
			if (rotYInitiated ){
				
				// up-rotation
				if (rotYamt < 0){
					if (rotYcounter > rotYamt){
						rotateX(-rotYstride * mouseSensitivity);
						rotYcounter -= rotYstride;
						rotYstride *= 0.98;
					}
					else rotYInitiated = false;
				}
				// down-rotation
				else if (rotYamt > 0){
					if (rotYcounter < rotYamt){
						rotateX(rotYstride * mouseSensitivity);
						rotYcounter += rotYstride;
						rotYstride *= 0.98;
					}
					else rotYInitiated = false;
				}
			}
			
			// x-axxis rotation
			if (dx != 0){
				rotXstride = Math.abs(dx * 0.04f);
				rotXamt = dx;
				rotXcounter = 0;
				rotXInitiated = true;
			}
			
			if (rotXInitiated){
				
				// up-rotation
				if (rotXamt < 0){
					if (rotXcounter > rotXamt){
						rotateY(rotXstride * mouseSensitivity);
						rotXcounter -= rotXstride;
						rotXstride *= 0.96;
					}
					else rotXInitiated = false;
				}
				// down-rotation
				else if (rotXamt > 0){
					if (rotXcounter < rotXamt){
						rotateY(-rotXstride * mouseSensitivity);
						rotXcounter += rotXstride;
						rotXstride *= 0.96;
					}
					else rotXInitiated = false;
				}
			}
			
			glfwSetCursorPos(Window.getInstance().getWindow(),
					 lockedMousePosition.getX(),
					 lockedMousePosition.getY());
		}
		
		if (!position.equals(previousPosition)){
			cameraMoved = true;	
		}
		
		if (!forward.equals(previousForward)){
			cameraRotated = true;
		}
		
		setPreviousViewMatrix(viewMatrix);
		setPreviousViewProjectionMatrix(viewProjectionMatrix);
		setViewMatrix(new Matrix4f().View(this.getForward(), this.getUp()).mul(
				new Matrix4f().Translation(this.getPosition().mul(-1))));
		setViewProjectionMatrix(projectionMatrix.mul(viewMatrix));
		
		updateUBO();
	}
	
	public void move(Vec3f dir, float amount)
	{
		Vec3f newPos = position.add(dir.mul(amount));	
		setPosition(newPos);
	}
	
	private void updateUBO(){
		floatBuffer.clear();
		floatBuffer.put(BufferUtil.createFlippedBuffer(this.position));
		floatBuffer.put(0);
		floatBuffer.put(BufferUtil.createFlippedBuffer(viewMatrix));
		floatBuffer.put(BufferUtil.createFlippedBuffer(viewProjectionMatrix));
		floatBuffer.put(BufferUtil.createFlippedBuffer(frustumPlanes));
		ubo.updateData(floatBuffer, bufferSize);
	}
	
	private void initfrustumPlanes()
	{
		// ax * bx * cx +  d = 0; store a,b,c,d
		
		//left plane
		Quaternion leftPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) + this.projectionMatrix.get(0, 0) * (float) ((Math.tan(Math.toRadians(this.fovY/2)) * ((double) Window.getInstance().getWidth()/ (double) Window.getInstance().getHeight()))),
				this.projectionMatrix.get(3, 1) + this.projectionMatrix.get(0, 1),
				this.projectionMatrix.get(3, 2) + this.projectionMatrix.get(0, 2),
				this.projectionMatrix.get(3, 3) + this.projectionMatrix.get(0, 3));
		
				this.frustumPlanes[0] = Util.normalizePlane(leftPlane);
		
		//right plane
		Quaternion rightPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) - this.projectionMatrix.get(0, 0) * (float) ((Math.tan(Math.toRadians(this.fovY/2)) * ((double) Window.getInstance().getWidth()/ (double) Window.getInstance().getHeight()))),
				this.projectionMatrix.get(3, 1) - this.projectionMatrix.get(0, 1),
				this.projectionMatrix.get(3, 2) - this.projectionMatrix.get(0, 2),
				this.projectionMatrix.get(3, 3) - this.projectionMatrix.get(0, 3));
		
				this.frustumPlanes[1] = Util.normalizePlane(rightPlane);
		
		//bot plane
		Quaternion botPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) + this.projectionMatrix.get(1, 0),
				this.projectionMatrix.get(3, 1) + this.projectionMatrix.get(1, 1) * (float) Math.tan(Math.toRadians(this.fovY/2)),
				this.projectionMatrix.get(3, 2) + this.projectionMatrix.get(1, 2),
				this.projectionMatrix.get(3, 3) + this.projectionMatrix.get(1, 3));
		
				this.frustumPlanes[2] = Util.normalizePlane(botPlane);
		
		//top plane
		Quaternion topPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) - this.projectionMatrix.get(1, 0),
				this.projectionMatrix.get(3, 1) - this.projectionMatrix.get(1, 1) * (float) Math.tan(Math.toRadians(this.fovY/2)),
				this.projectionMatrix.get(3, 2) - this.projectionMatrix.get(1, 2),
				this.projectionMatrix.get(3, 3) - this.projectionMatrix.get(1, 3));
		
				this.frustumPlanes[3] = Util.normalizePlane(topPlane);
		
		//near plane
		Quaternion nearPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) + this.projectionMatrix.get(2, 0),
				this.projectionMatrix.get(3, 1) + this.projectionMatrix.get(2, 1),
				this.projectionMatrix.get(3, 2) + this.projectionMatrix.get(2, 2),
				this.projectionMatrix.get(3, 3) + this.projectionMatrix.get(2, 3));
		
				this.frustumPlanes[4] = Util.normalizePlane(nearPlane);
		
		//far plane
				Quaternion farPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) - this.projectionMatrix.get(2, 0),
				this.projectionMatrix.get(3, 1) - this.projectionMatrix.get(2, 1),
				this.projectionMatrix.get(3, 2) - this.projectionMatrix.get(2, 2),
				this.projectionMatrix.get(3, 3) - this.projectionMatrix.get(2, 3));
				
				this.frustumPlanes[5] = Util.normalizePlane(farPlane);
	}
	
	public void rotateY(float angle)
	{
		Vec3f hAxis = yAxis.cross(forward).normalize();
		
		forward.rotate(angle, yAxis).normalize();
		
		up = forward.cross(hAxis).normalize();
	}
	
	public void rotateX(float angle)
	{
		Vec3f hAxis = yAxis.cross(forward).normalize();

		forward.rotate(angle, hAxis).normalize();
		
		up = forward.cross(hAxis).normalize();
	}
	
	public Vec3f getLeft()
	{
		Vec3f left = forward.cross(up);
		left.normalize();
		return left;
	}
	
	public Vec3f getRight()
	{
		Vec3f right = up.cross(forward);
		right.normalize();
		return right;
	}

	public int getScaleFactor() {
		return this.scaleFactor;
	}

	public void setScaleFactor(int scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}
	
	public  void setProjection(float fovY, float width, float height)
	{
		this.fovY = fovY;
		this.width = width;
		this.height = height;
		
		this.projectionMatrix = new Matrix4f().PerspectiveProjection(fovY, width, height, Constants.ZNEAR, Constants.ZFAR);
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	public void setViewMatrix(Matrix4f viewMatrix) {
		this.viewMatrix = viewMatrix;
	}

	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;
	}

	public Vec3f getForward() {
		return forward;
	}

	public void setForward(Vec3f forward) {
		this.forward = forward;
	}

	public Vec3f getUp() {
		return up;
	}

	public void setUp(Vec3f up) {
		this.up = up;
	}

	public Quaternion[] getFrustumPlanes() {
		return frustumPlanes;
	}
	
	public float getFovY(){
		return this.fovY;
	}
	
	public float getWidth(){
		return this.width;
	}

	public float getHeight(){
		return this.height;
	}
	
	public void setViewProjectionMatrix(Matrix4f viewProjectionMatrix) {
		this.viewProjectionMatrix = viewProjectionMatrix;
	}
	
	public Matrix4f getViewProjectionMatrix() {
		return viewProjectionMatrix;
	}

	public Matrix4f getPreviousViewProjectionMatrix() {
		return previousViewProjectionMatrix;
	}

	public void setPreviousViewProjectionMatrix(
			Matrix4f previousViewProjectionMatrix) {
		this.previousViewProjectionMatrix = previousViewProjectionMatrix;
	}

	public Matrix4f getPreviousViewMatrix() {
		return previousViewMatrix;
	}

	public void setPreviousViewMatrix(Matrix4f previousViewMatrix) {
		this.previousViewMatrix = previousViewMatrix;
	}

	public Vec3f[] getFrustumCorners() {
		return frustumCorners;
	}

	public boolean isCameraMoved() {
		return cameraMoved;
	}

	public boolean isCameraRotated() {
		return cameraRotated;
	}
	
	public Vec3f getPreviousPosition() {
		return previousPosition;
	}

	public void setPreviousPosition(Vec3f previousPosition) {
		this.previousPosition = previousPosition;
	}
	
	public Vec3f getPreviousForward() {
		return previousForward;
	}

	private void setPreviousForward(Vec3f previousForward) {
		this.previousForward = previousForward;
	}

	private boolean isKEY_W_DOWN() {
		return KEY_W_DOWN;
	}

	private void setKEY_W_DOWN(boolean kEY_W_DOWN) {
		KEY_W_DOWN = kEY_W_DOWN;
	}

	private boolean isKEY_A_DOWN() {
		return KEY_A_DOWN;
	}

	private void setKEY_A_DOWN(boolean kEY_A_DOWN) {
		KEY_A_DOWN = kEY_A_DOWN;
	}

	private boolean isKEY_S_DOWN() {
		return KEY_S_DOWN;
	}

	private void setKEY_S_DOWN(boolean kEY_S_DOWN) {
		KEY_S_DOWN = kEY_S_DOWN;
	}

	private boolean isKEY_D_DOWN() {
		return KEY_D_DOWN;
	}

	private void setKEY_D_DOWN(boolean kEY_D_DOWN) {
		KEY_D_DOWN = kEY_D_DOWN;
	}

	private boolean isMOUSE_BUTTON2_DOWN() {
		return MOUSE_BUTTON2_DOWN;
	}

	private void setMOUSE_BUTTON2_DOWN(boolean mOUSE_BUTTON2_DOWN) {
		MOUSE_BUTTON2_DOWN = mOUSE_BUTTON2_DOWN;
	}

	private boolean isKEY_UP_DOWN() {
		return KEY_UP_DOWN;
	}

	private void setKEY_UP_DOWN(boolean kEY_UP_DOWN) {
		KEY_UP_DOWN = kEY_UP_DOWN;
	}

	private boolean isKEY_LEFT_DOWN() {
		return KEY_LEFT_DOWN;
	}

	private void setKEY_LEFT_DOWN(boolean kEY_LEFT_DOWN) {
		KEY_LEFT_DOWN = kEY_LEFT_DOWN;
	}

	private boolean isKEY_DOWN_DOWN() {
		return KEY_DOWN_DOWN;
	}

	private void setKEY_DOWN_DOWN(boolean kEY_DOWN_DOWN) {
		KEY_DOWN_DOWN = kEY_DOWN_DOWN;
	}

	private boolean isKEY_RIGHT_DOWN() {
		return KEY_RIGHT_DOWN;
	}

	private void setKEY_RIGHT_DOWN(boolean kEY_RIGHT_DOWN) {
		KEY_RIGHT_DOWN = kEY_RIGHT_DOWN;
	}

}
