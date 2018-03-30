
    package de.christopherstock.shooter.game.artefact;

    import  java.util.Vector;
    import  de.christopherstock.lib.LibAnimation;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;

    /*******************************************************************************************************************
    *   Manages a set of artefacts being holded by the player, a bot or a device.
    *******************************************************************************************************************/
    public class ArtefactSet
    {
        /** The currently equipped artefact. */
        public                      Artefact                currentArtefact         = null;

        /** All currently holded artefacts. */
        public                      Vector<Artefact>        artefacts               = null;

        /** This is a instance of the {@link ArtefactType} {@link ArtefactType#EHands}, that is always holded. */
        public                      Artefact                hands                   = null;

        public ArtefactSet()
        {
            //set and deliver default artefact
            this.artefacts = new Vector<Artefact>();
            this.hands = new Artefact( ArtefactType.EHands );
            this.currentArtefact = this.hands;

            this.deliverArtefact( this.hands );
        }

        public final void deliverArtefact( Artefact artefactToDeliver )
        {
            //check if this artefact type is already holded
            boolean contained = false;
            for ( Artefact a : this.artefacts)
            {
                if ( a.artefactType == artefactToDeliver.artefactType)
                {
                    contained = true;
                }
            }

            //add to wearpon stack if not already holded
            if ( !contained )
            {
                //sort wearpons according to enum
                int targetIndex = 0;
                for ( Artefact iW : this.artefacts)
                {
                    if ( artefactToDeliver.artefactType.ordinal() > iW.artefactType.ordinal() )
                    {
                        ++targetIndex;
                    }
                }
                this.artefacts.insertElementAt( artefactToDeliver, targetIndex );
            }
        }

        public final void extractArtefact( Object toDraw )
        {
            ( (Gadget)toDraw ).stopGiveAnim();
            Shooter.game.engine.hud.stopHandAnimation();

            //change to hands if this artefact is holded
            if ( toDraw == this.currentArtefact.artefactType.artefactKind)
            {
                this.currentArtefact = this.hands;
            }
            this.artefacts.remove( toDraw );
        }

        public final boolean showAmmoInHUD()
        {
            return
            (
                    //iCurrentItemGroup == ItemGroup.EWearpon
                    this.currentArtefact != null
                && this.currentArtefact.artefactType.isFireArm()
            );
        }


        public final void choosePreviousWearponOrGadget( boolean byPlayer )
        {
            //browse all wearpons
            for (int i = 0; i < this.artefacts.size(); ++i )
            {
                //find current wearpon
                if (this.currentArtefact == null )
                {
                    this.setArtefact(this.artefacts.elementAt( i ) );
                    break;
                }
                else if (this.currentArtefact == this.artefacts.elementAt( i ) )
                {
                    int previousIndex = i - 1;
                    if ( previousIndex < 0 )
                    {
                        //pick last wearpon
                        this.setArtefact(this.artefacts.elementAt(this.artefacts.size() - 1 ) );

                        //animate only if more than one wearpon is available!
                        if (this.artefacts.size() > 1 && byPlayer )
                        {
                            Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                        }
                    }
                    else
                    {
                        this.setArtefact(this.artefacts.elementAt( previousIndex ) );
                        if ( byPlayer ) Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                    }
                    break;
                }
            }
        }

        public final void chooseNextWearponOrGadget( boolean byPlayer )
        {
            //browse all holded wearpons
            for (int i = 0; i < this.artefacts.size(); ++i )
            {
                //find current wearpon
                if (this.getArtefact() == null )
                {
                    this.setArtefact(this.artefacts.elementAt( i ) );
                    break;
                }
                else if (this.getArtefact() == this.artefacts.elementAt( i ) )
                {
                    int nextIndex = i + 1;
                    if ( nextIndex >= this.artefacts.size() )
                    {
                        //pick first wearpon
                        this.setArtefact(this.artefacts.elementAt( 0 ) );

                        //animate only if more than one wearpon is available!
                        if (this.artefacts.size() > 1 && byPlayer )
                        {
                            Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                        }
                    }
                    else
                    {
                        this.setArtefact(this.artefacts.elementAt( nextIndex ) );
                        if ( byPlayer ) Shooter.game.engine.hud.startHandAnimation( LibAnimation.EAnimationShow, null );
                    }
                    break;
                }
            }
        }

        public final Artefact getArtefact()
        {
            return this.currentArtefact;
        }

        public final ArtefactType getArtefactType()
        {
            return this.currentArtefact.artefactType;
        }

        public final void setArtefact( Artefact newWearpon )
        {
            this.currentArtefact = newWearpon;
        }

        public final void drawArtefactOrtho()
        {
            if (this.currentArtefact != null ) this.currentArtefact.drawOrtho();
        }

        public final boolean isMagazineEmpty()
        {
            return (this.currentArtefact.magazineAmmo == 0 );
        }

        public final boolean isWearponDelayed()
        {
            return (this.currentArtefact.currentDelayAfterUse > System.currentTimeMillis() );
        }

        public final boolean contains( Artefact a )
        {
            for ( Artefact art : this.artefacts)
            {
                if ( art.artefactType == a.artefactType)
                {
                    return true;
                }
            }
            return false;
        }

        public final void assignMagazine( Artefact a )
        {
            for ( Artefact art : this.artefacts)
            {
                if ( art.artefactType == a.artefactType)
                {
                    art.magazineAmmo = a.magazineAmmo;
                }
            }
        }
    }
