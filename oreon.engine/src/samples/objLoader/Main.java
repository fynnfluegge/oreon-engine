package samples.objLoader;

import engine.core.Game;
import modules.gui.GUIs.VoidGUI;


public class Main{

public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new VoidGUI());
		game.getEngine().createWindow(800,800,"Actionbox");
		game.init();
		game.getScenegraph().addObject(new OBJ());
		game.launch();
	}
}
