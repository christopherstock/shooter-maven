/*  $Id: ShooterSound.java 1275 2014-07-02 06:16:12Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.io.sound;

    import  java.io.ByteArrayInputStream;
    import  javax.media.*;
    import  javax.media.format.*;
    import  javax.media.protocol.FileTypeDescriptor;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.io.datasource.LibIODataSource;
    import  de.christopherstock.shooter.*;

    /**************************************************************************************
    *   The background sound engine. Uses the JavaMediaFramework player that is able
    *   to playback mp3 files with nanosecond precision.
    *
    *   @version    0.3.11
    *   @author     Christopher Stock
    **************************************************************************************/
    public enum SoundBg
    {
/*
        EExtraction(    131000000000L,       134649000000L,     4323000000L,    134649000000L         ),
        EPelagic2(      131000000000L,       134649000000L,     4323000000L,    134649000000L         ),
*/
        EExtraction(    0L,                  134649000000L,     4323000000L,    134649000000L         ),
        EPelagic2(      0L,                  134649000000L,     4323000000L,    134649000000L         ),

        ;

        private         static          SoundBg                 currentSound            = null;

        protected                       Player                  iPlayer                 = null;
        protected                       ControllerListener      iControllerListener     = null;

        protected                       long                    iInitNanoSecondStart    = 0;
        protected                       long                    iInitNanoSecondEnd      = 0;
        protected                       long                    iLoopNanoSecondStart    = 0;
        protected                       long                    iLoopNanoSecondEnd      = 0;

        private SoundBg( long aInitNanoSecondStart, long aInitNanoSecondEnd, long aLoopNanoSecondStart, long aLoopNanoSecondEnd )
        {
            iInitNanoSecondStart = aInitNanoSecondStart;
            iInitNanoSecondEnd   = aInitNanoSecondEnd;
            iLoopNanoSecondStart = aLoopNanoSecondStart;
            iLoopNanoSecondEnd   = aLoopNanoSecondEnd;
        }

        public static final void init()
        {
            //add the mp3 plugin in order to play mp3 sounds
            addMp3Plugin();

            //create all players for all sounds
            for ( SoundBg sound : values() )
            {
                sound.createPlayer();
            }
        }

        private static final void addMp3Plugin()
        {
            Format input1 = new AudioFormat( AudioFormat.MPEGLAYER3 );
            Format input2 = new AudioFormat( AudioFormat.MPEG       );
            Format output = new AudioFormat( AudioFormat.LINEAR     );

            PlugInManager.addPlugIn
            (
                com.sun.media.codec.audio.mpa.JavaDecoder.class.getName(),
                new Format[]{input1, input2},
                new Format[]{output},
                PlugInManager.CODEC
            );
        }

        private final void createPlayer()
        {
            try
            {
                ByteArrayInputStream bais  = LibIO.preStreamJarResource( ShooterSettings.Path.ESoundsBg.iUrl + toString() + LibExtension.mp3.getSpecifier() );
                byte[]               bytes = LibIO.readStreamBuffered( bais );
                LibIODataSource      ds    = new LibIODataSource( LibIO.createByteBufferFromByteArray( bytes ), FileTypeDescriptor.MPEG_AUDIO );

                iPlayer = Manager.createPlayer( ds );
            }
            catch ( Throwable t )
            {
                ShooterDebug.sound.trace( t );
            }
        }

        public static final void startSound( SoundBg sound )
        {
            //stop current sound
            stopCurrentSound();

            //assign new sound as current
            currentSound = sound;

            //start new sound threaded
            currentSound.start();
        }

        public static final void stopCurrentSound()
        {
            //stop current sound
            if ( currentSound != null && currentSound.iPlayer != null )
            {
                currentSound.iPlayer.removeControllerListener( currentSound.iControllerListener );
                currentSound.iPlayer.stop();
            }
        }

        private final void start()
        {
            if ( iPlayer != null )
            {
                //create and add the controller listener
                final String soundName = this.toString();
                iControllerListener = new ControllerListener()
                {
                    public void controllerUpdate( ControllerEvent ce )
                    {
                        ShooterDebug.sound.out( "Sound [" + soundName + "] ControllerUpdate [" + ce + "]" );

                        //being invoked when the sound is realized
                        if ( ce instanceof RealizeCompleteEvent )
                        {
                            //set initial start and end time and play
                            iPlayer.setMediaTime( new Time( iInitNanoSecondStart ) );
                            iPlayer.setStopTime(  new Time( iInitNanoSecondEnd   ) );
                            ShooterDebug.sound.out( "Play init from [" + iInitNanoSecondStart + "] to [" + iInitNanoSecondEnd + "]" );
                            iPlayer.start();
                        }

                        //being invoked when the sound is stopped
                        if ( ce instanceof StopAtTimeEvent )
                        {
                            //set initial start and end time and play
                            iPlayer.setMediaTime( new Time( iLoopNanoSecondStart ) );
                            iPlayer.setStopTime(  new Time( iLoopNanoSecondEnd   ) );
                            ShooterDebug.sound.out( "Play loop from [" + iLoopNanoSecondStart + "] to [" + iLoopNanoSecondEnd + "]" );
                            iPlayer.start();
                        }
                    }
                };
                iPlayer.addControllerListener( iControllerListener );

                //realize the player
                iPlayer.realize();
            }
        }
    }
