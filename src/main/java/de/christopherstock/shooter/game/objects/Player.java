
    package de.christopherstock.shooter.game.objects;

    import  java.util.*;
    import  javax.vecmath.*;
    import  org.lwjgl.opengl.*;
    import  de.christopherstock.lib.Lib.LibAnimation;
    import  de.christopherstock.lib.Lib.ViewSet;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.*;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.game.artefact.ArtefactSet;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.artefact.ArtefactType.GiveTakeAnim;
    import  de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.objects.PlayerView.*;
    import  de.christopherstock.shooter.io.hid.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.*;
    import  de.christopherstock.shooter.ui.hud.HUD.ChangeAction;

    /*******************************************************************************************************************
    *   Represents the player.
    *******************************************************************************************************************/
    public class Player implements LibGameObject, PlayerSettings, ShotSpender
    {
        public static interface HealthChangeListener
        {
            public abstract void healthChanged();
        }

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
        private                     float                   iCurrentSpeedFalling        = SPEED_FALLING_MIN;

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

        private                     HealthChangeListener    iHealthChangeCallback       = null;

        private                     boolean                 iFalling                    = false;

        public Player( ViewSet aStartPosition, boolean aDisableGravity )
        {
            //init and set cylinder
            iCylinder               = new Cylinder( this, new LibVertex( aStartPosition.pos.x, aStartPosition.pos.y, aStartPosition.pos.z ), RADIUS_BODY, DEPTH_TOTAL_STANDING, ShooterSettings.Performance.COLLISION_CHECKING_STEPS, ShooterDebug.playerCylinder, false, PlayerSettings.MAX_CLIMBING_UP_Z, PlayerSettings.MIN_CLIMBING_UP_Z, ShooterSettings.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh );
            iView                   = new PlayerView(     this, aStartPosition.rot );

            //ShooterDebug.bugfix.out( "Reset player view!" );

            iAmmoSet                = new AmmoSet();
            iHealthChangeCallback   = Shooter.mainThread.iHUD;
            iDisableGravity         = aDisableGravity;
            iArtefactSet            = new ArtefactSet();
        }

        private final void handleKeys()
        {
            //only if alive
            if ( !iDead )
            {
                handleKeysForMovement();        //handle game keys to specify player's new position
                handleKeysForActions();         //handle game keys to invoke actions
                iView.handleKeysForView();      //handle game keys to specify player's new view
            }
        }

        /***************************************************************************************************************
        *   Affects a game-key per game-tick and alters the Player's values.
        ***************************************************************************************************************/
        private void handleKeysForMovement()
        {
/*
            Keys.ticksKeyLeftHold     = ( Keys.keyLeftHold     ? Keys.ticksKeyLeftHold     + 1 : 0 );
            Keys.ticksKeyRightHold    = ( Keys.keyRightHold    ? Keys.ticksKeyRightHold    + 1 : 0 );
            Keys.ticksKeyPageUpHold   = ( Keys.keyPageUpHold   ? Keys.ticksKeyPageUpHold   + 1 : 0 );
            Keys.ticksKeyPageDownHold = ( Keys.keyPageDownHold ? Keys.ticksKeyPageDownHold + 1 : 0 );
*/
            //not if player is falling
            if ( !iFalling )
            {
                //forewards
                if ( Keys.keyHoldWalkUp )
                {
                    //change character's position
                    iCylinder.getTarget().x = iCylinder.getTarget().x - LibMath.sinDeg( iView.iRot.z ) * SPEED_WALKING;
                    iCylinder.getTarget().y = iCylinder.getTarget().y - LibMath.cosDeg( iView.iRot.z ) * SPEED_WALKING;

                    //increase walkY-axis-angles
                    iWalkingAngleY          += SPEED_WALKING_ANGLE_Y;
                    iWalkingAngleWearponX   += SPEED_WALKING_ANGLE_WEARPON_X;
                    iWalkingAngleWearponY   += SPEED_WALKING_ANGLE_WEARPON_Y;
                    iWalkingAngleY          = iWalkingAngleY        > 360.0f ? iWalkingAngleY - 360.0f        : iWalkingAngleY;
                    iWalkingAngleWearponX   = iWalkingAngleWearponX > 360.0f ? iWalkingAngleWearponX - 360.0f : iWalkingAngleWearponX;
                    iWalkingAngleWearponY   = iWalkingAngleWearponY > 360.0f ? iWalkingAngleWearponY - 360.0f : iWalkingAngleWearponY;
                }

                //backwards
                if ( Keys.keyHoldWalkDown )
                {
                    //change character's position
                    iCylinder.getTarget().x = iCylinder.getTarget().x + LibMath.sinDeg( iView.iRot.z ) * SPEED_WALKING;
                    iCylinder.getTarget().y = iCylinder.getTarget().y + LibMath.cosDeg( iView.iRot.z ) * SPEED_WALKING;

                    //increase walkY-axis-angles
                    //walkingAngle1 += CHARACTER_WALKING_ANGLE_1_SPEED;
                    iWalkingAngleWearponX += SPEED_WALKING_ANGLE_WEARPON_X;
                    iWalkingAngleWearponY += SPEED_WALKING_ANGLE_WEARPON_Y;
                    //walkingAngle1 = walkingAngle1 > 360.0f ? walkingAngle1 - 360.0f : walkingAngle1;
                    iWalkingAngleWearponX = iWalkingAngleWearponX > 360.0f ? iWalkingAngleWearponX - 360.0f : iWalkingAngleWearponX;
                    iWalkingAngleWearponY = iWalkingAngleWearponY > 360.0f ? iWalkingAngleWearponY - 360.0f : iWalkingAngleWearponY;

                    //decrease walkY-axis-angle
                    iWalkingAngleY -= SPEED_WALKING_ANGLE_Y;
                    //walkingAngle2 -= CHARACTER_WALKING_ANGLE_2_SPEED;
                    //walkingAngle3 -= CHARACTER_WALKING_ANGLE_3_SPEED;
                    iWalkingAngleY = iWalkingAngleY < 0.0f ? iWalkingAngleY + 360.0f : iWalkingAngleY;
                    //walkingAngle2 = walkingAngle2 < 0.0f ? walkingAngle2 + 360.0f : walkingAngle2;
                    //walkingAngle3 = walkingAngle3 < 0.0f ? walkingAngle3 + 360.0f : walkingAngle3;
                }

                //left
                if ( Keys.keyHoldTurnLeft || Keys.keyHoldStrafeLeft )
                {
                    if ( Keys.keyHoldAlternate || Keys.keyHoldStrafeLeft )
                    {
                        iCylinder.getTarget().x = iCylinder.getTarget().x - LibMath.cosDeg( iView.iRot.z ) * SPEED_STRAFING;
                        iCylinder.getTarget().y = iCylinder.getTarget().y + LibMath.sinDeg( iView.iRot.z ) * SPEED_STRAFING;
                    }
                }

                //right
                if ( Keys.keyHoldTurnRight || Keys.keyHoldStrafeRight )
                {
                    if ( Keys.keyHoldAlternate  || Keys.keyHoldStrafeRight )
                    {
                        iCylinder.getTarget().x = iCylinder.getTarget().x + LibMath.cosDeg( iView.iRot.z ) * SPEED_STRAFING;
                        iCylinder.getTarget().y = iCylinder.getTarget().y - LibMath.sinDeg( iView.iRot.z ) * SPEED_STRAFING;
                    }
                }
            }
        }

        private void handleKeysForActions()
        {
            //launch a shot - delay after shot depends on current wearpon
            if ( Keys.keyHoldFire || MouseInput.mouseHoldFire )
            {
                //perform nothing if the key must be released
                if ( Keys.keyHoldFireMustBeReleased )
                {
                    //stop launching the shot
                    iLaunchShot = false;
                }
                else
                {
                    //launch the shot!
                    iLaunchShot = true;

                    //release the key if the gun requires this
                    if ( iArtefactSet.getArtefactType().getCurrentShotNeedsKeyRelease() )
                    {
                        //force release after number of shots withput release is reached
                        if ( iArtefactSet.iCurrentArtefact.iCurrentShotsWithoutKeyRelease >= iArtefactSet.getArtefactType().iShotsTillKeyReleaseRequired )
                        {
                            //key-release required before next shot will be launched :)
                            Keys.keyHoldFireMustBeReleased = true;
                        }
                    }
                }
            }
            else
            {
                //reset wearpon's shots withput key release
                iArtefactSet.iCurrentArtefact.iCurrentShotsWithoutKeyRelease = 0;

                //reset
                Keys.keyHoldFireMustBeReleased = false;

                //stop launching the shot
                iLaunchShot = false;
            }

            //cycle through artefacts
            if ( MouseInput.mouseWheelDown )
            {
                MouseInput.mouseWheelDown = false;
                Level.currentPlayer().orderWearponOrGadget( ChangeAction.EActionNext );
            }
            else if ( MouseInput.mouseWheelUp )
            {
                MouseInput.mouseWheelUp = false;
                Level.currentPlayer().orderWearponOrGadget( ChangeAction.EActionPrevious );
            }

            //check zooming
            if ( MouseInput.mouseHoldZoom || Keys.keyHoldZoom )
            {
                iAiming = true;
                iZoom += General.SPEED_ZOOM;
                if ( iZoom > iArtefactSet.getArtefactType().getZoom() ) iZoom = iArtefactSet.getArtefactType().getZoom();
                iScaleFactor = Level.currentPlayer().iZoom / General.MAX_ZOOM;
            }
            else
            {
                iAiming = false;
                iZoom -= General.SPEED_ZOOM;
                if ( iZoom < 0.0f ) iZoom = 0.0f;
                iScaleFactor = Level.currentPlayer().iZoom / General.MAX_ZOOM;
            }

            //launch crouching
            Keys.crouching.checkLaunchingAction();

            //launch reload
            Keys.reload.checkLaunchingAction();

            //launch an action
            Keys.playerAction.checkLaunchingAction();

            //launch avatar message
            Keys.enterKey.checkLaunchingAction();

            //launch explosion
            Keys.explosion.checkLaunchingAction();

            //launch health fx
            Keys.gainHealth.checkLaunchingAction();

            //launch damage fx
            Keys.damageFx.checkLaunchingAction();
        }

        public final Cylinder getCylinder()
        {
            return iCylinder;
        }

        public final void toggleCrouching()
        {
            iCrouching = !iCrouching;
        }

        private final void performFloorChange()
        {
            //no gravity no floor change
            if ( iDisableGravity ) return;

            //browse all faces and set the player to the highest

            //try algo with cylinder first
            Float highestZ = Level.currentSection().getHighestFloor( iCylinder, null  );

            //ShooterDebug.bugfix.out( "highestZ: " + highestZ );

            //check if the player is falling - if no highest point or highest point is too far away
            if ( highestZ == null || highestZ.floatValue() < iCylinder.getAnchor().z - PlayerSettings.MAX_CLIMBING_UP_Z / 2  )
            {
                if ( iCurrentSpeedFalling > SPEED_FALLING_MAX ) iCurrentSpeedFalling = SPEED_FALLING_MAX;

                iCylinder.getAnchor().z -= iCurrentSpeedFalling;
                iCurrentSpeedFalling    *= SPEED_FALLING_MULTIPLIER;
                iFalling                = true;
            }
            else
            {
                //assign face's z
                iCurrentSpeedFalling    = SPEED_FALLING_MIN;
                iCylinder.getAnchor().z = highestZ.floatValue();
                iFalling                = false;
            }

            //ShooterDebug.bugfix.out( "z: " + iCylinder.getAnchor().z );

            //check if current level has an invisible z-0-layer
            if ( Level.currentSection().hasInvisibleZLayer() )
            {
                if ( iCylinder.getAnchor().z < 0.0f ) iCylinder.getAnchor().z = 0.0f;
            }
        }
/*
        @Deprecated
        public final void drawDebugLog( Graphics2D g )
        {
            //int y = GLPanel.PANEL_HEIGHT - Offset.EBorderHudY;
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
            boolean isFirearm = ( iArtefactSet.getArtefactType().iArtefactKind instanceof FireArm );
            return
            (
                    isFirearm
                ?   new LibShot
                    (
                        ShotType.ESharpAmmo,
                        LibShot.ShotOrigin.EPlayer,
                        ( (FireArm)( iArtefactSet.getArtefactType().iArtefactKind ) ).getCurrentIrregularityHorz(),
                        ( (FireArm)( iArtefactSet.getArtefactType().iArtefactKind ) ).getCurrentIrregularityVert(),
                        iCylinder.getAnchor().x    + ( LibMath.sinDeg( iView.iRot.z + 90.0f ) * iView.iHandTransX ) - ( LibMath.sinDeg( iView.iRot.z ) * iView.iHandTransY ),
                        iCylinder.getAnchor().y    + ( LibMath.cosDeg( iView.iRot.z + 90.0f ) * iView.iHandTransX ) - ( LibMath.cosDeg( iView.iRot.z ) * iView.iHandTransY ),
                        iCylinder.getAnchor().z + iView.iDepthHand,
                        iView.iRot.z,
                        iView.iRot.x,
                        iArtefactSet.getArtefactType().getShotRange(),
                        iArtefactSet.getArtefactType().iArtefactKind.getBulletHoleSize(),
                        ShooterDebug.shotAndHit,
                        iArtefactSet.getArtefactType().iArtefactKind.getSliverParticleQuantity(),
                        FxSettings.SLIVER_ANGLE_MOD,
                        iArtefactSet.getArtefactType().getDamage(),
                        iArtefactSet.getArtefactType().iArtefactKind.getSliverParticleSize(),
                        iArtefactSet.getArtefactType().getBreaksWalls(),
                        ( (FireArm)iArtefactSet.getArtefactType().iArtefactKind ).getProjectile(),
                        General.FADE_OUT_FACES_TOTAL_TICKS
                    )
                :   new LibShot
                    (
                        ShotType.ESharpAmmo,
                        LibShot.ShotOrigin.EPlayer,
                        modHorzCC,
                        0.0f,
                        iCylinder.getAnchor().x,
                        iCylinder.getAnchor().y,
                        iCylinder.getAnchor().z + iView.iDepthHand,
                        iView.iRot.z,
                        iView.iRot.x,
                        iArtefactSet.getArtefactType().getShotRange(),
                        iArtefactSet.getArtefactType().iArtefactKind.getBulletHoleSize(),
                        ShooterDebug.shotAndHit,
                        iArtefactSet.getArtefactType().iArtefactKind.getSliverParticleQuantity(),
                        FxSettings.SLIVER_ANGLE_MOD,
                        iArtefactSet.getArtefactType().getDamage(),
                        iArtefactSet.getArtefactType().iArtefactKind.getSliverParticleSize(),
                        iArtefactSet.getArtefactType().getBreaksWalls(),
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
                    !Shooter.mainThread.iHUD.animationActive()
                &&
                    (
                            !( iArtefactSet.iCurrentArtefact.iArtefactType.iArtefactKind instanceof Gadget )
                        ||  ( (Gadget)iArtefactSet.iCurrentArtefact.iArtefactType.iArtefactKind ).iGiveTakeAnimState == GiveTakeAnim.ENone
                    )
            )
            {
                Shooter.mainThread.iHUD.startHandAnimation( LibAnimation.EAnimationHide, changeAction );
            }
        }

        /***************************************************************************************************************
        *
        *   @return     A value from -1.0 to 1.0.
        ***************************************************************************************************************/
        public final float getWalkingAngleModifier()
        {
            boolean disable = General.DISABLE_PLAYER_WALKING_ANGLE_Y;
            return ( disable ? 0.0f : LibMath.sinDeg( iWalkingAngleY ) );
        }
        public final float getWalkingAngleCarriedModifierX()
        {
            return LibMath.sinDeg( iWalkingAngleWearponX );
        }
        public final float getWalkingAngleCarriedModifierY()
        {
            return LibMath.sinDeg( iWalkingAngleWearponY );
        }

        public final void hurt( int descent )
        {
            boolean invincible = PlayerSettings.INVINCIBILITY;
            if ( invincible ) return;

            //player can only lose energy if he is not dying
            if ( !iDead && iHealth > 0 )
            {
                //play hurt sound
                SoundFg.EPlayerHit1.playGlobalFx( 20 );

                //substract damage - clip rock bottom
                setHealth( iHealth - descent );

                //start red screen anim
                //HUDFx.launchDamageFX( descent );

                //start damage fx
                HUDFx.launchDamageFX( 15 );

                //play damage sound delayed
                SoundFg.EPlayerHit1.playGlobalFx( 20 );
            }
        }

        public final void heal( int gainer )
        {
            //player can only be healed if it is not too late
            if ( !iDead && iHealth < MAX_HEALTH )
            {
                //substract damage - clip rock bottom
                setHealth( iHealth + gainer );

                //start healing screen anim
                HUDFx.launchHealthFX( gainer );
            }
        }

        public final void launchAction( Gadget gadget )
        {
            ShooterDebug.playerAction.out( "launchAction()" );

            //some artefacts are not applied on the level
            if ( gadget != null && gadget.iParentKind == ArtefactType.EAdrenaline )
            {
                Level.currentSection().startAdrenaline();
            }
            else
            {
                //launch an action using the actionCylinder!
                Cylinder actionCylinder = iCylinder.copy();

                if ( gadget == null )
                {
                    actionCylinder.setRadius( PlayerSettings.RADIUS_ACTION );
                }
                else
                {
                    actionCylinder.setRadius( gadget.iParentKind.getShotRange() );
                }

                //launch the action on the level
                Level.currentSection().launchAction( actionCylinder, gadget, iView.iRot.z );
            }
        }

        public final LibVertex getAnchor()
        {
            return iCylinder.getAnchor();
        }

        public final float getCarriersFaceAngle()
        {
            return 0.0f;
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            return getCylinder().launchShot( shot );
        }

        public final void drawStandingCircle()
        {
            iCylinder.drawStandingCircle();
        }

        public final int getHealth()
        {
            return iHealth;
        }

        private final void setHealth( int health )
        {
            iHealth = health;

            //clip roof
            if ( iHealth > MAX_HEALTH )
            {
                iHealth = MAX_HEALTH;
            }

            //clip ceiling - kill if player falls to death
            if ( iHealth <= 0 )
            {
                iHealth = 0;
                kill();
            }

            //health changed
            iHealthChangeCallback.healthChanged();
        }

        public final ViewSet getCameraPositionAndRotation()
        {
            //3rd person camera?
            float modX = ( ! General.ENABLE_3RD_PERSON_CAMERA ? 0.0f : LibMath.sinDeg( iView.iRot.z ) * 2.0f );
            float modY = ( ! General.ENABLE_3RD_PERSON_CAMERA ? 0.0f : LibMath.cosDeg( iView.iRot.z ) * 2.0f );
            float modZ = iView.iDepthEye + getWalkingAngleModifier() * PlayerSettings.AMP_WALKING_Z;

            float posX = 0.0f;
            float posY = 0.0f;
            float posZ = 0.0f;

            //check if user is dying
            if ( iDead )
            {
                posX = -iCylinder.getAnchor().x;
                posZ = -iCylinder.getAnchor().z - PlayerSettings.DEPTH_DEATH - ( iView.iDepthEye - iView.iDieModTransZ );
                posY = -iCylinder.getAnchor().y;
            }
            else
            {
                posX = -iCylinder.getAnchor().x - modX;
                posZ = -iCylinder.getAnchor().z - modZ - iView.iDieModTransZ;
                posY = -iCylinder.getAnchor().y - modY;
            }

            //zoom
            if ( iZoom != 0.0f )
            {
                //calc new player pos was hard work but is unused :(
                boolean calcNewPlayerPos_HardWork = false;
                if ( calcNewPlayerPos_HardWork )
                {
                    Point3d iEndPoint3d = LibMathGeometry.getDistantPoint( new Point3d( posX, posY, posZ ), -iZoom, iView.iRot.x, iView.iRot.z );

                    posX = (float)iEndPoint3d.x;
                    posY = (float)iEndPoint3d.y;
                    posZ = (float)iEndPoint3d.z;
                }

                //set glu perspective zoom
                LibGL3D.view.setNewGluFaceAngle( LibGLView.VIEW_ANGLE - iZoom );

                //enter matrix mode modelview
                GL11.glMatrixMode( GL11.GL_MODELVIEW );
            }

            return new ViewSet
            (
                posX,
                posY,
                posZ,
                iView.iRot.x + iView.iDieModX,
                iView.iRot.y + iView.iDieModY,
                iView.iRot.z - iView.iDieModZ
            );
        }

        /***********************************************************************************
        *   What would this method possibly do to the player?
        ***********************************************************************************/
        public final void kill()
        {
            iHealth             = 0;
            iDead               = true;
            iView.iDyingState   = DyingState.EFallingDown;

            //play hit sound
            SoundFg.EPlayerHit1.playGlobalFx( 40   );
            SoundFg.EPlayerHit1.playGlobalFx( 60   );
            SoundFg.EPlayerHit1.playGlobalFx( 80   );
            SoundFg.EPlayerHit1.playGlobalFx( 100  );
            SoundFg.EPlayerHit1.playGlobalFx( 120  );

            //lower wearpon
            Shooter.mainThread.iHUD.startHandAnimation( LibAnimation.EAnimationHide, ChangeAction.EActionDie );
        }

        public final boolean isDead()
        {
            return iDead;
        }

        public final boolean isDeadAnimationOver()
        {
            return ( iDead && iView.dyingAnimationOver() );
        }

        public final AmmoSet getAmmoSet()
        {
            return iAmmoSet;
        }

        public final PlayerView getView()
        {
            return iView;
        }

        public final void moveToNewPosition()
        {
            getCylinder().moveToTargetPosition( getView().iDepthTotal, General.DISABLE_PLAYER_TO_WALL_COLLISIONS, General.DISABLE_PLAYER_TO_BOT_COLLISIONS );
        }

        public HitPointCarrier getHitPointCarrier()
        {
            return HitPointCarrier.EPlayer;
        }

        public final void onRun()
        {
            getCylinder().setAnchorAsTargetPosition();  //set current position as the target-position
            handleKeys();                               //handle all game keys for the player
            getView().centerVerticalLook();             //change vertical camera
            getView().animateDying();                   //animate dying
            moveToNewPosition();                        //move player to new position ( collisions may influence new position )
            performFloorChange();                       //move player according to map collision ( floors )

            //handle artefact ( fire, reload, give ) if no HUD anim is running
            if ( !Shooter.mainThread.iHUD.animationActive() )
            {
                if ( iArtefactSet.iCurrentArtefact != null )
                {
                    iArtefactSet.iCurrentArtefact.handleArtefact( this, iLaunchShot, iAmmoSet );
                }
            }
        }

        public final boolean getLaunchShot()
        {
            //operate synchronous
            return iLaunchShot;
        }

        public final boolean isHealthLow()
        {
            return ( iHealth <= HUDSettings.PLAYER_LOW_HEALTH_WARNING_PERCENT );
        }

        public final void launchAction( LibCylinder aCylinder, Object gadget, float faceAngle )
        {
            //actions have no effect on the player ?
        }
    }
