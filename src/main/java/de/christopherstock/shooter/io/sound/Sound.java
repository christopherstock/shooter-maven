
    package de.christopherstock.shooter.io.sound;

    import  de.christopherstock.lib.io.hid.LibSoundClip;
    import  de.christopherstock.shooter.Shooter;
    import  de.christopherstock.shooter.ShooterSetting;
    import  javax.media.format.*;
    import  javax.media.*;
    import  java.util.Vector;

    /*******************************************************************************************************************
    *   The sound system.
    *******************************************************************************************************************/
    public class Sound
    {
        public                          Vector<LibSoundClip>    soundQueue                  = new Vector<LibSoundClip>();

        private                         SoundBg                 currentBgSound              = null;

        public void init()
        {
            this.addMp3Plugin();

            this.initFg();
            this.initBg();
        }

        public void onRun()
        {
            //ShooterDebug.sound.out( "maintaining [" + soundQueue.size() + "] sounds" );

            //browse queue reversed
            for ( int i = this.soundQueue.size() - 1; i >= 0; --i )
            {
                //reference queded item
                LibSoundClip queuedItem = this.soundQueue.elementAt( i );

                //remove finished sounds
                if ( queuedItem.hasSoundFinished() )
                {
                    this.soundQueue.removeElement( queuedItem );
                }
                //countdown delayed sounds
                else if ( queuedItem.delay > 0 )
                {
                    //subsctract delay
                    --queuedItem.delay;

                    //start sound if delay reached 0
                    if ( queuedItem.delay == 0 )
                    {
                        queuedItem.start();
                    }
                }
                //adjust volume for currently played sound
                else if ( queuedItem.isDistanced() )
                {
                    float distanceToPlayer = (float) Shooter.game.engine.player.getCylinder().getCenterHorz().distance( queuedItem.getDistantLocation() );
                    float volume = ( distanceToPlayer <= ShooterSetting.Sounds.SPEECH_PLAYER_DISTANCE_MAX_VOLUME ? LibSoundClip.VOLUME_MAX : ( 1.0f - ( distanceToPlayer / ShooterSetting.Sounds.SPEECH_PLAYER_DISTANCE_MUTE ) ) );

                    queuedItem.updateDistancedSound( volume );
                }
            }
        }

        private void addMp3Plugin()
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

        private void initFg()
        {
            for ( SoundFg sound : SoundFg.values() )
            {
                sound.loadBytes();
            }
        }

        private void initBg()
        {
            for ( SoundBg sound : SoundBg.values() )
            {
                sound.createPlayer();
            }
        }

        public void startBgSound(SoundBg sound )
        {
            //stop current sound
            this.stopCurrentBgSound();

            //assign new sound as current
            this.currentBgSound = sound;

            //start new sound threaded
            this.currentBgSound.start();
        }

        public void stopCurrentBgSound()
        {
            //stop current sound
            if ( this.currentBgSound != null && this.currentBgSound.player != null )
            {
                this.currentBgSound.player.removeControllerListener( this.currentBgSound.controllerListener);
                this.currentBgSound.player.stop();

                this.currentBgSound.player.setMediaTime( new Time( 0 ) );
            }
        }
    }
