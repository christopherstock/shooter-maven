
    package de.christopherstock.lib.gl;

    /**************************************************************************************
    *   The Texture-System.
    **************************************************************************************/
    public abstract interface LibTexture
    {
        public abstract LibGLTexture getTexture();
        public abstract LibGLImage getTextureImage();
        public abstract void loadImage( String url );
    }
