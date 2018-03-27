
    package de.christopherstock.lib.game;

    import  java.awt.geom.*;
    import  javax.vecmath.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.*;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   All calculations for one shot.
    *******************************************************************************************************************/
    public class LibShot
    {
        public interface ShotSource
        {
            LibShot getShot( float modZ );
        }

        public static enum ShotType
        {
            /** a non-striking glView */
            EViewOnly,
            /** a striking projectile */
            ESharpAmmo,
        }

        public static enum ShotOrigin
        {
            /** the one and only player */
            EPlayer,
            /** all friendly bots */
            EFriends,
            /** all hostile bots */
            EEnemies,
        }

        private                     LibDebug                debug                       = null;
        public                      ShotType                type                        = null;
        public                      ShotOrigin              origin                      = null;
        public                      Point3d                 srcPoint3d                  = null;
        public                      Point3d                 endPoint3d                  = null;
        public                      Point2D.Float           srcPointHorz                = null;
        public                      Point2D.Float           srcPointVert                = null;
        public                      Line2D.Float            lineShotHorz                = null;
        public                      Line2D.Float            lineShotVert                = null;
        public                      LibHoleSize             bulletHoleSize              = null;
        public                      LibParticleQuantity     particleQuantity            = null;
        public                      FXSize                  sliverSize                  = null;
        public                      float                   sliverAngleMod              = 0.0f;
        public                      int                     damage                      = 0;
        public                      boolean                 wallBreakingAmmo            = false;
        public                      LibD3dsFile             projectile                  = null;
        private                     float                   rotZ                        = 0.0f;
        private                     Point2D.Float           endPointHorz                = null;
        private                     Point2D.Float           endPointVert                = null;
        public                      float                   rotX                        = 0.0f;
        private                     float                   shotRange                   = 0.0f;
        private                     int                     fadeOutTicks                = 0;

        public LibShot
        (
            ShotType            type,
            ShotOrigin          origin,
            float               irregularityHorz,
            float               irregularityVert,
            float               startX,
            float               startY,
            float               startZ,
            float               rotZ,
            float               rotX,
            float               shotRange,
            LibHoleSize         bulletHoleSize,
            LibDebug            debug,
            LibParticleQuantity particleQuantity,
            float               sliverAngleMod,
            int                 iamage,
            FXSize              sliverSize,
            boolean             wallBreakingAmmo,
            LibD3dsFile         projectile,
            int                 fadeOutTicks
        )
        {
            this.type = type;
            this.origin = origin;
            this.rotZ = rotZ;
            this.rotX = rotX;
            this.shotRange = shotRange;
            this.bulletHoleSize = bulletHoleSize;
            this.debug = debug;
            this.particleQuantity = particleQuantity;
            this.sliverAngleMod = sliverAngleMod;
            this.damage = iamage;
            this.sliverSize = sliverSize;
            this.wallBreakingAmmo = wallBreakingAmmo;
            this.projectile = projectile;
            this.fadeOutTicks = fadeOutTicks;

            //debug.out( "=======================================" );

            //let the wearpon affect the horz- and vert-fire-angles
            this.rotZ += irregularityHorz;
            this.rotX += irregularityVert;

            this.srcPoint3d = new Point3d
            (
                startX,
                startY,
                startZ
            );

            this.endPoint3d = LibMathGeometry.getDistantPoint(this.srcPoint3d, this.shotRange, this.rotX, this.rotZ);

            //calculate end point and shot-line for the horizontal axis
            this.srcPointHorz = new Point2D.Float( (float) this.srcPoint3d.x, (float) this.srcPoint3d.y );
            this.endPointHorz = new Point2D.Float
            (
                    this.srcPointHorz.x - LibMath.sinDeg(this.rotZ) * this.shotRange,
                    this.srcPointHorz.y - LibMath.cosDeg(this.rotZ) * this.shotRange
            );
            this.lineShotHorz = new Line2D.Float(this.srcPointHorz, this.endPointHorz);
            //debug.out( "SHOT LINE HORZ [" + srcPoint3d.x + "," + srcPoint3d.y + "] to [" + endPointHorz.x + "," + endPointHorz.y + "]" );

            //calculate end point and shot-line for the vertical axis
            this.srcPointVert = new Point2D.Float( 0.0f, (float) this.srcPoint3d.z );
            this.endPointVert = new Point2D.Float
            (
                    this.srcPointVert.x + LibMath.cosDeg(this.rotX) * this.shotRange,
                    this.srcPointVert.y - LibMath.sinDeg(this.rotX) * this.shotRange
            );
            this.lineShotVert = new Line2D.Float(this.srcPointVert, this.endPointVert);
            //debug.out( "SHOT LINE VERT " + srcPointVert + endPointVert );

            //Debug.drawLine( new Vertex( 2.0f, 1.0f, 2.0f, -1.0f, -1.0f ), new Vertex( 0.0f, 0.0f, 0.0f, -1.0f, -1.0f ) );
            //Debug.drawLine( srcPointVert.x, srcPointVert.y, endPointVert.x, endPointVert.y );
            //Debug.drawLine( new Vertex( srcPoint.x, srcPoint.y, srcPointVert.y, -1.0f, -1.0f ), new Vertex( endPointVert.x, 0.0f, 0.0f, -1.0f, -1.0f ) );
        }

        public final void drawShotLine( int lifetime )
        {
            //draw shot line with own calculations
            for (float distance = 0.0f; distance < this.shotRange; distance += 0.15f )
            {
                LibFXManager.launchStaticPoint
                (
                        this.debug,
                    //wrong calc!!
                    new LibVertex
                    (
                        (float)(this.srcPoint3d.x - LibMath.sinDeg(this.rotZ) * distance ),
                        (float)(this.srcPoint3d.y - LibMath.cosDeg(this.rotZ) * distance ),
                        (float)(this.srcPoint3d.z - LibMath.sinDeg(this.rotX) * distance )
                    ),
                    LibColors.EShotLine,
                    0.01f, //bulletHoleSize.size / 8, //DebugSettings.DEBUG_POINT_SIZE
                    lifetime,
                        this.fadeOutTicks
                );
            }
        }
    }
