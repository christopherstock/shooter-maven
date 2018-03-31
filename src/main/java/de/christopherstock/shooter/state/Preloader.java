
    package de.christopherstock.shooter.state;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  javax.imageio.*;
    import  java.awt.image.*;
    import  java.io.*;

    /*******************************************************************************************************************
    *   Represents the preloader shown in the 1st state of the game.
    *******************************************************************************************************************/
    public class Preloader
    {
        /** The background image. */
        private                     LibGLTextureImage       bgImage                     = null;

        /** The current percentage of loaded contents. */
        private                     int                     percentageLoaded            = 0;

        /***************************************************************************************************************
        *   Creates a new preloader.
        ***************************************************************************************************************/
        public Preloader()
        {
            try
            {
                BufferedImage bg = ImageIO.read( LibIO.preStreamJarResource( ShooterSetting.Path.EScreen.url + "bg.jpg"   ) );
                this.bgImage = new LibGLTextureImage( bg, LibGLTextureImage.ImageUsage.EOrtho, ShooterDebug.glImage, true );
            }
            catch ( IOException ioe )
            {
                ShooterDebug.error.err( "ERROR! on loading preloader bg image." );
            }
        }

        /***************************************************************************************************************
        *   Being invoked when the preloader should be drawn.
        ***************************************************************************************************************/
        public final void draw()
        {
            // clear screen
            Shooter.game.engine.glView.clearGl( LibColors.EWhite );

            // draw bg image
            Shooter.game.engine.glView.drawOrthoBitmapBytes
            (
                this.bgImage,
                ( Shooter.game.engine.glView.width  - this.bgImage.width  ) / 2,
                ( Shooter.game.engine.glView.height - this.bgImage.height ) / 2
            );

            // draw progress
            LibGLTextureImage text = LibGLTextureImage.getFromString( this.percentageLoaded + " %", Shooter.game.engine.fonts.preloader, LibColors.EOrangeMF.colABGR, null, null, ShooterDebug.glImage );
            Shooter.game.engine.glView.drawOrthoBitmapBytes
            (
                text,
                ( Shooter.game.engine.glView.width      - text.width  ) / 2,
                ( Shooter.game.engine.glView.height / 2 - text.height ) / 2
            );
        }

        /***************************************************************************************************************
        *   Being invoked when the preloader should increase.
        *
        *   @param percentage The new percentage value to increase to.
        ***************************************************************************************************************/
        public final void increase( int percentage )
        {
            // ShooterDebug.init.out( "preloader increase to [" + percentage + "]" );

            this.percentageLoaded = percentage;

            // repaint
            Shooter.game.draw();
        }
    }
