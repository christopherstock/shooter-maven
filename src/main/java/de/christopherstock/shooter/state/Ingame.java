
    package de.christopherstock.shooter.state;

    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterDebug;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.mesh.BotMeshes.ArmsPosition;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.level.setup.*;
    import  de.christopherstock.lib.LibViewSet;
    import  de.christopherstock.lib.fx.*;

    /*******************************************************************************************************************
    *   The application's main thread. Start this thread to run the application.
    *******************************************************************************************************************/
    public class Ingame
    {
        public final void draw()
        {
            //level may be null if not set
            if ( Level.currentSection() != null )
            {
                //reset gl
                Shooter.game.engine.glView.clearGl( Level.currentSection().getBackgroundColor() );

                //get camera from player's position and orientation
                LibViewSet cam = Shooter.game.engine.player.getCameraPositionAndRotation();
/*
                //draw scene bg
                Level.currentSection().drawBg( cam );
*/
                //set player's camera
                Shooter.game.engine.glView.setCamera( cam );

                //this would be the right time to enable lights
                // Shooter.game.engine.glView.setLightsOn();

                //draw all game components
                Level.currentSection().drawWalls();    // draw the level
                Level.currentSection().drawSkyBox();   // draw sky box
                Level.currentSection().drawAllItems(); // draw all items
                Level.currentSection().drawAllBots();  // draw all bots

                //draw player's crosshair if the wearpon uses ammo
              //if ( ShooterGameShooter.game.engine.player.showAmmoInHUD() ) ShooterGameShooter.game.engine.player.getCrosshair().draw();

                //bullet holes and fx points
                Shooter.game.engine.bulletHoleManager.drawAll();            //draw all bullet holes
                LibFXManager.drawAll();                                     //draw all fx points
                Shooter.game.engine.player.drawStandingCircle();            //draw circle on players bottom location

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
            if ( Shooter.game.engine.keys.toggleMainMenu.launchAction)
            {
                Shooter.game.engine.keys.toggleMainMenu.launchAction = false;
                Shooter.game.orderMainStateChange( MainState.EMainMenu );
            }

            //launch test exploisions?
            if ( Shooter.game.engine.keys.explosion.launchAction)
            {
                Shooter.game.engine.keys.explosion.launchAction = false;
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
            if ( Shooter.game.engine.keys.playerAction.launchAction)
            {
                Shooter.game.engine.keys.playerAction.launchAction = false;
                Shooter.game.engine.player.launchAction( null );
            }

            //player action?
            if ( Shooter.game.engine.keys.crouching.launchAction)
            {
                Shooter.game.engine.keys.crouching.launchAction = false;
                Shooter.game.engine.player.toggleCrouching();
            }

            //gainHealth
            if ( Shooter.game.engine.keys.gainHealth.launchAction)
            {
                Shooter.game.engine.keys.gainHealth.launchAction = false;

                //heal player
                Shooter.game.engine.player.heal( 10 );
            }

            //hurt
            if ( Shooter.game.engine.keys.damageFx.launchAction)
            {
                Shooter.game.engine.keys.damageFx.launchAction = false;

                //hurt player
                Shooter.game.engine.player.hurt( 10 );
            }

            //launch msg?
            if ( Shooter.game.engine.keys.enterKey.launchAction)
            {
                Shooter.game.engine.keys.enterKey.launchAction = false;

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
                if ( ShooterGameLevel.current().bots.size() > 0 )
                {
                    for ( Bot b : ShooterGameLevel.current().bots )
                    {
                        //move bot limbs



                        //launch bot sound
                        //b.makeDistancedSound( ShooterSound.EFemaleGiggle1 );
                    }
                }
*/
                //show avatar message
                //AvatarMessage.showDebugMessage();

                //Level.current().bots.elementAt( 0 ).say( Sound.EFemaleGiggle1 );
            }
        }
    }
