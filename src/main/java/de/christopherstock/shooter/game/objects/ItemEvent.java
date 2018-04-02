
    package de.christopherstock.shooter.game.objects;

    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.ArtefactType;
    import  de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.level.*;
    import  de.christopherstock.shooter.ui.hud.HUDMessageManager;

    /*******************************************************************************************************************
    *   An event being invoked - not only by picking up items but on level start etc.
    *******************************************************************************************************************/
    public enum ItemEvent implements GameEvent
    {
        EGainAmmo20Bullet9mm,
        EGainAmmo40ShotgunShells,
        EGainAmmo20Bullet51mm,
        EGainAmmo20Bullet44mm,
        EGainAmmo20Bullet792mm,
        EGainAmmo120Bullet792mm,
        EGainAmmo18MagnumBullet,
        EGainAmmo20TranquilizerDarts,

        EGainWearponShotgun,
        EGainWearponWaltherPPK,
        EGainWearponMagnum357,
        EGainWearponKnife,

        EGainGadgetBottle1,
        EGainGadgetHandset1,
        EGainGadgetCrackers,
        EGainGadgetKeycard,

        EGiveCrackers,
        EGiveKeycard,

        ELevel1ChangeToNextLevel,
        ELevel1ChangeToPreviousLevel,

        ;

        public void perform( Bot bot )
        {
            switch ( this )
            {
                case EGainAmmo20Bullet44mm:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.EBullet44mm, 20 );
                    break;
                }

                case EGainAmmo18MagnumBullet:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.EMagnumBullet357, 18 );
                    break;
                }

                case EGainAmmo20Bullet9mm:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.EBullet9mm, 20 );
                    break;
                }

                case EGainAmmo20Bullet51mm:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.EBullet51mm, 20 );
                    break;
                }

                case EGainAmmo20Bullet792mm:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.EBullet792mm, 20 );
                    break;
                }

                case EGainAmmo120Bullet792mm:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.EBullet792mm, 120 );
                    break;
                }

                case EGainAmmo40ShotgunShells:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.EShotgunShells, 40 );
                    break;
                }

                case EGainAmmo20TranquilizerDarts:
                {
                    Shooter.game.engine.player.getAmmoSet().addAmmo( AmmoType.ETranquilizerDarts, 20 );
                    break;
                }

                case EGainWearponKnife:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.EKnife ) );
                    break;
                }

                case EGainWearponWaltherPPK:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.EWaltherPPK ) );
                    break;
                }

                case EGainWearponMagnum357:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.EMagnum357 ) );
                    break;
                }

                case EGainWearponShotgun:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.ESpaz12 ) );
                    break;
                }

                case EGainGadgetBottle1:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.EBottleVolvic ) );
                    break;
                }

                case EGainGadgetHandset1:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.EMobilePhoneSEW890i ) );
                    break;
                }

                case EGainGadgetCrackers:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.EChips ) );
                    break;
                }

                case EGainGadgetKeycard:
                {
                    Shooter.game.engine.player.artefactSet.deliverArtefact( new Artefact( ArtefactType.EKeycard1 ) );
                    break;
                }

                case EGiveCrackers:
                {
                    EGainGadgetCrackers.perform( null );
                    Shooter.game.engine.hudMessagesManager.showMessage( ShooterStrings.HUDMessages.TAKE_CRACKERS );
                    break;
                }

                case EGiveKeycard:
                {
                    EGainGadgetKeycard.perform( null );
                    Shooter.game.engine.hudMessagesManager.showMessage( ShooterStrings.HUDMessages.TAKE_KEYCARD );
                    break;
                }

                case ELevel1ChangeToNextLevel:
                {
                    ShooterDebug.level.out( "change to next level" );
                    Level.currentSection().orderLevelSectionChangeUp( false );
                    break;
                }

                case ELevel1ChangeToPreviousLevel:
                {
                    ShooterDebug.level.out( "change to previous level section" );
                    Level.currentSection().orderLevelSectionChangeDown( false );
                    break;
                }
            }
        }
    }
