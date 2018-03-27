
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
        private     static  final       float               VOLUME_MUTE             = 0.0f;
        private     static  final       float               BALANCE_ONLY_LEFT       = -1.0f;
        private     static  final       float               BALANCE_ONLY_RIGHT      = 1.0f;
        public      static  final       float               BALANCE_BOTH            = 0.0f;

        private                         Clip                clip                    = null;
        private                         LibDebug            debug                   = null;
        private                         LibSoundFactory     factory                 = null;
        private                         FloatControl        volumeControl           = null;
        private                         FloatControl        balanceControl          = null;

        private                         float               volume                  = 0.0f;
        private                         float               balance                 = 0.0f;
        public                          int                 delay                   = 0;

        private                         boolean             soundHasFinished        = false;

        private                         Point2D.Float       distantLocation         = null;

        public LibSoundClip( LibDebug debug, LibSoundFactory factory, float volume, float balance, int delay, Point2D.Float distantLocation )
        {
            this.debug   = debug;
            this.factory = factory;

            this.volume  = volume;
            this.balance = balance;
            this.delay   = delay;

            this.distantLocation = distantLocation;

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
                soundHasFinished = true;
                return;
            }
*/
            try
            {
                //create clip
                this.clip = (Clip)AudioSystem.getLine(this.factory.info);

                try
                {
                    this.clip.open(this.factory.audioFormat, this.factory.bytes, 0, this.factory.bytes.length );

                    //debug.out( "Initialized sound clip! Memory info is:" );
                    //debug.mem();
                }
                catch ( OutOfMemoryError oome )
                {
                    this.debug.err( "Caught OutOfMemoryError on initing sound clip" );
/*
                    //disable sound system
                    disabledByMemory    = true;
                    soundHasFinished   = true;
*/
                    return;
                }

                //volume control
                this.volumeControl = (FloatControl) this.clip.getControl( FloatControl.Type.MASTER_GAIN );
                this.setVolume(this.volume);

                //ignore balance for now!
/*
                try
                {
                    //balance control, -1.0f to 1.0f, 0.0f is both speakers 50%
                    balanceControl = (FloatControl)clip.getControl( FloatControl.Type.BALANCE );
                    setBalance( balance );
                }
                catch ( Exception e )
                {
                    debug.err( "Unsupported control type 'balance' for this wav" );
                }
*/
                //add listener
                this.clip.addLineListener( this );

                //start clip from the beginning
                this.clip.setMicrosecondPosition( 0 );
                this.clip.start();
            }
            catch ( Throwable t )
            {
                this.debug.err( "Exception on playing java player\n" + t );
                this.soundHasFinished = true;
            }
        }

        private void setVolume(float aVolume)
        {
            this.volume = aVolume;
            this.clipVolume();

            if (this.volumeControl != null )
            {
              //float dB = (float)( Math.log( volume ) / Math.log( 10.0f ) * 20.0f );
                float dB = this.volumeControl.getMinimum() + ( (this.volumeControl.getMaximum() - this.volumeControl.getMinimum() ) * aVolume );

                //ShooterDebug.bugfix.out( "setVolume [" + volume + "] db [" + dB + "]  min [" + volumeControl.getMinimum() + "] max [" + volumeControl.getMaximum() + "]" );

                this.volumeControl.setValue( dB );
            }
        }

        public void setBalance( float aBalance )
        {
            this.balance = aBalance;
            this.clipBalance();

            if (this.balanceControl != null )
            {
                this.balanceControl.setValue(this.balance);
            }
        }

        private void clipVolume()
        {
            if (this.volume < VOLUME_MUTE ) this.volume = VOLUME_MUTE;
            if (this.volume > VOLUME_MAX  ) this.volume = VOLUME_MAX;
        }

        private void clipBalance()
        {
            if (this.balance < BALANCE_ONLY_LEFT  ) this.balance = BALANCE_ONLY_LEFT;
            if (this.balance > BALANCE_ONLY_RIGHT ) this.balance = BALANCE_ONLY_RIGHT;
        }

        public boolean hasSoundFinished()
        {
            return this.soundHasFinished;
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
                this.soundHasFinished = true;
            }
        }

        public boolean isDistanced()
        {
            return (this.distantLocation != null );
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
            return this.distantLocation;
        }
    }
