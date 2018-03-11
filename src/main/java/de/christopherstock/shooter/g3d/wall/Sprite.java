/*  $Id: Sprite.java 1288 2014-10-11 17:19:47Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.g3d.wall;

    import  java.awt.geom.Point2D;
    import  de.christopherstock.lib.Lib.Invert;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.Lib.Scalation;
    import  de.christopherstock.lib.fx.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.LibMath;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;
    import  de.christopherstock.shooter.level.*;

    /**************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class Sprite extends Wall implements LibSprite
    {
        private     static  final   long    serialVersionUID        = -8942237814400103635L;

        public Sprite( LibD3dsFile file, LibVertex aAnchor, Scalation scale, WallCollidable collidable, WallTex tex )
        {
            super(   file, aAnchor, 90.0f, scale, Invert.ENo, collidable, WallAction.ESprite, WallClimbable.ENo, DrawMethod.EAlwaysDraw, tex, null, 0, WallHealth.EUnbreakale, null, null );
        }

        @Override
        public void animateSprite( LibVertex trans )
        {
            //get wall's 2d center point and player's 2d point
            Point2D.Float spriteAnk2d = getCenterPointXY();
            Point2D.Float playerAnk2d = Level.currentPlayer().getCylinder().getCenterHorz();

            //get angles
            float anglePlayerToSprite = LibMath.getAngleCorrect( playerAnk2d, spriteAnk2d );
            float angleSpriteToPlayer = LibMath.normalizeAngle( anglePlayerToSprite );

            //ShooterDebug.bugfix.out( "angleSpriteToPlayer: [" + angleSpriteToPlayer + "]" );
            //angleSpriteToPlayer = (int)angleSpriteToPlayer;

            LibVertex     ank   = new LibVertex( spriteAnk2d.x, spriteAnk2d.y, getAnchor().z );

            //ShooterDebug.bugfix.out( "buggy ank is [" + ank + "]" );

            //new LibFXInitializer().launchStaticPoint( ank, LibColors.EBlue, 1, 0.05f );

            //LibVertex ank = getAnchor();

            translateAndRotateXYZ
            (
                ( trans == null ? 0.0f : trans.x ),
                ( trans == null ? 0.0f : trans.y ),
                ( trans == null ? 0.0f : trans.z ),
                0.0f,
                0.0f,
                angleSpriteToPlayer,
                ank,
                LibTransformationMode.EOriginalsToTransformed
            );
        }
    }
