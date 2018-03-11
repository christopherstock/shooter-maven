/*  $Id: GameEvent.java 1240 2013-01-02 14:43:47Z jenetic.bytemare@googlemail.com $
 *  =======================================================================================
 */
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
