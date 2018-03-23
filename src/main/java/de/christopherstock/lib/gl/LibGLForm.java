
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.event.*;
    import  java.awt.image.*;
    import  javax.swing.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.LibGLFrame.GLCallbackForm;

    /*******************************************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *******************************************************************************************************************/
    public class LibGLForm implements WindowListener, FocusListener
    {
        public                      GLCallbackForm  iForm                       = null;
        public                      JFrame          iNativeForm                 = null;
        private                     BufferedImage   iIconImage                  = null;
        public                      BufferedImage   iBgImage                    = null;

        public LibGLForm( GLCallbackForm aForm, String aTitle, Component contentPane, int width, int height, BufferedImage aIconImage, BufferedImage aBgImage )
        {
            this.iForm = aForm;
            this.iIconImage = aIconImage;
            this.iBgImage = aBgImage;

            //instanciate JFrame
            this.iNativeForm = new LibGLFrame(this.iBgImage);

            //get screen environment
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            this.iNativeForm.setIconImage(this.iIconImage);
            this.iNativeForm.setTitle(                    aTitle                          );
            this.iNativeForm.setDefaultCloseOperation(    WindowConstants.EXIT_ON_CLOSE            );

            this.iNativeForm.setLocation( ( screenSize.width - width ) / 2, ( screenSize.height - height ) / 2 );
            this.iNativeForm.setSize( width, height );

            this.iNativeForm.setResizable(                false                           );
            this.iNativeForm.setUndecorated(              true                            );

            //add listener
            this.iNativeForm.addWindowListener(           this                            );
            this.iNativeForm.addFocusListener(            this                            );

            //set canvas as content pane
            this.iNativeForm.getContentPane().add(        contentPane                     );

            //show form
            this.iNativeForm.setVisible(                  true                            );

            //stick in foreground ( may raise a SucurityException )
            try
            {
                this.iNativeForm.setAlwaysOnTop(          true                            );
            }
            catch ( SecurityException se )
            {
                //ignore exception
            }
        }

        public void windowClosing( WindowEvent arg0 )
        {
            //( (LibGLFrame)nativeForm ).iBgImage = null;
            this.iForm.onFormDestroyed();
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

        public void focusLost( FocusEvent fe )
        {
            //no operations
        }

        public void focusGained( FocusEvent fe )
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
