
    package de.christopherstock.lib.g3d;

    import  java.util.*;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   Represents a cylinder.
    *******************************************************************************************************************/
    public abstract interface LibGeomObject
    {
        public abstract LibVertex getAnchor();
        public abstract boolean checkCollisionHorz( LibCylinder c );
        public abstract Vector<Float> checkCollisionVert( LibCylinder c, Object exclude );
        public abstract void translate( float tX, float tY, float tZ, LibTransformationMode transformationMode );
        public abstract void setNewAnchor( LibVertex newAnchor, boolean performTranslationOnFaces, LibTransformationMode transformationMode );
    }
