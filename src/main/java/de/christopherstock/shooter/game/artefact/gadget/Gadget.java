
    package de.christopherstock.shooter.game.artefact.gadget;

    import  java.awt.geom.Point2D;
    import  de.christopherstock.lib.Lib;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.LibHoleSize;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterSettings.PlayerSettings;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactKind;
    import  de.christopherstock.shooter.game.artefact.ArtefactType.*;

    /*******************************************************************************************************************
    *   A close-combat Wearpon.
    *******************************************************************************************************************/
    @SuppressWarnings("PointlessArithmeticExpression")
    public final class Gadget extends ArtefactKind
    {
        private                 int                     iGiveTakeAnim               = 0;
        public                  GiveTakeAnim            iGiveTakeAnimState          = null;

        protected               int                     iTicksAnimGive              = 0;
        protected               int                     iTicksAnimHold              = 0;
        protected               int                     iTicksAnimRecall            = 0;

        public Gadget( int aTicksAnimGive, int aTicksAnimHold, int aTicksAnimRecall )
        {
            this.iGiveTakeAnimState = GiveTakeAnim.ENone;
            this.iTicksAnimGive = aTicksAnimGive;
            this.iTicksAnimHold = aTicksAnimHold;
            this.iTicksAnimRecall = aTicksAnimRecall;
        }

        @Override
        public boolean use( Artefact artefact, ShotSpender ss, Point2D.Float shooterXY )
        {
            //can only be used if not being animated
            if (this.iGiveTakeAnimState == GiveTakeAnim.ENone )
            {
                //ShooterDebug.major.out( "use gadget !" );

                //start give anim for this gadget
                this.startGiveAnim();

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
            switch (this.iGiveTakeAnimState)
            {
                case ENone:
                {
                    //no mods
                    break;
                }

                case EOffer:
                {
                    if ( --this.iGiveTakeAnim == 0 )
                    {
                        this.iGiveTakeAnim = this.iTicksAnimHold;
                        this.iGiveTakeAnimState = GiveTakeAnim.EHold;
                    }
                    break;
                }

                case EHold:
                {
                    if ( --this.iGiveTakeAnim == 0 )
                    {
                        this.iGiveTakeAnim = this.iTicksAnimRecall;
                        this.iGiveTakeAnimState = GiveTakeAnim.EDraw;

                        //launch the gadget-action here!
                        Shooter.game.engine.player.launchAction( this );
                    }
                    break;
                }

                case EDraw:
                {
                    if ( --this.iGiveTakeAnim == 0 )
                    {
                        this.iGiveTakeAnimState = GiveTakeAnim.ENone;
                    }
                    break;
                }
            }
        }

        public final void startGiveAnim()
        {
            this.iGiveTakeAnim = this.iTicksAnimGive;
            this.iGiveTakeAnimState = GiveTakeAnim.EOffer;
        }

        public final void stopGiveAnim()
        {
            this.iGiveTakeAnim = 0;
            this.iGiveTakeAnimState = GiveTakeAnim.ENone;
        }

        public final int[] getGiveTakeDrawMod()
        {
            int[] ret = new int[] { 0, 0, };

            //give/take animation?
            switch (this.iGiveTakeAnimState)
            {
                case ENone:
                {
                    //no mods
                    break;
                }

                case EOffer:
                {
                    ret[ 0 ] -= (this.iParentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * (this.iTicksAnimGive - this.iGiveTakeAnim) / this.iTicksAnimGive;
                    ret[ 1 ] += (this.iParentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * (this.iTicksAnimGive - this.iGiveTakeAnim) / this.iTicksAnimGive;
                    break;
                }

                case EHold:
                {
                    ret[ 0 ] -= (this.iParentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * 1;
                    ret[ 1 ] += (this.iParentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * 1;
                    break;
                }

                case EDraw:
                {
                    ret[ 0 ] -= (this.iParentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * this.iGiveTakeAnim / this.iTicksAnimRecall;
                    ret[ 1 ] += (this.iParentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * this.iGiveTakeAnim / this.iTicksAnimRecall;
                    break;
                }
            }

            return ret;
        }
    }
