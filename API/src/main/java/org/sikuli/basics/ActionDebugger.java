package org.sikuli.basics;

import org.sikuli.script.Location;
import org.sikuli.script.Region;

import java.util.Collection;
import java.util.Iterator;

public interface ActionDebugger {
    <L extends Location> L debugClick(L location);
    <R extends Region> R debugClick(R region);
    <L extends Location> L debugRightClick(L location);
    <R extends Region> R debugRightClick(R region);
    <L extends Location> L debugDoubleClick(L location);
    <R extends Region> R debugDoubleClick(R region);
    <R extends Region> R debugWhere(R region);
    <R extends Region> R debugMatch(R region);
    <R extends Region, L extends Collection<R>> L debugMatches(L collection);
    <R extends Region> Iterator<R> debugMatches(Iterator<R> iterator);
}
