
    package de.christopherstock.lib.gl;

    import  de.christopherstock.lib.io.hid.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   Represents a material that is bound to a texture.
    *******************************************************************************************************************/
    public interface LibMaterial
    {
        LibColors[] getSliverColors();

        boolean isPenetrable();

        LibTexture getBulletHoleTexture();

        LibSound getBulletImpactSound();
    }
