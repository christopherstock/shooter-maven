
    package de.christopherstock.shooter.g3d.wall;

    import  java.util.IllegalFormatCodePointException;

    import de.christopherstock.lib.LibInvert;
    import de.christopherstock.lib.LibTransformationMode;
    import de.christopherstock.lib.LibScalation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.LibMath;
    import de.christopherstock.shooter.Shooter;
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
        private                     boolean             iDoorOpening                        = false;
        private                     boolean             iDoorLocked                         = false;
        private                     ArtefactType        iDoorKey                            = null;
        private                     int                 iDoorAnimation                      = 0;

        private                     int                 iDoorUnlockedCountdown              = 0;
        private                     int                 iDoorLockedCountdown                = 0;
        private                     int                 iAutoLockDelay                      = 0;

        private                     Door                iWallElevator                       = null;
        private                     Door                iWallElevatorDoorOne                = null;
        private                     Door                iWallElevatorDoorTwo             = null;
        private                     Wall                iWallElevatorCeiling                = null;

        private                     Door                iWallSluiceDoor                     = null;
        private                     Door                iWallSluiceFloor                    = null;
        private                     int                 iDoorTargetSectionOnClosed          = 0;

        private                     boolean             iAutoLock                           = false;

        private                     boolean             toggleDoorTopNextTick               = false;
        private                     boolean             toggleDoorBottomNextTick            = false;

        public Door( LibD3dsFile file, float x, float y, float z, float rotZ, WallCollidable aCollidable, WallAction doorAction, WallClimbable aClimbable, LibTexture tex, WallHealth wallHealth, ArtefactType aDoorKey, boolean aAutoLock, int aAutoLockDelay )
        {
            super( file, new LibVertex( x, y, z ), rotZ, LibScalation.ENone, LibInvert.ENo, aCollidable, doorAction, aClimbable, DrawMethod.EAlwaysDraw, tex, null, 0, wallHealth, null, null );

            this.iDoorKey = aDoorKey;
            this.iAutoLock = aAutoLock;
            this.iAutoLockDelay = aAutoLockDelay;
            if (this.iDoorKey != null )
            {
                this.iDoorLocked = true;
            }
        }

        public void setConnectedElevator( Door connectedElevator, int targetSectionToShowOnClosed )
        {
            this.iWallElevator = connectedElevator;
            this.iDoorTargetSectionOnClosed = targetSectionToShowOnClosed;
        }

        public void setConnectedSluiceDoor( int targetSectionToShowOnClosed, Door connectedSluiceDoor, Door connectedSluiceFloor )
        {
            this.iWallSluiceDoor = connectedSluiceDoor;
            this.iWallSluiceFloor = connectedSluiceFloor;
            this.iDoorTargetSectionOnClosed = targetSectionToShowOnClosed;
        }

        public final void setElevatorDoors( Door wallDoorTop, Door wallDoorBottom, Wall wallCeiling )
        {
            this.iWallElevatorDoorOne = wallDoorTop;
            this.iWallElevatorDoorTwo = wallDoorBottom;
            this.iWallElevatorCeiling = wallCeiling;
        }

        protected void animateDoor()
        {
            if (this.toggleDoorTopNextTick)
            {
                this.toggleDoorTopNextTick = false;

                //toggle connected door if any
                if (this.iWallElevatorDoorTwo != null )
                {
                    if (this.iWallAction == WallAction.EElevatorUp )
                    {
                        this.iWallElevatorDoorOne.toggleWallOpenClose( null );
                    }
                    else
                    {
                        this.iWallElevatorDoorTwo.toggleWallOpenClose( null );
                    }
                }
            }

            if (this.toggleDoorBottomNextTick)
            {
                this.toggleDoorBottomNextTick = false;

                if (this.iWallElevatorDoorTwo != null )
                {
                    if (this.iWallAction == WallAction.EElevatorUp )
                    {
                        this.iWallElevatorDoorTwo.toggleWallOpenClose( null );
                    }
                    else
                    {
                        this.iWallElevatorDoorOne.toggleWallOpenClose( null );
                    }
                }
            }

            //check if the door is being opened or being closed
            if (this.iDoorOpening)
            {
                //open the door
                if (this.iDoorAnimation < DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                {
                    //increase animation counter
                    ++this.iDoorAnimation;

                    //translate mesh
                    switch (this.iWallAction)
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
                            float rotX   = LibMath.cosDeg(this.iStartupRotZ - 90.0f ) * angle;
                            float rotY   = LibMath.sinDeg(this.iStartupRotZ - 90.0f ) * angle;

                            this.translateAndRotateXYZ
                            (
                                0.0f,
                                0.0f,
                                0.0f,
                                rotX * this.iDoorAnimation,
                                rotY * this.iDoorAnimation,
                                0.0f,
                                    this.getAnchor(),
                                LibTransformationMode.EOriginalsToTransformed
                            );

                            //rotate mesh's bullet holes
                            BulletHole.rotateForWall( this, rotX, rotY, 0.0f );
                            break;
                        }
                    }

                    //check if door is open now
                    if (this.iDoorAnimation == DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                    {
                        this.toggleDoorTopNextTick = true;

                        //lock the door automatically after countdown
                        if (this.iAutoLock)
                        {
                            this.iDoorLockedCountdown = this.iAutoLockDelay;
                        }
                    }
                }
            }
            else
            {
                //close the door
                if (this.iDoorAnimation > 0 )
                {
                    //decrease animation counter
                    --this.iDoorAnimation;

                    //disable auto lock
                    this.iDoorLockedCountdown = -1;

                    //translate mesh
                    switch (this.iWallAction)
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
                            float rotX   = LibMath.cosDeg(this.iStartupRotZ - 90.0f ) * angle;
                            float rotY   = LibMath.sinDeg(this.iStartupRotZ - 90.0f ) * angle;

                            this.translateAndRotateXYZ
                            (
                                0.0f,
                                0.0f,
                                0.0f,
                                rotX * this.iDoorAnimation,
                                rotY * this.iDoorAnimation,
                                0.0f,
                                    this.getAnchor(),
                                LibTransformationMode.EOriginalsToTransformed
                            );

                            //rotate mesh's bullet holes
                            BulletHole.rotateForWall( this, -rotX, -rotY, 0.0f );
                            break;
                        }
                    }

                    //check if door is closed now
                    if (this.iDoorAnimation == 0 )
                    {
                        //toggle connected elevator if any
                        if (this.iWallElevator != null )
                        {
                            //only if player stands on the elevator
                          //if ( iWallElevator.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
                            if (this.iWallElevator.checkCollisionVert( Shooter.game.engine.player.getCylinder(), null ).size() > 0 )
                            {
                                //open connected elevator door
                                this.iWallElevator.toggleWallOpenClose( null );

                                //change to desired section
                                LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, this.iDoorTargetSectionOnClosed, false );
                            }
                        }

                        //open connected sluice wall if any
                        if (this.iWallSluiceDoor != null )
                        {
                            //only if player stands on the sluice floor
                          //if ( iWallSluiceFloor.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
                            if (this.iWallSluiceFloor.checkCollisionVert( Shooter.game.engine.player.getCylinder(), null ).size() > 0 )
                            {
                                //open connected sluice door
                                this.iWallSluiceDoor.toggleWallOpenClose( null );

                                //change to desired section
                                LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, this.iDoorTargetSectionOnClosed, false );
                            }
                        }

                        //toggle connected door if any
                        this.toggleDoorBottomNextTick = true;
                    }
                }
            }

            //countdown door unlocking
            if (this.iDoorUnlockedCountdown > 0 )
            {
                if ( --this.iDoorUnlockedCountdown == 0 )
                {
                    ShooterDebug.playerAction.out( "unlock the door!" );

                    //unlock and open the door
                    this.iDoorLocked = false;
                    this.iDoorOpening = true;

                    this.makeDistancedSound( SoundFg.EDoorOpen1 );
                }
            }

            //countdown door locking
            if (this.iDoorLockedCountdown > 0 )
            {
                if ( --this.iDoorLockedCountdown == 0 )
                {
                    ShooterDebug.playerAction.out( "lock the door!" );

                    //lock the door
                    this.iDoorOpening = false;

                    this.makeDistancedSound( SoundFg.EDoorClose1 );
                }
            }
        }

        protected void slideAsDoor( boolean open, boolean left )
        {
            float transX = LibMath.cosDeg(this.iStartupRotZ - 90.0f ) * ( DoorSettings.DOOR_SLIDING_TRANSLATION_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );
            float transY = LibMath.sinDeg(this.iStartupRotZ - 90.0f ) * ( DoorSettings.DOOR_SLIDING_TRANSLATION_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );

            //translate mesh
            this.translate
            (
                0.0f + transX * this.iDoorAnimation * ( left ? -1.0f : +1.0f ),
                0.0f + transY * this.iDoorAnimation * ( left ? -1.0f : +1.0f ),
                0.0f,
                LibTransformationMode.EOriginalsToTransformed
            );

            //translate mesh's bullet holes
            BulletHole.translateAll( this, ( open ? 1.0f : -1.0f ) * (this.iWallAction == WallAction.EDoorSlideRight ? 1.0f : -1.0f ) * transX, ( open ? 1.0f : -1.0f ) * (this.iWallAction == WallAction.EDoorSlideRight ? 1.0f : -1.0f ) * transY, 0.0f );

            //check collision to player
            this.checkOnDoorAnimation( open );
        }

        protected final void swingAsDoor( boolean open, boolean counterClockwise )
        {
            //rotate mesh
            float angle = DoorSettings.DOOR_ANGLE_OPEN * this.iDoorAnimation / DoorSettings.DOOR_TICKS_OPEN_CLOSE;

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
            BulletHole.rotateForWall( this, 0.0f, 0.0f, ( open ? ( counterClockwise ? 1.0f : -1.0f ) : ( counterClockwise ? -1.0f : 1.0f ) ) * DoorSettings.DOOR_ANGLE_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );

            //check collision to player
            this.checkOnDoorAnimation( open );
        }

        protected final void moveAsElevator( boolean up )
        {
            //translate mesh and bullet hole
            this.translate( 0.0f, 0.0f, ( up ? 0.125f : -0.125f ), LibTransformationMode.EOriginalsToOriginals );
            BulletHole.translateAll( this, 0.0f, 0.0f, ( up ? 0.125f : -0.125f ) );

            //same for ceiling
            if (this.iWallElevatorCeiling != null )
            {
                this.iWallElevatorCeiling.translate( 0.0f, 0.0f, ( up ? 0.125f : -0.125f ), LibTransformationMode.EOriginalsToOriginals );
                BulletHole.translateAll(this.iWallElevatorCeiling, 0.0f, 0.0f, ( up ? 0.125f : -0.125f ) );
            }
        }

        protected final void checkOnDoorAnimation( boolean opening )
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
                this.iDoorOpening = !opening;
                this.makeDistancedSound( ( opening ? SoundFg.EDoorClose1 : SoundFg.EDoorOpen1 ) );
            }
        }

        protected final void toggleWallOpenClose( Object artefact )
        {
            if ( artefact == null )
            {
                if (this.iDoorLocked)
                {
                    ShooterDebug.playerAction.out( "door is locked!" );
                    this.makeDistancedSound( SoundFg.ELocked1 );
                }
                else
                {
                    //check if this is an elevator door - doors can't be toggled while elevator is moving!
                    if (this.iWallElevator != null )
                    {
                        if ( this == this.iWallElevator.iWallElevatorDoorOne || this == this.iWallElevator.iWallElevatorDoorTwo )
                        {
                            if (this.iWallElevator.iDoorAnimation != 0 && this.iWallElevator.iDoorAnimation != DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                            {
                                //deny wall toggle
                                //ShooterDebug.bugfix.out( "deny wall toggle" );
                                return;
                            }
                        }
                    }

                    //check if this is a sluice door - doors can't be opened if the other door is open
                    if (this.iWallSluiceDoor != null )
                    {
                        //if ( this == iWallElevator.iWallElevatorDoorTop || this == iWallElevator.iWallElevatorDoorBottom )
                        {
                            //check if connected sluice door is moving
                            if (this.iWallSluiceDoor.iDoorAnimation != 0 /* && iWallSluiceDoor.iDoorAnimation != DoorSettings.DOOR_TICKS_OPEN_CLOSE */ )
                            {
                                //deny wall toggle!
                                //ShooterDebug.bugfix.out( "deny wall toggle" );

                                //close the other door immediately if it's opened
                                if (this.iWallSluiceDoor.iDoorOpening )
                                {
                                    this.iWallSluiceDoor.toggleDoorOpening();
                                }

                                return;
                            }
/*
                            //.. or if the player stands on the floor
                            if ( iWallSluiceFloor.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
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
                if (this.iDoorLocked)
                {
                    if ( ( (Gadget)artefact ).iParentKind == this.iDoorKey && this.iDoorUnlockedCountdown == 0 )
                    {
                        ShooterDebug.playerAction.out( "use doorkey!" );
                        this.makeDistancedSound( SoundFg.EUnlocked1 );

                        this.iDoorUnlockedCountdown = 100;

                        //open the door - later?
                        //iDoorOpening = true;
                    }
                }
            }
        }

        private void toggleDoorOpening()
        {
            this.iDoorOpening = !this.iDoorOpening;
            ShooterDebug.playerAction.out( "launch door change, doorOpening now [" + this.iDoorOpening + "]" );
            if (this.iDoorOpening)
            {
                this.makeDistancedSound( SoundFg.EDoorOpen1 );
            }
            else
            {
                this.makeDistancedSound( SoundFg.EDoorClose1 );
            }
        }
    }
