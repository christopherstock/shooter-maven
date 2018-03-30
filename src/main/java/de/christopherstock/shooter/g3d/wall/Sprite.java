
    package de.christopherstock.shooter.g3d.wall;

    import  java.awt.geom.Point2D;

    import de.christopherstock.lib.LibTransformationMode;
    import de.christopherstock.lib.LibInvert;
    import de.christopherstock.lib.LibScalation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.lib.math.LibMath;
    import de.christopherstock.lib.ui.LibSprite;
    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;

    /*******************************************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *******************************************************************************************************************/
    public class Sprite extends Wall implements LibSprite
    {
        public Sprite( LibD3dsFile file, LibVertex anchor, LibScalation scale, WallCollidable collidable, WallTex tex )
        {
            super(   file, anchor, 90.0f, scale, LibInvert.ENo, collidable, WallAction.ESprite, WallClimbable.ENo, DrawMethod.EAlwaysDraw, tex, null, 0, WallHealth.EUnbreakale, null, null );
        }

        public void animateSprite( LibVertex trans )
        {
            //get wall's 2d center point and player's 2d point
            Point2D.Float spriteAnk2d = this.getCenterPointXY();
            Point2D.Float playerAnk2d = Shooter.game.engine.player.getCylinder().getCenterHorz();

            //get angles
            float anglePlayerToSprite = LibMath.getAngleCorrect( playerAnk2d, spriteAnk2d );
            float angleSpriteToPlayer = LibMath.normalizeAngle( anglePlayerToSprite );

            //ShooterDebug.bugfix.out( "angleSpriteToPlayer: [" + angleSpriteToPlayer + "]" );
            //angleSpriteToPlayer = (int)angleSpriteToPlayer;

            LibVertex     ank   = new LibVertex( spriteAnk2d.x, spriteAnk2d.y, this.getAnchor().z );

            //ShooterDebug.bugfix.out( "buggy ank is [" + ank + "]" );

            //new LibFXInitializer().launchStaticPoint( ank, LibColors.EBlue, 1, 0.05f );

            //LibVertex ank = getAnchor();

            this.translateAndRotateXYZ
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
