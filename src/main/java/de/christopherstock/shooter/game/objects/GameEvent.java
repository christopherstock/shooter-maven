
    package de.christopherstock.shooter.game.objects;

    /*******************************************************************************************************************
    *   An event being invoked - not only by picking up items but on level start etc.
    *******************************************************************************************************************/
    public interface GameEvent
    {
        void perform( Bot bot );
    }
