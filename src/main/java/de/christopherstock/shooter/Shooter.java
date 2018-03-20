
    package de.christopherstock.shooter;

    import  de.christopherstock.shooter.base.*;

    /*******************************************************************************************************************
    *   Project's Main-Class containing the {@link #main(String[])} method.
    *
    *   TODO            Remove all KILLEFIT!!
    *   TODO            Maven Goal for creating JNLP file.
    *   TODO            Remove author and version tag.
    *   TODO            make new subclass:  Wearpon that derives from ArtefactKind!!
    *   TODO            Maven Goal for signing JAR.
    *   TODO            Move all CloseCombat constants to settings!
    *   TODO            Remove all linter warnings!
    *   TODO            Setup, copy and prune 'dist'.
    *   TODO            Setup, copy and prune '_ASSETS'.
    *   TODO            Copy and prune 'MayDay' folder.
    *   TODO            Unify all docblocks (length 120), imports and all coding style!
    *   TODO            Mayflower preloader and textures ( posters ).
    *   TODO            Remove all prefixes.
    *   TODO            Revise menu.
    *   TODO            Create example test!
    *   TODO            Different heights for particle effects.
    *   TODO            Revise preloader!
    *   TODO            Revise level.
    *   TODO            Remove ALL static fields!
    *   TODO            Turn static field Level.player to non-static!!
    *   TODO            Fix all code lints.
    *   TODO            Prune all static fields!!
    *   TODO            Revise level design.
    *   TODO            Update Eclipse.
    *   TODO            Zoom Auto Shotgun.
    *   TODO            only for FireArms! Move ArtefactType.iFXImages to Firearms (Wearpons)!
    *   TODO            Enable Fullscreen.
    *   TODO            Let Maven create JNLP.
    *   TODO            Source in pom.xml to 1.8
    *   TODO            Add assets.
    *   TODO            Complete version 0.3.11 and add DONE items to History.
    *
    *   TODO HIGH       slow panning for zoomed view?
    *   TODO            Refactor the Artefact-System.
    *   TODO            Simple switch for lighting?
    *   TODO            Prefix 'Shooter' only for main and base classes!
    *   TODO            WearponKind.java - remove iParentKind ?
    *   TODO            enum GiveTakeAnim to Gadget?
    *   TODO            Own class for FireFxOffset ?? (use Point2D or Distance-class?)
    *   TODO ASAP       Stop sounds when the game is quit!
    *   TODO ASAP       suitable door textures
    *   TODO ASAP       different wall heights?
    *   TODO HIGH       new textures from http://www.cgtextures.com/
    *   TODO HIGH       wall type "two glassed wall-windows with one socket in the middle"
    *   TODO HIGH       door sockets have wrong tiling
    *   TODO INIT       sprites for bot fire ( muzzle flash )!
    *   TODO LOW        let bot drop multiple items on being killed
    *   TODO WEAK       improve collisions? ( make player collisions via ray casting? )
    *   TODO WEAK       bsp engine / algo
    *   TODO WEAK       create cool story level data
    *   TODO WEAK       create double-handed bots or artefacts? - 2 x iCurrentArtefact?
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
    *
    *   @version    0.3.11
    *   @author     Christopher Stock
    *******************************************************************************************/
    public class Shooter
    {
        public      static          ShooterMainThread       mainThread          = null;

        /**************************************************************************************
        *   The Project's main-method launched on the application's startup.
        *   The job is to instanciate and to start the {@link ShooterMainThread}.
        *
        *   @param  args    Via space separated arguments transfered from the command-line.
        **************************************************************************************/
        public static void main( String[] args )
        {
            //acclaim
            ShooterDebug.major.out( "Welcome to the Shooter project, [" + ShooterVersion.getCurrentVersionDesc() + "] Debug Mode is [" + ShooterDebug.DEBUG_MODE + "]" );

            //start shooter's main thread
            mainThread = new ShooterMainThread();
            mainThread.start();
        }
    }
