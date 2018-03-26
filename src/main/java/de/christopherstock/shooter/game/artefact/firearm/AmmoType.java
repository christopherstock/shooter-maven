
    package de.christopherstock.shooter.game.artefact.firearm;

    import  java.awt.image.BufferedImage;

    import de.christopherstock.lib.LibParticleQuantity;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.LibHoleSize;
    import  de.christopherstock.lib.gl.LibGLImage;
    import  de.christopherstock.lib.gl.LibGLImage.ImageUsage;
    import  de.christopherstock.lib.io.LibExtension;
    import  de.christopherstock.lib.ui.LibImage;
    import  de.christopherstock.shooter.ShooterDebug;
    import de.christopherstock.shooter.ShooterSetting;

    /*******************************************************************************************************************
    *   The type of ammunition.
    *******************************************************************************************************************/
    public enum AmmoType
    {
        /** for the 4.4mm                       */  EBullet44mm(        LibHoleSize.E44mm,     200,     LibParticleQuantity.ELow,     10,  FXSize.ESmall  ),
        /** for the assault rifle 5.1mm         */  EBullet51mm(        LibHoleSize.E51mm,     400,     LibParticleQuantity.EMedium,  12,  FXSize.ESmall  ),
        /** for the assault rifle 7.92mm        */  EBullet792mm(       LibHoleSize.E79mm,     400,     LibParticleQuantity.EHigh,    14,  FXSize.EMedium ),
        /** for the 9mm                         */  EBullet9mm(         LibHoleSize.E9mm,      200,     LibParticleQuantity.EMassive, 16,  FXSize.EMedium ),
        /** for the shotgun and auto-shotgun    */  EShotgunShells(     LibHoleSize.EHuge,     100,     LibParticleQuantity.EMassive, 8,   FXSize.ELarge  ),
        /** for the magnum 3.57mm               */  EMagnumBullet357(   LibHoleSize.EHuge,     200,     LibParticleQuantity.EMassive, 20,  FXSize.ELarge  ),
        /** for the tranquilizer gun            */  ETranquilizerDarts( LibHoleSize.E79mm,     60,      LibParticleQuantity.EMedium,  0,   FXSize.EMedium ),

        /** for the flamer                      */  EFlamerGas(         LibHoleSize.E51mm,     5,       LibParticleQuantity.ELow,     10,  FXSize.ELarge  ),
        /** for the grenade launcher            */  EGrenadeRolls(      LibHoleSize.EHuge,     12,      LibParticleQuantity.EHigh,    30,  FXSize.ELarge  ),
        /** Pipebombs                           */  EPipeBombs(         LibHoleSize.E51mm,     10,      LibParticleQuantity.EMassive, 40,  FXSize.ELarge  ),
        ;

        protected       LibHoleSize             iBulletHoleSize         = null;
        protected       FXSize                  iSliverParticleSize     = null;
        protected       int                     iMaxAmmo                = 0;
        protected LibParticleQuantity iSliverParticleQuantity = null;
        private LibGLImage              iHUDAmmoImage           = null;
        private int                     iDamage                 = 0;

        private AmmoType(LibHoleSize aBulletHoleSize, int aMaxAmmo, LibParticleQuantity aSliverParticleQuantity, int aDamage, FXSize aSliverParticleSize )
        {
            this.iBulletHoleSize = aBulletHoleSize;
            this.iMaxAmmo = aMaxAmmo;
            this.iSliverParticleQuantity = aSliverParticleQuantity;
            this.iDamage = aDamage;
            this.iSliverParticleSize = aSliverParticleSize;
        }

        protected final void loadImage()
        {
            BufferedImage bufferedImage = LibImage.load( ShooterSetting.Path.EShells.url + this.toString() + LibExtension.png.getSpecifier(), ShooterDebug.glImage, false );
            this.iHUDAmmoImage = new LibGLImage( bufferedImage, ImageUsage.EOrtho, ShooterDebug.glImage, true );
        }

        protected final LibGLImage getImage()
        {
            return this.iHUDAmmoImage;
        }

        protected final int getDamage()
        {
            return this.iDamage;
        }
    }
