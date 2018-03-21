
    package de.christopherstock.lib.gl;

    /*******************************************************************************************************************
    *   The Texture-System.
    *******************************************************************************************************************/
    public interface LibTexture
    {
        LibGLTexture getTexture();
        LibGLImage getTextureImage();
        void loadImage(String url);
    }
