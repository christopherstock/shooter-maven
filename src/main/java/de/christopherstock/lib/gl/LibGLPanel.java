
    package de.christopherstock.lib.gl;

    import  java.awt.*;

    import de.christopherstock.shooter.Shooter;
    import  org.lwjgl.opengl.*;

    /*******************************************************************************************************************
    *   The Panek.
    *******************************************************************************************************************/
    public class LibGLPanel
    {
        public interface GLDrawCallback
        {
            void draw2D();
            void draw3D();
        }

        private                     LibGLCanvas             canvas                  = null;

        public                      int                     width                   = 0;
        public                      int                     height                  = 0;

        public LibGLPanel()
        {
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
        }

        public final Component getNativePanel()
        {
            return this.canvas;
        }

        public final void display()
        {
            //invoke callback 3d drawing
            Shooter.game.draw3D();

            //invoke callback 2d drawing
            Shooter.game.draw2D();

            //update native LWJGL Display each tick
            Display.update();
        }

        public Graphics2D getGraphics()
        {
            return (Graphics2D) this.getNativePanel().getGraphics();
        }
    }
