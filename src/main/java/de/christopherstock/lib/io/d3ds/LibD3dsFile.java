/*  $Id: ShooterD3ds.java 1244 2013-01-02 15:53:29Z jenetic.bytemare@googlemail.com $
 *  =================================================================================
 */   
    package de.christopherstock.lib.io.d3ds;
        
    /********************************************************************************
     *   The interface to a 3ds file.
     ********************************************************************************/
    public interface LibD3dsFile
    {
        public abstract void initFile( LibD3dsImporter aD3dsFile );
        public abstract LibD3dsImporter getFile();
    }
    