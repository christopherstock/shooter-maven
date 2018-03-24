
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   The debug system.
    *******************************************************************************************************************/
    class LibFXSliver extends LibFX
    {
        public                      LibColors[]         iSliverColors               = null;
        public                      int                 iParticlesToLaunch          = 0;
        public                      float               iAngleMod                   = 0.0f;
        public                      FXGravity           iGravity                    = null;
        public                      float               iBaseZ                      = 0;

        protected LibFXSliver(LibDebug aDebug, LibVertex aAnchor, LibColors[] aSliverColors, LibParticleQuantity particleQuantity, float aAngleMod, int aLifetime, FXSize aSize, FXGravity aGravity, float aBaseZ, int aFadeOutTicks )
        {
            super( aDebug, aAnchor, aLifetime, aFadeOutTicks );

            this.iSliverColors = aSliverColors;
            this.iAngleMod = aAngleMod;
            this.iSize = aSize;
            this.iGravity = aGravity;
            this.iBaseZ = aBaseZ;

            switch ( particleQuantity )
            {
                case ETiny:
                {
                    this.iParticlesToLaunch = LibMath.getRandom( 1, 3 );
                    break;
                }

                case ELow:
                {
                    this.iParticlesToLaunch = LibMath.getRandom( 3, 6 );
                    break;
                }

                case EMedium:
                {
                    this.iParticlesToLaunch = LibMath.getRandom( 6, 12 );
                    break;
                }

                case EHigh:
                {
                    this.iParticlesToLaunch = LibMath.getRandom( 10, 20 );
                    break;
                }

                case EMassive:
                {
                    this.iParticlesToLaunch = LibMath.getRandom( 15, 30 );
                    break;
                }
            }
        }

        protected final void launch( float angle )
        {
            for (int i = 0; i < this.iParticlesToLaunch; ++i )
            {
                float radius    = 0.01f * LibMath.getRandom( 1, 30 );
                float angleMod  = ( i * this.iAngleMod * 2 / this.iParticlesToLaunch) - this.iAngleMod;

                LibFXManager.start
                (
                    new LibFXPoint
                    (
                            this.iDebug,
                            this.iBaseZ,
                        FXType.ESliver,
                            this.iSliverColors[ LibMath.getRandom( 0, this.iSliverColors.length - 1 ) ],
                        angle + angleMod,
                            this.iAnchor.x + radius * LibMath.sinDeg( angle ),
                            this.iAnchor.y + radius * LibMath.cosDeg( angle ),
                            this.iAnchor.z,
                            this.iSize,
                        0,
                            this.iLifetime,
                            this.iGravity,
                            this.iFadeOutTicks,
                        null //new Sprite( Others.ESprite1, new LibVertex( iPoint.x, iPoint.y, iPoint.z ), LibScalation.ELowerThreeQuarters, WallCollidable.ENo, WallTex.ESliver1 )
                    )
                );
            }
        }
    }
