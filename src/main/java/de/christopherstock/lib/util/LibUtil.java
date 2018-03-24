
    package de.christopherstock.lib.util;

    /*******************************************************************************************************************
    *   Offers independent lib functionality ???
    *******************************************************************************************************************/
    public abstract class LibUtil
    {
        public static boolean contains( int[] arr, int element )
        {
            for ( int number : arr )
            {
                if ( number == element )
                {
                    return true;
                }
            }

            return false;
        }
    }
