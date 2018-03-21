
    package de.christopherstock.shooter.ui.hud;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.Fonts;
    import  de.christopherstock.shooter.ShooterSettings.HUDSettings;
    import  de.christopherstock.shooter.ShooterSettings.OffsetsOrtho;
    import  de.christopherstock.shooter.ui.hud.HUDMessageManager.AnimState;

    /*******************************************************************************************************************
    *   The HUD messages system. HUD messages pop up to give the user a short notice
    *   about an event. E.g. picking up items.
    *******************************************************************************************************************/
    final class HUDMessage
    {
        private                     int                     iAnim                   = 0;
        private                     AnimState               iAnimState              = null;
        private                     LibGLImage              iTextImg                = null;
        private                     String                  iText                   = null;

        protected HUDMessage( String aText )
        {
            iAnimState  = AnimState.EPopUp;
            iAnim       = HUDSettings.MSG_TICKS_POP_UP - 1;
            iText       = aText;
        }

        public final void show()
        {
            //init img and add this message to the queue
            iTextImg       = LibGLImage.getFromString
            (
                iText,
                Fonts.EAvatarMessage,
                ShooterSettings.Colors.EHudMsgFg.colARGB,
                null,
                ShooterSettings.Colors.EHudMsgOutline.colARGB,
                ShooterDebug.glImage
            );
        }

        /***************************************************************************************************************
        *   Animate this message.
        *
        *   @return     <code>true</code> if the animation for this msg is over and it can be removed from the queue.
        *               Otherwise <code>false</code>.
        ***************************************************************************************************************/
        protected boolean animate()
        {
            //next animation-tick!
            if ( iAnim > -1 ) --iAnim;

            if ( iAnim == -1 )
            {
                //check next state
                switch ( iAnimState )
                {
                    case EPopUp:
                    {
                        iAnim        = HUDSettings.MSG_TICKS_STILL - 1;
                        iAnimState   = AnimState.EStill;
                        return false;
                    }

                    case EStill:
                    {
                        iAnim        = HUDSettings.MSG_TICKS_POP_DOWN - 1;
                        iAnimState   = AnimState.EPopDown;
                        return false;
                    }

                    case EPopDown:
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        protected final void draw( int drawY )
        {
            //only draw if an avatar-animation is active
            if ( iAnim > -1 )
            {
                //get panel's current alpha
                float     alphaFg = 0;
                switch ( iAnimState )
                {
                    case EPopUp:
                    {
                        alphaFg     = HUDSettings.MSG_OPACITY - HUDSettings.MSG_OPACITY * iAnim / HUDSettings.MSG_TICKS_POP_UP;
                        break;
                    }
                    case EStill:
                    {
                        alphaFg     = HUDSettings.MSG_OPACITY;
                        break;
                    }
                    case EPopDown:
                    {
                        alphaFg     = HUDSettings.MSG_OPACITY * iAnim / HUDSettings.MSG_TICKS_POP_DOWN;
                        break;
                    }
                }

                //draw text
                int x = LibGL3D.panel.width  - OffsetsOrtho.EBorderHudX - iTextImg.width;
                LibGL3D.view.drawOrthoBitmapBytes( iTextImg, x, drawY, alphaFg );
            }
        }

        protected int getTexImgHeight()
        {
            return iTextImg.height;
        }
    }
