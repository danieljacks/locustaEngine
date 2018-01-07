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
import textures.ModelTexture;
import toolbox.ICamera;

public class MasterRenderer {

	public static final float FOV = 60;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1100;

	public static final float RED = 0.83f;
	public static final float GREEN = 0.9f;
	public static final float BLUE = 0.92f;

	private static final Vector4f NO_CLIP = new Vector4f(0, 0, 0, 1);

	private Matrix4f projectionMatrix;
	private Map<TexturedModel, List<Entity>> entityBatch = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntityBatch = new HashMap<TexturedModel, List<Entity>>();

	private StaticShader shader = new StaticShader();
	private EntityRenderer staticRenderer;
	private ShinyRenderer shinyRenderer;
	private ShinyShader shinyShader = new ShinyShader();
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	private NormalMappingRenderer normalMapRenderer;
	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;

	public MasterRenderer(Loader loader, Camera camera) {
		enableCulling();
		createProjectionMatrix();
		staticRenderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		shadowMapRenderer = new ShadowMapMasterRenderer(camera);
		shinyRenderer = new ShinyRenderer(projectionMatrix, new Skybox(loader));
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
		prepare();

		shader.start();
		shader.loadClipPlane(NO_CLIP);
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLights(scene.getLights());
		shader.loadViewMatrix(cubeMapCamera);
		staticRenderer.render(entityBatch);
		shader.stop();
		normalMapRenderer.render(normalMapEntityBatch, NO_CLIP, scene.getLights(), cubeMapCamera);
		terrainShader.start();
		terrainShader.loadClipPlane(NO_CLIP);
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLights(scene.getLights());
		terrainShader.loadViewMatrix(cubeMapCamera);
		terrainRenderer.render(scene.getTerrains(), shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		shinyShader.loadViewMatrix(cubeMapCamera);
		shinyRenderer.render(scene.getShinyEntities(), cubeMapCamera);
		skyboxRenderer.render(cubeMapCamera, RED, GREEN, BLUE);
		// terrains.clear();
		entityBatch.clear();
		normalMapEntityBatch.clear();
		// shinyEntities.clear();

	}

	public void render(Scene scene, Vector4f clipPlane) {
		prepare();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLights(scene.getLights());
		shader.loadViewMatrix(scene.getCamera());
		staticRenderer.render(entityBatch);
		shader.stop();
		normalMapRenderer.render(normalMapEntityBatch, clipPlane, scene.getLights(), scene.getCamera());
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLights(scene.getLights());
		terrainShader.loadViewMatrix(scene.getCamera());
		terrainRenderer.render(scene.getTerrains(), shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		shinyShader.loadViewMatrix(scene.getCamera());
		shinyRenderer.render(scene.getShinyEntities(), scene.getCamera());
		skyboxRenderer.render(scene.getCamera(), RED, GREEN, BLUE);
		// terrains.clear();
		entityBatch.clear();
		normalMapEntityBatch.clear();
		// shinyEntities.clear();
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

	public void renderShadowMap(List<Entity> entityList, Light sun) {// TOTO:
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
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
		shinyRenderer.cleanUp();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}

	private void createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	public void renderEnvironmentMap(Scene scene, ShinyEntity entity){
		EnviroMapRenderer.renderEnvironmentMap(scene, entity, this);
	}

}
