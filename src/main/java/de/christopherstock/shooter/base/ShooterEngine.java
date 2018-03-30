
    package de.christopherstock.shooter.base;

    import  java.awt.image.*;
    import  java.io.*;
    import  javax.imageio.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.LibFPS;
    import  de.christopherstock.lib.ui.LibUI;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.*;
    import  de.christopherstock.shooter.game.objects.Player;
    import  de.christopherstock.shooter.io.hid.lwjgl.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.state.*;
    import  de.christopherstock.shooter.ui.Fonts;
    import  de.christopherstock.shooter.ui.hud.*;
    import  org.lwjgl.opengl.Display;

    /*******************************************************************************************************************
    *   The game engine holds all game systems.
    *******************************************************************************************************************/
    public class ShooterEngine
    {
        /** Preloader. */
        public                      Preloader               preloader                   = null;
        /** The global player-instance being controlled by the user. */
        public                      Player                  player                      = null;
        /** The frame contains a native Java frame and canvas. */
        public                      LibFrame                frame                       = null;
        /** The gl view manages all GL operations. */
        public                      LibGLView               glView                      = null;
        /** Current main state. */
        public                      MainState               mainState                   = MainState.EPreloader;
        /** Main state to change to. */
        public                      MainState               mainStateToChangeTo         = null;
        /** Fonts. */
        public                      Fonts                   fonts                       = null;
        /** Mouse. */
        public                      LWJGLMouse              mouse                       = null;
        /** Keys. */
        public                      LWJGLKeys               keys                        = null;
        /** Textures. */
        public                      ShooterTexture          textures                    = null;
        /** 3D Studio Max system. */
        public                      ShooterD3ds             d3ds                        = null;
        /** Heads up display. */
        public                      HUD                     hud                         = null;
        /** Frames per second counter. */
        public                      LibFPS                  fps                         = null;
        /** Heads up display effects. */
        public                      HUDFx                   hudFx                       = null;
        /** The sound system. */
        public                      Sound                   sound                       = null;
        /** The main menu. */
        public                      MainMenu                mainMenu                    = null;

        /***************************************************************************************************************
        *   Inits the game engine.
        ***************************************************************************************************************/
        protected void init()
        {
            this.initUi();
            this.initPreloader();
            this.initFrame();
            this.initGL();
            this.initFonts();

            this.preloader.increase( 10 );
            this.initMouse();

            this.preloader.increase( 20 );
            this.initKeys();

            this.preloader.increase( 30 );
            this.initTextures();

            this.preloader.increase( 40 );
            this.init3ds();

            this.preloader.increase( 50 );
            this.initHUD();

            this.preloader.increase( 60 );
            this.initFPS();

            this.preloader.increase( 70 );
            this.initHUDFx();

            this.preloader.increase( 80 );
            this.initSound();

            this.preloader.increase( 90 );
            this.initMainMenu();





            this.initRest();
        }

        /***************************************************************************************************************
        *   Inits the ui.
        ***************************************************************************************************************/
        private void initUi()
        {
            ShooterDebug.init.out( "init UI look and feel" );

            LibUI.setLookAndFeel( ShooterDebug.error );
            LibUI.determineFullScreenSize();
        }

        /***************************************************************************************************************
        *   Inits the preloader.
        ***************************************************************************************************************/
        private void initPreloader()
        {
            ShooterDebug.init.out( "init Preloader" );

            this.preloader = new Preloader();
        }

        /***************************************************************************************************************
        *   Inits the native JFrame.
        ***************************************************************************************************************/
        private void initFrame()
        {
            BufferedImage iconImage = null;
            try
            {
                iconImage = ImageIO.read( LibIO.preStreamJarResource( ShooterSetting.Path.EScreen.url + "icon.png" ) );
            }
            catch ( IOException ioe )
            {
                ShooterDebug.error.err( "ERROR! Frame Icon could not be loaded!" );
            }

            this.frame = new LibFrame
            (
                ShooterSetting.Form.FORM_TITLE,
                ShooterSetting.Form.FORM_WIDTH,
                ShooterSetting.Form.FORM_HEIGHT,
                iconImage
            );
            this.frame.init();
        }

        /***************************************************************************************************************
        *   Inits the Open GL system.
        ***************************************************************************************************************/
        private void initGL()
        {
            ShooterDebug.init.out( "init GL" );

            this.glView = new LibGLView
            (
                ShooterDebug.init,
                this.frame,
                ShooterSetting.Form.FORM_WIDTH,
                ShooterSetting.Form.FORM_HEIGHT
            );
            this.glView.init();
        }

        /***************************************************************************************************************
        *   Inits the mouse.
        ***************************************************************************************************************/
        private void initMouse()
        {
            this.mouse = new LWJGLMouse();
            this.mouse.init();
        }

        /***************************************************************************************************************
        *   Inits the keys.
        ***************************************************************************************************************/
        private void initKeys()
        {
            this.keys = new LWJGLKeys();
        }

        /***************************************************************************************************************
        *   Inits the textures.
        ***************************************************************************************************************/
        private void initTextures()
        {
            this.textures = new ShooterTexture();
            this.textures.loadAllImages();
            this.glView.initTextures( this.textures.getAllTextureImages() );
        }

        /***************************************************************************************************************
        *   Inits all 3D Studio Max Model.
        ***************************************************************************************************************/
        private void init3ds()
        {
            this.d3ds = new ShooterD3ds( ShooterDebug.d3ds );
            this.d3ds.init();
        }

        /***************************************************************************************************************
        *   Inits the Heads Up Display.
        ***************************************************************************************************************/
        private void initHUD()
        {
            this.hud = new HUD();
        }

        /***************************************************************************************************************
        *   Inits the Frames Per Second counter.
        ***************************************************************************************************************/
        private void initFPS()
        {
            this.fps = new LibFPS( this.fonts.fps, ShooterSetting.Colors.EFpsFg.colABGR, ShooterSetting.Colors.EFpsOutline.colABGR, ShooterDebug.glImage );
        }

        /***************************************************************************************************************
        *   Inits the HUD effects.
        ***************************************************************************************************************/
        private void initHUDFx()
        {
            this.hudFx = new HUDFx();
            this.hudFx.init();
        }

        /***************************************************************************************************************
        *   Inits the sound system.
        ***************************************************************************************************************/
        private void initSound()
        {
            this.sound = new Sound();
            this.sound.init();
        }

        /***************************************************************************************************************
        *   Inits the main menu.
        ***************************************************************************************************************/
        private void initMainMenu()
        {
            this.mainMenu = new MainMenu();
            this.mainMenu.init();
        }

        /***************************************************************************************************************
        *   Inits the rest.
        *   TODO split!
        ***************************************************************************************************************/
        private void initRest()
        {







            //reset and change to startup main state
            this.preloader.increase( 100 );
            Shooter.game.orderMainStateChangeTo( ShooterSetting.Startup.STARTUP_STATE_AFTER_PRELOADER );
            LevelChange.orderLevelChange( Startup.STARTUP_LEVEL_MAIN, Startup.STARTUP_LEVEL_SECTION, true );
        }

        private void initFonts()
        {
            this.fonts = new Fonts();
            this.fonts.init();
        }

        public void destroy()
        {
            Display.destroy();
            System.exit( 0 );
        }
    }
