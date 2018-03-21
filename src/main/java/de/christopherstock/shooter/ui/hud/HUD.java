
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
                Level.currentPlayer().iArtefactSet.drawArtefactOrtho();

                //draw ammo if the wearpon uses ammo
                if ( Level.currentPlayer().iArtefactSet.showAmmoInHUD() )
                {
                    //draw ammo
                    drawAmmo();

                    //draw crosshair if placer aims
                    if ( Level.currentPlayer().iAiming )
                    {
                        drawCrosshair();
                    }
                }

                //draw health if currently changed
                boolean drawHealthWarning = Level.currentPlayer().isHealthLow();
                if ( iHealthShowTimer > 0 || drawHealthWarning )
                {
                    drawHealth( drawHealthWarning );
                }
            }

            //draw avatar message ( if active )
            AvatarMessage.drawMessage();

            //draw all hud messages
            HUDMessageManager.getSingleton().drawAll();

            //draw fullscreen hud effects
            HUDFx.drawHUDEffects();

            //draw frames per second last
            Shooter.mainThread.fps.draw( OffsetsOrtho.EBorderHudX, OffsetsOrtho.EBorderHudY );

            //draw debug logs
            //Level.currentPlayer().drawDebugLog(      g );
        }

        public void drawAmmo()
        {
            Artefact currentWearpon = Level.currentPlayer().iArtefactSet.getArtefact();

            //only if this is a reloadable wearpon
            if ( currentWearpon.iArtefactType.isFireArm() /* && Shooter.mainThread.hud.iAnimationState == LibAnimation.EAnimationNone */ )
            {
                //create current ammo string
                iCurrentAmmoStringMagazineAmmo  = currentWearpon.getCurrentAmmoStringMagazineAmmo();
                iCurrentAmmoStringTotalAmmo     = currentWearpon.getCurrentAmmoStringTotalAmmo( Level.currentPlayer().iAmmoSet );

                //recreate ammo image if changed
                if ( iDisplayAmmoStringMagazineAmmo == null || !iDisplayAmmoStringTotalAmmo.equals( iCurrentAmmoStringTotalAmmo ) || !iDisplayAmmoStringMagazineAmmo.equals( iCurrentAmmoStringMagazineAmmo ) )
                {
                    iDisplayAmmoStringMagazineAmmo = iCurrentAmmoStringMagazineAmmo;
                    iDisplayAmmoStringTotalAmmo    = iCurrentAmmoStringTotalAmmo;
                    iAmmoImageMagazineAmmo = LibGLImage.getFromString
                    (
                        iDisplayAmmoStringMagazineAmmo,
                        Fonts.EAmmo,
                        Colors.EFpsFg.colARGB,
                        null,
                        Colors.EFpsOutline.colARGB,
                        ShooterDebug.glImage
                    );
                    iAmmoImageTotalAmmo = LibGLImage.getFromString
                    (
                        iDisplayAmmoStringTotalAmmo,
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
                LibGL3D.view.drawOrthoBitmapBytes( iAmmoImageMagazineAmmo, LibGL3D.panel.width - OffsetsOrtho.EBorderHudX - 50 - ( (FireArm)currentWearpon.iArtefactType.iArtefactKind ).getAmmoTypeImage().width - iAmmoImageMagazineAmmo.width, OffsetsOrtho.EBorderHudY );

                //draw total ammo
                LibGL3D.view.drawOrthoBitmapBytes( iAmmoImageTotalAmmo, LibGL3D.panel.width - OffsetsOrtho.EBorderHudX - iAmmoImageTotalAmmo.width, OffsetsOrtho.EBorderHudY );
            }
        }

        public final void healthChanged()
        {
            iHealthShowTimer = HUDSettings.TICKS_SHOW_HEALTH_AFTER_CHANGE;
        }

        private void drawHealth( boolean drawHealthWarning )
        {
            //draw player health
            iCurrentHealthString = String.valueOf( Level.currentPlayer().getHealth() );

            //recreate ammo image if changed
            if ( iDisplayHealthString == null || !iDisplayHealthString.equals( iCurrentHealthString ) )
            {
                iDisplayHealthString = iCurrentHealthString;

                iHealthImage = LibGLImage.getFromString
                (
                    iDisplayHealthString,
                    Fonts.EHealth,
                    ( drawHealthWarning ? Colors.EHealthFgWarning.colABGR : Colors.EHealthFgNormal.colABGR ),
                    null,
                    Colors.EHealthOutline.colARGB,
                    ShooterDebug.glImage
                );
            }

            //fade last displayed ticks ( not for health warning )
            float alpha = 1.0f;
            if ( iHealthShowTimer < HUDSettings.TICKS_FADE_OUT_HEALTH && !drawHealthWarning )
            {
                alpha = iHealthShowTimer / (float)HUDSettings.TICKS_FADE_OUT_HEALTH;
            }

            //if ( iHealthShowTimer <  )

            //draw health image
            LibGL3D.view.drawOrthoBitmapBytes( iHealthImage, OffsetsOrtho.EBorderHudX, OffsetsOrtho.EBorderHudY, alpha );
        }

        public final void drawCrosshair()
        {
            //draw crosshair
            int   modY = 0; //(int)( ( ShooterGameLevel.currentPlayer().getView().rot.x / PlayerAttributes.MAX_LOOKING_X ) * ( LibGL3D.panel.height / 5 ) );
            CrossHair crosshair = Level.currentPlayer().iArtefactSet.getArtefactType().getCrossHair();
            LibGL3D.view.drawOrthoBitmapBytes( crosshair.getImage(), LibGL3D.panel.width / 2 - crosshair.getImage().width / 2, LibGL3D.panel.height / 2 - crosshair.getImage().height / 2 + modY );
        }

        public final void onRun()
        {
            HUDFx.animateEffects();                         //animate hud-effects
            AvatarMessage.animate();                        //animate avatar msgs
            animateHUDScores();                             //animate health timer etc.
            animateRightHand();                             //animate right hand
            HUDMessageManager.getSingleton().animateAll();  //animate hud msgs
        }

        private void animateHUDScores()
        {
            //animate HUD-effects
            if ( iHealthShowTimer > 0 ) --iHealthShowTimer;
        }

        private void animateRightHand()
        {
            //animate right hand
            if ( iAnimationPlayerRightHand > 0 )
            {
                switch ( iAnimationState )
                {
                    case EAnimationNone:
                    {
                        break;
                    }

                    case EAnimationShow:
                    {
                        --iAnimationPlayerRightHand;

                        //check if animation is over
                        if ( iAnimationPlayerRightHand == 0 )
                        {
                            iAnimationState = LibAnimation.EAnimationNone;
                        }
                        break;
                    }

                    case EAnimationHide:
                    {
                        --iAnimationPlayerRightHand;

                        //check if animation is over
                        if ( iAnimationPlayerRightHand == 0 )
                        {
                            switch ( iActionAfterHide )
                            {
                                case EActionNext:
                                {
                                    Level.currentPlayer().iArtefactSet.chooseNextWearponOrGadget( true );
                                    break;
                                }

                                case EActionPrevious:
                                {
                                    Level.currentPlayer().iArtefactSet.choosePreviousWearponOrGadget( true );
                                    break;
                                }

                                case EActionReload:
                                {
                                    //reload ammo
                                    Level.currentPlayer().iArtefactSet.getArtefact().performReload( Level.currentPlayer().iAmmoSet, true, null, false );

                                    iAnimationState = LibAnimation.EAnimationShow;
                                    iAnimationPlayerRightHand = ShooterSettings.Performance.TICKS_WEARPON_HIDE_SHOW;
                                    break;
                                }

                                case EActionDie:
                                {
                                    Level.currentPlayer().iArtefactSet.setArtefact( Level.currentPlayer().iArtefactSet.iHands );

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
            iAnimationPlayerRightHand    = ShooterSettings.Performance.TICKS_WEARPON_HIDE_SHOW;
            iAnimationState              = newAnimationState;
            iActionAfterHide             = newActionAfterHide;
        }

        public final void stopHandAnimation()
        {
            iAnimationPlayerRightHand    = 0;
            iAnimationState              = LibAnimation.EAnimationNone;
        }

        public final boolean animationActive()
        {
            return ( iAnimationPlayerRightHand != 0 );
        }

        public final int getAnimationRightHand()
        {
            return iAnimationPlayerRightHand;
        }

        public void resetAnimation()
        {
            iAnimationPlayerRightHand = 0;
        }
    }
