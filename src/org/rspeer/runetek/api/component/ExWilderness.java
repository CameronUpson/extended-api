package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;

public class ExWilderness {

    private static final int INTER_MASTER_ENTER_WILDERNESS_WARNING = 475;
    private static final int INTER_MASTER_WILDERNESS_LEVEL = 90;

    /**
     * Gets the wilderness level.
     *
     * @return The wilderness level.
     */
    public static int getLevel() {
        final InterfaceComponent level = Interfaces.getFirst(a -> a.getParentIndex() ==  INTER_MASTER_WILDERNESS_LEVEL && a.getText().contains("Level: "));
        return level == null ? 0 : Integer.parseInt(level.getText().replace("Level: ", ""));
    }

    /**
     * Checks whether the enter wilderness warning interface is present.
     *
     * @return True if the enter wilderness warning interface is present; false otherwise.
     */
    public static boolean hasWarning() {
        final InterfaceComponent enter_wilderness = Interfaces.getFirst(a -> a.getParentIndex() == INTER_MASTER_ENTER_WILDERNESS_WARNING && a.containsAction("Enter Wilderness"));
        return enter_wilderness != null;
    }

    /**
     * Clicks the enter wilderness button on the enter wilderness warning interface and checks the remember option if
     * present.
     *
     * @return True if the enter wilderness button was clicked; false otherwise.
     */
    public static boolean enter() {
        final InterfaceComponent enter_wilderness = Interfaces.getFirst(a -> a.getParentIndex() == INTER_MASTER_ENTER_WILDERNESS_WARNING && a.containsAction("Enter Wilderness"));
        if (enter_wilderness == null)
            return false;

        final InterfaceComponent enter_wilderness_remember = Interfaces.getFirst(a -> a.getParentIndex() == INTER_MASTER_ENTER_WILDERNESS_WARNING && a.containsAction("Disable warning"));
        if (enter_wilderness_remember != null)
            enter_wilderness_remember.click();

        return enter_wilderness.click();
    }
}
