
    package de.christopherstock.lib.g3d;

    /*******************************************************************************************************************
    *   Represents a face.
    *******************************************************************************************************************/
    public class LibTriangle
    {
        private LibVertex              a                    = null;
        private LibVertex              b                    = null;
        private LibVertex              c                    = null;

        public LibTriangle( LibVertex a, LibVertex b, LibVertex c )
        {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
