
    package de.christopherstock.shooter.g3d.mesh;

    import  java.util.*;

    import de.christopherstock.lib.LibTransformationMode;
    import de.christopherstock.lib.LibInvert;
    import de.christopherstock.lib.LibOffset;
    import de.christopherstock.lib.LibRotation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;

    /*******************************************************************************************************************
    *   Represents a mesh.
    *******************************************************************************************************************/
    public class BotMesh extends Mesh
    {
        public                      LibRotation             iPitch                  = new LibRotation();
        public                      float                   iLimbSpeed              = 0.0f;
        public                      Vector<LibRotation>     iTargetPitch            = new Vector<LibRotation>();

        /***************************************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  aFaces              All faces this mesh shall consist of.
        *   @param  aAnchor             The meshes' anchor point.
        ***************************************************************************************************************/
        public BotMesh( LibFaceTriangle[] aFaces, LibVertex aAnchor, float aInitRotZ, float aInitScale, LibGameObject aParentGameObject, float aDamageMultiplier )
        {
            super( aFaces, aAnchor, aInitRotZ, aInitScale, LibInvert.ENo, aParentGameObject, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );
            for ( LibFaceTriangle ft : aFaces )
            {
                ft.setDamageMultiplier( aDamageMultiplier );
            }
        }

        public final LibVertex translateLimb( LibOffset trans )
        {
            LibVertex   limbAnk = this.getAnchor().copy();

            limbAnk.x += trans.x;
            limbAnk.y += trans.y;
            limbAnk.z += trans.z;

            this.translateAndRotateXYZ(  limbAnk.x,  limbAnk.y,  limbAnk.z,  0.0f,    0.0f,       0.0f,       limbAnk,    LibTransformationMode.EOriginalsToTransformed      );

            return limbAnk;
        }

        public final LibVertex translateAndRotateLimb( LibOffset trans )
        {
            //rotate
            LibVertex   limbAnk = this.getAnchor().copy();

                        limbAnk.x += trans.x;
                        limbAnk.y += trans.y;
                        limbAnk.z += trans.z;

            //translate and turn around x, y and z axis sequentially is no more necessary!?
            this.translateAndRotateXYZ(  limbAnk.x,  limbAnk.y,  limbAnk.z, this.iPitch.x, this.iPitch.y, this.iPitch.z,       limbAnk,    LibTransformationMode.EOriginalsToTransformed      );

            return limbAnk;
        }

        public final void rotateAroundAnchor( LibVertex anchor, LibRotation pitch )
        {
            //translate and turn around x, y and z axis sequentially is no more necessary!?
            this.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, pitch.x, pitch.y, pitch.z, anchor, LibTransformationMode.ETransformedToTransformed );
        }

        public final void transformOwn(LibOffset trans, float rotX, float rotY, float rotZ )
        {
            //rotate
            LibVertex   limbAnk = this.getAnchor().copy();

                        limbAnk.x += trans.x;
                        limbAnk.y += trans.y;
                        limbAnk.z += trans.z;

            //translate and turn around x, y and z axis sequentially is no more necessary!?
            this.translateAndRotateXYZ(  limbAnk.x,  limbAnk.y,  limbAnk.z,  rotX,    rotY,       rotZ,       limbAnk,    LibTransformationMode.EOriginalsToTransformed      );
        }

        public final void setTargetPitchs( LibRotation[] targetPitchs )
        {
            this.iTargetPitch = new Vector<LibRotation>( Arrays.asList( targetPitchs ) );
        }

        public final boolean reachToTargetPitch( int currentTargetPitch )
        {
            //check if the target pitch is already reached
            boolean skipReach = false;
            boolean reached   = false;
            if (this.iPitch.equalRounded(this.iTargetPitch.elementAt( currentTargetPitch ) ) )
            {
                if (this.iTargetPitch.size() == 1 )
                {
                    skipReach = true;
                }

                reached = true;
            }

            if ( !skipReach )
            {
                this.iPitch.reachToAbsolute(this.iTargetPitch.elementAt( currentTargetPitch ), this.iTargetPitch.elementAt( currentTargetPitch ).speed);
            }

            return reached;
        }

        /***************************************************************************************************************
        *   Represents a mesh.
        *
        *   @deprecated     This calculation is wrong!
        *                   The anchor point of the child limb has to be rotated like the parent child!
        ***************************************************************************************************************/
        @Deprecated
        public final LibVertex transformAroundOtherLimb(LibOffset trans, BotMesh otherLimb, BotMesh ownLimb, LibVertex otherAnk )
        {
            //roate anchor for lower right arm and right hand
            LibVertex   ownAnk = otherAnk.copy();

                        ownAnk.x += trans.x;
                        ownAnk.y += trans.y;
                        ownAnk.z += trans.z;

            this.translateAndRotateXYZ(  ownAnk.x,   ownAnk.y,   ownAnk.z,   0.0f,                   0.0f,                   0.0f,                   ownAnk, LibTransformationMode.EOriginalsToTransformed      );

            //pitch around all axis sequentially is necessary!
            ownAnk.rotateXYZ( otherLimb.iPitch.x,       0.0f,                       0.0f,                   otherAnk    );
            ownAnk.rotateXYZ( 0.0f,                     otherLimb.iPitch.y,         0.0f,                   otherAnk    );
            ownAnk.rotateXYZ( 0.0f,                     0.0f,                       otherLimb.iPitch.z,     otherAnk    );

          //ownAnk.rotateXYZ( otherLimb.iPitch.x,   otherLimb.iPitch.y,                       otherLimb.iPitch.z,                   otherAnk    );

            //translate and pitch around all axis sequentially

            this.translateAndRotateXYZ(  0.0f,       0.0f,       0.0f,       ownLimb.iPitch.x,       0.0f,                   0.0f,                   ownAnk, LibTransformationMode.ETransformedToTransformed    );
            this.translateAndRotateXYZ(  0.0f,       0.0f,       0.0f,       0.0f,                   ownLimb.iPitch.y,       0.0f,                   ownAnk, LibTransformationMode.ETransformedToTransformed    );
            this.translateAndRotateXYZ(  0.0f,       0.0f,       0.0f,       0.0f,                   0.0f,                   ownLimb.iPitch.z,       ownAnk, LibTransformationMode.ETransformedToTransformed    );

            return ownAnk;
        }
    }
