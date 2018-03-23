
    package de.christopherstock.shooter.base;

    import  java.awt.*;
    import  java.awt.image.*;
    import  java.io.*;
    import  javax.imageio.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.LibFPS;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.*;
    import  de.christopherstock.shooter.game.objects.Player;
    import  de.christopherstock.shooter.io.hid.lwjgl.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.state.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   The game engine holding all engine systems.
    *******************************************************************************************************************/
    public class ShooterEngine
    {
        /** Icon image for the frame. */
        private                     BufferedImage           iconImage                   = null;
        /** The bg image for the preloader. */
        private                     BufferedImage           bgImage                     = null;

        /** Preloader. */
        public                      Preloader               preloader                   = null;
        /** Heads up display. */
        public                      HUD                     hud                         = null;
        /** Frames per second counter. */
        public                      LibFPS                  fps                         = null;
        /** Current main state. */
        public                      MainState               mainState                   = MainState.EPreloader;
        /** Main state to change to. */
        public                      MainState               mainStateToChangeTo         = null;

        /***************************************************************************************************************
        *   The global player-instance being controlled by the user.
        ***************************************************************************************************************/
        public                      Player                  player                      = null;

        /***************************************************************************************************************
        *   Inits the ui.
        ***************************************************************************************************************/
        protected void initUi()
        {
            ShooterDebug.init.out( "init UI" );

            this.iconImage = null;
            this.bgImage   = null;

            //load form utils
            try
            {
                this.iconImage = ImageIO.read( LibIO.preStreamJarResource( ShooterSettings.Path.EScreen.iUrl + "icon.png" ) );
                this.bgImage   = ImageIO.read( LibIO.preStreamJarResource( ShooterSettings.Path.EScreen.iUrl + "bg.jpg"   ) );
            }
            catch ( IOException ioe )
            {
                ShooterDebug.error.err( "ERROR! Screen-Graphics could not be loaded!" );
            }

            //set host-os lookAndFeel
            LibGLForm.setLookAndFeel( ShooterDebug.error );
        }

        /***************************************************************************************************************
        *   Inits the preloader.
        ***************************************************************************************************************/
        protected void initPreloader()
        {
            ShooterDebug.init.out( "init Preloader" );

            this.preloader = new Preloader
            (
                new LibGLImage( this.bgImage,   LibGLImage.ImageUsage.EOrtho, ShooterDebug.glImage, true  ),
                new LibGLImage( this.iconImage, LibGLImage.ImageUsage.EOrtho, ShooterDebug.glImage, false )
            );
        }

        /***************************************************************************************************************
        *   Inits the Open GL system.
        ***************************************************************************************************************/
        protected void initGL()
        {
            ShooterDebug.init.out( "init GL" );

            if ( ShooterDebug.ENABLE_FULLSCREEN && !ShooterDebug.DEBUG_MODE )
            {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                ShooterSettings.Form.FORM_WIDTH  = screenSize.width;
                ShooterSettings.Form.FORM_HEIGHT = screenSize.height;
            }

            LibGL3D.init
            (
                ShooterSettings.Form.FORM_WIDTH,
                ShooterSettings.Form.FORM_HEIGHT,
                ShooterSettings.Form.FORM_TITLE,
                Shooter.game,
                Shooter.game,
                this.iconImage,
                this.bgImage,
                ShooterDebug.init
            );
        }

        /***************************************************************************************************************
        *   Inits the rest.
        ***************************************************************************************************************/
        protected void initRest()
        {
            ShooterDebug.init.out( "init REST ???" );

            //center mouse and make it invisible
            LWJGLMouse.init();

            //init fonts
            this.initFonts();

            //load texture images and perform repaint
            this.preloader.increase( "Loading textures" );
            ShooterTexture.loadImages();

            ShooterDebug.init.out( "initUi 6" );

            //assign textures and perform repaint
            this.preloader.increase( "Assigning textures" );
            LibGL3D.view.initTextures( ShooterTexture.getAllTextureImages() );

            ShooterDebug.init.out( "initUi 7" );

            //init 3d studio max objects and perform repaint
            this.preloader.increase( "Loading 3dsmax files" );
            ShooterD3ds.init( ShooterDebug.d3ds );

            ShooterDebug.init.out( "initUi 8" );

            //init hud
            this.preloader.increase( "Initing HUD and sound" );
            this.hud = new HUD();
            this.fps = new LibFPS( Fonts.EFps, ShooterSettings.Colors.EFpsFg.colABGR, ShooterSettings.Colors.EFpsOutline.colABGR, ShooterDebug.glImage );

            //init HUD fx
            HUDFx.init();

            //init sounds and bg sounds
            SoundFg.init();
            SoundBg.init();

            ShooterDebug.init.out( "initUi 9" );

            //switch main state to 'game' and order change to level 1
            this.preloader.increase( "Init main menu and launch game" );

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

        private void initFonts()
        {
            try
            {
                Fonts.EAmmo          = Lib.createFont( Path.EFont.iUrl + "sourceSansPro.otf", 18.0f ); // new Font( "verdana",     Font.BOLD,  12 );
                Fonts.EHealth        = Lib.createFont( Path.EFont.iUrl + "sourceSansPro.otf", 18.0f ); // new Font( "verdana",     Font.BOLD,  12 );
                Fonts.EFps           = Lib.createFont( Path.EFont.iUrl + "sourceSansPro.otf", 18.0f ); // new Font( "verdana",     Font.BOLD,  12 );
                Fonts.EAvatarMessage = Lib.createFont( Path.EFont.iUrl + "sourceSansPro.otf", 18.0f ); // new Font( "verdana",     Font.BOLD,  12 );

                Fonts.EMainMenu      = Lib.createFont( Path.EFont.iUrl + "sourceSansPro.otf", 55.0f );
            }
            catch ( Throwable t )
            {
                ShooterDebug.error.trace( t );
                System.exit( 1 );
            }
        }
    }
