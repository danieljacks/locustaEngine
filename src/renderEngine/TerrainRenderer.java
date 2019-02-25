package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Fog;
import entities.Light;
import models.RawModel;
import scene.Shadow;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexturePack;
import toolbox.ICamera;
import toolbox.Maths;

public class TerrainRenderer {

	private TerrainShader shader;

	public TerrainRenderer(Matrix4f projectionMatrix, int maxLights) {
		this.shader = new TerrainShader(maxLights);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(List<Terrain> terrains, Matrix4f toShadowSpace, Vector4f clipPlane, List<Light> lights,
			ICamera camera, Vector3f skyColor, Fog fog, Shadow shadow, float ambientLightLevel) {
		shader.start();
		prepare(clipPlane, lights, camera, skyColor, fog, shadow, ambientLightLevel);
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		for (Terrain terrain : terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
					GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
		shader.stop();
	}

	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}
	
	private void bindTextures(Terrain terrain){
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		//GL11.glTexCoord2d(2, 2);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		//GL11.glTexCoord2d(100, 100);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		//GL11.glTexCoord2d(100, 100);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		//GL11.glTexCoord2d(100, 100);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
		//GL11.glTexCoord2d(100, 100);
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	private void prepare(Vector4f clipPlane, List<Light> lights, ICamera camera, Vector3f skyColor, Fog fog, 
			Shadow shadow, float ambientLightLevel) {
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(skyColor);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		shader.loadFog(fog.getDensity(), fog.getGradient());
		shader.loadShadow(shadow);
		shader.loadAmbientLightLevel(ambientLightLevel);
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
}
