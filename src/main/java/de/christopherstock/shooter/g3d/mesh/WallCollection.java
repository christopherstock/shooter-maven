
    package de.christopherstock.shooter.g3d.mesh;

    import  java.util.*;
    import de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.wall.Wall;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;

    /*******************************************************************************************************************
    *   Represents a mesh-collection.
    *******************************************************************************************************************/
    public class WallCollection extends MeshCollection
    {
        public WallCollection( Wall[] aWalls )
        {
            this( null, aWalls );
        }

        public WallCollection( Wall aAnchorWall, Wall[] aWalls )
        {
            this( aAnchorWall, aWalls, null, null, true );
        }

        public WallCollection( Wall aAnchorWall, Wall[] aWalls, LibVertex anchorWallPostTrans, LibVertex anchorWallPostRot, boolean propagadeDestroyToChilds )
        {
            if ( aWalls == null ) aWalls = new Wall[] {};
            this.iMeshes = aWalls;

            //translate all walls by anchorWall if specified
            if ( aAnchorWall != null )
            {
                //assign child walls to destroy if this wall is destroyed
                if ( propagadeDestroyToChilds ) aAnchorWall.assignChildWallsToDestroy( aWalls );

                Vector<Mesh> newWalls = new Vector<Mesh>();
                newWalls.add( aAnchorWall );

                LibVertex anchorWallAnk = aAnchorWall.getAnchor();
                for ( Wall wall : aWalls )
                {
                    //fixed double translation :p
                    LibVertex newWallAnk = new LibVertex( 0.0f, 0.0f, 0.0f ); //wall.getAnchor().copy();

                    //translate and rotate anchor by room offset and set as new anchor
                    newWallAnk.translate( anchorWallAnk );
                    newWallAnk.rotateXYZ( 0.0f, 0.0f, aAnchorWall.iStartupRotZ, aAnchorWall.getAnchor() );

                    //set anchor and perform translation on originals
                    wall.setNewAnchor( newWallAnk, true, LibTransformationMode.EOriginalsToOriginals );

                    wall.iStartupRotZ = wall.iStartupRotZ + aAnchorWall.iStartupRotZ;

                    //turn wall by new anchor for room rotation
                    wall.translateAndRotateXYZ
                    (
                        0.0f,
                        0.0f,
                        0.0f,
                        0.0f,
                        0.0f,
                        aAnchorWall.iStartupRotZ,
                        newWallAnk,
                        LibTransformationMode.EOriginalsToOriginals
                    );

                    //ShooterDebug.bugfix.out("new ank: ["+newAnk+"]");
                    newWalls.add( wall );
                }
                this.iMeshes = newWalls.toArray(this.iMeshes);
                //ShooterDebug.bugfix.out("new wc: ["+walls.length+"]");

                //perform post-translation on anchor wall
                if ( anchorWallPostTrans != null )
                {
                    aAnchorWall.translateAndRotateXYZ
                    (
                        anchorWallPostTrans.x,
                        anchorWallPostTrans.y,
                        anchorWallPostTrans.z,
                        anchorWallPostRot.x,
                        anchorWallPostRot.y,
                        anchorWallPostRot.z,
                        aAnchorWall.getAnchor(),
                        LibTransformationMode.EOriginalsToOriginals
                    );

                    aAnchorWall.getAnchor().translate( anchorWallPostTrans );
                }
            }
        }

        public final void launchAction( Cylinder cylinder, Gadget gadget, float faceAngle )
        {
            //launch action on all meshes
            for ( Mesh wall : this.iMeshes)
            {
                ( (Wall)wall ).launchAction( cylinder, gadget, faceAngle );
            }
        }

        public final void animate()
        {
            //animate all meshes
            for ( Mesh wall : this.iMeshes)
            {
                ( (Wall)wall ).animate();
            }
        }
    }
