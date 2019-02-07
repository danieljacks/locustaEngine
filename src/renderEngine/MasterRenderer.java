package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animationRenderer.AnimatedModelRenderer;
import entities.Camera;
import entities.Entity;
import entities.Light;
import environmentMapRenderer.EnviroMapRenderer;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import scene.Scene;
import scene.Shadow;
import shadows.ShadowMapMasterRenderer;
import shinyEntities.ShinyEntity;
import shinyEntities.ShinyRenderer;
import skybox.Skybox;
import skybox.SkyboxRenderer;
import sunRenderer.SunRenderer;
import toolbox.ICamera;

public class MasterRenderer {

	private static final Vector4f NO_CLIP = new Vector4f(0, 0, 0, 1);

	private int maxLights;

	private Matrix4f projectionMatrix;
	private Map<TexturedModel, List<Entity>> entityBatch = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntityBatch = new HashMap<TexturedModel, List<Entity>>();

	private EntityRenderer staticRenderer;
	private ShinyRenderer shinyRenderer;
	private TerrainRenderer terrainRenderer;
	private NormalMappingRenderer normalMapRenderer;
	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	private SunRenderer sunRenderer;
	private AnimatedModelRenderer animatedRenderer;

	public MasterRenderer(Loader loader, Camera camera, int maxLights, Shadow shadow, Skybox skybox) {
		this.maxLights = maxLights;
		enableCulling();
		createProjectionMatrix(camera.getFov(), camera.getNearPlane(), camera.getFarPlane());
		staticRenderer = new EntityRenderer(projectionMatrix, maxLights);
		terrainRenderer = new TerrainRenderer(projectionMatrix, maxLights);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix, skybox);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix, maxLights);
		shadowMapRenderer = new ShadowMapMasterRenderer(camera, shadow.getOffset(), shadow.getShadowDistance(), shadow.getQuality());
		shinyRenderer = new ShinyRenderer(projectionMatrix, skybox);
		sunRenderer = new SunRenderer(loader);
		animatedRenderer = new AnimatedModelRenderer();
	}

	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}

	public void renderScene(Scene scene, Vector4f clipPlane) {
		for (Entity entity : scene.getEntities()) {
			if(entity.getModel().getTexture().isHasNormalMap()){
				processNormalMapEntity(entity);
			}else{
				processEntity(entity);
			}
		}
		render(scene, clipPlane, scene.getCamera(), false);
	}

	public void renderLowQualityScene(Scene scene, ICamera cubeMapCamera) {
		for (Entity entity : scene.getEntities()) {
			if(entity.getModel().getTexture().isHasNormalMap()){
				processNormalMapEntity(entity);
			}else{
				processEntity(entity);
			}
		}
		render(scene, NO_CLIP, cubeMapCamera, true);
	}
	
	public void renderShadowMap(List<Entity> entityList, Light sun) {
		//TODO: normal entities too
		for (Entity entity : entityList) {
			processEntity(entity);
		}
		shadowMapRenderer.render(entityBatch, sun);
		entityBatch.clear();
	}

	public void render(Scene scene, Vector4f clipPlane, ICamera camera, boolean lowQuality) {
		prepare(scene.getSky().getColour());
		staticRenderer.render(entityBatch, clipPlane, scene.getLights(), camera, scene.getSky().getColour(), scene.getFog());
		normalMapRenderer.render(normalMapEntityBatch, clipPlane, scene.getLights(), camera,
				scene.getSky().getColour(), scene.getFog());
		terrainRenderer.render(scene.getTerrains(), shadowMapRenderer.getToShadowMapSpaceMatrix(), clipPlane,
				scene.getLights(), camera, scene.getSky().getColour(), scene.getFog(), scene.getShadow());
		if(!lowQuality){
			shinyRenderer.render(scene.getShinyEntities(), camera);
		}
		skyboxRenderer.render(camera, scene.getSky().getColour());
		sunRenderer.render(scene.getSky().getSuns(), camera);
		animatedRenderer.render(scene.getAnimatedEntities(), camera,
				scene.getSky().getSuns().get(0).getLightDirection(), scene.getFog(), scene.getSky().getColour());
		entityBatch.clear();
		normalMapEntityBatch.clear();
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void processEntity(Entity entity) {

		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entityBatch.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entityBatch.put(entityModel, newBatch);
		}

	}

	public void processNormalMapEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntityBatch.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntityBatch.put(entityModel, newBatch);
		}
	}

	public int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}

	public void cleanUp() {
		staticRenderer.cleanUp();
		terrainRenderer.cleanUp();
		normalMapRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
		shinyRenderer.cleanUp();
		sunRenderer.cleanUp();
		animatedRenderer.cleanUp();
	}

	public void prepare(Vector3f skyColor) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(skyColor.getX(), skyColor.getY(), skyColor.getZ(), 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}

	private void createProjectionMatrix(float fov, float nearPlane, float farPlane) {
		projectionMatrix = new Matrix4f();
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
	}

	public void renderEnvironmentMap(Scene scene, ShinyEntity entity) {
		EnviroMapRenderer.renderEnvironmentMap(scene, entity, this);
	}

	public int getMaxLights() {
		return maxLights;
	}

	public void setMaxLights(int maxLights) {
		this.maxLights = maxLights;
	}

}
