package apps.samples.objLoader;

import engine.core.Game;
import modules.gui.GUIs.VoidGUI;


public class Main{

public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new VoidGUI());
		game.getEngine().createWindow(1280,720,"Actionbox");
		game.init();
//		game.getScenegraph().addObject(new Tree01Instanced(new Vec3f(1060,0,-830),
//				 Constants.Tree0101highPolyModelMatricesBinding,
//				 Constants.Tree0101highPolyWorldMatricesBinding));
		game.launch();
	}
}
