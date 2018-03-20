
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;

    /**************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public interface LibSprite
    {
        public abstract float getCenterZ();

        public abstract void translate( float f, float g, float h, LibTransformationMode eoriginalstooriginals );

        public abstract void draw();

        public abstract void animateSprite( LibVertex object );
    }
