
    package de.christopherstock.lib.fx;

    import de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.gl.LibGLView.Align3D;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   One particle point of any effect.
    *******************************************************************************************************************/
    class LibFXPoint
    {
        private                     LibSprite           iSprite                         = null;
        private                     LibVertex           iLastPoint                      = null;

        private                     LibVertex           iPoint                          = null;
        public                      float               iPointSize                      = 0.0f;
        private                     int                 iDelayTicksBefore               = 0;
        private                     LibColors           iColor                          = null;

        public                      LibDebug            iDebug                          = null;
        public                      float               iBaseZ                          = 0.0f;
        public                      FXType              iType                           = null;
        public                      Align3D             iAlign3D                        = null;
        private                     Align3D             iRotationAlign                  = null;
        private                     float               iStartAngle                     = 0.0f;
        private                     float               iSpeedZ                         = 0.0f;
        private                     float               iSpeedModified                  = 0.0f;
        private                     float               iSpeedXY                        = 0.0f;
        private                     int                 iLifetime                       = 0;
        private                     int                 iCurrentTick                    = 0;
        private                     float               iRotation                       = 0.0f;
        private                     FXGravity           iGravity                        = null;
        private                     int                 iFadeOutTicks                   = 0;

        protected LibFXPoint( LibDebug aDebug, float aBaseZ, FXType aType, LibColors aColor, float aStartAngle, float aX, float aY, float aZ, FXSize size, int aDelayTicks, int aLifetime, FXGravity aGravity, int aFadeOutTicks, LibSprite aSprite )
        {
            this.iDebug = aDebug;
            this.iBaseZ = aBaseZ;
            this.iType = aType;
            this.iPoint = new LibVertex( aX, aY, aZ, 0.0f, 1.0f );
            this.iColor = aColor;
            this.iStartAngle = aStartAngle;
            this.iCurrentTick = 0;
            this.iLifetime = aLifetime;
            this.iDelayTicksBefore = aDelayTicks;
            this.iGravity = aGravity;
            this.iAlign3D = Align3D.values()[ LibMath.getRandom( 0, Align3D.values().length - 1 )  ];
            this.iRotationAlign = Align3D.values()[ LibMath.getRandom( 0, Align3D.values().length - 1 )  ];
            this.iFadeOutTicks = aFadeOutTicks;

            //assign Sprite
            this.iSprite = aSprite;
            if (this.iSprite != null )
            {
                this.iSprite.translate( 0.0f, 0.0f, -(this.iSprite.getCenterZ() - this.iPoint.z ), LibTransformationMode.EOriginalsToOriginals );
            }

            //switch type specific
            switch ( aType )
            {
                case EStaticDebugPoint:
                {
                    this.iPointSize = 0.01f;    //debug point size
                    break;
                }

                case ESliver:
                {
                    this.iSpeedModified = 0.0f;

                    switch (this.iGravity)
                    {
                        case ELow:
                        {
                            this.iSpeedZ = 0.00001f * LibMath.getRandom( 1, 3 );
                            this.iSpeedXY = 0.0015f  * LibMath.getRandom( 2, 25 );
                            break;
                        }
                        case ENormal:
                        {
                            this.iSpeedZ = 0.00008f * LibMath.getRandom( 1, 3 );
                            this.iSpeedXY = 0.0030f  * LibMath.getRandom( 2, 25 );
                            break;
                        }
                        case EHigh:
                        {
                            this.iSpeedZ = 0.00012f * LibMath.getRandom( 1, 3 );
                            this.iSpeedXY = 0.0045f  * LibMath.getRandom( 2, 25 );
                            break;
                        }
                    }

                    switch ( size )
                    {
                        case ESmall:
                        {
                            this.iPointSize = 0.003f * LibMath.getRandom( 8, 12 );
                            break;
                        }
                        case EMedium:
                        {
                            this.iPointSize = 0.003f * LibMath.getRandom( 10, 15 );
                            break;
                        }
                        case ELarge:
                        {
                            this.iPointSize = 0.003f * LibMath.getRandom( 12, 18 );
                            break;
                        }
                    }

                    break;
                }

                case EExplosion:
                {
                    switch ( size )
                    {
                        case ESmall:
                        {
                            this.iSpeedXY = 0.0001f    * LibMath.getRandom( 1,  5  );
                            this.iSpeedModified = 0.00001f   * LibMath.getRandom( 5,  45 );
                            this.iSpeedZ = 0.000001f  * LibMath.getRandom( 10, 90 );
                            this.iPointSize = 0.001f     * LibMath.getRandom( 5,  15 );
                            break;
                        }
                        case EMedium:
                        {
                            this.iSpeedXY = 0.0003f    * LibMath.getRandom( 1,  5  );
                            this.iSpeedModified = 0.00001f   * LibMath.getRandom( 5,  45 );
                            this.iSpeedZ = 0.000002f  * LibMath.getRandom( 10, 90 );
                            this.iPointSize = 0.001f     * LibMath.getRandom( 10, 20 );
                            break;
                        }
                        case ELarge:
                        {
                            this.iSpeedXY = 0.0008f    * LibMath.getRandom( 1,  5  );
                            this.iSpeedModified = 0.00001f   * LibMath.getRandom( 5,  45 );
                            this.iSpeedZ = 0.000003f  * LibMath.getRandom( 10, 90 );
                            this.iPointSize = 0.001f     * LibMath.getRandom( 15, 25 );
                            break;
                        }
                    }
                    break;
                }
            }
        }

        protected final void animate()
        {
            if (this.iDelayTicksBefore > 0)
            {
                --this.iDelayTicksBefore;
            }
            else
            {
                switch(this.iType)
                {
                    case EStaticDebugPoint:
                    {
                        ++this.iCurrentTick;


                        break;
                    }

                    case ESliver:
                    {
                        if (this.iPoint.z <= this.iBaseZ && this.iCurrentTick > this.iLifetime / 10 )
                        {
                            this.iPoint.z = this.iBaseZ;
                        }
                        else
                        {
                            float zbase = this.iCurrentTick; // - FxSettings.LIVETIME_SLIVER / 10 );
                            this.iPoint.x -= this.iSpeedXY * LibMath.sinDeg(this.iStartAngle);
                            this.iPoint.y -= this.iSpeedXY * LibMath.cosDeg(this.iStartAngle);
                            this.iPoint.z -= zbase * zbase * this.iSpeedZ;

                            this.iRotation += 5.0f;
                            if (this.iRotation >= 360.0f ) this.iRotation = 0.0f;

                            //clip z on the floor!
                            if (this.iPoint.z <= this.iBaseZ)
                            {
                                this.iPoint.z = this.iBaseZ;

                                //playing sound on dropping to the floor will lag the game (solve!) :/
                                //Sound toPlay = Sound.values()[ LibMath.getRandom( Sound.ERubble1.ordinal(), Sound.ERubble3.ordinal() ) ];
                                //toPlay.playDistancedFx( new Point2D.Float( iPoint.x, iPoint.y ) );
                            }
                        }

                        //increase tick-counter, current speed and rotation
                        ++this.iCurrentTick;
                        this.iSpeedXY += this.iSpeedModified;

                        break;
                    }

                    case EExplosion:
                    {
                        if (this.iCurrentTick < this.iLifetime / 5 )
                        {
                            //raise
                            float x = this.iLifetime / 5 - this.iCurrentTick;
                            this.iPoint.x += this.iSpeedXY * LibMath.sinDeg(this.iStartAngle);
                            this.iPoint.y += this.iSpeedXY * LibMath.cosDeg(this.iStartAngle);
                            this.iPoint.z += x * x * this.iSpeedZ;

                            this.iRotation += 10.0f;
                            if (this.iRotation >= 360.0f ) this.iRotation = 0.0f;
                        }
                        else
                        {
                            //fall
                            if (this.iPoint.z <= this.iBaseZ)
                            {
                                this.iPoint.z = this.iBaseZ;
                            }
                            else
                            {
                                float x = this.iCurrentTick - this.iLifetime / 5;
                                this.iPoint.x += this.iSpeedXY * LibMath.sinDeg(this.iStartAngle);
                                this.iPoint.y += this.iSpeedXY * LibMath.cosDeg(this.iStartAngle);
                                this.iPoint.z -= x * x * this.iSpeedZ;

                                //clip z on the floor!
                                this.iRotation += 5.0f;
                                if (this.iRotation >= 360.0f ) this.iRotation = 0.0f;
                            }
                        }

                        //increase tick-counter, current speed and rotation
                        ++this.iCurrentTick;
                        this.iSpeedXY += this.iSpeedModified;

                        break;

                    }
                }
            }
        }

        public final void draw( Align3D align3D )
        {
            //if ( s == null ) s = new Sprite( Others.ESprite1, 0.0f, 0.0f, 0.0f, LibScalation.ELowerHalf, WallCollidable.ENo, WallTex.ESliver1 );
            //if ( s == null ) s = new Sprite( Others.ESprite1, iPoint.x, iPoint.y, iPoint.z, LibScalation.ELowerHalf, WallCollidable.ENo, WallTex.ESliver1 );

            /*
            if ( spriteAnk == null )
            {
                spriteAnk = new LibVertex( iPoint );
            }
            */

            if (this.iSprite != null )
            {
                if (this.iLastPoint != null )
                {
                    this.iSprite.translate(this.iPoint.x - this.iLastPoint.x, this.iPoint.y - this.iLastPoint.y, this.iPoint.z - this.iLastPoint.z, LibTransformationMode.EOriginalsToOriginals );
                }
            }

            LibFaceQuad face = null;

            switch ( align3D )
            {
                case AXIS_Z:
                {
                    face = new LibFaceQuad
                    (
                            this.iDebug,
                        new LibVertex(this.iPoint.x, this.iPoint.y, this.iPoint.z ),
                        new LibVertex(this.iPoint.x - this.iPointSize, this.iPoint.y - this.iPointSize, this.iPoint.z ),
                        new LibVertex(this.iPoint.x - this.iPointSize, this.iPoint.y + this.iPointSize, this.iPoint.z ),
                        new LibVertex(this.iPoint.x + this.iPointSize, this.iPoint.y + this.iPointSize, this.iPoint.z ),
                        new LibVertex(this.iPoint.x + this.iPointSize, this.iPoint.y - this.iPointSize, this.iPoint.z ),
                            this.iColor
                    );
                    break;
                }

                case AXIS_X:
                {
                    face = new LibFaceQuad
                    (
                            this.iDebug,
                        new LibVertex(this.iPoint.x, this.iPoint.y, this.iPoint.z ),
                        new LibVertex(this.iPoint.x, this.iPoint.y - this.iPointSize, this.iPoint.z - this.iPointSize),
                        new LibVertex(this.iPoint.x, this.iPoint.y + this.iPointSize, this.iPoint.z - this.iPointSize),
                        new LibVertex(this.iPoint.x, this.iPoint.y + this.iPointSize, this.iPoint.z + this.iPointSize),
                        new LibVertex(this.iPoint.x, this.iPoint.y - this.iPointSize, this.iPoint.z + this.iPointSize),
                            this.iColor
                    );
                    break;
                }

                case AXIS_Y:
                default:
                {
                    face = new LibFaceQuad
                    (
                            this.iDebug,
                        new LibVertex(this.iPoint.x, this.iPoint.y, this.iPoint.z ),
                        new LibVertex(this.iPoint.x - this.iPointSize, this.iPoint.y, this.iPoint.z - this.iPointSize),
                        new LibVertex(this.iPoint.x + this.iPointSize, this.iPoint.y, this.iPoint.z - this.iPointSize),
                        new LibVertex(this.iPoint.x + this.iPointSize, this.iPoint.y, this.iPoint.z + this.iPointSize),
                        new LibVertex(this.iPoint.x - this.iPointSize, this.iPoint.y, this.iPoint.z + this.iPointSize),
                            this.iColor
                    );
                    break;
                }
            }

            switch (this.iRotationAlign)
            {
                case AXIS_X:
                {
                    face.translateAndRotateXYZ( new LibMatrix(this.iRotation, 0.0f, 0.0f ), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }

                case AXIS_Y:
                {
                    face.translateAndRotateXYZ( new LibMatrix( 0.0f, this.iRotation, 0.0f ), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }

                case AXIS_Z:
                {
                    face.translateAndRotateXYZ( new LibMatrix( 0.0f, 0.0f, this.iRotation), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }
            }

            //fade out
            if (this.iCurrentTick > this.iLifetime - this.iFadeOutTicks)
            {
              //ShooterDebug.bugfix.out( "FADE OUT FACE POINT .. [" + iCurrentTick + "]" );
              //face.fadeOut( 0.5f ); //1.0f / iLifetime - iFadeOutTicks );

                float a = this.iCurrentTick - (this.iLifetime - this.iFadeOutTicks);
                float b = this.iFadeOutTicks;

                face.fadeOut( ( a / b ) );
            }

            //draw face
            face.draw();

            //draw sprite ..
            if (this.iSprite != null )
            {
                this.iSprite.animateSprite( null );
                this.iSprite.draw();
                this.iLastPoint = new LibVertex(this.iPoint);
            }
        }

        protected boolean isLifetimeOver()
        {
            return (this.iCurrentTick >= this.iLifetime);
        }

        protected boolean isDelayedBefore()
        {
            return (this.iDelayTicksBefore > 0 );
        }

        protected FXType getType()
        {
            return this.iType;
        }
    }
