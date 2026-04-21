#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler2;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

void main() {
   vec4 color = Color;
   vec3 pos = vec3(Position.xyz);
   ivec4 icol = ivec4(round(Color * 255));


    // r = 255, g= lines, b = 55

    if (Color == vec4(170./255., Color.g, 55./255., Color.a)) {
        pos.y += Color.g * 255.0 * 10; // 10px per line
        color.b = 0.;
        color.g = 0.;
    }

   gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

   sphericalVertexDistance = fog_spherical_distance(pos);
   cylindricalVertexDistance = fog_cylindrical_distance(pos);
   vertexColor = color * texelFetch(Sampler2, UV2 / 16, 0);
   texCoord0 = UV0;

}