package engine.core;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import engine.math.Vec2f;

public class Input {
	
	private static Input instance = null;

	private ArrayList<Integer> pushedKeys = new ArrayList<Integer>();
	private ArrayList<Integer> releasedKeys = new ArrayList<Integer>();
	
	private ArrayList<Integer> pushedButtons = new ArrayList<Integer>();
	private ArrayList<Integer> releasedButtons = new ArrayList<Integer>();
	
	private Vec2f cursorPosition;
	private Vec2f lockedCursorPosition;
	
	private boolean pause = false;
	
	@SuppressWarnings("unused")
	private GLFWKeyCallback keyCallback;
	 
	@SuppressWarnings("unused")
	private GLFWCursorPosCallback cursorPosCallback;
	
	@SuppressWarnings("unused")
	private GLFWMouseButtonCallback mouseButtonCallback;
	
	public static Input getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Input();
	    }
	      return instance;
	}
	
	protected Input()
	{
		cursorPosition = new Vec2f();
		
		glfwSetKeyCallback(Window.getInstance().getWindow(), (keyCallback = new GLFWKeyCallback() {

            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
            	if (action == GLFW_PRESS){
            		if (!pushedKeys.contains(key)){
            			pushedKeys.add(key);
            		}
                }
            	
                if (action == GLFW_RELEASE){
                	pushedKeys.remove(new Integer(key));
                }
            }
        }));
		
		glfwSetMouseButtonCallback(Window.getInstance().getWindow(), (mouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                if(button == 2 && action == GLFW_PRESS) {
                	lockedCursorPosition = new Vec2f(cursorPosition);
                	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                }

                if(button == 2 && action == GLFW_RELEASE) {
                	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                }
                
                if (action == GLFW_PRESS){
                	if (!pushedButtons.contains(button)){
                		pushedButtons.add(button);
                	}
                }
                
                if (action == GLFW_RELEASE){
                	pushedButtons.remove(new Integer(button));
                }
            }
		}));
		
		glfwSetCursorPosCallback(Window.getInstance().getWindow(), (cursorPosCallback = new GLFWCursorPosCallback() {

            @Override
            public void invoke(long window, double xpos, double ypos) {
            	cursorPosition.setX((float) xpos);
            	cursorPosition.setY((float) ypos);
            }

		}));
	}
	
	public boolean isKeyPushed(int key)
	{
		return pushedKeys.contains(key);
	}
	
	public boolean isKeyReleased(int key)
	{
		return releasedKeys.contains(key);
	}
	
	public boolean isButtonPushed(int key)
	{
		return pushedButtons.contains(key);
	}
	
	public boolean isButtonreleased(int key)
	{
		return releasedButtons.contains(key);
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
	public Vec2f getCursorPosition() {
		return cursorPosition;
	}

	public void setCursorPosition(Vec2f cursorPosition) {
		this.cursorPosition = cursorPosition;
		
		glfwSetCursorPos(Window.getInstance().getWindow(),
				cursorPosition.getX(),
				cursorPosition.getY());
	}

	public Vec2f getLockedCursorPosition() {
		return lockedCursorPosition;
	}

	public void setLockedCursorPosition(Vec2f lockedCursorPosition) {
		this.lockedCursorPosition = lockedCursorPosition;
	}
	
	public ArrayList<Integer> getPushedKeys() {
		return pushedKeys;
	}

	public void setPushedKeys(ArrayList<Integer> pushedKeys) {
		this.pushedKeys = pushedKeys;
	}
}
