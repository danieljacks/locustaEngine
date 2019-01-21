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
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import shinyEntities.ShinyEntity;
import shinyEntities.ShinyRenderer;
import shinyEntities.ShinyShader;
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
	private ShinyShader shinyShader;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	private NormalMappingRenderer normalMapRenderer;
	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	private SunRenderer sunRenderer;
	private AnimatedModelRenderer animatedRenderer;

	public MasterRenderer(Loader loader, Camera camera, int maxLights) {
		this.maxLights = maxLights;
		terrainShader = new TerrainShader(maxLights);
		shinyShader = new ShinyShader();
		enableCulling();
		createProjectionMatrix(camera.getFov(), camera.getNearPlane(), camera.getFarPlane());
		staticRenderer = new EntityRenderer(projectionMatrix, maxLights);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix, 1200);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix, maxLights);
		shadowMapRenderer = new ShadowMapMasterRenderer(camera, 100, 300, 4096); //TODO: remove hardcoded
		shinyRenderer = new ShinyRenderer(projectionMatrix, new Skybox(loader, 1200));
		sunRenderer = new SunRenderer(loader);
		animatedRenderer = new AnimatedModelRenderer();
	}

	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}

	public void renderScene(Scene scene, Vector4f clipPlane) {
		for (Entity entity : scene.getEntities()) {
			processEntity(entity);
		}
		for (Entity entity : scene.getNormalMapEntities()) {
			processNormalMapEntity(entity);
		}
		render(scene, clipPlane);
	}

	public void renderLowQualityScene(Scene scene, ICamera cubeMapCamera) {
		for (Entity entity : scene.getEntities()) {
			processEntity(entity);
		}
		for (Entity entity : scene.getNormalMapEntities()) {
			processNormalMapEntity(entity);
		}
		prepare(scene.getSky().getColour());
		staticRenderer.render(entityBatch, NO_CLIP, scene.getLights(), cubeMapCamera, scene.getSky().getColour());
		normalMapRenderer.render(normalMapEntityBatch, NO_CLIP, scene.getLights(), cubeMapCamera, scene.getSky().getColour());
		terrainShader.start();
		terrainShader.loadClipPlane(NO_CLIP);
		terrainShader.loadSkyColour(scene.getSky().getColour());
		terrainShader.loadLights(scene.getLights());
		terrainShader.loadViewMatrix(cubeMapCamera);
		terrainRenderer.render(scene.getTerrains(), shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		shinyShader.loadViewMatrix(cubeMapCamera);
		shinyRenderer.render(scene.getShinyEntities(), cubeMapCamera);
		skyboxRenderer.render(cubeMapCamera, new Vector3f(scene.getSky().getColour()));
		sunRenderer.render(scene.getSky().getSuns(),scene.getCamera());
		entityBatch.clear();
		normalMapEntityBatch.clear();
	}

	public void render(Scene scene, Vector4f clipPlane) {
		prepare(scene.getSky().getColour());
		staticRenderer.render(entityBatch, clipPlane, scene.getLights(), scene.getCamera(), scene.getSky().getColour());
		normalMapRenderer.render(normalMapEntityBatch, clipPlane, scene.getLights(), scene.getCamera(), scene.getSky().getColour());
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColour(scene.getSky().getColour());
		terrainShader.loadLights(scene.getLights());
		terrainShader.loadViewMatrix(scene.getCamera());
		terrainRenderer.render(scene.getTerrains(), shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		shinyShader.loadViewMatrix(scene.getCamera());
		shinyRenderer.render(scene.getShinyEntities(), scene.getCamera());
		skyboxRenderer.render(scene.getCamera(), scene.getSky().getColour());
		sunRenderer.render(scene.getSky().getSuns(),scene.getCamera());
		animatedRenderer.render(scene.getAnimatedEntities(), scene.getCamera(), scene.getSky().getSuns().get(0).getLightDirection(), scene.getFog(), scene.getSky());
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

	public void renderShadowMap(List<Entity> entityList, Light sun) {// TODO:
																		// all
																		// entities
																		// to
																		// shadowmap
		for (Entity entity : entityList) {
			processEntity(entity);
		}
		shadowMapRenderer.render(entityBatch, sun);
		entityBatch.clear();
	}

	public int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}

	public void cleanUp() {
		staticRenderer.cleanUp();
		terrainShader.cleanUp();
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
	
	public void renderEnvironmentMap(Scene scene, ShinyEntity entity){
		EnviroMapRenderer.renderEnvironmentMap(scene, entity, this);
	}

	public int getMaxLights() {
		return maxLights;
	}

	public void setMaxLights(int maxLights) {
		this.maxLights = maxLights;
	}

}
