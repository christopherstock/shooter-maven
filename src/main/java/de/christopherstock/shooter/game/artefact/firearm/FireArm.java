
    package de.christopherstock.shooter.game.artefact.firearm;

    import  java.awt.geom.Point2D;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.ShotSpender;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.d3ds.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactKind;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;

    /*******************************************************************************************************************
    *   A Wearpons that uses ammo.
    *******************************************************************************************************************/
    public final class FireArm extends ArtefactKind
    {
        public                      SoundFg             iSoundFire                      = null;
        private                     SoundFg             iSoundReload                    = null;
        private                     SoundFg             iSoundBulletShell               = null;
        public                      AmmoType            iAmmoType                       = null;
        private                     int                 iWearponIrregularityVert        = 0;
        private                     int                 iWearponIrregularityHorz        = 0;
        private                     int                 iShotCount                      = 0;
        private                     int                 iShotCountRandomMod             = 0;
        public                      int                 iMagazineSize                   = 0;

        public                      LibD3dsFile         iProjectile                     = null;
        
        public FireArm( AmmoType aAmmoType, int aMagazineSize, int aWearponIrregularityDepth, int aWearponIrregularityAngle, int aShotCount, int aShotCountRandomMod, SoundFg aUseSound, SoundFg aReloadSound, SoundFg aBulletShellSound, LibD3dsFile aProjectile )
        {
            this.iMagazineSize = aMagazineSize;
            this.iAmmoType = aAmmoType;
            this.iWearponIrregularityVert = aWearponIrregularityDepth;
            this.iWearponIrregularityHorz = aWearponIrregularityAngle;
            this.iShotCount = aShotCount;
            this.iShotCountRandomMod = aShotCountRandomMod;
            this.iSoundFire = aUseSound;
            this.iSoundReload = aReloadSound;
            this.iSoundBulletShell = aBulletShellSound;
            this.iProjectile = aProjectile;
        }

        @Override
        public int getDamage()
        {
            return this.iAmmoType.getDamage();
        }

        @Override
        public boolean use( Artefact a, ShotSpender ss, Point2D.Float shooterXY )
        {
            //ShooterDebug.bugfix.out( "Fire FireArm .." );

            //only fire if magazine is not empty
            if ( a.iMagazineAmmo > 0 )
            {
                //ShooterDebug.bugfix.out( "No ammo!" );

                //reduce magazine-ammo by 1
                a.iMagazineAmmo -= 1;

                //launch shot-sound-fx
                if (this.iSoundFire != null )
                {
                    if ( shooterXY == null )
                    {
                        this.iSoundFire.playGlobalFx();
                    }
                    else
                    {
                        this.iSoundFire.playDistancedFx( shooterXY );
                    }
                }

                //launch bullet shell sound-fx
                if (this.iSoundBulletShell != null )
                {
                    if ( shooterXY == null )
                    {
                        this.iSoundBulletShell.playGlobalFx( 8 );
                    }
                    else
                    {
                        this.iSoundBulletShell.playDistancedFx( shooterXY, 8 );
                    }
                }

                //launch number of shots this gun fires
                int shotsToFire = this.getCurrentShotCount();
                for ( int i = 0; i < shotsToFire; ++i )
                {
                    //clear all debug fx points before firing!
                    //LibFXManager.clearDebugPoints();

                    //get shot from player and launch it
                    LibShot s = ss.getShot( 0.0f );
                    //draw the shot line and launch the shot
                    //s.drawShotLine( FxSettings.LIFETIME_DEBUG );
                    Level.currentSection().launchShot( s );
                }

                return true;
            }

            return false;
        }

        public final int getCurrentShotCount()
        {
            return this.iShotCount + LibMath.getRandom( -this.iShotCountRandomMod, this.iShotCountRandomMod);
        }

        public final float getCurrentIrregularityVert()
        {
            //return modifier-z for the current wearpon
            return ( LibMath.getRandom( -this.iWearponIrregularityVert, this.iWearponIrregularityVert) * 0.01f );
        }

        public final float getCurrentIrregularityHorz()
        {
            //return modifier-z for the current wearpon
            return ( LibMath.getRandom( -this.iWearponIrregularityHorz, this.iWearponIrregularityHorz) * 0.01f );
        }

        public final LibGLImage getAmmoTypeImage()
        {
            return this.iAmmoType.getImage();
        }

        @Override
        public final Lib.ParticleQuantity getSliverParticleQuantity()
        {
            return this.iAmmoType.iSliverParticleQuantity;
        }

        @Override
        public final FXSize getSliverParticleSize()
        {
            return this.iAmmoType.iSliverParticleSize;
        }

        @Override
        public final LibHoleSize getBulletHoleSize()
        {
            return this.iAmmoType.iBulletHoleSize;
        }

        public final SoundFg getReloadSound()
        {
            return this.iSoundReload;
        }
        
        public final LibD3dsFile getProjectile()
        {
            return this.iProjectile;
        }
    }
