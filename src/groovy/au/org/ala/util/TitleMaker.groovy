package au.org.ala.util

/**
 * Convert an identifier-type name with underscores or camel-case into something that looks
 * like a title.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TitleMaker {
    /**
     * Convert an identifier into a title.
     * <ul>
     *     <li>"ThisIsATitle" - "This Is A Title"</li>
     *     <li>"iAm_A_Title" - "I Am A Title"</li>
     * </ul>
     *
     * @param id The identifier
     *
     * @return The title
     */
    def String makeTitle(String id) {
        def title = new StringBuilder(id.length() + 4)
        def tr = new StringReader(id)
        def upper = true
        def split = false
        def c

        while ((c = tr.read()) >= 0) {
            if (c == '_') {
                title.append(' ')
                upper = true
                split = false
            } else if (Character.isLowerCase(c) && upper) {
                c = Character.toUpperCase(c)
                title.append((char) c)
                upper = false
                split = true
            } else if (split && Character.isUpperCase(c)) {
                title.append(' ')
                title.append((char) c)
                split = true
                upper = false
            } else if (Character.isWhitespace(c)) {
                title.append(' ')
                split = false
                upper = true
            } else if (Character.isAlphabetic(c) || Character.isDigit(c)) {
                title.append((char) c)
                split = true
                upper = false
            } else {
                title.append(' ')
                split = false
                upper = true
            }
        }
        return title.toString().trim().replaceAll("\\s+", " ")
    }
}
