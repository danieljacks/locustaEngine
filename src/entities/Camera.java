package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import toolbox.ICamera;

public class Camera implements ICamera{
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();
	
	private float distanceFromPlayer = 35;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;
	private float fov;
	private float nearPlane;
	private float farPlane;
	
	private Player player;
	
	public Camera(Player player, float fov, float nearPlane, float farPlane){
		this.player = player;
		this.fov = fov;
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
		this.projectionMatrix = createProjectionMatrix(fov, nearPlane, farPlane);
	}
	
	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		yaw%=360;
		updateViewMatrix();
	}
	
	
	public void invertPitch(){
		this.pitch = -pitch;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance + 4;
	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch+4)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch+4)));
	}
	
	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * 0.03f;
		distanceFromPlayer -= zoomLevel;
		if(distanceFromPlayer<5){
			distanceFromPlayer = 5;
		}
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * 0.2f;
			pitch -= pitchChange;
//			if(pitch < 0){
//				pitch = 0;
//			}else if(pitch > 90){
//				pitch = 90;
//			}
		}
	}
	
	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(1)){
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	@Override
	public void reflect(float height){
		this.pitch = -pitch;
		this.position.y = position.y - 2 * (position.y - height);
		updateViewMatrix();
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getProjectionViewMatrix() {
		return Matrix4f.mul(projectionMatrix, viewMatrix, null);
	}
	
	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
	}

	public float getNearPlane() {
		return nearPlane;
	}

	public void setNearPlane(float nearPlane) {
		this.nearPlane = nearPlane;
	}

	public float getFarPlane() {
		return farPlane;
	}

	public void setFarPlane(float farPlane) {
		this.farPlane = farPlane;
	}

	private void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x,-position.y,-position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	}

	private static Matrix4f createProjectionMatrix(float fov, float nearPlane, float farPlane){
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = farPlane - nearPlane;
	
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((farPlane + nearPlane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}
}
