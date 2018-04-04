
    package de.christopherstock.lib.gl;

    import  de.christopherstock.lib.LibDebug;
    import  de.christopherstock.lib.g3d.LibVertex;
    import  de.christopherstock.lib.game.LibGameObject;

    /*******************************************************************************************************************
    *   Implemented by an Object to determine if it's climbable or not. 
    *
    *   TODO prune!
    *******************************************************************************************************************/
    public interface LibFloorStack
    {
        Float getHighestFloor
        (
            LibGameObject parentGameObject,
            LibVertex     anchor,
            float         radius,
            float         height,
            int           collisionCheckingSteps,
            LibDebug      debug,
            boolean       debugDrawBotCircles,
            float         bottomCollisionToleranceZ,
            float         minBottomCollisionToleranceZ,
            int           ellipseSegments,
            Object        exclude
        );
    }
