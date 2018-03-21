
    package de.christopherstock.shooter.level.setup;

    import  de.christopherstock.shooter.g3d.mesh.WallCollection;
    import  de.christopherstock.shooter.level.*;

    /**************************************************************************************
    *   All settings for all levels the game consists of.
    **************************************************************************************/
    public abstract class LevelSetup
    {
        protected           float       iOffsetX                = 0.0f;
        protected           float       iOffsetY                = 0.0f;
        protected           float       iOffsetZ                = 0.0f;

        public abstract LevelConfigMain createNewLevelConfig();

        public abstract WallCollection[] createNewGlobalMeshData();

        public abstract WallCollection[][] createNewSectionMeshData();

        public abstract LevelConfigSection[] createNewSectionConfigData();
    }
