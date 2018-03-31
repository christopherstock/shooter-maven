
    package de.christopherstock.shooter.game.artefact.gadget;

    import  java.awt.geom.Point2D;

    import de.christopherstock.lib.LibParticleQuantity;
    import  de.christopherstock.lib.fx.LibFX.FXSize;
    import  de.christopherstock.lib.g3d.LibHoleSize;
    import  de.christopherstock.lib.game.LibShot.*;
    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterSetting.PlayerSettings;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactKind;
    import  de.christopherstock.shooter.game.artefact.ArtefactType.*;

    /*******************************************************************************************************************
    *   A non-combat item for the adventure part.
    *******************************************************************************************************************/
    @SuppressWarnings("PointlessArithmeticExpression")
    public final class Gadget extends ArtefactKind
    {
        private                 int             giveTakeAnim            = 0;
        public                  GiveTakeAnim    giveTakeAnimState       = null;

        private                 int             ticksAnimGive           = 0;
        private                 int             ticksAnimHold           = 0;
        private                 int             ticksAnimRecall         = 0;

        public Gadget( int ticksAnimGive, int ticksAnimHold, int ticksAnimRecall )
        {
            this.giveTakeAnimState = GiveTakeAnim.ENone;
            this.ticksAnimGive     = ticksAnimGive;
            this.ticksAnimHold     = ticksAnimHold;
            this.ticksAnimRecall   = ticksAnimRecall;
        }

        @Override
        public boolean use( Artefact artefact, ShotSource ss, Point2D.Float shooterXY )
        {
            //can only be used if not being animated
            if (this.giveTakeAnimState == GiveTakeAnim.ENone )
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
        public final LibParticleQuantity getSliverParticleQuantity()
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
            switch (this.giveTakeAnimState)
            {
                case ENone:
                {
                    //no mods
                    break;
                }

                case EOffer:
                {
                    if ( --this.giveTakeAnim == 0 )
                    {
                        this.giveTakeAnim = this.ticksAnimHold;
                        this.giveTakeAnimState = GiveTakeAnim.EHold;
                    }
                    break;
                }

                case EHold:
                {
                    if ( --this.giveTakeAnim == 0 )
                    {
                        this.giveTakeAnim = this.ticksAnimRecall;
                        this.giveTakeAnimState = GiveTakeAnim.EDraw;

                        //launch the gadget-action here!
                        Shooter.game.engine.player.launchAction( this );
                    }
                    break;
                }

                case EDraw:
                {
                    if ( --this.giveTakeAnim == 0 )
                    {
                        this.giveTakeAnimState = GiveTakeAnim.ENone;
                    }
                    break;
                }
            }
        }

        private void startGiveAnim()
        {
            this.giveTakeAnim = this.ticksAnimGive;
            this.giveTakeAnimState = GiveTakeAnim.EOffer;
        }

        public final void stopGiveAnim()
        {
            this.giveTakeAnim = 0;
            this.giveTakeAnimState = GiveTakeAnim.ENone;
        }

        public final int[] getGiveTakeDrawMod()
        {
            int[] ret = new int[] { 0, 0, };

            //give/take animation?
            switch (this.giveTakeAnimState)
            {
                case ENone:
                {
                    //no mods
                    break;
                }

                case EOffer:
                {
                    ret[ 0 ] -= (this.parentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * (this.ticksAnimGive - this.giveTakeAnim) / this.ticksAnimGive;
                    ret[ 1 ] += (this.parentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * (this.ticksAnimGive - this.giveTakeAnim) / this.ticksAnimGive;
                    break;
                }

                case EHold:
                {
                    ret[ 0 ] -= (this.parentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * 1;
                    ret[ 1 ] += (this.parentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * 1;
                    break;
                }

                case EDraw:
                {
                    ret[ 0 ] -= (this.parentKind.getArtefactImage().width  / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * this.giveTakeAnim / this.ticksAnimRecall;
                    ret[ 1 ] += (this.parentKind.getArtefactImage().height / PlayerSettings.GIVE_TAKE_ANIM_RATIO ) * this.giveTakeAnim / this.ticksAnimRecall;
                    break;
                }
            }

            return ret;
        }
    }
