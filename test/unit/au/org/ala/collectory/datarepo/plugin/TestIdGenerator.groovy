package au.org.ala.collectory.datarepo.plugin

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TestIdGenerator {
    int id = 1

    synchronized def getNextTempDataResource() {
        return "drt" + (id ++)
    }
    synchronized def getNextDataResourceId() {
        return "dr" + (id ++)
    }
}
