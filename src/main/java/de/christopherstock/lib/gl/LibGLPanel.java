
    package de.christopherstock.lib.gl;

    import  java.awt.*;

    /*******************************************************************************************************************
    *   The Form.
    *******************************************************************************************************************/
    public abstract class LibGLPanel
    {
        public interface GLDrawCallback
        {
            void draw2D();
            void draw3D();
        }

        public                      int                 width                   = 0;
        public                      int                 height                  = 0;

        public abstract Component getNativePanel();

        public Graphics2D getGraphics()
        {
            return (Graphics2D) this.getNativePanel().getGraphics();
        }

        public abstract void display();
    }
