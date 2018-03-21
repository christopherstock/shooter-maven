
    package de.christopherstock.shooter.game.bot;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.shooter.base.ShooterD3ds.*;
    import  de.christopherstock.shooter.base.ShooterTexture.*;
    import  de.christopherstock.shooter.game.bot.BotFactory.*;
    import  de.christopherstock.shooter.game.objects.Bot.BotBodyType;
    import  de.christopherstock.shooter.game.objects.Bot.BotSkinType;

    /*******************************************************************************************************************
    *   Valid mesh combinations
    *******************************************************************************************************************/
    public class BotPattern
    {
        public              BotPatternBase       iBase                   = null;
        public              BotKind                     iKind                   = null;

        public              BotSkinType                 iSkinType               = null;
        public              LibTexture                  iSkin                   = null;
        public              BotBodyType                 iBodyType               = null;

        public              Bots                        iHat                    = null;
        public              Bots                        iGlasses                = null;
        public              Bots                        iHead                   = null;
        public              Bots                        iFace                   = null;

        public              LibTexture                  iTexGlassesGlass        = null;
        public              LibTexture                  iTexGlassesHolder       = null;
        public              LibTexture                  iTexHat                 = null;
        public              LibTexture                  iTexFaceEyesOpen        = null;
        public              LibTexture                  iTexFaceEyesShut        = null;
        public              LibTexture                  iTexHair                = null;

        public              Bots                        iNeck                   = null;
        public              Bots                        iRightUpperArm          = null;
        public              Bots                        iRightLowerArm          = null;
        public              Bots                        iTorso                  = null;
        public              Bots                        iLeftUpperArm           = null;
        public              Bots                        iLeftLowerArm           = null;
        public              Bots                        iRightHand              = null;
        public              Bots                        iLeftHand               = null;
        public              Bots                        iRightUpperLeg          = null;
        public              Bots                        iLeftUpperLeg           = null;
        public              Bots                        iRightLowerLeg          = null;
        public              Bots                        iLeftLowerLeg           = null;
        public              Bots                        iRightFoot              = null;
        public              Bots                        iLeftFoot               = null;

        public BotPattern
        (
            BotPatternBase  aBase,
            BotKind                 aKind
        )
        {
            iBase               = aBase;
            iKind               = aKind;

            //assign skin and body type
            iSkinType           = iBase.iSkinType;
            iBodyType           = iBase.iBodyType;
            if ( iSkinType == null ) iSkinType = iKind.iSkinTypes[ LibMath.getRandom( 0, iKind.iSkinTypes.length - 1 ) ];
            if ( iBodyType == null ) iBodyType = iKind.iBodyTypes[ LibMath.getRandom( 0, iKind.iBodyTypes.length - 1 ) ];

            //assign skin according to type
            switch ( iSkinType )
            {
                case ERose:         iSkin = BotTex.ESkinRose;           break;
                case ELightBrown:   iSkin = BotTex.ESkinLightBrown;     break;
                case EBrown:        iSkin = BotTex.ESkinBrown;          break;
                case EBlack:        iSkin = BotTex.ESkinBlack;          break;
                case EYellow:       iSkin = BotTex.ESkinYellow;         break;
            }

            //set face
            iFace = Bots.EFace;

            //select head
            switch ( iBodyType )
            {
                case EMaleNormal:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0: iHead = Bots.EHeadMale1;   break;
                        case 1: iHead = Bots.EHeadMale1;   break;
                    }
                    break;
                }

                case EFemaleNormal:
                {
                    switch ( LibMath.getRandom( 0, 2 ) )
                    {
                        case 0: iHead = Bots.EHeadFemale1;   break;
                        case 1: iHead = Bots.EHeadFemale2;   break;
                        case 2: iHead = Bots.EHeadFemale3;   break;
                    }
                    break;
                }
            }

            //select hair
            BotTex[] possibleHairTexs = new BotTex[] {};
            switch ( iSkinType )
            {
                case EYellow:
                {
                    possibleHairTexs = new BotTex[]
                    {
                        BotTex.EHairBlack,
                        BotTex.EHairLightBrown,
                    };
                    break;
                }

                case EBlack:
                {
                    possibleHairTexs = new BotTex[]
                    {
                        BotTex.EHairBlack,
                    };
                    break;
                }

                case ERose:
                {
                    possibleHairTexs = new BotTex[]
                    {
                        BotTex.EHairBlack,
                        BotTex.EHairAshBlonde,
                        BotTex.EHairBlonde,
                        BotTex.EHairLightBrown,
                        BotTex.EHairRed,
                    };
                    break;
                }

                case ELightBrown:
                {
                    possibleHairTexs = new BotTex[]
                    {
                        BotTex.EHairBlack,
                        BotTex.EHairAshBlonde,
                        BotTex.EHairBlonde,
                        BotTex.EHairLightBrown,
                        BotTex.EHairRed,
                    };
                    break;
                }

                case EBrown:
                {
                    possibleHairTexs = new BotTex[]
                    {
                        BotTex.EHairBlack,
                        BotTex.EHairLightBrown,
                    };
                    break;
                }
            }
            iTexHair = possibleHairTexs[ ( possibleHairTexs.length == 1 ? 0 : LibMath.getRandom( 0, possibleHairTexs.length - 1 ) ) ];

            //select glasses
            boolean setGlasses = false;
            switch ( iKind.iGlasses )
            {
                case EAlways:    setGlasses = true;                                 break;
                case ENever:     setGlasses = false;                                break;
                case ESometimes: setGlasses = ( LibMath.getRandom( 0, 2 ) == 0 );   break;
                case EOften:     setGlasses = ( LibMath.getRandom( 0, 1 ) == 0 );   break;
            }
            if ( setGlasses )
            {
                iGlasses = Bots.EGlasses; iTexGlassesGlass = WallTex.EGlass1; iTexGlassesHolder = WallTex.EChrome1;
            }

            //select hat
            boolean setHat = false;
            switch ( iKind.iHat )
            {
                case EAlways:    setHat = true;                                 break;
                case ENever:     setHat = false;                                break;
                case ESometimes: setHat = ( LibMath.getRandom( 0, 2 ) == 0 );   break;
                case EOften:     setHat = ( LibMath.getRandom( 0, 1 ) == 0 );   break;

            }
            if ( setHat )
            {
                iHat = Bots.EHat; iTexHat = BotTex.EClothBlue1;
            }


            //switch color type
            switch ( iSkinType )
            {
                case EYellow:
                {
                    switch ( iBodyType )
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceFemale4YellowEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale4YellowEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceFemale4YellowEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale4YellowEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceMale2YellowEyesOpen;     iTexFaceEyesShut = BotTex.EFaceMale2YellowEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceMale2YellowEyesOpen;     iTexFaceEyesShut = BotTex.EFaceMale2YellowEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case EBlack:
                {
                    switch ( iBodyType )
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceFemale1BlackEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale1BlackEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceFemale2BlackEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale2BlackEyesShut;          break;
                                case 2: iTexFaceEyesOpen = BotTex.EFaceFemale3BlackEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale3BlackEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceMale1BlackEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale1BlackEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceMale2BlackEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale2BlackEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case EBrown:
                {
                    switch ( iBodyType )
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceFemale1BrownEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale1BrownEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceFemale2BrownEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale2BrownEyesShut;          break;
                                case 2: iTexFaceEyesOpen = BotTex.EFaceFemale3BrownEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale3BrownEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceMale1BrownEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale1BrownEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceMale2BrownEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale2BrownEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case ELightBrown:
                {
                    switch ( iBodyType )
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceFemale1LightBrownEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale1LightBrownEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceFemale2LightBrownEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale2LightBrownEyesShut;          break;
                                case 2: iTexFaceEyesOpen = BotTex.EFaceFemale3LightBrownEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale3LightBrownEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceMale1LightBrownEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale1LightBrownEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceMale2LightBrownEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale2LightBrownEyesShut;          break;
                                case 2: iTexFaceEyesOpen = BotTex.EFaceMale3LightBrownEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale3LightBrownEyesOpen;          break;
                            }
                            break;
                        }
                    }

                    break;
                }

                case ERose:
                {
                    switch ( iBodyType )
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceFemale1RoseEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale1RoseEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceFemale2RoseEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale2RoseEyesShut;          break;
                                case 2: iTexFaceEyesOpen = BotTex.EFaceFemale3RoseEyesOpen;     iTexFaceEyesShut = BotTex.EFaceFemale3RoseEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0: iTexFaceEyesOpen = BotTex.EFaceMale1RoseEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale1RoseEyesShut;          break;
                                case 1: iTexFaceEyesOpen = BotTex.EFaceMale2RoseEyesOpen;       iTexFaceEyesShut = BotTex.EFaceMale2RoseEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            //set default limb objects
            iNeck               = Bots.ENeck;
            iRightHand          = Bots.ERightHand;
            iLeftHand           = Bots.ELeftHand;
            iRightFoot          = Bots.ERightFoot;
            iLeftFoot           = Bots.ELeftFoot;

            //set limb objects according to gender
            switch ( iBodyType )
            {
                case EMaleNormal:
                {
                    iTorso          = Bots.ETorsoMale1;
                    iRightUpperArm  = Bots.ERightUpperArmMale;
                    iRightLowerArm  = Bots.ERightLowerArmMale;
                    iLeftUpperArm   = Bots.ELeftUpperArmMale;
                    iLeftLowerArm   = Bots.ELeftLowerArmMale;
                    iRightUpperLeg  = Bots.ERightUpperLegMale;
                    iLeftUpperLeg   = Bots.ELeftUpperLegMale;
                    iRightLowerLeg  = Bots.ERightLowerLegMale;
                    iLeftLowerLeg   = Bots.ELeftLowerLegMale;
                    break;
                }

                case EFemaleNormal:
                {
                    iTorso          = Bots.ETorsoFemale1;
                    iRightUpperArm  = Bots.ERightUpperArmFemale;
                    iRightLowerArm  = Bots.ERightLowerArmFemale;
                    iLeftUpperArm   = Bots.ELeftUpperArmFemale;
                    iLeftLowerArm   = Bots.ELeftLowerArmFemale;
                    iRightUpperLeg  = Bots.ERightUpperLegFemale;
                    iLeftUpperLeg   = Bots.ELeftUpperLegFemale;
                    iRightLowerLeg  = Bots.ERightLowerLegFemale;
                    iLeftLowerLeg   = Bots.ELeftLowerLegFemale;
                    break;
                }
            }
        }
    }
