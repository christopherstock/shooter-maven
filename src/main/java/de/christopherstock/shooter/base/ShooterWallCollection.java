
    package de.christopherstock.shooter.base;

    import  java.util.*;
    import de.christopherstock.lib.util.LibUtil;
    import de.christopherstock.lib.LibInvert;
    import de.christopherstock.lib.LibTransformationMode;
    import de.christopherstock.lib.LibScalation;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.ShooterSetting.DoorSettings;
    import  de.christopherstock.shooter.ShooterSetting.Performance;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;
    import  de.christopherstock.shooter.base.ShooterD3ds.*;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.g3d.wall.*;
    import  de.christopherstock.shooter.g3d.wall.Wall.*;

    /*******************************************************************************************************************
    *   The current version enumeration.
    *******************************************************************************************************************/
    public abstract class ShooterWallCollection
    {
        public enum WallStyle
        {
            ENoWall,
            ESolidWall,
            EWindows,
            EWindowsAndCeilingWindows,
            ;
        }

        public enum DoorStyle
        {
            ENoDoor,
            EAnchorDefault,
            EAnchorInverted,
            ;
        }

        public static WallCollection createFloor( float x, float y, float z, float rotZ, int sizeX, int sizeY, LibTexture tex )
        {
            return createRoom
            (
                x,
                y,
                z,
                rotZ,
                sizeX,
                sizeY,
                WallStyle.ENoWall,
                WallStyle.ENoWall,
                WallStyle.ENoWall,
                WallStyle.ENoWall,
                tex,
                WallHealth.EUnbreakale,
                WallAction.ENone,
                DoorStyle.ENoDoor,
                0,
                null,
                tex,
                null,
                null,
                null,
                null,
                null,
                null
            );
        }

        public static WallCollection createGround( LibTexture tex, float z )
        {
            return new WallCollection
            (
                //environment ( large meshes ) and bounds
                new Wall[]
                {
                    new Wall(   Others.EFloor100x100,  new LibVertex(  0.0f,    0.0f,    z     ), 0.0f,   LibScalation.ENone,  LibInvert.ENo, WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, tex, null, 0, WallHealth.EUnbreakale, null, null ),
                    new Wall(   Others.EFloor100x100,  new LibVertex(  0.0f,    -100.0f, z     ), 0.0f,   LibScalation.ENone,  LibInvert.ENo, WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, tex, null, 0, WallHealth.EUnbreakale, null, null ),
                    new Wall(   Others.EFloor100x100,  new LibVertex(  -100.0f, 0.0f,    z     ), 0.0f,   LibScalation.ENone,  LibInvert.ENo, WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, tex, null, 0, WallHealth.EUnbreakale, null, null ),
                    new Wall(   Others.EFloor100x100,  new LibVertex(  -100.0f, -100.0f, z     ), 0.0f,   LibScalation.ENone,  LibInvert.ENo, WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, tex, null, 0, WallHealth.EUnbreakale, null, null ),
                }
            );
        }

        /***************************************************************************************************************
        *   Creates a room with maximum possibilities for adjustments.
        ***************************************************************************************************************/
        public static WallCollection createWall( LibD3dsFile file, float x, float y, float z, float rotZ, int sizeX, LibTexture doorTex, WallHealth doorHealth )
        {
            Vector<Wall> allWalls = new Vector<Wall>();

            Wall anchor = null;
            for ( int i = 0; i < sizeX; ++i )
            {
                if ( i == 0 )
                {
                    Wall w = new Wall( file,  new LibVertex(  x,   y,    z    ), rotZ,  LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, doorTex, null, 0, doorHealth, null, null );
                    anchor = w;
                }
                else
                {
                    Wall w = new Wall( file,  new LibVertex(  i * -1.0f,  0.0f,    0.0f    ), 0.0f,  LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, doorTex, null, 0, doorHealth, null, null );
                    allWalls.add( w );
                }
            }

            return new WallCollection( anchor, allWalls.toArray( new Wall[] {} ) );
        }

        public static WallCollection createElevator( float x, float y, float z, float rotZ, LibTexture floorTex, LibTexture doorTex, WallAction action, WallTex wallTex, WallTex ceilingTex, int targetSectionDoorTwo, int targetSectionDoorOne )
        {
            float CABIN_HEIGHT = 2.5f;

            Vector<Wall> allWalls = new Vector<Wall>();

            Door wallElevatorFloor      = new Door(   Others.EFloor2x2, x,       y,       z,                                                   rotZ,  WallCollidable.EYes,  action, WallClimbable.EYes, floorTex, WallHealth.EUnbreakale, null, false, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY );
            Door wallElevatorCeiling    = null;
            Door wallDoorOne            = new Door(   Others.EDoor1,    0.0f,    0.0f,    ( action == WallAction.EElevatorUp ? CABIN_HEIGHT : 0.0f  ), 90.0f, WallCollidable.EYes, WallAction.EDoorSlideLeft, WallClimbable.ENo, doorTex, WallHealth.EUnbreakale, null, true, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY );
            Door wallDoorTwo            = new Door(   Others.EDoor1,    0.0f,    0.0f,    ( action == WallAction.EElevatorUp ? 0.0f : -CABIN_HEIGHT ), 90.0f, WallCollidable.EYes, WallAction.EDoorSlideLeft, WallClimbable.ENo, doorTex, WallHealth.EUnbreakale, null, true, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY );

            //create shaft if desired
            if ( wallTex != null )
            {
                allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  0.0f,   0.0f,    0.0f    ), 270.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  2.0f,   0.0f,    0.0f    ), 270.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  0.0f,   2.0f,    0.0f    ), 180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex, null, 0, WallHealth.EUnbreakale, null, null ) );

                allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  0.0f,   0.0f,    ( action == WallAction.EElevatorUp ? CABIN_HEIGHT : -CABIN_HEIGHT )    ), 270.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
                allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  2.0f,   0.0f,    ( action == WallAction.EElevatorUp ? CABIN_HEIGHT : -CABIN_HEIGHT )    ), 270.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
                allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  0.0f,   2.0f,    ( action == WallAction.EElevatorUp ? CABIN_HEIGHT : -CABIN_HEIGHT )    ), 180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ) );

                //door sockets
                allWalls.add( new Wall(   Others.EWall2Door,   new LibVertex(  0.0f,    0.0f,   0.0f                                                       ), 180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
                allWalls.add( new Wall(   Others.EWall2Door,   new LibVertex(  0.0f,    0.0f,   ( action == WallAction.EElevatorUp ? CABIN_HEIGHT : -CABIN_HEIGHT )    ), 180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
            }

            //set connected elevator for doors
            wallDoorOne.setConnectedElevator(   wallElevatorFloor, targetSectionDoorOne );
            wallDoorTwo.setConnectedElevator(   wallElevatorFloor, targetSectionDoorTwo );

            //create ceiling if desired
            if ( ceilingTex != null )
            {
                wallElevatorCeiling = new Door(   Others.EFloor2x2, 0.0f, 0.0f, 2.20f, 0.0f,  WallCollidable.EYes,  action, WallClimbable.ENo, ceilingTex, WallHealth.EUnbreakale, null, false, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY );
                allWalls.add( wallElevatorCeiling );
            }

            //set elevator doors
            wallElevatorFloor.setElevatorDoors( wallDoorOne, wallDoorTwo, wallElevatorCeiling );

            allWalls.add( wallDoorTwo );
            allWalls.add( wallDoorOne );

            //ELevel1PlayerOffice
            return new WallCollection
            (
                wallElevatorFloor,

                //environment ( large meshes ) and bounds
                allWalls.toArray( new Wall[] {} )
            );
        }

        public static WallCollection createSluice( float x, float y, float z, float rotZ, LibTexture floorTex, LibTexture doorTex, WallTex wallTex, WallTex ceilingTex, WallTex doorSocketTex, int sizeY, int targetSectionDoorOne, int targetSectionDoorTwo )
        {
            Vector<Wall> allWalls = new Vector<Wall>();

            Door wallSluiceFloor        = null;

            switch ( sizeY )
            {
                case 2:     wallSluiceFloor = new Door(   Others.EFloor2x2, x,       y,         z,                                                     rotZ,  WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, floorTex, WallHealth.EUnbreakale, null, false, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY ); break;
                case 6:     wallSluiceFloor = new Door(   Others.EFloor2x6, x,       y,         z,                                                     rotZ,  WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, floorTex, WallHealth.EUnbreakale, null, false, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY ); break;
            }

            Door wallSluiceCeiling      = null;
            Door wallDoorOne            = new Door(   Others.EDoor1,    0.0f,    0.0f,      0.0f, 90.0f, WallCollidable.EYes, WallAction.EDoorSlideLeft, WallClimbable.ENo, doorTex, WallHealth.EUnbreakale, null, true, DoorSettings.DOOR_SLUICE_AUTO_CLOSE_DELAY );
            Door wallDoorTwo            = new Door(   Others.EDoor1,    0.0f,    sizeY,     0.0f, 90.0f, WallCollidable.EYes, WallAction.EDoorSlideLeft, WallClimbable.ENo, doorTex, WallHealth.EUnbreakale, null, true, DoorSettings.DOOR_SLUICE_AUTO_CLOSE_DELAY );

            //set connected sluice doors
            wallDoorOne.setConnectedSluiceDoor( targetSectionDoorTwo, wallDoorTwo, wallSluiceFloor   );
            wallDoorTwo.setConnectedSluiceDoor( targetSectionDoorOne, wallDoorOne, wallSluiceFloor   );

            //create shaft if desired
            if ( wallTex != null )
            {
                for ( float yy = 0.0f; yy < sizeY; yy += 2.0f )
                {
                    //walls
                    allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  0.0f,   yy,    0.0f    ), 270.0f,  LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
                    allWalls.add( new Wall(   Others.EWall2Solid,  new LibVertex(  2.0f,   yy,    0.0f    ), 270.0f,  LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
                }

                //door sockets
                allWalls.add( new Wall(   Others.EWall2Door,   new LibVertex(  0.0f,    0.0f,   0.0f    ), 180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, doorSocketTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
                allWalls.add( new Wall(   Others.EWall2Door,   new LibVertex(  0.0f,    sizeY,  0.0f    ), 180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, doorSocketTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
            }

            //create ceiling if desired
            if ( ceilingTex != null )
            {
                for ( float yy = 0.0f; yy < sizeY; yy += 2.0f )
                {
                    wallSluiceCeiling = new Door(   Others.EFloor2x2, 0.0f, yy, 2.20f, 0.0f,  WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, ceilingTex, WallHealth.EUnbreakale, null, false, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY );
                    allWalls.add( wallSluiceCeiling );
                }
            }

            allWalls.add( wallDoorTwo );
            allWalls.add( wallDoorOne );

            //ELevel1PlayerOffice
            return new WallCollection
            (
                wallSluiceFloor,

                //environment ( large meshes ) and bounds
                allWalls.toArray( new Wall[] {} )
            );
        }

        public static WallCollection createStaircase( float x, float y, float z, boolean toUpper, boolean toLower, float initRotZ, LibTexture wallTex )
        {
            Wall[] wallsBasement = new Wall[]
            {
                new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   0.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),

                new Wall(   Others.EWall2Solid,      new LibVertex(  3.0f,   5.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall1Solid,      new LibVertex(  3.0f,   6.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   2.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   2.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   4.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   4.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   6.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   6.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   8.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   8.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall1Solid,      new LibVertex(  0.0f,   9.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall1Solid,      new LibVertex(  6.0f,   9.0f,   0.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   9.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  4.0f,   9.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                new Wall(   Others.EWall2Solid,      new LibVertex(  2.0f,   9.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
            };

            Wall[] wallsUp =
            (
                    toUpper
                ?   new Wall[]
                    {
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   0.0f,   7.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   0.0f,   7.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   3.0f,   7.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   3.0f,   7.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   6.0f,   7.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   6.0f,   7.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),


                        new Wall(   Others.EStairs3x3,      new LibVertex(  0.0f,   3.0f,   0.0f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.EMarble1, new LibTexture[] { WallTex.EMarble2, }, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EStairs3x3,      new LibVertex(  6.0f,   6.0f,   2.5f   ),  180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.EMarble1, new LibTexture[] { WallTex.EMarble2, }, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   6.0f,   2.5f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   6.0f,   2.5f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   0.0f,   5.0f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   0.0f,   5.0f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   9.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  4.0f,   9.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  2.0f,   9.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   9.0f,   5.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  4.0f,   9.0f,   5.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  2.0f,   9.0f,   5.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   2.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   4.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   6.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   8.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  0.0f,   9.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   2.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   4.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   6.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   8.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  6.0f,   9.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   2.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   4.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   6.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   8.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  0.0f,   9.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   2.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   4.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   6.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   8.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  6.0f,   9.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  3.0f,   5.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  3.0f,   6.0f,   2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  3.0f,   5.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  3.0f,   6.0f,   5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2WindowSocket, new LibVertex(  3.0f,   3.0f,   5.0f    ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1WindowSocket, new LibVertex(  1.0f,   3.0f,   5.0f    ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                    }
                :   new Wall[]
                    {

                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   0.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   0.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   3.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   3.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   6.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   6.0f,   2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECeiling1,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2WindowSocket, new LibVertex(  3.0f,   3.0f,   0.0f    ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1WindowSocket, new LibVertex(  1.0f,   3.0f,   0.0f    ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                    }
            );

            Wall[] wallsDown =
            (
                    toLower
                ?   new Wall[]
                    {
                        new Wall(   Others.EStairs3x3,      new LibVertex(  6.0f,   6.0f,   -2.5f   ),  180.0f, LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.EMarble1, new LibTexture[] { WallTex.EMarble2, }, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EStairs3x3,      new LibVertex(  0.0f,   3.0f,   -5.0f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.EMarble1, new LibTexture[] { WallTex.EMarble2, }, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   6.0f,   -2.5f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   6.0f,   -2.5f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   0.0f,   -5.0f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   0.0f,   -5.0f   ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   9.0f,   -2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  4.0f,   9.0f,   -2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  2.0f,   9.0f,   -2.5f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   9.0f,   -5.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  4.0f,   9.0f,   -5.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  2.0f,   9.0f,   -5.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   2.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   4.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   6.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   8.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  0.0f,   9.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   2.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   4.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   6.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   8.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  6.0f,   9.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   2.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   4.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   6.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  0.0f,   8.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  0.0f,   9.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   2.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   4.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   6.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  6.0f,   8.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  6.0f,   9.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2Solid,      new LibVertex(  3.0f,   5.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  3.0f,   6.0f,   -2.5f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall2Solid,      new LibVertex(  3.0f,   5.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1Solid,      new LibVertex(  3.0f,   6.0f,   -5.0f    ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2WindowSocket, new LibVertex(  6.0f,   3.0f,   -5.0f   ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1WindowSocket, new LibVertex(  4.0f,   3.0f,   -5.0f   ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                    }
                :   new Wall[]
                    {
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   3.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   3.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  0.0f,   6.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EFloor3x3,       new LibVertex(  3.0f,   6.0f,   0.0f    ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1,   null, 0, WallHealth.EUnbreakale, null, null ),

                        new Wall(   Others.EWall2WindowSocket, new LibVertex(  6.0f,   3.0f,   0.0f    ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                        new Wall(   Others.EWall1WindowSocket, new LibVertex(  4.0f,   3.0f,   0.0f    ), 0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex,   null, 0, WallHealth.EUnbreakale, null, null ),
                    }
            );

            Vector<Wall> allWallsV = new Vector<Wall>();
            allWallsV.addAll( Arrays.asList( wallsUp ) );
            allWallsV.addAll( Arrays.asList( wallsDown ) );
            allWallsV.addAll( Arrays.asList( wallsBasement ) );

            return new WallCollection
            (
                new Wall(       Others.EFloor3x3,   new LibVertex(  x,      y,      z       ),  initRotZ,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, WallTex.ECarpet1, null, 0, WallHealth.EUnbreakale, null, null ),
                allWallsV.toArray( new Wall[] {} )
            );
        }

        /***************************************************************************************************************
        *   Creates a room with maximum possibilities for adjustments.
        ***************************************************************************************************************/
        public static WallCollection createRoom( float x, float y, float z, float rotZ, int sizeX, int sizeY, WallStyle left, WallStyle top, WallStyle right, WallStyle bottom, LibTexture doorTex, WallHealth doorHealth, WallAction doorAction, DoorStyle doorStyle, int doorOffset, LibTexture wallTex, LibTexture floorTex, LibTexture ceilingTex, Wall[] furniture, int[] gapTop, int[] gapBottom, int[] gapLeft, int[] gapRight )
        {
            Vector<Wall> allWalls = new Vector<Wall>();

            //add floor and ceiling
            for ( int i = 0; i < sizeX; ++i )
            {
                for ( int j = 0; j < sizeY; ++j )
                {
                    //skip base tile
                    if ( i > 0 || j > 0 )
                    {
                        allWalls.add( new Wall(       Others.EFloor1x1,   new LibVertex(  0.0f + i * 1.0f, 0.0f + j * 1.0f, 0.0f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, floorTex,   null, 0, WallHealth.EUnbreakale, null, null ) );
                    }

                    //margin celiling ( for glass ceilings )
                    if ( ceilingTex != null )
                    {
                        if ( j > 0 && j < ( sizeY - 1 ) && i > 0 && i < ( sizeX - 1 ) )
                        {
                            allWalls.add( new Wall(       Others.EFloor1x1,   new LibVertex(  0.0f + i * 1.0f, 0.0f + j * 1.0f, 2.5f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                        }
                    }

                    //draw corners
                    if ( i == 0 && j == 0 )
                    {
                        if ( left == WallStyle.EWindowsAndCeilingWindows || top == WallStyle.EWindowsAndCeilingWindows )
                        {
                            allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  0.0f, 0.0f,    2.5f       ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EGlass1, null, 0, WallHealth.EGlass, null, null ) );
                        }
                        else if ( ceilingTex != null )
                        {
                            allWalls.add( new Wall(       Others.EFloor1x1,   new LibVertex(  0.0f, 0.0f, 2.5f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                        }
                    }
                    else if ( i == sizeX - 1 && j == 0 )
                    {
                        if ( left == WallStyle.EWindowsAndCeilingWindows || bottom == WallStyle.EWindowsAndCeilingWindows )
                        {
                            allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  sizeX - 1, 0.0f,    2.5f       ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EGlass1, null, 0, WallHealth.EGlass, null, null ) );
                        }
                        else if ( ceilingTex != null )
                        {
                            allWalls.add( new Wall(       Others.EFloor1x1,   new LibVertex(  sizeX - 1.0f, 0.0f, 2.5f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                        }
                    }
                    else if ( i == sizeX - 1 && j == sizeY - 1 )
                    {
                        if ( bottom == WallStyle.EWindowsAndCeilingWindows || right == WallStyle.EWindowsAndCeilingWindows )
                        {
                            allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  sizeX - 1, sizeY - 1,    2.5f       ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EGlass1, null, 0, WallHealth.EGlass, null, null ) );
                        }
                        else if ( ceilingTex != null )
                        {
                            allWalls.add( new Wall(       Others.EFloor1x1,   new LibVertex(  sizeX - 1, sizeY - 1, 2.5f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                        }
                    }
                    else if ( i == 0 && j == sizeY - 1 )
                    {
                        if ( top == WallStyle.EWindowsAndCeilingWindows || right == WallStyle.EWindowsAndCeilingWindows )
                        {
                            allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  0.0f, sizeY - 1,    2.5f       ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EGlass1, null, 0, WallHealth.EGlass, null, null ) );
                        }
                        else if ( ceilingTex != null )
                        {
                            allWalls.add( new Wall(       Others.EFloor1x1,   new LibVertex(  0.0f, sizeY - 1, 2.5f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                        }
                    }
                }
            }

            //left wall
            for ( int i = 0; i < sizeX; ++i )
            {
                LibTexture ct = ( i == 0 || i == sizeX - 1 ? null : ceilingTex );
                if ( gapLeft != null && ( LibUtil.contains( gapLeft, i ) || LibUtil.contains( gapLeft, i - 1 ) ) )
                {
                    addStyledWall( allWalls, left, i * 1.0f, 0.0f, 180.0f, null, ct );
                }
                else
                {
                    addStyledWall( allWalls, left, i * 1.0f, 0.0f, 180.0f, wallTex, ct );
                }
            }

            //right wall
            for ( int i = 0; i < sizeX; ++i )
            {
                LibTexture ct = ( i == 0 || i == sizeX - 1 ? null : ceilingTex );
                if ( gapRight != null && ( LibUtil.contains( gapRight, i ) || LibUtil.contains( gapRight, i - 1 ) ) )
                {
                    addStyledWall( allWalls, right, 1.0f + i * 1.0f, sizeY, 0.0f, null, ct );
                }
                else
                {
                    addStyledWall( allWalls, right, 1.0f + i * 1.0f, sizeY, 0.0f, wallTex, ct );
                }
            }

            //top wall
            for ( int i = 0; i < sizeY; ++i )
            {
                LibTexture ct = ( i == 0 || i == sizeY - 1 ? null : ceilingTex );
                LibTexture wt = (  ( i == doorOffset || i == doorOffset + 1 ) && doorStyle != DoorStyle.ENoDoor ? null : wallTex );
                if ( gapTop != null && ( LibUtil.contains( gapTop, i ) || LibUtil.contains( gapTop, i - 1 ) ) )
                {
                    addStyledWall( allWalls, top, 0.0f, 1.0f + i * 1.0f, 90.0f, null, ct );
                }
                else
                {
                    addStyledWall( allWalls, top, 0.0f, 1.0f + i * 1.0f, 90.0f, wt, ct );
                    if ( wt == null && i == doorOffset )
                    {
                        allWalls.add( new Wall( Others.EWall2Door,  new LibVertex( 0.0f, 2.0f + i * 1.0f, 0.0f ),  90.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, wallTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                    }
                }
            }

            //bottom wall
            for ( int i = 0; i < sizeY; i += 1 )
            {
                LibTexture ct = ( i == 0 || i == sizeY - 1 ? null : ceilingTex );
                if ( gapBottom != null && ( LibUtil.contains( gapBottom, i ) || LibUtil.contains( gapBottom, i - 1 ) ) )
                {
                    addStyledWall( allWalls, bottom, sizeX, i * 1.0f, 270.0f, null, ct ); // wall tex is null!!
                }
                else
                {
                    addStyledWall( allWalls, bottom, sizeX, i * 1.0f, 270.0f, wallTex, ct );
                }
            }

            //Wall tile = new Wall(       Others.EFloor2x2,     new LibVertex(  0.0f,      0.0f,      0.0f       ),  0.0f,        LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, floorTex, null, WallEnergy.WallHealth.EUnbreakale, null, null );

            //add door
            //allWalls.add( tile );

            //add furtinure
            if ( furniture != null ) allWalls.addAll( Arrays.asList( furniture ) );

            //switch door style
            switch ( doorStyle )
            {
                case EAnchorDefault:
                {
                    Door door     = new Door( Others.EDoor1, x, y, z, rotZ, WallCollidable.EYes, doorAction, WallClimbable.ENo, doorTex, doorHealth, null, true, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY );
                    Wall baseTile = new Wall(       Others.EFloor1x1,   new LibVertex( 0.0f, 0.0f, 0.0f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, floorTex, null, 0, WallHealth.EUnbreakale, null, null );
                    allWalls.add( baseTile );

                    //translate all walls by door ..
                    for ( Wall w : allWalls )
                    {
                        w.translate( 0.0f, -doorOffset, 0.0f, LibTransformationMode.EOriginalsToOriginals );
                    }

                    //return right-anchored door
                    return new WallCollection
                    (
                        door,
                        allWalls.toArray( new Wall[] {} ),
                        new LibVertex
                        (
                            LibMath.sinDeg( rotZ ) * -2.0f,
                            LibMath.cosDeg( rotZ ) * 2.0f,
                            0.0f
                        ),
                        new LibVertex
                        (
                            0.0f,
                            0.0f,
                            0.0f
                        ),
                        false
                    );
                }

                case EAnchorInverted:
                {
                    Door door = new Door( Others.EDoor1, x, y, z, rotZ, WallCollidable.EYes, doorAction, WallClimbable.ENo, doorTex, doorHealth, null, true, DoorSettings.DOOR_DEFAULT_AUTO_CLOSE_DELAY );

                    Wall baseTile = new Wall(       Others.EFloor1x1,   new LibVertex(  0.0f, 0.0f, 0.0f ),  0.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, floorTex, null, 0, WallHealth.EUnbreakale, null, null );
                    allWalls.add( baseTile );

                    //translate all walls by door ..
                    for ( Wall w : allWalls )
                    {
                        w.translate( 0.0f, -doorOffset, 0.0f, LibTransformationMode.EOriginalsToOriginals );
                    }

                    //return left-anchored door
                    return new WallCollection
                    (
                        door,
                        allWalls.toArray( new Wall[] {} ),
                        new LibVertex
                        (
                            LibMath.sinDeg( rotZ ) * 0.0f,
                            LibMath.cosDeg( rotZ ) * 0.0f,
                            0.0f
                        ),
                        new LibVertex
                        (
                            0.0f,
                            0.0f,
                            180.0f
                        ),
                        false
                    );
                }

                case ENoDoor:
                default:
                {
                    Wall baseTile = new Wall(       Others.EFloor1x1,   new LibVertex(  x, y, z ), rotZ,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.EYes, DrawMethod.EAlwaysDraw, floorTex, null, 0, WallHealth.EUnbreakale, null, null );
                    return new WallCollection
                    (
                        baseTile,
                        allWalls.toArray( new Wall[] {} ),
                        null,
                        null,
                        false
                    );
                }
            }
        }


        public static WallCollection createShelves( float x, float y, float z, float rotZ )
        {
            Vector<Wall> boxes = new Vector<Wall>();

            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.6f,  0.0f, 0.5f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.25f, 0.0f, 0.5f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.25f,  0.0f, 0.5f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.6f,   0.0f, 0.5f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );

            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.6f,  0.0f, 0.9f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.25f, 0.0f, 0.9f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.25f,  0.0f, 0.9f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.6f,   0.0f, 0.9f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );

            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.6f,  0.0f, 1.29f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.25f, 0.0f, 1.29f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.25f,  0.0f, 1.29f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.6f,   0.0f, 1.29f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );

            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.6f,  0.0f, 1.69f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( -0.25f, 0.0f, 1.69f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.25f,  0.0f, 1.69f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );
            if ( LibMath.getRandom( 0, 2 ) == 0 ) boxes.add( new Wall(   Others.ECrate1, new LibVertex( 0.6f,   0.0f, 1.69f ), 0.0f, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null ) );

            return new WallCollection
            (
                new Wall(   Others.EShelves1, new LibVertex(  x, y, z ), rotZ, LibScalation.ENone, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1, null, 0, WallHealth.ESolidWood, FXSize.ELarge, null ),

                //boxes
                boxes.toArray( new Wall[] {} )
            );
        }

        public static Wall createCrate( float x, float y, float z, float rotZ, LibScalation scalation )
        {
            return new Wall(   Others.ECrate1, new LibVertex( x, y, z ), rotZ, scalation, LibInvert.ENo, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.ECrate1, null, 0, WallHealth.ECrate, FXSize.ESmall, null );
        }

        public static WallCollection createDeskOffice( float x, float y, float z, float rotZ )
        {
            return new WallCollection
            (
                new Wall(   Others.EDeskOffice1,    new LibVertex(  x, y, z    ), rotZ,  LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1, new LibTexture[] { WallTex.EScreen2, }, 0, WallHealth.ESolidWood, FXSize.ESmall, null   ),
                new Wall[]
                {
                  //new Wall(   Others.EKeyboard1,  new LibVertex(  -0.25f,  0.0f,    0.8f    ), 180.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        WallTex.EScreen2, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
                  //new Wall(   Others.EScreen1,    new LibVertex(  -0.75f,  0.0f,    0.8f    ), 180.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        WallTex.EScreen2, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),

                    new Wall(   Others.EScreen1,    new LibVertex(  -0.70f,  -1.00f,  0.8f    ), 150.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        new LibTexture[] { WallTex.EScreen2, WallTex.EScreen3, }, Performance.TICKS_TEXTURE_ANIMATION_SLOW, WallHealth.EElectricalDevice, FXSize.ESmall, null ),
                    new Wall(   Others.EKeyboard1,  new LibVertex(  -0.40f,  -0.65f,  0.8f    ), 160.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        new LibTexture[] { WallTex.EScreen2, }, 0, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
                }
            );
        }

        public static WallCollection createDeskLab( float x, float y, float z, float rotZ )
        {
            //let random assign different topping positions

           return new WallCollection
           (
               new Wall(   Others.EDeskLab1,    new LibVertex(  x, y, z    ), rotZ,  LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EBricks2, new LibTexture[] { WallTex.EPlastic1, }, 0, WallHealth.EUnbreakale, null, null ),
               new Wall[]
               {
                 //new Wall(   Others.EKeyboard1,  new LibVertex(  -0.25f,  0.0f,    0.8f    ), 180.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        WallTex.EScreen2, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
                 //new Wall(   Others.EScreen1,    new LibVertex(  -0.75f,  0.0f,    0.8f    ), 180.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        WallTex.EScreen2, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
                 //new Wall(   Others.EScreen1,    new LibVertex(  -0.70f,  -1.00f,  0.8f    ), 150.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        WallTex.EScreen2, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
                 //new Wall(   Others.EKeyboard1,  new LibVertex(  -0.40f,  -0.65f,  0.8f    ), 160.0f,    LibScalation.ENone,        LibInvert.EYes, WallCollidable.EYes, WallAction.ENone,   WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EWood1,        WallTex.EScreen2, WallHealth.EElectricalDevice, FXSize.ESmall, null   ),
               }
           );
        }

        private static void addStyledWall( Vector<Wall> allWalls, WallStyle style, float x, float y, float angle, LibTexture wallTex, LibTexture ceilingTex )
        {
            switch ( style )
            {
                case ENoWall:
                {
                    //just the ceiling
                    if ( ceilingTex != null ) allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  x, y,    2.5f       ),  angle + 180.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                    break;
                }

                case ESolidWall:
                {
                    if ( wallTex    != null ) allWalls.add( new Wall(         Others.EWall1Solid,         new LibVertex(  x, y,    0.0f       ),  angle,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                    if ( ceilingTex != null ) allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  x, y,    2.5f       ),  angle + 180.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                    break;
                }
                case EWindows:
                {
                    if ( wallTex    != null ) allWalls.add( new Wall(         Others.EWall1WindowSocket,  new LibVertex(  x, y,    0.0f       ),  angle,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, wallTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                    if ( wallTex    != null ) allWalls.add( new Wall(         Others.EWall1WindowGlass,   new LibVertex(  x, y,    0.0f       ),  angle,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EGlass1, null, 0, WallHealth.EGlass, null, null ) );
                    if ( ceilingTex != null ) allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  x, y,    2.5f       ),  angle + 180.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, ceilingTex, null, 0, WallHealth.EUnbreakale, null, null ) );
                    break;
                }
                case EWindowsAndCeilingWindows:
                {
                    if ( wallTex    != null ) allWalls.add( new Wall(         Others.EWall1WindowSocket,  new LibVertex(  x, y,    0.0f       ),  angle,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo,  DrawMethod.EAlwaysDraw, wallTex,    null, 0, WallHealth.EUnbreakale, null, null ) );
                    if ( wallTex    != null ) allWalls.add( new Wall(         Others.EWall1WindowGlass,   new LibVertex(  x, y,    0.0f       ),  angle,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo,  DrawMethod.EAlwaysDraw, WallTex.EGlass1,    null, 0, WallHealth.EGlass, null, null ) );
                    if ( ceilingTex != null ) allWalls.add( new Wall(         Others.EFloor1x1,           new LibVertex(  x, y,    2.5f       ),  angle + 180.0f,   LibScalation.ENone, LibInvert.ENo,   WallCollidable.EYes,  WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, WallTex.EGlass1, null, 0, WallHealth.EGlass, null, null ) );
                    break;
                }
            }
        }
    }
