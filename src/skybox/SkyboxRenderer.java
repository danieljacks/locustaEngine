package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class SkyboxRenderer {
	
	public Skybox skybox;
	private SkyboxShader shader;
	private float time =0;
	
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix){
		skybox = new Skybox(loader);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Camera camera, float r, float g, float b){
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColour(r, g, b);
		GL30.glBindVertexArray(skybox.getCube().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, skybox.getCube().getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void bindTextures(){
		time += DisplayManager.getFrameTimeSeconds() * 200;
		time %= 24000;
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 5000){
			texture1 = skybox.getNightTexture();
			texture2 = skybox.getNightTexture();
			blendFactor = (time - 0)/(5000 - 0);
		}else if(time >= 5000 && time < 8000){
			texture1 = skybox.getNightTexture();
			texture2 = skybox.getTexture();
			blendFactor = (time - 5000)/(8000 - 5000);
		}else if(time >= 8000 && time < 21000){
			texture1 = skybox.getTexture();
			texture2 = skybox.getTexture();
			blendFactor = (time - 8000)/(21000 - 8000);
		}else{
			texture1 = skybox.getTexture();
			texture2 = skybox.getNightTexture();
			blendFactor = (time - 21000)/(24000 - 21000);
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}
	
	
	

}
