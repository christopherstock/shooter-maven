
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
        private LibDebug        iDebug          = null;
        public                  byte[]          iBytes          = null;
        public                  DataLine.Info   iInfo           = null;
        public                  AudioFormat     iAudioFormat    = null;

        public LibSoundFactory( InputStream aInputStream, LibDebug aDebug ) throws Throwable
        {
            this.iDebug = aDebug;

            try
            {
                if ( aInputStream == null ) throw new FileNotFoundException( "sound is null!" );

                AudioInputStream    audioInputStream    = AudioSystem.getAudioInputStream( aInputStream );
                this.iAudioFormat = audioInputStream.getFormat();
                int                 size                = (int) (this.iAudioFormat.getFrameSize() * audioInputStream.getFrameLength() );

                this.iInfo = new DataLine.Info( Clip.class, this.iAudioFormat, size );
                this.iBytes = new byte[ size ];

                //noinspection ResultOfMethodCallIgnored
                audioInputStream.read(this.iBytes, 0, size );
                audioInputStream.close();
            }
            catch ( Throwable t )
            {
                this.iDebug.trace( t );
                throw t;
            }
        }

        public final LibSoundClip getInstancedClip( float volume, float balance, int delay, Point2D.Float aDistantLocation )
        {
            LibSoundClip sound = new LibSoundClip(this.iDebug, this, volume, balance, delay, aDistantLocation );
            //if ( aDistantLocation != null ) sound.updateDistancedSound();
            return sound;
        }
    }
