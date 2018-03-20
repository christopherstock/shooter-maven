/*  $Id: WearponKind.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.game.artefact;

    import  java.awt.geom.Point2D;
    import  de.christopherstock.lib.Lib;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.LibHoleSize;
import  de.christopherstock.lib.game.LibShot.ShotSpender;

    /**************************************************************************************
    *   The kind of artefact.
    *   
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract class ArtefactKind
    {
        public          ArtefactType        iParentKind         = null;

        public void setParent( ArtefactType aParent )
        {
            iParentKind = aParent;
        }

        public abstract LibHoleSize getBulletHoleSize();
        public abstract FXSize getSliverParticleSize();
        public abstract Lib.ParticleQuantity getSliverParticleQuantity();

        /**************************************************************************************
         *   @return <code>true</code> if the wearpon actually fired.
         *           <code>false</code> if the wearpon has not been fired ( if there is no ammo etc. ).
         **************************************************************************************/
         public abstract boolean use( Artefact artefact, ShotSpender ss, Point2D.Float shooterXY );
         public abstract int getDamage();
    }
