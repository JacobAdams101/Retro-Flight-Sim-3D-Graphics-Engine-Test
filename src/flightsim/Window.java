
package flightsim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author jacob
 */
public class Window extends JPanel
{

    public JFrame window;

    private World worldToDraw;


    public Window(String title, int resolutionWidth, int resoultionHeight)
    {

        window = new JFrame(title); //Set the windows name
        //window.setSize(100, 100); //Set window size
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //window.dispose();
        //window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setUndecorated(true);
        window.add(this); //Add myself to the window as I am a Jframe so what this code here does is it adds the canvas for painting so to speak which allows you to call the paint() method

        window.setVisible(true); //Set the window to be visible
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Stop the program when the window is closed
        window.setResizable(false); //Set my window to be resizable



        IMAGEBUFFER = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(resolutionWidth, resoultionHeight);

        this.fov = 100f;
        this.height = IMAGEBUFFER.getHeight();
        this.width = IMAGEBUFFER.getWidth();
        this.aspectRatio = ((float)height) / ((float)width);
        this.tanReciprocal = 1f / (float)Math.tan((fov*Math.PI)/360f);
        this.Zfar = 1000;
        this.Znear = 1;

        this.halfWidth = this.width/2f;
        this.halfHeight = this.height/2f;

        this.setCursor( this.getToolkit().createCustomCursor(
               new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
               new Point(),
               null ) );
    }

    float fov;
    int height;
    int width;
    float aspectRatio;
    float tanReciprocal;
    float Zfar;
    float Znear;

    float halfWidth;
    float halfHeight;

    boolean drawOutlines = false;

    private final Vec3D ZPLANEPOINT = new Vec3D(0, 0, 0.1f);
    private final Vec3D ZPLANEFARPOINT = new Vec3D(0, 0, Zfar);
    private final Vec3D ZPLANENORMAL = new Vec3D(0, 0, 1f);

    private final Color FADECOLOUR = new Color(125, 125, 200);

    private final Color SKYCOLOR = new Color(Color.CYAN.getRed()+60, Color.CYAN.getGreen(), Color.CYAN.getBlue());

    private final Font FONT = new Font("Serif", Font.BOLD, 20);

    final int CROSSHAIRLENGTH = 7;
    final int CROSSHAIRTHICKNESS = 1;
    
    public boolean drawCrosshair = false;


    private Triangle[] culledTriangles;

    public synchronized void assignObjectToDraw(World world)
    {
        worldToDraw = world;
        culledTriangles = new Triangle[worldToDraw.getMesh().getTriangleCount()];
    }

    @Override
    public synchronized void paint(Graphics g)
    {
        g.drawImage(draw(), 0, 0, this.getWidth(), this.getHeight(), null);
    }
    final BufferedImage IMAGEBUFFER;

    public synchronized BufferedImage draw()
    {
        //Graphics g = IMAGEBUFFER.getGraphics(); //Create a graphics object of the 'result' image to draw to
        Graphics2D g = (Graphics2D) IMAGEBUFFER.getGraphics();
        g.setClip(0, 0, IMAGEBUFFER.getWidth(), IMAGEBUFFER.getHeight());
        float red, green, blue;
        int i, i2;
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        float normRotX, normRotY, normRotZ, x, y, z, X, Y, Z, lightX, lightY, lightZ; //Declare floats
        int nextPosition;
        float brightness, dist;
        if (worldToDraw != null)
        {

            if (worldToDraw.dayLighting != null)
            {
                red = ((float)SKYCOLOR.getRed())*worldToDraw.dayLighting[0];
                green = ((float)SKYCOLOR.getGreen())*worldToDraw.dayLighting[1];
                blue = ((float)SKYCOLOR.getBlue())*worldToDraw.dayLighting[2];

                g.setColor(new Color((int)red, (int)green, (int)blue));
                g.fillRect(0, 0, this.width, this.height);
            }

            nextPosition = 0;

            lightX = (worldToDraw.cam.cosy*worldToDraw.getMesh().LIGHTDIRECTION.X) + (worldToDraw.cam.siny*worldToDraw.getMesh().LIGHTDIRECTION.Z);
            lightY = (worldToDraw.cam.sinx*worldToDraw.cam.siny*worldToDraw.getMesh().LIGHTDIRECTION.X) + (worldToDraw.cam.cosx*worldToDraw.getMesh().LIGHTDIRECTION.Y) - (worldToDraw.cam.sinx*worldToDraw.cam.cosy*worldToDraw.getMesh().LIGHTDIRECTION.Z);
            lightZ = (worldToDraw.cam.cosx*worldToDraw.cam.cosy*worldToDraw.getMesh().LIGHTDIRECTION.Z) + (worldToDraw.cam.sinx*worldToDraw.getMesh().LIGHTDIRECTION.Y) - (worldToDraw.cam.cosx*worldToDraw.cam.siny*worldToDraw.getMesh().LIGHTDIRECTION.X);

            //culledTriangles.LIGHTDIRECTION = new Vec3D(lightX, lightY, lightZ);

            if (worldToDraw.getMesh() != null)
            {
                for (i = 0; i < worldToDraw.getMesh().TRIS.length; i++)
                {
                    Triangle t = worldToDraw.getMesh().TRIS[i];

                    normRotX = (worldToDraw.cam.cosy*t.NORM.X) + (worldToDraw.cam.siny*t.NORM.Z);
                    normRotY = (worldToDraw.cam.sinx*worldToDraw.cam.siny*t.NORM.X) + (worldToDraw.cam.cosx*t.NORM.Y) - (worldToDraw.cam.sinx*worldToDraw.cam.cosy*t.NORM.Z);
                    normRotZ = (worldToDraw.cam.cosx*worldToDraw.cam.cosy*t.NORM.Z) + (worldToDraw.cam.sinx*t.NORM.Y) - (worldToDraw.cam.cosx*worldToDraw.cam.siny*t.NORM.X);
                    /*
                    COMPUTE 1ST POINT (ROTATION, TRANSLATION ETC.)
                    */
                    //Translate x,y,z co-ordiantes so camera is at (0,0,0)
                    x = t.POINTS[0].X - worldToDraw.cam.pos.X;
                    y = t.POINTS[0].Y - worldToDraw.cam.pos.Y;
                    z = t.POINTS[0].Z - worldToDraw.cam.pos.Z;
                    //Rotate world around (0, 0, 0) so camera is pointing in the correct direction
                    X = (worldToDraw.cam.cosy*x) + (worldToDraw.cam.siny*z);
                    Y = (worldToDraw.cam.sinx*worldToDraw.cam.siny*x) + (worldToDraw.cam.cosx*y) - (worldToDraw.cam.sinx*worldToDraw.cam.cosy*z);
                    Z = (worldToDraw.cam.cosx*worldToDraw.cam.cosy*z) + (worldToDraw.cam.sinx*y) - (worldToDraw.cam.cosx*worldToDraw.cam.siny*x);
                    Triangle currentTriangle = new Triangle(X, Y, Z, t.COLOUR, normRotX, normRotY, normRotZ, t.APPLYLIGHTING);
                    if ((normRotX*X)+(normRotY*Y)+(normRotZ*Z) < 0) //Is the triangle facing the right way (OR DOUBLESIDED = true)
                    {

                        /*
                        Already Computed First (0th) Point but now add to x and y points
                        */
                        /*
                        COMPUTE REMAIN POINTS (ROTATION, TRANSLATION ETC.)
                        */
                        for (i2 = 1; i2 < 3; i2++) //Iterate for points of triangle, Already Computed First (0th) Point so no need to iterate i2 = 0
                        {
                            //Translate x,y,z co-ordiantes so camera is at (0,0,0)
                            x = t.POINTS[i2].X - worldToDraw.cam.pos.X;
                            y = t.POINTS[i2].Y - worldToDraw.cam.pos.Y;
                            z = t.POINTS[i2].Z - worldToDraw.cam.pos.Z;
                            //Rotate world around (0, 0, 0) so camera is pointing in the correct direction
                            X = (worldToDraw.cam.cosy*x) + (worldToDraw.cam.siny*z);
                            Y = (worldToDraw.cam.sinx*worldToDraw.cam.siny*x) + (worldToDraw.cam.cosx*y) - (worldToDraw.cam.sinx*worldToDraw.cam.cosy*z);
                            Z = (worldToDraw.cam.cosx*worldToDraw.cam.cosy*z) + (worldToDraw.cam.sinx*y) - (worldToDraw.cam.cosx*worldToDraw.cam.siny*x);
                            currentTriangle.POINTS[i2] = new Vec3D(X, Y, Z); //Add new point to triagnels
                            //currentTriangle.POINTS[i2].X = X;
                            //currentTriangle.POINTS[i2].Y = Y;
                            //currentTriangle.POINTS[i2].Z = Z;
                        }
                        /*
                        Test for clipping
                        */

                        Triangle[] clipped = clipAgainstPlane(ZPLANEPOINT, ZPLANENORMAL, currentTriangle);
                        if (clipped != null)
                        {
                            for (Triangle clippedTriangle : clipped)
                            {
                                culledTriangles[nextPosition] = clippedTriangle;
                                nextPosition++;
                            }
                        }
                        /*
                        culledTriangles[nextPosition] = currentTriangle;
                        nextPosition++;
                        */

                    }
                }
                quickSort(culledTriangles, 0, nextPosition-1);

                final float ATW = aspectRatio*tanReciprocal*halfWidth;
                final float TH = tanReciprocal*halfHeight;
                for (i = 0; i < nextPosition; i++)
                {      
                    Triangle t = culledTriangles[i];
                    for (i2 = 0; i2 < 3; i2++)
                    {
                        xPoints[i2] = (int) (((ATW*t.POINTS[i2].X) / t.POINTS[i2].Z) + halfWidth);
                        yPoints[i2] = (int) (((TH*t.POINTS[i2].Y) / t.POINTS[i2].Z) + halfHeight);
                    }   

                    //red = t.COLOUR.getRed();
                    //green = t.COLOUR.getGreen();
                    //blue = t.COLOUR.getBlue();
                    if (t.APPLYLIGHTING)
                    {
                        brightness =  0.5f + (((t.NORM.X*lightX)+(t.NORM.Y*lightY)+(t.NORM.Z*lightZ)) * -0.5f);
                        dist = t.POINTS[0].Z*0.0015f;
                        dist = dist*dist*dist;
                    }
                    else
                    {
                        brightness = 1;
                        dist = 0;
                    }
                    //brightness = 1;


                    red = ((t.COLOUR.getRed()*brightness)+(FADECOLOUR.getRed()*dist))/(1f+dist);
                    green = ((t.COLOUR.getGreen()*brightness)+(FADECOLOUR.getGreen()*dist))/(1f+dist);
                    blue = ((t.COLOUR.getBlue()*brightness)+(FADECOLOUR.getBlue()*dist))/(1f+dist);

                    //apply time of day lighting

                    red *= worldToDraw.dayLighting[0];
                    green *= worldToDraw.dayLighting[1];
                    blue *= worldToDraw.dayLighting[2];


                    g.setColor(new Color((int)red, (int)green, (int)blue)); //Get the correct colour
                    //g.drawRect(xPoints[0], yPoints[0], 10, 10);
                    g.fillPolygon(xPoints, yPoints, 3);
                    if (drawOutlines)
                    {
                        g.setColor(Color.BLACK);
                        g.drawPolygon(xPoints, yPoints, 3);
                    }
                }
            }

            if(drawCrosshair)
            {
                g.setXORMode(Color.WHITE);
                g.setColor(Color.BLACK);
                g.fillRect((int)halfWidth-CROSSHAIRLENGTH, (int)halfHeight-CROSSHAIRTHICKNESS, CROSSHAIRLENGTH*2, CROSSHAIRTHICKNESS*2);
                g.fillRect((int)halfWidth-CROSSHAIRTHICKNESS, (int)halfHeight-CROSSHAIRLENGTH, CROSSHAIRTHICKNESS*2, CROSSHAIRLENGTH*2);
                g.setPaintMode();
            }

            g.setColor(Color.BLACK);
            g.setFont(FONT);
            g.drawString("PRESS ESCAPE TO EXIT", 15, 15);
            g.drawString("SHIFT/CONTROL - THROTTLE UP/DOWN", 15, 30);
            g.drawString("WASD - MOVE (YAW/PITCH", 15, 45);
            g.drawString("Q/E - SWITCH PERSPECTIVE", 15, 60);
            //g.drawString("FPS: " + worldToDraw.fps, 50, 100);
            if (worldToDraw.plane.isInOverDrive())
            {
                g.setColor(Color.RED);
            }
            g.drawString("THRUST: " + (int)worldToDraw.plane.thrust, IMAGEBUFFER.getWidth()-147, IMAGEBUFFER.getHeight()-50);
            g.setColor(Color.BLACK);
            g.drawString("SPEED: " + (int)worldToDraw.plane.speed, IMAGEBUFFER.getWidth()-130, IMAGEBUFFER.getHeight()-35);
            
            
            g.drawString("AIR DENSITY: " + (int)(worldToDraw.plane.lastFluidDensity*100), IMAGEBUFFER.getWidth()-192, IMAGEBUFFER.getHeight()-15);

            g.drawString("ELEVATION: " + ((int)-worldToDraw.plane.pos.Y), 5, IMAGEBUFFER.getHeight()-15);

            g.drawString("LAT: " + (int)worldToDraw.plane.pos.X, 82, IMAGEBUFFER.getHeight()-50);
            g.drawString("LONG: " + (int)worldToDraw.plane.pos.Z, 63, IMAGEBUFFER.getHeight()-35);
        }
        return IMAGEBUFFER;
    }

    public synchronized static Vec3D intersectPlane(Vec3D planeP, Vec3D planeN, Vec3D lineStart, Vec3D lineEnd)
    {
        //Normalise vectors (Removed as not needed for now)
        //float mag = (float)Math.sqrt((planeN.X*planeN.X)+(planeN.Y*planeN.Y)+(planeN.Z*planeN.Z));
        //planeN = new Vec3D(planeN.X/mag, planeN.Y/mag, planeN.Z/mag);
        float dot = -((planeN.X*planeP.X)+(planeN.Y*planeP.Y)+(planeN.Z*planeP.Z));
        float ad = (lineStart.X*planeN.X)+(lineStart.Y*planeN.Y)+(lineStart.Z*planeN.Z);
        float bd = (lineEnd.X*planeN.X)+(lineEnd.Y*planeN.Y)+(lineEnd.Z*planeN.Z);
        float t = (-dot-ad)/(bd-ad);
        Vec3D lineStartToEnd = new Vec3D(lineEnd.X-lineStart.X, lineEnd.Y-lineStart.Y, lineEnd.Z-lineStart.Z);
        Vec3D lineToIntersect = new Vec3D(lineStartToEnd.X*t, lineStartToEnd.Y*t, lineStartToEnd.Z*t);
        return new Vec3D(lineStart.X+lineToIntersect.X, lineStart.Y+lineToIntersect.Y, lineStart.Z+lineToIntersect.Z);
    }

    public synchronized Triangle[] clipAgainstPlane(Vec3D planeP, Vec3D planeN, Triangle inTri)
    {
        //Normalise vectors (Removed as not needed for now)
        //float mag = (float)Math.sqrt((planeN.X*planeN.X)+(planeN.Y*planeN.Y)+(planeN.Z*planeN.Z));
        //planeN = new Vec3D(planeN.X/mag, planeN.Y/mag, planeN.Z/mag);
        Vec3D insidePoints[] = new Vec3D[3];
        int insidePointCount = 0;
        Vec3D outsidePoints[] = new Vec3D[3];
        int outsidePointCount = 0;

        Triangle outTri1, outTri2;

        float d0 = dist(inTri.POINTS[0], planeN, planeP);
        float d1 = dist(inTri.POINTS[1], planeN, planeP);
        float d2 = dist(inTri.POINTS[2], planeN, planeP);

        if (d0 >= 0)
        {
            insidePoints[insidePointCount] = inTri.POINTS[0];
            insidePointCount++;
        }
        else
        {
            outsidePoints[outsidePointCount] = inTri.POINTS[0];
            outsidePointCount++;
        }
        if (d1 >= 0)
        {
            insidePoints[insidePointCount] = inTri.POINTS[1];
            insidePointCount++;
        }
        else
        {
            outsidePoints[outsidePointCount] = inTri.POINTS[1];
            outsidePointCount++;
        }
        if (d2 >= 0)
        {
            insidePoints[insidePointCount] = inTri.POINTS[2];
            insidePointCount++;
        }
        else
        {
            outsidePoints[outsidePointCount] = inTri.POINTS[2];
            outsidePointCount++;
        }

        if (insidePointCount == 0)
        {
            return null;
        }
        if (insidePointCount == 3)
        {
            outTri1 = inTri;
            return new Triangle[]{outTri1};
        }
        if (insidePointCount == 1 && outsidePointCount == 2)
        {
            outTri1 = new Triangle(insidePoints[0], intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]), intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[1]), inTri.COLOUR, inTri.NORM, inTri.APPLYLIGHTING);
            return new Triangle[]{outTri1};
        }
        if (insidePointCount == 2 && outsidePointCount == 1)
        {
            Vec3D tempPoint = intersectPlane(planeP, planeN, insidePoints[0], outsidePoints[0]);
            outTri1 = new Triangle(insidePoints[0], insidePoints[1], tempPoint, inTri.COLOUR, inTri.NORM, inTri.APPLYLIGHTING);
            outTri2 = new Triangle(insidePoints[1], tempPoint, intersectPlane(planeP, planeN, insidePoints[1], outsidePoints[0]), inTri.COLOUR, inTri.NORM, inTri.APPLYLIGHTING);
            return new Triangle[]{outTri1, outTri2};
        }
        return null;
    }

    private synchronized float dist(Vec3D p, Vec3D planeN, Vec3D planeP)
    {
        //float mag = (float)Math.sqrt((planeN.X*planeN.X)+(planeN.Y*planeN.Y)+(planeN.Z*planeN.Z));
        //planeN = new Vec3D(planeN.X/mag, planeN.Y/mag, planeN.Z/mag);
        return ((planeN.X*p.X)+(planeN.Y*p.Y)+(planeN.Z*p.Z))-((planeN.X*planeP.X)+(planeN.Y*planeP.Y)+(planeN.Z*planeP.Z));
    }

    public synchronized void render()
    {
        this.repaint();
    }

    private synchronized static void quickSort(Triangle[] m, int begin, int end)
    {
        if (begin < end)
        {
            int partitionIndex = partition(m, begin, end);

            quickSort(m, begin, partitionIndex-1);
            quickSort(m, partitionIndex+1, end);
        }
    }
    private synchronized static int partition(Triangle[] m, int begin, int end)
    {
        float pivot = m[end].getZMidPoint();
        int i = (begin-1);

        for (int j = begin; j < end; j++)
        {
            if (m[j].getZMidPoint() >= pivot)
            {
                i++;

                Triangle swapTemp = m[i];
                m[i] = m[j];
                m[j] = swapTemp;
            }
        }

        Triangle swapTemp = m[i+1];
        m[i+1] = m[end];
        m[end] = swapTemp;

        return i+1;
    }
}
