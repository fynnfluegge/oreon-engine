package modules.lighting;

import engine.core.Window;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.scenegraph.components.Component;
import modules.query.OcclusionQuery;

public class Light extends Component{
	
	protected Vec3f color;
	protected float intensity;
	private OcclusionQuery occlusionQuery;
	
	public Light(Vec3f color, float intensity)
	{
		this.color = color;
		this.intensity = intensity;
		occlusionQuery = new OcclusionQuery();
	}
	
	public Light() {
		occlusionQuery = new OcclusionQuery();
	}
	
	public Vec2f getScreenSpacePosition(){
		
		Quaternion clipSpacePos = getTransform().getModelViewProjectionMatrix().mul(new Quaternion(0,0,0,1));
		Vec3f ndcSpacePos = new Vec3f(clipSpacePos.getX()/clipSpacePos.getW(),clipSpacePos.getY()/clipSpacePos.getW(),clipSpacePos.getZ()/clipSpacePos.getW());
		
		if (ndcSpacePos.getX() < -1 || ndcSpacePos.getX() > 1 || ndcSpacePos.getY() < -1 || ndcSpacePos.getY() > 1){
			return null;
		}
		
		Vec2f windowSpacePos = (new Vec2f(ndcSpacePos.getX(), ndcSpacePos.getY()).add(1.0f)).div(2.0f).mul(new Vec2f(Window.getInstance().getWidth(),Window.getInstance().getHeight()));
		
		return windowSpacePos;
	}
	
	public void update(){
		super.update();
	}
	
	public void occlusionQuery(){
		occlusionQuery.doQuery(this);
	}
	
	public Vec3f getColor() {
		return color;
	}
	public void setColor(Vec3f color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public OcclusionQuery getOcclusionQuery() {
		return occlusionQuery;
	}

	public void setOcclusionQuery(OcclusionQuery occlusionQuery) {
		this.occlusionQuery = occlusionQuery;
	}
}