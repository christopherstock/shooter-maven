
    package de.christopherstock.shooter.g3d;

    import  java.awt.geom.*;
    import  java.util.Vector;

    import de.christopherstock.lib.LibTransformationMode;
    import de.christopherstock.lib.LibInvert;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.BulletHoles;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.g3d.wall.Wall;
    import  de.christopherstock.shooter.game.objects.*;

    /*******************************************************************************************************************
    *   Represents a bullet-hole on a wall or an object.
    *******************************************************************************************************************/
    public class BulletHole
    {
        private     static          Vector<BulletHole>      bulletHoles                                     = new Vector<BulletHole>();

        private                     LibHitPoint             iHitPoint                                       = null;
        private                     Mesh                    iProjectile                                     = null;
        private                     LibFaceEllipseWall      iFace                                           = null;
        private                     LibD3dsFile             iProjectileTemplate                             = null;
        private                     LibVertex               iOriginalPosition                               = null;
        private                     LibVertex               iPosition                                       = null;
        private                     float                   iCarriersLastFaceAngle                          = 0.0f;

        private BulletHole(LibHitPoint aHitPoint, LibD3dsFile aProjectile)
        {
            this.iHitPoint = aHitPoint;
            this.iProjectileTemplate = aProjectile;

            if (this.iProjectileTemplate != null )
            {
                float rotZ = this.iHitPoint.horzShotAngle + 90.0f;

                //only set once .. :/
                this.iProjectile = new Mesh( Shooter.game.engine.d3ds.getFaces(this.iProjectileTemplate), this.iHitPoint.vertex, 0.0f, 1.0f, LibInvert.ENo, this.iHitPoint.carrier, LibTransformationMode.EOriginalsToOriginals, DrawMethod.EAlwaysDraw );
                this.iProjectile.translateAndRotateXYZ
                (
                    0.0f,
                    0.0f,
                    0.0f,
                     LibMath.sinDeg( rotZ ) * this.iHitPoint.shot.rotX,
                    -LibMath.cosDeg( rotZ ) * this.iHitPoint.shot.rotX,    //lucky punch
                    LibMath.normalizeAngle( rotZ ),
                        this.iProjectile.iAnchor,
                    LibTransformationMode.EOriginalsToTransformed
                );

                //ShooterDebug.bugfix.out( "> " +  iHitPoint.shot.rotX );
            }

           //ShooterDebug.bugfix.out( "Vert face angle set to [" + vertFaceAngle + "]" );

            ShooterDebug.bulletHole.out( "new bullet hole: face-angle h [" + this.iHitPoint.horzFaceAngle + "] v [" + this.iHitPoint.vertFaceAngle + "]" );

            //get suitable distance to avoid overlapping bullet-holes
            float distanceHorzFromFace  = this.getSuitableDistanceFromHorzFace();
            float distanceVertFromFace  = this.getSuitableDistanceFromVertFace();
            float distX                 = -distanceHorzFromFace * -LibMath.cosDeg( aHitPoint.horzFaceAngle);
            float distY                 =  distanceHorzFromFace *  LibMath.sinDeg( aHitPoint.horzFaceAngle);
            float distZ                 = -distanceVertFromFace *  LibMath.sinDeg( aHitPoint.vertFaceAngle);

            //get the two holes that hit the wall from both sides
            Point2D.Float pointHorz1 = new Point2D.Float( aHitPoint.vertex.x + distX, aHitPoint.vertex.y + distY );
            Point2D.Float pointHorz2 = new Point2D.Float( aHitPoint.vertex.x - distX, aHitPoint.vertex.y - distY );
            ShooterDebug.bulletHole.out( " bullet-hole: [" + pointHorz1.x + "][" + pointHorz1.y + "] [" + pointHorz2.x + "][" + pointHorz2.y + "]" );

            Point2D.Float pointVert1 = new Point2D.Float( aHitPoint.shot.srcPointVert.x, aHitPoint.vertex.z - distZ );
            Point2D.Float pointVert2 = new Point2D.Float( aHitPoint.shot.srcPointVert.x, aHitPoint.vertex.z + distZ );

            //select the nearer point
            float distanceHorz1 = (float)aHitPoint.shot.srcPointHorz.distance( pointHorz1 );
            float distanceHorz2 = (float)aHitPoint.shot.srcPointHorz.distance( pointHorz2 );


            Point2D.Float nearerHolePointHorz = ( distanceHorz1 < distanceHorz2 ? pointHorz1 : pointHorz2 );
            //Point2D.Float nearerHolePointHorz = ( distanceHorz1 < distanceHorz2 ? pointHorz2 : pointHorz1 );

            float distanceVert1 = (float)aHitPoint.shot.srcPointVert.distance( pointVert1 );
            float distanceVert2 = (float)aHitPoint.shot.srcPointVert.distance( pointVert2 );
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
            this.iOriginalPosition = new LibVertex( nearerHolePointHorz.x, nearerHolePointHorz.y, nearerHolePointVert.y );
            this.iPosition = new LibVertex( nearerHolePointHorz.x, nearerHolePointHorz.y, nearerHolePointVert.y );

            //check distance to Carrier if any
            if ( aHitPoint.carrier != null )
            {
                this.iCarriersLastFaceAngle = aHitPoint.carrier.getCarriersFaceAngle();
            }

            //setup the face
            this.updateFace( true );
        }

        private void updateFace(boolean newRandomTexRot )
        {
            this.iFace = new LibFaceEllipseWall
            (
                ShooterDebug.face,
                    this.iHitPoint.bulletHoleTexture,
                    this.iHitPoint.horzFaceAngle,
                    this.iHitPoint.vertFaceAngle,
                    this.iPosition.x,
                    this.iPosition.y,
                    this.iPosition.z,
                    this.iHitPoint.shot.bulletHoleSize.size,
                    this.iHitPoint.shot.bulletHoleSize.size,
                ( newRandomTexRot ?  LibMath.getRandom( 0, 360 ) : this.iFace.iTextureRotation ),
                ShooterSetting.Performance.ELLIPSE_SEGMENTS
            );
        }

        public static void drawAll()
        {
            //draw all points
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                if ( bulletHole.iProjectileTemplate != null )
                {
                    //projectile
                    if ( bulletHole.iProjectile != null )
                    {
                        bulletHole.iProjectile.draw();
                    }
                }
                else
                {
                    //bullet hole
                    bulletHole.iFace.draw();
                }
            }
        }

        public static void translateAll(LibGameObject bhc, float transX, float transY, float transZ )
        {
            //browse all bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //check if this bullet hole belongs to the specified bot
                if ( bulletHole.iHitPoint.carrier == bhc )
                {
                    //translate it!
                    bulletHole.iPosition.x           += transX;
                    bulletHole.iPosition.y           += transY;
                    bulletHole.iPosition.z           += transZ;
                    bulletHole.iOriginalPosition.x   += transX;
                    bulletHole.iOriginalPosition.y   += transY;
                    bulletHole.iOriginalPosition.z   += transZ;
                    bulletHole.updateFace( false );

                    //translate projectile
                    if ( bulletHole.iProjectile != null )
                    {
                        bulletHole.iProjectile.translate( transX, transY, transZ, LibTransformationMode.ETransformedToTransformed );
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
                if ( bulletHole.iHitPoint.carrier == bhc )
                {
                    //reverse last translation
                    LibMatrix transformationMatrix = new LibMatrix( 0.0f, 0.0f, rotZ - bulletHole.iCarriersLastFaceAngle );
                    LibVertex translatedHitPoint = transformationMatrix.transformVertexF( bulletHole.iPosition, bhc.getAnchor() );

                    //assign new face angle
                    bulletHole.iHitPoint.horzFaceAngle += ( rotZ - bulletHole.iCarriersLastFaceAngle );

                    //asssign new hit-point and update face
                    bulletHole.iPosition = translatedHitPoint;
                    bulletHole.updateFace( false );

                    //assign new rotations
                    bulletHole.iCarriersLastFaceAngle = rotZ;
                }
            }
        }

        public static void rotateForWall(Wall bhc, float rotX, float rotY, float rotZ )
        {
            //browse all bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //check if this bullet hole belongs to the specified mersh
                if ( bulletHole.iHitPoint.carrier == bhc )
                {
                    //rotate bulletHole for mesh
                    bulletHole.rotateAroundVertex( bhc.getAnchor(), rotX, rotY, rotZ );

                    //rotate projectile
                    if ( bulletHole.iProjectile != null )
                    {
                        bulletHole.iProjectile.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, rotX, rotY, rotZ, bhc.getAnchor(), LibTransformationMode.ETransformedToTransformed );
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
                if ( BulletHole.bulletHoles.elementAt( i ).iHitPoint.carrier == bhc )
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
                if ( BulletHole.bulletHoles.elementAt( i ).iHitPoint.carrier == bhc )
                {
                    //darken bulletHole
                    BulletHole.bulletHoles.elementAt( i ).darken( opacity );
                }
            }
        }

        private void darken(float opacity )
        {
            this.iFace.darken( opacity );
            //if ( projectile != null ) projectile.darkenAllFaces( opacity, useRandomSubstract, useRandomAdd, maxSubstract, maxAdd );
        }

        private float getSuitableDistanceFromHorzFace()
        {
            float dis = BulletHoles.MIN_DISTANCE_FROM_FACE;

            //check distance to all other bullet-holes
            for ( BulletHole bulletHole : BulletHole.bulletHoles )
            {
                //should be distance3d :/
                if ( new Point2D.Float( bulletHole.iHitPoint.vertex.x, bulletHole.iHitPoint.vertex.y ).distance( new Point2D.Float(this.iHitPoint.vertex.x, this.iHitPoint.vertex.y ) ) < BulletHoles.MIN_POINT_DISTANCE_FOR_SAME_LAYER_MULTIPLIER * this.iHitPoint.shot.bulletHoleSize.size )
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
                if ( Math.abs( bulletHole.iHitPoint.vertex.z - this.iHitPoint.vertex.z ) < BulletHoles.MIN_POINT_DISTANCE_FOR_SAME_LAYER_MULTIPLIER * this.iHitPoint.shot.bulletHoleSize.size )
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
            LibVertex translatedHitPoint   = transformationMatrix.transformVertexF(this.iPosition, vertex );

            //increase face angle for rotZ
            this.iHitPoint.horzFaceAngle += rotZ;
            ShooterDebug.bulletHole.out( " setting bullet hole angle to [" + this.iHitPoint.horzFaceAngle + "]" );

            //asssign new hit-point and update face
            this.iPosition = translatedHitPoint;

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
