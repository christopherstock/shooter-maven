
    package de.christopherstock.shooter.ui.hud;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLTextureImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;

    /*******************************************************************************************************************
    *   Represents a pop-up-message with an avatar image and a message.
    *******************************************************************************************************************/
    public enum CrossHair
    {
        ECircle,
        EDefault,
        EPrecise,
        ESmallest,
        ;

        private                 LibGLTextureImage           img                 = null;

        public static void loadImages()
        {
            for ( CrossHair crossHairImage : values() )
            {
                crossHairImage.loadImage();
            }
        }

        private void loadImage()
        {
            this.img = new LibGLTextureImage( LibImage.load( ShooterSetting.Path.ECrossHair.url + this.toString() + LibExtension.png.getSpecifier(), ShooterDebug.glImage, false ), ImageUsage.EOrtho, ShooterDebug.glImage, true );
        }

        public final LibGLTextureImage getImage()
        {
            return this.img;
        }
    }
