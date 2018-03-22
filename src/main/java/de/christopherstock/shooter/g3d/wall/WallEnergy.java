
    package de.christopherstock.shooter.g3d.wall;

    import  java.io.Serializable;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.shooter.io.sound.*;

    /*******************************************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *******************************************************************************************************************/
    public class WallEnergy implements Serializable
    {
        private     static  final   long                serialVersionUID                = 2556328066454482916L;

        protected                   int                 iHealthStart                    = 0;
        protected                   boolean             iDestroyed                      = false;
        protected                   int                 iHealthCurrent                  = 0;
        protected                   float               iDyingTransZ                    = 0.0f;
        protected                   int                 iCurrentDyingTick               = 0;
        protected                   float               iKillAngleHorz                  = 0.0f;
        protected                   FXSize              iExplosionSize                  = null;
        protected                   SoundFg        iExplosionSound                 = null;

        protected WallEnergy( WallHealth aWallHealth, FXSize aExplosionSize, SoundFg aExplosionSound )
        {
            this.iHealthStart = aWallHealth.getHealth();
            this.iHealthCurrent = aWallHealth.getHealth();
            this.iExplosionSize = aExplosionSize;
            this.iExplosionSound = aExplosionSound;
        }

        public void reset()
        {
            this.iHealthCurrent = this.iHealthStart;
        }
    }
