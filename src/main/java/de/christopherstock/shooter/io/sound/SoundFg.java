
    package de.christopherstock.shooter.io.sound;

    import  java.awt.geom.*;
    import  java.io.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.io.hid.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.Sounds;

    /*******************************************************************************************************************
    *   The sound system.
    *******************************************************************************************************************/
    public enum SoundFg implements LibSound
    {
        EBulletShell1,
        ECamera,

        EDoorClack1,
        EDoorClose1,
        EDoorOpen1,

        EExplosion1,
        EFemaleGiggle1,
        ELocked1,
        EPickUpItem1,
        EPlayerHit1,

        EReload1,
        EReload2,

        EShotAssault1,
        EShotRifle1,
        EShotShotgun1,
        EShotSilenced,

        ETranquilizerShot,
        EUnlocked1,

        EWallElectric1,
        EWallFlesh1,
        EWallGlass1,
        EWallSolid1,
        EWallWood1,

        ;

        private     static  final   int                     MAX_SOUND_QUEUE_SIZE    = 512;

        private                     LibSoundFactory         factory                 = null;

        public void loadBytes()
        {
            String uri = ShooterSetting.Path.ESoundsFg.url + this.toString() + LibExtension.wav.getSpecifier();

            try
            {
                //PRE-stream sound resource from JAR ( won't work otherwise! )
                ByteArrayInputStream bais = LibIO.preStreamJarResource( uri );

                //store all bytes
                this.factory = new LibSoundFactory( bais, ShooterDebug.sound );

                //release the stream
                bais.close();
            }
            catch ( Throwable t )
            {
                ShooterDebug.error.out( "Could not read sound file [" + uri + "]" );
                ShooterDebug.error.trace( t );
                System.exit( 0 );
            }
        }

        public final void playDistancedFx( Point2D.Float distantLocation )
        {
            this.playDistancedFx( distantLocation, 0 );
        }

        /***************************************************************************************************************
        *   Starts a one-shot sound effect being on a specified distance from the listener.
        ***************************************************************************************************************/
        public final void playDistancedFx( Point2D.Float distantLocation, int delay )
        {
            //specify volume from distance
            float distanceToPlayer = (float)Shooter.game.engine.player.getCylinder().getCenterHorz().distance( distantLocation );
            float volume =
            (
                    distanceToPlayer <= Sounds.SPEECH_PLAYER_DISTANCE_MAX_VOLUME
                ?   LibSoundClip.VOLUME_MAX
                :   ( LibSoundClip.VOLUME_MAX - ( distanceToPlayer / Sounds.SPEECH_PLAYER_DISTANCE_MUTE ) )
            );

            //ShooterDebug.bugfix.out( "play distanced fx with volume [" + volume + "]" );

            this.playFx( volume, LibSoundClip.BALANCE_BOTH, delay, distantLocation );
        }

        public final void playGlobalFx()
        {
            this.playGlobalFx( 0 );
        }

        /***************************************************************************************************************
        *   Starts a one-shot sound effect that is not referenced.
        ***************************************************************************************************************/
        public final void playGlobalFx( int delay )
        {
            this.playFx( LibSoundClip.VOLUME_MAX, LibSoundClip.BALANCE_BOTH, delay, null );
        }

        /***************************************************************************************************************
        *   Starts a one-shot sound effect that is not referenced.
        ***************************************************************************************************************/
        private void playFx(float volume, float balance, int delay, Point2D.Float distantLocation )
        {
            //break on disabled system
            if ( ShooterDebug.DISABLE_SOUNDS || ShooterDebug.DEBUG_MODE ) return;

            //break on low memory
            //if ( LibSoundClip.disabledByMemory ) return;

            //reference sound fx
            LibSoundClip sound = this.factory.getInstancedClip( volume, balance, delay, distantLocation );

            try
            {
                //start immediately if not delayed
                if ( delay == 0 )
                {
                    sound.start();
                }

                //add to queue
                if ( Shooter.game.engine.sound.soundQueue.size() < MAX_SOUND_QUEUE_SIZE )
                {
                    Shooter.game.engine.sound.soundQueue.addElement( sound );
                }
            }
            catch ( Throwable t )
            {
                ShooterDebug.error.err( "Throwable caught on playing sound fx " + t );
            }
        }
    }
