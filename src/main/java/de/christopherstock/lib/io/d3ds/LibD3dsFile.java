
    package de.christopherstock.lib.io.d3ds;
        
    /*******************************************************************************************************************
     *   The interface to a 3ds file.
     ******************************************************************************************************************/
    public interface LibD3dsFile
    {
        void initFile( LibD3dsImporter d3dsFile );

        LibD3dsImporter getFile();
    }
    