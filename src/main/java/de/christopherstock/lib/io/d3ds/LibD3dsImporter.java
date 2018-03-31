
    package de.christopherstock.lib.io.d3ds;

    import  java.io.*;
    import  java.util.*;
    import  de.christopherstock.lib.*;
    import  de.christopherstock.lib.g3d.*;
    import  de.christopherstock.lib.io.*;
    import  de.christopherstock.lib.ui.*;

    /*******************************************************************************************************************
    *   A 3d studio max ascii scene export file importer using regular expressions.
    *   Switched from enum to ordinary class in order to deliver instances of the meshes.
    *******************************************************************************************************************/
    public class LibD3dsImporter
    {
        /***************************************************************************************************************
        *   All vertex-coordinates from the .ase-file are DIVIDED
        *   by this factor during import.
        ***************************************************************************************************************/
        private     static  final   int                 POINTS_SCALATION            = 10;

        /***************************************************************************************************************
        *   Warning on loading d3ds-files if the ase file specifies more than this number of faces.
        *   No more low poly model.
        ***************************************************************************************************************/
        private     static  final   int                 MAX_FACES                   = 3000;

        private                     String              filename                    = null;
        private                     LibDebug            debug                       = null;
        private                     LibMaxTriangle[]    faces                       = null;
        private                     LibMaxMaterial[]    materials3ds                = null;

        public LibD3dsImporter( String filename, LibDebug debug )
        {
            this.filename = filename;
            this.debug    = debug;

            this.debug.out( "=======================================" );
            this.debug.out( "Parsing 3dsmax-file [" + this.filename + "]" );

            //open the AsciiSceneImport-file and the output-file
            try
            {
                //read the file
                String      fileSrc = this.readAse();

                //pick and parse materials
                String      chunkMaterialList   = LibStrings.getViaRegEx( fileSrc, "\\*MATERIAL_LIST \\{.+?\\n\\}" )[ 0 ];
                this.parseMaterials( chunkMaterialList );

                //pick and parse meshes
                String[]    chunksGeomObjects   = LibStrings.getViaRegEx( fileSrc, "\\*GEOMOBJECT \\{.+?\\n\\}"    );
                this.parseMeshes( chunksGeomObjects );

                //warning if more than max faces
                if (this.faces.length > MAX_FACES )
                {
                    this.debug.err( "WARNING! 3dsmax-file [" + this.filename + "] specifies more than [" + MAX_FACES + "] faces - [" + this.faces.length + "] faces defined" );
                }
            }
            catch( Exception e )
            {
                this.debug.err( "ERROR on loading d3ds resource [" + this.filename + "]" );
                this.debug.trace( e );
                System.exit( 0 );
            }
        }

        private String readAse()
        {
            try
            {
                InputStream is = LibIO.preStreamJarResource(this.filename);

                //check if file could be found
                if ( is == null )
                {
                    throw new IOException( "FATAL! resource [" + this.filename + "] not found" );
                }

                BufferedReader  inStream = new BufferedReader( new InputStreamReader( is ) );
                StringBuilder    sb       = new StringBuilder();
                while ( true )
                {
                    String l = inStream.readLine();
                    if ( l == null ) break;
                    sb.append( l     );
                    sb.append(  "\n" );
                }
                String ret = sb.toString();
                inStream.close();
                return ret;
            }
            catch ( Exception ioe )
            {
                this.debug.err( "ERROR loading 3ds max file [" + this.filename + "]" );
                this.debug.trace( ioe );
                System.exit( 0 );

                return null;
            }
        }

        private void parseMaterials( String src )
        {
            //check if materials are defined
            String[] chunksMaterials = LibStrings.getViaRegEx( src, "\\t\\*MATERIAL \\d+ \\{.+?\\n\\t\\}" );
            if ( chunksMaterials != null )
            {
                //browse all material-chunks and assign all materials
                this.materials3ds = new LibMaxMaterial[ chunksMaterials.length ];
                for ( int i = 0; i < chunksMaterials.length; ++i )
                {
                    //get material name ( mandatory )
                    String[][]  materialNameAa  = LibStrings.getViaRegExGrouped( chunksMaterials[ i ], 1, "\\*MATERIAL_NAME \"([^\"]+)\"" );
                    String      materialName    = materialNameAa[ 0 ][ 0 ];
                    LibColors   materialColor   = LibColors.getByName( materialName );

                    //get material offsets ( optional )
                    String[][]  materialOffsetUaa = LibStrings.getViaRegExGrouped( chunksMaterials[ i ], 1, "\\*UVW_U_OFFSET ([\\-\\d\\.]+)" );
                    float       materialOffsetU   = ( materialOffsetUaa == null ? 0.0f : Float.parseFloat( materialOffsetUaa[ 0 ][ 0 ] ) );
                    String[][]  materialOffsetVaa = LibStrings.getViaRegExGrouped( chunksMaterials[ i ], 1, "\\*UVW_V_OFFSET ([\\-\\d\\.]+)" );
                    float       materialOffsetV   = ( materialOffsetVaa == null ? 0.0f : Float.parseFloat( materialOffsetVaa[ 0 ][ 0 ] ) );

                    //get material tiling ( optional )
                    String[][]  materialTilingUaa = LibStrings.getViaRegExGrouped( chunksMaterials[ i ], 1, "\\*UVW_U_TILING ([\\-\\d\\.]+)" );
                    float       materialTilingU   = ( materialTilingUaa == null ? 1.0f : Float.parseFloat( materialTilingUaa[ 0 ][ 0 ] ) );
                    String[][]  materialTilingVaa = LibStrings.getViaRegExGrouped( chunksMaterials[ i ], 1, "\\*UVW_V_TILING ([\\-\\d\\.]+)" );
                    float       materialTilingV   = ( materialTilingVaa == null ? 1.0f : Float.parseFloat( materialTilingVaa[ 0 ][ 0 ] ) );

                    //assign material
                    this.materials3ds[ i ] = new LibMaxMaterial( materialName, materialColor, materialOffsetU, materialOffsetV, materialTilingU, materialTilingV  );
                    this.debug.out( "Material [" + i + "]: [" + this.materials3ds[ i ].name + "][" + this.materials3ds[ i ].offsetU + "][" + this.materials3ds[ i ].offsetV + "][" + this.materials3ds[ i ].tilingU + "][" + this.materials3ds[ i ].tilingV + "]" );
                }
            }
        }

        private void parseMeshes( String[] meshesSrc )
        {
            this.debug.out( "meshes to parse: ["+meshesSrc.length+"]" );

            Vector<LibMaxTriangle>      allFaces                = new Vector<LibMaxTriangle>();
            LibMaxVertex[]              vertices3ds             = null;
            LibMaxFace[]                faces3ds                = null;
            LibMaxTextureVertex[]       textureVertices3ds      = null;

            //browse all meshes
            int cur = 0;
            for ( String meshSrc : meshesSrc )
            {
                //BufferedReader inStream = new BufferedReader( new StringReader( chunksGeomObjects[ i ] ) );

                //next texture!
                ++cur;
                this.debug.out( "\nImporting mesh # [" + cur + "]" );

                //get number of vertices
                String[][]  numVerticesAA   = LibStrings.getViaRegExGrouped( meshSrc, 1, "\\*MESH_NUMVERTEX (\\d+)" );
                int         numVertices     = Integer.parseInt( numVerticesAA[ 0 ][ 0 ] );
                            vertices3ds     = new LibMaxVertex[ numVertices ];
                this.debug.out( "number of vertices: [" + numVertices + "]" );

                //get number of faces
                String[][]  numFacesAA      = LibStrings.getViaRegExGrouped( meshSrc, 1, "\\*MESH_NUMFACES (\\d+)" );
                int         numFaces        = Integer.parseInt( numFacesAA[ 0 ][ 0 ] );
                            faces3ds        = new LibMaxFace[ numFaces ];
                this.debug.out( "number of faces: [" + numFaces + "]" );

                //get number of texture-vertices
                String[][]  numTVerticesAA      = LibStrings.getViaRegExGrouped( meshSrc, 1, "\\*MESH_NUMTVERTEX (\\d+)" );
                int         numTVertices        = Integer.parseInt( numTVerticesAA[ 0 ][ 0 ] );
                            textureVertices3ds  = new LibMaxTextureVertex[ numTVertices ];
                this.debug.out( "number of texture-vertices: [" + numTVertices + "]" );

                //read all vertices
                String[][]  verticesAA      = LibStrings.getViaRegExGrouped( meshSrc, 3, "\\*MESH_VERTEX\\s+\\d+\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)\\n" );
                this.debug.out( "parsing [" + verticesAA.length + "] vertices.." );
                //assign them
                for ( int i = 0; i < verticesAA.length; ++i )
                {
                    //assign all vertices
                    vertices3ds[ i ] = new LibMaxVertex
                    (
                        Float.parseFloat( verticesAA[ i ][ 0 ] ) / POINTS_SCALATION,
                        Float.parseFloat( verticesAA[ i ][ 1 ] ) / POINTS_SCALATION,
                        Float.parseFloat( verticesAA[ i ][ 2 ] ) / POINTS_SCALATION
                    );
                    //Debug.d3dsRegEx.out( "[" + verticesAA[ i ][ 0 ] + "][" + verticesAA[ 0 ][ 1 ] + "][" + verticesAA[ 0 ][ 2 ] + "]" );
                }

                //read all face normals ( optional )
                String[][]  facesNormalsAA  = LibStrings.getViaRegExGrouped( meshSrc, 4, "\\*MESH_FACENORMAL\\s+(\\d+)\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)" );
                LibMaxVertex[] facesNormals    = null;
                if ( facesNormalsAA != null )
                {
                    facesNormals = new LibMaxVertex[ facesNormalsAA.length ];

                    for ( int i = 0; i < facesNormalsAA.length; ++i )
                    {
                        facesNormals[ i ] = new LibMaxVertex
                        (
                            Float.parseFloat( facesNormalsAA[ i ][ 1 ] ),
                            Float.parseFloat( facesNormalsAA[ i ][ 2 ] ),
                            Float.parseFloat( facesNormalsAA[ i ][ 3 ] )
                        );
                    }

                    this.debug.out( "parsed [" + facesNormalsAA.length + "] face-normals" );
                }

                //read all faces
                String[][]  facesAA         = LibStrings.getViaRegExGrouped( meshSrc, 4, "\\*MESH_FACE\\s+(\\d+)\\:\\s+A\\:\\s+([\\d\\.\\-]+)\\s+B\\:\\s+([\\d\\.\\-]+)\\s+C\\:\\s+([\\d\\.\\-]+)" );
                this.debug.out( "parsing [" + facesAA.length + "] faces.." );
                //assign them
                for ( int i = 0; i < facesAA.length; ++i )
                {
                    //assign all faces
                    faces3ds[ i ] = new LibMaxFace
                    (
                        ( facesNormals == null ? null : facesNormals[ i ] ),
                        vertices3ds[ Integer.parseInt( facesAA[ i ][ 1 ] ) ],
                        vertices3ds[ Integer.parseInt( facesAA[ i ][ 2 ] ) ],
                        vertices3ds[ Integer.parseInt( facesAA[ i ][ 3 ] ) ]
                    );
                }

                //read all texture-vertices
                String[][]  textureVerticesAA      = LibStrings.getViaRegExGrouped( meshSrc, 3, "\\*MESH_TVERT\\s+\\d+\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)\\n" );
                if ( textureVerticesAA != null )
                {
                    this.debug.out( "parsing [" + textureVerticesAA.length + "] texture-vertices.." );
                    //assign them
                    for ( int i = 0; i < textureVerticesAA.length; ++i )
                    {
                        //assign all vertices
                        textureVertices3ds[ i ] = new LibMaxTextureVertex
                        (
                            Float.parseFloat( textureVerticesAA[ i ][ 0 ] ),
                            Float.parseFloat( textureVerticesAA[ i ][ 1 ] )
                        );
                        //Debug.d3dsRegEx.out( "[" + textureVerticesAA[ i ][ 0 ] + "][" + textureVerticesAA[ i ][ 1 ] + "][" + textureVerticesAA[ i ][ 2 ] + "]" );
                    }
                }

                //read all texture-faces and assign them
                String[][]  textureFacesAA         = LibStrings.getViaRegExGrouped( meshSrc, 3, "\\*MESH_TFACE\\s+\\d+\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)\\s+([\\d\\.\\-]+)\\n" );
                if ( textureFacesAA != null )
                {
                    this.debug.out( "parsing [" + textureFacesAA.length + "] texture-faces.." );
                    for ( int currentTextureFace = 0; currentTextureFace < faces3ds.length; ++currentTextureFace )
                    {
                        //assign all texture-faces
                        faces3ds[ currentTextureFace ].vertex1.u = textureVertices3ds[ Integer.parseInt( textureFacesAA[ currentTextureFace ][ 0 ] ) ].u;
                        faces3ds[ currentTextureFace ].vertex1.v = textureVertices3ds[ Integer.parseInt( textureFacesAA[ currentTextureFace ][ 0 ] ) ].v;
                        faces3ds[ currentTextureFace ].vertex2.u = textureVertices3ds[ Integer.parseInt( textureFacesAA[ currentTextureFace ][ 1 ] ) ].u;
                        faces3ds[ currentTextureFace ].vertex2.v = textureVertices3ds[ Integer.parseInt( textureFacesAA[ currentTextureFace ][ 1 ] ) ].v;
                        faces3ds[ currentTextureFace ].vertex3.u = textureVertices3ds[ Integer.parseInt( textureFacesAA[ currentTextureFace ][ 2 ] ) ].u;
                        faces3ds[ currentTextureFace ].vertex3.v = textureVertices3ds[ Integer.parseInt( textureFacesAA[ currentTextureFace ][ 2 ] ) ].v;
                    }
                }

                //set material used if an error occurs on mapping textures ..
                LibMaxMaterial material = new LibMaxMaterial( null, LibColors.ERed, 0.0f, 0.0f, 1.0f, 1.0f );

                //get number of texture-vertices
                String[][]  materialRefAA       = LibStrings.getViaRegExGrouped( meshSrc, 1, "\\*MATERIAL_REF (\\d+)" );
                if ( materialRefAA != null )
                {
                    int         materialRef         = Integer.parseInt( materialRefAA[ 0 ][ 0 ] );
                    material = this.materials3ds[ materialRef ];
                    this.debug.out( "material ref is: [" + materialRef + "]" );
                }
                else
                {
                    this.debug.out( "This mesh has no material." );
                }

                this.debug.out( "picked texture: " + material.name );

                //add all faces to the vector
                for( LibMaxFace face : faces3ds )
                {
                    //Debug.d3dsRegEx.out( "CREATE FACE!" + material.name );
                    try
                    {
                        //create free-triangle and add it to the faces stack
                        LibMaxTriangle ft = new LibMaxTriangle
                        (
                            new LibVertex( 0.0f, 0.0f, 0.0f ),
                            material.name,
                            material.color,
                            new LibVertex ( face.vertex1.y,     face.vertex1.x,     face.vertex1.z, ( face.vertex1.u + material.offsetU ) * material.tilingU, ( face.vertex1.v + material.offsetV ) * material.tilingV ),
                            new LibVertex ( face.vertex2.y,     face.vertex2.x,     face.vertex2.z, ( face.vertex2.u + material.offsetU ) * material.tilingU, ( face.vertex2.v + material.offsetV ) * material.tilingV ),
                            new LibVertex ( face.vertex3.y,     face.vertex3.x,     face.vertex3.z, ( face.vertex3.u + material.offsetU ) * material.tilingU, ( face.vertex3.v + material.offsetV ) * material.tilingV ),
                            ( face.faceNormal == null ? null : new LibVertex ( face.faceNormal.y, face.faceNormal.x, face.faceNormal.z  ) )
                        );
                        allFaces.add( ft );
                    }
                    catch ( Exception ioe )
                    {
                        this.debug.err( "I/O Exception on writing parsed ASE-File!" );
                        this.debug.trace( ioe );
                    }
                }
            }

            //convert all faces from vector to array
            this.faces = allFaces.toArray( new LibMaxTriangle[] {} );

            //done
            this.debug.out( "done" );
        }

        public final LibMaxTriangle[] getFaces()
        {
            return this.faces;
        }
    }
