//package cs134_project;

//import javax.media.nativewindow.WindowClosingProtocol;
import com.jogamp.nativewindow.WindowClosingProtocol;
//import javax.media.opengl.*;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;

import java.util.Random;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class JavaFramework {

    // Set this to true to force the game to exit.
    private static boolean shouldExit;

    // The previous frame's keyboard state.
    private static boolean kbPrevState[] = new boolean[256];

    // The current frame's keyboard state.
    private static boolean kbState[] = new boolean[256];

    // Position of the sprite.
    private static int[] spritePos = new int[]{320, 240};

    // Texture for the sprite.
    private static int spriteTex, weed1, weed2, weed3, bg1, bg2,zero,one,two,three,four,five,six,seven,eight,nine;

    // Size of the sprite.
    private static int[] spriteSize = new int[]{50, 50};
    private static int[] obstaclesSize = new int[]{150, 150};
    private static int[] bgSize = new int[]{1917, 480};
    private static int obsspeed = 1;
    private static int limit = 50;
//    spriteSize[0] = 50;
//    spriteSize[1] = 50;
        public static long initTime=0;
    //variables for camera moving
    private static int cam = 1000;//initial value for cam=1sec=1000ms
    private static final int roundtime = 60000;//change speed of cam every 60sec=60,000ms
    private static byte firstframecheck = 0;
    private static long frametime;
    private static long firstframe, secondframe, beginstage = 0, endstage = 60000, lastcammove, endmove, currtime;
    static int height = 480;
    static int width = 640;
    static int[] difficulty = new int[]{2000, 1500, 1000, 500, 100};
        
    public static int[] numSize= new int[]{0,0};

    //judge if is game over
    public static boolean isGameOver = false;
    public static GameOver gameOver;

    public static void main(String[] args) {
        int bgx1 = 0, bgx2 = 1917;
        ArrayList<Integer> obstacles = new ArrayList<>();
        ArrayList<int[]> obstaclepos = new ArrayList<>();
        int difficultylvl = 0;
        int counter = 0;
        int gcounter = 0;
        GLProfile gl2Profile;

        try {
            // Make sure we have a recent version of OpenGL
            gl2Profile = GLProfile.get(GLProfile.GL2);
        } catch (GLException ex) {
            System.out.println("OpenGL max supported version is too low.");
            System.exit(1);
            return;
        }

        // Create the window and OpenGL context.
        GLWindow window = GLWindow.create(new GLCapabilities(gl2Profile));
        window.setSize(640, 480);
        window.setTitle("Dank Dodger");
        window.setVisible(true);
        window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.isAutoRepeat()) {
                    return;
                }
                kbState[keyEvent.getKeyCode()] = true;
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.isAutoRepeat()) {
                    return;
                }
                kbState[keyEvent.getKeyCode()] = false;
            }
        });

        // Setup OpenGL state.
        window.getContext().makeCurrent();
        GL2 gl = window.getGL().getGL2();
        gl.glViewport(0, 0, 640, 480);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, 640, 480, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Load the texture.
        spriteTex = glTexImageTGAFile(gl, "swagship.tga", obstaclesSize);
        weed1 = glTexImageTGAFile(gl, "weed1.tga", obstaclesSize);
        weed2 = glTexImageTGAFile(gl, "weed2.tga", obstaclesSize);
        weed3 = glTexImageTGAFile(gl, "weed3.tga", obstaclesSize);
        bg1 = glTexImageTGAFile(gl, "bg.tga", obstaclesSize);
        bg2 = glTexImageTGAFile(gl, "bg.tga", obstaclesSize);
        boolean left_or_right = true;//true for facing right, false for facing left

        //initiate cam
        Camera camera = new Camera();
        gameOver=new GameOver(gl);
        initTime=System.nanoTime();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////// The game loop
        while (!shouldExit) {
            System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);

            // Actually, this runs the entire OS message pump.
            window.display();
            
            if (!window.isVisible()) {
                shouldExit = true;
                break;
            }

            // Game logic.
            if (!isGameOver)
            {
                currtime=(System.nanoTime()-initTime)/1000000000;
                if (kbState[KeyEvent.VK_ESCAPE]) {
                    shouldExit = true;
                }

                if (kbState[KeyEvent.VK_A] && spritePos[0] > 0) {
                    left_or_right = false;
                    spritePos[0] -= 10;
                }

                if (kbState[KeyEvent.VK_D] && spritePos[0] < (640 - spriteSize[0])) {
                    left_or_right = true;
                    spritePos[0] += 10;
                }

                if (kbState[KeyEvent.VK_W] && spritePos[1] > 0) {
                    spritePos[1] -= 10;
                }

                if (kbState[KeyEvent.VK_S] && spritePos[1] < (480 - spriteSize[1])) {
                    spritePos[1] += 10;
                }

                //if currtime>endstage, increase both beginstage and endstage by 60,000ms and divide cam by half
                if (currtime > endstage) {
                    beginstage += 60000;
                    endstage += 60000;
                    cam = cam / 2;
                }

                //if currtime-lastcammove >cam = time to move the camera, xmin and +1, update lastcamemove=currtime
                if ((currtime - lastcammove) > cam) {
                    camera.xmin++;
                    camera.xmax++;
                    if (spritePos[0] > 0) {
                        spritePos[0] -= 1;
                    }
                    lastcammove = currtime;
                }
                gl.glClearColor(0, 0, 0, 1);
                gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

    ///////////////////////////////////////////////////////////update sprites and locations ///////////////////////////////////////////////////////////
                counter++;
                gcounter++;

    //            TODO draw background
                if (bgx1 < -1920) {
                    bgx1 = bgx2 + 1917;
                } else if (bgx2 < -1920) {
                    bgx2 = bgx1 + 1917;
                }
            }
            glDrawSprite(gl, bg2, bgx2--, 0, bgSize[0], bgSize[1], true);
            glDrawSprite(gl, bg1, bgx1--, 0, bgSize[0], bgSize[1], true);
            

//            draw obstacles
            if (!isGameOver)
                updateObstacles(gl, obstacles, obstaclepos, left_or_right, counter,gcounter, difficultylvl);

            
            if ((counter > limit) & (!isGameOver)){
                counter = 0;
            }
            if (!isGameOver)
                glDrawSprite(gl, spriteTex, spritePos[0], spritePos[1], spriteSize[0], spriteSize[1], left_or_right);
            
            //draw time elapsed

            drawtime(currtime,gl);
            if(isGameOver)
             	gameOver.draw();
            // Present to the player.
            //window.swapBuffers();
        }
        System.exit(0);
    }

    public static void updateObstacles(GL2 gl, ArrayList<Integer> obstacles, ArrayList<int[]> obstaclepos, boolean lor, int counter,int gcounter, int difficultylvl) {
//    	add obstacle
//        if (counter == difficulty[difficultylvl]) {
            if (counter == limit){
//    		rand gen to choose one of the 3 weed icons
            Random random = new Random();
            int randweed = random.nextInt(3);
            switch (randweed) {
                case 0:
                    obstacles.add(weed1);
                    break;
                case 1:
                    obstacles.add(weed2);
                    break;
                case 2:
                    obstacles.add(weed3);
            }
            int randy = random.nextInt(780) - 300; // TODO give it random val from -300 to 480 unless you change the size of the weed icons. 
            int[] pos = new int[]{640, randy};

            obstaclepos.add(pos);
        }
            if(gcounter % 100 == 0) {
//                obsspeed+= 2;
                  obsspeed++;
                  if (limit - 2 >= 2)
                    limit-=2;
            }

//    	increase difficulty
//        if (counter % 1000 == 0) {
//            counter = 0;
//            if (difficultylvl != 5) {
//                difficultylvl++;
//            }
//        }

        for (int i = 0; i < obstaclepos.size(); i++) {
            if (obstaclepos.get(i)[0] <= -300) {
                // remove that obstacle
                obstacles.remove(i);
                obstaclepos.remove(i);
            } else {
                obstaclepos.get(i)[0]-= obsspeed;
            }
            if(inside(obstaclepos.get(i)[0],obstaclepos.get(i)[1],spritePos[0],spritePos[0]+spriteSize[0],spritePos[1],spritePos[1]+spriteSize[1])
                    ||inside(obstaclepos.get(i)[0]+spriteSize[0],obstaclepos.get(i)[1],spritePos[0],spritePos[0]+spriteSize[0],spritePos[1],spritePos[1]+spriteSize[1])
                 	||inside(obstaclepos.get(i)[0],obstaclepos.get(i)[1]+spriteSize[1],spritePos[0],spritePos[0]+spriteSize[0],spritePos[1],spritePos[1]+spriteSize[1])
                 	||inside(obstaclepos.get(i)[0]+spriteSize[0],obstaclepos.get(i)[1]+spriteSize[1],spritePos[0],spritePos[0]+spriteSize[0],spritePos[1],spritePos[1]+spriteSize[1]))
                 {
                 	isGameOver=true;
                 }
        }

        for (int i = 0; i < obstacles.size(); i++) {
            glDrawSprite(gl, obstacles.get(i), obstaclepos.get(i)[0], obstaclepos.get(i)[1], spriteSize[0], spriteSize[1], true);
        }
    }
    static boolean inside(int x , int y,int fromx,int tox,int fromy ,int toy)
    {
    	if(x>=fromx&&x<=tox&&y>=fromy&&y<=toy)
    		return true;
    	return false;
    }

    // Load a file into an OpenGL texture and return that texture.
    public static int glTexImageTGAFile(GL2 gl, String filename, int[] out_size) {
        final int BPP = 4;

        DataInputStream file = null;
        try {
            // Open the file.
            file = new DataInputStream(new FileInputStream(filename));
        } catch (FileNotFoundException ex) {
            System.err.format("File: %s -- Could not open for reading.", filename);
            return 0;
        }

        try {
            // Skip first two bytes of data we don't need.
            file.skipBytes(2);

            // Read in the image type.  For our purposes the image type
            // should be either a 2 or a 3.
            int imageTypeCode = file.readByte();
            if (imageTypeCode != 2 && imageTypeCode != 3) {
                file.close();
                System.err.format("File: %s -- Unsupported TGA type: %d", filename, imageTypeCode);
                return 0;
            }

            // Skip 9 bytes of data we don't need.
            file.skipBytes(9);

            int imageWidth = Short.reverseBytes(file.readShort());
            int imageHeight = Short.reverseBytes(file.readShort());
            int bitCount = file.readByte();
            file.skipBytes(1);

            // Allocate space for the image data and read it in.
            byte[] bytes = new byte[imageWidth * imageHeight * BPP];

            // Read in data.
            if (bitCount == 32) {
                for (int it = 0; it < imageWidth * imageHeight; ++it) {
                    bytes[it * BPP + 0] = file.readByte();
                    bytes[it * BPP + 1] = file.readByte();
                    bytes[it * BPP + 2] = file.readByte();
                    bytes[it * BPP + 3] = file.readByte();
                }
            } else {
                for (int it = 0; it < imageWidth * imageHeight; ++it) {
                    bytes[it * BPP + 0] = file.readByte();
                    bytes[it * BPP + 1] = file.readByte();
                    bytes[it * BPP + 2] = file.readByte();
                    bytes[it * BPP + 3] = -1;
                }
            }

            file.close();

            // Load into OpenGL
            int[] texArray = new int[1];
            gl.glGenTextures(1, texArray, 0);
            int tex = texArray[0];
            gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
            gl.glTexImage2D(
                    GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth, imageHeight, 0,
                    GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes));
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

            out_size[0] = imageWidth;
            out_size[1] = imageHeight;
            return tex;
        } catch (IOException ex) {
            System.err.format("File: %s -- Unexpected end of file.", filename);
            return 0;
        }
    }

    public static void glDrawSprite(GL2 gl, int tex, int x, int y, int w, int h, boolean left_or_right) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
        gl.glBegin(GL2.GL_QUADS);
        {
            gl.glColor3ub((byte) -1, (byte) -1, (byte) -1);
            if (left_or_right == true) {
                gl.glTexCoord2f(0, 1);
                gl.glVertex2i(x, y);
                gl.glTexCoord2f(1, 1);
                gl.glVertex2i(x + w, y);
                gl.glTexCoord2f(1, 0);
                gl.glVertex2i(x + w, y + h);
                gl.glTexCoord2f(0, 0);
                gl.glVertex2i(x, y + h);
            } else {
                gl.glTexCoord2f(1, 1);
                gl.glVertex2i(x, y);
                gl.glTexCoord2f(0, 1);
                gl.glVertex2i(x + w, y);
                gl.glTexCoord2f(0, 0);
                gl.glVertex2i(x + w, y + h);
                gl.glTexCoord2f(1, 0);
                gl.glVertex2i(x, y + h);
            }
        }
        gl.glEnd();
    }
    public static void drawtime(long time,GL2 gl)
    {
        //load 0-9 digits
        zero=glTexImageTGAFile(gl, "0.tga", numSize);
        one=glTexImageTGAFile(gl, "1.tga", numSize);
        two=glTexImageTGAFile(gl, "2.tga", numSize);
        three=glTexImageTGAFile(gl, "3.tga", numSize);
        four=glTexImageTGAFile(gl, "4.tga", numSize);
        five=glTexImageTGAFile(gl, "5.tga", numSize);
        six=glTexImageTGAFile(gl, "6.tga", numSize);
        seven=glTexImageTGAFile(gl, "7.tga", numSize);
        eight=glTexImageTGAFile(gl, "8.tga", numSize);
        nine=glTexImageTGAFile(gl, "9.tga", numSize);
        
        String strTime=Long.toString(time);
        char chr;
        int currX=2;
        for (int i=0;i<strTime.length();i++)
        {
            chr=strTime.charAt(i);
            currX+=numSize[0];
            switch (chr){
                case '0':
                    glDrawSprite(gl, zero, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '1':
                    glDrawSprite(gl, one, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '2':
                    glDrawSprite(gl, two, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '3':
                    glDrawSprite(gl, three, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '4':
                    glDrawSprite(gl, four, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '5':
                    glDrawSprite(gl, five, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '6':
                    glDrawSprite(gl, six, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '7':
                    glDrawSprite(gl, seven, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '8':
                    glDrawSprite(gl, eight, currX, numSize[1]+2, numSize[0], numSize[1], true);
                    break;
                case '9':
                    glDrawSprite(gl, nine, currX, numSize[1]+2, numSize[0], numSize[1], true);
                     break;
                default: break;
            }
                
        }
    }
}
