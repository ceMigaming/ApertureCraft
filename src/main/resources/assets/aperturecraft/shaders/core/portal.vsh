#version 330

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

out vec2 a_texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    a_texCoord0 = UV0;
}