
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
        protected               String                  iDescSection            = null;
        protected               BackGround              iBg                     = null;
        protected               LibColors               iBgCol                  = null;
        protected               ItemToPickUp[]          iItems                  = null;
        protected               BotFactory[]            iBots                   = null;

        public LevelConfigSection( String aDesc, LibColors aBgCol, BackGround aBg, ItemToPickUp[] aItems, BotFactory[] aStartupBots )
        {
            this.iDescSection = aDesc;
            this.iBgCol = aBgCol;
            this.iBg = aBg;
            this.iItems = aItems;
            this.iBots = aStartupBots;
        }
    }
