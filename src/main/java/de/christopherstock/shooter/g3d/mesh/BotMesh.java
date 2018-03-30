
    package de.christopherstock.shooter.g3d.mesh;

    import  java.util.*;
    import  de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.LibInvert;
    import  de.christopherstock.lib.LibOffset;
    import  de.christopherstock.lib.LibRotation;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;

    /*******************************************************************************************************************
    *   Represents a mesh.
    *******************************************************************************************************************/
    public class BotMesh extends Mesh
    {
        public                  LibRotation                 pitch               = new LibRotation();
        public                  float                       limbSpeed           = 0.0f;
        public                  Vector<LibRotation>         targetPitch         = new Vector<LibRotation>();

        /***************************************************************************************************************
        *   Constructs a new mesh with the specified properties.
        *
        *   @param  faces   All faces this mesh shall consist of.
        *   @param  anchor  The meshes' anchor point.
        ***************************************************************************************************************/
        public BotMesh( LibFaceTriangle[] faces, LibVertex anchor, float initRotZ, float initScale, LibGameObject parentGameObject, float damageMultiplier )
        {
            super( faces, anchor, initRotZ, initScale, LibInvert.ENo, parentGameObject, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );
            for ( LibFaceTriangle ft : faces )
            {
                ft.setDamageMultiplier( damageMultiplier );
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
            this.translateAndRotateXYZ(  limbAnk.x,  limbAnk.y,  limbAnk.z, this.pitch.x, this.pitch.y, this.pitch.z,       limbAnk,    LibTransformationMode.EOriginalsToTransformed      );

            return limbAnk;
        }

        public final void rotateAroundAnchor( LibVertex anchor, LibRotation pitch )
        {
            //translate and turn around x, y and z axis sequentially is no more necessary!?
            this.translateAndRotateXYZ( 0.0f, 0.0f, 0.0f, pitch.x, pitch.y, pitch.z, anchor, LibTransformationMode.ETransformedToTransformed );
        }

        public final void transformOwn( LibOffset trans, float rotX, float rotY, float rotZ )
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
            this.targetPitch = new Vector<LibRotation>( Arrays.asList( targetPitchs ) );
        }

        public final boolean reachToTargetPitch( int currentTargetPitch )
        {
            //check if the target pitch is already reached
            boolean skipReach = false;
            boolean reached   = false;
            if (this.pitch.equalRounded(this.targetPitch.elementAt( currentTargetPitch ) ) )
            {
                if (this.targetPitch.size() == 1 )
                {
                    skipReach = true;
                }

                reached = true;
            }

            if ( !skipReach )
            {
                this.pitch.reachToAbsolute(this.targetPitch.elementAt( currentTargetPitch ), this.targetPitch.elementAt( currentTargetPitch ).speed);
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
            ownAnk.rotateXYZ( otherLimb.pitch.x,       0.0f,                       0.0f,                   otherAnk    );
            ownAnk.rotateXYZ( 0.0f,                     otherLimb.pitch.y,         0.0f,                   otherAnk    );
            ownAnk.rotateXYZ( 0.0f,                     0.0f,                       otherLimb.pitch.z,     otherAnk    );

          //ownAnk.rotateXYZ( otherLimb.pitch.x,   otherLimb.pitch.y,                       otherLimb.pitch.z,                   otherAnk    );

            //translate and pitch around all axis sequentially

            this.translateAndRotateXYZ(  0.0f,       0.0f,       0.0f,       ownLimb.pitch.x,       0.0f,                   0.0f,                   ownAnk, LibTransformationMode.ETransformedToTransformed    );
            this.translateAndRotateXYZ(  0.0f,       0.0f,       0.0f,       0.0f,                   ownLimb.pitch.y,       0.0f,                   ownAnk, LibTransformationMode.ETransformedToTransformed    );
            this.translateAndRotateXYZ(  0.0f,       0.0f,       0.0f,       0.0f,                   0.0f,                   ownLimb.pitch.z,       ownAnk, LibTransformationMode.ETransformedToTransformed    );

            return ownAnk;
        }
    }
