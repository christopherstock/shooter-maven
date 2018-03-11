/*  $Id: LibMaxTriangle.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =================================================================================
 *  The 3dsmax-loader.
 */
    package de.christopherstock.lib.io.d3ds;

    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.ui.*;

    public class LibMaxTriangle
    {
        public          LibVertex       iAnchor         = null;
        public          String          iTextureName    = null;
        public          LibColors       iCol            = null;
        public          LibVertex       iA              = null;
        public          LibVertex       iB              = null;
        public          LibVertex       iC              = null;
        public          LibVertex       iFaceNormal     = null;

        public LibMaxTriangle( LibVertex aAnchor, String aTextureName, LibColors aCol, LibVertex aA, LibVertex aB, LibVertex aC, LibVertex aFaceNormal )
        {
            iAnchor         = aAnchor;
            iTextureName    = aTextureName;
            iCol            = aCol;
            iA              = aA;
            iB              = aB;
            iC              = aC;
            iFaceNormal     = aFaceNormal;
        }
    }
