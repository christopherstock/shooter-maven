
    package de.christopherstock.lib.ui;

    import  java.awt.*;
    import  java.awt.geom.*;
    import  java.awt.image.*;
    import  java.io.*;
    import  java.util.*;
    import  javax.imageio.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   The Image-Loader.
    *******************************************************************************************************************/
    @SuppressWarnings("PointlessArithmeticExpression")
    public class LibImage
    {
        public static byte[] getBytesFromImg( BufferedImage bufferedImage, LibDebug debug )
        {
            byte[]      imgBytes    = null;
            DataBuffer  db          = bufferedImage.getRaster().getDataBuffer();

            debug.out( "w: " + bufferedImage.getWidth() );
            debug.out( "h: " + bufferedImage.getHeight() );
            //Debug.bugfix.out( "b: " + aBufferedImage.bytes.capacity() );

            if ( db instanceof DataBufferInt )
            {
                int[] ints = ((DataBufferInt)db ).getData();
                debug.out( "number of ints is: ["+ints.length+"]" );

                imgBytes = LibMath.intArrayToByteArray( ints );
                debug.out( "number of bytes is: ["+imgBytes.length+"]" );
            }
            else if ( db instanceof DataBufferByte )
            {
                imgBytes = ((DataBufferByte)db ).getData();
                debug.out( "number of bytes is: ["+imgBytes.length+"]" );
            }
            else
            {
                debug.out( "Unsupported data buffer type on loading ortho image" );
                throw new NullPointerException();
            }

            return imgBytes;
        }

        public static BufferedImage getFullOpaque( Color col, int width, int height )
        {
            //create dynamic buffered image and draw context
            BufferedImage   template2 = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            Graphics2D      g2 = (Graphics2D)template2.getGraphics();

            //draw outlined string
            g2.setColor( col );
            g2.fillRect( 0, 0, width, height / 2 );
            g2.fillRect( 0, height / 2, width / 2, height / 2 );

            //convert to and return as GLImage
            return template2;
        }

        public static BufferedImage load( String url, LibDebug debug, boolean switchRGBAtoARGB )
        {
            BufferedImage ret = null;

            try
            {
                //load the buffered image
                BufferedImage buf = ImageIO.read( LibIO.preStreamJarResource( url ) );
/*
                    final boolean supportAlphaChannel = false;
                    if ( supportAlphaChannel )
                    {
                        //unused - used for 2d drawing in order to draw with a different alpha value
                        BufferedImage   bufA    = new BufferedImage( buf.getWidth(), buf.getHeight(), BufferedImage.TYPE_INT_ARGB );
                        Graphics        g       = bufA.getGraphics();
                        g.drawImage( buf, 0, 0, null );

                        //assign alpha-buffered-image
                        glImages[ i ] = new LibGLTextureImage( bufA, usage, debug );
                    }
                    else
*/
                //direct assignment without alpha
                ret = buf;

                //manipulate bytes if desired
                if ( switchRGBAtoARGB )
                {
                    ret = changeRGBtoBGR( ret );
                }
            }
            catch( Exception e )
            {
                debug.err( "ERROR! Couldn't read image [" + url + "]" );
                debug.trace( e );
                System.exit( 0 );
            }

            return ret;
        }

        public static BufferedImage flipVert( BufferedImage bi )
        {
            AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance( 1, -1 );
            tx.translate( 0, -bi.getHeight( null ) );
            return new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR ).filter( bi, null );
        }

        public static BufferedImage flipHorz( BufferedImage bi )
        {
            AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance( -1, 1 );
            tx.translate( -bi.getWidth( null ), 0 );
            return new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR ).filter( bi, null );
        }

        public static byte[] insertAlphaByte( byte[] rgbBytes )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            for ( int i = 0; i < rgbBytes.length; i += 3 )
            {
                //set opaque alpha value
                baos.write( rgbBytes[ i + 0 ] );
                baos.write( rgbBytes[ i + 1 ] );
                baos.write( rgbBytes[ i + 2 ] );

                baos.write( 0xff );
            }

            //Debug.bugfix.out( "fixed to [" + baos.size() + "] bytes" );

            return baos.toByteArray();
        }

        /***************************************************************************************************************
        *   Flips all bytes in the stream.
        ***************************************************************************************************************/
        public static byte[] flipBytesTest( byte[] original )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream( original.length );

            for ( int i = original.length - 1; i >= 0; --i )
            {
                baos.write( original[ i ] );
            }

            return baos.toByteArray();
        }

        private static BufferedImage changeRGBtoBGR( BufferedImage src )
        {
            BufferedImage ret = src;

            for ( int x = 0; x < ret.getWidth(); ++x )
            {
                for ( int y = 0; y < ret.getHeight(); ++y )
                {
                    Color col = new Color( ret.getRGB( x, y ) );
                    col = new Color( col.getBlue(), col.getGreen(), col.getRed() );
                    ret.setRGB( x, y, col.getRGB() );
                }
            }

            return ret;
        }

        public static BufferedImage resizeImage( BufferedImage in, int newWidth, int newHeight )
        {
            BufferedImage scaledImage = new BufferedImage( newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = scaledImage.createGraphics();
            AffineTransform xform = AffineTransform.getScaleInstance( (float)newWidth / (float)in.getWidth(), (float)newHeight / (float)in.getHeight() );
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.drawImage( in, xform, null );
            graphics2D.dispose();

            return scaledImage;
        }

        /***************************************************************************************************************
        *   Flips all columns of this BufferedImage
        *   so its byte-array is returned horizontal mirrored.
        *
        *   @param  original    The bitmap's bytes - length must be a multiple of 4.
        ***************************************************************************************************************/
        @Deprecated
        public static byte[] flipBytesHorzARGB( byte[] original )
        {
            byte[] flipped = new byte[ original.length ];

            int newIndex = original.length - 4;

            for ( int i = 0; i < original.length; i += 4 )
            {
                flipped[ newIndex + 0 ] = original[ i + 0 ];
                flipped[ newIndex + 1 ] = original[ i + 1 ];
                flipped[ newIndex + 2 ] = original[ i + 2 ];
                flipped[ newIndex + 3 ] = original[ i + 3 ];

                newIndex -= 4;
            }

            return flipped;
        }

        /***************************************************************************************************************
        *   Flips all columns of this BufferedImage
        *   so its byte-array is returned horizontal mirrored.
        *
        *   @param  original    The bitmap's bytes - length must be a multiple of 4.
        *   @param  rowLength   The number of pixels (ARGB values) per row
        *   @deprecated         Replaced by {@link #flipVert(BufferedImage)}
        ***************************************************************************************************************/
        @Deprecated
        public static byte[] flipBytesVertARGB( byte[] original, int rowLength )
        {
            int                     bytesPerRow = rowLength * 4;
            ByteArrayOutputStream   baos        = new ByteArrayOutputStream();

            for ( int i = original.length - bytesPerRow; i >= 0; i -= bytesPerRow )
            {
                for ( int j = 0; j < bytesPerRow; ++j )
                {
                    baos.write( original[ i + j ] );
                }
            }

            return baos.toByteArray();
        }

        @Deprecated
        public static BufferedImage[] loadAllEnumNames( Vector<Object> enumConstants, String path, LibExtension extension, LibDebug debug, boolean switchRGBAtoARGB )
        {
            BufferedImage[] ret = new BufferedImage[ enumConstants.size() ];

            for ( int i = 0; i < enumConstants.size(); ++i )
            {
                String url = path + enumConstants.elementAt( i ).toString() + extension.getSpecifier();
                ret[ i ] = load( url, debug, switchRGBAtoARGB );
            }

            return ret;
        }
    }
