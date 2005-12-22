package com.planet_ink.coffee_mud.Libraries;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.planet_ink.coffee_mud.Libraries.interfaces.TextEncoders;
import com.planet_ink.coffee_mud.core.Log;

/* 
   Copyright 2000-2006 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class CMEncoder extends StdLibrary implements TextEncoders
{
    public String ID(){return "CMEncoder";}
    private byte[] encodeBuffer = new byte[65536];
    private Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
    private Inflater decompresser = new Inflater();
    /* Base 64 Encoding stuff */
    private static byte[] ALPHABET;
    
    public CMEncoder()
    {
        super();
        byte[] __bytes;
        try
        {
            __bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes( PREFERRED_ENCODING );
        }   // end try
        catch (java.io.UnsupportedEncodingException use)
        {
            __bytes = _NATIVE_ALPHABET; // Fall back to native encoding
        }   // end catch
        if(ALPHABET==null) ALPHABET = __bytes;
    }

    public String decompressString(byte[] b)
    {
        try
        {
            if ((b == null)||(b.length==0)) return "";

            decompresser.reset();
            decompresser.setInput(b);

            synchronized (encodeBuffer)
            {
                int len = decompresser.inflate(encodeBuffer);
                return new String(encodeBuffer, 0, len, "UTF-8");
            }
        }
        catch (Exception ex)
        {
            Log.errOut("MUD", "Error occur during decompression.");
            encodeBuffer=new byte[65536];
            return "";
        }
    }

    public byte[] compressString(String s)
    {
        byte[] result = null;

        try
        {
            compresser.reset();
            compresser.setInput(s.getBytes("UTF-8"));
            compresser.finish();
            
            synchronized (encodeBuffer)
            {
                if(s.length()>encodeBuffer.length)
                    encodeBuffer=new byte[s.length()];
                encodeBuffer[0]=0;

                int len = compresser.deflate(encodeBuffer);
                result = new byte[len];
                System.arraycopy(encodeBuffer, 0, result, 0, len);
            }
        }
        catch (Exception ex)
        {
            Log.errOut("MUD", "Error occur during compression");
            encodeBuffer=new byte[65536];
        }

        return result;
    }
    
    protected static byte[] encode3to4( byte[] b4, byte[] threeBytes, int numSigBytes )
    {
        encode3to4( threeBytes, 0, numSigBytes, b4, 0 );
        return b4;
    }

    
    protected static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes,
                                       byte[] destination, int destOffset )
    {
        int inBuff =   ( numSigBytes > 0 ? ((source[ srcOffset     ] << 24) >>>  8) : 0 )
                     | ( numSigBytes > 1 ? ((source[ srcOffset + 1 ] << 24) >>> 16) : 0 )
                     | ( numSigBytes > 2 ? ((source[ srcOffset + 2 ] << 24) >>> 24) : 0 );

        switch( numSigBytes )
        {
            case 3:
                destination[ destOffset     ] = ALPHABET[ (inBuff >>> 18)        ];
                destination[ destOffset + 1 ] = ALPHABET[ (inBuff >>> 12) & 0x3f ];
                destination[ destOffset + 2 ] = ALPHABET[ (inBuff >>>  6) & 0x3f ];
                destination[ destOffset + 3 ] = ALPHABET[ (inBuff       ) & 0x3f ];
                return destination;
                
            case 2:
                destination[ destOffset     ] = ALPHABET[ (inBuff >>> 18)        ];
                destination[ destOffset + 1 ] = ALPHABET[ (inBuff >>> 12) & 0x3f ];
                destination[ destOffset + 2 ] = ALPHABET[ (inBuff >>>  6) & 0x3f ];
                destination[ destOffset + 3 ] = EQUALS_SIGN;
                return destination;
                
            case 1:
                destination[ destOffset     ] = ALPHABET[ (inBuff >>> 18)        ];
                destination[ destOffset + 1 ] = ALPHABET[ (inBuff >>> 12) & 0x3f ];
                destination[ destOffset + 2 ] = EQUALS_SIGN;
                destination[ destOffset + 3 ] = EQUALS_SIGN;
                return destination;
                
            default:
                return destination;
        }
    }
    
    public String B64encodeObject( java.io.Serializable serializableObject )
    {
        return B64encodeObject( serializableObject, NO_OPTIONS );
    }
    
    public String B64encodeObject( java.io.Serializable serializableObject, int options )
    {
        java.io.ByteArrayOutputStream  baos  = null; 
        java.io.OutputStream           b64os = null; 
        java.io.ObjectOutputStream     oos   = null; 
        java.util.zip.GZIPOutputStream gzos  = null;
        
        int gzip           = (options & GZIP);
        int dontBreakLines = (options & DONT_BREAK_LINES);
        
        try
        {
            baos  = new java.io.ByteArrayOutputStream();
            b64os = new B64OutputStream( baos, ENCODE | dontBreakLines );
    
            if( gzip == GZIP )
            {
                gzos = new java.util.zip.GZIPOutputStream( b64os );
                oos  = new java.io.ObjectOutputStream( gzos );
            }
            else
                oos   = new java.io.ObjectOutputStream( b64os );
            
            oos.writeObject( serializableObject );
        }
        catch( java.io.IOException e )
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try{ oos.close();   } catch( Exception e ){}
            try{ gzos.close();  } catch( Exception e ){}
            try{ b64os.close(); } catch( Exception e ){}
            try{ baos.close();  } catch( Exception e ){}
        }
        
        try 
        {
            return new String( baos.toByteArray(), PREFERRED_ENCODING );
        }
        catch (java.io.UnsupportedEncodingException uue)
        {
            return new String( baos.toByteArray() );
        }
        
    } 
    
    public String B64encodeBytes( byte[] source )
    {
        return B64encodeBytes( source, 0, source.length, NO_OPTIONS );
    }   // end encodeBytes
    
    public String B64encodeBytes( byte[] source, int options )
    {   
        return B64encodeBytes( source, 0, source.length, options );
    }   // end encodeBytes
    
    public String B64encodeBytes( byte[] source, int off, int len )
    {
        return B64encodeBytes( source, off, len, NO_OPTIONS );
    }   // end encodeBytes
    
    
    public String B64encodeBytes( byte[] source, int off, int len, int options )
    {
        int dontBreakLines = ( options & DONT_BREAK_LINES );
        int gzip           = ( options & GZIP   );
        
        if( gzip == GZIP )
        {
            java.io.ByteArrayOutputStream  baos  = null;
            java.util.zip.GZIPOutputStream gzos  = null;
            B64OutputStream         b64os = null;
            
    
            try
            {
                baos = new java.io.ByteArrayOutputStream();
                b64os = new B64OutputStream( baos, ENCODE | dontBreakLines );
                gzos  = new java.util.zip.GZIPOutputStream( b64os ); 
            
                gzos.write( source, off, len );
                gzos.close();
            }
            catch( java.io.IOException e )
            {
                e.printStackTrace();
                return null;
            }
            finally
            {
                try{ gzos.close();  } catch( Exception e ){}
                try{ b64os.close(); } catch( Exception e ){}
                try{ baos.close();  } catch( Exception e ){}
            }
            try
            {
                return new String( baos.toByteArray(), PREFERRED_ENCODING );
            }
            catch (java.io.UnsupportedEncodingException uue)
            {
                return new String( baos.toByteArray() );
            }
        }
        boolean breakLines = dontBreakLines == 0;
        
        int    len43   = len * 4 / 3;
        byte[] outBuff = new byte[   ( len43 )                      // Main 4:3
                                   + ( (len % 3) > 0 ? 4 : 0 )      // Account for padding
                                   + (breakLines ? ( len43 / MAX_LINE_LENGTH ) : 0) ]; // New lines      
        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        for( ; d < len2; d+=3, e+=4 )
        {
            encode3to4( source, d+off, 3, outBuff, e );

            lineLength += 4;
            if( breakLines && lineLength == MAX_LINE_LENGTH )
            {   
                outBuff[e+4] = NEW_LINE;
                e++;
                lineLength = 0;
            }
        }

        if( d < len )
        {
            encode3to4( source, d+off, len - d, outBuff, e );
            e += 4;
        }
        try
        {
            return new String( outBuff, 0, e, PREFERRED_ENCODING );
        }
        catch (java.io.UnsupportedEncodingException uue)
        {
            return new String( outBuff, 0, e );
        }
        
    }
    
    protected static int decode4to3( byte[] source, int srcOffset, byte[] destination, int destOffset )
    {
        if( source[ srcOffset + 2] == EQUALS_SIGN )
        {
            int outBuff =   ( ( DECODABET[ source[ srcOffset    ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1] ] & 0xFF ) << 12 );
            
            destination[ destOffset ] = (byte)( outBuff >>> 16 );
            return 1;
        }
        
        else if( source[ srcOffset + 3 ] == EQUALS_SIGN )
        {
            int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1 ] ] & 0xFF ) << 12 )
                          | ( ( DECODABET[ source[ srcOffset + 2 ] ] & 0xFF ) <<  6 );
            
            destination[ destOffset     ] = (byte)( outBuff >>> 16 );
            destination[ destOffset + 1 ] = (byte)( outBuff >>>  8 );
            return 2;
        }
        
        else
        {
            try{
            int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1 ] ] & 0xFF ) << 12 )
                          | ( ( DECODABET[ source[ srcOffset + 2 ] ] & 0xFF ) <<  6)
                          | ( ( DECODABET[ source[ srcOffset + 3 ] ] & 0xFF )      );

            
            destination[ destOffset     ] = (byte)( outBuff >> 16 );
            destination[ destOffset + 1 ] = (byte)( outBuff >>  8 );
            destination[ destOffset + 2 ] = (byte)( outBuff       );

            return 3;
            }catch( Exception e){
                Log.errOut("CMEncoder",e);
                return -1;
            }
        }
    }
    
    public byte[] B64decode( byte[] source, int off, int len )
    {
        int    len34   = len * 3 / 4;
        byte[] outBuff = new byte[ len34 ]; // Upper limit on size of output
        int    outBuffPosn = 0;
        
        byte[] b4        = new byte[4];
        int    b4Posn    = 0;
        int    i         = 0;
        byte   sbiCrop   = 0;
        byte   sbiDecode = 0;
        for( i = off; i < off+len; i++ )
        {
            sbiCrop = (byte)(source[i] & 0x7f); // Only the low seven bits
            sbiDecode = DECODABET[ sbiCrop ];
            
            if( sbiDecode >= WHITE_SPACE_ENC ) // White space, Equals sign or better
            {
                if( sbiDecode >= EQUALS_SIGN_ENC )
                {
                    b4[ b4Posn++ ] = sbiCrop;
                    if( b4Posn > 3 )
                    {
                        outBuffPosn += decode4to3( b4, 0, outBuff, outBuffPosn );
                        b4Posn = 0;
                        
                        if( sbiCrop == EQUALS_SIGN )
                            break;
                    }
                } 
            }
            else
            {
                System.err.println( "Bad Base64 input character at " + i + ": " + source[i] + "(decimal)" );
                return null;
            } 
        }
                                   
        byte[] out = new byte[ outBuffPosn ];
        System.arraycopy( outBuff, 0, out, 0, outBuffPosn ); 
        return out;
    }
    
    public byte[] B64decode( String s )
    {   
        byte[] bytes;
        try
        {
            bytes = s.getBytes( PREFERRED_ENCODING );
        }
        catch( java.io.UnsupportedEncodingException uee )
        {
            bytes = s.getBytes();
        }
        bytes = B64decode( bytes, 0, bytes.length );
        if( bytes != null && bytes.length >= 4 )
        {
            
            int head = (bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);       
            if( java.util.zip.GZIPInputStream.GZIP_MAGIC == head ) 
            {
                java.io.ByteArrayInputStream  bais = null;
                java.util.zip.GZIPInputStream gzis = null;
                java.io.ByteArrayOutputStream baos = null;
                byte[] buffer = new byte[2048];
                int    length = 0;

                try
                {
                    baos = new java.io.ByteArrayOutputStream();
                    bais = new java.io.ByteArrayInputStream( bytes );
                    gzis = new java.util.zip.GZIPInputStream( bais );

                    while( ( length = gzis.read( buffer ) ) >= 0 )
                    {
                        baos.write(buffer,0,length);
                    }
                    bytes = baos.toByteArray();

                }
                catch( java.io.IOException e )
                {}
                finally
                {
                    try{ baos.close(); } catch( Exception e ){}
                    try{ gzis.close(); } catch( Exception e ){}
                    try{ bais.close(); } catch( Exception e ){}
                }
            }
        }
        
        return bytes;
    }

    public Object B64decodeToObject( String encodedObject )
    {
        byte[] objBytes = B64decode( encodedObject );
        
        java.io.ByteArrayInputStream  bais = null;
        java.io.ObjectInputStream     ois  = null;
        Object obj = null;
        
        try
        {
            bais = new java.io.ByteArrayInputStream( objBytes );
            ois  = new java.io.ObjectInputStream( bais );
        
            obj = ois.readObject();
        }
        catch( java.io.IOException e )
        {
            e.printStackTrace();
            obj = null;
        }
        catch( java.lang.ClassNotFoundException e )
        {
            e.printStackTrace();
            obj = null;
        }
        finally
        {
            try{ bais.close(); } catch( Exception e ){}
            try{ ois.close();  } catch( Exception e ){}
        }
        
        return obj;
    }
    
    public boolean B64encodeToFile( byte[] dataToEncode, String filename )
    {
        boolean success = false;
        B64OutputStream bos = null;
        try
        {
            bos = new B64OutputStream( 
                      new java.io.FileOutputStream( filename ), ENCODE );
            bos.write( dataToEncode );
            success = true;
        }
        catch( java.io.IOException e )
        {
            
            success = false;
        }
        finally
        {
            try{ bos.close(); } catch( Exception e ){}
        }
        
        return success;
    }
    
    public boolean B64decodeToFile( String dataToDecode, String filename )
    {
        boolean success = false;
        B64OutputStream bos = null;
        try
        {
                bos = new B64OutputStream( 
                          new java.io.FileOutputStream( filename ), DECODE );
                bos.write( dataToDecode.getBytes( PREFERRED_ENCODING ) );
                success = true;
        }
        catch( java.io.IOException e )
        {
            success = false;
        }
        finally
        {
                try{ bos.close(); } catch( Exception e ){}
        }
        
        return success;
    }
    
    public byte[] B64decodeFromFile( String filename )
    {
        byte[] decodedData = null;
        B64InputStream bis = null;
        try
        {
            java.io.File file = new java.io.File( filename );
            byte[] buffer = null;
            int length   = 0;
            int numBytes = 0;
            
            if( file.length() > Integer.MAX_VALUE )
            {
                System.err.println( "File is too big for this convenience method (" + file.length() + " bytes)." );
                return null;
            }
            buffer = new byte[ (int)file.length() ];
            
            bis = new B64InputStream( 
                      new java.io.BufferedInputStream( 
                      new java.io.FileInputStream( file ) ), DECODE );
            
            while( ( numBytes = bis.read( buffer, length, 4096 ) ) >= 0 )
                length += numBytes;
            
            decodedData = new byte[ length ];
            System.arraycopy( buffer, 0, decodedData, 0, length );
            
        }
        catch( java.io.IOException e )
        {
            System.err.println( "Error decoding from file " + filename );
        }
        finally
        {
            try{ bis.close(); } catch( Exception e) {}
        }
        
        return decodedData;
    }
    
    public String B64encodeFromFile( String filename )
    {
        String encodedData = null;
        B64InputStream bis = null;
        try
        {
            java.io.File file = new java.io.File( filename );
            byte[] buffer = new byte[ (int)(file.length() * 1.4) ];
            int length   = 0;
            int numBytes = 0;
            bis = new B64InputStream( 
                      new java.io.BufferedInputStream( 
                      new java.io.FileInputStream( file ) ), ENCODE );
            while( ( numBytes = bis.read( buffer, length, 4096 ) ) >= 0 )
                length += numBytes;
            encodedData = new String( buffer, 0, length, PREFERRED_ENCODING );
                
        }
        catch( java.io.IOException e )
        {
            System.err.println( "Error encoding from file " + filename );
        }
        finally
        {
            try{ bis.close(); } catch( Exception e) {}
        }
        
        return encodedData;
    }
    
    private static class B64InputStream extends java.io.FilterInputStream
    {
        private boolean encode;         // Encoding or decoding
        private int     position;       // Current position in the buffer
        private byte[]  buffer;         // Small buffer holding converted data
        private int     bufferLength;   // Length of buffer (3 or 4)
        private int     numSigBytes;    // Number of meaningful bytes in the buffer
        private int     lineLength;
        private boolean breakLines;     // Break lines at less than 80 characters
        
        public B64InputStream( java.io.InputStream in )
        {   
            this( in, DECODE );
        }
        
        public B64InputStream( java.io.InputStream in, int options )
        {   
            super( in );
            this.breakLines   = (options & DONT_BREAK_LINES) != DONT_BREAK_LINES;
            this.encode       = (options & ENCODE) == ENCODE;
            this.bufferLength = encode ? 4 : 3;
            this.buffer   = new byte[ bufferLength ];
            this.position = -1;
            this.lineLength = 0;
        }
        
        public int read() throws java.io.IOException 
        { 
            if( position < 0 )
            {
                if( encode )
                {
                    byte[] b3 = new byte[3];
                    int numBinaryBytes = 0;
                    for( int i = 0; i < 3; i++ )
                    {
                        try
                        { 
                            int b = in.read();
                            
                            if( b >= 0 )
                            {
                                b3[i] = (byte)b;
                                numBinaryBytes++;
                            }
                            
                        }
                        catch( java.io.IOException e )
                        {
                            if( i == 0 )
                                throw e;
                            
                        }
                    }
                    
                    if( numBinaryBytes > 0 )
                    {
                        encode3to4( b3, 0, numBinaryBytes, buffer, 0 );
                        position = 0;
                        numSigBytes = 4;
                    }
                    else
                    {
                        return -1;
                    }
                }
                else
                {
                    byte[] b4 = new byte[4];
                    int i = 0;
                    for( i = 0; i < 4; i++ )
                    {
                        int b = 0;
                        do{ b = in.read(); }
                        while( b >= 0 && DECODABET[ b & 0x7f ] <= WHITE_SPACE_ENC );
                        
                        if( b < 0 )
                            break; // Reads a -1 if end of stream
                        
                        b4[i] = (byte)b;
                    }
                    
                    if( i == 4 )
                    {
                        numSigBytes = decode4to3( b4, 0, buffer, 0 );
                        position = 0;
                    }
                    else if( i == 0 ){
                        return -1;
                    }
                    else
                    {
                        throw new java.io.IOException( "Improperly padded Base64 input." );
                    } 
                    
                }
            }
            if( position >= 0 )
            {
                if( position >= numSigBytes )
                    return -1;
                
                if( encode && breakLines && lineLength >= MAX_LINE_LENGTH )
                {
                    lineLength = 0;
                    return '\n';
                }
                lineLength++;
                int b = buffer[ position++ ];
                if( position >= bufferLength )
                    position = -1;
                return b & 0xFF;
            }
            throw new java.io.IOException( "Error in Base64 code reading stream." );
        } 
        
        public int read( byte[] dest, int off, int len ) throws java.io.IOException
        {
            int i;
            int b;
            for( i = 0; i < len; i++ )
            {
                b = read();
                
                if( b >= 0 )
                    dest[off + i] = (byte)b;
                else if( i == 0 )
                    return -1;
                else
                    break;
            }
            return i;
        }
    }
    
    private static class B64OutputStream extends java.io.FilterOutputStream
    {
        private boolean encode;
        private int     position;
        private byte[]  buffer;
        private int     bufferLength;
        private int     lineLength;
        private boolean breakLines;
        private byte[]  b4; // Scratch used in a few places
        private boolean suspendEncoding;
        
        public B64OutputStream( java.io.OutputStream out )
        {   
            this( out, ENCODE );
        }
        public B64OutputStream( java.io.OutputStream out, int options )
        {   
            super( out );
            this.breakLines   = (options & DONT_BREAK_LINES) != DONT_BREAK_LINES;
            this.encode       = (options & ENCODE) == ENCODE;
            this.bufferLength = encode ? 3 : 4;
            this.buffer       = new byte[ bufferLength ];
            this.position     = 0;
            this.lineLength   = 0;
            this.suspendEncoding = false;
            this.b4           = new byte[4];
        }
        
        public void write(int theByte) throws java.io.IOException
        {
            if( suspendEncoding )
            {
                super.out.write( theByte );
                return;
            }
            if( encode )
            {
                buffer[ position++ ] = (byte)theByte;
                if( position >= bufferLength )
                {
                    out.write( encode3to4( b4, buffer, bufferLength ) );

                    lineLength += 4;
                    if( breakLines && lineLength >= MAX_LINE_LENGTH )
                    {
                        out.write( NEW_LINE );
                        lineLength = 0;
                    }
                    position = 0;
                }
            }
            else
            {
                if( DECODABET[ theByte & 0x7f ] > WHITE_SPACE_ENC )
                {
                    buffer[ position++ ] = (byte)theByte;
                    if( position >= bufferLength )
                    {
                        int len = decode4to3( buffer, 0, b4, 0 );
                        out.write( b4, 0, len );
                        position = 0;
                    }
                }
                else if( DECODABET[ theByte & 0x7f ] != WHITE_SPACE_ENC )
                {
                    throw new java.io.IOException( "Invalid character in Base64 data." );
                }
            }
        }
        public void write( byte[] theBytes, int off, int len ) throws java.io.IOException
        {
            if( suspendEncoding )
            {
                super.out.write( theBytes, off, len );
                return;
            }
            for( int i = 0; i < len; i++ )
            {
                write( theBytes[ off + i ] );
            }
        }
        
        public void flushBase64() throws java.io.IOException 
        {
            if( position > 0 )
            {
                if( encode )
                {
                    out.write( encode3to4( b4, buffer, position ) );
                    position = 0;
                }
                else
                {
                    throw new java.io.IOException( "Base64 input not properly padded." );
                }
            }
        }

        public void close() throws java.io.IOException
        {
            flushBase64();
            super.close();
            
            buffer = null;
            out    = null;
        }
        public void suspendEncoding() throws java.io.IOException 
        {
            flushBase64();
            this.suspendEncoding = true;
        }
        
        public void resumeEncoding()
        {
            this.suspendEncoding = false;
        }
    }
}
