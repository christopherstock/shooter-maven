
    package de.christopherstock.lib.g3d.face;

    import  java.awt.geom.*;
    import  java.util.*;
    import  javax.vecmath.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.d3ds.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   Represents a triangle face.
    *******************************************************************************************************************/
    public class LibFaceTriangle extends LibFace
    {
        public                  LibGameObject       iParentGameObject           = null;
        public                  float               iLowestX                    = 0.0f;
        public                  float               iHighestX                   = 0.0f;
        public                  float               iLowestY                    = 0.0f;
        public                  float               iHighestY                   = 0.0f;
        public                  float               iLowestZ                    = 0.0f;
        public                  float               iHighestZ                   = 0.0f;
        public                  Line2D.Float        iCollisionLineHorz1         = null;
        public                  Line2D.Float        iCollisionLineHorz2         = null;
        public                  Line2D.Float        iCollisionLineHorz3         = null;
        public                  boolean             continueDestroyAnim         = true;
        public                  float               iDamageMultiplier           = 0.0f;
        public                  LibTexture          iDefaultTexture             = null;
        public                  int                 iFadeOutFacesTicks          = 0;
        private                 int                 iEllipseSegments            = 0;

        /***************************************************************************************************************
        *   Copy constructor.
        ***************************************************************************************************************/
        public LibFaceTriangle( LibDebug aDebug, LibMaxTriangle maxTriangle, LibGLTexture tex, LibTexture aDefaultTexture, int aFadeOutFacesTicks, int aEllipseSegments )
        {
            this
            (
                aDebug,
                maxTriangle.iAnchor.copy(),
                tex,
                maxTriangle.iCol,
                maxTriangle.iA.copy(),
                maxTriangle.iB.copy(),
                maxTriangle.iC.copy(),
                ( maxTriangle.iFaceNormal == null ? null : maxTriangle.iFaceNormal.copy() ),
                aDefaultTexture,
                aFadeOutFacesTicks,
                aEllipseSegments
            );
        }

        private LibFaceTriangle( LibDebug aDebug, LibVertex ank, LibGLTexture texture, LibColors col, LibVertex a, LibVertex b, LibVertex c, LibVertex aFaceNormal, LibTexture aDefaultTexture, int aFadeOutFacesTicks, int aEllipseSegments )
        {
            super( aDebug, ank, texture, col, aFaceNormal );

            this.iEllipseSegments = aEllipseSegments;

            //assign default texture
            this.iDefaultTexture = aDefaultTexture;

            //assign fade out ticks for faces
            this.iFadeOutFacesTicks = aFadeOutFacesTicks;

            //set vertices
            this.setOriginalVertices( new LibVertex[] { a, b, c, } );

            //init all values
            this.updateCollisionValues();
        }

        public final void assignParentGameObject( LibGameObject aParentGameObject )
        {
            //connect to parent mesh
            this.iParentGameObject = aParentGameObject;
        }

        @Override
        protected final void setCollisionValues()
        {
            //set collission values
            float x1 = this.iTransformedVertices[ 0 ].x;
            float x2 = this.iTransformedVertices[ 1 ].x;
            float x3 = this.iTransformedVertices[ 2 ].x;

            float y1 = this.iTransformedVertices[ 0 ].y;
            float y2 = this.iTransformedVertices[ 1 ].y;
            float y3 = this.iTransformedVertices[ 2 ].y;

            float z1 = this.iTransformedVertices[ 0 ].z;
            float z2 = this.iTransformedVertices[ 1 ].z;
            float z3 = this.iTransformedVertices[ 2 ].z;

            //get minimum and maximum X Y and Z
            this.iLowestX = (float)LibMath.min( x1, x2, x3 );
            this.iLowestY = (float)LibMath.min( y1, y2, y3 );
            this.iLowestZ = (float)LibMath.min( z1, z2, z3 );
            this.iHighestX = (float)LibMath.max( x1, x2, x3 );
            this.iHighestY = (float)LibMath.max( y1, y2, y3 );
            this.iHighestZ = (float)LibMath.max( z1, z2, z3 );

            //get all horizontal vertices in 2d-system
            Point2D.Float   ph1              = new Point2D.Float( x1, y1 );
            Point2D.Float   ph2              = new Point2D.Float( x2, y2 );
            Point2D.Float   ph3              = new Point2D.Float( x3, y3 );

            //calculate angle horz - why is the simplest min-max distance operative now?
            float   angleHorz       = LibMath.getAngleCorrect( new Point2D.Float(this.iLowestX, this.iLowestY), new Point2D.Float(this.iHighestX, this.iHighestY) );

            //angle vert is 0°
            float   angleVert       = 0.0f;

            //set angle vert to 90° if all z-points equal
            if ( z1 == z2 && z1 == z3 && z2 == z3 )
            {
                angleVert = 90.0f;
            }

           //angleVert           = LibMath.getAngleCorrect( new Point2D.Float( 0.0f, iLowestZ ), new Point2D.Float( 1.0f, iHighestZ ) );

            //assign angles and collision lines
            this.setFaceAngleHorz( angleHorz );
            this.setFaceAngleVert( angleVert );

            //assign XY collision lines between the three vertices
            this.iCollisionLineHorz1 = new Line2D.Float( ph1.x, ph1.y, ph2.x, ph2.y );
            this.iCollisionLineHorz2 = new Line2D.Float( ph1.x, ph1.y, ph3.x, ph3.y );
            this.iCollisionLineHorz3 = new Line2D.Float( ph2.x, ph2.y, ph3.x, ph3.y );
        }

        /***************************************************************************************************************
        *   Kai's algo.
        *
        *   @param  shot    The shot to be fired on this mesh.
        *   @return         The nearest hit-point with lots of additional information
        *                   or <code>null</code> if no hit-point was found.
        *
        *   @deprecated     unlinked
        ***************************************************************************************************************/
        @Deprecated
        public LibHitPoint launchShotKai( LibShot shot )
        {
            //only visible faces can be hit
            switch (this.iDrawMethod)
            {
                case EInvisible:
                {
                    break;
                }

                case EAlwaysDraw:
              //case EHideIfTooDistant:
                {
                    LibVertex[] vertices = this.getVerticesToDraw();

                    Point3d p1 = new Point3d( vertices[ 0 ].x, vertices[ 0 ].y, vertices[ 0 ].z );
                    Point3d p2 = new Point3d( vertices[ 1 ].x, vertices[ 1 ].y, vertices[ 1 ].z );
                    Point3d p3 = new Point3d( vertices[ 2 ].x, vertices[ 2 ].y, vertices[ 2 ].z );

                    //get line-face-intersection
                    Point3d intersectionPoint = LibMathGeometry.getLineFaceIntersectionD( shot.iSrcPoint3d, shot.iEndPoint3d, p1, p2, p3 );

                    //check collision
                    if ( intersectionPoint == null )
                    {
                        return null;
                    }

                    Point2D.Float intersectionPointHorz = new Point2D.Float( (float)intersectionPoint.x, (float)intersectionPoint.y );

                    //get horizontal values
                    float           exactDistanceHorz     = (float)shot.iSrcPointHorz.distance( intersectionPointHorz  );                //get exact distance
                    float           shotAngleHorz         = LibMath.getAngleCorrect( shot.iSrcPointHorz, intersectionPointHorz );       //get angle between player and hit-point
                    float           invertedShotAngleHorz = 360.0f - ( shotAngleHorz - 180.0f  );                                       //get opposite direction of shot
                    float           sliverAngleHorz       = shotAngleHorz - this.iFaceAngleHorz * 2;                                         //get Sliver angle

                    //debug.shotAndHit.out( "exactDistanceHorz:     [" + exactDistanceHorz + "]" );
                    //debug.shotAndHit.out( "shotAngleHorz:         [" + shotAngleHorz + "]" );
                    //debug.shotAndHit.out( "invertedShotAngleHorz: [" + invertedShotAngleHorz + "]" );
                    //debug.shotAndHit.out( "SliverAngleHorz:       [" + sliverAngleHorz + "]" );

                  //Point2D.Float   intersectionPointVert   = new Point2D.Float( 0, (float)intersectionPoint.z );

                    //get vertical values ( unused with new collision algo )
                  //float exactDistanceVert = (float)shot.iSrcPointVert.distance( intersectionPointVert );       //get exact distance

                    LibGLTexture    faceTexture   = this.getTexture();
                    LibMaterial     faceMaterial  = ( faceTexture == null ? this.iDefaultTexture.getTexture().getMaterial() : faceTexture.getMaterial() );

                    //debug.bugfix.out( "DISTANCE [" + intersectionPoint.distance( shot.iSrcPoint3d ) + "] HIT ! shot ["+shot.iSrcPoint3d+"] to ["+shot.iEndPoint3d+"] FACE is ["+face1+"]["+face2+"]["+face3+"]" );
                    //FX.launchDebugPoint( new LibVertex( (float)intersectionPoint.x, (float)intersectionPoint.y, (float)intersectionPoint.z ), LibColors.EPink, 150, 0.03f );

                    if (this.iLowestZ == this.iHighestZ)
                    {
                        sliverAngleHorz = LibMath.normalizeAngle( 360.0f - shotAngleHorz );
                    }

                    //return hit point
                    return new LibHitPoint
                    (
                        shot,
                            this.iParentGameObject,
                        faceMaterial.getBulletHoleTexture().getTexture(),
                            this.getTexture(),
                        faceMaterial.getSliverColors(),
                        new LibVertex( (float)intersectionPoint.x, (float)intersectionPoint.y, (float)intersectionPoint.z ),
                        exactDistanceHorz,
                        shotAngleHorz,
                        invertedShotAngleHorz,
                        sliverAngleHorz,
                            this.iFaceAngleHorz,
                            this.iFaceAngleVert,
                            this.iDamageMultiplier,
                            this.iFadeOutFacesTicks,
                            this.iEllipseSegments
                    );
                }
            }

            return null;
        }

        /***************************************************************************************************************
        *   Using new raycast algo. ( incomplete? )
        *
        *   @param  shot    The shot to be fired on this mesh.
        *   @return         The nearest hit-point with lots of additional information
        *                   or <code>null</code> if no hit-point was found.
        ***************************************************************************************************************/
        public LibHitPoint launchShotNew( LibShot shot )
        {
            //only visible faces can be hit
            switch (this.iDrawMethod)
            {
                case EInvisible:
                {
                    break;
                }

                case EAlwaysDraw:
                //case EHideIfTooDistant:
                {
                    LibVertex[] vertices = this.getVerticesToDraw();

                    Point3d p1 = new Point3d( vertices[ 0 ].x, vertices[ 0 ].y, vertices[ 0 ].z );
                    Point3d p2 = new Point3d( vertices[ 1 ].x, vertices[ 1 ].y, vertices[ 1 ].z );
                    Point3d p3 = new Point3d( vertices[ 2 ].x, vertices[ 2 ].y, vertices[ 2 ].z );

                    //get line-face-intersection
                    Point3d intersectionPoint = LibMathGeometry.getLineFaceIntersectionD( shot.iSrcPoint3d, shot.iEndPoint3d, p1, p2, p3 );

                    //check collision
                    if ( intersectionPoint == null )
                    {
                        return null;
                    }

                    Point2D.Float intersectionPointHorz = new Point2D.Float( (float)intersectionPoint.x, (float)intersectionPoint.y );

                    //get horizontal values
                    float           exactDistanceHorz     = (float)shot.iSrcPointHorz.distance( intersectionPointHorz  );                //get exact distance
                    float           shotAngleHorz         = LibMath.getAngleCorrect( shot.iSrcPointHorz, intersectionPointHorz );       //get angle between player and hit-point
                    float           invertedShotAngleHorz = 360.0f - ( shotAngleHorz - 180.0f  );                                       //get opposite direction of shot
                    float           sliverAngleHorz       = shotAngleHorz - this.iFaceAngleHorz * 2;                                         //get Sliver angle

                    //debug.shotAndHit.out( "exactDistanceHorz:     [" + exactDistanceHorz + "]" );
                    //debug.shotAndHit.out( "shotAngleHorz:         [" + shotAngleHorz + "]" );
                    //debug.shotAndHit.out( "invertedShotAngleHorz: [" + invertedShotAngleHorz + "]" );
                    //debug.shotAndHit.out( "SliverAngleHorz:       [" + sliverAngleHorz + "]" );

                  //Point2D.Float   intersectionPointVert   = new Point2D.Float( 0, (float)intersectionPoint.z );

                    //get vertical values ( unused with new collision algo )
                  //float exactDistanceVert = (float)shot.iSrcPointVert.distance( intersectionPointVert );       //get exact distance

                    LibGLTexture    faceTexture   = this.getTexture();
                    LibMaterial     faceMaterial  = ( faceTexture == null ? this.iDefaultTexture.getTexture().getMaterial() : faceTexture.getMaterial() );

                    //debug.bugfix.out( "DISTANCE [" + intersectionPoint.distance( shot.iSrcPoint3d ) + "] HIT ! shot ["+shot.iSrcPoint3d+"] to ["+shot.iEndPoint3d+"] FACE is ["+face1+"]["+face2+"]["+face3+"]" );
                    //FX.launchDebugPoint( new LibVertex( (float)intersectionPoint.x, (float)intersectionPoint.y, (float)intersectionPoint.z ), LibColors.EPink, 150, 0.03f );

                    if (this.iLowestZ == this.iHighestZ)
                    {
                        sliverAngleHorz = LibMath.normalizeAngle( 360.0f - shotAngleHorz );
                    }

                    //return hit point
                    return new LibHitPoint
                    (
                        shot,
                            this.iParentGameObject,
                        faceMaterial.getBulletHoleTexture().getTexture(),
                            this.getTexture(),
                        faceMaterial.getSliverColors(),
                        new LibVertex( (float)intersectionPoint.x, (float)intersectionPoint.y, (float)intersectionPoint.z ),
                        exactDistanceHorz,
                        shotAngleHorz,
                        invertedShotAngleHorz,
                        sliverAngleHorz,
                            this.iFaceAngleHorz,
                            this.iFaceAngleVert,
                            this.iDamageMultiplier,
                            this.iFadeOutFacesTicks,
                            this.iEllipseSegments
                    );
                }
            }

            return null;
        }

        /***************************************************************************************************************
        *   Only suitable for faces that have collision lines specified!
        *
        *   @param  shot    The shot to be fired on this mesh.
        *   @return         The nearest hit-point with lots of additional information
        *                   or <code>null</code> if no hit-point was found.
        ***************************************************************************************************************/
        //@Deprecated
        public LibHitPoint launchShotOld( LibShot shot )
        {
            //only visible faces can be hit
            switch (this.iDrawMethod)
            {
                case EInvisible:
                {
                    break;
                }

                case EAlwaysDraw:
                //case EHideIfTooDistant:
                {
                    //horizontal collission lines have to be present
                    if (this.iCollisionLineHorz1 == null || this.iCollisionLineHorz2 == null || this.iCollisionLineHorz3 == null ) return null;

                    Point2D.Float intersectionPointHorz = null;

                    //we could check if face's z are equal - but the new shot algo is better! :-)

                    //check horizontal collision
                    if ( shot.iLineShotHorz.intersectsLine(this.iCollisionLineHorz1) )
                    {
                        //get intersection point horz
                        //debug.shotAndHit.out( "==============\nHORZ FACE HIT!" );
                        intersectionPointHorz = LibMathGeometry.findLineSegmentIntersection( shot.iLineShotHorz, this.iCollisionLineHorz1, this.iDebug);
                        if ( intersectionPointHorz == null )
                        {
                            this.iDebug.err( "Intersection Point not calculated due to buggy external API." );
                            return null;
                        }
                    }
                    else if ( shot.iLineShotHorz.intersectsLine(this.iCollisionLineHorz2) )
                    {
                        //get intersection point horz
                        //debug.shotAndHit.out( "==============\nHORZ FACE HIT!" );
                        intersectionPointHorz = LibMathGeometry.findLineSegmentIntersection( shot.iLineShotHorz, this.iCollisionLineHorz2, this.iDebug);
                        if ( intersectionPointHorz == null )
                        {
                            this.iDebug.err( "Intersection Point not calculated due to buggy external API." );
                            return null;
                        }
                    }
                    else if ( shot.iLineShotHorz.intersectsLine(this.iCollisionLineHorz3) )
                    {
                        //get intersection point horz
                        //debug.shotAndHit.out( "==============\nHORZ FACE HIT!" );
                        intersectionPointHorz = LibMathGeometry.findLineSegmentIntersection( shot.iLineShotHorz, this.iCollisionLineHorz3, this.iDebug);
                        if ( intersectionPointHorz == null )
                        {
                            this.iDebug.err( "Intersection Point not calculated due to buggy external API." );
                            return null;
                        }
                    }
                    else
                    {
                        return null;
                    }

                    //get horizontal values
                    float           exactDistanceHorz     = (float)shot.iSrcPointHorz.distance( intersectionPointHorz );                //get exact distance
                    float           shotAngleHorz         = LibMath.getAngleCorrect( shot.iSrcPointHorz, intersectionPointHorz );       //get angle between player and hit-point
                    float           invertedShotAngleHorz = 360.0f - ( shotAngleHorz - 180.0f  );                                       //get opposite direction of shot
                    float           sliverAngleHorz       = shotAngleHorz - this.iFaceAngleHorz * 2;                                         //get Sliver angle
/*
                    debug.shotAndHit.out( "exactDistanceHorz:     [" + exactDistanceHorz + "]" );
                    debug.shotAndHit.out( "shotAngleHorz:         [" + shotAngleHorz + "]" );
                    debug.shotAndHit.out( "invertedShotAngleHorz: [" + invertedShotAngleHorz + "]" );
                    debug.shotAndHit.out( "SliverAngleHorz:       [" + sliverAngleHorz + "]" );
*/
                    //calculate face's vertical collision line ( if we assume that this is a straight vertical face! )
                    Line2D.Float collisionLineVert = new Line2D.Float( new Point2D.Float( exactDistanceHorz, this.iLowestZ), new Point2D.Float( exactDistanceHorz, this.iHighestZ) );
                    //debug.shotAndHit.out( "face's collision line vert is: [" + collisionLineVert + "]" );

                    if ( !shot.iLineShotVert.intersectsLine( collisionLineVert ) )
                    {
                        //debug.shotAndHit.out( "VERTICAL FACE MISSED!" );
                        return null;
                    }

                    //get then intersection point for the vertical axis
                    //debug.shotAndHit.out( "VERTICAL FACE HIT!" );
                    Point2D.Float   intersectionPointVert   = LibMathGeometry.findLineSegmentIntersection( shot.iLineShotVert, collisionLineVert, this.iDebug);
                    float           z                       = intersectionPointVert.y;
                    //debug.shotAndHit.out( ">> INTERSECTION POINT VERT: " + intersectionPointVert );

                    //get vertical values
                  //float exactDistanceVert = (float)shot.iSrcPointVert.distance( intersectionPointVert );       //get exact distance

                  //debug.shotAndHit.out( ">> EXACT DISTANCE VERT: " + exactDistanceVert );
//                  debug.shot.out( ">> SHOT-ANGLE-VERT: " + shotAngleVert );
//                  debug.shot.out( ">> SLIVER-ANGLE-VERT: " + SliverAngleVert );

                    LibGLTexture    faceTexture   = this.getTexture();
                    LibMaterial     faceMaterial  = ( faceTexture == null ? this.iDefaultTexture.getTexture().getMaterial() : faceTexture.getMaterial() );

                    if (this.iLowestZ == this.iHighestZ)
                    {
                        sliverAngleHorz = shotAngleHorz;
                    }

                    //return hit point
                    return new LibHitPoint
                    (
                        shot,
                            this.iParentGameObject,
                        faceMaterial.getBulletHoleTexture().getTexture(),
                            this.getTexture(),
                        faceMaterial.getSliverColors(),
                        new LibVertex( intersectionPointHorz.x, intersectionPointHorz.y, z ),
                        exactDistanceHorz,
                        shotAngleHorz,
                        invertedShotAngleHorz,
                        sliverAngleHorz,
                            this.iFaceAngleHorz,
                            this.iFaceAngleVert,
                            this.iDamageMultiplier,
                            this.iFadeOutFacesTicks,
                            this.iEllipseSegments
                    );
                }
            }

            return null;
        }

        public final LibFaceTriangle copy()
        {
            LibVertex ankCopy  = this.getAnchor().copy();
            LibVertex aCopy    = new LibVertex(this.iOriginalVertices[ 0 ] );
            LibVertex bCopy    = new LibVertex(this.iOriginalVertices[ 1 ] );
            LibVertex cCopy    = new LibVertex(this.iOriginalVertices[ 2 ] );
            LibVertex normCopy = (this.iNormal == null ? null : this.iNormal.copy() );
            return new LibFaceTriangle
            (
                    this.iDebug,
                ankCopy,
                    this.getTexture(),
                    this.getColor(),
                aCopy,
                bCopy,
                cCopy,
                normCopy,
                    this.iDefaultTexture,
                    this.iFadeOutFacesTicks,
                    this.iEllipseSegments
            );
        }

        public boolean checkCollisionHorz( LibCylinder cylinder )
        {
            //this method is never called?

            //cylinders will not collide on floors
            //if ( face.iLowestZ == face.iHighestZ ) return false;

            return this.checkCollisionHorz( cylinder, true, false );
        }

        public boolean checkCollisionHorz( LibCylinder cylinder, boolean useBottomToleranceZ, boolean invertBottomTolerance   )
        {
            //cylinders will not collide on floors
            //if ( face.iLowestZ == face.iHighestZ ) return false;
            return cylinder.checkCollisionHorzLines( this, useBottomToleranceZ, invertBottomTolerance   );
        }

        public Vector<Float> checkCollisionVert( LibCylinder cylinder, Object exclude )
        {
            Vector<Float> v = new Vector<Float>();

            //check horz intersection
            if ( cylinder.getCircle().intersects( new Rectangle2D.Float(this.iLowestX, this.iLowestY, this.iHighestX - this.iLowestX, this.iHighestY - this.iLowestY) ) )
            {
                //do not check heights intersection !
                // if ( cylinder.heightsIntersect( iLowestZ, iHighestZ, false ) )
                {
                    //debug.bugfix.out("INTERSECT - return float with z [" + iHighestZ + "]");
                    v.add( new Float(this.iHighestZ) );
                }
            }

            return v;
        }

        public void setDamageMultiplier( float aDamageMultiplier )
        {
            this.iDamageMultiplier = aDamageMultiplier;
        }
/*
        public final Float checkCollision( Point2D.Float point )
        {
            //create the collision rectangle of this triangle face
            Rectangle2D.Float faceBaseHorz = new Rectangle2D.Float( minX, minY, maxX - minX, maxY - minY );

            //check if player is in rect
            if ( faceBaseHorz.contains( point ) )    //if ( Player.user.cylinder.checkHorzCenterCollision( x1, y1, x2, y2 ) )    //old solution
            {
                //add this z point to the values
                return new Float( iHighestZ );
            }

            return null;
        }
*/
    }
