
    package de.christopherstock.shooter.game.objects;

    import de.christopherstock.lib.LibRotation;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.ShooterSetting.PlayerSettings;
    import  de.christopherstock.shooter.io.hid.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   The players field of glView.
    *******************************************************************************************************************/
    public class PlayerView
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

        protected                   float           iDepthTotal                 = PlayerSettings.DEPTH_TOTAL_STANDING;
        protected                   float           iDepthEye                   = PlayerSettings.DEPTH_EYE_STANDING;
        protected                   float           iDepthHand                  = PlayerSettings.DEPTH_HAND_STANDING;
        protected                   float           iHandTransX                 = PlayerSettings.TRANS_X_HAND;
        protected                   float           iHandTransY                 = PlayerSettings.TRANS_Y_HAND;

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
            if ( Shooter.game.engine.keys.keyHoldTurnLeft && !Shooter.game.engine.keys.keyHoldAlternate )
            {
                this.iRot.z += PlayerSettings.SPEED_TURNING_Z * speedMultiplier;
                this.iRot.z = this.iRot.z >= 360.0f ? this.iRot.z - 360.0f : this.iRot.z;
            }

            //turn right
            if ( Shooter.game.engine.keys.keyHoldTurnRight && !Shooter.game.engine.keys.keyHoldAlternate )
            {
                this.iRot.z -= PlayerSettings.SPEED_TURNING_Z * speedMultiplier;
                this.iRot.z = this.iRot.z < 0.0f ? this.iRot.z + 360.0f : this.iRot.z;
            }

            //check mouse movement
            if ( Shooter.game.engine.mouse.movementX != 0 )
            {
                this.iRot.z -= Shooter.game.engine.mouse.movementX * speedMultiplier;
                this.iRot.z = this.iRot.z < 0.0f ? this.iRot.z + 360.0f : this.iRot.z;
                this.iRot.z = this.iRot.z >= 360.0f ? this.iRot.z - 360.0f : this.iRot.z;

                Shooter.game.engine.mouse.movementX = 0;
            }

            //check x-looking-centering via keys
            if ( Shooter.game.engine.keys.keyHoldLookUp )
            {
                this.iRot.x -= PlayerSettings.SPEED_LOOKING_X * speedMultiplier;
                this.iRot.x = this.iRot.x < -PlayerSettings.MAX_LOOKING_X ? -PlayerSettings.MAX_LOOKING_X : this.iRot.x;
            }
            else if ( Shooter.game.engine.keys.keyHoldLookDown )
            {
                this.iRot.x += PlayerSettings.SPEED_LOOKING_X * speedMultiplier;
                this.iRot.x = this.iRot.x > PlayerSettings.MAX_LOOKING_X ? PlayerSettings.MAX_LOOKING_X : this.iRot.x;
            }
            else if ( Shooter.game.engine.keys.keyHoldWalkUp || Shooter.game.engine.keys.keyHoldWalkDown )
            {
                //center back on walking
                if ( !General.DISABLE_LOOK_CENTERING_X ) this.iCenterLookXthisTick = true;
            }

            //check x-looking-centering via mouse
            if ( Shooter.game.engine.mouse.movementY != 0 )
            {
                this.iRot.x -= Shooter.game.engine.mouse.movementY * speedMultiplier;
                this.iRot.x = this.iRot.x < -PlayerSettings.MAX_LOOKING_X ? -PlayerSettings.MAX_LOOKING_X : this.iRot.x;
                this.iRot.x = this.iRot.x > PlayerSettings.MAX_LOOKING_X  ? PlayerSettings.MAX_LOOKING_X  : this.iRot.x;

                Shooter.game.engine.mouse.movementY = 0;
            }

            if ( Shooter.game.engine.keys.keyHoldCenterView )
            {
                this.iCenterLookX = true;
            }

            if (this.iParentPlayer.iCrouching )
            {
                //only move if required
                if
                (
                        this.iDepthEye  > PlayerSettings.DEPTH_EYE_CROUCHING
                    || this.iDepthHand  > PlayerSettings.DEPTH_HAND_CROUCHING
                    || this.iDepthTotal > PlayerSettings.DEPTH_TOTAL_CROUCHING
                )
                {
                    this.iDepthEye   -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.iDepthHand  -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.iDepthTotal =  PlayerSettings.DEPTH_TOTAL_CROUCHING;

                    if (this.iDepthEye <  PlayerSettings.DEPTH_EYE_CROUCHING  ) this.iDepthEye  = PlayerSettings.DEPTH_EYE_CROUCHING;
                    if (this.iDepthHand < PlayerSettings.DEPTH_HAND_CROUCHING ) this.iDepthHand = PlayerSettings.DEPTH_HAND_CROUCHING;
                }
            }
            else
            {
                //only move if required
                if
                (
                        this.iDepthEye <  PlayerSettings.DEPTH_EYE_STANDING
                    || this.iDepthHand <  PlayerSettings.DEPTH_HAND_STANDING
                    || this.iDepthTotal < PlayerSettings.DEPTH_TOTAL_STANDING
                )
                {
                    this.iDepthEye   += PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.iDepthHand  += PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.iDepthTotal =  PlayerSettings.DEPTH_TOTAL_STANDING;

                    if (this.iDepthEye  > PlayerSettings.DEPTH_EYE_STANDING  ) this.iDepthEye  = PlayerSettings.DEPTH_EYE_STANDING;
                    if (this.iDepthHand > PlayerSettings.DEPTH_HAND_STANDING ) this.iDepthHand = PlayerSettings.DEPTH_HAND_STANDING;

                    //check collision on standing up
                    this.iParentPlayer.getCylinder().height = this.iDepthTotal;
                    if ( !ShooterSetting.General.DISABLE_PLAYER_TO_WALL_COLLISIONS && Level.currentSection().checkCollisionOnWalls(this.iParentPlayer.getCylinder() ) )
                    {
                        this.iDepthEye -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                        this.iDepthHand -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                        this.iDepthTotal = PlayerSettings.DEPTH_TOTAL_CROUCHING;
                        this.iParentPlayer.getCylinder().height = this.iDepthTotal;

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
                    this.iRot.x -= PlayerSettings.SPEED_CENTERING_X;
                    if (this.iRot.x <= 0.0f )
                    {
                        this.iRot.x = 0.0f;
                        this.iCenterLookX = false;
                    }
                }
                else if (this.iRot.x < 0.0f )
                {
                    this.iRot.x += PlayerSettings.SPEED_CENTERING_X;
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
                        this.iDieModX -= PlayerSettings.ROTATION_DYING / 10;
                        this.iDieModY += PlayerSettings.ROTATION_DYING;
                        this.iDieModZ += PlayerSettings.ROTATION_DYING;  // * UMath.getRandom( 0, 10 ) / 10;

                        //let player sink
                        this.iDieModTransZ += PlayerSettings.SPEED_DYING_SINKING;
                        if (this.iDieModTransZ >= this.iDepthEye) this.iDieModTransZ = this.iDepthEye;

                        //start anim each x ticks
                        if ( (this.iDyingAnimation++ ) % 5 == 0 ) Shooter.game.engine.hudFx.launchDamageFX( 6 );

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
                            Shooter.game.engine.hudFx.launchDamageFX( 15 );
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
                        if ( ++this.iDyingAnimation < ShooterSetting.Performance.MAX_TICKS_DAMAGE_FX )
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
                        if ( ++this.iDyingAnimation < ShooterSetting.Performance.TICKS_DYING_FX )
                        {
                            Shooter.game.engine.hudFx.launchDyingFX();
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
                        if ( ++this.iDyingAnimation < ShooterSetting.Performance.TICKS_DEAD_FX )
                        {
                            Shooter.game.engine.hudFx.launchDeadFX();
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
                        if ( ++this.iDyingAnimation < ShooterSetting.Performance.TICKS_REINCARNATION_FX )
                        {
                            Shooter.game.engine.hudFx.launchReincarnationFX();
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
