
    package de.christopherstock.shooter.ui.hud;

    import  java.util.*;

    import  de.christopherstock.shooter.ShooterSettings.OffsetsOrtho;
    import  de.christopherstock.shooter.level.*;

    /**************************************************************************************
    *   Manages the hud-messaging-queue
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public final class HUDMessageManager
    {
        protected static enum AnimState
        {
            EPopUp,
            EStill,
            EPopDown,
            ;
        }

        private         static          HUDMessageManager       singleton               = null;

        private                         Vector<HUDMessage>      messageQueue            = new Vector<HUDMessage>();

        public static final HUDMessageManager getSingleton()
        {
            if ( singleton == null ) singleton = new HUDMessageManager();
            return singleton;
        }

        public final void showMessage( String txt )
        {
            HUDMessage hudMessage = new HUDMessage( txt );
            hudMessage.show();
            messageQueue.add( hudMessage );
        }

        public final void animateAll()
        {
            //browse reversed for easy pruning
            for ( int j = messageQueue.size() - 1; j >= 0; --j )
            {
                //animate - remove if returned
                if ( messageQueue.elementAt( j ).animate() )
                {
                    messageQueue.removeElementAt( j );
                }
            }
        }

        public final void drawAll()
        {
            //only if messages are available
            if ( messageQueue.size() > 0 )
            {
                int drawY = OffsetsOrtho.EBorderHudY + ( Level.currentPlayer().iArtefactSet.showAmmoInHUD() ? 2 * messageQueue.elementAt( 0 ).getTexImgHeight() : 0 );

                for ( int i = messageQueue.size() - 1; i >= 0; --i )
                {
                    messageQueue.elementAt( i ).draw( drawY );
                    drawY += messageQueue.elementAt( i ).getTexImgHeight();
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
