
    package de.christopherstock.shooter.level;

    import  de.christopherstock.lib.LibViewSet;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.objects.ItemEvent;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.level.Level.*;

    /*******************************************************************************************************************
    *   The configuration set for one main level.
    *******************************************************************************************************************/
    public class LevelConfigMain
    {
        private                 String                  descLevel                   = null;
        protected               LibViewSet              startPosition               = null;
        protected               ArtefactType[]          startupWearpons             = null;
        protected               ItemEvent[]             startupItems                = null;
        protected               InvisibleZeroLayerZ     hasInvisibleZLayer          = null;
        protected               SoundBg                 bgSound                     = null;

        public LevelConfigMain
        (
            String              desc,
            LibViewSet          startPosition,
            ArtefactType[]      startupWearpons,
            ItemEvent[]         startupItems,
            InvisibleZeroLayerZ hasInvisibleZLayer,
            SoundBg             bgSound
        )
        {
            this.descLevel          = desc;
            this.startPosition      = startPosition;
            this.startupWearpons    = startupWearpons;
            this.startupItems       = startupItems;
            this.hasInvisibleZLayer = hasInvisibleZLayer;
            this.bgSound            = bgSound;
        }
    }
