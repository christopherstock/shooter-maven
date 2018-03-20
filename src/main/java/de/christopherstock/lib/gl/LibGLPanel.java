
    package de.christopherstock.lib.gl;

    import  java.awt.*;

    /**************************************************************************************
    *   The Form.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract class LibGLPanel
    {
        public static interface GLDrawCallback
        {
            public abstract void draw2D();
            public abstract void draw3D();
        }

        public                      int                 width                   = 0;
        public                      int                 height                  = 0;

        public abstract Component getNativePanel();

        public Graphics2D getGraphics()
        {
            return (Graphics2D)getNativePanel().getGraphics();
        }

        public abstract void display();
    }
