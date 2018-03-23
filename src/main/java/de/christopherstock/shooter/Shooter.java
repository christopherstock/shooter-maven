
    package de.christopherstock.shooter;

    import  de.christopherstock.shooter.base.*;

    /*******************************************************************************************************************
    *   The main class.
    *
    *   TODO            Maven Profiles for debug prod ?
    *
    *   TODO            Maven Goal: documentation.
    *   TODO            Maven Goal for signing JAR.
    *   TODO            Maven Goal for bundling natives in JAR.
    *   TODO            Maven Goal for creating JNLP file.
    *
    *   TODO            Turn all elements in ShooterInit non static!
    *
    *   TODO            Prune onsolete ticker system!
    *   TODO            Check or prune EIntroLogo!
    *   TODO            Fix all multilined docblocks to single lines!
    *   TODO            Remove all linter warnings!
    *   TODO            Setup, copy and prune 'dist'.
    *   TODO            Setup, copy and prune '_ASSETS'.
    *   TODO            Turn ShooterInit not static.
    *   TODO            Outsource ShooterGame to shooter main thread.
    *   TODO            Turn static field Level.player to non-static!!
    *   TODO            Revise menu.
    *   TODO            Mayflower preloader and textures ( posters ).
    *   TODO            Use Mayflower font.
    *   TODO            Remove ALL static fields!
    *
    *   TODO            Copy and prune 'MayDay' folder.
    *   TODO            All enum constants to upper case.
    *   TODO            Fix all code lints.
    *   TODO            Revise preloader!
    *   TODO            Revise level.
    *   TODO            Delete all 'a' and 'i' prefixes from variables.
    *
    *   TODO            Move all CloseCombat constants to settings!
    *   TODO            make new subclass:  Wearpon that derives from ArtefactKind!!
    *   TODO            Remove all prefixes.
    *   TODO            Create example test!
    *   TODO            Different heights for particle effects.
    *   TODO            Cleanup game initialization.
    *   TODO            Revise level design.
    *   TODO            Update Eclipse.
    *   TODO            Zoom Auto Shotgun.
    *   TODO            only for FireArms! Move ArtefactType.iFXImages to Firearms (Wearpons)!
    *   TODO            Enable Fullscreen.
    *   TODO            Let Maven create JNLP.
    *   TODO            Source in pom.xml to 1.8
    *   TODO            Add assets.
    *   TODO            Complete version 0.3.11 and add DONE items to History.
    *   TODO            Optimize fields in ArtefactType!
    *
    *   TODO            let random assign different topping positions for ShooterWallCollection.createDeskOffice
    *   TODO            slow panning for zoomed view?
    *   TODO            Refactor the Artefact-System.
    *   TODO            Simple switch for lighting?
    *   TODO            Prefix 'Shooter' only for main and base classes!
    *   TODO            WearponKind.java - remove iParentKind ?
    *   TODO            enum GiveTakeAnim to Gadget?
    *   TODO            Own class for FireFxOffset ?? (use Point2D or Distance-class?)
    *   TODO            suitable door textures
    *   TODO            different wall heights?
    *   TODO            new textures from http://www.cgtextures.com/
    *   TODO            wall type "two glassed wall-windows with one socket in the middle"
    *   TODO            door sockets have wrong tiling
    *   TODO            sprites for bot fire ( muzzle flash )!
    *   TODO            let bot drop multiple items on being killed
    *   TODO            improve collisions? ( make player collisions via ray casting? )
    *   TODO            bsp engine / algo
    *   TODO            Stop sounds when the game is quit!
    *   TODO            create cool story level data
    *   TODO            create double-handed bots or artefacts? - 2 x iCurrentArtefact?
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
            ShooterDebug.major.out( "Welcome to the Shooter project [" + ShooterVersion.getCurrentVersionDesc() + "]" );

            //start main game thread
            game = new ShooterGame();
            game.start();
        }
    }
