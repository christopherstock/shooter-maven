
    package de.christopherstock.shooter.state;

    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterDebug;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.mesh.BotMeshes.ArmsPosition;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.level.setup.*;
    import de.christopherstock.lib.LibViewSet;
    import  de.christopherstock.lib.fx.*;

    /*******************************************************************************************************************
    *   The application's main thread. Start this thread to run the application.
    *******************************************************************************************************************/
    public class Ingame
    {
        // TODO prune!
        private static Ingame singleton                   = null;

        private Ingame()
        {
            //preloader constructor
        }

        public static Ingame getSingleton()
        {
            if ( singleton == null ) singleton = new Ingame();
            return singleton;
        }

        public final void draw()
        {
            //level may be null if not set
            if ( Level.currentSection() != null )
            {
                //reset gl
                Shooter.game.engine.glView.clearGl( Level.currentSection().getBackgroundColor() );

                //get camera from player's position and orientation
                LibViewSet cam = Shooter.game.engine.player.getCameraPositionAndRotation();

                //draw scene bg
                Level.currentSection().drawBg( cam );

                //set player's camera
                Shooter.game.engine.glView.setCamera( cam );

                //this would be the right time to enable lights
                //draw all game components
                Level.currentSection().draw();                          //draw the level
                Level.currentSection().drawAllItems();                  //draw all items
                Level.currentSection().drawAllBots();                   //draw all bots

                //draw player's crosshair if the wearpon uses ammo
              //if ( ShooterGameShooter.game.engine.player.showAmmoInHUD() ) ShooterGameShooter.game.engine.player.getCrosshair().draw();

                //bullet holes and fx points
                BulletHole.drawAll();                                           //draw all bullet holes
                LibFXManager.drawAll();                                         //draw all fx points
                Shooter.game.engine.player.drawStandingCircle();          //draw circle on players bottom location

                //flush face queue to force an immediate redraw
                Shooter.game.engine.glView.flushFaceQueue( Shooter.game.engine.player.getAnchor() );
            }

            //draw hud
            Shooter.game.engine.hud.draw();
        }


        public void checkGameKeyEvents()
        {
            //check main menu toggle
            Shooter.game.engine.keys.toggleMainMenu.checkLaunchingAction();

            //change to main menu
            if ( Shooter.game.engine.keys.toggleMainMenu.iLaunchAction )
            {
                Shooter.game.engine.keys.toggleMainMenu.iLaunchAction = false;
                Shooter.game.orderMainStateChange( MainState.EMainMenu );
            }

            //launch test exploisions?
            if ( Shooter.game.engine.keys.explosion.iLaunchAction )
            {
                Shooter.game.engine.keys.explosion.iLaunchAction = false;
/*
                float baseZ = ShooterGameShooter.game.engine.player.getAnchor().z;

                LibFXManager.launchExplosion( new LibVertex( 0.0f, 0.0f, 0.05f ), FXSize.ESmall,  FXTime.EShort, FxSettings.LIFETIME_EXPLOSION,  baseZ  );
                LibFXManager.launchExplosion( new LibVertex( 2.0f, 2.0f, 0.05f ), FXSize.EMedium, FXTime.EMedium, FxSettings.LIFETIME_EXPLOSION, baseZ  );
                LibFXManager.launchExplosion( new LibVertex( 4.0f, 4.0f, 0.05f ), FXSize.ELarge,  FXTime.ELong, FxSettings.LIFETIME_EXPLOSION,   baseZ  );

                //play explosion sound
                ShooterSound.EExplosion1.playDistancedFx( new Point2D.Float( 0.0f, 0.0f ) );
*/
            }

            //player action?
            if ( Shooter.game.engine.keys.playerAction.iLaunchAction )
            {
                Shooter.game.engine.keys.playerAction.iLaunchAction = false;
                Shooter.game.engine.player.launchAction( null );
            }

            //player action?
            if ( Shooter.game.engine.keys.crouching.iLaunchAction )
            {
                Shooter.game.engine.keys.crouching.iLaunchAction = false;
                Shooter.game.engine.player.toggleCrouching();
            }

            //gainHealth
            if ( Shooter.game.engine.keys.gainHealth.iLaunchAction )
            {
                Shooter.game.engine.keys.gainHealth.iLaunchAction = false;

                //heal player
                Shooter.game.engine.player.heal( 10 );
            }

            //hurt
            if ( Shooter.game.engine.keys.damageFx.iLaunchAction )
            {
                Shooter.game.engine.keys.damageFx.iLaunchAction = false;

                //hurt player
                Shooter.game.engine.player.hurt( 10 );
            }

            //launch msg?
            if ( Shooter.game.engine.keys.enterKey.iLaunchAction )
            {
                Shooter.game.engine.keys.enterKey.iLaunchAction = false;

                try
                {
                    Level.currentSection().getBotByID( LevelSetupTestMayflowerOffice.OFFICE_PARTNER_1 ).botMeshes.assignArmsPosition( ArmsPosition.EAimHighBoth );
                }
                catch ( Throwable t )
                {
                    ShooterDebug.error.out( "Throwable caught in debug code!" );
                }

/*
                //launch action on all bots
                if ( ShooterGameLevel.current().iBots.size() > 0 )
                {
                    for ( Bot b : ShooterGameLevel.current().iBots )
                    {
                        //move bot limbs



                        //launch bot sound
                        //b.makeDistancedSound( ShooterSound.EFemaleGiggle1 );
                    }
                }
*/
                //show avatar message
                //AvatarMessage.showDebugMessage();

                //Level.current().iBots.elementAt( 0 ).say( Sound.EFemaleGiggle1 );
            }
        }
    }
