/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class InternalBookmark implements Bookmark
{
    private static final InternalBookmark EMPTY = new InternalBookmark( Collections.emptySet() );

    private final Iterable<String> values;

    private InternalBookmark( Iterable<String> values )
    {
        this.values = values;
    }

    public static InternalBookmark empty()
    {
        return EMPTY;
    }

    public static InternalBookmark from( Iterable<Bookmark> bookmarks )
    {
        if ( bookmarks == null )
        {
            return empty();
        }

        if ( bookmarks instanceof Collection )
        {
            int size = ((Collection) bookmarks).size();
            if ( size == 0 )
            {
                return empty();
            }
            else if ( size == 1 )
            {
                return from( bookmarks.iterator().next() );
            }
        }

        Set<String> newValues = new HashSet<>();
        for ( Bookmark value : bookmarks )
        {
            if ( value == null )
            {
                continue; // skip any null bookmark objects
            }
            assertInternalBookmark( value );
            ((InternalBookmark) value).values.forEach( newValues::add );
        }
        return new InternalBookmark( newValues );
    }

    private static InternalBookmark from( Bookmark bookmark )
    {
        if ( bookmark == null )
        {
            return empty();
        }
        assertInternalBookmark( bookmark );
        return (InternalBookmark) bookmark; // we directly return the same bookmark back
    }

    private static void assertInternalBookmark( Bookmark bookmark )
    {
        if ( !(bookmark instanceof InternalBookmark) )
        {
            throw new IllegalArgumentException( String.format( "Received bookmark '%s' is not generated by driver sessions.", bookmark ) );
        }
    }

    public static InternalBookmark parse( String value )
    {
        if ( value == null )
        {
            return empty();
        }
        return parse( Collections.singletonList( value ) );
    }

    /**
     * Used for test only
     */
    public static InternalBookmark parse( Iterable<String> values )
    {
        if ( values == null )
        {
            return empty();
        }
        return new InternalBookmark( values );
    }

    public boolean isEmpty()
    {
        if ( values instanceof Collection )
        {
            return ((Collection) values).isEmpty();
        }
        return !values.iterator().hasNext();
    }

    public Iterable<String> values()
    {
        return values;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        InternalBookmark bookmark = (InternalBookmark) o;
        return Objects.equals( values, bookmark.values );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( values );
    }

    @Override
    public String toString()
    {
        return "Bookmark{values=" + values + "}";
    }
}
