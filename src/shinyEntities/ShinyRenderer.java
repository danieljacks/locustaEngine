package shinyEntities;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import models.RawModel;
import models.TexturedModel;
import skybox.Skybox;
import toolbox.Maths;

public class ShinyRenderer {

	private ShinyShader shader;
	private Skybox skybox;

	public ShinyRenderer(Matrix4f projectionMatrix, Skybox skybox) {
		this.skybox = skybox;
		shader = new ShinyShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(List<ShinyEntity> entities, Camera camera) {
		shader.start();
		shader.loadViewMatrix(camera);
		bindEnvironmentMap();
		for (ShinyEntity entity : entities) {
			TexturedModel model = entity.getModel();
			bindModelVao(model);
			loadModelMatrix(entity);
			bindTexture(model);
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindVao();
		}
		shader.stop();
	}

	public void cleanUp() {
		shader.cleanUp();
	}
	
	private void bindEnvironmentMap(){
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox.getTexture());
	}

	private void bindModelVao(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}

	private void unbindVao() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void bindTexture(TexturedModel model) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void loadModelMatrix(ShinyEntity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 0, entity.getRotY(), 0,
				entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
