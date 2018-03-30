
    package de.christopherstock.shooter.game.objects;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import de.christopherstock.lib.LibTransformationMode;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.game.*;
    import  de.christopherstock.lib.math.*;
    import  de.christopherstock.lib.ui.*;
    import  de.christopherstock.shooter.*;
    import  de.christopherstock.shooter.ShooterSetting.ItemSettings;
    import  de.christopherstock.shooter.g3d.mesh.*;
    import  de.christopherstock.shooter.game.artefact.Artefact;
    import  de.christopherstock.shooter.game.artefact.firearm.*;
    import  de.christopherstock.shooter.ui.hud.*;

    /*******************************************************************************************************************
    *   An item being able to be picked up by the player.
    *******************************************************************************************************************/
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
        private LibRotating iIsRotating             = null;
        private                     float               iRotationZ              = 0.0f;
        private boolean             iRemove                 = false;
        private Artefact            iArtefact               = null;
        protected                   float               iDropTarget             = 0.0f;
        protected                   float               iDropBegin              = 0.0f;

        public ItemToPickUp( ItemKind aKind, Artefact aArtefact, float x, float y, float z, float aRotZ, LibRotating aIsRotating )
        {
            this.iKind = aKind;
            this.iArtefact = aArtefact;
            this.iAnchor = new LibVertex( x, y, z );
            this.iStartRotZ = aRotZ;
            this.iIsRotating = aIsRotating;
        }

        public final void draw()
        {
            if (this.iKind.iMeshFile != null )
            {
                //draw mesh
                this.iMesh.draw();
            }
            else
            {
                boolean drawDebugCircle = true;
                if ( drawDebugCircle )
                {
                    //draw debug shape
                    switch (this.iKind.iType )
                    {
                        case ECircle:
                        {
                            if ( ShooterDebug.DEBUG_DRAW_ITEM_CIRCLE )
                            {
                                //Debug.item.out( "drawing item .." );
                                new LibFaceEllipseFloor( ShooterDebug.face, null, DEBUG_COLOR, this.iAnchor.x, this.iAnchor.y, this.iAnchor.z, this.iKind.iRadius, this.iKind.iRadius, ShooterSetting.Performance.ELLIPSE_SEGMENTS ).draw();
                            }
                            break;
                        }
                    }
                }
            }
        }

        public final boolean shallBeRemoved()
        {
            return this.iRemove;
        }

        public final void animate()
        {
            if (this.iMesh != null )
            {
                //rotate if desired
                if (this.iIsRotating == LibRotating.EYes )
                {
                    this.iMesh.translateAndRotateXYZ(this.iAnchor.x, this.iAnchor.y, this.iAnchor.z, 0.0f, 0.0f, this.iRotationZ, null, LibTransformationMode.EOriginalsToTransformed );
                    this.iRotationZ += ItemSettings.SPEED_ROTATING;
                }

                //drop if desired
                if (this.iDropBegin > this.iDropTarget)
                {
                    this.iDropBegin -= ItemSettings.SPEED_FALLING;

                    //clip on target
                    if (this.iDropBegin <= this.iDropTarget)
                    {
                        this.iDropBegin = this.iDropTarget;

                        //turn to lying item
                        this.loadD3ds();
                    }
                    else
                    {
                        this.iMesh.translateAndRotateXYZ( 0.0f, 0.0f, -ItemSettings.SPEED_FALLING, 0.0f, 0.0f, 0.0f, null, LibTransformationMode.EOriginalsToOriginals );
                    }
                }
            }

            //check if collected by player
            this.checkPlayerCollision();
        }

        private void checkPlayerCollision()
        {
            //check collision of 2 circles ( easy  task.. )
            Area player = new Area( Shooter.game.engine.player.getCylinder().getCircle() );
            Ellipse2D.Float itemCircle = new Ellipse2D.Float(this.iAnchor.x - this.iKind.iRadius, this.iAnchor.y - this.iKind.iRadius, 2 * this.iKind.iRadius, 2 * this.iKind.iRadius );
            Area item   = new Area( itemCircle );
            player.intersect( item );
            if
            (
                    !player.isEmpty()
                &&  Shooter.game.engine.player.getCylinder().checkCollision(this.iAnchor.z )
            )
            {
                if ( !this.iCollisionWithPlayer)
                {
                    boolean assignAmmoToNewArtefact = false;
                    //check if player already holds this artefact
                    if (this.iArtefact != null )
                    {
                        if ( !Shooter.game.engine.player.iArtefactSet.contains(this.iArtefact) )
                        {
                            //ShooterDebug.major.out( "player has not this item" );
                            assignAmmoToNewArtefact = true;
                        }
                    }

                    //perform item event
                    for ( GameEvent event : this.iKind.iItemEvents )
                    {
                        event.perform( null );
                    }

                    //play sound fx
                    if (this.iKind.iPickupSound != null )
                    {
                        this.iKind.iPickupSound.playGlobalFx();
                    }

                    //give magazine ammo to player
                    if (this.iArtefact != null )
                    {
                        if ( assignAmmoToNewArtefact )
                        {
                            //give ammo to new artefact
                            //ShooterDebug.major.out( "firearm has ammo " + iArtefact.magazineAmmo );
                            Shooter.game.engine.player.iArtefactSet.assignMagazine(this.iArtefact);
                        }
                        else
                        {
                            if (this.iArtefact.artefactType.artefactKind instanceof FireArm )
                            {
                                //give ammo from magazine to stack
                                Shooter.game.engine.player.iAmmoSet.addAmmo( ( (FireArm) this.iArtefact.artefactType.artefactKind).ammoType, this.iArtefact.magazineAmmo);
                            }
                        }
                    }

                    //show hud message
                    if (this.iKind.iHudMessage != null )
                    {
                        //ShooterDebug.bugfix.out( "hud message launching" );
                        HUDMessageManager.getSingleton().showMessage(this.iKind.iHudMessage );
                    }
                }

                //mark as collected
                this.iCollisionWithPlayer = true;

                //check if single event
                if (this.iKind.iSingleEvent )
                {
                    this.iRemove = true;
                }
            }
            else
            {
                //release collision ( for repeated items )
                this.iCollisionWithPlayer = false;
            }
        }

        public final void loadD3ds()
        {
            if (this.iKind.iMeshFile != null )
            {
                this.iMesh = new Mesh( Shooter.game.engine.d3ds.getFaces(this.iKind.iMeshFile ), this.iAnchor, this.iStartRotZ, 1.0f, LibInvert.ENo, this, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );

                //translate rotating items to the meshe's center in order to rotate around the (fixed) anchor
                if (this.iIsRotating == LibRotating.EYes )
                {
                    Point2D.Float center = this.iMesh.getCenterPointXY();
                    this.iMesh.translate( -center.x, -center.y, 0.0f, LibTransformationMode.EOriginalsToOriginals );
                }
            }
        }

        public final void assignMesh( Mesh mesh )
        {
            this.iMesh = mesh; //new Mesh( mesh.getFaces(), anchor, iStartRotZ, 1.0f, LibInvert.ENo, this, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );
        }

        public final LibVertex getAnchor()
        {
            return this.iAnchor;
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

        public final void launchAction(LibCylinder cylinder, Object gadget, float faceAngle )
        {
            //actions have no effect on items
        }
    }
