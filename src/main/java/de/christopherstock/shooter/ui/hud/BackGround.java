/*  $Id: BackGround.java 1257 2013-01-04 23:50:02Z jenetic.bytemare@gmail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.ui.hud;

    import  java.awt.image.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLImage.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.PlayerSettings;

    /**************************************************************************************
    *   The Gadgets / ( non-wearpons ) the player can hold.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public enum BackGround
    {
        ECountry1,
        EMeadow1,
        ENight1,
        ;

        public                          LibGLImage      bgImage                     = null;

        public static final void loadImages()
        {
            for ( BackGround bg : values() )
            {
                bg.loadImage();
            }
        }

        public final void loadImage()
        {
            BufferedImage bufferedImage = LibImage.load( ShooterSettings.Path.EBackGrounds.iUrl + toString() + LibExtension.jpg.getSpecifier(), ShooterDebug.glImage, false );
            bgImage = new LibGLImage( bufferedImage, ImageUsage.EOrtho, ShooterDebug.glImage, true );
        }

        public final void drawOrtho( float playerRotX, float playerRotZ )
        {
            //translate left/right
            int x = (int)( bgImage.width * playerRotZ / 360 );

            //translate up/down
            int y = -bgImage.height / 4;
            if ( playerRotX > 0 )
            {
                y += (int)( bgImage.height / 4 * playerRotX / PlayerSettings.MAX_LOOKING_X );
            }
            else if ( playerRotX < 0 )
            {
                y += (int)( bgImage.height / 4 * playerRotX / PlayerSettings.MAX_LOOKING_X );
            }

            //Debug.bugfix.out( "x: [" + x + "] rotZ [" + playerRotZ + "] rotY [" + playerRotX + "]" );

            LibGL3D.view.drawOrthoBitmapBytes( bgImage, x, LibGL3D.panel.height - bgImage.height / 2 + y );
            x -= bgImage.width;
            LibGL3D.view.drawOrthoBitmapBytes( bgImage, x, LibGL3D.panel.height - bgImage.height / 2 + y );
        }
    }
