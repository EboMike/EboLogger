package com.ebomike.ebologger.model;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ProgramGraphTest {
    private StackTraceElement frame1 = new StackTraceElement("FooClass", "bar", "FooClass.java", 100);
    private StackTraceElement frame2 = new StackTraceElement("FooClass", "foobar", "FooClass.java", 230);
    private StackTraceElement frame3 = new StackTraceElement("SubClass", "sump", "SubClass.java", 38);
    private StackTraceElement frame4 = new StackTraceElement("SubClass", "sump", "SubClass.java", 130);
    private StackTraceElement frame5 = new StackTraceElement("FooClass", "blorb", "FooClass.java", 15);

    @Test
    public void testGetHierarchy() throws Exception {
        ProgramGraph graph = new ProgramGraph();

        StackTraceElement[] trace = new StackTraceElement[] {
                null, null,     // The first two are ignored because they're expected to be inside
                                // the logger code
                frame1, frame2, frame3, frame4, frame5 };

        CallHierarchy hierarchy = graph.getHierarchy(trace);
        assertThat(hierarchy.unpackHierarchy(), equalTo(Arrays.copyOfRange(trace, 2, trace.length)));
    }

    @Test
    public void testGetHierarchy_Same() throws Exception {
        ProgramGraph graph = new ProgramGraph();

        StackTraceElement[] trace = new StackTraceElement[] {
                null, null,     // The first two are ignored because they're expected to be inside
                                // the logger code
                frame1, frame2, frame3, frame4, frame5 };

        CallHierarchy hierarchy = graph.getHierarchy(trace);
        CallHierarchy hierarchy2 = graph.getHierarchy(trace);
        assertThat(hierarchy, equalTo(hierarchy2));
    }

    @Test
    public void testGetHierarchy_SameRoot() throws Exception {
        ProgramGraph graph = new ProgramGraph();

        StackTraceElement[] trace1 = new StackTraceElement[] {
                null, null,     // The first two are ignored because they're expected to be inside
                                // the logger code
                frame1, frame3, frame4 };

        StackTraceElement[] trace2 = new StackTraceElement[] {
                null, null,     // The first two are ignored because they're expected to be inside
                                // the logger code
                frame5, frame2, frame3, frame4 };

        CallHierarchy hierarchy1 = graph.getHierarchy(trace1);
        CallHierarchy hierarchy2 = graph.getHierarchy(trace2);
        assertThat(hierarchy1.getParent(), is(hierarchy2.getParent().getParent()));
        assertThat(hierarchy1, is(not(hierarchy2.getParent())));
    }

    @Test
    public void testGetHierarchy_DoubleFrame() throws Exception {
        ProgramGraph graph = new ProgramGraph();

        StackTraceElement[] trace = new StackTraceElement[] {
                null, null,     // The first two are ignored because they're expected to be inside
                // the logger code
                frame1, frame2, frame3, frame2, frame2, frame3, frame4, frame5 };

        CallHierarchy hierarchy = graph.getHierarchy(trace);
        assertThat(hierarchy.unpackHierarchy(), equalTo(Arrays.copyOfRange(trace, 2, trace.length)));
    }

    @Test
    public void testGetHierarchy_DoubleFrameSameRoot() throws Exception {
        ProgramGraph graph = new ProgramGraph();

        StackTraceElement[] trace1 = new StackTraceElement[] {
                null, null,     // The first two are ignored because they're expected to be inside
                // the logger code
                frame1, frame2, frame3, frame2, frame2, frame3, frame4, frame5 };

        StackTraceElement[] trace2 = new StackTraceElement[] {
                null, null,     // The first two are ignored because they're expected to be inside
                // the logger code
                frame2, frame3, frame2, frame3, frame2, frame3, frame4, frame5 };

        CallHierarchy hierarchy1 = graph.getHierarchy(trace1);
        CallHierarchy hierarchy2 = graph.getHierarchy(trace2);
        assertThat(hierarchy1.getParent().getParent().getParent().getParent(),
                is(hierarchy2.getParent().getParent().getParent().getParent()));
    }
}
