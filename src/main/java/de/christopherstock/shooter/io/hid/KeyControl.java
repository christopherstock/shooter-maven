
    package de.christopherstock.shooter.io.hid;

    /*******************************************************************************************************************
    *   Control for one key enables key blocking after being pressed.
    *******************************************************************************************************************/
    public class KeyControl
    {
        public                          boolean         iKeyHold                = false;
        public                          boolean         iLaunchAction           = false;
        private long            iNextMillis             = 0;
        private long            iDelayAfterReload       = 0;

        public KeyControl( int aDelayAfterReload )
        {
            this.iDelayAfterReload = aDelayAfterReload;
        }

        public void checkLaunchingAction()
        {
            if (this.iKeyHold)
            {
                //check reload blocker
                if (this.iNextMillis <= System.currentTimeMillis() )
                {
                    //set timestamp for next allowed player action
                    this.iNextMillis = System.currentTimeMillis() + this.iDelayAfterReload;

                    //trigger the action
                    this.iLaunchAction = true;
                }
            }
        }
    }
