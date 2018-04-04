
    package de.christopherstock.lib;

    /*******************************************************************************************************************
    *   Specifies scalation identifiers.
    *******************************************************************************************************************/
    public enum LibScalation
    {
        ELowerThreeQuarters(    0.25f  ),
        ELowerTwoThirds(        0.33f  ),
        ELowerHalf(             0.5f   ),
        ELowerThirds(           0.66f  ),
        ENone(                  1.0f   ),
        EAddQuarter(            1.25f  ),
        EAddThird(              1.33f  ),
        EAddHalf(               1.5f   ),
        EAddTwoThirds(          1.66f  ),
        EAddThreeQuarters(      1.75f  ),
        EDouble(                2.0f   ),
        ETriple(                3.0f   ),
        EQuadruple(             4.0f   ),
        EQuintuple(             5.0f   ),
        ESextuple(              6.0f   ),
        ESeptuple(              7.0f   ),
        EOctuple(               8.0f   ),
        ENinefold(              9.0f   ),
        EDecuple(               10.0f  ),
        ;

        private         float               scaleFactor             = 0.0f;

        private LibScalation( float scaleFactor )
        {
            this.scaleFactor = scaleFactor;
        }

        public final float getScaleFactor()
        {
            return this.scaleFactor;
        }
    }
