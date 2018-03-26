
    package de.christopherstock.lib.fx;

    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import de.christopherstock.lib.gl.LibAlign3D;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   Initializes and starts an effect.
    *******************************************************************************************************************/
    public final class LibFXManager
    {
        private         static          Vector<LibFXPoint>         fxPoints                    = new Vector<LibFXPoint>();

        public static void launchStaticPoint( LibDebug debug, LibVertex vertex, LibColors col, float size, int lifetime, int fadeOutTicks )
        {
            LibFXPoint fx = new LibFXPoint
            (
                debug,
                0.0f,
                LibFX.FXType.EStaticDebugPoint,
                col,
                0.0f,
                vertex.x,
                vertex.y,
                vertex.z,
                null,
                0,
                lifetime,
                FXGravity.ENormal,
                fadeOutTicks,
                null //new Sprite( Others.ESprite1, new LibVertex( iPoint.x, iPoint.y, iPoint.z ), LibScalation.ELowerThreeQuarters, WallCollidable.ENo, WallTex.ESliver1 )
            );
//          fx.lifetime  = lifetime;
            fx.pointSize = size;
            LibFXManager.start( fx );
        }

        public static void launchExplosion( LibDebug debug, LibVertex v, LibFX.FXSize size, LibFX.FXTime time, int lifetime, float baseZ, int fadeOutTicks )
        {
            LibFXExplosion fx = new LibFXExplosion( debug, v, size, time, lifetime, baseZ, fadeOutTicks );
            fx.launch();
        }

        public static void launchSliver( LibDebug debug, LibVertex v, LibColors[] sliverColors, float angle, LibParticleQuantity pq, float angleMod, int lifetime, FXSize size, FXGravity gravity, float baseZ, int fadeoutTicks )
        {
            LibFXSliver fx = new LibFXSliver( debug, v, sliverColors, pq, angleMod, lifetime, size, gravity, baseZ, fadeoutTicks );
            fx.launch( angle );
        }

        public static void onRun()
        {
            //browse all points reversed
            for ( int i = fxPoints.size() - 1; i >= 0; --i )
            {
                //prune or animate
                if ( fxPoints.elementAt( i ).isLifetimeOver() )
                {
                    fxPoints.removeElementAt( i );
                }
                else
                {
                    fxPoints.elementAt( i ).animate();
                }
            }
        }

        public static void drawAll()
        {
            //draw all points
            for ( LibFXPoint fxPoint : fxPoints )
            {
                //do not draw delayed points
                if ( !fxPoint.isDelayedBefore() )
                {
                    if ( fxPoint.type == LibFX.FXType.EStaticDebugPoint )
                    {
                        fxPoint.draw( LibAlign3D.AXIS_X );
                        fxPoint.draw( LibAlign3D.AXIS_Y );
                        fxPoint.draw( LibAlign3D.AXIS_Z );
                    }
                    else
                    {
                        fxPoint.draw( fxPoint.align3D);
                    }
                }
            }
        }

        public static void clearDebugPoints()
        {
            //browse all points reversed
            for ( int i = fxPoints.size() - 1; i >= 0; --i )
            {
                //prune or animate
                if ( fxPoints.elementAt( i ).getType() == LibFX.FXType.EStaticDebugPoint )
                {
                    fxPoints.removeElementAt( i );
                }
            }
        }

        public static void removeAllFxPoints()
        {
            fxPoints.removeAllElements();
        }

        protected static void start( LibFXPoint point )
        {
            //adding the fxpoint to the vector makes it active
            fxPoints.addElement( point );
        }
    }
