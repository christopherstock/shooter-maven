
    package de.christopherstock.shooter.state;

    import  java.util.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.Fonts;
    import  de.christopherstock.shooter.ShooterSettings.HUDSettings;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;
    import  de.christopherstock.shooter.g3d.mesh.*;
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
            ESaveGame(                  "SAVE GAME"                     ),
            ELoadGame(                  "LOAD GAME"                     ),
            EPreferences(               "PREFERENCES"                   ),
            ECredits(                   "CREDITS"                       ),
            EQuitGame(                  "QUIT GAME"                     ),
            ;

            protected   LibGLImage  unselected                      = null;
            protected   LibGLImage  selected                        = null;

            private MainMenuItem( String label )
            {
                unselected = LibGLImage.getFromString( label,   Fonts.EMainMenu, LibColors.EBlack.colABGR,  null, LibColors.EWhite.colABGR, ShooterDebug.glImage );
                selected   = LibGLImage.getFromString( label,   Fonts.EMainMenu, LibColors.EWhite.colABGR,  null, null,                     ShooterDebug.glImage );
            }

            public void draw( int x, int y, MainMenuItem selectedItem )
            {
                LibGL3D.view.drawOrthoBitmapBytes( ( this == selectedItem ? selected : unselected ), x, y, 1.0f );
            }
        }

        private         static          MainStateMainMenu                singleton                       = null;

        private         static          MainMenuItem            currentMainMenuItem             = null;

        private                         LibGLImage              blackPane                       = null;

        private                         int                     menuChangeBlocker               = 0;

        private MainStateMainMenu()
        {
            blackPane = LibGLImage.getFullOpaque( LibColors.EBlack.colABGR, LibGL3D.panel.width, LibGL3D.panel.height, ShooterDebug.glImage );

        }

        public static void init()
        {
            getSingleton();
            currentMainMenuItem = MainMenuItem.EStartNewGameFacility;
        }

        public final void draw2D()
        {
            //draw hud
            Shooter.mainThread.hud.draw2D();

            //draw black pane
            LibGL3D.view.drawOrthoBitmapBytes( blackPane,               0,   0,   0.5f );

            //draw main menu
            for ( MainMenuItem m : MainMenuItem.values() )
            {
                m.draw( 100, 600 - m.ordinal() * 85, currentMainMenuItem );
            }
        }

        public final void previousItem()
        {
            if ( menuChangeBlocker == 0 )
            {
                if ( currentMainMenuItem.ordinal() > 0 )
                {
                    currentMainMenuItem = MainMenuItem.values()[ currentMainMenuItem.ordinal() - 1 ];
                    menuChangeBlocker = HUDSettings.TICKS_MAIN_MENU_BLOCKER;
                    SoundFg.ELocked1.playGlobalFx();
                }
            }
        }

        public final void nextItem()
        {
            if ( menuChangeBlocker == 0 )
            {
                if ( currentMainMenuItem.ordinal() < MainMenuItem.values().length - 1 )
                {
                    currentMainMenuItem = MainMenuItem.values()[ currentMainMenuItem.ordinal() + 1 ];
                    menuChangeBlocker = HUDSettings.TICKS_MAIN_MENU_BLOCKER;
                    SoundFg.ELocked1.playGlobalFx();
                }
            }
        }

        public final void selectItem()
        {
            if ( menuChangeBlocker == 0 )
            {
                menuChangeBlocker = HUDSettings.TICKS_MAIN_MENU_BLOCKER;
                SoundFg.ELocked1.playGlobalFx();
                switch ( currentMainMenuItem )
                {
                    case EStartNewGameFacility:
                    {
                        LevelChange.orderLevelChange( new LevelSetupTestFacility(), ShooterSettings.Startup.STARTUP_LEVEL_SECTION, true );
                        Shooter.mainThread.orderMainStateChangeTo( MainState.EIngame );
                        break;
                    }

                    case EStartNewGameTestOffice:
                    {
                        LevelChange.orderLevelChange( new LevelSetupTestOfficeCasino(), ShooterSettings.Startup.STARTUP_LEVEL_SECTION, true );
                        Shooter.mainThread.orderMainStateChangeTo( MainState.EIngame );
                        break;
                    }

                    case EContinueGame:
                    {
                        //ShooterGameLevel.orderLevelChange( ShooterSettings.General.STARTUP_LEVEL, true );
                        Shooter.mainThread.orderMainStateChangeTo( MainState.EIngame );
                        break;
                    }

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

                    case EQuitGame:
                    {
                        LibGL3D.destroy();
                        break;
                    }
                }
            }
        }

        public final void onRun()
        {
            if ( menuChangeBlocker > 0 ) --menuChangeBlocker;

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
                Shooter.mainThread.orderMainStateChangeTo( MainState.EIngame );
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
