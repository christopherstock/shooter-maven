
    package de.christopherstock.lib.ui;

    import  de.christopherstock.lib.LibDebug;
    import  javax.swing.*;

    /*******************************************************************************************************************
    *   Offers independent UI functionality.
    *******************************************************************************************************************/
    public class LibUI
    {
        /***************************************************************************************************************
        *   Sets the look-and-feel of the host operating system for all UI components.
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
    }
