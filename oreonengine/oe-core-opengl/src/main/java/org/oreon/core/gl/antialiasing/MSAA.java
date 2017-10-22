package org.oreon.core.gl.antialiasing;

import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Transform;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.MeshGenerator;

public class MSAA {
	
	private MSAAShader shader;
	private Matrix4f m_Orthographic;
//	private GUIVAO vao;
	
	public MSAA() {
	
		m_Orthographic = new Matrix4f().Orthographic2D();
		Transform orthoTransform = new Transform();
		orthoTransform.setTranslation(0, 0, 0);
		orthoTransform.setScaling(CoreSystem.getInstance().getWindow().getWidth(), CoreSystem.getInstance().getWindow().getHeight(), 0);
		m_Orthographic = m_Orthographic.mul(orthoTransform.getWorldMatrix());
		
//		setVao(new GUIVAO());
//		getVao().addData(MeshGenerator.Quad2D());
	}
	
	public void render() {
		
	}

}
