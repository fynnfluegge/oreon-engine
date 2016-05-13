package engine.buffers;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import engine.core.Util;
import engine.core.Vertex;
import engine.modeling.Mesh;

public class MeshVAO {

	private int vbo;
	private int ibo;
	private int vaoId;
	private int size;
		
	
	public MeshVAO()
	{
		vbo = glGenBuffers();
		ibo = glGenBuffers();
		vaoId = glGenVertexArrays();
		size = 0;
	}
	public void addData(Mesh mesh)
	{
			size = mesh.getIndices().length;
		
			glBindVertexArray(vaoId);
			
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBufferAOS(mesh.getVertices()), GL_STATIC_DRAW);
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(mesh.getIndices()), GL_STATIC_DRAW);
			
			glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.BYTES, 0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, Vertex.BYTES, 12);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, Vertex.BYTES, 24);
			
			glBindVertexArray(0);
	}
	
	
	public void draw()
	{
			glBindVertexArray(vaoId);
			
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
			
			glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			
			glBindVertexArray(0);
	}
}
