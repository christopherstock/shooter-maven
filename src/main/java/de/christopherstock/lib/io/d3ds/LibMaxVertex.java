
    package de.christopherstock.lib.io.d3ds;

    class LibMaxVertex
    {
        public                  float       x           = 0.0f;
        public                  float       y           = 0.0f;
        public                  float       z           = 0.0f;

        public                  float       u           = 0.0f;
        public                  float       v           = 0.0f;

        /***************************************************************************************************************
        *   Creates a copy of the specified vertex.
        ***************************************************************************************************************/
        public LibMaxVertex( LibMaxVertex v )
        {
            this( v.x, v.y, v.z );
        }

        public LibMaxVertex( float x, float y, float z )
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
