
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
        EExtraction(     131000000000L,       134649000000L,     4323000000L,     134649000000L         ),
        EPelagic2(       131000000000L,       134649000000L,     4323000000L,     134649000000L         ),
*/
        EExtraction(     0L,                  138820000000L,     0L,              138820000000L         ),
        EInvestigationX( 0L,                  134600000000L,     0L,              134600000000L         ),
/*
        EPelagic2(       0L,                  134649000000L,     4323000000L,     134649000000L         ),
*/
        ;

        protected           Player                  player                      = null;
        protected           ControllerListener      controllerListener          = null;

        public              long                    initNanoSecondStart         = 0;
        private             long                    initNanoSecondEnd           = 0;
        private             long                    loopNanoSecondStart         = 0;
        private             long                    loopNanoSecondEnd           = 0;

        private SoundBg( long initNanoSecondStart, long initNanoSecondEnd, long loopNanoSecondStart, long loopNanoSecondEnd )
        {
            this.initNanoSecondStart = initNanoSecondStart;
            this.initNanoSecondEnd   = initNanoSecondEnd;
            this.loopNanoSecondStart = loopNanoSecondStart;
            this.loopNanoSecondEnd   = loopNanoSecondEnd;
        }

        public void createPlayer()
        {
            try
            {
                ByteArrayInputStream bais  = LibIO.preStreamJarResource( ShooterSetting.Path.ESoundsBg.url + this.toString() + LibExtension.au.getSpecifier() );
                byte[]               bytes = LibIO.readStreamBuffered( bais );
                LibIODataSource      ds    = new LibIODataSource( LibIO.createByteBufferFromByteArray( bytes ), FileTypeDescriptor.BASIC_AUDIO );

                this.player = Manager.createPlayer( ds );
            }
            catch ( Throwable t )
            {
                ShooterDebug.sound.trace( t );
            }
        }

        public void start()
        {
            if (this.player != null )
            {
                //create and add the controller listener
                final String soundName = this.toString();
                this.controllerListener = new ControllerListener()
                {
                    public void controllerUpdate( ControllerEvent ce )
                    {
                        ShooterDebug.sound.out( "Sound [" + soundName + "] ControllerUpdate [" + ce + "]" );

                        //being invoked when the sound is realized
                        if ( ce instanceof RealizeCompleteEvent )
                        {
                            //set initial start and end time and play
                            SoundBg.this.player.setMediaTime( new Time(SoundBg.this.initNanoSecondStart) );
                            SoundBg.this.player.setStopTime(  new Time(SoundBg.this.initNanoSecondEnd) );
                            ShooterDebug.sound.out( "Play init from [" + SoundBg.this.initNanoSecondStart + "] to [" + SoundBg.this.initNanoSecondEnd + "]" );
                            SoundBg.this.player.start();
                        }

                        //being invoked when the sound is stopped
                        if ( ce instanceof StopAtTimeEvent )
                        {
                            //set initial start and end time and play
                            SoundBg.this.player.setMediaTime( new Time(SoundBg.this.loopNanoSecondStart) );
                            SoundBg.this.player.setStopTime(  new Time(SoundBg.this.loopNanoSecondEnd) );
                            ShooterDebug.sound.out( "Play loop from [" + SoundBg.this.loopNanoSecondStart + "] to [" + SoundBg.this.loopNanoSecondEnd + "]" );
                            SoundBg.this.player.start();
                        }
                    }
                };
                this.player.addControllerListener(this.controllerListener);

                //realize the player
                this.player.realize();
            }
        }
    }
