
    package de.christopherstock.lib.io.d3ds;

    class LibMaxFace
    {
        public      LibMaxVertex        faceNormal          = null;
        public      LibMaxVertex        vertex1             = null;
        public      LibMaxVertex        vertex2             = null;
        public      LibMaxVertex        vertex3             = null;

        public LibMaxFace( LibMaxVertex faceNormal, LibMaxVertex vertex1, LibMaxVertex vertex2, LibMaxVertex vertex3 )
        {
            this.faceNormal = faceNormal;
            this.vertex1 = new LibMaxVertex( vertex1 );
            this.vertex2 = new LibMaxVertex( vertex2 );
            this.vertex3 = new LibMaxVertex( vertex3 );
        }
    }
