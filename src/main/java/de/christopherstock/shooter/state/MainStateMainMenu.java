
    package de.christopherstock.shooter.state;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.Fonts;
    import  de.christopherstock.shooter.ShooterSetting.HUDSettings;
    import  de.christopherstock.shooter.io.hid.Keys;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.level.setup.*;

    /*******************************************************************************************************************
    *   The Heads Up Display.
    *******************************************************************************************************************/
    public class MainStateMainMenu
    {
        public enum MainMenuItem
        {
            EStartNewGameFacility(      "START NEW GAME - FACILITY"     ),
            EStartNewGameTestOffice(    "START NEW GAME - TEST-OFFICE"  ),
            EContinueGame(              "CONTINUE GAME"                 ),
/*
            ESaveGame(                  "SAVE GAME"                     ),
            ELoadGame(                  "LOAD GAME"                     ),
            EPreferences(               "PREFERENCES"                   ),
            ECredits(                   "CREDITS"                       ),
*/
            EQuitGame(                  "QUIT GAME"                     ),
            ;

            protected   LibGLImage  unselected                      = null;
            protected   LibGLImage  selected                        = null;

            private MainMenuItem( String label )
            {
                this.unselected = LibGLImage.getFromString( label, Fonts.EMainMenu, LibColors.EWhite.colABGR,  null, LibColors.EBlack.colABGR, ShooterDebug.glImage );
                this.selected   = LibGLImage.getFromString( label, Fonts.EMainMenu, LibColors.EOrangeMF.colABGR, null, LibColors.EBlack.colABGR, ShooterDebug.glImage );
            }

            protected void draw(int x, int y, MainMenuItem selectedItem)
            {
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( ( this == selectedItem ? this.selected : this.unselected), x, y, 1.0f );
            }
        }

        private         static          MainStateMainMenu                singleton                       = null;

        private         static          MainMenuItem            currentMainMenuItem             = null;

        private                         LibGLImage              blackPane                       = null;

        private                         int                     menuChangeBlocker               = 0;

        private MainStateMainMenu()
        {
            this.blackPane = LibGLImage.getFullOpaque( LibColors.EBlackTranslucent.colABGR, Shooter.game.engine.gl.panel.width, Shooter.game.engine.gl.panel.height, ShooterDebug.glImage );

        }

        public static void init()
        {
            getSingleton();
            currentMainMenuItem = MainMenuItem.EStartNewGameFacility;
        }

        public final void draw()
        {
            //draw hud
            Shooter.game.engine.hud.draw();

            //draw black pane
            Shooter.game.engine.gl.view.drawOrthoBitmapBytes( this.blackPane, 0,   0,   0.1f );

            //draw main menu
            for ( MainMenuItem m : MainMenuItem.values() )
            {
                m.draw( ( Shooter.game.engine.gl.panel.width - m.unselected.width ) / 2, 600 - m.ordinal() * 85, currentMainMenuItem );
            }
        }

        private void previousItem()
        {
            if (this.menuChangeBlocker == 0 )
            {
                if ( currentMainMenuItem.ordinal() > 0 )
                {
                    currentMainMenuItem = MainMenuItem.values()[ currentMainMenuItem.ordinal() - 1 ];
                    this.menuChangeBlocker = HUDSettings.TICKS_MAIN_MENU_BLOCKER;
                    SoundFg.ELocked1.playGlobalFx();
                }
            }
        }

        private void nextItem()
        {
            if (this.menuChangeBlocker == 0 )
            {
                if ( currentMainMenuItem.ordinal() < MainMenuItem.values().length - 1 )
                {
                    currentMainMenuItem = MainMenuItem.values()[ currentMainMenuItem.ordinal() + 1 ];
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
                switch ( currentMainMenuItem )
                {
                    case EStartNewGameFacility:
                    {
                        LevelChange.orderLevelChange( new LevelSetupTestMayflowerOffice(), ShooterSetting.Startup.STARTUP_LEVEL_SECTION, true );
                        Shooter.game.orderMainStateChangeTo( MainState.EIngame );
                        break;
                    }

                    case EStartNewGameTestOffice:
                    {
                        LevelChange.orderLevelChange( new LevelSetupTestFacility(), ShooterSetting.Startup.STARTUP_LEVEL_SECTION, true );
                        Shooter.game.orderMainStateChangeTo( MainState.EIngame );
                        break;
                    }

                    case EContinueGame:
                    {
                        //ShooterGameLevel.orderLevelChange( ShooterSetting.General.STARTUP_LEVEL, true );
                        Shooter.game.orderMainStateChangeTo( MainState.EIngame );
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

                            ShooterDebug.saveLoad.out( "saving walls # " + w.iMeshes.length );
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
                            ShooterDebug.saveLoad.out( "" + ( (WallCollection)loaded[ 1 ] ).iMeshes.length );
                            ShooterDebug.saveLoad.out( "" + ( (WallCollection)loaded[ 2 ] ).iMeshes.length );
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
                        Shooter.game.engine.gl.destroy();
                        break;
                    }
                }
            }
        }

        public final void onRun()
        {
            if (this.menuChangeBlocker > 0 ) --this.menuChangeBlocker;

            //check enter press
            Keys.enterKey.checkLaunchingAction();
        }

        public static MainStateMainMenu getSingleton()
        {
            if ( singleton == null )
            {
                singleton = new MainStateMainMenu();
            }

            return singleton;
        }

        public void checkMenuKeyEvents()
        {
            //check main menu toggle
            Keys.toggleMainMenu.checkLaunchingAction();

            //change to game
            if ( Keys.toggleMainMenu.iLaunchAction )
            {
                Keys.toggleMainMenu.iLaunchAction = false;
                Shooter.game.orderMainStateChangeTo( MainState.EIngame );
            }

            //change current main menu item
            if ( Keys.keyHoldWalkDown )
            {
                MainStateMainMenu.getSingleton().nextItem();
            }

            if ( Keys.keyHoldWalkUp )
            {
                MainStateMainMenu.getSingleton().previousItem();
            }

            //launch msg?
            if ( Keys.enterKey.iLaunchAction )
            {
                Keys.enterKey.iLaunchAction = false;

                MainStateMainMenu.getSingleton().selectItem();
            }
        }
    }
