
    package de.christopherstock.shooter;

    import  de.christopherstock.shooter.base.*;

    /*******************************************************************************************************************
    *   The main class.
    *
    *   TODO    Improve output for preloader / startup phase.
    *   TODO    Delete all 'a' and 'i' prefixes from variables. ( start at Door )
    *
    *   TODO    Add skybox!
    *   TODO    Different heights for particle effects.
    *   TODO    Mayflower branding and textures ( posters etc. ).
    *
    *   TODO    only for FireArms! Move ArtefactType.iFXImages to Firearms (Wearpons)!
    *   TODO    Optimize fields in ArtefactType!
    *   TODO    Outsource iconImage and UI-matters from ShooterEngine.
    *   TODO    Remove ALL static fields!
    *   TODO    Revise level design.
    *   TODO    Remove all linter warnings!
    *   TODO    Move all CloseCombat constants to settings!
    *   TODO    make new subclass:  Wearpon that derives from ArtefactKind!!
    *   TODO    Complete version 4.0.0.
    *   TODO    Complete the MVP.
    *
    *   TODO    let random assign different topping positions for ShooterWallCollection.createDeskOffice
    *   TODO    slow panning for zoomed glView?
    *   TODO    Refactor the Artefact-System.
    *   TODO    Simple switch for lighting?
    *   TODO    Prefix 'Shooter' only for main and base classes!
    *   TODO    WearponKind.java - remove iParentKind ?
    *   TODO    enum GiveTakeAnim to Gadget?
    *   TODO    Own class for FireFxOffset ?? (use Point2D or Distance-class?)
    *   TODO    suitable door textures
    *   TODO    different wall heights?
    *   TODO    new textures from http://www.cgtextures.com/
    *   TODO    wall type "two glassed wall-windows with one socket in the middle"
    *   TODO    door sockets have wrong tiling
    *   TODO    sprites for bot fire ( muzzle flash )!
    *   TODO    let bot drop multiple items on being killed
    *   TODO    improve collisions? ( make player collisions via ray casting? )
    *   TODO    bsp engine / algo
    *   TODO    Stop sounds when the game is quit!
    *   TODO    create cool story level data
    *   TODO    create double-handed bots or artefacts? - 2 x iCurrentArtefact?
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
