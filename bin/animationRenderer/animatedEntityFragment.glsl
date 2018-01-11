#version 150

const vec2 lightBias = vec2(0.7, 0.6);//just indicates the balance between diffuse and ambient lighting

in vec2 pass_textureCoords;
in vec3 pass_normal;
in float visibility;

out vec4 out_colour;

uniform sampler2D diffuseMap;
uniform vec3 lightDirection;
uniform vec3 skyColour;

void main(void){
	
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);		
	vec3 unitNormal = normalize(pass_normal);
	float diffuseLight = max(dot(-lightDirection, unitNormal), 0.0) * lightBias.x + lightBias.y;
	out_colour = diffuseColour * diffuseLight;
	out_colour = mix(vec4(skyColour,1.0),out_colour, visibility);
	//out_colour.rgb = mix(out_colour.rgb, vec3(1.0), skyColour);
	
}