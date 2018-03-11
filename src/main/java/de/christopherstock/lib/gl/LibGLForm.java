/*  $Id: LibGLForm.java 1287 2014-10-11 17:10:14Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.event.*;
    import  java.awt.image.*;
    import  javax.swing.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.LibGLFrame.GLCallbackForm;

    /**************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *
    *   @author         Christopher Stock
    *   @version        0.3.11
    **************************************************************************************/
    public class LibGLForm implements WindowListener, FocusListener
    {
        public                      GLCallbackForm  iForm                       = null;
        public                      JFrame          iNativeForm                 = null;
        private                     BufferedImage   iIconImage                  = null;
        public                      BufferedImage   iBgImage                    = null;

        public LibGLForm( GLCallbackForm aForm, String aTitle, Component contentPane, int width, int height, BufferedImage aIconImage, BufferedImage aBgImage )
        {
            iForm       = aForm;
            iIconImage  = aIconImage;
            iBgImage    = aBgImage;

            //instanciate JFrame
            iNativeForm = new LibGLFrame( iBgImage );

            //get screen environment
            Point centerPoint   = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();

            iNativeForm.setIconImage(                iIconImage                      );
            iNativeForm.setTitle(                    aTitle                          );
            iNativeForm.setDefaultCloseOperation(    JFrame.EXIT_ON_CLOSE            );
            iNativeForm.setLocation(                 (int)centerPoint.getX() - width / 2, (int)centerPoint.getY() - height / 2 );
            iNativeForm.setSize(                     width, height                   );
            iNativeForm.setResizable(                false                           );
            iNativeForm.setUndecorated(              true                            );

            //add listener
            iNativeForm.addWindowListener(           this                            );
            iNativeForm.addFocusListener(            this                            );

            //set canvas as content pane
            iNativeForm.getContentPane().add(        contentPane                     );

            //show form
            iNativeForm.setVisible(                  true                            );

            //stick in foreground ( may raise a SucurityException )
            try
            {
                iNativeForm.setAlwaysOnTop(          true                            );
            }
            catch ( SecurityException se )
            {
                //ignore exception
            }
        }

        @Override
        public void windowClosing( WindowEvent arg0 )
        {
            //( (LibGLFrame)nativeForm ).iBgImage = null;
            iForm.onFormDestroyed();
        }

        @Override
        public void windowOpened(       WindowEvent arg0 )
        {
            //no operations
        }

        @Override
        public void windowClosed(       WindowEvent arg0 )
        {
            //no operations
        }

        @Override
        public void windowIconified(    WindowEvent arg0 )
        {
            //no operations
        }

        @Override
        public void windowDeiconified(  WindowEvent arg0 )
        {
            //no operations
        }

        @Override
        public void windowActivated(    WindowEvent arg0 )
        {
            //no operations
        }

        @Override
        public void windowDeactivated(  WindowEvent arg0 )
        {
            //no operations
        }

        @Override
        public void focusLost( FocusEvent fe )
        {
            //no operations
        }

        @Override
        public void focusGained( FocusEvent fe )
        {
            //no operations
        }

        /********************************************************************************
        *   Sets lookAndFeel of the host operating system.
        *********************************************************************************/
        public static final void setLookAndFeel( LibDebug debug )
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
