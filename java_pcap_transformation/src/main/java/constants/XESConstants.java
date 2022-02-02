package constants;

/**
 * Class containing static String-constants for creating the events.
 * Strings are required for naming the key or value of a XES (and XML)-tag when using Java DOM-libraries.
 */
public class XESConstants {
    /**
     * Constant for using when a key should be created - often used
     */
    public static final String KEY_STRING="key";
    /**
     * Constant for creating a value to a key - often used
     */
    public static final String VALUE_STRING="value";
    /**
     * Constant for creating an event-tag inside of the XES-file
     */
    public static final String EVENT_STRING="event";
    /**
     * Constant for naming a tag containing the sender of a packet as "initiator"
     */
    public static final String INITIATOR_STRING="initiator";
    /**
     * Constant for naming a tag containing the sender of a packet as "sender"
     */
    public static final String SENDER_STRING="sender";
    /**
     * Constant used for usage as key-value when creatinga tag for the receiver
     */
    public static final String RECEIVER_STRING="receiver";
    /**
     * Constant for naming the sender of a packet as "requester" inside of a tag-element
     */
    public static final String REQUESTER_STRING="requester";
    /**
     * Constant for setting the key-value whether an ACK was returned (to a PSH) or not
     */
    public static final String ACKRETURNED_STRING="ACK returned";
    /**
     * Constant for creating a date-tag - often used
     */
    public static final String TIME_NAME ="time:timestamp";
    /**
     * Constant for creating the concept-name as key of a tag - often used
     */
    public static final String CONCEPT_NAME="concept:name";
    /**
     * Constant for creating a string-tag. Often used.
     */
    public static final String STRING_ARGUMENT="string";
    /**
     * Constant for creating a date-tag. Often used.
     */
    public static final String DATE_ARGUMENT="date";
    /**
     * Constant for creating a trace-tag. Often used.
     */
    public static final String TRACE_ARGUMENT="trace";
    /**
     * Constant for creating a tag of type boolean. Seldom used.
     */
    public static final String BOOLEAN_ARGUMENT="boolean";
    /**
     * Constant for naming a handshake-event. Always used when handshakes are included (all analysed services)
     */
    public static final String HANDSHAKE_CONCEPT_NAME="Established handshake";
    /**
     * Constant for naming a finishes-event (Representing a TCP-finishing process)
     */
    public static final String FINISHING_CONCEPT_NAME="Finish connection";
    /**
     * Constant for setting the concept:name-value of an overcovert-trace
     */
    public static final String OVERCOVERT_TRACE_NAME="Attacking the overcovert service";
    /**
     * Constant for setting the concept:name-value of a mostwanted-trace
     */
    public static final String MOSTWANTED_TRACE_NAME="Attacking the mostwanted service";
}
