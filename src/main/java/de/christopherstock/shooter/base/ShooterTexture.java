
    package de.christopherstock.shooter.base;

    import  java.awt.image.*;
    import  java.util.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLTexture.Translucency;
    import  de.christopherstock.lib.gl.LibGLImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.g3d.*;

    /*******************************************************************************************************************
    *   The Texture-System.
    *******************************************************************************************************************/
    public class ShooterTexture
    {
        public static final class TexObject
        {
            public                      LibGLTexture        iTexture            = null;
            public                      LibGLImage          iTextureImage       = null;

            protected TexObject( Translucency aTranslucency, Material aMaterial, LibTexture aMask )
            {
                this.iTexture = new LibGLTexture
                (
                    LibGLTexture.getNextFreeID(),
                    aTranslucency,
                    aMaterial,
                    ( aMask == null ? null : new Integer( aMask.getTexture().getId() ) )
                );
            }

            public final void loadImage( String url )
            {
                //load all textures
                BufferedImage bufferedImage = LibImage.load( url, ShooterDebug.glImage, false );
                this.iTextureImage = new LibGLImage( bufferedImage, ImageUsage.ETexture, ShooterDebug.glImage, true );
            }
        }

        /*
         *  Leave this class on 1st position!
         */
        public static enum Mask implements LibTexture
        {
            EMaskFence1,
            EMaskPlant1,
            EMaskPlant2,
            EMaskTree1,
            EMaskTree2,
            EMaskSliver1,
            ;

            private                     TexObject                   iTexObject          = null;

            private Mask()
            {
                this( Translucency.EOpaque );
            }

            private Mask( Translucency translucency )
            {
                this( translucency, Material.EUndefined );
            }

            private Mask( Translucency aTranslucency, Material aMaterial )
            {
                this( aTranslucency, aMaterial, null );
            }

            private Mask( Translucency aTranslucency, Material aMaterial, LibTexture aMask )
            {
                this.iTexObject = new TexObject( aTranslucency, aMaterial, aMask );
            }

            public final void loadImage( String url )
            {
                this.iTexObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.iTexObject.iTexture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.iTexObject.iTextureImage;
            }
        }

        public static enum BulletHoleTex implements LibTexture
        {
            EBulletHoleBrownBricks1,
            EBulletHoleGlass1mask,
            EBulletHoleGlass1(            Translucency.EHasMaskBulletHole,  Material.EUndefined,  EBulletHoleGlass1mask   ),
            EBulletHoleConcrete1,
            EBulletHolePlastic1,
            EBulletHoleSteel1,
            EBulletHoleSteel2,
            EBulletHoleWood1,
            ;

            private                     TexObject                   iTexObject          = null;

            private BulletHoleTex()
            {
                this( Translucency.EOpaque );
            }

            private BulletHoleTex( Translucency translucency )
            {
                this( translucency, Material.EUndefined );
            }

            private BulletHoleTex( Translucency aTranslucency, Material aMaterial )
            {
                this( aTranslucency, aMaterial, null );
            }

            private BulletHoleTex( Translucency aTranslucency, Material aMaterial, LibTexture aMask )
            {
                this.iTexObject = new TexObject( aTranslucency, aMaterial, aMask );
            }

            public final void loadImage( String url )
            {
                this.iTexObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.iTexObject.iTexture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.iTexObject.iTextureImage;
            }
        }

        public static enum WallTex implements LibTexture
        {
            EBlackMetal1(                   Material.ESteel1                                                               ),
            EBricks1(                       Material.EBrownBricks                                                          ),
            EBricks2(                       Material.ERedBricks                                                            ),
            ECarpet1,
            ECarpet2,
            EChrome1(                       Material.ESteel1                                                               ),
            EChrome2(                       Material.ESteel2                                                               ),
            ECeiling1,
            EClothDarkRed,
            EConcrete1,
            ECrate1,
            EGlass1(                        Translucency.EGlass,    Material.EGlass                                        ),
            EGrass1,
            EKeyboard1(                     Material.EPlastic1                                                             ),
            ELeather1,
            ELeather2,
            EMarble1,
            EMarble2,
            EMetal1,
            EMetal2,
            EPlant1(                        Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskPlant1                ),
            EPlant2(                        Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskPlant2                ),
            EPlastic1(                      Material.EPlastic1                                                             ),
            EPoster1(                       Material.EPlastic1                                                             ),
            EPoster2(                       Material.EPlastic1                                                             ),
            ERingBook1,
            EScreen1(                       Material.EGlass                                        ),
            EScreen2(                       Material.EGlass                                        ),
            EScreen3(                       Material.EGlass                                        ),
            ESliver1(                       Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskSliver1               ),
            ESodaMachine1(                  Material.EElectricDevice                                                         ),
            ESodaMachine2(                  Material.EElectricDevice                                                         ),
            ESodaMachine3(                  Material.EElectricDevice                                                         ),
            EStones1,
            ETest,
            ETest2,
            ETest3,
            ETree1(                         Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskTree1                 ),
            ETree2(                         Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskTree2                 ),
            EWallpaper4,
            EWhiteboard1(                   Material.EPlastic1                                                             ),
            EWood1(                         Material.EWood                                                                 ),
            EWood2(                         Material.EWood                                                                 ),
/*
            ECactus1,
            ECactus2,
            ECar1,
            ECar2,
            ECarpet1,
            ECeiling1,
            ECeramic1,
            EContainer1,
            EContainer2,
            ECrop1,
            EFence1(                        Translucency.EHasMask,    LibGLMaterial.EUndefined,     Mask.EMaskFence1                ),
            EForest1,
            EGrass1,
            EGrass2,
            EGrass3,
            EHouse1,
            EHouse2,
            EHouse3,
            EHouse4,
            EHouse5,
            EHouse6,
            EHouse7,
            EHouse8,
            EHouse9,
            EJeansBlue1(                    Translucency.EOpaque,   LibGLMaterial.EHumanFlesh                                       ),
            EKeyboard1,
            ELeaf1,
            ELeather1,
            ELeather2,
            EMarble1,
            EMetal1,
            EPlastic1,
            EPlastic2,
            EScreen1,
            EShirtRed1(                     Translucency.EOpaque,   LibGLMaterial.EHumanFlesh                            ),
            ESign1,
            ESign2,
            ESoil1,
            ERoad1,
            ERoad2,
            ERug1,
            ESand1,
            EStones1,
            EWater1,
            EWallpaper1(                    Translucency.EOpaque,   LibGLMaterial.EConcrete                            ),
            EWallpaper2(                    Translucency.EOpaque,   LibGLMaterial.EConcrete                            ),
            EWallpaper3,
            EWood1(                         Translucency.EOpaque,   LibGLMaterial.EWood                                ),
            EWood2(                         Translucency.EOpaque,   LibGLMaterial.EWood                                ),
            EWood3(                         Translucency.EOpaque,   LibGLMaterial.EWood                                ),
            EWood4(                         Translucency.EOpaque,   LibGLMaterial.EWood                                ),
*/
            ;

            private                     TexObject                   iTexObject          = null;

            private WallTex()
            {
                this( Translucency.EOpaque, Material.EUndefined, null );
            }

            private WallTex( Material aMaterial )
            {
                this( Translucency.EOpaque, aMaterial, null );
            }

            private WallTex( Translucency aTranslucency, Material aMaterial )
            {
                this( aTranslucency, aMaterial, null );
            }

            private WallTex( Translucency aTranslucency, Material aMaterial, LibTexture aMask )
            {
                this.iTexObject = new TexObject( aTranslucency, aMaterial, aMask );
            }

            public final void loadImage( String url )
            {
                this.iTexObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.iTexObject.iTexture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.iTexObject.iTextureImage;
            }
        }

        public static enum BotTex implements LibTexture
        {
            EClothBlue1,
            EClothBlueYellowStripes1,
            EClothBlueStar1,
            EClothBlack,
            EClothBlue,
            EClothChemise1,
            EClothChemise2,
            ETorsoUpperSuiteGrey1,
            EClothCamouflageBlue,
            EClothSecurity,
            EClothSecurityBadge,
            EHairBlonde,
            EHairBlack,
            EHairRed,
            EHairLightBrown,
            EHairAshBlonde,
            EHand1,

            EFaceFemale1RoseEyesOpen,
            EFaceFemale1RoseEyesShut,
            EFaceFemale1BrownEyesOpen,
            EFaceFemale1BlackEyesOpen,
            EFaceFemale1BlackEyesShut,
            EFaceFemale1LightBrownEyesOpen,
            EFaceFemale1LightBrownEyesShut,
            EFaceFemale1BrownEyesShut,
            EFaceFemale2RoseEyesOpen,
            EFaceFemale2RoseEyesShut,
            EFaceFemale2BrownEyesOpen,
            EFaceFemale2BrownEyesShut,
            EFaceFemale2LightBrownEyesOpen,
            EFaceFemale2LightBrownEyesShut,
            EFaceFemale2BlackEyesOpen,
            EFaceFemale2BlackEyesShut,
            EFaceFemale3BlackEyesOpen,
            EFaceFemale3BlackEyesShut,
            EFaceFemale3LightBrownEyesOpen,
            EFaceFemale3LightBrownEyesShut,
            EFaceFemale3BrownEyesOpen,
            EFaceFemale3BrownEyesShut,
            EFaceFemale3RoseEyesOpen,
            EFaceFemale3RoseEyesShut,
            EFaceFemale4YellowEyesOpen,
            EFaceFemale4YellowEyesShut,

            EFaceMale1RoseEyesOpen,
            EFaceMale1RoseEyesShut,
            EFaceMale1BrownEyesOpen,
            EFaceMale1BrownEyesShut,
            EFaceMale1BlackEyesOpen,
            EFaceMale1BlackEyesShut,
            EFaceMale1LightBrownEyesOpen,
            EFaceMale1LightBrownEyesShut,

            EFaceMale2RoseEyesOpen,
            EFaceMale2RoseEyesShut,
            EFaceMale2BrownEyesOpen,
            EFaceMale2BrownEyesShut,
            EFaceMale2BlackEyesOpen,
            EFaceMale2BlackEyesShut,
            EFaceMale2LightBrownEyesOpen,
            EFaceMale2LightBrownEyesShut,

            EFaceMale2YellowEyesOpen,
            EFaceMale2YellowEyesShut,

            EFaceMale3LightBrownEyesOpen,

            EShoeWhite1,
            EShoeBlack1,
            EShoeBrown1,
            EShoeBrownDark1,
            ESkinRose,
            ESkinLightBrown,
            ESkinBrown,
            ESkinBlack,
            ESkinYellow,
            ETorsoLowerBlackTrousers,
            ETorsoLowerBlueSuite,
            ETorsoUpperSecurity,
            ETorsoUpperBlueSuite,
            ETorsoUpperChemise1,
            ETorsoUpperChemise2,
            ETorsoUpperChemise3,
            ETorsoUpperChemise4,
            ;

            private                     TexObject                   iTexObject          = null;

            private BotTex()
            {
                this( Translucency.EOpaque, Material.EHumanFlesh, null );
            }

            private BotTex( Translucency aTranslucency, Material aMaterial, LibTexture aMask )
            {
                this.iTexObject = new TexObject( aTranslucency, aMaterial, aMask );
            }

            public final void loadImage( String url )
            {
                this.iTexObject.loadImage( url );
            }

            public final LibGLImage getTextureImage()
            {
                return this.iTexObject.iTextureImage;
            }

            public LibGLTexture getTexture()
            {
                return this.iTexObject.iTexture;
            }
        }

        public static enum ItemTex implements LibTexture
        {
            EAmmoShotgunShell,
            EAmmoBullet9mm,
            EAmmoBullet51mm,
            EAmmoBullet792mm,
            EAmmoBullet44mm,
            EAmmoBulletMagnum,
            ECrackers,
            EKnife,
            EMauzer,
            EMg34,
            EApple,
            ;

            private                     TexObject                   iTexObject          = null;

            private ItemTex()
            {
                this( Translucency.EOpaque );
            }

            private ItemTex( Translucency translucency )
            {
                this( translucency, Material.EUndefined );
            }

            private ItemTex( Translucency aTranslucency, Material aMaterial )
            {
                this( aTranslucency, aMaterial, null );
            }

            private ItemTex( Translucency aTranslucency, Material aMaterial, LibTexture aMask )
            {
                this.iTexObject = new TexObject( aTranslucency, aMaterial, aMask );
            }

            public final void loadImage( String url )
            {
                this.iTexObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.iTexObject.iTexture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.iTexObject.iTextureImage;
            }
        }

        public static LibTexture getByName(String name )
        {
            if ( name == null ) return null;

            for ( Mask tex : Mask.values() )
            {
                if ( tex.name().compareToIgnoreCase( name ) == 0 )
                {
                    return tex;
                }
            }
            for ( BulletHoleTex tex : BulletHoleTex.values() )
            {
                if ( tex.name().compareToIgnoreCase( name ) == 0 )
                {
                    return tex;
                }
            }
            for ( WallTex tex : WallTex.values() )
            {
                if ( tex.name().compareToIgnoreCase( name ) == 0 )
                {
                    return tex;
                }
            }
            for ( BotTex tex : BotTex.values() )
            {
                if ( tex.name().compareToIgnoreCase( name ) == 0 )
                {
                    return tex;
                }
            }
            for ( ItemTex tex : ItemTex.values() )
            {
                if ( tex.name().compareToIgnoreCase( name ) == 0 )
                {
                    return tex;
                }
            }

            return null;
        }

        public static void loadImages()
        {
            ShooterDebug.init.out( "initUi 5.0" );
            for ( Mask texture : Mask.values() )
            {
                ShooterDebug.init.out( "> load [" + ShooterSettings.Path.ETexturesMask.iUrl + texture.toString() + LibExtension.jpg.getSpecifier() + "]" );
                texture.loadImage( ShooterSettings.Path.ETexturesMask.iUrl + texture.toString() + LibExtension.jpg.getSpecifier() );
            }
            ShooterDebug.init.out( "initUi 5.2" );
            for ( BulletHoleTex texture : BulletHoleTex.values() )
            {
                texture.loadImage( ShooterSettings.Path.ETexturesBulletHole.iUrl + texture.toString() + LibExtension.jpg.getSpecifier() );
            }
            ShooterDebug.init.out( "initUi 5.3" );
            for ( WallTex texture : WallTex.values() )
            {
                texture.loadImage( ShooterSettings.Path.ETexturesWall.iUrl + texture.toString() + LibExtension.jpg.getSpecifier() );
            }
            ShooterDebug.init.out( "initUi 5.4" );
            for ( BotTex texture : BotTex.values() )
            {
                texture.loadImage( ShooterSettings.Path.ETexturesBot.iUrl + texture.toString() + LibExtension.jpg.getSpecifier() );
            }
            ShooterDebug.init.out( "initUi 5.5" );
            for ( ItemTex texture : ItemTex.values() )
            {
                texture.loadImage( ShooterSettings.Path.ETexturesItem.iUrl + texture.toString() + LibExtension.jpg.getSpecifier() );
            }
        }

        public static LibGLImage[] getAllTextureImages()
        {
            Vector<LibGLImage> ret = new Vector<LibGLImage>();

            for ( Mask m : Mask.values() )
            {
                ret.addElement( m.getTextureImage() );
            }
            for ( BulletHoleTex b : BulletHoleTex.values() )
            {
                ret.addElement( b.getTextureImage() );
            }
            for ( WallTex w : WallTex.values() )
            {
                ret.addElement( w.getTextureImage() );
            }
            for ( BotTex c : BotTex.values() )
            {
                ret.addElement( c.getTextureImage() );
            }
            for ( ItemTex b : ItemTex.values() )
            {
                ret.addElement( b.getTextureImage() );
            }


            return ret.toArray( new LibGLImage[] {} );
        }
    }
