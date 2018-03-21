
    package de.christopherstock.shooter.g3d.mesh;

    import  java.util.*;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.Lib.Offset;
    import  de.christopherstock.lib.Lib.Rotation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.shooter.ShooterSettings.BotSettings.RotationSpeed;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.base.ShooterD3ds.*;
    import  de.christopherstock.shooter.base.ShooterTexture.*;
    import  de.christopherstock.shooter.game.bot.*;

    /**************************************************************************************
    *   Represents a mesh.
    **************************************************************************************/
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

        public      static  final   Offset              OFFSET_ABSOLUTE_HEAD                = new Offset(   0.0f,       -0.05f,     1.225f  );

        public      static  final   Offset              OFFSET_ABSOLUTE_LEFT_UPPER_ARM      = new Offset(   -0.075f,    0.0f,       1.1f    );
        public      static  final   Offset              OFFSET_ABSOLUTE_LEFT_LOWER_ARM      = new Offset(   -0.215f,    -0.013f,    0.95f    );
        public      static  final   Offset              OFFSET_ABSOLUTE_LEFT_HAND           = new Offset(   -0.15f,     -0.053f,    0.8f    );

        public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_UPPER_ARM     = new Offset(   0.075f,     0.0f,       1.1f    );
        public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_LOWER_ARM     = new Offset(   0.215f,     -0.013f,     0.95f    );
        public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_HAND          = new Offset(   0.15f,      -0.053f,    0.8f    );
      //public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_LOWER_ARM     = new Offset(   0.215f,     0.013f,     0.95f    );
      //public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_HAND          = new Offset(   0.15f,      -0.025f,    0.8f    );

        public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_UPPER_LEG     = new Offset(   0.075f,     -0.05f,     0.68f   );
        public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_LOWER_LEG     = new Offset(   0.135f,     -0.05f,     0.38f   );
        public      static  final   Offset              OFFSET_ABSOLUTE_RIGHT_FOOT          = new Offset(   0.21f,      -0.03f,     0.08f   );

        public      static  final   Offset              OFFSET_ABSOLUTE_LEFT_UPPER_LEG      = new Offset(   -0.075f,    -0.05f,     0.68f   );
        public      static  final   Offset              OFFSET_ABSOLUTE_LEFT_LOWER_LEG      = new Offset(   -0.135f,    -0.05f,     0.38f   );
        public      static  final   Offset              OFFSET_ABSOLUTE_LEFT_FOOT           = new Offset(   -0.21f,     -0.03f,     0.08f   );

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

        /**************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  aAnchor             The meshes' anchor point.
        **************************************************************************************/
        public BotMeshes( BotPattern aTemp, LibVertex aAnchor, LibGameObject aParentGameObject, Items itemLeft, Items itemRight )
        {
            //assign template
            iTemplate = aTemp;

            BotPatternBase aTemplate = aTemp.iBase;

            if ( aTemp.iHat != null )
            {
                iHat = new BotMesh( ShooterD3ds.getFaces( aTemp.iHat ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.0f );
                iHat.changeTexture(             WallTex.ETest.getTexture(),          aTemp.iTexHat.getTexture()                  );
            }

            if ( aTemp.iGlasses != null )
            {
                iGlasses = new BotMesh( ShooterD3ds.getFaces( aTemp.iGlasses ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
                iGlasses.changeTexture(             WallTex.ETest.getTexture(),         aTemp.iTexGlassesGlass.getTexture()         );
                iGlasses.changeTexture(             BotTex.EHairBlonde.getTexture(),    aTemp.iTexGlassesHolder.getTexture()        );
            }

            //assign limbs
            iHead           = new BotMesh( ShooterD3ds.getFaces( aTemp.iHead               ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
            iFace           = new BotMesh( ShooterD3ds.getFaces( aTemp.iFace               ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f );
            iRightUpperArm  = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightUpperArm      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            iRightLowerArm  = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightLowerArm      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            iTorso          = new BotMesh( ShooterD3ds.getFaces( aTemp.iTorso              ), aAnchor, 0.0f, 1.0f, aParentGameObject, 1.0f  );
            iNeck           = new BotMesh( ShooterD3ds.getFaces( aTemp.iNeck               ), aAnchor, 0.0f, 1.0f, aParentGameObject, -1.0f  );
            iLeftUpperArm   = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftUpperArm       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            iLeftLowerArm   = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftLowerArm       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            iRightHand      = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightHand          ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            iLeftHand       = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftHand           ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            iRightUpperLeg  = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightUpperLeg      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            iLeftUpperLeg   = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftUpperLeg       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            iRightLowerLeg  = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightLowerLeg      ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            iLeftLowerLeg   = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftLowerLeg       ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.5f  );
            iRightFoot      = new BotMesh( ShooterD3ds.getFaces( aTemp.iRightFoot          ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );
            iLeftFoot       = new BotMesh( ShooterD3ds.getFaces( aTemp.iLeftFoot           ), aAnchor, 0.0f, 1.0f, aParentGameObject, 0.33f );

            //assign textures for bot meshes
            iRightUpperLeg.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightUpperLeg == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightUpperLeg .getTexture()  )         );
            iLeftUpperLeg.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftUpperLeg  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftUpperLeg  .getTexture()  )         );
            iRightLowerLeg.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightLowerLeg == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightLowerLeg .getTexture()  )         );
            iLeftLowerLeg.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftLowerLeg  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftLowerLeg  .getTexture()  )         );
            iLeftFoot.changeTexture(        WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftFoot      == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftFoot      .getTexture()  )         );
            iRightFoot.changeTexture(       WallTex.ETest.getTexture(),          ( aTemplate.iTexRightFoot     == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightFoot     .getTexture()  )         );
            iLeftUpperArm.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftUpperArm  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftUpperArm  .getTexture()  )         );
            iRightUpperArm.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightUpperArm == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightUpperArm .getTexture()  )         );
            iLeftLowerArm.changeTexture(    WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftLowerArm  == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftLowerArm  .getTexture()  )         );
            iRightLowerArm.changeTexture(   WallTex.ETest.getTexture(),          ( aTemplate.iTexRightLowerArm == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightLowerArm .getTexture()  )         );
            iLeftHand.changeTexture(        WallTex.ETest.getTexture(),          ( aTemplate.iTexLeftHand      == null ? aTemp.iSkin.getTexture() : aTemplate.iTexLeftHand      .getTexture()  )         );
            iRightHand.changeTexture(       WallTex.ETest.getTexture(),          ( aTemplate.iTexRightHand     == null ? aTemp.iSkin.getTexture() : aTemplate.iTexRightHand     .getTexture()  )         );
            iNeck.changeTexture(            WallTex.ETest.getTexture(),          ( aTemplate.iTexNeck          == null ? aTemp.iSkin.getTexture() : aTemplate.iTexNeck          .getTexture()  )         );
            iTorso.changeTexture(           WallTex.ETest.getTexture(),          ( aTemplate.iTexTorso    == null ? aTemp.iSkin.getTexture() : aTemplate.iTexTorso    .getTexture()  )         );

            iHead.changeTexture(            BotTex.EHairBlonde.getTexture(),     aTemp.iTexHair.getTexture()                            );
            iFace.changeTexture(            WallTex.ETest.getTexture(),          aTemp.iTexFaceEyesOpen.getTexture()                    );

            //assign initial arm position
            assignArmsPosition( ArmsPosition.ERestInHip             );
            assignLegsPosition( LegsPosition.EStandSpreadLegged     );
            assignHeadPosition( HeadPosition.EStill                 );

            Vector<Mesh> newMeshes = new Vector<Mesh>();
            newMeshes.add( iHead            );
            newMeshes.add( iFace            );
            newMeshes.add( iRightUpperArm   );
            newMeshes.add( iRightLowerArm   );
            newMeshes.add( iRightHand       );
            newMeshes.add( iLeftUpperArm    );
            newMeshes.add( iLeftLowerArm    );
            newMeshes.add( iLeftHand        );
            newMeshes.add( iTorso           );
            newMeshes.add( iNeck            );
            newMeshes.add( iRightUpperLeg   );
            newMeshes.add( iRightLowerLeg   );
            newMeshes.add( iLeftLowerLeg    );
            newMeshes.add( iLeftUpperLeg    );
            newMeshes.add( iRightFoot       );
            newMeshes.add( iLeftFoot        );

            if ( iGlasses           != null ) newMeshes.add( iGlasses           );
            if ( iHat               != null ) newMeshes.add( iHat               );
            if ( iEquippedItemLeft  != null ) newMeshes.add( iEquippedItemLeft  );
            if ( iEquippedItemRight != null ) newMeshes.add( iEquippedItemRight );

            iMeshes = newMeshes.toArray( new Mesh[] {} );

            //assign items
            setItem( Arm.ELeft,  itemLeft,  aAnchor, aParentGameObject );
            setItem( Arm.ERight, itemRight, aAnchor, aParentGameObject );
        }

        public void assignArmsPosition( ArmsPosition newArmsPosition )
        {
            iCurrentArmsPosition = newArmsPosition;
            switch ( newArmsPosition )
            {
                case EPickUpRight:
                {
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EPickUp,    }, false );
                    break;
                }

                case EPickUpLeft:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EPickUp,      }, false );
                    break;
                }

                case EBackDownRight:
                {
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EHangDown,    }, false );
                    break;
                }

                case EBackDownLeft:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EHangDown,    }, false );
                    break;
                }

                case EDownBoth:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EHangDown,     }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EHangDown,     }, false );
                    break;
                }

                case EReloadLeftUp:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadPrimaryHandUp,         }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadSecondaryHandUp,       }, false );
                    break;
                }

                case EReloadRightUp:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadSecondaryHandUp,       }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadPrimaryHandUp,         }, false );
                    break;
                }

                case EReloadLeftDown:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadPrimaryHandDown,       }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadSecondaryHandDown,     }, false );
                    break;
                }

                case EReloadRightDown:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EReloadSecondaryHandDown,     }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EReloadPrimaryHandDown,       }, false );
                    break;
                }

                case ETest1:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EPointToSide,  }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EPointToSide,  }, false );
                    break;
                }

                case EWalk:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EWalk1, ArmTarget.EWalk2, }, true );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EWalk2, ArmTarget.EWalk1, }, true );
                    break;
                }

                case ERestInHip:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.ERestInHip, }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.ERestInHip, }, false );
                    break;
                }

                case EAimHighRight:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EHangDown,  }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EAimHigh,   }, false );
                    break;
                }

                case EAimHighLeft:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EAimHigh,  }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EHangDown,     }, false );
                    break;
                }

                case EAimHighBoth:
                {
                    assignArmTargets( Arm.ELeft,  new ArmTarget[] { ArmTarget.EAimHigh,  }, false );
                    assignArmTargets( Arm.ERight, new ArmTarget[] { ArmTarget.EAimHigh,  }, false );
                    break;
                }
            }
        }

        public boolean isAssignedArmsPosition( ArmsPosition toCheck )
        {
            return ( iCurrentArmsPosition == toCheck );
        }

        public boolean isAssignedLegsPosition( LegsPosition toCheck )
        {
            return ( iCurrentLegsPosition == toCheck );
        }

        private void assignArmTargets( Arm arm, ArmTarget[] newArmTargets, boolean repeat )
        {
            limbsStandStill = false;

            //reset current arm targets
            switch ( arm )
            {
                case ELeft:
                {
                    currentTargetPitchLeftArm     = 0;
                    repeatTargetPitchesLeftArm    = repeat;
                    completedTargetPitchesLeftArm = false;
                    break;
                }

                case ERight:
                {
                    currentTargetPitchRightArm      = 0;
                    repeatTargetPitchesRightArm     = repeat;
                    completedTargetPitchesRightArm  = false;
                    break;
                }
            }

            Rotation[] upperArmPitch      = new Rotation[ newArmTargets.length ];
            Rotation[] lowerArmPitch      = new Rotation[ newArmTargets.length ];
            Rotation[] handPitch          = new Rotation[ newArmTargets.length ];

            for ( int i = 0; i < newArmTargets.length; ++i )
            {
                upperArmPitch[ i ] = getNewArmPosition( arm, newArmTargets[ i ] )[ 0 ];
                lowerArmPitch[ i ] = getNewArmPosition( arm, newArmTargets[ i ] )[ 1 ];
                handPitch[     i ] = getNewArmPosition( arm, newArmTargets[ i ] )[ 2 ];
            }

            //assign to specified arm
            switch ( arm )
            {
                case ELeft:
                {
                    iLeftUpperArm.setTargetPitchs(   upperArmPitch );
                    iLeftLowerArm.setTargetPitchs(   lowerArmPitch );
                    iLeftHand.setTargetPitchs(       handPitch );

                    if ( iEquippedItemLeft != null ) iEquippedItemLeft.setTargetPitchs(   handPitch );

                    break;
                }

                case ERight:
                {
                    iRightUpperArm.setTargetPitchs(  upperArmPitch );
                    iRightLowerArm.setTargetPitchs(  lowerArmPitch );
                    iRightHand.setTargetPitchs(      handPitch );

                    if ( iEquippedItemRight != null ) iEquippedItemRight.setTargetPitchs(  handPitch );

                    break;
                }
            }
        }

        private Rotation[] getNewArmPosition( Arm arm, ArmTarget newArmPosition )
        {
            Rotation upperArmPitch      = new Rotation();
            Rotation lowerArmPitch      = new Rotation();
            Rotation handPitch          = new Rotation();

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

            return new Rotation[] { upperArmPitch, lowerArmPitch, handPitch, };
        }

        public void assignLegsPosition( LegsPosition newLegsPosition )
        {
            iCurrentLegsPosition = newLegsPosition;

            //reset current leg targets
            currentTargetPitchLeftLeg   = 0;
            currentTargetPitchRightLeg  = 0;

            //reset current leg target if the position has changed
            switch ( newLegsPosition )
            {
                case EWalk:
                {
                    assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EWalk1, LegTarget.EWalk2,   }   );
                    assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EWalk2, LegTarget.EWalk1,   }   );
                    break;
                }

                case EStandSpreadLegged:
                {
                    assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EStandingSpreadLegged, }   );
                    assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EStandingSpreadLegged, }   );
                    break;
                }

                case EKickRight:
                {
                    assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EKicking,                }   );
                    break;
                }

                case EKickLeft:
                {
                    assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EKicking,                }   );
                    assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    break;
                }

                case EKickRightHigh:
                {
                    assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EKickingHigh,            }   );
                    assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    break;
                }

                case EKickLeftHigh:
                {
                    assignLegTargets( Leg.ELeft,   new LegTarget[] { LegTarget.EStandingSpreadLegged,   }   );
                    assignLegTargets( Leg.ERight,  new LegTarget[] { LegTarget.EKickingHigh,                }   );
                    break;
                }
            }
        }

        private void assignLegTargets( Leg leg, LegTarget[] newLegTargets )
        {
            limbsStandStill = false;

            //reset current leg targets
            currentTargetPitchLeftLeg   = 0;
            currentTargetPitchRightLeg  = 0;

            Rotation[] upperLegPitch      = new Rotation[ newLegTargets.length ];
            Rotation[] lowerLegPitch      = new Rotation[ newLegTargets.length ];
            Rotation[] footPitch          = new Rotation[ newLegTargets.length ];

            for ( int i = 0; i < newLegTargets.length; ++i )
            {
                upperLegPitch[ i ] = getNewLegPosition( leg, newLegTargets[ i ] )[ 0 ];
                lowerLegPitch[ i ] = getNewLegPosition( leg, newLegTargets[ i ] )[ 1 ];
                footPitch[     i ] = getNewLegPosition( leg, newLegTargets[ i ] )[ 2 ];
            }

            //assign to specified leg
            switch ( leg )
            {
                case ELeft:
                {
                    //flip y axis for left leg!

                    iLeftUpperLeg.setTargetPitchs(   upperLegPitch );
                    iLeftLowerLeg.setTargetPitchs(   lowerLegPitch );
                    iLeftFoot.setTargetPitchs(       footPitch );
                    break;
                }

                case ERight:
                {
                    iRightUpperLeg.setTargetPitchs(  upperLegPitch );
                    iRightLowerLeg.setTargetPitchs(  lowerLegPitch );
                    iRightFoot.setTargetPitchs(      footPitch );

                    break;
                }
            }
        }

        public void assignHeadPosition( HeadPosition newHeadPosition )
        {
            iCurrentHeadPosition = newHeadPosition;
            switch ( iCurrentHeadPosition )
            {
                case EStill:
                {
                    assignHeadTargets( new HeadTarget[] { HeadTarget.EDefault, }, false );
                    break;
                }

                case ENodOnce:
                {
                    assignHeadTargets( new HeadTarget[] { HeadTarget.EDefault, HeadTarget.EAcceptDown, HeadTarget.EDefault, }, false );
                    break;
                }

                case ENodTwice:
                {
                    assignHeadTargets( new HeadTarget[] { HeadTarget.EDefault, HeadTarget.EAcceptDown, HeadTarget.EDefault, HeadTarget.EAcceptDown, HeadTarget.EDefault, }, false );
                    break;
                }
            }
        }

        public boolean isAssignedHeadPosition( HeadPosition toCheck )
        {
            return ( iCurrentHeadPosition == toCheck );
        }

        private void assignHeadTargets( HeadTarget[] newHeadTargets, boolean repeat )
        {
            limbsStandStill = false;

            //reset current head targets
            currentTargetPitchHead      = 0;
            repeatTargetPitchesHead     = repeat;
            completedTargetPitchesHead  = false;

            Rotation[] headPitch        = new Rotation[ newHeadTargets.length ];

            for ( int i = 0; i < newHeadTargets.length; ++i )
            {
                headPitch[     i ] = getNewHeadPosition( newHeadTargets[ i ] );
            }

            iHead.setTargetPitchs(      headPitch );
            iFace.setTargetPitchs(      headPitch );

            if ( iHat     != null ) iHat.setTargetPitchs(       headPitch );
            if ( iGlasses != null ) iGlasses.setTargetPitchs(   headPitch );
        }

        private final Rotation[] getNewLegPosition( Leg leg, LegTarget newLegPosition )
        {
            Rotation upperLegPitch  = new Rotation();
            Rotation lowerLegPitch  = new Rotation();
            Rotation footPitch      = new Rotation();

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

            return new Rotation[] { upperLegPitch, lowerLegPitch, footPitch, };
        }


        private final Rotation getNewHeadPosition( HeadTarget newHeadPosition )
        {
            Rotation headPitch      = new Rotation();

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

            if ( !limbsStandStill || dying || equipmentChanged || playerMoved )
            {
                //ShooterDebug.bot.out( "performance-intensive setTargetPitches .." );

                //cache the bot!
                limbsStandStill = setTargetPitches();
            }

            //set target pitches
            if ( !limbsStandStill || faceAngleChanged || dying || equipmentChanged || playerMoved )
            {
                //ShooterDebug.bot.out( "performance-intensive transformAllLimbs .." );

                //perform transformations on limbs - this costs lots of performance :(
                transformAllLimbs();
            }

            //turn all faces x around bot's anchor
            if ( dying )
            {
                turnAllLimbsX( getAnchor(), dyingAngle );
            }

            //turn all faces around bot's anchor
            if ( !limbsStandStill || faceAngleChanged || dying || equipmentChanged || playerMoved )
            {
                if ( facingAngle != 0.0f ) turnAllLimbsZ( getAnchor(), facingAngle );
            }

            equipmentChanged = false;
        }

        private boolean setTargetPitches()
        {
            boolean noLimbMoved = true;

            //head
            boolean h = iHead.reachToTargetPitch(       currentTargetPitchHead );
                        iFace.reachToTargetPitch(       currentTargetPitchHead );
                        if ( iHat     != null ) iHat.reachToTargetPitch(        currentTargetPitchHead );
                        if ( iGlasses != null ) iGlasses.reachToTargetPitch(    currentTargetPitchHead );

            //check if all limbs of the head reached finish
            if ( h )
            {
                if ( iHead.iTargetPitch.size() > 1 )
                {
                    //repeat ?
                    if ( repeatTargetPitchesHead )
                    {
                        noLimbMoved = false;
                        ++currentTargetPitchHead;
                        if ( currentTargetPitchHead >= iHead.iTargetPitch.size() )
                        {
                            currentTargetPitchHead = 0;
                        }
                    }
                    else
                    {
                        if ( currentTargetPitchHead < iHead.iTargetPitch.size() - 1 )
                        {
                            noLimbMoved = false;
                            ++currentTargetPitchHead;
                        }
                        else
                        {
                            completedTargetPitchesHead = true;
                        }
                    }
                }
                else
                {
                    completedTargetPitchesHead = true;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //right arm
            boolean ra1 = iRightUpperArm.reachToTargetPitch( currentTargetPitchRightArm );
            boolean ra2 = iRightLowerArm.reachToTargetPitch( currentTargetPitchRightArm );
            boolean ra3 = iRightHand.reachToTargetPitch(     currentTargetPitchRightArm );
                          if ( iEquippedItemRight != null ) iEquippedItemRight.reachToTargetPitch(  currentTargetPitchRightArm );
            //check if all limbs of the left arm reached finish
            if ( ra1 & ra2 & ra3 )
            {
                if ( iRightUpperArm.iTargetPitch.size() > 1 )
                {
                    if ( repeatTargetPitchesRightArm )
                    {
                        noLimbMoved = false;
                        ++currentTargetPitchRightArm;
                        if ( currentTargetPitchRightArm >= iRightUpperArm.iTargetPitch.size() )
                        {
                            currentTargetPitchRightArm = 0;
                        }
                    }
                    else
                    {
                        if ( currentTargetPitchRightArm < iRightUpperArm.iTargetPitch.size() - 1 )
                        {
                            noLimbMoved = false;
                            ++currentTargetPitchRightArm;
                        }
                        else
                        {
                            completedTargetPitchesRightArm = true;
                        }
                    }
                }
                else
                {
                    completedTargetPitchesRightArm = true;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //left arm
            boolean la1 = iLeftUpperArm.reachToTargetPitch( currentTargetPitchLeftArm );
            boolean la2 = iLeftLowerArm.reachToTargetPitch( currentTargetPitchLeftArm );
            boolean la3 = iLeftHand.reachToTargetPitch(     currentTargetPitchLeftArm );
                          if ( iEquippedItemLeft != null ) iEquippedItemLeft.reachToTargetPitch(  currentTargetPitchLeftArm );
            //check if all limbs of the left arm reached finish
            if ( la1 & la2 & la3 )
            {
                if ( iLeftUpperArm.iTargetPitch.size() > 1 )
                {
                    if ( repeatTargetPitchesLeftArm )
                    {
                        noLimbMoved = false;
                        ++currentTargetPitchLeftArm;
                        if ( currentTargetPitchLeftArm >= iLeftUpperArm.iTargetPitch.size() )
                        {
                            currentTargetPitchLeftArm = 0;
                        }
                    }
                    else
                    {
                        if ( currentTargetPitchLeftArm < iLeftUpperArm.iTargetPitch.size() - 1 )
                        {
                            noLimbMoved = false;
                            ++currentTargetPitchLeftArm;
                        }
                        else
                        {
                            completedTargetPitchesLeftArm = true;
                        }
                    }
                }
                else
                {
                    completedTargetPitchesLeftArm = true;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //right leg
            boolean rl1 = iRightUpperLeg.reachToTargetPitch( currentTargetPitchRightLeg );
            boolean rl2 = iRightLowerLeg.reachToTargetPitch( currentTargetPitchRightLeg );
            boolean rl3 = iRightFoot.reachToTargetPitch(     currentTargetPitchRightLeg );
            //check if all limbs of the right leg reached finish
            if ( rl1 & rl2 & rl3 )
            {
                if ( iRightUpperLeg.iTargetPitch.size() > 1 )
                {
                    noLimbMoved = false;

                    ++currentTargetPitchRightLeg;
                    if ( currentTargetPitchRightLeg >= iRightUpperLeg.iTargetPitch.size() ) currentTargetPitchRightLeg = 0;
                }
            }
            else
            {
                noLimbMoved = false;
            }

            //left leg
            boolean ll1 = iLeftUpperLeg.reachToTargetPitch( currentTargetPitchLeftLeg );
            boolean ll2 = iLeftLowerLeg.reachToTargetPitch( currentTargetPitchLeftLeg );
            boolean ll3 = iLeftFoot.reachToTargetPitch(     currentTargetPitchLeftLeg );
            //check if all limbs of the left leg reached finish
            if ( ll1 & ll2 & ll3 )
            {
                if ( iLeftUpperLeg.iTargetPitch.size() > 1 )
                {
                    noLimbMoved = false;

                    ++currentTargetPitchLeftLeg;
                    if ( currentTargetPitchLeftLeg >= iLeftUpperLeg.iTargetPitch.size() ) currentTargetPitchLeftLeg = 0;
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
            iHead.translateAndRotateLimb(                               BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            iFace.translateAndRotateLimb(                               BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            if ( iHat     != null ) iHat.translateAndRotateLimb(        BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );
            if ( iGlasses != null ) iGlasses.translateAndRotateLimb(    BotMeshes.OFFSET_ABSOLUTE_HEAD                                                                          );

            //right arm
            LibVertex rightUpperArmAnk = iRightUpperArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_UPPER_ARM );
            LibVertex rightLowerArmAnk = iRightLowerArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_LOWER_ARM );
            LibVertex rightHandArmAnk  = iRightHand.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_RIGHT_HAND      );
            if ( iEquippedItemRight != null ) iEquippedItemRight.translateLimb(   BotMeshes.OFFSET_ABSOLUTE_RIGHT_HAND      );
            iRightUpperArm.rotateAroundAnchor( rightUpperArmAnk, iRightUpperArm.iPitch );
            iRightLowerArm.rotateAroundAnchor( rightUpperArmAnk, iRightUpperArm.iPitch );
            iRightHand.rotateAroundAnchor(     rightUpperArmAnk, iRightUpperArm.iPitch );
            if ( iEquippedItemRight != null ) iEquippedItemRight.rotateAroundAnchor(     rightUpperArmAnk, iRightUpperArm.iPitch );
            rightLowerArmAnk.rotateXYZ( iRightUpperArm.iPitch.x, iRightUpperArm.iPitch.y, iRightUpperArm.iPitch.z, rightUpperArmAnk );
            rightHandArmAnk.rotateXYZ(  iRightUpperArm.iPitch.x, iRightUpperArm.iPitch.y, iRightUpperArm.iPitch.z, rightUpperArmAnk );
            iRightLowerArm.rotateAroundAnchor( rightLowerArmAnk, iRightLowerArm.iPitch );
            iRightHand.rotateAroundAnchor(     rightLowerArmAnk, iRightLowerArm.iPitch );
            if ( iEquippedItemRight != null ) iEquippedItemRight.rotateAroundAnchor(     rightLowerArmAnk, iRightLowerArm.iPitch );
            rightHandArmAnk.rotateXYZ(  iRightLowerArm.iPitch.x, iRightLowerArm.iPitch.y, iRightLowerArm.iPitch.z, rightLowerArmAnk );
            iRightHand.rotateAroundAnchor(     rightHandArmAnk, iRightHand.iPitch );
            if ( iEquippedItemRight != null ) iEquippedItemRight.rotateAroundAnchor(     rightHandArmAnk, iRightHand.iPitch );

            //left arm
            LibVertex leftUpperArmAnk = iLeftUpperArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_UPPER_ARM );
            LibVertex leftLowerArmAnk = iLeftLowerArm.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_LOWER_ARM );
            LibVertex leftHandArmAnk  = iLeftHand.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_LEFT_HAND      );
            if ( iEquippedItemLeft != null ) iEquippedItemLeft.translateLimb(   BotMeshes.OFFSET_ABSOLUTE_LEFT_HAND      );
            iLeftUpperArm.rotateAroundAnchor( leftUpperArmAnk, iLeftUpperArm.iPitch );
            iLeftLowerArm.rotateAroundAnchor( leftUpperArmAnk, iLeftUpperArm.iPitch );
            iLeftHand.rotateAroundAnchor(     leftUpperArmAnk, iLeftUpperArm.iPitch );
            if ( iEquippedItemLeft != null ) iEquippedItemLeft.rotateAroundAnchor(     leftUpperArmAnk, iLeftUpperArm.iPitch );
            leftLowerArmAnk.rotateXYZ( iLeftUpperArm.iPitch.x, iLeftUpperArm.iPitch.y, iLeftUpperArm.iPitch.z, leftUpperArmAnk );
            leftHandArmAnk.rotateXYZ(  iLeftUpperArm.iPitch.x, iLeftUpperArm.iPitch.y, iLeftUpperArm.iPitch.z, leftUpperArmAnk );
            iLeftLowerArm.rotateAroundAnchor( leftLowerArmAnk, iLeftLowerArm.iPitch );
            iLeftHand.rotateAroundAnchor(     leftLowerArmAnk, iLeftLowerArm.iPitch );
            if ( iEquippedItemLeft != null ) iEquippedItemLeft.rotateAroundAnchor(     leftLowerArmAnk, iLeftLowerArm.iPitch );
            leftHandArmAnk.rotateXYZ(  iLeftLowerArm.iPitch.x, iLeftLowerArm.iPitch.y, iLeftLowerArm.iPitch.z, leftLowerArmAnk );
            iLeftHand.rotateAroundAnchor(     leftHandArmAnk, iLeftHand.iPitch );
            if ( iEquippedItemLeft != null ) iEquippedItemLeft.rotateAroundAnchor(     leftHandArmAnk, iLeftHand.iPitch );

            //right leg
            LibVertex rightUpperLegAnk = iRightUpperLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_UPPER_LEG );
            LibVertex rightLowerLegAnk = iRightLowerLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_RIGHT_LOWER_LEG );
            LibVertex rightfootLegAnk  = iRightFoot.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_RIGHT_FOOT      );
            iRightUpperLeg.rotateAroundAnchor( rightUpperLegAnk, iRightUpperLeg.iPitch );
            iRightLowerLeg.rotateAroundAnchor( rightUpperLegAnk, iRightUpperLeg.iPitch );
            iRightFoot.rotateAroundAnchor(     rightUpperLegAnk, iRightUpperLeg.iPitch );
            rightLowerLegAnk.rotateXYZ( iRightUpperLeg.iPitch.x, iRightUpperLeg.iPitch.y, iRightUpperLeg.iPitch.z, rightUpperLegAnk );
            rightfootLegAnk.rotateXYZ(  iRightUpperLeg.iPitch.x, iRightUpperLeg.iPitch.y, iRightUpperLeg.iPitch.z, rightUpperLegAnk );
            iRightLowerLeg.rotateAroundAnchor( rightLowerLegAnk, iRightLowerLeg.iPitch );
            iRightFoot.rotateAroundAnchor(     rightLowerLegAnk, iRightLowerLeg.iPitch );
            rightfootLegAnk.rotateXYZ(  iRightLowerLeg.iPitch.x, iRightLowerLeg.iPitch.y, iRightLowerLeg.iPitch.z, rightLowerLegAnk );
            iRightFoot.rotateAroundAnchor(     rightfootLegAnk, iRightFoot.iPitch );

            //left leg
            LibVertex leftUpperLegAnk = iLeftUpperLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_UPPER_LEG );
            LibVertex leftLowerLegAnk = iLeftLowerLeg.translateLimb(            BotMeshes.OFFSET_ABSOLUTE_LEFT_LOWER_LEG );
            LibVertex leftfootLegAnk  = iLeftFoot.translateLimb(                BotMeshes.OFFSET_ABSOLUTE_LEFT_FOOT      );
            iLeftUpperLeg.rotateAroundAnchor( leftUpperLegAnk, iLeftUpperLeg.iPitch );
            iLeftLowerLeg.rotateAroundAnchor( leftUpperLegAnk, iLeftUpperLeg.iPitch );
            iLeftFoot.rotateAroundAnchor(     leftUpperLegAnk, iLeftUpperLeg.iPitch );
            leftLowerLegAnk.rotateXYZ( iLeftUpperLeg.iPitch.x, iLeftUpperLeg.iPitch.y, iLeftUpperLeg.iPitch.z, leftUpperLegAnk );
            leftfootLegAnk.rotateXYZ(  iLeftUpperLeg.iPitch.x, iLeftUpperLeg.iPitch.y, iLeftUpperLeg.iPitch.z, leftUpperLegAnk );
            iLeftLowerLeg.rotateAroundAnchor( leftLowerLegAnk, iLeftLowerLeg.iPitch );
            iLeftFoot.rotateAroundAnchor(     leftLowerLegAnk, iLeftLowerLeg.iPitch );
            leftfootLegAnk.rotateXYZ(  iLeftLowerLeg.iPitch.x, iLeftLowerLeg.iPitch.y, iLeftLowerLeg.iPitch.z, leftLowerLegAnk );
            iLeftFoot.rotateAroundAnchor(     leftfootLegAnk, iLeftFoot.iPitch );

            //torso and neck are not transformed neither translated
            iTorso.translateLimb( new Offset( 0.0f, 0.0f, 0.0f ) );
            iNeck.translateLimb(  new Offset( 0.0f, 0.0f, 0.0f ) );
        }

        public void turnAllLimbsX( LibVertex botAnchor, float angleX )
        {
            for ( Mesh mesh : iMeshes )
            {
                mesh.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, angleX, 0.0f, 0.0f, botAnchor, LibTransformationMode.ETransformedToTransformed );
            }
        }

        public void turnAllLimbsZ( LibVertex botAnchor, float facingAngle )
        {
            for ( Mesh mesh : iMeshes )
            {
                mesh.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, facingAngle, botAnchor, LibTransformationMode.ETransformedToTransformed );
            }
        }

        public void changeFaceTexture( LibGLTexture oldTex, LibGLTexture newTex )
        {
            iFace.changeTexture( oldTex, newTex );
        }

        public void fadeOutAllFaces()
        {
            if ( iGlasses           != null ) iGlasses          .fadeOutAllFaces();
            if ( iHat               != null ) iHat              .fadeOutAllFaces();
            if ( iHead              != null ) iHead             .fadeOutAllFaces();
            if ( iFace              != null ) iFace             .fadeOutAllFaces();
            if ( iRightUpperArm     != null ) iRightUpperArm    .fadeOutAllFaces();
            if ( iRightLowerArm     != null ) iRightLowerArm    .fadeOutAllFaces();
            if ( iTorso             != null ) iTorso            .fadeOutAllFaces();
            if ( iNeck              != null ) iNeck             .fadeOutAllFaces();
            if ( iLeftUpperArm      != null ) iLeftUpperArm     .fadeOutAllFaces();
            if ( iLeftLowerArm      != null ) iLeftLowerArm     .fadeOutAllFaces();
            if ( iRightHand         != null ) iRightHand        .fadeOutAllFaces();
            if ( iLeftHand          != null ) iLeftHand         .fadeOutAllFaces();
            if ( iRightUpperLeg     != null ) iRightUpperLeg    .fadeOutAllFaces();
            if ( iLeftUpperLeg      != null ) iLeftUpperLeg     .fadeOutAllFaces();
            if ( iRightLowerLeg     != null ) iRightLowerLeg    .fadeOutAllFaces();
            if ( iLeftLowerLeg      != null ) iLeftLowerLeg     .fadeOutAllFaces();
            if ( iRightFoot         != null ) iRightFoot        .fadeOutAllFaces();
            if ( iLeftFoot          != null ) iLeftFoot         .fadeOutAllFaces();
            if ( iEquippedItemLeft  != null ) iEquippedItemLeft .fadeOutAllFaces();
            if ( iEquippedItemRight != null ) iEquippedItemRight.fadeOutAllFaces();
        }

        public void setItem( Arm arm, Items newItem, LibVertex anchor, LibGameObject aParentGameObject )
        {
            //ShooterDebug.bugfix.out( "Set item [" + newItem + "] on arm [" + arm + "]" );

            Vector<Mesh> newMeshes = new Vector<Mesh>();
            newMeshes.addAll( Arrays.asList( iMeshes ) );

            switch ( arm )
            {
                case ELeft:
                {
                    //remove old item
                    if ( iEquippedItemLeft != null )
                    {
                        newMeshes.remove( iEquippedItemLeft );
                        equipmentChanged = true;
                        iEquippedItemLeft = null;
                    }
                    if ( newItem != null )
                    {
                        //rotate item on startup
                        Mesh item = new Mesh( ShooterD3ds.getFaces( newItem ), new LibVertex( 0.0f, 0.0f, 0.0f ) );
                        item.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 270.0f, 0.0f, 90.0f, null, LibTransformationMode.EOriginalsToOriginals );

                        iEquippedItemLeft  = new BotMesh( item.getFaces(), anchor, 0.0f, 1.0f, aParentGameObject, 0.0f );
                        iEquippedItemLeft.mirrorFaces( true, false, false );

                        iEquippedItemLeft.setTargetPitchs( iHead.iTargetPitch.toArray( new Rotation[] {} ) );
                        newMeshes.add( iEquippedItemLeft  );
                        equipmentChanged = true;
                    }
                    break;
                }

                case ERight:
                {
                    //remove old item
                    if ( iEquippedItemRight != null )
                    {
                        newMeshes.remove( iEquippedItemRight );
                        equipmentChanged = true;
                        iEquippedItemRight = null;
                    }
                    if ( newItem != null )
                    {
                        //rotate item on startup
                        Mesh item = new Mesh( ShooterD3ds.getFaces( newItem ), new LibVertex( 0.0f, 0.0f, 0.0f ) );
                        item.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, 270.0f, 0.0f, 90.0f, null, LibTransformationMode.EOriginalsToOriginals );

                        iEquippedItemRight = new BotMesh( item.getFaces(), anchor, 0.0f, 1.0f, aParentGameObject, 0.0f );

                        iEquippedItemRight.setTargetPitchs( iHead.iTargetPitch.toArray( new Rotation[] {} ) );
                        newMeshes.add( iEquippedItemRight );
                        equipmentChanged = true;
                    }
                    break;
                }
            }

            //return as array
            iMeshes = newMeshes.toArray( new Mesh[] {} );
        }
    }
