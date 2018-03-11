/*  $Id: Mesh.java 1288 2014-10-11 17:19:47Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.g3d.mesh;

    import  java.awt.geom.*;
    import  java.io.*;
    import  java.util.*;
    import  de.christopherstock.lib.Lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.g3d.wall.Wall.WallClimbable;
    import  de.christopherstock.shooter.io.sound.*;

    /**************************************************************************************
    *   Represents a mesh.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class Mesh implements LibGeomObject, Serializable
    {
        private     static  final   long                serialVersionUID            = 4360022553250020228L;

        public                      LibVertex           iAnchor                     = null;

        private                     LibFaceTriangle[]      iFaces                      = null;

        public Mesh( LibFaceTriangle[] aFaces, LibVertex aAnchor )
        {
            this( aFaces, aAnchor, 0.0f, 1.0f, Invert.ENo, null, LibTransformationMode.EOriginalsToOriginals, DrawMethod.EAlwaysDraw );
        }

        /**************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  aFaces              All faces this mesh shall consist of.
        *   @param  aAnchor             The meshes' anchor point.
        **************************************************************************************/
        public Mesh( LibFaceTriangle[] aFaces, LibVertex aAnchor, float aInitRotZ, float aInitScale, Invert aInvert, LibGameObject aParentGameObject, LibTransformationMode transformationMode, DrawMethod aDrawMethod )
        {
            iFaces = aFaces;

            //rotate all faces
            performOriginalRotationOnFaces(  aInitRotZ  );
            if ( aInitScale != 1.0f         ) performOriginalScalationOnFaces( aInitScale );
            if ( aInvert    == Invert.EYes  ) performOriginalInvertOnFaces();

            //set and translate by new anchor
            setNewAnchor( aAnchor, true, transformationMode );

            //assign parent game object
            assignParentOnFaces(     aParentGameObject  );
            assignDrawMethodOnFaces( aDrawMethod        );
        }

        public LibFaceTriangle[] getFaces()
        {
            return iFaces;
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
            //assign new anchor for this mesh and for all it's faces - translate all faces by the new anchor!
            iAnchor = newAnchor;
            for ( int i = 0; i < iFaces.length; ++i )
            {
                iFaces[ i ].setNewAnchor( iAnchor, performTranslationOnFaces, transformationMode );
            }
        }

        protected void assignParentOnFaces( LibGameObject aParentGameObject )
        {
            for ( int i = 0; i < iFaces.length; ++i )
            {
                iFaces[ i ].assignParentGameObject( aParentGameObject );
            }
        }

        protected void assignDrawMethodOnFaces( DrawMethod aDrawMethod )
        {
            for ( int i = 0; i < iFaces.length; ++i )
            {
                iFaces[ i ].setDrawMethod( aDrawMethod );
            }
        }

        private void performOriginalRotationOnFaces( float aRotZ )
        {
            LibMatrix transformationMatrix = new LibMatrix( 0.0f, 0.0f, aRotZ );

            for ( int i = 0; i < iFaces.length; ++i )
            {
                iFaces[ i ].translateAndRotateXYZ( transformationMatrix, 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
            }
        }

        private void performOriginalScalationOnFaces( float aScale )
        {
            for ( int i = 0; i < iFaces.length; ++i )
            {
                iFaces[ i ].scale( aScale, true );
            }
        }

        private void performOriginalInvertOnFaces()
        {
            for ( int i = 0; i < iFaces.length; ++i )
            {
                iFaces[ i ].invert();
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
            for ( LibFaceTriangle face : iFaces )
            {
                //translate and init this face
                face.translate( tX, tY, tZ, transformationMode );
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
            LibMatrix transformationMatrix = new LibMatrix( rotX, rotY, rotZ );

            //rotate all faces
            for ( LibFaceTriangle face : iFaces )
            {
                //translate and init this face
                face.translateAndRotateXYZ( transformationMatrix, tX, tY, tZ, transformationMode, alternateAnchor );
            }
        }

        public final void mirrorFaces( boolean x, boolean y, boolean z )
        {
            //draw all faces
            for ( LibFaceTriangle face : iFaces )
            {
                face.mirror( x, y, z );
            }
        }

        /**************************************************************************************
        *   Draws the mesh.
        **************************************************************************************/
        public void draw()
        {
            //draw all faces
            for ( LibFaceTriangle face : iFaces )
            {
                face.draw();
            }
        }

        public final boolean checkAction( LibCylinder cylinder, boolean useBottomToleranceZ, boolean invertBottomTolerance )
        {
            //browse all faces and check if any face is affected by the action
            for ( LibFaceTriangle face : iFaces )
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
            return checkCollisionHorz( cylinder, WallClimbable.EYes );
        }

        public boolean checkCollisionHorz( LibCylinder cylinder, WallClimbable wallClimbable )
        {
            //check all faces
            for ( LibFaceTriangle face : iFaces )
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
            for ( LibFaceTriangle face : iFaces )
            {
                vecZ.addAll( face.checkCollisionVert( cylinder, exclude ) );
            }

            return vecZ;
        }

        public void changeTexture( LibGLTexture oldTex, LibGLTexture newTex )
        {
            for ( LibFaceTriangle face : iFaces )
            {
                face.changeTexture( oldTex, newTex );
            }
        }

        public void fadeOutAllFaces()
        {
            for ( LibFaceTriangle face : iFaces )
            {
                face.fadeOut( ShooterSettings.General.FADE_OUT_FACES );
            }
        }

        public void darkenAllFaces( float aOpacity, boolean useRandomSubstract, boolean useRandomAdd, float maxSubstract, float maxAdd )
        {
            //darken all faces
            for ( LibFaceTriangle face : iFaces )
            {
                float opacity = aOpacity;

                if ( useRandomSubstract ) opacity -= ( maxSubstract * ( (float)LibMath.getRandom( 0, 100 ) / (float)100 ) );
                if ( useRandomAdd       ) opacity += ( maxAdd       * ( (float)LibMath.getRandom( 0, 100 ) / (float)100 ) );

                face.darken( opacity );
            }
        }

        public Vector<LibHitPoint> launchShot( LibShot shot )
        {
            Vector<LibHitPoint> hitPoints = new Vector<LibHitPoint>();

            //fire all faces and collect all hit-points
            for ( LibFaceTriangle face : iFaces )
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
            return iAnchor;
        }

        public final void makeDistancedSound( SoundFg fx )
        {
            fx.playDistancedFx( new Point2D.Float( iAnchor.x, iAnchor.y ) );
        }

        public final float getCenterZ()
        {
            Float lowestZ  = null;
            Float highestZ = null;

            for ( LibFace f : getFaces() )
            {
                for ( LibVertex v : f.getVerticesToDraw() )
                {
                    if ( lowestZ  == null || v.z < lowestZ.floatValue()  ) lowestZ  = new Float( v.z );
                    if ( highestZ == null || v.z > highestZ.floatValue() ) highestZ = new Float( v.z );
                }
            }

            if ( lowestZ == null || highestZ == null )
            {
                return 0.0f;
            }

            return ( lowestZ.floatValue() + ( highestZ.floatValue() - lowestZ.floatValue() ) / 2 );
        }

        public final Point2D.Float getCenterPointXY()
        {
            Float lowestX  = null;
            Float lowestY  = null;
            Float highestX = null;
            Float highestY = null;

            for ( LibFace f : getFaces() )
            {
                for ( LibVertex v : f.getOriginalVertices() )
                {
                    if ( lowestX  == null || v.x < lowestX.floatValue()  ) lowestX  = new Float( v.x );
                    if ( lowestY  == null || v.y < lowestY.floatValue()  ) lowestY  = new Float( v.y );
                    if ( highestX == null || v.x > highestX.floatValue() ) highestX = new Float( v.x );
                    if ( highestY == null || v.y > highestY.floatValue() ) highestY = new Float( v.y );
                }
            }

            if ( lowestX == null || lowestY == null || highestX == null || highestY == null )
            {
                return null;
            }

            return new Point2D.Float
            (
                lowestX.floatValue() + ( highestX.floatValue() - lowestX.floatValue() ) / 2,
                lowestY.floatValue() + ( highestY.floatValue() - lowestY.floatValue() ) / 2
            );
        }

        public final void setTranslatedAsOriginalVertices()
        {
            for ( LibFaceTriangle face : iFaces )
            {
                face.iOriginalVertices = face.iTransformedVertices;
            }
        }
/*
        public Vector<Float> checkCollision( Point2D.Float point )
        {
            Vector<Float> vecZ = new Vector<Float>();

            //check all faces
            for ( FaceTriangle face : iFaces )
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
