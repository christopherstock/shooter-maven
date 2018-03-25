
    package de.christopherstock.shooter.editor;

    import  java.awt.*;
    import  javax.swing.*;

    /*******************************************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *******************************************************************************************************************/
    public class EditorFrame extends JFrame
    {
        public      static      final       int     FRAME_WIDTH         = 800;
        public      static      final       int     FRAME_HEIGHT        = 600;

        public EditorFrame( JPanel content )
        {
            int width  = FRAME_WIDTH;
            int height = FRAME_HEIGHT;

            //get screen environment
            Point centerPoint   = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();

          //form.setIconImage(                  iIconImage                          );
            this.setTitle(                      "Shooter Level Editor"              );
            this.setDefaultCloseOperation(      JFrame.EXIT_ON_CLOSE                );
            this.setLocation(                   (int)centerPoint.getX() - width / 2, (int)centerPoint.getY() - height / 2 );
            this.setSize(                       width, height                       );
            this.setResizable(                  false                               );
            this.setUndecorated(                false                               );

            this.getContentPane().add( content );
            this.pack();
            this.setLocationByPlatform( true );
            this.setVisible( true );

            //stick in foreground ( may raise a SucurityException )
            try
            {
                this.setAlwaysOnTop(            true                                );
            }
            catch ( SecurityException se )
            {
                //ignore exception
            }
        }
    }
