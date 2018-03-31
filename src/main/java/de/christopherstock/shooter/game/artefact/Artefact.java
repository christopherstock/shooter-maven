
    package de.christopherstock.shooter.game.artefact;

    import  java.awt.geom.Point2D;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.LibShot.ShotSource;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.game.artefact.firearm.AmmoSet;
    import  de.christopherstock.shooter.game.artefact.firearm.FireArm;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.ui.hud.HUD.*;

    /*******************************************************************************************************************
    *   Represents one occurence of an {@link ArtefactType} being hold by a player or a bot.
    *******************************************************************************************************************/
    public class Artefact
    {
        public              ArtefactType            artefactType                        = null;

        public              int                     currentShotsWithoutKeyRelease       = 0;
        public              long                    currentDelayAfterUse                = 0;
        private             boolean                 drawFireFXtick                      = false;
        public              int                     magazineAmmo                        = 0;

        public Artefact( ArtefactType artefactType )
        {
            this.artefactType = artefactType;
        }

        public final void fire( ShotSource ss, Point2D.Float shooterXY )
        {
            //check if delay after use is still active
            if (this.currentDelayAfterUse > System.currentTimeMillis() )
            {
                //delay after wearpon use
            }
            //try to fire wearpon
            else if (this.artefactType.artefactKind.use( this, ss, shooterXY ) )
            {
                //draw fire fx
                this.drawFireFXtick = true;

                //set delay after this shot
                this.currentDelayAfterUse = System.currentTimeMillis() + this.artefactType.delayAfterUse;

                //increase number of shots since last key release
                ++this.currentShotsWithoutKeyRelease;
            }
        }

        public final void handleArtefact( ShotSource ss, boolean doFire, AmmoSet ammoSet )
        {
            //check artefact actions
            if (this.currentDelayAfterUse > System.currentTimeMillis() )
            {
                //delay after artefact use
            }
            else
            {
                //reload?
                if ( Shooter.game.engine.keys.reload.launchAction || Shooter.game.engine.mouse.holdButtonCenter)
                {
                    //reload if the wearpon uses ammo
                    Shooter.game.engine.keys.reload.launchAction = false;
                    Shooter.game.engine.mouse.holdButtonCenter = false;

                    //if the wearpon has ammo
                    if (this.artefactType.isFireArm() )
                    {
                        this.reload( ammoSet, true, true, null );
                    }
                }
                //shoot
                else if ( doFire )
                {
                    this.fire( ss, null );
                }
            }

            //animate start / give anim
            if (this.artefactType.artefactKind instanceof Gadget )
            {
                ( (Gadget) this.artefactType.artefactKind).handleGadget();
            }
        }
        public final void drawOrtho()
        {
            //this is player's gun
            Player p = Shooter.game.engine.player;

            int     modX    = ( 20 + (int)(  20 * p.getWalkingAngleCarriedModifierX() ) );      //clip on right border
            int     modY    = ( -(int)( 10 * p.getWalkingAngleCarriedModifierY() ) );

            //hide/show animation?
            if ( Shooter.game.engine.hud.animationActive() )
            {
                switch ( Shooter.game.engine.hud.animationState)
                {
                    case EAnimationNone:
                    {
                        break;
                    }
                    case EAnimationHide:
                    {
                        modY -= this.artefactType.getArtefactImage().height - Shooter.game.engine.hud.getAnimationRightHand() * this.artefactType.getArtefactImage().height / ShooterSetting.Performance.TICKS_WEARPON_HIDE_SHOW;
                        break;
                    }
                    case EAnimationShow:
                    {
                        modY -= Shooter.game.engine.hud.getAnimationRightHand() * this.artefactType.getArtefactImage().height / ShooterSetting.Performance.TICKS_WEARPON_HIDE_SHOW;
                        break;
                    }
                }
            }

            //give/take animation?
            int[] modGiveTake = new int[] { 0, 0, };
            if (this.artefactType.artefactKind instanceof Gadget )
            {
                modGiveTake = ( (Gadget) this.artefactType.artefactKind).getGiveTakeDrawMod();
            }

            //zoom animation?
            if ( p.zoom != 0.0f )
            {
                modX += this.artefactType.getArtefactImage().width  * 0.75f * p.scaleFactor * 2;
                modY -= this.artefactType.getArtefactImage().height * 0.5f  * p.scaleFactor / 2;
            }

            //draw fire fx behind artefact
            if (this.drawFireFXtick && this.artefactType.artefactKind instanceof FireArm )
            {
                //no random translation!
                int         randomX     = 0; //LibMath.getRandom( -5, 5 );
                int         randomY     = 0; //LibMath.getRandom( -5, 5 );

                //only draw if fx image could be read
                if (this.artefactType.fXImages.length > 1 )
                {
                    int         randomIndex = LibMath.getRandom( 0, this.artefactType.fXImages.length - 1 );
                    LibGLTextureImage fxImage     = this.artefactType.fXImages[ randomIndex ];

                    Shooter.game.engine.glView.drawOrthoBitmapBytes
                    (
                        fxImage,
                        randomX + modX + modGiveTake[ 0 ] + Shooter.game.engine.glView.width - this.artefactType.fXOffset.x,
                        randomY + modY + modGiveTake[ 1 ] + this.artefactType.fXOffset.y,
                        1.0f,
                        1.0f + p.scaleFactor * 1.5f,
                        1.0f + p.scaleFactor * 1.5f,
                        true
                    );
                }

                this.drawFireFXtick = false;
            }

            //draw artefact
            if ( !Shooter.game.engine.hud.hideWearpon)
            {
                Shooter.game.engine.glView.drawOrthoBitmapBytes
                (
                    this.artefactType.getArtefactImage(),
                    modX + modGiveTake[ 0 ] + Shooter.game.engine.glView.width - this.artefactType.getArtefactImage().width,
                    modY + modGiveTake[ 1 ] - this.artefactType.getArtefactImage().height / 8,
                    1.0f,
                    1.0f + p.scaleFactor * 1.5f,
                    1.0f + p.scaleFactor * 1.5f,
                    true
                );
            }
        }

        public final String getCurrentAmmoStringMagazineAmmo()
        {
            return String.valueOf(this.magazineAmmo);
        }

        public final String getCurrentAmmoStringTotalAmmo( AmmoSet ammoSet )
        {
            return String.valueOf( ammoSet.getAmmo( ( (FireArm) this.artefactType.artefactKind).ammoType) );
        }

        public void reload( AmmoSet ammoSet, boolean reloadAnimationRequired, boolean playSound, Point2D.Float reloaderXY )
        {
            //if ammo to reload is available
            if ( ammoSet.getAmmo( ( (FireArm) this.artefactType.artefactKind).ammoType) > 0 )
            {
                //if the wearpon is not fully loaded
                if ( ( (FireArm) this.artefactType.artefactKind).magazineSize != this.magazineAmmo)
                {
                    //start HUD-animation 'hide' only if required
                    if ( reloadAnimationRequired )
                    {
                        //start reload animation
                        Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationHide, ChangeAction.EActionReload );
                    }
                    else
                    {
                        //perform reload
                        this.performReload( ammoSet, playSound, reloaderXY, false );
                    }
                }
            }
        }

        public final void performReload( AmmoSet ammoSet, boolean playSound, Point2D.Float reloaderXY, boolean freeAmmo )
        {
            FireArm fireArm = ( (FireArm) this.artefactType.artefactKind);

            //put unused ammo back onto the stack!
            ammoSet.addAmmo( fireArm.ammoType, this.magazineAmmo);

            //check ammo stock
            int ammo = ammoSet.getAmmo( fireArm.ammoType);
            int ammoToReload = ( ammo >= fireArm.magazineSize ? fireArm.magazineSize : ammo );

            if ( freeAmmo ) ammoToReload = fireArm.magazineSize;

            //load ammoToReload into the magazine and substract it from the ammo stock
            this.magazineAmmo = ammoToReload;
            ammoSet.substractAmmo( fireArm.ammoType, ammoToReload );

            //play reload sound
            if ( playSound )
            {
                if ( reloaderXY == null )
                {
                    fireArm.getReloadSound().playGlobalFx();
                }
                else
                {
                    fireArm.getReloadSound().playDistancedFx( reloaderXY );
                }
            }
        }

        public final ItemToPickUp getPickUpItem( LibVertex ank )
        {
            if (this.artefactType.pickUpItemKind == null ) return null;
            return new ItemToPickUp(this.artefactType.pickUpItemKind, this, ank.x, ank.y, ank.z, LibMath.getRandom( 0, 360 ), LibRotating.ENo );
        }
    }
