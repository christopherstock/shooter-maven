
    package de.christopherstock.lib.io.datasource;

    import  java.io.IOException;
    import  java.nio.ByteBuffer;
    import  java.nio.BufferUnderflowException;
    import  javax.media.protocol.PullSourceStream;
    import  javax.media.protocol.Seekable;
    import  javax.media.protocol.ContentDescriptor;

    /*******************************************************************************************************************
    *   A seekable IO stream for the JFM.
    *******************************************************************************************************************/
    public class LibIOSeekableStream implements PullSourceStream, Seekable
    {
        private                 ByteBuffer              inputBuffer;

        /***************************************************************************************************************
        *   Creates a new instance of SeekableStream
        ***************************************************************************************************************/
        public LibIOSeekableStream( ByteBuffer inputBuffer )
        {
            this.inputBuffer = inputBuffer;
            this.seek( 0 ); // set the ByteBuffer to to beginning
        }

        /***************************************************************************************************************
        *   Find out if the end of the stream has been reached.
        *
        *   @return Returns <CODE>true</CODE> if there is no more data.
        ***************************************************************************************************************/
        public boolean endOfStream()
        {
            return ( !this.inputBuffer.hasRemaining() );
        }

        /***************************************************************************************************************
        *   Get the current content type for this stream.
        *
        *   @return The current <CODE>ContentDescriptor</CODE> for this stream.
        ***************************************************************************************************************/
        public ContentDescriptor getContentDescriptor()
        {
            return null;
        }

        /***************************************************************************************************************
        *   Get the size, in bytes, of the content on this stream.
        *
        *   @return The content length in bytes.
        ***************************************************************************************************************/
        public long getContentLength()
        {
            return this.inputBuffer.capacity();
        }

        /***************************************************************************************************************
        *   Obtain the object that implements the specified
        *   <code>Class</code> or <code>Interface</code>
        *   The full class or interface name must be used.
        *   <p>
        *
        *   The control is not supported.
        *   <code>null</code> is returned.
        *
        *   @return <code>null</code>.
        ***************************************************************************************************************/
        public Object getControl( String controlType )
        {
            return null;
        }

        /***************************************************************************************************************
        *   Obtain the collection of objects that
        *   control the object that implements this interface.
        *   <p>
        *
        *   No controls are supported.
        *   A zero length array is returned.
        *
        *   @return A zero length array
        ***************************************************************************************************************/
        public Object[] getControls()
        {
            Object[] objects = new Object[ 0 ];
            return objects;
        }

        /***************************************************************************************************************
        *   Find out if this media object can position anywhere in the
        *   stream. If the stream is not random access, it can only be repositioned
        *   to the beginning.
        *
        *   @return Returns <CODE>true</CODE> if the stream is random access, <CODE>false</CODE> if the stream can only
        *   be reset to the beginning.
        ***************************************************************************************************************/
        public boolean isRandomAccess()
        {
            return true;
        }

        /***************************************************************************************************************
        *   Block and read data from the stream.
        *   <p>
        *   Reads up to <CODE>length</CODE> bytes from the input stream into
        *   an array of bytes.
        *   If the first argument is <code>null</code>, up to
        *   <CODE>length</CODE> bytes are read and discarded.
        *   Returns -1 when the end
        *   of the media is reached.
        *
        *   This method  only returns 0 if it was called with
        *   a <CODE>length</CODE> of 0.
        *
        *   @param  buffer The buffer to read bytes into.
        *   @param  offset The offset into the buffer at which to begin writing data.
        *   @param  length The number of bytes to read.
        *   @return The number of bytes read, -1 indicating the end of stream, or 0 indicating <CODE>read</CODE>
        *           was called with <CODE>length</CODE> 0.
        *
        *   @throws IOException Thrown if an error occurs while reading.
        ***************************************************************************************************************/
        public int read(byte[] buffer, int offset, int length) throws IOException
        {
            // return n (number of bytes read), -1 (eof), 0 (asked for zero bytes)

            if ( length == 0 )
                return 0;
            try
            {
                this.inputBuffer.get(buffer,offset,length);
                return length;
            }
            catch ( BufferUnderflowException E )
            {
                return -1;
            }
        }

        public void close()
        {
        }

        /***************************************************************************************************************
        *   Seek to the specified point in the stream.
        *
        *   @param  where The position to seek to.
        *   @return The new stream position.
        ***************************************************************************************************************/
        public long seek( long where )
        {
            try
            {
                this.inputBuffer.position((int)(where));
                return where;
            }
            catch (IllegalArgumentException E)
            {
                return this.tell(); // staying at the current position
            }
        }

        /***************************************************************************************************************
        *   Obtain the current point in the stream.
        ***************************************************************************************************************/
        public long tell()
        {
            return this.inputBuffer.position();
        }

        /***************************************************************************************************************
        *   Find out if data is available now.
        *   Returns <CODE>true</CODE> if a call to <CODE>read</CODE> would block for data.
        *
        *   @return Returns <CODE>true</CODE> if read would block; otherwise returns <CODE>false</CODE>.
        ***************************************************************************************************************/
        public boolean willReadBlock()
        {
           return (this.inputBuffer.remaining() == 0 );
        }
    }
