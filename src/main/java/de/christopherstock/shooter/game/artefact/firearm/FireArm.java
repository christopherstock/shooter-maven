
    package de.christopherstock.shooter.game.artefact.firearm;

    import  java.awt.geom.Point2D;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.ShotSource;
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
        private                     SoundFg             soundFire               = null;
        private                     SoundFg             soundReload             = null;
        private                     SoundFg             soundBulletShell        = null;
        public                      AmmoType            ammoType                = null;
        private                     int                 wearponIrregularityVert = 0;
        private                     int                 wearponIrregularityHorz = 0;
        private                     int                 shotCount               = 0;
        private                     int                 shotCountRandomMod      = 0;
        public                      int                 magazineSize            = 0;

        private                     LibD3dsFile         projectile              = null;
        
        public FireArm( AmmoType ammoType, int magazineSize, int wearponIrregularityDepth, int wearponIrregularityAngle, int shotCount, int shotCountRandomMod, SoundFg useSound, SoundFg reloadSound, SoundFg bulletShellSound, LibD3dsFile projectile )
        {
            this.magazineSize = magazineSize;
            this.ammoType = ammoType;
            this.wearponIrregularityVert = wearponIrregularityDepth;
            this.wearponIrregularityHorz = wearponIrregularityAngle;
            this.shotCount = shotCount;
            this.shotCountRandomMod = shotCountRandomMod;
            this.soundFire = useSound;
            this.soundReload = reloadSound;
            this.soundBulletShell = bulletShellSound;
            this.projectile = projectile;
        }

        @Override
        public int getDamage()
        {
            return this.ammoType.getDamage();
        }

        @Override
        public boolean use( Artefact a, ShotSource ss, Point2D.Float shooterXY )
        {
            //ShooterDebug.bugfix.out( "Fire FireArm .." );

            //only fire if magazine is not empty
            if ( a.magazineAmmo > 0 )
            {
                //ShooterDebug.bugfix.out( "No ammo!" );

                //reduce magazine-ammo by 1
                a.magazineAmmo -= 1;

                //launch shot-sound-fx
                if (this.soundFire != null )
                {
                    if ( shooterXY == null )
                    {
                        this.soundFire.playGlobalFx();
                    }
                    else
                    {
                        this.soundFire.playDistancedFx( shooterXY );
                    }
                }

                //launch bullet shell sound-fx
                if (this.soundBulletShell != null )
                {
                    if ( shooterXY == null )
                    {
                        this.soundBulletShell.playGlobalFx( 8 );
                    }
                    else
                    {
                        this.soundBulletShell.playDistancedFx( shooterXY, 8 );
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

        private int getCurrentShotCount()
        {
            return this.shotCount + LibMath.getRandom( -this.shotCountRandomMod, this.shotCountRandomMod);
        }

        public final float getCurrentIrregularityVert()
        {
            //return modifier-z for the current wearpon
            return ( LibMath.getRandom( -this.wearponIrregularityVert, this.wearponIrregularityVert) * 0.01f );
        }

        public final float getCurrentIrregularityHorz()
        {
            //return modifier-z for the current wearpon
            return ( LibMath.getRandom( -this.wearponIrregularityHorz, this.wearponIrregularityHorz) * 0.01f );
        }

        public final LibGLTextureImage getAmmoTypeImage()
        {
            return this.ammoType.getImage();
        }

        @Override
        public final LibParticleQuantity getSliverParticleQuantity()
        {
            return this.ammoType.sliverParticleQuantity;
        }

        @Override
        public final FXSize getSliverParticleSize()
        {
            return this.ammoType.sliverParticleSize;
        }

        @Override
        public final LibHoleSize getBulletHoleSize()
        {
            return this.ammoType.bulletHoleSize;
        }

        public final SoundFg getReloadSound()
        {
            return this.soundReload;
        }
        
        public final LibD3dsFile getProjectile()
        {
            return this.projectile;
        }
    }
