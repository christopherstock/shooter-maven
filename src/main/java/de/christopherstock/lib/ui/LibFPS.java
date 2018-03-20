
    package de.christopherstock.lib.ui;

    import  java.awt.Color;
    import  java.awt.Font;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.*;

    /**************************************************************************************
    *   The Frames Per Second display.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
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
            iFont       = aFont;
            iColFont    = aColFont;
            iColOutline = aColOutline;
            iDebug      = aDebug;
        }

        public final void finishedDrawing()
        {
            //init fps-counter
            if ( iStartMeassuringMillis == 0 )
            {
                iFramesDrawn             = 0;
                iStartMeassuringMillis   = System.currentTimeMillis();
            }
            else
            {
                //increase number of drawn frames
                ++iFramesDrawn;

                //check if 1 sec is over
                if ( System.currentTimeMillis() - iStartMeassuringMillis >= Lib.MILLIS_PER_SECOND )
                {
                    iCurrentFps = LibGLImage.getFromString
                    (
                        iFramesDrawn + " fps",
                        iFont,
                        iColFont,
                        null,
                        iColOutline,
                        iDebug
                    );
                    iFramesDrawn             = 0;
                    iStartMeassuringMillis   = System.currentTimeMillis();
                }
            }
        }

        public final void draw( int offX, int offY )
        {
            if ( iCurrentFps != null )
            {
                LibGL3D.view.drawOrthoBitmapBytes( iCurrentFps, LibGL3D.panel.width - offX - iCurrentFps.width, LibGL3D.panel.height - offY - iCurrentFps.height );
            }
        }
    }
