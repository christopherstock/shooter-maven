
    package de.christopherstock.shooter.game.artefact;

    import  java.util.Vector;
    import  de.christopherstock.lib.Lib.LibAnimation;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;

    /*******************************************************************************************************************
    *   Manages a set of artefacts being holded by the player, a bot or a device.
    *******************************************************************************************************************/
    public class ArtefactSet
    {
        /** The currently equipped artefact. */
        public                      Artefact                iCurrentArtefact            = null;

        /** All currently holded artefacts. */
        public                      Vector<Artefact>        iArtefacts                  = null;

        /** This is a instance of the {@link ArtefactType} {@link ArtefactType#EHands}, that is always holded. */
        public                      Artefact                iHands                      = null;

        public ArtefactSet()
        {
            //set and deliver default artefact
            this.iArtefacts = new Vector<Artefact>();
            this.iHands = new Artefact( ArtefactType.EHands );
            this.iCurrentArtefact = this.iHands;
            this.deliverArtefact(this.iHands);
        }

        public final void deliverArtefact( Artefact artefactToDeliver )
        {
            //check if this artefact type is already holded
            boolean contained = false;
            for ( Artefact a : this.iArtefacts)
            {
                if ( a.iArtefactType == artefactToDeliver.iArtefactType )
                {
                    contained = true;
                }
            }

            //add to wearpon stack if not already holded
            if ( !contained )
            {
                //sort wearpons according to enum
                int targetIndex = 0;
                for ( Artefact iW : this.iArtefacts)
                {
                    if ( artefactToDeliver.iArtefactType.ordinal() > iW.iArtefactType.ordinal() )
                    {
                        ++targetIndex;
                    }
                }
                this.iArtefacts.insertElementAt( artefactToDeliver, targetIndex );
            }
        }

        public final void extractArtefact( Object toDraw )
        {
            ( (Gadget)toDraw ).stopGiveAnim();
            Shooter.game.engine.hud.stopHandAnimation();

            //change to hands if this artefact is holded
            if ( toDraw == this.iCurrentArtefact.iArtefactType.iArtefactKind )
            {
                this.iCurrentArtefact = this.iHands;
            }
            this.iArtefacts.remove( toDraw );
        }

        public final boolean showAmmoInHUD()
        {
            return
            (
                    //iCurrentItemGroup == ItemGroup.EWearpon
                    this.iCurrentArtefact != null
                && this.iCurrentArtefact.iArtefactType.isFireArm()
            );
        }


        public final void choosePreviousWearponOrGadget( boolean byPlayer )
        {
            //browse all wearpons
            for (int i = 0; i < this.iArtefacts.size(); ++i )
            {
                //find current wearpon
                if (this.iCurrentArtefact == null )
                {
                    this.setArtefact(this.iArtefacts.elementAt( i ) );
                    break;
                }
                else if (this.iCurrentArtefact == this.iArtefacts.elementAt( i ) )
                {
                    int previousIndex = i - 1;
                    if ( previousIndex < 0 )
                    {
                        //pick last wearpon
                        this.setArtefact(this.iArtefacts.elementAt(this.iArtefacts.size() - 1 ) );

                        //animate only if more than one wearpon is available!
                        if (this.iArtefacts.size() > 1 && byPlayer )
                        {
                            Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                        }
                    }
                    else
                    {
                        this.setArtefact(this.iArtefacts.elementAt( previousIndex ) );
                        if ( byPlayer ) Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                    }
                    break;
                }
            }
        }

        public final void chooseNextWearponOrGadget( boolean byPlayer )
        {
            //browse all holded wearpons
            for (int i = 0; i < this.iArtefacts.size(); ++i )
            {
                //find current wearpon
                if (this.getArtefact() == null )
                {
                    this.setArtefact(this.iArtefacts.elementAt( i ) );
                    break;
                }
                else if (this.getArtefact() == this.iArtefacts.elementAt( i ) )
                {
                    int nextIndex = i + 1;
                    if ( nextIndex >= this.iArtefacts.size() )
                    {
                        //pick first wearpon
                        this.setArtefact(this.iArtefacts.elementAt( 0 ) );

                        //animate only if more than one wearpon is available!
                        if (this.iArtefacts.size() > 1 && byPlayer )
                        {
                            Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                        }
                    }
                    else
                    {
                        this.setArtefact(this.iArtefacts.elementAt( nextIndex ) );
                        if ( byPlayer ) Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                    }
                    break;
                }
            }
        }

        public final Artefact getArtefact()
        {
            return this.iCurrentArtefact;
        }

        public final ArtefactType getArtefactType()
        {
            return this.iCurrentArtefact.iArtefactType;
        }

        public final void setArtefact( Artefact newWearpon )
        {
            this.iCurrentArtefact = newWearpon;
        }

        public final void drawArtefactOrtho()
        {
            if (this.iCurrentArtefact != null ) this.iCurrentArtefact.drawOrtho();
        }

        public final boolean isMagazineEmpty()
        {
            return (this.iCurrentArtefact.iMagazineAmmo == 0 );
        }

        public final boolean isWearponDelayed()
        {
            return (this.iCurrentArtefact.iCurrentDelayAfterUse > System.currentTimeMillis() );
        }

        public final boolean contains( Artefact a )
        {
            for ( Artefact art : this.iArtefacts)
            {
                if ( art.iArtefactType == a.iArtefactType )
                {
                    return true;
                }
            }
            return false;
        }

        public final void assignMagazine( Artefact a )
        {
            for ( Artefact art : this.iArtefacts)
            {
                if ( art.iArtefactType == a.iArtefactType )
                {
                    art.iMagazineAmmo = a.iMagazineAmmo;
                }
            }
        }
    }
