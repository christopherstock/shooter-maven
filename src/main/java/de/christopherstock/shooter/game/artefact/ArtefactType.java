
    package de.christopherstock.shooter.game.artefact;

    import  java.awt.image.*;
    import  java.util.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLImage.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.General;
    import  de.christopherstock.shooter.ShooterSettings.PlayerSettings;
    import  de.christopherstock.shooter.base.ShooterD3ds.Items;
    import  de.christopherstock.shooter.base.ShooterD3ds.Others;
    import  de.christopherstock.shooter.game.artefact.closecombat.CloseCombat;
    import  de.christopherstock.shooter.game.artefact.firearm.AmmoType;
    import  de.christopherstock.shooter.game.artefact.firearm.FireArm;
    import  de.christopherstock.shooter.game.artefact.firearm.FireArmFXOffset;
    import  de.christopherstock.shooter.game.artefact.gadget.Gadget;
    import  de.christopherstock.shooter.game.objects.*;
    import  de.christopherstock.shooter.io.sound.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   All different artefacts ( cc-wearpons, fire-arms and gadgets ) the game makes use of.
    *******************************************************************************************************************/
    public enum ArtefactType
    {
        //cc                                                                                                                                                                                                                                                    range,                                  afterShotDelayMS,   useNeedsKeyRelease, shotsTillKeyReleaseRequired     offsetForFireFX                    zoom                        mesh                item                                            crosshair               breaks walls
        EHands(             new CloseCombat( 10 ),                                                                                                                                                                                                              PlayerSettings.RADIUS_CLOSE_COMBAT,     500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EKnife(             new CloseCombat( 15 ),                                                                                                                                                                                                              PlayerSettings.RADIUS_CLOSE_COMBAT,     500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),

        //fireArms                       ammoType                       magazine    irrDepth    irrAngle    shotCount       scRandMod   useSound                        reloadSound             bulletShellSound
        EAutoShotgun(       new FireArm( AmmoType.EShotgunShells,       5,          60,         60,         25,             5,          SoundFg.EShotShotgun1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       100.0f,                                 300,                false,              1,                              new FireArmFXOffset( 325, 100 ),   General.MAX_ZOOM * 3 / 4,   null,               null,                                           CrossHair.ECircle ,     false               ),
        EWaltherPPK(        new FireArm( AmmoType.EBullet44mm,          7,          3,          3,          1,              0,          SoundFg.EShotSilenced,          SoundFg.EReload1,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  350,                true,               1,                              new FireArmFXOffset( 263, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EPistol(            new FireArm( AmmoType.EBullet9mm,           8,          10,         10,         1,              0,          SoundFg.EShotRifle1,            SoundFg.EReload1,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  150,                true,               1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EMagnum357(         new FireArm( AmmoType.EMagnumBullet357,     6,          5,          5,          1,              0,          SoundFg.EShotRifle1,            SoundFg.EReload2,       null,                  null                            ),       75.0f,                                  500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       Items.EPistol1,     ItemKind.EWearponPistol9mm,                     CrossHair.EDefault,     true                ),
        EHuntingRifle(      new FireArm( AmmoType.EBullet51mm,          50,         35,         35,         1,              0,          SoundFg.EShotRifle1,            SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  100,                true,               3,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        ESpaz12(            new FireArm( AmmoType.EShotgunShells,       5,          100,        100,        10,             3,          SoundFg.EShotShotgun1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       Items.EShotgun1,    ItemKind.EWearponShotgun,                       CrossHair.ECircle ,     false               ),
        EAutomaticShotgun(  new FireArm( AmmoType.EShotgunShells,       5,          60,         60,         15,             2,          SoundFg.EShotShotgun1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  400,                false,              1,                              new FireArmFXOffset( 325, 100 ),   General.MAX_ZOOM * 3 / 4,   null,               null,                                           CrossHair.ECircle ,     false               ),
        ERCP180(            new FireArm( AmmoType.EBullet792mm,         180,        10,         10,         1,              0,          SoundFg.EShotAssault1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  50,                 false,              1,                              new FireArmFXOffset( 315, 90  ),   General.MAX_ZOOM / 2,       null,               null,                                           CrossHair.EDefault,     false               ),
        ESniperRifle(       new FireArm( AmmoType.EBullet792mm,         8,          0,          0,          1,              0,          SoundFg.EShotSilenced,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       250.0f,                                 500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   General.MAX_ZOOM,           null,               null,                                           CrossHair.EPrecise,     false               ),
        ETranquilizerGun(   new FireArm( AmmoType.ETranquilizerDarts,   8,          0,          0,          1,              0,          SoundFg.ETranquilizerShot,      SoundFg.EReload2,       SoundFg.EBulletShell1, Others.ETranquilizerDart        ),       250.0f,                                 500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   General.MAX_ZOOM * 3 / 4,   null,               null,                                           CrossHair.EPrecise,     false               ),

        //gadgets
        EKeycard1(          new Gadget( 10, 25, 10 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EEnvelope(          new Gadget( 30, 40, 30 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EMacAir(            new Gadget( 30, 40, 30 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EAirMailLetter(     new Gadget( 30, 40, 30 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EBottleVolvic(      new Gadget( 30, 40, 30 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EMobilePhoneSEW890i(new Gadget( 30, 40, 30 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EChips(             new Gadget( 30, 40, 30 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EAdrenaline(        new Gadget( 10, 40, 10 ),                                                                                                                                                                                                           PlayerSettings.RADIUS_ACTION,           1000,               false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),

        ;

        public static enum GiveTakeAnim
        {
            ENone,
            EOffer,
            EHold,
            EDraw,
            ;
        }

        /** The kind of artefact. */
        public                      ArtefactKind       iArtefactKind                       = null;






        // -- All these fields belong to ArtefactKind !!

        /** The orthogonal image for displaying this artefact to the player. */
        private                     LibGLImage          iArtefactImage               = null;


        protected                   long                iDelayAfterUse                  = 0;
        protected                   boolean             iUseNeedsKeyRelease             = false;
        public                      int                 iShotsTillKeyReleaseRequired    = 0;

        protected                   float               iRange                          = 0.0f;



        protected                   LibGLImage[]        iFXImages                       = null;
        protected                   FireArmFXOffset     iFXOffset                     = null;



        // --- All these fields are not suitable for gadgets!
        protected                   float               iZoom                           = 0.0f;

        public                      Items               iItemMesh                       = null;
        public                      ItemKind            iPickUpItemKind                 = null;
        public                      CrossHair           iCrossHair                      = null;
        public                      boolean             iBreaksWalls                    = false;



        private ArtefactType( ArtefactKind aArtefactKind, float aRange, int aDelayAfterUse, boolean aUseNeedsKeyRelease, int aShotsTillKeyReleaseRequired, FireArmFXOffset aFXOffset, float aZoom, Items aItemMesh, ItemKind aPickUpItemKind, CrossHair aCrossHair, boolean aBreaksWalls )
        {
            this.iArtefactKind = aArtefactKind;
            this.iDelayAfterUse = aDelayAfterUse;
            this.iUseNeedsKeyRelease = aUseNeedsKeyRelease;
            this.iRange = aRange;
            this.iShotsTillKeyReleaseRequired = aShotsTillKeyReleaseRequired;
            this.iFXOffset = aFXOffset;
            this.iZoom = aZoom;
            this.iItemMesh = aItemMesh;
            this.iPickUpItemKind = aPickUpItemKind;
            this.iCrossHair = aCrossHair;
            this.iBreaksWalls = aBreaksWalls;

            this.iArtefactKind.setParent( this );
        }

        public int getDamage()
        {
            return this.iArtefactKind.getDamage();
        }

        public static void loadImages()
        {
            for ( ArtefactType wearpon : values() )
            {
                wearpon.loadImage();
            }
        }

        public final void loadImage()
        {
            BufferedImage bufferedImage   = LibImage.load( ShooterSettings.Path.EArtefact.iUrl + this.toString() + LibExtension.png.getSpecifier(), ShooterDebug.glImage, false );
            this.iArtefactImage = new LibGLImage( bufferedImage,   ImageUsage.EOrtho, ShooterDebug.glImage, true );
            Vector<LibGLImage> fxImages = new Vector<LibGLImage>();
            while ( true )
            {
                String ext = ( fxImages.size() > 0 ? String.valueOf( fxImages.size() + 1 ) : "" );
                String url = ShooterSettings.Path.EArtefactMuzzleFlash.iUrl + this.toString() + ext + LibExtension.png.getSpecifier();

                //break if file does not exist ( allows desired different flashes )
                if (this.getClass().getResourceAsStream( url ) == null )
                {
                    break;
                }

                BufferedImage bufferedImageFX = LibImage.load( url, ShooterDebug.glImage, false );
                fxImages.add( new LibGLImage( bufferedImageFX, ImageUsage.EOrtho, ShooterDebug.glImage, true ) );
            }

            this.iFXImages = fxImages.toArray( new LibGLImage[] {} );
        }

        public final boolean getCurrentShotNeedsKeyRelease()
        {
            return this.iUseNeedsKeyRelease;
        }

        public final float getShotRange()
        {
            return this.iRange;
        }

        public final boolean isFireArm()
        {
            return (this.iArtefactKind instanceof FireArm );
        }

        public final float getZoom()
        {
            return this.iZoom;
        }

        public final CrossHair getCrossHair()
        {
            return this.iCrossHair;
        }

        public final boolean getBreaksWalls()
        {
            return this.iBreaksWalls;
        }

        public final LibGLImage getArtefactImage()
        {
            return this.iArtefactImage;
        }
    }
