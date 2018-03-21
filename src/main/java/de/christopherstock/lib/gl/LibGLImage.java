
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.image.*;
    import  java.nio.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.ui.*;

    public class LibGLImage
    {
        public static enum SrcPixelFormat
        {
            ERGB,
            ERGBA,
            ;
        }

        public static enum ImageUsage
        {
            EOrtho,
            ETexture,
            ;
        }

        public              int                 width                       = 0;
        public              int                 height                      = 0;
        public              ByteBuffer          bytes                       = null;
        public              SrcPixelFormat      srcPixelFormat              = null;

        public LibGLImage( BufferedImage aBufferedImage, ImageUsage imageUsage, LibDebug debug, boolean flipAllBytes )
        {
            BufferedImage bufferedImage = aBufferedImage;

            width   = bufferedImage.getWidth();
            height  = bufferedImage.getHeight();

            //detect pixel format
            if ( bufferedImage.getColorModel().hasAlpha() )
            {
                debug.out( "format is RGB-A" );
                srcPixelFormat = SrcPixelFormat.ERGBA;
            }
            else
            {
                debug.out( "format is RGB" );
                srcPixelFormat = SrcPixelFormat.ERGB;
            }

            //flip buffered image horizontally
            if ( flipAllBytes )
            {
                bufferedImage = LibImage.flipHorz( bufferedImage );
            }
            else
            {
                bufferedImage = LibImage.flipVert( bufferedImage );
            }

            //read buffer according to usage
            switch ( imageUsage )
            {
                case EOrtho:
                {
                    bytes   = getByteBuffer( bufferedImage, debug, flipAllBytes );
                    break;
                }

                case ETexture:
                {
                    bytes   = getByteBuffer( bufferedImage, debug, flipAllBytes );
                    break;
                }
            }
        }

        public static ByteBuffer getByteBuffer(BufferedImage aBufferedImage, LibDebug debug, boolean flipAllBytes )
        {
            byte[] bytes = LibImage.getBytesFromImg( aBufferedImage, debug );

            //flip all bytes
            if ( flipAllBytes ) bytes = LibImage.flipBytesTest( bytes );

            ByteBuffer ret = ByteBuffer.allocateDirect( bytes.length );
            //ret.order( ByteOrder.nativeOrder() );
            ret.put( bytes );
            ret.flip();

            return ret;
        }

        public static LibGLImage[] convertAll(BufferedImage[] bufferedImages, ImageUsage imageUsage, LibDebug debug )
        {
            LibGLImage[] ret = new LibGLImage[ bufferedImages.length ];

            for ( int i = 0; i < bufferedImages.length; ++i )
            {
                ret[ i ] = new LibGLImage( bufferedImages[ i ], imageUsage, debug, true );
            }

            return ret;
        }

        public static LibGLImage getFromString(String stringToDisplay, Font font, Color colFg, Color colShadow, Color colOutline, LibDebug debug )
        {
            Graphics2D      g           = LibGL3D.panel.getGraphics();

            int             imgWidth    = LibStrings.getStringWidth(  g, stringToDisplay, font );
            int             imgHeight   = LibStrings.getStringHeight( g, font );

            //secure minimum width
            if ( imgWidth  < 1 ) imgWidth  = 1;

            //create dynamic buffered image and draw context
            BufferedImage   template2 = new BufferedImage( imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB );
            Graphics2D      g2 = (Graphics2D)template2.getGraphics();

            //fill bg if desired
            //if ( colBg != null ) LibDrawing.fillRect( g2, 0, 0, imgWidth, imgHeight, colBg );

            //draw outlined string
            LibStrings.drawString( g2, colFg, colShadow, colOutline, font, LibAnchor.EAnchorLeftTop, stringToDisplay, 0, 0 );

            //convert to and return as GLImage
            return new LibGLImage( template2, ImageUsage.EOrtho, debug, false );
        }

        public static LibGLImage getFullOpaque(Color col, int width, int height, LibDebug debug )
        {
            //create dynamic buffered image and draw context
            BufferedImage   template2 = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            Graphics2D      g2 = (Graphics2D)template2.getGraphics();

            //draw outlined string
            g2.setColor( col );
            g2.fillRect( 0, 0, width, height );

            //convert to and return as GLImage
            return new LibGLImage( template2, ImageUsage.EOrtho, debug, false );
        }
    }
