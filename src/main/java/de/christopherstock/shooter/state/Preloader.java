
    package de.christopherstock.shooter.state;

    import  java.awt.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.Fonts;
    import  de.christopherstock.shooter.ShooterSettings.Path;

    /*******************************************************************************************************************
    *   The application's main thread. Start this thread to run the application.
    *******************************************************************************************************************/
    public class Preloader
    {
        public                      LibGLImage              bgImage                     = null;
        public                      LibGLImage              preloaderImage              = null;

        private                     int                     preloaderTest               = 0;
        private                     String                  preloaderMsg                = null;

        public final void draw2D()
        {
            try
            {
                //create preloader image if not done yet
                LibGL3D.view.drawOrthoBitmapBytes(this.bgImage, 0, 0 );

                LibGL3D.view.drawOrthoBitmapBytes(this.preloaderImage, LibGL3D.panel.width / 2 - this.preloaderImage.width / 2,                       LibGL3D.panel.height / 2 + this.preloaderImage.height / 2 + 50 );
                LibGL3D.view.drawOrthoBitmapBytes(this.preloaderImage, LibGL3D.panel.width / 2 - this.preloaderImage.width / 2 - 100 + this.preloaderTest, LibGL3D.panel.height / 2 + this.preloaderImage.height / 2      );

                LibGLImage text = LibGLImage.getFromString(this.preloaderMsg + " [ " + this.preloaderTest + " / 100 ]", Fonts.EAmmo, LibColors.EBlack.colABGR, null, LibColors.EGrey.colABGR, ShooterDebug.glImage );
                LibGL3D.view.drawOrthoBitmapBytes( text, LibGL3D.panel.width / 2, LibGL3D.panel.height / 4 + text.height / 2 );
            }
            catch ( Exception e )
            {
                ShooterDebug.error.trace( e );
            }
        }

        public final void draw3D()
        {
            LibGL3D.view.clearGl( LibColors.EBlack );
        }

        public final void increase( String msg )
        {
            ShooterDebug.init.out( "preloader increase [" + msg + "]" );

            this.preloaderMsg = msg;
            this.preloaderTest += 20;

            ShooterDebug.init.out( "display ?" );
            LibGL3D.panel.display();
            ShooterDebug.init.out( "display ? Ok" );
        }

        public final void initFonts()
        {
            try
            {
                Fonts.EIntro         = new Font( "verdana",     Font.BOLD,  25 );
                Fonts.EAmmo          = new Font( "verdana",     Font.BOLD,  12 );
                Fonts.EHealth        = new Font( "verdana",     Font.BOLD,  12 );
                Fonts.EFps           = new Font( "verdana",     Font.BOLD,  12 );
                Fonts.EAvatarMessage = new Font( "verdana",     Font.BOLD,  12 );
                Fonts.EDebug         = new Font( "courier new", Font.PLAIN, 10 );

                Fonts.EMainMenu      = Lib.createFont( Path.EFont.iUrl + "chiller.ttf", 65.0f );
            }
            catch ( Throwable t )
            {
                ShooterDebug.error.trace( t );
                System.exit( 1 );
            }
        }
    }
