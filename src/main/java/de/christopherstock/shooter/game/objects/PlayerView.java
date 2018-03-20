
    package de.christopherstock.shooter.game.objects;

    import  de.christopherstock.lib.Lib.Rotation;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.General;
    import  de.christopherstock.shooter.ShooterSettings.PlayerSettings;
    import  de.christopherstock.shooter.io.hid.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /**************************************************************************************
    *   The players field of view.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class PlayerView implements PlayerSettings
    {
        public enum DyingState
        {
            EFallingDown,
            EBleeding,
            ELying,
            EDying,
            ERelief,
            EReincarnation,
            ;
        }

        protected                   Rotation        iRot                        = null;

        /**************************************************************************************
        *   Force x-centering till centered.
        **************************************************************************************/
        private                     boolean         iCenterLookX                = false;

        /**************************************************************************************
        *   Force x-centering this tick
        **************************************************************************************/
        private                     boolean         iCenterLookXthisTick        = false;

        private                     Player          iParentPlayer               = null;

        protected                   float           iDepthTotal                 = DEPTH_TOTAL_STANDING;
        protected                   float           iDepthEye                   = DEPTH_EYE_STANDING;
        protected                   float           iDepthHand                  = DEPTH_HAND_STANDING;
        protected                   float           iHandTransX                 = TRANS_X_HAND;
        protected                   float           iHandTransY                 = TRANS_Y_HAND;

        protected                   float           iDieModX                    = 0.0f;
        protected                   float           iDieModY                    = 0.0f;
        protected                   float           iDieModZ                    = 0.0f;
        protected                   float           iDieModTransZ               = 0.0f;

        protected                   DyingState      iDyingState                 = null;

        private                     int             iDyingAnimation             = 0;

        public PlayerView( Player aParent, Rotation aRot )
        {
            iParentPlayer   = aParent;
            iRot            = aRot.copy();
        }

        protected final void handleKeysForView()
        {
            float speedMultiplier = 1.0f;
            if ( iParentPlayer.iZoom != 0.0f )
            {
                speedMultiplier -= ( iParentPlayer.iScaleFactor * 0.9f );
            }

            //turn left
            if ( Keys.keyHoldTurnLeft && !Keys.keyHoldAlternate )
            {
                iRot.z += SPEED_TURNING_Z * speedMultiplier;
                iRot.z = iRot.z >= 360.0f ? iRot.z - 360.0f : iRot.z;
            }

            //turn right
            if ( Keys.keyHoldTurnRight && !Keys.keyHoldAlternate )
            {
                iRot.z -= SPEED_TURNING_Z * speedMultiplier;
                iRot.z = iRot.z < 0.0f ? iRot.z + 360.0f : iRot.z;
            }

            //check mouse movement
            if ( MouseInput.mouseMovementX != 0 )
            {
                //ShooterDebug.mouse.out( "HANDLE: mouse movement X ["+MouseInput.mouseMovementX+"]" );

                iRot.z -= MouseInput.mouseMovementX * speedMultiplier;
                iRot.z = iRot.z < 0.0f ? iRot.z + 360.0f : iRot.z;
                iRot.z = iRot.z >= 360.0f ? iRot.z - 360.0f : iRot.z;

                MouseInput.mouseMovementX = 0;
            }

            //check x-looking-centering via keys
            if ( Keys.keyHoldLookUp )
            {
                iRot.x -= SPEED_LOOKING_X * speedMultiplier;
                iRot.x = iRot.x < -MAX_LOOKING_X ? -MAX_LOOKING_X : iRot.x;
            }
            else if ( Keys.keyHoldLookDown )
            {
                iRot.x += SPEED_LOOKING_X * speedMultiplier;
                iRot.x = iRot.x > MAX_LOOKING_X ? MAX_LOOKING_X : iRot.x;
            }
            else if ( Keys.keyHoldWalkUp || Keys.keyHoldWalkDown )
            {
                //center back on walking
                if ( !General.DISABLE_LOOK_CENTERING_X ) iCenterLookXthisTick = true;
            }

            //check x-looking-centering via mouse
            if ( MouseInput.mouseMovementY != 0 )
            {
                iRot.x -= MouseInput.mouseMovementY * speedMultiplier;
                iRot.x = iRot.x < -MAX_LOOKING_X ? -MAX_LOOKING_X : iRot.x;
                iRot.x = iRot.x > MAX_LOOKING_X  ? MAX_LOOKING_X  : iRot.x;

                MouseInput.mouseMovementY = 0;
            }

            if ( Keys.keyHoldCenterView )
            {
                iCenterLookX = true;
            }

            if ( iParentPlayer.iCrouching )
            {
                //only move if required
                if
                (
                        iDepthEye   > DEPTH_EYE_CROUCHING
                    ||  iDepthHand  > DEPTH_HAND_CROUCHING
                    ||  iDepthTotal > DEPTH_TOTAL_CROUCHING
                )
                {
                    iDepthEye  -= SPEED_CROUCH_TOGGLE;
                    iDepthHand -= SPEED_CROUCH_TOGGLE;
                    iDepthTotal = DEPTH_TOTAL_CROUCHING;

                    if ( iDepthEye  < DEPTH_EYE_CROUCHING  ) iDepthEye  = DEPTH_EYE_CROUCHING;
                    if ( iDepthHand < DEPTH_HAND_CROUCHING ) iDepthHand = DEPTH_HAND_CROUCHING;
                }
            }
            else
            {
                //only move if required
                if
                (
                        iDepthEye   < DEPTH_EYE_STANDING
                    ||  iDepthHand  < DEPTH_HAND_STANDING
                    ||  iDepthTotal < DEPTH_TOTAL_STANDING
                )
                {
                    iDepthEye  += SPEED_CROUCH_TOGGLE;
                    iDepthHand += SPEED_CROUCH_TOGGLE;
                    iDepthTotal = DEPTH_TOTAL_STANDING;

                    if ( iDepthEye  > DEPTH_EYE_STANDING  ) iDepthEye  = DEPTH_EYE_STANDING;
                    if ( iDepthHand > DEPTH_HAND_STANDING ) iDepthHand = DEPTH_HAND_STANDING;

                    //check collision on standing up
                    iParentPlayer.getCylinder().iHeight = iDepthTotal;
                    boolean disableWallCollisions = ShooterSettings.General.DISABLE_PLAYER_TO_WALL_COLLISIONS;
                    if ( !disableWallCollisions && Level.currentSection().checkCollisionOnWalls( iParentPlayer.getCylinder() ) )
                    {
                        iDepthEye  -= SPEED_CROUCH_TOGGLE;
                        iDepthHand -= SPEED_CROUCH_TOGGLE;
                        iDepthTotal = DEPTH_TOTAL_CROUCHING;
                        iParentPlayer.getCylinder().iHeight = iDepthTotal;

                        //crouch the player
                        iParentPlayer.iCrouching = true;
                    }
                }
            }
        }

        public final void centerVerticalLook()
        {
            //center rot.x?
            if ( iCenterLookX || iCenterLookXthisTick )
            {
                iCenterLookXthisTick = false;
                if ( iRot.x > 0.0f )
                {
                    iRot.x -= SPEED_CENTERING_X;
                    if ( iRot.x <= 0.0f )
                    {
                        iRot.x = 0.0f;
                        iCenterLookX = false;
                    }
                }
                else if ( iRot.x < 0.0f )
                {
                    iRot.x += SPEED_CENTERING_X;
                    if ( iRot.x >= 0.0f )
                    {
                        iRot.x = 0.0f;
                        iCenterLookX = false;
                    }
                }
            }
        }

        public final void animateDying()
        {
            //only if player is dead
            if ( iParentPlayer.isDead() )
            {
                switch ( iDyingState )
                {
                    case EFallingDown:
                    {
                        //shake player's head
                        iDieModX -= ROTATION_DYING / 10;
                        iDieModY += ROTATION_DYING;
                        iDieModZ += ROTATION_DYING;  // * UMath.getRandom( 0, 10 ) / 10;

                        //let player sink
                        iDieModTransZ += SPEED_DYING_SINKING;
                        if ( iDieModTransZ >= iDepthEye ) iDieModTransZ = iDepthEye;

                        //start anim each x ticks
                        if ( ( iDyingAnimation++ ) % 5 == 0 ) HUDFx.launchDamageFX( 6 );

                        //check change to next dying state
                        if ( iDieModTransZ >= iDepthEye )
                        {
                            iDyingAnimation = 0;
                            iDyingState     = DyingState.EBleeding;
                        }
                        break;
                    }

                    case EBleeding:
                    {
                        if ( ++iDyingAnimation < 5 )
                        {
                            HUDFx.launchDamageFX( 15 );
                        }
                        else
                        {
                            iDyingAnimation = 0;
                            iDyingState = DyingState.ELying;
                        }
                        break;
                    }

                    case ELying:
                    {
                        if ( ++iDyingAnimation < ShooterSettings.Performance.MAX_TICKS_DAMAGE_FX )
                        {
                        }
                        else
                        {
                            iDyingAnimation = 0;
                            iDyingState = DyingState.EDying;
                        }
                        break;
                    }

                    case EDying:
                    {
                        if ( ++iDyingAnimation < ShooterSettings.Performance.TICKS_DYING_FX )
                        {
                            HUDFx.launchDyingFX();
                        }
                        else
                        {
                            iDyingAnimation = 0;
                            iDyingState = DyingState.ERelief;
                        }
                        break;
                    }

                    case ERelief:
                    {
                        if ( ++iDyingAnimation < ShooterSettings.Performance.TICKS_DEAD_FX )
                        {
                            HUDFx.launchDeadFX();
                        }
                        else
                        {
                            iDyingAnimation = 0;
                            iDyingState = DyingState.EReincarnation;
                        }
                        break;
                    }

                    case EReincarnation:
                    {
                        if ( ++iDyingAnimation < ShooterSettings.Performance.TICKS_REINCARNATION_FX )
                        {
                            HUDFx.launchReincarnationFX();
                        }
                        else
                        {
                            //goto main menu


                        }
                        break;
                    }
                }
            }
        }

        public final boolean dyingAnimationOver()
        {
            return ( iDyingState != DyingState.EFallingDown );
        }
    }
