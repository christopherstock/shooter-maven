
    package de.christopherstock.shooter;

    import  de.christopherstock.shooter.base.*;

    /*******************************************************************************************************************
    *   The main class.
    *
    *   TODO    Check, why floating point differences in 3dsmax ASE export files exist?
    *   TODO    Optimize fields in ArtefactType!
    *   TODO    Make new subclass:  Wearpon that derives from ArtefactKind!!
    *   TODO    Only for FireArms! Move ArtefactType.fXImages to Firearms (Wearpons)!
    *   TODO    Move all CloseCombat constants to settings!
    *   TODO    Let random assign different topping positions for ShooterWallCollection.createDeskOffice
    *   TODO    Refactor the Artefact-System.
    *   TODO    Slow panning for zoomed glView?
    *   TODO    WearponKind.java - remove parentKind ?
    *   TODO    Enum GiveTakeAnim to Gadget?
    *   TODO    Own class for FireFxOffset ?? (use Point2D or Distance-class?)
    *   TODO    Suitable door textures
    *   TODO    Different wall heights?
    *   TODO    New textures from http://www.cgtextures.com/
    *   TODO    Wall type "two glassed wall-windows with one socket in the middle"
    *   TODO    Door sockets have wrong tiling
    *   TODO    Sprites for bot fire ( muzzle flash )!
    *   TODO    Let bot drop multiple items on being killed
    *   TODO    Improve collisions? ( make player collisions via ray casting? )
    *   TODO    Create double-handed bots or artefacts? - 2 x currentArtefact?
    *******************************************************************************************************************/
    public class Shooter
    {
        /** The static preloader instance of the shooter game. */
        public      static                  ShooterGame                         game                = null;

        /***************************************************************************************************************
        *   The Project's main-method launched on the application's startup.
        *   The job is to instanciate and to start the {@link ShooterGame}.
        *
        *   @param  args    Arguments from the command-line.
        ***************************************************************************************************************/
        public static void main( String[] args )
        {
            // disable debug mode if run mode is production
            ShooterDebug.checkDebugMode( args );

            // acclaim
            ShooterDebug.major.out( "Welcome to the Shooter project [" + ShooterVersion.getCurrentVersionDesc() + "]" );

            //start main game thread
            game = new ShooterGame();
            game.start();
        }
    }
