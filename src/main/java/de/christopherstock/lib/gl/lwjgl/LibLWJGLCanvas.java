
    package de.christopherstock.lib.gl.lwjgl;

    import  java.awt.*;
    import  java.awt.event.*;
    import  java.awt.image.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *******************************************************************************************************************/
    public class LibLWJGLCanvas extends Canvas implements FocusListener
    {
        private     static  final   long            serialVersionUID    = 7582941416008508760L;

        public                      BufferedImage   iBgImage            = null;

        public LibLWJGLCanvas( BufferedImage aBgImage )
        {
            this.iBgImage = aBgImage;

            //set focusable so lwjgl display will regain the focus
            this.setFocusable( true );

            this.addFocusListener( this );
        }

        @Override
        public void paint( Graphics g )
        {
            //calling super-paint here is required
            super.paint( g );

            //cast to 2d
            Graphics2D g2d = (Graphics2D)g;

            //clip
            g2d.setClip( 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE );

            //draw black rect
            g2d.setColor( LibColors.EBlack.colARGB );
            g2d.fillRect( 0, 0, this.getWidth(), this.getHeight() );

            //draw bg image centered
            g2d.drawImage(this.iBgImage, (this.getWidth() - this.iBgImage.getWidth() ) / 2, (this.getHeight() - this.iBgImage.getHeight() ) / 2, null );
        }

        public void focusGained( FocusEvent f )
        {
            //ShooterDebug.bugfix.out( "GAINED FOCUS" );
        }

        public void focusLost( FocusEvent f )
        {
            //ShooterDebug.bugfix.out( "LOST FOCUS" );
/*
            if ( doIt-- <= 0 )
            {
                requestFocus();
            }
*/
        }
        //int doIt = 5;
    }
