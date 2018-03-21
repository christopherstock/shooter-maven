
    package de.christopherstock.shooter.game.objects;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.BotSettings;
    import  de.christopherstock.shooter.ShooterSettings.FxSettings;
    import  de.christopherstock.shooter.ShooterSettings.General;
    import  de.christopherstock.shooter.ShooterSettings.PlayerSettings;
    import  de.christopherstock.shooter.base.ShooterD3ds.*;
    import  de.christopherstock.shooter.g3d.*;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.g3d.mesh.BotMeshes.*;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactSet;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.bot.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.*;

    /*******************************************************************************************************************
    *   The superclass of all non-player-characters.
    *******************************************************************************************************************/
    public class Bot implements LibGameObject, ShotSpender
    {
        public enum BotAliveState
        {
            EAlive,
            ETranquilized,
            EDead,
            ;
        }

        public enum BotBodyType
        {
            EMaleNormal,
            EFemaleNormal,
            ;
        }

        public enum BotType
        {
            ETypeEnemy,
            ETypeFriend,
            ;
        }

        public enum BotHanded
        {
            ELeftHanded,
            ERightHanded,
            //EBothHanded,
            ;
        }

        public enum BotSkinType
        {
            ERose,
            ELightBrown,
            EBrown,
            EBlack,
            EYellow,
            ;

            public  static  final   BotSkinType[]   SET_GLOBAL          = BotSkinType.values();
            public  static  final   BotSkinType[]   SET_NORTH_EUROPEAN  = new BotSkinType[] { ERose,        ELightBrown,    };
            public  static  final   BotSkinType[]   SET_SOUTH_EUROPEAN  = new BotSkinType[] { ELightBrown,  EBrown,         };
            public  static  final   BotSkinType[]   SET_NORTH_AFRICAN   = new BotSkinType[] { EBrown,       EBlack,         };
            public  static  final   BotSkinType[]   SET_SOUTH_AFRICAN   = new BotSkinType[] { EBlack,                       };
            public  static  final   BotSkinType[]   SET_ASIAN           = new BotSkinType[] { EYellow,                      };
        }

        public enum BotJob
        {
            ELeadPlayerToLastWaypoint,
            EWalkWaypoints,
            EWatchPlayer,
            EStandStill,
            EDying,
            EAttackPlayerFire,
            EAttackPlayerReload,

            //sequenced jobs require a non-sequenced job at the end!
            ESequenceDelay,
            ESequenceTurnToPlayer,
            ESequenceNodOnce,
            ESequenceNodTwice,
            ESequenceGrabSpringRight,
            ESequenceFlushRightEquippedItem,
            ESequenceDeliverRightEquippedItem,
            ESequenceGrabBackDownRight,
            ESequenceGrabSpringLeft,
            ESequenceFlushLeftEquippedItem,
            ESequenceDeliverLeftEquippedItem,
            ESequenceGrabBackDownLeft,
            ESequenceReloadHandUp,
            ESequenceReloadHandDown,
            ;
        }

        public enum BotState
        {
            EDying,
            EWalkTowardsPlayer,
            EWatchPlayer,
            EStandStill,
            EWalkToNextWayPoint,
            ;
        }

        public enum BotHealth
        {
            ECivilian(          30  ),
            ESecurity(          100 ),
            EPrivateSoldier(    150 ),
            ;

            public      int     iEnergy     = 0;

            private BotHealth( int aEnergy )
            {
                iEnergy = aEnergy;
            }
        }

        public enum DyingDirection
        {
            EFront,
            EBack,
            ;
        }

        public static interface BotAction
        {
        }

        public static final class BotUseAction implements BotAction
        {
            public          BotEvent        iEvent        = null;

            public BotUseAction( BotEvent aEvent )
            {
                iEvent = aEvent;
            }
        }

        public static final class BotGiveAction implements BotAction
        {
            public          ArtefactType        iKey        = null;
            public          BotEvent            iEvent      = null;

            public BotGiveAction( ArtefactType aKey, BotEvent aEvent )
            {
                iKey = aKey;
                iEvent = aEvent;
            }
        }

        public                      BotMeshes           iBotMeshes                  = null;

        public                      AmmoSet             iAmmoSet                    = null;

        public                      ArtefactSet         iArtefactSet                = null;


        /** current facing angle ( z axis ). */
        private                     float               iFacingAngle                = 0.0f;

        /** current drop dead angle ( x axis ). */
        private                     boolean             iFaceAngleChanged           = false;

        private                     Vector<BotJob>      iJobs                       = null;

        private                     BotType             iType                       = null;
        private                     BotState            iState                      = null;
        private                     Point2D.Float[]     iWayPoints                  = null;
        private                     int                 iCurrentWayPointIndex       = 0;
        private                     int                 iHealth                     = 0;

        private                     float               iTargetOffsetZ              = 0.0f;
        private                     float               iOffsetZ                    = 0.0f;

        private                     BotAliveState       iAliveState                 = null;

        private                     DyingDirection      iDyingDirection             = null;
        private                     float               iDyingAngle                 = 0.0f;
        public                      long                iDisappearTimer             = 0;

        /** the collision unit for simple collisions */
        private                     Cylinder            iCylinder                   = null;

        private                     long                iNextEyeChange              = 0;
        private                     boolean             iEyesOpen                   = true;

        private                     BotPattern   iTemplate                   = null;
        private                     LibVertex           iStartPosition              = null;
        private                     BotAction[]         iActions                    = null;
        public                      int                 iID                         = 0;

        private                     int                 iInterAnimationsDelay       = 0;
        private                     BotHanded           iHanded                     = null;

        public                      Items               iNextItemToDeliverLeftHand  = null;
        public                      Items               iNextItemToDeliverRightHand = null;

        public Bot( BotPattern aTemplate, BotType aType, LibVertex aStartPosition, Point2D.Float[] aWayPoints, BotJob aJob, Artefact[] aArtefacts, float aFacingAngle, BotAction[] aActions, BotHealth aBotHealth, int aID, BotHanded aBotHanded )
        {
            iType           = aType;
            iStartPosition  = aStartPosition.copy();
            iCylinder       = new Cylinder( this, iStartPosition, aTemplate.iBase.iRadius, aTemplate.iBase.iHeight, 0, ShooterDebug.bot, ShooterDebug.DEBUG_DRAW_BOT_CIRCLES, 0.0f, 0.0f, ShooterSettings.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh );
            iWayPoints      = aWayPoints;
            iJobs           = new Vector<BotJob>();
            iJobs.add(      aJob );
            iHealth         = aBotHealth.iEnergy;
            iTemplate       = aTemplate;
            iActions        = aActions;
            iID             = aID;
            iHanded         = aBotHanded;
            iAliveState     = BotAliveState.EAlive;

            //set startup facing angle
            iFacingAngle    = aFacingAngle;

            iAmmoSet        = new AmmoSet();
            iArtefactSet    = new ArtefactSet();

            //deliver all artefacts if given
            if ( aArtefacts != null )
            {
                for ( Artefact a : aArtefacts )
                {
                    iArtefactSet.deliverArtefact( a );
                }

                iArtefactSet.chooseNextWearponOrGadget( false );
                iArtefactSet.iCurrentArtefact.reload( iAmmoSet, false, false, null );
            }

            Items leftItem  = null;
            Items rightItem = null;

            switch ( iHanded )
            {
                case ELeftHanded:
                {
                    leftItem = iArtefactSet.iCurrentArtefact.iArtefactType.iItemMesh;
                    break;
                }

                case ERightHanded:
                {
                    rightItem = iArtefactSet.iCurrentArtefact.iArtefactType.iItemMesh;
                    break;
                }
            }

            //init mesh-collection
            iBotMeshes      = new BotMeshes( iTemplate, iStartPosition, this, leftItem, rightItem );

            //set next eye blink
            iEyesOpen       = true;
            setNextEyeChange();

            //animate 1st tick? .. triggers onRun :/ not required!
            boolean deprecatedAnimateOnCreate = false;
            if ( deprecatedAnimateOnCreate ) animate();
        }

        private final void setNextEyeChange()
        {
            //only if not dead
            if ( iAliveState == BotAliveState.EAlive )
            {
                iNextEyeChange =
                (
                        System.currentTimeMillis()
                    +   (
                                iEyesOpen
                            ?   LibMath.getRandom( BotSettings.EYE_BLINK_INTERVAL_MIN, BotSettings.EYE_BLINK_INTERVAL_MAX )
                            :   BotSettings.EYE_CLOSED_INTERVAL
                        )
                );
            }
        }

        public final boolean checkCollision( Cylinder aCylinder )
        {
            return ( iAliveState == BotAliveState.EAlive ? iCylinder.checkCollisionHorz( aCylinder ) : false );
        }

        public final void launchAction( LibCylinder aCylinder, Object artefact, float faceAngle )
        {
            //only if alive
            if ( iAliveState == BotAliveState.EAlive )
            {
                //check if collision appears
                if ( iCylinder.checkCollisionHorz( aCylinder ) )
                {
                    float anglePlayerToWall = LibMath.getAngleCorrect( Level.currentPlayer().getCylinder().getCenterHorz(), iCylinder.getCenterHorz() );
                    anglePlayerToWall = LibMath.normalizeAngle( -anglePlayerToWall );
                    float angleDistance     = LibMath.getAngleDistanceAbsolute( faceAngle, anglePlayerToWall );

                    //ShooterDebug.bugfix.out( "faceAngle [" + ( faceAngle ) + "] wall [" + anglePlayerToWall + "] " );
                    //ShooterDebug.bugfix.out( " delta [" + angleDistance + "]" );

                    //clip angle distance
                    if ( angleDistance < PlayerSettings.MAX_ACTION_VIEW_ANGLE )
                    {
                        if ( artefact == null )
                        {
                            ShooterDebug.playerAction.out( "launch action on bot" );

                            //LAUNCH all USE-actions
                            if ( iActions != null )
                            {
                                for ( BotAction action : iActions )
                                {
                                    if ( action instanceof BotUseAction )
                                    {
                                        BotUseAction ga = (BotUseAction)action;
                                        ga.iEvent.perform( this );
                                    }
                                }
                            }
                        }
                        else
                        {
                            ShooterDebug.playerAction.out( "launch gadget-action on bot" );

                            //CHECK all GIVE-actions
                            if ( iActions != null )
                            {
                                for ( BotAction action : iActions )
                                {
                                    if ( action instanceof BotGiveAction )
                                    {
                                        BotGiveAction ga = (BotGiveAction)action;

                                        //LAUNCH if KEY matches
                                        if ( ga.iKey == ( (Gadget)artefact ).iParentKind )
                                        {
                                            ga.iEvent.perform( this );
                                            Level.currentPlayer().iArtefactSet.extractArtefact( artefact );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        protected final boolean checkCollision( Ellipse2D.Float aEllipse )
        {
            return ( iAliveState == BotAliveState.EAlive ? iCylinder.checkCollision( aEllipse ) : false );
        }

        public final Point2D.Float getCenterHorz()
        {
            return iCylinder.getCenterHorz();
        }

        protected final void drawDebugCircles()
        {
            if ( ShooterDebug.DEBUG_DRAW_BOT_CIRCLES )
            {
                //Debug.bot.out( "drawing bot .." );
                int VERTICAL_SLICES = 4;
                LibVertex ank = getAnchor();
                LibColors col = null;
                switch ( iType )
                {
                    case ETypeFriend:   col = LibColors.EBlueLight;    break;
                    case ETypeEnemy:    col = LibColors.EBlueDark;      break;
                }
                for ( int i = 0; i <= VERTICAL_SLICES; ++i )
                {
                    new LibFaceEllipseFloor( ShooterDebug.face, null, col, ank.x, ank.y, ank.z + ( i * iCylinder.getHeight() / VERTICAL_SLICES ), iCylinder.getRadius(), iCylinder.getRadius(), ShooterSettings.Performance.ELLIPSE_SEGMENTS ).draw();
                }
            }
        }

        protected final float getHeight()
        {
            return iCylinder.getHeight();
        }

        protected final void setCenterHorz( float newX, float newY )
        {
            iCylinder.setNewAnchor( new LibVertex( newX, newY, iCylinder.getAnchor().z ), false, null );
        }

        public final Cylinder getCylinder()
        {
            return iCylinder;
        }

        protected final boolean checkCollisionsToOtherBots()
        {
            for ( Bot bot : Level.currentSection().getBots() )
            {
                //skip own !
                if ( this == bot ) continue;

                //check bot-collision with other bot
                if ( bot.checkCollision( iCylinder ) )
                {
                    ShooterDebug.bot.out( "own bot touched" );
                    return true;

                    //sleep if being touched
                    //bot.state = BotState.EStateSleeping;
                }
            }

            return false;
        }

        protected final boolean checkCollisionsToPlayer()
        {
            //check bot-collision with player
            if ( checkCollision( Level.currentPlayer().getCylinder().getCircle() ) )
            {
                ShooterDebug.bot.out( "player touched" );
                return true;

                //sleep if being touched
                //bot.state = BotState.EStateSleeping;
            }

            return false;
        }

        public final void animate()
        {
            boolean playerMoved         = false;

            switch ( iType )
            {
                case ETypeEnemy:
                case ETypeFriend:
                {

                    //Debug.bot.out( "angle player to bot is: " + anglePlayerToBot );
                    //Debug.bot.out( "angle bot to player is: " + angleBotToPlayer );

                    Point2D.Float bot = getCenterHorz();

                    float   nextPosX            = 0.0f;
                    float   nextPosY            = 0.0f;
                            iFaceAngleChanged   = false;

                    //check the bot's current job
                    switch ( iJobs.elementAt( 0 ) )
                    {
                        case ELeadPlayerToLastWaypoint:
                        {
                            //calculate bot's angle and distance to the player
                            Point2D.Float   player           = Level.currentPlayer().getCylinder().getCenterHorz();

                            //check distance from player to next waypoint and from bot to next waypoint
                            float   distancePlayerToNextWaypoint = (float)player.distance( iWayPoints[ iCurrentWayPointIndex ] );
                            float   distanceBotToNextWaypoint    = (float)bot.distance(    iWayPoints[ iCurrentWayPointIndex ] );
                            boolean playerOutOfBotReach          = ( (float)player.distance( bot ) > ShooterSettings.BotSettings.MAX_LEADING_DISTANCE_TO_PLAYER );

                            //wait for player if he is out of reach and farer from the next waypoint than the bot
                            if ( playerOutOfBotReach && distancePlayerToNextWaypoint > distanceBotToNextWaypoint )
                            {
                                //wait for player
                                iState = BotState.EWatchPlayer;
                            }
                            else
                            {
                                //walk to next waypoint
                                iState = BotState.EWalkToNextWayPoint;
                            }
                            break;
                        }

                        case EWalkWaypoints:
                        {
                            iState = BotState.EWalkToNextWayPoint;
                            break;
                        }

                        case EWatchPlayer:
                        {
                            iState = BotState.EWatchPlayer;
                            break;
                        }

                        case EStandStill:
                        {
                            iState = BotState.EStandStill;
                            break;
                        }

                        case EDying:
                        {
                            iState = BotState.EDying;
                            break;
                        }

                        case EAttackPlayerReload:
                        {
                            iState = BotState.EStandStill;

//                            iArtefactSet.iCurrentArtefact.reload( iAmmoSet, false, true, iCylinder.getCenterHorz() );
                            iArtefactSet.iCurrentArtefact.performReload( iAmmoSet, true, iCylinder.getCenterHorz(), true );

                            //init reload action
                            setNewJobQueue
                            (
                                new BotJob[]
                                {
                                    BotJob.ESequenceDelay,
                                    BotJob.ESequenceDelay,
                                    BotJob.ESequenceDelay,
                                    BotJob.EAttackPlayerFire,
                                }
                            );

                            break;
                        }

                        case EAttackPlayerFire:
                        {
                            iState = BotState.EWatchPlayer;

                            //check if bot's wearpon is delayed
                            if ( iArtefactSet.isWearponDelayed() )
                            {
                                //do nothing if the wearpon is delayed

                            }
                            //check if bot's wearpon is empty
                            else if ( iArtefactSet.isMagazineEmpty() )
                            {
                                //perform reload animation
                                setNewJobQueue
                                (
                                    new BotJob[]
                                    {
                                        BotJob.ESequenceReloadHandUp,
                                        BotJob.ESequenceDelay,
                                        BotJob.ESequenceReloadHandDown,
                                        BotJob.ESequenceDelay,
                                        BotJob.EAttackPlayerReload,
                                    }
                                );
                            }
                            else
                            {
                                //check if bot sees the player now
                                Point2D.Float   player           = Level.currentPlayer().getCylinder().getCenterHorz();
                                float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                                float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                                //ShooterDebug.bugfix.out( "angle [" + angleBotToPlayer + "] facing: " + iFacingAngle );
                                //ShooterDebug.bugfix.out( "attacking bot holds " + iArtefactSet.iCurrentArtefact.iArtefactType );

                                //check if bot looks into player's direction
                                if ( Math.abs( angleBotToPlayer - iFacingAngle ) <= BotSettings.TARGET_TURNING_TOLERANCE )
                                {
                                    if ( iBotMeshes.isAssignedArmsPosition( ( iHanded == BotHanded.ELeftHanded ? ArmsPosition.EAimHighLeft : ArmsPosition.EAimHighRight ) ) )
                                    {
                                        if
                                        (
                                                ( iBotMeshes.completedTargetPitchesLeftArm  && iHanded == BotHanded.ELeftHanded  )
                                            ||  ( iBotMeshes.completedTargetPitchesRightArm && iHanded == BotHanded.ERightHanded )
                                        )
                                        {
                                            //fire if bot sees the player and player is alive
                                            if
                                            (
                                                    !Level.currentPlayer().isDeadAnimationOver()
                                                &&  botSeesThePlayer()
                                            )
                                            {
                                                //fire
                                                iArtefactSet.iCurrentArtefact.fire( this, iCylinder.getCenterHorz() );
                                            }
                                        }
                                    }
                                    else
                                    {
                                        //assign
                                        iBotMeshes.assignArmsPosition( ( iHanded == BotHanded.ELeftHanded ? ArmsPosition.EAimHighLeft : ArmsPosition.EAimHighRight ) );
                                    }
                                }
                            }
                            break;
                        }

                        case ESequenceDelay:
                        {
                            if ( iInterAnimationsDelay <= 0 )
                            {
                                iInterAnimationsDelay = BotSettings.INTER_ANIMATIONS_DELAY;
                            }
                            else
                            {
                                if ( --iInterAnimationsDelay <= 0 )
                                {
                                    iInterAnimationsDelay = 0;
                                    iJobs.remove( 0 );
                                }
                            }
                            break;
                        }

                        case ESequenceTurnToPlayer:
                        {
                            //turn to player
                            iState = BotState.EWatchPlayer;

                            //check if bot sees the player now
                            Point2D.Float   player           = Level.currentPlayer().getCylinder().getCenterHorz();
                            float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                            float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                            //check if bot looks into player's direction
                            if ( Math.abs( angleBotToPlayer - iFacingAngle ) <= BotSettings.TARGET_TURNING_TOLERANCE )
                            {
                                iJobs.remove( 0 );
                            }
                            break;
                        }

                        case ESequenceNodOnce:
                        {
                            //check if assigned
                            if ( iBotMeshes.isAssignedHeadPosition( HeadPosition.ENodOnce ) )
                            {
                                if ( iBotMeshes.completedTargetPitchesHead )
                                {
                                    iBotMeshes.assignHeadPosition( HeadPosition.EStill );
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignHeadPosition( HeadPosition.ENodOnce );
                            }
                            break;
                        }

                        case ESequenceNodTwice:
                        {
                            //check if assigned
                            if ( iBotMeshes.isAssignedHeadPosition( HeadPosition.ENodTwice ) )
                            {
                                if ( iBotMeshes.completedTargetPitchesHead )
                                {
                                    iBotMeshes.assignHeadPosition( HeadPosition.EStill );
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignHeadPosition( HeadPosition.ENodTwice );
                            }
                            break;
                        }

                        case ESequenceGrabSpringRight:
                        {
                            //check if assigned
                            if ( iBotMeshes.isAssignedArmsPosition( ArmsPosition.EPickUpRight ) )
                            {
                                if ( iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignArmsPosition( ArmsPosition.EPickUpRight );
                            }
                            break;
                        }

                        case ESequenceFlushRightEquippedItem:
                        {
                            //draw item to bot
                            iBotMeshes.setItem( Arm.ERight, null,  getAnchor(), this );

                            //next job
                            iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceDeliverRightEquippedItem:
                        {
                            //give item to bot
                            iBotMeshes.setItem( Arm.ERight, iNextItemToDeliverRightHand,  getAnchor(), this );

                            //next job
                            iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceGrabBackDownRight:
                        {
                            //check if assigned
                            if ( iBotMeshes.isAssignedArmsPosition( ArmsPosition.EBackDownRight ) )
                            {
                                if ( iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignArmsPosition( ArmsPosition.EBackDownRight );
                            }
                            break;
                        }

                        case ESequenceGrabSpringLeft:
                        {
                            //check if assigned
                            if ( iBotMeshes.isAssignedArmsPosition( ArmsPosition.EPickUpLeft ) )
                            {
                                if ( iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignArmsPosition( ArmsPosition.EPickUpLeft );
                            }
                            break;
                        }

                        case ESequenceFlushLeftEquippedItem:
                        {
                            //draw item to bot
                            iBotMeshes.setItem( Arm.ELeft, null,  getAnchor(), this );

                            //next job
                            iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceDeliverLeftEquippedItem:
                        {
                            //give item to bot
                            iBotMeshes.setItem( Arm.ELeft, iNextItemToDeliverLeftHand,  getAnchor(), this );

                            //next job
                            iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceGrabBackDownLeft:
                        {
                            //check if assigned
                            if ( iBotMeshes.isAssignedArmsPosition( ArmsPosition.EBackDownLeft ) )
                            {
                                if ( iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignArmsPosition( ArmsPosition.EBackDownLeft );
                            }
                            break;
                        }

                        case ESequenceReloadHandUp:
                        {
                            iState = BotState.EStandStill;

                            //check if assigned
                            if ( iBotMeshes.isAssignedArmsPosition( ( iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftUp : ArmsPosition.EReloadRightUp ) ) )
                            {
                                if ( iHanded == BotHanded.ELeftHanded && iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    iJobs.remove( 0 );
                                }
                                else if ( iHanded == BotHanded.ERightHanded && iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignArmsPosition( ( iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftUp : ArmsPosition.EReloadRightUp ) );
                            }
                            break;
                        }

                        case ESequenceReloadHandDown:
                        {
                            iState = BotState.EStandStill;

                            //check if assigned
                            if ( iBotMeshes.isAssignedArmsPosition( ( iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftDown : ArmsPosition.EReloadRightDown ) ) )
                            {
                                if ( iHanded == BotHanded.ELeftHanded && iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    iJobs.remove( 0 );
                                }
                                else if ( iHanded == BotHanded.ERightHanded && iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                iBotMeshes.assignArmsPosition( ( iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftDown : ArmsPosition.EReloadRightDown ) );
                            }
                            break;
                        }
                    }

                    //perform action according to current state
                    switch ( iState )
                    {
                        case EStandStill:
                        {
                            //remain on position and do not change angle
                            nextPosX = bot.x;
                            nextPosY = bot.y;
                            break;
                        }

                        case EDying:
                        {
                            //bot is still
                            nextPosX = bot.x;
                            nextPosY = bot.y;
/*
                            //no use for turning to player!
                            if ( !iLeaveDeadZ )
                            {
                               //turn bot to player
                                Point2D.Float   player           = ShooterGameLevel.currentPlayer().getCylinder().getCenterHorz();
                                float           angleBotToPlayer = LibMath.getAngleCorrect( getCenterHorz(), player );

                                rotateBotTo( angleBotToPlayer );

                                if ( !iFaceAngleChanged )
                                {
                                    iLeaveDeadZ = true;
                                }
                            }
*/
                            break;
                        }

                        case EWatchPlayer:
                        {
                            //calculate bot's angle and distance to the player
                            Point2D.Float   player           = Level.currentPlayer().getCylinder().getCenterHorz();
                            float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                            float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                            //bot is standing still turning to the player
                            nextPosX = bot.x;
                            nextPosY = bot.y;

                            final float MAX_BOT_WATCH_RADIUS = 10.0f;

                            float botDistanceToPlayer = (float)player.distance( bot );
                            if ( angleBotToPlayer != iFacingAngle && botDistanceToPlayer < MAX_BOT_WATCH_RADIUS )
                            {
                                rotateBotTo( angleBotToPlayer );
                            }
                            break;
                        }

                        case EWalkTowardsPlayer:
                        {
                            //calculate bot's angle and distance to the player
                            Point2D.Float   player           = Level.currentPlayer().getCylinder().getCenterHorz();
                            float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                            float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                            //bot walks towards the player - turning to him
                            nextPosX = bot.x - LibMath.sinDeg( -angleBotToPlayer ) * BotSettings.SPEED_WALKING;
                            nextPosY = bot.y - LibMath.cosDeg( -angleBotToPlayer ) * BotSettings.SPEED_WALKING;
                            if ( angleBotToPlayer != iFacingAngle ) iFaceAngleChanged = true;
                            iFacingAngle = angleBotToPlayer;
                            break;
                        }

                        case EWalkToNextWayPoint:
                        {
                            //shouldn't have been initialized without wayPoints ..
                            if ( iWayPoints != null )
                            {
                                //check if the current wayPoint has been reached?
                                if ( checkCollision( new Ellipse2D.Float( iWayPoints[ iCurrentWayPointIndex ].x - BotSettings.WAY_POINT_RADIUS, iWayPoints[ iCurrentWayPointIndex ].y - BotSettings.WAY_POINT_RADIUS, 2 * BotSettings.WAY_POINT_RADIUS, 2 * BotSettings.WAY_POINT_RADIUS ) ) )
                                {
                                    //wayPoint has been reached
                                    //Debug.info( "wayPoint reached" );

                                    //target next wayPoint
                                    ++iCurrentWayPointIndex;

                                    //begin with 1st waypoint
                                    if ( iCurrentWayPointIndex >= iWayPoints.length ) iCurrentWayPointIndex = 0;
                                }

                                //set arms and legs walking
                                if ( !iBotMeshes.isAssignedLegsPosition( LegsPosition.EWalk ) )
                                {
                                    iBotMeshes.assignLegsPosition( LegsPosition.EWalk );
                                }
                                if ( !iBotMeshes.isAssignedArmsPosition( ArmsPosition.EWalk ) )
                                {
                                    iBotMeshes.assignArmsPosition( ArmsPosition.EWalk );
                                }

                                //move player towards current waypoint
                                Point2D.Float currentWayPoint = iWayPoints[ iCurrentWayPointIndex ];

                                //move bot towards the wayPoint
                                float angleWaypointToBot = LibMath.getAngleCorrect( currentWayPoint, bot );
                                float angleBotToWayPoint = LibMath.normalizeAngle( angleWaypointToBot - 180.0f );
                                //Debug.bot.out( "angle player to bot is: " + anglePlayerToBot );
                                //Debug.bot.out( "angle bot to player is: " + angleBotToPlayer );

                                //bot walks towards current waypoint - turning to him
                                nextPosX = bot.x - LibMath.sinDeg( -angleBotToWayPoint ) * BotSettings.SPEED_WALKING;
                                nextPosY = bot.y - LibMath.cosDeg( -angleBotToWayPoint ) * BotSettings.SPEED_WALKING;

                                //order rotation to
                                rotateBotTo( angleBotToWayPoint );
                            }
/*
                            //check if this bot sees the player
                            if ( botSeesThePlayer( -angleBotToPlayer ) )
                            {
                                //player sighted
                            }
*/
                            break;
                        }
                    }

                    //check collisions
                    float oldPosX = getCenterHorz().x;
                    float oldPosY = getCenterHorz().y;

                    //set to new position ( if position changed! )
                    if ( oldPosX != nextPosX || oldPosY != nextPosY )
                    {
                        setCenterHorz( nextPosX, nextPosY );

                        //check collisions to other bots
                        if ( checkCollisionsToOtherBots() )
                        {
                            //set back to old position
                            setCenterHorz( oldPosX, oldPosY );
                            playerMoved = false;
                        }
                        //check collisions to player
                        else if ( checkCollisionsToPlayer() )
                        {
                            //undo setting to new position
                            setCenterHorz( oldPosX, oldPosY );
                            playerMoved = false;
                        }
                        else
                        {
                            //leave the new position
                            playerMoved = true;

                            //translate all bullet holes to new position
                            BulletHole.translateAll( this, nextPosX - oldPosX, nextPosY - oldPosY, 0.0f );
                        }
                    }

                    //rotate if faceAngle changed
                    if ( iFaceAngleChanged )
                    {
                        BulletHole.rotateForBot( this, iFacingAngle );
                    }
                    break;
                }
            }

            //perform transformations here

            //let eyes blink ( or close on dying )
            blinkEyes();

            //translate and rotate the bot's mesh
            LibVertex botAnchor = getAnchor().copy();

            //translate anchor if desired
            botAnchor.z += iOffsetZ;

            //set anchors for all meshes!
            iBotMeshes.setNewAnchor( botAnchor, false, null );

            //animate dying
            animateDying();

            //transform bot's limbs
            iBotMeshes.transformLimbs( iFacingAngle, iDyingAngle, iFaceAngleChanged, playerMoved );
        }

        public final void draw()
        {
            switch ( iType )
            {
                case ETypeEnemy:
                case ETypeFriend:
                {
                    //turn bullet holes?? :(

                    //draw bot's mesh
                    iBotMeshes.draw();

                    //draw bot's debug-circles
                    drawDebugCircles();

                    break;
                }
            }
        }

        public final LibShot getShot( float modZ )
        {
            return new LibShot
            (
                ShotType.ESharpAmmo,
                LibShot.ShotOrigin.EEnemies,

                LibMath.getRandom( -10, 10 ) + modZ,       // 0.0f,   //irregularityHorz
                LibMath.getRandom( -2,  2  ),       // 0.0f,   //irregularityVert

                getAnchor().x,
                getAnchor().y,
                getAnchor().z + PlayerSettings.DEPTH_HAND_STANDING,   // take hand height from player
                180.0f + ( 180.0f - iFacingAngle ),     //rotZ
                0.0f,                                   //rotX
                BotSettings.SHOT_RANGE,    // bot has constant shot range ???
                LibHoleSize.E44mm,   // bot always fires 9mm    ??
                ShooterDebug.shotAndHit,
                ArtefactType.EMagnum357.iArtefactKind.getSliverParticleQuantity(),
                FxSettings.SLIVER_ANGLE_MOD,
                10,
                ArtefactType.EMagnum357.iArtefactKind.getSliverParticleSize(),
                ArtefactType.EMagnum357.getBreaksWalls(),
                ( (FireArm)ArtefactType.EMagnum357.iArtefactKind ).getProjectile(),
                General.FADE_OUT_FACES_TOTAL_TICKS
            );
        }

        public final LibShot getViewShot()
        {
            return new LibShot
            (
                ShotType.EViewOnly,
                LibShot.ShotOrigin.EEnemies,
                0.0f,   //irregularityHorz
                0.0f,   //irregularityVert
                getAnchor().x,
                getAnchor().y,
                getAnchor().z + ( getHeight() * 3 / 4 ),
                180.0f + ( 180.0f - iFacingAngle ),     //rotZ
                0.0f,                                   //rotX
                BotSettings.VIEW_RANGE,
                LibHoleSize.ENone,
                ShooterDebug.shotAndHit,
                ArtefactType.EMagnum357.iArtefactKind.getSliverParticleQuantity(),
                FxSettings.SLIVER_ANGLE_MOD,
                0,
                ArtefactType.EMagnum357.iArtefactKind.getSliverParticleSize(),
                false,
                null,
                General.FADE_OUT_FACES_TOTAL_TICKS
            );
        }

        public final boolean botSeesThePlayer()
        {
            //check if this enemy bot sees the player - copy current angle
            LibShot       shot     = getViewShot();
            LibHitPoint[] hitPoint = Level.currentSection().launchShot( shot );

            //draw view shot line
            //shot.drawShotLine( FxSettings.LIFETIME_DEBUG );

            return ( hitPoint.length > 0 && hitPoint[ 0 ].iCarrier.getHitPointCarrier() == HitPointCarrier.EPlayer );
        }

        public final LibVertex getAnchor()
        {
            return iCylinder.getAnchor();
        }

        public final float getCarriersFaceAngle()
        {
            return iFacingAngle;
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            return iBotMeshes.launchShot( shot );
        }

        public final boolean isDead()
        {
            return ( iAliveState == BotAliveState.EDead );
        }

        public HitPointCarrier getHitPointCarrier()
        {
            return HitPointCarrier.EBot;
        }

        private void rotateBotTo( float targetAngle )
        {
            //get the rotation of the bot
            float angleDistance   = LibMath.getAngleDistanceRelative( iFacingAngle, targetAngle );
            float turningDistance = Math.abs( angleDistance );

            if ( turningDistance >= BotSettings.SPEED_TURNING_MIN )
            {
                //clip turning distance
                if ( turningDistance > BotSettings.SPEED_TURNING_MAX ) turningDistance = BotSettings.SPEED_TURNING_MAX;

                //ShooterDebug.bot.out( "turning bot, src ["+iFacingAngle+"] target ["+targetAngle+"] distance is [" + angleDistance + "]" );

                iFaceAngleChanged = true;

                if ( angleDistance < 0 )
                {
                    iFacingAngle = LibMath.normalizeAngle( iFacingAngle - turningDistance );
                }
                else if ( angleDistance > 0 )
                {
                    iFacingAngle = LibMath.normalizeAngle( iFacingAngle + turningDistance );
                }
            }
        }

        public void makeDistancedSound( SoundFg fx )
        {
            fx.playDistancedFx( getCenterHorz() );
        }

        private void blinkEyes()
        {
            if ( iAliveState != BotAliveState.EAlive )
            {
                if ( iEyesOpen )
                {
                    iBotMeshes.changeFaceTexture( iBotMeshes.iTemplate.iTexFaceEyesOpen.getTexture(), iBotMeshes.iTemplate.iTexFaceEyesShut.getTexture() );
                    iEyesOpen = false;
                }
            }
            //let eyes blink
            else if ( System.currentTimeMillis() >= iNextEyeChange )
            {
                iEyesOpen = !iEyesOpen;
                if ( iEyesOpen )
                {
                    iBotMeshes.changeFaceTexture( iBotMeshes.iTemplate.iTexFaceEyesShut.getTexture(), iBotMeshes.iTemplate.iTexFaceEyesOpen.getTexture() );
                }
                else
                {
                    iBotMeshes.changeFaceTexture( iBotMeshes.iTemplate.iTexFaceEyesOpen.getTexture(), iBotMeshes.iTemplate.iTexFaceEyesShut.getTexture() );
                }
                setNextEyeChange();
            }
        }

        public void fallAsleep()
        {
            if ( iAliveState == BotAliveState.EAlive )
            {
                killOrTranquilize( BotAliveState.ETranquilized );
            }
        }

        public void hurt( int damage )
        {
            iHealth -= damage;

            //bot dies?
            if ( iAliveState != BotAliveState.EDead && iHealth <= 0 )
            {
                killOrTranquilize( BotAliveState.EDead );
            }
        }

        private void animateDying()
        {
            switch ( iAliveState )
            {
                case EAlive:
                {
                    break;
                }

                case ETranquilized:
                case EDead:
                {
                    switch ( iDyingDirection )
                    {
                        case EFront:
                        {
                            if ( iDyingAngle < 10.0f  )
                            {
                                iDyingAngle += 1.0f;

                                if ( iOffsetZ < iTargetOffsetZ )
                                {
                                    iOffsetZ += iTargetOffsetZ / 45;
                                }
                            }
                            else
                            {
                                iDyingAngle += 10.0f;

                                if ( iOffsetZ < iTargetOffsetZ )
                                {
                                    iOffsetZ += iTargetOffsetZ / 9;
                                }
                            }

                            if ( iDyingAngle >= 90.0f ) iDyingAngle = 90.0f;
                            break;
                        }

                        case EBack:
                        {
                            if ( iDyingAngle > -10.0f  )
                            {
                                iDyingAngle -= 1.0f;

                                if ( iOffsetZ < iTargetOffsetZ )
                                {
                                    iOffsetZ += iTargetOffsetZ / 45;
                                }
                            }
                            else
                            {
                                iDyingAngle -= 10.0f;

                                if ( iOffsetZ < iTargetOffsetZ )
                                {
                                    iOffsetZ += iTargetOffsetZ / 9;
                                }
                            }
                            if ( iDyingAngle <= -90.0f ) iDyingAngle = -90.0f;
                            break;
                        }
                    }
                }
            }
        }

        public void killOrTranquilize( BotAliveState newState )
        {
            if ( iAliveState != BotAliveState.ETranquilized )
            {
                //start disappear timer ( will only count for dead bots ) - only if bot was
                iDisappearTimer = FxSettings.LIFETIME_CORPSE;
                iDyingDirection = DyingDirection.values()[ LibMath.getRandom( 0, DyingDirection.values().length - 1 ) ];
                iBotMeshes.assignArmsPosition( ArmsPosition.EDownBoth           );
                iBotMeshes.assignLegsPosition( LegsPosition.EStandSpreadLegged  );
                iBotMeshes.assignHeadPosition( HeadPosition.EStill              );
                switch ( iDyingDirection )
                {
                    case EBack:
                    {
                        iTargetOffsetZ = 0.05f;
                        break;
                    }
                    case EFront:
                    {
                        iTargetOffsetZ = 0.15f;
                        break;
                    }
                }
            }

            //let bot die
            iHealth         = 0;
            iAliveState     = newState;

            //drop all artefacts and set dying
            dropAllArtefacts();
            setNewJobQueue( new BotJob[] { BotJob.EDying } );
        }

        public void setNewJobQueue( BotJob[] newJobs )
        {
            iJobs.removeAllElements();
            iJobs.addAll( Arrays.asList( newJobs ) );
        }

        public void fadeOutAllFaces()
        {
            iBotMeshes.fadeOutAllFaces();
        }

        public final int getHealth()
        {
            return iHealth;
        }

        public final void dropAllArtefacts()
        {
            //turn artefacts to pickable items
            for ( Artefact toDrop : iArtefactSet.iArtefacts )
            {
                ItemToPickUp p = toDrop.getPickUpItem( getAnchor() );
                if ( p != null )
                {
                    //p.loadD3ds(); //old solution
                    iBotMeshes.iEquippedItemRight.setTranslatedAsOriginalVertices();
                    p.assignMesh( iBotMeshes.iEquippedItemRight );
                    p.iDropTarget = getAnchor().z;
                    p.iDropBegin  = iBotMeshes.iEquippedItemRight.getCenterZ();
                    Level.currentSection().addItem( p );
                }
            }

            //strip off artefacts
            iBotMeshes.setItem( Arm.ERight, null,  getAnchor(), this );
            iBotMeshes.setItem( Arm.ELeft,  null,  getAnchor(), this );
        }
    }
