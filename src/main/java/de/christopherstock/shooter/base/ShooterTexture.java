
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
    public abstract class ShooterTexture
    {
        private     static  HashMap<String, LibTexture>     allTextures     = new HashMap<String, LibTexture>();

        private static final class TexObject
        {
            protected           LibGLTexture                texture                 = null;
            protected           LibGLImage                  image                   = null;

            protected TexObject( Translucency aTranslucency, Material aMaterial, LibTexture aMask )
            {
                this.texture = new LibGLTexture
                (
                    LibGLTexture.getNextFreeID(),
                    aTranslucency,
                    aMaterial,
                    ( aMask == null ? null : aMask.getTexture().getId() )
                );
            }

            protected final void loadImage(String url)
            {
                //load all textures
                BufferedImage bufferedImage = LibImage.load( url, ShooterDebug.glImage, false );
                this.image = new LibGLImage( bufferedImage, ImageUsage.ETexture, ShooterDebug.glImage, true );
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

            private                         TexObject                   texObject               = null;

            private Mask()
            {
                this.texObject = new TexObject( Translucency.EOpaque, Material.EUndefined, null );
            }

            public final void loadImage( String url )
            {
                this.texObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.texObject.texture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.texObject.image;
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

            private                     TexObject                   texObject           = null;

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
                this.texObject = new TexObject( aTranslucency, aMaterial, aMask );
            }

            public final void loadImage( String url )
            {
                this.texObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.texObject.texture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.texObject.image;
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
            EPoster3(                       Material.EPlastic1                                                             ),
            EPoster4(                       Material.EPlastic1                                                             ),
            EPoster5(                       Material.EPlastic1                                                             ),
            EPoster6(                       Material.EPlastic1                                                             ),
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
            ;

            private                     TexObject                   texObject           = null;

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
                this.texObject = new TexObject( aTranslucency, aMaterial, aMask );
            }

            public final void loadImage( String url )
            {
                this.texObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.texObject.texture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.texObject.image;
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

            private                     TexObject                   texObject           = null;

            private BotTex()
            {
                this.texObject = new TexObject( Translucency.EOpaque, Material.EHumanFlesh, null );
            }

            public final void loadImage( String url )
            {
                this.texObject.loadImage( url );
            }

            public final LibGLImage getTextureImage()
            {
                return this.texObject.image;
            }

            public LibGLTexture getTexture()
            {
                return this.texObject.texture;
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

            private                     TexObject           texObject                   = null;

            private ItemTex()
            {
                this.texObject = new TexObject( Translucency.EOpaque, Material.EUndefined, null );
            }

            public final void loadImage( String url )
            {
                this.texObject.loadImage( url );
            }

            public LibGLTexture getTexture()
            {
                return this.texObject.texture;
            }

            public final LibGLImage getTextureImage()
            {
                return this.texObject.image;
            }
        }

        public static LibTexture getByName( String name )
        {
            if ( name == null ) return null;

            return allTextures.get( name );
        }

        public static void loadImages()
        {
            for ( Mask texture : Mask.values() )
            {
                texture.loadImage( ShooterSetting.Path.ETexturesMask.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                allTextures.put( texture.name(), texture );
            }
            for ( BulletHoleTex texture : BulletHoleTex.values() )
            {
                texture.loadImage( ShooterSetting.Path.ETexturesBulletHole.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                allTextures.put( texture.name(), texture );
            }
            for ( WallTex texture : WallTex.values() )
            {
                texture.loadImage( ShooterSetting.Path.ETexturesWall.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                allTextures.put( texture.name(), texture );
            }
            for ( BotTex texture : BotTex.values() )
            {
                texture.loadImage( ShooterSetting.Path.ETexturesBot.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                allTextures.put( texture.name(), texture );
            }
            for ( ItemTex texture : ItemTex.values() )
            {
                texture.loadImage( ShooterSetting.Path.ETexturesItem.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                allTextures.put( texture.name(), texture );
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
