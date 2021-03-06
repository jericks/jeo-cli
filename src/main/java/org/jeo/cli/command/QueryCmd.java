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
import com.vividsolutions.jts.geom.Envelope;
import org.jeo.cli.JeoCLI;
import org.jeo.filter.Filter;
import org.jeo.util.Pair;
import org.jeo.vector.FeatureCursor;
import org.jeo.vector.VectorDataset;
import org.jeo.vector.VectorQuery;
import org.osgeo.proj4j.CoordinateReferenceSystem;

import java.util.List;

import static java.lang.String.format;

@Parameters(commandNames="query", commandDescription="Run a query against a data set")
public class QueryCmd extends VectorCmd {

    @Parameter(names = {"-i", "--input"}, description = "Input data set")
    String dataRef;

    @Parameter(names = {"-b", "--bbox"}, description = "Bounding box (xmin,ymin,xmax,ymax)")
    Envelope bbox;

    @Parameter(names = {"-f", "--filter"}, description = "Predicate used to constrain results")
    Filter filter;

    @Parameter(names = {"-l", "--limit" }, description = "Maximum number of results to return")
    Integer limit;

    @Parameter(names = {"-s", "--skip" }, description = "Number of results to skip over")
    Integer offset;

    @Parameter(names = {"-p", "--props" }, description = "Feature properties to include, comma separated")
    List<String> props;

    @Parameter(names = {"-c", "--crs"}, description = "Projection of input")
    CoordinateReferenceSystem crs;

    @Parameter(names = {"-o", "--output"}, description = "Output for results")
    VectorSink sink = new GeoJSONSink();

    @Override
    protected void run(JeoCLI cli) throws Exception {
        VectorQuery q = new VectorQuery();
        if (bbox != null) {
            q.bounds(bbox);
        }
        if (filter != null) {
            q.filter(filter);
        }
        if (limit != null) {
            q.limit(limit);
        }
        if (offset != null) {
            q.offset(offset);
        }
        if (props != null && !props.isEmpty()) {
            q.fields(props);
        }

        Pair<FeatureCursor,VectorDataset> input = input(dataRef, q, cli);

        FeatureCursor cursor = input.first;
        if (crs != null) {
            cursor = cursor.crs(crs);
        }

        try {
            sink.encode(cursor, input.second, cli);
        }
        finally {
            cursor.close();
        }
    }
}
