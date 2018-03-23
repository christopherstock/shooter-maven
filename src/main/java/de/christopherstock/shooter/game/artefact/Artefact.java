
    package de.christopherstock.shooter.game.artefact;

    import  java.awt.geom.Point2D;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.Lib.LibAnimation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.LibShot.ShotSpender;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.game.artefact.firearm.AmmoSet;
    import  de.christopherstock.shooter.game.artefact.firearm.FireArm;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.io.hid.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.HUD.*;

    /*******************************************************************************************************************
    *   Represents one occurence of an {@link ArtefactType} being hold by a player or a bot.
    *******************************************************************************************************************/
    public class Artefact
    {
        public              ArtefactType            iArtefactType                       = null;

        public              int                     iCurrentShotsWithoutKeyRelease      = 0;
        public              long                    iCurrentDelayAfterUse               = 0;
        private             boolean                 iDrawFireFXtick                     = false;
        public              int                     iMagazineAmmo                       = 0;

        public Artefact( ArtefactType aArtefactType )
        {
            this.iArtefactType = aArtefactType;
        }

        public final void fire( ShotSpender ss, Point2D.Float shooterXY )
        {
            //check if delay after use is still active
            if (this.iCurrentDelayAfterUse > System.currentTimeMillis() )
            {
                //delay after wearpon use
            }
            //try to fire wearpon
            else if (this.iArtefactType.iArtefactKind.use( this, ss, shooterXY ) )
            {
                //draw fire fx
                this.iDrawFireFXtick = true;

                //set delay after this shot
                this.iCurrentDelayAfterUse = System.currentTimeMillis() + this.iArtefactType.iDelayAfterUse;

                //increase number of shots since last key release
                ++this.iCurrentShotsWithoutKeyRelease;
            }
        }

        public final void handleArtefact( ShotSpender ss, boolean doFire, AmmoSet ammoSet )
        {
            //check artefact actions
            if (this.iCurrentDelayAfterUse > System.currentTimeMillis() )
            {
                //delay after artefact use
            }
            else
            {
                //reload?
                if ( Keys.reload.iLaunchAction || MouseInput.mouseHoldReload )
                {
                    //reload if the wearpon uses ammo
                    Keys.reload.iLaunchAction   = false;
                    MouseInput.mouseHoldReload  = false;

                    //if the wearpon has ammo
                    if (this.iArtefactType.isFireArm() )
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
            if (this.iArtefactType.iArtefactKind instanceof Gadget )
            {
                ( (Gadget) this.iArtefactType.iArtefactKind ).handleGadget();
            }
        }
        public final void drawOrtho()
        {
            //this is player's gun
            Player p = Level.currentPlayer();

            int     modX    = ( 20 + (int)(  20 * p.getWalkingAngleCarriedModifierX() ) );      //clip on right border
            int     modY    = ( -(int)( 10 * p.getWalkingAngleCarriedModifierY() ) );

            //hide/show animation?
            if ( Shooter.game.engine.hud.animationActive() )
            {
                switch ( Shooter.game.engine.hud.iAnimationState )
                {
                    case EAnimationNone:
                    {
                        break;
                    }
                    case EAnimationHide:
                    {
                        modY -= this.iArtefactType.getArtefactImage().height - Shooter.game.engine.hud.getAnimationRightHand() * this.iArtefactType.getArtefactImage().height / ShooterSettings.Performance.TICKS_WEARPON_HIDE_SHOW;
                        break;
                    }
                    case EAnimationShow:
                    {
                        modY -= Shooter.game.engine.hud.getAnimationRightHand() * this.iArtefactType.getArtefactImage().height / ShooterSettings.Performance.TICKS_WEARPON_HIDE_SHOW;
                        break;
                    }
                }
            }

            //give/take animation?
            int[] modGiveTake = new int[] { 0, 0, };
            if (this.iArtefactType.iArtefactKind instanceof Gadget )
            {
                modGiveTake = ( (Gadget) this.iArtefactType.iArtefactKind ).getGiveTakeDrawMod();
            }

            //zoom animation?
            if ( p.iZoom != 0.0f )
            {
                modX += this.iArtefactType.getArtefactImage().width  * 0.75f * p.iScaleFactor * 2;
                modY -= this.iArtefactType.getArtefactImage().height * 0.5f  * p.iScaleFactor / 2;
            }

            //draw fire fx behind artefact
            if (this.iDrawFireFXtick && this.iArtefactType.iArtefactKind instanceof FireArm )
            {
                //no random translation!
                int         randomX     = 0; //LibMath.getRandom( -5, 5 );
                int         randomY     = 0; //LibMath.getRandom( -5, 5 );

                //only draw if fx image could be read
                if (this.iArtefactType.iFXImages.length > 1 )
                {
                    int         randomIndex = LibMath.getRandom( 0, this.iArtefactType.iFXImages.length - 1 );
                    LibGLImage  fxImage     = this.iArtefactType.iFXImages[ randomIndex ];

                    LibGL3D.view.drawOrthoBitmapBytes
                    (
                        fxImage,
                        randomX + modX + modGiveTake[ 0 ] + LibGL3D.panel.width - this.iArtefactType.iFXOffset.x,
                        randomY + modY + modGiveTake[ 1 ] + this.iArtefactType.iFXOffset.y,
                        1.0f,
                        1.0f + p.iScaleFactor * 1.5f,
                        1.0f + p.iScaleFactor * 1.5f,
                        true
                    );
                }

                this.iDrawFireFXtick = false;
            }

            //draw artefact
            if ( !Shooter.game.engine.hud.iHideWearpon )
            {
                LibGL3D.view.drawOrthoBitmapBytes
                (
                        this.iArtefactType.getArtefactImage(),
                    modX + modGiveTake[ 0 ] + LibGL3D.panel.width - this.iArtefactType.getArtefactImage().width,
                    modY + modGiveTake[ 1 ] - this.iArtefactType.getArtefactImage().height / 8,
                    1.0f,
                    1.0f + p.iScaleFactor * 1.5f,
                    1.0f + p.iScaleFactor * 1.5f,
                    true
                );
            }
        }

        public final String getCurrentAmmoStringMagazineAmmo()
        {
            return String.valueOf(this.iMagazineAmmo);
        }

        public final String getCurrentAmmoStringTotalAmmo( AmmoSet ammoSet )
        {
            return String.valueOf( ammoSet.getAmmo( ( (FireArm) this.iArtefactType.iArtefactKind ).iAmmoType ) );
        }

        public void reload( AmmoSet ammoSet, boolean reloadAnimationRequired, boolean playSound, Point2D.Float reloaderXY )
        {
            //if ammo to reload is available
            if ( ammoSet.getAmmo( ( (FireArm) this.iArtefactType.iArtefactKind ).iAmmoType ) > 0 )
            {
                //if the wearpon is not fully loaded
                if ( ( (FireArm) this.iArtefactType.iArtefactKind ).iMagazineSize != this.iMagazineAmmo)
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
            FireArm fireArm = ( (FireArm) this.iArtefactType.iArtefactKind );

            //put unused ammo back onto the stack!
            ammoSet.addAmmo( fireArm.iAmmoType, this.iMagazineAmmo);

            //check ammo stock
            int ammo = ammoSet.getAmmo( fireArm.iAmmoType );
            int ammoToReload = ( ammo >= fireArm.iMagazineSize ? fireArm.iMagazineSize : ammo );

            if ( freeAmmo ) ammoToReload = fireArm.iMagazineSize;

            //load ammoToReload into the magazine and substract it from the ammo stock
            this.iMagazineAmmo = ammoToReload;
            ammoSet.substractAmmo( fireArm.iAmmoType, ammoToReload );

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
            if (this.iArtefactType.iPickUpItemKind == null ) return null;
            return new ItemToPickUp(this.iArtefactType.iPickUpItemKind, this, ank.x, ank.y, ank.z, LibMath.getRandom( 0, 360 ), Lib.Rotating.ENo );
        }

        /***************************************************************************************************************
        *   Draws a 3D model of the current holding wearpon in front of the player's eye.
        ***************************************************************************************************************/
/*
        public final void draw3D( GL gl )
        {
            //float modX = Player.getWalkingAngle2Modifier()  / 20;
            //float modY = -Player.getWalkingAngle3Modifier() / 40;

            gl.glLoadIdentity();                        //new identity please
            gl.glNormal3f( 0.0f, 0.0f, 0.0f );          //normalize

            gl.glEnable(  GL.GL_BLEND       );                                  //enable Blending
            gl.glBlendFunc( GL.GL_ONE, GL.GL_ZERO );                             //blend Screen Color With Zero (Black)

            //draw the 3d-shotgun
          //Meshes.shotgun.draw();

            gl.glDisable(   GL.GL_BLEND       );                                //disnable Blending

        }
*/
    }
