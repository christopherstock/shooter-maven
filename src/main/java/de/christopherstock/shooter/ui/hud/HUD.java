
    package de.christopherstock.shooter.ui.hud;

    import  de.christopherstock.lib.LibAnimation;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.ui.LibFPS;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.Colors;
    import  de.christopherstock.shooter.ShooterSetting.HUDSettings;
    import  de.christopherstock.shooter.ShooterSetting.OffsetsOrtho;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.AvatarMessage.AvatarImage;

    /*******************************************************************************************************************
    *   The Heads Up Display.
    *******************************************************************************************************************/
    public class HUD
    {
        public enum ChangeAction
        {
            EActionNext,
            EActionPrevious,
            EActionReload,
            EActionDie,
            ;
        }

        public              LibAnimation        animationState                      = LibAnimation.EAnimationNone;
        private             ChangeAction        actionAfterHide                     = null;

        private             LibGLTextureImage   ammoImageMagazineAmmo               = null;
        private             LibGLTextureImage   ammoImageTotalAmmo                  = null;
        private             String              displayAmmoStringMagazineAmmo       = null;
        private             String              displayAmmoStringTotalAmmo          = null;
        private             String              currentAmmoStringMagazineAmmo       = null;
        private             String              currentAmmoStringTotalAmmo          = null;

        private             LibGLTextureImage   healthImage                         = null;
        private             String              displayHealthString                 = null;
        private             String              currentHealthString                 = null;

        private             int                 animationPlayerRightHand            = 0;
        private             int                 healthShowTimer                     = 0;

        public              boolean             hideWearpon                         = false;

        /** Frames per second counter. */
        public              LibFPS              fps                                 = null;

        public HUD()
        {
            //parse ortho offsets
            OffsetsOrtho.parseOffsets( Shooter.game.engine.glView.width, Shooter.game.engine.glView.height );

            //load all images
            ArtefactType.loadImages();
            AmmoSet.loadImages();
            AvatarImage.loadImages();
            CrossHair.loadImages();

            //init fps
            this.initFPS();
        }

        public final void draw()
        {
            //level may not be initialized
            if ( Level.currentSection() != null )
            {
                //draw player's wearpon or gadget
                Shooter.game.engine.player.artefactSet.drawArtefactOrtho();

                //draw ammo if the wearpon uses ammo
                if ( Shooter.game.engine.player.artefactSet.showAmmoInHUD() )
                {
                    //draw ammo
                    this.drawAmmo();

                    //draw crosshair if placer aims
                    if ( Shooter.game.engine.player.aiming)
                    {
                        this.drawCrosshair();
                    }
                }

                //draw health if currently changed
                boolean drawHealthWarning = Shooter.game.engine.player.isHealthLow();
                if (this.healthShowTimer > 0 || drawHealthWarning )
                {
                    this.drawHealth( drawHealthWarning );
                }
            }

            //draw avatar message ( if active )
            AvatarMessage.drawMessage();

            //draw all hud messages
            Shooter.game.engine.hudMessagesManager.drawAll();

            //draw fullscreen hud effects
            Shooter.game.engine.hudFx.drawHUDEffects();

            //draw frames per second last
            Shooter.game.engine.hud.fps.draw( OffsetsOrtho.EBorderHudX, OffsetsOrtho.EBorderHudY );

            //draw debug logs
            //Shooter.game.engine.player.drawDebugLog(      g );
        }

        private void drawAmmo()
        {
            Artefact currentWearpon = Shooter.game.engine.player.artefactSet.getArtefact();

            //only if this is a reloadable wearpon
            if ( currentWearpon.artefactType.isFireArm() /* && Shooter.game.hud.animationState == LibAnimation.EAnimationNone */ )
            {
                //create current ammo string
                this.currentAmmoStringMagazineAmmo = currentWearpon.getCurrentAmmoStringMagazineAmmo();
                this.currentAmmoStringTotalAmmo = currentWearpon.getCurrentAmmoStringTotalAmmo( Shooter.game.engine.player.ammoSet);

                //recreate ammo image if changed
                if (this.displayAmmoStringMagazineAmmo == null || !this.displayAmmoStringTotalAmmo.equals(this.currentAmmoStringTotalAmmo) || !this.displayAmmoStringMagazineAmmo.equals(this.currentAmmoStringMagazineAmmo) )
                {
                    this.displayAmmoStringMagazineAmmo = this.currentAmmoStringMagazineAmmo;
                    this.displayAmmoStringTotalAmmo = this.currentAmmoStringTotalAmmo;
                    this.ammoImageMagazineAmmo = LibGLTextureImage.getFromString
                    (
                        this.displayAmmoStringMagazineAmmo,
                        Shooter.game.engine.fonts.ammo,
                        Colors.EFpsFg.colARGB,
                        null,
                        Colors.EFpsOutline.colARGB,
                        ShooterDebug.glImage
                    );
                    this.ammoImageTotalAmmo = LibGLTextureImage.getFromString
                    (
                            this.displayAmmoStringTotalAmmo,
                        Shooter.game.engine.fonts.ammo,
                        Colors.EFpsFg.colARGB,
                        null,
                        Colors.EFpsOutline.colARGB,
                        ShooterDebug.glImage
                    );
                }

                //draw shell image
                Shooter.game.engine.glView.drawOrthoBitmapBytes( ( (FireArm)currentWearpon.artefactType.artefactKind).getAmmoTypeImage(), Shooter.game.engine.glView.width - OffsetsOrtho.EBorderHudX - 50, OffsetsOrtho.EBorderHudY );

                //draw magazine ammo
                Shooter.game.engine.glView.drawOrthoBitmapBytes(this.ammoImageMagazineAmmo, Shooter.game.engine.glView.width - OffsetsOrtho.EBorderHudX - 50 - ( (FireArm)currentWearpon.artefactType.artefactKind).getAmmoTypeImage().width - this.ammoImageMagazineAmmo.width, OffsetsOrtho.EBorderHudY );

                //draw total ammo
                Shooter.game.engine.glView.drawOrthoBitmapBytes(this.ammoImageTotalAmmo, Shooter.game.engine.glView.width - OffsetsOrtho.EBorderHudX - this.ammoImageTotalAmmo.width, OffsetsOrtho.EBorderHudY );
            }
        }

        public final void healthChanged()
        {
            this.healthShowTimer = HUDSettings.TICKS_SHOW_HEALTH_AFTER_CHANGE;
        }

        private void drawHealth( boolean drawHealthWarning )
        {
            //draw player health
            this.currentHealthString = String.valueOf( Shooter.game.engine.player.getHealth() );

            //recreate ammo image if changed
            if (this.displayHealthString == null || !this.displayHealthString.equals(this.currentHealthString) )
            {
                this.displayHealthString = this.currentHealthString;

                this.healthImage = LibGLTextureImage.getFromString
                (
                    this.displayHealthString,
                    Shooter.game.engine.fonts.health,
                    ( drawHealthWarning ? Colors.EHealthFgWarning.colABGR : Colors.EHealthFgNormal.colABGR ),
                    null,
                    Colors.EHealthOutline.colARGB,
                    ShooterDebug.glImage
                );
            }

            //fade last displayed ticks ( not for health warning )
            float alpha = 1.0f;
            if (this.healthShowTimer < HUDSettings.TICKS_FADE_OUT_HEALTH && !drawHealthWarning )
            {
                alpha = this.healthShowTimer / (float)HUDSettings.TICKS_FADE_OUT_HEALTH;
            }

            //if ( healthShowTimer <  )

            //draw health image
            Shooter.game.engine.glView.drawOrthoBitmapBytes(this.healthImage, OffsetsOrtho.EBorderHudX, OffsetsOrtho.EBorderHudY, alpha );
        }

        private void drawCrosshair()
        {
            //draw crosshair
            int   modY = 0; //(int)( ( ShooterGameShooter.game.engine.player.getView().rot.x / PlayerAttributes.MAX_LOOKING_X ) * ( Shooter.game.engine.frame.height / 5 ) );
            CrossHair crosshair = Shooter.game.engine.player.artefactSet.getArtefactType().getCrossHair();
            Shooter.game.engine.glView.drawOrthoBitmapBytes
            (
                crosshair.getImage(),
                Shooter.game.engine.glView.width  / 2 - crosshair.getImage().width  / 2,
                Shooter.game.engine.glView.height / 2 - crosshair.getImage().height / 2 + modY
            );
        }

        public final void onRun()
        {
            Shooter.game.engine.hudFx.animateEffects();             //animate hud-effects
            AvatarMessage.animate();                                //animate avatar msgs
            this.animateHUDScores();                                //animate health timer etc.
            this.animateRightHand();                                //animate right hand
            Shooter.game.engine.hudMessagesManager.animateAll();    //animate hud msgs
        }

        private void animateHUDScores()
        {
            //animate HUD-effects
            if (this.healthShowTimer > 0 ) --this.healthShowTimer;
        }

        private void animateRightHand()
        {
            //animate right hand
            if (this.animationPlayerRightHand > 0 )
            {
                switch (this.animationState)
                {
                    case EAnimationNone:
                    {
                        break;
                    }

                    case EAnimationShow:
                    {
                        --this.animationPlayerRightHand;

                        //check if animation is over
                        if (this.animationPlayerRightHand == 0 )
                        {
                            this.animationState = LibAnimation.EAnimationNone;
                        }
                        break;
                    }

                    case EAnimationHide:
                    {
                        --this.animationPlayerRightHand;

                        //check if animation is over
                        if (this.animationPlayerRightHand == 0 )
                        {
                            switch (this.actionAfterHide)
                            {
                                case EActionNext:
                                {
                                    Shooter.game.engine.player.artefactSet.chooseNextWearponOrGadget( true );
                                    break;
                                }

                                case EActionPrevious:
                                {
                                    Shooter.game.engine.player.artefactSet.choosePreviousWearponOrGadget( true );
                                    break;
                                }

                                case EActionReload:
                                {
                                    //reload ammo
                                    Shooter.game.engine.player.artefactSet.getArtefact().performReload( Shooter.game.engine.player.ammoSet, true, null, false );

                                    this.animationState = LibAnimation.EAnimationShow;
                                    this.animationPlayerRightHand = ShooterSetting.Performance.TICKS_WEARPON_HIDE_SHOW;
                                    break;
                                }

                                case EActionDie:
                                {
                                    Shooter.game.engine.player.artefactSet.setArtefact( Shooter.game.engine.player.artefactSet.hands);

                                    //hideWearpon = true;
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        public final void startHandAnimation( LibAnimation newAnimationState, ChangeAction newActionAfterHide )
        {
            this.animationPlayerRightHand = ShooterSetting.Performance.TICKS_WEARPON_HIDE_SHOW;
            this.animationState = newAnimationState;
            this.actionAfterHide = newActionAfterHide;
        }

        public final void stopHandAnimation()
        {
            this.animationPlayerRightHand = 0;
            this.animationState = LibAnimation.EAnimationNone;
        }

        public final boolean animationActive()
        {
            return (this.animationPlayerRightHand != 0 );
        }

        public final int getAnimationRightHand()
        {
            return this.animationPlayerRightHand;
        }

        public void resetAnimation()
        {
            this.animationPlayerRightHand = 0;
        }

        /***************************************************************************************************************
        *   Inits the Frames Per Second counter.
        ***************************************************************************************************************/
        private void initFPS()
        {
            ShooterDebug.init.out( "init FPS counter" );

            this.fps = new LibFPS( Shooter.game.engine.fonts.fps, ShooterSetting.Colors.EFpsFg.colABGR, ShooterSetting.Colors.EFpsOutline.colABGR, ShooterDebug.glImage );
        }
    }
