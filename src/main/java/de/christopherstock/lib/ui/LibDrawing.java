
    package de.christopherstock.lib.ui;

    import  java.awt.*;

    /*******************************************************************************************************************
    *   Offers independent drawing operations.
    *******************************************************************************************************************/
    public class LibDrawing
    {
        /***************************************************************************************************************
        *   All nine available anchors for drawables.
        ***************************************************************************************************************/
        public static enum LibAnchor
        {
            ELeftTop,
            ELeftMiddle,
            ELeftBottom,
            ECenterTop,
            ECenterMiddle,
            ECenterBottom,
            ERightTop,
            ERightMiddle,
            ERightBottom,
            ;
        }

        public static enum TriangleDirection
        {
            ELeft,
            ERight,
            ;
        }

        /***************************************************************************************************************
        *   Paints a filled rect in the specified color and dimension.
        *
        *   @param  gc          The Context to draw onto.
        *   @param  x           destination x to draw to.
        *   @param  y           destination y to draw to.
        *   @param  width       destination rect's width.
        *   @param  height      destination rect's height.
        *   @param  color       The color to fill the rect with.
        ***************************************************************************************************************/
        public static void fillRect( Graphics2D gc, int x, int y, int width, int height, Color color )
        {
            gc.setPaint( color );
            gc.fillRect( x, y, x + width, y + height );
        }
    }
