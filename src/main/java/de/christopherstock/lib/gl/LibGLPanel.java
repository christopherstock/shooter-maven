
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.event.WindowEvent;
    import  java.awt.event.WindowListener;
    import  java.awt.image.BufferedImage;
    import  de.christopherstock.shooter.Shooter;
    import  org.lwjgl.opengl.*;
    import  javax.swing.*;

    /*******************************************************************************************************************
    *   The Panel is completely obsolete!.
    *******************************************************************************************************************/
    public class LibGLPanel implements WindowListener
    {
        private                     LibGLFrame              nativeFrame             = null;
        private                     LibGLCanvas             canvas                  = null;

        private                     BufferedImage           iconImage               = null;

        public                      int                     width                   = 0;
        public                      int                     height                  = 0;

        public LibGLPanel( String aTitle, int width, int height, BufferedImage aIconImage )
        {
            this.iconImage = aIconImage;

            try
            {
                this.canvas = new LibGLCanvas();

                //set canvas focusable
                this.canvas.setFocusable( true );
            }
            catch ( Exception e )
            {
                //ignore exception
            }

            // TODO move to LibGLFrame!

            //instanciate JFrame
            this.nativeFrame = new LibGLFrame();

            //get screen environment
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            this.nativeFrame.setIconImage(this.iconImage);
            this.nativeFrame.setTitle(                  aTitle                          );
            this.nativeFrame.setDefaultCloseOperation(  WindowConstants.EXIT_ON_CLOSE            );

            this.nativeFrame.setLocation( ( screenSize.width - width ) / 2, ( screenSize.height - height ) / 2 );
            this.nativeFrame.setSize( width, height );

            this.nativeFrame.setResizable(              false                           );
            this.nativeFrame.setUndecorated(            true                            );

            //add listener
            this.nativeFrame.addWindowListener(         this                            );

            //set canvas as content pane
            this.nativeFrame.getContentPane().add(      this.canvas                     );

            //show form
            this.nativeFrame.setVisible(                true                            );

            //stick in foreground ( may raise a SucurityException )
            try
            {
                this.nativeFrame.setAlwaysOnTop(        true                            );
            }
            catch ( SecurityException se )
            {
                //ignore exception
            }
        }

        public final Component getNativePanel()
        {
            return this.canvas;
        }

        public final void display()
        {
            //invoke callback 3d drawing
            Shooter.game.draw();

            //update native LWJGL Display each tick
            Display.update();
        }

        public Graphics2D getGraphics()
        {
            return (Graphics2D) this.getNativePanel().getGraphics();
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
