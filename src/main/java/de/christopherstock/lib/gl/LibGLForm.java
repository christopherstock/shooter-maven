
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.event.*;
    import  java.awt.image.*;
    import  javax.swing.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.shooter.Shooter;

    /*******************************************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *******************************************************************************************************************/
    public class LibGLForm implements WindowListener
    {
        private                     LibGLFrame              nativeFrame         = null;
        private                     BufferedImage           iconImage           = null;

        public LibGLForm( String aTitle, Component contentPane, int width, int height, BufferedImage aIconImage )
        {
            this.iconImage = aIconImage;

            //instanciate JFrame
            this.nativeFrame = new LibGLFrame();

            //get screen environment
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            this.nativeFrame.setIconImage(this.iconImage);
            this.nativeFrame.setTitle(                    aTitle                          );
            this.nativeFrame.setDefaultCloseOperation(    WindowConstants.EXIT_ON_CLOSE            );

            this.nativeFrame.setLocation( ( screenSize.width - width ) / 2, ( screenSize.height - height ) / 2 );
            this.nativeFrame.setSize( width, height );

            this.nativeFrame.setResizable(                false                           );
            this.nativeFrame.setUndecorated(              true                            );

            //add listener
            this.nativeFrame.addWindowListener(           this                            );

            //set canvas as content pane
            this.nativeFrame.getContentPane().add(        contentPane                     );

            //show form
            this.nativeFrame.setVisible(                  true                            );

            //stick in foreground ( may raise a SucurityException )
            try
            {
                this.nativeFrame.setAlwaysOnTop(          true                            );
            }
            catch ( SecurityException se )
            {
                //ignore exception
            }
        }

        public void windowClosing( WindowEvent arg0 )
        {
            //( (LibGLFrame)nativeForm ).iBgImage = null;
            Shooter.game.quit();
        }

        public void windowOpened(       WindowEvent arg0 )
        {
            //no operations
        }

        public void windowClosed(       WindowEvent arg0 )
        {
            //no operations
        }

        public void windowIconified(    WindowEvent arg0 )
        {
            //no operations
        }

        public void windowDeiconified(  WindowEvent arg0 )
        {
            //no operations
        }

        public void windowActivated(    WindowEvent arg0 )
        {
            //no operations
        }

        public void windowDeactivated(  WindowEvent arg0 )
        {
            //no operations
        }

        /***************************************************************************************************************
        *   Sets lookAndFeel of the host operating system.
        ***************************************************************************************************************/
        public static void setLookAndFeel(LibDebug debug )
        {
            try
            {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
            catch( Exception e )
            {
                debug.err( "Setting host-operating system lookAndFeel failed!\n" + e.toString() );
            }
        }
    }
