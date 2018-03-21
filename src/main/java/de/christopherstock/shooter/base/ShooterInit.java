
    package de.christopherstock.shooter.base;

    import  java.awt.image.*;
    import  java.io.*;
    import  javax.imageio.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.LibFPS;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.*;
    import  de.christopherstock.shooter.io.hid.lwjgl.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.state.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   The application's main thread. Start this thread to run the application.
    *******************************************************************************************************************/
    public class ShooterInit
    {
        /***************************************************************************************************************
        *   Inits the ui.
        ***************************************************************************************************************/
        protected static final void initUi()
        {
            ShooterDebug.init.out( "initUi 1" );

            BufferedImage iconImage = null;
            BufferedImage bgImage   = null;

            //load form utils
            try
            {
                iconImage   = ImageIO.read( LibIO.preStreamJarResource( ShooterSettings.Path.EScreen.iUrl + "icon.png" ) );
                bgImage     = ImageIO.read( LibIO.preStreamJarResource( ShooterSettings.Path.EScreen.iUrl + "bg.jpg"   ) );

                MainStatePreloader.getSingleton().preloaderImage  = new LibGLImage( iconImage, LibGLImage.ImageUsage.EOrtho, ShooterDebug.glImage, false );
                MainStatePreloader.getSingleton().bgImage         = new LibGLImage( bgImage,   LibGLImage.ImageUsage.EOrtho, ShooterDebug.glImage, true );
            }
            catch ( IOException ioe )
            {
                ShooterDebug.error.err( "ERROR! Screen-Graphics could not be loaded!" );
            }
            ShooterDebug.init.out( "initUi 2" );

            //set host-os lookAndFeel
            LibGLForm.setLookAndFeel( ShooterDebug.error );
            ShooterDebug.init.out( "initUi 3" );

            //init external gl library
            LibGL3D.init
            (
                ShooterSettings.Form.FORM_WIDTH,
                ShooterSettings.Form.FORM_HEIGHT,
                ShooterSettings.Form.FORM_TITLE,
                Shooter.mainThread,
                Shooter.mainThread,
                iconImage,
                bgImage,
                ShooterDebug.init
            );

            ShooterDebug.init.out( "initUi 4" );
        }

        /***************************************************************************************************************
        *   Inits the rest.
        ***************************************************************************************************************/
        protected static final void initRest()
        {
            ShooterDebug.init.out( "initUi 5" );

            //center mouse and make it invisible
            LWJGLMouse.init();

            //init fonts
            MainStatePreloader.getSingleton().initFonts();


            //load texture images and perform repaint
            MainStatePreloader.getSingleton().increase( "Loading textures" );
            ShooterTexture.loadImages();

            ShooterDebug.init.out( "initUi 6" );

            //assign textures and perform repaint
            MainStatePreloader.getSingleton().increase( "Assigning textures" );
            LibGL3D.view.initTextures( ShooterTexture.getAllTextureImages() );

            ShooterDebug.init.out( "initUi 7" );

            //init 3d studio max objects and perform repaint
            MainStatePreloader.getSingleton().increase( "Loading 3dsmax files" );
            ShooterD3ds.init( ShooterDebug.d3ds );

            ShooterDebug.init.out( "initUi 8" );

            //init hud
            MainStatePreloader.getSingleton().increase( "Initing HUD and sound" );
            Shooter.mainThread.iHUD = new HUD();
            Shooter.mainThread.iFPS = new LibFPS( Fonts.EFps, ShooterSettings.Colors.EFpsFg.colABGR, ShooterSettings.Colors.EFpsOutline.colABGR, ShooterDebug.glImage );

            //init HUD fx
            HUDFx.init();

            //init sounds and bg sounds
            SoundFg.init();
            SoundBg.init();

            ShooterDebug.init.out( "initUi 9" );

            //switch main state to 'game' and order change to level 1
            MainStatePreloader.getSingleton().increase( "Init main menu and launch game" );

            //init main menu
            MainStateMainMenu.init();

            //no level init required!init all level data and game levels
            //ShooterLevelCurrent.init( Startup.STARTUP_LEVEL_MAIN );
            //ShooterGameLevel.init();

            //reset and change to startup main state
            Shooter.mainThread.orderMainStateChangeTo( ShooterSettings.Startup.STARTUP_STATE_AFTER_PRELOADER );
            LevelChange.orderLevelChange( Startup.STARTUP_LEVEL_MAIN, Startup.STARTUP_LEVEL_SECTION, true );

            ShooterDebug.init.out( "initUi 10" );
        }
    }
