/*  $Id: ShooterLevelConfig.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.level;

    import  de.christopherstock.lib.Lib.ViewSet;
import de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.objects.ItemEvent;
    import  de.christopherstock.shooter.io.sound.*;
import  de.christopherstock.shooter.level.Level.*;

    /**************************************************************************************
    *   The configuration set for one main level.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class LevelConfigMain
    {
        protected               String                  iDescLevel              = null;
        protected               ViewSet                 iStartPosition          = null;
        protected               ArtefactType[]          iStartupWearpons        = null;
        protected               ItemEvent[]             iStartupItems           = null;
        protected               InvisibleZeroLayerZ     iHasInvisibleZLayer     = null;
        protected               SoundBg          iBgSound                = null;

        public LevelConfigMain( String aDesc, ViewSet aStartPosition, ArtefactType[] aStartupWearpons, ItemEvent[] aStartupItems, InvisibleZeroLayerZ aHasInvisibleZLayer, SoundBg aBgSound )
        {
            iDescLevel          = aDesc;
            iStartPosition      = aStartPosition;
            iStartupWearpons    = aStartupWearpons;
            iStartupItems       = aStartupItems;
            iHasInvisibleZLayer = aHasInvisibleZLayer;
            iBgSound            = aBgSound;
        }
    }
