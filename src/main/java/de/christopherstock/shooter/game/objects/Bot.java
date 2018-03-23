
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
                this.iEnergy = aEnergy;
            }
        }

        public enum DyingDirection
        {
            EFront,
            EBack,
            ;
        }

        public interface BotAction
        {
        }

        public static final class BotUseAction implements BotAction
        {
            public          BotEvent        iEvent        = null;

            public BotUseAction( BotEvent aEvent )
            {
                this.iEvent = aEvent;
            }
        }

        public static final class BotGiveAction implements BotAction
        {
            public          ArtefactType        iKey        = null;
            public          BotEvent            iEvent      = null;

            public BotGiveAction( ArtefactType aKey, BotEvent aEvent )
            {
                this.iKey = aKey;
                this.iEvent = aEvent;
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
            this.iType = aType;
            this.iStartPosition = aStartPosition.copy();
            this.iCylinder = new Cylinder( this, this.iStartPosition, aTemplate.iBase.iRadius, aTemplate.iBase.iHeight, 0, ShooterDebug.bot, ShooterDebug.DEBUG_DRAW_BOT_CIRCLES, 0.0f, 0.0f, ShooterSettings.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh );
            this.iWayPoints = aWayPoints;
            this.iJobs = new Vector<BotJob>();
            this.iJobs.add(      aJob );
            this.iHealth = aBotHealth.iEnergy;
            this.iTemplate = aTemplate;
            this.iActions = aActions;
            this.iID = aID;
            this.iHanded = aBotHanded;
            this.iAliveState = BotAliveState.EAlive;

            //set startup facing angle
            this.iFacingAngle = aFacingAngle;

            this.iAmmoSet = new AmmoSet();
            this.iArtefactSet = new ArtefactSet();

            //deliver all artefacts if given
            if ( aArtefacts != null )
            {
                for ( Artefact a : aArtefacts )
                {
                    this.iArtefactSet.deliverArtefact( a );
                }

                this.iArtefactSet.chooseNextWearponOrGadget( false );
                this.iArtefactSet.iCurrentArtefact.reload(this.iAmmoSet, false, false, null );
            }

            Items leftItem  = null;
            Items rightItem = null;

            switch (this.iHanded)
            {
                case ELeftHanded:
                {
                    leftItem = this.iArtefactSet.iCurrentArtefact.iArtefactType.iItemMesh;
                    break;
                }

                case ERightHanded:
                {
                    rightItem = this.iArtefactSet.iCurrentArtefact.iArtefactType.iItemMesh;
                    break;
                }
            }

            //init mesh-collection
            this.iBotMeshes = new BotMeshes(this.iTemplate, this.iStartPosition, this, leftItem, rightItem );

            //set next eye blink
            this.iEyesOpen = true;
            this.setNextEyeChange();

            //animate 1st tick? .. triggers render :/ not required!
            boolean deprecatedAnimateOnCreate = false;
            if ( deprecatedAnimateOnCreate ) this.animate();
        }

        private void setNextEyeChange()
        {
            //only if not dead
            if (this.iAliveState == BotAliveState.EAlive )
            {
                this.iNextEyeChange =
                (
                        System.currentTimeMillis()
                    +   (
                                this.iEyesOpen
                            ?   LibMath.getRandom( BotSettings.EYE_BLINK_INTERVAL_MIN, BotSettings.EYE_BLINK_INTERVAL_MAX )
                            :   BotSettings.EYE_CLOSED_INTERVAL
                        )
                );
            }
        }

        public final boolean checkCollision( Cylinder aCylinder )
        {
            return (this.iAliveState == BotAliveState.EAlive ? this.iCylinder.checkCollisionHorz( aCylinder ) : false );
        }

        public final void launchAction( LibCylinder aCylinder, Object artefact, float faceAngle )
        {
            //only if alive
            if (this.iAliveState == BotAliveState.EAlive )
            {
                //check if collision appears
                if (this.iCylinder.checkCollisionHorz( aCylinder ) )
                {
                    float anglePlayerToWall = LibMath.getAngleCorrect( Shooter.game.engine.player.getCylinder().getCenterHorz(), this.iCylinder.getCenterHorz() );
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
                            if (this.iActions != null )
                            {
                                for ( BotAction action : this.iActions)
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
                            if (this.iActions != null )
                            {
                                for ( BotAction action : this.iActions)
                                {
                                    if ( action instanceof BotGiveAction )
                                    {
                                        BotGiveAction ga = (BotGiveAction)action;

                                        //LAUNCH if KEY matches
                                        if ( ga.iKey == ( (Gadget)artefact ).iParentKind )
                                        {
                                            ga.iEvent.perform( this );
                                            Shooter.game.engine.player.iArtefactSet.extractArtefact( artefact );
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
            return (this.iAliveState == BotAliveState.EAlive ? this.iCylinder.checkCollision( aEllipse ) : false );
        }

        public final Point2D.Float getCenterHorz()
        {
            return this.iCylinder.getCenterHorz();
        }

        protected final void drawDebugCircles()
        {
            if ( ShooterDebug.DEBUG_DRAW_BOT_CIRCLES )
            {
                //Debug.bot.out( "drawing bot .." );
                int VERTICAL_SLICES = 4;
                LibVertex ank = this.getAnchor();
                LibColors col = null;
                switch (this.iType)
                {
                    case ETypeFriend:   col = LibColors.EBlueLight;    break;
                    case ETypeEnemy:    col = LibColors.EBlueDark;      break;
                }
                for ( int i = 0; i <= VERTICAL_SLICES; ++i )
                {
                    new LibFaceEllipseFloor( ShooterDebug.face, null, col, ank.x, ank.y, ank.z + ( i * this.iCylinder.getHeight() / VERTICAL_SLICES ), this.iCylinder.getRadius(), this.iCylinder.getRadius(), ShooterSettings.Performance.ELLIPSE_SEGMENTS ).draw();
                }
            }
        }

        protected final float getHeight()
        {
            return this.iCylinder.getHeight();
        }

        protected final void setCenterHorz( float newX, float newY )
        {
            this.iCylinder.setNewAnchor( new LibVertex( newX, newY, this.iCylinder.getAnchor().z ), false, null );
        }

        public final Cylinder getCylinder()
        {
            return this.iCylinder;
        }

        protected final boolean checkCollisionsToOtherBots()
        {
            for ( Bot bot : Level.currentSection().getBots() )
            {
                //skip own !
                if ( this == bot ) continue;

                //check bot-collision with other bot
                if ( bot.checkCollision(this.iCylinder) )
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
            if (this.checkCollision( Shooter.game.engine.player.getCylinder().getCircle() ) )
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

            switch (this.iType)
            {
                case ETypeEnemy:
                case ETypeFriend:
                {

                    //Debug.bot.out( "angle player to bot is: " + anglePlayerToBot );
                    //Debug.bot.out( "angle bot to player is: " + angleBotToPlayer );

                    Point2D.Float bot = this.getCenterHorz();

                    float   nextPosX            = 0.0f;
                    float   nextPosY            = 0.0f;
                    this.iFaceAngleChanged = false;

                    //check the bot's current job
                    switch (this.iJobs.elementAt( 0 ) )
                    {
                        case ELeadPlayerToLastWaypoint:
                        {
                            //calculate bot's angle and distance to the player
                            Point2D.Float   player           = Shooter.game.engine.player.getCylinder().getCenterHorz();

                            //check distance from player to next waypoint and from bot to next waypoint
                            float   distancePlayerToNextWaypoint = (float)player.distance(this.iWayPoints[this.iCurrentWayPointIndex] );
                            float   distanceBotToNextWaypoint    = (float)bot.distance(this.iWayPoints[this.iCurrentWayPointIndex] );
                            boolean playerOutOfBotReach          = ( (float)player.distance( bot ) > ShooterSettings.BotSettings.MAX_LEADING_DISTANCE_TO_PLAYER );

                            //wait for player if he is out of reach and farer from the next waypoint than the bot
                            if ( playerOutOfBotReach && distancePlayerToNextWaypoint > distanceBotToNextWaypoint )
                            {
                                //wait for player
                                this.iState = BotState.EWatchPlayer;
                            }
                            else
                            {
                                //walk to next waypoint
                                this.iState = BotState.EWalkToNextWayPoint;
                            }
                            break;
                        }

                        case EWalkWaypoints:
                        {
                            this.iState = BotState.EWalkToNextWayPoint;
                            break;
                        }

                        case EWatchPlayer:
                        {
                            this.iState = BotState.EWatchPlayer;
                            break;
                        }

                        case EStandStill:
                        {
                            this.iState = BotState.EStandStill;
                            break;
                        }

                        case EDying:
                        {
                            this.iState = BotState.EDying;
                            break;
                        }

                        case EAttackPlayerReload:
                        {
                            this.iState = BotState.EStandStill;

//                            iArtefactSet.iCurrentArtefact.reload( iAmmoSet, false, true, iCylinder.getCenterHorz() );
                            this.iArtefactSet.iCurrentArtefact.performReload(this.iAmmoSet, true, this.iCylinder.getCenterHorz(), true );

                            //init reload action
                            this.setNewJobQueue
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
                            this.iState = BotState.EWatchPlayer;

                            //check if bot's wearpon is delayed
                            if (this.iArtefactSet.isWearponDelayed() )
                            {
                                //do nothing if the wearpon is delayed

                            }
                            //check if bot's wearpon is empty
                            else if (this.iArtefactSet.isMagazineEmpty() )
                            {
                                //perform reload animation
                                this.setNewJobQueue
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
                                Point2D.Float   player           = Shooter.game.engine.player.getCylinder().getCenterHorz();
                                float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                                float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                                //ShooterDebug.bugfix.out( "angle [" + angleBotToPlayer + "] facing: " + iFacingAngle );
                                //ShooterDebug.bugfix.out( "attacking bot holds " + iArtefactSet.iCurrentArtefact.iArtefactType );

                                //check if bot looks into player's direction
                                if ( Math.abs( angleBotToPlayer - this.iFacingAngle) <= BotSettings.TARGET_TURNING_TOLERANCE )
                                {
                                    if (this.iBotMeshes.isAssignedArmsPosition( (this.iHanded == BotHanded.ELeftHanded ? ArmsPosition.EAimHighLeft : ArmsPosition.EAimHighRight ) ) )
                                    {
                                        if
                                        (
                                                (this.iBotMeshes.completedTargetPitchesLeftArm  && this.iHanded == BotHanded.ELeftHanded  )
                                            ||  (this.iBotMeshes.completedTargetPitchesRightArm && this.iHanded == BotHanded.ERightHanded )
                                        )
                                        {
                                            //fire if bot sees the player and player is alive
                                            if
                                            (
                                                    !Shooter.game.engine.player.isDeadAnimationOver()
                                                && this.botSeesThePlayer()
                                            )
                                            {
                                                //fire
                                                this.iArtefactSet.iCurrentArtefact.fire( this, this.iCylinder.getCenterHorz() );
                                            }
                                        }
                                    }
                                    else
                                    {
                                        //assign
                                        this.iBotMeshes.assignArmsPosition( (this.iHanded == BotHanded.ELeftHanded ? ArmsPosition.EAimHighLeft : ArmsPosition.EAimHighRight ) );
                                    }
                                }
                            }
                            break;
                        }

                        case ESequenceDelay:
                        {
                            if (this.iInterAnimationsDelay <= 0 )
                            {
                                this.iInterAnimationsDelay = BotSettings.INTER_ANIMATIONS_DELAY;
                            }
                            else
                            {
                                if ( --this.iInterAnimationsDelay <= 0 )
                                {
                                    this.iInterAnimationsDelay = 0;
                                    this.iJobs.remove( 0 );
                                }
                            }
                            break;
                        }

                        case ESequenceTurnToPlayer:
                        {
                            //turn to player
                            this.iState = BotState.EWatchPlayer;

                            //check if bot sees the player now
                            Point2D.Float   player           = Shooter.game.engine.player.getCylinder().getCenterHorz();
                            float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                            float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                            //check if bot looks into player's direction
                            if ( Math.abs( angleBotToPlayer - this.iFacingAngle) <= BotSettings.TARGET_TURNING_TOLERANCE )
                            {
                                this.iJobs.remove( 0 );
                            }
                            break;
                        }

                        case ESequenceNodOnce:
                        {
                            //check if assigned
                            if (this.iBotMeshes.isAssignedHeadPosition( HeadPosition.ENodOnce ) )
                            {
                                if (this.iBotMeshes.completedTargetPitchesHead )
                                {
                                    this.iBotMeshes.assignHeadPosition( HeadPosition.EStill );
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignHeadPosition( HeadPosition.ENodOnce );
                            }
                            break;
                        }

                        case ESequenceNodTwice:
                        {
                            //check if assigned
                            if (this.iBotMeshes.isAssignedHeadPosition( HeadPosition.ENodTwice ) )
                            {
                                if (this.iBotMeshes.completedTargetPitchesHead )
                                {
                                    this.iBotMeshes.assignHeadPosition( HeadPosition.EStill );
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignHeadPosition( HeadPosition.ENodTwice );
                            }
                            break;
                        }

                        case ESequenceGrabSpringRight:
                        {
                            //check if assigned
                            if (this.iBotMeshes.isAssignedArmsPosition( ArmsPosition.EPickUpRight ) )
                            {
                                if (this.iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignArmsPosition( ArmsPosition.EPickUpRight );
                            }
                            break;
                        }

                        case ESequenceFlushRightEquippedItem:
                        {
                            //draw item to bot
                            this.iBotMeshes.setItem( Arm.ERight, null, this.getAnchor(), this );

                            //next job
                            this.iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceDeliverRightEquippedItem:
                        {
                            //give item to bot
                            this.iBotMeshes.setItem( Arm.ERight, this.iNextItemToDeliverRightHand, this.getAnchor(), this );

                            //next job
                            this.iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceGrabBackDownRight:
                        {
                            //check if assigned
                            if (this.iBotMeshes.isAssignedArmsPosition( ArmsPosition.EBackDownRight ) )
                            {
                                if (this.iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignArmsPosition( ArmsPosition.EBackDownRight );
                            }
                            break;
                        }

                        case ESequenceGrabSpringLeft:
                        {
                            //check if assigned
                            if (this.iBotMeshes.isAssignedArmsPosition( ArmsPosition.EPickUpLeft ) )
                            {
                                if (this.iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignArmsPosition( ArmsPosition.EPickUpLeft );
                            }
                            break;
                        }

                        case ESequenceFlushLeftEquippedItem:
                        {
                            //draw item to bot
                            this.iBotMeshes.setItem( Arm.ELeft, null, this.getAnchor(), this );

                            //next job
                            this.iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceDeliverLeftEquippedItem:
                        {
                            //give item to bot
                            this.iBotMeshes.setItem( Arm.ELeft, this.iNextItemToDeliverLeftHand, this.getAnchor(), this );

                            //next job
                            this.iJobs.remove( 0 );

                            break;
                        }

                        case ESequenceGrabBackDownLeft:
                        {
                            //check if assigned
                            if (this.iBotMeshes.isAssignedArmsPosition( ArmsPosition.EBackDownLeft ) )
                            {
                                if (this.iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignArmsPosition( ArmsPosition.EBackDownLeft );
                            }
                            break;
                        }

                        case ESequenceReloadHandUp:
                        {
                            this.iState = BotState.EStandStill;

                            //check if assigned
                            if (this.iBotMeshes.isAssignedArmsPosition( (this.iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftUp : ArmsPosition.EReloadRightUp ) ) )
                            {
                                if (this.iHanded == BotHanded.ELeftHanded && this.iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                                else if (this.iHanded == BotHanded.ERightHanded && this.iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignArmsPosition( (this.iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftUp : ArmsPosition.EReloadRightUp ) );
                            }
                            break;
                        }

                        case ESequenceReloadHandDown:
                        {
                            this.iState = BotState.EStandStill;

                            //check if assigned
                            if (this.iBotMeshes.isAssignedArmsPosition( (this.iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftDown : ArmsPosition.EReloadRightDown ) ) )
                            {
                                if (this.iHanded == BotHanded.ELeftHanded && this.iBotMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                                else if (this.iHanded == BotHanded.ERightHanded && this.iBotMeshes.completedTargetPitchesRightArm )
                                {
                                    this.iJobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.iBotMeshes.assignArmsPosition( (this.iHanded == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftDown : ArmsPosition.EReloadRightDown ) );
                            }
                            break;
                        }
                    }

                    //perform action according to current state
                    switch (this.iState)
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
                                Point2D.Float   player           = ShooterGameShooter.game.engine.player.getCylinder().getCenterHorz();
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
                            Point2D.Float   player           = Shooter.game.engine.player.getCylinder().getCenterHorz();
                            float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                            float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                            //bot is standing still turning to the player
                            nextPosX = bot.x;
                            nextPosY = bot.y;

                            final float MAX_BOT_WATCH_RADIUS = 10.0f;

                            float botDistanceToPlayer = (float)player.distance( bot );
                            if ( angleBotToPlayer != this.iFacingAngle && botDistanceToPlayer < MAX_BOT_WATCH_RADIUS )
                            {
                                this.rotateBotTo( angleBotToPlayer );
                            }
                            break;
                        }

                        case EWalkTowardsPlayer:
                        {
                            //calculate bot's angle and distance to the player
                            Point2D.Float   player           = Shooter.game.engine.player.getCylinder().getCenterHorz();
                            float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                            float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                            //bot walks towards the player - turning to him
                            nextPosX = bot.x - LibMath.sinDeg( -angleBotToPlayer ) * BotSettings.SPEED_WALKING;
                            nextPosY = bot.y - LibMath.cosDeg( -angleBotToPlayer ) * BotSettings.SPEED_WALKING;
                            if ( angleBotToPlayer != this.iFacingAngle) this.iFaceAngleChanged = true;
                            this.iFacingAngle = angleBotToPlayer;
                            break;
                        }

                        case EWalkToNextWayPoint:
                        {
                            //shouldn't have been initialized without wayPoints ..
                            if (this.iWayPoints != null )
                            {
                                //check if the current wayPoint has been reached?
                                if (this.checkCollision( new Ellipse2D.Float(this.iWayPoints[this.iCurrentWayPointIndex].x - BotSettings.WAY_POINT_RADIUS, this.iWayPoints[this.iCurrentWayPointIndex].y - BotSettings.WAY_POINT_RADIUS, 2 * BotSettings.WAY_POINT_RADIUS, 2 * BotSettings.WAY_POINT_RADIUS ) ) )
                                {
                                    //wayPoint has been reached
                                    //Debug.info( "wayPoint reached" );

                                    //target next wayPoint
                                    ++this.iCurrentWayPointIndex;

                                    //begin with 1st waypoint
                                    if (this.iCurrentWayPointIndex >= this.iWayPoints.length ) this.iCurrentWayPointIndex = 0;
                                }

                                //set arms and legs walking
                                if ( !this.iBotMeshes.isAssignedLegsPosition( LegsPosition.EWalk ) )
                                {
                                    this.iBotMeshes.assignLegsPosition( LegsPosition.EWalk );
                                }
                                if ( !this.iBotMeshes.isAssignedArmsPosition( ArmsPosition.EWalk ) )
                                {
                                    this.iBotMeshes.assignArmsPosition( ArmsPosition.EWalk );
                                }

                                //move player towards current waypoint
                                Point2D.Float currentWayPoint = this.iWayPoints[this.iCurrentWayPointIndex];

                                //move bot towards the wayPoint
                                float angleWaypointToBot = LibMath.getAngleCorrect( currentWayPoint, bot );
                                float angleBotToWayPoint = LibMath.normalizeAngle( angleWaypointToBot - 180.0f );
                                //Debug.bot.out( "angle player to bot is: " + anglePlayerToBot );
                                //Debug.bot.out( "angle bot to player is: " + angleBotToPlayer );

                                //bot walks towards current waypoint - turning to him
                                nextPosX = bot.x - LibMath.sinDeg( -angleBotToWayPoint ) * BotSettings.SPEED_WALKING;
                                nextPosY = bot.y - LibMath.cosDeg( -angleBotToWayPoint ) * BotSettings.SPEED_WALKING;

                                //order rotation to
                                this.rotateBotTo( angleBotToWayPoint );
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
                    float oldPosX = this.getCenterHorz().x;
                    float oldPosY = this.getCenterHorz().y;

                    //set to new position ( if position changed! )
                    if ( oldPosX != nextPosX || oldPosY != nextPosY )
                    {
                        this.setCenterHorz( nextPosX, nextPosY );

                        //check collisions to other bots
                        if (this.checkCollisionsToOtherBots() )
                        {
                            //set back to old position
                            this.setCenterHorz( oldPosX, oldPosY );
                            playerMoved = false;
                        }
                        //check collisions to player
                        else if (this.checkCollisionsToPlayer() )
                        {
                            //undo setting to new position
                            this.setCenterHorz( oldPosX, oldPosY );
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
                    if (this.iFaceAngleChanged)
                    {
                        BulletHole.rotateForBot( this, this.iFacingAngle);
                    }
                    break;
                }
            }

            //perform transformations here

            //let eyes blink ( or close on dying )
            this.blinkEyes();

            //translate and rotate the bot's mesh
            LibVertex botAnchor = this.getAnchor().copy();

            //translate anchor if desired
            botAnchor.z += this.iOffsetZ;

            //set anchors for all meshes!
            this.iBotMeshes.setNewAnchor( botAnchor, false, null );

            //animate dying
            this.animateDying();

            //transform bot's limbs
            this.iBotMeshes.transformLimbs(this.iFacingAngle, this.iDyingAngle, this.iFaceAngleChanged, playerMoved );
        }

        public final void draw()
        {
            switch (this.iType)
            {
                case ETypeEnemy:
                case ETypeFriend:
                {
                    //turn bullet holes?? :(

                    //draw bot's mesh
                    this.iBotMeshes.draw();

                    //draw bot's debug-circles
                    this.drawDebugCircles();

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

                    this.getAnchor().x,
                    this.getAnchor().y,
                    this.getAnchor().z + PlayerSettings.DEPTH_HAND_STANDING,   // take hand height from player
                180.0f + ( 180.0f - this.iFacingAngle),     //rotZ
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
                    this.getAnchor().x,
                    this.getAnchor().y,
                    this.getAnchor().z + (this.getHeight() * 3 / 4 ),
                180.0f + ( 180.0f - this.iFacingAngle),     //rotZ
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
            LibShot       shot     = this.getViewShot();
            LibHitPoint[] hitPoint = Level.currentSection().launchShot( shot );

            //draw view shot line
            //shot.drawShotLine( FxSettings.LIFETIME_DEBUG );

            return ( hitPoint.length > 0 && hitPoint[ 0 ].iCarrier.getHitPointCarrier() == HitPointCarrier.EPlayer );
        }

        public final LibVertex getAnchor()
        {
            return this.iCylinder.getAnchor();
        }

        public final float getCarriersFaceAngle()
        {
            return this.iFacingAngle;
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            return this.iBotMeshes.launchShot( shot );
        }

        public final boolean isDead()
        {
            return (this.iAliveState == BotAliveState.EDead );
        }

        public HitPointCarrier getHitPointCarrier()
        {
            return HitPointCarrier.EBot;
        }

        private void rotateBotTo( float targetAngle )
        {
            //get the rotation of the bot
            float angleDistance   = LibMath.getAngleDistanceRelative(this.iFacingAngle, targetAngle );
            float turningDistance = Math.abs( angleDistance );

            if ( turningDistance >= BotSettings.SPEED_TURNING_MIN )
            {
                //clip turning distance
                if ( turningDistance > BotSettings.SPEED_TURNING_MAX ) turningDistance = BotSettings.SPEED_TURNING_MAX;

                //ShooterDebug.bot.out( "turning bot, src ["+iFacingAngle+"] target ["+targetAngle+"] distance is [" + angleDistance + "]" );

                this.iFaceAngleChanged = true;

                if ( angleDistance < 0 )
                {
                    this.iFacingAngle = LibMath.normalizeAngle(this.iFacingAngle - turningDistance );
                }
                else if ( angleDistance > 0 )
                {
                    this.iFacingAngle = LibMath.normalizeAngle(this.iFacingAngle + turningDistance );
                }
            }
        }

        public void makeDistancedSound( SoundFg fx )
        {
            fx.playDistancedFx(this.getCenterHorz() );
        }

        private void blinkEyes()
        {
            if (this.iAliveState != BotAliveState.EAlive )
            {
                if (this.iEyesOpen)
                {
                    this.iBotMeshes.changeFaceTexture(this.iBotMeshes.iTemplate.iTexFaceEyesOpen.getTexture(), this.iBotMeshes.iTemplate.iTexFaceEyesShut.getTexture() );
                    this.iEyesOpen = false;
                }
            }
            //let eyes blink
            else if ( System.currentTimeMillis() >= this.iNextEyeChange)
            {
                this.iEyesOpen = !this.iEyesOpen;
                if (this.iEyesOpen)
                {
                    this.iBotMeshes.changeFaceTexture(this.iBotMeshes.iTemplate.iTexFaceEyesShut.getTexture(), this.iBotMeshes.iTemplate.iTexFaceEyesOpen.getTexture() );
                }
                else
                {
                    this.iBotMeshes.changeFaceTexture(this.iBotMeshes.iTemplate.iTexFaceEyesOpen.getTexture(), this.iBotMeshes.iTemplate.iTexFaceEyesShut.getTexture() );
                }
                this.setNextEyeChange();
            }
        }

        public void fallAsleep()
        {
            if (this.iAliveState == BotAliveState.EAlive )
            {
                this.killOrTranquilize( BotAliveState.ETranquilized );
            }
        }

        public void hurt( int damage )
        {
            this.iHealth -= damage;

            //bot dies?
            if (this.iAliveState != BotAliveState.EDead && this.iHealth <= 0 )
            {
                this.killOrTranquilize( BotAliveState.EDead );
            }
        }

        private void animateDying()
        {
            switch (this.iAliveState)
            {
                case EAlive:
                {
                    break;
                }

                case ETranquilized:
                case EDead:
                {
                    switch (this.iDyingDirection)
                    {
                        case EFront:
                        {
                            if (this.iDyingAngle < 10.0f  )
                            {
                                this.iDyingAngle += 1.0f;

                                if (this.iOffsetZ < this.iTargetOffsetZ)
                                {
                                    this.iOffsetZ += this.iTargetOffsetZ / 45;
                                }
                            }
                            else
                            {
                                this.iDyingAngle += 10.0f;

                                if (this.iOffsetZ < this.iTargetOffsetZ)
                                {
                                    this.iOffsetZ += this.iTargetOffsetZ / 9;
                                }
                            }

                            if (this.iDyingAngle >= 90.0f ) this.iDyingAngle = 90.0f;
                            break;
                        }

                        case EBack:
                        {
                            if (this.iDyingAngle > -10.0f  )
                            {
                                this.iDyingAngle -= 1.0f;

                                if (this.iOffsetZ < this.iTargetOffsetZ)
                                {
                                    this.iOffsetZ += this.iTargetOffsetZ / 45;
                                }
                            }
                            else
                            {
                                this.iDyingAngle -= 10.0f;

                                if (this.iOffsetZ < this.iTargetOffsetZ)
                                {
                                    this.iOffsetZ += this.iTargetOffsetZ / 9;
                                }
                            }
                            if (this.iDyingAngle <= -90.0f ) this.iDyingAngle = -90.0f;
                            break;
                        }
                    }
                }
            }
        }

        public void killOrTranquilize( BotAliveState newState )
        {
            if (this.iAliveState != BotAliveState.ETranquilized )
            {
                //start disappear timer ( will only count for dead bots ) - only if bot was
                this.iDisappearTimer = FxSettings.LIFETIME_CORPSE;
                this.iDyingDirection = DyingDirection.values()[ LibMath.getRandom( 0, DyingDirection.values().length - 1 ) ];
                this.iBotMeshes.assignArmsPosition( ArmsPosition.EDownBoth           );
                this.iBotMeshes.assignLegsPosition( LegsPosition.EStandSpreadLegged  );
                this.iBotMeshes.assignHeadPosition( HeadPosition.EStill              );
                switch (this.iDyingDirection)
                {
                    case EBack:
                    {
                        this.iTargetOffsetZ = 0.05f;
                        break;
                    }
                    case EFront:
                    {
                        this.iTargetOffsetZ = 0.15f;
                        break;
                    }
                }
            }

            //let bot die
            this.iHealth = 0;
            this.iAliveState = newState;

            //drop all artefacts and set dying
            this.dropAllArtefacts();
            this.setNewJobQueue( new BotJob[] { BotJob.EDying } );
        }

        public void setNewJobQueue( BotJob[] newJobs )
        {
            this.iJobs.removeAllElements();
            this.iJobs.addAll( Arrays.asList( newJobs ) );
        }

        public void fadeOutAllFaces()
        {
            this.iBotMeshes.fadeOutAllFaces();
        }

        public final int getHealth()
        {
            return this.iHealth;
        }

        public final void dropAllArtefacts()
        {
            //turn artefacts to pickable items
            for ( Artefact toDrop : this.iArtefactSet.iArtefacts )
            {
                ItemToPickUp p = toDrop.getPickUpItem(this.getAnchor() );
                if ( p != null )
                {
                    //p.loadD3ds(); //old solution
                    this.iBotMeshes.iEquippedItemRight.setTranslatedAsOriginalVertices();
                    p.assignMesh(this.iBotMeshes.iEquippedItemRight );
                    p.iDropTarget = this.getAnchor().z;
                    p.iDropBegin  = this.iBotMeshes.iEquippedItemRight.getCenterZ();
                    Level.currentSection().addItem( p );
                }
            }

            //strip off artefacts
            this.iBotMeshes.setItem( Arm.ERight, null, this.getAnchor(), this );
            this.iBotMeshes.setItem( Arm.ELeft,  null, this.getAnchor(), this );
        }
    }
