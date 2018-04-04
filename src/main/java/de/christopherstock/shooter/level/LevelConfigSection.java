
    package de.christopherstock.shooter.level;

    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.game.bot.*;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   The configuration set for one level section.
    *******************************************************************************************************************/
    public class LevelConfigSection
    {
        protected               String                  descSection             = null;

        // TODO prune!
        protected               BackGround              bg                      = null;

        protected               LibColors               bgCol                   = null;
        protected               ItemToPickUp[]          items                   = null;
        protected               BotFactory[]            bots                    = null;

        public LevelConfigSection( String desc, LibColors bgCol, BackGround bg, ItemToPickUp[] items, BotFactory[] startupBots )
        {
            this.descSection = desc;
            this.bgCol       = bgCol;
            this.bg          = bg;
            this.items       = items;
            this.bots        = startupBots;
        }
    }
