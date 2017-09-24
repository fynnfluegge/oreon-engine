package org.oreon.core.model;

import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

public class AssimpModelLoader {
	
	public static void loadModel(String path) {
		AIScene aiScene = Assimp.aiImportFile(path, 0);
	}
	
	public static void main(String[] args) {
		
	}
}
