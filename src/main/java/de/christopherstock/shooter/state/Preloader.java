
    package de.christopherstock.shooter.state;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.Fonts;

    /*******************************************************************************************************************
    *   Represents the preloader shown in the 1st state of the game.
    *******************************************************************************************************************/
    public class Preloader
    {
        /** The background image. TODO outsource! */
        private                     LibGLImage              bgImage                     = null;

        /** The current percentage of loaded contents. */
        private                     int                     percentageLoaded            = 0;

        /***************************************************************************************************************
        *   Creates a new preloader.
        *
        *   @param bgImage The image to show in the background of the preloader screen.
        ***************************************************************************************************************/
        public Preloader( LibGLImage bgImage )
        {
            this.bgImage = bgImage;
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
            LibGLImage text = LibGLImage.getFromString( this.percentageLoaded + " %", Fonts.EPreloader, LibColors.EOrangeMF.colABGR, null, null, ShooterDebug.glImage );
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
            ShooterDebug.init.out( "preloader increase to [" + percentage + "]" );

            this.percentageLoaded = percentage;

            // repaint
            Shooter.game.engine.frame.display();
        }
    }
