
    package de.christopherstock.lib.ui;

    import  java.awt.*;
    import  de.christopherstock.lib.math.*;

    /*******************************************************************************************************************
    *   Offeres predefined colors.
    *******************************************************************************************************************/
    public enum LibColors
    {
        EBlack(                         0xff000000      ),
        EBlackTranslucent(              0x77000000      ),

        ERed(                           0xffff0000      ),
        ERedTranslucent(                0x77ff0000      ),

        ERedDark(                       0xff662222      ),
        ERedLight(                      0xffff6666      ),
        EGreen(                         0xff00ff00      ),
        EGreenLight(                    0xff55ff55      ),
        EBlue(                          0xff0000ff      ),
        EBlueLight(                     0xff98c4f9      ),
        EBlueDark(                      0xff000099      ),
        EWhite(                         0xffffffff      ),

        ESkin(                          0xfff2cec5      ),
        EOrangeMF(                      0xffec7404      ),

        EBrown(                         0xffb68527      ),
        EBrownDark(                     0xff89651e      ),
        EBrownLight(                    0xffe7b148      ),

        EPink(                          0xffff5555      ),
        EYellow(                        0xffffff00      ),
        EYellowLight(                   0xffffff66      ),
        EYellowTranslucent(             0x55ffff00      ),

        EGrey(                          0xff909090      ),
        EGreyLight(                     0xffbbbbbb      ),
        EGreyDark(                      0xff444444      ),

        EExplosion1(                    0xff785216      ),
        EExplosion2(                    0xff62471c      ),
        EExplosion3(                    0xff8b7757      ),
        EExplosion4(                    0xffb09468      ),
        EExplosion5(                    0xff7a6d58      ),
        EExplosion6(                    0xff845124      ),
        EExplosion7(                    0xff9d7e62      ),
        EExplosion8(                    0xff786e65      ),
        EExplosion9(                    0xff737373      ),
        EExplosion10(                   0xff888787      ),
        EExplosion11(                   0xff343434      ),
        EExplosion12(                   0xff000000      ),

        ESliverBricks1(                 0xff9c353c      ),
        ESliverBricks2(                 0xff852128      ),
        ESliverBricks3(                 0xffba3d24      ),
        ESliverBricks4(                 0xffc9c9c9      ),
        ESliverBricks5(                 0xffa1a1a1      ),

        ESliverBlood1(                  0xffce2222      ),
        ESliverBlood2(                  0xffa02929      ),
        ESliverBlood3(                  0xffd12b2b      ),

        ESliverGlass1(                  0xff9cd4f1      ),
        ESliverGlass2(                  0xffe9f0f4      ),
        ESliverGlass3(                  0xffdcf3ff      ),

        ESliverSteel1(                  0xff979fa2      ),
        ESliverSteel2(                  0xffb5bfc1      ),
        ESliverSteel3(                  0xffb3bdc1      ),

        EShotLine(                      0xfff7f26b      ),
        ECrosshair(                     0xff6666ff      ),

        ;

        private             int         rgb             = 0;
        public              float[]     f4              = null;
        public              float[]     f3              = null;
        public              Color       colARGB         = null;
        public              Color       colABGR         = null;

        private LibColors( int rgb )
        {
            this.rgb = rgb;
            this.f3 = LibMath.col2f3( rgb );
            this.f4 = LibMath.col2f4( rgb );
            this.colARGB = new Color( rgb, true );
            this.colABGR = new Color( this.colARGB.getBlue(), this.colARGB.getGreen(), this.colARGB.getRed(), this.colARGB.getAlpha() );
        }

        public static LibColors getByName(String name )
        {
            if ( name == null ) return null;

            for ( LibColors col : values() )
            {
                if ( col.name().compareToIgnoreCase( name ) == 0 )
                {
                    return col;
                }
            }

            return null;
        }
    }
