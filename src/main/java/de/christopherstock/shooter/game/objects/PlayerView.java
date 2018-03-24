
    package de.christopherstock.shooter.game.objects;

    import de.christopherstock.lib.LibRotation;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.General;
    import  de.christopherstock.shooter.ShooterSettings.PlayerSettings;
    import  de.christopherstock.shooter.io.hid.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   The players field of view.
    *******************************************************************************************************************/
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

        protected LibRotation iRot                        = null;

        /***************************************************************************************************************
        *   Force x-centering till centered.
        ***************************************************************************************************************/
        private                     boolean         iCenterLookX                = false;

        /***************************************************************************************************************
        *   Force x-centering this tick
        ***************************************************************************************************************/
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

        public PlayerView( Player aParent, LibRotation aRot )
        {
            this.iParentPlayer = aParent;
            this.iRot = aRot.copy();
        }

        protected final void handleKeysForView()
        {
            float speedMultiplier = 1.0f;
            if (this.iParentPlayer.iZoom != 0.0f )
            {
                speedMultiplier -= (this.iParentPlayer.iScaleFactor * 0.9f );
            }

            //turn left
            if ( Keys.keyHoldTurnLeft && !Keys.keyHoldAlternate )
            {
                this.iRot.z += SPEED_TURNING_Z * speedMultiplier;
                this.iRot.z = this.iRot.z >= 360.0f ? this.iRot.z - 360.0f : this.iRot.z;
            }

            //turn right
            if ( Keys.keyHoldTurnRight && !Keys.keyHoldAlternate )
            {
                this.iRot.z -= SPEED_TURNING_Z * speedMultiplier;
                this.iRot.z = this.iRot.z < 0.0f ? this.iRot.z + 360.0f : this.iRot.z;
            }

            //check mouse movement
            if ( MouseInput.mouseMovementX != 0 )
            {
                //ShooterDebug.mouse.out( "HANDLE: mouse movement X ["+MouseInput.mouseMovementX+"]" );

                this.iRot.z -= MouseInput.mouseMovementX * speedMultiplier;
                this.iRot.z = this.iRot.z < 0.0f ? this.iRot.z + 360.0f : this.iRot.z;
                this.iRot.z = this.iRot.z >= 360.0f ? this.iRot.z - 360.0f : this.iRot.z;

                MouseInput.mouseMovementX = 0;
            }

            //check x-looking-centering via keys
            if ( Keys.keyHoldLookUp )
            {
                this.iRot.x -= SPEED_LOOKING_X * speedMultiplier;
                this.iRot.x = this.iRot.x < -MAX_LOOKING_X ? -MAX_LOOKING_X : this.iRot.x;
            }
            else if ( Keys.keyHoldLookDown )
            {
                this.iRot.x += SPEED_LOOKING_X * speedMultiplier;
                this.iRot.x = this.iRot.x > MAX_LOOKING_X ? MAX_LOOKING_X : this.iRot.x;
            }
            else if ( Keys.keyHoldWalkUp || Keys.keyHoldWalkDown )
            {
                //center back on walking
                if ( !General.DISABLE_LOOK_CENTERING_X ) this.iCenterLookXthisTick = true;
            }

            //check x-looking-centering via mouse
            if ( MouseInput.mouseMovementY != 0 )
            {
                this.iRot.x -= MouseInput.mouseMovementY * speedMultiplier;
                this.iRot.x = this.iRot.x < -MAX_LOOKING_X ? -MAX_LOOKING_X : this.iRot.x;
                this.iRot.x = this.iRot.x > MAX_LOOKING_X  ? MAX_LOOKING_X  : this.iRot.x;

                MouseInput.mouseMovementY = 0;
            }

            if ( Keys.keyHoldCenterView )
            {
                this.iCenterLookX = true;
            }

            if (this.iParentPlayer.iCrouching )
            {
                //only move if required
                if
                (
                        this.iDepthEye > DEPTH_EYE_CROUCHING
                    || this.iDepthHand > DEPTH_HAND_CROUCHING
                    || this.iDepthTotal > DEPTH_TOTAL_CROUCHING
                )
                {
                    this.iDepthEye -= SPEED_CROUCH_TOGGLE;
                    this.iDepthHand -= SPEED_CROUCH_TOGGLE;
                    this.iDepthTotal = DEPTH_TOTAL_CROUCHING;

                    if (this.iDepthEye < DEPTH_EYE_CROUCHING  ) this.iDepthEye = DEPTH_EYE_CROUCHING;
                    if (this.iDepthHand < DEPTH_HAND_CROUCHING ) this.iDepthHand = DEPTH_HAND_CROUCHING;
                }
            }
            else
            {
                //only move if required
                if
                (
                        this.iDepthEye < DEPTH_EYE_STANDING
                    || this.iDepthHand < DEPTH_HAND_STANDING
                    || this.iDepthTotal < DEPTH_TOTAL_STANDING
                )
                {
                    this.iDepthEye += SPEED_CROUCH_TOGGLE;
                    this.iDepthHand += SPEED_CROUCH_TOGGLE;
                    this.iDepthTotal = DEPTH_TOTAL_STANDING;

                    if (this.iDepthEye > DEPTH_EYE_STANDING  ) this.iDepthEye = DEPTH_EYE_STANDING;
                    if (this.iDepthHand > DEPTH_HAND_STANDING ) this.iDepthHand = DEPTH_HAND_STANDING;

                    //check collision on standing up
                    this.iParentPlayer.getCylinder().iHeight = this.iDepthTotal;
                    if ( !ShooterSettings.General.DISABLE_PLAYER_TO_WALL_COLLISIONS && Level.currentSection().checkCollisionOnWalls(this.iParentPlayer.getCylinder() ) )
                    {
                        this.iDepthEye -= SPEED_CROUCH_TOGGLE;
                        this.iDepthHand -= SPEED_CROUCH_TOGGLE;
                        this.iDepthTotal = DEPTH_TOTAL_CROUCHING;
                        this.iParentPlayer.getCylinder().iHeight = this.iDepthTotal;

                        //crouch the player
                        this.iParentPlayer.iCrouching = true;
                    }
                }
            }
        }

        public final void centerVerticalLook()
        {
            //center rot.x?
            if (this.iCenterLookX || this.iCenterLookXthisTick)
            {
                this.iCenterLookXthisTick = false;
                if (this.iRot.x > 0.0f )
                {
                    this.iRot.x -= SPEED_CENTERING_X;
                    if (this.iRot.x <= 0.0f )
                    {
                        this.iRot.x = 0.0f;
                        this.iCenterLookX = false;
                    }
                }
                else if (this.iRot.x < 0.0f )
                {
                    this.iRot.x += SPEED_CENTERING_X;
                    if (this.iRot.x >= 0.0f )
                    {
                        this.iRot.x = 0.0f;
                        this.iCenterLookX = false;
                    }
                }
            }
        }

        public final void animateDying()
        {
            //only if player is dead
            if (this.iParentPlayer.isDead() )
            {
                switch (this.iDyingState)
                {
                    case EFallingDown:
                    {
                        //shake player's head
                        this.iDieModX -= ROTATION_DYING / 10;
                        this.iDieModY += ROTATION_DYING;
                        this.iDieModZ += ROTATION_DYING;  // * UMath.getRandom( 0, 10 ) / 10;

                        //let player sink
                        this.iDieModTransZ += SPEED_DYING_SINKING;
                        if (this.iDieModTransZ >= this.iDepthEye) this.iDieModTransZ = this.iDepthEye;

                        //start anim each x ticks
                        if ( (this.iDyingAnimation++ ) % 5 == 0 ) HUDFx.launchDamageFX( 6 );

                        //check change to next dying state
                        if (this.iDieModTransZ >= this.iDepthEye)
                        {
                            this.iDyingAnimation = 0;
                            this.iDyingState = DyingState.EBleeding;
                        }
                        break;
                    }

                    case EBleeding:
                    {
                        if ( ++this.iDyingAnimation < 5 )
                        {
                            HUDFx.launchDamageFX( 15 );
                        }
                        else
                        {
                            this.iDyingAnimation = 0;
                            this.iDyingState = DyingState.ELying;
                        }
                        break;
                    }

                    case ELying:
                    {
                        if ( ++this.iDyingAnimation < ShooterSettings.Performance.MAX_TICKS_DAMAGE_FX )
                        {
                        }
                        else
                        {
                            this.iDyingAnimation = 0;
                            this.iDyingState = DyingState.EDying;
                        }
                        break;
                    }

                    case EDying:
                    {
                        if ( ++this.iDyingAnimation < ShooterSettings.Performance.TICKS_DYING_FX )
                        {
                            HUDFx.launchDyingFX();
                        }
                        else
                        {
                            this.iDyingAnimation = 0;
                            this.iDyingState = DyingState.ERelief;
                        }
                        break;
                    }

                    case ERelief:
                    {
                        if ( ++this.iDyingAnimation < ShooterSettings.Performance.TICKS_DEAD_FX )
                        {
                            HUDFx.launchDeadFX();
                        }
                        else
                        {
                            this.iDyingAnimation = 0;
                            this.iDyingState = DyingState.EReincarnation;
                        }
                        break;
                    }

                    case EReincarnation:
                    {
                        if ( ++this.iDyingAnimation < ShooterSettings.Performance.TICKS_REINCARNATION_FX )
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
            return (this.iDyingState != DyingState.EFallingDown );
        }
    }
