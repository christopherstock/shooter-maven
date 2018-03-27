
    package de.christopherstock.lib.io.d3ds;

    import  de.christopherstock.lib.ui.*;

    class LibMaxMaterial
    {
        public      String      name        = null;
        public      LibColors   color       = null;
        public      float       offsetU     = 0.0f;
        public      float       offsetV     = 0.0f;
        public      float       tilingU     = 0.0f;
        public      float       tilingV     = 0.0f;

        public LibMaxMaterial( String name, LibColors color, float offsetU, float offsetV, float tilingU, float tilingV )
        {
            this.name    = name;
            this.color   = color;
            this.offsetU = offsetU;
            this.offsetV = offsetV;
            this.tilingU = tilingU;
            this.tilingV = tilingV;
        }
    }
