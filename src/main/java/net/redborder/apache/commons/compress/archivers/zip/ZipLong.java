/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package net.redborder.apache.commons.compress.archivers.zip;

import java.io.Serializable;

/**
 * Utility class that represents a four byte integer with conversion
 * rules for the big endian byte order of ZIP files.
 * @Immutable
 */
public final class ZipLong implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    //private static final int BYTE_BIT_SIZE = 8;

    private static final int BYTE_1 = 1;
    private static final int BYTE_1_MASK = 0xFF00;
    private static final int BYTE_1_SHIFT = 8;

    private static final int BYTE_2 = 2;
    private static final int BYTE_2_MASK = 0xFF0000;
    private static final int BYTE_2_SHIFT = 16;

    private static final int BYTE_3 = 3;
    private static final long BYTE_3_MASK = 0xFF000000L;
    private static final int BYTE_3_SHIFT = 24;

    private final long value;

    /** Central File Header Signature */
    public static final ZipLong CFH_SIG = new ZipLong(0X02014B50L);

    /** Local File Header Signature */
    public static final ZipLong LFH_SIG = new ZipLong(0X04034B50L);

    /**
     * Data Descriptor signature.
     *
     * <p>Actually, PKWARE uses this as marker for split/spanned
     * archives and other archivers have started to use it as Data
     * Descriptor signature (as well).</p>
     * @since 1.1
     */
    public static final ZipLong DD_SIG = new ZipLong(0X08074B50L);

    /**
     * Value stored in size and similar fields if ZIP64 extensions are
     * used.
     * @since 1.3
     */
    static final ZipLong ZIP64_MAGIC = new ZipLong(ZipConstants.ZIP64_MAGIC);

    /**
     * Marks ZIP archives that were supposed to be split or spanned
     * but only needed a single segment in then end (so are actually
     * neither split nor spanned).
     *
     * <p>This is the "PK00" prefix found in some archives.</p>
     * @since 1.5
     */
    public static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER =
        new ZipLong(0X30304B50L);

    /**
     * Archive extra data record signature.
     * @since 1.5
     */
    public static final ZipLong AED_SIG = new ZipLong(0X08064B50L);

    /**
     * Create instance from a number.
     * @param value the long to store as a ZipLong
     */
    public ZipLong(long value) {
        this.value = value;
    }

    /**
     * Create instance from bytes.
     * @param bytes the bytes to store as a ZipLong
     */
    public ZipLong (byte[] bytes) {
        this(bytes, 0);
    }

    /**
     * Create instance from the four bytes starting at offset.
     * @param bytes the bytes to store as a ZipLong
     * @param offset the offset to start
     */
    public ZipLong (byte[] bytes, int offset) {
        value = ZipLong.getValue(bytes, offset);
    }

    /**
     * Get value as four bytes in big endian byte order.
     * @return value as four bytes in big endian order
     */
    public byte[] getBytes() {
        return ZipLong.getBytes(value);
    }

    /**
     * Get value as Java long.
     * @return value as a long
     */
    public long getValue() {
        return value;
    }

    /**
     * Get value as four bytes in big endian byte order.
     * @param value the value to convert
     * @return value as four bytes in big endian byte order
     */
    public static byte[] getBytes(long value) {
        byte[] result = new byte[ZipConstants.WORD];
        putLong(value, result, 0);
        return result;
    }

    /**
     * put the value as four bytes in big endian byte order.
     * @param value the Java long to convert to bytes
     * @param buf the output buffer
     * @param  offset
     *         The offset within the output buffer of the first byte to be written.
     *         must be non-negative and no larger than <tt>buf.length-4</tt>
     */

    public static void putLong(long value, byte[] buf, int offset) {
        buf[offset++] = (byte) ((value & ZipConstants.BYTE_MASK));
        buf[offset++] = (byte) ((value & BYTE_1_MASK) >> BYTE_1_SHIFT);
        buf[offset++] = (byte) ((value & BYTE_2_MASK) >> BYTE_2_SHIFT);
        buf[offset] = (byte) ((value & BYTE_3_MASK) >> BYTE_3_SHIFT);
    }

    public void putLong(byte[] buf, int offset) {
        putLong(value, buf, offset);
    }

    /**
     * Helper method to get the value as a Java long from four bytes starting at given array offset
     * @param bytes the array of bytes
     * @param offset the offset to start
     * @return the corresponding Java long value
     */
    public static long getValue(byte[] bytes, int offset) {
        long value = (bytes[offset + BYTE_3] << BYTE_3_SHIFT) & BYTE_3_MASK;
        value += (bytes[offset + BYTE_2] << BYTE_2_SHIFT) & BYTE_2_MASK;
        value += (bytes[offset + BYTE_1] << BYTE_1_SHIFT) & BYTE_1_MASK;
        value += (bytes[offset] & ZipConstants.BYTE_MASK);
        return value;
    }

    /**
     * Helper method to get the value as a Java long from a four-byte array
     * @param bytes the array of bytes
     * @return the corresponding Java long value
     */
    public static long getValue(byte[] bytes) {
        return getValue(bytes, 0);
    }

    /**
     * Override to make two instances with same value equal.
     * @param o an object to compare
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ZipLong)) {
            return false;
        }
        return value == ((ZipLong) o).getValue();
    }

    /**
     * Override to make two instances with same value equal.
     * @return the value stored in the ZipLong
     */
    @Override
    public int hashCode() {
        return (int) value;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // impossible
            throw new RuntimeException(cnfe);
        }
    }

    @Override
    public String toString() {
        return "ZipLong value: " + value;
    }
}
