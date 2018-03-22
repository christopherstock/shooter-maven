
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
        protected void initUi()
        {
            ShooterDebug.init.out( "initUi 1" );

            BufferedImage iconImage = null;
            BufferedImage bgImage   = null;

            //load form utils
            try
            {
                iconImage   = ImageIO.read( LibIO.preStreamJarResource( ShooterSettings.Path.EScreen.iUrl + "icon.png" ) );
                bgImage     = ImageIO.read( LibIO.preStreamJarResource( ShooterSettings.Path.EScreen.iUrl + "bg.jpg"   ) );

                Shooter.game.preloader = new Preloader();

                Shooter.game.preloader.preloaderImage  = new LibGLImage( iconImage, LibGLImage.ImageUsage.EOrtho, ShooterDebug.glImage, false );
                Shooter.game.preloader.bgImage         = new LibGLImage( bgImage,   LibGLImage.ImageUsage.EOrtho, ShooterDebug.glImage, true );
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
                Shooter.game,
                Shooter.game,
                iconImage,
                bgImage,
                ShooterDebug.init
            );

            ShooterDebug.init.out( "initUi 4" );
        }

        /***************************************************************************************************************
        *   Inits the rest.
        ***************************************************************************************************************/
        protected void initRest()
        {
            ShooterDebug.init.out( "initUi 5" );

            //center mouse and make it invisible
            LWJGLMouse.init();

            //init fonts
            Shooter.game.preloader.initFonts();


            //load texture images and perform repaint
            Shooter.game.preloader.increase( "Loading textures" );
            ShooterTexture.loadImages();

            ShooterDebug.init.out( "initUi 6" );

            //assign textures and perform repaint
            Shooter.game.preloader.increase( "Assigning textures" );
            LibGL3D.view.initTextures( ShooterTexture.getAllTextureImages() );

            ShooterDebug.init.out( "initUi 7" );

            //init 3d studio max objects and perform repaint
            Shooter.game.preloader.increase( "Loading 3dsmax files" );
            ShooterD3ds.init( ShooterDebug.d3ds );

            ShooterDebug.init.out( "initUi 8" );

            //init hud
            Shooter.game.preloader.increase( "Initing HUD and sound" );
            Shooter.game.hud = new HUD();
            Shooter.game.fps = new LibFPS( Fonts.EFps, ShooterSettings.Colors.EFpsFg.colABGR, ShooterSettings.Colors.EFpsOutline.colABGR, ShooterDebug.glImage );

            //init HUD fx
            HUDFx.init();

            //init sounds and bg sounds
            SoundFg.init();
            SoundBg.init();

            ShooterDebug.init.out( "initUi 9" );

            //switch main state to 'game' and order change to level 1
            Shooter.game.preloader.increase( "Init main menu and launch game" );

            //init main menu
            MainStateMainMenu.init();

            //no level init required!init all level data and game levels
            //ShooterLevelCurrent.init( Startup.STARTUP_LEVEL_MAIN );
            //ShooterGameLevel.init();

            //reset and change to startup main state
            Shooter.game.orderMainStateChangeTo( ShooterSettings.Startup.STARTUP_STATE_AFTER_PRELOADER );
            LevelChange.orderLevelChange( Startup.STARTUP_LEVEL_MAIN, Startup.STARTUP_LEVEL_SECTION, true );

            ShooterDebug.init.out( "initUi 10" );
        }
    }
