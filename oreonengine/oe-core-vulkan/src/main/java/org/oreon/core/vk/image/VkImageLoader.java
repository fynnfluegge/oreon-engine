package org.oreon.core.vk.image;

import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.oreon.core.image.ImageMetaData;

public class VkImageLoader {
	
	public static ImageMetaData getImageMetaData(String file){
		
		ByteBuffer imageBuffer;
        try {
            imageBuffer = ioResourceToByteBuffer(file, 128 * 128);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

	    IntBuffer x = BufferUtils.createIntBuffer(1);
	    IntBuffer y = BufferUtils.createIntBuffer(1);
	    IntBuffer channels = BufferUtils.createIntBuffer(1);
	    
	    // Use info to read image metadata without decoding the entire image.
        if (!stbi_info_from_memory(imageBuffer, x, y, channels)) {
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        }
	    
	    return new ImageMetaData(x.get(0), y.get(0), channels.get(0));
	}

	public static ByteBuffer decodeImage(String file){
		
		String absolutePath = VkImageLoader.class.getClassLoader().getResource(file).getPath().substring(1);

	    IntBuffer x = BufferUtils.createIntBuffer(1);
	    IntBuffer y = BufferUtils.createIntBuffer(1);
	    IntBuffer channels = BufferUtils.createIntBuffer(1);
	    
	    ByteBuffer image = stbi_load(absolutePath, x, y, channels, STBI_rgb_alpha);
	    
	    return image;
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
                    InputStream source = VkImageLoader.class.getClassLoader().getResourceAsStream(resource);
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
