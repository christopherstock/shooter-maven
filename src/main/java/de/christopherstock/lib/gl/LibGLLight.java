
    package de.christopherstock.lib.gl;

    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   The GL-View.
    *******************************************************************************************************************/
    public class LibGLLight
    {
        public                  LibVertex               ank                 = null;
        public                  float                   rotZ                = 0.0f;
        public                  float                   spotCutoff          = 0.0f;
        public                  LibColors               colDiffuse          = null;

        public LibGLLight( LibVertex ank, float rotZ, float spotCutoff, LibColors colDiffuse )
        {
            this.ank = ank;
            this.rotZ = rotZ;
            this.spotCutoff = spotCutoff;
            this.colDiffuse = colDiffuse;
        }
    }
