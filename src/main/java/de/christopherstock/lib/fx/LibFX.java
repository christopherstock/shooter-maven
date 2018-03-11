/*  $Id: LibFX.java 1250 2013-01-02 21:00:46Z jenetic.bytemare@gmail.com $
 *  =======================================================================================
 */
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;

    /**************************************************************************************
    *   The effect system.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract class LibFX
    {
        public static enum FXSize
        {
            ESmall,
            EMedium,
            ELarge,
        }

        public static enum FXTime
        {
            EShort,
            EMedium,
            ELong,
        }

        public static enum FXType
        {
            EStaticDebugPoint,
            EExplosion,
            ESliver,
        }

        public static enum FXGravity
        {
            ELow,
            ENormal,
            EHigh,
        }

        @SuppressWarnings( "unused" )
        private     static  final   int                 MAX_FX                      = 0;

        protected                   int                 iFadeOutTicks               = 0;
        protected                   LibDebug            iDebug                      = null;
        protected                   LibVertex           iAnchor                     = null;
        protected                   FXSize              iSize                       = null;
        protected                   FXTime              iTime                       = null;
        protected                   int                 iLifetime                   = 0;

        public LibFX( LibDebug aDebug, LibVertex aAnchor, int aLifetime, int aFadeOutTicks )
        {
            iDebug          = aDebug;
            iAnchor         = aAnchor;
            iLifetime       = aLifetime;
            iFadeOutTicks   = aFadeOutTicks;
        }
    }
