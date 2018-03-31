
    package de.christopherstock.shooter.g3d.mesh;

    import  java.awt.geom.*;
    import  java.io.*;
    import  java.util.*;
    import  de.christopherstock.lib.LibInvert;
    import  de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.g3d.wall.Wall.WallClimbable;
    import  de.christopherstock.shooter.io.sound.*;

    /*******************************************************************************************************************
    *   Represents a mesh.
    *******************************************************************************************************************/
    public class Mesh implements LibGeomObject, Serializable
    {
        public                      LibVertex               anchor              = null;

        private                     LibFaceTriangle[]       faces               = null;

        public Mesh( LibFaceTriangle[] faces, LibVertex anchor )
        {
            this( faces, anchor, 0.0f, 1.0f, LibInvert.ENo, null, LibTransformationMode.EOriginalsToOriginals, DrawMethod.EAlwaysDraw );
        }

        /***************************************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  faces              All faces this mesh shall consist of.
        *   @param  anchor             The meshes' anchor point.
        ***************************************************************************************************************/
        public Mesh( LibFaceTriangle[] faces, LibVertex anchor, float initRotZ, float initScale, LibInvert invert, LibGameObject parentGameObject, LibTransformationMode transformationMode, DrawMethod drawMethod )
        {
            this.faces = faces;

            //rotate all faces
            this.performOriginalRotationOnFaces(  initRotZ  );
            if ( initScale != 1.0f         ) this.performOriginalScalationOnFaces( initScale );
            if ( invert    == LibInvert.EYes  ) this.performOriginalInvertOnFaces();

            //set and translate by new anchor
            this.setNewAnchor( anchor, true, transformationMode );

            //assign parent game object
            this.assignParentOnFaces(     parentGameObject  );
            this.assignDrawMethodOnFaces( drawMethod        );
        }

        public LibFaceTriangle[] getFaces()
        {
            return this.faces;
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
            //assign new anchor for this mesh and for all it's faces - translate all faces by the new anchor!
            this.anchor = newAnchor;
            for (LibFaceTriangle iFace : this.faces) {
                iFace.setNewAnchor(this.anchor, performTranslationOnFaces, transformationMode);
            }
        }

        protected void assignParentOnFaces( LibGameObject parentGameObject )
        {
            for (LibFaceTriangle iFace : this.faces) {
                iFace.assignParentGameObject(parentGameObject);
            }
        }

        protected void assignDrawMethodOnFaces( DrawMethod drawMethod )
        {
            for (LibFaceTriangle iFace : this.faces) {
                iFace.setDrawMethod( drawMethod );
            }
        }

        private void performOriginalRotationOnFaces( float rotZ )
        {
            LibMatrix transformationMatrix = new LibMatrix( 0.0f, 0.0f, rotZ );

            for (LibFaceTriangle iFace : this.faces) {
                iFace.translateAndRotateXYZ(transformationMatrix, 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null);
            }
        }

        private void performOriginalScalationOnFaces( float scale )
        {
            for (LibFaceTriangle face : this.faces) {
                face.scale(scale, true);
            }
        }

        private void performOriginalInvertOnFaces()
        {
            for (LibFaceTriangle iFace : this.faces) {
                iFace.invert();
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
            for ( LibFaceTriangle face : this.faces)
            {
                //translate and init this face
                face.translate(x, y, z, transformationMode );
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
            LibMatrix transformationMatrix = new LibMatrix( rotX, rotY, rotZ );

            //rotate all faces
            for ( LibFaceTriangle face : this.faces)
            {
                //translate and init this face
                face.translateAndRotateXYZ( transformationMatrix, tX, tY, tZ, transformationMode, alternateAnchor );
            }
        }

        public final void mirrorFaces( boolean x, boolean y, boolean z )
        {
            //draw all faces
            for ( LibFaceTriangle face : this.faces)
            {
                face.mirror( x, y, z );
            }
        }

        /***************************************************************************************************************
        *   Draws the mesh.
        ***************************************************************************************************************/
        public void draw()
        {
            //draw all faces
            for ( LibFaceTriangle face : this.faces)
            {
                face.draw();
            }
        }

        protected final boolean checkAction( LibCylinder cylinder, boolean useBottomToleranceZ, boolean invertBottomTolerance )
        {
            //browse all faces and check if any face is affected by the action
            for ( LibFaceTriangle face : this.faces)
            {
                if ( cylinder.checkCollisionHorzLines( face, useBottomToleranceZ, invertBottomTolerance ) )
                {
                    return true;
                }
            }

            return false;
        }

        public boolean checkCollisionHorz( LibCylinder cylinder )
        {
            return this.checkCollisionHorz( cylinder, WallClimbable.EYes );
        }

        public boolean checkCollisionHorz( LibCylinder cylinder, WallClimbable wallClimbable )
        {
            //check all faces
            for ( LibFaceTriangle face : this.faces)
            {
                boolean b = face.checkCollisionHorz( cylinder, ( wallClimbable == WallClimbable.EYes ), false );
                if ( b ) return true;
            }

            return false;
        }

        public Vector<Float> checkCollisionVert( LibCylinder cylinder, Object exclude )
        {
            Vector<Float> vecZ = new Vector<Float>();

            //check all faces
            for ( LibFaceTriangle face : this.faces)
            {
                vecZ.addAll( face.checkCollisionVert( cylinder, exclude ) );
            }

            return vecZ;
        }

        public void changeTexture(LibGLTextureMetaData oldTex, LibGLTextureMetaData newTex )
        {
            for ( LibFaceTriangle face : this.faces)
            {
                face.changeTexture( oldTex, newTex );
            }
        }

        public void fadeOutAllFaces()
        {
            for ( LibFaceTriangle face : this.faces)
            {
                face.fadeOut( ShooterSetting.General.FADE_OUT_FACES );
            }
        }

        protected void darkenAllFaces( float opacity, boolean useRandomSubstract, boolean useRandomAdd, float maxSubstract, float maxAdd )
        {
            //darken all faces
            for ( LibFaceTriangle face : this.faces)
            {
                float targetOpacity = opacity;

                if ( useRandomSubstract ) targetOpacity -= ( maxSubstract * ( (float)LibMath.getRandom( 0, 100 ) / (float)100 ) );
                if ( useRandomAdd       ) targetOpacity += ( maxAdd       * ( (float)LibMath.getRandom( 0, 100 ) / (float)100 ) );

                face.darken( targetOpacity );
            }
        }

        public Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> hitPoints = new Vector<LibHitPoint>();

            //fire all faces and collect all hit-points
            for ( LibFaceTriangle face : this.faces)
            {
                //try new shot algo
                LibHitPoint hp = face.launchShotNew( shot );

                //do NOT use the old shot algo ! the new algo is fully operative! don't think so .. NO! :(
                if ( hp == null )
                {
                    //try old shot algo if no hitpoint was returned
                    hp = face.launchShotOld( shot );
                }

                //enqueue to collection if hitpoint is available
                if ( hp != null )
                {
                    hitPoints.add( hp );
                }
            }

            return hitPoints;
        }

        public final LibVertex getAnchor()
        {
            return this.anchor;
        }

        protected final void makeDistancedSound( SoundFg fx )
        {
            fx.playDistancedFx( new Point2D.Float(this.anchor.x, this.anchor.y ) );
        }

        public final float getCenterZ()
        {
            Float lowestZ  = null;
            Float highestZ = null;

            for ( LibFace f : this.getFaces() )
            {
                for ( LibVertex v : f.getVerticesToDraw() )
                {
                    if ( lowestZ  == null || v.z < lowestZ) lowestZ  = v.z;
                    if ( highestZ == null || v.z > highestZ) highestZ = v.z;
                }
            }

            if ( lowestZ == null || highestZ == null )
            {
                return 0.0f;
            }

            return (lowestZ + (highestZ - lowestZ) / 2 );
        }

        public final Point2D.Float getCenterPointXY()
        {
            Float lowestX  = null;
            Float lowestY  = null;
            Float highestX = null;
            Float highestY = null;

            for ( LibFace f : this.getFaces() )
            {
                for ( LibVertex v : f.getOriginalVertices() )
                {
                    if ( lowestX  == null || v.x < lowestX) lowestX   = v.x;
                    if ( lowestY  == null || v.y < lowestY) lowestY   = v.y;
                    if ( highestX == null || v.x > highestX) highestX = v.x;
                    if ( highestY == null || v.y > highestY) highestY = v.y;
                }
            }

            if ( lowestX == null || lowestY == null || highestX == null || highestY == null )
            {
                return null;
            }

            return new Point2D.Float
            (
                    lowestX + ( highestX - lowestX ) / 2,
                    lowestY + ( highestY - lowestY ) / 2
            );
        }

        public final void setTranslatedAsOriginalVertices()
        {
            for ( LibFaceTriangle face : this.faces)
            {
                face.originalVertices = face.transformedVertices;
            }
        }
/*
        public Vector<Float> checkCollision( Point2D.Float point )
        {
            Vector<Float> vecZ = new Vector<Float>();

            //check all faces
            for ( FaceTriangle face : faces )
            {
                Float f = face.checkCollision( point );

                if ( f != null )
                {
                    vecZ.addElement( f );
                }
            }

            return vecZ;
        }
*/
    }
