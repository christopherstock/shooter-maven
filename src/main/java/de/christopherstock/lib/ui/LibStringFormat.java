
    package de.christopherstock.lib.ui;

    import  java.util.*;
    import  java.text.*;

    /*******************************************************************************************************************
    *   Formats strings and date objects.
    *******************************************************************************************************************/
    public class LibStringFormat
    {
        private     static          LibStringFormat     singleton                       = null;

        private                     NumberFormat        formatNumber                    = NumberFormat.getNumberInstance();
        private                     NumberFormat        formatPercent                   = NumberFormat.getPercentInstance(                                          Locale.GERMANY );
        private                     DateFormat          formatDateTime                  = DateFormat.getDateTimeInstance(   DateFormat.MEDIUM, DateFormat.MEDIUM,   Locale.GERMANY );
        private                     DateFormat          formatDate                      = DateFormat.getDateInstance(       DateFormat.MEDIUM,                      Locale.GERMANY );

        public static LibStringFormat getSingleton()
        {
            if ( singleton == null ) singleton = new LibStringFormat();
            return singleton;
        }

        /***************************************************************************************************************
        *   Formats the given number to a readable string. e.g. 9483455.12 to 9.483.455,12
        ***************************************************************************************************************/
        public final String formatNumber( long value )
        {
            return this.formatNumber.format( value );
        }

        public final String formatPercent( float value )
        {
            return this.formatPercent.format( value );
        }

        public final String formatiereDateTime( long millis )
        {
            return this.formatDateTime( new Date( millis ) );
        }

        /***************************************************************************************************************
        *   Formats the specified date to a date- and time-string.
        *
        *   @param  date    A date.
        *   @return         The date and time as a string ( e.g. "19.07.2010 20:11:18" )
        ***************************************************************************************************************/
        private String formatDateTime( Date date)
        {
            return this.formatDateTime.format( date );
        }

        /***************************************************************************************************************
        *   Formats the current date to a date- and time-string.
        *
        *   @return         The current date and time as a string ( e.g. "19.07.2010 20:11:18" )
        ***************************************************************************************************************/
        public final String formatDateTime()
        {
            Date   date   = new Date();
            String millis = String.valueOf( System.currentTimeMillis() );

            return this.formatDateTime.format( date ) + "," + millis.substring( millis.length() - 3 );
        }

        /***************************************************************************************************************
        *   Formats a timestamp to a data-string.
        *
        *   @param  millis  The number of millis passed since the epoche. ( 01.01.1970 00:00:00 )
        *   @return         Ein Datumswert als String. ( z.B. "28.03.2005" )
        ***************************************************************************************************************/
        public final String formatDate( long millis )
        {
            return this.formatDate( new Date( millis ) );
        }

        /***************************************************************************************************************
        *   Formats a date to a date-string.
        *
        *   @param  date    A date.
        *   @return         The date as string. ( e.g. "17.08.2010" )
        ***************************************************************************************************************/
        private String formatDate( Date date)
        {
            return this.formatDate.format( date );
        }
    }
