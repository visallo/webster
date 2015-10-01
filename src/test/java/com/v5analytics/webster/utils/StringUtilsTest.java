package com.v5analytics.webster.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class StringUtilsTest {
    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty(" "));
        assertTrue(StringUtils.isEmpty("     "));
    }

    @Test
    public void testContainsAnEmpty() {
        assertTrue(StringUtils.containsAnEmpty(new String[] {"abc", "", "def"}));
        assertTrue(StringUtils.containsAnEmpty(new String[] {""}));
        assertTrue(StringUtils.containsAnEmpty(new String[] {"abc", "def", "   "}));
        assertTrue(StringUtils.containsAnEmpty(new String[] {"abc", "def", null}));
    }
}
