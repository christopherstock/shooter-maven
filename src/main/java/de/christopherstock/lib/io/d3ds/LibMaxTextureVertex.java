/*  $Id: LibMaxTextureVertex.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =================================================================================
 *  The 3dsmax-loader.
 */
    package de.christopherstock.lib.io.d3ds;

    class LibMaxTextureVertex
    {
        public float    u   = 0;
        public float    v   = 0;

        public LibMaxTextureVertex( float initU, float initV )
        {
            u = initU;
            v = initV;
        }
    }
