
    package de.christopherstock.lib.ui;

    import  java.util.*;
    import  java.text.*;

    /*******************************************************************************************************************
    *   Formats strings and date objects.
    *******************************************************************************************************************/
    public class LibStringFormat
    {
        private         static      NumberFormat        formatNumber                    = NumberFormat.getNumberInstance();
        private         static      NumberFormat        formatPercent                   = NumberFormat.getPercentInstance(                                          Locale.GERMANY );
        private         static      DateFormat          formatDateTime                  = DateFormat.getDateTimeInstance(   DateFormat.MEDIUM, DateFormat.MEDIUM,   Locale.GERMANY );
        private         static      DateFormat          formatDate                      = DateFormat.getDateInstance(       DateFormat.MEDIUM,                      Locale.GERMANY );

        /***************************************************************************************************************
        *   Formats the given number to a readable string. e.g. 9483455.12 to 9.483.455,12
        ***************************************************************************************************************/
        public static String formatNumber( long value )
        {
            return formatNumber.format( value );
        }

        public static String formatPercent( float value )
        {
            return formatPercent.format( value );
        }

        public static String formatiereDateTime( long millis )
        {
            return formatDateTime( new Date( millis ) );
        }

        /***************************************************************************************************************
        *   Formats the specified date to a date- and time-string.
        *
        *   @param  date    A date.
        *   @return         The date and time as a string ( e.g. "19.07.2010 20:11:18" )
        ***************************************************************************************************************/
        private static String formatDateTime( Date date)
        {
            return formatDateTime.format( date );
        }

        /***************************************************************************************************************
        *   Formats the current date to a date- and time-string.
        *
        *   @return         The current date and time as a string ( e.g. "19.07.2010 20:11:18" )
        ***************************************************************************************************************/
        public static String formatDateTime()
        {
            Date   date   = new Date();
            String millis = String.valueOf( System.currentTimeMillis() );

            return formatDateTime.format( date ) + "," + millis.substring( millis.length() - 3 );
        }

        /***************************************************************************************************************
        *   Formats a timestamp to a data-string.
        *
        *   @param  millis  The number of millis passed since the epoche. ( 01.01.1970 00:00:00 )
        *   @return         Ein Datumswert als String. ( z.B. "28.03.2005" )
        ***************************************************************************************************************/
        public static String formatDate( long millis )
        {
            return formatDate( new Date( millis ) );
        }

        /***************************************************************************************************************
        *   Formats a date to a date-string.
        *
        *   @param  date    A date.
        *   @return         The date as string. ( e.g. "17.08.2010" )
        ***************************************************************************************************************/
        private static String formatDate( Date date)
        {
            return formatDate.format( date );
        }
    }
