
    package de.christopherstock.lib.game;

    import  java.util.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.math.*;

    /**************************************************************************************
    *   Represents a mesh.
    **************************************************************************************/
    public abstract interface LibGameObject
    {
        public static enum HitPointCarrier
        {
            EWall,
            EPlayer,
            EBot,
        }

        public abstract LibVertex getAnchor();
        public abstract HitPointCarrier getHitPointCarrier();
        public abstract float getCarriersFaceAngle();
        public abstract Vector<LibHitPoint> launchShot( LibShot shot );
        public abstract void launchAction( LibCylinder aCylinder, Object gadget, float faceAngle );
    }
