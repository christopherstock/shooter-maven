
    package de.christopherstock.lib.gl;

    import  java.awt.image.*;
    import  org.lwjgl.opengl.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.LibGLFrame.GLCallbackForm;
    import de.christopherstock.lib.gl.LibGLPanel.*;

    public class LibGL3D
    {
        public                      LibGLView           view                            = null;
        public                      LibGLPanel          panel                           = null;
        public                      LibGLForm           form                            = null;

        public void init
        (
            int                 formWidth,
            int                 formHeight,
            String              formTitle,
            GLCallbackForm      callbackForm,
            BufferedImage       iconImage,
            LibDebug            debug
        )
        {
            //init gl ui components

            //init panel
            this.panel = new LibGLPanel();

            //show lwjgl form
            this.form = new LibGLForm
            (
                callbackForm,
                formTitle,
                this.panel.getNativePanel(),
                formWidth,
                formHeight,
                iconImage
            );

            //init lwjgl view
            this.view = new LibGLView
            (
                this.panel,
                debug,
                (float)formWidth / (float)formHeight
            );
            this.view.init( formWidth, formHeight );
        }

        public void destroy()
        {
            //destroy the display
            Display.destroy();
            System.exit( 0 );
        }
    }
