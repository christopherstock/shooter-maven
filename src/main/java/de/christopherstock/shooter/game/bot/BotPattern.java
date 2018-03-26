
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
        private BotKind                     iKind                   = null;

        private BotSkinType                 iSkinType               = null;
        public              LibTexture                  iSkin                   = null;
        private BotBodyType                 iBodyType               = null;

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
            this.iBase = aBase;
            this.iKind = aKind;

            //assign skin and body type
            this.iSkinType = this.iBase.iSkinType;
            this.iBodyType = this.iBase.iBodyType;
            if (this.iSkinType == null ) this.iSkinType = this.iKind.iSkinTypes[ LibMath.getRandom( 0, this.iKind.iSkinTypes.length - 1 ) ];
            if (this.iBodyType == null ) this.iBodyType = this.iKind.iBodyTypes[ LibMath.getRandom( 0, this.iKind.iBodyTypes.length - 1 ) ];

            //assign skin according to type
            switch (this.iSkinType)
            {
                case ERose:
                    this.iSkin = BotTex.ESkinRose;           break;
                case ELightBrown:
                    this.iSkin = BotTex.ESkinLightBrown;     break;
                case EBrown:
                    this.iSkin = BotTex.ESkinBrown;          break;
                case EBlack:
                    this.iSkin = BotTex.ESkinBlack;          break;
                case EYellow:
                    this.iSkin = BotTex.ESkinYellow;         break;
            }

            //set face
            this.iFace = Bots.EFace;

            //select head
            switch (this.iBodyType)
            {
                case EMaleNormal:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0:
                            this.iHead = Bots.EHeadMale1;   break;
                        case 1:
                            this.iHead = Bots.EHeadMale1;   break;
                    }
                    break;
                }

                case EFemaleNormal:
                {
                    switch ( LibMath.getRandom( 0, 2 ) )
                    {
                        case 0:
                            this.iHead = Bots.EHeadFemale1;   break;
                        case 1:
                            this.iHead = Bots.EHeadFemale2;   break;
                        case 2:
                            this.iHead = Bots.EHeadFemale3;   break;
                    }
                    break;
                }
            }

            //select hair
            BotTex[] possibleHairTexs = new BotTex[] {};
            switch (this.iSkinType)
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
            this.iTexHair = possibleHairTexs[ ( possibleHairTexs.length == 1 ? 0 : LibMath.getRandom( 0, possibleHairTexs.length - 1 ) ) ];

            //select glasses
            boolean setGlasses = false;
            switch (this.iKind.iGlasses )
            {
                case EAlways:    setGlasses = true;                                 break;
                case ENever:     setGlasses = false;                                break;
                case ESometimes: setGlasses = ( LibMath.getRandom( 0, 2 ) == 0 );   break;
                case EOften:     setGlasses = ( LibMath.getRandom( 0, 1 ) == 0 );   break;
            }
            if ( setGlasses )
            {
                this.iGlasses = Bots.EGlasses;
                this.iTexGlassesGlass = WallTex.EGlass1;
                this.iTexGlassesHolder = WallTex.EChrome1;
            }

            //select hat
            boolean setHat = false;
            switch (this.iKind.iHat )
            {
                case EAlways:    setHat = true;                                 break;
                case ENever:     setHat = false;                                break;
                case ESometimes: setHat = ( LibMath.getRandom( 0, 2 ) == 0 );   break;
                case EOften:     setHat = ( LibMath.getRandom( 0, 1 ) == 0 );   break;

            }
            if ( setHat )
            {
                this.iHat = Bots.EHat;
                this.iTexHat = BotTex.EClothBlue1;
            }


            //switch color type
            switch (this.iSkinType)
            {
                case EYellow:
                {
                    switch (this.iBodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale4YellowEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale4YellowEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale4YellowEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale4YellowEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale2YellowEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale2YellowEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale2YellowEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale2YellowEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case EBlack:
                {
                    switch (this.iBodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale1BlackEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale1BlackEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale2BlackEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale2BlackEyesShut;          break;
                                case 2:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale3BlackEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale3BlackEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale1BlackEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale1BlackEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale2BlackEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale2BlackEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case EBrown:
                {
                    switch (this.iBodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale1BrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale1BrownEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale2BrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale2BrownEyesShut;          break;
                                case 2:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale3BrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale3BrownEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale1BrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale1BrownEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale2BrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale2BrownEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case ELightBrown:
                {
                    switch (this.iBodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale1LightBrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale1LightBrownEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale2LightBrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale2LightBrownEyesShut;          break;
                                case 2:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale3LightBrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale3LightBrownEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale1LightBrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale1LightBrownEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale2LightBrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale2LightBrownEyesShut;          break;
                                case 2:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale3LightBrownEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale3LightBrownEyesOpen;          break;
                            }
                            break;
                        }
                    }

                    break;
                }

                case ERose:
                {
                    switch (this.iBodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale1RoseEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale1RoseEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale2RoseEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale2RoseEyesShut;          break;
                                case 2:
                                    this.iTexFaceEyesOpen = BotTex.EFaceFemale3RoseEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceFemale3RoseEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale1RoseEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale1RoseEyesShut;          break;
                                case 1:
                                    this.iTexFaceEyesOpen = BotTex.EFaceMale2RoseEyesOpen;
                                    this.iTexFaceEyesShut = BotTex.EFaceMale2RoseEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            //set default limb objects
            this.iNeck = Bots.ENeck;
            this.iRightHand = Bots.ERightHand;
            this.iLeftHand = Bots.ELeftHand;
            this.iRightFoot = Bots.ERightFoot;
            this.iLeftFoot = Bots.ELeftFoot;

            //set limb objects according to gender
            switch (this.iBodyType)
            {
                case EMaleNormal:
                {
                    this.iTorso = Bots.ETorsoMale1;
                    this.iRightUpperArm = Bots.ERightUpperArmMale;
                    this.iRightLowerArm = Bots.ERightLowerArmMale;
                    this.iLeftUpperArm = Bots.ELeftUpperArmMale;
                    this.iLeftLowerArm = Bots.ELeftLowerArmMale;
                    this.iRightUpperLeg = Bots.ERightUpperLegMale;
                    this.iLeftUpperLeg = Bots.ELeftUpperLegMale;
                    this.iRightLowerLeg = Bots.ERightLowerLegMale;
                    this.iLeftLowerLeg = Bots.ELeftLowerLegMale;
                    break;
                }

                case EFemaleNormal:
                {
                    this.iTorso = Bots.ETorsoFemale1;
                    this.iRightUpperArm = Bots.ERightUpperArmFemale;
                    this.iRightLowerArm = Bots.ERightLowerArmFemale;
                    this.iLeftUpperArm = Bots.ELeftUpperArmFemale;
                    this.iLeftLowerArm = Bots.ELeftLowerArmFemale;
                    this.iRightUpperLeg = Bots.ERightUpperLegFemale;
                    this.iLeftUpperLeg = Bots.ELeftUpperLegFemale;
                    this.iRightLowerLeg = Bots.ERightLowerLegFemale;
                    this.iLeftLowerLeg = Bots.ELeftLowerLegFemale;
                    break;
                }
            }
        }
    }
