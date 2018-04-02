
    package de.christopherstock.shooter.game.objects;

    import  java.awt.geom.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
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

        private     static  final   LibColors           DEBUG_COLOR                 = LibColors.EGreen;

        private                     LibVertex           anchor                      = null;
        private                     ItemKind            kind                        = null;
        private                     boolean             collisionWithPlayer         = false;
        private                     Mesh                mesh                        = null;
        private                     float               startRotZ                   = 0.0f;
        private                     LibRotating         isRotating                  = null;
        private                     float               rotationZ                   = 0.0f;
        private                     boolean             remove                      = false;
        private                     Artefact            artefact                    = null;
        protected                   float               dropTarget                  = 0.0f;
        protected                   float               dropBegin                   = 0.0f;

        public ItemToPickUp( ItemKind kind, Artefact artefact, float x, float y, float z, float rotZ, LibRotating isRotating )
        {
            this.kind       = kind;
            this.artefact   = artefact;
            this.anchor     = new LibVertex( x, y, z );
            this.startRotZ  = rotZ;
            this.isRotating = isRotating;
        }

        public final void draw()
        {
            if (this.kind.meshFile != null )
            {
                //draw mesh
                this.mesh.draw();
            }
            else
            {
                boolean drawDebugCircle = true;
                if ( drawDebugCircle )
                {
                    //draw debug shape
                    switch (this.kind.type)
                    {
                        case ECircle:
                        {
                            if ( ShooterDebug.DEBUG_DRAW_ITEM_CIRCLE )
                            {
                                //Debug.item.out( "drawing item .." );
                                new LibFaceEllipseFloor( ShooterDebug.face, null, DEBUG_COLOR, this.anchor.x, this.anchor.y, this.anchor.z, this.kind.radius, this.kind.radius, ShooterSetting.Performance.ELLIPSE_SEGMENTS ).draw();
                            }
                            break;
                        }
                    }
                }
            }
        }

        public final boolean shallBeRemoved()
        {
            return this.remove;
        }

        public final void animate()
        {
            if (this.mesh != null )
            {
                //rotate if desired
                if (this.isRotating == LibRotating.EYes )
                {
                    this.mesh.translateAndRotateXYZ(this.anchor.x, this.anchor.y, this.anchor.z, 0.0f, 0.0f, this.rotationZ, null, LibTransformationMode.EOriginalsToTransformed );
                    this.rotationZ += ItemSettings.SPEED_ROTATING;
                }

                //drop if desired
                if (this.dropBegin > this.dropTarget)
                {
                    this.dropBegin -= ItemSettings.SPEED_FALLING;

                    //clip on target
                    if (this.dropBegin <= this.dropTarget)
                    {
                        this.dropBegin = this.dropTarget;

                        //turn to lying item
                        this.loadD3ds();
                    }
                    else
                    {
                        this.mesh.translateAndRotateXYZ( 0.0f, 0.0f, -ItemSettings.SPEED_FALLING, 0.0f, 0.0f, 0.0f, null, LibTransformationMode.EOriginalsToOriginals );
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
            Ellipse2D.Float itemCircle = new Ellipse2D.Float(this.anchor.x - this.kind.radius, this.anchor.y - this.kind.radius, 2 * this.kind.radius, 2 * this.kind.radius);
            Area item   = new Area( itemCircle );
            player.intersect( item );
            if
            (
                    !player.isEmpty()
                &&  Shooter.game.engine.player.getCylinder().checkCollision(this.anchor.z )
            )
            {
                if ( !this.collisionWithPlayer)
                {
                    boolean assignAmmoToNewArtefact = false;
                    //check if player already holds this artefact
                    if (this.artefact != null )
                    {
                        if ( !Shooter.game.engine.player.artefactSet.contains(this.artefact) )
                        {
                            //ShooterDebug.major.out( "player has not this item" );
                            assignAmmoToNewArtefact = true;
                        }
                    }

                    //perform item event
                    for ( GameEvent event : this.kind.itemEvents)
                    {
                        event.perform( null );
                    }

                    //play sound fx
                    if (this.kind.pickupSound != null )
                    {
                        this.kind.pickupSound.playGlobalFx();
                    }

                    //give magazine ammo to player
                    if (this.artefact != null )
                    {
                        if ( assignAmmoToNewArtefact )
                        {
                            //give ammo to new artefact
                            //ShooterDebug.major.out( "firearm has ammo " + artefact.magazineAmmo );
                            Shooter.game.engine.player.artefactSet.assignMagazine(this.artefact);
                        }
                        else
                        {
                            if (this.artefact.artefactType.artefactKind instanceof FireArm )
                            {
                                //give ammo from magazine to stack
                                Shooter.game.engine.player.ammoSet.addAmmo( ( (FireArm) this.artefact.artefactType.artefactKind).ammoType, this.artefact.magazineAmmo);
                            }
                        }
                    }

                    //show hud message
                    if (this.kind.hudMessage != null )
                    {
                        //ShooterDebug.bugfix.out( "hud message launching" );
                        Shooter.game.engine.hudMessagesManager.showMessage(this.kind.hudMessage);
                    }
                }

                //mark as collected
                this.collisionWithPlayer = true;

                //check if single event
                if (this.kind.singleEvent)
                {
                    this.remove = true;
                }
            }
            else
            {
                //release collision ( for repeated items )
                this.collisionWithPlayer = false;
            }
        }

        public final void loadD3ds()
        {
            if (this.kind.meshFile != null )
            {
                this.mesh = new Mesh( Shooter.game.engine.d3ds.getFaces(this.kind.meshFile), this.anchor, this.startRotZ, 1.0f, LibInvert.ENo, this, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );

                //translate rotating items to the meshe's center in order to rotate around the (fixed) anchor
                if (this.isRotating == LibRotating.EYes )
                {
                    Point2D.Float center = this.mesh.getCenterPointXY();
                    this.mesh.translate( -center.x, -center.y, 0.0f, LibTransformationMode.EOriginalsToOriginals );
                }
            }
        }

        public final void assignMesh( Mesh mesh )
        {
            this.mesh = mesh; //new Mesh( mesh.getFaces(), anchor, startRotZ, 1.0f, LibInvert.ENo, this, LibTransformationMode.EOriginalsToTransformed, DrawMethod.EAlwaysDraw );
        }

        public final LibVertex getAnchor()
        {
            return this.anchor;
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
