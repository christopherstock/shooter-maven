/*  $Id: ShooterLevelSectionConfig.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.level;

    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.game.bot.*;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /**************************************************************************************
    *   The configuration set for one level section.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class LevelConfigSection
    {
        protected               String                  iDescSection            = null;
        protected               BackGround              iBg                     = null;
        protected               LibColors               iBgCol                  = null;
        protected               ItemToPickUp[]          iItems                  = null;
        protected               BotFactory[]     iBots                   = null;

        public LevelConfigSection( String aDesc, LibColors aBgCol, BackGround aBg, ItemToPickUp[] aItems, BotFactory[] aStartupBots )
        {
            iDescSection        = aDesc;
            iBgCol              = aBgCol;
            iBg                 = aBg;
            iItems              = aItems;
            iBots               = aStartupBots;
        }
    }
