
    package de.christopherstock.lib.gl;

    /*******************************************************************************************************************
    *   The Texture-System.
    *
    *   TODO simplify to single method getTextureObject() !!
    *******************************************************************************************************************/
    public interface LibTexture
    {
        LibGLTextureMetaData getMetaData();
        LibGLTextureImage    getImage();
        String               getName();
    }
