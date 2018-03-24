
    package de.christopherstock.shooter.g3d.mesh;

    import  java.util.*;

    import de.christopherstock.lib.LibTransformationMode;
    import de.christopherstock.lib.LibOffset;
    import de.christopherstock.lib.LibRotation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.ShooterSettings.BotSettings.RotationSpeed;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.base.ShooterD3ds.*;
    import  de.christopherstock.shooter.base.ShooterTexture.*;
    import  de.christopherstock.shooter.game.bot.*;

    /*******************************************************************************************************************
    *   Represents a mesh.
    *******************************************************************************************************************/
    public class BotMeshes extends MeshCollection
    {
        private     static  final   long    serialVersionUID    = 4535159050554256206L;

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

        public      static  final LibOffset OFFSET_ABSOLUTE_HEAD                = new LibOffset(   0.0f,       -0.05f,     1.225f  );

        public      static  final LibOffset OFFSET_ABSOLUTE_LEFT_UPPER_ARM      = new LibOffset(   -0.075f,    0.0f,       1.1f    );
        public      static  final LibOffset OFFSET_ABSOLUTE_LEFT_LOWER_ARM      = new LibOffset(   -0.215f,    -0.013f,    0.95f    );
        public      static  final LibOffset OFFSET_ABSOLUTE_LEFT_HAND           = new LibOffset(   -0.15f,     -0.053f,    0.8f    );

        public      static  final LibOffset OFFSET_ABSOLUTE_RIGHT_UPPER_ARM     = new LibOffset(   0.075f,     0.0f,       1.1f    );
        public      static  final LibOffset OFFSET_ABSOLUTE_RIGHT_LOWER_ARM     = new LibOffset(   0.215f,     -0.013f,     0.95f    );
        public      static  final LibOffset OFFSET_ABSOLUTE_RIGHT_HAND          = new LibOffset(   0.15f,      -0.053f,    0.8f    );
      //public      static  final   LibOffset              OFFSET_ABSOLUTE_RIGHT_LOWER_ARM     = new LibOffset(   0.215f,     0.013f,     0.95f    );
      //public      static  final   LibOffset              OFFSET_ABSOLUTE_RIGHT_HAND          = new LibOffset(   0.15f,      -0.025f,    0.8f    );

        public      static  final LibOffset OFFSET_ABSOLUTE_RIGHT_UPPER_LEG     = new LibOffset(   0.075f,     -0.05f,     0.68f   );
        public      static  final LibOffset OFFSET_ABSOLUTE_RIGHT_LOWER_LEG     = new LibOffset(   0.135f,     -0.05f,     0.38f   );
        public      static  final LibOffset OFFSET_ABSOLUTE_RIGHT_FOOT          = new LibOffset(   0.21f,      -0.03f,     0.08f   );

        public      static  final LibOffset OFFSET_ABSOLUTE_LEFT_UPPER_LEG      = new LibOffset(   -0.075f,    -0.05f,     0.68f   );
        public      static  final LibOffset OFFSET_ABSOLUTE_LEFT_LOWER_LEG      = new LibOffset(   -0.135f,    -0.05f,     0.38f   );
        public      static  final LibOffset OFFSET_ABSOLUTE_LEFT_FOOT           = new LibOffset(   -0.21f,     -0.03f,     0.08f   );

        private                     boolean             limbsStandStill                       = false;

        public                      int                 currentTargetPitchHead              = 0;
        public                      int                 currentTargetPitchLeftArm           = 0;
        public                      int                 currentTargetPitchRightArm          = 0;
        public                      int                 currentTargetPitchLeftLeg           = 0;
        public                      int                 currentTargetPitchRightLeg          = 0;

        public                      boolean             repeatTargetPitchesHead             = false;
        public                      boolean             repeatTargetPitchesLeftArm          = false;
        public                      boolean             repeatTargetPitchesRightArm         = false;
        public                      boolean             repeatTargetPitchesLeftLeg          = false;
        public                      boolean             repeatTargetPitchesRightLeg         = false;

        public                      boolean             completedTargetPitchesHead          = false;
        public                      boolean             completedTargetPitchesLeftArm       = false;
        public                      boolean             completedTargetPitchesRightArm      = false;


        public                      BotPattern   iTemplate                           = null;

        public                      BotMesh             iEquippedItemLeft                   = null;
        public                      BotMesh             iEquippedItemRight                  = null;

        public                      BotMesh             iGlasses                            = null;
        public                      BotMesh             iHat                                = null;
        public                      BotMesh             iHead                               = null;
        public                      BotMesh             iFace                               = null;
        public                      BotMesh             iRightUpperArm                      = null;
        public                      BotMesh             iRightLowerArm                      = null;
        public                      BotMesh             iTorso                              = null;
        public                      BotMesh             iNeck                               = null;
        public                      BotMesh             iLeftUpperArm                       = null;
        public                      BotMesh             iLeftLowerArm                       = null;
        public                      BotMesh             iRightHand                          = null;
        public                      BotMesh             iLeftHand                           = null;
        public                      BotMesh             iRightUpperLeg                      = null;
        public                      BotMesh             iLeftUpperLeg                       = null;
        public                      BotMesh             iRightLowerLeg                      = null;
        public                      BotMesh             iLeftLowerLeg                       = null;
        public                      BotMesh             iRightFoot                          = null;
        public                      BotMesh             iLeftFoot                           = null;

        public                      boolean             equipmentChanged                    = false;

        public                      HeadPosition        iCurrentHeadPosition                = null;
        public                      ArmsPosition        iCurrentArmsPosition                = null;
        public                      LegsPosition        iCurrentLegsPosition                = null;

        /***************************************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  aAnchor             The meshes' anchor point.
        ***************************************************************************************************************/
        public BotMeshes( BotPattern aTemp, LibVertex aAnchor, LibGameObject aParentGameObject, Items itemLeft, Items itemRight )
        {
            //assign template
            this.iTemplate = aTemp;

            BotPatternBase aTemplate = aTemp.iBase;

            if ( aTemp.iHat != null )
            {
                this.iHat = new BotMesh( ShooterD3ds.getFaces( aTemp.iHat ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.0f );
                this.iHat.changeTexture(             WallTex.ETest.getTexture(),          aTemp.iTexHat.getTexture()                  );
            }

            if ( aTemp.iGlasses != null )
            {
                this.iGlasses = new BotMesh( ShooterD3ds.getFaces( aTemp.iGlasses ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
                this.iGlasses.changeTexture(             WallTex.ETest.getTexture(),         aTemp.iTexGlassesGlass.getTexture()         );
                this.iGlasses.changeTexture(             BotTex.EHairBlonde.getTexture(),    aTemp.iTexGlassesHolder.getTexture()        );
            }

            //assign limbs
            this.iHead = new BotMesh( ShooterD3ds.getFaces( aTemp.iHead               ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
            this.iFace = new BotMesh( ShooterD3ds.getFaces( aTemp.iFace               ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
            this.iRightUpperArm = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightUpperArm      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.iRightLowerArm = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightLowerArm      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.iTorso = new BotMesh( ShooterD3ds.getFaces( aTemp.iTorso              ), aAnchor, 0.0f, 1.0f, aParentGameObject, 1.0f  );
            this.iNeck = new BotMesh( ShooterD3ds.getFaces( aTemp.iNeck               ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f  );
            this.iLeftUpperArm = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftUpperArm       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.iLeftLowerArm = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftLowerArm       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.iRightHand = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightHand          ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.iLeftHand = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftHand           ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.iRightUpperLeg = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightUpperLeg      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.iLeftUpperLeg = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftUpperLeg       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.iRightLowerLeg = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightLowerLeg      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.iLeftLowerLeg = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftLowerLeg       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            this.iRightFoot = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightFoot          ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            this.iLeftFoot = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftFoot           ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );

            //assign textures for bot meshes
            this.iRightUpperLeg.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightUpperLeg == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightUpperLeg .getTexture()  )         );
            this.iLeftUpperLeg.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftUpperLeg  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftUpperLeg  .getTexture()  )         );
            this.iRightLowerLeg.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightLowerLeg == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightLowerLeg .getTexture()  )         );
            this.iLeftLowerLeg.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftLowerLeg  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftLowerLeg  .getTexture()  )         );
            this.iLeftFoot.changeTexture(        WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftFoot      == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftFoot      .getTexture()  )         );
            this.iRightFoot.changeTexture(       WallTex.ETest.getTexture(),          ( aTemplate.iTexRightFoot     == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightFoot     .getTexture()  )         );
            this.iLeftUpperArm.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftUpperArm  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftUpperArm  .getTexture()  )         );
            this.iRightUpperArm.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightUpperArm == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightUpperArm .getTexture()  )         );
            this.iLeftLowerArm.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftLowerArm  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftLowerArm  .getTexture()  )         );
            this.iRightLowerArm.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightLowerArm == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightLowerArm .getTexture()  )         );
            this.iLeftHand.changeTexture(        WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftHand      == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftHand      .getTexture()  )         );
            this.iRightHand.changeTexture(       WallTex.ETest.getTexture(),          ( aTemplate.iTexRightHand     == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightHand     .getTexture()  )         );
            this.iNeck.changeTexture(            WallTex.ETest.getTexture(),          ( aTemplate.iTexNeck          == null ? aTemp.iSkin.getTexture() : aTemplate.iTexNeck          .getTexture()  )         );
            this.iTorso.changeTexture(           WallTex.ETest.getTexture(),          ( aTemplate.iTexTorso    == null ? aTemp.iSkin.getTexture() : aTemplate.iTexTorso    .getTexture()  )         );

            this.iHead.changeTexture(            BotTex.EHairBlonde.getTexture(),     aTemp.iTexHair.getTexture()                            );
            this.iFace.changeTexture(            WallTex.ETest.getTexture(),          aTemp.iTexFaceEyesOpen.getTexture()                    );

            //assign initial arm position
            this.assignArmsPosition( ArmsPosition.ERestInHip             );
            this.assignLegsPosition( LegsPosition.EStandSpreadLegged     );
            this.assignHeadPosition( HeadPosition.EStill                 );

            Vector<Mesh> newMeshes = new Vector<Mesh>();
            newMeshes.add(this.iHead);
            newMeshes.add(this.iFace);
            newMeshes.add(this.iRightUpperArm);
            newMeshes.add(this.iRightLowerArm);
            newMeshes.add(this.iRightHand);
            newMeshes.add(this.iLeftUpperArm);
            newMeshes.add(this.iLeftLowerArm);
            newMeshes.add(this.iLeftHand);
            newMeshes.add(this.iTorso);
            newMeshes.add(this.iNeck);
            newMeshes.add(this.iRightUpperLeg);
            newMeshes.add(this.iRightLowerLeg);
            newMeshes.add(this.iLeftLowerLeg);
            newMeshes.add(this.iLeftUpperLeg);
            newMeshes.add(this.iRightFoot);
            newMeshes.add(this.iLeftFoot);

            if (this.iGlasses != null ) newMeshes.add(this.iGlasses);
            if (this.iHat != null ) newMeshes.add(this.iHat);
            if (this.iEquippedItemLeft != null ) newMeshes.add(this.iEquippedItemLeft);
            if (this.iEquippedItemRight != null ) newMeshes.add(this.iEquippedItemRight);

            this.iMeshes = newMeshes.toArray( new Mesh[] {} );

            //assign items
            this.setItem( Arm.ELeft,  itemLeft,  aAnchor, aParentGameObject );
            this.setItem( Arm.ERight, itemRight, aAnchor, aParentGameObject );
        }

        public void assignArmsPosition( ArmsPosition newArmsPosition )
        {
            this.iCurrentArmsPosition = newArmsPosition;
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
            return (this.iCurrentArmsPosition == toCheck );
        }

        public boolean isAssignedLegsPosition( LegsPosition toCheck )
        {
            return (this.iCurrentLegsPosition == toCheck );
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
                    this.iLeftUpperArm.setTargetPitchs(   upperArmPitch );
                    this.iLeftLowerArm.setTargetPitchs(   lowerArmPitch );
                    this.iLeftHand.setTargetPitchs(       handPitch );

                    if (this.iEquippedItemLeft != null ) this.iEquippedItemLeft.setTargetPitchs(   handPitch );

                    break;
                }

                case ERight:
                {
                    this.iRightUpperArm.setTargetPitchs(  upperArmPitch );
                    this.iRightLowerArm.setTargetPitchs(  lowerArmPitch );
                    this.iRightHand.setTargetPitchs(      handPitch );

                    if (this.iEquippedItemRight != null ) this.iEquippedItemRight.setTargetPitchs(  handPitch );

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
            this.iCurrentLegsPosition = newLegsPosition;

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

                    this.iLeftUpperLeg.setTargetPitchs(   upperLegPitch );
                    this.iLeftLowerLeg.setTargetPitchs(   lowerLegPitch );
                    this.iLeftFoot.setTargetPitchs(       footPitch );
                    break;
                }

                case ERight:
                {
                    this.iRightUpperLeg.setTargetPitchs(  upperLegPitch );
                    this.iRightLowerLeg.setTargetPitchs(  lowerLegPitch );
                    this.iRightFoot.setTargetPitchs(      footPitch );

                    break;
                }
            }
        }

        public void assignHeadPosition( HeadPosition newHeadPosition )
        {
            this.iCurrentHeadPosition = newHeadPosition;
            switch (this.iCurrentHeadPosition)
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
            return (this.iCurrentHeadPosition == toCheck );
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

            this.iHead.setTargetPitchs(      headPitch );
            this.iFace.setTargetPitchs(      headPitch );

            if (this.iHat != null ) this.iHat.setTargetPitchs(       headPitch );
            if (this.iGlasses != null ) this.iGlasses.setTargetPitchs(   headPitch );
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
            boolean h = this.iHead.reachToTargetPitch(this.currentTargetPitchHead);
            this.iFace.reachToTargetPitch(this.currentTargetPitchHead);
                        if (this.iHat != null ) this.iHat.reachToTargetPitch(this.currentTargetPitchHead);
                        if (this.iGlasses != null ) this.iGlasses.reachToTargetPitch(this.currentTargetPitchHead);

            //check if all limbs of the head reached finish
            if ( h )
            {
                if (this.iHead.iTargetPitch.size() > 1 )
                {
                    //repeat ?
                    if (this.repeatTargetPitchesHead)
                    {
                        noLimbMoved = false;
                        ++this.currentTargetPitchHead;
                        if (this.currentTargetPitchHead >= this.iHead.iTargetPitch.size() )
                        {
                            this.currentTargetPitchHead = 0;
                        }
                    }
                    else
                    {
                        if (this.currentTargetPitchHead < this.iHead.iTargetPitch.size() - 1 )
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
            boolean ra1 = this.iRightUpperArm.reachToTargetPitch(this.currentTargetPitchRightArm);
            boolean ra2 = this.iRightLowerArm.reachToTargetPitch(this.currentTargetPitchRightArm);
            boolean ra3 = this.iRightHand.reachToTargetPitch(this.currentTargetPitchRightArm);
                          if (this.iEquippedItemRight != null ) this.iEquippedItemRight.reachToTargetPitch(this.currentTargetPitchRightArm);
            //check if all limbs of the left arm reached finish
            if ( ra1 & ra2 & ra3 )
            {
                if (this.iRightUpperArm.iTargetPitch.size() > 1 )
                {
                    if (this.repeatTargetPitchesRightArm)
                    {
                        noLimbMoved = false;
                        ++this.currentTargetPitchRightArm;
                        if (this.currentTargetPitchRightArm >= this.iRightUpperArm.iTargetPitch.size() )
                        {
                            this.currentTargetPitchRightArm = 0;
                        }
                    }
                    else
                    {
                        if (this.currentTargetPitchRightArm < this.iRightUpperArm.iTargetPitch.size() - 1 )
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
            boolean la1 = this.iLeftUpperArm.reachToTargetPitch(this.currentTargetPitchLeftArm);
            boolean la2 = this.iLeftLowerArm.reachToTargetPitch(this.currentTargetPitchLeftArm);
            boolean la3 = this.iLeftHand.reachToTargetPitch(this.currentTargetPitchLeftArm);
                          if (this.iEquippedItemLeft != null ) this.iEquippedItemLeft.reachToTargetPitch(this.currentTargetPitchLeftArm);
            //check if all limbs of the left arm reached finish
            if ( la1 & la2 & la3 )
            {
                if (this.iLeftUpperArm.iTargetPitch.size() > 1 )
                {
                    if (this.repeatTargetPitchesLeftArm)
                    {
                        noLimbMoved = false;
                        ++this.currentTargetPitchLeftArm;
                        if (this.currentTargetPitchLeftArm >= this.iLeftUpperArm.iTargetPitch.size() )
                        {
                            this.currentTargetPitchLeftArm = 0;
                        }
                    }
                    else
                    {
                        if (this.currentTargetPitchLeftArm < this.iLeftUpperArm.iTargetPitch.size() - 1 )
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
            boolean rl1 = this.iRightUpperLeg.reachToTargetPitch(this.currentTargetPitchRightLeg);
            boolean rl2 = this.iRightLowerLeg.reachToTargetPitch(this.currentTargetPitchRightLeg);
            boolean rl3 = this.iRightFoot.reachToTargetPitch(this.currentTargetPitchRightLeg);
            //check if all limbs of the right leg reached finish
            if ( rl1 & rl2 & rl3 )
            {
                if (this.iRightUpperLeg.iTargetPitch.size() > 1 )
                {
                    noLimbMoved = false;

                    ++this.currentTargetPitchRightLeg;
                    if (this.currentTargetPitchRightLeg >= this.iRightUpperLeg.iTargetPitch.size() )
                        this.currentTargetPitchRightLeg = 0;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //left leg
            boolean ll1 = this.iLeftUpperLeg.reachToTargetPitch(this.currentTargetPitchLeftLeg);
            boolean ll2 = this.iLeftLowerLeg.reachToTargetPitch(this.currentTargetPitchLeftLeg);
            boolean ll3 = this.iLeftFoot.reachToTargetPitch(this.currentTargetPitchLeftLeg);
            //check if all limbs of the left leg reached finish
            if ( ll1 & ll2 & ll3 )
            {
                if (this.iLeftUpperLeg.iTargetPitch.size() > 1 )
                {
                    noLimbMoved = false;

                    ++this.currentTargetPitchLeftLeg;
                    if (this.currentTargetPitchLeftLeg >= this.iLeftUpperLeg.iTargetPitch.size() ) this.currentTargetPitchLeftLeg = 0;
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
            this.iHead.translateAndRotateLimb(                               BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            this.iFace.translateAndRotateLimb(                               BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            if (this.iHat != null ) this.iHat.translateAndRotateLimb(        BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            if (this.iGlasses != null ) this.iGlasses.translateAndRotateLimb(    BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );

            //right arm
            LibVertex rightUpperArmAnk = this.iRightUpperArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_UPPER_ARM );
            LibVertex rightLowerArmAnk = this.iRightLowerArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_LOWER_ARM );
            LibVertex rightHandArmAnk  = this.iRightHand.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_RIGHT_HAND      );
            if (this.iEquippedItemRight != null ) this.iEquippedItemRight.translateLimb(   BotMeshes.OFFSET_ABSOLUTE_RIGHT_HAND      );
            this.iRightUpperArm.rotateAroundAnchor( rightUpperArmAnk, this.iRightUpperArm.iPitch );
            this.iRightLowerArm.rotateAroundAnchor( rightUpperArmAnk, this.iRightUpperArm.iPitch );
            this.iRightHand.rotateAroundAnchor(     rightUpperArmAnk, this.iRightUpperArm.iPitch );
            if (this.iEquippedItemRight != null ) this.iEquippedItemRight.rotateAroundAnchor(     rightUpperArmAnk, this.iRightUpperArm.iPitch );
            rightLowerArmAnk.rotateXYZ(this.iRightUpperArm.iPitch.x, this.iRightUpperArm.iPitch.y, this.iRightUpperArm.iPitch.z, rightUpperArmAnk );
            rightHandArmAnk.rotateXYZ(this.iRightUpperArm.iPitch.x, this.iRightUpperArm.iPitch.y, this.iRightUpperArm.iPitch.z, rightUpperArmAnk );
            this.iRightLowerArm.rotateAroundAnchor( rightLowerArmAnk, this.iRightLowerArm.iPitch );
            this.iRightHand.rotateAroundAnchor(     rightLowerArmAnk, this.iRightLowerArm.iPitch );
            if (this.iEquippedItemRight != null ) this.iEquippedItemRight.rotateAroundAnchor(     rightLowerArmAnk, this.iRightLowerArm.iPitch );
            rightHandArmAnk.rotateXYZ(this.iRightLowerArm.iPitch.x, this.iRightLowerArm.iPitch.y, this.iRightLowerArm.iPitch.z, rightLowerArmAnk );
            this.iRightHand.rotateAroundAnchor(     rightHandArmAnk, this.iRightHand.iPitch );
            if (this.iEquippedItemRight != null ) this.iEquippedItemRight.rotateAroundAnchor(     rightHandArmAnk, this.iRightHand.iPitch );

            //left arm
            LibVertex leftUpperArmAnk = this.iLeftUpperArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_UPPER_ARM );
            LibVertex leftLowerArmAnk = this.iLeftLowerArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_LOWER_ARM );
            LibVertex leftHandArmAnk  = this.iLeftHand.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_LEFT_HAND      );
            if (this.iEquippedItemLeft != null ) this.iEquippedItemLeft.translateLimb(   BotMeshes.OFFSET_ABSOLUTE_LEFT_HAND      );
            this.iLeftUpperArm.rotateAroundAnchor( leftUpperArmAnk, this.iLeftUpperArm.iPitch );
            this.iLeftLowerArm.rotateAroundAnchor( leftUpperArmAnk, this.iLeftUpperArm.iPitch );
            this.iLeftHand.rotateAroundAnchor(     leftUpperArmAnk, this.iLeftUpperArm.iPitch );
            if (this.iEquippedItemLeft != null ) this.iEquippedItemLeft.rotateAroundAnchor(     leftUpperArmAnk, this.iLeftUpperArm.iPitch );
            leftLowerArmAnk.rotateXYZ(this.iLeftUpperArm.iPitch.x, this.iLeftUpperArm.iPitch.y, this.iLeftUpperArm.iPitch.z, leftUpperArmAnk );
            leftHandArmAnk.rotateXYZ(this.iLeftUpperArm.iPitch.x, this.iLeftUpperArm.iPitch.y, this.iLeftUpperArm.iPitch.z, leftUpperArmAnk );
            this.iLeftLowerArm.rotateAroundAnchor( leftLowerArmAnk, this.iLeftLowerArm.iPitch );
            this.iLeftHand.rotateAroundAnchor(     leftLowerArmAnk, this.iLeftLowerArm.iPitch );
            if (this.iEquippedItemLeft != null ) this.iEquippedItemLeft.rotateAroundAnchor(     leftLowerArmAnk, this.iLeftLowerArm.iPitch );
            leftHandArmAnk.rotateXYZ(this.iLeftLowerArm.iPitch.x, this.iLeftLowerArm.iPitch.y, this.iLeftLowerArm.iPitch.z, leftLowerArmAnk );
            this.iLeftHand.rotateAroundAnchor(     leftHandArmAnk, this.iLeftHand.iPitch );
            if (this.iEquippedItemLeft != null ) this.iEquippedItemLeft.rotateAroundAnchor(     leftHandArmAnk, this.iLeftHand.iPitch );

            //right leg
            LibVertex rightUpperLegAnk = this.iRightUpperLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_UPPER_LEG );
            LibVertex rightLowerLegAnk = this.iRightLowerLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_LOWER_LEG );
            LibVertex rightfootLegAnk  = this.iRightFoot.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_RIGHT_FOOT      );
            this.iRightUpperLeg.rotateAroundAnchor( rightUpperLegAnk, this.iRightUpperLeg.iPitch );
            this.iRightLowerLeg.rotateAroundAnchor( rightUpperLegAnk, this.iRightUpperLeg.iPitch );
            this.iRightFoot.rotateAroundAnchor(     rightUpperLegAnk, this.iRightUpperLeg.iPitch );
            rightLowerLegAnk.rotateXYZ(this.iRightUpperLeg.iPitch.x, this.iRightUpperLeg.iPitch.y, this.iRightUpperLeg.iPitch.z, rightUpperLegAnk );
            rightfootLegAnk.rotateXYZ(this.iRightUpperLeg.iPitch.x, this.iRightUpperLeg.iPitch.y, this.iRightUpperLeg.iPitch.z, rightUpperLegAnk );
            this.iRightLowerLeg.rotateAroundAnchor( rightLowerLegAnk, this.iRightLowerLeg.iPitch );
            this.iRightFoot.rotateAroundAnchor(     rightLowerLegAnk, this.iRightLowerLeg.iPitch );
            rightfootLegAnk.rotateXYZ(this.iRightLowerLeg.iPitch.x, this.iRightLowerLeg.iPitch.y, this.iRightLowerLeg.iPitch.z, rightLowerLegAnk );
            this.iRightFoot.rotateAroundAnchor(     rightfootLegAnk, this.iRightFoot.iPitch );

            //left leg
            LibVertex leftUpperLegAnk = this.iLeftUpperLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_UPPER_LEG );
            LibVertex leftLowerLegAnk = this.iLeftLowerLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_LOWER_LEG );
            LibVertex leftfootLegAnk  = this.iLeftFoot.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_LEFT_FOOT      );
            this.iLeftUpperLeg.rotateAroundAnchor( leftUpperLegAnk, this.iLeftUpperLeg.iPitch );
            this.iLeftLowerLeg.rotateAroundAnchor( leftUpperLegAnk, this.iLeftUpperLeg.iPitch );
            this.iLeftFoot.rotateAroundAnchor(     leftUpperLegAnk, this.iLeftUpperLeg.iPitch );
            leftLowerLegAnk.rotateXYZ(this.iLeftUpperLeg.iPitch.x, this.iLeftUpperLeg.iPitch.y, this.iLeftUpperLeg.iPitch.z, leftUpperLegAnk );
            leftfootLegAnk.rotateXYZ(this.iLeftUpperLeg.iPitch.x, this.iLeftUpperLeg.iPitch.y, this.iLeftUpperLeg.iPitch.z, leftUpperLegAnk );
            this.iLeftLowerLeg.rotateAroundAnchor( leftLowerLegAnk, this.iLeftLowerLeg.iPitch );
            this.iLeftFoot.rotateAroundAnchor(     leftLowerLegAnk, this.iLeftLowerLeg.iPitch );
            leftfootLegAnk.rotateXYZ(this.iLeftLowerLeg.iPitch.x, this.iLeftLowerLeg.iPitch.y, this.iLeftLowerLeg.iPitch.z, leftLowerLegAnk );
            this.iLeftFoot.rotateAroundAnchor(     leftfootLegAnk, this.iLeftFoot.iPitch );

            //torso and neck are not transformed neither translated
            this.iTorso.translateLimb( new LibOffset( 0.0f, 0.0f, 0.0f ) );
            this.iNeck.translateLimb(  new LibOffset( 0.0f, 0.0f, 0.0f ) );
        }

        public void turnAllLimbsX( LibVertex botAnchor, float angleX )
        {
            for ( Mesh mesh : this.iMeshes)
            {
                mesh.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, angleX, 0.0f, 0.0f, botAnchor, LibTransformationMode.ETransformedToTransformed );
            }
        }

        public void turnAllLimbsZ( LibVertex botAnchor, float facingAngle )
        {
            for ( Mesh mesh : this.iMeshes)
            {
                mesh.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, facingAngle, botAnchor, LibTransformationMode.ETransformedToTransformed );
            }
        }

        public void changeFaceTexture( LibGLTexture oldTex, LibGLTexture newTex )
        {
            this.iFace.changeTexture( oldTex, newTex );
        }

        public void fadeOutAllFaces()
        {
            if (this.iGlasses != null ) this.iGlasses.fadeOutAllFaces();
            if (this.iHat != null ) this.iHat.fadeOutAllFaces();
            if (this.iHead != null ) this.iHead.fadeOutAllFaces();
            if (this.iFace != null ) this.iFace.fadeOutAllFaces();
            if (this.iRightUpperArm != null ) this.iRightUpperArm.fadeOutAllFaces();
            if (this.iRightLowerArm != null ) this.iRightLowerArm.fadeOutAllFaces();
            if (this.iTorso != null ) this.iTorso.fadeOutAllFaces();
            if (this.iNeck != null ) this.iNeck.fadeOutAllFaces();
            if (this.iLeftUpperArm != null ) this.iLeftUpperArm.fadeOutAllFaces();
            if (this.iLeftLowerArm != null ) this.iLeftLowerArm.fadeOutAllFaces();
            if (this.iRightHand != null ) this.iRightHand.fadeOutAllFaces();
            if (this.iLeftHand != null ) this.iLeftHand.fadeOutAllFaces();
            if (this.iRightUpperLeg != null ) this.iRightUpperLeg.fadeOutAllFaces();
            if (this.iLeftUpperLeg != null ) this.iLeftUpperLeg.fadeOutAllFaces();
            if (this.iRightLowerLeg != null ) this.iRightLowerLeg.fadeOutAllFaces();
            if (this.iLeftLowerLeg != null ) this.iLeftLowerLeg.fadeOutAllFaces();
            if (this.iRightFoot != null ) this.iRightFoot.fadeOutAllFaces();
            if (this.iLeftFoot != null ) this.iLeftFoot.fadeOutAllFaces();
            if (this.iEquippedItemLeft != null ) this.iEquippedItemLeft.fadeOutAllFaces();
            if (this.iEquippedItemRight != null ) this.iEquippedItemRight.fadeOutAllFaces();
        }

        public void setItem( Arm arm, Items newItem, LibVertex anchor, LibGameObject aParentGameObject )
        {
            //ShooterDebug.bugfix.out( "Set item [" + newItem + "] on arm [" + arm + "]" );

            Vector<Mesh> newMeshes = new Vector<Mesh>();
            newMeshes.addAll( Arrays.asList(this.iMeshes) );

            switch ( arm )
            {
                case ELeft:
                {
                    //remove old item
                    if (this.iEquippedItemLeft != null )
                    {
                        newMeshes.remove(this.iEquippedItemLeft);
                        this.equipmentChanged = true;
                        this.iEquippedItemLeft = null;
                    }
                    if ( newItem != null )
                    {
                        //rotate item on startup
                        Mesh item = new Mesh( ShooterD3ds.getFaces( newItem ), new LibVertex( 0.0f, 0.0f, 0.0f ) );
                        item.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 270.0f, 0.0f, 90.0f, null, LibTransformationMode.EOriginalsToOriginals );

                        this.iEquippedItemLeft = new BotMesh( item.getFaces(), anchor, 0.0f, 1.0f, aParentGameObject, 0.0f );
                        this.iEquippedItemLeft.mirrorFaces( true, false, false );

                        this.iEquippedItemLeft.setTargetPitchs(this.iHead.iTargetPitch.toArray( new LibRotation[] {} ) );
                        newMeshes.add(this.iEquippedItemLeft);
                        this.equipmentChanged = true;
                    }
                    break;
                }

                case ERight:
                {
                    //remove old item
                    if (this.iEquippedItemRight != null )
                    {
                        newMeshes.remove(this.iEquippedItemRight);
                        this.equipmentChanged = true;
                        this.iEquippedItemRight = null;
                    }
                    if ( newItem != null )
                    {
                        //rotate item on startup
                        Mesh item = new Mesh( ShooterD3ds.getFaces( newItem ), new LibVertex( 0.0f, 0.0f, 0.0f ) );
                        item.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 270.0f, 0.0f, 90.0f, null, LibTransformationMode.EOriginalsToOriginals );

                        this.iEquippedItemRight = new BotMesh( item.getFaces(), anchor, 0.0f, 1.0f, aParentGameObject, 0.0f );

                        this.iEquippedItemRight.setTargetPitchs(this.iHead.iTargetPitch.toArray( new LibRotation[] {} ) );
                        newMeshes.add(this.iEquippedItemRight);
                        this.equipmentChanged = true;
                    }
                    break;
                }
            }

            //return as array
            this.iMeshes = newMeshes.toArray( new Mesh[] {} );
        }
    }
