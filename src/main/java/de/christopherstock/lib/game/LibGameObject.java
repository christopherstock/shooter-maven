
    package de.christopherstock.lib.game;

    import  java.util.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   Represents a mesh.
    *******************************************************************************************************************/
    public interface LibGameObject
    {
        enum HitPointCarrier
        {
            EWall,
            EPlayer,
            EBot,
        }

        LibVertex getAnchor();

        HitPointCarrier getHitPointCarrier();

        float getCarriersFaceAngle();

        Vector<LibHitPoint> launchShot( LibShot shot );

        void launchAction( LibCylinder cylinder, Object gadget, float faceAngle );
    }
