
    package de.christopherstock.shooter.base;

    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.io.hid.lwjgl.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.state.*;

    /*******************************************************************************************************************
    *   Represents the game with all components.
    *******************************************************************************************************************/
    public class ShooterGame extends Thread
    {
        /** A flag being set to true if a closing-event on the main form is invoked. */
        private                     boolean                 destroyed                   = false;

        /** The game engine. */
        public                      ShooterEngine           engine                      = null;

        /***************************************************************************************************************
        *   Creates a new game..
        ***************************************************************************************************************/
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
                this.engine.fps.update();

                //draw gl frame
                Shooter.game.engine.frame.display();

                //delay for specified delay time
                try
                {
                    Thread.sleep( ShooterSetting.Performance.THREAD_DELAY );
                }
                catch ( InterruptedException ie )
                {
                    ShooterDebug.error.out( ie );
                }
            }

            //stop all bg sounds ( hangs on mac )
            SoundBg.stopCurrentSound();
        }

        public void draw()
        {
            //clear face queue from last tick
            Shooter.game.engine.glView.clearFaceQueue();

            //draw 3d according to main state
            switch ( this.engine.mainState )
            {
                case EPreloader:
                {
                    this.engine.preloader.draw();
                    break;
                }

                case EMainMenu:
                {
                    Ingame.getSingleton().draw();
                    MainMenu.getSingleton().draw();
                    break;
                }

                case EIngame:
                {
                    Ingame.getSingleton().draw();
                    break;
                }
            }
        }

        public final void quit()
        {
            ShooterDebug.major.out( "Main Form was closed - Shooter is destroyed" );
            this.destroyed = true;
        }

        public final void orderMainStateChangeTo( MainState aFutureMainState )
        {
            this.engine.mainStateToChangeTo = aFutureMainState;
        }

        private void performMainStateChange()
        {
            if (this.engine.mainStateToChangeTo != null )
            {
                //center mouse for new mainstate
                //LWJGLMouse.centerMouse();
                //ShooterDebug.mouse.out( "Centered Mouse" );

                this.engine.mainState = this.engine.mainStateToChangeTo;
                this.engine.mainStateToChangeTo = null;
            }
        }

        private void render()
        {
            // check main state change
            this.performMainStateChange();

            //switch for mainState
            switch ( this.engine.mainState )
            {
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
                    MainMenu.getSingleton().checkMenuKeyEvents();

                    //animate main menu
                    MainMenu.getSingleton().onRun();

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
                    Ingame.getSingleton().checkGameKeyEvents();

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
