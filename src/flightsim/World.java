/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightsim;


public class World
{
    public Plane plane;
    public Camera cam;
    private Mesh mesh;
    private Chunk[] chunks;

    public static class Chunk
    {
        public Mesh mesh;
        public float midPointX;
        public float midPointZ;
        public boolean active;

        public Chunk(Mesh mesh, float midPointX, float midPointZ)
        {
            this.mesh = mesh;
            this.midPointX = midPointX;
            this.midPointZ = midPointZ;
            active = true;
        }
    }

    public float dayLighting[];

    public float time = 12;


    public Mesh getMesh()
    {
        return mesh;
    }

    public final int CHUNKHEIGHTMAPSIZE = 30;
    public final float CHUNKSCALE = 1200;

    public final int SEED = 2;

    public final float MAXHEIGHT = 300;

    final int RENDERWIDTHINCHUNKS = 3;
    final int RENDERDEPTHINCHUNKS = 3;

    public World()
    {
        plane = new Plane();
        cam = new Camera(0, -0.8f, 2);
        mesh = new Mesh();
        chunks = new Chunk[RENDERWIDTHINCHUNKS*RENDERDEPTHINCHUNKS];
        for (int ix = 0; ix < RENDERWIDTHINCHUNKS; ix++)
        {
            for (int iy = 0; iy < RENDERDEPTHINCHUNKS; iy++)
            {
                chunks[ix+(iy*RENDERWIDTHINCHUNKS)] = new Chunk(generateChunk(ix-(RENDERWIDTHINCHUNKS/2), iy-(RENDERDEPTHINCHUNKS/2)), (((float)(ix-(RENDERWIDTHINCHUNKS/2)))+0.5f)*((float)CHUNKSCALE), (((float)(iy-(RENDERDEPTHINCHUNKS/2)))+0.5f)*((float)CHUNKSCALE));
                mesh = mesh.addMesh(chunks[ix+(iy*RENDERWIDTHINCHUNKS)].mesh);
            }
        }

        mesh = mesh.addMesh(plane.getMesh());

    }

    public Mesh generateChunk(int x, int y)
    {
        return WorldGenerator.generateTerrain(-MAXHEIGHT, 0, x*CHUNKSCALE, (x+1)*CHUNKSCALE, y*CHUNKSCALE, (y+1)*CHUNKSCALE, CHUNKHEIGHTMAPSIZE, x*CHUNKHEIGHTMAPSIZE, y*CHUNKHEIGHTMAPSIZE, SEED);
    }
    public boolean firstPerson = false;
    public void update(float timeElapsed)
    {
        dayLighting = getTimeLighting(time);
        time+= timeElapsed/60f;
        if (time >= 24)
        {
            time = 0;
        }

        plane.run(timeElapsed, cam);
        if (firstPerson)
        {
            cam.setPosition(plane.pos.X+plane.cameraFollowFirstPerson.X, plane.pos.Y+plane.cameraFollowFirstPerson.Y, plane.pos.Z+plane.cameraFollowFirstPerson.Z); //First Person
        }
        else
        {
            Vec3D centeredOffset = cam.getCenteredOffset(plane.ORIGINALCAMERAFOLLOWTHIRDPERSON);
            cam.setPosition(plane.pos.X+centeredOffset.X, plane.pos.Y+centeredOffset.Y, plane.pos.Z+centeredOffset.Z); //Third Person
        }
        final float MIXX = cam.pos.X - (((float)RENDERWIDTHINCHUNKS)*CHUNKSCALE*0.5f);
        final float MINZ = cam.pos.Z - (((float)RENDERDEPTHINCHUNKS)*CHUNKSCALE*0.5f);
        final float MAXX = cam.pos.X + (((float)RENDERWIDTHINCHUNKS)*CHUNKSCALE*0.5f);
        final float MAXZ = cam.pos.Z + (((float)RENDERDEPTHINCHUNKS)*CHUNKSCALE*0.5f);

        for (int i = 0; i < chunks.length; i++)
        {

            boolean generateNewChunk = false;
            if (chunks[i].midPointX < MIXX)
            {
                chunks[i].midPointX = chunks[i].midPointX+(CHUNKSCALE*RENDERWIDTHINCHUNKS);
                //chunks[i].midPointZ = chunks[i].midPointZ;
                generateNewChunk = true;
            }
            if (chunks[i].midPointX > MAXX)
            {
                chunks[i].midPointX = chunks[i].midPointX-(CHUNKSCALE*RENDERWIDTHINCHUNKS);
                //chunks[i].midPointZ = chunks[i].midPointZ;
                generateNewChunk = true;
            }
            if (chunks[i].midPointZ < MINZ)
            {
                //chunks[i].midPointX = chunks[i].midPointX;
                chunks[i].midPointZ = chunks[i].midPointZ+(CHUNKSCALE*RENDERDEPTHINCHUNKS);
                generateNewChunk = true;
            }
            if (chunks[i].midPointZ > MAXZ)
            {
                //chunks[i].midPointX = chunks[i].midPointX;
                chunks[i].midPointZ = chunks[i].midPointZ-(CHUNKSCALE*RENDERDEPTHINCHUNKS);
                generateNewChunk = true;
            }
            float X = (chunks[i].midPointX/((float)CHUNKSCALE)) - 0.5f;
            float Z = (chunks[i].midPointZ/((float)CHUNKSCALE)) - 0.5f;

            if (generateNewChunk)
            {
                //System.out.println("Generate new chunk X: " + X + " Z: " + Z);
                WorldGenerator.swapMeshTerrain(chunks[i].mesh, -MAXHEIGHT, 0, X*CHUNKSCALE, (X+1)*CHUNKSCALE, Z*CHUNKSCALE, (Z+1)*CHUNKSCALE, CHUNKHEIGHTMAPSIZE, (int)X*CHUNKHEIGHTMAPSIZE, (int)Z*CHUNKHEIGHTMAPSIZE, SEED);
            }
        }
    }

    public static float[] getTimeLighting(float time)
    {
        time = time % 24;
        float brightR = 1;
        float brightG = 1;
        float brightB = 1;
        if (time > 17)
        {
            time = time-16f;
            brightR = 1f/(time);
            brightG = 1f/(time*time*time*time);
            brightB = 1f/(time*time);
        }
        else if (time > 7)
        {
            brightR=1;
            brightG=1;
            brightB=1;
        }
        else if (time < 7)
        {
            time = 8f-time;
            brightR = 1f/(time);
            brightG = 1f/(time*time*time*time);
            brightB = 1f/(time*time);
        }
        return new float[]{brightR,brightG,brightB};
    }

}
