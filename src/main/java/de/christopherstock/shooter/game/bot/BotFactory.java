
    package de.christopherstock.shooter.game.bot;

    import  java.awt.geom.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.game.objects.Bot.*;

    /*******************************************************************************************************************
    *   The superclass of all non-player-characters.
    *******************************************************************************************************************/
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
            EUnitSpecialForces(     Glasses.ESometimes,     Hat.ESometimes, BotSkinType.SET_SOUTH_EUROPEAN, BotBodyType.values() ),
            EUnitOfficeEmployee(    Glasses.EOften,         Hat.ENever,     BotSkinType.SET_NORTH_EUROPEAN, BotBodyType.values() ),
            ;

            public          BotSkinType[]       skinTypes       = null;
            public          BotBodyType[]       bodyTypes       = null;
            public          Glasses             glasses         = null;
            public          Hat                 hat             = null;

            private BotKind( Glasses glasses, Hat hat, BotSkinType[] skinTypes, BotBodyType[] bodyTypes )
            {
                this.glasses   = glasses;
                this.hat       = hat;
                this.skinTypes = skinTypes;
                this.bodyTypes = bodyTypes;
            }
        }

        private         BotKind             kind                = null;
        private         LibVertex           anchor              = null;
        private         float               facingAngle         = 0.0f;
        private         BotAction[]         actions             = null;
        private         int                 id                  = 0;

        public BotFactory( int id, BotKind kind, LibVertex anchor, float facingAngle, BotAction[] actions )
        {
            this.kind        = kind;
            this.anchor      = anchor;
            this.facingAngle = facingAngle;
            this.actions     = actions;
            this.id          = id;
        }

        public final Bot createBot()
        {
            switch ( this.kind )
            {
                case EUnitOfficeEmployee:
                {
                    switch ( LibMath.getRandom( 0, 3 ) )
                    {
                        default: return new Bot(  new BotPattern( BotPatternBase.EOfficeEmployee1, this.kind),  Bot.BotType.ETypeFriend, this.anchor,  null, BotJob.EStandStill,           null, this.facingAngle, this.actions, BotHealth.ECivilian, this.id, BotHanded.ERightHanded          );
                    }
                    //break;
                }

                case EUnitSecurity:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0: return new Bot(  new BotPattern( BotPatternBase.ESecurityLight, this.kind),  Bot.BotType.ETypeEnemy, this.anchor,  null, BotJob.EAttackPlayerFire,           new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, this.facingAngle, this.actions, BotHealth.ESecurity, this.id, BotHanded.ERightHanded      );
                        case 1: return new Bot(  new BotPattern( BotPatternBase.ESecurityHeavy, this.kind),  Bot.BotType.ETypeEnemy, this.anchor,  null, BotJob.EAttackPlayerFire,           new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, this.facingAngle, this.actions, BotHealth.ESecurity, this.id, BotHanded.ERightHanded     );
                    }
                    break;
                }

                case EUnitPilot:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0: return new Bot(  new BotPattern( BotPatternBase.EPilot1, this.kind),  Bot.BotType.ETypeFriend, this.anchor,  new Point2D.Float[] { new Point2D.Float( 7.0f, 4.0f ), new Point2D.Float( 7.0f, 8.0f ), }, BotJob.EWalkWaypoints,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, this.facingAngle, this.actions, BotHealth.ECivilian, this.id, BotHanded.ERightHanded    );
                        case 1: return new Bot(  new BotPattern( BotPatternBase.EPilot1, this.kind),  Bot.BotType.ETypeFriend, this.anchor,  new Point2D.Float[] { new Point2D.Float( 7.0f, 4.0f ), new Point2D.Float( 7.0f, 8.0f ), }, BotJob.EWalkWaypoints,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, this.facingAngle, this.actions, BotHealth.ECivilian, this.id, BotHanded.ERightHanded    );
                    }
                    break;
                }

                case EUnitSpecialForces:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0: return new Bot(  new BotPattern( BotPatternBase.ESpecialForces, this.kind),  Bot.BotType.ETypeEnemy, this.anchor,  null, BotJob.EAttackPlayerFire,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, this.facingAngle, this.actions, BotHealth.EPrivateSoldier, this.id, BotHanded.ERightHanded);
                        case 1: return new Bot(  new BotPattern( BotPatternBase.ESpecialForces, this.kind),  Bot.BotType.ETypeEnemy, this.anchor,  null, BotJob.EAttackPlayerFire,     new Artefact[] { new Artefact( ArtefactType.ESpaz12 ) }, this.facingAngle, this.actions, BotHealth.EPrivateSoldier, this.id, BotHanded.ERightHanded);
                    }
                    break;
                }
            }

            return null;
        }
    }
