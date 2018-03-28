
    package de.christopherstock.lib.gl;

    import  java.awt.*;
    import  java.awt.image.*;
    import  java.nio.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.ui.*;
    import de.christopherstock.shooter.Shooter;

    public class LibGLTextureImage
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

        public LibGLTextureImage(BufferedImage bufferedImage, ImageUsage imageUsage, LibDebug debug, boolean flipAllBytes )
        {
            this.width = bufferedImage.getWidth();
            this.height = bufferedImage.getHeight();

            //detect pixel format
            if ( bufferedImage.getColorModel().hasAlpha() )
            {
                debug.out( "format is RGB-A" );
                this.srcPixelFormat = SrcPixelFormat.ERGBA;
            }
            else
            {
                debug.out( "format is RGB" );
                this.srcPixelFormat = SrcPixelFormat.ERGB;
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
                    this.bytes = getByteBuffer( bufferedImage, debug, flipAllBytes );
                    break;
                }

                case ETexture:
                {
                    this.bytes = getByteBuffer( bufferedImage, debug, flipAllBytes );
                    break;
                }
            }
        }

        private static ByteBuffer getByteBuffer( BufferedImage aBufferedImage, LibDebug debug, boolean flipAllBytes )
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

        public static LibGLTextureImage[] convertAll(BufferedImage[] bufferedImages, ImageUsage imageUsage, LibDebug debug )
        {
            LibGLTextureImage[] ret = new LibGLTextureImage[ bufferedImages.length ];

            for ( int i = 0; i < bufferedImages.length; ++i )
            {
                ret[ i ] = new LibGLTextureImage( bufferedImages[ i ], imageUsage, debug, true );
            }

            return ret;
        }

        public static LibGLTextureImage getFromString(String stringToDisplay, Font font, Color colFg, Color colShadow, Color colOutline, LibDebug debug )
        {
            Graphics2D g         = Shooter.game.engine.frame.getGraphics();

            int        imgWidth  = LibStrings.getStringWidth(  g, stringToDisplay, font );
            int        imgHeight = LibStrings.getStringHeight( g, font );

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
            return new LibGLTextureImage( template2, ImageUsage.EOrtho, debug, false );
        }

        public static LibGLTextureImage getFullOpaque(Color col, int width, int height, LibDebug debug )
        {
            //create dynamic buffered image and draw context
            BufferedImage   template2 = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            Graphics2D      g2 = (Graphics2D)template2.getGraphics();

            //draw outlined string
            g2.setColor( col );
            g2.fillRect( 0, 0, width, height );

            //convert to and return as GLImage
            return new LibGLTextureImage( template2, ImageUsage.EOrtho, debug, false );
        }
    }
