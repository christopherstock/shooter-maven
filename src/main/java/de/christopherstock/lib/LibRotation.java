
    package de.christopherstock.lib;

    import de.christopherstock.lib.math.LibMath;

    /*******************************************************************************************************************
    *   Specifies a body rotation.
    *******************************************************************************************************************/
    public final class LibRotation
    {
        public      float       x           = 0.0f;
        public      float       y           = 0.0f;
        public      float       z           = 0.0f;
        public      float       iSpeed      = 0.0f;

        public LibRotation()
        {
            this.set( 0.0f, 0.0f, 0.0f, 0.0f );
        }

        public LibRotation(float aX, float aY, float aZ )
        {
            this.set( aX, aY, aZ, 0.0f );
        }

        public void set( float aX, float aY, float aZ, float aSpeed )
        {
            this.x = aX;
            this.y = aY;
            this.z = aZ;
            this.iSpeed = aSpeed;
        }

        public void reachToAbsolute(LibRotation targetPitch, float absoluteDistance )
        {
            this.x = LibMath.reachToAbsolute(this.x, targetPitch.x, absoluteDistance );
            this.y = LibMath.reachToAbsolute(this.y, targetPitch.y, absoluteDistance );
            this.z = LibMath.reachToAbsolute(this.z, targetPitch.z, absoluteDistance );
        }

        public LibRotation copy()
        {
            return new LibRotation(this.x, this.y, this.z);
        }

        public boolean equal( LibRotation r )
        {
            return ( r.x == this.x && r.y == this.y && r.z == this.z);
        }

        public boolean equalRounded( LibRotation other )
        {
            return
            (
                    Math.round(this.x) == Math.round( other.x )
                &&  Math.round(this.y) == Math.round( other.y )
                &&  Math.round(this.z) == Math.round( other.z )
            );
        }

        @Override
        public String toString()
        {
            return "[" + this.x + "," + this.y + "," + this.z + "]";
        }
    }
