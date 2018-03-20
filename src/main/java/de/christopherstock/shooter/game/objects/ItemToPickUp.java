
    package de.christopherstock.shooter.game.objects;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.Lib.Invert;
    import  de.christopherstock.lib.Lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSettings.ItemSettings;
    import  de.christopherstock.shooter.base.*;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import de.christopherstock.shooter.game.artefact.Artefact;
    import de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.level.*;
import  de.christopherstock.shooter.ui.hud.*;

    /**************************************************************************************
    *   An item being able to be picked up by the player.
    *
    *   @author     Christopher Stock
    *   @version    0.3.11
    **************************************************************************************/
    public class ItemToPickUp implements LibGameObject
    {
        public static enum ItemType
        {
            ECircle,
            ;
        }

        private     static  final   LibColors           DEBUG_COLOR         = LibColors.EGreen;

        private                     LibVertex           iAnchor                 = null;
        private                     ItemKind            iKind                   = null;
        private                     boolean             iCollisionWithPlayer    = false;
        private                     Mesh                iMesh                   = null;
        private                     float               iStartRotZ              = 0.0f;
        private                     Lib.Rotating        iIsRotating             = null;
        private                     float               iRotationZ              = 0.0f;
        protected                   boolean             iRemove                 = false;
        protected                   Artefact            iArtefact               = null;
        protected                   float               iDropTarget             = 0.0f;
        protected                   float               iDropBegin              = 0.0f;

        public ItemToPickUp( ItemKind aKind, Artefact aArtefact, float x, float y, float z, float aRotZ, Lib.Rotating aIsRotating )
        {
            iKind       = aKind;
            iArtefact   = aArtefact;
            iAnchor     = new LibVertex( x, y, z );
            iStartRotZ  = aRotZ;
            iIsRotating = aIsRotating;
        }

        public final void draw()
        {
            if ( iKind.iMeshFile != null )
            {
                //draw mesh
                iMesh.draw();
            }
            else
            {
                boolean drawDebugCircle = true;
                if ( drawDebugCircle )
                {
                    //draw debug shape
                    switch ( iKind.iType )
                    {
                        case ECircle:
                        {
                            if ( ShooterDebug.DEBUG_DRAW_ITEM_CIRCLE )
                            {
                                //Debug.item.out( "drawing item .." );
                                new LibFaceEllipseFloor( ShooterDebug.face, null, DEBUG_COLOR, iAnchor.x, iAnchor.y, iAnchor.z, iKind.iRadius, iKind.iRadius, ShooterSettings.Performance.ELLIPSE_SEGMENTS ).draw();
                            }
                            break;
                        }
                    }
                }
            }
        }

        public final boolean shallBeRemoved()
        {
            return iRemove;
        }

        public final void animate()
        {
            if ( iMesh != null )
            {
                //rotate if desired
                if ( iIsRotating == Lib.Rotating.EYes )
                {
                    iMesh.translateAndRotateXYZ( iAnchor.x, iAnchor.y, iAnchor.z, 0.0f, 0.0f, iRotationZ, null, LibTransformationMode.EOriginalsToTransformed );
                    iRotationZ += ItemSettings.SPEED_ROTATING;
                }

                //drop if desired
                if ( iDropBegin > iDropTarget )
                {
                    iDropBegin -= ItemSettings.SPEED_FALLING;

                    //clip on target
                    if ( iDropBegin <= iDropTarget )
                    {
                        iDropBegin = iDropTarget;

                        //turn to lying item
                        loadD3ds();
                    }
                    else
                    {
                        iMesh.translateAndRotateXYZ( 0.0f, 0.0f, -ItemSettings.SPEED_FALLING, 0.0f, 0.0f, 0.0f, null, LibTransformationMode.EOriginalsToOriginals );
                    }
                }
            }

            //check if collected by player
            checkPlayerCollision();
        }

        private final void checkPlayerCollision()
        {
            //check collision of 2 circles ( easy  task.. )
            Area player = new Area( Level.currentPlayer().getCylinder().getCircle() );
            Ellipse2D.Float itemCircle = new Ellipse2D.Float( iAnchor.x - iKind.iRadius, iAnchor.y - iKind.iRadius, 2 * iKind.iRadius, 2 * iKind.iRadius );
            Area item   = new Area( itemCircle );
            player.intersect( item );
            if
            (
                    !player.isEmpty()
                &&  Level.currentPlayer().getCylinder().checkCollision( iAnchor.z )
            )
            {
                if ( !iCollisionWithPlayer )
                {
                    boolean assignAmmoToNewArtefact = false;
                    //check if player already holds this artefact
                    if ( iArtefact != null )
                    {
                        if ( !Level.currentPlayer().iArtefactSet.contains( iArtefact ) )
                        {
                            //ShooterDebug.major.out( "player has not this item" );
                            assignAmmoToNewArtefact = true;
                        }
                    }

                    //perform item event
                    for ( GameEvent event : iKind.iItemEvents )
                    {
                        event.perform( null );
                    }

                    //play sound fx
                    if ( iKind.iPickupSound != null )
                    {
                        iKind.iPickupSound.playGlobalFx();
                    }

                    //give magazine ammo to player
                    if ( iArtefact != null )
                    {
                        if ( assignAmmoToNewArtefact )
                        {
                            //give ammo to new artefact
                            //ShooterDebug.major.out( "firearm has ammo " + iArtefact.iMagazineAmmo );
                            Level.currentPlayer().iArtefactSet.assignMagazine( iArtefact );
                        }
                        else
                        {
                            if ( iArtefact.iArtefactType.iArtefactKind instanceof FireArm )
                            {
                                //give ammo from magazine to stack
                                Level.currentPlayer().iAmmoSet.addAmmo( ( (FireArm)iArtefact.iArtefactType.iArtefactKind ).iAmmoType, iArtefact.iMagazineAmmo );
                            }
                        }
                    }

                    //show hud message
                    if ( iKind.iHudMessage != null )
                    {
                        //ShooterDebug.bugfix.out( "hud message launching" );
                        HUDMessageManager.getSingleton().showMessage( iKind.iHudMessage );
                    }
                }

                //mark as collected
                iCollisionWithPlayer = true;

                //check if single event
                if ( iKind.iSingleEvent )
                {
                    iRemove = true;
                }
            }
            else
            {
                //release collision ( for repeated items )
                iCollisionWithPlayer = false;
            }
        }

        public final void loadD3ds()
        {
            if ( iKind.iMeshFile != null )
            {
                iMesh = new Mesh( ShooterD3ds.getFaces( iKind.iMeshFile ), iAnchor, iStartRotZ, 1.0f, Invert.ENo, this, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );

                //translate rotating items to the meshe's center in order to rotate around the (fixed) anchor
                if ( iIsRotating == Lib.Rotating.EYes )
                {
                    Point2D.Float center = iMesh.getCenterPointXY();
                    iMesh.translate( -center.x, -center.y, 0.0f, LibTransformationMode.EOriginalsToOriginals );
                }
            }
        }

        public final void assignMesh( Mesh mesh )
        {
            iMesh = mesh; //new Mesh( mesh.getFaces(), iAnchor, iStartRotZ, 1.0f, Invert.ENo, this, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );
        }

        public final LibVertex getAnchor()
        {
            return iAnchor;
        }

        public final float getCarriersFaceAngle()
        {
            return 0.0f;
        }

        public final HitPointCarrier getHitPointCarrier()
        {
            return null;
        }

        public final Vector<LibHitPoint> launchShot( LibShot s )
        {
            return null;
        }

        public final void launchAction( LibCylinder aCylinder, Object gadget, float faceAngle )
        {
            //actions have no effect on items
        }
    }
