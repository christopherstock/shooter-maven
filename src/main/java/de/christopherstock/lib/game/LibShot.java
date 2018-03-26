
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
        public interface ShotSpender
        {
            LibShot getShot(float modZ);
        }

        public static enum ShotType
        {
            /** a non-striking view */
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

        private                     LibDebug                iDebug                      = null;
        public                      ShotType                iType                       = null;
        public                      ShotOrigin              iOrigin                     = null;
        public                      Point3d                 iSrcPoint3d                 = null;
        public                      Point3d                 iEndPoint3d                 = null;
        public                      Point2D.Float           iSrcPointHorz               = null;
        public                      Point2D.Float           iSrcPointVert               = null;
        public                      Line2D.Float            iLineShotHorz               = null;
        public                      Line2D.Float            iLineShotVert               = null;
        public                      LibHoleSize             iBulletHoleSize             = null;
        public LibParticleQuantity iParticleQuantity           = null;
        public                      FXSize                  iSliverSize                 = null;
        public                      float                   iSliverAngleMod             = 0.0f;
        public                      int                     iDamage                     = 0;
        public                      boolean                 iWallBreakingAmmo           = false;
        public                      LibD3dsFile             iProjectile                 = null;
        private                     float                   iRotZ                       = 0.0f;
        private                     Point2D.Float           iEndPointHorz               = null;
        private                     Point2D.Float           iEndPointVert               = null;
        public                      float                   iRotX                       = 0.0f;
        private                     float                   iShotRange                  = 0.0f;
        private                     int                     iFadeOutTicks               = 0;

        public LibShot
        (
            ShotType                aType,
            ShotOrigin              aOrigin,
            float                   irregularityHorz,
            float                   irregularityVert,
            float                   startX,
            float                   startY,
            float                   startZ,
            float                   aRotZ,
            float                   aRotX,
            float                   aShotRange,
            LibHoleSize             aBulletHoleSize,
            LibDebug                aDebug,
            LibParticleQuantity aParticleQuantity,
            float                   aSliverAngleMod,
            int                     aDamage,
            FXSize                  aSliverSize,
            boolean                 aWallBreakingAmmo,
            LibD3dsFile             aProjectile,
            int                     aFadeOutTicks
        )
        {
            this.iType = aType;
            this.iOrigin = aOrigin;
            this.iRotZ = aRotZ;
            this.iRotX = aRotX;
            this.iShotRange = aShotRange;
            this.iBulletHoleSize = aBulletHoleSize;
            this.iDebug = aDebug;
            this.iParticleQuantity = aParticleQuantity;
            this.iSliverAngleMod = aSliverAngleMod;
            this.iDamage = aDamage;
            this.iSliverSize = aSliverSize;
            this.iWallBreakingAmmo = aWallBreakingAmmo;
            this.iProjectile = aProjectile;
            this.iFadeOutTicks = aFadeOutTicks;

            //debug.out( "=======================================" );

            //let the wearpon affect the horz- and vert-fire-angles
            this.iRotZ += irregularityHorz;
            this.iRotX += irregularityVert;

            this.iSrcPoint3d = new Point3d
            (
                startX,
                startY,
                startZ
            );

            this.iEndPoint3d = LibMathGeometry.getDistantPoint(this.iSrcPoint3d, this.iShotRange, this.iRotX, this.iRotZ);

            //calculate end point and shot-line for the horizontal axis
            this.iSrcPointHorz = new Point2D.Float( (float) this.iSrcPoint3d.x, (float) this.iSrcPoint3d.y );
            this.iEndPointHorz = new Point2D.Float
            (
                    this.iSrcPointHorz.x - LibMath.sinDeg(this.iRotZ) * this.iShotRange,
                    this.iSrcPointHorz.y - LibMath.cosDeg(this.iRotZ) * this.iShotRange
            );
            this.iLineShotHorz = new Line2D.Float(this.iSrcPointHorz, this.iEndPointHorz);
            //debug.out( "SHOT LINE HORZ [" + iSrcPoint3d.x + "," + iSrcPoint3d.y + "] to [" + iEndPointHorz.x + "," + iEndPointHorz.y + "]" );

            //calculate end point and shot-line for the vertical axis
            this.iSrcPointVert = new Point2D.Float( 0.0f, (float) this.iSrcPoint3d.z );
            this.iEndPointVert = new Point2D.Float
            (
                    this.iSrcPointVert.x + LibMath.cosDeg(this.iRotX) * this.iShotRange,
                    this.iSrcPointVert.y - LibMath.sinDeg(this.iRotX) * this.iShotRange
            );
            this.iLineShotVert = new Line2D.Float(this.iSrcPointVert, this.iEndPointVert);
            //debug.out( "SHOT LINE VERT " + iSrcPointVert + iEndPointVert );

            //Debug.drawLine( new Vertex( 2.0f, 1.0f, 2.0f, -1.0f, -1.0f ), new Vertex( 0.0f, 0.0f, 0.0f, -1.0f, -1.0f ) );
            //Debug.drawLine( srcPointVert.x, srcPointVert.y, endPointVert.x, endPointVert.y );
            //Debug.drawLine( new Vertex( srcPoint.x, srcPoint.y, srcPointVert.y, -1.0f, -1.0f ), new Vertex( endPointVert.x, 0.0f, 0.0f, -1.0f, -1.0f ) );
        }

        public final void drawShotLine( int lifetime )
        {
            //draw shot line with own calculations
            for (float distance = 0.0f; distance < this.iShotRange; distance += 0.15f )
            {
                LibFXManager.launchStaticPoint
                (
                        this.iDebug,
                    //wrong calc!!
                    new LibVertex
                    (
                        (float)(this.iSrcPoint3d.x - LibMath.sinDeg(this.iRotZ) * distance ),
                        (float)(this.iSrcPoint3d.y - LibMath.cosDeg(this.iRotZ) * distance ),
                        (float)(this.iSrcPoint3d.z - LibMath.sinDeg(this.iRotX) * distance )
                    ),
                    LibColors.EShotLine,
                    0.01f, //iBulletHoleSize.size / 8, //DebugSettings.DEBUG_POINT_SIZE
                    lifetime,
                        this.iFadeOutTicks
                );
            }
        }
    }
