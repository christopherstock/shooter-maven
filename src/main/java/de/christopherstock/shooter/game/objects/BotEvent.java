
    package de.christopherstock.shooter.game.objects;

    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.base.ShooterD3ds.Items;
    import  de.christopherstock.shooter.game.objects.Bot.BotJob;
    import  de.christopherstock.shooter.ui.hud.*;
    import  de.christopherstock.shooter.ui.hud.AvatarMessage.AvatarImage;

    /*******************************************************************************************************************
    *   An event being invoked - not only by picking up items but on level start etc.
    *******************************************************************************************************************/
    public enum BotEvent implements GameEvent
    {
        ELevel1AcclaimPlayer,
        ELevel1ExplainAction,

        ETakeMobileTest,
        ETakeCrackersTest,

        ;

        public void perform( Bot bot )
        {
            switch ( this )
            {
                case ELevel1AcclaimPlayer:
                {
                    //low prio messages
                    if ( AvatarMessage.queueIsEmpty() )
                    {
                        AvatarMessage.showMessage( AvatarImage.EWoman2, ShooterStrings.AvatarMessages.LEVEL1_OFFICE_PARTNER_ACCLAIM_TUTORIAL_LOOK,   ShooterSetting.Colors.EAvatarMessagePanelBgGrey.colABGR );
                        AvatarMessage.showMessage( AvatarImage.EWoman2, ShooterStrings.AvatarMessages.LEVEL1_OFFICE_PARTNER_ACCLAIM_TUTORIAL_WALK,   ShooterSetting.Colors.EAvatarMessagePanelBgGrey.colABGR );
                    }
                    break;
                }

                case ELevel1ExplainAction:
                {
                    AvatarMessage.showMessage( AvatarImage.EWoman2, ShooterStrings.AvatarMessages.LEVEL1_OFFICE_PARTNER_TUTORIAL_ACTION,   ShooterSetting.Colors.EAvatarMessagePanelBgGrey.colABGR );
                    break;
                }

                case ETakeMobileTest:
                {
                    //thank player
                    AvatarMessage.showMessage( AvatarImage.EWoman2, ShooterStrings.AvatarMessages.LEVEL1_OFFICE_PARTNER_TEST_MOBILE_GIVE,   ShooterSetting.Colors.EAvatarMessagePanelBgGrey.colABGR );

                    bot.nextItemToDeliverRightHand = Items.EApple;

                    //turn to player
                    bot.setNewJobQueue
                    (
                        new BotJob[]
                        {
                            BotJob.ESequenceTurnToPlayer,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceNodTwice,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceGrabSpringRight,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceDeliverRightEquippedItem,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceGrabBackDownRight,
                            BotJob.ESequenceDelay,
                          //BotJob.ESequenceFlushRightEquippedItem,
                            BotJob.EStandStill,
                        }
                    );
                    break;
                }

                case ETakeCrackersTest:
                {
                    //thank player
                    AvatarMessage.showMessage( AvatarImage.EWoman2, ShooterStrings.AvatarMessages.LEVEL1_OFFICE_PARTNER_TEST_CRACKERS_TRADE, ShooterSetting.Colors.EAvatarMessagePanelBgGrey.colABGR );

                    //give keycard
                    ItemEvent.EGiveKeycard.perform( bot );

                    bot.nextItemToDeliverLeftHand = Items.ECrackers;
                    bot.nextItemToDeliverRightHand = Items.EAmmoShotgunShell;

                    //turn to player
                    bot.setNewJobQueue
                    (
                        new BotJob[]
                        {
                            BotJob.ESequenceTurnToPlayer,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceNodOnce,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceGrabSpringLeft,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceDeliverLeftEquippedItem,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceGrabBackDownLeft,
                            BotJob.ESequenceDelay,

                            BotJob.ESequenceDeliverRightEquippedItem,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceGrabSpringRight,
                            BotJob.ESequenceDelay,
                          //BotJob.ESequenceFlushRightEquippedItem,
                            BotJob.ESequenceDelay,
                            BotJob.ESequenceGrabBackDownRight,
                            BotJob.EStandStill,
                        }
                    );
                    break;
                }
            }
        }
    }
