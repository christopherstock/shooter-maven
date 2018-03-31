
    package de.christopherstock.shooter.game.objects;

    import de.christopherstock.lib.LibRotation;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.ShooterSetting.PlayerSettings;
    import  de.christopherstock.shooter.level.*;

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

        protected                   LibRotation     rot                         = null;

        /***************************************************************************************************************
        *   Force x-centering till centered.
        ***************************************************************************************************************/
        private                     boolean         centerLookX                 = false;

        /***************************************************************************************************************
        *   Force x-centering this tick
        ***************************************************************************************************************/
        private                     boolean         centerLookXthisTick         = false;

        private                     Player          parentPlayer                = null;

        protected                   float           depthTotal                  = PlayerSettings.DEPTH_TOTAL_STANDING;
        protected                   float           depthEye                    = PlayerSettings.DEPTH_EYE_STANDING;
        protected                   float           depthHand                   = PlayerSettings.DEPTH_HAND_STANDING;
        protected                   float           handTransX                  = PlayerSettings.TRANS_X_HAND;
        protected                   float           handTransY                  = PlayerSettings.TRANS_Y_HAND;

        protected                   float           dieModX                     = 0.0f;
        protected                   float           dieModY                     = 0.0f;
        protected                   float           dieModZ                     = 0.0f;
        protected                   float           dieModTransZ                = 0.0f;

        protected                   DyingState      dyingState                  = null;

        private                     int             dyingAnimation              = 0;

        public PlayerView( Player parent, LibRotation rot )
        {
            this.parentPlayer = parent;
            this.rot = rot.copy();
        }

        protected final void handleKeysForView()
        {
            float speedMultiplier = 1.0f;
            if (this.parentPlayer.zoom != 0.0f )
            {
                speedMultiplier -= (this.parentPlayer.scaleFactor * 0.9f );
            }

            //turn left
            if ( Shooter.game.engine.keys.keyHoldTurnLeft && !Shooter.game.engine.keys.keyHoldAlternate )
            {
                this.rot.z += PlayerSettings.SPEED_TURNING_Z * speedMultiplier;
                this.rot.z = this.rot.z >= 360.0f ? this.rot.z - 360.0f : this.rot.z;
            }

            //turn right
            if ( Shooter.game.engine.keys.keyHoldTurnRight && !Shooter.game.engine.keys.keyHoldAlternate )
            {
                this.rot.z -= PlayerSettings.SPEED_TURNING_Z * speedMultiplier;
                this.rot.z = this.rot.z < 0.0f ? this.rot.z + 360.0f : this.rot.z;
            }

            //check mouse movement
            if ( Shooter.game.engine.mouse.movementX != 0 )
            {
                this.rot.z -= Shooter.game.engine.mouse.movementX * speedMultiplier;
                this.rot.z = this.rot.z < 0.0f ? this.rot.z + 360.0f : this.rot.z;
                this.rot.z = this.rot.z >= 360.0f ? this.rot.z - 360.0f : this.rot.z;

                Shooter.game.engine.mouse.movementX = 0;
            }

            //check x-looking-centering via keys
            if ( Shooter.game.engine.keys.keyHoldLookUp )
            {
                this.rot.x -= PlayerSettings.SPEED_LOOKING_X * speedMultiplier;
                this.rot.x = this.rot.x < -PlayerSettings.MAX_LOOKING_X ? -PlayerSettings.MAX_LOOKING_X : this.rot.x;
            }
            else if ( Shooter.game.engine.keys.keyHoldLookDown )
            {
                this.rot.x += PlayerSettings.SPEED_LOOKING_X * speedMultiplier;
                this.rot.x = this.rot.x > PlayerSettings.MAX_LOOKING_X ? PlayerSettings.MAX_LOOKING_X : this.rot.x;
            }
            else if ( Shooter.game.engine.keys.keyHoldWalkUp || Shooter.game.engine.keys.keyHoldWalkDown )
            {
                //center back on walking
                if ( !General.DISABLE_LOOK_CENTERING_X ) this.centerLookXthisTick = true;
            }

            //check x-looking-centering via mouse
            if ( Shooter.game.engine.mouse.movementY != 0 )
            {
                this.rot.x -= Shooter.game.engine.mouse.movementY * speedMultiplier;
                this.rot.x = this.rot.x < -PlayerSettings.MAX_LOOKING_X ? -PlayerSettings.MAX_LOOKING_X : this.rot.x;
                this.rot.x = this.rot.x > PlayerSettings.MAX_LOOKING_X  ? PlayerSettings.MAX_LOOKING_X  : this.rot.x;

                Shooter.game.engine.mouse.movementY = 0;
            }

            if ( Shooter.game.engine.keys.keyHoldCenterView )
            {
                this.centerLookX = true;
            }

            if (this.parentPlayer.crouching)
            {
                //only move if required
                if
                (
                        this.depthEye > PlayerSettings.DEPTH_EYE_CROUCHING
                    || this.depthHand > PlayerSettings.DEPTH_HAND_CROUCHING
                    || this.depthTotal > PlayerSettings.DEPTH_TOTAL_CROUCHING
                )
                {
                    this.depthEye -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.depthHand -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.depthTotal =  PlayerSettings.DEPTH_TOTAL_CROUCHING;

                    if (this.depthEye <  PlayerSettings.DEPTH_EYE_CROUCHING  ) this.depthEye = PlayerSettings.DEPTH_EYE_CROUCHING;
                    if (this.depthHand < PlayerSettings.DEPTH_HAND_CROUCHING ) this.depthHand = PlayerSettings.DEPTH_HAND_CROUCHING;
                }
            }
            else
            {
                //only move if required
                if
                (
                        this.depthEye <  PlayerSettings.DEPTH_EYE_STANDING
                    || this.depthHand <  PlayerSettings.DEPTH_HAND_STANDING
                    || this.depthTotal < PlayerSettings.DEPTH_TOTAL_STANDING
                )
                {
                    this.depthEye += PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.depthHand += PlayerSettings.SPEED_CROUCH_TOGGLE;
                    this.depthTotal =  PlayerSettings.DEPTH_TOTAL_STANDING;

                    if (this.depthEye > PlayerSettings.DEPTH_EYE_STANDING  ) this.depthEye = PlayerSettings.DEPTH_EYE_STANDING;
                    if (this.depthHand > PlayerSettings.DEPTH_HAND_STANDING ) this.depthHand = PlayerSettings.DEPTH_HAND_STANDING;

                    //check collision on standing up
                    this.parentPlayer.getCylinder().height = this.depthTotal;
                    if ( !ShooterSetting.General.DISABLE_PLAYER_TO_WALL_COLLISIONS && Level.currentSection().checkCollisionOnWalls(this.parentPlayer.getCylinder() ) )
                    {
                        this.depthEye -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                        this.depthHand -= PlayerSettings.SPEED_CROUCH_TOGGLE;
                        this.depthTotal = PlayerSettings.DEPTH_TOTAL_CROUCHING;
                        this.parentPlayer.getCylinder().height = this.depthTotal;

                        //crouch the player
                        this.parentPlayer.crouching = true;
                    }
                }
            }
        }

        public final void centerVerticalLook()
        {
            //center rot.x?
            if (this.centerLookX || this.centerLookXthisTick)
            {
                this.centerLookXthisTick = false;
                if (this.rot.x > 0.0f )
                {
                    this.rot.x -= PlayerSettings.SPEED_CENTERING_X;
                    if (this.rot.x <= 0.0f )
                    {
                        this.rot.x = 0.0f;
                        this.centerLookX = false;
                    }
                }
                else if (this.rot.x < 0.0f )
                {
                    this.rot.x += PlayerSettings.SPEED_CENTERING_X;
                    if (this.rot.x >= 0.0f )
                    {
                        this.rot.x = 0.0f;
                        this.centerLookX = false;
                    }
                }
            }
        }

        public final void animateDying()
        {
            //only if player is dead
            if (this.parentPlayer.isDead() )
            {
                switch (this.dyingState)
                {
                    case EFallingDown:
                    {
                        //shake player's head
                        this.dieModX -= PlayerSettings.ROTATION_DYING / 10;
                        this.dieModY += PlayerSettings.ROTATION_DYING;
                        this.dieModZ += PlayerSettings.ROTATION_DYING;  // * UMath.getRandom( 0, 10 ) / 10;

                        //let player sink
                        this.dieModTransZ += PlayerSettings.SPEED_DYING_SINKING;
                        if (this.dieModTransZ >= this.depthEye) this.dieModTransZ = this.depthEye;

                        //start anim each x ticks
                        if ( (this.dyingAnimation++ ) % 5 == 0 ) Shooter.game.engine.hudFx.launchDamageFX( 6 );

                        //check change to next dying state
                        if (this.dieModTransZ >= this.depthEye)
                        {
                            this.dyingAnimation = 0;
                            this.dyingState = DyingState.EBleeding;
                        }
                        break;
                    }

                    case EBleeding:
                    {
                        if ( ++this.dyingAnimation < 5 )
                        {
                            Shooter.game.engine.hudFx.launchDamageFX( 15 );
                        }
                        else
                        {
                            this.dyingAnimation = 0;
                            this.dyingState = DyingState.ELying;
                        }
                        break;
                    }

                    case ELying:
                    {
                        if ( ++this.dyingAnimation < ShooterSetting.Performance.MAX_TICKS_DAMAGE_FX )
                        {
                        }
                        else
                        {
                            this.dyingAnimation = 0;
                            this.dyingState = DyingState.EDying;
                        }
                        break;
                    }

                    case EDying:
                    {
                        if ( ++this.dyingAnimation < ShooterSetting.Performance.TICKS_DYING_FX )
                        {
                            Shooter.game.engine.hudFx.launchDyingFX();
                        }
                        else
                        {
                            this.dyingAnimation = 0;
                            this.dyingState = DyingState.ERelief;
                        }
                        break;
                    }

                    case ERelief:
                    {
                        if ( ++this.dyingAnimation < ShooterSetting.Performance.TICKS_DEAD_FX )
                        {
                            Shooter.game.engine.hudFx.launchDeadFX();
                        }
                        else
                        {
                            this.dyingAnimation = 0;
                            this.dyingState = DyingState.EReincarnation;
                        }
                        break;
                    }

                    case EReincarnation:
                    {
                        if ( ++this.dyingAnimation < ShooterSetting.Performance.TICKS_REINCARNATION_FX )
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
            return (this.dyingState != DyingState.EFallingDown );
        }
    }
