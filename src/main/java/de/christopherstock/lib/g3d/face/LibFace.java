
    package de.christopherstock.lib.g3d.face;

    import  java.io.Serializable;
    import  de.christopherstock.lib.LibTransformationMode;
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
        private     static  final   long                serialVersionUID                    = 3855014449381203086L;

        public static enum DrawMethod
        {
            /** Do not draw this face if it is too distant. */
            //EHideIfTooDistant,

            /** Always draw these faces. */
            EAlwaysDraw,

            /** For hidden level bounds etc. */
            EInvisible,
            ;
        }

        public                      LibDebug            iDebug                              = null;

        private                     LibVertex           iAnchor                             = null;
        private                     LibGLTexture        iTexture                            = null;
        private                     LibColors           iColor                              = null;

        protected                   LibVertex           iNormal                             = null;

        public                      LibVertex[]         iOriginalVertices                   = null;
        public                      LibVertex[]         iTransformedVertices                = null;

        protected                   float               iFaceAngleHorz                      = 0.0f;
        protected                   float               iFaceAngleVert                      = 0.0f;
        protected                   DrawMethod          iDrawMethod                         = null;
        public                      float               iAlpha                              = 1.0f;
        public                      float               iDarkenOpacity                             = 1.0f;

        /***************************************************************************************************************
        *   Constructs a new face.
        *
        *   @param  aAnchor      The anchor for this face.
        *   @param  aTexture   The texture to use. May be <code>null</code>.
        *   @param  aColor   The color for this face. May be <code>null</code>.
        ***************************************************************************************************************/
        public LibFace( LibDebug aDebug, LibVertex aAnchor, LibGLTexture aTexture, LibColors aColor, LibVertex aFaceNormal )
        {
            this.iDebug = aDebug;
            this.iAnchor = aAnchor;
            this.iTexture = aTexture; //( aTexture == null ? Default.EStones1.iTexture : aTexture  );
            this.iColor = ( aColor == null ? LibColors.EWhite : aColor );

            this.iNormal = aFaceNormal;

            this.iDrawMethod = DrawMethod.EAlwaysDraw;
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
            a.x = this.iTransformedVertices[ 0 ].x - this.iTransformedVertices[ 1 ].x;
            a.y = this.iTransformedVertices[ 0 ].y - this.iTransformedVertices[ 1 ].y;
            a.z = this.iTransformedVertices[ 0 ].z - this.iTransformedVertices[ 1 ].z;

            b.x = this.iTransformedVertices[ 1 ].x - this.iTransformedVertices[ 2 ].x;
            b.y = this.iTransformedVertices[ 1 ].y - this.iTransformedVertices[ 2 ].y;
            b.z = this.iTransformedVertices[ 1 ].z - this.iTransformedVertices[ 2 ].z;

            //calculate the cross product and place the resulting vector into the address specified by vertex_t *normal
            this.iNormal = new LibVertex( 0.0f, 0.0f, 0.0f );
            this.iNormal.x = ( a.y * b.z ) - ( a.z * b.y );
            this.iNormal.y = ( a.z * b.x ) - ( a.x * b.z );
            this.iNormal.z = ( a.x * b.y ) - ( a.y * b.x );
        }

        protected abstract void setCollisionValues();

        public final void setNewAnchor( LibVertex newAnchor, boolean performTranslationOnFaces, LibTransformationMode transformationMode )
        {
            this.iAnchor = newAnchor;
            if ( performTranslationOnFaces )
            {
                this.translate(this.iAnchor.x, this.iAnchor.y, this.iAnchor.z, transformationMode );
            }
        }

        /***************************************************************************************************************
        *   Translates the ORIGINAL vertices setting the TRANSFORMED vertices.
        *
        *   @param  tX  The x-modification value to translate all original vertices for.
        *   @param  tY  The y-modification value to translate all original vertices for.
        *   @param  tZ  The z-modification value to translate all original vertices for.
        ***************************************************************************************************************/
        public void translate( float tX, float tY, float  tZ, LibTransformationMode transformationMode )
        {
            LibVertex[] newTransformedVertices  = new LibVertex[this.iOriginalVertices.length ];
            LibVertex[] srcVertices             = null;

            //choose source
            switch ( transformationMode )
            {
                case ETransformedToTransformed:
                {
                    srcVertices = this.iTransformedVertices;
                    break;
                }

                case EOriginalsToOriginals:
                case EOriginalsToTransformed:
                default:
                {
                    srcVertices = this.iOriginalVertices;
                    break;
                }
            }

            //translate all original vertices
            for ( int i = 0; i < srcVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                newTransformedVertices[ i ] = new LibVertex
                (
                    srcVertices[ i ].x + tX,
                    srcVertices[ i ].y + tY,
                    srcVertices[ i ].z + tZ,
                    srcVertices[ i ].u,
                    srcVertices[ i ].v
                );
            }

            //choose destination
            switch ( transformationMode )
            {
                case EOriginalsToOriginals:
                {
                    this.iOriginalVertices = newTransformedVertices;
                    this.iTransformedVertices = newTransformedVertices;
                    break;
                }

                case EOriginalsToTransformed:
                case ETransformedToTransformed:
                {
                    this.iTransformedVertices = newTransformedVertices;
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
            transMatrix.transformVertices(this.iTransformedVertices, ( alternateAnchor == null ? this.iAnchor : alternateAnchor ) );

            //alter originals?
            if ( transformationMode == LibTransformationMode.EOriginalsToOriginals )
            {
                this.iOriginalVertices = this.iTransformedVertices;
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
            this.iTransformedVertices = new LibVertex[this.iOriginalVertices.length ];

            //translate all original vertices
            for (int i = 0; i < this.iOriginalVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                this.iTransformedVertices[ i ] = new LibVertex
                (
                        this.iOriginalVertices[ i ].x * scaleFactor,
                        this.iOriginalVertices[ i ].y * scaleFactor,
                        this.iOriginalVertices[ i ].z * scaleFactor,
                        this.iOriginalVertices[ i ].u,
                        this.iOriginalVertices[ i ].v
                );
            }

            //alter originals?
            if ( performOnOriginals )
            {
                this.iOriginalVertices = this.iTransformedVertices;
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
            this.iTransformedVertices = new LibVertex[this.iOriginalVertices.length ];

            //invert all original vertices
            for (int i = 0; i < this.iOriginalVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                this.iTransformedVertices[ i ] = new LibVertex
                (
                        this.iOriginalVertices[ i ].x * -1,
                        this.iOriginalVertices[ i ].y,
                        this.iOriginalVertices[ i ].z,
                        this.iOriginalVertices[ i ].u * -1,
                        this.iOriginalVertices[ i ].v
                );
            }

            //alter originals?
            if ( performOnOriginals )
            {
                this.iOriginalVertices = this.iTransformedVertices;
            }

            //update collision values
            this.updateCollisionValues();
        }

        protected final void setFaceAngleHorz( float aFaceAngleHorz )
        {
            this.iFaceAngleHorz = aFaceAngleHorz;
        }

        protected final void setFaceAngleVert( float aFaceAngleVert )
        {
            this.iFaceAngleVert = aFaceAngleVert;
        }

        public final void setOriginalVertices( LibVertex[] vertices )
        {
            this.iOriginalVertices = vertices;
            this.iTransformedVertices = vertices;
        }

        public final void mirror( boolean x, boolean y, boolean z )
        {
            //mirror all originals
            for (int i = 0; i < this.iOriginalVertices.length; ++i )
            {
                //remember to copy u and v and to make a new object!
                this.iOriginalVertices[ i ] = new LibVertex
                (
                    ( x ? -1 : 1 ) * this.iOriginalVertices[ i ].x,
                    ( y ? -1 : 1 ) * this.iOriginalVertices[ i ].y,
                    ( z ? -1 : 1 ) * this.iOriginalVertices[ i ].z,
                        this.iOriginalVertices[ i ].u,
                        this.iOriginalVertices[ i ].v
                );
            }

            //assign to transformed too !
            this.iTransformedVertices = this.iOriginalVertices;
        }

        public final void draw()
        {
            boolean draw = false;
            switch (this.iDrawMethod)
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
                Shooter.game.engine.gl.view.enqueueFaceToQueue( this );
            }
        }

        public final LibVertex getFaceNormal()
        {
            return this.iNormal; //iTransformedNormal; inoperative :(
        }

        public final LibGLTexture getTexture()
        {
            return this.iTexture;
        }

        public final float[] getColor3f()
        {
            return this.iColor.f3;
        }

        public final LibColors getColor()
        {
            return this.iColor;
        }

        public final LibVertex[] getVerticesToDraw()
        {
            return this.iTransformedVertices;
        }

        public final LibVertex[] getOriginalVertices()
        {
            return this.iOriginalVertices;
        }

        public final LibVertex getAnchor()
        {
            return this.iAnchor;
        }

        public void changeTexture( LibGLTexture oldTex, LibGLTexture newTex )
        {
            if (this.iTexture == oldTex )
            {
                this.iTexture = newTex;
            }
        }

        public void setDrawMethod( DrawMethod aDrawMethod )
        {
            this.iDrawMethod = aDrawMethod;
        }

        public void fadeOut( float delta )
        {
            this.iAlpha -= delta;
            if (this.iAlpha < 0.0f ) this.iAlpha = 0.0f;
        }

        public void darken( float opacity )
        {
            this.iDarkenOpacity = opacity;
            if (this.iDarkenOpacity > 1.0f ) this.iDarkenOpacity = 1.0f;
            if (this.iDarkenOpacity < 0.0f ) this.iDarkenOpacity = 0.0f;
        }

        public final float getAlpha()
        {
            return this.iAlpha;
        }

        public final float getDarkenOpacity()
        {
            return this.iDarkenOpacity;
        }
    }
