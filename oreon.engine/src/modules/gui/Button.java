package modules.gui;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.configs.AlphaBlending;
import engine.core.Input;
import engine.geometry.Mesh;
import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.scenegraph.components.Transform;
import engine.shader.gui.GuiShader;
import engine.textures.Texture2D;

public abstract class Button extends GUIElement{

	protected Texture2D buttonMap;
	protected Texture2D buttonClickMap;
	private boolean onClick = false;
	private Vec2f[] pos;
	
	public Button(){
		pos = new Vec2f[4];
		setOrthoTransform(new Transform());
	}
	
	public void init()
	{
		setShader(GuiShader.getInstance());
		setVao(new GUIVAO());
		setConfig(new AlphaBlending(0.0f));
		Mesh buttonMesh = GUIObjectLoader.load("button.gui");
		getVao().addData(buttonMesh);
		getVao().update(texCoords);
		setOrthographicMatrix(new Matrix4f().Orthographic2D());
		setOrthographicMatrix(getOrthographicMatrix().mul(getOrthoTransform().getWorldMatrix()));
		Quaternion q0 = new Quaternion(0,0,0,0);
		Quaternion q1 = new Quaternion(0,0,0,0);
		Quaternion q2 = new Quaternion(0,0,0,0);
		Quaternion q3 = new Quaternion(0,0,0,0);
		q0 = getOrthoTransform().getWorldMatrix().mul(new Quaternion(buttonMesh.getVertices()[0].getPos(),1));
		q1 = getOrthoTransform().getWorldMatrix().mul(new Quaternion(buttonMesh.getVertices()[1].getPos(),1));
		q2 = getOrthoTransform().getWorldMatrix().mul(new Quaternion(buttonMesh.getVertices()[2].getPos(),1));
		q3 = getOrthoTransform().getWorldMatrix().mul(new Quaternion(buttonMesh.getVertices()[3].getPos(),1));
		pos[0] = new Vec2f(q0.getX(),q0.getY()+5);
		pos[1] = new Vec2f(q1.getX(),q1.getY()+5);
		pos[2] = new Vec2f(q2.getX()-7,q2.getY()-5);
		pos[3] = new Vec2f(q3.getX()-7,q3.getY()-5);
	}
	
	public void render()
	{
		getConfig().enable();
		getShader().bind();
		getShader().updateUniforms(getOrthographicMatrix());
		glActiveTexture(GL_TEXTURE1);
		if (onClick)
			buttonClickMap.bind();
		else
			buttonMap.bind();
		getShader().updateUniforms(1);
		getVao().draw();
		getConfig().disable();
	}
	
	public void update()
	{
		if(Input.isButtonDown(0))
		{
			if(onClick())
			{
				onClick = true;
				onClickActionPerformed();
			}
		}
		if(Input.isButtonreleased(0))
			onClick = false;
	}
	
	public boolean onClick()
	{
		Vec2f mousePos = Input.getMousePos(); 
		
		if(pos[0].getX() < mousePos.getX() && pos[1].getX() < mousePos.getX() && pos[2].getX() > mousePos.getX() && pos[3].getX() > mousePos.getX()
			&& pos[0].getY() < mousePos.getY() && pos[3].getY() < mousePos.getY() && pos[1].getY() > mousePos.getY() && pos[2].getY() > mousePos.getY())
			return true;
		else
			return false;
	}
	
	public void onClickActionPerformed(){}
}
