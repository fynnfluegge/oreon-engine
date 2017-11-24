package org.oreon.core.gl.texture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL30.GL_R32F;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class ImageLoader {
	
	public static int[] loadImage(String file) {
		
		ByteBuffer imageBuffer;
        try {
            imageBuffer = ioResourceToByteBuffer(file, 128 * 128);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        IntBuffer w    = BufferUtils.createIntBuffer(1);
        IntBuffer h    = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        // Use info to read image metadata without decoding the entire image.
        if (!stbi_info_from_memory(imageBuffer, w, h, c)) {
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        }
  
//        System.out.println("Image width: " + w.get(0));
//        System.out.println("Image height: " + h.get(0));
//        System.out.println("Image components: " + c.get(0));
//        System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

        // Decode the image
        ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }
        
        int width = w.get(0);
        int height = h.get(0);
        int comp = c.get(0);
        
        int texId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texId);
        
        if (comp == 3) {
            if ((width & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
        } else {
        	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        }
        
        stbi_image_free(image);
        
        int[] data = {texId, w.get(), h.get()};
        
		return data;
	}
	
	// TODO
	public static int[] loadHeightmap(String file) {
		
		ByteBuffer imageBuffer;
        try {
            imageBuffer = ioResourceToByteBuffer(file, 128 * 128);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        IntBuffer w    = BufferUtils.createIntBuffer(1);
        IntBuffer h    = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        // Use info to read image metadata without decoding the entire image.
        if (!stbi_info_from_memory(imageBuffer, w, h, c)) {
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        }
  
        System.out.println("Image width: " + w.get(0));
        System.out.println("Image height: " + h.get(0));
        System.out.println("Image components: " + c.get(0));
        System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

        // Decode the image
//        ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
//        ShortBuffer image = stbi_load_16_from_memory(imageBuffer, h, w, c, 0);
        FloatBuffer image = stbi_loadf_from_memory(imageBuffer, h, w, c, 0);
        
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }
       
        int width = w.get(0);
        int height = h.get(0);
        int comp = c.get(0);
        
        int texId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texId);
        
        if (comp == 3) {
            if ((width & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, image);
        } else if (comp == 1){
        	glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, width, height, 0, GL_RED, GL_FLOAT, image);
        }
        
//        stbi_image_free(image);
        
        int[] data = {texId, w.get(), h.get()};
        
		return data;
	}
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                    InputStream source = ImageLoader.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
                ) {
                    buffer = BufferUtils.createByteBuffer(bufferSize);

                    while (true) {
                        int bytes = rbc.read(buffer);
                        if (bytes == -1) {
                            break;
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                        }
                    }
                }
            }

            buffer.flip();
            return buffer;
    }
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
	}
}
