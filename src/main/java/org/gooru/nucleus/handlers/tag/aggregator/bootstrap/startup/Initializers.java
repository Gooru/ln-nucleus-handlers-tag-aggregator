package org.gooru.nucleus.handlers.tag.aggregator.bootstrap.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.handlers.tag.aggregator.app.components.DataSourceRegistry;

/**
 * @author szgooru Created On: 08-Sep-2017
 */
public class Initializers implements Iterable<Initializer> {

    private final Iterator<Initializer> internalIterator;

    public Initializers() {
        List<Initializer> initializers = new ArrayList<>();
        initializers.add(DataSourceRegistry.getInstance());
        internalIterator = initializers.iterator();
    }

    @Override
    public Iterator<Initializer> iterator() {
        return new Iterator<Initializer>() {

            @Override
            public boolean hasNext() {
                return internalIterator.hasNext();
            }

            @Override
            public Initializer next() {
                return internalIterator.next();
            }
        };
    }
}
