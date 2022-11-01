#version 150

uniform sampler2D DiffuseSampler;

uniform vec4 ColorModulator;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    //TODO use a more modern aa alg (smaa?)
    //TODO make aa configuarable
    vec4 color = texture(DiffuseSampler, texCoord) * vertexColor;
    vec4 mcColor = color * ColorModulator;

    // we are rendering to an SRGB texture so we can leave the colors in the SRGB color space
    fragColor = mcColor;
}