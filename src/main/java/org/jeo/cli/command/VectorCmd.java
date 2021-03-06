package org.jeo.cli.command;

import org.jeo.cli.JeoCLI;
import org.jeo.data.Dataset;
import org.jeo.protobuf.ProtobufCursor;
import org.jeo.protobuf.ProtobufReader;
import org.jeo.util.Function;
import org.jeo.util.Optional;
import org.jeo.util.Pair;
import org.jeo.vector.FeatureCursor;
import org.jeo.vector.VectorDataset;
import org.jeo.vector.VectorQuery;
import org.jeo.vector.VectorQueryPlan;

import java.io.IOException;

import static java.lang.String.format;

/**
 * Base class for commands that work on vector data.
 */
public abstract class VectorCmd extends JeoCmd {

    /**
     * Opens a vector dataset from a dataset uri.
     */
    protected Optional<VectorDataset> openVectorDataset(String ref) throws IOException {
        Optional<Dataset> data = openDataset(ref);
        if (data.isPresent() && !(data.get() instanceof VectorDataset)) {
            throw new IllegalArgumentException(ref + " is not a vector dataset");
        }

        return data.map(new Function<Dataset, VectorDataset>() {
            @Override
            public VectorDataset apply(Dataset value) {
                return (VectorDataset) value;
            }
        });
    }

    /**
     * Obtains an input cursor from stdin.
     */
    protected FeatureCursor cursorFromStdin(JeoCLI cli) throws IOException {
        // look for input from stdin
        // TODO: something better than just assuming pbf
        return new ProtobufCursor(new ProtobufReader(cli.console().getInput()).setReadUntilLastFeature());
    }

    /**
     * Obtains an input cursor.
     * <p>
     *  If <tt>dataRef</tt> is not null, {@link #openVectorDataset(String)} is used to obtain a data set
     *  and cursor. Otherwise {@link #cursorFromStdin(org.jeo.cli.JeoCLI)} is used to get a cursor from
     *  stdin.
     * </p>
     */
    protected Pair<FeatureCursor,VectorDataset> input(String dataRef, VectorQuery q, JeoCLI cli)
        throws IOException {

        FeatureCursor cursor = null;
        VectorDataset data = null;
        if (dataRef != null) {
            data = openVectorDataset(dataRef).orElseThrow(
                () -> new IllegalArgumentException(format("%s is not a data set", dataRef)));
            cursor = data.cursor(q);
        }
        else {
            // look for a direct cursor from stdin
            cursor = cursorFromStdin(cli);
            cursor = new VectorQueryPlan(q).apply(cursor);
        }

        return Pair.of(cursor, data);
    }
}
