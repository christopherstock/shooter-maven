
    package de.christopherstock.shooter.base;

    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.io.hid.lwjgl.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.state.*;
    import  org.lwjgl.opengl.Display;

    /*******************************************************************************************************************
    *   Represents the game with all components.
    *******************************************************************************************************************/
    public class ShooterGame extends Thread
    {
        /** Flags the end of the main thread. */
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
        *   The game's main-thread run-method performing and endless loop of ticks.
        ***************************************************************************************************************/
        @Override
        public void run()
        {
            // opengl needs to be initialized in the same thread
            this.engine.init();

            // tick until main thread is destroyed
            while ( !this.destroyed )
            {
                this.render();

                //update fps
                this.engine.fps.update();

                //invoke callback 3d drawing
                this.draw();

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

            //update native LWJGL Display
            Display.update();
        }

        public final void quit()
        {
            ShooterDebug.major.out( "Main Form was closed - Shooter is destroyed" );
            this.destroyed = true;
        }

        // TODO check if required?
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
                    break;
                }

                case EMainMenu:
                {
                    LWJGLKeys.checkKeys();
                    this.engine.mouse.update();

                    MainMenu.getSingleton().checkMenuKeyEvents();

                    MainMenu.getSingleton().onRun();

                    break;
                }

                case EIngame:
                {
                    LWJGLKeys.checkKeys();
                    this.engine.mouse.update();

                    LevelChange.checkChangeToSection();

                    Ingame.getSingleton().checkGameKeyEvents();

                    if ( Level.currentSection() != null )
                    {
                        Level.currentSection().render();
                    }

                    SoundFg.onRun();

                    break;
                }
            }
        }
    }
