
    package de.christopherstock.lib.g3d;

    import  javax.vecmath.*;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   Represents a point in 3d space.
    *******************************************************************************************************************/
    public class LibVertex extends Point3f
    {
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
        public LibVertex( float x, float y, float z )
        {
            this( x, y, z, 0.0f, 0.0f );
        }

        /***************************************************************************************************************
        *   Creates a vertex with specified texture coordinates.
        ***************************************************************************************************************/
        public LibVertex( LibVertex vertex )
        {
            this.x = vertex.x;
            this.y = vertex.y;
            this.z = vertex.z;
            this.u = vertex.u;
            this.v = vertex.v;
        }

        /***************************************************************************************************************
        *   Creates a vertex with specified texture coordinates.
        ***************************************************************************************************************/
        public LibVertex( float x, float y, float z, float u, float v )
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.u = u;
            this.v = v;
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

        public void translate( Point3f delta )
        {
            this.x += delta.x;
            this.y += delta.y;
            this.z += delta.z;
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
