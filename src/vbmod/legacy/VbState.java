package vbmod.legacy;

/** Set by the Sodium hooks so the Accessibility mixin knows the toggle is reachable elsewhere. */
public final class VbState {
    public static volatile boolean sodiumHooked;

    private VbState() {
    }
}
