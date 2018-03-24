
    package de.christopherstock.lib;

    /***************************************************************************************************************
    *   Represents a camera adjustment in 3d space.
    ***************************************************************************************************************/
    public final class LibViewSet
    {
        public LibOffset    pos                 = null;
        public LibRotation  rot                 = null;

        public LibViewSet(float aPosX, float aPosY, float aPosZ, float aRotX, float aRotY, float aRotZ )
        {
            this.pos = new LibOffset(   aPosX, aPosY, aPosZ );
            this.rot = new LibRotation( aRotX, aRotY, aRotZ );
        }
    }
