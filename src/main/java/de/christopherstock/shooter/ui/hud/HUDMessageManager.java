
    package de.christopherstock.shooter.ui.hud;

    import  java.util.*;
    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterSetting.OffsetsOrtho;

    /*******************************************************************************************************************
    *   Manages the hud-messaging-queue
    *******************************************************************************************************************/
    public final class HUDMessageManager
    {
        protected static enum AnimState
        {
            EPopUp,
            EStill,
            EPopDown,
            ;
        }

        private                         Vector<HUDMessage>      messageQueue            = new Vector<HUDMessage>();

        public final void showMessage( String txt )
        {
            HUDMessage hudMessage = new HUDMessage( txt );
            hudMessage.show();
            this.messageQueue.add( hudMessage );
        }

        public final void animateAll()
        {
            //browse reversed for easy pruning
            for (int j = this.messageQueue.size() - 1; j >= 0; --j )
            {
                //animate - remove if returned
                if (this.messageQueue.elementAt( j ).animate() )
                {
                    this.messageQueue.removeElementAt( j );
                }
            }
        }

        public final void drawAll()
        {
            //only if messages are available
            if (this.messageQueue.size() > 0 )
            {
                int drawY = OffsetsOrtho.EBorderHudY + ( Shooter.game.engine.player.artefactSet.showAmmoInHUD() ? 2 * this.messageQueue.elementAt( 0 ).getTexImgHeight() : 0 );

                for (int i = this.messageQueue.size() - 1; i >= 0; --i )
                {
                    this.messageQueue.elementAt( i ).draw( drawY );
                    drawY += this.messageQueue.elementAt( i ).getTexImgHeight();
                }
            }
        }
/*
        public final boolean messageQueueEmpty()
        {
            return messageQueue.isEmpty();

        }
*/
    }
