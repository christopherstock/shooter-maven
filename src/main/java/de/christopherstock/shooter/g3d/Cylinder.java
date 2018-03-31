
    package de.christopherstock.shooter.g3d;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.ShooterSetting;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.base.ShooterTexture.BotTex;
    import  de.christopherstock.shooter.level.*;

    /*******************************************************************************************************************
    *   Represents a cylinder.
    *******************************************************************************************************************/
    public class Cylinder implements LibGeomObject, LibCylinder
    {
        private     static  final   int                     VERTICAL_DEBUG_SLICES               = 4;

        private                     LibGameObject           parentGameObject                    = null;
        private                     LibVertex               anchor                              = null;
        private                     LibDebug                debug                               = null;

        /** Current center bottom point. */
        private                     float                   radius                              = 0.0f;

        private                     int                     ellipseSegments                     = 0;

        /** Cylinder's height. */
        public                      float                   height                              = 0.0f;
        private                     boolean                 debugDrawBotCircles                 = false;
        private                     int                     collisionCheckingSteps              = 0;
        private                     float                   bottomCollisionToleranceZ           = 0.0f;
        private                     float                   minBottomCollisionToleranceZ        = 0.0f;

        /** The target position for the cylinder to move to. Will be the next pos if no collisions appear. */
        private                     LibVertex               target                              = null;

        private                     LibMaterial             material                            = null;

        public Cylinder( LibGameObject parentGameObject, LibVertex anchor, float radius, float height, int collisionCheckingSteps, LibDebug debug, boolean debugDrawBotCircles, float bottomCollisionToleranceZ, float minBottomCollisionToleranceZ, int ellipseSegments, LibMaterial material )
        {
            this.parentGameObject = parentGameObject;
            this.anchor = anchor;
            this.radius = radius;
            this.height = height;
            this.collisionCheckingSteps = collisionCheckingSteps;
            this.debug = debug;
            this.debugDrawBotCircles = debugDrawBotCircles;
            this.bottomCollisionToleranceZ = bottomCollisionToleranceZ;
            this.minBottomCollisionToleranceZ = minBottomCollisionToleranceZ;
            this.ellipseSegments = ellipseSegments;
            this.material = material;
        }

        private void update(float pX, float pY, float pZ, float newHeight)
        {
            this.anchor = new LibVertex( pX, pY, pZ );

            //unchanged
            //radius = RADIUS;
            this.height = newHeight;

        }

        public final void drawDebug()
        {
            if (this.debugDrawBotCircles)
            {
                //Debug.bot.out( "drawing bot .." );
                for ( int i = 0; i <= VERTICAL_DEBUG_SLICES; ++i )
                {
                    new LibFaceEllipseFloor(this.debug, null, LibColors.ERed, this.anchor.x, this.anchor.y, this.anchor.z + ( i * this.height / VERTICAL_DEBUG_SLICES ), this.radius, this.radius, this.ellipseSegments).draw();
                }
            }
        }

        public final Point2D.Float getCenterHorz()
        {
            return new Point2D.Float(this.anchor.x, this.anchor.y );
        }

        public final void setNewAnchor( LibVertex newAnk, boolean b, LibTransformationMode tm )
        {
            this.anchor.x = newAnk.x;
            this.anchor.y = newAnk.y;
            this.anchor.z = newAnk.z;
        }

        public final void translate( float x, float y, float z, LibTransformationMode tm )
        {
            this.anchor.translate( x, y, z );
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> ret = new Vector<LibHitPoint>();
            LibHitPoint hp = this.launchAShot( shot );
            if ( hp != null ) ret.addElement( hp );
            return ret;
        }

        /**
        *   Workaround - returning a Vector of HitPoints here will result in:
        *
        *   Exception in thread "Thread-3" java.lang.NullPointerException
        *   at java.util.Vector.addAll(Unknown Source)
        */
        private LibHitPoint launchAShot(LibShot shot )
        {
            //if ( shot.origin == ShotOrigin.EEnemies ) ShooterDebug.bugfix.out( "launch shot: [" + "on cylinder" + "]" );

            //check horizontal intersection
            Point2D.Float   centerHorz              = new Point2D.Float(this.anchor.x, this.anchor.y );
            boolean         horizontalIntersection  = shot.lineShotHorz.ptSegDist( centerHorz ) <= this.radius;
            if ( horizontalIntersection )
            {
                //if ( shot.origin == ShotOrigin.EEnemies ) ShooterDebug.bugfix.out( " player hit" );

                this.debug.out( "bot horizontal hit !!" );

                //get horizontal intersecttion
                Point2D.Float intersectionPointHorz = LibMathGeometry.findLineCircleIntersection( shot.lineShotHorz, this.getCircle() );
                this.debug.out( "interP: ["+intersectionPointHorz+",]" );

                //get angle from horz hitPoint to shot-src-point
                float           angleCenterToHitPointHorz = LibMath.getAngleCorrect( centerHorz, intersectionPointHorz );
                float           angleHitPointHorzToCenter = angleCenterToHitPointHorz - 180.0f;

                float           exactDistanceHorz       = (float)shot.srcPointHorz.distance( intersectionPointHorz );
                float           shotAngleHorz           = LibMath.getAngleCorrect( shot.srcPointHorz, new Point2D.Float( (float)centerHorz.getX(), (float)centerHorz.getY() ) );        //get angle between player and hit-point
                float           invertedShotAngleHorz   = 360.0f - ( shotAngleHorz - 180.0f  );              //get opposite direction of shot
                float           sliverAngleHorz         = shotAngleHorz /* - faceAngleHorz */ * 2;              //get Sliver angle

                this.debug.out( "exactDistanceHorz:     [" + exactDistanceHorz       + "]" );
                this.debug.out( "shotAngleHorz:         [" + shotAngleHorz           + "]" );
                this.debug.out( "invertedShotAngleHorz: [" + invertedShotAngleHorz   + "]" );
                this.debug.out( "SliverAngleHorz:       [" + sliverAngleHorz         + "]" );

                //calculate face's vertical collision line
                Line2D.Float collisionLineVert = new Line2D.Float( new Point2D.Float( exactDistanceHorz, this.anchor.z ), new Point2D.Float( exactDistanceHorz, this.anchor.z + this.height) );
                this.debug.out( "bot's collision line vert is: [" + collisionLineVert + "]" );

                if ( !shot.lineShotVert.intersectsLine( collisionLineVert ) )
                {
                    this.debug.out( "VERTICAL FACE MISSED!" );
                    return null;
                }

                //get intersection point for the vertical axis
                this.debug.out( "VERTICAL FACE HIT!" );
                Point2D.Float   intersectionPointVert   = LibMathGeometry.findLineSegmentIntersection( shot.lineShotVert, collisionLineVert, this.debug);
                float           z                       = intersectionPointVert.y;
                this.debug.out( ">> INTERSECTION POINT VERT: " + intersectionPointVert );

                //get vertical values
                float exactDistanceVert = (float)shot.srcPointVert.distance( intersectionPointVert );       //get exact distance

                this.debug.out( ">> EXACT DISTANCE VERT: " + exactDistanceVert );
//                debug.out( ">> SHOT-ANGLE-VERT: " + shotAngleVert );
//                debug.out( ">> SLIVER-ANGLE-VERT: " + SliverAngleVert );

                //return hit point
                return new LibHitPoint
                (
                    shot,
                        this.parentGameObject,
                        this.material.getBulletHoleTexture().getMetaData(),
                    BotTex.ESkinBrown.getMetaData(),
                        this.material.getSliverColors(),
                    new LibVertex( intersectionPointHorz.x, intersectionPointHorz.y, z ),
                    exactDistanceHorz,
                    shotAngleHorz,
                    invertedShotAngleHorz,
                    sliverAngleHorz,
                    angleHitPointHorzToCenter - 90.0f,
                    0.0f,                    //faceAngleVert
                    1.0f,
                    General.FADE_OUT_FACES_TOTAL_TICKS,
                    ShooterSetting.Performance.ELLIPSE_SEGMENTS
                );
            }

            //return null
            return null;
        }

        public final Ellipse2D.Float getCircle()
        {
            return new Ellipse2D.Float(this.anchor.x - this.radius, this.anchor.y - this.radius, this.radius * 2, this.radius * 2 );
        }

        public final float getRadius()
        {
            return this.radius;
        }

        public final float setRadius( float newRadius )
        {
            return this.radius = newRadius;
        }

        public final LibVertex getTarget()
        {
            return this.target;
        }

        public final float getHeight()
        {
            return this.height;
        }

        /************************************************************************************
        *   Checks vertical intersection.
        *
        *   @param  wallZ1  wall's first  z-point. May be lower or higher than the 2nd.
        *   @param  wallZ2  wall's second z-point. May be lower or higher than the 1st.
        *   @return         <code>true</code> if the player's vertical collision line
        *                   intersects the given wall points. Otherwise <code>false</code>.
        ************************************************************************************/
        public final boolean heightsIntersect( float wallZ1, float wallZ2, boolean useBottomToleranceZ, boolean invertBottomTolerance )
        {
            float lowestWallZ   = Math.min( wallZ1, wallZ2 );
            float highestWallZ  = Math.max( wallZ1, wallZ2 );

            //ShooterDebug.bugfix.out( "heights intersect " + wallZ1 + " " + wallZ2 + " " + anchor.z + "  "   );

            float mod = ( invertBottomTolerance ? -1.0f : 1.0f );
            boolean miss =
            (
                    this.anchor.z + ( useBottomToleranceZ ? mod * this.bottomCollisionToleranceZ : mod * this.minBottomCollisionToleranceZ) > highestWallZ
                || this.anchor.z + this.height < lowestWallZ
            );
            //ShooterDebug.bugfix.out( "intersect miss " + miss );

            //ShooterDebug.bugfix.out( " check: ["+anchor.z+"]["+lowestWallZ+"]["+highestWallZ+"]["+height+"] miss: ["+miss+"]" );

            //check if the player's head or toe point lies in between the wall line
            return !miss;
        }

        public void drawStandingCircle()
        {
            if (this.debugDrawBotCircles)
            {
                new LibFaceEllipseFloor(this.debug, null, LibColors.ERed, this.anchor.x, this.anchor.y, this.anchor.z + 0.001f, this.radius, this.radius, this.ellipseSegments).draw();
            }
        }

        /***************************************************************************************************************
        *   Return the nearest available position z for the given point.
        *
        *   @param      floorsToCheck   All floors to check.
        *   @param      maxClimbDistZ   The maximum climbing distance z from the anchor's z-position.
        *   @return                     The nearest floor's z-position or <code>null</code> if non-existent.
        *   @deprecated                 May be useful one day.
        ***************************************************************************************************************/
        @Deprecated
        public final Float getNearestFloor( Vector<Float> floorsToCheck, float maxClimbDistZ )
        {
            if ( floorsToCheck.size() == 0 )
            {
                return null;
            }

            //get the nearest face-hit-point
            float nearestDistance = 0.0f;
            int   nearestIndex    = -1;
            for ( int i = 0; i < floorsToCheck.size(); ++i )
            {
                //assign 1st point - check if the distance is nearer
                float thisDistance = Math.abs(this.anchor.z - floorsToCheck.elementAt(i));
                if
                (
                        ( nearestIndex == -1 )
                    ||  ( thisDistance < nearestDistance )
                )
                {
                    //check if the point is under the player or, if lower, not too high to climb up to!
                    if
                    (
                            floorsToCheck.elementAt(i) < this.anchor.z
                        ||  thisDistance <= maxClimbDistZ
                    )
                    {
                        nearestIndex    = i;
                        nearestDistance = floorsToCheck.elementAt(i);

                    }
                }
            }

            //return the nearest distance
            return ( nearestIndex == -1 ? null : floorsToCheck.elementAt( nearestIndex ) );
        }

        public final Float getHighestOfCheckedFloor( Vector<Float> floorsToCheck )
        {
            if ( floorsToCheck.size() == 0 )
            {
                return null;
            }

            //get the highest face-hit-point
            Float highestZ = null;
            for ( int i = 0; i < floorsToCheck.size(); ++i )
            {
                //assign 1st point - check if the distance is nearer
                float thisDistance = Math.abs(this.anchor.z - floorsToCheck.elementAt(i));

                //only pick faces that..
                if
                (
                        //..are lower than the player
                        floorsToCheck.elementAt(i) < this.anchor.z
                        //..are not too high
                    ||  thisDistance <= this.bottomCollisionToleranceZ
                )
                {
                    //assign if higher
                    if ( highestZ == null || floorsToCheck.elementAt(i) > highestZ)
                    {
                        highestZ = floorsToCheck.elementAt( i );
                    }
                }
            }

            //return the nearest distance
            return highestZ;
        }

        public final void setAnchorAsTargetPosition()
        {
            this.target = new LibVertex(this.anchor.x, this.anchor.y, this.anchor.z );
        }

        public final void moveToTargetPosition( float newHeight, boolean disableWallCollisions, boolean disableBotCollisions )
        {
            //get distances from old to the new position
            float distX     = this.target.x - this.anchor.x;
            float distY     = this.target.y - this.anchor.y;
            float distZ     = this.target.z - this.anchor.z;

            //save original anchor cause it will change in the loop!
            float startX   = this.anchor.x;
            float startY   = this.anchor.y;
            float startZ   = this.anchor.z;

            //check target-point collision stepwise from far to near ( to increase walking speed )
            for (float j = this.collisionCheckingSteps; j > 0; --j )
            {
                //try next step point..
                float distanceToCheckX = distX * j / this.collisionCheckingSteps;
                float distanceToCheckY = distY * j / this.collisionCheckingSteps;
                float distanceToCheckZ = distZ * j / this.collisionCheckingSteps;

                //try point on the line between current and target point
                this.target.x = startX + distanceToCheckX;
                this.target.y = startY + distanceToCheckY;
                this.target.z = startZ + distanceToCheckZ;
                if (this.tryTargetPoint( newHeight, disableWallCollisions, disableBotCollisions ) ) return;

                //try point on x-clipped axis
                this.target.x = startX + distanceToCheckX;
                this.target.y = startY;
                this.target.z = startZ + distanceToCheckZ;
                if (this.tryTargetPoint( newHeight, disableWallCollisions, disableBotCollisions ) ) return;

                //try point on y-clipped axis
                this.target.x = startX;
                this.target.y = startY + distanceToCheckY;
                this.target.z = startZ + distanceToCheckZ;
                if (this.tryTargetPoint( newHeight, disableWallCollisions, disableBotCollisions ) ) return;
            }

            //update player's collision circle to initial position
            this.update( startX, startY, startZ, newHeight );
        }

        /***************************************************************************************************************
         *   Check collision with the given cylinder.
         *
         *    @param z The z point of the collision to check.
         *
         *    @return <code>true</code> if collision appears. Otherwise <code>false</code>.
         ***************************************************************************************************************/
         public final boolean checkCollision( float z )
         {
             //check collision of 2 circles ( easy  task.. )
             return
             (
                     z >= this.anchor.z
                 &&  z < (this.anchor.z + this.height)
             );
         }

        /***************************************************************************************************************
        *   Check collision with the given cylinder.
        *
        *    @param circle  Circle to check horizontal collision with.
        *    @return        <code>true</code> if collision appears. Otherwise <code>false</code>.
        ***************************************************************************************************************/
        public final boolean checkCollision( Ellipse2D.Float circle )
        {
            //check collision of 2 circles ( easy  task.. )
            Area circleHorz      = new Area( circle );
            Area ownCircleHorz   = new Area(this.getCircle() );
            circleHorz.intersect( ownCircleHorz );

            //check horizontal intersection
            return ( !circleHorz.isEmpty() );
        }

        public final boolean checkCollisionHorzLines( LibFaceTriangle face, boolean useBottomToleranceZ, boolean invertBottomTolerance  )
        {
            //check horizontal intersection
            Point2D.Double  cylinderPosHorz         = new Point2D.Double(this.anchor.x, this.anchor.y );
            boolean         horizontalIntersection1 = ( face.collisionLineHorz1.ptSegDist( cylinderPosHorz ) <= this.radius);
            boolean         horizontalIntersection2 = ( face.collisionLineHorz2.ptSegDist( cylinderPosHorz ) <= this.radius);
            boolean         horizontalIntersection3 = ( face.collisionLineHorz3.ptSegDist( cylinderPosHorz ) <= this.radius);

            if ( horizontalIntersection1 || horizontalIntersection2 || horizontalIntersection3 )
            {
                //check vertical intersection
                boolean verticalIntersection = this.heightsIntersect( face.lowestZ, face.highestZ, useBottomToleranceZ, invertBottomTolerance   );
                if ( verticalIntersection )
                {
                    //ShooterDebug.bugfix.out("INTERSECT - return float with z [" + face.highestZ + "]");
                    return true;
                }
            }

            return false;
        }

        /***************************************************************************************************************
        *   Check collision with the given cylinder.
        *
        *    @param c   Cylinder to check collision with.
        *    @return    <code>true</code> if collision appears. Otherwise <code>false</code>.
        ***************************************************************************************************************/
        public final boolean checkCollisionHorz( LibCylinder c )
        {
            //check horizontal and vertical intersection
            if (this.checkCollision( c.getCircle() ) && c.heightsIntersect(this.anchor.z, this.anchor.z + this.height, false, false ) )
            {
                //no height information is required here ..
                return true;
            }

            return false;
        }

        /***************************************************************************************************************
        *   Check collision with the given cylinder.
        *
        *    @param c   Cylinder to check collision with.
        *    @return    <code>true</code> if collision appears. Otherwise <code>false</code>.
        ***************************************************************************************************************/
        public final Vector<Float> checkCollisionVert( LibCylinder c, Object exclude )
        {
            Vector<Float> v = new Vector<Float>();

            //check horizontal and vertical intersection
            if (this.checkCollision( c.getCircle() ) && c.heightsIntersect(this.anchor.z, this.anchor.z + this.height, false, false ) )
            {
                //no height information is required here ..
                v.add( 42.0f );
            }

            return v;
        }

        public final LibVertex getAnchor()
        {
            return this.anchor;
        }

        private boolean tryTargetPoint(float newHeight, boolean disableWallCollisions, boolean disableBotCollisions)
        {
            //update player's collision circle with target location
            this.update(this.target.x, this.target.y, this.target.z, newHeight );

            //check if target-point is collision free
            boolean collisionFree =
            (
                    ( disableWallCollisions || !Level.currentSection().checkCollisionOnWalls( this ) )
                &&  ( disableBotCollisions  || !Level.currentSection().checkCollisionOnBots(  this ) )
            );

            if ( collisionFree )
            {
                //assign this point as the target-point and leave this method!
                this.update(this.target.x, this.target.y, this.target.z, newHeight );
            }

            return collisionFree;
        }

        public final Cylinder copy()
        {
            return new Cylinder
            (
                    this.parentGameObject,
                    this.anchor.copy(),
                    this.radius,
                    this.height,
                    this.collisionCheckingSteps,
                    this.debug,
                    this.debugDrawBotCircles,
                    this.bottomCollisionToleranceZ,
                    this.minBottomCollisionToleranceZ,
                ShooterSetting.Performance.ELLIPSE_SEGMENTS,
                    this.material
            );
        }

        /***************************************************************************************************************
        *   Check horizontal collision of the face and the player's center point.
        *
        *   @param  x1      rect x1.
        *   @param  y1      rect y1.
        *   @param  x2      rect x2.
        *   @param  y2      rect y2.
        *   @return         <code>true</code> if the cylinder's center point is in the rect.
        *   @deprecated     replaced by j2se api.
        ***************************************************************************************************************/
        @Deprecated
        public final boolean checkHorzCenterCollision( float x1, float y1, float x2, float y2 )
        {
            return
            (
                    ( x1 > x2 ? this.anchor.x >= x2 && this.anchor.x <= x1 : this.anchor.x >= x1 && this.anchor.x <= x2 )
                &&  ( y1 > y2 ? this.anchor.y >= y2 && this.anchor.y <= y1 : this.anchor.y >= y1 && this.anchor.y <= y2 )
            );
        }
    }
