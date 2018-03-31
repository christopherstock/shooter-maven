
    package de.christopherstock.lib.g3d.face;

    import  java.io.Serializable;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.Shooter;

    /*******************************************************************************************************************
    *   Represents a face with an anchor and a various number of vertices that define the polygon.
    *******************************************************************************************************************/
    public abstract class LibFace implements LibGeomObject, Serializable
    {
        public static enum DrawMethod
        {
            //Do not draw this face if it is too distant.
            //EHideIfTooDistant,

            /** Always draw these faces. */
            EAlwaysDraw,

            /** For hidden level bounds etc. */
            EInvisible,
            ;
        }

        protected                   LibDebug                debug                       = null;

        private                     LibVertex               anchor                      = null;
        private                     LibGLTextureMetaData    texture                     = null;
        private                     LibColors               color                       = null;

        private                     LibVertex               normal                      = null;

        public                      LibVertex[]             originalVertices            = null;
        public                      LibVertex[]             transformedVertices         = null;

        protected                   float                   faceAngleHorz               = 0.0f;
        protected                   float                   faceAngleVert               = 0.0f;
        protected                   DrawMethod              drawMethod                  = null;
        private                     float                   alpha                       = 1.0f;
        private                     float                   darkenOpacity               = 1.0f;

        /***************************************************************************************************************
        *   Constructs a new face.
        *
        *   @param  debug       The debug instance.
        *   @param  anchor      The anchor for this face.
        *   @param  texture     The texture to use. May be <code>null</code>.
        *   @param  color       The color for this face. May be <code>null</code>.
        ***************************************************************************************************************/
        protected LibFace( LibDebug debug, LibVertex anchor, LibGLTextureMetaData texture, LibColors color, LibVertex faceNormal )
        {
            this.debug = debug;
            this.anchor = anchor;
            this.texture = texture; //( aTexture == null ? Default.EStones1.texture : aTexture  );
            this.color = ( color == null ? LibColors.EWhite : color );

            this.normal = faceNormal;

            this.drawMethod = DrawMethod.EAlwaysDraw;
        }

        protected void updateCollisionValues()
        {
            //update normal
            this.updateFaceNormal();

            //call specific implementation
            this.setCollisionValues();
        }

        private void updateFaceNormal()
        {
            //this is the best algo although it does not seem to operate flawless :/
            LibVertex a = new LibVertex( 0.0f, 0.0f, 0.0f );
            LibVertex b = new LibVertex( 0.0f, 0.0f, 0.0f );

            //calculate the vectors A and B - note that v[3] is defined with counterclockwise winding in mind (?)
            a.x = this.transformedVertices[ 0 ].x - this.transformedVertices[ 1 ].x;
            a.y = this.transformedVertices[ 0 ].y - this.transformedVertices[ 1 ].y;
            a.z = this.transformedVertices[ 0 ].z - this.transformedVertices[ 1 ].z;

            b.x = this.transformedVertices[ 1 ].x - this.transformedVertices[ 2 ].x;
            b.y = this.transformedVertices[ 1 ].y - this.transformedVertices[ 2 ].y;
            b.z = this.transformedVertices[ 1 ].z - this.transformedVertices[ 2 ].z;

            //calculate the cross product and place the resulting vector into the address specified by vertex_t *normal
            this.normal = new LibVertex( 0.0f, 0.0f, 0.0f );
            this.normal.x = ( a.y * b.z ) - ( a.z * b.y );
            this.normal.y = ( a.z * b.x ) - ( a.x * b.z );
            this.normal.z = ( a.x * b.y ) - ( a.y * b.x );
        }

        protected abstract void setCollisionValues();

        public final void setNewAnchor( LibVertex newAnchor, boolean performTranslationOnFaces, LibTransformationMode transformationMode )
        {
            this.anchor = newAnchor;
            if ( performTranslationOnFaces )
            {
                this.translate(this.anchor.x, this.anchor.y, this.anchor.z, transformationMode );
            }
        }

        /***************************************************************************************************************
        *   Translates the ORIGINAL vertices setting the TRANSFORMED vertices.
        *
        *   @param  x  The x-modification value to translate all original vertices for.
        *   @param  y  The y-modification value to translate all original vertices for.
        *   @param  z  The z-modification value to translate all original vertices for.
        ***************************************************************************************************************/
        public void translate(float x, float y, float z, LibTransformationMode transformationMode )
        {
            LibVertex[] newTransformedVertices  = new LibVertex[this.originalVertices.length ];
            LibVertex[] srcVertices             = null;

            //choose source
            switch ( transformationMode )
            {
                case ETransformedToTransformed:
                {
                    srcVertices = this.transformedVertices;
                    break;
                }

                case EOriginalsToOriginals:
                case EOriginalsToTransformed:
                default:
                {
                    srcVertices = this.originalVertices;
                    break;
                }
            }

            //translate all original vertices
            for ( int i = 0; i < srcVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                newTransformedVertices[ i ] = new LibVertex
                (
                    srcVertices[ i ].x + x,
                    srcVertices[ i ].y + y,
                    srcVertices[ i ].z + z,
                    srcVertices[ i ].u,
                    srcVertices[ i ].v
                );
            }

            //choose destination
            switch ( transformationMode )
            {
                case EOriginalsToOriginals:
                {
                    this.originalVertices = newTransformedVertices;
                    this.transformedVertices = newTransformedVertices;
                    break;
                }

                case EOriginalsToTransformed:
                case ETransformedToTransformed:
                {
                    this.transformedVertices = newTransformedVertices;
                    break;
                }
            }

            //update collision values
            this.updateCollisionValues();
        }

        /***************************************************************************************************************
        *   Rotates the TRANSFORMED vertices setting the TRANSFORMED vertices.
        *   LibRotation operations shall always be performed after translation operations.
        *
        *   @param  transMatrix     The transformation-matrix to turn all vertices around.
        *   @param  tX              The amount to translate this vertex on the x-axis.
        *   @param  tY              The amount to translate this vertex on the y-axis.
        *   @param  tZ              The amount to translate this vertex on the z-axis.
        ***************************************************************************************************************/
        public void translateAndRotateXYZ( LibMatrix transMatrix, float tX, float tY, float tZ, LibTransformationMode transformationMode, LibVertex alternateAnchor )
        {
            //translate all original vertices
            this.translate( tX, tY, tZ, transformationMode );

            //rotate all transformed vertices
            transMatrix.transformVertices(this.transformedVertices, ( alternateAnchor == null ? this.anchor : alternateAnchor ) );

            //alter originals?
            if ( transformationMode == LibTransformationMode.EOriginalsToOriginals )
            {
                this.originalVertices = this.transformedVertices;
            }

            //update collision values
            this.updateCollisionValues();
        }

        /***************************************************************************************************************
        *   Translates the ORIGINAL vertices setting the TRANSFORMED vertices.
        *
        *   @param  scaleFactor     1.0 performs scalation to equal.
        ***************************************************************************************************************/
        public void scale( float scaleFactor, boolean performOnOriginals )
        {
            //prune old transformed vertices
            this.transformedVertices = new LibVertex[this.originalVertices.length ];

            //translate all original vertices
            for (int i = 0; i < this.originalVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                this.transformedVertices[ i ] = new LibVertex
                (
                        this.originalVertices[ i ].x * scaleFactor,
                        this.originalVertices[ i ].y * scaleFactor,
                        this.originalVertices[ i ].z * scaleFactor,
                        this.originalVertices[ i ].u,
                        this.originalVertices[ i ].v
                );
            }

            //alter originals?
            if ( performOnOriginals )
            {
                this.originalVertices = this.transformedVertices;
            }

            //update collision values
            this.updateCollisionValues();
        }

        /***************************************************************************************************************
        *   Inverts all vertices of this face.
        ***************************************************************************************************************/
        public void invert()
        {
            boolean performOnOriginals = true;

            //prune old transformed vertices
            this.transformedVertices = new LibVertex[this.originalVertices.length ];

            //invert all original vertices
            for (int i = 0; i < this.originalVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                this.transformedVertices[ i ] = new LibVertex
                (
                        this.originalVertices[ i ].x * -1,
                        this.originalVertices[ i ].y,
                        this.originalVertices[ i ].z,
                        this.originalVertices[ i ].u * -1,
                        this.originalVertices[ i ].v
                );
            }

            //alter originals?
            if ( performOnOriginals )
            {
                this.originalVertices = this.transformedVertices;
            }

            //update collision values
            this.updateCollisionValues();
        }

        protected final void setFaceAngleHorz( float aFaceAngleHorz )
        {
            this.faceAngleHorz = aFaceAngleHorz;
        }

        protected final void setFaceAngleVert( float aFaceAngleVert )
        {
            this.faceAngleVert = aFaceAngleVert;
        }

        protected final void setOriginalVertices(LibVertex[] vertices)
        {
            this.originalVertices = vertices;
            this.transformedVertices = vertices;
        }

        public final void mirror( boolean x, boolean y, boolean z )
        {
            //mirror all originals
            for (int i = 0; i < this.originalVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                this.originalVertices[ i ] = new LibVertex
                (
                    ( x ? -1 : 1 ) * this.originalVertices[ i ].x,
                    ( y ? -1 : 1 ) * this.originalVertices[ i ].y,
                    ( z ? -1 : 1 ) * this.originalVertices[ i ].z,
                        this.originalVertices[ i ].u,
                        this.originalVertices[ i ].v
                );
            }

            //assign to transformed too !
            this.transformedVertices = this.originalVertices;
        }

        public final void draw()
        {
            boolean draw = false;
            switch (this.drawMethod)
            {
                case EAlwaysDraw:
                {
                    draw = true;
                    break;
                }
/*
                case EHideIfTooDistant:
                {
                    //takes calc time!
                    //draw = ( getVerticesToDraw()[ 0 ].distance( ShooterGameLevel.currentPlayer().getAnchor() ) <= PlayerSettings.VIEW_DISTANCE );
                    break;
                }
*/
                case EInvisible:
                {
                    draw = false;
                    break;
                }
            }

            //only draw if desired
            if ( draw )
            {
                Shooter.game.engine.glView.enqueueFaceToQueue( this );
            }
        }

        public final LibVertex getFaceNormal()
        {
            return this.normal; //iTransformedNormal; inoperative :(
        }

        public final LibGLTextureMetaData getTexture()
        {
            return this.texture;
        }

        public final float[] getColor3f()
        {
            return this.color.f3;
        }

        protected final LibColors getColor()
        {
            return this.color;
        }

        public final LibVertex[] getVerticesToDraw()
        {
            return this.transformedVertices;
        }

        public final LibVertex[] getOriginalVertices()
        {
            return this.originalVertices;
        }

        public final LibVertex getAnchor()
        {
            return this.anchor;
        }

        public void changeTexture(LibGLTextureMetaData oldTex, LibGLTextureMetaData newTex )
        {
            if (this.texture == oldTex )
            {
                this.texture = newTex;
            }
        }

        public void setDrawMethod( DrawMethod aDrawMethod )
        {
            this.drawMethod = aDrawMethod;
        }

        public void fadeOut( float delta )
        {
            this.alpha -= delta;
            if (this.alpha < 0.0f ) this.alpha = 0.0f;
        }

        public void darken( float opacity )
        {
            this.darkenOpacity = opacity;
            if (this.darkenOpacity > 1.0f ) this.darkenOpacity = 1.0f;
            if (this.darkenOpacity < 0.0f ) this.darkenOpacity = 0.0f;
        }

        public final float getAlpha()
        {
            return this.alpha;
        }

        public final float getDarkenOpacity()
        {
            return this.darkenOpacity;
        }
    }
