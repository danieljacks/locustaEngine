package shinyEntities;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.RawModel;
import models.TexturedModel;
import skybox.Skybox;
import toolbox.ICamera;
import toolbox.Maths;

public class ShinyRenderer {

	private ShinyShader shader;

	public ShinyRenderer(Matrix4f projectionMatrix, Skybox skybox) {
		shader = new ShinyShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(List<ShinyEntity> entities, ICamera iCamera) {
		shader.start();
		shader.loadViewMatrix(iCamera);
		
		for (ShinyEntity entity : entities) {
			bindEnvironmentMap(entity);
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
	
	private void bindEnvironmentMap(ShinyEntity entity){
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, entity.getSceneBox().getID());
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
