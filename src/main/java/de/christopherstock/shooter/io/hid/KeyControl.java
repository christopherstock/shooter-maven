
    package de.christopherstock.shooter.io.hid;

    /*******************************************************************************************************************
    *   Control for one key enables key blocking after being pressed.
    *******************************************************************************************************************/
    public class KeyControl
    {
        public                          boolean         keyHold                     = false;
        public                          boolean         launchAction                = false;
        private                         long            nextMillis                  = 0;
        private                         long            delayAfterReload            = 0;

        public KeyControl( int delayAfterReload )
        {
            this.delayAfterReload = delayAfterReload;
        }

        public void checkLaunchingAction()
        {
            if (this.keyHold)
            {
                //check reload blocker
                if (this.nextMillis <= System.currentTimeMillis() )
                {
                    //set timestamp for next allowed player action
                    this.nextMillis = System.currentTimeMillis() + this.delayAfterReload;

                    //trigger the action
                    this.launchAction = true;
                }
            }
        }
    }
