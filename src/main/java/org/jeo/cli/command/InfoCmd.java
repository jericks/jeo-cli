/* Copyright 2013 The jeo project. All rights reserved.
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
package org.jeo.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.jeo.cli.JeoCLI;
import org.jeo.cli.Util;
import org.jeo.data.*;
import org.jeo.json.JeoJSONWriter;
import org.jeo.util.Pair;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Parameters(commandNames="info", commandDescription="Get information about a data source")
public class InfoCmd extends JeoCmd {

    @Parameter(description="data source", required=true)
    List<String> datas;

    @Override
    protected void run(JeoCLI cli) throws Exception {
        JeoJSONWriter w = cli.newJSONWriter();

        for (String data : datas) {
            Pair<URI,String> uri = Util.parseDataURI(data);

            Object obj = Drivers.open(uri.first);
            if (obj == null) {
                throw new IllegalArgumentException("Unable to open data source: " + uri);
            }

            print(obj, w, cli);
        }
    }

    void print(Object obj, JeoJSONWriter w, JeoCLI cli) throws IOException {
        if (obj instanceof Workspace) {
            print((Workspace) obj, w, cli);
        }
        else if (obj instanceof Dataset) {
            print((Dataset)obj, w, cli);
        }
        else {
            throw new IllegalArgumentException(
                "Object " + obj.getClass().getName() + " not supported");
        }
    }

    void print(Dataset dataset, JeoJSONWriter w, JeoCLI cli) throws IOException {
        w.dataset(dataset);
    }

    void print(Workspace workspace, JeoJSONWriter w, JeoCLI cli) throws IOException {
        w.workspace(workspace);
    }
}
