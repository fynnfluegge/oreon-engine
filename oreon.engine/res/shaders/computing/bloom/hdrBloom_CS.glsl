#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D brightColorSampler;

layout (binding = 1, rgba16f) uniform writeonly image2D bloomSceneSampler;