
    package de.christopherstock.lib.gl.lwjgl;

    import  java.awt.*;
    import  org.lwjgl.opengl.*;

    /*******************************************************************************************************************
    *   The Form.
    *******************************************************************************************************************/
    public class LibLWJGLPanel
    {
        public interface GLDrawCallback
        {
            void draw2D();
            void draw3D();
        }

        private                     Canvas                  canvas                  = null;
        private                     GLDrawCallback          drawCallback            = null;

        public                      int                     width                   = 0;
        public                      int                     height                  = 0;

        public LibLWJGLPanel( GLDrawCallback aDrawCallback )
        {
            this.drawCallback = aDrawCallback;

            try
            {
                this.canvas = new LibLWJGLCanvas();

                //set canvas focusable
                this.canvas.setFocusable( true );
            }
            catch ( Exception e )
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
            //only if the panel is initialized
//            if ( LibGL3D.glPanelInitialized )
            {
                //invoke callback 3d drawing
                this.drawCallback.draw3D();

                //invoke callback 2d drawing
                this.drawCallback.draw2D();

                //update native LWJGL Display each tick
                Display.update();
            }
        }

        public Graphics2D getGraphics()
        {
            return (Graphics2D) this.getNativePanel().getGraphics();
        }
    }
