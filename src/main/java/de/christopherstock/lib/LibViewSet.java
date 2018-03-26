
    package de.christopherstock.lib;

    /***************************************************************************************************************
    *   Represents a camera adjustment in 3d space.
    ***************************************************************************************************************/
    public final class LibViewSet
    {
        public LibOffset    pos                 = null;
        public LibRotation  rot                 = null;

        public LibViewSet( float posX, float posY, float posZ, float rotX, float rotY, float rotZ )
        {
            this.pos = new LibOffset(   posX, posY, posZ );
            this.rot = new LibRotation( rotX, rotY, rotZ );
        }
    }
