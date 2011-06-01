/*
 * OUnit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010, 2011  Antti Andreimann
 *
 * This file is part of OUnit.
 *
 * OUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OUnit.  If not, see <http://www.gnu.org/licenses/>.
 */

package foopackage.junit4;

import org.junit.*;
import static org.junit.Assert.*;

public class JUnit4SimpleTest {

    @Test
    public void thisWillPass() {
        assertTrue( true );
    }

    @Test
    public void thisWillPassAsWell() {
        assertTrue( true );
    }

    @Test
    public void thisWillFail() {
        assertTrue( false );
    }

    @Test
    public void thisWillFailAsWell() {
        assertEquals( "Politicians do not lie", true, false );
    }

    @Test
    public void thisWillFailToo() {
        assertTrue( "Housewives never cheat", false );
    }

}
