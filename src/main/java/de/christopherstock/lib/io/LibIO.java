
    package de.christopherstock.lib.io;

    import java.awt.*;
    import  java.io.*;
    import  java.nio.ByteBuffer;
    import  java.util.*;

    /*******************************************************************************************************************
    *   The GL-View.
    *******************************************************************************************************************/
    public abstract class LibIO
    {
        private     static      final       int             JAR_BUFFER_SIZE             = 0xffff;

        /***************************************************************************************************************
        *   Reads the given {@link InputStream} buffered and returns all read bytes as a byte-array.
        *
        *   @param  is      The InputStream to read buffered.
        *   @return         All bytes of the given InputStream as a byte-array.
        ***************************************************************************************************************/
        public static byte[] readStreamBuffered( InputStream is )
        {
            ByteArrayOutputStream   baos        = new ByteArrayOutputStream();
            int                     byteread    = 0;

            try
            {
                //read one byte after another until the EOF-flag is returned
                while ( ( byteread = is.read() ) != -1 )
                {
                    //write this byte, if it could be read, into the output-stream
                    baos.write( byteread );
                }

                is.close();
                baos.close();

                //return the output-stream as a byte-array
                return baos.toByteArray();
            }
            catch ( Exception e )
            {
                try
                {
                    is.close();
                    baos.close();
                }
                catch ( Exception u )
                {
                    //ignore exceptions
                }
                e.printStackTrace();
                return null;
            }
        }

        public static void saveObjects(String filename, Vector<Object> objectsToSave ) throws Throwable
        {
            FileOutputStream   fos = new FileOutputStream(   filename   );
            ObjectOutputStream oos = new ObjectOutputStream( fos        );

            for ( Object o : objectsToSave )
            {
                oos.writeObject( o );
            }

            oos.close();
        }

        public static Object[] loadObjects(String filename ) throws Throwable
        {
            Vector<Object>     ret = new Vector<Object>();

            FileInputStream    fis = new FileInputStream(    filename   );
            ObjectInputStream  ois = new ObjectInputStream(  fis        );

            try
            {
                while ( ois.available() > 0 )
                {
                    Object o = ois.readObject();
                    ret.add( o );
                }
            }
            finally
            {
                ois.close();
            }

            return ret.toArray( new Object[] {} );
        }

        public static ByteArrayInputStream preStreamJarResource(String url ) throws IOException
        {
            InputStream             in      = Thread.currentThread().getClass().getResourceAsStream( url );
            ByteArrayOutputStream   byteOut = new ByteArrayOutputStream();
            byte[]                  buffer  = new byte[ JAR_BUFFER_SIZE ];

            for ( int len; ( len = in.read( buffer ) ) != -1; )
            {
                byteOut.write( buffer, 0, len );
            }

            in.close();
            ByteArrayInputStream    byteIn = new ByteArrayInputStream( byteOut.toByteArray() );
            return byteIn;
        }

        public static ByteBuffer createByteBufferFromByteArray(byte[] bytes )
        {
            // Wrap a byte array into a buffer
            return ByteBuffer.wrap(bytes);
        }

        public static Font createFont(String filename, float size ) throws Throwable
        {
            return Font.createFont( Font.TRUETYPE_FONT, preStreamJarResource( filename ) ).deriveFont( size );
        }
    }
