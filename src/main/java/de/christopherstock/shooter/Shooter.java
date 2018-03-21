
    package de.christopherstock.shooter;

    import  de.christopherstock.shooter.base.*;

    /*******************************************************************************************************************
    *   The main class.
    *
    *   TODO            Maven Goal for signing JAR.
    *   TODO            Maven Goal for creating JNLP file.
    *   TODO            Fix all multilined docblocks to single lines!
    *   TODO            Remove all linter warnings!
    *   TODO            Setup, copy and prune 'dist'.
    *   TODO            Setup, copy and prune '_ASSETS'.
    *   TODO            Turn static field Level.player to non-static!!
    *   TODO            Remove ALL static fields!
    *   TODO            Copy and prune 'MayDay' folder.
    *   TODO            All enum constants to upper case.
    *   TODO            Prune 'ShooterIdeas'.
    *   TODO            Unify all docblocks (length 120), imports and all coding style!
    *   TODO            Fix all code lints.
    *   TODO            Mayflower preloader and textures ( posters ).
    *   TODO            Revise menu.
    *   TODO            Revise preloader!
    *   TODO            Revise level.
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
    *
    *   DONE            Removed runme and turn to maven goal etc.!
    *   DONE            Fixed the sound gap on the two looped bg sounds!
    *   DONE            Let the JMF playback sources from InputStreams.
    *   DONE            Pruned JLayer mp3 player.
    *   DONE            Solved mp3 playback via JMF and mp3 plugin.
    *   DONE            Pruned jogl.
    *   DONE            Pruned swing.
    *   DONE            Supported java 8 for jnlp.
    *   DONE            Adjusted bg sound loop points.
    *   DONE            Refactored level system.
    *   DONE            Different bg sounds for different levels.
    *   DONE            Loop bg sounds.
    *   DONE            Bg-sounds in shooter sound class.
    *   DONE            Support mp3 sound format.
    *   DONE            Fixed OutOfMemory problem on playing sound clips,
    *   DONE            animated textures,
    *   DONE            damage-darken for solid / non-textured faces,
    *   DONE            electric devices now unpenetrable,
    *   DONE            prune all references from lib,
    *   DONE            fixed wav player errors on startup,
    *   DONE            level-section connecting sluices,
    *   DONE            encapsulated level design,
    *   DONE            fixed hit point ( bullet hole ) gaps,
    *   DONE            dismissed: separate bullet holes to section or global,
    *   DONE            main level change,
    *******************************************************************************************************************/
    public class Shooter
    {
        public      static          ShooterMainThread       mainThread          = null;

        /***************************************************************************************************************
        *   The Project's main-method launched on the application's startup.
        *   The job is to instanciate and to start the {@link ShooterMainThread}.
        *
        *   @param  args    Via space separated arguments transfered from the command-line.
        ***************************************************************************************************************/
        public static void main( String[] args )
        {
            //acclaim
            ShooterDebug.major.out( "Welcome to the Shooter project, [" + ShooterVersion.getCurrentVersionDesc() + "] Debug Mode is [" + ShooterDebug.DEBUG_MODE + "]" );

            //start shooter's main thread
            mainThread = new ShooterMainThread();
            mainThread.start();
        }
    }
