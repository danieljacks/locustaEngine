package environmentMapRenderer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import renderEngine.MasterRenderer;
import scene.Scene;
import shinyEntities.ShinyEntity;
import textures.ModelTexture;

public class EnviroMapRenderer {

	public static void renderEnvironmentMap(Scene scene, ShinyEntity entity, MasterRenderer renderer) {
		
		CubeMapCamera camera = entity.getCamera();
		
		//create fbo
		int fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		//attach depth buffer
		int depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, entity.getSceneBoxSize(), entity.getSceneBoxSize());
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);

		//indicate that we want to render to the entire face
		GL11.glViewport(0, 0, entity.getSceneBoxSize(), entity.getSceneBoxSize());

		//loop faces
		for (int i = 0; i < 6; i++) {

			//attach face to fbo as color attachment 0
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
					GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, entity.getSceneBox().getID(), 0);
			
			//point camera in the right direction
			camera.switchToFace(i);
			
			//render scene to fbo, and therefore to the current face of the cubemap
			renderer.renderLowQualityScene(scene, camera);

		}
		
		//stop rendering to fbo
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		//delete fbo TODO: delete this if rendering every frame
//		GL30.glDeleteRenderbuffers(depthBuffer);
//		GL30.glDeleteFramebuffers(fbo);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, entity.getSceneBox().getID());
		GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);

	}

}
