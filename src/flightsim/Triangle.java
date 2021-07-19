/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightsim;

import java.awt.Color;

/**
 *
 * @author jacob
 */
public class Triangle
{
    public final Vec3D[] POINTS;

    public Color COLOUR;

    public Vec3D NORM;

    boolean APPLYLIGHTING;

    public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3)
    {
        this(new Vec3D(x1, y1, z1), new Vec3D(x2, y2, z2), new Vec3D(x3, y3, z3));
    }
    public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Color colour)
    {
        this(new Vec3D(x1, y1, z1), new Vec3D(x2, y2, z2), new Vec3D(x3, y3, z3), colour);
    }
    public Triangle(Vec3D p1, Vec3D p2, Vec3D p3)
    {
        this(p1, p2, p3, Color.BLACK);
    }
    public Triangle(Vec3D p1, Vec3D p2, Vec3D p3, Color colour)
    {
        this.POINTS = new Vec3D[]{p1, p2, p3};
        this.COLOUR = colour;
        this.NORM = getNormalVector(this);
        this.APPLYLIGHTING = true;
    }
    public Triangle(Vec3D p1, Vec3D p2, Vec3D p3, Color colour, boolean applyLighting)
    {
        this.POINTS = new Vec3D[]{p1, p2, p3};
        this.COLOUR = colour;
        this.NORM = getNormalVector(this);
        this.APPLYLIGHTING = applyLighting;
    }
    public Triangle(Vec3D p1, Vec3D p2, Vec3D p3, Color colour, Vec3D norm)
    {
        this.POINTS = new Vec3D[]{p1, p2, p3};
        this.COLOUR = colour;
        this.NORM = norm;
        this.APPLYLIGHTING = true;
    }
    public Triangle(Vec3D p1, Vec3D p2, Vec3D p3, Color colour, Vec3D norm, boolean applyLighting)
    {
        this.POINTS = new Vec3D[]{p1, p2, p3};
        this.COLOUR = colour;
        this.NORM = norm;
        this.APPLYLIGHTING = applyLighting;
    }
    public Triangle(float x1, float y1, float z1, Color colour, float nx, float ny, float nz, boolean applyLighting)
    {
        this.POINTS = new Vec3D[]{new Vec3D(x1, y1, z1), null, null};
        this.COLOUR = colour;
        this.NORM = new Vec3D(nx, ny, nz);
        this.APPLYLIGHTING = applyLighting;
    }
    public static Vec3D getNormalVector(Triangle t)
    {
        Vec3D line1 = new Vec3D(t.POINTS[0].X - t.POINTS[1].X, t.POINTS[0].Y - t.POINTS[1].Y, t.POINTS[0].Z - t.POINTS[1].Z);
        Vec3D line2 = new Vec3D(t.POINTS[1].X - t.POINTS[2].X, t.POINTS[1].Y - t.POINTS[2].Y, t.POINTS[1].Z - t.POINTS[2].Z);
        return getNormalVector(line1, line2);
    }
    public static Vec3D getNormalVector(Vec3D line1, Vec3D line2)
    {
        float x = (line1.Y*line2.Z)-(line1.Z*line2.Y);
        float y = (line1.Z*line2.X)-(line1.X*line2.Z);
        float z = (line1.X*line2.Y)-(line1.Y*line2.X);
        float magnitude = (float)Math.sqrt((x*x)+(y*y)+(z*z));
        return new Vec3D(x/magnitude, y/magnitude, z/magnitude);
    }

    public float getZMidPoint()
    {
        return (POINTS[0].Z+POINTS[1].Z+POINTS[2].Z)/3f;
    }
}
