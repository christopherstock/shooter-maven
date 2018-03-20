
    package de.christopherstock.lib.g3d;

    import  javax.vecmath.*;
    import  de.christopherstock.lib.math.*;

    /**************************************************************************************
    *   Represents a point in 3d space.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class LibVertex extends Point3f
    {
        private     static  final   long        serialVersionUID        = -5890364854059357978L;

        /**************************************************************************************
        *   The horizontal texture coordinate.
        **************************************************************************************/
        public                      float       u                       = 0.0f;

        /**************************************************************************************
        *   The vertical texture coordinate.
        **************************************************************************************/
        public                      float       v                       = 0.0f;

        public LibVertex()
        {
            this( 0.0f, 0.0f, 0.0f, 0.0f, 0.0f );
        }

        /**************************************************************************************
        *   Creates a vertex ignoring the texture coordinates.
        **************************************************************************************/
        public LibVertex( Point3f p )
        {
            this( p.x, p.y, p.z, 0.0f, 0.0f );
        }

        /**************************************************************************************
        *   Creates a vertex ignoring the texture coordinates.
        **************************************************************************************/
        public LibVertex( Point3d p )
        {
            this( (float)p.x, (float)p.y, (float)p.z, 0.0f, 0.0f );
        }

        /**************************************************************************************
        *   Creates a vertex ignoring the texture coordinates.
        **************************************************************************************/
        public LibVertex( float aX, float aY, float aZ )
        {
            this( aX, aY, aZ, 0.0f, 0.0f );
        }

        /**************************************************************************************
        *   Creates a vertex with specified texture coordinates.
        **************************************************************************************/
        public LibVertex( LibVertex aVertex )
        {
            x = aVertex.x;
            y = aVertex.y;
            z = aVertex.z;
            u = aVertex.u;
            v = aVertex.v;
        }

        /**************************************************************************************
        *   Creates a vertex with specified texture coordinates.
        **************************************************************************************/
        public LibVertex( float aX, float aY, float aZ, float aU, float aV )
        {
            x = aX;
            y = aY;
            z = aZ;
            u = aU;
            v = aV;
        }

        /**************************************************************************************
        *   Copy constructor.
        **************************************************************************************/
        public LibVertex copy()
        {
            return new LibVertex
            (
                x,
                y,
                z,
                u,
                v
            );
        }

        public void translate( float transX, float transY, float transZ )
        {
            x += transX;
            y += transY;
            z += transZ;
        }

        public void translate( Point3f by )
        {
            x += by.x;
            y += by.y;
            z += by.z;
        }

        public void rotateXYZ( float rotX, float rotY, float rotZ, LibVertex alternateAnchor )
        {
            LibMatrix transformationMatrix = new LibMatrix( rotX, rotY, rotZ );
            transformationMatrix.transformVertexF( this, alternateAnchor );
        }

        @Override
        public String toString()
        {
            return "[" + x + "," + y + "," + z + "]";
        }
    }
