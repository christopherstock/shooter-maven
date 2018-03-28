
    package de.christopherstock.shooter.lib.math;

    import  de.christopherstock.lib.math.LibMath;
    import  junit.framework.*;

    /*******************************************************************************************************************
    *   Unit test for simple App.
    *******************************************************************************************************************/
    public class LibMathTest extends TestCase
    {
        public void testNormalizeAngle()
        {
            assertEquals( 340.0f, LibMath.normalizeAngle( -20 ) );
            assertEquals( 40.0f,  LibMath.normalizeAngle( 400 ) );
            assertEquals( 80.0f,  LibMath.normalizeAngle( 800 ) );
        }
    }
