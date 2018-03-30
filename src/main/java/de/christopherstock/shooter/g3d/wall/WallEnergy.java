
    package de.christopherstock.shooter.g3d.wall;

    import  java.io.Serializable;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.shooter.io.sound.*;

    /*******************************************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *******************************************************************************************************************/
    public class WallEnergy implements Serializable
    {
        protected                   int                 healthStart             = 0;
        protected                   boolean             destroyed               = false;
        protected                   int                 healthCurrent           = 0;
        protected                   float               dyingTransZ             = 0.0f;
        protected                   int                 currentDyingTick        = 0;
        protected                   float               killAngleHorz           = 0.0f;
        protected                   FXSize              explosionSize           = null;
        protected                   SoundFg             explosionSound          = null;

        protected WallEnergy( WallHealth wallHealth, FXSize explosionSize, SoundFg explosionSound )
        {
            this.healthStart    = wallHealth.getHealth();
            this.healthCurrent  = wallHealth.getHealth();
            this.explosionSize  = explosionSize;
            this.explosionSound = explosionSound;
        }

        public void reset()
        {
            this.healthCurrent = this.healthStart;
        }
    }
