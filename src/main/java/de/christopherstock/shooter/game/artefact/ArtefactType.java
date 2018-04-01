
    package de.christopherstock.shooter.game.artefact;

    import  java.awt.image.*;
    import  java.util.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLTextureImage.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.ShooterSetting.PlayerSettings;
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
        // cc                                                                                                                                                                                                                                                    range,                                  afterShotDelayMS,   useNeedsKeyRelease, shotsTillKeyReleaseRequired     offsetForFireFX                    zoom                        mesh                item                                            crosshair               breaks walls
        EHands(             new CloseCombat( 10 ),                                                                                                                                                                                                              PlayerSettings.RADIUS_CLOSE_COMBAT,     500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EKnife(             new CloseCombat( 15 ),                                                                                                                                                                                                              PlayerSettings.RADIUS_CLOSE_COMBAT,     500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),

        // fireArms                      ammoType                       magazine    irrDepth    irrAngle    shotCount       scRandMod   useSound                        reloadSound             bulletShellSound
        EAutoShotgun(       new FireArm( AmmoType.EShotgunShells,       5,          60,         60,         25,             5,          SoundFg.EShotShotgun1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       100.0f,                                 300,                false,              1,                              new FireArmFXOffset( 325, 100 ),   General.MAX_ZOOM * 3 / 4,   null,               null,                                           CrossHair.ECircle ,     false               ),
        ESniperRifle(       new FireArm( AmmoType.EBullet792mm,         8,          0,          0,          1,              0,          SoundFg.EShotSilenced,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       250.0f,                                 500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   General.MAX_ZOOM,           null,               null,                                           CrossHair.EPrecise,     false               ),
        ESpaz12(            new FireArm( AmmoType.EShotgunShells,       5,          100,        100,        10,             3,          SoundFg.EShotShotgun1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       Items.EShotgun1,    ItemKind.EWearponShotgun,                       CrossHair.ECircle ,     false               ),
        ERCP180(            new FireArm( AmmoType.EBullet792mm,         180,        10,         10,         1,              0,          SoundFg.EShotAssault1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  50,                 false,              1,                              new FireArmFXOffset( 315, 90  ),   General.MAX_ZOOM / 2,       null,               null,                                           CrossHair.EDefault,     false               ),
        EPistol(            new FireArm( AmmoType.EBullet9mm,           8,          10,         10,         1,              0,          SoundFg.EShotRifle1,            SoundFg.EReload1,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  150,                true,               1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EWaltherPPK(        new FireArm( AmmoType.EBullet44mm,          7,          3,          3,          1,              0,          SoundFg.EShotSilenced,          SoundFg.EReload1,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  350,                true,               1,                              new FireArmFXOffset( 263, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        EMagnum357(         new FireArm( AmmoType.EMagnumBullet357,     6,          5,          5,          1,              0,          SoundFg.EShotRifle1,            SoundFg.EReload2,       null,                  null                            ),       75.0f,                                  500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       Items.EPistol1,     ItemKind.EWearponPistol9mm,                     CrossHair.EDefault,     true                ),
        ETranquilizerGun(   new FireArm( AmmoType.ETranquilizerDarts,   8,          0,          0,          1,              0,          SoundFg.ETranquilizerShot,      SoundFg.EReload2,       SoundFg.EBulletShell1, Others.ETranquilizerDart        ),       250.0f,                                 500,                false,              1,                              new FireArmFXOffset( 225, 200 ),   General.MAX_ZOOM * 3 / 4,   null,               null,                                           CrossHair.EPrecise,     false               ),
        EHuntingRifle(      new FireArm( AmmoType.EBullet51mm,          50,         35,         35,         1,              0,          SoundFg.EShotRifle1,            SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  100,                true,               3,                              new FireArmFXOffset( 225, 200 ),   0.0f,                       null,               null,                                           CrossHair.EDefault,     false               ),
        ETommyGun(          new FireArm( AmmoType.EShotgunShells,       5,          60,         60,         15,             2,          SoundFg.EShotShotgun1,          SoundFg.EReload2,       SoundFg.EBulletShell1, null                            ),       75.0f,                                  400,                false,              1,                              new FireArmFXOffset( 325, 100 ),   General.MAX_ZOOM * 3 / 4,   null,               null,                                           CrossHair.ECircle ,     false               ),

        // gadgets
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
        public                      ArtefactKind            artefactKind                    = null;




        // -- All these fields belong to ArtefactKind !!

        /** The orthogonal image for displaying this artefact to the player. */
        private                     LibGLTextureImage       artefactImage                   = null;


        protected                   long                    delayAfterUse                   = 0;
        private                     boolean                 useNeedsKeyRelease              = false;
        public                      int                     shotsTillKeyReleaseRequired     = 0;

        private                     float                   iRange                          = 0.0f;



        protected                   LibGLTextureImage[]     fXImages                        = null;
        protected                   FireArmFXOffset         fXOffset                        = null;



        // --- All these fields are not suitable for gadgets!
        private                     float                   zoom                            = 0.0f;

        public                      Items                   itemMesh                        = null;
        public                      ItemKind                pickUpItemKind                  = null;
        private                     CrossHair               crossHair                       = null;
        private                     boolean                 breaksWalls                     = false;

        private ArtefactType( ArtefactKind artefactKind, float range, int delayAfterUse, boolean useNeedsKeyRelease, int shotsTillKeyReleaseRequired, FireArmFXOffset fXOffset, float zoom, Items itemMesh, ItemKind pickUpItemKind, CrossHair crossHair, boolean breaksWalls )
        {
            this.artefactKind = artefactKind;
            this.delayAfterUse = delayAfterUse;
            this.useNeedsKeyRelease = useNeedsKeyRelease;
            this.iRange = range;
            this.shotsTillKeyReleaseRequired = shotsTillKeyReleaseRequired;
            this.fXOffset = fXOffset;
            this.zoom = zoom;
            this.itemMesh = itemMesh;
            this.pickUpItemKind = pickUpItemKind;
            this.crossHair = crossHair;
            this.breaksWalls = breaksWalls;

            this.artefactKind.setParent( this );
        }

        public int getDamage()
        {
            return this.artefactKind.getDamage();
        }

        public static void loadImages()
        {
            for ( ArtefactType wearpon : values() )
            {
                wearpon.loadImage();
            }
        }

        private void loadImage()
        {
            BufferedImage bufferedImage   = LibImage.load( ShooterSetting.Path.EArtefact.url + this.toString() + LibExtension.png.getSpecifier(), ShooterDebug.glImage, false );
            this.artefactImage = new LibGLTextureImage( bufferedImage,   ImageUsage.EOrtho, ShooterDebug.glImage, true );
            Vector<LibGLTextureImage> fxImages = new Vector<LibGLTextureImage>();
            while ( true )
            {
                String ext = ( fxImages.size() > 0 ? String.valueOf( fxImages.size() + 1 ) : "" );
                String url = ShooterSetting.Path.EArtefactMuzzleFlash.url + this.toString() + ext + LibExtension.png.getSpecifier();

                //break if file does not exist ( allows desired different flashes )
                if (this.getClass().getResourceAsStream( url ) == null )
                {
                    break;
                }

                BufferedImage bufferedImageFX = LibImage.load( url, ShooterDebug.glImage, false );
                fxImages.add( new LibGLTextureImage( bufferedImageFX, ImageUsage.EOrtho, ShooterDebug.glImage, true ) );
            }

            this.fXImages = fxImages.toArray( new LibGLTextureImage[] {} );
        }

        public final boolean getCurrentShotNeedsKeyRelease()
        {
            return this.useNeedsKeyRelease;
        }

        public final float getShotRange()
        {
            return this.iRange;
        }

        public final boolean isFireArm()
        {
            return (this.artefactKind instanceof FireArm );
        }

        public final float getZoom()
        {
            return this.zoom;
        }

        public final CrossHair getCrossHair()
        {
            return this.crossHair;
        }

        public final boolean getBreaksWalls()
        {
            return this.breaksWalls;
        }

        public final LibGLTextureImage getArtefactImage()
        {
            return this.artefactImage;
        }
    }
