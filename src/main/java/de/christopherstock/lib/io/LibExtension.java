
    package de.christopherstock.lib.io;

    /**************************************************************************************
    *   All resource extensions that matter.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public enum LibExtension
    {
        /** Portable network graphics. */
        png( ".png" ),

        /** Joint Photographic Experts Group. Compressed For large images */
        jpg( ".jpg" ),

        /** ASCII scene export. */
        ase( ".ase" ),

        /** Wave format. */
        wav( ".wav" ),

        /** MPEG Audio Layer III. */
        mp3( ".mp3" ),

        /** Sun Audio. */
        au( ".au" ),

        ;

        private                     String      iSpecifier          = null;

        private LibExtension( String aSpecifier )
        {
            iSpecifier = aSpecifier;
        }

        public String getSpecifier()
        {
            return iSpecifier;
        }
    }
