package com.code4ro.legalconsultation.util;

import org.springframework.util.StringUtils;

final public class StringComparator {

    public static boolean areEqualIgnoreCaseAndWhitespace(final String str1, final String str2) {
        final String str1WithoutWhitespace = StringUtils.trimAllWhitespace(str1);
        final String str2WithoutWhitespace = StringUtils.trimAllWhitespace(str2);

        return str1WithoutWhitespace.equalsIgnoreCase(str2WithoutWhitespace);
    }

    public static boolean containsIgnoreWhitespace(final String str1, final String str2) {
        final String str1WithoutWhitespace = StringUtils.trimAllWhitespace(str1);
        final String str2WithoutWhitespace = StringUtils.trimAllWhitespace(str2);

        return str1WithoutWhitespace.contains(str2WithoutWhitespace);
    }
}
