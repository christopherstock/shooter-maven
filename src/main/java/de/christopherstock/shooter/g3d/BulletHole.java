
    package de.christopherstock.shooter.g3d;

    import  java.awt.geom.*;
    import  java.util.Vector;
    import  de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.LibInvert;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.BulletHoles;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.g3d.wall.Wall;
    import  de.christopherstock.shooter.game.objects.*;

    /*******************************************************************************************************************
    *   Represents a bullet-hole on a wall or an object.
    *******************************************************************************************************************/
    public class BulletHole
    {
        private     static          Vector<BulletHole>      bulletHoles                                     = new Vector<BulletHole>();

        private                     LibHitPoint             hitPoint                                        = null;
        private                     Mesh                    projectile                                      = null;
        private                     LibFaceEllipseWall      face                                            = null;
        private                     LibD3dsFile             projectileTemplate                              = null;
        private                     LibVertex               originalPosition                                = null;
        private                     LibVertex               position                                        = null;
        private                     float                   carriersLastFaceAngle                           = 0.0f;

        private BulletHole( LibHitPoint hitPoint, LibD3dsFile projectile )
        {
            this.hitPoint           = hitPoint;
            this.projectileTemplate = projectile;

            if (this.projectileTemplate != null )
            {
                float rotZ = this.hitPoint.horzShotAngle + 90.0f;

                //only set once .. :/
                this.projectile = new Mesh( Shooter.game.engine.d3ds.getFaces(this.projectileTemplate), this.hitPoint.vertex, 0.0f, 1.0f, LibInvert.ENo, this.hitPoint.carrier, LibTransformationMode.EOriginalsToOriginals, DrawMethod.EAlwaysDraw );
                this.projectile.translateAndRotateXYZ
                (
                    0.0f,
                    0.0f,
                    0.0f,
                     LibMath.sinDeg( rotZ ) * this.hitPoint.shot.rotX,
                    -LibMath.cosDeg( rotZ ) * this.hitPoint.shot.rotX,    //lucky punch
                    LibMath.normalizeAngle( rotZ ),
                        this.projectile.anchor,
                    LibTransformationMode.EOriginalsToTransformed
                );

                //ShooterDebug.bugfix.out( "> " +  hitPoint.shot.rotX );
            }

           //ShooterDebug.bugfix.out( "Vert face angle set to [" + vertFaceAngle + "]" );

            ShooterDebug.bulletHole.out( "new bullet hole: face-angle h [" + this.hitPoint.horzFaceAngle + "] v [" + this.hitPoint.vertFaceAngle + "]" );

            //get suitable distance to avoid overlapping bullet-holes
            float distanceHorzFromFace  = this.getSuitableDistanceFromHorzFace();
            float distanceVertFromFace  = this.getSuitableDistanceFromVertFace();
            float distX                 = -distanceHorzFromFace * -LibMath.cosDeg( hitPoint.horzFaceAngle);
            float distY                 =  distanceHorzFromFace *  LibMath.sinDeg( hitPoint.horzFaceAngle);
            float distZ                 = -distanceVertFromFace *  LibMath.sinDeg( hitPoint.vertFaceAngle);

            //get the two holes that hit the wall from both sides
            Point2D.Float pointHorz1 = new Point2D.Float( hitPoint.vertex.x + distX, hitPoint.vertex.y + distY );
            Point2D.Float pointHorz2 = new Point2D.Float( hitPoint.vertex.x - distX, hitPoint.vertex.y - distY );
            ShooterDebug.bulletHole.out( " bullet-hole: [" + pointHorz1.x + "][" + pointHorz1.y + "] [" + pointHorz2.x + "][" + pointHorz2.y + "]" );

            Point2D.Float pointVert1 = new Point2D.Float( hitPoint.shot.srcPointVert.x, hitPoint.vertex.z - distZ );
            Point2D.Float pointVert2 = new Point2D.Float( hitPoint.shot.srcPointVert.x, hitPoint.vertex.z + distZ );

            //select the nearer point
            float distanceHorz1 = (float)hitPoint.shot.srcPointHorz.distance( pointHorz1 );
            float distanceHorz2 = (float)hitPoint.shot.srcPointHorz.distance( pointHorz2 );


            Point2D.Float nearerHolePointHorz = ( distanceHorz1 < distanceHorz2 ? pointHorz1 : pointHorz2 );
            //Point2D.Float nearerHolePointHorz = ( distanceHorz1 < distanceHorz2 ? pointHorz2 : pointHorz1 );

            float distanceVert1 = (float)hitPoint.shot.srcPointVert.distance( pointVert1 );
            float distanceVert2 = (float)hitPoint.shot.srcPointVert.distance( pointVert2 );
/*
            if ( distanceVert1 < distanceVert2 )
            {
                ShooterDebug.bugfix.out( "higher is nearer" );
            }
            else
            {
                ShooterDebug.bugfix.out( "lower is nearer" );
            }
*/
            Point2D.Float nearerHolePointVert = ( distanceVert1 < distanceVert2 ? pointVert1 : pointVert2 );

            //ShooterDebug.bugfix.out( "dis v: "  + distZ );

            //assign the position of the Bullet Hole
            this.originalPosition = new LibVertex( nearerHolePointHorz.x, nearerHolePointHorz.y, nearerHolePointVert.y );
            this.position = new LibVertex( nearerHolePointHorz.x, nearerHolePointHorz.y, nearerHolePointVert.y );

            //check distance to Carrier if any
            if ( hitPoint.carrier != null )
            {
                this.carriersLastFaceAngle = hitPoint.carrier.getCarriersFaceAngle();
            }

            //setup the face
            this.updateFace( true );
        }

        private void updateFace( boolean newRandomTexRot )
        {
            this.face = new LibFaceEllipseWall
            (
                ShooterDebug.face,
                    this.hitPoint.bulletHoleTexture,
                    this.hitPoint.horzFaceAngle,
                    this.hitPoint.vertFaceAngle,
                    this.position.x,
                    this.position.y,
                    this.position.z,
                    this.hitPoint.shot.bulletHoleSize.size,
                    this.hitPoint.shot.bulletHoleSize.size,
                ( newRandomTexRot ?  LibMath.getRandom( 0, 360 ) : this.face.iTextureRotation ),
                ShooterSetting.Performance.ELLIPSE_SEGMENTS
            );
        }

        public static void drawAll()
        {
            //draw all points
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                if ( bulletHole.projectileTemplate != null )
                {
                    //projectile
                    if ( bulletHole.projectile != null )
                    {
                        bulletHole.projectile.draw();
                    }
                }
                else
                {
                    //bullet hole
                    bulletHole.face.draw();
                }
            }
        }

        public static void translateAll(LibGameObject bhc, float transX, float transY, float transZ )
        {
            //browse all bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //check if this bullet hole belongs to the specified bot
                if ( bulletHole.hitPoint.carrier == bhc )
                {
                    //translate it!
                    bulletHole.position.x           += transX;
                    bulletHole.position.y           += transY;
                    bulletHole.position.z           += transZ;
                    bulletHole.originalPosition.x   += transX;
                    bulletHole.originalPosition.y   += transY;
                    bulletHole.originalPosition.z   += transZ;
                    bulletHole.updateFace( false );

                    //translate projectile
                    if ( bulletHole.projectile != null )
                    {
                        bulletHole.projectile.translate( transX, transY, transZ, LibTransformationMode.ETransformedToTransformed );
                    }
                }
            }
        }

        //only z-rotation are allowed for bullet-holes!
        public static void rotateForBot(Bot bhc, float rotZ )
        {
            //browse all bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //check if this bullet hole belongs to the specified bot
                if ( bulletHole.hitPoint.carrier == bhc )
                {
                    //reverse last translation
                    LibMatrix transformationMatrix = new LibMatrix( 0.0f, 0.0f, rotZ - bulletHole.carriersLastFaceAngle);
                    LibVertex translatedHitPoint = transformationMatrix.transformVertexF( bulletHole.position, bhc.getAnchor() );

                    //assign new face angle
                    bulletHole.hitPoint.horzFaceAngle += ( rotZ - bulletHole.carriersLastFaceAngle);

                    //asssign new hit-point and update face
                    bulletHole.position = translatedHitPoint;
                    bulletHole.updateFace( false );

                    //assign new rotations
                    bulletHole.carriersLastFaceAngle = rotZ;
                }
            }
        }

        public static void rotateForWall(Wall bhc, float rotX, float rotY, float rotZ )
        {
            //browse all bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //check if this bullet hole belongs to the specified mersh
                if ( bulletHole.hitPoint.carrier == bhc )
                {
                    //rotate bulletHole for mesh
                    bulletHole.rotateAroundVertex( bhc.getAnchor(), rotX, rotY, rotZ );

                    //rotate projectile
                    if ( bulletHole.projectile != null )
                    {
                        bulletHole.projectile.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, rotX, rotY, rotZ, bhc.getAnchor(), LibTransformationMode.ETransformedToTransformed );
                    }
                }
            }
        }

        public static void removeForWall(Wall bhc )
        {
            //browse all bullet-holes reversed
            for ( int i = BulletHole.bulletHoles.size() - 1; i >= 0; --i )
            {
                //check if this bullet hole belongs to the specified mersh
                if ( BulletHole.bulletHoles.elementAt( i ).hitPoint.carrier == bhc )
                {
                    //remove this bullet hole
                    BulletHole.bulletHoles.removeElementAt( i );
                }
            }
        }

        public static void darkenAll(Wall bhc, float opacity )
        {
            //browse all bullet-holes reversed
            for ( int i = BulletHole.bulletHoles.size() - 1; i >= 0; --i )
            {
                //check if this bullet hole belongs to the specified mersh
                if ( BulletHole.bulletHoles.elementAt( i ).hitPoint.carrier == bhc )
                {
                    //darken bulletHole
                    BulletHole.bulletHoles.elementAt( i ).darken( opacity );
                }
            }
        }

        private void darken(float opacity )
        {
            this.face.darken( opacity );
            //if ( projectile != null ) projectile.darkenAllFaces( opacity, useRandomSubstract, useRandomAdd, maxSubstract, maxAdd );
        }

        private float getSuitableDistanceFromHorzFace()
        {
            float dis = BulletHoles.MIN_DISTANCE_FROM_FACE;

            //check distance to all other bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //should be distance3d :/
                if ( new Point2D.Float( bulletHole.hitPoint.vertex.x, bulletHole.hitPoint.vertex.y ).distance( new Point2D.Float(this.hitPoint.vertex.x, this.hitPoint.vertex.y ) ) < BulletHoles.MIN_POINT_DISTANCE_FOR_SAME_LAYER_MULTIPLIER * this.hitPoint.shot.bulletHoleSize.size )
                {
                    dis += BulletHoles.STEP_DISTANCE_FROM_FACE;
                }
            }

            return dis;
        }

        private float getSuitableDistanceFromVertFace()
        {
            float dis = BulletHoles.MIN_DISTANCE_FROM_FACE;

            //check distance to all other bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //should be distance3d :/
                if ( Math.abs( bulletHole.hitPoint.vertex.z - this.hitPoint.vertex.z ) < BulletHoles.MIN_POINT_DISTANCE_FOR_SAME_LAYER_MULTIPLIER * this.hitPoint.shot.bulletHoleSize.size )
                {
                    dis += BulletHoles.STEP_DISTANCE_FROM_FACE;
                }
            }

            return dis;
        }

        private void rotateAroundVertex(LibVertex vertex, float rotX, float rotY, float rotZ )
        {
            //increase saved rot z
            ShooterDebug.bulletHole.out( " turn for [" + rotZ + "]" );

            //transform
            LibMatrix transformationMatrix = new LibMatrix( rotX, rotY, rotZ );
            LibVertex translatedHitPoint   = transformationMatrix.transformVertexF(this.position, vertex );

            //increase face angle for rotZ
            this.hitPoint.horzFaceAngle += rotZ;
            ShooterDebug.bulletHole.out( " setting bullet hole angle to [" + this.hitPoint.horzFaceAngle + "]" );

            //asssign new hit-point and update face
            this.position = translatedHitPoint;

            //update the bullet hole
            this.updateFace( false );
/*
            //projectile
            if ( projectile != null )
            {
                projectile = null;
            }
*/
        }

        public static void clearBulletHoles()
        {
            BulletHole.bulletHoles.removeAllElements();
        }

        public static void addBulletHole(LibHitPoint hitPoint, LibD3dsFile aProjectile )
        {
            //add to bullet-hole-stack, prune stack if overflowing
            BulletHole.bulletHoles.add( new BulletHole( hitPoint, aProjectile ) );
            if ( BulletHole.bulletHoles.size() > ShooterSetting.Performance.MAX_NUMBER_BULLET_HOLES ) BulletHole.bulletHoles.removeElementAt( 0 );

            ShooterDebug.bulletHole.out( " new bullet hole count: [" + BulletHole.bulletHoles.size() + "]" );
        }
    }
