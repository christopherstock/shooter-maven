
    package de.christopherstock.lib.ui;

    import  de.christopherstock.lib.LibDebug;
    import  de.christopherstock.shooter.ShooterDebug;
    import  de.christopherstock.shooter.ShooterSetting;
    import  javax.swing.*;
    import  java.awt.*;

    /*******************************************************************************************************************
    *   Offers independent UI functionality.
    *******************************************************************************************************************/
    public class LibUI
    {
        /***************************************************************************************************************
        *   Sets the look and feel of the host operating system for all UI components.
        *
        *   @param debug The debug instance.
        ***************************************************************************************************************/
        public static void setLookAndFeel( LibDebug debug )
        {
            try
            {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
            catch( Exception e )
            {
                debug.err( "Setting host-operating system lookAndFeel failed!\n" + e.toString() );
            }
        }

        public static void determineFullScreenSize()
        {
            if ( ShooterDebug.ENABLE_FULLSCREEN && !ShooterDebug.DEBUG_MODE )
            {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                ShooterSetting.Form.FORM_WIDTH  = screenSize.width;
                ShooterSetting.Form.FORM_HEIGHT = screenSize.height;
            }
        }
    }
