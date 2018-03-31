
    package de.christopherstock.lib.gl;

    import  java.nio.ByteBuffer;
    import  java.nio.ByteOrder;
    import  java.nio.FloatBuffer;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace;
    import  de.christopherstock.lib.gl.LibGLTextureImage.*;
    import  de.christopherstock.lib.gl.LibGLTextureMetaData.Translucency;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.Shooter;
    import org.lwjgl.BufferUtils;
    import  org.lwjgl.LWJGLException;
    import  org.lwjgl.opengl.*;
    import  org.lwjgl.util.glu.*;

    /*******************************************************************************************************************
    *   The GL-View.
    *******************************************************************************************************************/
    public final class LibGLView
    {
        public      static  final   float                   VIEW_ANGLE                  = 60.0f;
        private     static  final   float                   DEPTH_BUFFER_SIZE           = 1.0f;
        private     static  final   float                   VIEW_MIN                    = 0.1f;
        private     static  final   float                   VIEW_MAX                    = 100.0f;

        private                     LibDebug                debug                       = null;
        private                     LibFrame                panel                       = null;

        public                      int                     width                       = 0;
        public                      int                     height                      = 0;
        private                     float                   aspectRatio                 = 0.0f;

        private                     Vector<LibFace>         firstPrioDrawingQueue       = new Vector<LibFace>();
        private                     Vector<LibFace>         defaultFaceDrawingQueue     = new Vector<LibFace>();
        private                     LibGLTextureMetaData    lastOpaqueTexture           = null;

        public LibGLView( LibDebug debug, LibFrame panel, int width, int height )
        {
            this.debug       = debug;
            this.panel       = panel;

            this.width       = width;
            this.height      = height;

            this.aspectRatio = ( (float)width / (float)height );
        }

        public void init()
        {
            try
            {
                // find out what the current bits per pixel of the desktop is
                int currentBpp = Display.getDisplayMode().getBitsPerPixel();

                // find a display mode at 800x600
                DisplayMode[] dms           = Display.getAvailableDisplayModes();
                DisplayMode   displayMode   = null;

                for ( DisplayMode dm : dms )
                {
                    //check if this display mode fits
                    if
                    (
                            this.width  == dm.getWidth()
                        &&  this.height == dm.getHeight()
                        &&  currentBpp  == dm.getBitsPerPixel()
                    )
                    {
                        this.debug.out( " picked display mode [" + dm.getWidth() + "][" + dm.getHeight() + "][" + dm.getBitsPerPixel() + "]" );
                        displayMode = dm;
                        break;
                    }
                }

                // if can't find a mode, notify the user the give up
                if ( displayMode == null )
                {
                    this.debug.err( " Display mode not available!" );
                    return;
                }

                // configure and create the LWJGL display
                this.debug.out( " Setting display mode.." );
                Display.setDisplayMode( displayMode );
                this.debug.out( " Setting display mode Ok" );
                Display.setFullscreen( false );

                //((Canvas)frame.getCanvas() ).setFocusable(false);

                //set native canvas as parent displayable
                Display.setParent( this.panel.getCanvas() );
                this.debug.out( " Setting native Canvas Ok" );

                //create the display
                Display.setInitialBackground( 1.0f, 1.0f, 1.0f );
                Display.create();
                this.debug.out( " Display creation Ok" );

                //request focus ( hangs?? )
              //frame.getCanvas().requestFocus();
                this.debug.out( " Requesting focus Ok" );
            }
            catch ( LWJGLException e)
            {
                this.debug.trace( e );
            }

            //assign the frame's dimensions and parse its offsets
            this.width  = Display.getParent().getWidth();
            this.height = Display.getParent().getHeight();

            this.debug.out( " Assigned frame dimensions [" + this.width + "]x[" + this.height + "]" );

            //run through some based OpenGL capability settings

            // effect ?
            // GL11.glEnable(GL11.GL_CULL_FACE);

            //switch to projection-matrix-mode and set glView ratio
            this.setNewGluFaceAngle( VIEW_ANGLE );
/*
            GL11.glMatrixMode(      GL11.GL_PROJECTION                                      );
            GL11.glLoadIdentity();
            float ratio = ( (float)aFormWidth / (float)aFormHeight );
            GLU.gluPerspective( VIEW_ANGLE, ratio, VIEW_MIN, VIEW_MAX );
*/
            //enter matrix mode modelview
            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glLoadIdentity();

            //prepare
            GL11.glShadeModel(      GL11.GL_SMOOTH                                          );      //smooth Shading ( GL_FLAT: flat shading )
            GL11.glClearDepth(      DEPTH_BUFFER_SIZE                                       );      //set depth-buffer's size
            GL11.glEnable(          GL11.GL_DEPTH_TEST                                      );      //enable depth-sorting [ jams the scene :-( ]
            GL11.glDepthFunc(       GL11.GL_LEQUAL                                          );      //less or equal depth-testing! GL.GL_LESS caused problems in combination with blending!
            GL11.glHint(            GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST     );      //really nice perspective-calculations

            // disable all lights!
            GL11.glDisable(         GL11.GL_LIGHTING                                        );      //disable lighting
/*
            GL11.glDisable(         GL11.GL_LIGHT0                                          );
            GL11.glDisable(         GL11.GL_LIGHT1                                          );
            GL11.glDisable(         GL11.GL_LIGHT2                                          );
            GL11.glDisable(         GL11.GL_LIGHT3                                          );
            GL11.glDisable(         GL11.GL_LIGHT4                                          );
            GL11.glDisable(         GL11.GL_LIGHT5                                          );
            GL11.glDisable(         GL11.GL_LIGHT6                                          );
            GL11.glDisable(         GL11.GL_LIGHT7                                          );
            GL11.glDisable(         GL11.GL_COLOR_MATERIAL                                  );      //disable colored material by lights
            GL11.glLightModelf(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
*/
/*
            GL11.glEnable(         GL11.GL_LIGHTING                                         );      //disable lighting
            GL11.glEnable(         GL11.GL_COLOR_MATERIAL                                   );      //disable lighting

            FloatBuffer whiteLight = BufferUtils.createFloatBuffer(4);
            whiteLight.put(1.0f).put(0.0f).put(0.0f).put(1.0f).flip();
            GL11.glLight( GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteLight );
            GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, whiteLight);
*/
            GL11.glEnable(          GL11.GL_TEXTURE_2D                                      );      //enable textures
            GL11.glEnable(          GL11.GL_NORMALIZE                                       );      //force normal lengths to 1
/*
            GL11.glEnable(          GL11.GL_POINT_SMOOTH                                    );      //enable antialiasing for points
            GL11.glEnable(          GL11.GL_LINE_SMOOTH                                     );      //enable antialiasing for lines
            GL11.glEnable(          GL11.GL_POLYGON_SMOOTH                                  );      //enable antialiasing for polygons
*/
            //set perspective
//            GLU.gluPerspective( VIEW_ANGLE, ( (float)LibGL.frame.width / (float)LibGL.frame.height ), VIEW_MIN, VIEW_MAX );

        }

        public void clearFaceQueue()
        {
            this.defaultFaceDrawingQueue.clear();
            this.firstPrioDrawingQueue.clear();
        }

        public void enqueueFaceToQueue( LibFace aFace )
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
                this.firstPrioDrawingQueue.addElement( aFace );
            }
            //default queue ( masked faces & glass )
            else
            {
                //add to glass face queue
                this.defaultFaceDrawingQueue.addElement( aFace );
            }
        }

        public void flushFaceQueue( LibVertex aCameraViewpoint )
        {
          //ShooterDebugSystem.bugfix.out( "draw faces: ["+faceDrawingQueueOpaque.size()+"] opaque ["+faceDrawingQueueMasked.size()+"] masked ["+faceDrawingQueue.size()+" translucent]" );

            //sort & draw translucent faces according to distance
            this.sortAndDrawAllFaces(this.defaultFaceDrawingQueue,  aCameraViewpoint );
            this.sortAndDrawAllFaces(this.firstPrioDrawingQueue,    aCameraViewpoint );

            //flushing gl forces an immediate draw
            this.flushGL();
        }

        private void sortAndDrawAllFaces( Vector<LibFace> aFaceDrawingQueue, LibVertex aCameraViewpoint )
        {
            if ( aFaceDrawingQueue.size() > 0 )
            {
                Vector<LibFace> faceDrawingQueue = sortFacesAccordingToDistance( aFaceDrawingQueue, aCameraViewpoint );
                for ( LibFace faceTranslucent : faceDrawingQueue )
                {
                    //draw
                    this.drawFace( faceTranslucent.getVerticesToDraw(), faceTranslucent.getFaceNormal(), faceTranslucent.getTexture(), faceTranslucent.getColor3f(), faceTranslucent.getAlpha(), faceTranslucent.getDarkenOpacity() );
                }
            }
        }

        private static Vector<LibFace> sortFacesAccordingToDistance(Vector<LibFace> faces, LibVertex cameraViewPoint )
        {
            //debug.out( "sort face queue - [" + faces.size() + "] faces" );

            //fill hashmap with all faces and their distances to the player
            Hashtable<Float,Vector<LibFace>> distances = new Hashtable<Float,Vector<LibFace>>();
            for ( LibFace face : faces )
            {
                Float distance = null;

                //this is inoperative because the face normals are not points but directions!
                if ( face.getFaceNormal() != null ) distance = new Float( LibMathGeometry.getDistanceXY( face.getFaceNormal(), cameraViewPoint ) );

                {
                    distance = LibMathGeometry.getDistanceXY( face.getAnchor(), cameraViewPoint );
                }

                if ( distances.get( distance ) == null )
                {
                    Vector<LibFace> vf = new Vector<LibFace>();
                    vf.addElement( face );
                    distances.put( distance, vf );
                }
                else
                {
                    Vector<LibFace> vf = distances.get( distance );
                    vf.addElement( face );
                    distances.put( distance, vf );
                }
            }

            //sort by distances
            Float[] dists = distances.keySet().toArray( new Float[] {} );
            //debug.out( "different distances: [" + dists.length + "]" );
            Arrays.sort( dists );

            //browse all distances reversed ( ordered by FAREST till LOWEST )
            Vector<LibFace> ret = new Vector<LibFace>();
            for ( int i = dists.length - 1; i >= 0; --i )
            {
                //debug.out( "distance: [" + dists[ i ] + "]" );

                Vector<LibFace> vf2 = distances.get( dists[ i ] );
                ret.addAll( vf2 );
            }

            //debug.out( "return elements: [" + ret.size() + "]" );

            return ret;
        }

        public final void drawOrthoBitmapBytes(LibGLTextureImage glImage, int x, int y )
        {
            this.drawOrthoBitmapBytes( glImage, x, y, 1.0f );
        }

        public void drawOrthoBitmapBytes(LibGLTextureImage glImage, int x, int y, float alphaF )
        {
            this.drawOrthoBitmapBytes( glImage, x, y, alphaF, 1.0f, 1.0f, true );
        }

        public void setCamera( LibViewSet viewSet )
        {
            GL11.glLoadIdentity();                                                                      //create new identity

            GL11.glNormal3f(    0.0f,                   0.0f,           0.0f                    );      //normalize

            GL11.glRotatef(     viewSet.rot.y,          0.0f,           0.0f,           1.0f    );      //rotate z (!)
            GL11.glRotatef(     viewSet.rot.x,          1.0f,           0.0f,           0.0f    );      //rotate x
            GL11.glRotatef(     360.0f - viewSet.rot.z, 0.0f,           1.0f,           0.0f    );      //rotate y (!)

            GL11.glTranslatef(  viewSet.pos.x,          viewSet.pos.z,  viewSet.pos.y           );      //translate x z y
        }

        private void drawFace(LibVertex[] verticesToDraw, LibVertex faceNormal, LibGLTextureMetaData texture, float[] col, float alpha, float darken)
        {
            //draw plain color if texture is missing ( fx points or debug only )
            if ( texture == null )
            {
                //draw plain without texture
                GL11.glDisable(   GL11.GL_TEXTURE_2D    );

                //set face color
                GL11.glColor3f( col[ 0 ], col[ 1 ], col[ 2 ] );

                if ( alpha != 1.0f )
                {
                    GL11.glEnable(      GL11.GL_BLEND                                     );
                    GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA    );
                    GL11.glColor4f(     col[ 0 ], col[ 1 ], col[ 2 ], alpha                        );
                }

                if ( darken != 1.0f )
                {
                    GL11.glEnable(      GL11.GL_BLEND                                   );
                    GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA  );
                    GL11.glColor4f(     darken, darken, darken, 1.0f   );
                }

                //draw all vertices
                GL11.glBegin(      GL11.GL_POLYGON   );

                //set face normal
                if ( faceNormal != null ) GL11.glNormal3f( faceNormal.x, faceNormal.y, faceNormal.z );
                //if ( faceNormal != null ) GL11.glNormal3f( 0.0f, 0.0f, 0.0f );

                //draw all vertices
                for ( LibVertex currentVertex : verticesToDraw )
                {
                    this.drawVertex( currentVertex );
                }
                GL11.glEnd();

                if ( alpha != 1.0f || darken != 1.0f )
                {
                    GL11.glDisable( GL11.GL_BLEND                                     );
                }
            }
            //texture
            else
            {
                switch ( texture.getTranslucency() )
                {
                    case EGlass:
                    {
                        GL11.glEnable(      GL11.GL_BLEND                                   );
                      //GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA  );
                        GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE                  );

                        //texture
                        GL11.glEnable(      GL11.GL_TEXTURE_2D                              );      //enable texture-mapping
                        if (this.lastOpaqueTexture == null || this.lastOpaqueTexture.getId() != texture.getId() )
                        {
                            GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture.getId()             );      //bind face's texture
                        }
                        this.lastOpaqueTexture = texture;

                        //set glass color
                        GL11.glColor4f(     0.5f, 0.5f, 0.5f, 0.5f                          );

                        //draw all vertices
                        GL11.glBegin(       GL11.GL_POLYGON                                 );

                        //set face normal
                        if ( faceNormal != null ) GL11.glNormal3f( faceNormal.x, faceNormal.y, faceNormal.z );

                        //draw all vertices
                        for ( LibVertex currentVertex : verticesToDraw )
                        {
                            this.drawTexturedVertex( currentVertex );
                        }
                        GL11.glEnd();

                        //disable blending
                        GL11.glDisable(     GL11.GL_BLEND                                   );

                        break;
                    }

                    case EHasMask:
                    case EHasMaskBulletHole:
                    {
                        //enable texture-mapping
                        GL11.glEnable(      GL11.GL_TEXTURE_2D                              );

                        //set face color
                        GL11.glColor3f( col[ 0 ], col[ 1 ], col[ 2 ] );

                        //blend for mask
                        GL11.glEnable(      GL11.GL_BLEND                                   );
                        GL11.glBlendFunc( GL11.GL_DST_COLOR, GL11.GL_ZERO );

//                      GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA  );
//                      GL11.glColor4f(     0.5f, 0.5f, 0.5f, 0.25f                         );

                        //draw mask
                        GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture.getMaskId()         );
                        GL11.glBegin(       GL11.GL_POLYGON                                 );

                        //set face normal
                        if ( faceNormal != null ) GL11.glNormal3f( faceNormal.x, faceNormal.y, faceNormal.z );

                        //draw all vertices
                        for ( LibVertex currentVertex : verticesToDraw )
                        {
                            this.drawTexturedVertex( currentVertex );
                        }
                        GL11.glEnd();

                        //blend for texture
                        GL11.glBlendFunc( GL11.GL_ONE, GL11.GL_ONE );

                        //draw texture
                        GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture.getId()             );
                        GL11.glBegin(       GL11.GL_POLYGON                                 );

                        //set face normal
                        if ( faceNormal != null ) GL11.glNormal3f( faceNormal.x, faceNormal.y, faceNormal.z );

                        //draw all vertices
                        for ( LibVertex currentVertex : verticesToDraw )
                        {
                            this.drawTexturedVertex( currentVertex );
                        }
                        GL11.glEnd();

                        //disable blending
                        GL11.glDisable(         GL11.GL_BLEND                                   );

                        this.lastOpaqueTexture = null;

                        break;
                    }

                    case EOpaque:
                    {
                        //ShooterDebug.bugfix.out( "draw tex " + texture.getId() );

                        //texture
                        GL11.glEnable(          GL11.GL_TEXTURE_2D                        );      //enable texture-mapping

                        //cache last texture setting
                        if (this.lastOpaqueTexture == null || this.lastOpaqueTexture.getId() != texture.getId() )
                        {
                            GL11.glBindTexture(     GL11.GL_TEXTURE_2D, texture.getId()       );      //bind face's texture
                        }
                        this.lastOpaqueTexture = texture;

                        //set face color ( should be white ! )
                        GL11.glColor3f( col[ 0 ], col[ 1 ], col[ 2 ] );

                        if ( alpha != 1.0f )
                        {
                            GL11.glEnable(      GL11.GL_BLEND                                   );
                            GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA  );
                            GL11.glColor4f(     1.0f, 1.0f, 1.0f, alpha                         );
                        }

                        if ( darken != 1.0f )
                        {
                            GL11.glEnable(      GL11.GL_BLEND                                   );
                            GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA  );
                            GL11.glColor4f(     darken, darken, darken, 1.0f   );
                        }

                        //draw all vertices
                        GL11.glBegin(         GL11.GL_POLYGON   );

                        //set face normal
                        if ( faceNormal != null ) GL11.glNormal3f( faceNormal.x, faceNormal.y, faceNormal.z );

                        //draw all vertices
                        for ( LibVertex currentVertex : verticesToDraw )
                        {
                            this.drawTexturedVertex( currentVertex );
                        }
                        GL11.glEnd();

                        if ( alpha != 1.0f || darken != 1.0f )
                        {
                            GL11.glDisable( GL11.GL_BLEND );
                        }

                        break;
                    }
                }
            }
        }

        public final void drawVertices( LibVertex[] vertices )
        {
            for ( LibVertex vertex : vertices )
            {
                this.drawVertex( vertex );
            }
        }

        private void drawVertex(LibVertex v)
        {
            GL11.glVertex3f( v.x, v.z, v.y   );
        }

        private void drawTexturedVertex(LibVertex v)
        {
            GL11.glTexCoord2f( v.u, v.v );
            GL11.glVertex3f(   v.x, v.z, v.y   );
        }

        public final void initTextures( LibGLTextureImage[] texImages )
        {
            GL11.glGenTextures();

            for (int i = 0; i < texImages.length; ++i )
            {
                GL11.glBindTexture( GL11.GL_TEXTURE_2D, i );
                this.makeRGBTexture( texImages[ i ] );

                //this line disabled the lights on textures ! do NOT uncomment it !!
                //GL11.glTexEnvf(         GL11.GL_TEXTURE_ENV,  GL11.GL_TEXTURE_ENV_MODE,     GL11.GL_REPLACE   );
            }
        }

        /****************************************************************************************************************************************
        *   information about the data rewinding can be found at
        *   http://www.experts-exchange.com/Programming/Languages/Java/Q_22397090.html?sfQueryTermInfo=1+jogl
        ***************************************************************************************************************/
        private void makeRGBTexture(LibGLTextureImage img )
        {
            //bind texture to gl
            GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR ); // GL_NEAREST is also possible ..
            GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR );
            GL11.glTexImage2D(    GL11.GL_TEXTURE_2D, 0, this.getSrcPixelFormat( img.srcPixelFormat ), img.width, img.height, 0, this.getSrcPixelFormat( img.srcPixelFormat ), GL11.GL_UNSIGNED_BYTE, img.bytes );
        }

        public void clearGl( LibColors clearCol )
        {
            //clear the gl
            GL11.glClearColor(  clearCol.f3[ 0 ], clearCol.f3[ 1 ], clearCol.f3[ 2 ], 0.5f      );
            GL11.glClear(       GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT             );
        }

        private void flushGL()
        {
            //force all drawing
            GL11.glFlush();
        }

        public void drawOrthoBitmapBytes(LibGLTextureImage glImage, int x, int y, float alpha, float scaleX, float scaleY, boolean translateAnk )
        {
            //prepare rendering 2D
            this.enableOrtho();

            //be sure to disable texturing - bytes will not be drawn otherwise
            GL11.glDisable( GL11.GL_TEXTURE_2D );

            //blending allows transparent pixels
            GL11.glEnable(  GL11.GL_BLEND );

            //must have :( use higher quality alpha pixel pruning if full alpha
            if ( alpha == 1.0f )
            {
                //blend transparent alpha values
                GL11.glBlendFunc(   GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
            }
            else
            {
                //disable full alpha pixels - this is the solution we have been looking for a long time :)
                GL11.glEnable( GL11.GL_ALPHA_TEST );
                GL11.glAlphaFunc( GL11.GL_GREATER, 0 );

                //blend images translucent [ why can we mix GL 1.4 and GL 1.1 here and it works ?? ]
                GL11.glBlendFunc(   GL11.GL_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA );
                GL14.glBlendColor( 1.0f, 1.0f, 1.0f, alpha );
            }

            if ( translateAnk )
            {
                x -= ( scaleX - 1.0f ) * glImage.width;
                y -= ( scaleY - 1.0f ) * glImage.height;
            }

            //set and draw pixels - this is a workaround to allow negative coordinates
            GL11.glRasterPos2f( 0, 0 );
            GL11.glBitmap( 0, 0, 0, 0, x, y, glImage.bytes );
            GL11.glPixelZoom( scaleX, scaleY );
            GL11.glDrawPixels( glImage.width, glImage.height, this.getSrcPixelFormat( glImage.srcPixelFormat ), GL11.GL_UNSIGNED_BYTE, glImage.bytes );

            //disable blending
            GL11.glDisable( GL11.GL_BLEND );

            //restore previous perspective and model views
            this.disableOrtho();
        }

        private void enableOrtho()
        {
            // prepare to render in 2D
            GL11.glDisable( GL11.GL_DEPTH_TEST );                                       // so 2D stuff stays on top of 3D scene
            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPushMatrix();                                                        // preserve perspective glView
            GL11.glLoadIdentity();                                                      // clear the perspective matrix
            GL11.glOrtho( 0, Shooter.game.engine.glView.width, 0, Shooter.game.engine.glView.height, -1, 1 );     // turn on 2D
            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPushMatrix();                                                        // Preserve the Modelview Matrix
            GL11.glLoadIdentity();                                                      // clear the Modelview Matrix
        }

        private void disableOrtho()
        {
            // restore the original positions and views
            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glPopMatrix();
            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glPopMatrix();
            GL11.glEnable( GL11.GL_DEPTH_TEST );            // turn Depth Testing back on
        }

        public void setLightsOn( LibGLLight[] lights, LibColors ambient )
        {
            //enable lighting
            GL11.glEnable( GL11.GL_LIGHTING    );

            //disable all single light sources
            GL11.glDisable( GL11.GL_LIGHT0 );
            GL11.glDisable( GL11.GL_LIGHT1 );
            GL11.glDisable( GL11.GL_LIGHT2 );
            GL11.glDisable( GL11.GL_LIGHT3 );
            GL11.glDisable( GL11.GL_LIGHT4 );
            GL11.glDisable( GL11.GL_LIGHT5 );
            GL11.glDisable( GL11.GL_LIGHT6 );
            GL11.glDisable( GL11.GL_LIGHT7 );

            //ShooterDebug.bugfix.out("enable lights !");
            for ( int i = 0; ( i < lights.length && i < 8 ); ++i )
            {
                //enable single light sources
                int lightKey = 0;
                switch ( i )
                {
                    case 0: lightKey = GL11.GL_LIGHT0;  break;      case 1: lightKey = GL11.GL_LIGHT1;  break;
                    case 2: lightKey = GL11.GL_LIGHT2;  break;      case 3: lightKey = GL11.GL_LIGHT3;  break;
                    case 4: lightKey = GL11.GL_LIGHT4;  break;      case 5: lightKey = GL11.GL_LIGHT5;  break;
                    case 6: lightKey = GL11.GL_LIGHT6;  break;      case 7: lightKey = GL11.GL_LIGHT7;  break;
                }

                GL11.glEnable( lightKey );
                this.setLight( lightKey, lights[ i ] );
            }

            //set ambient light
            FloatBuffer buffAmbient  = (FloatBuffer)ByteBuffer.allocateDirect( 16 ).order( ByteOrder.nativeOrder() ).asFloatBuffer().put( ambient.f4 ).flip();
            GL11.glLightModel(  GL11.GL_LIGHT_MODEL_AMBIENT, buffAmbient );

            //effect ?
            GL11.glLightModeli( GL12.GL_LIGHT_MODEL_COLOR_CONTROL, GL12.GL_SEPARATE_SPECULAR_COLOR );

            //enable color material
            GL11.glEnable( GL11.GL_COLOR_MATERIAL );
/*
            float[] materialColor   = { 0.8f, 0.8f, 0.8f, 1.0f };
            ByteBuffer temp5 = ByteBuffer.allocateDirect( 16 );
            temp5.order( ByteOrder.nativeOrder() );
            FloatBuffer materialBuff = (FloatBuffer)temp5.asFloatBuffer().put( materialColor ).flip();

            GL11.glMaterial(      GL11.GL_FRONT,  GL11.GL_AMBIENT_AND_DIFFUSE, materialBuff );
          //GL11.glColorMaterial( GL11.GL_FRONT,  GL11.GL_AMBIENT_AND_DIFFUSE );
*/
/*
            float[] specRef = ambient.f4; //{ 0.8f, 0.8f, 0.8f, 1.0f };
            ByteBuffer temp6 = ByteBuffer.allocateDirect( 16 );
            temp6.order( ByteOrder.nativeOrder() );
            FloatBuffer specRefBuff = (FloatBuffer)temp6.asFloatBuffer().put( specRef ).flip();
*/
          //GL11.glMaterial( GL11.GL_FRONT_AND_BACK,  GL11.GL_AMBIENT_AND_DIFFUSE, materialBuff );
          //GL11.glColorMaterial( GL11.GL_FRONT_AND_BACK,  GL11.GL_AMBIENT_AND_DIFFUSE );
          //GL11.glMaterialfv( GL11.GL_BACK,  GL.GL_AMBIENT_AND_DIFFUSE, (FloatBuffer)temp5.asFloatBuffer().put( materialColor ).flip() );
        }

        public void setLightsOff()
        {
            //disable lights and color material
            GL11.glDisable( GL11.GL_LIGHTING        );
            GL11.glDisable( GL11.GL_COLOR_MATERIAL  );
        }

        private void setLight(int lightKey, LibGLLight light)
        {
            //enable light
            GL11.glEnable( lightKey );

            //set position                 x,               z,              y,              w       // w=1.0f: x y z describe the position, w=0.0f: x y z describe the axis direction - inoperative
            float[] lightPosition      = { light.ank.x,    light.ank.z,   light.ank.y,   1.0f    };

          //float[] lightAmbient       = { 0.0f, 0.0f, 0.0f,  1.0f, };

            float[] lightSpotDirection  = { LibMath.sinDeg( light.rotZ), 0.0f, LibMath.cosDeg( light.rotZ), 0.0f, };

            FloatBuffer buffPosition      = (FloatBuffer)ByteBuffer.allocateDirect( 16 ).order( ByteOrder.nativeOrder() ).asFloatBuffer().put( lightPosition            ).flip();
            FloatBuffer buffSpotDirection = (FloatBuffer)ByteBuffer.allocateDirect( 16 ).order( ByteOrder.nativeOrder() ).asFloatBuffer().put( lightSpotDirection       ).flip();
            FloatBuffer buffDiffuse       = (FloatBuffer)ByteBuffer.allocateDirect( 16 ).order( ByteOrder.nativeOrder() ).asFloatBuffer().put( light.colDiffuse.f4     ).flip();
            FloatBuffer buffSpecular      = (FloatBuffer)ByteBuffer.allocateDirect( 16 ).order( ByteOrder.nativeOrder() ).asFloatBuffer().put( light.colDiffuse.f4     ).flip();

            GL11.glLight(  lightKey, GL11.GL_POSITION, buffPosition );
            GL11.glLight(  lightKey, GL11.GL_DIFFUSE,  buffDiffuse  );
            GL11.glLight(  lightKey, GL11.GL_SPECULAR, buffSpecular );

          //GL11.glLight(  glKey, GL11.GL_EMISSION, (FloatBuffer)temp4.asFloatBuffer().put( lightSpecular ).flip() );
          //GL11.glLight(  glKey, GL11.GL_AMBIENT,  (FloatBuffer)temp .asFloatBuffer().put( lightAmbient  ).flip() );

            //spot size and direction
            GL11.glLightf(  lightKey,  GL11.GL_SPOT_CUTOFF,    light.spotCutoff);
            GL11.glLight( lightKey,  GL11.GL_SPOT_DIRECTION, buffSpotDirection    );

            GL11.glMaterial( GL11.GL_FRONT, GL11.GL_DIFFUSE,    buffDiffuse  );
            GL11.glMaterial( GL11.GL_FRONT, GL11.GL_SPECULAR,   buffSpecular );

          //GL11.glMaterial( GL11.GL_FRONT, GL11.GL_SHININESS, 96.0f );
        }

        private int getSrcPixelFormat(SrcPixelFormat spf)
        {
            switch ( spf )
            {
                case ERGB:      return GL11.GL_RGB;
                case ERGBA:     return GL11.GL_RGBA;
            }

            return 0;
        }

        public void setNewGluFaceAngle( float faceAngle )
        {
            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glLoadIdentity();
            GLU.gluPerspective( faceAngle, this.aspectRatio, VIEW_MIN, VIEW_MAX );
        }
    }
