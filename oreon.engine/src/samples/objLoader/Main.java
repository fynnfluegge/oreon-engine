package samples.objLoader;

import modules.gui.GUIs.VoidGUI;
import engine.main.Game;


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
