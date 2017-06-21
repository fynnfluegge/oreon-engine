package org.oreon.engine.apps.samples.ocean;

import org.oreon.engine.apps.samples.ocean.gui.GUI;
import org.oreon.engine.engine.core.Game;
import org.oreon.engine.modules.atmosphere.SkySphere;
import org.oreon.engine.modules.atmosphere.Sun;

public class OceanSimulation {

	public static void main(String[] args) {
		Game game = new Game();
		game.setGui(new GUI());
		game.getEngine().createWindow(1920, 1080, "Ocean");
		game.init();
		game.getScenegraph().addObject(new SkySphere());
		game.getScenegraph().addObject(new Sun());
		game.getScenegraph().setWater(new Ocean());
		game.launch();
	}
}
