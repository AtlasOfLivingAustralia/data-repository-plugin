package au.org.ala.collectory.state

import groovy.util.logging.Log
import groovy.util.logging.Log4j

/**
 * A state machine, implemented as a DSL.
 * <p>
 * An example machine is
 * <pre>
 * machine = StateMachine.make {
 *  state "start"
 *  state "state1", {
 *    titleKey = "state1.title"
 *    action = { "Something random" }
 *  }
 *  event "event1"
 *  transition "start", "event1", "state1", {
 *      "Perform an action"
 *  }
 *  start "start"
 *  error "start"
 * }
 * </pre>
 * <p>
 * The <code>state</code> operation builds a state. The state must have a name. A configuration block can also be
 * specified with the following common properties:
 * <ul>
 * <li><code>title</code> The human-readable title of the state</li>
 * <li><code>titleKey</code> A i18n message key for the title. If absent or not found, the title is used</li>
 * <li><code>description</code> A long description of the state</li>
 * <li><code>titleKey</code> A i18n message key for the description. If absent or not found, the description is used</li>
 * <li><code>tags</code> A list of strings tagging the state</li>
 * </ul>
 * <p>
 * And one additional state-specific property
 * </p>
 * <ul>
 * <li><code>action</code> An action to perform when entering the state</li>
 * </ul>
 * <p>
 * The <code>event</code> operation builds an event. The event has the same common properties properties as a state,
 * <p>
 * The <code>transition</code> operation builds a transtion from a state to another state upon receipt of an
 * event. The first three arguments are the names of the origin state, the triggering event and the destination event.
 * An optional fourth argument gives an action closure to perform. An optional fifth argument contains the additional
 * common properties.
 * <p>
 * The <code>start</code> operation specifies the name of an initial, start state. Note that the start state is
 * never entered, so putting an action on the state will usually do nothing, unless the state is returned to by
 * a transition..
 * <p>
 * The <code>error</code> operation specifies the name of an error state that is reached if there is an error in
 * processing. The error state's action is invoked when the error state is entered.
 *
 * <h2>Actions</h2>
 * Actions have the state or transition as a delegate. It is therefore possible to access properties such as
 * the titleKey, origin or tags directly within a closure. For example.
 * <pre>
 *  machine = StateMachine.make {
 *  state "start", {
 *    title = "Mr Title To You"
 *    action = { println title }
 *  }
 * }
 * </pre>
 * Would print <emph>Mr Title To You</emph> when the state is entered.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
@Log4j
class StateMachine {
    /** The list of states, name -> state */
    Map<String, State> states = [:]
    /** The map of events, name -> event */
    Map<String, Event> events = [:]
    /** The map of transitions, origin -> event -> transition */
    Map<State, Map<Event, Transition>> transitions = [:]
    /** The start state */
    State start
    /** The error state */
    State error

    /**
     * Constructor for a state machine.
     *
     * @param closure The configuration
     *
     * @return The configured state machine.
     */
    static def make(Closure closure) {
        def machine = new StateMachine()

        closure.delegate = machine
        closure()
        return machine
    }

    /**
     * Construct a new state.
     *
     * @param name The name of the state (required)
     * @param config Configuration closure
      * @return
     */
    def state(String name, Closure config = null) {
        assert name != null
        def state = new State(name, config)

        states[name] = state
        return state
    }

    /**
     * Construct an event
     *
     * @param name The name of the event
     * @param config The event condfiguration closure
     */
    def event(String name, Closure config = null) {
        assert name != null
        def event = new Event(name, config)

        events[name] = event
        return event
    }

    /**
     * Construct a transition
     *
     * @param origin The name of the origin state
     * @param event The name of the event
     * @param destination The name of the destination state
     * @param action The action to take during the transition
     * @param config The event configuration closure
     */
    def transition(String origin, String event, String destination, Closure action = null, Closure config = null) {
        def o = states[origin]
        def e = events[event]
        def d = states[destination]
        assert o != null
        assert e != null
        assert d != null

        def transition = new Transition(o, e, d, action, config)
        def td = transitions[o]
        if (td == null) {
            td = [:]
            transitions[o] = td
        }
        assert td[e] == null
        td[e] = transition
        return transition
    }

    /**
     * Define a start state.
     *
     * @param name The name of the start state
     *
     * @return The start state
     */
    def start(String name) {
        start = states[name]
        assert start != null
        return start
    }


    /**
     * Define an error state.
     *
     * @param name The name of the error state
     *
     * @return The error state
     */
    def error(String name) {
        error = states[name]
        assert error != null
        return error
    }

    /**
     * Move from one state to another via an event.
     * <p>
     * Actions on the transition and the destination event will be invoked during processing.
     *
     * @param state The state name
     * @param event The event name
     * @param context Any additional context
     *
     * @return The new state name
     */
    String process(String state, String event, Object context = null) {
        def o = states[state]
        def e = events[event]
        def d

        if (o == null || e == null)
            throw new IllegalArgumentException("Can't find ${state}/${event}")
        def td = transitions[o]
        def transition = td == null ? null : td[e]
        try {
            if (transition == null) {
                log.debug "Can't find transition for ${state}/${event}"
                d = error
            } else {
                log.debug "Transition ${transition.name}"
                if (transition.action)
                    transition.action(context)
                d = transition.destination
            }
        } catch (Exception ex) {
            log.warn "Exception during transition, moving to error state", ex
            d = error
        }
        try {
            if (d != null && d.action)
                d.action(context)
        } catch (Exception ex) {
            log.warn "Exception during state arrival, moving to error state", ex
            if (d != error && error != null && error.action != null) {
                try {
                    error.action(context)
                } catch (Exception ex1) {
                    log.error "OK, this is getting silly, error on entering error state", ex1
                }
            }
            d = error
        }
        return d?.name
    }

    /**
     * Get the list of external events that can be triggered in this state
     *
     * @param state The state name
     * @return
     */
    List<Event> getExternal(String state) {
        def s = states[state]
        def td = transitions[s]

        return td == null ? [] : td.keySet().findAll { event -> event.external } as List
    }


    /**
     * Abstract state machine configuration class
     */
    abstract class StateMachineConfig {
        /** The name of the entity */
        String name
        /** A i18n key for the human-readable title */
        String titleKey
        /** The standard title for the item */
        String title
        /** A i18n key for the human-readable description */
        String descriptionKey
        /** The standard description */
        String description
        /** Any tags associated with this configuration */
        List<String> tags

        StateMachineConfig(String name) {
            this.name = name;
            this.title = name;
            this.titleKey = "noKey"
            this.description = ""
            this.descriptionKey = "noKey"
        }

    }

    /** A specific state */
    class State extends StateMachineConfig {
        /** An action to perform when reaching this state */
        Closure action

        def State(String name, Closure config) {
            super(name)
            if (config != null) {
                //config.resolveStrategy = Closure.DELEGATE_FIRST
                config.delegate = this
                config()
            }
            if (action)
                action.delegate = this
        }

    }

    /** A named event */
    class Event extends StateMachineConfig {
        boolean external

        def Event(String name, Closure config) {
            super(name)
            external = true
            if (config != null) {
                //config.resolveStrategy = Closure.DELEGATE_FIRST
                config.delegate = this
                config()
            }
        }
    }

    /** A named transition */
    class Transition extends StateMachineConfig {
        /** The origin state */
        State origin
        /** The event */
        Event event
        /** The destination state */
        State destination
        /** The action to take during the transition */
        Closure action

        def Transition(State origin, Event event, State destination, Closure action, Closure config) {
            super(origin.name + " -> " + event.name + " -> " + destination.name)
            this.origin = origin
            this.event = event
            this.destination = destination
            this.action = action
            if (config != null) {
                //config.resolveStrategy = Closure.DELEGATE_FIRST
                config.delegate = this
                config()
            }
            if (this.action)
                this.action.delegate = this
        }
    }
}
