
    package de.christopherstock.shooter.ui.hud;

    import  java.awt.image.*;
    import  javax.imageio.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;

    /*******************************************************************************************************************
    *   The Heads Up Display.
    *******************************************************************************************************************/
    public class HUDFx
    {
        private         static          LibGLImage      damagePane                  = null;
        private         static          LibGLImage      healthPane                  = null;
        private         static          LibGLImage      deadPane                    = null;
        private         static          LibGLImage      adrenalinePane              = null;
        private         static          BufferedImage   reincarnationTube           = null;
        private         static          LibGLImage      gli                         = null;

        private         static          int             animationHUDHealthFX        = 0;
        private         static          int             animationHUDDamageFX        = 0;
        private         static          int             animationHUDDyingFX         = 0;
        public          static          int             animationHUDDeadFX          = 0;
        public          static          int             animationHUDReincarnationFX = 0;
        public          static          int             animationHUDDamageFXticks   = 0;

        private         static          float           opacityHealthFx             = 0.0f;
        private         static          float           opacityDamageFx             = 0.0f;

        public          static          boolean         drawDyingFx                 = false;
        public          static          boolean         drawDeadFx                  = false;
        public          static          boolean         drawReincarnationFx         = false;
        public          static          boolean         drawAdrenalineFx            = false;

        public static void init()
        {
            //init panes
            damagePane      = LibGLImage.getFullOpaque( LibColors.ERed.colABGR,    Shooter.game.engine.gl.panel.width, Shooter.game.engine.gl.panel.height, ShooterDebug.glImage );
            healthPane      = LibGLImage.getFullOpaque( LibColors.EWhite.colABGR,  Shooter.game.engine.gl.panel.width, Shooter.game.engine.gl.panel.height, ShooterDebug.glImage );
            damagePane      = LibGLImage.getFullOpaque( LibColors.ERed.colABGR,    Shooter.game.engine.gl.panel.width, Shooter.game.engine.gl.panel.height, ShooterDebug.glImage );
            deadPane        = LibGLImage.getFullOpaque( LibColors.EBlack.colABGR,  Shooter.game.engine.gl.panel.width, Shooter.game.engine.gl.panel.height, ShooterDebug.glImage );
            adrenalinePane  = LibGLImage.getFullOpaque( LibColors.EYellow.colABGR, Shooter.game.engine.gl.panel.width, Shooter.game.engine.gl.panel.height, ShooterDebug.glImage );

            try
            {
                reincarnationTube = ImageIO.read( LibIO.preStreamJarResource( ShooterSetting.Path.EScreen.iUrl + "reincarnation.png" ) );
                gli = new LibGLImage( reincarnationTube, ImageUsage.EOrtho, ShooterDebug.glImage, true );
            }
            catch ( Exception e )
            {
                ShooterDebug.error.trace( e );
                System.exit( 0 );
            }
        }

        public static void disableAllFx()
        {
            animationHUDHealthFX        = 0;
            animationHUDDamageFX        = 0;
            animationHUDDyingFX         = 0;
            animationHUDDeadFX          = 0;
            animationHUDReincarnationFX = 0;
            animationHUDDamageFXticks   = 0;

            opacityHealthFx             = 0.0f;
            opacityDamageFx             = 0.0f;

            drawDyingFx                 = false;
            drawDeadFx                  = false;
            drawReincarnationFx         = false;
            drawAdrenalineFx            = false;
        }
        
        public static void drawHUDEffects()
        {
            //draw healing effect?
            if ( animationHUDHealthFX > 0 )
            {
                float alpha = opacityHealthFx * animationHUDHealthFX / ShooterSetting.Performance.TICKS_HEALTH_FX;
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( healthPane, 0, 0, alpha );
            }

            //draw damage effect?
            if ( animationHUDDamageFX > 0 )
            {
                float alpha = opacityDamageFx * animationHUDDamageFX / animationHUDDamageFXticks;
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( damagePane, 0, 0, alpha );
            }

            //draw dying effect
            if ( drawDyingFx )
            {
                float alpha = (float)animationHUDDyingFX / (float) ShooterSetting.Performance.TICKS_DYING_FX;
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( damagePane, 0, 0, alpha );
            }

            //draw dead effect
            if ( drawDeadFx )
            {
                float alpha = (float)animationHUDDeadFX / (float) ShooterSetting.Performance.TICKS_DEAD_FX;
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( deadPane, 0, 0, alpha );
            }

            if ( drawAdrenalineFx )
            {
                float alpha = 0.05f;
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( adrenalinePane, 0, 0, alpha );
            }

            //draw reincarnation effect
            if ( drawReincarnationFx )
            {
                float size = ( 5 * (float)animationHUDReincarnationFX ) / ShooterSetting.Performance.TICKS_DEAD_FX;
                Shooter.game.engine.gl.view.drawOrthoBitmapBytes( gli, (int)( ( Shooter.game.engine.gl.panel.width - gli.width * size ) / 2 ), (int)( ( Shooter.game.engine.gl.panel.height - gli.height * size ) / 2 ), 1.0f, size, size, false );
            }
        }

        public static void launchDamageFX(int descent )
        {
            //set new opacity value and clip it
            opacityDamageFx = ShooterSetting.HUDSettings.MAX_OPACITY_DAMAGE_FX * descent / 15;
            if ( opacityDamageFx > ShooterSetting.HUDSettings.MAX_OPACITY_DAMAGE_FX ) opacityDamageFx = ShooterSetting.HUDSettings.MAX_OPACITY_DAMAGE_FX;

            //start damage animation
            animationHUDDamageFXticks   = ShooterSetting.Performance.MAX_TICKS_DAMAGE_FX * descent / 15;
            animationHUDDamageFX        = animationHUDDamageFXticks;
        }

        public static void launchHealthFX(int gain )
        {
            //set new opacity value and clip it
            opacityHealthFx = ShooterSetting.HUDSettings.MAX_OPACITY_HEALTH_FX * gain / 15;
            if ( opacityHealthFx > ShooterSetting.HUDSettings.MAX_OPACITY_HEALTH_FX ) opacityHealthFx = ShooterSetting.HUDSettings.MAX_OPACITY_HEALTH_FX;

            //start health animation
            animationHUDHealthFX = ShooterSetting.Performance.TICKS_HEALTH_FX;
        }

        public static void launchDyingFX()
        {
            if ( !drawDyingFx )
            {
                //start health animation
                drawDyingFx         = true;
                animationHUDDyingFX = 0;
            }
        }

        public static void launchDeadFX()
        {
            if ( !drawDeadFx )
            {
                //start health animation
                drawDeadFx          = true;
                animationHUDDeadFX  = 0;
            }
        }

        public static void launchReincarnationFX()
        {
            if ( !drawReincarnationFx )
            {
                //start health animation
                drawReincarnationFx          = true;
                animationHUDReincarnationFX  = 0;
            }
        }

        public static void animateEffects()
        {
            if ( animationHUDHealthFX       > 0     ) --animationHUDHealthFX;
            if ( animationHUDDamageFX       > 0     ) --animationHUDDamageFX;

            if ( drawDeadFx )
            {
                if ( animationHUDDeadFX  < ShooterSetting.Performance.TICKS_DEAD_FX ) ++animationHUDDeadFX;
            }
            if ( drawDyingFx )
            {
                if ( animationHUDDyingFX < ShooterSetting.Performance.TICKS_DYING_FX ) ++animationHUDDyingFX;
            }
            if ( drawReincarnationFx )
            {
                if ( animationHUDReincarnationFX < ShooterSetting.Performance.TICKS_REINCARNATION_FX ) ++animationHUDReincarnationFX;
            }
        }
    }
