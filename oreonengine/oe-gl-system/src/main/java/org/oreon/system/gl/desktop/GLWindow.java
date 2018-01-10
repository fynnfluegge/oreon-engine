package org.oreon.system.gl.desktop;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

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
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.oreon.core.gl.texture.ImageLoader;
import org.oreon.core.system.Window;

public class GLWindow extends Window{

	GLCapabilities capabilities;
	
	public void create()
	{
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);	
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);	
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);	
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);	
		
		setId(glfwCreateWindow(getWidth(), getHeight(), "OE3", 0, 0));
		
		if(getId() == 0) {
		    throw new RuntimeException("Failed to create window");
		}
		
		ByteBuffer bufferedImage = loadImageToByteBuffer("textures/logo/oreon_lwjgl_icon32.png");
		
		GLFWImage image = GLFWImage.malloc();
		
		image.set(32, 32, bufferedImage);
		
		GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);
		
		glfwSetWindowIcon(getId(), images);
		
		glfwMakeContextCurrent(getId());
		glfwSwapInterval(0);
		glfwShowWindow(getId());
		capabilities = GL.createCapabilities();
	}
	
	public void draw()
	{
		glfwSwapBuffers(getId());
	}
	
	public void shutdown()
	{
		glfwDestroyWindow(getId());
	}
	
	public boolean isCloseRequested()
	{
		return glfwWindowShouldClose(getId());
	}
	
	public void resize(int width, int height) {
		glfwSetWindowSize(getId(), width, height);
		setHeight(height);
		setWidth(width);
//		CoreSystem.getInstance().getScenegraph().getCamera().setProjection(70, width, height);
	}
	
	public ByteBuffer loadImageToByteBuffer(String file){
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
