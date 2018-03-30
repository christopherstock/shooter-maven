
    package de.christopherstock.shooter.ui.hud;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.HUDSettings;
    import  de.christopherstock.shooter.ShooterSetting.OffsetsOrtho;
    import  de.christopherstock.shooter.ui.hud.HUDMessageManager.AnimState;

    /*******************************************************************************************************************
    *   The HUD messages system. HUD messages pop up to give the user a short notice
    *   about an event. E.g. picking up items.
    *******************************************************************************************************************/
    final class HUDMessage
    {
        private                     int                     anim                = 0;
        private                     AnimState               animState           = null;
        private                     LibGLTextureImage       textImg             = null;
        private                     String                  text                = null;

        protected HUDMessage( String text )
        {
            this.animState = AnimState.EPopUp;
            this.anim = HUDSettings.MSG_TICKS_POP_UP - 1;
            this.text = text;
        }

        public final void show()
        {
            //init img and add this message to the queue
            this.textImg = LibGLTextureImage.getFromString
            (
                this.text,
                Shooter.game.engine.fonts.avatarMessage,
                ShooterSetting.Colors.EHudMsgFg.colARGB,
                null,
                ShooterSetting.Colors.EHudMsgOutline.colARGB,
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
            if (this.anim > -1 ) --this.anim;

            if (this.anim == -1 )
            {
                //check next state
                switch (this.animState)
                {
                    case EPopUp:
                    {
                        this.anim = HUDSettings.MSG_TICKS_STILL - 1;
                        this.animState = AnimState.EStill;
                        return false;
                    }

                    case EStill:
                    {
                        this.anim = HUDSettings.MSG_TICKS_POP_DOWN - 1;
                        this.animState = AnimState.EPopDown;
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
            if (this.anim > -1 )
            {
                //get frame's current alpha
                float     alphaFg = 0;
                switch (this.animState)
                {
                    case EPopUp:
                    {
                        alphaFg     = HUDSettings.MSG_OPACITY - HUDSettings.MSG_OPACITY * this.anim / HUDSettings.MSG_TICKS_POP_UP;
                        break;
                    }
                    case EStill:
                    {
                        alphaFg     = HUDSettings.MSG_OPACITY;
                        break;
                    }
                    case EPopDown:
                    {
                        alphaFg     = HUDSettings.MSG_OPACITY * this.anim / HUDSettings.MSG_TICKS_POP_DOWN;
                        break;
                    }
                }

                //draw text
                int x = Shooter.game.engine.glView.width  - OffsetsOrtho.EBorderHudX - this.textImg.width;
                Shooter.game.engine.glView.drawOrthoBitmapBytes(this.textImg, x, drawY, alphaFg );
            }
        }

        protected int getTexImgHeight()
        {
            return this.textImg.height;
        }
    }
