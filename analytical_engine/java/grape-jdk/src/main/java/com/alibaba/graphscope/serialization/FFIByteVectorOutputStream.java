/*
 * Copyright 2021 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.graphscope.serialization;

import com.alibaba.graphscope.ds.StringView;
import com.alibaba.graphscope.stdcxx.FFIByteVector;
import com.alibaba.graphscope.stdcxx.FFIByteVectorFactory;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

public class FFIByteVectorOutputStream extends OutputStream implements DataOutput {

    private FFIByteVector vector;
    private long offset;

    public FFIByteVectorOutputStream() {
        vector = (FFIByteVector) FFIByteVectorFactory.INSTANCE.create();
        offset = 0;
    }

    public FFIByteVectorOutputStream(FFIByteVector vector) {
        this.vector = vector;
        offset = 0;
    }

    @Override
    public void close() {
        vector.delete();
    }

    public void resize(long size) {
        vector.resize(size);
    }

    /**
     * Reset actually reset the write position, the size kept unchanged.
     */
    public void reset() {
        offset = 0;
    }

    public FFIByteVector getVector() {
        return vector;
    }

    /**
     * Return how many bytes has been written.
     *
     * @return
     */
    public long bytesWritten() {
        return offset;
    }

    /**
     * Finish setting, shrink vector size to offset.
     */
    public void finishSetting() {
        vector.finishSetting(offset);
    }

    /**
     * Writes a <code>boolean</code> value to this output stream. If the argument <code>v</code> is
     * <code>true</code>, the value <code>(byte)1</code> is written; if <code>v</code> is
     * <code>false</code>, the  value <code>(byte)0</code> is written. The byte written by this
     * method may be read by the <code>readBoolean</code> method of interface
     * <code>DataInput</code>, which will then return a <code>boolean</code> equal to
     * <code>v</code>.
     *
     * @param v the boolean to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeBoolean(boolean v) throws IOException {
        vector.ensure(offset, 1);
        vector.setRawByte(offset, (byte) (v ? 1 : 0));
        offset += 1;
    }

    /**
     * Writes to the output stream the eight low- order bits of the argument <code>v</code>. The 24
     * high-order bits of <code>v</code> are ignored. (This means  that <code>writeByte</code> does
     * exactly the same thing as <code>write</code> for an integer argument.) The byte written by
     * this method may be read by the <code>readByte</code> method of interface
     * <code>DataInput</code>, which will then return a <code>byte</code> equal to
     * <code>(byte)v</code>.
     *
     * @param v the byte value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeByte(int v) throws IOException {
        vector.ensure(offset, 1);
        vector.setRawByte(offset, (byte) v);
        offset += 1;
    }

    /**
     * Writes two bytes to the output stream to represent the value of the argument. The byte values
     * to be written, in the  order shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 8))
     * (byte)(0xff & v)
     * }</pre> <p>
     * The bytes written by this method may be read by the <code>readShort</code> method of
     * interface <code>DataInput</code> , which will then return a <code>short</code> equal to
     * <code>(short)v</code>.
     *
     * @param v the <code>short</code> value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeShort(int v) throws IOException {
        vector.ensure(offset, 2);
        vector.setRawShort(offset, (short) v);
        offset += 2;
    }

    /**
     * Writes a <code>char</code> value, which is comprised of two bytes, to the output stream. The
     * byte values to be written, in the  order shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 8))
     * (byte)(0xff & v)
     * }</pre><p>
     * The bytes written by this method may be read by the <code>readChar</code> method of
     * interface
     * <code>DataInput</code> , which will then return a <code>char</code> equal to
     * <code>(char)v</code>.
     *
     * @param v the <code>char</code> value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeChar(int v) throws IOException {
        vector.ensure(offset, 2);
        vector.setRawChar(offset, (char) v);
        offset += 2;
    }

    /**
     * Writes an <code>int</code> value, which is comprised of four bytes, to the output stream. The
     * byte values to be written, in the  order shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 24))
     * (byte)(0xff & (v >> 16))
     * (byte)(0xff & (v >>  8))
     * (byte)(0xff & v)
     * }</pre><p>
     * The bytes written by this method may be read by the <code>readInt</code> method of interface
     * <code>DataInput</code> , which will then
     * return an <code>int</code> equal to <code>v</code>.
     *
     * @param v the <code>int</code> value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeInt(int v) throws IOException {
        vector.ensure(offset, 4);
        vector.setRawInt(offset, v);
        offset += 4;
    }

    public void writeInt(int index, int v) {
        vector.ensure(index, 4);
        vector.setRawInt(index, v);
        offset = Math.max(offset, index + 4);
    }

    /**
     * Writes a <code>long</code> value, which is comprised of eight bytes, to the output stream.
     * The byte values to be written, in the  order shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 56))
     * (byte)(0xff & (v >> 48))
     * (byte)(0xff & (v >> 40))
     * (byte)(0xff & (v >> 32))
     * (byte)(0xff & (v >> 24))
     * (byte)(0xff & (v >> 16))
     * (byte)(0xff & (v >>  8))
     * (byte)(0xff & v)
     * }</pre><p>
     * The bytes written by this method may be read by the <code>readLong</code> method of
     * interface
     * <code>DataInput</code> , which will then return a <code>long</code> equal to <code>v</code>.
     *
     * @param v the <code>long</code> value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeLong(long v) throws IOException {
        vector.ensure(offset, 8);
        vector.setRawLong(offset, v);
        offset += 8;
    }

    public void writeLong(int index, long v) {
        vector.ensure(index, 8);
        vector.setRawLong(index, v);
        offset = Math.max(offset, index + 8);
    }

    /**
     * Writes a <code>float</code> value, which is comprised of four bytes, to the output stream. It
     * does this as if it first converts this
     * <code>float</code> value to an <code>int</code>
     * in exactly the manner of the <code>Float.floatToIntBits</code> method  and then writes the
     * <code>int</code> value in exactly the manner of the  <code>writeInt</code> method.  The
     * bytes written by this method may be read by the <code>readFloat</code> method of interface
     * <code>DataInput</code>, which will then return a <code>float</code> equal to <code>v</code>.
     *
     * @param v the <code>float</code> value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeFloat(float v) throws IOException {
        vector.ensure(offset, 4);
        vector.setRawFloat(offset, v);
        offset += 4;
    }

    /**
     * Writes a <code>double</code> value, which is comprised of eight bytes, to the output stream.
     * It does this as if it first converts this
     * <code>double</code> value to a <code>long</code>
     * in exactly the manner of the <code>Double.doubleToLongBits</code> method  and then writes
     * the
     * <code>long</code> value in exactly the manner of the  <code>writeLong</code> method. The
     * bytes written by this method may be read by the <code>readDouble</code> method of interface
     * <code>DataInput</code>, which will then return a <code>double</code> equal to
     * <code>v</code>.
     *
     * @param v the <code>double</code> value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeDouble(double v) throws IOException {
        vector.ensure(offset, 8);
        vector.setRawDouble(offset, v);
        offset += 8;
    }

    /**
     * Writes a string to the output stream. For every character in the string
     * <code>s</code>,  taken in order, one byte
     * is written to the output stream.  If
     * <code>s</code> is <code>null</code>, a <code>NullPointerException</code>
     * is thrown.<p>  If <code>s.length</code> is zero, then no bytes are written. Otherwise, the
     * character <code>s[0]</code> is written first, then <code>s[1]</code>, and so on; the last
     * character written is <code>s[s.length-1]</code>. For each character, one byte is written, the
     * low-order byte, in exactly the manner of the <code>writeByte</code> method . The high-order
     * eight bits of each character in the string are ignored.
     *
     * @param s the string of bytes to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeBytes(String s) throws IOException {
        int len = s.length();
        vector.ensure(offset, len);
        for (int i = 0; i < len; i++) {
            //            UnsafeHolder.U.putByte(newBase + i, (byte) data.charAt(i));
            vector.setRawByte(offset + i, (byte) s.charAt(i));
        }
        offset += len;
    }

    public void writeBytes(StringView s) throws IOException {
        int len = (int) s.size();
        vector.ensure(offset, len);
        for (int i = 0; i < len; i++) {
            vector.setRawByte(offset + i, (byte) s.byteAt(i));
        }
        offset += len;
    }

    /**
     * Writes every character in the string <code>s</code>, to the output stream, in order, two
     * bytes per character. If <code>s</code> is <code>null</code>, a <code>NullPointerException</code>
     * is thrown.  If <code>s.length</code> is zero, then no characters are written. Otherwise, the
     * character <code>s[0]</code> is written first, then <code>s[1]</code>, and so on; the last
     * character written is
     * <code>s[s.length-1]</code>. For each character,
     * two bytes are actually written, high-order byte first, in exactly the manner of the
     * <code>writeChar</code> method.
     *
     * @param s the string value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeChars(String s) throws IOException {
        int len = s.length();
        vector.ensure(offset, len * 2);
        for (int i = 0; i < len; i++) {
            //            UnsafeHolder.U.putChar(base + offset, data.charAt(i));
            vector.setRawChar(offset, (char) s.charAt(i));
            offset += 2;
        }
    }

    /**
     * Writes two bytes of length information to the output stream, followed by the
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * representation of  every character in the string <code>s</code>. If <code>s</code> is
     * <code>null</code>, a <code>NullPointerException</code> is thrown. Each character in the
     * string <code>s</code> is converted to a group of one, two, or three bytes, depending on the
     * value of the character.<p> If a character <code>c</code> is in the range
     * <code>&#92;u0001</code> through
     * <code>&#92;u007f</code>, it is represented
     * by one byte:
     * <pre>(byte)c </pre>  <p>
     * If a character <code>c</code> is <code>&#92;u0000</code> or is in the range
     * <code>&#92;u0080</code> through <code>&#92;u07ff</code>, then it is represented by two
     * bytes, to be written
     * in the order shown: <pre>{@code
     * (byte)(0xc0 | (0x1f & (c >> 6)))
     * (byte)(0x80 | (0x3f & c))
     * }</pre> <p> If a character
     * <code>c</code> is in the range <code>&#92;u0800</code>
     * through <code>uffff</code>, then it is represented by three bytes, to be written
     * in the order shown: <pre>{@code
     * (byte)(0xe0 | (0x0f & (c >> 12)))
     * (byte)(0x80 | (0x3f & (c >>  6)))
     * (byte)(0x80 | (0x3f & c))
     * }</pre>  <p> First,
     * the total number of bytes needed to represent all the characters of <code>s</code> is
     * calculated. If this number is larger than
     * <code>65535</code>, then a <code>UTFDataFormatException</code>
     * is thrown. Otherwise, this length is written to the output stream in exactly the manner of
     * the <code>writeShort</code> method; after this, the one-, two-, or three-byte representation
     * of each character in the string <code>s</code> is written.<p>  The bytes written by this
     * method may be read by the <code>readUTF</code> method of interface
     * <code>DataInput</code> , which will then
     * return a <code>String</code> equal to <code>s</code>.
     *
     * @param s the string value to be written.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeUTF(String s) throws IOException {
        int strlen = s.length();
        int utflen = 0;
        int c;

        long beginOffset = offset;
        offset += 4;
        vector.ensure(offset, 3 * strlen + 4);
        //        ensureSize(3 * strlen + 4);

        int i = 0;
        for (i = 0; i < strlen; i++) {
            c = s.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) {
                break;
            }
            //            UnsafeHolder.U.putByte(base + (offset++), (byte) c);
            vector.setRawByte(offset++, (byte) c);
            utflen++;
        }

        for (; i < strlen; i++) {
            c = s.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                //                UnsafeHolder.U.putByte(base + (offset++), (byte) c);
                vector.setRawByte(offset++, (byte) c);
                utflen++;
            } else if (c > 0x07FF) {
                //                UnsafeHolder.U.putByte(base + (offset++),
                vector.setRawByte(offset++, (byte) (0xE0 | ((c >> 12) & 0x0F)));
                //                UnsafeHolder.U.putByte(base + (offset++),
                vector.setRawByte(offset++, (byte) (0x80 | ((c >> 6) & 0x3F)));
                //                UnsafeHolder.U.putByte(base + (offset++),
                vector.setRawByte(offset++, (byte) (0x80 | ((c >> 0) & 0x3F)));
                utflen += 3;
            } else {
                //                UnsafeHolder.U.putByte(base + (offset++),
                vector.setRawByte(offset++, (byte) (0xC0 | ((c >> 6) & 0x1F)));
                //                UnsafeHolder.U.putByte(base + (offset++),
                vector.setRawByte(offset++, (byte) (0x80 | ((c >> 0) & 0x3F)));
                utflen += 2;
            }
        }
        //        UnsafeHolder.U.putInt(base + beginOffset, utflen);
        vector.setRawInt(beginOffset, utflen);
    }

    /**
     * Writes the specified byte to this output stream. The general contract for <code>write</code>
     * is that one byte is written to the output stream. The byte to be written is the eight
     * low-order bits of the argument <code>b</code>. The 24 high-order bits of <code>b</code> are
     * ignored.
     * <p>
     * Subclasses of <code>OutputStream</code> must provide an implementation for this method.
     *
     * @param b the <code>byte</code>.
     * @throws IOException if an I/O error occurs. In particular, an <code>IOException</code> may be
     *                     thrown if the output stream has been closed.
     */
    @Override
    public void write(int b) throws IOException {
        vector.ensure(offset, 1);
        vector.setRawByte(offset, (byte) b);
        offset += 1;
    }
}
