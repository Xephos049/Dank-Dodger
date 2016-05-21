//package cs134_project;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

public class Enemy {

	int x,y;
	GL2 gl;
	int curTankTex;
	int[] spriteSize = new int[2];
	public Enemy(GL2 g) {
		// TODO Auto-generated constructor stub
		gl=g;
		x=500;
		y=0;
		curTankTex = JavaFramework.glTexImageTGAFile(gl, "weed1.tga", spriteSize);
	}
	public void move()
	{
		x--;
	}
	public void draw()
	{
		JavaFramework.glDrawSprite(gl, curTankTex, x, y, spriteSize[0], spriteSize[1],true);
	}
}
