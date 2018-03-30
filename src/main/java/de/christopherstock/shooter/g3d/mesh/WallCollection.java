
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
        public WallCollection( Wall[] walls )
        {
            this( null, walls );
        }

        public WallCollection( Wall anchorWall, Wall[] walls )
        {
            this( anchorWall, walls, null, null, true );
        }

        public WallCollection( Wall anchorWall, Wall[] walls, LibVertex anchorWallPostTrans, LibVertex anchorWallPostRot, boolean propagadeDestroyToChilds )
        {
            if ( walls == null ) walls = new Wall[] {};
            this.meshes = walls;

            //translate all walls by anchorWall if specified
            if ( anchorWall != null )
            {
                //assign child walls to destroy if this wall is destroyed
                if ( propagadeDestroyToChilds ) anchorWall.assignChildWallsToDestroy( walls );

                Vector<Mesh> newWalls = new Vector<Mesh>();
                newWalls.add( anchorWall );

                LibVertex anchorWallAnk = anchorWall.getAnchor();
                for ( Wall wall : walls )
                {
                    //fixed double translation :p
                    LibVertex newWallAnk = new LibVertex( 0.0f, 0.0f, 0.0f ); //wall.getAnchor().copy();

                    //translate and rotate anchor by room offset and set as new anchor
                    newWallAnk.translate( anchorWallAnk );
                    newWallAnk.rotateXYZ( 0.0f, 0.0f, anchorWall.startupRotZ, anchorWall.getAnchor() );

                    //set anchor and perform translation on originals
                    wall.setNewAnchor( newWallAnk, true, LibTransformationMode.EOriginalsToOriginals );

                    wall.startupRotZ = wall.startupRotZ + anchorWall.startupRotZ;

                    //turn wall by new anchor for room rotation
                    wall.translateAndRotateXYZ
                    (
                        0.0f,
                        0.0f,
                        0.0f,
                        0.0f,
                        0.0f,
                        anchorWall.startupRotZ,
                        newWallAnk,
                        LibTransformationMode.EOriginalsToOriginals
                    );

                    //ShooterDebug.bugfix.out("new ank: ["+newAnk+"]");
                    newWalls.add( wall );
                }
                this.meshes = newWalls.toArray(this.meshes);
                //ShooterDebug.bugfix.out("new wc: ["+walls.length+"]");

                //perform post-translation on anchor wall
                if ( anchorWallPostTrans != null )
                {
                    anchorWall.translateAndRotateXYZ
                    (
                        anchorWallPostTrans.x,
                        anchorWallPostTrans.y,
                        anchorWallPostTrans.z,
                        anchorWallPostRot.x,
                        anchorWallPostRot.y,
                        anchorWallPostRot.z,
                        anchorWall.getAnchor(),
                        LibTransformationMode.EOriginalsToOriginals
                    );

                    anchorWall.getAnchor().translate( anchorWallPostTrans );
                }
            }
        }

        public final void launchAction( Cylinder cylinder, Gadget gadget, float faceAngle )
        {
            //launch action on all meshes
            for ( Mesh wall : this.meshes)
            {
                ( (Wall)wall ).launchAction( cylinder, gadget, faceAngle );
            }
        }

        public final void animate()
        {
            //animate all meshes
            for ( Mesh wall : this.meshes)
            {
                ( (Wall)wall ).animate();
            }
        }
    }
