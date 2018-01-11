package entities;

public class Fog {
	private float density;
	private float gradient;
	
	public Fog(){
		this.density = 0.0f;
		this.gradient = 0.0f;
	}
	
	public Fog(float density, float gradient){
		this.density = density;
		this.gradient = gradient;
	}
	
	public float getDensity() {
		return density;
	}
	public void setDensity(float density) {
		this.density = density;
	}
	public float getGradient() {
		return gradient;
	}
	public void setGradient(float gradient) {
		this.gradient = gradient;
	}
}
