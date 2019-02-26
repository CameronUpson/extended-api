package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;

public class ExWilderness {

    private static final int INTER_MASTER_ENTER_WILDERNESS_WARNING = 475;
    private static final int INTER_COMP_ENTER_WILDERNESS = 11;
    private static final int INTER_COMP_ENTER_WILDERNESS_REMEMBER = 13;

    private static final int INTER_MASTER_WILDERNESS_LEVEL = 90;
    private static final int INTER_COMP_WILDERNESS_LEVEL = 56;

    /**
     * Gets the wilderness level.
     *
     * @return The wilderness level.
     */
    public static int getWildernessLevel() {
        final InterfaceComponent level = Interfaces.getComponent(INTER_MASTER_WILDERNESS_LEVEL, INTER_COMP_WILDERNESS_LEVEL);
        return level == null ? 0 : Integer.parseInt(level.getText().replace("Level: ", ""));
    }

    /**
     * Checks whether the enter wilderness warning interface is present.
     *
     * @return True if the enter wilderness warning interface is present; false otherwise.
     */
    public static boolean hasWildernessWarning() {
        final InterfaceComponent enter_wilderness = Interfaces.getComponent(INTER_MASTER_ENTER_WILDERNESS_WARNING, INTER_COMP_ENTER_WILDERNESS);
        return enter_wilderness != null;
    }

    /**
     * Clicks the enter wilderness button on the enter wilderness warning interface and checks the remember option if
     * present.
     *
     * @return True if the enter wilderness button was clicked; false otherwise.
     */
    public static boolean enterWilderness() {
        final InterfaceComponent enter_wilderness = Interfaces.getComponent(INTER_MASTER_ENTER_WILDERNESS_WARNING, INTER_COMP_ENTER_WILDERNESS);
        if (enter_wilderness == null)
            return false;

        final InterfaceComponent enter_wilderness_remember = Interfaces.getComponent(INTER_MASTER_ENTER_WILDERNESS_WARNING, INTER_COMP_ENTER_WILDERNESS_REMEMBER);
        if (enter_wilderness_remember != null)
            enter_wilderness_remember.click();

        return enter_wilderness.click();
    }
}
