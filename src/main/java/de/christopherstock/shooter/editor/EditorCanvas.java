
    package de.christopherstock.shooter.editor;

    import  java.awt.Color;
    import  java.awt.Dimension;
    import  java.awt.GradientPaint;
    import  java.awt.Graphics;
    import  java.awt.Graphics2D;
    import  java.awt.Paint;
    import  java.awt.RenderingHints;
    import  javax.swing.JPanel;

    /*******************************************************************************************************************
    *   The Form holds the gl-canvas and the preloader-view.
    *******************************************************************************************************************/
    public class EditorCanvas extends JPanel
    {
        protected   static  final   Paint   GRADIENT_PAINT      = new GradientPaint(0, 0, Color.blue, 50, 50, Color.red, true);

        public      static  final   int     CANVAS_WIDTH        = 2000;
        public      static  final   int     CANVAS_HEIGHT       = 2000;

        public      static  final   int     GRID_SIZE           = 100;

        public EditorCanvas()
        {
            this.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );
        }

        @Override
        public void paint( Graphics g ) // paintComponent( Graphics g ) ?
        {
           super.paint( g );

           Graphics2D g2 = (Graphics2D)g; //.create();
           g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

           //draw bg
           g.setColor( Color.WHITE );
           g.fillRect( 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT );
/*
           g2.setPaint( GRADIENT_PAINT );
           g2.fillOval( 0, 0, getWidth(), getHeight() );
*/
           //draw grid
           for ( int x = 0; x < CANVAS_WIDTH; x += GRID_SIZE )
           {
               g.setColor( Color.LIGHT_GRAY );
               g.drawLine( x, 0, x, CANVAS_HEIGHT );
           }
           for ( int y = 0; y < CANVAS_HEIGHT; y += GRID_SIZE )
           {
               g.setColor( Color.LIGHT_GRAY );
               g.drawLine( 0, y, CANVAS_WIDTH, y );
           }

           //perform physical repaint
           g2.dispose();
        }
    }
