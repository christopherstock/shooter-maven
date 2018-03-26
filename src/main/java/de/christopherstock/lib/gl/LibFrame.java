
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
    public class LibFrame implements WindowListener
    {
        private                     JFrame                  frame                   = null;
        private                     Canvas                  canvas                  = null;

        public                      int                     width                   = 0;
        public                      int                     height                  = 0;

        public LibFrame(String aTitle, int width, int height, BufferedImage frameIcon )
        {
            this.canvas = new Canvas();
            this.frame = new JFrame();

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            this.frame.setIconImage( frameIcon );
            this.frame.setTitle(                  aTitle                          );
            this.frame.setDefaultCloseOperation(  WindowConstants.EXIT_ON_CLOSE            );

            this.frame.setLocation( ( screenSize.width - width ) / 2, ( screenSize.height - height ) / 2 );
            this.frame.setSize( width, height );

            this.frame.setResizable(              false                           );
            this.frame.setUndecorated(            true                            );

            //add listener
            this.frame.addWindowListener(         this                            );

            //set canvas as content pane
            this.frame.getContentPane().add(      this.canvas                     );

            //show form
            this.frame.setVisible(                true                            );
/*
            //stick in foreground ( may raise a SucurityException )
            try
            {
                this.frame.setAlwaysOnTop(        true                            );
            }
            catch ( SecurityException se )
            {
                //ignore exception
            }
*/
        }

        public final Canvas getCanvas()
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
            return (Graphics2D) this.getCanvas().getGraphics();
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
