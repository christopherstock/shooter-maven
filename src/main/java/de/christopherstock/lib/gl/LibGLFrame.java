
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.image.*;
    import  javax.swing.*;

    /*******************************************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *******************************************************************************************************************/
    public class LibGLFrame extends JFrame
    {
        public interface GLCallbackForm
        {
            void onFormDestroyed();
        }

        private     static  final   long            serialVersionUID        = -4651971360551632489L;

        public                      BufferedImage   iBgImage                = null;

        public LibGLFrame( BufferedImage aBgImage )
        {
            iBgImage = aBgImage;
        }

        @Override
        public void paint( Graphics g )
        {
            //do NOT call super-paint here! form will not draw otherwise
            //super.paint( g );

            //cast to 2d
            Graphics2D g2d = (Graphics2D)g;

            if ( iBgImage != null )
            {
                //draw bg image
                g2d.setClip( 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE );
                g2d.drawImage( iBgImage, 0, 0, null );

                //call super-paint here in order to draw gl stuff!
                super.paint( g );
            }
        }
    }
