
    package de.christopherstock.shooter.g3d.mesh;

    import  java.io.*;
    import  java.util.*;
    import  de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   Represents a collection of meshes.
    *******************************************************************************************************************/
    abstract class MeshCollection implements LibGeomObject, Serializable
    {
        private                 LibVertex           anchor              = null;
        protected               Mesh[]              meshes              = null;

        protected MeshCollection()
        {
            //abstract constructor
        }

        /***************************************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  anchor             The meshes' anchor point.
        ***************************************************************************************************************/
        public MeshCollection( LibVertex anchor, Mesh[] meshes )
        {
            this.anchor = anchor;
            this.meshes = meshes;
        }

        /***************************************************************************************************************
        *   Sets/updates anchor.
        *
        *   @param  newAnchor                   The new anchor vertex point.
        *   @param  performTranslationOnFaces   Determines if the faces shall be translated
        *                                       by the new anchor.
        ***************************************************************************************************************/
        public void setNewAnchor( LibVertex newAnchor, boolean performTranslationOnFaces, LibTransformationMode transformationMode )
        {
            this.anchor = newAnchor;
            for (Mesh iMesh : this.meshes) {
                iMesh.setNewAnchor(newAnchor, performTranslationOnFaces, transformationMode);
            }
        }

        public void assignParentOnFaces( LibGameObject parentGameObject )
        {
            for (Mesh iMesh : this.meshes) {
                iMesh.assignParentOnFaces( parentGameObject );
            }
        }

        /***************************************************************************************************************
        *   Translates all faces.
        *
        *   @param  x  The translation for axis x.
        *   @param  y  The translation for axis y.
        *   @param  z  The translation for axis z.
        ***************************************************************************************************************/
        public void translate( float x, float y, float z, LibTransformationMode transformationMode )
        {
            //translate all faces ( resetting the rotation! )
            for ( Mesh mesh : this.meshes)
            {
                //translate and init this face
                mesh.translate( x, y, z, transformationMode );
            }
        }

        /***************************************************************************************************************
        *   Rotates all faces of this mesh.
        *
        *   @param  tX          The amount to translate this vertex on the x-axis.
        *   @param  tY          The amount to translate this vertex on the y-axis.
        *   @param  tZ          The amount to translate this vertex on the z-axis.
        *   @param  rotX        The x-axis-angle to turn all vertices around.
        *   @param  rotY        The y-axis-angle to turn all vertices around.
        *   @param  rotZ        The z-axis-angle to turn all vertices around.
        ***************************************************************************************************************/
        public void translateAndRotateXYZ( float tX, float tY, float tZ, float rotX, float rotY, float rotZ, LibVertex alternateAnchor, LibTransformationMode transformationMode )
        {
            //rotate all faces
            for ( Mesh mesh : this.meshes)
            {
                //translate and init this face
                mesh.translateAndRotateXYZ( tX, tY, tZ, rotX, rotY, rotZ, alternateAnchor, transformationMode );
            }
        }

        /***************************************************************************************************************
        *   Draws the mesh.
        ***************************************************************************************************************/
        public final void draw()
        {
            //draw all faces
            for ( Mesh mesh : this.meshes)
            {
                mesh.draw();
            }
        }

        public final boolean checkCollisionHorz( LibCylinder cylinder )
        {
            //check all meshes
            for ( Mesh mesh : this.meshes)
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
            for ( Mesh mesh : this.meshes)
            {
                vecZ.addAll( mesh.checkCollisionVert( cylinder, exclude ) );
            }

            return vecZ;
        }

        public final Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> hitPoints = new Vector<LibHitPoint>();

            //fire all faces and collect all hit-points
            for ( Mesh mesh : this.meshes)
            {
                hitPoints.addAll( mesh.launchShot( shot ) );
            }

            return hitPoints;
        }

        public final LibVertex getAnchor()
        {
            return this.anchor;
        }
    }
