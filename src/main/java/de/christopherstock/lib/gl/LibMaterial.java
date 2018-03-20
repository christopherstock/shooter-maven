
    package de.christopherstock.lib.gl;

    import  de.christopherstock.lib.io.hid.*;
    import  de.christopherstock.lib.ui.*;

    /**************************************************************************************
    *   Represents a material that is bound to a texture.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract interface LibMaterial
    {
        public abstract LibColors[] getSliverColors();

        public abstract boolean isPenetrable();

        public abstract LibTexture getBulletHoleTexture();

        public abstract LibSound getBulletImpactSound();
    }
