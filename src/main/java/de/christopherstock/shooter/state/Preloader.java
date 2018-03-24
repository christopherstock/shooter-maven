
    package de.christopherstock.shooter.state;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.Fonts;

    /*******************************************************************************************************************
    *   The application's main thread. Start this thread to run the application.
    *******************************************************************************************************************/
    public class Preloader
    {
        private                     LibGLImage              bgImage                     = null;

        private                     int                     preloaderTest               = 0;
        private                     String                  preloaderMsg                = null;

        public Preloader( LibGLImage bgImage )
        {
            this.bgImage        = bgImage;
        }

        public final void draw2D()
        {
            try
            {
                //create preloader image if not done yet
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( this.bgImage, ( Shooter.game.engine.gl.panel.width - this.bgImage.width ) / 2, 150 );

                LibGLImage text = LibGLImage.getFromString(this.preloaderMsg + " [ " + this.preloaderTest + " / 100 ]", Fonts.EAmmo, LibColors.EBlack.colABGR, null, LibColors.EGrey.colABGR, ShooterDebug.glImage );
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( text, Shooter.game.engine.gl.panel.width / 2, Shooter.game.engine.gl.panel.height / 4 + text.height / 2 );
            }
            catch ( Exception e )
            {
                ShooterDebug.error.trace( e );
            }
        }

        public final void draw3D()
        {
            Shooter.game.engine.gl.view.clearGl( LibColors.EWhite );
        }

        public final void increase( String msg )
        {
            ShooterDebug.init.out( "preloader increase [" + msg + "]" );

            this.preloaderMsg = msg;
            this.preloaderTest += 20;

            Shooter.game.engine.gl.panel.display();
        }
    }
