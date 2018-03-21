
    package de.christopherstock.shooter.io.hid.lwjgl;

    import  org.lwjgl.input.*;
    import  org.lwjgl.opengl.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.g3d.BulletHole;
    import  de.christopherstock.shooter.io.hid.*;

    public class LWJGLKeys extends Keys
    {
        public static void checkKeys()
        {
            boolean displayHasFocus = Display.isActive();

            //view and walking keys
            keyHoldStrafeLeft                   = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_A          ) );
            keyHoldStrafeRight                  = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_D          ) );
            keyHoldWalkUp                       = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_W          ) || Keyboard.isKeyDown( Keyboard.KEY_UP    ) ) );
            keyHoldWalkDown                     = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_S          ) || Keyboard.isKeyDown( Keyboard.KEY_DOWN  ) ) );
            keyHoldTurnLeft                     = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_Q          ) || Keyboard.isKeyDown( Keyboard.KEY_LEFT  ) ) );
            keyHoldTurnRight                    = displayHasFocus && ( ( Keyboard.isKeyDown( Keyboard.KEY_E          ) || Keyboard.isKeyDown( Keyboard.KEY_RIGHT ) ) );
            keyHoldLookUp                       = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_NUMPAD8    ) || Keyboard.isKeyDown( Keyboard.KEY_8    ) );
            keyHoldLookDown                     = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_NUMPAD2    ) || Keyboard.isKeyDown( Keyboard.KEY_2    ) );
            keyHoldCenterView                   = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_NUMPAD5    ) || Keyboard.isKeyDown( Keyboard.KEY_5    ) );
            keyHoldAlternate                    = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_LMENU      ) );
            keyHoldFire                         = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_LCONTROL   ) || Keyboard.isKeyDown( Keyboard.KEY_RCONTROL ) );
            keyHoldZoom                         = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_X          ) );

            //action keys
            playerAction.iKeyHold               = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_SPACE      ) );
            crouching.iKeyHold                  = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_Y          ) );
            reload.iKeyHold                     = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_R          ) );

            //debug keys
            gainHealth.iKeyHold                 = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_H          ) );
            damageFx.iKeyHold                   = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_T          ) );
            enterKey.iKeyHold              = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_RETURN     ) );
            explosion.iKeyHold                  = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_C          ) );
            toggleMainMenu.iKeyHold             = displayHasFocus && (   Keyboard.isKeyDown( Keyboard.KEY_ESCAPE     ) );

            if ( Keyboard.isKeyDown( Keyboard.KEY_B ) )
            {
                BulletHole.clearBulletHoles();
            }
/*
            if ( Keyboard.isKeyDown( Keyboard.KEY_N ) )
            {
                ShooterGameLevel.current().toggleAdrenaline();
            }
*/
            if ( Keyboard.isKeyDown( Keyboard.KEY_M ) )
            {
                LibGL3D.destroy();
            }

            //check if the ALT keys are pressed
            if ( Keyboard.isKeyDown( Keyboard.KEY_LMENU ) || Keyboard.isKeyDown( Keyboard.KEY_RMENU ) )
            {
                //ShooterDebug.bugfix.out( "ALT KEY DOWN " + System.currentTimeMillis() );

                //destroy and recreate keyboard to release the alt keys
                try
                {
                    //LibGL3D.panel.getNativePanel().requestFocus();

                    Keyboard.destroy();
                    Keyboard.create();
                }
                catch ( Throwable t )
                {
                    ShooterDebug.error.trace( t );
                }
            }

            try
            {
/*
                if ( Keyboard.isKeyDown( Keyboard.KEY_1 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 0 ], true );
                if ( Keyboard.isKeyDown( Keyboard.KEY_2 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 1 ], true );
                if ( Keyboard.isKeyDown( Keyboard.KEY_3 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 2 ], true );
                if ( Keyboard.isKeyDown( Keyboard.KEY_4 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 3 ], true );
                if ( Keyboard.isKeyDown( Keyboard.KEY_5 ) ) ShooterGameLevel.orderLevelChange( ShooterLevelSectionConfig.values()[ 4 ], true );
*/
            }
            catch ( ArrayIndexOutOfBoundsException aioobe ) { /* */ }
/*
            if ( Keyboard.isKeyDown( Keyboard.KEY_ADD ) )
            {
                HUDMessageManager.getSingleton().showMessage( ShooterStrings.HUDMessages.PICKED_UP_SHOTGUN_SHELLS );
            }
*/
        }
    }
