package org.sikuli.basics;

import org.sikuli.script.Location;
import org.sikuli.script.Region;

import java.util.Collection;
import java.util.Iterator;

public class DebugAction {
    private static ActionDebugger debugger;

    static {
        debugger = new NoActionDebugger();
    }

    static public void setDebugger(ActionDebugger value) {
        debugger = value;
    }

    static public <L extends Location> L debugClick(L location) {
        return debugger.debugClick(location);
    }

    static public <R extends Region> R debugClick(R region) {
        return debugger.debugClick(region);
    }

    static public <L extends Location> L debugRightClick(L location) {
        return debugger.debugRightClick(location);
    }

    static public <R extends Region> R debugRightClick(R region) {
        return debugger.debugRightClick(region);
    }

    static public <L extends Location> L debugDoubleClick(L location) {
        return debugger.debugDoubleClick(location);
    }

    static public <R extends Region> R debugDoubleClick(R region) {
        return debugger.debugDoubleClick(region);
    }

    static public <R extends Region> R debugWhere(R region) {
        return debugger.debugWhere(region);
    }

    static public <R extends Region> R debugMatch(R region) {
        return debugger.debugMatch(region);
    }

    static public <R extends Region, L extends Collection<R>> L debugMatches(L collection) {
        return debugger.debugMatches(collection);
    }

    static public <R extends Region> Iterator<R> debugMatches(Iterator<R> iterator) {
        return debugger.debugMatches(iterator);
    }
}
