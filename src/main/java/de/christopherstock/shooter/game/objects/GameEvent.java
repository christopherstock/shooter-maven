
    package de.christopherstock.shooter.game.objects;

    /**************************************************************************************
    *   An event being invoked - not only by picking up items but on level start etc.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public abstract interface GameEvent
    {
        public abstract void perform( Bot bot );
    }
