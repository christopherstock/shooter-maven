
    package de.christopherstock.shooter.ui;

    import  de.christopherstock.lib.io.LibIO;
    import  de.christopherstock.shooter.ShooterDebug;
    import  de.christopherstock.shooter.ShooterSetting.Path;
    import  java.awt.*;

    public final class Fonts
    {
        public                  Font            ammo                                = null;
        public                  Font            preloader                           = null;
        public                  Font            health                              = null;
        public                  Font            fps                                 = null;
        public                  Font            avatarMessage                       = null;
        public                  Font            mainMenu                            = null;

        public void init()
        {
            try
            {
                this.ammo          = LibIO.createFont( Path.EFont.url + "sourceSansPro.otf", 18.0f ); // new Font( "verdana",     Font.BOLD,  12 );
                this.preloader     = LibIO.createFont( Path.EFont.url + "sourceSansPro.otf", 25.0f );
                this.health        = LibIO.createFont( Path.EFont.url + "sourceSansPro.otf", 18.0f );
                this.fps           = LibIO.createFont( Path.EFont.url + "sourceSansPro.otf", 18.0f );
                this.avatarMessage = LibIO.createFont( Path.EFont.url + "sourceSansPro.otf", 18.0f );
                this.mainMenu      = LibIO.createFont( Path.EFont.url + "sourceSansPro.otf", 55.0f );
            }
            catch ( Throwable t )
            {
                ShooterDebug.error.trace( t );
                System.exit( 1 );
            }
        }
    }
