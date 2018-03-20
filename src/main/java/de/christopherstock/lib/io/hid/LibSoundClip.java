
    package de.christopherstock.lib.io.hid;

    import  java.awt.geom.*;
    import  javax.sound.sampled.*;
    import  javax.sound.sampled.LineEvent.*;
    import  de.christopherstock.lib.*;

    /**************************************************************************************
    *   A one shot threaded sound clip.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class LibSoundClip extends Thread implements LineListener
    {
        public      static  final       float               VOLUME_MAX              = 1.0f;
        public      static  final       float               VOLUME_MUTE             = 0.0f;
        public      static  final       float               BALANCE_ONLY_LEFT       = -1.0f;
        public      static  final       float               BALANCE_ONLY_RIGHT      = 1.0f;
        public      static  final       float               BALANCE_BOTH            = 0.0f;

      //public      static              boolean             disabledByMemory        = false;

        private                         Clip                iClip                   = null;
        private                         LibDebug            iDebug                  = null;
        private                         LibSoundFactory     iFactory                = null;
        private                         FloatControl        iVolumeControl          = null;
        private                         FloatControl        iBalanceControl         = null;

        private                         float               iVolume                 = 0.0f;
        private                         float               iBalance                = 0.0f;
        public                          int                 iDelay                  = 0;

        private                         boolean             iSoundHasFinished       = false;

        private                         Point2D.Float       iDistantLocation        = null;

        public LibSoundClip( LibDebug aDebug, LibSoundFactory aFactory, float aVolume, float aBalance, int aDelay, Point2D.Float aDistantLocation )
        {
            iDebug   = aDebug;
            iFactory = aFactory;

            iVolume  = aVolume;
            iBalance = aBalance;
            iDelay   = aDelay;

            iDistantLocation = aDistantLocation;

            clipVolume();
            clipBalance();
        }

        @Override
        public void run()
        {
/*
            //break if the sound system has been disabled due to low memory
            if ( disabledByMemory )
            {
                iSoundHasFinished = true;
                return;
            }
*/
            try
            {
                //create clip
                iClip = (Clip)AudioSystem.getLine( iFactory.iInfo );

                try
                {
                    iClip.open( iFactory.iAudioFormat, iFactory.iBytes, 0, iFactory.iBytes.length );

                    //iDebug.out( "Initialized sound clip! Memory info is:" );
                    //iDebug.mem();
                }
                catch ( OutOfMemoryError oome )
                {
                    iDebug.err( "Caught OutOfMemoryError on initing sound clip" );
/*
                    //disable sound system
                    disabledByMemory    = true;
                    iSoundHasFinished   = true;
*/
                    return;
                }

                //volume control
                iVolumeControl = (FloatControl)iClip.getControl( FloatControl.Type.MASTER_GAIN );
                setVolume( iVolume );

                //ignore balance for now!
/*
                try
                {
                    //balance control, -1.0f to 1.0f, 0.0f is both speakers 50%
                    iBalanceControl = (FloatControl)iClip.getControl( FloatControl.Type.BALANCE );
                    setBalance( iBalance );
                }
                catch ( Exception e )
                {
                    iDebug.err( "Unsupported control type 'balance' for this wav" );
                }
*/
                //add listener
                iClip.addLineListener( this );

                //start clip from the beginning
                iClip.setMicrosecondPosition( 0 );
                iClip.start();
            }
            catch ( Throwable t )
            {
                iDebug.err( "Exception on playing java player\n" + t );
                iSoundHasFinished = true;
            }
        }

        public void setVolume( float aVolume )
        {
            iVolume = aVolume;
            clipVolume();

            if ( iVolumeControl != null )
            {
              //float dB = (float)( Math.log( iVolume ) / Math.log( 10.0f ) * 20.0f );
                float dB = iVolumeControl.getMinimum() + ( ( iVolumeControl.getMaximum() - iVolumeControl.getMinimum() ) * aVolume );

                //ShooterDebug.bugfix.out( "setVolume [" + iVolume + "] db [" + dB + "]  min [" + iVolumeControl.getMinimum() + "] max [" + iVolumeControl.getMaximum() + "]" );

                iVolumeControl.setValue( dB );
            }
        }

        public void setBalance( float aBalance )
        {
            iBalance = aBalance;
            clipBalance();

            if ( iBalanceControl != null )
            {
                iBalanceControl.setValue( iBalance );
            }
        }

        public void clipVolume()
        {
            if ( iVolume < VOLUME_MUTE ) iVolume = VOLUME_MUTE;
            if ( iVolume > VOLUME_MAX  ) iVolume = VOLUME_MAX;
        }

        public void clipBalance()
        {
            if ( iBalance < BALANCE_ONLY_LEFT  ) iBalance = BALANCE_ONLY_LEFT;
            if ( iBalance > BALANCE_ONLY_RIGHT ) iBalance = BALANCE_ONLY_RIGHT;
        }

        public boolean hasSoundFinished()
        {
            return iSoundHasFinished;
        }

        public void update( LineEvent le )
        {
            LineEvent.Type t = le.getType();

            if ( t == Type.STOP || t == Type.CLOSE )
            {
                //log
                //iDebug.out( "Sound [" + this + "] finished!" );

                //release the line ( prevents OutOfMemoryErrors ! )
                le.getLine().close();

                //mark finished
                iSoundHasFinished = true;
            }
        }

        public boolean isDistanced()
        {
            return ( iDistantLocation != null );
        }

        public void updateDistancedSound( float newVolume )
        {
            //clip volume
            if ( newVolume < LibSoundClip.VOLUME_MUTE ) newVolume = LibSoundClip.VOLUME_MUTE;
            if ( newVolume > LibSoundClip.VOLUME_MAX  ) newVolume = LibSoundClip.VOLUME_MAX;

            setVolume( newVolume );
            //iDebug.out( "update volume to:  volume [" + volume + "]"  );
        }

        public final Point2D.Float getDistantLocation()
        {
            return iDistantLocation;
        }
    }
