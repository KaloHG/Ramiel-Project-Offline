#version 330

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

in vec3 Position;
in vec2 UV;
in vec4 Color;

out vec2 FragUV;
out vec4 FragColor;

void main()
{
    FragUV = UV;
    FragColor = Color;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}