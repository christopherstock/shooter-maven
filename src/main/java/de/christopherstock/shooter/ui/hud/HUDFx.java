
    package de.christopherstock.shooter.ui.hud;

    import  java.awt.image.*;
    import  javax.imageio.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLTextureImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;

    /*******************************************************************************************************************
    *   The Heads Up Display.
    *******************************************************************************************************************/
    public class HUDFx
    {
        private                             LibGLTextureImage   damagePane                  = null;
        private                             LibGLTextureImage   healthPane                  = null;
        private                             LibGLTextureImage   deadPane                    = null;
        private                             LibGLTextureImage   adrenalinePane              = null;
        private                             BufferedImage       reincarnationTube           = null;
        private                             LibGLTextureImage   gli                         = null;

        private                             int                 animationHUDHealthFX        = 0;
        private                             int                 animationHUDDamageFX        = 0;
        private                             int                 animationHUDDyingFX         = 0;
        private                             int                 animationHUDDeadFX          = 0;
        private                             int                 animationHUDReincarnationFX = 0;
        private                             int                 animationHUDDamageFXticks   = 0;

        private                             float               opacityHealthFx             = 0.0f;
        private                             float               opacityDamageFx             = 0.0f;

        private                             boolean             drawDyingFx                 = false;
        private                             boolean             drawDeadFx                  = false;
        private                             boolean             drawReincarnationFx         = false;
        public                              boolean             drawAdrenalineFx            = false;

        public void init()
        {
            //init panes
            this.damagePane      = LibGLTextureImage.getFullOpaque( LibColors.ERedTranslucent.colABGR,    Shooter.game.engine.glView.width, Shooter.game.engine.glView.height, ShooterDebug.glImage );
            this.healthPane      = LibGLTextureImage.getFullOpaque( LibColors.EWhite.colABGR,             Shooter.game.engine.glView.width, Shooter.game.engine.glView.height, ShooterDebug.glImage );
            this.deadPane        = LibGLTextureImage.getFullOpaque( LibColors.EBlack.colABGR,             Shooter.game.engine.glView.width, Shooter.game.engine.glView.height, ShooterDebug.glImage );
            this.adrenalinePane  = LibGLTextureImage.getFullOpaque( LibColors.EYellowTranslucent.colABGR, Shooter.game.engine.glView.width, Shooter.game.engine.glView.height, ShooterDebug.glImage );

            try
            {
                this.reincarnationTube = ImageIO.read( LibIO.preStreamJarResource( ShooterSetting.Path.EScreen.url + "reincarnation.png" ) );
                this.gli = new LibGLTextureImage( this.reincarnationTube, ImageUsage.EOrtho, ShooterDebug.glImage, true );
            }
            catch ( Exception e )
            {
                ShooterDebug.error.trace( e );
                System.exit( 0 );
            }
        }

        public void disableAllFx()
        {
            this.animationHUDHealthFX        = 0;
            this.animationHUDDamageFX        = 0;
            this.animationHUDDyingFX         = 0;
            this.animationHUDDeadFX          = 0;
            this.animationHUDReincarnationFX = 0;
            this.animationHUDDamageFXticks   = 0;

            this.opacityHealthFx             = 0.0f;
            this.opacityDamageFx             = 0.0f;

            this.drawDyingFx                 = false;
            this.drawDeadFx                  = false;
            this.drawReincarnationFx         = false;
            this.drawAdrenalineFx            = false;
        }
        
        public void drawHUDEffects()
        {
            //draw healing effect?
            if ( this.animationHUDHealthFX > 0 )
            {
                float alpha = this.opacityHealthFx * this.animationHUDHealthFX / ShooterSetting.Performance.TICKS_HEALTH_FX;
                Shooter.game.engine.glView.drawOrthoBitmapBytes( this.healthPane, 0, 0, alpha );
            }

            //draw damage effect?
            if ( this.animationHUDDamageFX > 0 )
            {
                float alpha = this.opacityDamageFx * this.animationHUDDamageFX / this.animationHUDDamageFXticks;
                Shooter.game.engine.glView.drawOrthoBitmapBytes( this.damagePane, 0, 0, alpha );
            }

            //draw dying effect
            if ( this.drawDyingFx )
            {
                float alpha = (float)this.animationHUDDyingFX / (float) ShooterSetting.Performance.TICKS_DYING_FX;
                Shooter.game.engine.glView.drawOrthoBitmapBytes( this.damagePane, 0, 0, alpha );
            }

            //draw dead effect
            if ( this.drawDeadFx )
            {
                float alpha = ( (float)this.animationHUDDeadFX / (float) ShooterSetting.Performance.TICKS_DEAD_FX );
                Shooter.game.engine.glView.drawOrthoBitmapBytes( this.deadPane, 0, 0, alpha );
            }

            // draw adrenaline fx
            if ( this.drawAdrenalineFx )
            {
                float alpha = 0.05f;
                Shooter.game.engine.glView.drawOrthoBitmapBytes( this.adrenalinePane, 0, 0, alpha );
            }

            //draw reincarnation effect
            if ( this.drawReincarnationFx )
            {
                float size = ( 5 * (float)this.animationHUDReincarnationFX ) / ShooterSetting.Performance.TICKS_DEAD_FX;
                Shooter.game.engine.glView.drawOrthoBitmapBytes( this.gli, (int)( ( Shooter.game.engine.glView.width - this.gli.width * size ) / 2 ), (int)( ( Shooter.game.engine.glView.height - this.gli.height * size ) / 2 ), 1.0f, size, size, false );
            }
        }

        public void launchDamageFX(int descent )
        {
            //set new opacity value and clip it
            this.opacityDamageFx = ShooterSetting.HUDSettings.MAX_OPACITY_DAMAGE_FX * descent / 15;
            if ( this.opacityDamageFx > ShooterSetting.HUDSettings.MAX_OPACITY_DAMAGE_FX ) this.opacityDamageFx = ShooterSetting.HUDSettings.MAX_OPACITY_DAMAGE_FX;

            //start damage animation
            this.animationHUDDamageFXticks   = ShooterSetting.Performance.MAX_TICKS_DAMAGE_FX * descent / 15;
            this.animationHUDDamageFX        = this.animationHUDDamageFXticks;
        }

        public void launchHealthFX(int gain )
        {
            //set new opacity value and clip it
            this.opacityHealthFx = ShooterSetting.HUDSettings.MAX_OPACITY_HEALTH_FX * gain / 15;
            if ( this.opacityHealthFx > ShooterSetting.HUDSettings.MAX_OPACITY_HEALTH_FX ) this.opacityHealthFx = ShooterSetting.HUDSettings.MAX_OPACITY_HEALTH_FX;

            //start health animation
            this.animationHUDHealthFX = ShooterSetting.Performance.TICKS_HEALTH_FX;
        }

        public void launchDyingFX()
        {
            if ( !this.drawDyingFx )
            {
                //start health animation
                this.drawDyingFx         = true;
                this.animationHUDDyingFX = 0;
            }
        }

        public void launchDeadFX()
        {
            if ( !this.drawDeadFx )
            {
                //start health animation
                this.drawDeadFx          = true;
                this.animationHUDDeadFX  = 0;
            }
        }

        public void launchReincarnationFX()
        {
            if ( !this.drawReincarnationFx )
            {
                //start health animation
                this.drawReincarnationFx          = true;
                this.animationHUDReincarnationFX  = 0;
            }
        }

        public void animateEffects()
        {
            if ( this.animationHUDHealthFX       > 0     ) --this.animationHUDHealthFX;
            if ( this.animationHUDDamageFX       > 0     ) --this.animationHUDDamageFX;

            if ( this.drawDeadFx )
            {
                if ( this.animationHUDDeadFX  < ShooterSetting.Performance.TICKS_DEAD_FX ) ++this.animationHUDDeadFX;
            }
            if ( this.drawDyingFx )
            {
                if ( this.animationHUDDyingFX < ShooterSetting.Performance.TICKS_DYING_FX ) ++this.animationHUDDyingFX;
            }
            if ( this.drawReincarnationFx )
            {
                if ( this.animationHUDReincarnationFX < ShooterSetting.Performance.TICKS_REINCARNATION_FX ) ++this.animationHUDReincarnationFX;
            }
        }
    }
