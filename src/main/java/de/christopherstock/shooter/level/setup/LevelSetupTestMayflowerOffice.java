
    package de.christopherstock.shooter.level.setup;

    import  de.christopherstock.lib.LibInvert;
    import  de.christopherstock.lib.LibScalation;
    import  de.christopherstock.lib.LibViewSet;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.base.ShooterD3ds.Others;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;
    import  de.christopherstock.shooter.base.ShooterWallCollection.*;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.gl.LibTexture;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.g3d.mesh.WallCollection;
    import  de.christopherstock.shooter.g3d.wall.*;
    import  de.christopherstock.shooter.g3d.wall.Wall.*;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.bot.*;
    import  de.christopherstock.shooter.game.bot.BotFactory.*;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.level.Level.*;

    /*******************************************************************************************************************
    *   All settings for level 'Test Facility'.
    *******************************************************************************************************************/
    public class LevelSetupTestMayflowerOffice extends LevelSetup
    {
        private     static      final   WallTex     WALL_OFFICE                 = WallTex.EBricks2;
        private     static      final   WallTex     CARPET_OFFICE               = WallTex.ECarpet2;

        public      static      final   int         SECTION_ONE                 = 0;
        public      static      final   int         SECTION_TWO                 = 1;
        public      static      final   int         SECTION_THREE               = 2;
        public      static      final   int         SECTION_FOUR                = 3;

        public      static      final   int         OFFICE_PARTNER_1            = 0;
        private     static      final   int         OFFICE_PARTNER_2            = 1;

        @Override
        public final LevelConfigMain createNewLevelConfig()
        {
            return new LevelConfigMain
            (
                "Test Facility",
                new LibViewSet( 2.0f, 2.0f, 2.5f, 0.0f, 0.0f, 185.0f ),
                new ArtefactType[]   {  ArtefactType.ETranquilizerGun,  ArtefactType.EWaltherPPK,  ArtefactType.EMagnum357, ArtefactType.EAutoShotgun, ArtefactType.ESpaz12,  ArtefactType.ERCP180,  ArtefactType.ESniperRifle, ArtefactType.EAdrenaline, },
                new ItemEvent[]  {  ItemEvent.EGainAmmo20TranquilizerDarts, ItemEvent.EGainAmmo20TranquilizerDarts, ItemEvent.EGainAmmo20TranquilizerDarts, ItemEvent.EGainAmmo20TranquilizerDarts, ItemEvent.EGainAmmo20Bullet44mm,  ItemEvent.EGainAmmo20Bullet44mm, ItemEvent.EGainAmmo20Bullet44mm,ItemEvent.EGainAmmo20Bullet44mm,ItemEvent.EGainAmmo20Bullet44mm, ItemEvent.EGainAmmo40ShotgunShells, ItemEvent.EGainAmmo40ShotgunShells, ItemEvent.EGainAmmo40ShotgunShells, ItemEvent.EGainAmmo18MagnumBullet, ItemEvent.EGainGadgetHandset1, ItemEvent.EGainGadgetCrackers, ItemEvent.EGainAmmo120Bullet792mm, ItemEvent.EGainAmmo120Bullet792mm, },
                InvisibleZeroLayerZ.ENo,
                SoundBg.EExtraction
            );
        }

        @Override
        public final WallCollection[] createNewGlobalMeshData()
        {
            // globals
            return new WallCollection[]
            {
                //sluices
                ShooterWallCollection.createSluice(     5.0f, 12.0f,   2.5f, 0.0f, CARPET_OFFICE, WallTex.EWood1, WALL_OFFICE, WallTex.ECeiling1, WALL_OFFICE, 2, SECTION_ONE,   SECTION_TWO   ),
                ShooterWallCollection.createSluice(     5.0f, 22.0f,   2.5f, 0.0f, CARPET_OFFICE, WallTex.EWood1, WALL_OFFICE, WallTex.ECeiling1, WALL_OFFICE, 2, SECTION_TWO,   SECTION_THREE ),
              //ShooterWallCollection.createSluice(     5.0f, 42.0f,   2.5f, 0.0f, CARPET_OFFICE, WallTex.EWood1, WALL_OFFICE, WallTex.ECeiling1, WALL_OFFICE, 2, SECTION_THREE, SECTION_FOUR  ),

                //elevator
                ShooterWallCollection.createElevator(   5.0f, 42.0f,   2.5f, 0.0f, WallTex.EMarble1, WallTex.EWood1, WallAction.EElevatorDown, WallTex.EBricks2, WallTex.ECeiling1, SECTION_THREE, SECTION_FOUR ),

                //ground
                //ShooterWallCollection.createGround( WALL_OFFICE, 0.0f ), // -0.01f ),
            };
        }

        @Override
        public final LevelConfigSection[] createNewSectionConfigData()
        {
            return new LevelConfigSection[]
            {
                //player's office
                new LevelConfigSection
                (
                    "Test Facility - 1st section",
                    LibColors.EWhite,
                    new ItemToPickUp[]
                    {
                        //new ItemToPickUp( ItemKind.EGameEventLevel1ChangeToNextSection, null, 10.0f, 5.0f, 0.0f, 0.0f, LibRotating.ENo ),
                    },
                    new BotFactory[]
                    {
                        new BotFactory( OFFICE_PARTNER_1, BotKind.EUnitPilot,          new LibVertex( 5.0f,  1.0f, 2.5f ), 225.0f, new Bot.BotAction[] { /* new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) */ } ),
                        new BotFactory( OFFICE_PARTNER_2, BotKind.EUnitOfficeEmployee, new LibVertex( 2.5f,  6.0f, 2.5f ), 0.0f,   new Bot.BotAction[] { new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) } ),
                    }
                ),

                //storage
                new LevelConfigSection
                (
                    "Test Facility - 2nd section",
                    LibColors.EWhite,
                    new ItemToPickUp[]
                    {
                    },
                    new BotFactory[]
                    {
                        new BotFactory( OFFICE_PARTNER_1, BotKind.EUnitSecurity,          new LibVertex( 8.75f, 18.0f, 2.5f ), 135.0f, new Bot.BotAction[] { /* new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) */ } ),
                    }
                ),

                //casino
                new LevelConfigSection
                (
                    "Test Facility - 3rd section",
                    LibColors.EWhite,
                    new ItemToPickUp[]
                    {
                    },
                    new BotFactory[]
                    {
                        new BotFactory( OFFICE_PARTNER_1, BotKind.EUnitSpecialForces,          new LibVertex( 9.0f, 35.0f, 2.5f ), 135.0f, new Bot.BotAction[] { /* new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) */ } ),
                        new BotFactory( OFFICE_PARTNER_1, BotKind.EUnitSpecialForces,          new LibVertex( 9.0f, 29.0f, 2.5f ), 135.0f, new Bot.BotAction[] { /* new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) */ } ),
                    }
                ),

                //basement hallway
                new LevelConfigSection
                (
                    "Test Facility - 4th section",
                    LibColors.EWhite,
                    new ItemToPickUp[]
                    {
                    },
                    new BotFactory[]
                    {
                        new BotFactory( OFFICE_PARTNER_2, BotKind.EUnitOfficeEmployee, new LibVertex( 7.0f,  25.0f, 0.0f ), 180.0f, new Bot.BotAction[] { new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) } ),
                        new BotFactory( OFFICE_PARTNER_1, BotKind.EUnitSpecialForces,  new LibVertex( 9.0f,  29.0f, 0.0f ), 190.0f, new Bot.BotAction[] { /* new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) */ } ),
                        new BotFactory( OFFICE_PARTNER_1, BotKind.EUnitSecurity,       new LibVertex( 6.5f,  33.0f, 0.0f ), 185.0f, new Bot.BotAction[] { /* new Bot.BotUseAction( BotEvent.ELevel1AcclaimPlayer ), new Bot.BotGiveAction( ArtefactType.EMobilePhoneSEW890i, BotEvent.ETakeMobileTest ), new Bot.BotGiveAction( ArtefactType.EChips, BotEvent.ETakeCrackersTest ) */ } ),
                    }
                ),
            };
        }

        @Override
        public final WallCollection[][] createNewSectionMeshData()
        {
            return new WallCollection[][]
            {
                //1st floor - player's office
                new WallCollection[]
                {
                    //player's office
                    ShooterWallCollection.createRoom
                    (
                        0.0f, 0.0f, 2.5f, 0.0f, 8, 12,
                        WallStyle.EWindowsAndCeilingWindows, WallStyle.EWindowsAndCeilingWindows, WallStyle.ESolidWall, WallStyle.EWindowsAndCeilingWindows,
                        WallTex.EGlass1, WallHealth.ESolidGlass, WallAction.EDoorSlideRight,
                        DoorStyle.ENoDoor, 0,
                        WALL_OFFICE, CARPET_OFFICE, WallTex.ECeiling1,
                        new Wall[]
                        {
                            new Wall(   Others.EChairOffice1,   new LibVertex(  1.0f,   1.0f,   0.0f    ), 255.0f,  LibScalation.ENone,        LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ELeather1,     new LibTexture[] { WallTex.EChrome2, },   0, WallHealth.ESolidWood, FXSize.ESmall, null  ),
                            new Sprite( Others.ESprite1,        new LibVertex(  1.25f,  6.5f,   0.0f    ),          LibScalation.EAddHalf,     WallCollidable.EYes, WallTex.EPlant2 ),
                            new Wall(   Others.EWhiteboard1,    new LibVertex(  2.5f,   12.0f,  0.7f    ), 90.0f,   LibScalation.EDouble,      LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster8,  null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                            new Wall(   Others.EPoster1,        new LibVertex(  4.0f,   12.0f,  0.7f    ), 90.0f,   LibScalation.EAddHalf,     LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster4,      null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                            new Wall(   Others.ESofa1,          new LibVertex(  7.0f,   2.0f,   0.0f    ), 0.0f,    LibScalation.ENone,        LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ELeather2,     new LibTexture[] { WallTex.ETest, }, 0, WallHealth.EFurniture, FXSize.ELarge, null  ),
                        },
                        null,
                        null,
                        null,
                        new int[] { 5, }
                    ),

                    //desk office
                    ShooterWallCollection.createDeskOffice( 2.0f, 3.5f, 2.5f, 270.0f ),

                    //desk lab
                    ShooterWallCollection.createDeskLab( 5.0f, 7.5f, 2.5f, 270.0f ),
                },

                //1st floor - storage
                new WallCollection[]
                {
                    //storage
                    ShooterWallCollection.createRoom
                    (
                        4.0f, 14.0f, 2.5f, 0.0f, 8, 8,
                        WallStyle.ESolidWall, WallStyle.EWindowsAndCeilingWindows, WallStyle.ESolidWall, WallStyle.EWindowsAndCeilingWindows,
                        WallTex.EGlass1, WallHealth.ESolidGlass, WallAction.EDoorSlideRight,
                        DoorStyle.ENoDoor, 0,
                        WALL_OFFICE, CARPET_OFFICE, WallTex.ECeiling1,
                        new Wall[]
                        {
                            new Wall(   Others.EPoster1,        new LibVertex(  5.0f,   8.0f,  0.50f   ), 90.0f,   LibScalation.EDouble,     LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster6,      null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                        },
                        null,
                        null,
                        new int[] { 1, },
                        new int[] { 1, }
                    ),

                    //shelves
                    ShooterWallCollection.createShelves( 11.0f, 15.5f, 2.5f, 70.0f  ),
                    ShooterWallCollection.createShelves( 11.0f, 18.0f, 2.5f, 90.0f  ),
                    ShooterWallCollection.createShelves( 11.0f, 20.5f, 2.5f, 100.0f ),

                    //box
                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      5.5f, 16.5f, 2.5f, 100.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.66f, 10.0f, LibScalation.ETriple ), }
                    ),

                    //box
                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      6.5f, 18.5f, 2.5f, 90.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.66f, -10.0f, LibScalation.ETriple ), }
                    ),
                },

                //1st floor - casino
                new WallCollection[]
                {
                    //hallway
                    ShooterWallCollection.createRoom
                    (
                        4.0f, 24.0f, 2.5f, 0.0f, 4, 4,
                        WallStyle.ESolidWall, WallStyle.ESolidWall, WallStyle.ENoWall, WallStyle.ESolidWall,
                        WallTex.EGlass1, WallHealth.ESolidGlass, WallAction.EDoorSlideRight,
                        DoorStyle.ENoDoor, 0,
                        WALL_OFFICE, CARPET_OFFICE, WallTex.ECeiling1,
                        new Wall[]
                        {
                        },
                        null,
                        null,
                        new int[] { 1, },
                        null
                    ),

                    //hallway
                    ShooterWallCollection.createRoom
                    (
                        4.0f, 38.0f, 2.5f, 0.0f, 4, 4,
                        WallStyle.ENoWall, WallStyle.ESolidWall, WallStyle.ESolidWall, WallStyle.ESolidWall,
                        WallTex.EGlass1, WallHealth.ESolidGlass, WallAction.EDoorSlideRight,
                        DoorStyle.ENoDoor, 0,
                        WALL_OFFICE, CARPET_OFFICE, WallTex.ECeiling1,
                        new Wall[]
                        {
                        },
                        null,
                        null,
                        null,
                        new int[] { 1, }
                    ),

                    //casino
                    ShooterWallCollection.createRoom
                    (
                        4.0f, 28.0f, 2.5f, 0.0f, 8, 10,
                        WallStyle.ESolidWall, WallStyle.ESolidWall, WallStyle.ESolidWall, WallStyle.EWindowsAndCeilingWindows,
                        WallTex.EGlass1, WallHealth.ESolidGlass, WallAction.EDoorSlideRight,
                        DoorStyle.ENoDoor, 0,
                        WALL_OFFICE, CARPET_OFFICE, WallTex.ECeiling1,
                        new Wall[]
                        {
                            new Wall(   Others.ESodaMachine1,   new LibVertex( 6.5f, 0.3f, 0.0f ), 270.0f, LibScalation.ENone,   LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ESodaMachine2, null, 0, WallHealth.EVendingMachine, FXSize.ELarge, SoundFg.EExplosion1 ),
                            new Wall(   Others.ESodaMachine1,   new LibVertex( 6.5f, 9.7f, 0.0f ), 90.0f,  LibScalation.ENone,   LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ESodaMachine3, null, 0, WallHealth.EVendingMachine, FXSize.ELarge, SoundFg.EExplosion1 ),

                            new Sprite( Others.ESprite1,        new LibVertex( 5.0f, 0.6f, 0.0f ), LibScalation.EAddThird,   WallCollidable.EYes, WallTex.EPlant1 ),
                            new Sprite( Others.ESprite1,        new LibVertex( 5.0f, 9.4f, 0.0f ), LibScalation.EAddThird,   WallCollidable.EYes, WallTex.EPlant1 ),
                            new Sprite( Others.ESprite1,        new LibVertex( 7.0f, 5.0f, 0.0f ), LibScalation.EAddThird,   WallCollidable.EYes, WallTex.EPlant2 ),

                            new Wall(   Others.ESofa1,          new LibVertex( 7.5f, 3.0f, 0.0f ), 0.0f,   LibScalation.ENone,   LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EClothDarkRed, new LibTexture[] { WallTex.ETest, }, 0, WallHealth.EFurniture,      FXSize.ELarge, null                     ),
                            new Wall(   Others.ESofa1,          new LibVertex( 7.5f, 7.0f, 0.0f ), 0.0f,   LibScalation.ENone,   LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ELeather2,     new LibTexture[] { WallTex.ETest, }, 0, WallHealth.EFurniture,      FXSize.ELarge, null                     ),

                            new Wall(   Others.EPoster1,        new LibVertex(  4.5f,  0.0f,  0.50f   ), -90.0f,   LibScalation.EDouble,     LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster7,      null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                            new Wall(   Others.EPoster1,        new LibVertex(  5.5f,  10.0f,  0.50f   ), 90.0f,   LibScalation.EDouble,     LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster5,      null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),

                            new Wall(   Others.EPoster1,        new LibVertex(  4.0f,  -2.0f,  0.50f   ),  0.0f,   LibScalation.EDouble,     LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster4,      null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                            new Wall(   Others.EPoster1,        new LibVertex(  4.0f,  12.0f,  0.50f   ), 0.0f,   LibScalation.EDouble,     LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster6,      null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                        },
                        null,
                        null,
                        new int[] { 0, 1, 2, },
                        new int[] { 0, 1, 2, }
                    ),
                    ShooterWallCollection.createShelves( 5.0f, 30.0f, 2.5f, 0.0f   ),
                    ShooterWallCollection.createShelves( 7.0f, 30.0f, 2.5f, 10.0f  ),
                    ShooterWallCollection.createShelves( 5.0f, 32.0f, 2.5f, 10.0f  ),
                    ShooterWallCollection.createShelves( 7.0f, 32.0f, 2.5f, 0.0f   ),
                    ShooterWallCollection.createShelves( 5.0f, 34.0f, 2.5f, -10.0f ),
                    ShooterWallCollection.createShelves( 7.0f, 34.0f, 2.5f, 10.0f  ),
                    ShooterWallCollection.createShelves( 5.0f, 36.0f, 2.5f, 0.0f   ),
                    ShooterWallCollection.createShelves( 7.0f, 36.0f, 2.5f, 10.0f  ),
                },

                //basement - hallway
                new WallCollection[]
                {
                    //hallway
                    ShooterWallCollection.createRoom
                    (
                        4.0f, 30.0f, -0.01f, 0.0f, 4, 12,
                        WallStyle.ENoWall, WallStyle.ESolidWall, WallStyle.ESolidWall, WallStyle.ESolidWall,
                        WallTex.EGlass1, WallHealth.ESolidGlass, WallAction.EDoorSlideRight,
                        DoorStyle.ENoDoor, 0,
                        WALL_OFFICE, WallTex.ECarpet1, WallTex.ECeiling1,
                        new Wall[]
                        {
                            new Wall(   Others.EWhiteboard1,    new LibVertex(  0.0f,   -2.5f,  0.7f    ), 180.0f,   LibScalation.EDouble,      LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster8,  null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                            new Wall(   Others.EWhiteboard1,    new LibVertex(  0.0f,   3.5f,  0.7f    ), 180.0f,   LibScalation.EDouble,      LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster9,  null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),
                            new Wall(   Others.EWhiteboard1,    new LibVertex(  0.0f,   9.5f,  0.7f    ), 180.0f,   LibScalation.EDouble,      LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster3,  null, 0,               WallHealth.ESolidWood, FXSize.ESmall, null  ),

                            new Sprite( Others.ESprite1,        new LibVertex( 3.2f, 3.0f, 0.0f  ), LibScalation.EAddHalf,   WallCollidable.EYes, WallTex.EPlant1 ),
                            new Sprite( Others.ESprite1,        new LibVertex( 3.2f, 6.5f, 0.0f  ), LibScalation.EAddHalf,   WallCollidable.EYes, WallTex.EPlant1 ),
                            new Sprite( Others.ESprite1,        new LibVertex( 3.2f, 10.0f, 0.0f ), LibScalation.EAddHalf,   WallCollidable.EYes, WallTex.EPlant2 ),
                        },
                        null,
                        null,
                        null,
                        new int[] { 1, }
                    ),

                    //casino
                    ShooterWallCollection.createRoom
                    (
                        4.0f, 22.0f, -0.01f, 0.0f, 9, 14,
                        WallStyle.EWindowsAndCeilingWindows, WallStyle.ESolidWall, WallStyle.EWindowsAndCeilingWindows, WallStyle.EWindowsAndCeilingWindows,
                        WallTex.EGlass1, WallHealth.ESolidGlass, WallAction.EDoorSwingCounterClockwise,
                        DoorStyle.EAnchorDefault, 6,
                        WallTex.EBricks2, WallTex.ECarpet1, WallTex.ECeiling1,
                        new Wall[]
                        {
                            new Sprite( Others.ESprite1, new LibVertex( 5.5f, 0.75f, 0.0f ), LibScalation.EAddThird,   WallCollidable.EYes, WallTex.EPlant1 ),
                            new Wall(   Others.ESodaMachine1,   new LibVertex( 7.5f, 0.5f,  0.0f ), 270.0f, LibScalation.ENone,   LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ESodaMachine2,   null, 0,         WallHealth.EVendingMachine, FXSize.ELarge, SoundFg.EExplosion1 ),

                            new Sprite( Others.ESprite1, new LibVertex( 8.0f, 3.0f,  0.0f ), LibScalation.EAddHalf,   WallCollidable.EYes, WallTex.EPlant2 ),
                            new Sprite( Others.ESprite1, new LibVertex( 8.0f, 7.5f,  0.0f ), LibScalation.EAddHalf,   WallCollidable.EYes, WallTex.EPlant1 ),
                            new Sprite( Others.ESprite1, new LibVertex( 8.0f, 12.5f, 0.0f ), LibScalation.EAddHalf,   WallCollidable.EYes, WallTex.EPlant2 ),
                            new Wall(   Others.ESofa1,   new LibVertex( 8.5f, 10.0f, 0.0f ), 0.0f,   LibScalation.ENone,   LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EClothDarkRed, new LibTexture[] { WallTex.ETest, }, 0, WallHealth.EFurniture,      FXSize.ELarge, null                     ),
                            new Wall(   Others.ESofa1,   new LibVertex( 8.5f, 5.0f,  0.0f ), 0.0f,   LibScalation.ENone,   LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EClothDarkRed, new LibTexture[] { WallTex.ETest, }, 0, WallHealth.EFurniture,      FXSize.ELarge, null                     ),
                        },
                        null,
                        null,
                        null,
                        new int[] { 0, 1, 2, }
                    ),

                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      5.5f, 20.0f, -0.01f, 10.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.65f, -10.0f, LibScalation.ETriple ), }
                    ),

                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      7.0f, 20.0f, -0.01f, 20.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.65f, -10.0f, LibScalation.ETriple ), }
                    ),

                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      8.5f, 20.0f, -0.01f, 40.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.65f, -10.0f, LibScalation.ETriple ), }
                    ),

                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      5.5f, 18.5f, -0.01f, 70.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.65f, -10.0f, LibScalation.ETriple ), }
                    ),

                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      7.0f, 18.5f, -0.01f, 90.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.65f, -10.0f, LibScalation.ETriple ), }
                    ),

                    new WallCollection
                    (
                        ShooterWallCollection.createCrate(      8.5f, 18.5f, -0.01f, 80.0f, LibScalation.ETriple ),
                        new Wall[] { ShooterWallCollection.createCrate(  0.0f, 0.0f, 0.65f, -10.0f, LibScalation.ETriple ), }
                    ),

                    //small office
                    ShooterWallCollection.createRoom
                    (
                        4.0f, 24.0f, -0.01f,   180.0f,   5,  5,
                        WallStyle.ESolidWall, WallStyle.ENoWall, WallStyle.ESolidWall, WallStyle.EWindowsAndCeilingWindows,
                        WallTex.EWood1, WallHealth.EUnbreakale, WallAction.EDoorSwingClockwise,
                        DoorStyle.ENoDoor, 0,
                        WallTex.EBricks2, WallTex.ECarpet1, WallTex.ECeiling1,
                        new Wall[]
                        {
                            new Sprite( Others.ESprite1,      new LibVertex( 1.0f, 4.0f, 0.0f ), LibScalation.EAddQuarter, WallCollidable.EYes, WallTex.EPlant2 ),
                            new Wall(   Others.EPoster1,      new LibVertex( 3.0f,   0.01f, 0.7f   ), 270.0f,  LibScalation.EAddHalf,     LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EPoster7,      null,                                   0, WallHealth.ESolidWood, FXSize.ESmall, null   ),
                            new Wall(   Others.EChairOffice1, new LibVertex( 3.0f,   1.0f,  0.0f   ), 290.0f,  LibScalation.ENone,        LibInvert.ENo,  WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ELeather1,     new LibTexture[] { WallTex.EChrome2, }, 0, WallHealth.ESolidWood, FXSize.ESmall, null   ),
                            new Article( Others.EWhiteboard1, 3.0f,   4.8f,  0.9f, 90.0f, LibScalation.EAddThird, LibInvert.ENo, WallTex.EPoster3, WallHealth.ESolidWood, FXSize.ESmall, null ),
                        },
                        new int[] { 1, 2, },
                        null,
                        null,
                        null
                    ),

                    //office desk ( must be a separate wallCollection! .. cannot be applied to small office?
                    new WallCollection
                    (
                        new Wall(       Others.EDeskOffice1,    new LibVertex( 0.5f, 22.5f, -0.01f   ), 90.0f,   LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone,  WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1, new LibTexture[] { WallTex.EScreen2, }, 0, WallHealth.ESolidWood, FXSize.ESmall, null   ),
                        new Wall[]
                        {
                            new Wall(   Others.EKeyboard1,      new LibVertex(  -0.25f,  0.0f,  0.8f ), 180.0f, LibScalation.ENone, LibInvert.EYes, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1, new LibTexture[] { WallTex.EScreen2, }, 0, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
                            new Wall(   Others.EScreen1,        new LibVertex(  -0.75f,  0.0f,  0.8f ), 180.0f, LibScalation.ENone, LibInvert.EYes, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,  new LibTexture[] { WallTex.EScreen2, }, 0, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
                        }
                    ),
                },
            };
        }
    }
