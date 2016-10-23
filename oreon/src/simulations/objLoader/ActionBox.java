package simulations.objLoader;

import modules.gui.GUIs.VoidGUI;
import engine.main.Game;


public class ActionBox{

public static void main(String[] args) {
		
		Game game = new Game(800,800,"Actionbox");
		game.setGui(new VoidGUI());
		game.getEngine().createWindow();
		game.getScenegraph().addObject(new ActionBoxModel());
		game.getScenegraph().addObject(new OBJ());
		game.init();
		game.launch();
	}
}
