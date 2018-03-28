
    package de.christopherstock.lib.gl;

    /*******************************************************************************************************************
    *   The Texture-System.
    *
    *   TODO simplify to single method getTextureObject() !!
    *******************************************************************************************************************/
    public interface LibTexture
    {
        LibGLTexture getTexture();

        LibGLImage getTextureImage();

        void loadImage( String url );
    }
