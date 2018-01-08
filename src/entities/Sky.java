package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import skybox.Skybox;
import sunRenderer.Sun;

public class Sky {
	private Vector3f colour = new Vector3f(0.0f, 0.0f, 0.0f);
	private Skybox skybox;
	private List<Sun> suns = new ArrayList<Sun>();
	
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
	public List<Sun> getSuns() {
		return suns;
	}
	public void setSuns(List<Sun> suns) {
		this.suns = suns;
	}
	
	public void addSun(Sun sun){
		suns.add(sun);
	}
	public void removeSun(Sun sun){
		suns.remove(sun);
	}
}
