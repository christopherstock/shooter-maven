
    package de.christopherstock.shooter.g3d.mesh;

    import  java.io.*;
    import  java.util.*;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.math.*;

    /**************************************************************************************
    *   Represents a collection of meshes.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    abstract class MeshCollection implements LibGeomObject, Serializable
    {
        private     static  final   long        serialVersionUID            = -5269079453567674781L;

        public                      Mesh[]      iMeshes                     = null;
        private                     LibVertex   iAnchor                     = null;

        protected MeshCollection()
        {
            //abstract constructor
        }

        /**************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  aAnchor             The meshes' anchor point.
        **************************************************************************************/
        public MeshCollection( LibVertex aAnchor, Mesh[] aMeshes )
        {
            iAnchor  = aAnchor;
            iMeshes  = aMeshes;
        }

        /******************************************************************************************
        *   Sets/updates anchor.
        *
        *   @param  newAnchor                   The new anchor vertex point.
        *   @param  performTranslationOnFaces   Determines if the faces shall be translated
        *                                       by the new anchor.
        ******************************************************************************************/
        public void setNewAnchor( LibVertex newAnchor, boolean performTranslationOnFaces, LibTransformationMode transformationMode )
        {
            iAnchor = newAnchor;
            for ( int i = 0; i < iMeshes.length; ++i )
            {
                iMeshes[ i ].setNewAnchor( newAnchor, performTranslationOnFaces, transformationMode );
            }
        }

        public void assignParentOnFaces( LibGameObject aParentGameObject )
        {
            for ( int i = 0; i < iMeshes.length; ++i )
            {
                iMeshes[ i ].assignParentOnFaces( aParentGameObject );
            }
        }

        /**************************************************************************************
        *   Translates all faces.
        *
        *   @param  tX  The translation for axis x.
        *   @param  tY  The translation for axis y.
        *   @param  tZ  The translation for axis z.
        **************************************************************************************/
        public void translate( float tX, float tY, float tZ, LibTransformationMode transformationMode )
        {
            //translate all faces ( resetting the rotation! )
            for ( Mesh mesh : iMeshes )
            {
                //translate and init this face
                mesh.translate( tX, tY, tZ, transformationMode );
            }
        }

        /**************************************************************************************
        *   Rotates all faces of this mesh.
        *
        *   @param  tX          The amount to translate this vertex on the x-axis.
        *   @param  tY          The amount to translate this vertex on the y-axis.
        *   @param  tZ          The amount to translate this vertex on the z-axis.
        *   @param  rotX        The x-axis-angle to turn all vertices around.
        *   @param  rotY        The y-axis-angle to turn all vertices around.
        *   @param  rotZ        The z-axis-angle to turn all vertices around.
        **************************************************************************************/
        public void translateAndRotateXYZ( float tX, float tY, float tZ, float rotX, float rotY, float rotZ, LibVertex alternateAnchor, LibTransformationMode transformationMode )
        {
            //rotate all faces
            for ( Mesh mesh : iMeshes )
            {
                //translate and init this face
                mesh.translateAndRotateXYZ( tX, tY, tZ, rotX, rotY, rotZ, alternateAnchor, transformationMode );
            }
        }

        /**************************************************************************************
        *   Draws the mesh.
        **************************************************************************************/
        public final void draw()
        {
            //draw all faces
            for ( Mesh mesh : iMeshes )
            {
                mesh.draw();
            }
        }

        public final boolean checkCollisionHorz( LibCylinder cylinder )
        {
            //check all meshes
            for ( Mesh mesh : iMeshes )
            {
                boolean b = mesh.checkCollisionHorz( cylinder );
                if ( b ) return true;
            }

            return false;
        }

        public final Vector<Float> checkCollisionVert( LibCylinder cylinder, Object exclude )
        {
            Vector<Float> vecZ = new Vector<Float>();

            //check all meshes
            for ( Mesh mesh : iMeshes )
            {
                vecZ.addAll( mesh.checkCollisionVert( cylinder, exclude ) );
            }

            return vecZ;
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> hitPoints = new Vector<LibHitPoint>();

            //fire all faces and collect all hit-points
            for ( Mesh mesh : iMeshes )
            {
                hitPoints.addAll( mesh.launchShot( shot ) );
            }

            return hitPoints;
        }

        public final LibVertex getAnchor()
        {
            return iAnchor;
        }
    }
