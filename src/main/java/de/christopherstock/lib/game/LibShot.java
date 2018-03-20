/*  $Id: LibShot.java 1294 2014-10-12 15:50:36Z jenetic.bytemare $
 *  =======================================================================================
 */
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

    /**************************************************************************************
    *   All calculations for one shot.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class LibShot
    {
        public static interface ShotSpender
        {
            public abstract LibShot getShot( float modZ );
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
        public                      Lib.ParticleQuantity    iParticleQuantity           = null;
        public                      FXSize                  iSliverSize                 = null;
        public                      float                   iSliverAngleMod             = 0.0f;
        public                      int                     iDamage                     = 0;
        public                      boolean                 iWallBreakingAmmo           = false;
        public                      LibD3dsFile                iProjectile                 = null;
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
            Lib.ParticleQuantity    aParticleQuantity,
            float                   aSliverAngleMod,
            int                     aDamage,
            FXSize                  aSliverSize,
            boolean                 aWallBreakingAmmo,
            LibD3dsFile             aProjectile,
            int                     aFadeOutTicks
        )
        {
            iType               = aType;
            iOrigin             = aOrigin;
            iRotZ               = aRotZ;
            iRotX               = aRotX;
            iShotRange          = aShotRange;
            iBulletHoleSize     = aBulletHoleSize;
            iDebug              = aDebug;
            iParticleQuantity   = aParticleQuantity;
            iSliverAngleMod     = aSliverAngleMod;
            iDamage             = aDamage;
            iSliverSize         = aSliverSize;
            iWallBreakingAmmo   = aWallBreakingAmmo;
            iProjectile         = aProjectile;
            iFadeOutTicks       = aFadeOutTicks;

            //iDebug.out( "=======================================" );

            //let the wearpon affect the horz- and vert-fire-angles
            iRotZ           += irregularityHorz;
            iRotX           += irregularityVert;

            iSrcPoint3d     = new Point3d
            (
                startX,
                startY,
                startZ
            );

            iEndPoint3d     = LibMathGeometry.getDistantPoint( iSrcPoint3d, iShotRange, iRotX, iRotZ );

            //calculate end point and shot-line for the horizontal axis
            iSrcPointHorz = new Point2D.Float( (float)iSrcPoint3d.x, (float)iSrcPoint3d.y );
            iEndPointHorz   = new Point2D.Float
            (
                iSrcPointHorz.x - LibMath.sinDeg( iRotZ ) * iShotRange,
                iSrcPointHorz.y - LibMath.cosDeg( iRotZ ) * iShotRange
            );
            iLineShotHorz   = new Line2D.Float( iSrcPointHorz, iEndPointHorz );
            //iDebug.out( "SHOT LINE HORZ [" + iSrcPoint3d.x + "," + iSrcPoint3d.y + "] to [" + iEndPointHorz.x + "," + iEndPointHorz.y + "]" );

            //calculate end point and shot-line for the vertical axis
            iSrcPointVert = new Point2D.Float( 0.0f, (float)iSrcPoint3d.z );
            iEndPointVert   = new Point2D.Float
            (
                iSrcPointVert.x + LibMath.cosDeg( iRotX ) * iShotRange,
                iSrcPointVert.y - LibMath.sinDeg( iRotX ) * iShotRange
            );
            iLineShotVert   = new Line2D.Float( iSrcPointVert, iEndPointVert );
            //iDebug.out( "SHOT LINE VERT " + iSrcPointVert + iEndPointVert );

            //Debug.drawLine( new Vertex( 2.0f, 1.0f, 2.0f, -1.0f, -1.0f ), new Vertex( 0.0f, 0.0f, 0.0f, -1.0f, -1.0f ) );
            //Debug.drawLine( srcPointVert.x, srcPointVert.y, endPointVert.x, endPointVert.y );
            //Debug.drawLine( new Vertex( srcPoint.x, srcPoint.y, srcPointVert.y, -1.0f, -1.0f ), new Vertex( endPointVert.x, 0.0f, 0.0f, -1.0f, -1.0f ) );
        }

        public final void drawShotLine( int lifetime )
        {
            //draw shot line with own calculations
            for ( float distance = 0.0f; distance < iShotRange; distance += 0.15f )
            {
                LibFXManager.launchStaticPoint
                (
                    iDebug,
                    //wrong calc!!
                    new LibVertex
                    (
                        (float)( iSrcPoint3d.x - LibMath.sinDeg( iRotZ ) * distance ),
                        (float)( iSrcPoint3d.y - LibMath.cosDeg( iRotZ ) * distance ),
                        (float)( iSrcPoint3d.z - LibMath.sinDeg( iRotX ) * distance )
                    ),
                    LibColors.EShotLine,
                    0.01f, //iBulletHoleSize.size / 8, //DebugSettings.DEBUG_POINT_SIZE
                    lifetime,
                    iFadeOutTicks
                );
            }
        }
    }
