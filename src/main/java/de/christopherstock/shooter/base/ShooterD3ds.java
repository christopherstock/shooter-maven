
    package de.christopherstock.shooter.base;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.gl.LibTexture;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.io.d3ds.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;

    /*******************************************************************************************************************
    *   All available Discreet 3D studio max resource files. The ordinal index
    *   of the enum constant is the filename of the ascii scene export file (.ase).
    *   Remember to set the UVW mapping for all ase files being exported
    *   otherwise the texture will not be displayed correctly.
    *******************************************************************************************************************/
    public class ShooterD3ds
    {
        public static enum Others implements LibD3dsFile
        {
            EChairOffice1,
            ECrate1,
            EDoor1,
            EDeskOffice1,
            EDeskLab1,
            EFloor1x1,
            EFloor2x2,
            EFloor2x6,
            EFloor3x3,
            EFloor3x12,
            EFloor5x5,
            EFloor6x6,
            EFloor20x20,
            EFloor100x100,
            EKeyboard1,
            ELevel1PlayersOffice,
            EPoster1,
            EPoster2,
            ERingBook1,
            EScreen1,
            EShelves1,
            ESkyBox1,
            ESodaMachine1,
            ESofa1,
            EStairs3x3,
            ESprite1,
            ESprite2,
            ETranquilizerDart,
            EWall1Solid,
            EWall2Solid,
            EWall1WindowSocket,
            EWall1WindowGlass,
            EWall2WindowSocket,
            EWall2WindowGlass,
            EWall2Door,
            EWall1Fence,
            EWall2Fence,
            EWhiteboard1,
/*
            EHouse10,
            ETestTube,
            ETestFloor,
            ETestSphere,
            ECactus1,
            ECactus2,
            ECar1,
            ECeilingTiles3x10,
            EContainer1,
            EContainer2,
            ECrop1,
            ECross,
            EFence1,
            EGlassDoor1,
            EGlassChestDoor1,
            EHouse1,
            ETree1,
            EForest1,
            EFloorMarble1x1,
            EFloorMarble1x10,
            EFloorMarble3x10,
            ELevelBounds,
            ELevelOffice1,
            ELevelDesert1,
            ELevelDispatch1,
            ELevelParkingLot1,
            ELevelQatar1,
            EExecutiveOffice1,
            EExecutiveOffice1Door1,
            EExecutiveOffice2,
            EExecutiveOffice3,
            ELevelBase1,
            ETable1,
            ETable2,
            ETestFace,
            EPlant1,
            EPalm1,
            EPowerPole,
            ESign1,
            ESign2,
            ERoad1,
            ERock1,
*/
            ;

            protected           LibD3dsImporter             d3dsfile            = null;

            public final void initFile( LibD3dsImporter d3dsFile )
            {
                this.d3dsfile = d3dsFile;
            }

            public final LibD3dsImporter getFile()
            {
                return this.d3dsfile;
            }
        }

        public static enum Bots implements LibD3dsFile
        {
            EFace,
            EHat,
            EGlasses,
            EHeadFemale1,
            EHeadFemale2,
            EHeadFemale3,
            EHeadMale1,
            ELeftUpperArmMale,
            ERightUpperArmMale,
            ELeftUpperArmFemale,
            ERightUpperArmFemale,
            ERightUpperLegFemale,
            ELeftUpperLegFemale,
            ERightLowerLegFemale,
            ELeftLowerLegFemale,
            ELeftLowerArmFemale,
            ELeftLowerArmMale,
            ERightLowerArmFemale,
            ERightLowerArmMale,
            ERightUpperLegMale,
            ELeftUpperLegMale,
            ELeftLowerLegMale,
            ERightLowerLegMale,
            ETorsoMale1,
            ETorsoFemale1,
            ENeck,
            ERightHand,
            ELeftHand,
            ERightFoot,
            ELeftFoot,
            ;

            protected           LibD3dsImporter             d3dsfile                = null;

            public final void initFile( LibD3dsImporter d3dsFile )
            {
                this.d3dsfile = d3dsFile;
            }

            public final  LibD3dsImporter getFile()
            {
                return this.d3dsfile;
            }
        }

        public static enum Items implements LibD3dsFile
        {
            EAmmoBullet44mm,
            EAmmoBullet51mm,
            EAmmoBullet792mm,
            EAmmoBulletMagnum,
            EAmmoShotgunShell,
            EAmmoBullet9mm,
            EPistol1,
            EKnife,
            EShotgun1,
            ECrackers,
            EApple,
            ;

            protected           LibD3dsImporter             d3dsfile                    = null;

            public final void initFile( LibD3dsImporter d3dsFile )
            {
                this.d3dsfile = d3dsFile;
            }

            public final LibD3dsImporter getFile()
            {
                return this.d3dsfile;
            }
        }

        private                     LibDebug                    debug                   = null;

        public ShooterD3ds( LibDebug debug )
        {
            this.debug = debug;
        }

        /***************************************************************************************************************
        *   Init all 3dsmax-files.
        ***************************************************************************************************************/
        public void init()
        {
            for ( LibD3dsFile file : Bots.values() )
            {
                file.initFile( new LibD3dsImporter( ShooterSetting.Path.E3dsMaxBot.url + file.toString() + LibExtension.ase.getSpecifier(), this.debug ) );
            }

            for ( LibD3dsFile file : Others.values() )
            {
                file.initFile( new LibD3dsImporter( ShooterSetting.Path.E3dsMaxOther.url + file.toString() + LibExtension.ase.getSpecifier(), this.debug ) );
            }

            for ( LibD3dsFile file : Items.values() )
            {
                file.initFile( new LibD3dsImporter( ShooterSetting.Path.E3dsMaxItem.url + file.toString() + LibExtension.ase.getSpecifier(), this.debug ) );
            }
        }

        /***************************************************************************************************************
        *   Returns a COPY of an imported 3dsmax mesh.
        ***************************************************************************************************************/
        public LibFaceTriangle[] getFaces( LibD3dsFile file )
        {
            //copy original faces
            LibMaxTriangle[]  originalFaces = file.getFile().getFaces();
            LibFaceTriangle[] copiedFaces   = new LibFaceTriangle[ originalFaces.length ];
            for ( int i = 0; i < copiedFaces.length; ++i )
            {
                LibTexture texture = Shooter.game.engine.textures.getByName( originalFaces[ i ].textureName );

                copiedFaces[ i ] = new LibFaceTriangle
                (
                    ShooterDebug.face,
                    originalFaces[ i ],
                    (
                            texture == null
                        ?   null
                        :   texture.getMetaData()
                    ),
                    WallTex.EConcrete1,
                    General.FADE_OUT_FACES_TOTAL_TICKS,
                    ShooterSetting.Performance.ELLIPSE_SEGMENTS
                );
            }

            return copiedFaces;
        }
    }
