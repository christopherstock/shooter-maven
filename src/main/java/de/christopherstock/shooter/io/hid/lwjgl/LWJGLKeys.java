
    package de.christopherstock.shooter.io.hid.lwjgl;

    import de.christopherstock.shooter.g3d.BulletHoleManager;
    import  de.christopherstock.shooter.io.hid.*;
    import  org.lwjgl.input.*;
    import  org.lwjgl.opengl.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.g3d.BulletHole;

    public class LWJGLKeys
    {
        public                  boolean             keyHoldFireMustBeReleased       = false;

        public                  boolean             keyHoldWalkUp                   = false;
        public                  boolean             keyHoldWalkDown                 = false;

        public                  boolean             keyHoldTurnLeft                 = false;
        public                  boolean             keyHoldTurnRight                = false;

        public                  boolean             keyHoldStrafeLeft               = false;
        public                  boolean             keyHoldStrafeRight              = false;

        public                  boolean             keyHoldLookUp                   = false;
        public                  boolean             keyHoldLookDown                 = false;

        public                  boolean             keyHoldAlternate                = false;
        public                  boolean             keyHoldFire                     = false;
        public                  boolean             keyHoldCenterView               = false;
        public                  boolean             keyHoldZoom                     = false;

        public                  KeyControl          crouching                       = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_CROUCHING         );
        public                  KeyControl          gainHealth                      = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_GAIN_HEALTH       );
        public                  KeyControl          damageFx                        = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_DAMAGE_FX         );
        public                  KeyControl          playerAction                    = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_PLAYER_ACTION     );
        public                  KeyControl          reload                          = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_RELOAD            );
        public                  KeyControl          enterKey                        = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_AVATAR_MESSAGE    );
        public                  KeyControl          explosion                       = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_EXPLOSION         );
        public                  KeyControl          toggleMainMenu                  = new KeyControl( ShooterSetting.Performance.DELAY_AFTER_MAIN_MENU_TOGGLE  );

        public void checkKeys()
        {
            boolean displayHasFocus = Display.isActive();

            //view and walk
            this.keyHoldStrafeLeft = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_A          ) );
            this.keyHoldStrafeRight= displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_D          ) );
            this.keyHoldWalkUp     = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_W          ) || Keyboard.isKeyDown( Keyboard.KEY_UP    ) ) );
            this.keyHoldWalkDown   = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_S          ) || Keyboard.isKeyDown( Keyboard.KEY_DOWN  ) ) );
            this.keyHoldTurnLeft   = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_Q          ) || Keyboard.isKeyDown( Keyboard.KEY_LEFT  ) ) );
            this.keyHoldTurnRight  = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_E          ) || Keyboard.isKeyDown( Keyboard.KEY_RIGHT ) ) );
            this.keyHoldLookUp     = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_NUMPAD8    ) || Keyboard.isKeyDown( Keyboard.KEY_8    ) );
            this.keyHoldLookDown   = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_NUMPAD2    ) || Keyboard.isKeyDown( Keyboard.KEY_2    ) );
            this.keyHoldCenterView = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_NUMPAD5    ) || Keyboard.isKeyDown( Keyboard.KEY_5    ) );
            this.keyHoldAlternate  = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_LMENU      ) );
            this.keyHoldFire       = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_LCONTROL   ) || Keyboard.isKeyDown( Keyboard.KEY_RCONTROL ) );
            this.keyHoldZoom       = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_X          ) );

            //actions
            this.playerAction.keyHold = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_SPACE      ) );
            this.crouching.keyHold    = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_Y          ) );
            this.reload.keyHold       = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_R          ) );

            //debug keys
            this.gainHealth.keyHold     = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_H          ) );
            this.damageFx.keyHold       = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_T          ) );
            this.enterKey.keyHold       = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_RETURN     ) );
            this.explosion.keyHold      = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_C          ) );
            this.toggleMainMenu.keyHold = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_ESCAPE     ) );

            if ( Keyboard.isKeyDown( Keyboard.KEY_B ) )
            {
                Shooter.game.engine.bulletHoleManager.clearBulletHoles();
            }
/*
            if ( Keyboard.isKeyDown( Keyboard.KEY_N ) )
            {
                ShooterGameLevel.current().toggleAdrenaline();
            }
*/
            if ( Keyboard.isKeyDown( Keyboard.KEY_M ) )
            {
                Shooter.game.quit();
            }

            //check if the ALT keys are pressed
            if ( Keyboard.isKeyDown( Keyboard.KEY_LMENU ) || Keyboard.isKeyDown( Keyboard.KEY_RMENU ) )
            {
                //ShooterDebug.bugfix.out( "ALT KEY DOWN " + System.currentTimeMillis() );

                //destroy and recreate keyboard to release the alt keys
                try
                {
                    //LibGL.frame.getCanvas().requestFocus();

                    Keyboard.destroy();
                    Keyboard.create();
                }
                catch ( Throwable t )
                {
                    ShooterDebug.error.trace( t );
                }
            }
/*
            if ( Keyboard.isKeyDown( Keyboard.KEY_1 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 0 ], true );
            if ( Keyboard.isKeyDown( Keyboard.KEY_2 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 1 ], true );
            if ( Keyboard.isKeyDown( Keyboard.KEY_3 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 2 ], true );
*/
        }
    }
