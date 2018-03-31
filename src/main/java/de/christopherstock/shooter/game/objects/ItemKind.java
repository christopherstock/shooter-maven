
    package de.christopherstock.shooter.game.objects;

    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.base.ShooterD3ds.*;
    import  de.christopherstock.shooter.game.objects.ItemToPickUp.*;
    import  de.christopherstock.shooter.io.sound.*;

    /*******************************************************************************************************************
    *   All different item kinds.
    *******************************************************************************************************************/
    public enum ItemKind
    {
        EAmmoBullet9mm(                        ItemType.ECircle, ShooterSetting.ItemSettings.AMMO_RADIUS,         Items.EAmmoBullet9mm,       ShooterStrings.HUDMessages.PICKED_UP_BULLETS_9MM,    SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainAmmo20Bullet9mm,         }, true  ),
        EAmmoShotgunShell(                     ItemType.ECircle, ShooterSetting.ItemSettings.AMMO_RADIUS,         Items.EAmmoShotgunShell,    ShooterStrings.HUDMessages.PICKED_UP_SHOTGUN_SHELLS, SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainAmmo40ShotgunShells,     }, true  ),
        EAmmoBullet44mm(                       ItemType.ECircle, ShooterSetting.ItemSettings.AMMO_RADIUS,         Items.EAmmoBullet44mm,      ShooterStrings.HUDMessages.PICKED_UP_BULLETS_44MM,   SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainAmmo20Bullet44mm,        }, true  ),
        EAmmoBullet51mm(                       ItemType.ECircle, ShooterSetting.ItemSettings.AMMO_RADIUS,         Items.EAmmoBullet51mm,      ShooterStrings.HUDMessages.PICKED_UP_BULLETS_51MM,   SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainAmmo20Bullet51mm,        }, true  ),
        EAmmoBullet792mm(                      ItemType.ECircle, ShooterSetting.ItemSettings.AMMO_RADIUS,         Items.EAmmoBullet792mm,     ShooterStrings.HUDMessages.PICKED_UP_BULLETS_792MM,  SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainAmmo20Bullet792mm,       }, true  ),
        EAmmoBulletMagnum(                     ItemType.ECircle, ShooterSetting.ItemSettings.AMMO_RADIUS,         Items.EAmmoBulletMagnum,    ShooterStrings.HUDMessages.PICKED_UP_MAGNUM_BULLETS, SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainAmmo18MagnumBullet,      }, true  ),

        EWearponPistol9mm(                     ItemType.ECircle, ShooterSetting.ItemSettings.WEARPON_RADIUS,      Items.EPistol1,             ShooterStrings.HUDMessages.PICKED_UP_PISTOL_9MM,     SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainWearponWaltherPPK,       }, true  ),
        EWearponPistol2(                       ItemType.ECircle, ShooterSetting.ItemSettings.WEARPON_RADIUS,      Items.EPistol1,             ShooterStrings.HUDMessages.PICKED_UP_PISTOL_2,       SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainWearponMagnum357,        }, true  ),
        EWearponKnife(                         ItemType.ECircle, ShooterSetting.ItemSettings.WEARPON_RADIUS,      Items.EKnife,               ShooterStrings.HUDMessages.PICKED_UP_KNIFE,          SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainWearponKnife,            }, true  ),
        EItemBottle1(                          ItemType.ECircle, ShooterSetting.ItemSettings.ITEM_RADIUS,         Items.EPistol1,             ShooterStrings.HUDMessages.PICKED_UP_BOTTLE,         SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainGadgetBottle1,           }, true  ),

        EItemCrackers(                         ItemType.ECircle, ShooterSetting.ItemSettings.ITEM_RADIUS,         Items.ECrackers,            ShooterStrings.HUDMessages.PICKED_UP_CRACKERS,       SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainGadgetCrackers,          }, true  ),

        EWearponShotgun(                       ItemType.ECircle, ShooterSetting.ItemSettings.WEARPON_RADIUS,      Items.EShotgun1,            ShooterStrings.HUDMessages.PICKED_UP_SHOTGUN,        SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainWearponShotgun,          }, true  ),
        EItemHandset1(                         ItemType.ECircle, ShooterSetting.ItemSettings.ITEM_RADIUS,         Items.EPistol1,             ShooterStrings.HUDMessages.PICKED_UP_HANDSET,        SoundFg.EPickUpItem1, new ItemEvent[] { ItemEvent.EGainGadgetHandset1,          }, true  ),

        EGameEventLevel1AcclaimPlayer(         ItemType.ECircle, ShooterSetting.ItemSettings.EVENT_RADIUS,        null,                       null,                                                null,                 new GameEvent[] { BotEvent.ELevel1AcclaimPlayer,          }, false ),
        EGameEventLevel1ExplainAction(         ItemType.ECircle, ShooterSetting.ItemSettings.EVENT_RADIUS,        null,                       null,                                                null,                 new GameEvent[] { BotEvent.ELevel1ExplainAction,          }, false ),
        EGameEventLevel1ChangeToNextSection(   ItemType.ECircle, ShooterSetting.ItemSettings.LEVEL_CHANGE_RADIUS, null,                       null,                                                null,                 new GameEvent[] { ItemEvent.ELevel1ChangeToNextLevel,     }, false ),
        EGameEventLevel1ChangeToPreviousLevel( ItemType.ECircle, ShooterSetting.ItemSettings.LEVEL_CHANGE_RADIUS, null,                       null,                                                null,                 new GameEvent[] { ItemEvent.ELevel1ChangeToPreviousLevel, }, false ),

        ;

        protected           ItemType            type                = null;

        protected           float               radius              = 0.0f;
        protected           LibD3dsFile         meshFile            = null;
        protected           String              hudMessage          = null;
        protected           SoundFg             pickupSound         = null;
        protected           GameEvent[]         itemEvents          = null;
        protected           boolean             singleEvent         = false;

        private ItemKind( ItemType type, float radius, LibD3dsFile meshFile, String hudMessage, SoundFg pickupSound, GameEvent[] itemEvents, boolean singleEvent )
        {
            this.type        = type;
            this.radius      = radius;
            this.meshFile    = meshFile;
            this.hudMessage  = hudMessage;
            this.pickupSound = pickupSound;
            this.itemEvents  = itemEvents;
            this.singleEvent = singleEvent;
        }
    }
