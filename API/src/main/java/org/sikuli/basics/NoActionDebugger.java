package org.sikuli.basics;

import org.sikuli.script.Location;
import org.sikuli.script.Region;

import java.util.Collection;
import java.util.Iterator;

public class NoActionDebugger implements ActionDebugger {
    @Override
    public <L extends Location> L debugClick(L location) {
        return location;
    }

    @Override
    public <R extends Region> R debugClick(R region) {
        return region;
    }

    @Override
    public <L extends Location> L debugRightClick(L location) {
        return location;
    }

    @Override
    public <R extends Region> R debugRightClick(R region) {
        return region;
    }

    @Override
    public <L extends Location> L debugDoubleClick(L location) {
        return location;
    }

    @Override
    public <R extends Region> R debugDoubleClick(R region) {
        return region;
    }

    @Override
    public <R extends Region> R debugWhere(R region) {
        return region;
    }

    @Override
    public <R extends Region> R debugMatch(R region) {
        return region;
    }

    @Override
    public <R extends Region, L extends Collection<R>> L debugMatches(L collection) {
        return collection;
    }

    @Override
    public <R extends Region> Iterator<R> debugMatches(Iterator<R> iterator) {
        return iterator;
    }
}
