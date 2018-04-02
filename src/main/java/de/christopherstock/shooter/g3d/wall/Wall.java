
    package de.christopherstock.shooter.g3d.wall;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
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
    import de.christopherstock.shooter.ShooterSetting.*;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.mesh.Mesh;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;

    /*******************************************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *******************************************************************************************************************/
    public class Wall extends Mesh implements LibGameObject, LibClimbable
    {
        /***************************************************************************************************************
        *   Specifies, if game objects and shots collide on a wall.
        ***************************************************************************************************************/
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

        protected                   WallAction          wallAction                      = null;
        private                     Wall[]              childWalls                      = null;
        private                     WallClimbable       climbable                       = null;
        private                     WallCollidable      collisionEnabled                = Wall.WallCollidable.ENo;

        private                     WallEnergy          energy                          = null;

        public                      float               startupRotZ                     = 0.0f;
        private                     Float               baseZ                           = null;

        private                     LibTexture[]        changeTexture2                  = null;
        private                     int                 changeTexture2Index             = 0;
        private                     int                 changeTexture2Animation         = 0;
        private                     int                 changeTexture2Delay             = 0;

        public Wall(LibD3dsFile aD3dsFile, LibVertex aAnchor, float aStartupRotZ, LibScalation aScalation, LibInvert aInvert, Wall.WallCollidable aCollisionEnabled, Wall.WallAction aWallAction, WallClimbable aClimbable, DrawMethod aDrawMethod, LibTexture aChangeTexture1, LibTexture[] aChangeTexture2, int aChangeTexture2Delay, WallHealth aWallHealth, FXSize aExplosionSize, SoundFg aExplosionSound )
        {
            super( Shooter.game.engine.d3ds.getFaces( aD3dsFile ), aAnchor, ( aWallAction == Wall.WallAction.ESprite ? 0.0f : aStartupRotZ ), aScalation.getScaleFactor(), aInvert, null, LibTransformationMode.EOriginalsToOriginals, aDrawMethod );
            this.assignParentOnFaces( this );
            this.assignDrawMethodOnFaces( aDrawMethod );
            this.collisionEnabled = aCollisionEnabled;
            this.wallAction = aWallAction;
            this.startupRotZ = aStartupRotZ;
            this.climbable = aClimbable;
            this.energy = new WallEnergy( aWallHealth, aExplosionSize, aExplosionSound );

            //change texture if desired
            if ( aChangeTexture1 != null ) this.changeTexture( WallTex.ETest.getMetaData(),   aChangeTexture1.getMetaData() );
            if ( aChangeTexture2 != null )
            {
                this.changeTexture( WallTex.ETest2.getMetaData(),  aChangeTexture2[ 0 ].getMetaData() );
                this.changeTexture2 = aChangeTexture2;
                this.changeTexture2Delay = aChangeTexture2Delay;
            }

            //translate sprites to center point
            if (this.wallAction == Wall.WallAction.ESprite )
            {
                Point2D.Float transToCenterXY = this.getCenterPointXY();
                this.translate( aAnchor.x - transToCenterXY.x, aAnchor.y - transToCenterXY.y, 0.0f, LibTransformationMode.EOriginalsToOriginals );
            }

            //darken all destroyable faces for a more realistic effect
            if ( aWallHealth != WallHealth.EUnbreakale ) this.darkenAllFaces( 0.9f, true, true, 0.1f, 0.1f );
        }

        public void assignChildWallsToDestroy( Wall[] childWalls )
        {
            this.childWalls = childWalls;
        }

        public final void animate()
        {
            //animate if not destroyed
            if ( !this.energy.destroyed)
            {
                //check if this mesh is associated with an action
                switch (this.wallAction)
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
                if (this.changeTexture2 != null && this.changeTexture2.length > 1 )
                {
                    //tick till delay
                    if ( ++this.changeTexture2Animation >= this.changeTexture2Delay)
                    {
                        //reset animation
                        this.changeTexture2Animation = 0;

                        //remember current index
                        int currentIndex = this.changeTexture2Index;

                        //next index - reset if required
                        ++this.changeTexture2Index;
                        if (this.changeTexture2Index >= this.changeTexture2.length ) this.changeTexture2Index = 0;

                        //change texture
                        this.changeTexture(this.changeTexture2[ currentIndex ].getMetaData(), this.changeTexture2[this.changeTexture2Index].getMetaData() );
                    }
                }
            }

            //animate if destroyed
            if (this.energy.destroyed)
            {
                //int animatedFaces = 0;
                ++this.energy.currentDyingTick;
                this.energy.dyingTransZ = -( (this.energy.currentDyingTick * this.energy.currentDyingTick) * 0.002f );

                //remove all bullet holes this wall carries
                Shooter.game.engine.bulletHoleManager.removeForWall( this );

                for ( LibFaceTriangle f : this.getFaces() )
                {
                    if ( f.continueDestroyAnim )
                    {
                        //++animatedFaces;
                        ShooterDebug.wallDestroy.out( "highestZ: ["+f.highestZ +"] ankZ: " + f.getAnchor().z + " playaZ: " + Shooter.game.engine.player.getAnchor().z + " faceZ: [" + f.getAnchor().z + "]" );

                        float distanceFromFloor = ( LibMath.getRandom( 1, 100 ) * 0.3f / 100 ); // -0.1f

                        //init base z on 1st animation tick
                        if (this.baseZ == null )
                        {
                            float           baseZ   = Float.MIN_VALUE;
                            Point2D.Float   xy      = this.getCenterPointXY();
                            float           z       = this.getCenterZ();

                            Float baseZF = Level.currentSection().getHighestFloor( new Cylinder( null, new LibVertex( xy.x, xy.y, z ), 0.1f, 0.1f, 0, ShooterDebug.floorChange, false, 0.01f, 0.01f, ShooterSetting.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh ), this );
                            if ( baseZF != null )
                            {
                                baseZ = baseZF;
                            }
                            else
                            {
                                baseZ = Shooter.game.engine.player.getAnchor().z;
                            }
                            this.baseZ = baseZ;
                        }

                        if ( f.highestZ - distanceFromFloor - ( ( f.highestZ - f.lowestZ) / 2 ) < this.baseZ)
                        {
                            f.continueDestroyAnim = false;
                        }
                        else
                        {
                            //translate
                            f.translate
                            (
                                LibMath.sinDeg(this.energy.killAngleHorz) * 0.01f + LibMath.getRandom( -15, +15 ) * 0.005f,
                                LibMath.cosDeg(this.energy.killAngleHorz) * 0.01f + LibMath.getRandom( -15, +15 ) * 0.005f,
                                    this.energy.dyingTransZ,
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

        public final void launchAction( LibCylinder cylinder, Object gadget, float faceAngle )
        {
            //only if not destroyed
            if ( !this.energy.destroyed)
            {
                //only launch an action if this mesh has an according action
                if (this.wallAction != Wall.WallAction.ENone )
                {
                    //perform an action if this mesh is affected
                    if (this.checkAction( cylinder, true, false ) )
                    {
                        float anglePlayerToWall = LibMath.getAngleCorrect( cylinder.getCenterHorz(), this.getCenterPointXY() );
                        anglePlayerToWall = LibMath.normalizeAngle( -anglePlayerToWall );
                        float angleDistance     = LibMath.getAngleDistanceAbsolute( faceAngle, anglePlayerToWall );

                        //ShooterDebug.bugfix.out( "faceAngle [" + ( faceAngle ) + "] wall [" + anglePlayerToWall + "] " );
                        //ShooterDebug.bugfix.out( " delta [" + angleDistance + "]" );

                        //clip angle distance
                        if ( angleDistance < PlayerSettings.MAX_ACTION_VIEW_ANGLE )
                        {
                            switch (this.wallAction)
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

        public final float getCarriersFaceAngle()
        {
            return 0.0f;
        }

        /***************************************************************************************************************
        *   Fires a shot and returns all hit-points.
        *
        *   @param  shot    The shot to be fired on this mesh.
        *   @return         The nearest hitpoint with lots of informations.
        *                   <code>null</code> if no face was hit.
        ***************************************************************************************************************/
        @Override
        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> hitPoints = new Vector<LibHitPoint>();

            //only launch shot on faces if collision is active and this is not a sprite
            if (this.collisionEnabled == Wall.WallCollidable.EYes && this.wallAction != Wall.WallAction.ESprite )
            {
                hitPoints.addAll( super.launchShot( shot ) );
            }

            return hitPoints;
        }

        @Override
        public final boolean checkCollisionHorz( LibCylinder cylinder )
        {
            //only check collisions  if collision is active
            if (this.collisionEnabled == Wall.WallCollidable.EYes )
            {
                return super.checkCollisionHorz( cylinder, this.climbable);
            }

            return false;
        }

        @Override
        public final Vector<Float> checkCollisionVert( LibCylinder cylinder, Object exclude )
        {
            //only check collisions  if collision is active AND this wall shall not be excluded
            if (this.climbable == WallClimbable.EYes && exclude != this )
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
            if ( collisionEnabled.getBoolean() )
            {
                vecZ.addAll( super.checkCollision( point ) );
            }

            return vecZ;
        }
*/
        public HitPointCarrier getHitPointCarrier()
        {
            return HitPointCarrier.EWall;
        }

        public void hurt( int h, float shotAngleHorz )
        {
            //only if destroyable
            if (this.energy.healthCurrent > 0 )
            {
                //lower current health
                this.energy.healthCurrent -= h;

                float opacity = ShooterSetting.FxSettings.MIN_DARKEN_FACES + ( 1.0f - ShooterSetting.FxSettings.MIN_DARKEN_FACES ) * ( ( (float) this.energy.healthCurrent / (float) this.energy.healthStart) );

                //darken faces
                this.darkenAllFaces( opacity , true, false, 0.1f, 0.0f );

                //darken all bullet holes for this wall
                Shooter.game.engine.bulletHoleManager.darkenAll( this, opacity );

                //check if killed
                if (this.energy.healthCurrent <= 0 )
                {
                    //kill wall
                    this.kill( shotAngleHorz );
                }
            }
        }

        private void kill( float shotAngleHorz )
        {
            //ShooterDebug.major.out( "wall-mesh destroyed!" );

            this.energy.healthCurrent = 0;
            this.energy.destroyed = true;
            this.collisionEnabled = Wall.WallCollidable.ENo;
            this.energy.killAngleHorz = shotAngleHorz;

            //get mesh's center and launch explosion from there
            if (this.energy.explosionSize != null )
            {
                //launch sliver fx on this hole
                Point2D.Float xy = this.getCenterPointXY();
                float z = this.getCenterZ();
                float baseZ     = Float.MIN_VALUE;
                Float baseZF    = Level.currentSection().getHighestFloor( new Cylinder( null, new LibVertex( xy.x, xy.y, z ), 0.05f, 0.01f, 0, ShooterDebug.floorChange, false, 0.01f, 0.01f, ShooterSetting.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh ), this );
                if ( baseZF != null )
                {
                    baseZ = baseZF;
                }
                baseZ += 0.05f;

                //ShooterDebug.bugfix.out( "z> " + baseZ );

                LibFXManager.launchExplosion( ShooterDebug.face, new LibVertex( xy.x, xy.y, baseZ ), this.energy.explosionSize, FXTime.EMedium, FxSettings.LIFETIME_EXPLOSION, baseZ, General.FADE_OUT_FACES_TOTAL_TICKS );
            }

            //lower opacity to 0
            this.darkenAllFaces( ShooterSetting.FxSettings.MIN_DARKEN_FACES, false, true, 0.0f, 0.1f );

            //kill all child walls if any
            if ( this.childWalls != null )
            {
                for ( Wall w : this.childWalls )
                {
                    w.kill( shotAngleHorz );
                }
            }

            //play explosion sound if specified
            if ( this.energy.explosionSound != null )
            {
                this.energy.explosionSound.playDistancedFx( new Point2D.Float(this.anchor.x, this.anchor.y ) );
            }
        }

        @Override
        public void draw()
        {
            //draw wall
            super.draw();
        }

        public final boolean isClimbable()
        {
            return ( this.climbable == WallClimbable.EYes );
        }
    }
