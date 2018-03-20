
    package de.christopherstock.shooter.game.bot;

    import  java.awt.geom.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;
    import     de.christopherstock.shooter.game.artefact.Artefact;
    import     de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.game.objects.Bot.*;

    /**************************************************************************************
    *   The superclass of all non-player-characters.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class BotFactory
    {
        public static enum Glasses
        {
            ENever,
            EAlways,
            ESometimes,
            EOften,
            ;
        }

        public static enum Hat
        {
            ENever,
            EAlways,
            ESometimes,
            EOften,
            ;
        }

        public static enum BotKind
        {
            EUnitSecurity(          Glasses.ENever,         Hat.ENever,     BotSkinType.SET_NORTH_EUROPEAN, BotBodyType.values() ),
            EUnitPilot(             Glasses.EAlways,        Hat.EAlways,    BotSkinType.SET_SOUTH_EUROPEAN, BotBodyType.values() ),
            EUnitSpecialForces(     Glasses.ESometimes,     Hat.ESometimes, BotSkinType.SET_NORTH_AFRICAN,  BotBodyType.values() ),
            EUnitOfficeEmployee(    Glasses.EOften,         Hat.ENever,     BotSkinType.SET_NORTH_EUROPEAN, BotBodyType.values() ),
            ;

            public          BotSkinType[]       iSkinTypes  = null;
            public          BotBodyType[]       iBodyTypes  = null;
            public          Glasses             iGlasses    = null;
            public          Hat                 iHat        = null;

            private BotKind( Glasses aGlasses, Hat aHat, BotSkinType[] aSkinTypes, BotBodyType[] aBodyTypes )
            {
                iGlasses    = aGlasses;
                iHat        = aHat;
                iSkinTypes  = aSkinTypes;
                iBodyTypes  = aBodyTypes;
            }
        }

        private         BotKind             iKind           = null;
        private         LibVertex           iAnchor         = null;
        private         float               iFacingAngle    = 0.0f;
        private         BotAction[]         iActions        = null;
        private         int                 iID             = 0;

        public BotFactory( int aID, BotKind aKind, LibVertex aAnchor, float aFacingAngle, BotAction[] aActions )
        {
            iKind        = aKind;
            iAnchor      = aAnchor;
            iFacingAngle = aFacingAngle;
            iActions     = aActions;
            iID          = aID;
        }

        public final Bot createBot()
        {
            switch ( iKind )
            {
                case EUnitOfficeEmployee:
                {
                    switch ( LibMath.getRandom( 0, 3 ) )
                    {
                        default: return new Bot(  new BotPattern( BotPatternBase.EOfficeEmployee1,   iKind   ),  Bot.BotType.ETypeFriend, iAnchor,  null, BotJob.EStandStill,           null,     iFacingAngle, iActions, BotHealth.ECivilian, iID, BotHanded.ERightHanded          );
                    }
                    //break;
                }

                case EUnitSecurity:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0: return new Bot(  new BotPattern( BotPatternBase.ESecurityLight,     iKind   ),  Bot.BotType.ETypeEnemy, iAnchor,  null, BotJob.EAttackPlayerFire,           new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, iFacingAngle, iActions, BotHealth.ESecurity, iID, BotHanded.ERightHanded      );
                        case 1: return new Bot(  new BotPattern( BotPatternBase.ESecurityHeavy,     iKind   ),  Bot.BotType.ETypeEnemy, iAnchor,  null, BotJob.EAttackPlayerFire,           new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, iFacingAngle, iActions, BotHealth.ESecurity, iID, BotHanded.ERightHanded     );
                    }
                    break;
                }

                case EUnitPilot:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0: return new Bot(  new BotPattern( BotPatternBase.EPilot1,            iKind   ),  Bot.BotType.ETypeFriend, iAnchor,  new Point2D.Float[] { new Point2D.Float( 7.0f, 4.0f ), new Point2D.Float( 7.0f, 10.0f ), }, BotJob.EWalkWaypoints,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, iFacingAngle, iActions, BotHealth.ECivilian, iID, BotHanded.ERightHanded    );
                        case 1: return new Bot(  new BotPattern( BotPatternBase.EPilot1,            iKind   ),  Bot.BotType.ETypeFriend, iAnchor,  new Point2D.Float[] { new Point2D.Float( 7.0f, 4.0f ), new Point2D.Float( 7.0f, 10.0f ), }, BotJob.EWalkWaypoints,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, iFacingAngle, iActions, BotHealth.ECivilian, iID, BotHanded.ERightHanded    );
                    }
                    break;
                }

                case EUnitSpecialForces:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0: return new Bot(  new BotPattern( BotPatternBase.ESpecialForces,     iKind   ),  Bot.BotType.ETypeFriend, iAnchor,  null, BotJob.EWatchPlayer,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, iFacingAngle, iActions, BotHealth.EPrivateSoldier, iID, BotHanded.ERightHanded );
                        case 1: return new Bot(  new BotPattern( BotPatternBase.ESpecialForces,     iKind   ),  Bot.BotType.ETypeFriend, iAnchor,  null, BotJob.EWatchPlayer,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, iFacingAngle, iActions, BotHealth.EPrivateSoldier, iID, BotHanded.ERightHanded );
                    }
                    break;
                }
            }

            return null;
        }
    }
