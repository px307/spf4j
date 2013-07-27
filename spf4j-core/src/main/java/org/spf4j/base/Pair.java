
/*
 * Copyright (c) 2001, Zoltan Farkas All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.spf4j.base;

import com.google.common.base.Objects;
import java.util.List;

/**
 *
 * @author zoly
 */
public class Pair<A, B> {

    public Pair(final A first, final B second) {
        this.first = first;
        this.second = second;
    }
    
    public static <A, B> Pair<A, B> of(final A first, final B second) {
        return new Pair<A, B>(first, second);
    }
    
    /**
     * Creates a pair from a (str1,str2) pair.
     * @param stringPair
     * @return null if this is not a pair.
     */
    public static Pair<String, String> from(final String stringPair) {
        if (!stringPair.startsWith("(") || !stringPair.endsWith(")")) {
            return null;
        }
        int commaIdx = stringPair.indexOf(',');
        if (commaIdx < 0) {
            return null;
        }
        StringBuilder first = new StringBuilder();
        int idx = Strings.readCsvElement(stringPair, 1, first);
        if (stringPair.charAt(idx) != ',') {
            return null;
        }
        StringBuilder second = new StringBuilder();
        Strings.readCsvElement(stringPair, idx + 1, stringPair.length() - 1, second);
        return Pair.of(first.toString(), second.toString());
    }
        
    //CHECKSTYLE:OFF
    protected final A first;
    
    protected final B second;
    //CHECKSTYLE:ON
    
    public final A getFirst() {
        return first;
    }

    public final B getSecond() {
        return second;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<A, B> other = (Pair<A, B>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(first, second);
    }

    @Override
    public final String toString() {
        return "(" + Strings.toCsvElement(first.toString())
                + "," + Strings.toCsvElement(second.toString()) + ')';
    }
    
    public final List<Object> toList() {
        return java.util.Arrays.asList(first, second);
    }
    
    
}