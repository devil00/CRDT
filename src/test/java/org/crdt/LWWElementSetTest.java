package org.crdt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.crdt.models.LWWElement;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LWWElementSetTest {

    @Test
    public void testAdd_Element() {
        LWWElementSet<String> lwwElementSet = new LWWElementSet<>();
        lwwElementSet.add("abc");
        lwwElementSet.add("abc");
        Assert.assertTrue(lwwElementSet.contains("abc"));
    }

    @Test
    public void testContains_AllElements() {
        LWWElementSet<String> lwwElementSet = new LWWElementSet<>();
        lwwElementSet.add("foo");
        lwwElementSet.add("bar");
        List<String> lst = Stream.of("foo", "bar").collect(Collectors.toList());
        Assert.assertTrue(lwwElementSet.containsAll(lst));
    }

    @Test
    public void testRemove_Element() {
        LWWElementSet<String> lwwElementSet = new LWWElementSet<>();
        lwwElementSet.add("abc");
        lwwElementSet.remove("abc");
        Assert.assertFalse(lwwElementSet.contains("abc"));
    }

    @Test
    public void testMerge_WithSameTimeStampAddAndRemoveBiasTowardsAdd() {
        LWWElement<String> el1 = new LWWElement<>("a");
        LWWElement<String> el2 = new LWWElement<>("b");
        LWWElementSet<String> lwwElementSet1 = new LWWElementSet<>(ImmutableSet.of(el1, el2), Collections.emptySet());
        LWWElement<String> el3 = new LWWElement<>("d");
        LWWElementSet<String> lwwElementSet2 = new LWWElementSet<>(ImmutableSet.of(el3, el1), ImmutableSet.of(el3));

        lwwElementSet1.merge(lwwElementSet2);
        Assert.assertTrue(lwwElementSet1.containsAll(ImmutableList.of("a", "b", "d")));
    }

    @Test
    public void testMerge() {
        LWWElementSet<String> lwwElementSet1 = new LWWElementSet<>();
        LWWElementSet<String> lwwElementSet2 = new LWWElementSet<>();
        lwwElementSet1.add("ab");
        lwwElementSet2.add("ab");
        lwwElementSet1.add("bc");
        lwwElementSet2.add("ca");
        Assert.assertTrue(lwwElementSet1.merge(lwwElementSet2).equals(lwwElementSet2.merge(lwwElementSet1)));
        Assert.assertTrue(lwwElementSet1.containsAll(ImmutableSet.of("ab", "bc", "ca")));

        lwwElementSet2.remove("ab");
        Assert.assertTrue(lwwElementSet2.containsAll(ImmutableSet.of("bc", "ca")));

        lwwElementSet1.remove("ab");
        lwwElementSet1.remove("bc");
        lwwElementSet1.remove("ca");
        Assert.assertTrue(lwwElementSet1.isEmpty());

        lwwElementSet1.add("xyz");
        Assert.assertTrue(lwwElementSet1.contains("xyz"));
    }

    @Test
    public void testIsEmpty() {
        LWWElementSet<String> lwwElementSet1 = new LWWElementSet<>();
        Assert.assertTrue(lwwElementSet1.isEmpty());
        lwwElementSet1.add("ab");

        Assert.assertFalse(lwwElementSet1.isEmpty());
        lwwElementSet1.remove("ab");
        Assert.assertTrue(lwwElementSet1.isEmpty());
    }

    @Test
    public void testSize() {
        LWWElementSet<String> lwwElementSet1 = new LWWElementSet<>();
        lwwElementSet1.add("ab");
        lwwElementSet1.remove("bc");

        Assert.assertTrue(lwwElementSet1.size() == 1);
    }
}
