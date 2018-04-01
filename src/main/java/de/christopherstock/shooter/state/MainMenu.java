
    package de.christopherstock.shooter.state;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.HUDSettings;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.level.setup.*;

    /*******************************************************************************************************************
    *   The Heads Up Display.
    *******************************************************************************************************************/
    public class MainMenu
    {
        public enum MainMenuItem
        {
            EStartNewGame(              "START NEW GAME"                ),
/*
            EStartNewGameTestOffice(    "START NEW GAME - TEST-OFFICE"  ),
*/
            EContinueGame(              "CONTINUE GAME"                 ),
/*
            ESaveGame(                  "SAVE GAME"                     ),
            ELoadGame(                  "LOAD GAME"                     ),
            EPreferences(               "PREFERENCES"                   ),
            ECredits(                   "CREDITS"                       ),
*/
            EQuitGame(                  "QUIT GAME"                     ),
            ;

            protected               LibGLTextureImage       unselected                      = null;
            protected               LibGLTextureImage       selected                        = null;

            private MainMenuItem( String label )
            {
                this.unselected = LibGLTextureImage.getFromString( label, Shooter.game.engine.fonts.mainMenu, LibColors.EWhite.colABGR,    null, LibColors.EBlack.colABGR, ShooterDebug.glImage );
                this.selected   = LibGLTextureImage.getFromString( label, Shooter.game.engine.fonts.mainMenu, LibColors.EOrangeMF.colABGR, null, LibColors.EBlack.colABGR, ShooterDebug.glImage );
            }

            protected void draw( int x, int y, MainMenuItem selectedItem )
            {
                Shooter.game.engine.glView.drawOrthoBitmapBytes( ( this == selectedItem ? this.selected : this.unselected), x, y, 1.0f );
            }
        }

        private             MainMenuItem            currentMainMenuItem             = null;

        private             LibGLTextureImage       blackPane                       = null;

        private             int                     menuChangeBlocker               = 0;

        public MainMenu()
        {
            this.blackPane = LibGLTextureImage.getFullOpaque( LibColors.EBlackTranslucent.colABGR, Shooter.game.engine.glView.width, Shooter.game.engine.glView.height, ShooterDebug.glImage );
        }

        public void init()
        {
            this.currentMainMenuItem = MainMenuItem.EStartNewGame;
        }

        public final void draw()
        {
            //draw hud
            Shooter.game.engine.hud.draw();

            //draw black pane
            Shooter.game.engine.glView.drawOrthoBitmapBytes( this.blackPane, 0,   0,   0.1f );

            //draw main menu
            for ( MainMenuItem m : MainMenuItem.values() )
            {
                m.draw( ( Shooter.game.engine.glView.width - m.unselected.width ) / 2, 600 - m.ordinal() * 85, this.currentMainMenuItem );
            }
        }

        private void previousItem()
        {
            if (this.menuChangeBlocker == 0 )
            {
                if ( this.currentMainMenuItem.ordinal() > 0 )
                {
                    this.currentMainMenuItem = MainMenuItem.values()[ this.currentMainMenuItem.ordinal() - 1 ];
                    this.menuChangeBlocker = HUDSettings.TICKS_MAIN_MENU_BLOCKER;
                    SoundFg.ELocked1.playGlobalFx();
                }
            }
        }

        private void nextItem()
        {
            if (this.menuChangeBlocker == 0 )
            {
                if ( this.currentMainMenuItem.ordinal() < MainMenuItem.values().length - 1 )
                {
                    this.currentMainMenuItem = MainMenuItem.values()[ this.currentMainMenuItem.ordinal() + 1 ];
                    this.menuChangeBlocker = HUDSettings.TICKS_MAIN_MENU_BLOCKER;
                    SoundFg.ELocked1.playGlobalFx();
                }
            }
        }

        private void selectItem()
        {
            if (this.menuChangeBlocker == 0 )
            {
                this.menuChangeBlocker = HUDSettings.TICKS_MAIN_MENU_BLOCKER;
                SoundFg.ELocked1.playGlobalFx();
                switch ( this.currentMainMenuItem )
                {
                    case EStartNewGame:
                    {
                        LevelChange.orderLevelChange( new LevelSetupTestMayflowerOffice(), ShooterSetting.Startup.STARTUP_LEVEL_SECTION, true );
                        Shooter.game.orderMainStateChange( MainState.EIngame );
                        break;
                    }
/*
                    case EStartNewGameTestOffice:
                    {
                        LevelChange.orderLevelChange( new LevelSetupTestFacility(), ShooterSetting.Startup.STARTUP_LEVEL_SECTION, true );
                        Shooter.game.orderMainStateChange( MainState.EIngame );
                        break;
                    }
*/
                    case EContinueGame:
                    {
                        //ShooterGameLevel.orderLevelChange( ShooterSetting.General.STARTUP_LEVEL, true );
                        Shooter.game.orderMainStateChange( MainState.EIngame );
                        break;
                    }
/*
                    case ESaveGame:
                    {
                        Vector<Object> toSave = new Vector<Object>();

                        //can these objects be restored ??? :/

                        toSave.add( WallTex.EBricks2 );

                        for ( WallCollection w : LevelCurrent.currentGlobalMeshData )
                        {
                            toSave.add( w );

                            ShooterDebug.saveLoad.out( "saving walls # " + w.meshes.length );
                        }

                        try
                        {
                            LibIO.saveObjects( "out.bin", toSave );
                        }
                        catch ( Throwable t )
                        {
                            ShooterDebug.error.out( "Exception thrown on saving\n" + t );
                        }
                        break;
                    }

                    case ELoadGame:
                    {
                        try
                        {
                            Object[] loaded = LibIO.loadObjects( "out.bin" );

                            ShooterDebug.saveLoad.out( "Loaded [" + loaded.length + "] objects:" );

                            ShooterDebug.saveLoad.out( loaded[ 0 ] );
                            ShooterDebug.saveLoad.out( "" + ( (WallCollection)loaded[ 1 ] ).meshes.length );
                            ShooterDebug.saveLoad.out( "" + ( (WallCollection)loaded[ 2 ] ).meshes.length );
                        }
                        catch ( Throwable t )
                        {
                            ShooterDebug.error.out( "Exception thrown on loading\n" + t );
                        }
                        break;
                    }

                    case ECredits:
                    {


                        break;
                    }

                    case EPreferences:
                    {

                        break;
                    }
*/
                    case EQuitGame:
                    {
                        Shooter.game.quit();
                        break;
                    }
                }
            }
        }

        public final void onRun()
        {
            if (this.menuChangeBlocker > 0 ) --this.menuChangeBlocker;

            //check enter press
            Shooter.game.engine.keys.enterKey.checkLaunchingAction();
        }

        public void checkMenuKeyEvents()
        {
            //check main menu toggle
            Shooter.game.engine.keys.toggleMainMenu.checkLaunchingAction();

            //change to game
            if ( Shooter.game.engine.keys.toggleMainMenu.launchAction)
            {
                Shooter.game.engine.keys.toggleMainMenu.launchAction = false;
                Shooter.game.orderMainStateChange( MainState.EIngame );
            }

            //change current main menu item
            if ( Shooter.game.engine.keys.keyHoldWalkDown )
            {
                Shooter.game.engine.mainMenu.nextItem();
            }

            if ( Shooter.game.engine.keys.keyHoldWalkUp )
            {
                Shooter.game.engine.mainMenu.previousItem();
            }

            //launch msg?
            if ( Shooter.game.engine.keys.enterKey.launchAction)
            {
                Shooter.game.engine.keys.enterKey.launchAction = false;

                Shooter.game.engine.mainMenu.selectItem();
            }
        }
    }
