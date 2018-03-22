
    package de.christopherstock.lib.g3d;

    import  javax.vecmath.*;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   Represents a point in 3d space.
    *******************************************************************************************************************/
    public class LibVertex extends Point3f
    {
        private     static  final   long        serialVersionUID        = -5890364854059357978L;

        /***************************************************************************************************************
        *   The horizontal texture coordinate.
        ***************************************************************************************************************/
        public                      float       u                       = 0.0f;

        /***************************************************************************************************************
        *   The vertical texture coordinate.
        ***************************************************************************************************************/
        public                      float       v                       = 0.0f;

        public LibVertex()
        {
            this( 0.0f, 0.0f, 0.0f, 0.0f, 0.0f );
        }

        /***************************************************************************************************************
        *   Creates a vertex ignoring the texture coordinates.
        ***************************************************************************************************************/
        public LibVertex( Point3f p )
        {
            this( p.x, p.y, p.z, 0.0f, 0.0f );
        }

        /***************************************************************************************************************
        *   Creates a vertex ignoring the texture coordinates.
        ***************************************************************************************************************/
        public LibVertex( Point3d p )
        {
            this( (float)p.x, (float)p.y, (float)p.z, 0.0f, 0.0f );
        }

        /***************************************************************************************************************
        *   Creates a vertex ignoring the texture coordinates.
        ***************************************************************************************************************/
        public LibVertex( float aX, float aY, float aZ )
        {
            this( aX, aY, aZ, 0.0f, 0.0f );
        }

        /***************************************************************************************************************
        *   Creates a vertex with specified texture coordinates.
        ***************************************************************************************************************/
        public LibVertex( LibVertex aVertex )
        {
            this.x = aVertex.x;
            this.y = aVertex.y;
            this.z = aVertex.z;
            this.u = aVertex.u;
            this.v = aVertex.v;
        }

        /***************************************************************************************************************
        *   Creates a vertex with specified texture coordinates.
        ***************************************************************************************************************/
        public LibVertex( float aX, float aY, float aZ, float aU, float aV )
        {
            this.x = aX;
            this.y = aY;
            this.z = aZ;
            this.u = aU;
            this.v = aV;
        }

        /***************************************************************************************************************
        *   Copy constructor.
        ***************************************************************************************************************/
        public LibVertex copy()
        {
            return new LibVertex
            (
                    this.x,
                    this.y,
                    this.z,
                    this.u,
                    this.v
            );
        }

        public void translate( float transX, float transY, float transZ )
        {
            this.x += transX;
            this.y += transY;
            this.z += transZ;
        }

        public void translate( Point3f by )
        {
            this.x += by.x;
            this.y += by.y;
            this.z += by.z;
        }

        public void rotateXYZ( float rotX, float rotY, float rotZ, LibVertex alternateAnchor )
        {
            LibMatrix transformationMatrix = new LibMatrix( rotX, rotY, rotZ );
            transformationMatrix.transformVertexF( this, alternateAnchor );
        }

        @Override
        public String toString()
        {
            return "[" + this.x + "," + this.y + "," + this.z + "]";
        }
    }
