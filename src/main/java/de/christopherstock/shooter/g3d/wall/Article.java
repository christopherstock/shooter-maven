
    package de.christopherstock.shooter.g3d.wall;

    import  de.christopherstock.lib.Lib.Invert;
    import  de.christopherstock.lib.Lib.Scalation;
    import  de.christopherstock.lib.fx.LibFX.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.g3d.face.LibFace.*;
    import  de.christopherstock.lib.io.d3ds.LibD3dsFile;
    import  de.christopherstock.shooter.base.ShooterTexture.WallTex;
    import  de.christopherstock.shooter.io.sound.*;

    /**************************************************************************************
    *   Represents a mesh with all faces collision enabled.
    **************************************************************************************/
    public class Article extends Wall
    {
        private     static  final   long    serialVersionUID        = -896222659347000332L;

        public Article( LibD3dsFile file, float x, float y, float z, float rotZ, Scalation scale, Invert invert, WallTex tex, WallHealth health, FXSize size, SoundFg explosionSound )
        {
            super( file, new LibVertex( x, y, z ), rotZ, scale, invert, WallCollidable.EYes, WallAction.ENone, WallClimbable.ENo, DrawMethod.EAlwaysDraw, tex, null, 0, health, size, explosionSound );
        }
    }
