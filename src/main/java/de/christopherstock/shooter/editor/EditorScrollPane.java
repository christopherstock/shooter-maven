/*  $Id: EditorScrollPane.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.editor;

    import  java.awt.*;

    import  javax.swing.*;

    /**************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *
    *   @author         Christopher Stock
    *   @version        0.3.11
    **************************************************************************************/
    public class EditorScrollPane extends JScrollPane
    {
        private         static      final       long        serialVersionUID        = -3442613841565776417L;

        public EditorScrollPane()
        {
           setPreferredSize( new Dimension( 924, 700 ) );

        }
    }
