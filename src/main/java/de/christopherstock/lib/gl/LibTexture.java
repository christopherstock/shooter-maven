
    package de.christopherstock.lib.gl;

    /*******************************************************************************************************************
    *   The Texture-System.
    *******************************************************************************************************************/
    public interface LibTexture
    {
        LibGLTextureMetaData getMetaData();
        LibGLTextureImage    getImage();
        String               getName();
    }
