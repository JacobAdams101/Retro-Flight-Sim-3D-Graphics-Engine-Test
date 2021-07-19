
package flightsim;

import java.awt.Color;


public class Plane
{
    public Vec3D pos; //Plane position
    public Vec3D velocity; //Plane velocity

    public final Vec3D PLANEORIGINALDIRECTION = new Vec3D(0, 0, 1); //Normalised normal vector pointing in direction of plane (not to be confused with normalised velocity)
    public Vec3D planeDirection = new Vec3D(0, 0, 1); //Normalised normal vector pointing in direction of plane (not to be confused with normalised velocity)
    
    private final Vec3D PLANEORIGINALLIFTDIRECTION = new Vec3D(0, -1, 0); //Normalised normal vector pointing in direction of lift of plane (not to be confused with normalised velocity)
    private Vec3D planeLiftDirection = new Vec3D(0, -1, 0); //Normalised normal vector pointing in direction of lift of plane (not to be confused with normalised velocity)

    public Vec3D planeNormal; //Normalised normal vector to wings modeled as planes

    public Vec3D cameraFollowFirstPerson = new Vec3D(0, -0.4f, 2.2f); //Position of camera relative to the plane
    
    public final Vec3D ORIGINALCAMERAFOLLOWFIRSTPERSON = new Vec3D(0, -0.4f, 2.2f); //Position of camera relative to the plane
    public final Vec3D ORIGINALCAMERAFOLLOWTHIRDPERSON = new Vec3D(0, -3f, -6f); //Position of camera relative to the plane

    public float thrust; //Thrust force of plane

    public float speed;

    public final float COLLISIONSPHERERADISU = 50; //Radius of the collision sphere

    public final float MASS = 2; //Mass of plane

    public final float G = 15; //Gravitational field strength

    public final float LIFTCOEFFICIENT = 0.01f; //Coefficient of how effective lift is (simplified lift formula)
    public float WINGSPANAREA = 1; //Area of wing span

    public final float DRAGCOEFFICIENT = 0.0065f; //Coefficient of how effective drag is (simplified drag formula)
    public float AREAOFDRAG = 1; //Supposed area of of air particles hitting plane

    final float THRUSTJERK = 10f; //How quickly can you increase your thrust (effecticvely maxJerk*mass)
    final float MAXTHRUST = 75f; //How quickly can you "accelerate" (this is actually a force so technically how fast can you "mass*accelerate")
    final float AUTOTHRUSTTURNDOWN = 50f; //The limit where the thrust will turn down
    final float THRUSTTURNDOWNSPEED = 5f;
    
    final float AIRDENSITY0 = 1f;
    final float AIRPRESSURE0 = 20000f;

    final float ROTATIONMAXSPEED = 2f;
    final float ROTATIONACCELERATION = 0.4f;
    
    final float SEALEVEL = 0;

    private float yaw;
    private float pitch;
    
    private float yawSpeed = 0;
    private float pitchSpeed = 0;
    
    public float lastFluidDensity = 0;
    
    private final Mesh PLANEBODYORIGINALMESH;
    private final Mesh PLANEPROPELLAORIGINALMESH;
    private final Mesh PLANEORIGINALMESH;
    
    private Mesh planeTransformedMesh;
    
    public float COEFFICIENTOFRESTITUTION = 0.01f;
    
    public Mesh getMesh()
    {
        return planeTransformedMesh;
    }

    public Plane()
    {
        pos = new Vec3D(0, -300, 0);
        velocity = new Vec3D(0, 0, 140);
        thrust = 45;
        yaw = 0;
        pitch = 0;
        
        PLANEBODYORIGINALMESH = new Mesh("BiplaneBODY.obj", true, new Color(200, 40, 40));
        PLANEPROPELLAORIGINALMESH = new Mesh("BiplanePROPELLA.obj", true, new Color(80, 80, 80));
        PLANEORIGINALMESH = PLANEBODYORIGINALMESH.addMesh(PLANEPROPELLAORIGINALMESH);
        
        planeTransformedMesh = new Mesh("Biplane.obj", true, new Color(200, 40, 40));
        planeTransformedMesh.addMesh(new Mesh("BiplanePROPELLA.obj", true, new Color(80, 80, 80)));
        //planeOriginalMesh.translateMesh(pos);
        //planeMesh = new Mesh();
        //planeMesh = planeMesh.addMesh(generateCube(0, 0, 0, 2, 2, 2, Color.RED));
        //planeMesh = planeMesh.addMesh(generateFace(new Vec3D[]{new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), new Vec3D(1, 1, 0), new Vec3D(0, 1, 0),}, Color.RED));
        //planeMesh = planeMesh.addMesh(generateFace(new Vec3D[]{new Vec3D(1, 1, 1), new Vec3D(0, 1, 1), new Vec3D(0, 0, 1)}, Color.BLUE));
    }
    
    

    public boolean isInOverDrive()
    {
        return thrust > AUTOTHRUSTTURNDOWN;
    }
    
    private float getLiftPitchCoefficient(float pitch)
    {
        return 0.1f + (float)Math.sin(pitch*2f);

    }
    
    
    public synchronized void run(float timeElapsed, Camera camfollwing)
    {
        float horizontalSpeedSquared = (velocity.X*velocity.X)+(velocity.Z*velocity.Z);
        speed = velocity.mag();
        Vec3D normalisedVelocity;
        if (speed == 0)
        {
            normalisedVelocity = new Vec3D(0, 0, 0);
        }
        else 
        {
            normalisedVelocity = new Vec3D(velocity.X/speed, velocity.Y/speed, velocity.Z/speed);
        }
        //System.out.println("E^" + ((AIRDENSITY0*G*(this.pos.Y-SEALEVEL))/(AIRPRESSURE0)));
        lastFluidDensity = AIRDENSITY0*(float)Math.exp(((AIRDENSITY0*G*(this.pos.Y-SEALEVEL))/(AIRPRESSURE0)));
        
        float dragForce = speed*speed*DRAGCOEFFICIENT*AREAOFDRAG*lastFluidDensity;
        //System.out.println(lastFluidDensity);
        float liftForce = LIFTCOEFFICIENT*WINGSPANAREA*horizontalSpeedSquared*lastFluidDensity*getLiftPitchCoefficient(this.pitch);

        
        float actualThrust = thrust*lastFluidDensity*lastFluidDensity;
        
        //Update velocity using F=ma
        velocity = velocity.translate(new Vec3D((((-1f*normalisedVelocity.X*dragForce)+(actualThrust*planeDirection.X)+(liftForce*planeLiftDirection.X))/MASS)*timeElapsed,(((MASS*G)+(actualThrust*planeDirection.Y)+(-1f*normalisedVelocity.Y*dragForce)+(liftForce*planeLiftDirection.Y)) / MASS)*timeElapsed, (((-1f*normalisedVelocity.Z*dragForce)+(actualThrust*planeDirection.Z)+(liftForce*planeLiftDirection.Z))/MASS)*timeElapsed));

        move(new Vec3D(velocity.X*timeElapsed, velocity.Y*timeElapsed, velocity.Z*timeElapsed));
        
        if (isInOverDrive())
        {
            thrust -= THRUSTTURNDOWNSPEED*timeElapsed;
        }
        rotate(yawSpeed * timeElapsed, pitchSpeed * timeElapsed, camfollwing);
        
        yawSpeed -= yawSpeed * 0.05f;
        pitchSpeed -= pitchSpeed * 0.05f;
        
        testCollide();
        
        rotatePropella(0.01f * this.thrust);
        
        updatePosition();
    }
    public synchronized void rotatePropella(float theta)
    {
        float sinTheta = (float)Math.sin(theta);
        float cosTheta = (float)Math.cos(theta);
        
        final float XROTOFFSET = 0f;
        final float YROTOFFSET = 0.195f;
        
        float x, y;
        
        int i;
        int i2;
        
        for (i = 0; i < PLANEPROPELLAORIGINALMESH.getTriangleCount(); i++)
        {
            x = PLANEPROPELLAORIGINALMESH.TRIS[i].NORM.X;
            y = PLANEPROPELLAORIGINALMESH.TRIS[i].NORM.Y;
            //z = PLANEPROPELLAORIGINALMESH.TRIS[i].NORM.Z; //Unused as z maps to itself
            PLANEPROPELLAORIGINALMESH.TRIS[i].NORM.X = (cosTheta*x)-(sinTheta*y);
            PLANEPROPELLAORIGINALMESH.TRIS[i].NORM.Y = (sinTheta*x)+(cosTheta*y);
            for (i2 = 0; i2 < 3; i2++)
            {
                x = PLANEPROPELLAORIGINALMESH.TRIS[i].POINTS[i2].X-XROTOFFSET;
                y = PLANEPROPELLAORIGINALMESH.TRIS[i].POINTS[i2].Y-YROTOFFSET;
                //z = PLANEPROPELLAORIGINALMESH.TRIS[i].NORM.Z; //Unused as z maps to itself
                PLANEPROPELLAORIGINALMESH.TRIS[i].POINTS[i2].X = (cosTheta*x)-(sinTheta*y)+XROTOFFSET;
                PLANEPROPELLAORIGINALMESH.TRIS[i].POINTS[i2].Y = (sinTheta*x)+(cosTheta*y)+YROTOFFSET;
            }
        }
    }
    public synchronized void testCollide()
    {
        if (pos.Y > 0)
        {
            pos.Y = 0;
            velocity.Y *= -COEFFICIENTOFRESTITUTION;
        }
    }

    public synchronized void move(Vec3D x)
    {
        pos = pos.translate(x);
    }
    public synchronized void rotate(float yaw, float pitch, Camera camFollowing)
    {
        this.yaw += yaw;
        this.pitch -= pitch;

        camFollowing.rotate(pitch, -yaw);
    }
    
    public void updatePosition()
    {
        int i;
        int i2;
        float x, y, z;
        
        //Assign values to cos/sin pitch/yaw to save time computing trigonometric functions for rotations
        float cosYaw = (float)Math.cos(yaw);
        float sinYaw = (float)Math.sin(yaw);
        float cosPitch = (float)Math.cos(pitch);
        float sinPitch = (float)Math.sin(pitch);
        
        //Rotate planes direction vector
        x = PLANEORIGINALDIRECTION.X;
        y = PLANEORIGINALDIRECTION.Y;
        z = PLANEORIGINALDIRECTION.Z;
        planeDirection.X = (cosYaw*x) + (sinYaw*sinPitch*y)+(sinYaw*cosPitch*z);
        planeDirection.Y = (cosPitch*y) - (sinPitch*z);
        planeDirection.Z = (cosYaw*cosPitch*z) + (cosYaw*sinPitch*y) - (sinYaw*x);
        
        //Rotate the planes lift direction vector
        x = PLANEORIGINALLIFTDIRECTION.X;
        y = PLANEORIGINALLIFTDIRECTION.Y;
        z = PLANEORIGINALLIFTDIRECTION.Z;
        planeLiftDirection.X = (cosYaw*x) + (sinYaw*sinPitch*y)+(sinYaw*cosPitch*z);
        planeLiftDirection.Y = (cosPitch*y) - (sinPitch*z);
        planeLiftDirection.Z = (cosYaw*cosPitch*z) + (cosYaw*sinPitch*y) - (sinYaw*x);
        
        //Rotate camera offset for firtst person
        x = ORIGINALCAMERAFOLLOWFIRSTPERSON.X;
        y = ORIGINALCAMERAFOLLOWFIRSTPERSON.Y;
        z = ORIGINALCAMERAFOLLOWFIRSTPERSON.Z;
        cameraFollowFirstPerson.X = (cosYaw*x) + (sinYaw*sinPitch*y)+(sinYaw*cosPitch*z);
        cameraFollowFirstPerson.Y = (cosPitch*y) - (sinPitch*z);
        cameraFollowFirstPerson.Z = (cosYaw*cosPitch*z) + (cosYaw*sinPitch*y) - (sinYaw*x);
        
        //Rotate camera offset for third person
        //Not needed anymore as plane third person camera moves around plane
        
        
        for (i = 0; i < PLANEORIGINALMESH.getTriangleCount(); i++)
        {
            //Rotate triangles precalculated normal vector
            x = PLANEORIGINALMESH.TRIS[i].NORM.X;
            y = PLANEORIGINALMESH.TRIS[i].NORM.Y;
            z = PLANEORIGINALMESH.TRIS[i].NORM.Z;
            planeTransformedMesh.TRIS[i].NORM.X = (cosYaw*x) + (sinYaw*sinPitch*y)+(sinYaw*cosPitch*z);
            planeTransformedMesh.TRIS[i].NORM.Y = (cosPitch*y) - (sinPitch*z);
            planeTransformedMesh.TRIS[i].NORM.Z = (cosYaw*cosPitch*z) + (cosYaw*sinPitch*y) - (sinYaw*x);
            for(i2 = 0; i2 < 3; i2++)
            {
                //Rotate triangles x,y,z co-ordinates and translate
                x = PLANEORIGINALMESH.TRIS[i].POINTS[i2].X;
                y = PLANEORIGINALMESH.TRIS[i].POINTS[i2].Y;
                z = PLANEORIGINALMESH.TRIS[i].POINTS[i2].Z;
                planeTransformedMesh.TRIS[i].POINTS[i2].X = (cosYaw*x) + (sinYaw*sinPitch*y)+(sinYaw*cosPitch*z) + pos.X;
                planeTransformedMesh.TRIS[i].POINTS[i2].Y = (cosPitch*y) - (sinPitch*z) + pos.Y;
                planeTransformedMesh.TRIS[i].POINTS[i2].Z = (cosYaw*cosPitch*z) + (cosYaw*sinPitch*y) - (sinYaw*x) + pos.Z;
            }
        }
    }

    public synchronized void thrustUp(float timeElapsed)
    {
        thrust += THRUSTJERK * timeElapsed;
        if (thrust > MAXTHRUST)
        {
            thrust = MAXTHRUST;
        }
    }

    public synchronized void thrustDown(float timeElapsed)
    {
        thrust -= THRUSTJERK * timeElapsed;
        if (thrust < 0)
        {
            thrust = 0;
        }
    }

    public synchronized void pitchUp(float timeElapsed)
    {
        pitchSpeed -= ROTATIONACCELERATION * timeElapsed;
        if (pitchSpeed < -ROTATIONMAXSPEED)
        {
            pitchSpeed = -ROTATIONMAXSPEED;
        }
    }

    public synchronized void pitchDown(float timeElapsed)
    {
        pitchSpeed += ROTATIONACCELERATION * timeElapsed;
        if (pitchSpeed > ROTATIONMAXSPEED)
        {
            pitchSpeed = ROTATIONMAXSPEED;
        }
    }

    public synchronized void yawLeft(float timeElapsed)
    {
        yawSpeed -= ROTATIONACCELERATION * timeElapsed;
        if (yawSpeed < -ROTATIONMAXSPEED)
        {
            yawSpeed = -ROTATIONMAXSPEED;
        }
    }

    public synchronized void yawRight(float timeElapsed)
    {
        yawSpeed += ROTATIONACCELERATION * timeElapsed;
        if (yawSpeed > ROTATIONMAXSPEED)
        {
            yawSpeed = ROTATIONMAXSPEED;
        }
    }
}
