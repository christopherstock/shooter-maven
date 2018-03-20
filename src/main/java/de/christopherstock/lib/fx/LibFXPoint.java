
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.gl.LibGLView.Align3D;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /**************************************************************************************
    *   One particle point of any effect.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
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
            iDebug              = aDebug;
            iBaseZ              = aBaseZ;
            iType               = aType;
            iPoint              = new LibVertex( aX, aY, aZ, 0.0f, 1.0f );
            iColor              = aColor;
            iStartAngle         = aStartAngle;
            iCurrentTick        = 0;
            iLifetime           = aLifetime;
            iDelayTicksBefore   = aDelayTicks;
            iGravity            = aGravity;
            iAlign3D            = Align3D.values()[ LibMath.getRandom( 0, Align3D.values().length - 1 )  ];
            iRotationAlign      = Align3D.values()[ LibMath.getRandom( 0, Align3D.values().length - 1 )  ];
            iFadeOutTicks       = aFadeOutTicks;

            //assign Sprite
            iSprite             = aSprite;
            if ( iSprite != null )
            {
                iSprite.translate( 0.0f, 0.0f, -( iSprite.getCenterZ() - iPoint.z ), LibTransformationMode.EOriginalsToOriginals );
            }

            //switch type specific
            switch ( aType )
            {
                case EStaticDebugPoint:
                {
                    iPointSize      = 0.01f;    //debug point size
                    break;
                }

                case ESliver:
                {
                    iSpeedModified   = 0.0f;

                    switch ( iGravity )
                    {
                        case ELow:
                        {
                            iSpeedZ         = 0.00001f * LibMath.getRandom( 1, 3 );
                            iSpeedXY        = 0.0015f  * LibMath.getRandom( 2, 25 );
                            break;
                        }
                        case ENormal:
                        {
                            iSpeedZ         = 0.00008f * LibMath.getRandom( 1, 3 );
                            iSpeedXY        = 0.0030f  * LibMath.getRandom( 2, 25 );
                            break;
                        }
                        case EHigh:
                        {
                            iSpeedZ         = 0.00012f * LibMath.getRandom( 1, 3 );
                            iSpeedXY        = 0.0045f  * LibMath.getRandom( 2, 25 );
                            break;
                        }
                    }

                    switch ( size )
                    {
                        case ESmall:
                        {
                            iPointSize       = 0.003f * LibMath.getRandom( 8, 12 );
                            break;
                        }
                        case EMedium:
                        {
                            iPointSize       = 0.003f * LibMath.getRandom( 10, 15 );
                            break;
                        }
                        case ELarge:
                        {
                            iPointSize       = 0.003f * LibMath.getRandom( 12, 18 );
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
                            iSpeedXY        = 0.0001f    * LibMath.getRandom( 1,  5  );
                            iSpeedModified  = 0.00001f   * LibMath.getRandom( 5,  45 );
                            iSpeedZ         = 0.000001f  * LibMath.getRandom( 10, 90 );
                            iPointSize      = 0.001f     * LibMath.getRandom( 5,  15 );
                            break;
                        }
                        case EMedium:
                        {
                            iSpeedXY        = 0.0003f    * LibMath.getRandom( 1,  5  );
                            iSpeedModified  = 0.00001f   * LibMath.getRandom( 5,  45 );
                            iSpeedZ         = 0.000002f  * LibMath.getRandom( 10, 90 );
                            iPointSize      = 0.001f     * LibMath.getRandom( 10, 20 );
                            break;
                        }
                        case ELarge:
                        {
                            iSpeedXY        = 0.0008f    * LibMath.getRandom( 1,  5  );
                            iSpeedModified  = 0.00001f   * LibMath.getRandom( 5,  45 );
                            iSpeedZ         = 0.000003f  * LibMath.getRandom( 10, 90 );
                            iPointSize      = 0.001f     * LibMath.getRandom( 15, 25 );
                            break;
                        }
                    }
                    break;
                }
            }
        }

        protected final void animate()
        {
            if ( iDelayTicksBefore > 0)
            {
                --iDelayTicksBefore;
            }
            else
            {
                switch( iType )
                {
                    case EStaticDebugPoint:
                    {
                        ++iCurrentTick;


                        break;
                    }

                    case ESliver:
                    {
                        if ( iPoint.z <= iBaseZ && iCurrentTick > iLifetime / 10 )
                        {
                            iPoint.z = iBaseZ;
                        }
                        else
                        {
                            float zbase = iCurrentTick; // - FxSettings.LIVETIME_SLIVER / 10 );
                            iPoint.x -= iSpeedXY * LibMath.sinDeg( iStartAngle );
                            iPoint.y -= iSpeedXY * LibMath.cosDeg( iStartAngle );
                            iPoint.z -= zbase * zbase * iSpeedZ;

                            iRotation     += 5.0f;
                            if ( iRotation >= 360.0f ) iRotation = 0.0f;

                            //clip z on the floor!
                            if ( iPoint.z <= iBaseZ )
                            {
                                iPoint.z = iBaseZ;

                                //playing sound on dropping to the floor will lag the game (solve!) :/
                                //Sound toPlay = Sound.values()[ LibMath.getRandom( Sound.ERubble1.ordinal(), Sound.ERubble3.ordinal() ) ];
                                //toPlay.playDistancedFx( new Point2D.Float( iPoint.x, iPoint.y ) );
                            }
                        }

                        //increase tick-counter, current speed and rotation
                        ++iCurrentTick;
                        iSpeedXY += iSpeedModified;

                        break;
                    }

                    case EExplosion:
                    {
                        if ( iCurrentTick < iLifetime / 5 )
                        {
                            //raise
                            float x = iLifetime / 5 - iCurrentTick;
                            iPoint.x += iSpeedXY * LibMath.sinDeg( iStartAngle );
                            iPoint.y += iSpeedXY * LibMath.cosDeg( iStartAngle );
                            iPoint.z += x * x * iSpeedZ;

                            iRotation     += 10.0f;
                            if ( iRotation >= 360.0f ) iRotation = 0.0f;
                        }
                        else
                        {
                            //fall
                            if ( iPoint.z <= iBaseZ )
                            {
                                iPoint.z = iBaseZ;
                            }
                            else
                            {
                                float x = iCurrentTick - iLifetime / 5;
                                iPoint.x += iSpeedXY * LibMath.sinDeg( iStartAngle );
                                iPoint.y += iSpeedXY * LibMath.cosDeg( iStartAngle );
                                iPoint.z -= x * x * iSpeedZ;

                                //clip z on the floor!
                                iRotation     += 5.0f;
                                if ( iRotation >= 360.0f ) iRotation = 0.0f;
                            }
                        }

                        //increase tick-counter, current speed and rotation
                        ++iCurrentTick;
                        iSpeedXY += iSpeedModified;

                        break;

                    }
                }
            }
        }

        public final void draw( Align3D align3D )
        {
            //if ( s == null ) s = new Sprite( Others.ESprite1, 0.0f, 0.0f, 0.0f, Scalation.ELowerHalf, WallCollidable.ENo, WallTex.ESliver1 );
            //if ( s == null ) s = new Sprite( Others.ESprite1, iPoint.x, iPoint.y, iPoint.z, Scalation.ELowerHalf, WallCollidable.ENo, WallTex.ESliver1 );

            /*
            if ( spriteAnk == null )
            {
                spriteAnk = new LibVertex( iPoint );
            }
            */

            if ( iSprite != null )
            {
                if ( iLastPoint != null )
                {
                    iSprite.translate( iPoint.x - iLastPoint.x, iPoint.y - iLastPoint.y, iPoint.z - iLastPoint.z, LibTransformationMode.EOriginalsToOriginals );
                }
            }

            LibFaceQuad face = null;

            switch ( align3D )
            {
                case AXIS_Z:
                {
                    face = new LibFaceQuad
                    (
                        iDebug,
                        new LibVertex( iPoint.x, iPoint.y, iPoint.z ),
                        new LibVertex( iPoint.x - iPointSize, iPoint.y - iPointSize, iPoint.z ),
                        new LibVertex( iPoint.x - iPointSize, iPoint.y + iPointSize, iPoint.z ),
                        new LibVertex( iPoint.x + iPointSize, iPoint.y + iPointSize, iPoint.z ),
                        new LibVertex( iPoint.x + iPointSize, iPoint.y - iPointSize, iPoint.z ),
                        iColor
                    );
                    break;
                }

                case AXIS_X:
                {
                    face = new LibFaceQuad
                    (
                        iDebug,
                        new LibVertex( iPoint.x, iPoint.y, iPoint.z ),
                        new LibVertex( iPoint.x, iPoint.y - iPointSize, iPoint.z - iPointSize ),
                        new LibVertex( iPoint.x, iPoint.y + iPointSize, iPoint.z - iPointSize ),
                        new LibVertex( iPoint.x, iPoint.y + iPointSize, iPoint.z + iPointSize ),
                        new LibVertex( iPoint.x, iPoint.y - iPointSize, iPoint.z + iPointSize ),
                        iColor
                    );
                    break;
                }

                case AXIS_Y:
                default:
                {
                    face = new LibFaceQuad
                    (
                        iDebug,
                        new LibVertex( iPoint.x, iPoint.y, iPoint.z ),
                        new LibVertex( iPoint.x - iPointSize, iPoint.y, iPoint.z - iPointSize ),
                        new LibVertex( iPoint.x + iPointSize, iPoint.y, iPoint.z - iPointSize ),
                        new LibVertex( iPoint.x + iPointSize, iPoint.y, iPoint.z + iPointSize ),
                        new LibVertex( iPoint.x - iPointSize, iPoint.y, iPoint.z + iPointSize ),
                        iColor
                    );
                    break;
                }
            }

            switch ( iRotationAlign )
            {
                case AXIS_X:
                {
                    face.translateAndRotateXYZ( new LibMatrix( iRotation, 0.0f, 0.0f ), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }

                case AXIS_Y:
                {
                    face.translateAndRotateXYZ( new LibMatrix( 0.0f, iRotation, 0.0f ), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }

                case AXIS_Z:
                {
                    face.translateAndRotateXYZ( new LibMatrix( 0.0f, 0.0f, iRotation ), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }
            }

            //fade out
            if ( iCurrentTick > iLifetime - iFadeOutTicks )
            {
              //ShooterDebug.bugfix.out( "FADE OUT FACE POINT .. [" + iCurrentTick + "]" );
              //face.fadeOut( 0.5f ); //1.0f / iLifetime - iFadeOutTicks );

                float a = iCurrentTick - ( iLifetime - iFadeOutTicks );
                float b = iFadeOutTicks;

                face.fadeOut( ( a / b ) );
            }

            //draw face
            face.draw();

            //draw sprite ..
            if ( iSprite != null )
            {
                iSprite.animateSprite( null );
                iSprite.draw();
                iLastPoint = new LibVertex( iPoint );
            }
        }

        protected boolean isLifetimeOver()
        {
            return ( iCurrentTick >= iLifetime );
        }

        protected boolean isDelayedBefore()
        {
            return ( iDelayTicksBefore > 0 );
        }

        protected FXType getType()
        {
            return iType;
        }
    }
