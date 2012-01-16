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

public class JUnit4ErrorTest {

    @Test
    public void thisWillThrowAnError() throws Exception {
        throw new Exception("Expected unexpected error");
    }

    @Test
    public void thisWillThrowAnotherError() throws Exception {
        throw new Exception("Another interesting error");
    }

    @Test
    public void thisWillPass() {
        assertTrue( true );
    }

    @Test
    public void thisWillFail() {
        assertEquals( "Wrong animal in my pocket", "a hamster", "a rat" );
    }

    @Test
    public void thisWillFailAsWell() {
        assertEquals("Love is in the air", "Love is down below");
    }

    @Ignore
    @Test
    public void thisWillBeSkipped() {
    }
}
