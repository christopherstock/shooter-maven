
    package de.christopherstock.shooter.game.artefact.firearm;

    import  java.util.*;

    /*******************************************************************************************************************
    *   Represents the ammunition for the player, a bot or a device.
    *******************************************************************************************************************/
    public class AmmoSet
    {
        private Hashtable<AmmoType,Integer>     ammo                    = null;

        public AmmoSet()
        {
            this.ammo = new Hashtable<AmmoType,Integer>();

            for ( AmmoType at : AmmoType.values() )
            {
                this.ammo.put( at, 0 );
            }
        }

        public int getAmmo( AmmoType at )
        {
            return this.ammo.get(at);
        }

        public void substractAmmo( AmmoType at, int substraction )
        {
            int oldAmmo = this.ammo.get(at);
            oldAmmo -= substraction;
            this.ammo.put( at, oldAmmo );
        }

        public void addAmmo( AmmoType at, int addition )
        {
            int oldAmmo = this.ammo.get(at);
            oldAmmo += addition;
            if ( oldAmmo > at.maxAmmo) oldAmmo = at.maxAmmo;
            this.ammo.put( at, oldAmmo );
        }

        public static void loadImages()
        {
            for ( AmmoType ammo : AmmoType.values() )
            {
                ammo.loadImage();
            }
        }
    }
