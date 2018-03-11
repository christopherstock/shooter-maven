/*  $Id: LibGeomObject.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.lib.g3d;

    import  java.util.*;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.math.*;

    /**************************************************************************************
    *   Represents a cylinder.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract interface LibGeomObject
    {
        public abstract LibVertex getAnchor();
        public abstract boolean checkCollisionHorz( LibCylinder c );
        public abstract Vector<Float> checkCollisionVert( LibCylinder c, Object exclude );
        public abstract void translate( float tX, float tY, float tZ, LibTransformationMode transformationMode );
        public abstract void setNewAnchor( LibVertex newAnchor, boolean performTranslationOnFaces, LibTransformationMode transformationMode );
    }
