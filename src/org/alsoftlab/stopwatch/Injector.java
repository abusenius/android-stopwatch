package org.alsoftlab.stopwatch;

import dagger.ObjectGraph;

/**
 * Initialized dependency graph.
 */
public class Injector
{
    public static ObjectGraph graph;
    public static void init(Object... modules)
    {
        graph = ObjectGraph.create(modules);
    }

    public static void inject(Object target)
    {
        graph.inject(target);
    }
}
