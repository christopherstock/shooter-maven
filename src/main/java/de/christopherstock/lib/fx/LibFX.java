
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;

    /*******************************************************************************************************************
    *   The effect system.
    *******************************************************************************************************************/
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

        protected                   LibDebug            debug                       = null;
        protected                   LibVertex           anchor                      = null;
        protected                   int                 lifetime                    = 0;
        protected                   int                 fadeOutTicks                = 0;
        protected                   FXSize              size                        = null;
        protected                   FXTime              time                        = null;

        protected LibFX(LibDebug debug, LibVertex anchor, int lifetime, int fadeOutTicks)
        {
            this.debug        = debug;
            this.anchor       = anchor;
            this.lifetime     = lifetime;
            this.fadeOutTicks = fadeOutTicks;
        }
    }
