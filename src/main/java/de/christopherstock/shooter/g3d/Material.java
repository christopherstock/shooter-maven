
    package de.christopherstock.shooter.g3d;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.hid.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.ShooterSetting.Colors;
    import  de.christopherstock.shooter.base.ShooterTexture.BulletHoleTex;
    import  de.christopherstock.shooter.io.sound.*;

    /*******************************************************************************************************************
    *   Different materials that can be assigned for texture surfaces.
    *******************************************************************************************************************/
    public enum Material implements LibMaterial
    {
        EBrownBricks(       false,      Colors.SLIVER_COLOR_BROWN_BRICKS,   SoundFg.EWallSolid1     ),
        EConcrete(          false,      Colors.SLIVER_COLOR_WALL,           SoundFg.EWallSolid1     ),
        EElectricDevice(    false,      Colors.SLIVER_COLOR_GLASS,          SoundFg.EWallElectric1  ),
        EGlass(             true ,      Colors.SLIVER_COLOR_GLASS,          SoundFg.EWallGlass1     ),
        EHumanFlesh(        false,      Colors.SLIVER_COLOR_BLOOD,          SoundFg.EWallFlesh1     ),
        EPlastic1(          false,      Colors.SLIVER_COLOR_GLASS,          SoundFg.EWallSolid1     ),
        ERedBricks(         false,      Colors.SLIVER_COLOR_RED_BRICKS,     SoundFg.EWallSolid1     ),
        ESteel1(            false,      Colors.SLIVER_COLOR_STEEL,          SoundFg.EWallSolid1     ),
        ESteel2(            false,      Colors.SLIVER_COLOR_STEEL,          SoundFg.EWallSolid1     ),
        EUndefined(         false,      Colors.SLIVER_COLOR_WALL,           SoundFg.EWallSolid1     ),
        EWood(              false,      Colors.SLIVER_COLOR_WALL,           SoundFg.EWallWood1      ),
        ;

        private     boolean         penetrable              = false;
        private     LibColors[]     sliverColors            = null;
        private     SoundFg         bulletImpactSound       = null;

        private Material( boolean penetrable, LibColors[] sliverColors, SoundFg bulletSound )
        {
            this.penetrable        = penetrable;
            this.sliverColors      = sliverColors;
            this.bulletImpactSound = bulletSound;
        }

        /***************************************************************************************************************
        *   Don't move to the constructor - Circle reference in {@link BulletHoleTex}.
        ***************************************************************************************************************/
        public LibTexture getBulletHoleTexture()
        {
            switch ( this )
            {
                case EElectricDevice:   return BulletHoleTex.EBulletHoleGlass1;
                case EBrownBricks:      return BulletHoleTex.EBulletHoleBrownBricks1;
                case EConcrete:         return BulletHoleTex.EBulletHoleConcrete1;
                case EGlass:            return BulletHoleTex.EBulletHoleGlass1;
                case EHumanFlesh:       return BulletHoleTex.EBulletHoleSteel1;
                case EPlastic1:         return BulletHoleTex.EBulletHolePlastic1;
                case ERedBricks:        return BulletHoleTex.EBulletHoleConcrete1;
                case ESteel1:           return BulletHoleTex.EBulletHoleSteel1;
                case ESteel2:           return BulletHoleTex.EBulletHoleSteel2;
                case EUndefined:        return BulletHoleTex.EBulletHoleSteel1;
                case EWood:             return BulletHoleTex.EBulletHoleWood1;
            }

            return null;
        }

        public LibColors[] getSliverColors()
        {
            return this.sliverColors;
        }

        public boolean isPenetrable()
        {
            return this.penetrable;
        }

        public LibSound getBulletImpactSound()
        {
            return this.bulletImpactSound;
        }
    }
