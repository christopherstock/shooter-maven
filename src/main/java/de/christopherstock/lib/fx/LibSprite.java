/*  $Id: Sprite.java 1242 2013-01-02 15:32:27Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
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
