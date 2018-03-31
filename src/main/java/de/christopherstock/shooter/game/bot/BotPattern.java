
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
        public              BotPatternBase              base                        = null;
        private             BotKind                     kind                        = null;

        private             BotSkinType                 skinType                    = null;
        public              LibTexture                  skin                        = null;
        private             BotBodyType                 bodyType                    = null;

        public              Bots                        hat                         = null;
        public              Bots                        glasses                     = null;
        public              Bots                        head                        = null;
        public              Bots                        face                        = null;

        public              LibTexture                  texGlassesGlass             = null;
        public              LibTexture                  texGlassesHolder            = null;
        public              LibTexture                  texHat                      = null;
        public              LibTexture                  texFaceEyesOpen             = null;
        public              LibTexture                  texFaceEyesShut             = null;
        public              LibTexture                  texHair                     = null;

        public              Bots                        neck                        = null;
        public              Bots                        rightUpperArm               = null;
        public              Bots                        rightLowerArm               = null;
        public              Bots                        torso                       = null;
        public              Bots                        leftUpperArm                = null;
        public              Bots                        leftLowerArm                = null;
        public              Bots                        rightHand                   = null;
        public              Bots                        leftHand                    = null;
        public              Bots                        rightUpperLeg               = null;
        public              Bots                        leftUpperLeg                = null;
        public              Bots                        rightLowerLeg               = null;
        public              Bots                        leftLowerLeg                = null;
        public              Bots                        rightFoot                   = null;
        public              Bots                        leftFoot                    = null;

        public BotPattern
        (
            BotPatternBase base,
            BotKind        kind
        )
        {
            this.base = base;
            this.kind = kind;

            //assign skin and body type
            this.skinType = this.base.skinType;
            this.bodyType = this.base.bodyType;
            if (this.skinType == null ) this.skinType = this.kind.skinTypes[ LibMath.getRandom( 0, this.kind.skinTypes.length - 1 ) ];
            if (this.bodyType == null ) this.bodyType = this.kind.bodyTypes[ LibMath.getRandom( 0, this.kind.bodyTypes.length - 1 ) ];

            //assign skin according to type
            switch (this.skinType)
            {
                case ERose:
                    this.skin = BotTex.ESkinRose;           break;
                case ELightBrown:
                    this.skin = BotTex.ESkinLightBrown;     break;
                case EBrown:
                    this.skin = BotTex.ESkinBrown;          break;
                case EBlack:
                    this.skin = BotTex.ESkinBlack;          break;
                case EYellow:
                    this.skin = BotTex.ESkinYellow;         break;
            }

            //set face
            this.face = Bots.EFace;

            //select head
            switch (this.bodyType)
            {
                case EMaleNormal:
                {
                    switch ( LibMath.getRandom( 0, 1 ) )
                    {
                        case 0:
                            this.head = Bots.EHeadMale1;   break;
                        case 1:
                            this.head = Bots.EHeadMale1;   break;
                    }
                    break;
                }

                case EFemaleNormal:
                {
                    switch ( LibMath.getRandom( 0, 2 ) )
                    {
                        case 0:
                            this.head = Bots.EHeadFemale1;   break;
                        case 1:
                            this.head = Bots.EHeadFemale2;   break;
                        case 2:
                            this.head = Bots.EHeadFemale3;   break;
                    }
                    break;
                }
            }

            //select hair
            BotTex[] possibleHairTexs = new BotTex[] {};
            switch (this.skinType)
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
            this.texHair = possibleHairTexs[ ( possibleHairTexs.length == 1 ? 0 : LibMath.getRandom( 0, possibleHairTexs.length - 1 ) ) ];

            //select glasses
            boolean setGlasses = false;
            switch (this.kind.glasses)
            {
                case EAlways:    setGlasses = true;                                 break;
                case ENever:     setGlasses = false;                                break;
                case ESometimes: setGlasses = ( LibMath.getRandom( 0, 2 ) == 0 );   break;
                case EOften:     setGlasses = ( LibMath.getRandom( 0, 1 ) == 0 );   break;
            }
            if ( setGlasses )
            {
                this.glasses = Bots.EGlasses;
                this.texGlassesGlass = WallTex.EGlass1;
                this.texGlassesHolder = WallTex.EChrome1;
            }

            //select hat
            boolean setHat = false;
            switch (this.kind.hat)
            {
                case EAlways:    setHat = true;                                 break;
                case ENever:     setHat = false;                                break;
                case ESometimes: setHat = ( LibMath.getRandom( 0, 2 ) == 0 );   break;
                case EOften:     setHat = ( LibMath.getRandom( 0, 1 ) == 0 );   break;

            }
            if ( setHat )
            {
                this.hat = Bots.EHat;
                this.texHat = BotTex.EClothBlue1;
            }


            //switch color type
            switch (this.skinType)
            {
                case EYellow:
                {
                    switch (this.bodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale4YellowEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale4YellowEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale4YellowEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale4YellowEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceMale2YellowEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale2YellowEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceMale2YellowEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale2YellowEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case EBlack:
                {
                    switch (this.bodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale1BlackEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale1BlackEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale2BlackEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale2BlackEyesShut;          break;
                                case 2:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale3BlackEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale3BlackEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceMale1BlackEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale1BlackEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceMale2BlackEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale2BlackEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case EBrown:
                {
                    switch (this.bodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale1BrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale1BrownEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale2BrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale2BrownEyesShut;          break;
                                case 2:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale3BrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale3BrownEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceMale1BrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale1BrownEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceMale2BrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale2BrownEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }

                case ELightBrown:
                {
                    switch (this.bodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale1LightBrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale1LightBrownEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale2LightBrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale2LightBrownEyesShut;          break;
                                case 2:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale3LightBrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale3LightBrownEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceMale1LightBrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale1LightBrownEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceMale2LightBrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale2LightBrownEyesShut;          break;
                                case 2:
                                    this.texFaceEyesOpen = BotTex.EFaceMale3LightBrownEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale3LightBrownEyesOpen;          break;
                            }
                            break;
                        }
                    }

                    break;
                }

                case ERose:
                {
                    switch (this.bodyType)
                    {
                        case EFemaleNormal:
                        {
                            switch ( LibMath.getRandom( 0, 2 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale1RoseEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale1RoseEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale2RoseEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale2RoseEyesShut;          break;
                                case 2:
                                    this.texFaceEyesOpen = BotTex.EFaceFemale3RoseEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceFemale3RoseEyesShut;          break;
                            }
                            break;
                        }

                        case EMaleNormal:
                        {
                            //select face
                            switch ( LibMath.getRandom( 0, 1 ) )
                            {
                                case 0:
                                    this.texFaceEyesOpen = BotTex.EFaceMale1RoseEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale1RoseEyesShut;          break;
                                case 1:
                                    this.texFaceEyesOpen = BotTex.EFaceMale2RoseEyesOpen;
                                    this.texFaceEyesShut = BotTex.EFaceMale2RoseEyesShut;          break;
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            //set default limb objects
            this.neck = Bots.ENeck;
            this.rightHand = Bots.ERightHand;
            this.leftHand = Bots.ELeftHand;
            this.rightFoot = Bots.ERightFoot;
            this.leftFoot = Bots.ELeftFoot;

            //set limb objects according to gender
            switch (this.bodyType)
            {
                case EMaleNormal:
                {
                    this.torso = Bots.ETorsoMale1;
                    this.rightUpperArm = Bots.ERightUpperArmMale;
                    this.rightLowerArm = Bots.ERightLowerArmMale;
                    this.leftUpperArm = Bots.ELeftUpperArmMale;
                    this.leftLowerArm = Bots.ELeftLowerArmMale;
                    this.rightUpperLeg = Bots.ERightUpperLegMale;
                    this.leftUpperLeg = Bots.ELeftUpperLegMale;
                    this.rightLowerLeg = Bots.ERightLowerLegMale;
                    this.leftLowerLeg = Bots.ELeftLowerLegMale;
                    break;
                }

                case EFemaleNormal:
                {
                    this.torso = Bots.ETorsoFemale1;
                    this.rightUpperArm = Bots.ERightUpperArmFemale;
                    this.rightLowerArm = Bots.ERightLowerArmFemale;
                    this.leftUpperArm = Bots.ELeftUpperArmFemale;
                    this.leftLowerArm = Bots.ELeftLowerArmFemale;
                    this.rightUpperLeg = Bots.ERightUpperLegFemale;
                    this.leftUpperLeg = Bots.ELeftUpperLegFemale;
                    this.rightLowerLeg = Bots.ERightLowerLegFemale;
                    this.leftLowerLeg = Bots.ELeftLowerLegFemale;
                    break;
                }
            }
        }
    }
