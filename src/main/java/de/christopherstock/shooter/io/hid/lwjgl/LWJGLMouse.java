
    package de.christopherstock.shooter.io.hid.lwjgl;

    import  org.lwjgl.input.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.General;
    import  de.christopherstock.shooter.io.hid.*;

    public class LWJGLMouse extends MouseInput
    {
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
                MouseInput.mouseWheelDown = true;
            }
            else if ( wheelSpin > 0 )
            {
                //ShooterDebug.mouse.out( "Wheel rolled up - previous wearpon" );
                MouseInput.mouseWheelUp   = true;
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
                    MouseInput.mouseHoldFire = down;
                    break;
                }

                //right mouse key
                case 1:
                {
                    MouseInput.mouseHoldZoom = down;
                    break;
                }

                //center mouse key
                case 2:
                {
                    MouseInput.mouseHoldReload = down;
                    break;
                }
            }
        }

        private static void checkMovement()
        {
            int distX = Mouse.getEventDX();
            int distY = Mouse.getEventDY();

            MouseInput.mouseMovementX += distX * ShooterSetting.General.MOUSE_MOVEMENT_MULTIPLIER;
            MouseInput.mouseMovementY += distY * ShooterSetting.General.MOUSE_MOVEMENT_MULTIPLIER;

            //clip movements
            if ( MouseInput.mouseMovementX > General.MOUSE_MAX_MOVEMENT_X ) MouseInput.mouseMovementX = General.MOUSE_MAX_MOVEMENT_X;
            if ( MouseInput.mouseMovementY > General.MOUSE_MAX_MOVEMENT_Y ) MouseInput.mouseMovementY = General.MOUSE_MAX_MOVEMENT_Y;
        }

        public static void init()
        {
            //grab mouse to lwjgl display
            Mouse.setGrabbed( true );
/*            
            try
            {
                //inoperative on mac :(
                BufferedImage bi  = ImageIO.read( LibIO.preStreamJarResource( ShooterSetting.Path.EScreen.url + "invisible.png" ) );
                LibGLImage    img = new LibGLImage( bi, LibGLImage.ImageUsage.EOrtho, ShooterDebug.glimage, false );
                Mouse.setNativeCursor( new Cursor( bi.getWidth(), bi.getHeight(), bi.getWidth() / 2, bi.getHeight() / 2, 1, img.bytes.asIntBuffer(), null ) );

                final int SIZE_INT = 4;
                Cursor cursor = null;
                int cursorImageCount = 1;
                int cursorWidth = Cursor.getMaxCursorSize();
                int cursorHeight = cursorWidth;
                IntBuffer cursorImages;
                IntBuffer cursorDelays = null;
                // Create a single cursor, completely transparent
                cursorImages = ByteBuffer.allocateDirect(cursorWidth * cursorHeight * cursorImageCount * SIZE_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
                for (int j = 0; j < cursorWidth; j++) {
                    for (int l = 0; l < cursorHeight; l++) {
                        cursorImages.put(0x00000000);
                    }
                }
                cursorImages.flip();
                cursor = new Cursor(Cursor.getMaxCursorSize(), Cursor.getMaxCursorSize(), Cursor.getMaxCursorSize() / 2, Cursor.getMaxCursorSize() / 2, cursorImageCount, cursorImages, cursorDelays);
                // turn it on
                Mouse.setNativeCursor(cursor);
            }
            catch ( Throwable t )
            {
                ShooterDebug.error.out( "Mouse.setNativeCursor threw a throwable" );
                ShooterDebug.error.trace( t );
            }
*/            
        }
    }
