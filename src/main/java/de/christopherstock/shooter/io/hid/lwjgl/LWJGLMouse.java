
    package de.christopherstock.shooter.io.hid.lwjgl;

    import  org.lwjgl.input.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.General;

    public class LWJGLMouse
    {
        public                      float       movementX           = 0;
        public                      float       movementY           = 0;

        public                      boolean     holdButtonLeft      = false;
        public                      boolean     holdButtonCenter    = false;
        public                      boolean     holdButtonRight     = false;

        public                      boolean     wheelDown           = false;
        public                      boolean     wheelUp             = false;

        public void init()
        {
            //grab mouse to LWJGL display
            Mouse.setGrabbed( true );
        }

        public void update()
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

        private void checkWheel()
        {
            //check wheel event
            int wheelSpin = Mouse.getEventDWheel();
            if ( wheelSpin < 0 )
            {
                //ShooterDebug.mouse.out( "Wheel rolled down - next wearpon" );
                wheelDown = true;
            }
            else if ( wheelSpin > 0 )
            {
                //ShooterDebug.mouse.out( "Wheel rolled up - previous wearpon" );
                wheelUp = true;
            }
        }

        private void checkButton()
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
                    holdButtonLeft = down;
                    break;
                }

                //right mouse key
                case 1:
                {
                    holdButtonRight = down;
                    break;
                }

                //center mouse key
                case 2:
                {
                    holdButtonCenter = down;
                    break;
                }
            }
        }

        private void checkMovement()
        {
            int distX = Mouse.getEventDX();
            int distY = Mouse.getEventDY();

            movementX += distX * ShooterSetting.General.MOUSE_MOVEMENT_MULTIPLIER;
            movementY += distY * ShooterSetting.General.MOUSE_MOVEMENT_MULTIPLIER;

            //clip movements
            if ( movementX > General.MOUSE_MAX_MOVEMENT_X ) movementX = General.MOUSE_MAX_MOVEMENT_X;
            if ( movementY > General.MOUSE_MAX_MOVEMENT_Y ) movementY = General.MOUSE_MAX_MOVEMENT_Y;
        }
    }
