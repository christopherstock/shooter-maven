
    package de.christopherstock.lib.gl;

    /**************************************************************************************
    *   The Texture-System.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract interface LibTexture
    {
        public abstract LibGLTexture getTexture();
        public abstract LibGLImage getTextureImage();
        public abstract void loadImage( String url );
    }
