
    package de.christopherstock.lib.g3d;

    public enum LibHoleSize
    {
        /** for wearpons that do not produce bullet holes. */
        ENone(  0.0f    ),
        ETiny(  0.015f  ),
        ESmall( 0.020f  ),
        E44mm(  0.025f  ),
        E51mm(  0.030f  ),
        E79mm(  0.035f  ),
        E9mm(   0.040f  ),
        EHuge(  0.045f  ),
        ;

        public  float   size    = 0.0f;

        private LibHoleSize( float size )
        {
            this.size = size;
        }
    }
