
    package de.christopherstock.shooter.io.sound;

    import  java.io.ByteArrayInputStream;
    import  javax.media.*;
    import  javax.media.protocol.FileTypeDescriptor;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.io.datasource.LibIODataSource;
    import  de.christopherstock.shooter.*;

    /*******************************************************************************************************************
    *   The background sound engine. Uses the JavaMediaFramework player that is able
    *   to playback mp3 files with nanosecond precision.
    *******************************************************************************************************************/
    public enum SoundBg
    {
/*
        EExtraction(    131000000000L,       134649000000L,     4323000000L,    134649000000L         ),
        EPelagic2(      131000000000L,       134649000000L,     4323000000L,    134649000000L         ),
*/
        EExtraction(     0L,                  134649000000L,     4323000000L,    134649000000L         ),
        EInvestigationX( 0L,                  134649000000L,     4323000000L,    134649000000L         ),
/*
        EPelagic2(      0L,                  134649000000L,     4323000000L,    134649000000L         ),
*/
        ;

        private         static          SoundBg                 currentSound            = null;

        private Player                  iPlayer                 = null;
        private ControllerListener      iControllerListener     = null;

        private long                    iInitNanoSecondStart    = 0;
        private long                    iInitNanoSecondEnd      = 0;
        private long                    iLoopNanoSecondStart    = 0;
        private long                    iLoopNanoSecondEnd      = 0;

        private SoundBg( long aInitNanoSecondStart, long aInitNanoSecondEnd, long aLoopNanoSecondStart, long aLoopNanoSecondEnd )
        {
            this.iInitNanoSecondStart = aInitNanoSecondStart;
            this.iInitNanoSecondEnd = aInitNanoSecondEnd;
            this.iLoopNanoSecondStart = aLoopNanoSecondStart;
            this.iLoopNanoSecondEnd = aLoopNanoSecondEnd;
        }

        public void createPlayer()
        {
            try
            {
                ByteArrayInputStream bais  = LibIO.preStreamJarResource( ShooterSetting.Path.ESoundsBg.url + this.toString() + LibExtension.au.getSpecifier() );
                byte[]               bytes = LibIO.readStreamBuffered( bais );
                LibIODataSource      ds    = new LibIODataSource( LibIO.createByteBufferFromByteArray( bytes ), FileTypeDescriptor.BASIC_AUDIO );

                this.iPlayer = Manager.createPlayer( ds );
            }
            catch ( Throwable t )
            {
                ShooterDebug.sound.trace( t );
            }
        }

        public static void startSound(SoundBg sound )
        {
            //stop current sound
            stopCurrentSound();

            //assign new sound as current
            currentSound = sound;

            //start new sound threaded
            currentSound.start();
        }

        public static void stopCurrentSound()
        {
            //stop current sound
            if ( currentSound != null && currentSound.iPlayer != null )
            {
                currentSound.iPlayer.removeControllerListener( currentSound.iControllerListener );
                currentSound.iPlayer.stop();
            }
        }

        private void start()
        {
            if (this.iPlayer != null )
            {
                //create and add the controller listener
                final String soundName = this.toString();
                this.iControllerListener = new ControllerListener()
                {
                    public void controllerUpdate( ControllerEvent ce )
                    {
                        ShooterDebug.sound.out( "Sound [" + soundName + "] ControllerUpdate [" + ce + "]" );

                        //being invoked when the sound is realized
                        if ( ce instanceof RealizeCompleteEvent )
                        {
                            //set initial start and end time and play
                            SoundBg.this.iPlayer.setMediaTime( new Time(SoundBg.this.iInitNanoSecondStart) );
                            SoundBg.this.iPlayer.setStopTime(  new Time(SoundBg.this.iInitNanoSecondEnd) );
                            ShooterDebug.sound.out( "Play init from [" + SoundBg.this.iInitNanoSecondStart + "] to [" + SoundBg.this.iInitNanoSecondEnd + "]" );
                            SoundBg.this.iPlayer.start();
                        }

                        //being invoked when the sound is stopped
                        if ( ce instanceof StopAtTimeEvent )
                        {
                            //set initial start and end time and play
                            SoundBg.this.iPlayer.setMediaTime( new Time(SoundBg.this.iLoopNanoSecondStart) );
                            SoundBg.this.iPlayer.setStopTime(  new Time(SoundBg.this.iLoopNanoSecondEnd) );
                            ShooterDebug.sound.out( "Play loop from [" + SoundBg.this.iLoopNanoSecondStart + "] to [" + SoundBg.this.iLoopNanoSecondEnd + "]" );
                            SoundBg.this.iPlayer.start();
                        }
                    }
                };
                this.iPlayer.addControllerListener(this.iControllerListener);

                //realize the player
                this.iPlayer.realize();
            }
        }
    }
