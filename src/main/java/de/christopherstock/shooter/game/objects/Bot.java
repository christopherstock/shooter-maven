
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
    import  de.christopherstock.shooter.ShooterSetting.BotSettings;
    import  de.christopherstock.shooter.ShooterSetting.FxSettings;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.ShooterSetting.PlayerSettings;
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
    public class Bot implements LibGameObject, ShotSource
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
            ECivilian(       30  ),
            ESecurity(       100 ),
            EPrivateSoldier( 150 ),
            ;

            protected       int         energy              = 0;

            private BotHealth( int energy )
            {
                this.energy = energy;
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
            protected           BotEvent            event               = null;

            public BotUseAction( BotEvent event )
            {
                this.event = event;
            }
        }

        public static final class BotGiveAction implements BotAction
        {
            protected           ArtefactType        key                 = null;
            protected           BotEvent            event               = null;

            public BotGiveAction( ArtefactType key, BotEvent event )
            {
                this.key   = key;
                this.event = event;
            }
        }

        public                      BotMeshes               botMeshes                       = null;

        private                     AmmoSet                 ammoSet                         = null;

        private                     ArtefactSet             artefactSet                     = null;

        /** current facing angle ( z axis ). */
        private                     float                   facingAngle                     = 0.0f;

        /** current drop dead angle ( x axis ). */
        private                     boolean                 faceAngleChanged                = false;

        private                     Vector<BotJob>          jobs                            = null;

        private                     BotType                 type                            = null;
        private                     BotState                state                           = null;
        private                     Point2D.Float[]         wayPoints                       = null;
        private                     int                     currentWayPointIndex            = 0;
        private                     int                     health                          = 0;

        private                     float                   targetOffsetZ                   = 0.0f;
        private                     float                   offsetZ                         = 0.0f;

        private                     BotAliveState           aliveState                      = null;

        private                     DyingDirection          dyingDirection                  = null;
        private                     float                   dyingAngle                      = 0.0f;
        public                      long                    disappearTimer                  = 0;

        /** the collision unit for simple collisions */
        private                     Cylinder                cylinder                        = null;

        private                     long                    nextEyeChange                   = 0;
        private                     boolean                 eyesOpen                        = true;

        private                     BotPattern              template                        = null;
        private                     LibVertex               startPosition                   = null;
        private                     BotAction[]             actions                         = null;
        public                      int                     id                              = 0;

        private                     int                     interAnimationsDelay            = 0;
        private                     BotHanded               handed                          = null;

        public                      Items                   nextItemToDeliverLeftHand       = null;
        public                      Items                   nextItemToDeliverRightHand      = null;

        public Bot
        (
            BotPattern      template,
            BotType         type,
            LibVertex       startPosition,
            Point2D.Float[] wayPoints,
            BotJob          job,
            Artefact[]      artefacts,
            float           facingAngle,
            BotAction[]     actions,
            BotHealth       botHealth,
            int             id,
            BotHanded       botHanded
        )
        {
            this.type          = type;
            this.startPosition = startPosition.copy();
            this.cylinder      = new Cylinder( this, this.startPosition, template.base.radius, template.base.height, 0, ShooterDebug.bot, ShooterDebug.DEBUG_DRAW_BOT_CIRCLES, 0.0f, 0.0f, ShooterSetting.Performance.ELLIPSE_SEGMENTS, Material.EHumanFlesh );
            this.wayPoints     = wayPoints;
            this.jobs          = new Vector<BotJob>();
            this.jobs.add( job );
            this.health       = botHealth.energy;
            this.template     = template;
            this.actions      = actions;
            this.id           = id;
            this.handed       = botHanded;
            this.aliveState   = BotAliveState.EAlive;

            //set startup facing angle
            this.facingAngle = facingAngle;

            this.ammoSet     = new AmmoSet();
            this.artefactSet = new ArtefactSet();

            //deliver all artefacts if given
            if ( artefacts != null )
            {
                for ( Artefact a : artefacts )
                {
                    this.artefactSet.deliverArtefact( a );
                }

                this.artefactSet.chooseNextWearponOrGadget( false );
                this.artefactSet.currentArtefact.reload(this.ammoSet, false, false, null );
            }

            Items leftItem  = null;
            Items rightItem = null;

            switch (this.handed)
            {
                case ELeftHanded:
                {
                    leftItem = this.artefactSet.currentArtefact.artefactType.itemMesh;
                    break;
                }

                case ERightHanded:
                {
                    rightItem = this.artefactSet.currentArtefact.artefactType.itemMesh;
                    break;
                }
            }

            //init mesh-collection
            this.botMeshes = new BotMeshes(this.template, this.startPosition, this, leftItem, rightItem );

            //set next eye blink
            this.eyesOpen = true;
            this.setNextEyeChange();

            //animate 1st tick? .. triggers render :/ not required!
            boolean deprecatedAnimateOnCreate = false;
            if ( deprecatedAnimateOnCreate ) this.animate();
        }

        private void setNextEyeChange()
        {
            //only if not dead
            if (this.aliveState == BotAliveState.EAlive )
            {
                this.nextEyeChange =
                (
                        System.currentTimeMillis()
                    +   (
                                this.eyesOpen
                            ?   LibMath.getRandom( BotSettings.EYE_BLINK_INTERVAL_MIN, BotSettings.EYE_BLINK_INTERVAL_MAX )
                            :   BotSettings.EYE_CLOSED_INTERVAL
                        )
                );
            }
        }

        public final boolean checkCollision( Cylinder cylinder )
        {
            return (this.aliveState == BotAliveState.EAlive ? this.cylinder.checkCollisionHorz( cylinder ) : false );
        }

        public final void launchAction(LibCylinder cylinder, Object artefact, float faceAngle )
        {
            //only if alive
            if (this.aliveState == BotAliveState.EAlive )
            {
                //check if collision appears
                if (this.cylinder.checkCollisionHorz(cylinder) )
                {
                    float anglePlayerToWall = LibMath.getAngleCorrect( Shooter.game.engine.player.getCylinder().getCenterHorz(), this.cylinder.getCenterHorz() );
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
                            if (this.actions != null )
                            {
                                for ( BotAction action : this.actions)
                                {
                                    if ( action instanceof BotUseAction )
                                    {
                                        BotUseAction ga = (BotUseAction)action;
                                        ga.event.perform( this );
                                    }
                                }
                            }
                        }
                        else
                        {
                            ShooterDebug.playerAction.out( "launch gadget-action on bot" );

                            //CHECK all GIVE-actions
                            if (this.actions != null )
                            {
                                for ( BotAction action : this.actions)
                                {
                                    if ( action instanceof BotGiveAction )
                                    {
                                        BotGiveAction ga = (BotGiveAction)action;

                                        //LAUNCH if KEY matches
                                        if ( ga.key == ( (Gadget)artefact ).parentKind)
                                        {
                                            ga.event.perform( this );
                                            Shooter.game.engine.player.artefactSet.extractArtefact( artefact );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private boolean checkCollision( Ellipse2D.Float ellipse )
        {
            return (this.aliveState == BotAliveState.EAlive ? this.cylinder.checkCollision( ellipse ) : false );
        }

        private Point2D.Float getCenterHorz()
        {
            return this.cylinder.getCenterHorz();
        }

        private void drawDebugCircles()
        {
            if ( ShooterDebug.DEBUG_DRAW_BOT_CIRCLES )
            {
                //Debug.bot.out( "drawing bot .." );
                int VERTICAL_SLICES = 4;
                LibVertex ank = this.getAnchor();
                LibColors col = null;
                switch (this.type)
                {
                    case ETypeFriend:   col = LibColors.EBlueLight;    break;
                    case ETypeEnemy:    col = LibColors.EBlueDark;      break;
                }
                for ( int i = 0; i <= VERTICAL_SLICES; ++i )
                {
                    new LibFaceEllipseFloor( ShooterDebug.face, null, col, ank.x, ank.y, ank.z + ( i * this.cylinder.getHeight() / VERTICAL_SLICES ), this.cylinder.getRadius(), this.cylinder.getRadius(), ShooterSetting.Performance.ELLIPSE_SEGMENTS ).draw();
                }
            }
        }

        private float getHeight()
        {
            return this.cylinder.getHeight();
        }

        private void setCenterHorz( float newX, float newY )
        {
            this.cylinder.setNewAnchor( new LibVertex( newX, newY, this.cylinder.getAnchor().z ), false, null );
        }

        public final Cylinder getCylinder()
        {
            return this.cylinder;
        }

        private boolean checkCollisionsToOtherBots()
        {
            for ( Bot bot : Level.currentSection().getBots() )
            {
                //skip own !
                if ( this == bot ) continue;

                //check bot-collision with other bot
                if ( bot.checkCollision(this.cylinder) )
                {
                    ShooterDebug.bot.out( "own bot touched" );
                    return true;

                    //sleep if being touched
                    //bot.state = BotState.EStateSleeping;
                }
            }

            return false;
        }

        private boolean checkCollisionsToPlayer()
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

            switch (this.type)
            {
                case ETypeEnemy:
                case ETypeFriend:
                {

                    //Debug.bot.out( "angle player to bot is: " + anglePlayerToBot );
                    //Debug.bot.out( "angle bot to player is: " + angleBotToPlayer );

                    Point2D.Float bot = this.getCenterHorz();

                    float   nextPosX            = 0.0f;
                    float   nextPosY            = 0.0f;
                    this.faceAngleChanged = false;

                    //check the bot's current job
                    switch (this.jobs.elementAt( 0 ) )
                    {
                        case ELeadPlayerToLastWaypoint:
                        {
                            //calculate bot's angle and distance to the player
                            Point2D.Float   player           = Shooter.game.engine.player.getCylinder().getCenterHorz();

                            //check distance from player to next waypoint and from bot to next waypoint
                            float   distancePlayerToNextWaypoint = (float)player.distance(this.wayPoints[this.currentWayPointIndex] );
                            float   distanceBotToNextWaypoint    = (float)bot.distance(this.wayPoints[this.currentWayPointIndex] );
                            boolean playerOutOfBotReach          = ( (float)player.distance( bot ) > ShooterSetting.BotSettings.MAX_LEADING_DISTANCE_TO_PLAYER );

                            //wait for player if he is out of reach and farer from the next waypoint than the bot
                            if ( playerOutOfBotReach && distancePlayerToNextWaypoint > distanceBotToNextWaypoint )
                            {
                                //wait for player
                                this.state = BotState.EWatchPlayer;
                            }
                            else
                            {
                                //walk to next waypoint
                                this.state = BotState.EWalkToNextWayPoint;
                            }
                            break;
                        }

                        case EWalkWaypoints:
                        {
                            this.state = BotState.EWalkToNextWayPoint;
                            break;
                        }

                        case EWatchPlayer:
                        {
                            this.state = BotState.EWatchPlayer;
                            break;
                        }

                        case EStandStill:
                        {
                            this.state = BotState.EStandStill;
                            break;
                        }

                        case EDying:
                        {
                            this.state = BotState.EDying;
                            break;
                        }

                        case EAttackPlayerReload:
                        {
                            this.state = BotState.EStandStill;

//                            artefactSet.currentArtefact.reload( ammoSet, false, true, cylinder.getCenterHorz() );
                            this.artefactSet.currentArtefact.performReload(this.ammoSet, true, this.cylinder.getCenterHorz(), true );

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
                            this.state = BotState.EWatchPlayer;

                            //check if bot's wearpon is delayed
                            if (this.artefactSet.isWearponDelayed() )
                            {
                                //do nothing if the wearpon is delayed

                            }
                            //check if bot's wearpon is empty
                            else if (this.artefactSet.isMagazineEmpty() )
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

                                //ShooterDebug.bugfix.out( "angle [" + angleBotToPlayer + "] facing: " + facingAngle );
                                //ShooterDebug.bugfix.out( "attacking bot holds " + artefactSet.currentArtefact.artefactType );

                                //check if bot looks into player's direction
                                if ( Math.abs( angleBotToPlayer - this.facingAngle) <= BotSettings.TARGET_TURNING_TOLERANCE )
                                {
                                    if (this.botMeshes.isAssignedArmsPosition( (this.handed == BotHanded.ELeftHanded ? ArmsPosition.EAimHighLeft : ArmsPosition.EAimHighRight ) ) )
                                    {
                                        if
                                        (
                                                (this.botMeshes.completedTargetPitchesLeftArm  && this.handed == BotHanded.ELeftHanded  )
                                            ||  (this.botMeshes.completedTargetPitchesRightArm && this.handed == BotHanded.ERightHanded )
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
                                                this.artefactSet.currentArtefact.fire( this, this.cylinder.getCenterHorz() );
                                            }
                                        }
                                    }
                                    else
                                    {
                                        //assign
                                        this.botMeshes.assignArmsPosition( (this.handed == BotHanded.ELeftHanded ? ArmsPosition.EAimHighLeft : ArmsPosition.EAimHighRight ) );
                                    }
                                }
                            }
                            break;
                        }

                        case ESequenceDelay:
                        {
                            if (this.interAnimationsDelay <= 0 )
                            {
                                this.interAnimationsDelay = BotSettings.INTER_ANIMATIONS_DELAY;
                            }
                            else
                            {
                                if ( --this.interAnimationsDelay <= 0 )
                                {
                                    this.interAnimationsDelay = 0;
                                    this.jobs.remove( 0 );
                                }
                            }
                            break;
                        }

                        case ESequenceTurnToPlayer:
                        {
                            //turn to player
                            this.state = BotState.EWatchPlayer;

                            //check if bot sees the player now
                            Point2D.Float   player           = Shooter.game.engine.player.getCylinder().getCenterHorz();
                            float           anglePlayerToBot = LibMath.getAngleCorrect( player, bot );
                            float           angleBotToPlayer = LibMath.normalizeAngle( anglePlayerToBot - 180.0f ); //checked normalizing - also works wothout :)

                            //check if bot looks into player's direction
                            if ( Math.abs( angleBotToPlayer - this.facingAngle) <= BotSettings.TARGET_TURNING_TOLERANCE )
                            {
                                this.jobs.remove( 0 );
                            }
                            break;
                        }

                        case ESequenceNodOnce:
                        {
                            //check if assigned
                            if (this.botMeshes.isAssignedHeadPosition( HeadPosition.ENodOnce ) )
                            {
                                if (this.botMeshes.completedTargetPitchesHead )
                                {
                                    this.botMeshes.assignHeadPosition( HeadPosition.EStill );
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignHeadPosition( HeadPosition.ENodOnce );
                            }
                            break;
                        }

                        case ESequenceNodTwice:
                        {
                            //check if assigned
                            if (this.botMeshes.isAssignedHeadPosition( HeadPosition.ENodTwice ) )
                            {
                                if (this.botMeshes.completedTargetPitchesHead )
                                {
                                    this.botMeshes.assignHeadPosition( HeadPosition.EStill );
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignHeadPosition( HeadPosition.ENodTwice );
                            }
                            break;
                        }

                        case ESequenceGrabSpringRight:
                        {
                            //check if assigned
                            if (this.botMeshes.isAssignedArmsPosition( ArmsPosition.EPickUpRight ) )
                            {
                                if (this.botMeshes.completedTargetPitchesRightArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignArmsPosition( ArmsPosition.EPickUpRight );
                            }
                            break;
                        }

                        case ESequenceFlushRightEquippedItem:
                        {
                            //draw item to bot
                            this.botMeshes.setItem( Arm.ERight, null, this.getAnchor(), this );

                            //next job
                            this.jobs.remove( 0 );

                            break;
                        }

                        case ESequenceDeliverRightEquippedItem:
                        {
                            //give item to bot
                            this.botMeshes.setItem( Arm.ERight, this.nextItemToDeliverRightHand, this.getAnchor(), this );

                            //next job
                            this.jobs.remove( 0 );

                            break;
                        }

                        case ESequenceGrabBackDownRight:
                        {
                            //check if assigned
                            if (this.botMeshes.isAssignedArmsPosition( ArmsPosition.EBackDownRight ) )
                            {
                                if (this.botMeshes.completedTargetPitchesRightArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignArmsPosition( ArmsPosition.EBackDownRight );
                            }
                            break;
                        }

                        case ESequenceGrabSpringLeft:
                        {
                            //check if assigned
                            if (this.botMeshes.isAssignedArmsPosition( ArmsPosition.EPickUpLeft ) )
                            {
                                if (this.botMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignArmsPosition( ArmsPosition.EPickUpLeft );
                            }
                            break;
                        }

                        case ESequenceFlushLeftEquippedItem:
                        {
                            //draw item to bot
                            this.botMeshes.setItem( Arm.ELeft, null, this.getAnchor(), this );

                            //next job
                            this.jobs.remove( 0 );

                            break;
                        }

                        case ESequenceDeliverLeftEquippedItem:
                        {
                            //give item to bot
                            this.botMeshes.setItem( Arm.ELeft, this.nextItemToDeliverLeftHand, this.getAnchor(), this );

                            //next job
                            this.jobs.remove( 0 );

                            break;
                        }

                        case ESequenceGrabBackDownLeft:
                        {
                            //check if assigned
                            if (this.botMeshes.isAssignedArmsPosition( ArmsPosition.EBackDownLeft ) )
                            {
                                if (this.botMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignArmsPosition( ArmsPosition.EBackDownLeft );
                            }
                            break;
                        }

                        case ESequenceReloadHandUp:
                        {
                            this.state = BotState.EStandStill;

                            //check if assigned
                            if (this.botMeshes.isAssignedArmsPosition( (this.handed == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftUp : ArmsPosition.EReloadRightUp ) ) )
                            {
                                if (this.handed == BotHanded.ELeftHanded && this.botMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                                else if (this.handed == BotHanded.ERightHanded && this.botMeshes.completedTargetPitchesRightArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignArmsPosition( (this.handed == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftUp : ArmsPosition.EReloadRightUp ) );
                            }
                            break;
                        }

                        case ESequenceReloadHandDown:
                        {
                            this.state = BotState.EStandStill;

                            //check if assigned
                            if (this.botMeshes.isAssignedArmsPosition( (this.handed == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftDown : ArmsPosition.EReloadRightDown ) ) )
                            {
                                if (this.handed == BotHanded.ELeftHanded && this.botMeshes.completedTargetPitchesLeftArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                                else if (this.handed == BotHanded.ERightHanded && this.botMeshes.completedTargetPitchesRightArm )
                                {
                                    this.jobs.remove( 0 );
                                }
                            }
                            else
                            {
                                //assign
                                this.botMeshes.assignArmsPosition( (this.handed == BotHanded.ELeftHanded ? ArmsPosition.EReloadLeftDown : ArmsPosition.EReloadRightDown ) );
                            }
                            break;
                        }
                    }

                    //perform action according to current state
                    switch (this.state)
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

                                if ( !faceAngleChanged )
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
                            if ( angleBotToPlayer != this.facingAngle && botDistanceToPlayer < MAX_BOT_WATCH_RADIUS )
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
                            if ( angleBotToPlayer != this.facingAngle) this.faceAngleChanged = true;
                            this.facingAngle = angleBotToPlayer;
                            break;
                        }

                        case EWalkToNextWayPoint:
                        {
                            //shouldn't have been initialized without wayPoints ..
                            if (this.wayPoints != null )
                            {
                                //check if the current wayPoint has been reached?
                                if (this.checkCollision( new Ellipse2D.Float(this.wayPoints[this.currentWayPointIndex].x - BotSettings.WAY_POINT_RADIUS, this.wayPoints[this.currentWayPointIndex].y - BotSettings.WAY_POINT_RADIUS, 2 * BotSettings.WAY_POINT_RADIUS, 2 * BotSettings.WAY_POINT_RADIUS ) ) )
                                {
                                    //wayPoint has been reached
                                    //Debug.info( "wayPoint reached" );

                                    //target next wayPoint
                                    ++this.currentWayPointIndex;

                                    //begin with 1st waypoint
                                    if (this.currentWayPointIndex >= this.wayPoints.length ) this.currentWayPointIndex = 0;
                                }

                                //set arms and legs walking
                                if ( !this.botMeshes.isAssignedLegsPosition( LegsPosition.EWalk ) )
                                {
                                    this.botMeshes.assignLegsPosition( LegsPosition.EWalk );
                                }
                                if ( !this.botMeshes.isAssignedArmsPosition( ArmsPosition.EWalk ) )
                                {
                                    this.botMeshes.assignArmsPosition( ArmsPosition.EWalk );
                                }

                                //move player towards current waypoint
                                Point2D.Float currentWayPoint = this.wayPoints[this.currentWayPointIndex];

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
                            Shooter.game.engine.bulletHoleManager.translateAll( this, nextPosX - oldPosX, nextPosY - oldPosY, 0.0f );
                        }
                    }

                    //rotate if faceAngle changed
                    if (this.faceAngleChanged)
                    {
                        Shooter.game.engine.bulletHoleManager.rotateForBot( this, this.facingAngle);
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
            botAnchor.z += this.offsetZ;

            //set anchors for all meshes!
            this.botMeshes.setNewAnchor( botAnchor, false, null );

            //animate dying
            this.animateDying();

            //transform bot's limbs
            this.botMeshes.transformLimbs(this.facingAngle, this.dyingAngle, this.faceAngleChanged, playerMoved );
        }

        public final void draw()
        {
            switch (this.type)
            {
                case ETypeEnemy:
                case ETypeFriend:
                {
                    //turn bullet holes?? :(

                    //draw bot's mesh
                    this.botMeshes.draw();

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
                180.0f + ( 180.0f - this.facingAngle),     //rotZ
                0.0f,                                   //rotX
                BotSettings.SHOT_RANGE,    // bot has constant shot range ???
                LibHoleSize.E44mm,   // bot always fires 9mm    ??
                ShooterDebug.shotAndHit,
                ArtefactType.EMagnum357.artefactKind.getSliverParticleQuantity(),
                FxSettings.SLIVER_ANGLE_MOD,
                10,
                ArtefactType.EMagnum357.artefactKind.getSliverParticleSize(),
                ArtefactType.EMagnum357.getBreaksWalls(),
                ( (FireArm)ArtefactType.EMagnum357.artefactKind).getProjectile(),
                General.FADE_OUT_FACES_TOTAL_TICKS
            );
        }

        private LibShot getViewShot()
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
                180.0f + ( 180.0f - this.facingAngle),     //rotZ
                0.0f,                                   //rotX
                BotSettings.VIEW_RANGE,
                LibHoleSize.ENone,
                ShooterDebug.shotAndHit,
                ArtefactType.EMagnum357.artefactKind.getSliverParticleQuantity(),
                FxSettings.SLIVER_ANGLE_MOD,
                0,
                ArtefactType.EMagnum357.artefactKind.getSliverParticleSize(),
                false,
                null,
                General.FADE_OUT_FACES_TOTAL_TICKS
            );
        }

        private boolean botSeesThePlayer()
        {
            //check if this enemy bot sees the player - copy current angle
            LibShot       shot     = this.getViewShot();
            LibHitPoint[] hitPoint = Level.currentSection().launchShot( shot );

            //draw glView shot line
            //shot.drawShotLine( FxSettings.LIFETIME_DEBUG );

            return ( hitPoint.length > 0 && hitPoint[ 0 ].carrier.getHitPointCarrier() == HitPointCarrier.EPlayer );
        }

        public final LibVertex getAnchor()
        {
            return this.cylinder.getAnchor();
        }

        public final float getCarriersFaceAngle()
        {
            return this.facingAngle;
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            return this.botMeshes.launchShot( shot );
        }

        public final boolean isDead()
        {
            return (this.aliveState == BotAliveState.EDead );
        }

        public HitPointCarrier getHitPointCarrier()
        {
            return HitPointCarrier.EBot;
        }

        private void rotateBotTo( float targetAngle )
        {
            //get the rotation of the bot
            float angleDistance   = LibMath.getAngleDistanceRelative(this.facingAngle, targetAngle );
            float turningDistance = Math.abs( angleDistance );

            if ( turningDistance >= BotSettings.SPEED_TURNING_MIN )
            {
                //clip turning distance
                if ( turningDistance > BotSettings.SPEED_TURNING_MAX ) turningDistance = BotSettings.SPEED_TURNING_MAX;

                //ShooterDebug.bot.out( "turning bot, src ["+facingAngle+"] target ["+targetAngle+"] distance is [" + angleDistance + "]" );

                this.faceAngleChanged = true;

                if ( angleDistance < 0 )
                {
                    this.facingAngle = LibMath.normalizeAngle(this.facingAngle - turningDistance );
                }
                else if ( angleDistance > 0 )
                {
                    this.facingAngle = LibMath.normalizeAngle(this.facingAngle + turningDistance );
                }
            }
        }

        public void makeDistancedSound( SoundFg fx )
        {
            fx.playDistancedFx(this.getCenterHorz() );
        }

        private void blinkEyes()
        {
            if (this.aliveState != BotAliveState.EAlive )
            {
                if (this.eyesOpen)
                {
                    this.botMeshes.changeFaceTexture(this.botMeshes.template.texFaceEyesOpen.getMetaData(), this.botMeshes.template.texFaceEyesShut.getMetaData() );
                    this.eyesOpen = false;
                }
            }
            //let eyes blink
            else if ( System.currentTimeMillis() >= this.nextEyeChange)
            {
                this.eyesOpen = !this.eyesOpen;
                if (this.eyesOpen)
                {
                    this.botMeshes.changeFaceTexture(this.botMeshes.template.texFaceEyesShut.getMetaData(), this.botMeshes.template.texFaceEyesOpen.getMetaData() );
                }
                else
                {
                    this.botMeshes.changeFaceTexture(this.botMeshes.template.texFaceEyesOpen.getMetaData(), this.botMeshes.template.texFaceEyesShut.getMetaData() );
                }
                this.setNextEyeChange();
            }
        }

        public void fallAsleep()
        {
            if (this.aliveState == BotAliveState.EAlive )
            {
                this.killOrTranquilize( BotAliveState.ETranquilized );
            }
        }

        public void hurt( int damage )
        {
            this.health -= damage;

            //bot dies?
            if (this.aliveState != BotAliveState.EDead && this.health <= 0 )
            {
                this.killOrTranquilize( BotAliveState.EDead );
            }
        }

        private void animateDying()
        {
            switch (this.aliveState)
            {
                case EAlive:
                {
                    break;
                }

                case ETranquilized:
                case EDead:
                {
                    switch (this.dyingDirection)
                    {
                        case EFront:
                        {
                            if (this.dyingAngle < 10.0f  )
                            {
                                this.dyingAngle += 1.0f;

                                if (this.offsetZ < this.targetOffsetZ)
                                {
                                    this.offsetZ += this.targetOffsetZ / 45;
                                }
                            }
                            else
                            {
                                this.dyingAngle += 10.0f;

                                if (this.offsetZ < this.targetOffsetZ)
                                {
                                    this.offsetZ += this.targetOffsetZ / 9;
                                }
                            }

                            if (this.dyingAngle >= 90.0f ) this.dyingAngle = 90.0f;
                            break;
                        }

                        case EBack:
                        {
                            if (this.dyingAngle > -10.0f  )
                            {
                                this.dyingAngle -= 1.0f;

                                if (this.offsetZ < this.targetOffsetZ)
                                {
                                    this.offsetZ += this.targetOffsetZ / 45;
                                }
                            }
                            else
                            {
                                this.dyingAngle -= 10.0f;

                                if (this.offsetZ < this.targetOffsetZ)
                                {
                                    this.offsetZ += this.targetOffsetZ / 9;
                                }
                            }
                            if (this.dyingAngle <= -90.0f ) this.dyingAngle = -90.0f;
                            break;
                        }
                    }
                }
            }
        }

        private void killOrTranquilize(BotAliveState newState)
        {
            if (this.aliveState != BotAliveState.ETranquilized )
            {
                //start disappear timer ( will only count for dead bots ) - only if bot was
                this.disappearTimer = FxSettings.LIFETIME_CORPSE;
                this.dyingDirection = DyingDirection.values()[ LibMath.getRandom( 0, DyingDirection.values().length - 1 ) ];
                this.botMeshes.assignArmsPosition( ArmsPosition.EDownBoth           );
                this.botMeshes.assignLegsPosition( LegsPosition.EStandSpreadLegged  );
                this.botMeshes.assignHeadPosition( HeadPosition.EStill              );
                switch (this.dyingDirection)
                {
                    case EBack:
                    {
                        this.targetOffsetZ = 0.05f;
                        break;
                    }
                    case EFront:
                    {
                        this.targetOffsetZ = 0.15f;
                        break;
                    }
                }
            }

            //let bot die
            this.health = 0;
            this.aliveState = newState;

            //drop all artefacts and set dying
            this.dropAllArtefacts();
            this.setNewJobQueue( new BotJob[] { BotJob.EDying } );
        }

        public void setNewJobQueue( BotJob[] newJobs )
        {
            this.jobs.removeAllElements();
            this.jobs.addAll( Arrays.asList( newJobs ) );
        }

        public void fadeOutAllFaces()
        {
            this.botMeshes.fadeOutAllFaces();
        }

        public final int getHealth()
        {
            return this.health;
        }

        private void dropAllArtefacts()
        {
            //turn artefacts to pickable items
            for ( Artefact toDrop : this.artefactSet.artefacts)
            {
                ItemToPickUp p = toDrop.getPickUpItem(this.getAnchor() );
                if ( p != null )
                {
                    //p.loadD3ds(); //old solution
                    this.botMeshes.equippedItemRight.setTranslatedAsOriginalVertices();
                    p.assignMesh(this.botMeshes.equippedItemRight);
                    p.dropTarget = this.getAnchor().z;
                    p.dropBegin = this.botMeshes.equippedItemRight.getCenterZ();
                    Level.currentSection().addItem( p );
                }
            }

            //strip off artefacts
            this.botMeshes.setItem( Arm.ERight, null, this.getAnchor(), this );
            this.botMeshes.setItem( Arm.ELeft,  null, this.getAnchor(), this );
        }
    }
