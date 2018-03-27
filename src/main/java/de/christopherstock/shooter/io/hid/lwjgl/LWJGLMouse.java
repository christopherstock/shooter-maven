
    package de.christopherstock.shooter.io.hid.lwjgl;

    import  org.lwjgl.input.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.General;

    public class LWJGLMouse
    {
        public          static      float       mouseMovementX      = 0;
        public          static      float       mouseMovementY      = 0;

        public          static      boolean     mouseHoldLeft       = false;
        public          static      boolean     mouseHoldCenter     = false;
        public          static      boolean     mouseHoldRight      = false;

        public          static      boolean     mouseWheelDown      = false;
        public          static      boolean     mouseWheelUp        = false;

        public static void checkMouse()
        {
            //handle all occuring mouse events
            while ( Mouse.next() )
            {
                //check wheel event
                checkWheel();

                //check button event
                checkButton();

                //check movement event
                checkMovement();
            }

            //center mouse in glView - ignore this event!
            //if ( centerMouse ) centerMouse();
        }

        private static void checkWheel()
        {
            //check wheel event
            int wheelSpin = Mouse.getEventDWheel();
            if ( wheelSpin < 0 )
            {
                //ShooterDebug.mouse.out( "Wheel rolled down - next wearpon" );
                mouseWheelDown = true;
            }
            else if ( wheelSpin > 0 )
            {
                //ShooterDebug.mouse.out( "Wheel rolled up - previous wearpon" );
                mouseWheelUp   = true;
            }
        }

        private static void checkButton()
        {
            //check button event
            int     buttonPress = Mouse.getEventButton();
            boolean down        = Mouse.getEventButtonState();

            //ShooterDebug.mouse.out( "Mouse button press: [" + buttonPress + "]" );

            switch ( buttonPress )
            {
                //left mouse key
                case 0:
                {
                    mouseHoldLeft = down;
                    break;
                }

                //right mouse key
                case 1:
                {
                    mouseHoldRight = down;
                    break;
                }

                //center mouse key
                case 2:
                {
                    mouseHoldCenter = down;
                    break;
                }
            }
        }

        private static void checkMovement()
        {
            int distX = Mouse.getEventDX();
            int distY = Mouse.getEventDY();

            mouseMovementX += distX * ShooterSetting.General.MOUSE_MOVEMENT_MULTIPLIER;
            mouseMovementY += distY * ShooterSetting.General.MOUSE_MOVEMENT_MULTIPLIER;

            //clip movements
            if ( mouseMovementX > General.MOUSE_MAX_MOVEMENT_X ) mouseMovementX = General.MOUSE_MAX_MOVEMENT_X;
            if ( mouseMovementY > General.MOUSE_MAX_MOVEMENT_Y ) mouseMovementY = General.MOUSE_MAX_MOVEMENT_Y;
        }

        public static void init()
        {
            //grab mouse to lwjgl display
            Mouse.setGrabbed( true );
        }
    }
