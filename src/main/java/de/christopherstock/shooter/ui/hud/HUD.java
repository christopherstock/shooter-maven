
    package de.christopherstock.shooter.ui.hud;

    import  de.christopherstock.lib.Lib.LibAnimation;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.Colors;
    import  de.christopherstock.shooter.ShooterSettings.Fonts;
    import  de.christopherstock.shooter.ShooterSettings.HUDSettings;
    import  de.christopherstock.shooter.ShooterSettings.OffsetsOrtho;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.game.objects.Player.HealthChangeListener;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.AvatarMessage.AvatarImage;

    /*******************************************************************************************************************
    *   The Heads Up Display.
    *******************************************************************************************************************/
    public class HUD implements HealthChangeListener
    {
        public enum ChangeAction
        {
            EActionNext,
            EActionPrevious,
            EActionReload,
            EActionDie,
            ;
        }

        public                          LibAnimation    iAnimationState                 = LibAnimation.EAnimationNone;
        private                         ChangeAction    iActionAfterHide                = null;

        private                         LibGLImage      iAmmoImageMagazineAmmo          = null;
        private                         LibGLImage      iAmmoImageTotalAmmo             = null;
        private                         String          iDisplayAmmoStringMagazineAmmo  = null;
        private                         String          iDisplayAmmoStringTotalAmmo     = null;
        private                         String          iCurrentAmmoStringMagazineAmmo  = null;
        private                         String          iCurrentAmmoStringTotalAmmo     = null;

        private                         LibGLImage      iHealthImage                    = null;
        private                         String          iDisplayHealthString            = null;
        private                         String          iCurrentHealthString            = null;

        private                         int             iAnimationPlayerRightHand       = 0;
        private                         int             iHealthShowTimer                = 0;

        public                          boolean         iHideWearpon                    = false;

        public HUD()
        {
            //parse ortho offsets
            OffsetsOrtho.parseOffsets( LibGL3D.panel.width, LibGL3D.panel.height );

            //load all images
            ArtefactType.loadImages();
            AmmoSet.loadImages();
            BackGround.loadImages();
            AvatarImage.loadImages();
            CrossHair.loadImages();

            //init fps

        }

        public final void draw2D()
        {
            //level may not be initialized
            if ( Level.currentSection() != null )
            {
                //draw player's wearpon or gadget
                Shooter.game.engine.player.iArtefactSet.drawArtefactOrtho();

                //draw ammo if the wearpon uses ammo
                if ( Shooter.game.engine.player.iArtefactSet.showAmmoInHUD() )
                {
                    //draw ammo
                    this.drawAmmo();

                    //draw crosshair if placer aims
                    if ( Shooter.game.engine.player.iAiming )
                    {
                        this.drawCrosshair();
                    }
                }

                //draw health if currently changed
                boolean drawHealthWarning = Shooter.game.engine.player.isHealthLow();
                if (this.iHealthShowTimer > 0 || drawHealthWarning )
                {
                    this.drawHealth( drawHealthWarning );
                }
            }

            //draw avatar message ( if active )
            AvatarMessage.drawMessage();

            //draw all hud messages
            HUDMessageManager.getSingleton().drawAll();

            //draw fullscreen hud effects
            HUDFx.drawHUDEffects();

            //draw frames per second last
            Shooter.game.engine.fps.draw( OffsetsOrtho.EBorderHudX, OffsetsOrtho.EBorderHudY );

            //draw debug logs
            //Shooter.game.engine.player.drawDebugLog(      g );
        }

        public void drawAmmo()
        {
            Artefact currentWearpon = Shooter.game.engine.player.iArtefactSet.getArtefact();

            //only if this is a reloadable wearpon
            if ( currentWearpon.iArtefactType.isFireArm() /* && Shooter.game.hud.iAnimationState == LibAnimation.EAnimationNone */ )
            {
                //create current ammo string
                this.iCurrentAmmoStringMagazineAmmo = currentWearpon.getCurrentAmmoStringMagazineAmmo();
                this.iCurrentAmmoStringTotalAmmo = currentWearpon.getCurrentAmmoStringTotalAmmo( Shooter.game.engine.player.iAmmoSet );

                //recreate ammo image if changed
                if (this.iDisplayAmmoStringMagazineAmmo == null || !this.iDisplayAmmoStringTotalAmmo.equals(this.iCurrentAmmoStringTotalAmmo) || !this.iDisplayAmmoStringMagazineAmmo.equals(this.iCurrentAmmoStringMagazineAmmo) )
                {
                    this.iDisplayAmmoStringMagazineAmmo = this.iCurrentAmmoStringMagazineAmmo;
                    this.iDisplayAmmoStringTotalAmmo = this.iCurrentAmmoStringTotalAmmo;
                    this.iAmmoImageMagazineAmmo = LibGLImage.getFromString
                    (
                            this.iDisplayAmmoStringMagazineAmmo,
                        Fonts.EAmmo,
                        Colors.EFpsFg.colARGB,
                        null,
                        Colors.EFpsOutline.colARGB,
                        ShooterDebug.glImage
                    );
                    this.iAmmoImageTotalAmmo = LibGLImage.getFromString
                    (
                            this.iDisplayAmmoStringTotalAmmo,
                        Fonts.EAmmo,
                        Colors.EFpsFg.colARGB,
                        null,
                        Colors.EFpsOutline.colARGB,
                        ShooterDebug.glImage
                    );
                }

                //draw shell image
                LibGL3D.view.drawOrthoBitmapBytes( ( (FireArm)currentWearpon.iArtefactType.iArtefactKind ).getAmmoTypeImage(), LibGL3D.panel.width - OffsetsOrtho.EBorderHudX - 50, OffsetsOrtho.EBorderHudY );

                //draw magazine ammo
                LibGL3D.view.drawOrthoBitmapBytes(this.iAmmoImageMagazineAmmo, LibGL3D.panel.width - OffsetsOrtho.EBorderHudX - 50 - ( (FireArm)currentWearpon.iArtefactType.iArtefactKind ).getAmmoTypeImage().width - this.iAmmoImageMagazineAmmo.width, OffsetsOrtho.EBorderHudY );

                //draw total ammo
                LibGL3D.view.drawOrthoBitmapBytes(this.iAmmoImageTotalAmmo, LibGL3D.panel.width - OffsetsOrtho.EBorderHudX - this.iAmmoImageTotalAmmo.width, OffsetsOrtho.EBorderHudY );
            }
        }

        public final void healthChanged()
        {
            this.iHealthShowTimer = HUDSettings.TICKS_SHOW_HEALTH_AFTER_CHANGE;
        }

        private void drawHealth( boolean drawHealthWarning )
        {
            //draw player health
            this.iCurrentHealthString = String.valueOf( Shooter.game.engine.player.getHealth() );

            //recreate ammo image if changed
            if (this.iDisplayHealthString == null || !this.iDisplayHealthString.equals(this.iCurrentHealthString) )
            {
                this.iDisplayHealthString = this.iCurrentHealthString;

                this.iHealthImage = LibGLImage.getFromString
                (
                        this.iDisplayHealthString,
                    Fonts.EHealth,
                    ( drawHealthWarning ? Colors.EHealthFgWarning.colABGR : Colors.EHealthFgNormal.colABGR ),
                    null,
                    Colors.EHealthOutline.colARGB,
                    ShooterDebug.glImage
                );
            }

            //fade last displayed ticks ( not for health warning )
            float alpha = 1.0f;
            if (this.iHealthShowTimer < HUDSettings.TICKS_FADE_OUT_HEALTH && !drawHealthWarning )
            {
                alpha = this.iHealthShowTimer / (float)HUDSettings.TICKS_FADE_OUT_HEALTH;
            }

            //if ( iHealthShowTimer <  )

            //draw health image
            LibGL3D.view.drawOrthoBitmapBytes(this.iHealthImage, OffsetsOrtho.EBorderHudX, OffsetsOrtho.EBorderHudY, alpha );
        }

        public final void drawCrosshair()
        {
            //draw crosshair
            int   modY = 0; //(int)( ( ShooterGameShooter.game.engine.player.getView().rot.x / PlayerAttributes.MAX_LOOKING_X ) * ( LibGL3D.panel.height / 5 ) );
            CrossHair crosshair = Shooter.game.engine.player.iArtefactSet.getArtefactType().getCrossHair();
            LibGL3D.view.drawOrthoBitmapBytes( crosshair.getImage(), LibGL3D.panel.width / 2 - crosshair.getImage().width / 2, LibGL3D.panel.height / 2 - crosshair.getImage().height / 2 + modY );
        }

        public final void onRun()
        {
            HUDFx.animateEffects();                         //animate hud-effects
            AvatarMessage.animate();                        //animate avatar msgs
            this.animateHUDScores();                             //animate health timer etc.
            this.animateRightHand();                             //animate right hand
            HUDMessageManager.getSingleton().animateAll();  //animate hud msgs
        }

        private void animateHUDScores()
        {
            //animate HUD-effects
            if (this.iHealthShowTimer > 0 ) --this.iHealthShowTimer;
        }

        private void animateRightHand()
        {
            //animate right hand
            if (this.iAnimationPlayerRightHand > 0 )
            {
                switch (this.iAnimationState)
                {
                    case EAnimationNone:
                    {
                        break;
                    }

                    case EAnimationShow:
                    {
                        --this.iAnimationPlayerRightHand;

                        //check if animation is over
                        if (this.iAnimationPlayerRightHand == 0 )
                        {
                            this.iAnimationState = LibAnimation.EAnimationNone;
                        }
                        break;
                    }

                    case EAnimationHide:
                    {
                        --this.iAnimationPlayerRightHand;

                        //check if animation is over
                        if (this.iAnimationPlayerRightHand == 0 )
                        {
                            switch (this.iActionAfterHide)
                            {
                                case EActionNext:
                                {
                                    Shooter.game.engine.player.iArtefactSet.chooseNextWearponOrGadget( true );
                                    break;
                                }

                                case EActionPrevious:
                                {
                                    Shooter.game.engine.player.iArtefactSet.choosePreviousWearponOrGadget( true );
                                    break;
                                }

                                case EActionReload:
                                {
                                    //reload ammo
                                    Shooter.game.engine.player.iArtefactSet.getArtefact().performReload( Shooter.game.engine.player.iAmmoSet, true, null, false );

                                    this.iAnimationState = LibAnimation.EAnimationShow;
                                    this.iAnimationPlayerRightHand = ShooterSettings.Performance.TICKS_WEARPON_HIDE_SHOW;
                                    break;
                                }

                                case EActionDie:
                                {
                                    Shooter.game.engine.player.iArtefactSet.setArtefact( Shooter.game.engine.player.iArtefactSet.iHands );

                                    //iHideWearpon = true;
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
            this.iAnimationPlayerRightHand = ShooterSettings.Performance.TICKS_WEARPON_HIDE_SHOW;
            this.iAnimationState = newAnimationState;
            this.iActionAfterHide = newActionAfterHide;
        }

        public final void stopHandAnimation()
        {
            this.iAnimationPlayerRightHand = 0;
            this.iAnimationState = LibAnimation.EAnimationNone;
        }

        public final boolean animationActive()
        {
            return (this.iAnimationPlayerRightHand != 0 );
        }

        public final int getAnimationRightHand()
        {
            return this.iAnimationPlayerRightHand;
        }

        public void resetAnimation()
        {
            this.iAnimationPlayerRightHand = 0;
        }
    }
