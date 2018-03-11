/*  $Id: LibFXSliver.java 1242 2013-01-02 15:32:27Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /**************************************************************************************
    *   The debug system.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    class LibFXSliver extends LibFX
    {
        public                      LibColors[]         iSliverColors               = null;
        public                      int                 iParticlesToLaunch          = 0;
        public                      float               iAngleMod                   = 0.0f;
        public                      FXGravity           iGravity                    = null;
        public                      float               iBaseZ                      = 0;

        protected LibFXSliver( LibDebug aDebug, LibVertex aAnchor, LibColors[] aSliverColors, Lib.ParticleQuantity particleQuantity, float aAngleMod, int aLifetime, FXSize aSize, FXGravity aGravity, float aBaseZ, int aFadeOutTicks )
        {
            super( aDebug, aAnchor, aLifetime, aFadeOutTicks );

            iSliverColors   = aSliverColors;
            iAngleMod       = aAngleMod;
            iSize           = aSize;
            iGravity        = aGravity;
            iBaseZ          = aBaseZ;

            switch ( particleQuantity )
            {
                case ETiny:
                {
                    iParticlesToLaunch = LibMath.getRandom( 1, 3 );
                    break;
                }

                case ELow:
                {
                    iParticlesToLaunch = LibMath.getRandom( 3, 6 );
                    break;
                }

                case EMedium:
                {
                    iParticlesToLaunch = LibMath.getRandom( 6, 12 );
                    break;
                }

                case EHigh:
                {
                    iParticlesToLaunch = LibMath.getRandom( 10, 20 );
                    break;
                }

                case EMassive:
                {
                    iParticlesToLaunch = LibMath.getRandom( 15, 30 );
                    break;
                }
            }
        }

        protected final void launch( float angle )
        {
            for ( int i = 0; i < iParticlesToLaunch; ++i )
            {
                float radius    = 0.01f * LibMath.getRandom( 1, 30 );
                float angleMod  = ( i * iAngleMod * 2 / iParticlesToLaunch ) - iAngleMod;

                LibFXManager.start
                (
                    new LibFXPoint
                    (
                        iDebug,
                        iBaseZ,
                        FXType.ESliver,
                        iSliverColors[ LibMath.getRandom( 0, iSliverColors.length - 1 ) ],
                        angle + angleMod,
                        iAnchor.x + radius * LibMath.sinDeg( angle ),
                        iAnchor.y + radius * LibMath.cosDeg( angle ),
                        iAnchor.z,
                        iSize,
                        0,
                        iLifetime,
                        iGravity,
                        iFadeOutTicks,
                        null //new Sprite( Others.ESprite1, new LibVertex( iPoint.x, iPoint.y, iPoint.z ), Scalation.ELowerThreeQuarters, WallCollidable.ENo, WallTex.ESliver1 )
                    )
                );
            }
        }
    }
