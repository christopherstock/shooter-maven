
    package de.christopherstock.shooter.level;

    import  java.util.*;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.level.setup.*;

    /*******************************************************************************************************************
    *   Stores all information for the current active level set.
    *******************************************************************************************************************/
    public abstract class LevelCurrent
    {
        protected   static          LevelConfigMain         currentLevelConfig              = null;
        private     static          WallCollection[]        currentGlobalMeshData           = null;
        private     static          WallCollection[][]      currentSectionMeshData          = null;
        protected   static          LevelConfigSection[]    currentSectionConfigData        = null;

        public      static          LevelSetup              currentLevelMain                = null;
        protected   static          Level[]                 currentSections                 = null;
        protected   static          Level                   currentSection                  = null;

        public static void init( LevelSetup levelToStart )
        {
            //create new instances and store them
            currentLevelConfig          = levelToStart.createNewLevelConfig();
            currentGlobalMeshData       = levelToStart.createNewGlobalMeshData();
            currentSectionMeshData      = levelToStart.createNewSectionMeshData();
            currentSectionConfigData    = levelToStart.createNewSectionConfigData();
        }

        public static WallCollection[] getLevelWalls( int newSectionIndex )
        {
            //pick all walls from global data and current section
            Vector<WallCollection> levelWalls = new Vector<WallCollection>();

            levelWalls.addAll( Arrays.asList( currentGlobalMeshData ) );
            levelWalls.addAll( Arrays.asList( currentSectionMeshData[ newSectionIndex ] ) );

            return levelWalls.toArray( new WallCollection[] {} );
        }
    }
