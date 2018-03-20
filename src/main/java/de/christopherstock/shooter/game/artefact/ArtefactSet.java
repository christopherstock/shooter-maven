
    package de.christopherstock.shooter.game.artefact;

    import  java.util.Vector;
    import  de.christopherstock.lib.Lib.LibAnimation;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;

    /**************************************************************************************
    *   Manages a set of artefacts being holded by the player, a bot or a device.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
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
            iArtefacts              = new Vector<Artefact>();
            iHands                  = new Artefact( ArtefactType.EHands );
            iCurrentArtefact        = iHands;
            deliverArtefact( iHands );
        }

        public final void deliverArtefact( Artefact artefactToDeliver )
        {
            //check if this artefact type is already holded
            boolean contained = false;
            for ( Artefact a : iArtefacts )
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
                for ( Artefact iW : iArtefacts )
                {
                    if ( artefactToDeliver.iArtefactType.ordinal() > iW.iArtefactType.ordinal() )
                    {
                        ++targetIndex;
                    }
                }
                iArtefacts.insertElementAt( artefactToDeliver, targetIndex );
            }
        }

        public final void extractArtefact( Object toDraw )
        {
            ( (Gadget)toDraw ).stopGiveAnim();
            Shooter.mainThread.iHUD.stopHandAnimation();

            //change to hands if this artefact is holded
            if ( toDraw == iCurrentArtefact.iArtefactType.iArtefactKind )
            {
                iCurrentArtefact = iHands;
            }
            iArtefacts.remove( toDraw );
        }

        public final boolean showAmmoInHUD()
        {
            return
            (
                    //iCurrentItemGroup == ItemGroup.EWearpon
                    iCurrentArtefact != null
                &&  iCurrentArtefact.iArtefactType.isFireArm()
            );
        }


        public final void choosePreviousWearponOrGadget( boolean byPlayer )
        {
            //browse all wearpons
            for ( int i = 0; i < iArtefacts.size(); ++i )
            {
                //find current wearpon
                if ( iCurrentArtefact == null )
                {
                    setArtefact( iArtefacts.elementAt( i ) );
                    break;
                }
                else if ( iCurrentArtefact == iArtefacts.elementAt( i ) )
                {
                    int previousIndex = i - 1;
                    if ( previousIndex < 0 )
                    {
                        //pick last wearpon
                        setArtefact( iArtefacts.elementAt( iArtefacts.size() - 1 ) );

                        //animate only if more than one wearpon is available!
                        if ( iArtefacts.size() > 1 && byPlayer )
                        {
                            Shooter.mainThread.iHUD.startHandAnimation( LibAnimation.EAnimationShow, null );
                        }
                    }
                    else
                    {
                        setArtefact( iArtefacts.elementAt( previousIndex ) );
                        if ( byPlayer ) Shooter.mainThread.iHUD.startHandAnimation( LibAnimation.EAnimationShow, null );
                    }
                    break;
                }
            }
        }

        public final void chooseNextWearponOrGadget( boolean byPlayer )
        {
            //browse all holded wearpons
            for ( int i = 0; i < iArtefacts.size(); ++i )
            {
                //find current wearpon
                if ( getArtefact() == null )
                {
                    setArtefact( iArtefacts.elementAt( i ) );
                    break;
                }
                else if ( getArtefact() == iArtefacts.elementAt( i ) )
                {
                    int nextIndex = i + 1;
                    if ( nextIndex >= iArtefacts.size() )
                    {
                        //pick first wearpon
                        setArtefact( iArtefacts.elementAt( 0 ) );

                        //animate only if more than one wearpon is available!
                        if ( iArtefacts.size() > 1 && byPlayer )
                        {
                            Shooter.mainThread.iHUD.startHandAnimation( LibAnimation.EAnimationShow, null );
                        }
                    }
                    else
                    {
                        setArtefact( iArtefacts.elementAt( nextIndex ) );
                        if ( byPlayer ) Shooter.mainThread.iHUD.startHandAnimation( LibAnimation.EAnimationShow, null );
                    }
                    break;
                }
            }
        }

        public final Artefact getArtefact()
        {
            return iCurrentArtefact;
        }

        public final ArtefactType getArtefactType()
        {
            return iCurrentArtefact.iArtefactType;
        }

        public final void setArtefact( Artefact newWearpon )
        {
            iCurrentArtefact = newWearpon;
        }

        public final void drawArtefactOrtho()
        {
            if ( iCurrentArtefact != null ) iCurrentArtefact.drawOrtho();
        }

        public final boolean isMagazineEmpty()
        {
            return ( iCurrentArtefact.iMagazineAmmo == 0 );
        }

        public final boolean isWearponDelayed()
        {
            return ( iCurrentArtefact.iCurrentDelayAfterUse > System.currentTimeMillis() );
        }

        public final boolean contains( Artefact a )
        {
            for ( Artefact art : iArtefacts )
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
            for ( Artefact art : iArtefacts )
            {
                if ( art.iArtefactType == a.iArtefactType )
                {
                    art.iMagazineAmmo = a.iMagazineAmmo;
                }
            }
        }
    }
