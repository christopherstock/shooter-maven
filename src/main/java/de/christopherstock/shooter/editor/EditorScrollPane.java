
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
