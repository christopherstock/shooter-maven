
    package de.christopherstock.lib.gl;

    import  de.christopherstock.lib.ui.LibColors;
    import  java.awt.*;
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

        @Override
        public void paint( Graphics g )
        {
            //do NOT call super-paint here! form will not draw otherwise
            //super.paint( g );

            //cast to 2d
            Graphics2D g2d = (Graphics2D)g;

            g2d.setClip( 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE );
            g2d.setColor(LibColors.ERed.colARGB );
            g2d.fillRect( 0, 0, this.getWidth(), this.getHeight() );

            //call super-paint here in order to draw gl stuff!
            super.paint( g );
        }
    }
