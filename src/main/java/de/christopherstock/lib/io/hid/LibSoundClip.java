
    package de.christopherstock.lib.io.hid;

    import  java.awt.geom.*;
    import  javax.sound.sampled.*;
    import  javax.sound.sampled.LineEvent.*;
    import  de.christopherstock.lib.*;

    /*******************************************************************************************************************
    *   A one shot threaded sound clip.
    *******************************************************************************************************************/
    public class LibSoundClip extends Thread implements LineListener
    {
        public      static  final       float               VOLUME_MAX              = 1.0f;
        private static  final       float               VOLUME_MUTE             = 0.0f;
        private static  final       float               BALANCE_ONLY_LEFT       = -1.0f;
        private static  final       float               BALANCE_ONLY_RIGHT      = 1.0f;
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
            this.iDebug = aDebug;
            this.iFactory = aFactory;

            this.iVolume = aVolume;
            this.iBalance = aBalance;
            this.iDelay = aDelay;

            this.iDistantLocation = aDistantLocation;

            this.clipVolume();
            this.clipBalance();
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
                this.iClip = (Clip)AudioSystem.getLine(this.iFactory.iInfo );

                try
                {
                    this.iClip.open(this.iFactory.iAudioFormat, this.iFactory.iBytes, 0, this.iFactory.iBytes.length );

                    //debug.out( "Initialized sound clip! Memory info is:" );
                    //debug.mem();
                }
                catch ( OutOfMemoryError oome )
                {
                    this.iDebug.err( "Caught OutOfMemoryError on initing sound clip" );
/*
                    //disable sound system
                    disabledByMemory    = true;
                    iSoundHasFinished   = true;
*/
                    return;
                }

                //volume control
                this.iVolumeControl = (FloatControl) this.iClip.getControl( FloatControl.Type.MASTER_GAIN );
                this.setVolume(this.iVolume);

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
                    debug.err( "Unsupported control type 'balance' for this wav" );
                }
*/
                //add listener
                this.iClip.addLineListener( this );

                //start clip from the beginning
                this.iClip.setMicrosecondPosition( 0 );
                this.iClip.start();
            }
            catch ( Throwable t )
            {
                this.iDebug.err( "Exception on playing java player\n" + t );
                this.iSoundHasFinished = true;
            }
        }

        private void setVolume(float aVolume)
        {
            this.iVolume = aVolume;
            this.clipVolume();

            if (this.iVolumeControl != null )
            {
              //float dB = (float)( Math.log( iVolume ) / Math.log( 10.0f ) * 20.0f );
                float dB = this.iVolumeControl.getMinimum() + ( (this.iVolumeControl.getMaximum() - this.iVolumeControl.getMinimum() ) * aVolume );

                //ShooterDebug.bugfix.out( "setVolume [" + iVolume + "] db [" + dB + "]  min [" + iVolumeControl.getMinimum() + "] max [" + iVolumeControl.getMaximum() + "]" );

                this.iVolumeControl.setValue( dB );
            }
        }

        public void setBalance( float aBalance )
        {
            this.iBalance = aBalance;
            this.clipBalance();

            if (this.iBalanceControl != null )
            {
                this.iBalanceControl.setValue(this.iBalance);
            }
        }

        private void clipVolume()
        {
            if (this.iVolume < VOLUME_MUTE ) this.iVolume = VOLUME_MUTE;
            if (this.iVolume > VOLUME_MAX  ) this.iVolume = VOLUME_MAX;
        }

        private void clipBalance()
        {
            if (this.iBalance < BALANCE_ONLY_LEFT  ) this.iBalance = BALANCE_ONLY_LEFT;
            if (this.iBalance > BALANCE_ONLY_RIGHT ) this.iBalance = BALANCE_ONLY_RIGHT;
        }

        public boolean hasSoundFinished()
        {
            return this.iSoundHasFinished;
        }

        public void update( LineEvent le )
        {
            LineEvent.Type t = le.getType();

            if ( t == Type.STOP || t == Type.CLOSE )
            {
                //log
                //debug.out( "Sound [" + this + "] finished!" );

                //release the line ( prevents OutOfMemoryErrors ! )
                le.getLine().close();

                //mark finished
                this.iSoundHasFinished = true;
            }
        }

        public boolean isDistanced()
        {
            return (this.iDistantLocation != null );
        }

        public void updateDistancedSound( float newVolume )
        {
            //clip volume
            if ( newVolume < LibSoundClip.VOLUME_MUTE ) newVolume = LibSoundClip.VOLUME_MUTE;
            if ( newVolume > LibSoundClip.VOLUME_MAX  ) newVolume = LibSoundClip.VOLUME_MAX;

            this.setVolume( newVolume );
            //debug.out( "update volume to:  volume [" + volume + "]"  );
        }

        public final Point2D.Float getDistantLocation()
        {
            return this.iDistantLocation;
        }
    }
