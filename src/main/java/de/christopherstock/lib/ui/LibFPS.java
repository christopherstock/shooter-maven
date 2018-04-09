
    package de.christopherstock.lib.ui;

    import  java.awt.Color;
    import  java.awt.Font;
    import  java.util.concurrent.TimeUnit;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.Shooter;

    /*******************************************************************************************************************
    *   The Frames Per Second display.
    *******************************************************************************************************************/
    public class LibFPS
    {
        private LibGLTextureImage currentFps                  = null;
        private                     Font            font                        = null;
        private                     Color           colFont                     = null;
        private                     Color           colOutline                  = null;
        private                     LibDebug        debug                       = null;
        private                     long            startMeassuringMillis       = 0;
        private                     int             framesDrawn                 = 0;

        public LibFPS( Font font, Color colFont, Color colOutline, LibDebug debug )
        {
            this.font       = font;
            this.colFont    = colFont;
            this.colOutline = colOutline;
            this.debug      = debug;
        }

        public final void update()
        {
            //init fps-counter
            if (this.startMeassuringMillis == 0 )
            {
                this.framesDrawn = 0;
                this.startMeassuringMillis = System.currentTimeMillis();
            }
            else
            {
                //increase number of drawn frames
                ++this.framesDrawn;

                //check if 1 sec is over
                if ( System.currentTimeMillis() - this.startMeassuringMillis >= TimeUnit.SECONDS.toMillis( 1 ) )
                {
                    this.currentFps = LibGLTextureImage.getFromString
                    (
                            this.framesDrawn + " fps",
                            this.font,
                            this.colFont,
                        null,
                            this.colOutline,
                            this.debug
                    );
                    this.framesDrawn = 0;
                    this.startMeassuringMillis = System.currentTimeMillis();
                }
            }
        }

        public final void draw( int offX, int offY )
        {
            if (this.currentFps != null )
            {
                Shooter.game.engine.glView.drawOrthoBitmapBytes(this.currentFps, Shooter.game.engine.glView.width - offX - this.currentFps.width, Shooter.game.engine.glView.height - offY - this.currentFps.height );
            }
        }
    }
