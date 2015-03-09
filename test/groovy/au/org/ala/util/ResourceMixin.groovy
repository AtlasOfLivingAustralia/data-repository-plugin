package au.org.ala.util

import javax.xml.bind.JAXBContext

/**
 * Useful utilities for accessing resources and serializers.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class ResourceMixin {
    def marshaller(Class<?> clazz) {
        JAXBContext context = JAXBContext.newInstance(clazz)

        return context.createMarshaller()
    }

    def unmarshaller(Class<?> clazz) {
        JAXBContext context = JAXBContext.newInstance(clazz)

        return context.createUnmarshaller()
    }

    static def loadResource(Class<?> clazz, String name) {
        def reader = new InputStreamReader(clazz.getResourceAsStream(name))
        def buffer = new char[1024]
        def result = new StringWriter(1024)
        def n

        while ((n = reader.read(buffer)) >= 0)
            result.write(buffer, 0, n)
        return result.toString()
    }

    def loadResource(String name) {
        return loadResource(this.getClass(), name)
    }

    def assertEqualsIgnoreWhitespace(String expected, String actual) {
        def expected1 = expected.replaceAll("\\s", "")
        def actual1 = actual.replaceAll("\\s", "")
        assertEquals("Expecting {$expected} got ${actual}}", expected1, actual1)
    }
}
