
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
        private LibColors[]         sliverColors            = null;
        private int                 particlesToLaunch       = 0;
        private float               angleMod                = 0.0f;
        private FXGravity           gravity                 = null;
        private float               baseZ                   = 0;

        protected LibFXSliver(LibDebug aDebug, LibVertex aAnchor, LibColors[] aSliverColors, LibParticleQuantity particleQuantity, float aAngleMod, int aLifetime, FXSize aSize, FXGravity aGravity, float aBaseZ, int aFadeOutTicks )
        {
            super( aDebug, aAnchor, aLifetime, aFadeOutTicks );

            this.sliverColors = aSliverColors;
            this.angleMod = aAngleMod;
            this.size = aSize;
            this.gravity = aGravity;
            this.baseZ = aBaseZ;

            switch ( particleQuantity )
            {
                case ETiny:
                {
                    this.particlesToLaunch = LibMath.getRandom( 1, 3 );
                    break;
                }

                case ELow:
                {
                    this.particlesToLaunch = LibMath.getRandom( 3, 6 );
                    break;
                }

                case EMedium:
                {
                    this.particlesToLaunch = LibMath.getRandom( 6, 12 );
                    break;
                }

                case EHigh:
                {
                    this.particlesToLaunch = LibMath.getRandom( 10, 20 );
                    break;
                }

                case EMassive:
                {
                    this.particlesToLaunch = LibMath.getRandom( 15, 30 );
                    break;
                }
            }
        }

        protected final void launch( float angle )
        {
            for (int i = 0; i < this.particlesToLaunch; ++i )
            {
                float radius    = 0.01f * LibMath.getRandom( 1, 30 );
                float angleMod  = ( i * this.angleMod * 2 / this.particlesToLaunch) - this.angleMod;

                LibFXManager.start
                (
                    new LibFXPoint
                    (
                            this.debug,
                            this.baseZ,
                        FXType.ESliver,
                            this.sliverColors[ LibMath.getRandom( 0, this.sliverColors.length - 1 ) ],
                        angle + angleMod,
                            this.anchor.x + radius * LibMath.sinDeg( angle ),
                            this.anchor.y + radius * LibMath.cosDeg( angle ),
                            this.anchor.z,
                            this.size,
                        0,
                            this.lifetime,
                            this.gravity,
                            this.fadeOutTicks,
                        null //new Sprite( Others.ESprite1, new LibVertex( iPoint.x, iPoint.y, iPoint.z ), LibScalation.ELowerThreeQuarters, WallCollidable.ENo, WallTex.ESliver1 )
                    )
                );
            }
        }
    }
