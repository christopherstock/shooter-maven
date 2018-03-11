/*  $Id: Door.java 1297 2015-02-11 18:57:35Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.g3d.wall;

    import  java.util.IllegalFormatCodePointException;
    import  de.christopherstock.lib.Lib.Invert;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.Lib.Scalation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.LibMath;
    import  de.christopherstock.shooter.ShooterDebug;
    import  de.christopherstock.shooter.ShooterSettings.DoorSettings;
    import  de.christopherstock.shooter.g3d.*;
import de.christopherstock.shooter.game.artefact.ArtefactType;
import de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.io.sound.*;
import  de.christopherstock.shooter.level.*;

    /**************************************************************************************
    *   Represents a wall that acts as a door.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class Door extends Wall
    {
        private     static  final   long                serialVersionUID                    = 6679860859123677695L;

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
            super( file, new LibVertex( x, y, z ), rotZ, Scalation.ENone, Invert.ENo, aCollidable, doorAction, aClimbable, DrawMethod.EAlwaysDraw, tex, null, 0, wallHealth, null, null );

            iDoorKey            = aDoorKey;
            iAutoLock           = aAutoLock;
            iAutoLockDelay      = aAutoLockDelay;
            if ( iDoorKey != null )
            {
                iDoorLocked = true;
            }
        }

        public void setConnectedElevator( Door connectedElevator, int targetSectionToShowOnClosed )
        {
            iWallElevator                   = connectedElevator;
            iDoorTargetSectionOnClosed      = targetSectionToShowOnClosed;
        }

        public void setConnectedSluiceDoor( int targetSectionToShowOnClosed, Door connectedSluiceDoor, Door connectedSluiceFloor )
        {
            iWallSluiceDoor                 = connectedSluiceDoor;
            iWallSluiceFloor                = connectedSluiceFloor;
            iDoorTargetSectionOnClosed      = targetSectionToShowOnClosed;
        }

        public final void setElevatorDoors( Door wallDoorTop, Door wallDoorBottom, Wall wallCeiling )
        {
            iWallElevatorDoorOne        = wallDoorTop;
            iWallElevatorDoorTwo        = wallDoorBottom;
            iWallElevatorCeiling        = wallCeiling;
        }

        protected void animateDoor()
        {
            if ( toggleDoorTopNextTick )
            {
                toggleDoorTopNextTick = false;

                //toggle connected door if any
                if ( iWallElevatorDoorTwo != null )
                {
                    if ( iWallAction == WallAction.EElevatorUp )
                    {
                        iWallElevatorDoorOne.toggleWallOpenClose( null );
                    }
                    else
                    {
                        iWallElevatorDoorTwo.toggleWallOpenClose( null );
                    }
                }
            }

            if ( toggleDoorBottomNextTick )
            {
                toggleDoorBottomNextTick = false;

                if ( iWallElevatorDoorTwo != null )
                {
                    if ( iWallAction == WallAction.EElevatorUp )
                    {
                        iWallElevatorDoorTwo.toggleWallOpenClose( null );
                    }
                    else
                    {
                        iWallElevatorDoorOne.toggleWallOpenClose( null );
                    }
                }
            }

            //check if the door is being opened or being closed
            if ( iDoorOpening )
            {
                //open the door
                if ( iDoorAnimation < DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                {
                    //increase animation counter
                    ++iDoorAnimation;

                    //translate mesh
                    switch ( iWallAction )
                    {
                        case ENone:
                        case ESprite:
                        {
                            //impossible to happen
                            throw new IllegalFormatCodePointException( 0 );
                        }

                        case EDoorSlideLeft:
                        {
                            slideAsDoor( true, true );
                            break;
                        }

                        case EDoorSlideRight:
                        {
                            slideAsDoor( true, false );
                            break;
                        }

                        case EDoorSwingClockwise:
                        {
                            swingAsDoor( true, false );
                            break;
                        }

                        case EDoorSwingCounterClockwise:
                        {
                            swingAsDoor( true, true );
                            break;
                        }

                        case EElevatorUp:
                        {
                            moveAsElevator( true );
                            break;
                        }

                        case EElevatorDown:
                        {
                            moveAsElevator( false );
                            break;
                        }

                        case EDoorHatch:
                        {
                            //rotate mesh
                            float angle  = DoorSettings.DOOR_ANGLE_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE;
                            float rotX   = LibMath.cosDeg( iStartupRotZ - 90.0f ) * angle;
                            float rotY   = LibMath.sinDeg( iStartupRotZ - 90.0f ) * angle;

                            translateAndRotateXYZ
                            (
                                0.0f,
                                0.0f,
                                0.0f,
                                rotX * iDoorAnimation,
                                rotY * iDoorAnimation,
                                0.0f,
                                getAnchor(),
                                LibTransformationMode.EOriginalsToTransformed
                            );

                            //rotate mesh's bullet holes
                            BulletHole.rotateForWall( this, rotX, rotY, 0.0f );
                            break;
                        }
                    }

                    //check if door is open now
                    if ( iDoorAnimation == DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                    {
                        toggleDoorTopNextTick = true;

                        //lock the door automatically after countdown
                        if ( iAutoLock )
                        {
                            iDoorLockedCountdown = iAutoLockDelay;
                        }
                    }
                }
            }
            else
            {
                //close the door
                if ( iDoorAnimation > 0 )
                {
                    //decrease animation counter
                    --iDoorAnimation;

                    //disable auto lock
                    iDoorLockedCountdown = -1;

                    //translate mesh
                    switch ( iWallAction )
                    {
                        case ENone:
                        case ESprite:
                        {
                            //impossible to happen
                            throw new IllegalFormatCodePointException( 0 );
                        }

                        case EDoorSlideLeft:
                        {
                            slideAsDoor( false, true );
                            break;
                        }

                        case EDoorSlideRight:
                        {
                            slideAsDoor( false, false );
                            break;
                        }

                        case EDoorSwingClockwise:
                        {
                            swingAsDoor( false, false );
                            break;
                        }

                        case EDoorSwingCounterClockwise:
                        {
                            swingAsDoor( false, true );
                            break;
                        }

                        case EElevatorUp:
                        {
                            moveAsElevator( false );
                            break;
                        }

                        case EElevatorDown:
                        {
                            moveAsElevator( true );
                            break;
                        }

                        case EDoorHatch:
                        {
                            //rotate mesh
                            float angle  = DoorSettings.DOOR_ANGLE_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE;
                            float rotX   = LibMath.cosDeg( iStartupRotZ - 90.0f ) * angle;
                            float rotY   = LibMath.sinDeg( iStartupRotZ - 90.0f ) * angle;

                            translateAndRotateXYZ
                            (
                                0.0f,
                                0.0f,
                                0.0f,
                                rotX * iDoorAnimation,
                                rotY * iDoorAnimation,
                                0.0f,
                                getAnchor(),
                                LibTransformationMode.EOriginalsToTransformed
                            );

                            //rotate mesh's bullet holes
                            BulletHole.rotateForWall( this, -rotX, -rotY, 0.0f );
                            break;
                        }
                    }

                    //check if door is closed now
                    if ( iDoorAnimation == 0 )
                    {
                        //toggle connected elevator if any
                        if ( iWallElevator != null )
                        {
                            //only if player stands on the elevator
                          //if ( iWallElevator.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
                            if ( iWallElevator.checkCollisionVert( Level.currentPlayer().getCylinder(), null ).size() > 0 )
                            {
                                //open connected elevator door
                                iWallElevator.toggleWallOpenClose( null );

                                //change to desired section
                                LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, iDoorTargetSectionOnClosed, false );
                            }
                        }

                        //open connected sluice wall if any
                        if ( iWallSluiceDoor != null )
                        {
                            //only if player stands on the sluice floor
                          //if ( iWallSluiceFloor.checkAction( ShooterGameLevel.currentPlayer().getCylinder(), true, true ) )
                            if ( iWallSluiceFloor.checkCollisionVert( Level.currentPlayer().getCylinder(), null ).size() > 0 )
                            {
                                //open connected sluice door
                                iWallSluiceDoor.toggleWallOpenClose( null );

                                //change to desired section
                                LevelChange.orderLevelChange( LevelCurrent.currentLevelMain, iDoorTargetSectionOnClosed, false );
                            }
                        }

                        //toggle connected door if any
                        toggleDoorBottomNextTick = true;
                    }
                }
            }

            //countdown door unlocking
            if ( iDoorUnlockedCountdown > 0 )
            {
                if ( --iDoorUnlockedCountdown == 0 )
                {
                    ShooterDebug.playerAction.out( "unlock the door!" );

                    //unlock and open the door
                    iDoorLocked  = false;
                    iDoorOpening = true;

                    makeDistancedSound( SoundFg.EDoorOpen1 );
                }
            }

            //countdown door locking
            if ( iDoorLockedCountdown > 0 )
            {
                if ( --iDoorLockedCountdown == 0 )
                {
                    ShooterDebug.playerAction.out( "lock the door!" );

                    //lock the door
                    iDoorOpening = false;

                    makeDistancedSound( SoundFg.EDoorClose1 );
                }
            }
        }

        protected void slideAsDoor( boolean open, boolean left )
        {
            float transX = LibMath.cosDeg( iStartupRotZ - 90.0f ) * ( DoorSettings.DOOR_SLIDING_TRANSLATION_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );
            float transY = LibMath.sinDeg( iStartupRotZ - 90.0f ) * ( DoorSettings.DOOR_SLIDING_TRANSLATION_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );

            //translate mesh
            translate
            (
                0.0f + transX * iDoorAnimation * ( left ? -1.0f : +1.0f ),
                0.0f + transY * iDoorAnimation * ( left ? -1.0f : +1.0f ),
                0.0f,
                LibTransformationMode.EOriginalsToTransformed
            );

            //translate mesh's bullet holes
            BulletHole.translateAll( this, ( open ? 1.0f : -1.0f ) * ( iWallAction == WallAction.EDoorSlideRight ? 1.0f : -1.0f ) * transX, ( open ? 1.0f : -1.0f ) * ( iWallAction == WallAction.EDoorSlideRight ? 1.0f : -1.0f ) * transY, 0.0f );

            //check collision to player
            checkOnDoorAnimation( open );
        }

        protected final void swingAsDoor( boolean open, boolean counterClockwise )
        {
            //rotate mesh
            float angle = DoorSettings.DOOR_ANGLE_OPEN * iDoorAnimation / DoorSettings.DOOR_TICKS_OPEN_CLOSE;

            translateAndRotateXYZ
            (
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                ( counterClockwise ? 1.0f : -1.0f ) * angle,
                getAnchor(),
                LibTransformationMode.EOriginalsToTransformed
            );

            //rotate mesh's bullet holes
            BulletHole.rotateForWall( this, 0.0f, 0.0f, ( open ? ( counterClockwise ? 1.0f : -1.0f ) : ( counterClockwise ? -1.0f : 1.0f ) ) * DoorSettings.DOOR_ANGLE_OPEN / DoorSettings.DOOR_TICKS_OPEN_CLOSE );

            //check collision to player
            checkOnDoorAnimation( open );
        }

        protected final void moveAsElevator( boolean up )
        {
            //translate mesh and bullet hole
            translate( 0.0f, 0.0f, ( up ? 0.125f : -0.125f ), LibTransformationMode.EOriginalsToOriginals );
            BulletHole.translateAll( this, 0.0f, 0.0f, ( up ? 0.125f : -0.125f ) );

            //same for ceiling
            if ( iWallElevatorCeiling != null )
            {
                iWallElevatorCeiling.translate( 0.0f, 0.0f, ( up ? 0.125f : -0.125f ), LibTransformationMode.EOriginalsToOriginals );
                BulletHole.translateAll( iWallElevatorCeiling, 0.0f, 0.0f, ( up ? 0.125f : -0.125f ) );
            }
        }

        protected final void checkOnDoorAnimation( boolean opening )
        {
            boolean toggleDoor = false;

            if ( checkCollisionHorz( Level.currentPlayer().getCylinder() ) )
            {
                //toggle door
                toggleDoor = true;
            }
            else
            {
                //check collision to bots
                for ( Bot bot : Level.currentSection().getBots() )
                {
                    if ( checkCollisionHorz( bot.getCylinder() ) )
                    {
                        toggleDoor = true;
                    }
                }
            }

            if ( toggleDoor )
            {
                iDoorOpening = !opening;
                makeDistancedSound( ( opening ? SoundFg.EDoorClose1 : SoundFg.EDoorOpen1 ) );
            }
        }

        protected final void toggleWallOpenClose( Object artefact )
        {
            if ( artefact == null )
            {
                if ( iDoorLocked )
                {
                    ShooterDebug.playerAction.out( "door is locked!" );
                    makeDistancedSound( SoundFg.ELocked1 );
                }
                else
                {
                    //check if this is an elevator door - doors can't be toggled while elevator is moving!
                    if ( iWallElevator != null )
                    {
                        if ( this == iWallElevator.iWallElevatorDoorOne || this == iWallElevator.iWallElevatorDoorTwo )
                        {
                            if ( iWallElevator.iDoorAnimation != 0 && iWallElevator.iDoorAnimation != DoorSettings.DOOR_TICKS_OPEN_CLOSE )
                            {
                                //deny wall toggle
                                //ShooterDebug.bugfix.out( "deny wall toggle" );
                                return;
                            }
                        }
                    }

                    //check if this is a sluice door - doors can't be opened if the other door is open
                    if ( iWallSluiceDoor != null )
                    {
                        //if ( this == iWallElevator.iWallElevatorDoorTop || this == iWallElevator.iWallElevatorDoorBottom )
                        {
                            //check if connected sluice door is moving
                            if ( iWallSluiceDoor.iDoorAnimation != 0 /* && iWallSluiceDoor.iDoorAnimation != DoorSettings.DOOR_TICKS_OPEN_CLOSE */ )
                            {
                                //deny wall toggle!
                                //ShooterDebug.bugfix.out( "deny wall toggle" );

                                //close the other door immediately if it's opened
                                if ( iWallSluiceDoor.iDoorOpening )
                                {
                                    iWallSluiceDoor.toggleDoorOpening();
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
                    toggleDoorOpening();
                }
            }
            else
            {
                ShooterDebug.playerAction.out( "launch gadget action on door" );
                if ( iDoorLocked )
                {
                    if ( ( (Gadget)artefact ).iParentKind == iDoorKey && iDoorUnlockedCountdown == 0 )
                    {
                        ShooterDebug.playerAction.out( "use doorkey!" );
                        makeDistancedSound( SoundFg.EUnlocked1 );

                        iDoorUnlockedCountdown = 100;

                        //open the door - later?
                        //iDoorOpening = true;
                    }
                }
            }
        }

        private final void toggleDoorOpening()
        {
            iDoorOpening = !iDoorOpening;
            ShooterDebug.playerAction.out( "launch door change, doorOpening now [" + iDoorOpening + "]" );
            if ( iDoorOpening )
            {
                makeDistancedSound( SoundFg.EDoorOpen1 );
            }
            else
            {
                makeDistancedSound( SoundFg.EDoorClose1 );
            }
        }
    }
