
    package de.christopherstock.lib.io;

    /*******************************************************************************************************************
    *   All resource extensions that matter.
    *******************************************************************************************************************/
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

        private                     String              specifier           = null;

        private LibExtension( String specifier )
        {
            this.specifier = specifier;
        }

        public String getSpecifier()
        {
            return this.specifier;
        }
    }
