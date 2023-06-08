#version 330

uniform vec4 ColorMultiplier;

in vec2 FragUV;
in vec4 FragColor;

out vec4 FragColorOut;

void main()
{
    FragColorOut = FragColor * ColorMultiplier;
}