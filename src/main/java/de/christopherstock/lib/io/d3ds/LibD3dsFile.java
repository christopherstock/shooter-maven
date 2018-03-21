
    package de.christopherstock.lib.io.d3ds;
        
    /*******************************************************************************************************************
     *   The interface to a 3ds file.
     ******************************************************************************************************************/
    public interface LibD3dsFile
    {
        public abstract void initFile( LibD3dsImporter aD3dsFile );
        public abstract LibD3dsImporter getFile();
    }
    