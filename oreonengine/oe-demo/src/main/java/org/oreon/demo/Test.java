package org.oreon.demo;

import org.oreon.core.math.Vec2f;

public class Test {

	public static void main(String[] args) {
		
		int x = 127;
		int y = 14;
		int[] perm = {1,-1};
		
		System.out.println(new Vec2f(x,y).mul(perm[(x+y) % 1]));
	}

}
