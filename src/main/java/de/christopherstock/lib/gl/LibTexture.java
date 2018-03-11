/*  $Id: LibGLTexture.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
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
