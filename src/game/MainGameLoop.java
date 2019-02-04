package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import animation.AnimatedModelLoader;
import animation.Animation;
import animation.AnimationLoader;
import animationRenderer.AnimatedEntity;
import entities.Camera;
import entities.Entity;
import entities.Fog;
import entities.Light;
import entities.Player;
import entities.Sky;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import lensFlare.FlareManager;
import lensFlare.FlareTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import scene.Scene;
import scene.Shadow;
import shinyEntities.ShinyEntity;
import skybox.Skybox;
import sunRenderer.Sun;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import toolbox.MyFile;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		SceneLoader sceneLoader = new SceneLoader();
		Scene scene = sceneLoader.loadForestScene(loader);
		
		MasterRenderer renderer = new MasterRenderer(loader, scene.getCamera(), 4, scene.getShadow());
		TextMaster.init(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		FontType font = new FontType(loader.loadFontTexture("candara"), "candara");
		GUIText text = new GUIText("TEST", 3f, font, new Vector2f(0f, 0f), 1f, true);
		text.setColour(0, 0, 0);
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		//TODO: make it use all terrains
		MousePicker picker = new MousePicker(scene.getCamera(), renderer.getProjectionMatrix(), scene.getTerrains().get(0)); 

		// **********Particles************************

		ParticleTexture particle_fire = new ParticleTexture(loader.loadTexture("fire"), 8, true);
		ParticleSystem particleSystem_fire = new ParticleSystem(particle_fire, 200, 0.1f, -0.03f, 3, 3);
		particleSystem_fire.randomizeRotation();
		particleSystem_fire.setDirection(new Vector3f(0, 1, 0), 0.5f);
		particleSystem_fire.setLifeError(0.1f);
		particleSystem_fire.setSpeedError(0.4f);
		particleSystem_fire.setScaleError(0.8f);

		ParticleTexture particle_smoke = new ParticleTexture(loader.loadTexture("fire"), 8, true);
		ParticleSystem particleSystem_smoke = new ParticleSystem(particle_smoke, 1000, 10, -0.001f, 1, 3);
		particleSystem_smoke.randomizeRotation();
		particleSystem_smoke.setDirection(new Vector3f(0, 1, 1), 0.1f);
		particleSystem_smoke.setLifeError(0.1f);
		particleSystem_smoke.setSpeedError(0.4f);
		particleSystem_smoke.setScaleError(0.8f);
		
		//POSTPROCESSING
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);
		
		Player player = scene.getPlayer();
		
		// ****************Game Loop Below*********************
		while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Display.isCloseRequested()) {
			player.update(scene);
			scene.getCamera().update();
			picker.update();
			for(AnimatedEntity animatedEntity : scene.getAnimatedEntities()){
				animatedEntity.update();
			}
			for(Entity entity : scene.getEntities()){
				entity.update(scene);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
				Vector3f position = new Vector3f(player.getPosition());
				position.setY(player.getPosition().getY() + 2.2f);
				particleSystem_smoke.generateParticles(position, scene.getGravity());
			}
			particleSystem_fire.generateParticles(new Vector3f(40, 5.5f, -30), scene.getGravity());
			if(Mouse.isButtonDown(0) && picker.getCurrentTerrainPoint() != null){
				System.out.println(picker.getCurrentTerrainPoint());
				particleSystem_fire.generateParticles(picker.getCurrentTerrainPoint(), 0.1f);
			}
			
			ParticleMaster.update(scene.getCamera());

			List<Entity> shadowObjects = new ArrayList<>();
			shadowObjects.addAll(scene.getEntities());
			//shadowObjects.addAll(scene.getNormalMapEntities());
			renderer.renderShadowMap(shadowObjects, scene.getLights().get(0));
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			// render reflection teture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (scene.getCamera().getPosition().y - scene.getWaterTiles().get(0).getHeight());
			scene.getCamera().getPosition().y -= distance;
			scene.getCamera().invertPitch();
			renderer.renderScene(scene, new Vector4f(0, 1, 0, -scene.getWaterTiles().get(0).getHeight() + 1));
			scene.getCamera().getPosition().y += distance;
			scene.getCamera().invertPitch();

			// render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, scene.getWaterTiles().get(0).getHeight()));
			
			//generate scenebox for shiny entities
			for(ShinyEntity shiny : scene.getShinyEntities()){
				renderer.renderEnvironmentMap(scene, shiny);
			}
			
			// render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, 100000));
			scene.getFlare().render(scene.getCamera(), scene.getSky().getSuns().get(0).getWorldPosition(scene.getCamera().getPosition()));
			waterRenderer.render(scene.getWaterTiles(), scene.getCamera(), scene.getLights().get(0), scene.getSky().getColour(), scene.getFog());
			ParticleMaster.renderParicles(scene.getCamera());
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0,outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1,outputFbo2);
			//multisampleFbo.resolveToScreen();
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
			
			guiRenderer.render(scene.getGuiTextures());
			TextMaster.render();

			DisplayManager.updateDisplay();
		}

		// *********Clean Up Below**************
		PostProcessing.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		scene.getFlare().cleanUp();
		DisplayManager.closeDisplay();

	}
//	private static TexturedModel loadModel(String fileName, Loader loader){
//        RawModel model = OBJFileLoader.loadOBJ(fileName, loader);
//        ModelTexture texture = new ModelTexture(loader.loadTexture(fileName));
//        return new TexturedModel(model, texture);
//    }
}
