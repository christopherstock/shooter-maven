
    package de.christopherstock.lib.gl;

    import de.christopherstock.lib.g3d.LibVertex;
    import de.christopherstock.lib.ui.LibColors;

    public interface LibGLFace
    {
        LibVertex getAnchor();
        float        getAlpha();
        float        getDarkenOpacity();
        LibColors getColor();
        float[]      getColor3f();
        LibVertex    getFaceNormal();
        LibGLTexture getTexture();
        LibVertex[]  getVerticesToDraw();
    }
