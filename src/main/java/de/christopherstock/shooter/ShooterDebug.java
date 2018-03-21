
    package de.christopherstock.shooter;

    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   The debug system consisting of switchable debug groups.
    *******************************************************************************************************************/
    public enum ShooterDebug implements LibDebug
    {
        sound(              false   ),      //logs sound matters
        editor(             false   ),      //logs level editor matters
        bulletHole(         false   ),      //logs extra-info about bullet-holes
        saveLoad(           false   ),      //logs game save & load
        mouse(              false   ),      //logs mouse movement
        playerAction(       false   ),      //logs player actions
        bot(                false   ),      //logs bot behaviour
        face(               false   ),      //logs face calcs
        init(               false   ),      //logs initialization
        wallDestroy(        false   ),      //logs destroyed walls
        floorChange(        false   ),      //logs z change behaviour
        level(              false   ),      //logs player actions
        glImage(            false   ),      //logs player actions
        gl(                 false   ),      //logs player's collisions
        d3ds(               false   ),      //logs 3ds max parser
        shotAndHit(         false   ),      //logs shots and hitpoints ( lags performance! )
        playerCylinder(     false   ),      //logs player's cylinder

        bugfix(             true    ),      //prior debug group
        major(              true    ),      //prior debug group
        error(              true    ),      //prior debug group

        ;

        /***************************************************************************************************************
        *   Saying YES is definetely equivalent to the boolean <code>true</code>.
        ***************************************************************************************************************/
        public      static  final   boolean         YES                                 = true;

        /***************************************************************************************************************
        *   Saying NO is definetely equivalent to the boolean <code>false</code>.
        ***************************************************************************************************************/
        public      static  final   boolean         NO                                  = false;

        /***************************************************************************************************************
         *   The global debug switch that disables all debug outputs.
         **************************************************************************************************************/
        public      static  final   boolean         DEBUG_MODE                          = YES;

        public      static  final   boolean         DEBUG_DRAW_PLAYER_CIRCLE            = YES;
        public      static  final   boolean         DEBUG_DRAW_ITEM_CIRCLE              = YES;
        public      static  final   boolean         DEBUG_DRAW_BOT_CIRCLES              = NO;
        public      static  final   boolean         DEBUG_SHOW_FPS                      = YES;

        private                     boolean         iDebugEnabled                       = false;

        private ShooterDebug( boolean aDebugEnabled )
        {
            iDebugEnabled = aDebugEnabled;
        }

        public final void out( Object obj )
        {
            if ( DEBUG_MODE && iDebugEnabled    ) System.out.println( "[" + LibStringFormat.getSingleton().formatDateTime() + "] " + obj );
        }

        public final void err( Object obj )
        {
            if ( DEBUG_MODE                     ) System.err.println( "[" + LibStringFormat.getSingleton().formatDateTime() + "] " + obj );
        }

        public final void trace( Throwable obj )
        {
            if ( DEBUG_MODE                     ) obj.printStackTrace();
        }

        public final void mem()
        {
            if ( DEBUG_MODE && iDebugEnabled )
            {
                Runtime r = Runtime.getRuntime();
                out( "========================================================"                         );
                out( " free:  [" + LibStringFormat.getSingleton().formatNumber( r.freeMemory()  ) + "]" );
                out( " total: [" + LibStringFormat.getSingleton().formatNumber( r.totalMemory() ) + "]" );
                out( "  max:  [" + LibStringFormat.getSingleton().formatNumber( r.maxMemory()   ) + "]" );
            }
        }
    }
