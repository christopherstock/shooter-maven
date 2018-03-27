
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.event.WindowEvent;
    import  java.awt.event.WindowListener;
    import  java.awt.image.BufferedImage;
    import  de.christopherstock.shooter.Shooter;
    import  javax.swing.*;

    /*******************************************************************************************************************
    *   The Panel is completely obsolete!.
    *******************************************************************************************************************/
    public class LibFrame implements WindowListener
    {
        private                     String                  title                   = null;
        private                     int                     width                   = 0;
        private                     int                     height                  = 0;
        private                     BufferedImage           icon                    = null;

        private                     JFrame                  frame                   = null;
        private                     Canvas                  canvas                  = null;

        public LibFrame( String title, int width, int height, BufferedImage icon )
        {
            this.title  = title;
            this.width  = width;
            this.height = height;
            this.icon   = icon;

            this.frame  = new JFrame();
            this.canvas = new Canvas();
        }

        public void init()
        {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            this.frame.setIconImage( this.icon );
            this.frame.setTitle( this.title );
            this.frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
            this.frame.setLocation( ( screenSize.width - this.width ) / 2, ( screenSize.height - this.height ) / 2 );
            this.frame.setSize( this.width, this.height );
            this.frame.setResizable( false );
            this.frame.setUndecorated( true );
            this.frame.addWindowListener( this );
            this.frame.getContentPane().add( this.canvas );
            this.frame.setVisible( true );
        }

        public final Canvas getCanvas()
        {
            return this.canvas;
        }

        public Graphics2D getGraphics()
        {
            return (Graphics2D)this.canvas.getGraphics();
        }

        public void windowClosing( WindowEvent arg0 )
        {
            Shooter.game.quit();
        }

        public void windowOpened( WindowEvent arg0 )
        {
            // no operations
        }

        public void windowClosed( WindowEvent arg0 )
        {
            // no operations
        }

        public void windowIconified( WindowEvent arg0 )
        {
            // no operations
        }

        public void windowDeiconified( WindowEvent arg0 )
        {
            // no operations
        }

        public void windowActivated(  WindowEvent arg0 )
        {
            // no operation
        }

        public void windowDeactivated( WindowEvent arg0 )
        {
            // no operation
        }
    }
