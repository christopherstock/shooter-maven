
    package de.christopherstock.lib.io.d3ds;

    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.ui.*;

    public class LibMaxTriangle
    {
        public          LibVertex       anchor          = null;
        public          String          textureName     = null;
        public          LibColors       col             = null;
        public          LibVertex       a               = null;
        public          LibVertex       b               = null;
        public          LibVertex       c               = null;
        public          LibVertex       faceNormal      = null;

        public LibMaxTriangle( LibVertex anchor, String textureName, LibColors col, LibVertex a, LibVertex b, LibVertex c, LibVertex faceNormal )
        {
            this.anchor      = anchor;
            this.textureName = textureName;
            this.col         = col;
            this.a           = a;
            this.b           = b;
            this.c           = c;
            this.faceNormal  = faceNormal;
        }
    }
