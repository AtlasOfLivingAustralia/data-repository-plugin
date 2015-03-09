package au.org.ala.util
/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TitleMakerTest extends GroovyTestCase {
    def maker

    void setUp() {
        maker = new TitleMaker()
    }

    void testMakeTitle1() {
        assertEquals("I Am A Title", maker.makeTitle("IAmATitle"))
    }

    void testMakeTitle2() {
        assertEquals("I Am A Title", maker.makeTitle("iAmATitle"))
    }

    void testMakeTitle3() {
        assertEquals("I Am A Title", maker.makeTitle("I am a title"))
    }

    void testMakeTitle4() {
        assertEquals("I Am A Title", maker.makeTitle(" iAmATitle "))
    }

    void testMakeTitle5() {
        assertEquals("I Am A Title", maker.makeTitle("i_am_a_title"))
    }

    void testMakeTitle6() {
        assertEquals("I Am A Title", maker.makeTitle("i.am.a.title"))
    }
}
