/*  $Id: LibMaxVertex.java 1273 2014-02-04 13:56:08Z jenetic.bytemare@googlemail.com $
 *  =================================================================================
 *  The 3dsmax-loader.
 */
    package de.christopherstock.lib.io.d3ds;

    class LibMaxVertex
    {
        public float    x   = 0.0f;
        public float    y   = 0.0f;
        public float    z   = 0.0f;
        public float    u   = 0.0f;
        public float    v   = 0.0f;

        /***********************************************************************************
        *   returns a copy!
        *
        *   @param  v   The max-vertex to create a copy from.
        ***********************************************************************************/
        public LibMaxVertex( LibMaxVertex aV )
        {
            x = aV.x;
            y = aV.y;
            z = aV.z;
        }

        public LibMaxVertex( float initX, float initY, float initZ )
        {
            x = initX;
            y = initY;
            z = initZ;
        }
    }
