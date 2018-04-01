
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   The debug system.
    *******************************************************************************************************************/
    class LibFXExplosion extends LibFX
    {
        private static  final   LibColors[]         EXPLOSION_COLORS                = new LibColors[]
        {
            LibColors.EExplosion1,  LibColors.EExplosion2,  LibColors.EExplosion3,
            LibColors.EExplosion4,  LibColors.EExplosion5,  LibColors.EExplosion6,
            LibColors.EExplosion7,  LibColors.EExplosion8,  LibColors.EExplosion9,
            LibColors.EExplosion10, LibColors.EExplosion11, LibColors.EExplosion12,
        };

        private                     float baseZ = 0;

        protected LibFXExplosion( LibDebug debug, LibVertex anchor, FXSize size, FXTime time, int lifetime, float baseZ, int fadeOutTicks )
        {
            super( debug, anchor, lifetime, fadeOutTicks );

            this.size  = size;
            this.time  = time;
            this.baseZ = baseZ;
        }

        protected final void launch()
        {
            int numParticlesPerWave = 0;
            int numWaves            = 0;
            switch ( this.size )
            {
                case ESmall:
                {
                    numParticlesPerWave = 25 + LibMath.getRandom( -5, 10 );
                    break;
                }

                case EMedium:
                {
                    numParticlesPerWave = 50 + LibMath.getRandom( -5, 15 );
                    break;
                }

                case ELarge:
                {
                    numParticlesPerWave = 100 + LibMath.getRandom( -5, 20 );
                    break;
                }
            }

            switch (this.time)
            {
                case EShort:
                {
                    numWaves = 5 + LibMath.getRandom( -2, 2 );
                    break;
                }

                case EMedium:
                {
                    numWaves = 10 + LibMath.getRandom( -2, 4 );
                    break;
                }

                case ELong:
                {
                    numWaves = 15 + LibMath.getRandom( -2, 6 );
                    break;
                }
            }

            int angleSteps = 360 / numParticlesPerWave;

            for ( int wave  = 0; wave < numWaves; ++wave )
            {
                for ( int angle = 0; angle < 360; angle += angleSteps )
                {
                    float radius = 0.01f * LibMath.getRandom( 1, 16 );
                    LibFXManager.start
                    (
                        new LibFXPoint
                        (
                            this.debug,
                            this.baseZ,
                            FXType.EExplosion,
                            EXPLOSION_COLORS[ LibMath.getRandom( 0, EXPLOSION_COLORS.length - 1 ) ],
                            angle,
                            this.anchor.x + radius * LibMath.sinDeg( angle ),
                            this.anchor.y + radius * LibMath.cosDeg( angle ),
                            this.anchor.z,
                            this.size,
                            wave,
                            this.lifetime,
                            FXGravity.ENormal,
                            this.fadeOutTicks,
                            null //new Sprite( Others.ESprite1, new LibVertex( iPoint.x, iPoint.y, iPoint.z ), LibScalation.ELowerThreeQuarters, WallCollidable.ENo, WallTex.ESliver1 )
                        )
                    );
                }
            }
        }
    }
