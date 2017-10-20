package org.oreon.core.scene;

import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Quaternion;
import org.oreon.core.math.Vec3f;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Constants;
import org.oreon.core.util.Util;


public abstract class Camera {
	
private final Vec3f yAxis = new Vec3f(0,1,0);
	
	private Vec3f position;
	private Vec3f previousPosition;
	private Vec3f forward;
	private Vec3f previousForward;
	private Vec3f up;
	private float movAmt = 0.1f;
	private float rotAmt = 1.0f;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;
	private Matrix4f viewProjectionMatrix;
	private Matrix4f previousViewMatrix;
	private Matrix4f previousViewProjectionMatrix;
	private boolean isCameraMoved;
	private boolean isCameraRotated;
	
	private float width;
	private float height;
	private float fovY;

	private float rotYstride;
	private float rotYamt = 0;
	private float rotXstride;
	private float rotXamt = 0;
	private float mouseSensitivity = 0.08f;
	private boolean isUpRotation;
	private boolean isDownRotation;	
	private boolean isLeftRotation;	
	private boolean isRightRotation;	
	
	private Quaternion[] frustumPlanes = new Quaternion[6];
	private Vec3f[] frustumCorners = new Vec3f[8];
	
	protected Camera()
	{
		this(new Vec3f(-200,400,-20), new Vec3f(1,0,-1).normalize(), new Vec3f(0,1,0));
		setProjection(70, CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight());
		setViewMatrix(new Matrix4f().View(this.getForward(), this.getUp()).mul(
				new Matrix4f().Translation(this.getPosition().mul(-1))));
		initfrustumPlanes();
		previousViewMatrix = new Matrix4f().Zero();
		viewProjectionMatrix = new Matrix4f().Zero();
		previousViewProjectionMatrix = new Matrix4f().Zero();
	}
	
	private Camera(Vec3f position, Vec3f forward, Vec3f up)
	{
		setPosition(position);
		setForward(forward);
		setUp(up);
		up.normalize();
		forward.normalize();
	}
	
	public abstract void init();
	
	public abstract void update();
	
	public void move(Vec3f dir, float amount)
	{
		Vec3f newPos = position.add(dir.mul(amount));	
		setPosition(newPos);
	}
	
	private void initfrustumPlanes()
	{
		// ax * bx * cx +  d = 0; store a,b,c,d
		
		//left plane
		Quaternion leftPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) + this.projectionMatrix.get(0, 0) * (float) ((Math.tan(Math.toRadians(this.fovY/2)) * ((double) CoreSystem.getInstance().getWindow().getWidth()/ (double) CoreSystem.getInstance().getWindow().getHeight()))),
				this.projectionMatrix.get(3, 1) + this.projectionMatrix.get(0, 1),
				this.projectionMatrix.get(3, 2) + this.projectionMatrix.get(0, 2),
				this.projectionMatrix.get(3, 3) + this.projectionMatrix.get(0, 3));
		
				this.frustumPlanes[0] = Util.normalizePlane(leftPlane);
		
		//right plane
		Quaternion rightPlane = new Quaternion(
				this.projectionMatrix.get(3, 0) - this.projectionMatrix.get(0, 0) * (float) ((Math.tan(Math.toRadians(this.fovY/2)) * ((double) CoreSystem.getInstance().getWindow().getWidth()/ (double) CoreSystem.getInstance().getWindow().getHeight()))),
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
		return isCameraMoved;
	}

	public void setCameraMoved(boolean isCameraMoved) {
		this.isCameraMoved = isCameraMoved;
	}

	public boolean isCameraRotated() {
		return isCameraRotated;
	}
	
	public void setCameraRotated(boolean isCameraRotated) {
		this.isCameraRotated = isCameraRotated;
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

	public void setPreviousForward(Vec3f previousForward) {
		this.previousForward = previousForward;
	}

	public float getMovAmt() {
		return movAmt;
	}

	public void setMovAmt(float movAmt) {
		this.movAmt = movAmt;
	}

	public float getRotAmt() {
		return rotAmt;
	}

	public void setRotAmt(float rotAmt) {
		this.rotAmt = rotAmt;
	}

	public float getRotYstride() {
		return rotYstride;
	}

	public void setRotYstride(float rotYstride) {
		this.rotYstride = rotYstride;
	}

	public float getRotYamt() {
		return rotYamt;
	}

	public void setRotYamt(float rotYamt) {
		this.rotYamt = rotYamt;
	}

	public float getRotXstride() {
		return rotXstride;
	}

	public void setRotXstride(float rotXstride) {
		this.rotXstride = rotXstride;
	}

	public float getRotXamt() {
		return rotXamt;
	}

	public void setRotXamt(float rotXamt) {
		this.rotXamt = rotXamt;
	}

	public float getMouseSensitivity() {
		return mouseSensitivity;
	}

	public void setMouseSensitivity(float mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;
	}

	public boolean isUpRotation() {
		return isUpRotation;
	}

	public void setUpRotation(boolean isUpRotation) {
		this.isUpRotation = isUpRotation;
	}

	public boolean isDownRotation() {
		return isDownRotation;
	}

	public void setDownRotation(boolean isDownRotation) {
		this.isDownRotation = isDownRotation;
	}

	public boolean isLeftRotation() {
		return isLeftRotation;
	}

	public void setLeftRotation(boolean isLeftRotation) {
		this.isLeftRotation = isLeftRotation;
	}

	public boolean isRightRotation() {
		return isRightRotation;
	}

	public void setRightRotation(boolean isRightRotation) {
		this.isRightRotation = isRightRotation;
	}
}