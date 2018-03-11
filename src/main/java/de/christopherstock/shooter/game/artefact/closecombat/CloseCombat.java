/*  $Id: CloseCombat.java 1288 2014-10-11 17:19:47Z jenetic.bytemare $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.game.artefact.closecombat;

    import  java.awt.geom.Point2D;

    import  de.christopherstock.lib.Lib.*;
    import  de.christopherstock.lib.fx.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.game.LibShot.ShotSpender;
    import  de.christopherstock.lib.math.*;
import de.christopherstock.shooter.game.artefact.Artefact;
import de.christopherstock.shooter.game.artefact.ArtefactKind;
    import  de.christopherstock.shooter.io.sound.*;
import  de.christopherstock.shooter.level.*;

    /**************************************************************************************
    *   A close-combat Wearpon.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public final class CloseCombat extends ArtefactKind
    {
        private         int             iDamage         = 0;
        private         SoundFg    iUseSound       = null;

        public CloseCombat( int aDamage )
        {
            iDamage = aDamage;
        }

        @Override
        public boolean use( Artefact w, ShotSpender ss, Point2D.Float shooterXY )
        {
            //ShooterDebug.bugfix.out( "LAUNCH CC - damage is [" + iDamage + "]" );

            //launch use-sound-fx
            if ( iUseSound != null )
            {
                if ( shooterXY == null )
                {
                    iUseSound.playGlobalFx();
                }
                else
                {
                    iUseSound.playDistancedFx( shooterXY );
                }
            }

            //clear all debug fx points before firing!
            LibFXManager.clearDebugPoints();

            //let random decide direction ( hit left to right or right to left )
            float from  = -25.0f;
            float to    =  25.0f;
            float delta = 5.0f;		// TODO to settings!

            if ( LibMath.getRandom( 0, 1 ) == 0 )
            {
                //launch horizontal strayed shots
                for ( float f = from; f <= to; f += delta )
                {
                    //draw the shot line and launch the shot
                    LibShot s = ss.getShot( f );
                    //s.drawShotLine( FxSettings.LIFETIME_DEBUG );
                    if ( Level.currentSection().launchShot( s ).length > 0 )
                    {
                        //break on 1st hit ( only one hit per punch )
                        break;
                    }
                }
            }
            else
            {
                //launch horizontal strayed shots
                for ( float f = to; f >= from; f -= delta )
                {
                    //draw the shot line and launch the shot
                    LibShot s = ss.getShot( f );
                    //s.drawShotLine( FxSettings.LIFETIME_DEBUG );
                    if ( Level.currentSection().launchShot( s ).length > 0 )
                    {
                        //break on 1st hit ( only one hit per punch )
                        break;
                    }
                }
            }

            return true;
        }

        @Override
        public int getDamage()
        {
            return iDamage;
        }

        @Override
        public final ParticleQuantity getSliverParticleQuantity()
        {
            return ParticleQuantity.EMedium;
        }

        @Override
        public final FXSize getSliverParticleSize()
        {
            return FXSize.EMedium;
        }

        @Override
        public final LibHoleSize getBulletHoleSize()
        {
            return LibHoleSize.E9mm;
        }
    }
