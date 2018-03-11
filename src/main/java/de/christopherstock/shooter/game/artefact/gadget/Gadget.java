/*  $Id: Gadget.java 1288 2014-10-11 17:19:47Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.game.artefact.gadget;

    import  java.awt.geom.Point2D;

    import  de.christopherstock.lib.Lib;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.LibHoleSize;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.shooter.ShooterSettings.PlayerSettings;
import de.christopherstock.shooter.game.artefact.Artefact;
import de.christopherstock.shooter.game.artefact.ArtefactKind;
import de.christopherstock.shooter.game.artefact.ArtefactType.*;
import  de.christopherstock.shooter.level.*;

    /**************************************************************************************
    *   A close-combat Wearpon.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public final class Gadget extends ArtefactKind
    {
        private                 int                     iGiveTakeAnim               = 0;
        public                  GiveTakeAnim            iGiveTakeAnimState          = null;

        protected               int                     iTicksAnimGive              = 0;
        protected               int                     iTicksAnimHold              = 0;
        protected               int                     iTicksAnimRecall            = 0;

        public Gadget( int aTicksAnimGive, int aTicksAnimHold, int aTicksAnimRecall )
        {
            iGiveTakeAnimState      = GiveTakeAnim.ENone;
            iTicksAnimGive          = aTicksAnimGive;
            iTicksAnimHold          = aTicksAnimHold;
            iTicksAnimRecall        = aTicksAnimRecall;
        }

        @Override
        public boolean use( Artefact artefact, ShotSpender ss, Point2D.Float shooterXY )
        {
            //can only be used if not being animated
            if ( iGiveTakeAnimState == GiveTakeAnim.ENone )
            {
                //ShooterDebug.major.out( "use gadget !" );

                //start give anim for this gadget
                startGiveAnim();

                //gadget has been used
                return true;
            }

            return false;
        }

        @Override
        public int getDamage()
        {
            return 0;
        }

        @Override
        public final Lib.ParticleQuantity getSliverParticleQuantity()
        {
            return null;
        }

        @Override
        public final LibHoleSize getBulletHoleSize()
        {
            return null;
        }

        @Override
        public final FXSize getSliverParticleSize()
        {
            return null;
        }

        public final void handleGadget()
        {
            switch ( iGiveTakeAnimState )
            {
                case ENone:
                {
                    //no mods
                    break;
                }

                case EOffer:
                {
                    if ( --iGiveTakeAnim == 0 )
                    {
                        iGiveTakeAnim      = iTicksAnimHold;
                        iGiveTakeAnimState = GiveTakeAnim.EHold;
                    }
                    break;
                }

                case EHold:
                {
                    if ( --iGiveTakeAnim == 0 )
                    {
                        iGiveTakeAnim      = iTicksAnimRecall;
                        iGiveTakeAnimState = GiveTakeAnim.EDraw;

                        //launch the gadget-action here!
                        Level.currentPlayer().launchAction( this );
                    }
                    break;
                }

                case EDraw:
                {
                    if ( --iGiveTakeAnim == 0 )
                    {
                        iGiveTakeAnimState = GiveTakeAnim.ENone;
                    }
                    break;
                }
            }
        }

        public final void startGiveAnim()
        {
            iGiveTakeAnim       = iTicksAnimGive;
            iGiveTakeAnimState  = GiveTakeAnim.EOffer;
        }

        public final void stopGiveAnim()
        {
            iGiveTakeAnim       = 0;
            iGiveTakeAnimState  = GiveTakeAnim.ENone;
        }

        public final int[] getGiveTakeDrawMod()
        {
            int[] ret = new int[] { 0, 0, };

            //give/take animation?
            switch ( iGiveTakeAnimState )
            {
                case ENone:
                {
                    //no mods
                    break;
                }

                case EOffer:
                {
                    ret[ 0 ] -= ( iParentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * ( iTicksAnimGive - iGiveTakeAnim ) / iTicksAnimGive;
                    ret[ 1 ] += ( iParentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * ( iTicksAnimGive - iGiveTakeAnim ) / iTicksAnimGive;
                    break;
                }

                case EHold:
                {
                    ret[ 0 ] -= ( iParentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * 1;
                    ret[ 1 ] += ( iParentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * 1;
                    break;
                }

                case EDraw:
                {
                    ret[ 0 ] -= ( iParentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * iGiveTakeAnim / iTicksAnimRecall;
                    ret[ 1 ] += ( iParentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * iGiveTakeAnim / iTicksAnimRecall;
                    break;
                }
            }

            return ret;
        }
    }
