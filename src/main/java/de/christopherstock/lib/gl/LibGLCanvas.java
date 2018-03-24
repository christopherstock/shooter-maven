
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *******************************************************************************************************************/
    public class LibGLCanvas extends Canvas
    {
        public LibGLCanvas()
        {
            //set focusable so lwjgl display will regain the focus
            this.setFocusable( true );
        }

        @Override
        public void paint( Graphics g )
        {
            //cast to 2d
            Graphics2D g2d = (Graphics2D)g;

            // from black bg to white bg
            g2d.setClip( 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE );
            g2d.setColor( LibColors.EWhite.colARGB );
            g2d.fillRect( 0, 0, this.getWidth(), this.getHeight() );

            //calling super-paint here is required
            super.paint( g );
        }
    }
