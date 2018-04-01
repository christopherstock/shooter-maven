
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.gl.LibAlign3D;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   One particle point of any effect.
    *******************************************************************************************************************/
    class LibFXPoint
    {
        private                     LibSprite           sprite                          = null;
        private                     LibVertex           lastPoint                       = null;

        private                     LibVertex           point = null;
        public                      float               pointSize                       = 0.0f;
        private                     int                 delayTicksBefore                = 0;
        private                     LibColors           color                           = null;

        private LibDebug            debug                           = null;
        private                     float               baseZ                           = 0.0f;
        public                      FXType              type                            = null;
        public                      LibAlign3D          align3D                         = null;
        private                     LibAlign3D          rotationAlign                   = null;
        private                     float               startAngle                      = 0.0f;
        private                     float               speedZ                          = 0.0f;
        private                     float               speedModified                   = 0.0f;
        private                     float               speedXY                         = 0.0f;
        private                     int                 lifetime                        = 0;
        private                     int                 currentTick                     = 0;
        private                     float               rotation                        = 0.0f;
        private                     FXGravity           gravity                         = null;
        private                     int                 fadeOutTicks                    = 0;

        protected LibFXPoint( LibDebug debug, float baseZ, FXType type, LibColors color, float startAngle, float x, float y, float z, FXSize size, int delayTicks, int lifetime, FXGravity gravity, int fadeOutTicks, LibSprite sprite )
        {
            this.debug            = debug;
            this.baseZ            = baseZ;
            this.type             = type;
            this.point            = new LibVertex( x, y, z, 0.0f, 1.0f );
            this.color            = color;
            this.startAngle       = startAngle;
            this.currentTick      = 0;
            this.lifetime         = lifetime;
            this.delayTicksBefore = delayTicks;
            this.gravity          = gravity;
            this.align3D          = LibAlign3D.values()[ LibMath.getRandom( 0, LibAlign3D.values().length - 1 )  ];
            this.rotationAlign    = LibAlign3D.values()[ LibMath.getRandom( 0, LibAlign3D.values().length - 1 )  ];
            this.fadeOutTicks     = fadeOutTicks;

            //assign Sprite
            this.sprite = sprite;
            if (this.sprite != null )
            {
                this.sprite.translate( 0.0f, 0.0f, -(this.sprite.getCenterZ() - this.point.z ), LibTransformationMode.EOriginalsToOriginals );
            }

            //switch type specific
            switch ( type )
            {
                case EStaticDebugPoint:
                {
                    this.pointSize = 0.01f;    //debug point size
                    break;
                }

                case ESliver:
                {
                    this.speedModified = 0.0f;

                    switch (this.gravity)
                    {
                        case ELow:
                        {
                            this.speedZ = 0.000001f * (float)LibMath.getRandom( 1, 100 );
                            this.speedXY = 0.0015f * (float)LibMath.getRandom( 2, 25 );
                            break;
                        }
                        case ENormal:
                        {
                            this.speedZ = 0.000002f * (float)LibMath.getRandom( 1, 100 );
                            this.speedXY = 0.0030f * (float)LibMath.getRandom( 2, 25 );
                            break;
                        }
                        case EHigh:
                        {
                            this.speedZ = 0.000003f *  (float)LibMath.getRandom( 1, 100 );
                            this.speedXY = 0.0045f  * (float)LibMath.getRandom( 2, 25 );
                            break;
                        }
                    }

                    switch ( size )
                    {
                        case ESmall:
                        {
                            this.pointSize = 0.003f * LibMath.getRandom( 8, 12 );
                            break;
                        }
                        case EMedium:
                        {
                            this.pointSize = 0.003f * LibMath.getRandom( 10, 15 );
                            break;
                        }
                        case ELarge:
                        {
                            this.pointSize = 0.003f * LibMath.getRandom( 12, 18 );
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
                            this.speedXY = 0.0001f    * LibMath.getRandom( 1,  5  );
                            this.speedModified = 0.00001f   * LibMath.getRandom( 5,  45 );
                            this.speedZ = 0.000001f  * LibMath.getRandom( 10, 90 );
                            this.pointSize = 0.001f     * LibMath.getRandom( 5,  15 );
                            break;
                        }
                        case EMedium:
                        {
                            this.speedXY = 0.0003f    * LibMath.getRandom( 1,  5  );
                            this.speedModified = 0.00001f   * LibMath.getRandom( 5,  45 );
                            this.speedZ = 0.000002f  * LibMath.getRandom( 10, 90 );
                            this.pointSize = 0.001f     * LibMath.getRandom( 10, 20 );
                            break;
                        }
                        case ELarge:
                        {
                            this.speedXY = 0.0008f    * LibMath.getRandom( 1,  5  );
                            this.speedModified = 0.00001f   * LibMath.getRandom( 5,  45 );
                            this.speedZ = 0.000003f  * LibMath.getRandom( 10, 90 );
                            this.pointSize = 0.001f     * LibMath.getRandom( 15, 25 );
                            break;
                        }
                    }
                    break;
                }
            }
        }

        protected final void animate()
        {
            if (this.delayTicksBefore > 0)
            {
                --this.delayTicksBefore;
            }
            else
            {
                switch(this.type)
                {
                    case EStaticDebugPoint:
                    {
                        ++this.currentTick;


                        break;
                    }

                    case ESliver:
                    {
                        if (this.point.z <= this.baseZ && this.currentTick > this.lifetime / 10 )
                        {
                            this.point.z = this.baseZ;
                        }
                        else
                        {
                            float zbase = this.currentTick; // - FxSettings.LIVETIME_SLIVER / 10 );
                            this.point.x -= this.speedXY * LibMath.sinDeg(this.startAngle);
                            this.point.y -= this.speedXY * LibMath.cosDeg(this.startAngle);
                            this.point.z -= zbase * zbase * this.speedZ;

                            this.rotation += 5.0f;
                            if (this.rotation >= 360.0f ) this.rotation = 0.0f;

                            //clip z on the floor!
                            if (this.point.z <= this.baseZ)
                            {
                                this.point.z = this.baseZ;

                                //playing sound on dropping to the floor will lag the game (solve!) :/
                                //Sound toPlay = Sound.values()[ LibMath.getRandom( Sound.ERubble1.ordinal(), Sound.ERubble3.ordinal() ) ];
                                //toPlay.playDistancedFx( new Point2D.Float( point.x, point.y ) );
                            }
                        }

                        //increase tick-counter, current speed and rotation
                        ++this.currentTick;
                        this.speedXY += this.speedModified;

                        break;
                    }

                    case EExplosion:
                    {
                        if (this.currentTick < this.lifetime / 5 )
                        {
                            //raise
                            float x = this.lifetime / 5 - this.currentTick;
                            this.point.x += this.speedXY * LibMath.sinDeg(this.startAngle);
                            this.point.y += this.speedXY * LibMath.cosDeg(this.startAngle);
                            this.point.z += x * x * this.speedZ;

                            this.rotation += 10.0f;
                            if (this.rotation >= 360.0f ) this.rotation = 0.0f;
                        }
                        else
                        {
                            //fall
                            if (this.point.z <= this.baseZ)
                            {
                                this.point.z = this.baseZ;
                            }
                            else
                            {
                                float x = this.currentTick - this.lifetime / 5;
                                this.point.x += this.speedXY * LibMath.sinDeg(this.startAngle);
                                this.point.y += this.speedXY * LibMath.cosDeg(this.startAngle);
                                this.point.z -= x * x * this.speedZ;

                                //clip z on the floor!
                                this.rotation += 5.0f;
                                if (this.rotation >= 360.0f ) this.rotation = 0.0f;
                            }
                        }

                        //increase tick-counter, current speed and rotation
                        ++this.currentTick;
                        this.speedXY += this.speedModified;

                        break;

                    }
                }
            }
        }

        public final void draw( LibAlign3D align3D )
        {
            //if ( s == null ) s = new Sprite( Others.ESprite1, 0.0f, 0.0f, 0.0f, LibScalation.ELowerHalf, WallCollidable.ENo, WallTex.ESliver1 );
            //if ( s == null ) s = new Sprite( Others.ESprite1, point.x, point.y, point.z, LibScalation.ELowerHalf, WallCollidable.ENo, WallTex.ESliver1 );

            /*
            if ( spriteAnk == null )
            {
                spriteAnk = new LibVertex( point );
            }
            */

            if (this.sprite != null )
            {
                if (this.lastPoint != null )
                {
                    this.sprite.translate(this.point.x - this.lastPoint.x, this.point.y - this.lastPoint.y, this.point.z - this.lastPoint.z, LibTransformationMode.EOriginalsToOriginals );
                }
            }

            LibFaceQuad face = null;

            switch ( align3D )
            {
                case AXIS_Z:
                {
                    face = new LibFaceQuad
                    (
                            this.debug,
                        new LibVertex(this.point.x, this.point.y, this.point.z ),
                        new LibVertex(this.point.x - this.pointSize, this.point.y - this.pointSize, this.point.z ),
                        new LibVertex(this.point.x - this.pointSize, this.point.y + this.pointSize, this.point.z ),
                        new LibVertex(this.point.x + this.pointSize, this.point.y + this.pointSize, this.point.z ),
                        new LibVertex(this.point.x + this.pointSize, this.point.y - this.pointSize, this.point.z ),
                            this.color
                    );
                    break;
                }

                case AXIS_X:
                {
                    face = new LibFaceQuad
                    (
                            this.debug,
                        new LibVertex(this.point.x, this.point.y, this.point.z ),
                        new LibVertex(this.point.x, this.point.y - this.pointSize, this.point.z - this.pointSize),
                        new LibVertex(this.point.x, this.point.y + this.pointSize, this.point.z - this.pointSize),
                        new LibVertex(this.point.x, this.point.y + this.pointSize, this.point.z + this.pointSize),
                        new LibVertex(this.point.x, this.point.y - this.pointSize, this.point.z + this.pointSize),
                            this.color
                    );
                    break;
                }

                case AXIS_Y:
                default:
                {
                    face = new LibFaceQuad
                    (
                            this.debug,
                        new LibVertex(this.point.x, this.point.y, this.point.z ),
                        new LibVertex(this.point.x - this.pointSize, this.point.y, this.point.z - this.pointSize),
                        new LibVertex(this.point.x + this.pointSize, this.point.y, this.point.z - this.pointSize),
                        new LibVertex(this.point.x + this.pointSize, this.point.y, this.point.z + this.pointSize),
                        new LibVertex(this.point.x - this.pointSize, this.point.y, this.point.z + this.pointSize),
                            this.color
                    );
                    break;
                }
            }

            switch (this.rotationAlign)
            {
                case AXIS_X:
                {
                    face.translateAndRotateXYZ( new LibMatrix(this.rotation, 0.0f, 0.0f ), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }

                case AXIS_Y:
                {
                    face.translateAndRotateXYZ( new LibMatrix( 0.0f, this.rotation, 0.0f ), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }

                case AXIS_Z:
                {
                    face.translateAndRotateXYZ( new LibMatrix( 0.0f, 0.0f, this.rotation), 0.0f, 0.0f, 0.0f, LibTransformationMode.EOriginalsToOriginals, null );
                    break;
                }
            }

            //fade out
            if (this.currentTick > this.lifetime - this.fadeOutTicks)
            {
              //ShooterDebug.bugfix.out( "FADE OUT FACE POINT .. [" + currentTick + "]" );
              //face.fadeOut( 0.5f ); //1.0f / lifetime - fadeOutTicks );

                float a = this.currentTick - (this.lifetime - this.fadeOutTicks);
                float b = this.fadeOutTicks;

                face.fadeOut( ( a / b ) );
            }

            //draw face
            face.draw();

            //draw sprite ..
            if (this.sprite != null )
            {
                this.sprite.animateSprite( null );
                this.sprite.draw();
                this.lastPoint = new LibVertex(this.point);
            }
        }

        protected boolean isLifetimeOver()
        {
            return (this.currentTick >= this.lifetime);
        }

        protected boolean isDelayedBefore()
        {
            return (this.delayTicksBefore > 0 );
        }

        protected FXType getType()
        {
            return this.type;
        }
    }
