package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import skybox.Skybox;

public class Sky {
	private Vector3f colour = new Vector3f(0.0f, 0.0f, 0.0f);
	private Skybox skybox;
	
	public Vector3f getColour() {
		return colour;
	}
	public void setColour(Vector3f colour) {
		this.colour = colour;
	}
	public Skybox getSkybox() {
		return skybox;
	}
	public void setSkybox(Skybox skybox) {
		this.skybox = skybox;
	}
}
