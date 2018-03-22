
    package de.christopherstock.lib.g3d;

    /*******************************************************************************************************************
    *   Represents a face.
    *******************************************************************************************************************/
    public class LibTriangle
    {
        public                  LibVertex              a                    = null;
        public                  LibVertex              b                    = null;
        public                  LibVertex              c                    = null;

        public LibTriangle( LibVertex aA, LibVertex aB, LibVertex aC )
        {
            this.a = aA;
            this.b = aB;
            this.c = aC;
        }
    }
