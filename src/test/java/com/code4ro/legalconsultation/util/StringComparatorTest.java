package com.code4ro.legalconsultation.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StringComparatorTest {

    @Test
    public void shouldCompareAsEqualTwoStringsWithDifferentWhitespaces() {
        final String str1 = " test     ";
        final String str2 = "   test ";

        final boolean areEqual = StringComparator.areEqualIgnoreCaseAndWhitespace(str1, str2);
        assertThat(areEqual).isTrue();
    }

    @Test
    public void shouldCompareAsEqualTwoStringsWithoutWhitespaces() {
        final String str1 = "test";
        final String str2 = "test";

        final boolean areEqual = StringComparator.areEqualIgnoreCaseAndWhitespace(str1, str2);
        assertThat(areEqual).isTrue();
    }

    @Test
    public void shouldCompareAsUnequalTwoStringsWithDifferentWhitespaces() {
        final String str1 = " test1     ";
        final String str2 = "   test ";

        final boolean areEqual = StringComparator.areEqualIgnoreCaseAndWhitespace(str1, str2);
        assertThat(areEqual).isFalse();
    }

    @Test
    public void shouldCompareAsUnequalTwoStringsWithoutWhitespaces() {
        final String str1 = "test1";
        final String str2 = "test";

        final boolean areEqual = StringComparator.areEqualIgnoreCaseAndWhitespace(str1, str2);
        assertThat(areEqual).isFalse();
    }

    @Test
    public void shouldCheckContainsTwoStringsWithDifferentWhitespaces() {
        final String str1 = " test test1 test2     ";
        final String str2 = "   test      test ";

        final boolean areEqual = StringComparator.containsIgnoreWhitespace(str1, str2);
        assertThat(areEqual).isTrue();
    }

    @Test
    public void shouldCheckNotContainsTwoStringsWithDifferentWhitespaces() {
        final String str1 = " test test1 test2     ";
        final String str2 = "   test      1 ";

        final boolean areEqual = StringComparator.containsIgnoreWhitespace(str1, str2);
        assertThat(areEqual).isTrue();
    }

    @Test
    public void shouldCheckContainsTwoStringsWithoutWhitespaces() {
        final String str1 = "test123";
        final String str2 = "test";

        final boolean areEqual = StringComparator.containsIgnoreWhitespace(str1, str2);
        assertThat(areEqual).isTrue();
    }

    @Test
    public void shouldCheckNotContainsTwoStringsWithoutWhitespaces() {
        final String str1 = "test";
        final String str2 = "test1";

        final boolean areEqual = StringComparator.containsIgnoreWhitespace(str1, str2);
        assertThat(areEqual).isFalse();
    }
}
