
    package de.christopherstock.lib.fx;

    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;

    /*******************************************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *******************************************************************************************************************/
    public interface LibSprite
    {
        float getCenterZ();

        void translate(float f, float g, float h, LibTransformationMode eoriginalstooriginals);

        void draw();

        void animateSprite(LibVertex object);
    }
