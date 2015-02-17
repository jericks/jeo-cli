/* Copyright 2015 The jeo project. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jeo.cli;

import com.vividsolutions.jts.geom.Point;
import org.jeo.Tests;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class TransformTest extends CLITestSupport {

    @Test
    public void testScriptTransform() throws Exception {
        File script = Tests.newTmpFile(getClass().getResourceAsStream("transform.js"));
        cli.handle("transform", "-i", "mem://test#states", "-s", script.getAbsolutePath());

        featureOutput().each((f) -> {
            assertTrue(f.geometry() instanceof Point);
        });
    }
}
