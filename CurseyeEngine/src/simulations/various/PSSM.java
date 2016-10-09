package simulations.various;

import engine.main.Simulation;

public class PSSM extends Simulation{

	public void init(){
		super.init();
		CameraFrustum camera = new CameraFrustum();
		scenegraph.addObject(camera);
		scenegraph.addObject(new ShadowFrustum(camera));
	}
}
