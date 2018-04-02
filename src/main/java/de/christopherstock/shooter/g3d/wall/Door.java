
    package de.christopherstock.shooter.g3d.wall;

    import  java.util.IllegalFormatCodePointException;
    import  de.christopherstock.lib.LibInvert;
    import  de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.LibScalation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.LibMath;
    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterDebug;
    import  de.christopherstock.shooter.ShooterSetting.DoorSettings;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;

    /*******************************************************************************************************************
    *   Represents a wall that acts as a door.
    *******************************************************************************************************************/
    public class Door extends Wall
    {
        private                     boolean             doorOpening                         = false;
        private                     boolean             doorLocked                          = false;
        private                     ArtefactType        doorKey                             = null;
        private                     int                 doorAnimation                       = 0;

        private                     int                 doorUnlockedCountdown               = 0;
        private                     int                 doorLockedCountdown                 = 0;
        private                     int                 autoLockDelay                       = 0;

        private                     Door                wallElevator                        = null;
        private                     Door                wallElevatorDoorOne                 = null;
        private                     Door                wallElevatorDoorTwo                 = null;
        private                     Wall                wallElevatorCeiling                 = null;

        private                     Door                wallSluiceDoor                      = null;
        private                     Door                wallSluiceFloor                     = null;
        private                     int                 doorTargetSectionOnClosed           = 0;

        private                     boolean             autoLock                            = false;

        private                     boolean             toggleDoorTopNextTick               = false;
        private                     boolean             toggleDoorBottomNextTick            = false;

        public Door( LibD3dsFile file, float x, float y, float z, float rotZ, WallCollidable collidable, WallAction doorAction, WallClimbable climbable, LibTexture tex, WallHealth wallHealth, ArtefactType doorKey, boolean autoLock, int autoLockDelay )
        {
            super( file, new LibVertex( x, y, z ), rotZ, LibScalation.ENone, LibInvert.ENo, collidable, doorAction, climbable, DrawMethod.EAlwaysDraw, tex, null, 0, wallHealth, null, null );

            this.doorKey = doorKey;
            this.autoLock = autoLock;
            this.autoLockDelay = autoLockDelay;
            if (this.doorKey != null )
            {
                this.doorLocked = true;
            }
        }

        public void setConnectedElevator( Door connectedElevator, int targetSectionToShowOnClosed )
        {
            this.wallElevator = connectedElevator;
            this.doorTargetSectionOnClosed = targetSectionToShowOnClosed;
        }

        public void setConnectedSluiceDoor( int targetSectionToShowOnClosed, Door connectedSluiceDoor, Door connectedSluiceFloor )
        {
            this.wallSluiceDoor = connectedSluiceDoor;
            this.wallSluiceFloor = connectedSluiceFloor;
            this.doorTargetSectionOnClosed = targetSectionToShowOnClosed;
        }

        public final void setElevatorDoors( Door wallDoorTop, Door wallDoorBottom, Wall wallCeiling )
        {
            this.wallElevatorDoorOne = wallDoorTop;
            this.wallElevatorDoorTwo = wallDoorBottom;
            this.wallElevatorCeiling = wallCeiling;
        }

        protected void animateDoor()
        {
            if (this.toggleDoorTopNextTick)
            {
                this.toggleDoorTopNextTick = false;

                //toggle connected door if any
                if (this.wallElevatorDoorTwo != null )
                {
                    if (this.wallAction == WallAction.EElevatorUp )
                    {
                        this.wallElevatorDoorOne.toggleWallOpenClose( null );
                    }
                    else
                    {
                        this.wallElevatorDoorTwo.toggleWallOpenClose( null );
                    }
                }
            }

            if (this.toggleDoorBottomNextTick)
            {
                this.toggleDoorBottomNextTick = false;

                if (this.wallElevatorDoorTwo != null )
                {
                    if (this.wallAction == WallAction.EElevatorUp )
                    {
                        this.wallElevatorDoorTwo.toggleWallOpenClose( null );
                    }
                    else
                    {
                        this.wallElevatorDoorOne.toggleWallOpenClose( null );
                    }
                }
            }

            //check if the door is being opened or being closed
            if (this.doorOpening)
            {
                //open the door
                if (this.doorAnimation < DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                {
                    //increase animation counter
                    ++this.doorAnimation;

                    //translate mesh
                    switch (this.wallAction)
                    {
                        case ENone:
                        case ESprite:
                        {
                            //impossible to happen
                            throw new IllegalFormatCodePointException( 0 );
                        }

                        case EDoorSlideLeft:
                        {
                            this.slideAsDoor( true, true );
                            break;
                        }

                        case EDoorSlideRight:
                        {
                            this.slideAsDoor( true, false );
                            break;
                        }

                        case EDoorSwingClockwise:
                        {
                            this.swingAsDoor( true, false );
                            break;
                        }

                        case EDoorSwingCounterClockwise:
                        {
                            this.swingAsDoor( true, true );
                            break;
                        }

                        case EElevatorUp:
                        {
                            this.moveAsElevator( true );
                            break;
                        }

                        case EElevatorDown:
                        {
                            this.moveAsElevator( false );
                            break;
                        }

                        case EDoorHatch:
                        {
                            //rotate mesh
                            float angle  = DoorSettings.DOOR_ANGLE_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE;
                            float rotX   = LibMath.cosDeg(this.startupRotZ - 90.0f ) * angle;
                            float rotY   = LibMath.sinDeg(this.startupRotZ - 90.0f ) * angle;

                            this.translateAndRotateXYZ
                            (
                                0.0f,
                                0.0f,
                                0.0f,
                                rotX * this.doorAnimation,
                                rotY * this.doorAnimation,
                                0.0f,
                                    this.getAnchor(),
                                LibTransformationMode.EOriginalsToTransformed
                            );

                            //rotate mesh's bullet holes
                            Shooter.game.engine.bulletHoleManager.rotateForWall( this, rotX, rotY, 0.0f );
                            break;
                        }
                    }

                    //check if door is open now
                    if (this.doorAnimation == DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                    {
                        this.toggleDoorTopNextTick = true;

                        //lock the door automatically after countdown
                        if (this.autoLock)
                        {
                            this.doorLockedCountdown = this.autoLockDelay;
                        }
                    }
                }
            }
            else
            {
                //close the door
                if (this.doorAnimation > 0 )
                {
                    //decrease animation counter
                    --this.doorAnimation;

                    //disable auto lock
                    this.doorLockedCountdown = -1;

                    //translate mesh
                    switch (this.wallAction)
                    {
                        case ENone:
                        case ESprite:
                        {
                            //impossible to happen
                            throw new IllegalFormatCodePointException( 0 );
                        }

                        case EDoorSlideLeft:
                        {
                            this.slideAsDoor( false, true );
                            break;
                        }

                        case EDoorSlideRight:
                        {
                            this.slideAsDoor( false, false );
                            break;
                        }

                        case EDoorSwingClockwise:
                        {
                            this.swingAsDoor( false, false );
                            break;
                        }

                        case EDoorSwingCounterClockwise:
                        {
                            this.swingAsDoor( false, true );
                            break;
                        }

                        case EElevatorUp:
                        {
                            this.moveAsElevator( false );
                            break;
                        }

                        case EElevatorDown:
                        {
                            this.moveAsElevator( true );
                            break;
                        }

                        case EDoorHatch:
                        {
                            //rotate mesh
                            float angle  = DoorSettings.DOOR_ANGLE_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE;
                            float rotX   = LibMath.cosDeg(this.startupRotZ - 90.0f ) * angle;
                            float rotY   = LibMath.sinDeg(this.startupRotZ - 90.0f ) * angle;

                            this.translateAndRotateXYZ
                            (
                                0.0f,
                                0.0f,
                                0.0f,
                                rotX * this.doorAnimation,
                                rotY * this.doorAnimation,
                                0.0f,
                                    this.getAnchor(),
                                LibTransformationMode.EOriginalsToTransformed
                            );

                            //rotate mesh's bullet holes
                            Shooter.game.engine.bulletHoleManager.rotateForWall( this, -rotX, -rotY, 0.0f );
                            break;
                        }
                    }

                    //check if door is closed now
                    if (this.doorAnimation == 0 )
                    {
                        //toggle connected elevator if any
                        if (this.wallElevator != null )
                        {
                            //only if player stands on the elevator
                          //if ( wallElevator.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
                            if (this.wallElevator.checkCollisionVert( Shooter.game.engine.player.getCylinder(), null ).size() > 0 )
                            {
                                //open connected elevator door
                                this.wallElevator.toggleWallOpenClose( null );

                                //change to desired section
                                LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, this.doorTargetSectionOnClosed, false );
                            }
                        }

                        //open connected sluice wall if any
                        if (this.wallSluiceDoor != null )
                        {
                            //only if player stands on the sluice floor
                          //if ( wallSluiceFloor.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
                            if (this.wallSluiceFloor.checkCollisionVert( Shooter.game.engine.player.getCylinder(), null ).size() > 0 )
                            {
                                //open connected sluice door
                                this.wallSluiceDoor.toggleWallOpenClose( null );

                                //change to desired section
                                LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, this.doorTargetSectionOnClosed, false );
                            }
                        }

                        //toggle connected door if any
                        this.toggleDoorBottomNextTick = true;
                    }
                }
            }

            //countdown door unlocking
            if (this.doorUnlockedCountdown > 0 )
            {
                if ( --this.doorUnlockedCountdown == 0 )
                {
                    ShooterDebug.playerAction.out( "unlock the door!" );

                    //unlock and open the door
                    this.doorLocked = false;
                    this.doorOpening = true;

                    this.makeDistancedSound( SoundFg.EDoorOpen1 );
                }
            }

            //countdown door locking
            if (this.doorLockedCountdown > 0 )
            {
                if ( --this.doorLockedCountdown == 0 )
                {
                    ShooterDebug.playerAction.out( "lock the door!" );

                    //lock the door
                    this.doorOpening = false;

                    this.makeDistancedSound( SoundFg.EDoorClose1 );
                }
            }
        }

        private void slideAsDoor(boolean open, boolean left)
        {
            float transX = LibMath.cosDeg(this.startupRotZ - 90.0f ) * ( DoorSettings.DOOR_SLIDING_TRANSLATION_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );
            float transY = LibMath.sinDeg(this.startupRotZ - 90.0f ) * ( DoorSettings.DOOR_SLIDING_TRANSLATION_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );

            //translate mesh
            this.translate
            (
                0.0f + transX * this.doorAnimation * ( left ? -1.0f : +1.0f ),
                0.0f + transY * this.doorAnimation * ( left ? -1.0f : +1.0f ),
                0.0f,
                LibTransformationMode.EOriginalsToTransformed
            );

            //translate mesh's bullet holes
            Shooter.game.engine.bulletHoleManager.translateAll( this, ( open ? 1.0f : -1.0f ) * (this.wallAction == WallAction.EDoorSlideRight ? 1.0f : -1.0f ) * transX, ( open ? 1.0f : -1.0f ) * (this.wallAction == WallAction.EDoorSlideRight ? 1.0f : -1.0f ) * transY, 0.0f );

            //check collision to player
            this.checkOnDoorAnimation( open );
        }

        private void swingAsDoor( boolean open, boolean counterClockwise )
        {
            //rotate mesh
            float angle = DoorSettings.DOOR_ANGLE_OPEN * this.doorAnimation / DoorSettings.DOOR_TICKS_OPEN_CLOSE;

            this.translateAndRotateXYZ
            (
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                ( counterClockwise ? 1.0f : -1.0f ) * angle,
                    this.getAnchor(),
                LibTransformationMode.EOriginalsToTransformed
            );

            //rotate mesh's bullet holes
            Shooter.game.engine.bulletHoleManager.rotateForWall( this, 0.0f, 0.0f, ( open ? ( counterClockwise ? 1.0f : -1.0f ) : ( counterClockwise ? -1.0f : 1.0f ) ) * DoorSettings.DOOR_ANGLE_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );

            //check collision to player
            this.checkOnDoorAnimation( open );
        }

        private void moveAsElevator( boolean up )
        {
            float MOVE_DELTA = 0.125f;

            //translate mesh and bullet hole
            this.translate( 0.0f, 0.0f, ( up ? MOVE_DELTA : -MOVE_DELTA ), LibTransformationMode.EOriginalsToOriginals );
            Shooter.game.engine.bulletHoleManager.translateAll( this, 0.0f, 0.0f, ( up ? MOVE_DELTA : -MOVE_DELTA ) );

            //same for ceiling
            if (this.wallElevatorCeiling != null )
            {
                this.wallElevatorCeiling.translate( 0.0f, 0.0f, ( up ? MOVE_DELTA : -MOVE_DELTA ), LibTransformationMode.EOriginalsToOriginals );
                Shooter.game.engine.bulletHoleManager.translateAll(this.wallElevatorCeiling, 0.0f, 0.0f, ( up ? MOVE_DELTA : -MOVE_DELTA ) );
            }
        }

        private void checkOnDoorAnimation( boolean opening )
        {
            boolean toggleDoor = false;

            if (this.checkCollisionHorz( Shooter.game.engine.player.getCylinder() ) )
            {
                //toggle door
                toggleDoor = true;
            }
            else
            {
                //check collision to bots
                for ( Bot bot : Level.currentSection().getBots() )
                {
                    if (this.checkCollisionHorz( bot.getCylinder() ) )
                    {
                        toggleDoor = true;
                    }
                }
            }

            if ( toggleDoor )
            {
                this.doorOpening = !opening;
                this.makeDistancedSound( ( opening ? SoundFg.EDoorClose1 : SoundFg.EDoorOpen1 ) );
            }
        }

        protected final void toggleWallOpenClose( Object artefact )
        {
            if ( artefact == null )
            {
                if (this.doorLocked)
                {
                    ShooterDebug.playerAction.out( "door is locked!" );
                    this.makeDistancedSound( SoundFg.ELocked1 );
                }
                else
                {
                    //check if this is an elevator door - doors can't be toggled while elevator is moving!
                    if (this.wallElevator != null )
                    {
                        if ( this == this.wallElevator.wallElevatorDoorOne || this == this.wallElevator.wallElevatorDoorTwo)
                        {
                            if (this.wallElevator.doorAnimation != 0 && this.wallElevator.doorAnimation != DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                            {
                                //deny wall toggle
                                //ShooterDebug.bugfix.out( "deny wall toggle" );
                                return;
                            }
                        }
                    }

                    //check if this is a sluice door - doors can't be opened if the other door is open
                    if (this.wallSluiceDoor != null )
                    {
                        //if ( this == wallElevator.iWallElevatorDoorTop || this == wallElevator.iWallElevatorDoorBottom )
                        {
                            //check if connected sluice door is moving
                            if (this.wallSluiceDoor.doorAnimation != 0 /* && wallSluiceDoor.doorAnimation != DoorSettings.DOOR_TICKS_OPEN_CLOSE */ )
                            {
                                //deny wall toggle!
                                //ShooterDebug.bugfix.out( "deny wall toggle" );

                                //close the other door immediately if it's opened
                                if (this.wallSluiceDoor.doorOpening)
                                {
                                    this.wallSluiceDoor.toggleDoorOpening();
                                }

                                return;
                            }
/*
                            //.. or if the player stands on the floor
                            if ( wallSluiceFloor.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
                            {
                                return;
                            }
*/
                        }
                    }

                    //toggle door opening
                    this.toggleDoorOpening();
                }
            }
            else
            {
                ShooterDebug.playerAction.out( "launch gadget action on door" );
                if (this.doorLocked)
                {
                    if ( ( (Gadget)artefact ).parentKind == this.doorKey && this.doorUnlockedCountdown == 0 )
                    {
                        ShooterDebug.playerAction.out( "use doorkey!" );
                        this.makeDistancedSound( SoundFg.EUnlocked1 );

                        this.doorUnlockedCountdown = 100;

                        //open the door - later?
                        //doorOpening = true;
                    }
                }
            }
        }

        private void toggleDoorOpening()
        {
            this.doorOpening = !this.doorOpening;
            ShooterDebug.playerAction.out( "launch door change, doorOpening now [" + this.doorOpening + "]" );
            if (this.doorOpening)
            {
                this.makeDistancedSound( SoundFg.EDoorOpen1 );
            }
            else
            {
                this.makeDistancedSound( SoundFg.EDoorClose1 );
            }
        }
    }
