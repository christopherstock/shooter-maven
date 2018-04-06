
    package de.christopherstock.shooter.level;

    import  java.awt.geom.Point2D;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.lib.gl.LibFloorStack;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.*;
    import  de.christopherstock.shooter.base.ShooterD3ds;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.g3d.wall.*;
    import  de.christopherstock.shooter.game.artefact.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.bot.*;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.ui.hud.HUD.*;

    /*******************************************************************************************************************
    *   Holds all values that are bound to the current active level.
    *******************************************************************************************************************/
    public class Level implements LibFloorStack
    {
        private                     Vector<ItemToPickUp>        items                       = null;
        private                     Vector<Bot>                 bots                        = null;
        private                     WallCollection[]            wallCollections             = null;
        private                     int                         currentSectionIndex         = 0;
        private                     int                         adrenalineTicks             = 0;
        private                     int                         adrenalineDelayLevel        = 0;
        private                     int                         adrenalineDelayPlayer       = 0;
        private                     Wall                        skyBox                      = null;

        public static enum InvisibleZeroLayerZ
        {
            ENo,
            EYes,
            ;
        }

        private Level( int config )
        {
            //keep target level as own config
            this.currentSectionIndex = config;
        }

        public static void init()
        {
            // ShooterDebug.bugfix.out( "Init level" );

            // remove all bullet holes
            Shooter.game.engine.bulletHoleManager.clearBulletHoles();

            // remove all particles
            LibFXManager.removeAllFxPoints();

            //create player
            Shooter.game.engine.player = new Player
            (
                LevelCurrent.currentLevelConfig.startPosition,
                General.DISABLE_GRAVITY
            );

            //init all game levels
            LevelCurrent.currentSections = new Level[ LevelCurrent.currentSectionConfigData.length ];
            for ( int i = 0; i < LevelCurrent.currentSectionConfigData.length; ++i )
            {
                LevelCurrent.currentSections[ i ] = new Level( i );
                LevelCurrent.currentSections[ i ].initLevel();
            }

            //init the player for the 1st level section
            LevelCurrent.currentSections[ 0 ].initPlayer();
        }

        private void initPlayer()
        {
            //handle startup items and wearpons to the player
            for ( ItemEvent i : LevelCurrent.currentLevelConfig.startupItems)
            {
                i.perform( null );
            }
            for ( ArtefactType w : LevelCurrent.currentLevelConfig.startupWearpons)
            {
                Artefact toDeliver = new Artefact( w );
                Shooter.game.engine.player.artefactSet.deliverArtefact( toDeliver );

                //reload if firearm
                if ( w.isFireArm() )
                {
                    //ShooterDebug.bugfix.out( "reload initial wearpon" );
                    toDeliver.reload( Shooter.game.engine.player.ammoSet, false, false, null );
                }
            }

            //reset HUD-animation so change to 1st artefact can occur
            Shooter.game.engine.hud.resetAnimation();

            //change to 1st artefact
            Shooter.game.engine.player.orderWearponOrGadget( ChangeAction.EActionNext );
        }

        private void initLevel()
        {
            //spawn specified items
            this.items = new Vector<ItemToPickUp>();
            if ( LevelCurrent.currentSectionConfigData[this.currentSectionIndex].items != null )
            {
                for ( ItemToPickUp aItem : LevelCurrent.currentSectionConfigData[this.currentSectionIndex].items)
                {
                    //load item's d3ds and add it to the stack
                    aItem.loadD3ds();
                    this.items.add( aItem );
                }
            }

            //create and add all bots
            this.bots = new Vector<Bot>();
            for ( BotFactory b : LevelCurrent.currentSectionConfigData[this.currentSectionIndex].bots)
            {
                //init / reset bot
                Bot botToAdd = b.createBot();
                //ShooterDebug.bugfix.out( "reset bot" );
                this.addBot( botToAdd );
            }

            this.skyBox = new Wall
            (
                ShooterD3ds.Others.ESkyBox1,
                new LibVertex( 0.0f, 0.0f, 0.0f ),
                290.0f,
                LibScalation.ENone,
                LibInvert.ENo,
                Wall.WallCollidable.ENo,
                Wall.WallAction.ENone,
                Wall.WallClimbable.ENo,
                LibFace.DrawMethod.EAlwaysDraw,
                null,
                null,
                0,
                WallHealth.EUnbreakale,
                FXSize.ESmall,
                null
            );
        }

        private void addBot( Bot botToAdd )
        {
            this.bots.add( botToAdd );
            ShooterDebug.bot.out( "adding bot. capacity is now [" + this.bots.size() + "]" );
        }

        /***************************************************************************************************************
        *   Draws the level onto the screen.
        ***************************************************************************************************************/
        public final void drawWalls()
        {
            //draw all walls
            for ( WallCollection meshCollection : this.wallCollections )
            {
                meshCollection.draw();
            }
        }

        /***************************************************************************************************************
        *   Draws the skybox onto the screen.
        ***************************************************************************************************************/
        public final void drawSkyBox()
        {
            this.skyBox.draw();
        }

        public final Float getHighestFloor(LibGameObject parentGameObject, LibVertex anchor, float radius, float height, int collisionCheckingSteps, LibDebug debug, boolean debugDrawBotCircles, float bottomCollisionToleranceZ, float minBottomCollisionToleranceZ, int ellipseSegments, Object exclude )
        {
            return this.getHighestFloor( new Cylinder(parentGameObject, anchor, radius, height, collisionCheckingSteps, debug, debugDrawBotCircles, bottomCollisionToleranceZ, minBottomCollisionToleranceZ, ellipseSegments, Material.EHumanFlesh ), exclude );
        }

        public final Float getHighestFloor( Cylinder cylinder, Object exclude )
        {
            //collect all hit points
            Vector<Float> hitPointsZ = new Vector<Float>();
            for ( WallCollection meshCollection : this.wallCollections)
            {
                //launch vertical collision check on all mesh-collections
                hitPointsZ.addAll( meshCollection.checkCollisionVert( cylinder, exclude ) );
            }
            Float ret = cylinder.getHighestOfCheckedFloor( hitPointsZ );

            //return nearest floor
            return ret;
        }

        public final void launchAction( Cylinder cylinder, Gadget gadget, float faceAngle )
        {
            //launch action on all mesh collections
            for ( WallCollection meshCollection : this.wallCollections)
            {
                //launch the shot on this mesh-collection
                meshCollection.launchAction( cylinder, gadget, faceAngle );
            }

            //launch action on all bots
            for ( Bot b : this.bots)
            {
                b.launchAction( cylinder, gadget, faceAngle );
            }
        }

        public final LibHitPoint[] launchShot( LibShot s )
        {
            //collect all hit points
            Vector<LibHitPoint> allHitPoints = new Vector<LibHitPoint>();

            //launch the shot on all walls
            for ( WallCollection wallCollection : this.wallCollections)
            {
                allHitPoints.addAll( wallCollection.launchShot( s ) );
            }

            //launch the shot on all bots' collision unit ( if not shot by a bot! )
            if ( s.origin != ShotOrigin.EEnemies )
            {
                for ( Bot bot : this.getBots() )
                {
                    allHitPoints.addAll( bot.launchShot( s ) );
                }
            }

            //launch the shot on the player ( if not shot by the player )
            if ( s.origin != ShotOrigin.EPlayer )
            {
                allHitPoints.addAll( Shooter.game.engine.player.launchShot( s ) );
            }

            //get all affected hitpoints
            LibHitPoint[] affectedHitPoints = null;

            //check if wall-breaking ammo has been used!
            if ( s.wallBreakingAmmo)
            {
                //all hitpoints are afftected
                affectedHitPoints = allHitPoints.toArray( new LibHitPoint[] {} );
            }
            else
            {
                //get nearest hitpoints ( considering penetrable walls )
                affectedHitPoints = LibHitPoint.getAffectedHitPoints( allHitPoints );
            }

            //draw nearest hp in 3d
            //if ( nearestHitPoint != null ) LibFXManager.launchStaticPoint( nearestHitPoint.vertex, LibColors.EWhite, 0.05f, 300 );

            //hurt all game objects only once
            Vector<LibGameObject> hitObjects = new Vector<LibGameObject>();

            //browse all afftected hitpoints
            for ( int i = 0; i < affectedHitPoints.length; ++i )
            {
                LibHitPoint affectedHitPoint = affectedHitPoints[ i ];

                //perform an operation if
                if
                (
                        //the game object is able to be hit
                        affectedHitPoint.carrier != null && affectedHitPoint.carrier.getHitPointCarrier() != null

                        //sharp ammo has been used
                    &&  s.type == ShotType.ESharpAmmo
                )
                {
                    //ShooterDebug.bugfix.out( " faceangle of hit face: [" + nearestHitPoint.horzFaceAngle + "]" );

                    //check the hitPoint's receiver
                    switch ( affectedHitPoint.carrier.getHitPointCarrier() )
                    {
                        case EWall:
                        {
                            Wall w = (Wall)affectedHitPoint.carrier;
                            boolean drawBulletHoleAndPlaySound = true;

                            //check if this is a projectile
                            if ( s.projectile != null )
                            {
                                //no bullet hole but projectile for last point ( if wall is not penetrable )
                                if ( i == affectedHitPoints.length - 1 && !affectedHitPoint.wallTexture.getMaterial().isPenetrable() )
                                {
                                    drawBulletHoleAndPlaySound  = false;

                                    //append projectile for last point
                                    Shooter.game.engine.bulletHoleManager.addBulletHole( affectedHitPoint, s.projectile);
                                }
                            }

                            //append bullet hole
                            if ( drawBulletHoleAndPlaySound && s.bulletHoleSize != LibHoleSize.ENone )
                            {
                                //draw bullet hole
                                Shooter.game.engine.bulletHoleManager.addBulletHole( affectedHitPoint, null );
                            }

                            //only once per wall
                            if ( !hitObjects.contains( w ) )
                            {
                                hitObjects.add( w );

                                //hurt wall ( not for projectiles
                                if ( s.projectile == null )
                                {
                                    w.hurt( s.damage, affectedHitPoint.horzInvertedShotAngle);
                                }

                                //draw sliver
                                affectedHitPoint.launchWallSliver
                                (
                                    s.particleQuantity,
                                    s.sliverAngleMod,
                                    FxSettings.LIFETIME_SLIVER,
                                    s.sliverSize,
                                    FXGravity.ENormal,
                                    affectedHitPoint.carrier,
                                    Level.currentSection()
                                );

                                //play wall sound ( not for target wall ! )
                                if ( affectedHitPoint.wallTexture != null && drawBulletHoleAndPlaySound )
                                {
                                    affectedHitPoint.wallTexture.getMaterial().getBulletImpactSound().playDistancedFx( new Point2D.Float( affectedHitPoint.vertex.x, affectedHitPoint.vertex.y ) );
                                }
                            }
                            break;
                        }

                        case EPlayer:
                        {
                            //only once
                            if ( !hitObjects.contains( Shooter.game.engine.player ) )
                            {
                                hitObjects.add( Shooter.game.engine.player );

                                //player loses health
                                Shooter.game.engine.player.hurt( LibMath.getRandom( 1, 1 ) );

                                //draw sliver
                                affectedHitPoint.launchWallSliver
                                (
                                    s.particleQuantity,
                                    s.sliverAngleMod,
                                    FxSettings.LIFETIME_SLIVER,
                                    s.sliverSize,
                                    FXGravity.ENormal,
                                    affectedHitPoint.carrier,
                                    Level.currentSection()
                                );
                            }
                            break;
                        }

                        case EBot:
                        {
                            //only once per bot
                            Bot b       = (Bot)affectedHitPoint.carrier;
                            if ( !hitObjects.contains( b ) )
                            {
                                hitObjects.add( b );

                                if ( s.projectile == null )
                                {
                                    //hurt bot
                                    int damage  = ( affectedHitPoint.damageMultiplier == -1 ? b.getHealth() : (int)( s.damage * affectedHitPoint.damageMultiplier) );
                                    ShooterDebug.bot.out( "damage is " + damage );
                                    b.hurt( damage );

                                    //draw blood
                                    affectedHitPoint.launchWallSliver
                                    (
                                        s.particleQuantity,
                                        s.sliverAngleMod,
                                        FxSettings.LIFETIME_BLOOD,
                                        FXSize.ELarge,
                                        FXGravity.ELow,
                                        affectedHitPoint.carrier,
                                        Level.currentSection()
                                    );
                                }
                                else
                                {
                                    //player falls asleep
                                    b.fallAsleep();
                                }

                                //play hit sound
                                affectedHitPoint.wallTexture.getMaterial().getBulletImpactSound().playDistancedFx( new Point2D.Float( affectedHitPoint.vertex.x, affectedHitPoint.vertex.y ) );
                            }
                            break;
                        }
                    }
                }

                //show debugs
              //s.debug.out( "hit point: [" + nearestHitPoint.vertex.x + "," + nearestHitPoint.vertex.y + ", " + nearestHitPoint.vertex.z + "]" );
              //s.debug.out( "shotAngle [" + nearestHitPoint.horzShotAngle + "] SliverAngle [" + nearestHitPoint.iHorzSliverAngle + "] invertedShotAngle [" + nearestHitPoint.horzInvertedShotAngle + "] faceAngle [" + nearestHitPoint.horzFaceAngle + "]" );
            }

            //s.debug.out( "=====================================================================\n" );

            return affectedHitPoints;
        }

        private void animateWalls()
        {
            //browse all mesh-collections
            for ( WallCollection meshCollection : this.wallCollections)
            {
                //animate all mesh-collections
                meshCollection.animate();
            }
        }

        private void animateBots()
        {
            for (int i = this.bots.size() - 1; i >= 0; --i )
            {
                //animate bot
                this.bots.elementAt( i ).animate();

                //prune if disappearing
                if (this.bots.elementAt( i ).isDead() )
                {
                    //decrease disappear timer
                    --this.bots.elementAt( i ).disappearTimer;

                    if (this.bots.elementAt( i ).disappearTimer <= General.FADE_OUT_FACES_TOTAL_TICKS )
                    {
                        this.bots.elementAt( i ).fadeOutAllFaces();
                    }

                    if (this.bots.elementAt( i ).disappearTimer <= 0 )
                    {
                        this.bots.removeElementAt( i );
                    }
                }
            }
        }

        public static Level currentSection()
        {
            return LevelCurrent.currentSection;
        }

        public final void drawAllBots()
        {
            //Debug.bot.out( "drawing ALL bots .."+botQueue.size()+"" );
            for ( Bot bot : this.bots)
            {
                bot.draw();
            }
        }

        public final boolean checkCollisionOnWalls( Cylinder cylinder )
        {
            //browse all mesh collections
            for ( WallCollection meshCollection : this.wallCollections)
            {
                //launch the collision on all mesh-collections
                if ( meshCollection.checkCollisionHorz( cylinder ) ) return true;
            }

            return false;
        }

        public final boolean checkCollisionOnBots( Cylinder cylinder )
        {
            //browse all bots
            for ( Bot bot : this.bots)
            {
                //launch the cylinder on all mesh-collections
                if ( bot.checkCollision( cylinder ) ) return true;
            }

            return false;
        }

        public final void orderLevelSectionChangeUp( boolean reset )
        {
            LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, this.currentSectionIndex + 1, reset );
        }

        public final void orderLevelSectionChangeDown( boolean reset )
        {
            LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, this.currentSectionIndex - 1, reset );
        }

        public final void drawAllItems()
        {
            //Debug.item.out( "drawing ALL items .."+itemQueue.size()+"" );
            for ( ItemToPickUp item : this.items)
            {
                item.draw();
            }
        }

        private void animateItems()
        {
            //browse reversed
            for (int j = this.items.size() - 1; j >= 0; --j )
            {
                //check if item is collected
                if (this.items.elementAt( j ).shallBeRemoved() )
                {
                    //remove collected items
                    this.items.removeElementAt( j );
                }
                else
                {
                    //check collisions on non-collected items
                    this.items.elementAt( j ).animate();
                }
            }
        }

        private void animateSkyBox()
        {
            this.skyBox.setNewAnchor( Shooter.game.engine.player.getAnchor(), true, LibTransformationMode.EOriginalsToTransformed );
        }

        public final Vector<Bot> getBots()
        {
            return this.bots;
        }

        public final LibColors getBackgroundColor()
        {
            return LevelCurrent.currentSectionConfigData[this.currentSectionIndex].bgCol;
        }

        public final void render()
        {
            boolean runPlayer = false;
            boolean runLevel  = false;

            //check player and level animation
            if (this.adrenalineTicks-- > 0 )
            {
                //enable adrenaline fx
                Shooter.game.engine.hudFx.drawAdrenalineFx = true;

                //check player animation
                if (this.adrenalineDelayPlayer > 0 )
                {
                    --this.adrenalineDelayPlayer;
                }
                else
                {
                    //animate level and restart delay
                    this.adrenalineDelayPlayer = 2;

                    //animate player
                    runPlayer = true;
                }

                //check level animation
                if (this.adrenalineDelayLevel > 0 )
                {
                    --this.adrenalineDelayLevel;
                }
                else
                {
                    //animate level and restart delay
                    this.adrenalineDelayLevel = 10;

                    //animate level
                    runLevel = true;
                }
            }
            else
            {
                //disable adrenaline fx
                Shooter.game.engine.hudFx.drawAdrenalineFx = false;

                //animate player and level
                runPlayer = true;
                runLevel = true;
            }

            //run player
            if ( runPlayer )
            {
                Shooter.game.engine.player.render();
            }

            //run level
            if ( runLevel )
            {
                //animate all walls
                this.animateWalls();

                //animate all bots
                this.animateBots();

                //check if player picked up an item
                this.animateItems();

                // animate skyBox
                this.animateSkyBox();

                //animate particle systems and HUD
                LibFXManager.onRun();
                Shooter.game.engine.hud.onRun();
            }
        }

        public final boolean hasInvisibleZLayer()
        {
            return ( LevelCurrent.currentLevelConfig.hasInvisibleZLayer == InvisibleZeroLayerZ.EYes );
        }

        public final void startAdrenaline()
        {
            this.adrenalineTicks = ShooterSetting.General.TICKS_ADRENALINE;
        }

        public final Bot getBotByID( int id )
        {
            for ( Bot bot : this.bots)
            {
                if ( bot.id == id )
                {
                    return bot;
                }
            }

            return null;
        }

        public final void assignWalls()
        {
            //assign walls
            this.wallCollections = LevelCurrent.getLevelWalls(this.currentSectionIndex);
        }

        public final void addItem( ItemToPickUp p )
        {
            this.items.add( p );
        }
    }
