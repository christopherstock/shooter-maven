
    package de.christopherstock.shooter.ui.hud;

    import  java.awt.image.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLImage.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.PlayerSettings;

    /*******************************************************************************************************************
    *   The Gadgets / ( non-wearpons ) the player can hold.
    *******************************************************************************************************************/
    public enum BackGround
    {
        ECountry1,
        EMeadow1,
        ENight1,
        ;

        private LibGLImage      bgImage                     = null;

        public static void loadImages()
        {
            for ( BackGround bg : values() )
            {
                bg.loadImage();
            }
        }

        private void loadImage()
        {
            BufferedImage bufferedImage = LibImage.load( ShooterSetting.Path.EBackGrounds.iUrl + this.toString() + LibExtension.jpg.getSpecifier(), ShooterDebug.glImage, false );
            this.bgImage = new LibGLImage( bufferedImage, ImageUsage.EOrtho, ShooterDebug.glImage, true );
        }

        public final void drawOrtho( float playerRotX, float playerRotZ )
        {
            //translate left/right
            int x = (int)(this.bgImage.width * playerRotZ / 360 );

            //translate up/down
            int y = -this.bgImage.height / 4;
            if ( playerRotX > 0 )
            {
                y += (int)(this.bgImage.height / 4 * playerRotX / PlayerSettings.MAX_LOOKING_X );
            }
            else if ( playerRotX < 0 )
            {
                y += (int)(this.bgImage.height / 4 * playerRotX / PlayerSettings.MAX_LOOKING_X );
            }

            //Debug.bugfix.out( "x: [" + x + "] rotZ [" + playerRotZ + "] rotY [" + playerRotX + "]" );

            Shooter.game.engine.gl.view.drawOrthoBitmapBytes(this.bgImage, x, Shooter.game.engine.gl.panel.height - this.bgImage.height / 2 + y );
            x -= this.bgImage.width;
            Shooter.game.engine.gl.view.drawOrthoBitmapBytes(this.bgImage, x, Shooter.game.engine.gl.panel.height - this.bgImage.height / 2 + y );
        }
    }
