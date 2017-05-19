package apps.oreonworlds.assets.plants;

import apps.oreonworlds.shaders.plants.GrassShader;
import engine.buffers.MeshVAO;
import engine.configs.AlphaTestCullFaceDisable;
import engine.core.RenderingEngine;
import engine.math.Vec3f;
import engine.scenegraph.components.RenderInfo;
import modules.instancing.InstancedDataObject;
import modules.instancing.InstancingObject;
import modules.modelLoader.obj.Model;
import modules.modelLoader.obj.OBJLoader;

public class Grass01ClusterGroup extends InstancingObject{

	public Grass01ClusterGroup(){
		
		RenderingEngine.getInstancingThreadHandler().getInstancingObjects().add(this);
		
		Model[] models = new OBJLoader().load("./res/oreonworlds/assets/plants/Grass_01","grassmodel.obj","grassmodel.mtl");
	
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			MeshVAO meshBuffer = new MeshVAO();
			model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			
			meshBuffer.addData(model.getMesh());

			object.setRenderInfo(new RenderInfo(new AlphaTestCullFaceDisable(0.1f), GrassShader.getInstance()));
				
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		addChild(new Grass01Cluster(200,new Vec3f(1098,0,437),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1130,0,485),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1181,0,456),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1174,0,400),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1096,0,380),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1144,0,440),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1070,0,499),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1128,0,406),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1053,0,443),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1092,0,472),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(794,0,-70),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(839,0,-40),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(854,0,11),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(823,0,91),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(900,0,-10),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1168,0,-1150),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1795,0,-1259),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1684,0,-1248),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1431,0,-1190),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1091,0,-1121),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(951,0,-95),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(918,0,-62),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(907,0,-100),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(922,0,-45),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(821,0,53),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1056,0,557),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1014,0,579),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1023,0,498),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(651,0,1162),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(608,0,1141),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(581,0,1131),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(548,0,1115),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(521,0,1158),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(601,0,1244),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(585,0,1201),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(467,0,1214),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(365,0,1495),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(312,0,1552),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(345,0,1631),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(233,0,1587),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(208,0,1662),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1171,0,-1094),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1224,0,-1119),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1078,0,-1075),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1043,0,-1024),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(498,0,1423),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(417,0,-1474),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(227,0,1624),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(142,0,-1652),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(192,0,1588),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(156,0,1664),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(292,0,1623),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(450,0,-1528),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(388,0,-1570),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1789,0,-1215),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1736,0,-1209),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1664,0,-1191),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1592,0,-1245),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1592,0,-1261),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1478,0,-1217),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1482,0,-1244),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1466,0,-1199),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1371,0,-1175),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1334,0,-1164),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1376,0,-1155),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1385,0,-1212),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1779,0,-1168),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1519,0,-1272),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1122,0,-1140),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1039,0,-1081),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(999,0,-1080),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(959,0,-1077),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1196,0,-1120),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1126,0,-1088),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1085,0,-1032),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1038,0,-1062),getObjectData()));
		addChild(new Grass01Cluster(300,new Vec3f(1024,0,-989),getObjectData()));
		addChild(new Grass01Cluster(300,new Vec3f(996,0,-1030),getObjectData()));
		addChild(new Grass01Cluster(300,new Vec3f(934,0,-1019),getObjectData()));
		addChild(new Grass01Cluster(300,new Vec3f(882,0,-1041),getObjectData()));
		addChild(new Grass01Cluster(300,new Vec3f(991,0,-991),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1010,0,-1022),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(991,0,-1001),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1094,0,-736),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1128,0,-738),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1085,0,-725),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1094,0,-736),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1053,0,-710),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1204,0,-748),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1181,0,-743),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(1018,0,-686),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(940,0,-610),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1016,0,-650),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(993,0,-631),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(965,0,-628),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1016,0,-103),getObjectData()));
		addChild(new Grass01Cluster(100,new Vec3f(726,0,220),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(787,0,187),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(799,0,216),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(825,0,195),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(730,0,175),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(719,0,218),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(803,0,229),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(782,0,803),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(772,0,747),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(802,0,729),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(833,0,769),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1196,0,-476),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1248,0,-491),getObjectData()));
		addChild(new Grass01Cluster(200,new Vec3f(1215,0,-504),getObjectData()));
	
//		setThread(new Thread(this));
//		getThread().start();
	}
	
	public void update(){
		super.update();
	}
	
	public void run(){
		super.run();
	}
}
