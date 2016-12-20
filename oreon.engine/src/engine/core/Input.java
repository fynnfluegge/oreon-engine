package engine.core;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import engine.math.Vec2f;

public class Input {
	
	private static ArrayList<Integer> pushedKeys = new ArrayList<Integer>();
	private static ArrayList<Integer> releasedKeys = new ArrayList<Integer>();
	private static ArrayList<Integer> keysHolding = new ArrayList<Integer>();
	
	private static ArrayList<Integer> holdButtons = new ArrayList<Integer>();
	private static ArrayList<Integer> releasedButtons = new ArrayList<Integer>();
	
	private static boolean pause = false;
	
	public static void update()
	{
		clear();
		Integer key;
		Integer button;
		boolean hold = false;
		
		while(Keyboard.next())
		{
			key = getKey();
			hold = Keyboard.getEventKeyState();
			if(hold && !pushedKeys.contains(key))
			{	
				pushedKeys.add(key);
				keysHolding.add(key);
			}
			if(!hold)
			{	
				pushedKeys.remove(key);
				keysHolding.remove(key);
				releasedKeys.add(key);
			}
			if (Keyboard.KEY_P == key && !pushedKeys.contains(key))
			{
				if (!pause) pause = true;
				else pause = false;
			}
		}
		
		while(Mouse.next())
		{	
			button = getButton();
			hold = Mouse.getEventButtonState();
			if(hold && !holdButtons.contains(button))
			{	
				holdButtons.add(button);
			}
			if(!hold)
			{	
				holdButtons.remove(button);
				releasedButtons.add(button);
			}
		}
				
	}
	
	public static void clear()
	{
		releasedKeys.clear();
		pushedKeys.clear();
		releasedButtons.clear();
		holdButtons.clear();
	}
	
	public static int getKey()
	{	
		return Keyboard.getEventKey();
	}
	
	public static boolean getKeyDown(int key)
	{
		return pushedKeys.contains(key);
	}
	
	public static boolean getKeyreleased(int key)
	{
		return releasedKeys.contains(key);
	}
	
	public static boolean getHoldingKey(int key)
	{
		return keysHolding.contains(key);
	}
	
	public static ArrayList<Integer> getHoldingKeys()
	{
		return keysHolding;
	}
	
	public static int getButton()
	{	
		return Mouse.getEventButton();
	}
	
	public static boolean isButtonDown(int key)
	{
		return holdButtons.contains(key);
	}
	
	public static boolean isButtonreleased(int key)
	{
		return releasedButtons.contains(key);
	}
	
	public static Vec2f getMousePos(){
		return  new Vec2f(Mouse.getX(), Mouse.getY());
	}
	
	public static void setMousePosition(Vec2f pos)
	{
		Mouse.setCursorPosition((int) pos.getX(), (int) pos.getY());
	}
	
	public static void setCursor(boolean enabled)
	{
		Mouse.setGrabbed(!enabled);
	}

	public static boolean isPause() {
		return pause;
	}

	public static void setPause(boolean pause) {
		Input.pause = pause;
	}
	
	
}