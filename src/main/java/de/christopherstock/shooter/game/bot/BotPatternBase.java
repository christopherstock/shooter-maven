
    package de.christopherstock.shooter.game.bot;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.ShooterSetting.*;
    import  de.christopherstock.shooter.base.ShooterTexture.*;
    import  de.christopherstock.shooter.game.objects.Bot.*;

    public enum BotPatternBase
    {
        ENaked
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  null,
            /* Tex aTexRightLowerArm    */  null,
            /* Tex aTexTorsoUpper       */  null,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  null,
            /* Tex aTexLeftLowerArm     */  null,
            /* Tex aTexRightUpperLeg    */  null,
            /* Tex aTexLeftUpperLeg     */  null,
            /* Tex aTexRightLowerLeg    */  null,
            /* Tex aTexLeftLowerLeg     */  null,
            /* Tex aTexRightHand        */  null,
            /* Tex aTexLeftHand         */  null,
            /* Tex aTexRightFoot        */  null,
            /* Tex aTexLeftFoot         */  null
        ),

        EOfficeEmployee1
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothChemise1,
            /* Tex aTexRightLowerArm    */  BotTex.EClothChemise1,
            /* Tex aTexTorsoUpper       */  BotTex.ETorsoUpperChemise1,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothChemise1,
            /* Tex aTexLeftLowerArm     */  BotTex.EClothChemise1,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothBlack,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothBlack,
            /* Tex aTexRightLowerLeg    */  BotTex.EClothBlack,
            /* Tex aTexLeftLowerLeg     */  BotTex.EClothBlack,
            /* Tex aTexRightHand        */  null,
            /* Tex aTexLeftHand         */  null,
            /* Tex aTexRightFoot        */  BotTex.EShoeBrown1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBrown1
        ),

        EOfficeEmployee2
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothChemise2,
            /* Tex aTexRightLowerArm    */  BotTex.EClothChemise2,
            /* Tex aTexTorsoUpper       */  BotTex.ETorsoUpperChemise2,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothChemise2,
            /* Tex aTexLeftLowerArm     */  BotTex.EClothChemise2,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothBlack,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothBlack,
            /* Tex aTexRightLowerLeg    */  BotTex.EClothBlack,
            /* Tex aTexLeftLowerLeg     */  BotTex.EClothBlack,
            /* Tex aTexRightHand        */  null,
            /* Tex aTexLeftHand         */  null,
            /* Tex aTexRightFoot        */  BotTex.EShoeBrown1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBrown1
        ),

        EOfficeEmployee3
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothChemise2,
            /* Tex aTexRightLowerArm    */  BotTex.EClothChemise2,
            /* Tex aTexTorsoUpper       */  BotTex.ETorsoUpperChemise4,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothChemise2,
            /* Tex aTexLeftLowerArm     */  BotTex.EClothChemise2,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothBlue,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothBlue,
            /* Tex aTexRightLowerLeg    */  BotTex.EClothBlue,
            /* Tex aTexLeftLowerLeg     */  BotTex.EClothBlue,
            /* Tex aTexRightHand        */  null,
            /* Tex aTexLeftHand         */  null,
            /* Tex aTexRightFoot        */  BotTex.EShoeBrown1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBrown1
        ),

        EOfficeEmployee4
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothChemise1,
            /* Tex aTexRightLowerArm    */  BotTex.EClothChemise1,
            /* Tex aTexTorsoUpper       */  BotTex.ETorsoUpperChemise3,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothChemise1,
            /* Tex aTexLeftLowerArm     */  BotTex.EClothChemise1,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothBlue,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothBlue,
            /* Tex aTexRightLowerLeg    */  BotTex.EClothBlue,
            /* Tex aTexLeftLowerLeg     */  BotTex.EClothBlue,
            /* Tex aTexRightHand        */  null,
            /* Tex aTexLeftHand         */  null,
            /* Tex aTexRightFoot        */  BotTex.EShoeBrown1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBrown1
        ),

        EPilot1
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothBlueStar1,
            /* Tex aTexRightLowerArm    */  BotTex.EClothBlueYellowStripes1,
            /* Tex aTexTorsoUpper       */  BotTex.ETorsoUpperBlueSuite,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothBlueStar1,
            /* Tex aTexLeftLowerArm     */  BotTex.EClothBlueYellowStripes1,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothBlue1,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothBlue1,
            /* Tex aTexRightLowerLeg    */  BotTex.EClothBlueYellowStripes1,
            /* Tex aTexLeftLowerLeg     */  BotTex.EClothBlueYellowStripes1,
            /* Tex aTexRightHand        */  null,
            /* Tex aTexLeftHand         */  null,
            /* Tex aTexRightFoot        */  BotTex.EShoeBrown1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBrown1
        ),

        ESpecialForces
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothCamouflageBlue,
            /* Tex aTexRightLowerArm    */  BotTex.EClothCamouflageBlue,
            /* Tex aTexTorsoUpper       */  BotTex.EClothCamouflageBlue,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothCamouflageBlue,
            /* Tex aTexLeftLowerArm     */  BotTex.EClothCamouflageBlue,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothCamouflageBlue,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothCamouflageBlue,
            /* Tex aTexRightLowerLeg    */  BotTex.EClothCamouflageBlue,
            /* Tex aTexLeftLowerLeg     */  BotTex.EClothCamouflageBlue,
            /* Tex aTexRightHand        */  null,
            /* Tex aTexLeftHand         */  null,
            /* Tex aTexRightFoot        */  BotTex.EShoeBlack1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBlack1
        ),

        ESecurityHeavy
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothSecurityBadge,
            /* Tex aTexRightLowerArm    */  null,
            /* Tex aTexTorsoUpper       */  BotTex.ETorsoUpperSecurity,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothSecurityBadge,
            /* Tex aTexLeftLowerArm     */  null,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothSecurity,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothSecurity,
            /* Tex aTexRightLowerLeg    */  BotTex.EClothSecurity,
            /* Tex aTexLeftLowerLeg     */  BotTex.EClothSecurity,
            /* Tex aTexRightHand        */  BotTex.EClothSecurity,
            /* Tex aTexLeftHand         */  BotTex.EClothSecurity,
            /* Tex aTexRightFoot        */  BotTex.EShoeBlack1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBlack1
        ),

        ESecurityLight
        (
            BotSettings.RADIUS,
            BotSettings.HEIGHT,
            null,
            null,
            /* Tex aTexRightUpperArm    */  BotTex.EClothSecurityBadge,
            /* Tex aTexRightLowerArm    */  null,
            /* Tex aTexTorsoUpper       */  BotTex.ETorsoUpperSecurity,
            /* Tex aTexNeck             */  null,
            /* Tex aTexLeftUpperArm     */  BotTex.EClothSecurityBadge,
            /* Tex aTexLeftLowerArm     */  null,
            /* Tex aTexRightUpperLeg    */  BotTex.EClothSecurity,
            /* Tex aTexLeftUpperLeg     */  BotTex.EClothSecurity,
            /* Tex aTexRightLowerLeg    */  null,
            /* Tex aTexLeftLowerLeg     */  null,
            /* Tex aTexRightHand        */  BotTex.EClothSecurity,
            /* Tex aTexLeftHand         */  BotTex.EClothSecurity,
            /* Tex aTexRightFoot        */  BotTex.EShoeBlack1,
            /* Tex aTexLeftFoot         */  BotTex.EShoeBlack1
        ),

        ;

        public              float                   radius                                  = 0.0f;
        public              float                   height                                  = 0.0f;

        public              BotBodyType             bodyType                                = null;
        public              BotSkinType             skinType                                = null;

        public              LibTexture              texRightUpperArm                        = null;
        public              LibTexture              texRightLowerArm                        = null;
        public              LibTexture              texTorso                                = null;
        public              LibTexture              texNeck                                 = null;
        public              LibTexture              texLeftUpperArm                         = null;
        public              LibTexture              texLeftLowerArm                         = null;
        public              LibTexture              texRightHand                            = null;
        public              LibTexture              texLeftHand                             = null;
        public              LibTexture              texRightUpperLeg                        = null;
        public              LibTexture              texLeftUpperLeg                         = null;
        public              LibTexture              texRightLowerLeg                        = null;
        public              LibTexture              texLeftLowerLeg                         = null;
        public              LibTexture              texRightFoot                            = null;
        public              LibTexture              texLeftFoot                             = null;

        private BotPatternBase
        (
            float       radius,
            float       height,
            BotBodyType bodyType,
            BotSkinType skinType,
            LibTexture  texRightUpperArm,
            LibTexture  texRightLowerArm,
            LibTexture  texTorso,
            LibTexture  texNeck,
            LibTexture  texLeftUpperArm,
            LibTexture  texLeftLowerArm,
            LibTexture  texRightUpperLeg,
            LibTexture  texLeftUpperLeg,
            LibTexture  texRightLowerLeg,
            LibTexture  texLeftLowerLeg,
            LibTexture  texRightHand,
            LibTexture  texLeftHand,
            LibTexture  texRightFoot,
            LibTexture  texLeftFoot
        )
        {
            this.radius           = radius;
            this.height           = height;

            this.skinType         = skinType;
            this.bodyType         = bodyType;

            this.texRightUpperArm = texRightUpperArm;
            this.texRightLowerArm = texRightLowerArm;
            this.texTorso         = texTorso;
            this.texNeck          = texNeck;
            this.texLeftUpperArm  = texLeftUpperArm;
            this.texLeftLowerArm  = texLeftLowerArm;
            this.texRightHand     = texRightHand;
            this.texLeftHand      = texLeftHand;
            this.texRightUpperLeg = texRightUpperLeg;
            this.texLeftUpperLeg  = texLeftUpperLeg;
            this.texRightLowerLeg = texRightLowerLeg;
            this.texLeftLowerLeg  = texLeftLowerLeg;
            this.texRightFoot     = texRightFoot;
            this.texLeftFoot      = texLeftFoot;
        }
    }
