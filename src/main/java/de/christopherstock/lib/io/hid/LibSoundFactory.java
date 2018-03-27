
    package de.christopherstock.lib.io.hid;

    import  java.awt.geom.*;
    import  java.io.*;
    import  javax.sound.sampled.*;
    import  de.christopherstock.lib.*;

    /*******************************************************************************************************************
    *   A factory being initialized with one sound.
    *   It delivers one-shot sound-clips from this sound.
    *******************************************************************************************************************/
    public class LibSoundFactory
    {
        private                 LibDebug            debug                   = null;
        public                  byte[]              bytes                   = null;
        public                  DataLine.Info       info                    = null;
        public                  AudioFormat         audioFormat             = null;

        public LibSoundFactory( InputStream inputStream, LibDebug debug ) throws Throwable
        {
            this.debug = debug;

            try
            {
                if ( inputStream == null ) throw new FileNotFoundException( "sound is null!" );

                AudioInputStream    audioInputStream    = AudioSystem.getAudioInputStream( inputStream );
                this.audioFormat = audioInputStream.getFormat();
                int                 size                = (int) (this.audioFormat.getFrameSize() * audioInputStream.getFrameLength() );

                this.info = new DataLine.Info( Clip.class, this.audioFormat, size );
                this.bytes = new byte[ size ];

                //noinspection ResultOfMethodCallIgnored
                audioInputStream.read(this.bytes, 0, size );
                audioInputStream.close();
            }
            catch ( Throwable t )
            {
                this.debug.trace( t );
                throw t;
            }
        }

        public final LibSoundClip getInstancedClip( float volume, float balance, int delay, Point2D.Float aDistantLocation )
        {
            LibSoundClip sound = new LibSoundClip(this.debug, this, volume, balance, delay, aDistantLocation );
            //if ( aDistantLocation != null ) sound.updateDistancedSound();
            return sound;
        }
    }
