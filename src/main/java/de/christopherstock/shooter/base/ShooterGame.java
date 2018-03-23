
    package de.christopherstock.shooter.base;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLFrame.*;
    import  de.christopherstock.lib.gl.LibGLPanel.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.io.hid.lwjgl.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.state.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   Represents the game with all components.
    *******************************************************************************************************************/
    public class ShooterGame extends Thread implements GLDrawCallback, GLCallbackForm
    {
        /** The game engine. */
        private                     ShooterEngine           engine                      = null;

        /** A flag being set to true if a closing-event on the main form is invoked. */
        private                     boolean                 destroyed                   = false;


        /** The preloader. */
        public                      Preloader               preloader                   = null;
        /** The heads up display. */
        public                      HUD                     hud                         = null;
        /** The FPS counter. */
        public                      LibFPS                  fps                         = null;
        /** The application's current main state. */
        private                     MainState               mainState                   = MainState.EPreloader;
        /** The application's main state to enter the next tick. */
        private                     MainState               mainStateToChangeTo         = null;



        public ShooterGame()
        {
            this.engine = new ShooterEngine();
        }

        /***************************************************************************************************************
        *   Inits the game engine.
        ***************************************************************************************************************/
        private void init()
        {
            this.engine.initUi();
            this.engine.initPreloader();
            this.engine.initGL();
            this.engine.initRest();
        }

        /***************************************************************************************************************
        *   The game's main-thread run-method performing and endless loop of ticks.
        ***************************************************************************************************************/
        @Override
        public void run()
        {
            // opengl needs to be initialized in the same thread
            this.init();

            // tick until main thread is destroyed
            while ( !this.destroyed )
            {
                this.render();

                //update fps
                this.fps.update();

                //draw gl panel
                LibGL3D.panel.display();

                //delay for specified delay time
                Lib.delay( ShooterSettings.Performance.THREAD_DELAY );
            }

            //stop all bg sounds ( hangs on mac )
            SoundBg.stopCurrentSound();
        }

        public final void draw2D()
        {
            //draw loading screen
            switch (this.mainState)
            {
                case EPreloader:
                {
                    //draw 2d
                    this.preloader.draw2D();
                    break;
                }

                case EIntroLogo:
                {
                    //draw 2d
                    MainStateIntroLogo.getSingleton().draw2D();
                    break;
                }

                case EMainMenu:
                {
                    //draw 2d
                    MainStateMainMenu.getSingleton().draw2D();
                    break;
                }

                case EIngame:
                {
                    //draw 2d
                    MainStateIngame.getSingleton().draw2D();
                    break;
                }
            }
        }

        public final void draw3D()
        {
            //clear face queue from last tick
            LibGL3D.view.clearFaceQueue();

            //draw 3d according to main state
            switch (this.mainState)
            {
                case EPreloader:
                {
                    //clear gl
                    this.preloader.draw3D();
                    break;
                }

                case EIntroLogo:
                {
                    //draw 3d
                    MainStateIntroLogo.getSingleton().draw3D();
                    break;
                }

                case EIngame:
                case EMainMenu:
                {
                    //draw 3d
                    MainStateIngame.getSingleton().draw3D();
                    break;
                }
            }
        }

        public final void onFormDestroyed()
        {
            ShooterDebug.major.out( "Main Form was closed - Shooter is destroyed" );
            this.destroyed = true;
        }

        public final void orderMainStateChangeTo( MainState aFutureMainState )
        {
            this.mainStateToChangeTo = aFutureMainState;
        }

        private void performMainStateChange()
        {
            if (this.mainStateToChangeTo != null )
            {
                //center mouse for new mainstate
                //LWJGLMouse.centerMouse();
                //ShooterDebug.mouse.out( "Centered Mouse" );

                this.mainState = this.mainStateToChangeTo;
                this.mainStateToChangeTo = null;
            }
        }

        private void render()
        {
            // check main state change
            this.performMainStateChange();

            //switch for mainState
            switch (this.mainState)
            {
                case EIntroLogo:
                {
                    //check keys and mouse (really?)

                    LWJGLKeys.checkKeys();
                    LWJGLMouse.checkMouse();

                    //check menu key events
                    MainStateIntroLogo.getSingleton().checkIntroLogoEvents();

                    //animate the intro logo
                    MainStateIntroLogo.getSingleton().onRun();

                    break;
                }

                case EPreloader:
                {
                    //nothing to animate ?
                    break;
                }

                case EMainMenu:
                {
                    //check keys and mouse for lwjgl
                    LWJGLKeys.checkKeys();
                    LWJGLMouse.checkMouse();

                    //check menu key events
                    MainStateMainMenu.getSingleton().checkMenuKeyEvents();

                    //animate main menu
                    MainStateMainMenu.getSingleton().onRun();

                    break;
                }

                case EIngame:
                {
                    //check keys and mouse for lwjgl
                    LWJGLKeys.checkKeys();
                    LWJGLMouse.checkMouse();

                    //perform synchronized level change
                    LevelChange.checkChangeToSection();

                    //check game key events
                    MainStateIngame.getSingleton().checkGameKeyEvents();

                    //animate player and level ( only if a level is assigned )
                    if ( Level.currentSection() != null )
                    {
                        //animate level
                        Level.currentSection().render();
                    }

                    //maintain sounds
                    SoundFg.onRun();

                    break;
                }
            }
        }
    }
