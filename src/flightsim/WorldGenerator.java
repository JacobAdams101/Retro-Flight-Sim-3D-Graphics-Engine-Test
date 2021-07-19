
package flightsim;


import java.awt.Color;
import java.util.Random;


public class WorldGenerator
{
    public static void swapMeshTerrain(Mesh mesh, float minY, float maxY, float minX, float maxX, float minZ, float maxZ, int size, int chunkX, int chunkY, int seed)
    {
        final int addOn = 15;
        float[][] height = new float[size+1+(addOn*2)][size+1+(addOn*2)];
        generateHeightMap(height, minY, maxY, 15, 0.7f, chunkX, chunkY, seed);
        generateHeightMap(height, minY, maxY, 10, 0.2f, chunkX, chunkY, seed+1);
        generateHeightMap(height, minY, maxY, 5, 0.05f, chunkX, chunkY, seed+2);
        generateHeightMap(height, minY, maxY, 1, 0.05f, chunkX, chunkY, seed+3);
        
        int pos = 0;
        
        for (int ix = 0; ix < size; ix++)
        {
            for (int iy = 0; iy < size; iy++)
            {
                float lx = minX+(((float)ix)*((maxX-minX)/((float)size)));
                float ux = minX+(((float)ix+1f)*((maxX-minX)/((float)size)));
                float lz = minZ+(((float)iy)*((maxZ-minZ)/((float)size)));
                float uz = minZ+(((float)iy+1f)*((maxZ-minZ)/((float)size)));
                
                float avg = (height[ix+1+addOn][iy+addOn]+height[ix+1+addOn][iy+1+addOn]+height[ix+addOn][iy+1+addOn]+height[ix+addOn][iy+addOn])/4f;
                float normalisedHeight = (avg-minY)/(maxY-minY);
                float seaLevel = (SANDSTART*(maxY-minY))+minY;
                float aP = height[ix+1+addOn][iy+addOn];
                float bP = height[ix+1+addOn][iy+1+addOn];
                float cP = height[ix+addOn][iy+1+addOn];
                float dP = height[ix+addOn][iy+addOn];
                int water = 0;
                if (aP > seaLevel)
                {    
                    aP = seaLevel;
                    water++;
                }
                if (bP > seaLevel)
                {    
                    bP = seaLevel;
                    water++;
                }

                if (cP > seaLevel)
                {    
                    cP = seaLevel;
                    water++;
                }

                if (dP > seaLevel)
                {    
                    dP = seaLevel;
                    water++;
                }
                if (water == 3)
                {
                    Mesh m = new Mesh(2);
                    if (aP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            new Vec3D(lx, dP, lz), 
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(ux, bP, uz),
                            new Vec3D(lx, cP, uz),
                            new Vec3D(lx, dP, lz),        
                            WATER
                        );
                        mesh.swapMesh(m, pos);
                        pos += 2;
                    }
                    if (bP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            new Vec3D(lx, cP, uz),
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(lx, cP, uz),
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, aP, lz),
                            WATER
                        );
                        mesh.swapMesh(m, pos);
                        pos += 2;
                    }
                    if (cP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(lx, cP, uz),
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, bP, uz),
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            WATER
                        );
                        mesh.swapMesh(m, pos);
                        pos += 2;
                    }
                    if (dP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, aP, lz),
                            new Vec3D(lx, cP, uz),
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(lx, cP, uz),
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            WATER
                        );
                        mesh.swapMesh(m, pos);
                        pos += 2;
                    }
                }
                else
                {
                Color c;
                if (water == 4)
                {
                    c = WATER;
                }
                else if (normalisedHeight > GRASSSTART)
                {
                    c = SAND;
                }
                else if (normalisedHeight > LOWROCKSTART)
                {
                    float rand = getRandom((chunkX*FIELDDIVISIONS)+(ix/(size/FIELDDIVISIONS)), (chunkY*FIELDDIVISIONS)+(iy/(size/FIELDDIVISIONS)), seed+10, 0, 1);
                    if (rand > 0.9)
                    {
                        c = FIELD1;
                    }
                    else if (rand > 0.8)
                    {
                        c = FIELD2;
                    }
                    else
                    {
                        c = GRASS;
                    }
                }
                else if (normalisedHeight > HIGHROCKSTART)
                {
                    c = ROCKLOW;

                }
                else if (normalisedHeight > SNOWSTART)
                {
                    c = ROCKHIGH;
                }
                else
                {
                    c = SNOW;
                }
                mesh.swapMesh(Mesh.generateFace
                (new Vec3D[] 
                    {
                        new Vec3D(ux, aP, lz),
                        new Vec3D(ux, bP, uz),
                        new Vec3D(lx, cP, uz),
                        new Vec3D(lx, dP, lz),
                    }, c, true
                ), pos);
                pos += 2;
                }
            }
        }
        
        for (int i = 0; i < NUMBEROFTREES; i++)
        {
            int x = (int)getRandom(chunkX, chunkY, seed+i, addOn, size+addOn);
            int z = (int)getRandom(chunkX, chunkY, seed+i+1, addOn, size+addOn);
            float baseHeight = height[x][z];
            
            float lx = minX+(((float)x-addOn)*((maxX-minX)/((float)size)));
            float lz = minZ+(((float)z-addOn)*((maxZ-minZ)/((float)size)));
            
            float rand = getRandom((chunkX*FIELDDIVISIONS)+((x-addOn)/(size/FIELDDIVISIONS)), (chunkY*FIELDDIVISIONS)+((z-addOn)/(size/FIELDDIVISIONS)), seed+10, 0, 1);
            float normalisedHeight = (baseHeight-minY)/(maxY-minY);
            int shift;
            if (normalisedHeight > SANDSTART || normalisedHeight < HIGHROCKSTART)
            {
                shift = 100;
            }
            else
            {
                shift = 0;
            }

            Mesh t = new Mesh(6);
            if ((rand > 0.8 && normalisedHeight < GRASSSTART && normalisedHeight > LOWROCKSTART))
            {
                t.TRIS[0] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    GRASSCOLOUR
                );
                t.TRIS[1] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    GRASSCOLOUR
                );
                t.TRIS[2] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz+GRASSSPACING),
                    GRASSCOLOUR
                );
                t.TRIS[3] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    GRASSCOLOUR
                );
                t.TRIS[4] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz-GRASSWIDTH),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz+GRASSWIDTH),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    GRASSCOLOUR
                );
                t.TRIS[5] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz+GRASSWIDTH),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz-GRASSWIDTH),
                    GRASSCOLOUR
                );
            }
            else 
            {
                if (getRandom(chunkX, chunkY, seed+i, 0, 1) > 0.8)
                {
                    shift-=2;
                    Color color;
                    if (getRandom(chunkX, chunkY, seed+i+1, 0, 1) > 0.5)
                    { 
                        color = BUSHCOLOUR;
                    }
                    else
                    {
                        color = Color.GRAY;
                    }
                    shift-=2;
                    t.TRIS[0] = new Triangle
                    (
                        new Vec3D(lx-BUSHWIDTH-BUSHOFFSET, baseHeight+shift, lz-BUSHWIDTH+BUSHOFFSET),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz-BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        color
                    );
                    t.TRIS[1] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        color
                    );
                    t.TRIS[2] = new Triangle
                    (
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH-BUSHOFFSET, baseHeight+shift, lz-BUSHWIDTH+BUSHOFFSET),
                        color
                    );
                    t.TRIS[3] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz-BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        color
                    );
                    t.TRIS[4] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH-BUSHOFFSET, baseHeight+shift, lz-BUSHWIDTH+BUSHOFFSET),
                        color
                    );
                    t.TRIS[5] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        color
                    );
                }
                else
                {
                    t.TRIS[0] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift, lz),
                        new Vec3D(lx-(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)),
                        new Vec3D(lx+(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)),
                        TREEBARK
                    );
                    t.TRIS[1] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift, lz),
                        new Vec3D(lx, baseHeight+shift, lz+(2f*ROOT3*TRUNKWIDTH)),
                        new Vec3D(lx-(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)), 
                        TREEBARK
                    );
                    t.TRIS[2] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift, lz),
                        new Vec3D(lx+(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)),
                        new Vec3D(lx, baseHeight+shift, lz+(2f*ROOT3*TRUNKWIDTH)), 
                        TREEBARK
                    );

                    float theta = getRandom(chunkX, chunkY, seed+i+1, 0, 2f*(float)Math.PI);
                    float cosy = (float)Math.cos(theta);
                    float siny = (float)Math.sin(theta);
                    float cosx = 1;
                    float sinx = 0;

                    t.TRIS[3] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift + (cosx*(-LEAVESHEIGHT)), lz + (sinx*(-LEAVESHEIGHT))),
                        new Vec3D(lx+(cosy*(-(3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*(-(3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz+(cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*(-(3f*LEAVESWIDTH)))), 
                        new Vec3D(lx+(cosy*(+(3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*(+(3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz+(cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*((3f*LEAVESWIDTH)))),
                        TREELEAVES
                    );
                    t.TRIS[4] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift + (cosx*(-LEAVESHEIGHT)), lz + (sinx*(-LEAVESHEIGHT))),
                        new Vec3D(lx + (siny*(+(2f*ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift - (sinx*cosy*((2f*ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*((2f*ROOT3*LEAVESWIDTH)))),
                        new Vec3D(lx+(cosy*(-(3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*(-(3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*(-(3f*LEAVESWIDTH)))),
                        TREELEAVES
                    );
                    t.TRIS[5] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift + (cosx*(-LEAVESHEIGHT)), lz + (sinx*(-LEAVESHEIGHT))),
                        new Vec3D(lx+(cosy*((3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*((3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*((3f*LEAVESWIDTH)))),
                        new Vec3D(lx + (siny*((2f*ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift - (sinx*cosy*(+(2f*ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*((2f*ROOT3*LEAVESWIDTH)))), 
                        TREELEAVES
                    );
                }
            }
            mesh.swapMesh(t, pos);
            pos += 6;
        }
        for (int i = 0; i < CLOUDCOUNT; i++)
        {
            
            float x = minX+(getRandom(chunkX, chunkY, seed+i, 0, size+1)*((maxX-minX)/((float)size)));
            float z = minZ+(getRandom(chunkX, chunkY, seed+i+1, 0, size+1)*((maxZ-minZ)/((float)size)));
            int width = (int)getRandom(chunkX, chunkY, seed+i, (maxX-minX)*CLOUDMINSIZE, (maxX-minX)*CLOUDMAXSIZE);
            int depth = (int)getRandom(chunkX, chunkY, seed+i+1, (maxZ-minZ)*CLOUDMINSIZE, (maxZ-minZ)*CLOUDMAXSIZE);
            mesh.swapMesh(Mesh.generateFace
                (
                new Vec3D[]
                {
                    new Vec3D(x, CLOUDHEIGHT, z+depth),
                    new Vec3D(x+width, CLOUDHEIGHT, z+depth),
                    new Vec3D(x+width, CLOUDHEIGHT, z),
                    new Vec3D(x, CLOUDHEIGHT, z), 
                }, Color.WHITE, false), pos);
            pos+=2;
            
        }
    }
    
    public static Mesh generateTerrain(float minY, float maxY, float minX, float maxX, float minZ, float maxZ, int size, int chunkX, int chunkY, int seed)
    {
        
        Mesh mesh = new Mesh();
        final int addOn = 15;
        float[][] height = new float[size+1+(addOn*2)][size+1+(addOn*2)];
        generateHeightMap(height, minY, maxY, 15, 0.7f, chunkX, chunkY, seed);
        generateHeightMap(height, minY, maxY, 10, 0.2f, chunkX, chunkY, seed+1);
        generateHeightMap(height, minY, maxY, 5, 0.05f, chunkX, chunkY, seed+2);
        generateHeightMap(height, minY, maxY, 1, 0.05f, chunkX, chunkY, seed+3);
        /*
        for (int ix = 0; ix < height.length; ix++)
        {
            for (int iy = 0; iy < height[0].length; iy++)
            {
                height[ix][iy] += minY + (((float)Math.random()) * (maxY-minY) * 0.2f);
            }
        }
        */
        for (int ix = 0; ix < size; ix++)
        {
            for (int iy = 0; iy < size; iy++)
            {
                
                
                float lx = minX+(((float)ix)*((maxX-minX)/((float)size)));
                float ux = minX+(((float)ix+1f)*((maxX-minX)/((float)size)));
                float lz = minZ+(((float)iy)*((maxZ-minZ)/((float)size)));
                float uz = minZ+(((float)iy+1f)*((maxZ-minZ)/((float)size)));
                
                float avg = (height[ix+1+addOn][iy+addOn]+height[ix+1+addOn][iy+1+addOn]+height[ix+addOn][iy+1+addOn]+height[ix+addOn][iy+addOn])/4f;
                float normalisedHeight = (avg-minY)/(maxY-minY);
                float seaLevel = (SANDSTART*(maxY-minY))+minY;
                float aP = height[ix+1+addOn][iy+addOn];
                float bP = height[ix+1+addOn][iy+1+addOn];
                float cP = height[ix+addOn][iy+1+addOn];
                float dP = height[ix+addOn][iy+addOn];
                int water = 0;
                if (aP > seaLevel)
                {    
                    aP = seaLevel;
                    water++;
                }
                if (bP > seaLevel)
                {    
                    bP = seaLevel;
                    water++;
                }

                if (cP > seaLevel)
                {    
                    cP = seaLevel;
                    water++;
                }

                if (dP > seaLevel)
                {    
                    dP = seaLevel;
                    water++;
                }
                if (water == 3)
                {
                    Mesh m = new Mesh(2);
                    if (aP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            new Vec3D(lx, dP, lz), 
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(ux, bP, uz),
                            new Vec3D(lx, cP, uz),
                            new Vec3D(lx, dP, lz),        
                            WATER
                        );
                        mesh = mesh.addMesh(m);
                    }
                    if (bP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            new Vec3D(lx, cP, uz),
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(lx, cP, uz),
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, aP, lz),
                            WATER
                        );
                        mesh = mesh.addMesh(m);
                    }
                    if (cP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(lx, cP, uz),
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, bP, uz),
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            WATER
                        );
                        mesh = mesh.addMesh(m);
                    }
                    if (dP != seaLevel)
                    {
                        m.TRIS[0] = new Triangle
                        (
                            new Vec3D(lx, dP, lz), 
                            new Vec3D(ux, aP, lz),
                            new Vec3D(lx, cP, uz),
                            SAND
                        );
                        
                        m.TRIS[1] = new Triangle
                        (
                            new Vec3D(lx, cP, uz),
                            new Vec3D(ux, aP, lz),
                            new Vec3D(ux, bP, uz),
                            WATER
                        );
                        mesh = mesh.addMesh(m);
                    }
                    
                }
                else
                {
                Color c;
                if (water == 4)
                {
                    c = WATER;
                }
                else if (normalisedHeight > GRASSSTART)
                {
                    c = SAND;
                }
                else if (normalisedHeight > LOWROCKSTART)
                {
                    float rand = getRandom((chunkX*FIELDDIVISIONS)+(ix/(size/FIELDDIVISIONS)), (chunkY*FIELDDIVISIONS)+(iy/(size/FIELDDIVISIONS)), seed+10, 0, 1);
                    if (rand > 0.9)
                    {
                        c = FIELD1;
                    }
                    else if (rand > 0.8)
                    {
                        c = FIELD2;
                    }
                    else
                    {
                        c = GRASS;
                    }
                }
                else if (normalisedHeight > HIGHROCKSTART)
                {
                    c = ROCKLOW;
                }
                else if (normalisedHeight > SNOWSTART)
                {
                    c = ROCKHIGH;
                }
                else
                {
                    c = SNOW;
                }
                mesh = mesh.addMesh(Mesh.generateFace
                (new Vec3D[] 
                    {
                        new Vec3D(ux, aP, lz),
                        new Vec3D(ux, bP, uz),
                        new Vec3D(lx, cP, uz),
                        new Vec3D(lx, dP, lz),
                    }, c, true
                ));
                }
            }
        }
        
        for (int i = 0; i < NUMBEROFTREES; i++)
        {
            int x = (int)getRandom(chunkX, chunkY, seed+i, addOn, size+addOn);
            int z = (int)getRandom(chunkX, chunkY, seed+i+1, addOn, size+addOn);
            float baseHeight = height[x][z];
            
            float lx = minX+(((float)x-addOn)*((maxX-minX)/((float)size)));
            float lz = minZ+(((float)z-addOn)*((maxZ-minZ)/((float)size)));
            
            float rand = getRandom((chunkX*FIELDDIVISIONS)+((x-addOn)/(size/FIELDDIVISIONS)), (chunkY*FIELDDIVISIONS)+((z-addOn)/(size/FIELDDIVISIONS)), seed+10, 0, 1);
            
            float normalisedHeight = (baseHeight-minY)/(maxY-minY);
            int shift;
            if (normalisedHeight > SANDSTART || normalisedHeight < HIGHROCKSTART)
            {
                shift = 100;
            }
            else
            {
                shift = 0;
            }

            
            Mesh t = new Mesh(6);
            
            if ((rand > 0.8 && normalisedHeight < GRASSSTART && normalisedHeight > LOWROCKSTART))
            {
                t.TRIS[0] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    GRASSCOLOUR
                );
                t.TRIS[1] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz),
                    GRASSCOLOUR
                );
                t.TRIS[2] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz+GRASSSPACING),
                    GRASSCOLOUR
                );
                t.TRIS[3] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx+GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    new Vec3D(GRASSOFFSET+lx-GRASSWIDTH, baseHeight+shift, GRASSOFFSET+lz+GRASSSPACING),
                    GRASSCOLOUR
                );
                t.TRIS[4] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz-GRASSWIDTH),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz+GRASSWIDTH),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    GRASSCOLOUR
                );
                t.TRIS[5] = new Triangle
                (
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift-GRASSHEIGHT, GRASSOFFSET+lz),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz+GRASSWIDTH),
                    new Vec3D(GRASSOFFSET+lx+GRASSSPACING, baseHeight+shift, GRASSOFFSET+lz-GRASSWIDTH),
                    GRASSCOLOUR
                );
            }
            else 
            {
                if (getRandom(chunkX, chunkY, seed+i, 0, 1) > 0.8)
                {
                    Color color;
                    if (getRandom(chunkX, chunkY, seed+i+1, 0, 1) > 0.5)
                    { 
                        color = BUSHCOLOUR;
                    }
                    else
                    {
                        color = ROCKLOW;
                    }
                    shift-=2;
                    t.TRIS[0] = new Triangle
                    (
                        new Vec3D(lx-BUSHWIDTH-BUSHOFFSET, baseHeight+shift, lz-BUSHWIDTH+BUSHOFFSET),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz-BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        color
                    );
                    t.TRIS[1] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        color
                    );
                    t.TRIS[2] = new Triangle
                    (
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH-BUSHOFFSET, baseHeight+shift, lz-BUSHWIDTH+BUSHOFFSET),
                        color
                    );
                    t.TRIS[3] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz-BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        color
                    );
                    t.TRIS[4] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH-BUSHOFFSET, baseHeight+shift, lz-BUSHWIDTH+BUSHOFFSET),
                        color
                    );
                    t.TRIS[5] = new Triangle
                    (
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift, lz+BUSHWIDTH),
                        new Vec3D(lx-BUSHWIDTH, baseHeight+shift-BUSHHEIGHT, lz+BUSHWIDTH),
                        new Vec3D(lx+BUSHWIDTH, baseHeight+shift-BUSHHEIGHT-BUSHOFFSET, lz-BUSHWIDTH),
                        color
                    );
                }
                else
                {
                    t.TRIS[0] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift, lz),
                        new Vec3D(lx-(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)),
                        new Vec3D(lx+(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)),
                        TREEBARK
                    );
                    t.TRIS[1] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift, lz),
                        new Vec3D(lx, baseHeight+shift, lz+(2f*ROOT3*TRUNKWIDTH)),
                        new Vec3D(lx-(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)), 
                        TREEBARK
                    );
                    t.TRIS[2] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift, lz),
                        new Vec3D(lx+(3f*TRUNKWIDTH), baseHeight+shift, lz-(ROOT3*TRUNKWIDTH)),
                        new Vec3D(lx, baseHeight+shift, lz+(2f*ROOT3*TRUNKWIDTH)), 
                        TREEBARK
                    );
                    float theta = getRandom(chunkX, chunkY, seed+i+1, 0, 2f*(float)Math.PI);
                    float cosy = (float)Math.cos(theta);
                    float siny = (float)Math.sin(theta);
                    float cosx = 1;
                    float sinx = 0;

                    t.TRIS[3] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift + (cosx*(-LEAVESHEIGHT)), lz + (sinx*(-LEAVESHEIGHT))),
                        new Vec3D(lx+(cosy*(-(3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*(-(3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz+(cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*(-(3f*LEAVESWIDTH)))), 
                        new Vec3D(lx+(cosy*(+(3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*(+(3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz+(cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*((3f*LEAVESWIDTH)))),
                        TREELEAVES
                    );
                    t.TRIS[4] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift + (cosx*(-LEAVESHEIGHT)), lz + (sinx*(-LEAVESHEIGHT))),
                        new Vec3D(lx + (siny*(+(2f*ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift - (sinx*cosy*((2f*ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*((2f*ROOT3*LEAVESWIDTH)))),
                        new Vec3D(lx+(cosy*(-(3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*(-(3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*(-(3f*LEAVESWIDTH)))),
                        TREELEAVES
                    );
                    t.TRIS[5] = new Triangle
                    (
                        new Vec3D(lx, baseHeight-TREEHEIGHT+shift + (cosx*(-LEAVESHEIGHT)), lz + (sinx*(-LEAVESHEIGHT))),
                        new Vec3D(lx+(cosy*((3f*LEAVESWIDTH))) + (siny*(-(ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift + (sinx*siny*((3f*LEAVESWIDTH))) - (sinx*cosy*(-(ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*(-(ROOT3*LEAVESWIDTH))) - (cosx*siny*((3f*LEAVESWIDTH)))),
                        new Vec3D(lx + (siny*((2f*ROOT3*LEAVESWIDTH))), baseHeight-TREEHEIGHT+shift - (sinx*cosy*(+(2f*ROOT3*LEAVESWIDTH))), lz + (cosx*cosy*((2f*ROOT3*LEAVESWIDTH)))), 
                        TREELEAVES
                    );
                }
            }
            mesh = mesh.addMesh(t);
        }
        for (int i = 0; i < CLOUDCOUNT; i++)
        {
            
            float x = minX+(getRandom(chunkX, chunkY, seed+i, 0, size+1)*((maxX-minX)/((float)size)));
            float z = minZ+(getRandom(chunkX, chunkY, seed+i+1, 0, size+1)*((maxZ-minZ)/((float)size)));
            int width = (int)getRandom(chunkX, chunkY, seed+i, (maxX-minX)*CLOUDMINSIZE, (maxX-minX)*CLOUDMAXSIZE);
            int depth = (int)getRandom(chunkX, chunkY, seed+i+1, (maxZ-minZ)*CLOUDMINSIZE, (maxZ-minZ)*CLOUDMAXSIZE);
            mesh = mesh.addMesh(Mesh.generateFace
                (
                new Vec3D[]
                {
                    new Vec3D(x, CLOUDHEIGHT, z+depth),
                    new Vec3D(x+width, CLOUDHEIGHT, z+depth),
                    new Vec3D(x+width, CLOUDHEIGHT, z),
                    new Vec3D(x, CLOUDHEIGHT, z), 
                }, CLOUDCOLOR, false));
            
        }
        return mesh;
    }
    
    public static float getRandom(int x, int y, int seed, float min, float max)
    {
        final long SIZE = 123456789;
        final long SEEDCOMP = (long)seed%SIZE;
        final long XCOMP = (long)x%SIZE;
        final long YCOMP = (long)y%SIZE;
        
        final long SEED = XCOMP+(YCOMP*SIZE) + (SEEDCOMP*SIZE*SIZE);
        //System.out.println("Seed: " + SEED);
        Random generator = new Random(SEED);
        generator.nextFloat();
        generator.nextFloat();
        return min + (generator.nextFloat() * (max-min));
    }
    
    public static void generateHeightMap(float[][] heightF, float minY, float maxY, int step, float scale, int chunkX, int chunkY, int seed)
    {
        
        float[][] height = new float[heightF.length][heightF[0].length];
        
        for (int ix = 0; ix < height.length; ix += step)
        {
            for (int iy = 0; iy < height[0].length; iy += step)
            {
                height[ix][iy] = getRandom(chunkX+ix, chunkY+iy, seed, minY, maxY) * scale;
                if (ix-step >= 0 && iy-step >= 0)
                {
                    for (int ix2 = 0; ix2 <= step; ix2++)
                    {
                        for (int iy2 = 0; iy2 <= step; iy2++)
                        {
                            if (ix2 == step && iy2 == step)
                            {
                                
                            }
                            else if (ix2 == step && iy2 == 0)
                            {
                                
                            }
                            else if (ix2 == 0 && iy2 == step)
                            {
                                
                            }
                            else if (ix2 == 0 && iy2 == 0)
                            {
                                
                            }
                            else
                            {
                                float x = ((float)(step-ix2))/((float)step);
                                float y = ((float)(step-iy2))/((float)step);
                                    //System.out.println("ix2 :" + ix2 + ", iy2: " + iy2 + " x: " + x + " y: " + y);
                                float distToMinMin = 1f/(float)Math.sqrt((x*x)+(y*y));
                                float distToMinMax = 1f/(float)Math.sqrt((x*x)+((y-1f)*(y-1f)));
                                float distToMaxMin = 1f/(float)Math.sqrt(((x-1f)*(x-1f))+(y*y));
                                float distToMaxMax = 1f/(float)Math.sqrt(((x-1f)*(x-1f))+((y-1f)*(y-1f)));
                                float currentHeight = ((height[ix][iy-step]*distToMaxMin) + (height[ix-step][iy]*distToMinMax) + (height[ix][iy]*distToMaxMax) + (height[ix-step][iy-step]*distToMinMin)) / (distToMinMin+distToMinMax+distToMaxMin+distToMaxMax);
                                //System.out.println("X: " + (ix-ix2) + " Y: " + (iy-iy2) + " Value: " + currentHeight);
                                height[ix-ix2][iy-iy2] = currentHeight;
                            }
                        }
                    }
                }
            }
        }
        for (int ix = 0; ix < height.length; ix++)
        {
            for (int iy = 0; iy < height[0].length; iy++)
            {
                heightF[ix][iy] += height[ix][iy];
            }
        }
    }
                        
    final static float SNOWSTART = 0.2f;
    final static float HIGHROCKSTART = 0.25f;
    final static float LOWROCKSTART = 0.5f;
    final static float GRASSSTART = 0.7f;
    final static float SANDSTART = 0.75f;
    
    final static Color GRASS = Color.GREEN;
    final static Color SAND = new Color(244, 226, 198);
    final static Color WATER = new Color(50, 50, 200);
    final static Color FIELD1 = new Color(158, 149, 78);
    final static Color FIELD2 = Color.GREEN.darker();
    final static Color ROCKLOW = Color.GRAY;
    final static Color ROCKHIGH = Color.LIGHT_GRAY;
    final static Color SNOW = Color.WHITE;
    
    final static int FIELDDIVISIONS = 3;
    
    /*
    Cloud Parameters
    */
    final static float CLOUDHEIGHT = -900;
    final static float CLOUDMINSIZE = 1f/8f;
    final static float CLOUDMAXSIZE = 1f/2f;
    final static int CLOUDCOUNT = 3;
    final static Color CLOUDCOLOR = Color.WHITE;
    /*
    Tree parameters
    */
    final static float NUMBEROFTREES = 90;
    final static float ROOT3 = (float)Math.sqrt(3);
    final static float TREEHEIGHT = 20;
    final static float TRUNKWIDTH = 1;
    final static float LEAVESHEIGHT = 30;
    final static float LEAVESWIDTH = 6;
    final static Color TREEBARK = Color.ORANGE.darker();
    final static Color TREELEAVES = Color.GREEN.darker();
    /*
    Bush parameters
    */
    final static float BUSHWIDTH = 10;
    final static float BUSHOFFSET = 2;
    final static float BUSHHEIGHT = 15;
    final static Color BUSHCOLOUR = Color.GREEN.darker().darker();
    
    /*
    Grass parameters
    */
    final static float GRASSWIDTH = 5;
    final static float GRASSHEIGHT = 15;
    final static float GRASSSPACING = 10;
    final static float GRASSOFFSET = 10;
    final static Color GRASSCOLOUR = Color.GREEN.darker();
}
