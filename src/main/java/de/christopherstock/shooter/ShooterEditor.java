
    package de.christopherstock.shooter;

    import  javax.swing.JPanel;
    import  javax.swing.UIManager;
    import  de.christopherstock.lib.LibDebug;
    import  de.christopherstock.shooter.editor.EditorCanvas;
    import  de.christopherstock.shooter.editor.EditorFrame;
    import  de.christopherstock.shooter.editor.EditorScrollPane;

    /**************************************************************************************
    *   The level editor.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class ShooterEditor
    {
        public      static      EditorFrame     frame           = null;
        public      static      JPanel          contentPanel    = null;

        public static final void main( String[] args )
        {
            //assign look and feel
            setLookAndFeel( ShooterDebug.editor );

            //create editor canvas and scrolling pane
            EditorCanvas        canvas  = new EditorCanvas();
            EditorScrollPane    scroll  = new EditorScrollPane();
            scroll.setViewportView( canvas );

            contentPanel = new JPanel();
            contentPanel.add( scroll );

            frame = new EditorFrame( contentPanel );
        }

        /********************************************************************************
         *   Sets lookAndFeel of the host operating system for all ui elements to come.
         *********************************************************************************/
         public static final void setLookAndFeel( LibDebug debug )
         {
             try
             {
                 UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
             }
             catch( Exception e )
             {
                 debug.err( "Setting host-operating system lookAndFeel failed!\n" + e.toString() );
             }
         }
    }
