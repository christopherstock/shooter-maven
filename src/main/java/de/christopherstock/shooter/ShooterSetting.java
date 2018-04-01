
    package de.christopherstock.shooter;

    import  de.christopherstock.lib.gl.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.level.setup.*;
    import  de.christopherstock.shooter.state.*;

    /*******************************************************************************************************************
    *   All settings for the project.
    *******************************************************************************************************************/
    public class ShooterSetting
    {
        public static final class Form
        {
            /** The window's title string. */
            public  static  final   String          FORM_TITLE                          = "LWJGL 3D Shooter, " + ShooterVersion.getCurrentVersionDesc();
            public  static          int             FORM_WIDTH                          = 1024;
            public  static          int             FORM_HEIGHT                         = 768;
        }

        public static final class Startup
        {
            public      static  final   MainState   STARTUP_STATE_AFTER_PRELOADER       = MainState.EIngame;
            public      static  final   LevelSetup  STARTUP_LEVEL_MAIN                  = new LevelSetupTestMayflowerOffice();
            public      static  final   int         STARTUP_LEVEL_SECTION               = LevelSetupTestMayflowerOffice.SECTION_ONE;
        }

        public static final class General
        {
            public      static  final   boolean     DISABLE_PLAYER_WALKING_ANGLE_Y      = ShooterDebug.YES;
            public      static  final   boolean     DISABLE_PLAYER_TO_WALL_COLLISIONS   = ShooterDebug.NO;
            public      static  final   boolean     DISABLE_PLAYER_TO_BOT_COLLISIONS    = ShooterDebug.NO;
            public      static  final   boolean     DISABLE_GRAVITY                     = ShooterDebug.NO;
            public      static  final   boolean     DISABLE_LOOK_CENTERING_X            = ShooterDebug.YES;

            public      static  final   boolean     ENABLE_3RD_PERSON_CAMERA            = ShooterDebug.NO;

            public      static  final   float       FADE_OUT_FACES                      = 0.04f;
            public      static  final   int         FADE_OUT_FACES_TOTAL_TICKS          = (int)( 1.0f / FADE_OUT_FACES );

            public      static  final   float       MOUSE_MOVEMENT_MULTIPLIER           = 0.1f;
            public      static  final   float       MOUSE_MAX_MOVEMENT_X                = 10.0f;
            public      static  final   float       MOUSE_MAX_MOVEMENT_Y                = 5.0f;

            public      static  final   float       SPEED_ZOOM                          = 4.0f;     //per tick
            public      static  final   float       MAX_ZOOM                            = LibGLView.VIEW_ANGLE - 0.5f;

            public      static  final   int         TICKS_ADRENALINE                    = 1500;
        }

        public static final class HUDSettings
        {
            public  static  final   boolean         SHOW_MAGAZINE_SIZE                  = true;

            public  static  final   float           MAX_OPACITY_HEALTH_FX               = 1.0f;
            public  static  final   float           MAX_OPACITY_DAMAGE_FX               = 1.0f;

            public  static  final   float           LINE_SPACING_RATIO_EMPTY_LINES      = 0.5f;
            public  static  final   int             TICKS_SHOW_HEALTH_AFTER_CHANGE      = 120;
            public  static  final   int             TICKS_FADE_OUT_HEALTH               = 20;

            public  static  final   int             PLAYER_LOW_HEALTH_WARNING_PERCENT   = 20;

            public  static  final   float           MSG_OPACITY                         = 1.0f;
            public  static  final   int             MSG_TICKS_POP_UP                    = 2;
            public  static  final   int             MSG_TICKS_STILL                     = 100;
            public  static  final   int             MSG_TICKS_POP_DOWN                  = 16;

            public  static  final   int             TICKS_MAIN_MENU_BLOCKER             = 5;
        }


        /***************************************************************************************************************
        *   Holding all of player's constant attributes.
        ***************************************************************************************************************/
        public class PlayerSettings
        {
            public  static  final   boolean         INVINCIBILITY                       = false;                //no damage

            //basics
            public  static  final   float           RADIUS_BODY                         = 0.4f;                 //player's standing radius
            public  static  final   float           RADIUS_CLOSE_COMBAT                 = 1.5f;
            public  static  final   float           RADIUS_ACTION                       = 1.75f;                //player's action radius

            public  static  final   float           VIEW_DISTANCE                       = 50.0f;                //max. glView distance - faces do not get drawed if current distance to player is higher

            public  static  final   float           MAX_ACTION_VIEW_ANGLE               = 80.0f;                //to left and right side of the center glView

            //depths
            public  static  final   float           DEPTH_TOTAL_STANDING                = 1.3f;                 //for collisions
            public  static  final   float           DEPTH_TOTAL_CROUCHING               = 0.7f;                 //for collisions

            public  static  final   float           DEPTH_EYE_STANDING                  = 1.2f;                 //player's eye-distance from floor
            public  static  final   float           DEPTH_EYE_CROUCHING                 = 0.6f;                 //player's eye-distance from floor

            public  static  final   float           DEPTH_HAND_STANDING                 = DEPTH_EYE_STANDING;   //player's hand-distance from floor
            public  static  final   float           DEPTH_HAND_CROUCHING                = DEPTH_EYE_CROUCHING;  //player's hand-distance from floor

            public  static  final   float           TRANS_X_HAND                        = 0.0f;                 //0.1f;     //player's hand x-translation for shots
            public  static  final   float           TRANS_Y_HAND                        = 0.0f;                 //0.0f;     //player's hand y-translation for shots

            public  static  final   float           DEPTH_DEATH                         = 0.08f;                //player's glView height on being dead

            //speed
            public  static  final   float           SPEED_WALKING                       = 0.2f; //0.15f;        //walking  speed
            public  static  final   float           SPEED_STRAFING                      = 0.1f;                 //strafing speed
            public  static  final   float           SPEED_CROUCH_TOGGLE                 = 0.1f;                 //speed to crouch / get up
            public  static  final   float           SPEED_FALLING_MIN                   = 0.075f;               //falling down on z axis per tick
            public  static  final   float           SPEED_FALLING_MAX                   = 0.3f;                 //falling down on z axis per tick
            public  static  final   float           SPEED_FALLING_MULTIPLIER            = 1.25f;                //falling   speed multiplier per tick
            public  static  final   float           SPEED_TURNING_Z                     = 2.0f;                 //2.5f; //turning   speed in ° (left/right)
            public  static  final   float           SPEED_LOOKING_X                     = 2.5f;                 //5.0f; //1.25f;    //looking   speed in ° (up/down)
            public  static  final   float           SPEED_CENTERING_X                   = 10.0f;                //centering speed in ° back to 0°
            public  static  final   float           SPEED_DYING_SINKING                 = 0.1f;                 //rotating  speed in ° x y z on dying

            //health
            public  static  final   int             MAX_HEALTH                          = 25;                   //maximum health value

            public  static  final   float           ROTATION_DYING                      = 4.5f;                 //rotating speed in ° x y z on dying
            public  static  final   float           MAX_LOOKING_X                       = 85.0f;                //max. look up/down in °

            public  static  final   float           MIN_CLIMBING_UP_Z                   = 0.02f;                //min auto climbing on non-climbable faces z
            public  static  final   float           MAX_CLIMBING_UP_Z                   = 1.0f;                 //max auto climbing on climbable faces z

            public  static  final   float           AMP_WALKING_Z                       = 0.10f;                 //0.05f;    //z-amplitude ratio

            public  static  final   float           SPEED_WALKING_ANGLE_Y               = 7.5f;                 //y-modification per walking-step in °
            public  static  final   float           SPEED_WALKING_ANGLE_WEARPON_X       = 5.0f;                 //y-modification per walking-step in °
            public  static  final   float           SPEED_WALKING_ANGLE_WEARPON_Y       = 2.5f;                 //y-modification per walking-step in °

            public  static  final   int             GIVE_TAKE_ANIM_RATIO                = 4;
        }

        public static final class DoorSettings
        {
            public  static  final   int             DOOR_TICKS_OPEN_CLOSE               = 20;
            public  static  final   float           DOOR_SLIDING_TRANSLATION_OPEN       = 1.9f;
            public  static  final   float           DOOR_ANGLE_OPEN                     = -90f;
            public  static  final   int             DOOR_DEFAULT_AUTO_CLOSE_DELAY       = 100;
            public  static  final   int             DOOR_SLUICE_AUTO_CLOSE_DELAY        = 50;
        }

        public static final class Performance
        {
            // --- min thread delay in ms per tick ---
            public  static  final   int             MIN_THREAD_DELAY                    = 16;

            // --- performance settings --- //
            public  static  final   int             COLLISION_CHECKING_STEPS            = 5;
            public  static  final   int             MAX_NUMBER_BULLET_HOLES             = 256;
            public  static  final   int             ELLIPSE_SEGMENTS                    = 45;

            // --- speed settings --- //
            public  static  final   int             TICKS_WEARPON_HIDE_SHOW             = 6;
            public  static  final   int             TICKS_HEALTH_FX                     = 50;
            public  static  final   int             MAX_TICKS_DAMAGE_FX                 = 100;
            public  static  final   int             TICKS_DEAD_FX                       = 50;
            public  static  final   int             TICKS_DYING_FX                      = 50;
            public  static  final   int             TICKS_REINCARNATION_FX              = 50;

            public  static  final   int             TICKS_TEXTURE_ANIMATION_SLOW        = 10;

            public  static  final   int             DELAY_AFTER_PLAYER_ACTION           = 200;
            public  static  final   int             DELAY_AFTER_LEVEL_CHANGE            = 200;
            public  static  final   int             DELAY_AFTER_AVATAR_MESSAGE          = 200;
            public  static  final   int             DELAY_AFTER_CROUCHING               = 200;
            public  static  final   int             DELAY_AFTER_GAIN_HEALTH             = 200;
            public  static  final   int             DELAY_AFTER_DAMAGE_FX               = 200;
            public  static  final   int             DELAY_AFTER_RELOAD                  = 200;
            public  static  final   int             DELAY_AFTER_EXPLOSION               = 200;
            public  static  final   int             DELAY_AFTER_MAIN_MENU_TOGGLE        = 200;
        }

        public static final class Sounds
        {
            public  static  final   float           SPEECH_PLAYER_DISTANCE_MAX_VOLUME   = 3.0f;
            public  static  final   float           SPEECH_PLAYER_DISTANCE_MUTE         = 50.0f;
        }

        public static final class BotSettings
        {
            public static final class RotationSpeed
            {
                /*******************************************************************************************************
                *   The relative speed of the limbs in percent of the full distance.
                *******************************************************************************************************/
                public  static  final   float       LIMBS                               = 5.0f;
                public  static  final   float       UPPER_ARM                           = 5.0f;
                public  static  final   float       LOWER_ARM                           = 7.5f;
                public  static  final   float       HEAD                                = 2.5f;
                public  static  final   float       HAND                                = 10.0f;
            }

            /***********************************************************************************************************
            *   The maximum turning speed in degrees per tick.
            ***********************************************************************************************************/
            public  static  final   float           SPEED_TURNING_MAX                   = 10.0f;

            /***********************************************************************************************************
            *   The minimum turning speed in degrees per tick.
            ***********************************************************************************************************/
            public  static  final   float           SPEED_TURNING_MIN                   = 1.0f;

            /***********************************************************************************************************
            *   The angle tolerance on turning the bot.
            ***********************************************************************************************************/
            public  static  final   float           TARGET_TURNING_TOLERANCE            = 2.0f;

            public  static  final   float           WAY_POINT_RADIUS                    = 0.25f;

            public  static  final   float           SHOT_RANGE                          = 20.0f;
            public  static  final   float           VIEW_RANGE                          = 25.0f;

            public  static  final   float           SPEED_WALKING                       = 0.05f;
            public  static  final   float           HEIGHT                              = 1.3f;
            public  static  final   float           RADIUS                              = 0.25f;

            public  static  final   float           MAX_LEADING_DISTANCE_TO_PLAYER      = 15.0f;

            public  static  final   int             EYE_CLOSED_INTERVAL                 = 50;
            public  static  final   int             EYE_BLINK_INTERVAL_MIN              = 3000;
            public  static  final   int             EYE_BLINK_INTERVAL_MAX              = 6000;

            public  static  final   int             INTER_ANIMATIONS_DELAY              = 25;
        }

        public static final class BulletHoles
        {
            public  static  final   float           MIN_DISTANCE_FROM_FACE              = 0.001f;
            public  static  final   float           STEP_DISTANCE_FROM_FACE             = 0.0001f;
            public  static  final   float           MIN_POINT_DISTANCE_FOR_SAME_LAYER_MULTIPLIER    = 4.0f;
        }

        public static final class OffsetsOrtho
        {
            public  static          int             EBorderHudX                         = 0;
            public  static          int             EBorderHudY                         = 0;
            protected static          int             EHudMsgY                            = 0;
            public  static          int             EAvatarBgPanelHeight                = 0;
            public  static          int             EAvatarMsgX                         = 0;
            public  static          int             EAvatarMsgY                         = 0;

            public static void parseOffsets(int panelWidth, int panelHeight )
            {
                EBorderHudX             = panelWidth  * 5 / 100;
                EBorderHudY             = panelHeight * 5 / 100;
                EHudMsgY                = panelHeight * 6 / 100;
                EAvatarBgPanelHeight    = 96;
                EAvatarMsgX             = 10;
                EAvatarMsgY             = 10;
            }
        }

        public static final class AvatarMessages
        {
            public  static  final   float           OPACITY_PANEL_BG                    = 0.5f;
            public  static  final   float           OPACITY_AVATAR_IMG                  = 1.0f;

            public  static  final   int             ANIM_TICKS_POP_UP                   = 10;
            public  static  final   int             ANIM_TICKS_STILL                    = 150;
            public  static  final   int             ANIM_TICKS_POP_DOWN                 = 10;
        }

        public static final class ItemSettings
        {
            public  static  final   float           AMMO_RADIUS                         = 0.75f;
            public  static  final   float           WEARPON_RADIUS                      = 0.75f;
            public  static  final   float           ITEM_RADIUS                         = 0.75f;
            public  static  final   float           LEVEL_CHANGE_RADIUS                 = 2.0f;
            public  static  final   float           EVENT_RADIUS                        = 0.75f;

            public  static  final   float           SPEED_ROTATING                      = 2.5f;
            public  static  final   float           SPEED_FALLING                       = 0.025f;
        }

        public static final class FxSettings
        {
            public  static  final   int             LIFETIME_EXPLOSION                  = 200;                  //in ticks
            public  static  final   int             LIFETIME_DEBUG                      = 200;                  //in ticks
            public  static  final   int             LIFETIME_SLIVER                     = 200;                  //in ticks
            public  static  final   int             LIFETIME_BLOOD                      = 400;                  //in ticks
            public  static  final   int             LIFETIME_CORPSE                     = 400;                  //in ticks

            public  static  final   float           MIN_DARKEN_FACES                    = 0.5f;
            public  static  final   float           SLIVER_ANGLE_MOD                    = 20.0f;
        }

        public static class Colors
        {
            public  static  final   LibColors       EAvatarMessageText                  = LibColors.EWhite;
            public  static  final   LibColors       EAvatarMessageTextOutline           = LibColors.EGreyDark;
            public  static  final   LibColors       EFpsFg                              = LibColors.EWhite;
            public  static  final   LibColors       EFpsOutline                         = LibColors.EGreyDark;
            public  static  final   LibColors       EAmmoFg                             = LibColors.EWhite;
            public  static  final   LibColors       EAmmoOutline                        = LibColors.EGreyDark;

            public  static  final   LibColors       EHealthFgNormal                     = LibColors.EWhite;
            public  static  final   LibColors       EHealthFgWarning                    = LibColors.ERed;

            public  static  final   LibColors       EHealthOutline                      = LibColors.EGreyDark;
            public  static  final   LibColors       EHudMsgFg                           = LibColors.EWhite;
            public  static  final   LibColors       EHudMsgOutline                      = LibColors.EBlack;

            public  static  final   LibColors       EAvatarMessagePanelBgRed            = LibColors.ERedLight;
            public  static  final   LibColors       EAvatarMessagePanelBgYellow         = LibColors.EYellow;
            public  static  final   LibColors       EAvatarMessagePanelBgGrey           = LibColors.EGreyLight;
            public  static  final   LibColors       EAvatarMessagePanelBgBlack          = LibColors.EBlack;
            public  static  final   LibColors       EAvatarMessagePanelBgGreen          = LibColors.EGreenLight;
            public  static  final   LibColors       EAvatarMessagePanelBgBlue           = LibColors.EBlueLight;

            public  static  final   LibColors[]     SLIVER_COLOR_WALL                   = new LibColors[]
            {
                LibColors.EExplosion1,  LibColors.EExplosion2,  LibColors.EExplosion3,
                LibColors.EExplosion4,  LibColors.EExplosion5,  LibColors.EExplosion6,
                LibColors.EExplosion7,  LibColors.EExplosion8,  LibColors.EExplosion9,
                LibColors.EExplosion10, LibColors.EExplosion11, LibColors.EExplosion12,
            };

            public  static  final   LibColors[]     SLIVER_COLOR_RED_BRICKS             = new LibColors[]
            {
                LibColors.ESliverBricks1,  LibColors.ESliverBricks2,  LibColors.ESliverBricks3,
                LibColors.ESliverBricks4,  LibColors.ESliverBricks5,
            };

            public  static  final   LibColors[]     SLIVER_COLOR_BROWN_BRICKS           = new LibColors[]
            {
                LibColors.EBrown, LibColors.EBrownDark, LibColors.EBrownLight,
            };

            public  static  final   LibColors[]     SLIVER_COLOR_BLOOD                  = new LibColors[]
            {
                LibColors.ESliverBlood1,  LibColors.ESliverBlood2,  LibColors.ESliverBlood3,
            };

            public  static  final   LibColors[]     SLIVER_COLOR_GLASS                  = new LibColors[]
            {
                LibColors.ESliverGlass1,  LibColors.ESliverGlass2,  LibColors.ESliverGlass3,
            };

            public  static  final   LibColors[]     SLIVER_COLOR_STEEL                  = new LibColors[]
            {
                LibColors.ESliverSteel1,  LibColors.ESliverSteel2,  LibColors.ESliverSteel3,
            };
        }

        /***************************************************************************************************************
        *   All paths the application makes use of.
        *   All references are specified absolute.
        ***************************************************************************************************************/
        public enum Path
        {
            EBackGrounds(           "/res/bg/"                      ),
            ETexturesDefault(       "/res/texture/default/"         ),
            ETexturesMask(          "/res/texture/mask/"            ),
            ETexturesBulletHole(    "/res/texture/bullethole/"      ),
            ETexturesBot(           "/res/texture/bot/"             ),
            ETexturesItem(          "/res/texture/item/"            ),
            ETexturesWall(          "/res/texture/wall/"            ),
            EAvatar(                "/res/hud/avatar/"              ),
            EArtefact(              "/res/hud/artefact/"            ),
            EArtefactMuzzleFlash(   "/res/hud/artefactMuzzleFlash"  ),
            EShells(                "/res/hud/shell/"               ),
            EGadget(                "/res/hud/gadget/"              ),
            EScreen(                "/res/screen/"                  ),
            ECrossHair(             "/res/crosshair/"               ),
            ESoundsBg(              "/res/sound/bg/"                ),
            ESoundsFg(              "/res/sound/fg/"                ),
            E3dsMaxBot(             "/res/d3ds/bot/"                ),
            E3dsMaxOther(           "/res/d3ds/other/"              ),
            E3dsMaxItem(            "/res/d3ds/item/"               ),
            E3dsMaxMenu(            "/res/d3ds/menu/"               ),
            EFont(                  "/res/font/"                    ),
            ;

            public                  String                  url                     = null;

            private Path( String url )
            {
                this.url = url;

                //assert leading slash
                if ( !this.url.startsWith( "/" ) ) this.url = "/" + this.url;

                //assert trailing slash
                if ( !this.url.endsWith(   "/" ) ) this.url = this.url + "/";
            }
        }
    }
