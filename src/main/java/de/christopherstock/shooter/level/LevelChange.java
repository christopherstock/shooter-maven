
    package de.christopherstock.shooter.level;

    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.level.setup.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   Specifies all tasks for changing a level.
    *******************************************************************************************************************/
    public class LevelChange
    {
        private         static          LevelSetup              levelMainToChangeTo             = null;
        private         static          int                     levelSectionIndexToChangeTo     = -1;
        private         static          boolean                 resetOnLevelChange              = false;
        private         static          long                    levelChangeBlocker              = 0;

        public static void orderLevelChange( LevelSetup newLevelMain, int newLevelSectionIndex, boolean reset )
        {
            levelMainToChangeTo         = newLevelMain;
            levelSectionIndexToChangeTo = newLevelSectionIndex;
            resetOnLevelChange          = reset;
        }

        public static void checkChangeToSection()
        {
            if ( levelSectionIndexToChangeTo != -1 )
            {
                if ( levelChangeBlocker <= System.currentTimeMillis() )
                {
                    //change to if possible
                    ShooterDebug.level.out( "change level to [" + levelSectionIndexToChangeTo + "]" );

                    //reset new level if desired
                    if ( resetOnLevelChange )
                    {
                        //init all level data and game levels
                        LevelCurrent.init( levelMainToChangeTo );
                        Level.init();

                        //disable all hud fx
                        Shooter.game.engine.hudFx.disableAllFx();

                        //start according bg music
                        if ( !ShooterDebug.DISABLE_SOUNDS && !ShooterDebug.DEBUG_MODE )
                        {
                            Shooter.game.engine.sound.startBgSound( LevelCurrent.currentLevelConfig.bgSound);
                        }
                    }

                    //show HUD message
                    Shooter.game.engine.hudMessagesManager.showMessage( "Changing to level section [" + LevelCurrent.currentSectionConfigData[ levelSectionIndexToChangeTo ].descSection + "]" );

                    //change current level - do NOT change to constructor! level is referenced in init() !
                    LevelCurrent.currentSection = LevelCurrent.currentSections[ levelSectionIndexToChangeTo ];

                    //call this AFTER initing level data !!
                    LevelCurrent.currentSection.assignWalls();

                    //suppress quick level change
                    levelChangeBlocker = System.currentTimeMillis() + 100;

                    //set no active level change
                    levelSectionIndexToChangeTo = -1;
                }
            }
        }
    }
