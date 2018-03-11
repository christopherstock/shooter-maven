/*  $Id: LibSound.java 1240 2013-01-02 14:43:47Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.lib.io.hid;

    import  java.awt.geom.*;

    /**************************************************************************************
    *   The interface to a sound clip.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract interface LibSound
    {
        public abstract void playDistancedFx( Point2D.Float distantLocation );


    }
