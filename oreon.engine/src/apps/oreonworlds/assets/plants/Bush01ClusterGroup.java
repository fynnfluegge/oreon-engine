package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.BushShader;
import apps.oreonworlds.shaders.plants.BushShadowShader;
import engine.buffers.MeshVAO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Bush01ClusterGroup extends InstancingObject{

	public Bush01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Bush_01","Bush_01.obj","Bush_01.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.1f), BushShader.getInstance(), BushShadowShader.getInstance()));

			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Bush01Cluster(12,new Vec3f(1218,0,-503),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(925,0,-1022),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(861,0,-1035),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(797,0,-1048),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1147,0,-469),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(1224,0,-279),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1303,0,-191),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1066,0,-1120),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1266,0,-1146),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1549,0,-1264),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1867,0,-1190),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1773,0,-1089),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1117,0,-737),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1162,0,-739),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1129,0,-697),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(976,0,-348),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(894,0,-94),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(836,0,-92),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(807,0,-195),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(755,0,208),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(930,0,293),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(920,0,231),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(611,0,997),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(555,0,1231),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(965,0,546),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(900,0,649),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(561,0,1406),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1181,0,-646),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(561,0,1406),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(988,0,-53),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1297,0,484),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(757,0,752),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(678,0,834),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(1607,0,-1256),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(1306,0,-1172),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(962,0,-1088),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(1093,0,-423),getObjectData()));
		addChild(new Bush01Cluster(20,new Vec3f(885,0,89),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(816,0,131),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(763,0,155),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(645,0,1010),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(438,0,1330),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(1728,0,-1214),getObjectData()));
		addChild(new Bush01Cluster(12,new Vec3f(992,0,-976),getObjectData()));
	}
}
