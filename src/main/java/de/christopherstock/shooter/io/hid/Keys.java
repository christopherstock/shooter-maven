/*  $Id: Keys.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.io.hid;

    import  de.christopherstock.shooter.*;

    /**************************************************************************************
    *   The GL-View.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract class Keys
    {
        public  static          boolean             keyHoldFireMustBeReleased       = false;

        //--- walking ---//
        public  static          boolean             keyHoldWalkUp                   = false;
        public  static          boolean             keyHoldWalkDown                 = false;

        //--- turning ---//
        public  static          boolean             keyHoldTurnLeft                 = false;
        public  static          boolean             keyHoldTurnRight                = false;

        //--- strafing ---//
        public  static          boolean             keyHoldStrafeLeft               = false;
        public  static          boolean             keyHoldStrafeRight              = false;

        //--- look ---//
        public  static          boolean             keyHoldLookUp                   = false;
        public  static          boolean             keyHoldLookDown                 = false;

        //--- modificators ---//
        public  static          boolean             keyHoldAlternate                = false;
        public  static          boolean             keyHoldFire                     = false;
        public  static          boolean             keyHoldCenterView               = false;
        public  static          boolean             keyHoldZoom                     = false;

        //--- actions ---//
        public  static          KeyControl          crouching                       = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_CROUCHING         );
        public  static          KeyControl          gainHealth                      = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_GAIN_HEALTH       );
        public  static          KeyControl          damageFx                        = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_DAMAGE_FX         );
        public  static          KeyControl          playerAction                    = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_PLAYER_ACTION     );
        public  static          KeyControl          reload                          = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_RELOAD            );
        public  static          KeyControl          enterKey                   = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_AVATAR_MESSAGE    );
        public  static          KeyControl          explosion                       = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_EXPLOSION         );
        public  static          KeyControl          toggleMainMenu                  = new KeyControl( ShooterSettings.Performance.DELAY_AFTER_MAIN_MENU_TOGGLE  );
    }
