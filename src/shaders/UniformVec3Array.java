package shaders;

import org.lwjgl.util.vector.Vector3f;

public class UniformVec3Array extends Uniform{
	
	private UniformVec3[] vec3Uniforms;

	public UniformVec3Array(String name, int size) {
		super(name);
		vec3Uniforms = new UniformVec3[size];
		for(int i=0;i<size;i++){
			vec3Uniforms[i] = new UniformVec3(name + "["+i+"]");
		}
	}
	
	@Override
	protected void storeUniformLocation(int programID) {
		for(UniformVec3 vec3Uniform : vec3Uniforms){
			vec3Uniform.storeUniformLocation(programID);
		}
	}

	public void loadVectorArray(Vector3f[] vectors){
		for(int i=0;i<vectors.length;i++){
			vec3Uniforms[i].loadVec3(vectors[i]);
		}
	}

}
