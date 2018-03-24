
    package de.christopherstock.lib.g3d;

    import  java.util.*;
    import de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   Represents a cylinder.
    *******************************************************************************************************************/
    public interface LibGeomObject
    {
        LibVertex getAnchor();
        boolean checkCollisionHorz(LibCylinder c);
        Vector<Float> checkCollisionVert(LibCylinder c, Object exclude);
        void translate(float tX, float tY, float tZ, LibTransformationMode transformationMode);
        void setNewAnchor(LibVertex newAnchor, boolean performTranslationOnFaces, LibTransformationMode transformationMode);
    }
