#version 330

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoord;
// layout(location = 1) in vec3 normal;

out vec4 color;
out vec2 a_texCoord0;

void main() {
    gl_Position = ModelViewMat * ProjMat * vec4(pos, 1);
    a_texCoord0 = texCoord;
}
