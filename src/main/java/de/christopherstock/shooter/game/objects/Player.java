
    package de.christopherstock.shooter.game.objects;

    import  java.util.*;
    import  javax.vecmath.*;

    import  org.lwjgl.opengl.*;
    import  de.christopherstock.lib.LibAnimation;
    import  de.christopherstock.lib.LibViewSet;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.*;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.game.artefact.ArtefactSet;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.artefact.ArtefactType.GiveTakeAnim;
    import  de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.objects.PlayerView.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.*;
    import  de.christopherstock.shooter.ui.hud.HUD.ChangeAction;

    /*******************************************************************************************************************
    *   Represents the player.
    *******************************************************************************************************************/
    public class Player implements LibGameObject, ShotSource
    {
        /** The player's collision cylinger represents his position and hiscollision body. */
        private                     Cylinder                iCylinder                   = null;

        /** Player's health. 100 is new-born. 0 is dead. */
        private                     int                     iHealth                     = 100;

        /** Disables all gravity checks. */
        private                     boolean                 iDisableGravity             = false;

        private                     PlayerView              iView                       = null;

        public                      AmmoSet                 iAmmoSet                    = null;

        public                      ArtefactSet             iArtefactSet                = null;

        /** Flags player's alive state. */
        private                     boolean                 iDead                       = false;
        /** X-axis-angle on walking. */
        private                     float                   iWalkingAngleY              = 0.0f;
        private                     float                   iWalkingAngleWearponX       = 0.0f;
        private                     float                   iWalkingAngleWearponY       = 0.0f;
        private                     float                   iCurrentSpeedFalling        = PlayerSettings.SPEED_FALLING_MIN;

        /***************************************************************************************************************
        *   Specifies if the player is currently crouching.
        ***************************************************************************************************************/
        public                      boolean                 iCrouching                  = false;

        /***************************************************************************************************************
        *
        ***************************************************************************************************************/
        private                     boolean                 iLaunchShot                 = false;

        public                      boolean                 iAiming                     = false;
        public                      float                   iZoom                       = 0.0f;
        public                      float                   iScaleFactor                = 0.0f;

        private                     boolean                 iFalling                    = false;

        public Player(LibViewSet aStartPosition, boolean aDisableGravity )
        {
            //init and set cylinder
            this.iCylinder = new Cylinder
            (
                this,
                new LibVertex( aStartPosition.pos.x, aStartPosition.pos.y, aStartPosition.pos.z ),
                PlayerSettings.RADIUS_BODY,
                PlayerSettings.DEPTH_TOTAL_STANDING,
                ShooterSetting.Performance.COLLISION_CHECKING_STEPS,
                ShooterDebug.playerCylinder,
                false,
                PlayerSettings.MAX_CLIMBING_UP_Z,
                PlayerSettings.MIN_CLIMBING_UP_Z,
                ShooterSetting.Performance.ELLIPSE_SEGMENTS,
                Material.EHumanFlesh
            );
            this.iView = new PlayerView(     this, aStartPosition.rot );

            //ShooterDebug.bugfix.out( "Reset player glView!" );

            this.iAmmoSet              = new AmmoSet();
            this.iDisableGravity       = aDisableGravity;
            this.iArtefactSet          = new ArtefactSet();
        }

        private void handleKeys()
        {
            //only if alive
            if ( !this.iDead)
            {
                this.handleKeysForMovement();        //handle game keys to specify player's new position
                this.handleKeysForActions();         //handle game keys to invoke actions
                this.iView.handleKeysForView();      //handle game keys to specify player's new glView
            }
        }

        /***************************************************************************************************************
        *   Affects a game-key per game-tick and alters the Player's values.
        ***************************************************************************************************************/
        private void handleKeysForMovement()
        {
            //not if player is falling
            if ( !this.iFalling)
            {
                //forewards
                if ( Shooter.game.engine.keys.keyHoldWalkUp )
                {
                    //change character's position
                    this.iCylinder.getTarget().x = this.iCylinder.getTarget().x - LibMath.sinDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_WALKING;
                    this.iCylinder.getTarget().y = this.iCylinder.getTarget().y - LibMath.cosDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_WALKING;

                    //increase walkY-axis-angles
                    this.iWalkingAngleY        += PlayerSettings.SPEED_WALKING_ANGLE_Y;
                    this.iWalkingAngleWearponX += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_X;
                    this.iWalkingAngleWearponY += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_Y;
                    this.iWalkingAngleY = this.iWalkingAngleY > 360.0f ? this.iWalkingAngleY - 360.0f        : this.iWalkingAngleY;
                    this.iWalkingAngleWearponX = this.iWalkingAngleWearponX > 360.0f ? this.iWalkingAngleWearponX - 360.0f : this.iWalkingAngleWearponX;
                    this.iWalkingAngleWearponY = this.iWalkingAngleWearponY > 360.0f ? this.iWalkingAngleWearponY - 360.0f : this.iWalkingAngleWearponY;
                }

                //backwards
                if ( Shooter.game.engine.keys.keyHoldWalkDown )
                {
                    //change character's position
                    this.iCylinder.getTarget().x = this.iCylinder.getTarget().x + LibMath.sinDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_WALKING;
                    this.iCylinder.getTarget().y = this.iCylinder.getTarget().y + LibMath.cosDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_WALKING;

                    //increase walkY-axis-angles
                    //walkingAngle1 += CHARACTER_WALKING_ANGLE_1_SPEED;
                    this.iWalkingAngleWearponX += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_X;
                    this.iWalkingAngleWearponY += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_Y;
                    //walkingAngle1 = walkingAngle1 > 360.0f ? walkingAngle1 - 360.0f : walkingAngle1;
                    this.iWalkingAngleWearponX = this.iWalkingAngleWearponX > 360.0f ? this.iWalkingAngleWearponX - 360.0f : this.iWalkingAngleWearponX;
                    this.iWalkingAngleWearponY = this.iWalkingAngleWearponY > 360.0f ? this.iWalkingAngleWearponY - 360.0f : this.iWalkingAngleWearponY;

                    //decrease walkY-axis-angle
                    this.iWalkingAngleY -= PlayerSettings.SPEED_WALKING_ANGLE_Y;
                    //walkingAngle2 -= CHARACTER_WALKING_ANGLE_2_SPEED;
                    //walkingAngle3 -= CHARACTER_WALKING_ANGLE_3_SPEED;
                    this.iWalkingAngleY = this.iWalkingAngleY < 0.0f ? this.iWalkingAngleY + 360.0f : this.iWalkingAngleY;
                    //walkingAngle2 = walkingAngle2 < 0.0f ? walkingAngle2 + 360.0f : walkingAngle2;
                    //walkingAngle3 = walkingAngle3 < 0.0f ? walkingAngle3 + 360.0f : walkingAngle3;
                }

                //left
                if ( Shooter.game.engine.keys.keyHoldTurnLeft || Shooter.game.engine.keys.keyHoldStrafeLeft )
                {
                    if ( Shooter.game.engine.keys.keyHoldAlternate || Shooter.game.engine.keys.keyHoldStrafeLeft )
                    {
                        this.iCylinder.getTarget().x = this.iCylinder.getTarget().x - LibMath.cosDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_STRAFING;
                        this.iCylinder.getTarget().y = this.iCylinder.getTarget().y + LibMath.sinDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_STRAFING;
                    }
                }

                //right
                if ( Shooter.game.engine.keys.keyHoldTurnRight || Shooter.game.engine.keys.keyHoldStrafeRight )
                {
                    if ( Shooter.game.engine.keys.keyHoldAlternate  || Shooter.game.engine.keys.keyHoldStrafeRight )
                    {
                        this.iCylinder.getTarget().x = this.iCylinder.getTarget().x + LibMath.cosDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_STRAFING;
                        this.iCylinder.getTarget().y = this.iCylinder.getTarget().y - LibMath.sinDeg(this.iView.iRot.z ) * PlayerSettings.SPEED_STRAFING;
                    }
                }
            }
        }

        private void handleKeysForActions()
        {
            //launch a shot - delay after shot depends on current wearpon
            if ( Shooter.game.engine.keys.keyHoldFire || Shooter.game.engine.mouse.holdButtonLeft)
            {
                //perform nothing if the key must be released
                if ( Shooter.game.engine.keys.keyHoldFireMustBeReleased )
                {
                    //stop launching the shot
                    this.iLaunchShot = false;
                }
                else
                {
                    //launch the shot!
                    this.iLaunchShot = true;

                    //release the key if the gun requires this
                    if (this.iArtefactSet.getArtefactType().getCurrentShotNeedsKeyRelease() )
                    {
                        //force release after number of shots withput release is reached
                        if (this.iArtefactSet.currentArtefact.currentShotsWithoutKeyRelease >= this.iArtefactSet.getArtefactType().shotsTillKeyReleaseRequired)
                        {
                            //key-release required before next shot will be launched :)
                            Shooter.game.engine.keys.keyHoldFireMustBeReleased = true;
                        }
                    }
                }
            }
            else
            {
                //reset wearpon's shots withput key release
                this.iArtefactSet.currentArtefact.currentShotsWithoutKeyRelease = 0;

                //reset
                Shooter.game.engine.keys.keyHoldFireMustBeReleased = false;

                //stop launching the shot
                this.iLaunchShot = false;
            }

            //cycle through artefacts
            if ( Shooter.game.engine.mouse.wheelDown)
            {
                Shooter.game.engine.mouse.wheelDown = false;
                Shooter.game.engine.player.orderWearponOrGadget( ChangeAction.EActionNext );
            }
            else if ( Shooter.game.engine.mouse.wheelUp)
            {
                Shooter.game.engine.mouse.wheelUp = false;
                Shooter.game.engine.player.orderWearponOrGadget( ChangeAction.EActionPrevious );
            }

            //check zooming
            if ( Shooter.game.engine.mouse.holdButtonRight || Shooter.game.engine.keys.keyHoldZoom )
            {
                this.iAiming = true;
                this.iZoom += General.SPEED_ZOOM;
                if (this.iZoom > this.iArtefactSet.getArtefactType().getZoom() ) this.iZoom = this.iArtefactSet.getArtefactType().getZoom();
                this.iScaleFactor = Shooter.game.engine.player.iZoom / General.MAX_ZOOM;
            }
            else
            {
                this.iAiming = false;
                this.iZoom -= General.SPEED_ZOOM;
                if (this.iZoom < 0.0f ) this.iZoom = 0.0f;
                this.iScaleFactor = Shooter.game.engine.player.iZoom / General.MAX_ZOOM;
            }

            //launch crouching
            Shooter.game.engine.keys.crouching.checkLaunchingAction();

            //launch reload
            Shooter.game.engine.keys.reload.checkLaunchingAction();

            //launch an action
            Shooter.game.engine.keys.playerAction.checkLaunchingAction();

            //launch avatar message
            Shooter.game.engine.keys.enterKey.checkLaunchingAction();

            //launch explosion
            Shooter.game.engine.keys.explosion.checkLaunchingAction();

            //launch health fx
            Shooter.game.engine.keys.gainHealth.checkLaunchingAction();

            //launch damage fx
            Shooter.game.engine.keys.damageFx.checkLaunchingAction();
        }

        public final Cylinder getCylinder()
        {
            return this.iCylinder;
        }

        public final void toggleCrouching()
        {
            this.iCrouching = !this.iCrouching;
        }

        private void performFloorChange()
        {
            //no gravity no floor change
            if (this.iDisableGravity) return;

            //browse all faces and set the player to the highest

            //try algo with cylinder first
            Float highestZ = Level.currentSection().getHighestFloor(this.iCylinder, null  );

            //ShooterDebug.bugfix.out( "highestZ: " + highestZ );

            //check if the player is falling - if no highest point or highest point is too far away
            if ( highestZ == null || highestZ < this.iCylinder.getAnchor().z - PlayerSettings.MAX_CLIMBING_UP_Z / 2  )
            {
                if (this.iCurrentSpeedFalling > PlayerSettings.SPEED_FALLING_MAX ) this.iCurrentSpeedFalling = PlayerSettings.SPEED_FALLING_MAX;

                this.iCylinder.getAnchor().z -= this.iCurrentSpeedFalling;
                this.iCurrentSpeedFalling *= PlayerSettings.SPEED_FALLING_MULTIPLIER;
                this.iFalling = true;
            }
            else
            {
                //assign face's z
                this.iCurrentSpeedFalling = PlayerSettings.SPEED_FALLING_MIN;
                this.iCylinder.getAnchor().z = highestZ;
                this.iFalling = false;
            }

            //ShooterDebug.bugfix.out( "z: " + iCylinder.getAnchor().z );

            //check if current level has an invisible z-0-layer
            if ( Level.currentSection().hasInvisibleZLayer() )
            {
                if (this.iCylinder.getAnchor().z < 0.0f ) this.iCylinder.getAnchor().z = 0.0f;
            }
        }
/*
        @Deprecated
        public final void drawDebugLog( Graphics2D g )
        {
            //int y = GLPanel.PANEL_HEIGHT - LibOffset.EBorderHudY;
            int y = OffsetsOrtho.EBorderHudY + 90;

            //HUD.releaseClip( g );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "posX:  " + iCylinder.getAnchor().x,  OffsetsOrtho.EBorderHudX, y - 80 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "posY:  " + iCylinder.getAnchor().y,  OffsetsOrtho.EBorderHudX, y - 70 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "posZ:  " + iCylinder.getAnchor().z,  OffsetsOrtho.EBorderHudX, y - 60 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "rotX:  " + iView.rot.x,              OffsetsOrtho.EBorderHudX, y - 50 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "rotY:  " + iView.rot.y,              OffsetsOrtho.EBorderHudX, y - 40 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "rotZ:  " + iView.rot.z,              OffsetsOrtho.EBorderHudX, y - 30 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "walk1: " + iWalkingAngleY,           OffsetsOrtho.EBorderHudX, y - 20 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "walk2: " + iWalkingAngleWearponX,           OffsetsOrtho.EBorderHudX, y - 10 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "walk3: " + iWalkingAngleWearponY,           OffsetsOrtho.EBorderHudX, y - 0 );
        }
*/
        public final LibShot getShot( float modHorzCC )
        {
            boolean isFirearm = (this.iArtefactSet.getArtefactType().artefactKind instanceof FireArm );
            return
            (
                    isFirearm
                ?   new LibShot
                    (
                        ShotType.ESharpAmmo,
                        LibShot.ShotOrigin.EPlayer,
                        ( (FireArm)(this.iArtefactSet.getArtefactType().artefactKind) ).getCurrentIrregularityHorz(),
                        ( (FireArm)(this.iArtefactSet.getArtefactType().artefactKind) ).getCurrentIrregularityVert(),
                            this.iCylinder.getAnchor().x    + ( LibMath.sinDeg(this.iView.iRot.z + 90.0f ) * this.iView.iHandTransX ) - ( LibMath.sinDeg(this.iView.iRot.z ) * this.iView.iHandTransY ),
                            this.iCylinder.getAnchor().y    + ( LibMath.cosDeg(this.iView.iRot.z + 90.0f ) * this.iView.iHandTransX ) - ( LibMath.cosDeg(this.iView.iRot.z ) * this.iView.iHandTransY ),
                            this.iCylinder.getAnchor().z + this.iView.iDepthHand,
                            this.iView.iRot.z,
                            this.iView.iRot.x,
                            this.iArtefactSet.getArtefactType().getShotRange(),
                            this.iArtefactSet.getArtefactType().artefactKind.getBulletHoleSize(),
                        ShooterDebug.shotAndHit,
                            this.iArtefactSet.getArtefactType().artefactKind.getSliverParticleQuantity(),
                        FxSettings.SLIVER_ANGLE_MOD,
                            this.iArtefactSet.getArtefactType().getDamage(),
                            this.iArtefactSet.getArtefactType().artefactKind.getSliverParticleSize(),
                            this.iArtefactSet.getArtefactType().getBreaksWalls(),
                        ( (FireArm) this.iArtefactSet.getArtefactType().artefactKind).getProjectile(),
                        General.FADE_OUT_FACES_TOTAL_TICKS
                    )
                :   new LibShot
                    (
                        ShotType.ESharpAmmo,
                        LibShot.ShotOrigin.EPlayer,
                        modHorzCC,
                        0.0f,
                            this.iCylinder.getAnchor().x,
                            this.iCylinder.getAnchor().y,
                            this.iCylinder.getAnchor().z + this.iView.iDepthHand,
                            this.iView.iRot.z,
                            this.iView.iRot.x,
                            this.iArtefactSet.getArtefactType().getShotRange(),
                            this.iArtefactSet.getArtefactType().artefactKind.getBulletHoleSize(),
                        ShooterDebug.shotAndHit,
                            this.iArtefactSet.getArtefactType().artefactKind.getSliverParticleQuantity(),
                        FxSettings.SLIVER_ANGLE_MOD,
                            this.iArtefactSet.getArtefactType().getDamage(),
                            this.iArtefactSet.getArtefactType().artefactKind.getSliverParticleSize(),
                            this.iArtefactSet.getArtefactType().getBreaksWalls(),
                        null,
                        General.FADE_OUT_FACES_TOTAL_TICKS
                    )
            );
        }
/*
        public final Crosshair getCrosshair()
        {
            return new Crosshair
            (
                iCylinder.getAnchor().x    + ( LibMath.sinDeg( iView.iRot.z + 90.0f ) * iView.iHandTransX ) - ( LibMath.sinDeg( iView.iRot.z ) * iView.iHandTransY ),
                iCylinder.getAnchor().y    + ( LibMath.cosDeg( iView.iRot.z + 90.0f ) * iView.iHandTransX ) - ( LibMath.cosDeg( iView.iRot.z ) * iView.iHandTransY ),
                iCylinder.getAnchor().z + iView.iDepthHand,
                iView.iRot.z,
                iView.iRot.x
            );
        }
*/
        public final void orderWearponOrGadget( HUD.ChangeAction changeAction )
        {
            //if no animation is active and no gadget is given
            if
            (
                    !Shooter.game.engine.hud.animationActive()
                &&
                    (
                            !(this.iArtefactSet.currentArtefact.artefactType.artefactKind instanceof Gadget )
                        ||  ( (Gadget) this.iArtefactSet.currentArtefact.artefactType.artefactKind).giveTakeAnimState == GiveTakeAnim.ENone
                    )
            )
            {
                Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationHide, changeAction );
            }
        }

        /***************************************************************************************************************
        *
        *   @return     A value from -1.0 to 1.0.
        ***************************************************************************************************************/
        private float getWalkingAngleModifier()
        {
            return ( General.DISABLE_PLAYER_WALKING_ANGLE_Y ? 0.0f : LibMath.sinDeg(this.iWalkingAngleY) );
        }
        public final float getWalkingAngleCarriedModifierX()
        {
            return LibMath.sinDeg(this.iWalkingAngleWearponX);
        }
        public final float getWalkingAngleCarriedModifierY()
        {
            return LibMath.sinDeg(this.iWalkingAngleWearponY);
        }

        public final void hurt( int descent )
        {
            if ( PlayerSettings.INVINCIBILITY ) return;

            //player can only lose energy if he is not dying
            if ( !this.iDead && this.iHealth > 0 )
            {
                //play hurt sound
                SoundFg.EPlayerHit1.playGlobalFx( 20 );

                //substract damage - clip rock bottom
                this.setHealth(this.iHealth - descent );

                //start red screen anim
                //HUDFx.launchDamageFX( descent );

                //start damage fx
                Shooter.game.engine.hudFx.launchDamageFX( 15 );

                //play damage sound delayed
                SoundFg.EPlayerHit1.playGlobalFx( 20 );
            }
        }

        public final void heal( int gainer )
        {
            //player can only be healed if it is not too late
            if ( !this.iDead && this.iHealth < PlayerSettings.MAX_HEALTH )
            {
                //substract damage - clip rock bottom
                this.setHealth(this.iHealth + gainer );

                //start healing screen anim
                Shooter.game.engine.hudFx.launchHealthFX( gainer );
            }
        }

        public final void launchAction( Gadget gadget )
        {
            ShooterDebug.playerAction.out( "launchAction()" );

            //some artefacts are not applied on the level
            if ( gadget != null && gadget.parentKind == ArtefactType.EAdrenaline )
            {
                Level.currentSection().startAdrenaline();
            }
            else
            {
                //launch an action using the actionCylinder!
                Cylinder actionCylinder = this.iCylinder.copy();

                if ( gadget == null )
                {
                    actionCylinder.setRadius( PlayerSettings.RADIUS_ACTION );
                }
                else
                {
                    actionCylinder.setRadius( gadget.parentKind.getShotRange() );
                }

                //launch the action on the level
                Level.currentSection().launchAction( actionCylinder, gadget, this.iView.iRot.z );
            }
        }

        public final LibVertex getAnchor()
        {
            return this.iCylinder.getAnchor();
        }

        public final float getCarriersFaceAngle()
        {
            return 0.0f;
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            return this.getCylinder().launchShot( shot );
        }

        public final void drawStandingCircle()
        {
            this.iCylinder.drawStandingCircle();
        }

        public final int getHealth()
        {
            return this.iHealth;
        }

        private void setHealth(int health )
        {
            this.iHealth = health;

            //clip roof
            if (this.iHealth > PlayerSettings.MAX_HEALTH )
            {
                this.iHealth = PlayerSettings.MAX_HEALTH;
            }

            //clip ceiling - kill if player falls to death
            if (this.iHealth <= 0 )
            {
                this.iHealth = 0;
                this.kill();
            }

            //health changed
            Shooter.game.engine.hud.healthChanged();
        }

        public final LibViewSet getCameraPositionAndRotation()
        {
            //3rd person camera?
            float modX = ( ! General.ENABLE_3RD_PERSON_CAMERA ? 0.0f : LibMath.sinDeg(this.iView.iRot.z ) * 2.0f );
            float modY = ( ! General.ENABLE_3RD_PERSON_CAMERA ? 0.0f : LibMath.cosDeg(this.iView.iRot.z ) * 2.0f );
            float modZ = this.iView.iDepthEye + this.getWalkingAngleModifier() * PlayerSettings.AMP_WALKING_Z;

            float posX = 0.0f;
            float posY = 0.0f;
            float posZ = 0.0f;

            //check if user is dying
            if (this.iDead)
            {
                posX = -this.iCylinder.getAnchor().x;
                posZ = -this.iCylinder.getAnchor().z - PlayerSettings.DEPTH_DEATH - (this.iView.iDepthEye - this.iView.iDieModTransZ );
                posY = -this.iCylinder.getAnchor().y;
            }
            else
            {
                posX = -this.iCylinder.getAnchor().x - modX;
                posZ = -this.iCylinder.getAnchor().z - modZ - this.iView.iDieModTransZ;
                posY = -this.iCylinder.getAnchor().y - modY;
            }

            //zoom
            if (this.iZoom != 0.0f )
            {
                //calc new player pos was hard work but is unused :(
                boolean calcNewPlayerPos_HardWork = false;
                if ( calcNewPlayerPos_HardWork )
                {
                    Point3d iEndPoint3d = LibMathGeometry.getDistantPoint( new Point3d( posX, posY, posZ ), -this.iZoom, this.iView.iRot.x, this.iView.iRot.z );

                    posX = (float)iEndPoint3d.x;
                    posY = (float)iEndPoint3d.y;
                    posZ = (float)iEndPoint3d.z;
                }

                //set glu perspective zoom
                Shooter.game.engine.glView.setNewGluFaceAngle( LibGLView.VIEW_ANGLE - this.iZoom);

                //enter matrix mode modelview
                GL11.glMatrixMode( GL11.GL_MODELVIEW );
            }

            return new LibViewSet
            (
                posX,
                posY,
                posZ,
                    this.iView.iRot.x + this.iView.iDieModX,
                    this.iView.iRot.y + this.iView.iDieModY,
                    this.iView.iRot.z - this.iView.iDieModZ
            );
        }

        /***********************************************************************************
        *   What would this method possibly do to the player?
        ***********************************************************************************/
        private void kill()
        {
            this.iHealth = 0;
            this.iDead = true;
            this.iView.iDyingState   = DyingState.EFallingDown;

            //play hit sound
            SoundFg.EPlayerHit1.playGlobalFx( 40   );
            SoundFg.EPlayerHit1.playGlobalFx( 60   );
            SoundFg.EPlayerHit1.playGlobalFx( 80   );
            SoundFg.EPlayerHit1.playGlobalFx( 100  );
            SoundFg.EPlayerHit1.playGlobalFx( 120  );

            //lower wearpon
            Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationHide, ChangeAction.EActionDie );
        }

        public final boolean isDead()
        {
            return this.iDead;
        }

        public final boolean isDeadAnimationOver()
        {
            return (this.iDead && this.iView.dyingAnimationOver() );
        }

        public final AmmoSet getAmmoSet()
        {
            return this.iAmmoSet;
        }

        private PlayerView getView()
        {
            return this.iView;
        }

        private void moveToNewPosition()
        {
            this.getCylinder().moveToTargetPosition(this.getView().iDepthTotal, General.DISABLE_PLAYER_TO_WALL_COLLISIONS, General.DISABLE_PLAYER_TO_BOT_COLLISIONS );
        }

        public HitPointCarrier getHitPointCarrier()
        {
            return HitPointCarrier.EPlayer;
        }

        public final void render()
        {
            this.getCylinder().setAnchorAsTargetPosition();  //set current position as the target-position
            this.handleKeys();                               //handle all game keys for the player
            this.getView().centerVerticalLook();             //change vertical camera
            this.getView().animateDying();                   //animate dying
            this.moveToNewPosition();                        //move player to new position ( collisions may influence new position )
            this.performFloorChange();                       //move player according to map collision ( floors )

            //handle artefact ( fire, reload, give ) if no HUD anim is running
            if ( !Shooter.game.engine.hud.animationActive() )
            {
                if (this.iArtefactSet.currentArtefact != null )
                {
                    this.iArtefactSet.currentArtefact.handleArtefact( this, this.iLaunchShot, this.iAmmoSet);
                }
            }
        }

        public final boolean getLaunchShot()
        {
            //operate synchronous
            return this.iLaunchShot;
        }

        public final boolean isHealthLow()
        {
            return (this.iHealth <= HUDSettings.PLAYER_LOW_HEALTH_WARNING_PERCENT );
        }

        public final void launchAction(LibCylinder cylinder, Object gadget, float faceAngle )
        {
            //actions have no effect on the player ?
        }
    }
