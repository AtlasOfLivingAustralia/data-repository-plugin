package au.org.ala.collectory.state

import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class StateMachineTest extends Specification {
    // Capture closure behaviour
    def capture

    def testCreateState1() {
        when:
        StateMachine machine = StateMachine.make {
            state "state-1"
        }
        then:
        machine.states.size() == 1
        def state = machine.states["state-1"]
        state != null
        state.name == "state-1"
        state.titleKey == "noKey"
        state.title == "state-1"
        state.descriptionKey == "noKey"
        state.description == ""
        state.tags == null
        state.action == null
    }

    def testCreateState2() {
        when:
        StateMachine machine = StateMachine.make {
            state "state-1", {
                titleKey = "state1.title"
                title = "Title Something"
                descriptionKey = "state1.description"
                description = "Description Something"
                tags = ["tag1"]
                action = { "An action" }
            }
        }
        then:
        machine.states.size() == 1
        def state = machine.states["state-1"]
        state != null
        state.name == "state-1"
        state.titleKey == "state1.title"
        state.title == "Title Something"
        state.descriptionKey == "state1.description"
        state.description == "Description Something"
        state.tags == ["tag1"]
        state.action != null
        state.action() == "An action"
    }
    
    def testCreateEvent1() {
        when:
        StateMachine machine = StateMachine.make {
            event "event-1"
        }
        then:
        machine.events.size() == 1
        def event = machine.events["event-1"]
        event != null
        event.name == "event-1"
        event.titleKey == "noKey"
        event.title == "event-1"
        event.descriptionKey == "noKey"
        event.description == ""
        event.tags == null
        event.external == true
    }

    def testCreateEvent2() {
        when:
        StateMachine machine = StateMachine.make {
            event "event-1", {
                titleKey = "event1.title"
                title = "Title Something"
                descriptionKey = "event1.description"
                description = "Description Something"
                tags = ["tag1"]
                external = false
            }
        }
        then:
        machine.events.size() == 1
        def event = machine.events["event-1"]
        event != null
        event.name == "event-1"
        event.titleKey == "event1.title"
        event.title == "Title Something"
        event.descriptionKey == "event1.description"
        event.description == "Description Something"
        event.tags == ["tag1"]
        event.external == false
    }

    def testCreateTransition1() {
        when:
        StateMachine machine = StateMachine.make {
            state "state-1"
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2"
        }
        then:
        machine.transitions.size() == 1
        def state = machine.states["state-1"]
        def event = machine.events["event-1"]
        def transition = machine.transitions[state][event]
        transition != null
        transition.name == "state-1 -> event-1 -> state-2"
        transition.titleKey == "noKey"
        transition.title == "state-1 -> event-1 -> state-2"
        transition.descriptionKey == "noKey"
        transition.tags == null
        transition.action == null
    }

    def testCreateTransition2() {
        when:
        def machine = StateMachine.make {
            state "state-1"
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2", { "An action" }, {
                titleKey = "transition1.title"
                title = "Title Something"
                descriptionKey = "transition1.description"
                description = "Description Something"
                tags = ["tag1"]
            }
        }
        then:
        machine.transitions.size() == 1
        def state = machine.states["state-1"]
        def event = machine.events["event-1"]
        def transition = machine.transitions[state][event]
        transition != null
        transition.name == "state-1 -> event-1 -> state-2"
        transition.titleKey == "transition1.title"
        transition.title == "Title Something"
        transition.descriptionKey == "transition1.description"
        transition.description == "Description Something"
        transition.tags == ["tag1"]
        transition.action != null
        transition.action() == "An action"
    }

    def testProcess1() {
        when:
        def machine = StateMachine.make {
            state "state-1"
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2"
        }
        def result = machine.process("state-1", "event-1")
        then:
        result == "state-2"
    }

    def testProcess2() {
        when:
        def machine = StateMachine.make {
            state "state-1"
            state "state-2"
            state "error"
            event "event-1"
            transition "state-1", "event-1", "state-2"
            error "error"
        }
        def result = machine.process("state-2", "event-1")
        then:
        result == "error"
    }

    def testProcess3() {
        when:
        def machine = StateMachine.make {
            state "state-1"
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2", {
                capture = "Captured"
            }
        }
        def result = machine.process("state-1", "event-1")
        then:
        result == "state-2"
        capture == "Captured"
    }

    def testProcess4() {
        when:
        def machine = StateMachine.make {
            state "state-1", { titleKey = "trace.value" }
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2", {
                capture = origin.titleKey
            }
        }
        def result = machine.process("state-1", "event-1")
        then:
        result == "state-2"
        capture == "trace.value"
    }

    def testProcess5() {
        when:
        def machine = StateMachine.make {
            state "state-1", { titleKey = "trace.value" }
            state "state-2", { action = { capture = delegate }}
            event "event-1"
            transition "state-1", "event-1", "state-2"
        }
        def result = machine.process("state-1", "event-1")
        then:
        result == "state-2"
        capture.name == "state-2"
    }

    def testProcess6() {
        when:
        def machine = StateMachine.make {
            state "state-1", { titleKey = "trace.value" }
            state "state-2", { action = { ctx -> capture = ctx }}
            event "event-1"
            transition "state-1", "event-1", "state-2"
        }
        def result = machine.process("state-1", "event-1", "Context6")
        then:
        result == "state-2"
        capture == "Context6"
    }

    def testProcess7() {
        when:
        def machine = StateMachine.make {
            state "state-1", { titleKey = "trace.value" }
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2", { ctx -> capture = ctx }
        }
        def result = machine.process("state-1", "event-1", "Context7")
        then:
        result == "state-2"
        capture == "Context7"
    }

    def testExternal1() {
        when:
        def machine = StateMachine.make {
            state "state-1", { titleKey = "trace.value" }
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2"
        }
        def result = machine.getExternal("state-1")
        then:
        result != null
        result.size() == 1
        result[0].name == "event-1"
    }

    def testExternal2() {
        when:
        def machine = StateMachine.make {
            state "state-1", { titleKey = "trace.value" }
            state "state-2"
            event "event-1"
            transition "state-1", "event-1", "state-2"
        }
        def result = machine.getExternal("state-2")
        then:
        result != null
        result.size() == 0
    }

    def testExternal3() {
        when:
        def machine = StateMachine.make {
            state "state-1", { titleKey = "trace.value" }
            state "state-2"
            event "event-1"
            event "event-2", { external = false }
            transition "state-1", "event-1", "state-2"
            transition "state-1", "event-2", "state-2"
        }
        def result = machine.getExternal("state-1")
        then:
        result != null
        result.size() == 1
        result[0].name == "event-1"
    }

}