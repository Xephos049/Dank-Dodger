//package cs134_project;

import com.jogamp.opengl.GL2;

public class GameOver {

	int x,y;
	GL2 gl;
	int curTankTex;
	int[] spriteSize = new int[2];
	public GameOver(GL2 g) {
		// TODO Auto-generated constructor stub
		gl=g;
		x=0;
		y=0;
		curTankTex = JavaFramework.glTexImageTGAFile(gl, "gameover.tga", spriteSize);
	}
	public void draw()
	{
		JavaFramework.glDrawSprite(gl, curTankTex, x, y, spriteSize[0], spriteSize[1],true);
	}
}
