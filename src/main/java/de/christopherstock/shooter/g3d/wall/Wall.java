/*  $Id: Wall.java 1288 2014-10-11 17:19:47Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.g3d.wall;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.Lib.Invert;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.fx.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.*;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.mesh.Mesh;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;

    /**************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class Wall extends Mesh implements LibGameObject, LibClimbable
    {
        private     static  final   long    serialVersionUID        = -7616653567459799140L;

        /**************************************************************************************
        *   Specifies, if game objects and shots collide on a wall.
        **************************************************************************************/
        public static enum WallCollidable
        {
            EYes,
            ENo,
            ;
        }

        public static enum WallClimbable
        {
            ENo,
            EYes,
            ;
        }

        public static enum WallAction
        {
            ENone,
            EDoorSlideLeft,
            EDoorSlideRight,
            EDoorSwingClockwise,
            EDoorSwingCounterClockwise,
            EDoorHatch,
            EElevatorUp,
            EElevatorDown,
            ESprite,
            ;
        }

        protected                   WallAction          iWallAction                     = null;
        private                     Wall[]              iChildWalls                     = null;
        public                      WallClimbable       iClimbable                      = null;
        private                     WallCollidable      iCollisionEnabled               = Wall.WallCollidable.ENo;

        private                     WallEnergy          iEnergy                         = null;

        public                      float               iStartupRotZ                    = 0.0f;
        public                      Float               iBaseZ                          = null;

        private                     LibTexture[]        iChangeTexture2                 = null;
        private                     int                 iChangeTexture2Index            = 0;
        private                     int                 iChangeTexture2Animation        = 0;
        private                     int                 iChangeTexture2Delay            = 0;

        public Wall( LibD3dsFile aD3dsFile, LibVertex aAnchor, float aStartupRotZ, Lib.Scalation aScalation, Invert aInvert, Wall.WallCollidable aCollisionEnabled, Wall.WallAction aWallAction, WallClimbable aClimbable, DrawMethod aDrawMethod, LibTexture aChangeTexture1, LibTexture[] aChangeTexture2, int aChangeTexture2Delay, WallHealth aWallHealth, FXSize aExplosionSize, SoundFg aExplosionSound )
        {
            super(              ShooterD3ds.getFaces( aD3dsFile ), aAnchor, ( aWallAction == Wall.WallAction.ESprite ? 0.0f : aStartupRotZ ), aScalation.getScaleFactor(), aInvert, null, LibTransformationMode.EOriginalsToOriginals, aDrawMethod );
            assignParentOnFaces( this );
            assignDrawMethodOnFaces( aDrawMethod );
            iCollisionEnabled   = aCollisionEnabled;
            iWallAction         = aWallAction;
            iStartupRotZ        = aStartupRotZ;
            iClimbable          = aClimbable;
            iEnergy             = new WallEnergy( aWallHealth, aExplosionSize, aExplosionSound );

            //change texture if desired
            if ( aChangeTexture1 != null ) changeTexture( WallTex.ETest.getTexture(),   aChangeTexture1.getTexture() );
            if ( aChangeTexture2 != null )
            {
                changeTexture( WallTex.ETest2.getTexture(),  aChangeTexture2[ 0 ].getTexture() );
                iChangeTexture2         = aChangeTexture2;
                iChangeTexture2Delay    = aChangeTexture2Delay;
            }

            //translate sprites to center point
            if ( iWallAction == Wall.WallAction.ESprite )
            {
                Point2D.Float transToCenterXY = getCenterPointXY();
                translate( aAnchor.x - transToCenterXY.x, aAnchor.y - transToCenterXY.y, 0.0f, LibTransformationMode.EOriginalsToOriginals );
            }

            //darken all destroyable faces for a more realistic effect
            if ( aWallHealth != WallHealth.EUnbreakale ) darkenAllFaces( 0.9f, true, true, 0.1f, 0.1f );
        }

        public void assignChildWallsToDestroy( Wall[] childWalls )
        {
            iChildWalls = childWalls;
        }

        public final void animate()
        {
            //animate if not destroyed
            if ( !iEnergy.iDestroyed )
            {
                //check if this mesh is associated with an action
                switch ( iWallAction )
                {
                    case ESprite:
                    {
                        //animate as sprite
                        ( (Sprite)this ).animateSprite( null );
                        break;
                    }

                    case EDoorSlideLeft:
                    case EDoorSlideRight:
                    case EDoorSwingClockwise:
                    case EDoorSwingCounterClockwise:
                    case EDoorHatch:
                    case EElevatorUp:
                    case EElevatorDown:
                    {
                        //animate as door
                        ( (Door)this ).animateDoor();
                        break;
                    }

                    case ENone:
                    {
                        //no action for this mesh
                        break;
                    }
                }

                //animate textures
                if (  iChangeTexture2 != null && iChangeTexture2.length > 1 )
                {
                    //tick till delay
                    if ( ++iChangeTexture2Animation >= iChangeTexture2Delay )
                    {
                        //reset animation
                        iChangeTexture2Animation = 0;

                        //remember current index
                        int currentIndex = iChangeTexture2Index;

                        //next index - reset if required
                        ++iChangeTexture2Index;
                        if ( iChangeTexture2Index >= iChangeTexture2.length ) iChangeTexture2Index = 0;

                        //change texture
                        changeTexture( iChangeTexture2[ currentIndex ].getTexture(), iChangeTexture2[ iChangeTexture2Index ].getTexture() );
                    }
                }
            }

            //animate if destroyed
            if ( iEnergy.iDestroyed )
            {
                //int animatedFaces = 0;
                ++iEnergy.iCurrentDyingTick;
                iEnergy.iDyingTransZ = -( ( iEnergy.iCurrentDyingTick * iEnergy.iCurrentDyingTick ) * 0.002f );

                //remove all bullet holes this wall carries
                BulletHole.removeForWall( this );

                for ( LibFaceTriangle f : getFaces() )
                {
                    if ( f.continueDestroyAnim )
                    {
                        //++animatedFaces;
                        ShooterDebug.wallDestroy.out( "highestZ: ["+f.iHighestZ+"] ankZ: " + f.getAnchor().z + " playaZ: " + Level.currentPlayer().getAnchor().z + " faceZ: [" + f.getAnchor().z + "]" );

                        float distanceFromFloor = ( LibMath.getRandom( 1, 100 ) * 0.3f / 100 ); // -0.1f

                        //init base z on 1st animation tick
                        if ( iBaseZ == null )
                        {
                            float           baseZ   = Float.MIN_VALUE;
                            Point2D.Float   xy      = getCenterPointXY();
                            float           z       = getCenterZ();

                            Float baseZF = Level.currentSection().getHighestFloor( new Cylinder( null, new LibVertex( xy.x, xy.y, z ), 0.1f, 0.1f, 0, ShooterDebug.floorChange, false, 0.01f, 0.01f, ShooterSettings.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh ), this );
                            if ( baseZF != null )
                            {
                                baseZ = baseZF.floatValue();
                            }
                            else
                            {
                                baseZ = Level.currentPlayer().getAnchor().z;
                            }
                            iBaseZ = new Float( baseZ );
                        }

                        if ( f.iHighestZ - distanceFromFloor - ( ( f.iHighestZ - f.iLowestZ ) / 2 ) < iBaseZ.floatValue() )
                        {
                            f.continueDestroyAnim = false;
                        }
                        else
                        {
                            //translate
                            f.translate
                            (
                                LibMath.sinDeg( iEnergy.iKillAngleHorz ) * 0.01f + LibMath.getRandom( -15, +15 ) * 0.005f,
                                LibMath.cosDeg( iEnergy.iKillAngleHorz ) * 0.01f + LibMath.getRandom( -15, +15 ) * 0.005f,
                                iEnergy.iDyingTransZ,
                                LibTransformationMode.ETransformedToTransformed
                            );
                        }
                    }
                }
/*
                if ( animatedFaces == 0 )
                {
                    continueDestroyAnim = false;
                }
*/
            }
        }

        @Override
        public final void launchAction( LibCylinder cylinder, Object gadget, float faceAngle )
        {
            //only if not destroyed
            if ( !iEnergy.iDestroyed )
            {
                //only launch an action if this mesh has an according action
                if ( iWallAction != Wall.WallAction.ENone )
                {
                    //perform an action if this mesh is affected
                    if ( checkAction( cylinder, true, false ) )
                    {
                        float anglePlayerToWall = LibMath.getAngleCorrect( cylinder.getCenterHorz(), getCenterPointXY() );
                        anglePlayerToWall = LibMath.normalizeAngle( -anglePlayerToWall );
                        float angleDistance     = LibMath.getAngleDistanceAbsolute( faceAngle, anglePlayerToWall );

                        //ShooterDebug.bugfix.out( "faceAngle [" + ( faceAngle ) + "] wall [" + anglePlayerToWall + "] " );
                        //ShooterDebug.bugfix.out( " delta [" + angleDistance + "]" );

                        //clip angle distance
                        if ( angleDistance < PlayerSettings.MAX_ACTION_VIEW_ANGLE )
                        {
                            switch ( iWallAction )
                            {
                                case ENone:
                                {
                                    break;
                                }

                                case ESprite:
                                {
                                    if ( gadget == null )
                                    {
                                        ShooterDebug.playerAction.out( "launch action on sprite" );
                                    }
                                    else
                                    {
                                        ShooterDebug.playerAction.out( "launch gadget action on sprite" );
                                    }
                                    break;
                                }

                                case EDoorSlideLeft:
                                case EDoorSlideRight:
                                case EDoorSwingClockwise:
                                case EDoorSwingCounterClockwise:
                                case EDoorHatch:
                                {
                                    //toggle as door
                                    ( (Door)this ).toggleWallOpenClose( gadget );
                                    break;
                                }

                                case EElevatorUp:
                                case EElevatorDown:
                                {
                                    //elevators cannot be activated directly!
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public final float getCarriersFaceAngle()
        {
            return 0.0f;
        }

        /**************************************************************************************
        *   Fires a shot and returns all hit-points.
        *
        *   @param  shot    The shot to be fired on this mesh.
        *   @return         The nearest hitpoint with lots of informations.
        *                   <code>null</code> if no face was hit.
        **************************************************************************************/
        @Override
        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> hitPoints = new Vector<LibHitPoint>();

            //only launch shot on faces if collision is active and this is not a sprite
            if ( iCollisionEnabled == Wall.WallCollidable.EYes && iWallAction != Wall.WallAction.ESprite )
            {
                hitPoints.addAll( super.launchShot( shot ) );
            }

            return hitPoints;
        }

        @Override
        public final boolean checkCollisionHorz( LibCylinder cylinder )
        {
            //only check collisions  if collision is active
            if ( iCollisionEnabled == Wall.WallCollidable.EYes )
            {
                return super.checkCollisionHorz( cylinder, iClimbable );
            }

            return false;
        }

        @Override
        public final Vector<Float> checkCollisionVert( LibCylinder cylinder, Object exclude )
        {
            //only check collisions  if collision is active AND this wall shall not be excluded
            if ( iClimbable == WallClimbable.EYes && exclude != this )
            {
                return super.checkCollisionVert( cylinder, exclude );
            }

            return new Vector<Float>();
        }
/*
        @Override
        public final Vector<Float> checkCollision( Point2D.Float point )
        {
            Vector<Float> vecZ = new Vector<Float>();

            //only check floor-change if collision is active
            if ( iCollisionEnabled.getBoolean() )
            {
                vecZ.addAll( super.checkCollision( point ) );
            }

            return vecZ;
        }
*/
        @Override
        public HitPointCarrier getHitPointCarrier()
        {
            return HitPointCarrier.EWall;
        }

        public void hurt( int h, float shotAngleHorz )
        {
            //only if destroyable
            if ( iEnergy.iHealthCurrent > 0 )
            {
                //lower current health
                iEnergy.iHealthCurrent -= h;

                float opacity = ShooterSettings.FxSettings.MIN_DARKEN_FACES + ( 1.0f - ShooterSettings.FxSettings.MIN_DARKEN_FACES ) * ( ( (float)iEnergy.iHealthCurrent / (float)iEnergy.iHealthStart ) );

                //darken faces
                darkenAllFaces( opacity , true, false, 0.1f, 0.0f );

                //darken all bullet holes for this wall
                BulletHole.darkenAll( this, opacity );

                //check if killed
                if ( iEnergy.iHealthCurrent <= 0 )
                {
                    //kill wall
                    kill( shotAngleHorz );
                }
            }
        }

        private void kill( float shotAngleHorz )
        {
            //ShooterDebug.major.out( "wall-mesh destroyed!" );

            iEnergy.iHealthCurrent      = 0;
            iEnergy.iDestroyed          = true;
            iCollisionEnabled           = Wall.WallCollidable.ENo;
            iEnergy.iKillAngleHorz      = shotAngleHorz;

            //get mesh's center and launch explosion from there
            if ( iEnergy.iExplosionSize != null )
            {
                //launch sliver fx on this hole
                Point2D.Float xy = getCenterPointXY();
                float z = getCenterZ();
                float baseZ     = Float.MIN_VALUE;
                Float baseZF    = Level.currentSection().getHighestFloor( new Cylinder( null, new LibVertex( xy.x, xy.y, z ), 0.05f, 0.01f, 0, ShooterDebug.floorChange, false, 0.01f, 0.01f, ShooterSettings.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh ), this );
                if ( baseZF != null )
                {
                    baseZ = baseZF.floatValue();
                }
                baseZ += 0.05f;

                //ShooterDebug.bugfix.out( "z> " + baseZ );

                LibFXManager.launchExplosion( ShooterDebug.face, new LibVertex( xy.x, xy.y, baseZ ), iEnergy.iExplosionSize, FXTime.EMedium, FxSettings.LIFETIME_EXPLOSION, baseZ, General.FADE_OUT_FACES_TOTAL_TICKS );
            }

            //lower opacity to 0
            darkenAllFaces( ShooterSettings.FxSettings.MIN_DARKEN_FACES, false, true, 0.0f, 0.1f );

            //kill all child walls if any
            if ( iChildWalls != null )
            {
                for ( Wall w : iChildWalls )
                {
                    w.kill( shotAngleHorz );
                }
            }

            //play explosion sound if specified
            if ( iEnergy.iExplosionSound != null )
            {
                iEnergy.iExplosionSound.playDistancedFx( new Point2D.Float( iAnchor.x, iAnchor.y ) );
            }
        }

        @Override
        public void draw()
        {
            //draw wall
            super.draw();
        }

        @Override
        public final boolean isClimbable()
        {
            return ( iClimbable == WallClimbable.EYes );
        }
    }
