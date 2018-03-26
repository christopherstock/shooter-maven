
    package de.christopherstock.lib.g3d;

    /*******************************************************************************************************************
    *   Represents a face.
    *******************************************************************************************************************/
    public class LibTriangle
    {
        public                  LibVertex              a                    = null;
        public                  LibVertex              b                    = null;
        public                  LibVertex              c                    = null;

        public LibTriangle( LibVertex a, LibVertex b, LibVertex c )
        {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
