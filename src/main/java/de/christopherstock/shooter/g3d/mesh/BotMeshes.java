
    package de.christopherstock.shooter.g3d.mesh;

    import  java.util.*;
    import  de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.LibOffset;
    import  de.christopherstock.lib.LibRotation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterSetting.BotSettings.RotationSpeed;
    import  de.christopherstock.shooter.base.ShooterD3ds.*;
    import  de.christopherstock.shooter.base.ShooterTexture.*;
    import  de.christopherstock.shooter.game.bot.*;

    /*******************************************************************************************************************
    *   Represents a mesh.
    *******************************************************************************************************************/
    public class BotMeshes extends MeshCollection
    {
        public static enum HeadPosition
        {
            EStill,
            ENodOnce,
            ENodTwice,
            ;
        }

        public static enum HeadTarget
        {
            EDefault,
            EAcceptDown,
            ;
        }

        public static enum ArmsPosition
        {
            ERestInHip,
            EAimHighRight,
            EAimHighLeft,
            EAimHighBoth,
            EDownBoth,
            EPickUpRight,
            EPickUpLeft,
            EBackDownRight,
            EBackDownLeft,
            EReloadLeftUp,
            EReloadLeftDown,
            EReloadRightUp,
            EReloadRightDown,

            ETest1,

            EWalk,

            ;
        }

        public static enum ArmTarget
        {
            ERestInHip,
            EPointToCeiling,
            EPointToSide,
            EPickUp,
            EAimHigh,
            EHangDown,
            EAimLow,
            EEllbowBack,
            EReloadPrimaryHandUp,
            EReloadPrimaryHandDown,
            EReloadSecondaryHandUp,
            EReloadSecondaryHandDown,

            ETest1,
            ETest2,

            EWalk1,
            EWalk2,
            ;
        }

        public static enum Arm
        {
            ELeft,
            ERight,
            ;
        }

        public static enum LegsPosition
        {
            EWalk,
            EStandSpreadLegged,
            EKickRight,
            EKickLeft,
            EKickRightHigh,
            EKickLeftHigh,
            ;
        }

        public static enum LegTarget
        {
            EStandingSpreadLegged,
            EKicking,
            EKickingHigh,
            EWalk1,
            EWalk2,
            ;
        }

        public static enum Leg
        {
            ELeft,
            ERight,
            ;
        }

        private static  final   LibOffset       OFFSET_ABSOLUTE_HEAD                = new LibOffset(   0.0f,       -0.05f,     1.225f  );

        private static  final   LibOffset       OFFSET_ABSOLUTE_LEFT_UPPER_ARM      = new LibOffset(   -0.075f,    0.0f,       1.1f    );
        private static  final   LibOffset       OFFSET_ABSOLUTE_LEFT_LOWER_ARM      = new LibOffset(   -0.215f,    -0.013f,    0.95f    );
        private static  final   LibOffset       OFFSET_ABSOLUTE_LEFT_HAND           = new LibOffset(   -0.15f,     -0.053f,    0.8f    );

        private static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_UPPER_ARM     = new LibOffset(   0.075f,     0.0f,       1.1f    );
        private static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_LOWER_ARM     = new LibOffset(   0.215f,     -0.013f,     0.95f    );
        private static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_HAND          = new LibOffset(   0.15f,      -0.053f,    0.8f    );
      //public  static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_LOWER_ARM     = new LibOffset(   0.215f,     0.013f,     0.95f    );
      //public  static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_HAND          = new LibOffset(   0.15f,      -0.025f,    0.8f    );

        private static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_UPPER_LEG     = new LibOffset(   0.075f,     -0.05f,     0.68f   );
        private static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_LOWER_LEG     = new LibOffset(   0.135f,     -0.05f,     0.38f   );
        private static  final   LibOffset       OFFSET_ABSOLUTE_RIGHT_FOOT          = new LibOffset(   0.21f,      -0.03f,     0.08f   );

        private static  final   LibOffset       OFFSET_ABSOLUTE_LEFT_UPPER_LEG      = new LibOffset(   -0.075f,    -0.05f,     0.68f   );
        private static  final   LibOffset       OFFSET_ABSOLUTE_LEFT_LOWER_LEG      = new LibOffset(   -0.135f,    -0.05f,     0.38f   );
        private static  final   LibOffset       OFFSET_ABSOLUTE_LEFT_FOOT           = new LibOffset(   -0.21f,     -0.03f,     0.08f   );

        private                 boolean         limbsStandStill                     = false;

        private                 int             currentTargetPitchHead              = 0;
        private                 int             currentTargetPitchLeftArm           = 0;
        private                 int             currentTargetPitchRightArm          = 0;
        private                 int             currentTargetPitchLeftLeg           = 0;
        private                 int             currentTargetPitchRightLeg          = 0;

        private                 boolean         repeatTargetPitchesHead             = false;
        private                 boolean         repeatTargetPitchesLeftArm          = false;
        private                 boolean         repeatTargetPitchesRightArm         = false;
        public                  boolean         repeatTargetPitchesLeftLeg          = false;
        public                  boolean         repeatTargetPitchesRightLeg         = false;

        public                  boolean         completedTargetPitchesHead          = false;
        public                  boolean         completedTargetPitchesLeftArm       = false;
        public                  boolean         completedTargetPitchesRightArm      = false;

        public                  BotPattern      template                            = null;

        private                 BotMesh         equippedItemLeft                    = null;
        public                  BotMesh         equippedItemRight                   = null;

        private                 BotMesh         glasses                             = null;
        private                 BotMesh         hat                                 = null;
        private                 BotMesh         head                                = null;
        private                 BotMesh         face                                = null;
        private                 BotMesh         rightUpperArm                       = null;
        private                 BotMesh         rightLowerArm                       = null;
        private                 BotMesh         torso                               = null;
        private                 BotMesh         neck                                = null;
        private                 BotMesh         leftUpperArm                        = null;
        private                 BotMesh         leftLowerArm                        = null;
        private                 BotMesh         rightHand                           = null;
        private                 BotMesh         leftHand                            = null;
        private                 BotMesh         rightUpperLeg                       = null;
        private                 BotMesh         leftUpperLeg                        = null;
        private                 BotMesh         rightLowerLeg                       = null;
        private                 BotMesh         leftLowerLeg                        = null;
        private                 BotMesh         rightFoot                           = null;
        private                 BotMesh         leftFoot                            = null;

        private                 boolean         equipmentChanged                    = false;

        private                 HeadPosition    currentHeadPosition = null;
        private                 ArmsPosition    currentArmsPosition = null;
        private                 LegsPosition    currentLegsPosition = null;

        /***************************************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  aAnchor             The meshes' anchor point.
        ***************************************************************************************************************/
        public BotMeshes( BotPattern aTemp, LibVertex aAnchor, LibGameObject aParentGameObject, Items itemLeft, Items itemRight )
        {
            //assign template
            this.template = aTemp;

            BotPatternBase aTemplate = aTemp.base;

            if ( aTemp.hat != null )
            {
                this.hat = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.hat), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.0f );
                this.hat.changeTexture(             WallTex.ETest.getMetaData(),          aTemp.texHat.getMetaData()                  );
            }

            if ( aTemp.glasses != null )
            {
                this.glasses = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.glasses), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
                this.glasses.changeTexture(             WallTex.ETest.getMetaData(),         aTemp.texGlassesGlass.getMetaData()         );
                this.glasses.changeTexture(             BotTex.EHairBlonde.getMetaData(),    aTemp.texGlassesHolder.getMetaData()        );
            }

            //assign limbs
            this.head = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.head), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
            this.face = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.face), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
            this.rightUpperArm = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.rightUpperArm), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.rightLowerArm = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.rightLowerArm), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.torso = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.torso), aAnchor, 0.0f, 1.0f, aParentGameObject, 1.0f  );
            this.neck = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.neck), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f  );
            this.leftUpperArm = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.leftUpperArm), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.leftLowerArm = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.leftLowerArm), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.rightHand = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.rightHand), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.leftHand = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.leftHand), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.rightUpperLeg = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.rightUpperLeg), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.leftUpperLeg = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.leftUpperLeg), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.rightLowerLeg = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.rightLowerLeg), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.leftLowerLeg = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.leftLowerLeg), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.rightFoot = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.rightFoot), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.leftFoot = new BotMesh( Shooter.game.engine.d3ds.getFaces( aTemp.leftFoot), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );

            //assign textures for bot meshes
            this.rightUpperLeg.changeTexture(   WallTex.ETest.getMetaData(),          ( aTemplate.texRightUpperLeg == null ? aTemp.skin.getMetaData() : aTemplate.texRightUpperLeg.getMetaData()  )         );
            this.leftUpperLeg.changeTexture(    WallTex.ETest.getMetaData(),          ( aTemplate.texLeftUpperLeg == null ? aTemp.skin.getMetaData() : aTemplate.texLeftUpperLeg.getMetaData()  )         );
            this.rightLowerLeg.changeTexture(   WallTex.ETest.getMetaData(),          ( aTemplate.texRightLowerLeg == null ? aTemp.skin.getMetaData() : aTemplate.texRightLowerLeg.getMetaData()  )         );
            this.leftLowerLeg.changeTexture(    WallTex.ETest.getMetaData(),          ( aTemplate.texLeftLowerLeg == null ? aTemp.skin.getMetaData() : aTemplate.texLeftLowerLeg.getMetaData()  )         );
            this.leftFoot.changeTexture(        WallTex.ETest.getMetaData(),          ( aTemplate.texLeftFoot == null ? aTemp.skin.getMetaData() : aTemplate.texLeftFoot.getMetaData()  )         );
            this.rightFoot.changeTexture(       WallTex.ETest.getMetaData(),          ( aTemplate.texRightFoot == null ? aTemp.skin.getMetaData() : aTemplate.texRightFoot.getMetaData()  )         );
            this.leftUpperArm.changeTexture(    WallTex.ETest.getMetaData(),          ( aTemplate.texLeftUpperArm == null ? aTemp.skin.getMetaData() : aTemplate.texLeftUpperArm.getMetaData()  )         );
            this.rightUpperArm.changeTexture(   WallTex.ETest.getMetaData(),          ( aTemplate.texRightUpperArm == null ? aTemp.skin.getMetaData() : aTemplate.texRightUpperArm.getMetaData()  )         );
            this.leftLowerArm.changeTexture(    WallTex.ETest.getMetaData(),          ( aTemplate.texLeftLowerArm == null ? aTemp.skin.getMetaData() : aTemplate.texLeftLowerArm.getMetaData()  )         );
            this.rightLowerArm.changeTexture(   WallTex.ETest.getMetaData(),          ( aTemplate.texRightLowerArm == null ? aTemp.skin.getMetaData() : aTemplate.texRightLowerArm.getMetaData()  )         );
            this.leftHand.changeTexture(        WallTex.ETest.getMetaData(),          ( aTemplate.texLeftHand == null ? aTemp.skin.getMetaData() : aTemplate.texLeftHand.getMetaData()  )         );
            this.rightHand.changeTexture(       WallTex.ETest.getMetaData(),          ( aTemplate.texRightHand == null ? aTemp.skin.getMetaData() : aTemplate.texRightHand.getMetaData()  )         );
            this.neck.changeTexture(            WallTex.ETest.getMetaData(),          ( aTemplate.texNeck == null ? aTemp.skin.getMetaData() : aTemplate.texNeck.getMetaData()  )         );
            this.torso.changeTexture(           WallTex.ETest.getMetaData(),          ( aTemplate.texTorso == null ? aTemp.skin.getMetaData() : aTemplate.texTorso.getMetaData()  )         );

            this.head.changeTexture(            BotTex.EHairBlonde.getMetaData(),     aTemp.texHair.getMetaData()                            );
            this.face.changeTexture(            WallTex.ETest.getMetaData(),          aTemp.texFaceEyesOpen.getMetaData()                    );

            //assign initial arm position
            this.assignArmsPosition( ArmsPosition.ERestInHip             );
            this.assignLegsPosition( LegsPosition.EStandSpreadLegged     );
            this.assignHeadPosition( HeadPosition.EStill                 );

            Vector<Mesh> newMeshes = new Vector<Mesh>();
            newMeshes.add(this.head);
            newMeshes.add(this.face);
            newMeshes.add(this.rightUpperArm);
            newMeshes.add(this.rightLowerArm);
            newMeshes.add(this.rightHand);
            newMeshes.add(this.leftUpperArm);
            newMeshes.add(this.leftLowerArm);
            newMeshes.add(this.leftHand);
            newMeshes.add(this.torso);
            newMeshes.add(this.neck);
            newMeshes.add(this.rightUpperLeg);
            newMeshes.add(this.rightLowerLeg);
            newMeshes.add(this.leftLowerLeg);
            newMeshes.add(this.leftUpperLeg);
            newMeshes.add(this.rightFoot);
            newMeshes.add(this.leftFoot);

            if (this.glasses != null ) newMeshes.add(this.glasses);
            if (this.hat != null ) newMeshes.add(this.hat);
            if (this.equippedItemLeft != null ) newMeshes.add(this.equippedItemLeft);
            if (this.equippedItemRight != null ) newMeshes.add(this.equippedItemRight);

            this.meshes = newMeshes.toArray( new Mesh[] {} );

            //assign items
            this.setItem( Arm.ELeft,  itemLeft,  aAnchor, aParentGameObject );
            this.setItem( Arm.ERight, itemRight, aAnchor, aParentGameObject );
        }

        public void assignArmsPosition( ArmsPosition newArmsPosition )
        {
            this.currentArmsPosition = newArmsPosition;
            switch ( newArmsPosition )
            {
                case EPickUpRight:
                {
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EPickUp,    }, false );
                    break;
                }

                case EPickUpLeft:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EPickUp,      }, false );
                    break;
                }

                case EBackDownRight:
                {
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EHangDown,    }, false );
                    break;
                }

                case EBackDownLeft:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EHangDown,    }, false );
                    break;
                }

                case EDownBoth:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EHangDown,     }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EHangDown,     }, false );
                    break;
                }

                case EReloadLeftUp:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadPrimaryHandUp,         }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadSecondaryHandUp,       }, false );
                    break;
                }

                case EReloadRightUp:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadSecondaryHandUp,       }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadPrimaryHandUp,         }, false );
                    break;
                }

                case EReloadLeftDown:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadPrimaryHandDown,       }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadSecondaryHandDown,     }, false );
                    break;
                }

                case EReloadRightDown:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadSecondaryHandDown,     }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadPrimaryHandDown,       }, false );
                    break;
                }

                case ETest1:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EPointToSide,  }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EPointToSide,  }, false );
                    break;
                }

                case EWalk:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EWalk1, ArmTarget.EWalk2, }, true );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EWalk2, ArmTarget.EWalk1, }, true );
                    break;
                }

                case ERestInHip:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.ERestInHip, }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.ERestInHip, }, false );
                    break;
                }

                case EAimHighRight:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EHangDown,  }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EAimHigh,   }, false );
                    break;
                }

                case EAimHighLeft:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EAimHigh,  }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EHangDown,     }, false );
                    break;
                }

                case EAimHighBoth:
                {
                    this.assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EAimHigh,  }, false );
                    this.assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EAimHigh,  }, false );
                    break;
                }
            }
        }

        public boolean isAssignedArmsPosition( ArmsPosition toCheck )
        {
            return (this.currentArmsPosition == toCheck );
        }

        public boolean isAssignedLegsPosition( LegsPosition toCheck )
        {
            return (this.currentLegsPosition == toCheck );
        }

        private void assignArmTargets( Arm arm, ArmTarget[] newArmTargets, boolean repeat )
        {
            this.limbsStandStill = false;

            //reset current arm targets
            switch ( arm )
            {
                case ELeft:
                {
                    this.currentTargetPitchLeftArm = 0;
                    this.repeatTargetPitchesLeftArm = repeat;
                    this.completedTargetPitchesLeftArm = false;
                    break;
                }

                case ERight:
                {
                    this.currentTargetPitchRightArm = 0;
                    this.repeatTargetPitchesRightArm = repeat;
                    this.completedTargetPitchesRightArm = false;
                    break;
                }
            }

            LibRotation[] upperArmPitch      = new LibRotation[ newArmTargets.length ];
            LibRotation[] lowerArmPitch      = new LibRotation[ newArmTargets.length ];
            LibRotation[] handPitch          = new LibRotation[ newArmTargets.length ];

            for ( int i = 0; i < newArmTargets.length; ++i )
            {
                upperArmPitch[ i ] = this.getNewArmPosition( arm, newArmTargets[ i ] )[ 0 ];
                lowerArmPitch[ i ] = this.getNewArmPosition( arm, newArmTargets[ i ] )[ 1 ];
                handPitch[     i ] = this.getNewArmPosition( arm, newArmTargets[ i ] )[ 2 ];
            }

            //assign to specified arm
            switch ( arm )
            {
                case ELeft:
                {
                    this.leftUpperArm.setTargetPitchs(   upperArmPitch );
                    this.leftLowerArm.setTargetPitchs(   lowerArmPitch );
                    this.leftHand.setTargetPitchs(       handPitch );

                    if (this.equippedItemLeft != null ) this.equippedItemLeft.setTargetPitchs(   handPitch );

                    break;
                }

                case ERight:
                {
                    this.rightUpperArm.setTargetPitchs(  upperArmPitch );
                    this.rightLowerArm.setTargetPitchs(  lowerArmPitch );
                    this.rightHand.setTargetPitchs(      handPitch );

                    if (this.equippedItemRight != null ) this.equippedItemRight.setTargetPitchs(  handPitch );

                    break;
                }
            }
        }

        private LibRotation[] getNewArmPosition(Arm arm, ArmTarget newArmPosition )
        {
            LibRotation upperArmPitch      = new LibRotation();
            LibRotation lowerArmPitch      = new LibRotation();
            LibRotation handPitch          = new LibRotation();

            //define roations
            switch ( newArmPosition )
            {
                case EWalk1:
                {
                    upperArmPitch.set(   45.0f,  0.0f, 0.0f, RotationSpeed.LIMBS );
                    lowerArmPitch.set(   0.0f,   0.0f, 0.0f, RotationSpeed.LIMBS );
                    handPitch.set(       0.0f,   0.0f, 0.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EWalk2:
                {
                    upperArmPitch.set(   -45.0f, 0.0f, 0.0f, RotationSpeed.LIMBS );
                    lowerArmPitch.set(   0.0f,   0.0f, 0.0f, RotationSpeed.LIMBS );
                    handPitch.set(       0.0f,   0.0f, 0.0f, RotationSpeed.LIMBS );
                    break;
                }

                case ETest1:
                {
                    upperArmPitch.set(   0.0f,   0.0f, -45.0f, RotationSpeed.LIMBS );
                    lowerArmPitch.set(   -30.0f, -30.0f, 0.0f, RotationSpeed.LIMBS );
                    handPitch.set(       0.0f,   0.0f, 0.0f, RotationSpeed.LIMBS );
                    break;
                }
                case ETest2:
                {
                    upperArmPitch.set(  90.0f,      0.0f,   0.0f,       RotationSpeed.LIMBS );
                    lowerArmPitch.set(  -90.0f,     -90.0f, 0.0f,       RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       0.0f,   0.0f,       RotationSpeed.LIMBS );
                    break;
                }

                case ERestInHip:
                {
                    upperArmPitch.set(  0.0f,       0.0f,       0.0f,   RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       -10.0f,     0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       0.0f,       -90.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EPickUp:
                {
                    upperArmPitch.set(  -75.0f,     0.0f,       -30.0f, RotationSpeed.LIMBS );
                    lowerArmPitch.set(  -40.0f,     0.0f,       0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       -67.5f,     -90.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EHangDown:
                {
                    upperArmPitch.set(  0.0f,       20.0f,      0.0f,   RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       -25.0f,     0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       -10.0f,     -90.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EPointToSide:
                {
                    upperArmPitch.set(  0.0f,       -50.0f,     0.0f,   RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       -110.0f,    0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       -95.0f,     0.0f,   RotationSpeed.LIMBS );
                    break;
                }

                case EAimLow:
                {
                    upperArmPitch.set(  -50.0f,     50.0f,      0.0f,   RotationSpeed.LIMBS );
                    lowerArmPitch.set(  -60.0f,     0.0f,       0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      -95.0f,     0.0f,       0.0f,   RotationSpeed.LIMBS );
                    break;
                }

                case EEllbowBack:
                {
                    upperArmPitch.set(  0.0f,       0.0f,       90.0f,  RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       40.0f,      90.0f,  RotationSpeed.LIMBS );
                    handPitch.set(      -55.0f,     0.0f,       0.0f,   RotationSpeed.LIMBS  );
                    break;
                }

                case EAimHigh:
                {
                    upperArmPitch.set(  -100.0f,    0.0f,       0.0f,   RotationSpeed.LIMBS  );
                    lowerArmPitch.set(  0.0f,       0.0f,       0.0f,   RotationSpeed.LIMBS  );
                    handPitch.set(      90.0f,      -82.5f,     90.0f,  RotationSpeed.LIMBS );

                    break;
                }

                case EPointToCeiling:
                {
                    upperArmPitch.set(  0.0f,       -120.0f,    0.0f,   RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       120.0f,     0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      185.0f,     0.0f,       0.0f,   RotationSpeed.LIMBS );
                    break;
                }

                case EReloadPrimaryHandUp:
                {
                    upperArmPitch.set(  -45.0f,     0.0f,       0.0f,   RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       30.0f,      0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       0.0f,       0.0f,   RotationSpeed.LIMBS );
                    break;
                }

                case EReloadPrimaryHandDown:
                {
                    upperArmPitch.set(  -45.0f,     0.0f,       0.0f,   RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       50.0f,      0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       0.0f,       0.0f,   RotationSpeed.LIMBS );
                    break;
                }

                case EReloadSecondaryHandDown:
                {
                    upperArmPitch.set(  -45.0f,     0.0f,       -45.0f, RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       45.0f,      0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       0.0f,       0.0f,   RotationSpeed.LIMBS );
                    break;
                }

                case EReloadSecondaryHandUp:
                {
                    upperArmPitch.set(  -45.0f,     0.0f,       -45.0f, RotationSpeed.LIMBS );
                    lowerArmPitch.set(  0.0f,       0.0f,       0.0f,   RotationSpeed.LIMBS );
                    handPitch.set(      0.0f,       0.0f,       0.0f,   RotationSpeed.LIMBS );
                    break;
                }
            }

            //assign to specified arm
            switch ( arm )
            {
                case ELeft:
                {
                    //flip y axis for left arm!
                    upperArmPitch.set(   upperArmPitch.x, -upperArmPitch.y, -upperArmPitch.z,   RotationSpeed.UPPER_ARM );
                    lowerArmPitch.set(   lowerArmPitch.x, -lowerArmPitch.y, -lowerArmPitch.z,   RotationSpeed.LOWER_ARM );
                    handPitch.set(       handPitch.x,     -handPitch.y,     -handPitch.z,       RotationSpeed.HAND     );
                    break;
                }

                case ERight:
                {
                    upperArmPitch.set(  upperArmPitch.x, upperArmPitch.y, upperArmPitch.z,  RotationSpeed.UPPER_ARM );
                    lowerArmPitch.set(  lowerArmPitch.x, lowerArmPitch.y, lowerArmPitch.z,  RotationSpeed.LOWER_ARM );
                    handPitch.set(      handPitch.x,     handPitch.y,     handPitch.z,      RotationSpeed.HAND     );

                    break;
                }
            }

            return new LibRotation[] { upperArmPitch, lowerArmPitch, handPitch, };
        }

        public void assignLegsPosition( LegsPosition newLegsPosition )
        {
            this.currentLegsPosition = newLegsPosition;

            //reset current leg targets
            this.currentTargetPitchLeftLeg = 0;
            this.currentTargetPitchRightLeg = 0;

            //reset current leg target if the position has changed
            switch ( newLegsPosition )
            {
                case EWalk:
                {
                    this.assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EWalk1, LegTarget.EWalk2,   }   );
                    this.assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EWalk2, LegTarget.EWalk1,   }   );
                    break;
                }

                case EStandSpreadLegged:
                {
                    this.assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EStandingSpreadLegged, }   );
                    this.assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EStandingSpreadLegged, }   );
                    break;
                }

                case EKickRight:
                {
                    this.assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    this.assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EKicking,                }   );
                    break;
                }

                case EKickLeft:
                {
                    this.assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EKicking,                }   );
                    this.assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    break;
                }

                case EKickRightHigh:
                {
                    this.assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EKickingHigh,            }   );
                    this.assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    break;
                }

                case EKickLeftHigh:
                {
                    this.assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    this.assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EKickingHigh,                }   );
                    break;
                }
            }
        }

        private void assignLegTargets( Leg leg, LegTarget[] newLegTargets )
        {
            this.limbsStandStill = false;

            //reset current leg targets
            this.currentTargetPitchLeftLeg = 0;
            this.currentTargetPitchRightLeg = 0;

            LibRotation[] upperLegPitch      = new LibRotation[ newLegTargets.length ];
            LibRotation[] lowerLegPitch      = new LibRotation[ newLegTargets.length ];
            LibRotation[] footPitch          = new LibRotation[ newLegTargets.length ];

            for ( int i = 0; i < newLegTargets.length; ++i )
            {
                upperLegPitch[ i ] = this.getNewLegPosition( leg, newLegTargets[ i ] )[ 0 ];
                lowerLegPitch[ i ] = this.getNewLegPosition( leg, newLegTargets[ i ] )[ 1 ];
                footPitch[     i ] = this.getNewLegPosition( leg, newLegTargets[ i ] )[ 2 ];
            }

            //assign to specified leg
            switch ( leg )
            {
                case ELeft:
                {
                    //flip y axis for left leg!

                    this.leftUpperLeg.setTargetPitchs(   upperLegPitch );
                    this.leftLowerLeg.setTargetPitchs(   lowerLegPitch );
                    this.leftFoot.setTargetPitchs(       footPitch );
                    break;
                }

                case ERight:
                {
                    this.rightUpperLeg.setTargetPitchs(  upperLegPitch );
                    this.rightLowerLeg.setTargetPitchs(  lowerLegPitch );
                    this.rightFoot.setTargetPitchs(      footPitch );

                    break;
                }
            }
        }

        public void assignHeadPosition( HeadPosition newHeadPosition )
        {
            this.currentHeadPosition = newHeadPosition;
            switch (this.currentHeadPosition)
            {
                case EStill:
                {
                    this.assignHeadTargets( new HeadTarget[] { HeadTarget.EDefault, }, false );
                    break;
                }

                case ENodOnce:
                {
                    this.assignHeadTargets( new HeadTarget[] { HeadTarget.EDefault, HeadTarget.EAcceptDown, HeadTarget.EDefault, }, false );
                    break;
                }

                case ENodTwice:
                {
                    this.assignHeadTargets( new HeadTarget[] { HeadTarget.EDefault, HeadTarget.EAcceptDown, HeadTarget.EDefault, HeadTarget.EAcceptDown, HeadTarget.EDefault, }, false );
                    break;
                }
            }
        }

        public boolean isAssignedHeadPosition( HeadPosition toCheck )
        {
            return (this.currentHeadPosition == toCheck );
        }

        private void assignHeadTargets( HeadTarget[] newHeadTargets, boolean repeat )
        {
            this.limbsStandStill = false;

            //reset current head targets
            this.currentTargetPitchHead = 0;
            this.repeatTargetPitchesHead = repeat;
            this.completedTargetPitchesHead = false;

            LibRotation[] headPitch        = new LibRotation[ newHeadTargets.length ];

            for ( int i = 0; i < newHeadTargets.length; ++i )
            {
                headPitch[     i ] = this.getNewHeadPosition( newHeadTargets[ i ] );
            }

            this.head.setTargetPitchs(      headPitch );
            this.face.setTargetPitchs(      headPitch );

            if (this.hat != null ) this.hat.setTargetPitchs(       headPitch );
            if (this.glasses != null ) this.glasses.setTargetPitchs(   headPitch );
        }

        private LibRotation[] getNewLegPosition(Leg leg, LegTarget newLegPosition )
        {
            LibRotation upperLegPitch  = new LibRotation();
            LibRotation lowerLegPitch  = new LibRotation();
            LibRotation footPitch      = new LibRotation();

            //define roations
            switch ( newLegPosition )
            {
                case EWalk1:
                {
                    upperLegPitch.set( 25.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    lowerLegPitch.set(  0.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    footPitch.set(      0.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EWalk2:
                {
                    upperLegPitch.set( -25.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    lowerLegPitch.set(  0.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    footPitch.set(      0.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EStandingSpreadLegged:
                {
                    upperLegPitch.set(  0.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    lowerLegPitch.set(  0.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    footPitch.set(      0.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EKicking:
                {
                    upperLegPitch.set(  -90.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    lowerLegPitch.set(  -90.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    footPitch.set(      -90.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    break;
                }

                case EKickingHigh:
                {
                    upperLegPitch.set(  -135.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    lowerLegPitch.set(  -135.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    footPitch.set(      -135.0f,   0.0f,   0.0f, RotationSpeed.LIMBS );
                    break;
                }
            }

            //assign to specified leg
            switch ( leg )
            {
                case ELeft:
                {
                    //flip y axis for left leg!
                    upperLegPitch.set(   upperLegPitch.x, -upperLegPitch.y, -upperLegPitch.z, RotationSpeed.LIMBS );
                    lowerLegPitch.set(   lowerLegPitch.x, -lowerLegPitch.y, -lowerLegPitch.z, RotationSpeed.LIMBS );
                    footPitch.set(       footPitch.x,     -footPitch.y,     -footPitch.z, RotationSpeed.LIMBS     );
                    break;
                }

                case ERight:
                {
                    upperLegPitch.set(  upperLegPitch.x, upperLegPitch.y, upperLegPitch.z, RotationSpeed.LIMBS );
                    lowerLegPitch.set(  lowerLegPitch.x, lowerLegPitch.y, lowerLegPitch.z, RotationSpeed.LIMBS );
                    footPitch.set(      footPitch.x,     footPitch.y,     footPitch.z, RotationSpeed.LIMBS     );

                    break;
                }
            }

            return new LibRotation[] { upperLegPitch, lowerLegPitch, footPitch, };
        }


        private LibRotation getNewHeadPosition(HeadTarget newHeadPosition )
        {
            LibRotation headPitch      = new LibRotation();

            //define roations
            switch ( newHeadPosition )
            {
                case EDefault:
                {
                    headPitch.set(      0.0f,   0.0f,   0.0f, RotationSpeed.HEAD );
                    break;
                }

                case EAcceptDown:
                {
                    headPitch.set(      30.0f,   0.0f,   0.0f, RotationSpeed.HEAD );
                    break;
                }
            }

            //headPitch.set(   headPitch.x, -headPitch.y, -headPitch.z );

            return headPitch;
        }

        public void transformLimbs( float facingAngle, float dyingAngle, boolean faceAngleChanged, boolean playerMoved )
        {
            boolean dying = ( dyingAngle != 0.0f );

            if ( !this.limbsStandStill || dying || this.equipmentChanged || playerMoved )
            {
                //ShooterDebug.bot.out( "performance-intensive setTargetPitches .." );

                //cache the bot!
                this.limbsStandStill = this.setTargetPitches();
            }

            //set target pitches
            if ( !this.limbsStandStill || faceAngleChanged || dying || this.equipmentChanged || playerMoved )
            {
                //ShooterDebug.bot.out( "performance-intensive transformAllLimbs .." );

                //perform transformations on limbs - this costs lots of performance :(
                this.transformAllLimbs();
            }

            //turn all faces x around bot's anchor
            if ( dying )
            {
                this.turnAllLimbsX(this.getAnchor(), dyingAngle );
            }

            //turn all faces around bot's anchor
            if ( !this.limbsStandStill || faceAngleChanged || dying || this.equipmentChanged || playerMoved )
            {
                if ( facingAngle != 0.0f ) this.turnAllLimbsZ(this.getAnchor(), facingAngle );
            }

            this.equipmentChanged = false;
        }

        private boolean setTargetPitches()
        {
            boolean noLimbMoved = true;

            //head
            boolean h = this.head.reachToTargetPitch(this.currentTargetPitchHead);
            this.face.reachToTargetPitch(this.currentTargetPitchHead);
                        if (this.hat != null ) this.hat.reachToTargetPitch(this.currentTargetPitchHead);
                        if (this.glasses != null ) this.glasses.reachToTargetPitch(this.currentTargetPitchHead);

            //check if all limbs of the head reached finish
            if ( h )
            {
                if (this.head.targetPitch.size() > 1 )
                {
                    //repeat ?
                    if (this.repeatTargetPitchesHead)
                    {
                        noLimbMoved = false;
                        ++this.currentTargetPitchHead;
                        if (this.currentTargetPitchHead >= this.head.targetPitch.size() )
                        {
                            this.currentTargetPitchHead = 0;
                        }
                    }
                    else
                    {
                        if (this.currentTargetPitchHead < this.head.targetPitch.size() - 1 )
                        {
                            noLimbMoved = false;
                            ++this.currentTargetPitchHead;
                        }
                        else
                        {
                            this.completedTargetPitchesHead = true;
                        }
                    }
                }
                else
                {
                    this.completedTargetPitchesHead = true;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //right arm
            boolean ra1 = this.rightUpperArm.reachToTargetPitch(this.currentTargetPitchRightArm);
            boolean ra2 = this.rightLowerArm.reachToTargetPitch(this.currentTargetPitchRightArm);
            boolean ra3 = this.rightHand.reachToTargetPitch(this.currentTargetPitchRightArm);
                          if (this.equippedItemRight != null ) this.equippedItemRight.reachToTargetPitch(this.currentTargetPitchRightArm);
            //check if all limbs of the left arm reached finish
            if ( ra1 & ra2 & ra3 )
            {
                if (this.rightUpperArm.targetPitch.size() > 1 )
                {
                    if (this.repeatTargetPitchesRightArm)
                    {
                        noLimbMoved = false;
                        ++this.currentTargetPitchRightArm;
                        if (this.currentTargetPitchRightArm >= this.rightUpperArm.targetPitch.size() )
                        {
                            this.currentTargetPitchRightArm = 0;
                        }
                    }
                    else
                    {
                        if (this.currentTargetPitchRightArm < this.rightUpperArm.targetPitch.size() - 1 )
                        {
                            noLimbMoved = false;
                            ++this.currentTargetPitchRightArm;
                        }
                        else
                        {
                            this.completedTargetPitchesRightArm = true;
                        }
                    }
                }
                else
                {
                    this.completedTargetPitchesRightArm = true;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //left arm
            boolean la1 = this.leftUpperArm.reachToTargetPitch(this.currentTargetPitchLeftArm);
            boolean la2 = this.leftLowerArm.reachToTargetPitch(this.currentTargetPitchLeftArm);
            boolean la3 = this.leftHand.reachToTargetPitch(this.currentTargetPitchLeftArm);
                          if (this.equippedItemLeft != null ) this.equippedItemLeft.reachToTargetPitch(this.currentTargetPitchLeftArm);
            //check if all limbs of the left arm reached finish
            if ( la1 & la2 & la3 )
            {
                if (this.leftUpperArm.targetPitch.size() > 1 )
                {
                    if (this.repeatTargetPitchesLeftArm)
                    {
                        noLimbMoved = false;
                        ++this.currentTargetPitchLeftArm;
                        if (this.currentTargetPitchLeftArm >= this.leftUpperArm.targetPitch.size() )
                        {
                            this.currentTargetPitchLeftArm = 0;
                        }
                    }
                    else
                    {
                        if (this.currentTargetPitchLeftArm < this.leftUpperArm.targetPitch.size() - 1 )
                        {
                            noLimbMoved = false;
                            ++this.currentTargetPitchLeftArm;
                        }
                        else
                        {
                            this.completedTargetPitchesLeftArm = true;
                        }
                    }
                }
                else
                {
                    this.completedTargetPitchesLeftArm = true;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //right leg
            boolean rl1 = this.rightUpperLeg.reachToTargetPitch(this.currentTargetPitchRightLeg);
            boolean rl2 = this.rightLowerLeg.reachToTargetPitch(this.currentTargetPitchRightLeg);
            boolean rl3 = this.rightFoot.reachToTargetPitch(this.currentTargetPitchRightLeg);
            //check if all limbs of the right leg reached finish
            if ( rl1 & rl2 & rl3 )
            {
                if (this.rightUpperLeg.targetPitch.size() > 1 )
                {
                    noLimbMoved = false;

                    ++this.currentTargetPitchRightLeg;
                    if (this.currentTargetPitchRightLeg >= this.rightUpperLeg.targetPitch.size() )
                        this.currentTargetPitchRightLeg = 0;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //left leg
            boolean ll1 = this.leftUpperLeg.reachToTargetPitch(this.currentTargetPitchLeftLeg);
            boolean ll2 = this.leftLowerLeg.reachToTargetPitch(this.currentTargetPitchLeftLeg);
            boolean ll3 = this.leftFoot.reachToTargetPitch(this.currentTargetPitchLeftLeg);
            //check if all limbs of the left leg reached finish
            if ( ll1 & ll2 & ll3 )
            {
                if (this.leftUpperLeg.targetPitch.size() > 1 )
                {
                    noLimbMoved = false;

                    ++this.currentTargetPitchLeftLeg;
                    if (this.currentTargetPitchLeftLeg >= this.leftUpperLeg.targetPitch.size() ) this.currentTargetPitchLeftLeg = 0;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //ShooterDebug.bot.out( "all limbs done NoLimbMoved [" + noLimbMoved + "]" );

            return noLimbMoved;
        }

        private void transformAllLimbs()
        {
            //transform head
            this.head.translateAndRotateLimb(                               BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            this.face.translateAndRotateLimb(                               BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            if (this.hat != null ) this.hat.translateAndRotateLimb(        BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            if (this.glasses != null ) this.glasses.translateAndRotateLimb(    BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );

            //right arm
            LibVertex rightUpperArmAnk = this.rightUpperArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_UPPER_ARM );
            LibVertex rightLowerArmAnk = this.rightLowerArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_LOWER_ARM );
            LibVertex rightHandArmAnk  = this.rightHand.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_RIGHT_HAND      );
            if (this.equippedItemRight != null ) this.equippedItemRight.translateLimb(   BotMeshes.OFFSET_ABSOLUTE_RIGHT_HAND      );
            this.rightUpperArm.rotateAroundAnchor( rightUpperArmAnk, this.rightUpperArm.pitch);
            this.rightLowerArm.rotateAroundAnchor( rightUpperArmAnk, this.rightUpperArm.pitch);
            this.rightHand.rotateAroundAnchor(     rightUpperArmAnk, this.rightUpperArm.pitch);
            if (this.equippedItemRight != null ) this.equippedItemRight.rotateAroundAnchor(     rightUpperArmAnk, this.rightUpperArm.pitch);
            rightLowerArmAnk.rotateXYZ(this.rightUpperArm.pitch.x, this.rightUpperArm.pitch.y, this.rightUpperArm.pitch.z, rightUpperArmAnk );
            rightHandArmAnk.rotateXYZ(this.rightUpperArm.pitch.x, this.rightUpperArm.pitch.y, this.rightUpperArm.pitch.z, rightUpperArmAnk );
            this.rightLowerArm.rotateAroundAnchor( rightLowerArmAnk, this.rightLowerArm.pitch);
            this.rightHand.rotateAroundAnchor(     rightLowerArmAnk, this.rightLowerArm.pitch);
            if (this.equippedItemRight != null ) this.equippedItemRight.rotateAroundAnchor(     rightLowerArmAnk, this.rightLowerArm.pitch);
            rightHandArmAnk.rotateXYZ(this.rightLowerArm.pitch.x, this.rightLowerArm.pitch.y, this.rightLowerArm.pitch.z, rightLowerArmAnk );
            this.rightHand.rotateAroundAnchor(     rightHandArmAnk, this.rightHand.pitch);
            if (this.equippedItemRight != null ) this.equippedItemRight.rotateAroundAnchor(     rightHandArmAnk, this.rightHand.pitch);

            //left arm
            LibVertex leftUpperArmAnk = this.leftUpperArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_UPPER_ARM );
            LibVertex leftLowerArmAnk = this.leftLowerArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_LOWER_ARM );
            LibVertex leftHandArmAnk  = this.leftHand.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_LEFT_HAND      );
            if (this.equippedItemLeft != null ) this.equippedItemLeft.translateLimb(   BotMeshes.OFFSET_ABSOLUTE_LEFT_HAND      );
            this.leftUpperArm.rotateAroundAnchor( leftUpperArmAnk, this.leftUpperArm.pitch);
            this.leftLowerArm.rotateAroundAnchor( leftUpperArmAnk, this.leftUpperArm.pitch);
            this.leftHand.rotateAroundAnchor(     leftUpperArmAnk, this.leftUpperArm.pitch);
            if (this.equippedItemLeft != null ) this.equippedItemLeft.rotateAroundAnchor(     leftUpperArmAnk, this.leftUpperArm.pitch);
            leftLowerArmAnk.rotateXYZ(this.leftUpperArm.pitch.x, this.leftUpperArm.pitch.y, this.leftUpperArm.pitch.z, leftUpperArmAnk );
            leftHandArmAnk.rotateXYZ(this.leftUpperArm.pitch.x, this.leftUpperArm.pitch.y, this.leftUpperArm.pitch.z, leftUpperArmAnk );
            this.leftLowerArm.rotateAroundAnchor( leftLowerArmAnk, this.leftLowerArm.pitch);
            this.leftHand.rotateAroundAnchor(     leftLowerArmAnk, this.leftLowerArm.pitch);
            if (this.equippedItemLeft != null ) this.equippedItemLeft.rotateAroundAnchor(     leftLowerArmAnk, this.leftLowerArm.pitch);
            leftHandArmAnk.rotateXYZ(this.leftLowerArm.pitch.x, this.leftLowerArm.pitch.y, this.leftLowerArm.pitch.z, leftLowerArmAnk );
            this.leftHand.rotateAroundAnchor(     leftHandArmAnk, this.leftHand.pitch);
            if (this.equippedItemLeft != null ) this.equippedItemLeft.rotateAroundAnchor(     leftHandArmAnk, this.leftHand.pitch);

            //right leg
            LibVertex rightUpperLegAnk = this.rightUpperLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_UPPER_LEG );
            LibVertex rightLowerLegAnk = this.rightLowerLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_LOWER_LEG );
            LibVertex rightfootLegAnk  = this.rightFoot.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_RIGHT_FOOT      );
            this.rightUpperLeg.rotateAroundAnchor( rightUpperLegAnk, this.rightUpperLeg.pitch);
            this.rightLowerLeg.rotateAroundAnchor( rightUpperLegAnk, this.rightUpperLeg.pitch);
            this.rightFoot.rotateAroundAnchor(     rightUpperLegAnk, this.rightUpperLeg.pitch);
            rightLowerLegAnk.rotateXYZ(this.rightUpperLeg.pitch.x, this.rightUpperLeg.pitch.y, this.rightUpperLeg.pitch.z, rightUpperLegAnk );
            rightfootLegAnk.rotateXYZ(this.rightUpperLeg.pitch.x, this.rightUpperLeg.pitch.y, this.rightUpperLeg.pitch.z, rightUpperLegAnk );
            this.rightLowerLeg.rotateAroundAnchor( rightLowerLegAnk, this.rightLowerLeg.pitch);
            this.rightFoot.rotateAroundAnchor(     rightLowerLegAnk, this.rightLowerLeg.pitch);
            rightfootLegAnk.rotateXYZ(this.rightLowerLeg.pitch.x, this.rightLowerLeg.pitch.y, this.rightLowerLeg.pitch.z, rightLowerLegAnk );
            this.rightFoot.rotateAroundAnchor(     rightfootLegAnk, this.rightFoot.pitch);

            //left leg
            LibVertex leftUpperLegAnk = this.leftUpperLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_UPPER_LEG );
            LibVertex leftLowerLegAnk = this.leftLowerLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_LOWER_LEG );
            LibVertex leftfootLegAnk  = this.leftFoot.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_LEFT_FOOT      );
            this.leftUpperLeg.rotateAroundAnchor( leftUpperLegAnk, this.leftUpperLeg.pitch);
            this.leftLowerLeg.rotateAroundAnchor( leftUpperLegAnk, this.leftUpperLeg.pitch);
            this.leftFoot.rotateAroundAnchor(     leftUpperLegAnk, this.leftUpperLeg.pitch);
            leftLowerLegAnk.rotateXYZ(this.leftUpperLeg.pitch.x, this.leftUpperLeg.pitch.y, this.leftUpperLeg.pitch.z, leftUpperLegAnk );
            leftfootLegAnk.rotateXYZ(this.leftUpperLeg.pitch.x, this.leftUpperLeg.pitch.y, this.leftUpperLeg.pitch.z, leftUpperLegAnk );
            this.leftLowerLeg.rotateAroundAnchor( leftLowerLegAnk, this.leftLowerLeg.pitch);
            this.leftFoot.rotateAroundAnchor(     leftLowerLegAnk, this.leftLowerLeg.pitch);
            leftfootLegAnk.rotateXYZ(this.leftLowerLeg.pitch.x, this.leftLowerLeg.pitch.y, this.leftLowerLeg.pitch.z, leftLowerLegAnk );
            this.leftFoot.rotateAroundAnchor(     leftfootLegAnk, this.leftFoot.pitch);

            //torso and neck are not transformed neither translated
            this.torso.translateLimb( new LibOffset( 0.0f, 0.0f, 0.0f ) );
            this.neck.translateLimb(  new LibOffset( 0.0f, 0.0f, 0.0f ) );
        }

        private void turnAllLimbsX(LibVertex botAnchor, float angleX)
        {
            for ( Mesh mesh : this.meshes)
            {
                mesh.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, angleX, 0.0f, 0.0f, botAnchor, LibTransformationMode.ETransformedToTransformed );
            }
        }

        private void turnAllLimbsZ(LibVertex botAnchor, float facingAngle)
        {
            for ( Mesh mesh : this.meshes)
            {
                mesh.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, facingAngle, botAnchor, LibTransformationMode.ETransformedToTransformed );
            }
        }

        public void changeFaceTexture(LibGLTextureMetaData oldTex, LibGLTextureMetaData newTex )
        {
            this.face.changeTexture( oldTex, newTex );
        }

        public void fadeOutAllFaces()
        {
            if (this.glasses != null ) this.glasses.fadeOutAllFaces();
            if (this.hat != null ) this.hat.fadeOutAllFaces();
            if (this.head != null ) this.head.fadeOutAllFaces();
            if (this.face != null ) this.face.fadeOutAllFaces();
            if (this.rightUpperArm != null ) this.rightUpperArm.fadeOutAllFaces();
            if (this.rightLowerArm != null ) this.rightLowerArm.fadeOutAllFaces();
            if (this.torso != null ) this.torso.fadeOutAllFaces();
            if (this.neck != null ) this.neck.fadeOutAllFaces();
            if (this.leftUpperArm != null ) this.leftUpperArm.fadeOutAllFaces();
            if (this.leftLowerArm != null ) this.leftLowerArm.fadeOutAllFaces();
            if (this.rightHand != null ) this.rightHand.fadeOutAllFaces();
            if (this.leftHand != null ) this.leftHand.fadeOutAllFaces();
            if (this.rightUpperLeg != null ) this.rightUpperLeg.fadeOutAllFaces();
            if (this.leftUpperLeg != null ) this.leftUpperLeg.fadeOutAllFaces();
            if (this.rightLowerLeg != null ) this.rightLowerLeg.fadeOutAllFaces();
            if (this.leftLowerLeg != null ) this.leftLowerLeg.fadeOutAllFaces();
            if (this.rightFoot != null ) this.rightFoot.fadeOutAllFaces();
            if (this.leftFoot != null ) this.leftFoot.fadeOutAllFaces();
            if (this.equippedItemLeft != null ) this.equippedItemLeft.fadeOutAllFaces();
            if (this.equippedItemRight != null ) this.equippedItemRight.fadeOutAllFaces();
        }

        public void setItem( Arm arm, Items newItem, LibVertex anchor, LibGameObject aParentGameObject )
        {
            //ShooterDebug.bugfix.out( "Set item [" + newItem + "] on arm [" + arm + "]" );

            Vector<Mesh> newMeshes = new Vector<Mesh>( Arrays.asList(this.meshes) );

            switch ( arm )
            {
                case ELeft:
                {
                    //remove old item
                    if (this.equippedItemLeft != null )
                    {
                        newMeshes.remove(this.equippedItemLeft);
                        this.equipmentChanged = true;
                        this.equippedItemLeft = null;
                    }
                    if ( newItem != null )
                    {
                        //rotate item on startup
                        Mesh item = new Mesh( Shooter.game.engine.d3ds.getFaces( newItem ), new LibVertex( 0.0f, 0.0f, 0.0f ) );
                        item.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 270.0f, 0.0f, 90.0f, null, LibTransformationMode.EOriginalsToOriginals );

                        this.equippedItemLeft = new BotMesh( item.getFaces(), anchor, 0.0f, 1.0f, aParentGameObject, 0.0f );
                        this.equippedItemLeft.mirrorFaces( true, false, false );

                        this.equippedItemLeft.setTargetPitchs(this.head.targetPitch.toArray( new LibRotation[] {} ) );
                        newMeshes.add(this.equippedItemLeft);
                        this.equipmentChanged = true;
                    }
                    break;
                }

                case ERight:
                {
                    //remove old item
                    if (this.equippedItemRight != null )
                    {
                        newMeshes.remove(this.equippedItemRight);
                        this.equipmentChanged = true;
                        this.equippedItemRight = null;
                    }
                    if ( newItem != null )
                    {
                        //rotate item on startup
                        Mesh item = new Mesh( Shooter.game.engine.d3ds.getFaces( newItem ), new LibVertex( 0.0f, 0.0f, 0.0f ) );
                        item.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 270.0f, 0.0f, 90.0f, null, LibTransformationMode.EOriginalsToOriginals );

                        this.equippedItemRight = new BotMesh( item.getFaces(), anchor, 0.0f, 1.0f, aParentGameObject, 0.0f );

                        this.equippedItemRight.setTargetPitchs(this.head.targetPitch.toArray( new LibRotation[] {} ) );
                        newMeshes.add(this.equippedItemRight);
                        this.equipmentChanged = true;
                    }
                    break;
                }
            }

            //return as array
            this.meshes = newMeshes.toArray( new Mesh[] {} );
        }
    }
