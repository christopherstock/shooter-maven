
    package de.christopherstock.shooter.editor;

    import  java.awt.*;
    import  javax.swing.*;

    /**************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    **************************************************************************************/
    public class EditorFrame extends JFrame
    {
        private     static      final       long    serialVersionUID    = 4439350945288277113L;

        public      static      final       int     FRAME_WIDTH         = 800;
        public      static      final       int     FRAME_HEIGHT        = 600;

        public EditorFrame( JPanel content )
        {
            int width  = FRAME_WIDTH;
            int height = FRAME_HEIGHT;

            //get screen environment
            Point centerPoint   = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();

          //form.setIconImage(                  iIconImage                          );
            setTitle(                      "Shooter Level Editor"              );
            setDefaultCloseOperation(      JFrame.EXIT_ON_CLOSE                );
            setLocation(                   (int)centerPoint.getX() - width / 2, (int)centerPoint.getY() - height / 2 );
            setSize(                       width, height                       );
            setResizable(                  false                               );
            setUndecorated(                false                               );

            getContentPane().add( content );
            pack();
            setLocationByPlatform( true );
            setVisible( true );

            //stick in foreground ( may raise a SucurityException )
            try
            {
                setAlwaysOnTop(            true                                );
            }
            catch ( SecurityException se )
            {
                //ignore exception
            }
        }
    }
