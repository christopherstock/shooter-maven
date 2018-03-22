
    package de.christopherstock.shooter.ui.hud;

    import  java.awt.*;
    import  java.awt.image.*;
    import  java.util.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.AvatarMessages;
    import  de.christopherstock.shooter.ShooterSettings.Fonts;
    import  de.christopherstock.shooter.ShooterSettings.OffsetsOrtho;
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

            public          BufferedImage           img         = null;

            public static void loadImages()
            {
                for ( AvatarImage avatarImage : values() )
                {
                    avatarImage.loadImage();
                }
            }

            private void loadImage()
            {
                this.img = LibImage.load( ShooterSettings.Path.EAvatar.iUrl + this.toString() + LibExtension.png.getSpecifier(), ShooterDebug.glImage, true );
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

        public      static          int                     currentDebugMsg         = 0;

        private     static          int                     anim                    = 0;
        private     static          AnimState               animState               = AnimState.EDisabled;

        private                     LibGLImage              bgBar                   = null;
        private                     Font                    iFont                   = null;
        private                     LibGLImage              iImgAvatar              = null;
        private                     LibGLImage[]            textLines               = null;
        private                     int                     blockHeight             = 0;
        private                     int                     iDrawX                  = 0;
        private                     int                     iDrawY                  = 0;
        private                     String                  iText                   = null;

        private AvatarMessage( AvatarImage aImage, String aText, Font aFont, Color bgColor )
        {
            this.iText = aText;
            this.iFont = aFont;
            this.iImgAvatar = new LibGLImage( aImage.img, ImageUsage.EOrtho, ShooterDebug.glImage, false );

            //calculate text
            String[] textLinesS = LibStrings.breakLinesOptimized( LibGL3D.panel.getGraphics(), this.iText, this.iFont, LibGL3D.panel.width - 3 * OffsetsOrtho.EAvatarMsgX - this.iImgAvatar.width - OffsetsOrtho.EBorderHudX );
            this.textLines = new LibGLImage[ textLinesS.length ];
            for (int i = 0; i < this.textLines.length; ++i )
            {
                this.textLines[ i ] = LibGLImage.getFromString( textLinesS[ i ], this.iFont, ShooterSettings.Colors.EAvatarMessageText.colABGR, null, ShooterSettings.Colors.EAvatarMessageTextOutline.colABGR, ShooterDebug.glImage );
            }
            this.blockHeight = (this.textLines.length * this.textLines[ 0 ].height ); //+ ( ( textLines.length - 1 ) * ShooterSettings.HUD.LINE_SPACING_RATIO_EMPTY_LINES ) );
            this.iDrawX = 3 * OffsetsOrtho.EAvatarMsgX + this.iImgAvatar.width;
            this.iDrawY = LibGL3D.panel.height - OffsetsOrtho.EAvatarMsgY - this.textLines[ 0 ].height - OffsetsOrtho.EAvatarBgPanelHeight / 2 + this.blockHeight / 2;

            //create bar if not done
            this.bgBar = LibGLImage.getFullOpaque( bgColor, LibGL3D.panel.width - this.iImgAvatar.width - 3 * OffsetsOrtho.EAvatarMsgX, this.iImgAvatar.height, ShooterDebug.glImage );
        }

        public static void showMessage(AvatarImage img, String text, Color bgColor )
        {
            //check if this message is already on the queue!
            for ( AvatarMessage m : messageQueue )
            {
                if ( m.iText.equals( text ) )
                {
                    return;
                }
            }

            //add a message to the queue
            messageQueue.add( new AvatarMessage( img, text, Fonts.EAvatarMessage, bgColor ) );
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
            //get panel's current alpha
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
            LibGL3D.view.drawOrthoBitmapBytes(this.bgBar, 2 * OffsetsOrtho.EAvatarMsgX + this.iImgAvatar.width, LibGL3D.panel.height - OffsetsOrtho.EAvatarMsgY - this.bgBar.height, alphaBgBar );

            //draw avatar image
            LibGL3D.view.drawOrthoBitmapBytes(this.iImgAvatar, OffsetsOrtho.EAvatarMsgX, LibGL3D.panel.height - OffsetsOrtho.EAvatarMsgY - this.bgBar.height, alphaAvatarImg );

            //draw text
            int y = this.iDrawY;
            for ( LibGLImage textLine : this.textLines)
            {
                LibGL3D.view.drawOrthoBitmapBytes(textLine, this.iDrawX, y, alphaAvatarImg);
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
                    AvatarMessage.showMessage( AvatarImage.EWoman, ShooterStrings.AvatarMessages.TUTORIAL_CYCLE_WEARPONS, ShooterSettings.Colors.EAvatarMessagePanelBgBlack.colABGR );
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
                    AvatarMessage.showMessage( AvatarImage.EWoman3, ShooterStrings.AvatarMessages.TUTORIAL_CROUCH, ShooterSettings.Colors.EAvatarMessagePanelBgGrey.colABGR );
                    break;
                }

                case 4:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman4, ShooterStrings.AvatarMessages.TUTORIAL_RELOAD, ShooterSettings.Colors.EAvatarMessagePanelBgRed.colABGR );
                    break;
                }

                case 5:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman5, ShooterStrings.AvatarMessages.TUTORIAL_FIRE, ShooterSettings.Colors.EAvatarMessagePanelBgYellow.colABGR );
                    break;
                }

                case 6:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman6, ShooterStrings.AvatarMessages.TUTORIAL_VIEW_UP_DOWN, ShooterSettings.Colors.EAvatarMessagePanelBgRed.colABGR );
                    break;
                }
            }

            //next debug message
            if ( ++AvatarMessage.currentDebugMsg >= 7 ) AvatarMessage.currentDebugMsg = 0;
        }
    }
