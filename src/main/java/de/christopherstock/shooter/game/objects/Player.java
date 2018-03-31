
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
        private                     Cylinder                cylinder                    = null;

        /** Player's health. 100 is new-born. 0 is dead. */
        private                     int                     health                      = 100;

        /** Disables all gravity checks. */
        private                     boolean                 disableGravity              = false;

        private                     PlayerView              view                        = null;

        public                      AmmoSet                 ammoSet                     = null;

        public                      ArtefactSet             artefactSet                 = null;

        /** Flags player's alive state. */
        private                     boolean                 dead                        = false;
        /** X-axis-angle on walking. */
        private                     float                   walkingAngleY               = 0.0f;
        private                     float                   walkingAngleWearponX        = 0.0f;
        private                     float                   walkingAngleWearponY        = 0.0f;
        private                     float                   currentSpeedFalling         = PlayerSettings.SPEED_FALLING_MIN;

        /***************************************************************************************************************
        *   Specifies if the player is currently crouching.
        ***************************************************************************************************************/
        public                      boolean                 crouching                   = false;

        /***************************************************************************************************************
        *
        ***************************************************************************************************************/
        private                     boolean                 launchShot                  = false;

        public                      boolean                 aiming                      = false;
        public                      float                   zoom                        = 0.0f;
        public                      float                   scaleFactor                 = 0.0f;

        private                     boolean                 falling                     = false;

        public Player( LibViewSet startPosition, boolean aDisableGravity )
        {
            //init and set cylinder
            this.cylinder = new Cylinder
            (
                this,
                new LibVertex( startPosition.pos.x, startPosition.pos.y, startPosition.pos.z ),
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
            this.view = new PlayerView(     this, startPosition.rot );

            //ShooterDebug.bugfix.out( "Reset player glView!" );

            this.ammoSet = new AmmoSet();
            this.disableGravity = aDisableGravity;
            this.artefactSet = new ArtefactSet();
        }

        private void handleKeys()
        {
            //only if alive
            if ( !this.dead)
            {
                this.handleKeysForMovement();        //handle game keys to specify player's new position
                this.handleKeysForActions();         //handle game keys to invoke actions
                this.view.handleKeysForView();      //handle game keys to specify player's new glView
            }
        }

        /***************************************************************************************************************
        *   Affects a game-key per game-tick and alters the Player's values.
        ***************************************************************************************************************/
        private void handleKeysForMovement()
        {
            //not if player is falling
            if ( !this.falling)
            {
                //forewards
                if ( Shooter.game.engine.keys.keyHoldWalkUp )
                {
                    //change character's position
                    this.cylinder.getTarget().x = this.cylinder.getTarget().x - LibMath.sinDeg(this.view.rot.z ) * PlayerSettings.SPEED_WALKING;
                    this.cylinder.getTarget().y = this.cylinder.getTarget().y - LibMath.cosDeg(this.view.rot.z ) * PlayerSettings.SPEED_WALKING;

                    //increase walkY-axis-angles
                    this.walkingAngleY += PlayerSettings.SPEED_WALKING_ANGLE_Y;
                    this.walkingAngleWearponX += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_X;
                    this.walkingAngleWearponY += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_Y;
                    this.walkingAngleY = this.walkingAngleY > 360.0f ? this.walkingAngleY - 360.0f        : this.walkingAngleY;
                    this.walkingAngleWearponX = this.walkingAngleWearponX > 360.0f ? this.walkingAngleWearponX - 360.0f : this.walkingAngleWearponX;
                    this.walkingAngleWearponY = this.walkingAngleWearponY > 360.0f ? this.walkingAngleWearponY - 360.0f : this.walkingAngleWearponY;
                }

                //backwards
                if ( Shooter.game.engine.keys.keyHoldWalkDown )
                {
                    //change character's position
                    this.cylinder.getTarget().x = this.cylinder.getTarget().x + LibMath.sinDeg(this.view.rot.z ) * PlayerSettings.SPEED_WALKING;
                    this.cylinder.getTarget().y = this.cylinder.getTarget().y + LibMath.cosDeg(this.view.rot.z ) * PlayerSettings.SPEED_WALKING;

                    //increase walkY-axis-angles
                    //walkingAngle1 += CHARACTER_WALKING_ANGLE_1_SPEED;
                    this.walkingAngleWearponX += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_X;
                    this.walkingAngleWearponY += PlayerSettings.SPEED_WALKING_ANGLE_WEARPON_Y;
                    //walkingAngle1 = walkingAngle1 > 360.0f ? walkingAngle1 - 360.0f : walkingAngle1;
                    this.walkingAngleWearponX = this.walkingAngleWearponX > 360.0f ? this.walkingAngleWearponX - 360.0f : this.walkingAngleWearponX;
                    this.walkingAngleWearponY = this.walkingAngleWearponY > 360.0f ? this.walkingAngleWearponY - 360.0f : this.walkingAngleWearponY;

                    //decrease walkY-axis-angle
                    this.walkingAngleY -= PlayerSettings.SPEED_WALKING_ANGLE_Y;
                    //walkingAngle2 -= CHARACTER_WALKING_ANGLE_2_SPEED;
                    //walkingAngle3 -= CHARACTER_WALKING_ANGLE_3_SPEED;
                    this.walkingAngleY = this.walkingAngleY < 0.0f ? this.walkingAngleY + 360.0f : this.walkingAngleY;
                    //walkingAngle2 = walkingAngle2 < 0.0f ? walkingAngle2 + 360.0f : walkingAngle2;
                    //walkingAngle3 = walkingAngle3 < 0.0f ? walkingAngle3 + 360.0f : walkingAngle3;
                }

                //left
                if ( Shooter.game.engine.keys.keyHoldTurnLeft || Shooter.game.engine.keys.keyHoldStrafeLeft )
                {
                    if ( Shooter.game.engine.keys.keyHoldAlternate || Shooter.game.engine.keys.keyHoldStrafeLeft )
                    {
                        this.cylinder.getTarget().x = this.cylinder.getTarget().x - LibMath.cosDeg(this.view.rot.z ) * PlayerSettings.SPEED_STRAFING;
                        this.cylinder.getTarget().y = this.cylinder.getTarget().y + LibMath.sinDeg(this.view.rot.z ) * PlayerSettings.SPEED_STRAFING;
                    }
                }

                //right
                if ( Shooter.game.engine.keys.keyHoldTurnRight || Shooter.game.engine.keys.keyHoldStrafeRight )
                {
                    if ( Shooter.game.engine.keys.keyHoldAlternate  || Shooter.game.engine.keys.keyHoldStrafeRight )
                    {
                        this.cylinder.getTarget().x = this.cylinder.getTarget().x + LibMath.cosDeg(this.view.rot.z ) * PlayerSettings.SPEED_STRAFING;
                        this.cylinder.getTarget().y = this.cylinder.getTarget().y - LibMath.sinDeg(this.view.rot.z ) * PlayerSettings.SPEED_STRAFING;
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
                    this.launchShot = false;
                }
                else
                {
                    //launch the shot!
                    this.launchShot = true;

                    //release the key if the gun requires this
                    if (this.artefactSet.getArtefactType().getCurrentShotNeedsKeyRelease() )
                    {
                        //force release after number of shots withput release is reached
                        if (this.artefactSet.currentArtefact.currentShotsWithoutKeyRelease >= this.artefactSet.getArtefactType().shotsTillKeyReleaseRequired)
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
                this.artefactSet.currentArtefact.currentShotsWithoutKeyRelease = 0;

                //reset
                Shooter.game.engine.keys.keyHoldFireMustBeReleased = false;

                //stop launching the shot
                this.launchShot = false;
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
                this.aiming = true;
                this.zoom += General.SPEED_ZOOM;
                if (this.zoom > this.artefactSet.getArtefactType().getZoom() ) this.zoom = this.artefactSet.getArtefactType().getZoom();
                this.scaleFactor = Shooter.game.engine.player.zoom / General.MAX_ZOOM;
            }
            else
            {
                this.aiming = false;
                this.zoom -= General.SPEED_ZOOM;
                if (this.zoom < 0.0f ) this.zoom = 0.0f;
                this.scaleFactor = Shooter.game.engine.player.zoom / General.MAX_ZOOM;
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
            return this.cylinder;
        }

        public final void toggleCrouching()
        {
            this.crouching = !this.crouching;
        }

        private void performFloorChange()
        {
            //no gravity no floor change
            if (this.disableGravity) return;

            //browse all faces and set the player to the highest

            //try algo with cylinder first
            Float highestZ = Level.currentSection().getHighestFloor(this.cylinder, null  );

            //ShooterDebug.bugfix.out( "highestZ: " + highestZ );

            //check if the player is falling - if no highest point or highest point is too far away
            if ( highestZ == null || highestZ < this.cylinder.getAnchor().z - PlayerSettings.MAX_CLIMBING_UP_Z / 2  )
            {
                if (this.currentSpeedFalling > PlayerSettings.SPEED_FALLING_MAX ) this.currentSpeedFalling = PlayerSettings.SPEED_FALLING_MAX;

                this.cylinder.getAnchor().z -= this.currentSpeedFalling;
                this.currentSpeedFalling *= PlayerSettings.SPEED_FALLING_MULTIPLIER;
                this.falling = true;
            }
            else
            {
                //assign face's z
                this.currentSpeedFalling = PlayerSettings.SPEED_FALLING_MIN;
                this.cylinder.getAnchor().z = highestZ;
                this.falling = false;
            }

            //ShooterDebug.bugfix.out( "z: " + cylinder.getAnchor().z );

            //check if current level has an invisible z-0-layer
            if ( Level.currentSection().hasInvisibleZLayer() )
            {
                if (this.cylinder.getAnchor().z < 0.0f ) this.cylinder.getAnchor().z = 0.0f;
            }
        }
/*
        @Deprecated
        public final void drawDebugLog( Graphics2D g )
        {
            //int y = GLPanel.PANEL_HEIGHT - LibOffset.EBorderHudY;
            int y = OffsetsOrtho.EBorderHudY + 90;

            //HUD.releaseClip( g );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "posX:  " + cylinder.getAnchor().x,  OffsetsOrtho.EBorderHudX, y - 80 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "posY:  " + cylinder.getAnchor().y,  OffsetsOrtho.EBorderHudX, y - 70 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "posZ:  " + cylinder.getAnchor().z,  OffsetsOrtho.EBorderHudX, y - 60 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "rotX:  " + view.rot.x,              OffsetsOrtho.EBorderHudX, y - 50 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "rotY:  " + view.rot.y,              OffsetsOrtho.EBorderHudX, y - 40 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "rotZ:  " + view.rot.z,              OffsetsOrtho.EBorderHudX, y - 30 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "walk1: " + walkingAngleY,           OffsetsOrtho.EBorderHudX, y - 20 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "walk2: " + walkingAngleWearponX,           OffsetsOrtho.EBorderHudX, y - 10 );
            LibStrings.drawString( g, LibColors.EWhite.colARGB, LibColors.EBlack.colARGB, null, Fonts.EDebug, LibAnchor.EAnchorLeftBottom, "walk3: " + walkingAngleWearponY,           OffsetsOrtho.EBorderHudX, y - 0 );
        }
*/
        public final LibShot getShot( float modHorzCC )
        {
            boolean isFirearm = (this.artefactSet.getArtefactType().artefactKind instanceof FireArm );
            return
            (
                    isFirearm
                ?   new LibShot
                    (
                        ShotType.ESharpAmmo,
                        LibShot.ShotOrigin.EPlayer,
                        ( (FireArm)(this.artefactSet.getArtefactType().artefactKind) ).getCurrentIrregularityHorz(),
                        ( (FireArm)(this.artefactSet.getArtefactType().artefactKind) ).getCurrentIrregularityVert(),
                            this.cylinder.getAnchor().x    + ( LibMath.sinDeg(this.view.rot.z + 90.0f ) * this.view.handTransX) - ( LibMath.sinDeg(this.view.rot.z ) * this.view.handTransY),
                            this.cylinder.getAnchor().y    + ( LibMath.cosDeg(this.view.rot.z + 90.0f ) * this.view.handTransX) - ( LibMath.cosDeg(this.view.rot.z ) * this.view.handTransY),
                            this.cylinder.getAnchor().z + this.view.depthHand,
                            this.view.rot.z,
                            this.view.rot.x,
                            this.artefactSet.getArtefactType().getShotRange(),
                            this.artefactSet.getArtefactType().artefactKind.getBulletHoleSize(),
                        ShooterDebug.shotAndHit,
                            this.artefactSet.getArtefactType().artefactKind.getSliverParticleQuantity(),
                        FxSettings.SLIVER_ANGLE_MOD,
                            this.artefactSet.getArtefactType().getDamage(),
                            this.artefactSet.getArtefactType().artefactKind.getSliverParticleSize(),
                            this.artefactSet.getArtefactType().getBreaksWalls(),
                        ( (FireArm) this.artefactSet.getArtefactType().artefactKind).getProjectile(),
                        General.FADE_OUT_FACES_TOTAL_TICKS
                    )
                :   new LibShot
                    (
                        ShotType.ESharpAmmo,
                        LibShot.ShotOrigin.EPlayer,
                        modHorzCC,
                        0.0f,
                            this.cylinder.getAnchor().x,
                            this.cylinder.getAnchor().y,
                            this.cylinder.getAnchor().z + this.view.depthHand,
                            this.view.rot.z,
                            this.view.rot.x,
                            this.artefactSet.getArtefactType().getShotRange(),
                            this.artefactSet.getArtefactType().artefactKind.getBulletHoleSize(),
                        ShooterDebug.shotAndHit,
                            this.artefactSet.getArtefactType().artefactKind.getSliverParticleQuantity(),
                        FxSettings.SLIVER_ANGLE_MOD,
                            this.artefactSet.getArtefactType().getDamage(),
                            this.artefactSet.getArtefactType().artefactKind.getSliverParticleSize(),
                            this.artefactSet.getArtefactType().getBreaksWalls(),
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
                cylinder.getAnchor().x    + ( LibMath.sinDeg( view.rot.z + 90.0f ) * view.handTransX ) - ( LibMath.sinDeg( view.rot.z ) * view.handTransY ),
                cylinder.getAnchor().y    + ( LibMath.cosDeg( view.rot.z + 90.0f ) * view.handTransX ) - ( LibMath.cosDeg( view.rot.z ) * view.handTransY ),
                cylinder.getAnchor().z + view.depthHand,
                view.rot.z,
                view.rot.x
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
                            !(this.artefactSet.currentArtefact.artefactType.artefactKind instanceof Gadget )
                        ||  ( (Gadget) this.artefactSet.currentArtefact.artefactType.artefactKind).giveTakeAnimState == GiveTakeAnim.ENone
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
            return ( General.DISABLE_PLAYER_WALKING_ANGLE_Y ? 0.0f : LibMath.sinDeg(this.walkingAngleY) );
        }
        public final float getWalkingAngleCarriedModifierX()
        {
            return LibMath.sinDeg(this.walkingAngleWearponX);
        }
        public final float getWalkingAngleCarriedModifierY()
        {
            return LibMath.sinDeg(this.walkingAngleWearponY);
        }

        public final void hurt( int descent )
        {
            if ( PlayerSettings.INVINCIBILITY ) return;

            //player can only lose energy if he is not dying
            if ( !this.dead && this.health > 0 )
            {
                //play hurt sound
                SoundFg.EPlayerHit1.playGlobalFx( 20 );

                //substract damage - clip rock bottom
/*
                this.setHealth(this.health - descent );
*/
                //start red screen anim
                //HUDFx.launchDamageFX( descent );

                //start damage fx
                Shooter.game.engine.hudFx.launchDamageFX( 1 );

                //play damage sound delayed
                SoundFg.EPlayerHit1.playGlobalFx( 20 );
            }
        }

        public final void heal( int gainer )
        {
            //player can only be healed if it is not too late
            if ( !this.dead && this.health < PlayerSettings.MAX_HEALTH )
            {
                //substract damage - clip rock bottom
                this.setHealth(this.health + gainer );

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
                Cylinder actionCylinder = this.cylinder.copy();

                if ( gadget == null )
                {
                    actionCylinder.setRadius( PlayerSettings.RADIUS_ACTION );
                }
                else
                {
                    actionCylinder.setRadius( gadget.parentKind.getShotRange() );
                }

                //launch the action on the level
                Level.currentSection().launchAction( actionCylinder, gadget, this.view.rot.z );
            }
        }

        public final LibVertex getAnchor()
        {
            return this.cylinder.getAnchor();
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
            this.cylinder.drawStandingCircle();
        }

        public final int getHealth()
        {
            return this.health;
        }

        private void setHealth(int health )
        {
            this.health = health;

            //clip roof
            if (this.health > PlayerSettings.MAX_HEALTH )
            {
                this.health = PlayerSettings.MAX_HEALTH;
            }

            //clip ceiling - kill if player falls to death
            if (this.health <= 0 )
            {
                this.health = 0;
                this.kill();
            }

            //health changed
            Shooter.game.engine.hud.healthChanged();
        }

        public final LibViewSet getCameraPositionAndRotation()
        {
            //3rd person camera?
            float modX = ( ! General.ENABLE_3RD_PERSON_CAMERA ? 0.0f : LibMath.sinDeg(this.view.rot.z ) * 2.0f );
            float modY = ( ! General.ENABLE_3RD_PERSON_CAMERA ? 0.0f : LibMath.cosDeg(this.view.rot.z ) * 2.0f );
            float modZ = this.view.depthEye + this.getWalkingAngleModifier() * PlayerSettings.AMP_WALKING_Z;

            float posX = 0.0f;
            float posY = 0.0f;
            float posZ = 0.0f;

            //check if user is dying
            if (this.dead)
            {
                posX = -this.cylinder.getAnchor().x;
                posZ = -this.cylinder.getAnchor().z - PlayerSettings.DEPTH_DEATH - (this.view.depthEye - this.view.dieModTransZ);
                posY = -this.cylinder.getAnchor().y;
            }
            else
            {
                posX = -this.cylinder.getAnchor().x - modX;
                posZ = -this.cylinder.getAnchor().z - modZ - this.view.dieModTransZ;
                posY = -this.cylinder.getAnchor().y - modY;
            }

            //zoom
            if (this.zoom != 0.0f )
            {
                //calc new player pos was hard work but is unused :(
                boolean calcNewPlayerPos_HardWork = false;
                if ( calcNewPlayerPos_HardWork )
                {
                    Point3d iEndPoint3d = LibMathGeometry.getDistantPoint( new Point3d( posX, posY, posZ ), -this.zoom, this.view.rot.x, this.view.rot.z );

                    posX = (float)iEndPoint3d.x;
                    posY = (float)iEndPoint3d.y;
                    posZ = (float)iEndPoint3d.z;
                }

                //set glu perspective zoom
                Shooter.game.engine.glView.setNewGluFaceAngle( LibGLView.VIEW_ANGLE - this.zoom);

                //enter matrix mode modelview
                GL11.glMatrixMode( GL11.GL_MODELVIEW );
            }

            return new LibViewSet
            (
                posX,
                posY,
                posZ,
                    this.view.rot.x + this.view.dieModX,
                    this.view.rot.y + this.view.dieModY,
                    this.view.rot.z - this.view.dieModZ
            );
        }

        /***********************************************************************************
        *   What would this method possibly do to the player?
        ***********************************************************************************/
        private void kill()
        {
            this.health = 0;
            this.dead = true;
            this.view.dyingState = DyingState.EFallingDown;

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
            return this.dead;
        }

        public final boolean isDeadAnimationOver()
        {
            return (this.dead && this.view.dyingAnimationOver() );
        }

        public final AmmoSet getAmmoSet()
        {
            return this.ammoSet;
        }

        private PlayerView getView()
        {
            return this.view;
        }

        private void moveToNewPosition()
        {
            this.getCylinder().moveToTargetPosition(this.getView().depthTotal, General.DISABLE_PLAYER_TO_WALL_COLLISIONS, General.DISABLE_PLAYER_TO_BOT_COLLISIONS );
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
                if (this.artefactSet.currentArtefact != null )
                {
                    this.artefactSet.currentArtefact.handleArtefact( this, this.launchShot, this.ammoSet);
                }
            }
        }

        public final boolean getLaunchShot()
        {
            //operate synchronous
            return this.launchShot;
        }

        public final boolean isHealthLow()
        {
            return (this.health <= HUDSettings.PLAYER_LOW_HEALTH_WARNING_PERCENT );
        }

        public final void launchAction( LibCylinder cylinder, Object gadget, float faceAngle )
        {
            //actions have no effect on the player ?
        }
    }
