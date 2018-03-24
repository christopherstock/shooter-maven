
    package de.christopherstock.lib.gl;

    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.gl.LibGLImage.*;
    import  de.christopherstock.lib.gl.LibGLTexture.Translucency;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   The GL-View.
    *******************************************************************************************************************/
    public abstract class LibGLView
    {
        public      static  final   float               VIEW_ANGLE                  = 60.0f;
        protected   static  final   float               DEPTH_BUFFER_SIZE           = 1.0f;
        protected   static  final   float               VIEW_MIN                    = 0.1f;
        protected   static  final   float               VIEW_MAX                    = 100.0f;

        protected                   float               iAspectRatio                = 0;
        protected                   LibDebug            iDebug                      = null;

        protected                   LibGLImage[]        iTextureImages              = null;
        protected                   boolean             iLightDebugPointSet         = false;

        private                     Vector<LibGLFace>   iFirstPrioDrawingQueue      = new Vector<LibGLFace>();
        private                     Vector<LibGLFace>   iDefaultFaceDrawingQueue    = new Vector<LibGLFace>();
        protected                   LibGLTexture        iLastOpaqueTexture          = null;

        protected LibGLView( float aAspectRatio, LibDebug aDebug )
        {
            this.iAspectRatio = aAspectRatio;
            this.iDebug = aDebug;
        }

        public void clearFaceQueue()
        {
            this.iDefaultFaceDrawingQueue.clear();
            this.iFirstPrioDrawingQueue.clear();
        }

        public void enqueueFaceToQueue( LibGLFace aFace )
        {
            //opaque
            if ( aFace.getTexture() == null || aFace.getTexture().getTranslucency() == Translucency.EOpaque )
            {
                //immediate draw for opaque faces
                this.drawFace( aFace.getVerticesToDraw(), aFace.getFaceNormal(), aFace.getTexture(), aFace.getColor3f(), aFace.getAlpha(), aFace.getDarkenOpacity() );
            }
            //1st prio ( masked faces )
            else if ( aFace.getTexture().getTranslucency() == Translucency.EHasMaskBulletHole )
            {
                //add to masked face queue
                this.iFirstPrioDrawingQueue.addElement( aFace );
            }
            //default queue ( masked faces & glass )
            else
            {
                //add to glass face queue
                this.iDefaultFaceDrawingQueue.addElement( aFace );
            }
        }

        public void flushFaceQueue( LibVertex aCameraViewpoint )
        {
          //ShooterDebugSystem.bugfix.out( "draw faces: ["+faceDrawingQueueOpaque.size()+"] opaque ["+faceDrawingQueueMasked.size()+"] masked ["+faceDrawingQueue.size()+" translucent]" );

            //sort & draw translucent faces according to distance
            this.sortAndDrawAllFaces(this.iDefaultFaceDrawingQueue,  aCameraViewpoint );
            this.sortAndDrawAllFaces(this.iFirstPrioDrawingQueue,    aCameraViewpoint );

            //flushing gl forces an immediate draw
            this.flushGL();
        }

        private void sortAndDrawAllFaces( Vector<LibGLFace> aFaceDrawingQueue, LibVertex aCameraViewpoint )
        {
            if ( aFaceDrawingQueue.size() > 0 )
            {
                Vector<LibGLFace> faceDrawingQueue = sortFacesAccordingToDistance( aFaceDrawingQueue, aCameraViewpoint );
                for ( LibGLFace faceTranslucent : faceDrawingQueue )
                {
                    //draw
                    this.drawFace( faceTranslucent.getVerticesToDraw(), faceTranslucent.getFaceNormal(), faceTranslucent.getTexture(), faceTranslucent.getColor3f(), faceTranslucent.getAlpha(), faceTranslucent.getDarkenOpacity() );
                }
            }
        }

        private static Vector<LibGLFace> sortFacesAccordingToDistance(Vector<LibGLFace> faces, LibVertex cameraViewPoint )
        {
            //debug.out( "sort face queue - [" + faces.size() + "] faces" );

            //fill hashmap with all faces and their distances to the player
            Hashtable<Float,Vector<LibGLFace>> distances = new Hashtable<Float,Vector<LibGLFace>>();
            for ( LibGLFace face : faces )
            {
                Float distance = null;
/*
                //this is inoperative because the face normals are not points but directions!
                if ( face.getFaceNormal() != null ) distance = new Float( LibMathGeometry.getDistanceXY( face.getFaceNormal(), cameraViewPoint ) );
*/
                {
                    distance = LibMathGeometry.getDistanceXY( face.getAnchor(), cameraViewPoint );
                }

                if ( distances.get( distance ) == null )
                {
                    Vector<LibGLFace> vf = new Vector<LibGLFace>();
                    vf.addElement( face );
                    distances.put( distance, vf );
                }
                else
                {
                    Vector<LibGLFace> vf = distances.get( distance );
                    vf.addElement( face );
                    distances.put( distance, vf );
                }
            }

            //sort by distances
            Float[] dists = distances.keySet().toArray( new Float[] {} );
            //debug.out( "different distances: [" + dists.length + "]" );
            Arrays.sort( dists );

            //browse all distances reversed ( ordered by FAREST till LOWEST )
            Vector<LibGLFace> ret = new Vector<LibGLFace>();
            for ( int i = dists.length - 1; i >= 0; --i )
            {
                //debug.out( "distance: [" + dists[ i ] + "]" );

                Vector<LibGLFace> vf2 = distances.get( dists[ i ] );
                ret.addAll( vf2 );
            }

            //debug.out( "return elements: [" + ret.size() + "]" );

            return ret;
        }

        public final void drawOrthoBitmapBytes( LibGLImage glImage, int x, int y )
        {
            this.drawOrthoBitmapBytes( glImage, x, y, 1.0f );
        }

        public void drawOrthoBitmapBytes( LibGLImage glImage, int x, int y, float alphaF )
        {
            this.drawOrthoBitmapBytes( glImage, x, y, alphaF, 1.0f, 1.0f, true );
        }

        public abstract void drawOrthoBitmapBytes( LibGLImage glImage, int x, int y, float alphaF, float scaleX, float scaleY, boolean translateAnk );

        public abstract void drawFace( LibVertex[] verticesToDraw, LibVertex faceNormal, LibGLTexture texture, float[] col, float alpha, float darken );

        public abstract void clearGl( LibColors clearCol );

        protected abstract void flushGL();

        public abstract void setCamera( LibViewSet viewSet );

        protected abstract void setOrthoOn();

        protected abstract void setOrthoOff();

        public abstract void setLightsOn( LibGLLight[] lights, LibColors ambient );

        public abstract void setLightsOff();

        protected abstract void setLight( int lightKey, LibGLLight light );

        public abstract void initTextures( LibGLImage[] texImages );

        protected abstract int getSrcPixelFormat( SrcPixelFormat spf );

        public abstract void setNewGluFaceAngle( float faceAngle );
    }
