package scene;

public class Shadow {
	private float shadowDistance = 300.0f;
	private float transitionDistance = 10.0f;
	private int quality = 2048;
	private float offset = 100;
	
	public float getShadowDistance() {
		return shadowDistance;
	}
	public void setShadowDistance(float shadowDistance) {
		this.shadowDistance = shadowDistance;
	}
	public float getTransitionDistance() {
		return transitionDistance;
	}
	public void setTransitionDistance(float transitionDistance) {
		this.transitionDistance = transitionDistance;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public float getOffset() {
		return offset;
	}
	public void setOffset(float offset) {
		this.offset = offset;
	}
	
}
