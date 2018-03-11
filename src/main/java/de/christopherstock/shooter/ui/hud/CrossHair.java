/*  $Id: CrossHair.java 1264 2013-01-06 16:55:45Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.ui.hud;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;

    /**************************************************************************************
    *   Represents a pop-up-message with an avatar image and a message.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public enum CrossHair
    {
        ECircle,
        EDefault,
        EPrecise,
        ESmallest,
        ;

        private         LibGLImage          iImg         = null;

        public static final void loadImages()
        {
            for ( CrossHair crossHairImage : values() )
            {
                crossHairImage.loadImage();
            }
        }

        private final void loadImage()
        {
            iImg = new LibGLImage( LibImage.load( ShooterSettings.Path.ECrossHair.iUrl + toString() + LibExtension.png.getSpecifier(), ShooterDebug.glImage, false ), ImageUsage.EOrtho, ShooterDebug.glImage, true );
        }

        public final LibGLImage getImage()
        {
            return iImg;
        }
    }
