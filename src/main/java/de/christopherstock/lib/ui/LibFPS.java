
    package de.christopherstock.lib.ui;

    import  java.awt.Color;
    import  java.awt.Font;
    import java.util.concurrent.TimeUnit;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.*;
    import de.christopherstock.shooter.Shooter;

    /*******************************************************************************************************************
    *   The Frames Per Second display.
    *******************************************************************************************************************/
    public class LibFPS
    {
        private                 LibGLImage      iCurrentFps                 = null;
        private                 Font            iFont                       = null;
        private                 Color           iColFont                    = null;
        private                 Color           iColOutline                 = null;
        private                 LibDebug        iDebug                      = null;
        private                 long            iStartMeassuringMillis      = 0;
        private                 int             iFramesDrawn                = 0;

        public LibFPS( Font aFont, Color aColFont, Color aColOutline, LibDebug aDebug )
        {
            this.iFont = aFont;
            this.iColFont = aColFont;
            this.iColOutline = aColOutline;
            this.iDebug = aDebug;
        }

        public final void update()
        {
            //init fps-counter
            if (this.iStartMeassuringMillis == 0 )
            {
                this.iFramesDrawn = 0;
                this.iStartMeassuringMillis = System.currentTimeMillis();
            }
            else
            {
                //increase number of drawn frames
                ++this.iFramesDrawn;

                //check if 1 sec is over
                if ( System.currentTimeMillis() - this.iStartMeassuringMillis >= TimeUnit.SECONDS.toMillis( 1 ) )
                {
                    this.iCurrentFps = LibGLImage.getFromString
                    (
                            this.iFramesDrawn + " fps",
                            this.iFont,
                            this.iColFont,
                        null,
                            this.iColOutline,
                            this.iDebug
                    );
                    this.iFramesDrawn = 0;
                    this.iStartMeassuringMillis = System.currentTimeMillis();
                }
            }
        }

        public final void draw( int offX, int offY )
        {
            if (this.iCurrentFps != null )
            {
                Shooter.game.engine.glView.drawOrthoBitmapBytes(this.iCurrentFps, Shooter.game.engine.frame.width - offX - this.iCurrentFps.width, Shooter.game.engine.frame.height - offY - this.iCurrentFps.height );
            }
        }
    }
