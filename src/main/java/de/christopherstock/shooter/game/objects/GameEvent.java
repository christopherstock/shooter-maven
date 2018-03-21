
    package de.christopherstock.shooter.game.objects;

    /**************************************************************************************
    *   An event being invoked - not only by picking up items but on level start etc.
    **************************************************************************************/
    public abstract interface GameEvent
    {
        public abstract void perform( Bot bot );
    }
