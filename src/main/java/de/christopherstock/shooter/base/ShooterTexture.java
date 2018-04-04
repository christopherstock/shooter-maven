
    package de.christopherstock.shooter.base;

    import  java.util.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.gl.LibGLTextureMetaData.Translucency;
    import  de.christopherstock.lib.gl.LibGLTextureImage.ImageUsage;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.g3d.*;

    /*******************************************************************************************************************
    *   The Texture-System.
    *******************************************************************************************************************/
    public class ShooterTexture
    {
        private             HashMap<Integer, LibTexture>    allTextures         = new HashMap<Integer, LibTexture>();

        private static final class TexObject
        {
            private             LibGLTextureMetaData        metaData                = null;
            private             LibGLTextureImage           image                   = null;

            private TexObject( Translucency translucency, Material material, LibTexture mask )
            {
                this.metaData = new LibGLTextureMetaData
                (
                    LibGLTextureMetaData.getNextFreeID(),
                    translucency,
                    material,
                    ( mask == null ? null : mask.getMetaData().getId() )
                );
            }

            private void init( String url )
            {
                this.image = new LibGLTextureImage
                (
                    LibImage.load( url, ShooterDebug.glImage, false ),
                    ImageUsage.ETexture,
                    ShooterDebug.glImage,
                    true
                );
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

            public LibGLTextureMetaData getMetaData()
            {
                return this.texObject.metaData;
            }

            public final LibGLTextureImage getImage()
            {
                return this.texObject.image;
            }

            public final String getName()
            {
                return this.name();
            }
        }

        public static enum BulletHoleTex implements LibTexture
        {
            EBulletHoleBrownBricks1,
            EBulletHoleGlass1mask,
            EBulletHoleGlass1(      Translucency.EHasMaskBulletHole, Material.EUndefined, EBulletHoleGlass1mask     ),
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

            private BulletHoleTex( Translucency translucency, Material material )
            {
                this( translucency, material, null );
            }

            private BulletHoleTex( Translucency translucency, Material material, LibTexture mask )
            {
                this.texObject = new TexObject( translucency, material, mask );
            }

            public LibGLTextureMetaData getMetaData()
            {
                return this.texObject.metaData;
            }

            public final LibGLTextureImage getImage()
            {
                return this.texObject.image;
            }

            public final String getName()
            {
                return this.name();
            }
        }

        public static enum WallTex implements LibTexture
        {
            EBlackMetal1(                   Material.ESteel1                                                        ),
            EBricks1(                       Material.EBrownBricks                                                   ),
            EBricks2(                       Material.ERedBricks                                                     ),
            ECarpet1,
            ECarpet2,
            EChrome1(                       Material.ESteel1                                                        ),
            EChrome2(                       Material.ESteel2                                                        ),
            ECeiling1,
            EClothDarkRed,
            EConcrete1,
            ECrate1,
            EGlass1(                        Translucency.EGlass,    Material.EGlass                                 ),
            EGrass1,
            EKeyboard1(                     Material.EPlastic1                                                      ),
            ELeather1,
            ELeather2,
            EMarble1,
            EMarble2,
            EMetal1,
            EMetal2,
            ESkyBox1_1,
            ESkyBox1_2,
            ESkyBox1_3,
            ESkyBox1_4,
            ESkyBox1_5,
            ESkyBox1_6,
            EPlant1(                        Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskPlant1         ),
            EPlant2(                        Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskPlant2         ),
            EPlastic1(                      Material.EPlastic1                                                      ),
            EPoster1(                       Material.EPlastic1                                                      ),
            EPoster2(                       Material.EPlastic1                                                      ),
            EPoster3(                       Material.EPlastic1                                                      ),
            EPoster4(                       Material.EPlastic1                                                      ),
            EPoster5(                       Material.EPlastic1                                                      ),
            EPoster6(                       Material.EPlastic1                                                      ),
            EPoster7(                       Material.EPlastic1                                                      ),
            EPoster8(                       Material.EPlastic1                                                      ),
            EPoster9(                       Material.EPlastic1                                                      ),
            ERingBook1,
            EScreen1(                       Material.EGlass                                                         ),
            EScreen2(                       Material.EGlass                                                         ),
            EScreen3(                       Material.EGlass                                                         ),
            ESliver1(                       Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskSliver1        ),
            ESodaMachine1(                  Material.EElectricDevice                                                ),
            ESodaMachine2(                  Material.EElectricDevice                                                ),
            ESodaMachine3(                  Material.EElectricDevice                                                ),
            EStones1,
            ETest,
            ETest2,
            ETest3,
            ETree1(                         Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskTree1          ),
            ETree2(                         Translucency.EHasMask,  Material.EUndefined,   Mask.EMaskTree2          ),
            EWallpaper4,
            EWhiteboard1(                   Material.EPlastic1                                                      ),
            EWood1(                         Material.EWood                                                          ),
            EWood2(                         Material.EWood                                                          ),
            ;

            private                     TexObject                   texObject           = null;

            private WallTex()
            {
                this( Translucency.EOpaque, Material.EUndefined, null );
            }

            private WallTex( Material material )
            {
                this( Translucency.EOpaque, material, null );
            }

            private WallTex( Translucency translucency, Material material )
            {
                this( translucency, material, null );
            }

            private WallTex( Translucency translucency, Material material, LibTexture mask )
            {
                this.texObject = new TexObject( translucency, material, mask );
            }

            public LibGLTextureMetaData getMetaData()
            {
                return this.texObject.metaData;
            }

            public final LibGLTextureImage getImage()
            {
                return this.texObject.image;
            }

            public final String getName()
            {
                return this.name();
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

            public final LibGLTextureImage getImage()
            {
                return this.texObject.image;
            }

            public LibGLTextureMetaData getMetaData()
            {
                return this.texObject.metaData;
            }

            public final String getName()
            {
                return this.name();
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

            public LibGLTextureMetaData getMetaData()
            {
                return this.texObject.metaData;
            }

            public final LibGLTextureImage getImage()
            {
                return this.texObject.image;
            }

            public final String getName()
            {
                return this.name();
            }
        }

        public LibTexture getByName( String name )
        {
            if ( name == null ) return null;

            for ( LibTexture texture : this.allTextures.values() )
            {
                if ( texture.getName().equals( name ) )
                {
                    return texture;
                }
            }

            return null;
        }

        protected void loadAllImages()
        {
            for ( Mask texture : Mask.values() )
            {
                texture.texObject.init( ShooterSetting.Path.ETexturesMask.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                this.allTextures.put( texture.getMetaData().getId(), texture );
            }
            for ( BulletHoleTex texture : BulletHoleTex.values() )
            {
                texture.texObject.init( ShooterSetting.Path.ETexturesBulletHole.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                this.allTextures.put( texture.getMetaData().getId(), texture );
            }
            for ( WallTex texture : WallTex.values() )
            {
                texture.texObject.init( ShooterSetting.Path.ETexturesWall.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                this.allTextures.put( texture.getMetaData().getId(), texture );
            }
            for ( BotTex texture : BotTex.values() )
            {
                texture.texObject.init( ShooterSetting.Path.ETexturesBot.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                this.allTextures.put( texture.getMetaData().getId(), texture );
            }
            for ( ItemTex texture : ItemTex.values() )
            {
                texture.texObject.init( ShooterSetting.Path.ETexturesItem.url + texture.toString() + LibExtension.jpg.getSpecifier() );
                this.allTextures.put( texture.getMetaData().getId(), texture );
            }
        }

        protected LibGLTextureImage[] getAllTextureImages()
        {
            Vector<LibGLTextureImage> ret  = new Vector<LibGLTextureImage>();

            for ( int i = 0; i < this.allTextures.values().size(); ++i )
            {
                ret.addElement( this.allTextures.get( i ).getImage() );
            }

            return ret.toArray( new LibGLTextureImage[] {} );
        }
    }
