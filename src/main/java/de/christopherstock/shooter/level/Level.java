/*  $Id: ShooterGameLevel.java 1261 2013-01-05 00:51:42Z jenetic.bytemare@gmail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.level;

    import  java.awt.geom.Point2D;
    import  java.util.*;
    import  de.christopherstock.lib.LibDebug;
    import  de.christopherstock.lib.Lib.ViewSet;
    import  de.christopherstock.lib.fx.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.lib.gl.LibFloorStack;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.*;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.g3d.wall.*;
    import  de.christopherstock.shooter.game.artefact.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.bot.*;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.ui.hud.*;
    import  de.christopherstock.shooter.ui.hud.HUD.*;

    /**************************************************************************************
    *   Holds all values that are bound to the current active level.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class Level implements LibFloorStack
    {
        /**************************************************************************************
        *   The global player-instance being controlled by the user. TODO ASAP why global static ??
        **************************************************************************************/
        private     static          Player                      player                          = null;

        private                     Vector<ItemToPickUp>        iItems                          = null;
        private                     Vector<Bot>                 iBots                           = null;
        private                     WallCollection[]            iWallCollections                = null;
        private                     int                         iCurrentSectionIndex            = 0;
        private                     int                         iAdrenalineTicks                = 0;
        private                     int                         iAdrenalineDelayLevel           = 0;
        private                     int                         iAdrenalineDelayPlayer          = 0;

        public static enum InvisibleZeroLayerZ
        {
            ENo,
            EYes,
            ;
        }

        private Level( int aConfig )
        {
            //keep target level as own config
            iCurrentSectionIndex = aConfig;
        }

        public static final void init()
        {
            //ShooterDebug.bugfix.out( "Init level" );

            //remove all bullet holes
            BulletHole.clearBulletHoles();

            //remove all particles
            LibFXManager.removeAllFxPoints();

            //create player
            player = new Player
            (
                LevelCurrent.currentLevelConfig.iStartPosition,
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

        public final void initPlayer()
        {
            //handle startup items and wearpons to the player
            for ( ItemEvent i : LevelCurrent.currentLevelConfig.iStartupItems )
            {
                i.perform( null );
            }
            for ( ArtefactType w : LevelCurrent.currentLevelConfig.iStartupWearpons )
            {
                Artefact toDeliver = new Artefact( w );
                player.iArtefactSet.deliverArtefact( toDeliver );

                //reload if firearm
                if ( w.isFireArm() )
                {
                    //ShooterDebug.bugfix.out( "reload initial wearpon" );
                    toDeliver.reload( player.iAmmoSet, false, false, null );
                }
            }

            //reset HUD-animation so change to 1st artefact can occur
            Shooter.mainThread.iHUD.resetAnimation();

            //change to 1st artefact
            player.orderWearponOrGadget( ChangeAction.EActionNext );
        }

        private void initLevel()
        {
            //spawn specified items
            iItems   = new Vector<ItemToPickUp>();
            if ( LevelCurrent.currentSectionConfigData[ iCurrentSectionIndex ].iItems != null )
            {
                for ( ItemToPickUp aItem : LevelCurrent.currentSectionConfigData[ iCurrentSectionIndex ].iItems )
                {
                    //load item's d3ds and add it to the stack
                    aItem.loadD3ds();
                    iItems.add( aItem );
                }
            }

            //create and add all bots
            iBots    = new Vector<Bot>();
            for ( BotFactory b : LevelCurrent.currentSectionConfigData[ iCurrentSectionIndex ].iBots )
            {
                //init / reset bot
                Bot botToAdd = b.createBot();
                //ShooterDebug.bugfix.out( "reset bot" );
                addBot( botToAdd );
            }
        }

        protected final void addBot( Bot botToAdd )
        {
            iBots.add( botToAdd );
            ShooterDebug.bot.out( "adding bot. capacity is now [" + iBots.size() + "]" );
        }

        /**************************************************************************************
        *   Draws the level onto the screen.
        **************************************************************************************/
        public final void draw()
        {
            //draw all walls
            for ( WallCollection meshCollection : iWallCollections )
            {
                meshCollection.draw();
            }
        }

        @Override
        public final Float getHighestFloor( LibGameObject aParentGameObject, LibVertex aAnchor, float aRadius, float aHeight, int aCollisionCheckingSteps, LibDebug aDebug, boolean aDebugDrawBotCircles, float aBottomCollisionToleranceZ, float aMinBottomCollisionToleranceZ, int aEllipseSegments, Object exclude )
        {
            return getHighestFloor( new Cylinder( aParentGameObject, aAnchor, aRadius, aHeight, aCollisionCheckingSteps, aDebug, aDebugDrawBotCircles, aBottomCollisionToleranceZ, aMinBottomCollisionToleranceZ, aEllipseSegments, Material.EHumanFlesh ), exclude );
        }

        public final Float getHighestFloor( Cylinder cylinder, Object exclude )
        {
            //collect all hit points
            Vector<Float> hitPointsZ = new Vector<Float>();
            for ( WallCollection meshCollection : iWallCollections )
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
            for ( WallCollection meshCollection : iWallCollections )
            {
                //launch the shot on this mesh-collection
                meshCollection.launchAction( cylinder, gadget, faceAngle );
            }

            //launch action on all bots
            for ( Bot b : iBots )
            {
                b.launchAction( cylinder, gadget, faceAngle );
            }
        }

        public final LibHitPoint[] launchShot( LibShot s )
        {
            //collect all hit points
            Vector<LibHitPoint> allHitPoints = new Vector<LibHitPoint>();

            //launch the shot on all walls
            for ( WallCollection wallCollection : iWallCollections )
            {
                allHitPoints.addAll( wallCollection.launchShot( s ) );
            }

            //launch the shot on all bots' collision unit ( if not shot by a bot! )
            if ( s.iOrigin != ShotOrigin.EEnemies )
            {
                for ( Bot bot : getBots() )
                {
                    allHitPoints.addAll( bot.launchShot( s ) );
                }
            }

            //launch the shot on the player ( if not shot by the player )
            if ( s.iOrigin != ShotOrigin.EPlayer )
            {
                allHitPoints.addAll( player.launchShot( s ) );
            }

            //get all affected hitpoints
            LibHitPoint[] affectedHitPoints = null;

            //check if wall-breaking ammo has been used!
            if ( s.iWallBreakingAmmo )
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
            //if ( nearestHitPoint != null ) LibFXManager.launchStaticPoint( nearestHitPoint.iVertex, LibColors.EWhite, 0.05f, 300 );

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
                        affectedHitPoint.iCarrier != null && affectedHitPoint.iCarrier.getHitPointCarrier() != null

                        //sharp ammo has been used
                    &&  s.iType == ShotType.ESharpAmmo
                )
                {
                    //ShooterDebug.bugfix.out( " faceangle of hit face: [" + nearestHitPoint.horzFaceAngle + "]" );

                    //check the hitPoint's receiver
                    switch ( affectedHitPoint.iCarrier.getHitPointCarrier() )
                    {
                        case EWall:
                        {
                            Wall w = (Wall)affectedHitPoint.iCarrier;
                            boolean drawBulletHoleAndPlaySound = true;

                            //check if this is a projectile
                            if ( s.iProjectile != null )
                            {
                                //no bullet hole but projectile for last point ( if wall is not penetrable )
                                if ( i == affectedHitPoints.length - 1 && !affectedHitPoint.iWallTexture.getMaterial().isPenetrable() )
                                {
                                    drawBulletHoleAndPlaySound  = false;

                                    //append projectile for last point
                                    BulletHole.addBulletHole( affectedHitPoint, s.iProjectile );
                                }
                            }

                            //append bullet hole
                            if ( drawBulletHoleAndPlaySound && s.iBulletHoleSize != LibHoleSize.ENone )
                            {
                                //draw bullet hole
                                BulletHole.addBulletHole( affectedHitPoint, null );
                            }

                            //only once per wall
                            if ( !hitObjects.contains( w ) )
                            {
                                hitObjects.add( w );

                                //hurt wall ( not for projectiles
                                if ( s.iProjectile == null )
                                {
                                    w.hurt( s.iDamage, affectedHitPoint.iHorzInvertedShotAngle );
                                }

                                //draw sliver
                                affectedHitPoint.launchWallSliver
                                (
                                    s.iParticleQuantity,
                                    s.iSliverAngleMod,
                                    FxSettings.LIFETIME_SLIVER,
                                    s.iSliverSize,
                                    FXGravity.ENormal,
                                    affectedHitPoint.iCarrier,
                                    Level.currentSection()
                                );

                                //play wall sound ( not for target wall ! )
                                if ( affectedHitPoint.iWallTexture != null && drawBulletHoleAndPlaySound )
                                {
                                    affectedHitPoint.iWallTexture.getMaterial().getBulletImpactSound().playDistancedFx( new Point2D.Float( affectedHitPoint.iVertex.x, affectedHitPoint.iVertex.y ) );
                                }
                            }
                            break;
                        }

                        case EPlayer:
                        {
                            //only once
                            if ( !hitObjects.contains( player ) )
                            {
                                hitObjects.add( player );

                                //player loses health
                                player.hurt( LibMath.getRandom( 1, 1 ) );

                                //draw sliver
                                affectedHitPoint.launchWallSliver
                                (
                                    s.iParticleQuantity,
                                    s.iSliverAngleMod,
                                    FxSettings.LIFETIME_SLIVER,
                                    s.iSliverSize,
                                    FXGravity.ENormal,
                                    affectedHitPoint.iCarrier,
                                    Level.currentSection()
                                );
                            }
                            break;
                        }

                        case EBot:
                        {
                            //only once per bot
                            Bot b       = (Bot)affectedHitPoint.iCarrier;
                            if ( !hitObjects.contains( b ) )
                            {
                                hitObjects.add( b );

                                if ( s.iProjectile == null )
                                {
                                    //hurt bot
                                    int damage  = ( affectedHitPoint.iDamageMultiplier == -1 ? b.getHealth() : (int)( s.iDamage * affectedHitPoint.iDamageMultiplier ) );
                                    ShooterDebug.bot.out( "damage is " + damage );
                                    b.hurt( damage );

                                    //draw blood
                                    affectedHitPoint.launchWallSliver
                                    (
                                        s.iParticleQuantity,
                                        s.iSliverAngleMod,
                                        FxSettings.LIFETIME_BLOOD,
                                        FXSize.ELarge,
                                        FXGravity.ELow,
                                        affectedHitPoint.iCarrier,
                                        Level.currentSection()
                                    );
                                }
                                else
                                {
                                    //player falls asleep
                                    b.fallAsleep();
                                }

                                //play hit sound
                                affectedHitPoint.iWallTexture.getMaterial().getBulletImpactSound().playDistancedFx( new Point2D.Float( affectedHitPoint.iVertex.x, affectedHitPoint.iVertex.y ) );
                            }
                            break;
                        }
                    }
                }

                //show debugs
              //s.iDebug.out( "hit point: [" + nearestHitPoint.iVertex.x + "," + nearestHitPoint.iVertex.y + ", " + nearestHitPoint.iVertex.z + "]" );
              //s.iDebug.out( "shotAngle [" + nearestHitPoint.iHorzShotAngle + "] SliverAngle [" + nearestHitPoint.iHorzSliverAngle + "] invertedShotAngle [" + nearestHitPoint.iHorzInvertedShotAngle + "] faceAngle [" + nearestHitPoint.iHorzFaceAngle + "]" );
            }

            //s.iDebug.out( "=====================================================================\n" );

            return affectedHitPoints;
        }

        private final void animateWalls()
        {
            //browse all mesh-collections
            for ( WallCollection meshCollection : iWallCollections )
            {
                //animate all mesh-collections
                meshCollection.animate();
            }
        }

        private final void animateBots()
        {
            for ( int i = iBots.size() - 1; i >= 0; --i )
            {
                //animate bot
                iBots.elementAt( i ).animate();

                //prune if disappearing
                if ( iBots.elementAt( i ).isDead() )
                {
                    //decrease disappear timer
                    --iBots.elementAt( i ).iDisappearTimer;

                    if ( iBots.elementAt( i ).iDisappearTimer <= General.FADE_OUT_FACES_TOTAL_TICKS )
                    {
                        iBots.elementAt( i ).fadeOutAllFaces();
                    }

                    if ( iBots.elementAt( i ).iDisappearTimer <= 0 )
                    {
                        iBots.removeElementAt( i );
                    }
                }
            }
        }

        public static Level currentSection()
        {
            return LevelCurrent.currentSection;
        }

        public static Player currentPlayer()
        {
            return player;
        }

        public final void drawAllBots()
        {
            //Debug.bot.out( "drawing ALL bots .."+botQueue.size()+"" );
            for ( Bot bot : iBots )
            {
                bot.draw();
            }
        }

        public final boolean checkCollisionOnWalls( Cylinder cylinder )
        {
            //browse all mesh collections
            for ( WallCollection meshCollection : iWallCollections )
            {
                //launch the collision on all mesh-collections
                if ( meshCollection.checkCollisionHorz( cylinder ) ) return true;
            }

            return false;
        }

        public final boolean checkCollisionOnBots( Cylinder cylinder )
        {
            //browse all bots
            for ( Bot bot : iBots )
            {
                //launch the cylinder on all mesh-collections
                if ( bot.checkCollision( cylinder ) ) return true;
            }

            return false;
        }

        public final void drawBg( ViewSet cam )
        {
            if ( LevelCurrent.currentSectionConfigData[ iCurrentSectionIndex ].iBg != null ) LevelCurrent.currentSectionConfigData[ iCurrentSectionIndex ].iBg.drawOrtho( cam.rot.x, cam.rot.z );
        }

        public final void orderLevelSectionChangeUp( boolean reset )
        {
            LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, iCurrentSectionIndex + 1, reset );
        }

        public final void orderLevelSectionChangeDown( boolean reset )
        {
            LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, iCurrentSectionIndex - 1, reset );
        }

        public final void drawAllItems()
        {
            //Debug.item.out( "drawing ALL items .."+itemQueue.size()+"" );
            for ( ItemToPickUp item : iItems )
            {
                item.draw();
            }
        }

        private final void animateItems()
        {
            //browse reversed
            for ( int j = iItems.size() - 1; j >= 0; --j )
            {
                //check if item is collected
                if ( iItems.elementAt( j ).shallBeRemoved() )
                {
                    //remove collected items
                    iItems.removeElementAt( j );
                }
                else
                {
                    //check collisions on non-collected items
                    iItems.elementAt( j ).animate();
                }
            }
        }

        public final Vector<Bot> getBots()
        {
            return iBots;
        }

        public final LibColors getBackgroundColor()
        {
            return LevelCurrent.currentSectionConfigData[ iCurrentSectionIndex ].iBgCol;
        }

        public final void onRun()
        {
            boolean runPlayer = false;
            boolean runLevel  = false;

            //check player and level animation
            if ( iAdrenalineTicks-- > 0 )
            {
                //enable adrenaline fx
                HUDFx.drawAdrenalineFx = true;

                //check player animation
                if ( iAdrenalineDelayPlayer > 0 )
                {
                    --iAdrenalineDelayPlayer;
                }
                else
                {
                    //animate level and restart delay
                    iAdrenalineDelayPlayer = 2;

                    //animate player
                    runPlayer = true;
                }

                //check level animation
                if ( iAdrenalineDelayLevel > 0 )
                {
                    --iAdrenalineDelayLevel;
                }
                else
                {
                    //animate level and restart delay
                    iAdrenalineDelayLevel = 10;

                    //animate level
                    runLevel = true;
                }
            }
            else
            {
                //disable adrenaline fx
                HUDFx.drawAdrenalineFx = false;

                //animate player and level
                runPlayer = true;
                runLevel = true;
            }

            //run player
            if ( runPlayer )
            {
                player.onRun();
            }

            //run level
            if ( runLevel )
            {
                //animate all walls
                animateWalls();

                //animate all bots
                animateBots();

                //check if player picked up an item
                animateItems();

                //animate particle systems and HUD
                LibFXManager.onRun();
                Shooter.mainThread.iHUD.onRun();
            }
        }

        public final boolean hasInvisibleZLayer()
        {
            return ( LevelCurrent.currentLevelConfig.iHasInvisibleZLayer == InvisibleZeroLayerZ.EYes );
        }

        public final void startAdrenaline()
        {
            iAdrenalineTicks = ShooterSettings.General.TICKS_ADRENALINE;
        }

        public final Bot getBotByID( int id )
        {
            for ( Bot bot : iBots )
            {
                if ( bot.iID == id )
                {
                    return bot;
                }
            }

            return null;
        }

        public final void assignWalls()
        {
            //assign walls
            iWallCollections = LevelCurrent.getLevelWalls( iCurrentSectionIndex );
        }

        public final void addItem( ItemToPickUp p )
        {
            iItems.add( p );
        }
    }
