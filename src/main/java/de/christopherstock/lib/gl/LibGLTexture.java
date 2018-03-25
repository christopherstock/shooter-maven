
    package de.christopherstock.lib.gl;

    import  java.io.Serializable;

    /*******************************************************************************************************************
    *   The Texture-System.
    *******************************************************************************************************************/
    public class LibGLTexture implements Serializable
    {
        public static enum Translucency
        {
            EGlass,
            EOpaque,
            EHasMask,
            EHasMaskBulletHole,
            ;
        }

        private     static          int                 freeID              = 0;

        private                     int                 id                  = 0;
        private                     Translucency        translucency        = null;
        private                     LibMaterial         material            = null;
        private                     Integer             maskId              = null;

        public LibGLTexture( int aId, Translucency aTranslucency, LibMaterial aMaterial, Integer aMaskId )
        {
            this.id = aId;
            this.translucency = aTranslucency;
            this.material = aMaterial;
            this.maskId = aMaskId;
        }

        public final int getId()
        {
            return this.id;
        }

        public final LibMaterial getMaterial()
        {
            return this.material;
        }

        public final Translucency getTranslucency()
        {
            return this.translucency;
        }

        public final int getMaskId()
        {
            return this.maskId;
        }

        public static int getNextFreeID()
        {
            return freeID++;
        }
    }
