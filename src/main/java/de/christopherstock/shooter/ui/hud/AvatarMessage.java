
    package de.christopherstock.shooter.ui.hud;

    import  java.awt.*;
    import  java.awt.image.*;
    import  java.util.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLTextureImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.AvatarMessages;
    import  de.christopherstock.shooter.ShooterSetting.OffsetsOrtho;
    import  de.christopherstock.shooter.base.*;

    /*******************************************************************************************************************
    *   Represents a pop-up-message with an avatar image and a message.
    *******************************************************************************************************************/
    public final class AvatarMessage
    {
        public static enum AvatarImage
        {
            EMan,
            EWoman,
            EWoman2,
            EWoman3,
            EWoman4,
            EWoman5,
            EWoman6,
            EWoman7,
            ;

            protected BufferedImage           img         = null;

            public static void loadImages()
            {
                for ( AvatarImage avatarImage : values() )
                {
                    avatarImage.loadImage();
                }
            }

            private void loadImage()
            {
                this.img = LibImage.load( ShooterSetting.Path.EAvatar.url + this.toString() + LibExtension.png.getSpecifier(), ShooterDebug.glImage, true );
            }
        }

        private static enum AnimState
        {
            EDisabled,
            EPopUp,
            EPresent,
            EPopDown,
            ;
        }

        private     static          Vector<AvatarMessage>   messageQueue            = new Vector<AvatarMessage>();

        private     static          int                     currentDebugMsg         = 0;

        private     static          int                     anim                    = 0;
        private     static          AnimState               animState               = AnimState.EDisabled;

        private                     LibGLTextureImage       bgBar                   = null;
        private                     Font                    font                    = null;
        private                     LibGLTextureImage       imgAvatar               = null;
        private                     LibGLTextureImage[]     textLines               = null;
        private                     int                     blockHeight             = 0;
        private                     int                     drawX                   = 0;
        private                     int                     drawY                   = 0;
        private                     String                  text                    = null;

        private AvatarMessage( AvatarImage image, String text, Font font, Color bgColor )
        {
            this.text = text;
            this.font = font;
            this.imgAvatar = new LibGLTextureImage( image.img, ImageUsage.EOrtho, ShooterDebug.glImage, false );

            //calculate text
            String[] textLinesS = LibStrings.breakLinesOptimized( Shooter.game.engine.frame.getGraphics(), this.text, this.font, Shooter.game.engine.glView.width - 3 * OffsetsOrtho.EAvatarMsgX - this.imgAvatar.width - OffsetsOrtho.EBorderHudX );
            this.textLines = new LibGLTextureImage[ textLinesS.length ];
            for (int i = 0; i < this.textLines.length; ++i )
            {
                this.textLines[ i ] = LibGLTextureImage.getFromString( textLinesS[ i ], this.font, ShooterSetting.Colors.EAvatarMessageText.colABGR, null, ShooterSetting.Colors.EAvatarMessageTextOutline.colABGR, ShooterDebug.glImage );
            }
            this.blockHeight = (this.textLines.length * this.textLines[ 0 ].height ); //+ ( ( textLines.length - 1 ) * ShooterSetting.HUD.LINE_SPACING_RATIO_EMPTY_LINES ) );
            this.drawX = 3 * OffsetsOrtho.EAvatarMsgX + this.imgAvatar.width;
            this.drawY = Shooter.game.engine.glView.height - OffsetsOrtho.EAvatarMsgY - this.textLines[ 0 ].height - OffsetsOrtho.EAvatarBgPanelHeight / 2 + this.blockHeight / 2;

            //create bar if not done
            this.bgBar = LibGLTextureImage.getFullOpaque( bgColor, Shooter.game.engine.glView.width - this.imgAvatar.width - 3 * OffsetsOrtho.EAvatarMsgX, this.imgAvatar.height, ShooterDebug.glImage );
        }

        public static void showMessage( AvatarImage img, String text, Color bgColor )
        {
            //check if this message is already on the queue!
            for ( AvatarMessage m : messageQueue )
            {
                if ( m.text.equals( text ) )
                {
                    return;
                }
            }

            //add a message to the queue
            messageQueue.add( new AvatarMessage( img, text, Shooter.game.engine.fonts.avatarMessage, bgColor ) );
        }

        public static void animate()
        {
            //next animation-tick!
            if ( anim > -1 ) --anim;

            if ( anim == -1 )
            {
                //check next state
                switch ( animState )
                {
                    case EDisabled:
                    {
                        //start next message if available
                        if ( messageQueue.size() > 0 )
                        {
                            //start the msg-animation
                            anim        = AvatarMessages.ANIM_TICKS_POP_UP - 1;
                            animState   = AnimState.EPopUp;
                        }
                        break;
                    }

                    case EPopUp:
                    {
                        anim        = AvatarMessages.ANIM_TICKS_STILL - 1;
                        animState   = AnimState.EPresent;
                        break;
                    }

                    case EPresent:
                    {
                        anim        = AvatarMessages.ANIM_TICKS_POP_DOWN - 1;
                        animState   = AnimState.EPopDown;
                        break;
                    }

                    case EPopDown:
                    {
                        animState   = AnimState.EDisabled;

                        //pop 1st message
                        messageQueue.removeElementAt( 0 );

                        break;
                    }
                }
            }
        }

        public static void drawMessage()
        {
            //only draw if an avatar-animation is active
            if ( anim > -1 && messageQueue.size() > 0 )
            {
                //draw this msg
                messageQueue.elementAt( 0 ).draw();
            }
        }

        private void draw()
        {
            //get frame's current alpha
            float alphaBgBar      = 0.0f;
            float alphaAvatarImg  = 0.0f;
            switch ( animState )
            {
                case EDisabled:
                {
                    //won't be passed
                    break;
                }

                case EPopUp:
                {
                    alphaBgBar     = AvatarMessages.OPACITY_PANEL_BG   - AvatarMessages.OPACITY_PANEL_BG   * anim / AvatarMessages.ANIM_TICKS_POP_UP;
                    alphaAvatarImg = AvatarMessages.OPACITY_AVATAR_IMG - AvatarMessages.OPACITY_AVATAR_IMG * anim / AvatarMessages.ANIM_TICKS_POP_UP;
                    break;
                }
                case EPresent:
                {
                    alphaBgBar     = AvatarMessages.OPACITY_PANEL_BG;
                    alphaAvatarImg = AvatarMessages.OPACITY_AVATAR_IMG;
                    break;
                }
                case EPopDown:
                {
                    alphaBgBar     = AvatarMessages.OPACITY_PANEL_BG   * anim / AvatarMessages.ANIM_TICKS_POP_DOWN;
                    alphaAvatarImg = AvatarMessages.OPACITY_AVATAR_IMG * anim / AvatarMessages.ANIM_TICKS_POP_DOWN;
                    break;
                }
            }

            //draw bg bar
            Shooter.game.engine.glView.drawOrthoBitmapBytes(this.bgBar, 2 * OffsetsOrtho.EAvatarMsgX + this.imgAvatar.width, Shooter.game.engine.glView.height - OffsetsOrtho.EAvatarMsgY - this.bgBar.height, alphaBgBar );

            //draw avatar image
            Shooter.game.engine.glView.drawOrthoBitmapBytes(this.imgAvatar, OffsetsOrtho.EAvatarMsgX, Shooter.game.engine.glView.height - OffsetsOrtho.EAvatarMsgY - this.bgBar.height, alphaAvatarImg );

            //draw text
            int y = this.drawY;
            for ( LibGLTextureImage textLine : this.textLines)
            {
                Shooter.game.engine.glView.drawOrthoBitmapBytes(textLine, this.drawX, y, alphaAvatarImg);
                y -= textLine.height;
            }
        }

        public static boolean queueIsEmpty()
        {
            return messageQueue.isEmpty();
        }

        @Deprecated
        public static void showDebugMessage()
        {
            switch ( AvatarMessage.currentDebugMsg )
            //switch ( LibMath.getRandom( 0, 2 ) )
            {
                case 0:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman, ShooterStrings.AvatarMessages.TUTORIAL_CYCLE_WEARPONS, ShooterSetting.Colors.EAvatarMessagePanelBgBlack.colABGR );
                    break;
                }

                case 1:
                {
                    break;
                }

                case 2:
                {
                    break;
                }

                case 3:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman3, ShooterStrings.AvatarMessages.TUTORIAL_CROUCH, ShooterSetting.Colors.EAvatarMessagePanelBgGrey.colABGR );
                    break;
                }

                case 4:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman4, ShooterStrings.AvatarMessages.TUTORIAL_RELOAD, ShooterSetting.Colors.EAvatarMessagePanelBgRed.colABGR );
                    break;
                }

                case 5:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman5, ShooterStrings.AvatarMessages.TUTORIAL_FIRE, ShooterSetting.Colors.EAvatarMessagePanelBgYellow.colABGR );
                    break;
                }

                case 6:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman6, ShooterStrings.AvatarMessages.TUTORIAL_VIEW_UP_DOWN, ShooterSetting.Colors.EAvatarMessagePanelBgRed.colABGR );
                    break;
                }
            }

            //next debug message
            if ( ++AvatarMessage.currentDebugMsg >= 7 ) AvatarMessage.currentDebugMsg = 0;
        }
    }
