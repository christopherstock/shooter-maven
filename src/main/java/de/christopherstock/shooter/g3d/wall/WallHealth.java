/*  $Id: WallHealth.java 1224 2012-12-31 12:32:24Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
    package de.christopherstock.shooter.g3d.wall;

    /**************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public enum WallHealth
    {
        EUnbreakale(        0   ),
        EGlass(             15  ),
        EWoodBrittle(       10  ),
        EWoodNormal(        15  ),
        EElectricalDevice(  15  ),
        ESolidGlass(        20  ),
        ESolidWood(         25  ),
        EVendingMachine(    25  ),
        ECrate(             30  ),
        EFurniture(         50  ),
        ;

        private     int     iHealth     = 0;

        private WallHealth( int aHealth )
        {
            iHealth = aHealth;
        }

        public int getHealth()
        {
            return iHealth;
        }
    }
