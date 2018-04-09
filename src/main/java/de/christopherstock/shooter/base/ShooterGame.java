
    package de.christopherstock.shooter.base;

    import  de.christopherstock.shooter.*;
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
            // opengl needs to be initialized and started in the same thread!
            this.engine.init();

            // tick until main thread is destroyed
            while ( !this.destroyed )
            {
                // meassure tick time
                long tickStart = System.currentTimeMillis();

                // render the current scene
                this.render();

                // update fps
                this.engine.hud.fps.update();

                // invoke callback 3d drawing
                this.draw();

                // delay
                long tickTime = (System.currentTimeMillis() - tickStart);
                long tickDelay = 0;
                if ( tickTime < ShooterSetting.Performance.MIN_THREAD_DELAY )
                {
                    tickDelay = ShooterSetting.Performance.MIN_THREAD_DELAY - tickTime;
                }
                else
                {
                    tickDelay = 0;
                }

                // delay for specified delay time
                try
                {
                    Thread.sleep( tickDelay );
                }
                catch ( InterruptedException ie )
                {
                    ShooterDebug.error.out( ie );
                }
            }

            // stop all bg sounds ( hangs on mac )
            this.engine.sound.stopCurrentBgSound();

            this.engine.destroy();
        }

        public void draw()
        {
            //clear face queue from last tick
            this.engine.glView.clearFaceQueue();

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
                    this.engine.ingame.draw();
                    this.engine.mainMenu.draw();
                    break;
                }

                case EIngame:
                {
                    this.engine.ingame.draw();
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

        public final void orderMainStateChange( MainState futureMainState )
        {
            this.engine.mainStateToChangeTo = futureMainState;
        }

        private void performMainStateChange()
        {
            if ( this.engine.mainStateToChangeTo != null )
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
                    this.engine.keys.checkKeys();

                    this.engine.mouse.update();

                    this.engine.mainMenu.checkMenuKeyEvents();
                    this.engine.mainMenu.onRun();

                    break;
                }

                case EIngame:
                {
                    this.engine.keys.checkKeys();
                    this.engine.mouse.update();

                    LevelChange.checkChangeToSection();

                    this.engine.ingame.checkGameKeyEvents();

                    if ( Level.currentSection() != null )
                    {
                        Level.currentSection().render();
                    }

                    this.engine.sound.onRun();

                    break;
                }
            }
        }
    }
