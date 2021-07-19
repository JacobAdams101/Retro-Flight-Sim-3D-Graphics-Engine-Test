/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightsim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author jacob
 */
public class Mesh
 {
    public Triangle[] TRIS;

    public Vec3D LIGHTDIRECTION;

    public Mesh()
    {
        this(0);
    }

    public Mesh(int triangleCount)
    {
        this(triangleCount, 2, 1.5f, 1);
    }

    public Mesh(int triangleCount, float x, float y, float z)
    {
        TRIS = new Triangle[triangleCount];

        float magnitude = (float)Math.sqrt((x*x)+(y*y)+(z*z));

        LIGHTDIRECTION = new Vec3D(x/magnitude, y/magnitude, z/magnitude);
    }

    public Mesh(String filename, boolean ignoreBackFaceCulling, Color c)
    {
        this();
        ArrayList<Vec3D>verticies = new ArrayList<>();
        ArrayList<Triangle>triangles = new ArrayList<>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(filename));

            File file = new File(filename); 

            if (file.exists())
            {

                String[] lines = br.lines().toArray(String[]::new);

                for (String line : lines)
                {
                    if (line.startsWith("v"))
                    {
                        String[] values = line.substring(2).split(" ");
                        verticies.add(new Vec3D(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2])));
                    }
                    if (line.startsWith("f"))
                    {
                        String[] values = line.substring(2).split(" ");

                        triangles.add(new Triangle(verticies.get(Integer.parseInt(values[0])-1).deepCopy(), verticies.get(Integer.parseInt(values[1])-1).deepCopy(), verticies.get(Integer.parseInt(values[2])-1).deepCopy(), c));
                        if (ignoreBackFaceCulling == true)
                        {
                            triangles.add(new Triangle(verticies.get(Integer.parseInt(values[2])-1).deepCopy(), verticies.get(Integer.parseInt(values[1])-1).deepCopy(), verticies.get(Integer.parseInt(values[0])-1).deepCopy(), c));
                        }
                    }
                }
            }
            TRIS = new Triangle[triangles.size()];
            for (int i = 0; i < triangles.size(); i++)
            {
                TRIS[i] = triangles.get(i);
            }
            br.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("The file " + filename + " has not been found!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }


    }

    public void translateMesh(Vec3D pos)
    {
        for (int i = 0; i < this.getTriangleCount(); i++)
        {
            for (int i2 = 0; i2 < 3; i2++)
            {
                this.TRIS[i].POINTS[i2].X += pos.X;
                this.TRIS[i].POINTS[i2].Y += pos.Y;
                this.TRIS[i].POINTS[i2].Z += pos.Z;
            }
        }
    }

    public synchronized void rotateMesh(float xRot, float yRot)
    {
        for (int i = 0; i < this.getTriangleCount(); i++)
        {
            for (int i2 = 0; i2 < 3; i2++)
            {
                float cosY = (float)Math.cos(yRot);
                float sinY = (float)Math.sin(yRot);
                /*
                float cosX = (float)Math.cos(xRot);
                float sinX = (float)Math.sin(xRot);

                float cosXYAW = (float)Math.cos(currentYaw);
                float sinXYAW = (float)Math.sin(currentYaw);
                */

                this.TRIS[i].POINTS[i2].X = (cosY*this.TRIS[i].POINTS[i2].X)+(sinY*this.TRIS[i].POINTS[i2].Z);
                //this.TRIS[i].POINTS[i2].Y = this.TRIS[i].POINTS[i2].Y; //Unused
                this.TRIS[i].POINTS[i2].Z = (cosY*this.TRIS[i].POINTS[i2].Z)-(sinY*this.TRIS[i].POINTS[i2].X);
                /*
                //this.TRIS[i].POINTS[i2].X = this.TRIS[i].POINTS[i2].X; //Unused
                this.TRIS[i].POINTS[i2].Y = (cosX*this.TRIS[i].POINTS[i2].Y)-(sinX*this.TRIS[i].POINTS[i2].Z);
                this.TRIS[i].POINTS[i2].Z = (sinX*this.TRIS[i].POINTS[i2].Y)+(cosX*this.TRIS[i].POINTS[i2].Z);
                */
            }
        }
    }

    public Mesh addMesh(Mesh m)
    {
        Mesh result = new Mesh(this.getTriangleCount()+m.getTriangleCount());
        result.LIGHTDIRECTION = this.LIGHTDIRECTION;
        System.arraycopy(this.TRIS, 0, result.TRIS, 0, this.TRIS.length);
        System.arraycopy(m.TRIS, 0, result.TRIS, this.TRIS.length, m.TRIS.length);
        return result;
    }
    public void swapMesh(Mesh m, int pos)
    {
        for (int i = 0; i < m.TRIS.length; i++)
        {
            this.TRIS[i+pos].COLOUR = m.TRIS[i].COLOUR;
            this.TRIS[i+pos].NORM.X = m.TRIS[i].NORM.X;
            this.TRIS[i+pos].NORM.Y = m.TRIS[i].NORM.Y;
            this.TRIS[i+pos].NORM.Z = m.TRIS[i].NORM.Z;
            this.TRIS[i+pos].APPLYLIGHTING = m.TRIS[i].APPLYLIGHTING;
            for (int i2 = 0; i2 < 3; i2++)
            {
                this.TRIS[i+pos].POINTS[i2].X = m.TRIS[i].POINTS[i2].X;
                this.TRIS[i+pos].POINTS[i2].Y = m.TRIS[i].POINTS[i2].Y;
                this.TRIS[i+pos].POINTS[i2].Z = m.TRIS[i].POINTS[i2].Z;
            }
        }
    }



    public int getTriangleCount()
    {
        return TRIS.length;
    }
    
    public static Mesh generateFace(Vec3D[] POINTS, Color colour, boolean applyLighting)
    {
        Mesh mesh = new Mesh(POINTS.length - 2);
        
        for (int i = 0; i < mesh.TRIS.length; i++)
        {
            mesh.TRIS[i] = new Triangle(POINTS[0].deepCopy(), POINTS[i+1].deepCopy(), POINTS[i+2].deepCopy(), colour, applyLighting);
        }
        return mesh;
    }
    
    public static Mesh generateCube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, Color c)
    {
        Mesh mesh = new Mesh(12);
        final float RADIUSX = sizeX/2;
        final float RADIUSY = sizeY/2;
        final float RADIUSZ = sizeZ/2;
        
        final float MINX = x - RADIUSX;
        final float MAXX = x + RADIUSX;
        final float MINY = y - RADIUSY;
        final float MAXY = y + RADIUSY;
        final float MINZ = z - RADIUSZ;
        final float MAXZ = z + RADIUSZ;
        Mesh[] faces = new Mesh[6];
        faces[0] = generateFace
        (
                new Vec3D[]
                {
                    new Vec3D(MINX, MAXY, MINZ),
                    new Vec3D(MAXX, MAXY, MINZ),
                    new Vec3D(MAXX, MINY, MINZ), 
                    new Vec3D(MINX, MINY, MINZ), 
                }, c, true
        );
        faces[1] = generateFace
        (
                new Vec3D[]
                {
                    new Vec3D(MINX, MINY, MINZ), 
                    new Vec3D(MAXX, MINY, MINZ),
                    new Vec3D(MAXX, MINY, MAXZ),
                    new Vec3D(MINX, MINY, MAXZ),
                }, c, true
        );
        faces[2] = generateFace
        (
                new Vec3D[]
                {
                    new Vec3D(MINX, MINY, MINZ),
                    new Vec3D(MINX, MINY, MAXZ), 
                    new Vec3D(MINX, MAXY, MAXZ),
                    new Vec3D(MINX, MAXY, MINZ),
                }, c, true
        );
        faces[3] = generateFace
        (
                new Vec3D[]
                {
                    new Vec3D(MINX, MINY, MAXZ), 
                    new Vec3D(MAXX, MINY, MAXZ), 
                    new Vec3D(MAXX, MAXY, MAXZ),
                    new Vec3D(MINX, MAXY, MAXZ),
                }, c, true
        );
        faces[4] = generateFace
        (
                new Vec3D[]
                {
                    new Vec3D(MINX, MAXY, MAXZ),
                    new Vec3D(MAXX, MAXY, MAXZ), 
                    new Vec3D(MAXX, MAXY, MINZ), 
                    new Vec3D(MINX, MAXY, MINZ), 
                }, c, true
        );
        faces[5] = generateFace
        (
                new Vec3D[]
                {
                    new Vec3D(MAXX, MAXY, MINZ),
                    new Vec3D(MAXX, MAXY, MAXZ), 
                    new Vec3D(MAXX, MINY, MAXZ), 
                    new Vec3D(MAXX, MINY, MINZ), 
                }, c, true
        );
        
        int i;
        
        for (i = 0; i < faces.length; i++)
        {
            mesh.TRIS[(i*2)] = faces[i].TRIS[0];
            mesh.TRIS[(i*2)+1] = faces[i].TRIS[1];
        }
        
        return mesh;
    }
}
