package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;

import java.util.function.Predicate;

/**
 * @author Typically
 */
public class ExWilderness {

    private static final int WILDERNESS_ROOT_INTERFACE_ID = 90;
    private static final int WILDERNESS_WARNING_INTERFACE_ID = 475;

    private static final InterfaceAddress WILDERNESS_ROOT_LEVEL_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(WILDERNESS_ROOT_INTERFACE_ID, interfaceComponent -> interfaceComponent.getText().contains("Level")));

    private static final InterfaceAddress WILDERNESS_WARNING_DISABLE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(WILDERNESS_WARNING_INTERFACE_ID, interfaceComponent -> interfaceComponent.containsAction("Disable warning")));

    private static final InterfaceAddress WILDERNESS_WARNING_ENTER_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(WILDERNESS_WARNING_INTERFACE_ID, interfaceComponent -> interfaceComponent.containsAction("Enter Wilderness")));

    private static final Area WILDERNESS_SURFACE_AREA = Area.rectangular(2944, 3521, 3391, 3967);
    private static final Area WILDERNESS_EDGEVILLE_DUNGEON_AREA = Area.rectangular(3135, 9918, 3072, 10047);

    /**
     * Returns the current Wilderness level
     *
     * @return the current Wilderness level
     */
    public static int getLevel() {
        // Get first interface component that contains the text "Level"
        final InterfaceComponent levelComponent = WILDERNESS_ROOT_LEVEL_ADDRESS.resolve();

        // If it is null then return -1
        if (levelComponent == null)
            return -1;

        // If it is not null then get the text and split it by a space
        final String[] levelStringArray = levelComponent.getText().split(" ");

        // If the length of the string array is 0; return -1
        if (levelStringArray.length == 0)
            return -1;

        // Get the level from the string array
        final String levelString = levelStringArray[1];

        // Parse the string to an integer
        return Integer.parseInt(levelString);
    }

    /**
     * Checks whether the wilderness warning is open or not
     *
     * @return true if the wilderness warning is open; false otherwise
     */
    public static boolean isWarningOpen() {
        // If the warning button is not null then the warning is open
        return WILDERNESS_WARNING_ENTER_ADDRESS.resolve() != null;
    }

    /**
     * Checks whether the local player is in the wilderness or not
     *
     * @return true if the local player is in the wilderness; false otherwise
     */
    public static boolean isInWilderness() {
        // If local player is contained within one of these areas (there are probably more) then return true
        // If the local player is not in any of the defined areas then fallback on the wilderness interface
        return WILDERNESS_SURFACE_AREA.contains(Players.getLocal())
                || WILDERNESS_EDGEVILLE_DUNGEON_AREA.contains(Players.getLocal())
                || WILDERNESS_ROOT_LEVEL_ADDRESS.resolve() != null;
    }


    /**
     * Checks whether a target player is in the wilderness or not
     *
     * @param target the player to check
     * @return true if the target player is in the wilderness; false otherwise
     */
    public static boolean isInWilderness(final Player target) {
        // If target player is contained within one of these areas (there are probably more) then return true
        return WILDERNESS_SURFACE_AREA.contains(target)
                || WILDERNESS_EDGEVILLE_DUNGEON_AREA.contains(target);
    }


    /**
     * Checks whether the specified player can be attack by a target player or not
     *
     * @param player the player to check from
     * @param target the player that is checked
     * @return true if the specified player can be attacked by the target player; false otherwise
     */
    public static boolean canAttack(final Player player, final Player target) {
        // Get combat levels from both players
        final int combatLevel = player.getCombatLevel();
        final int targetCombatLevel = target.getCombatLevel();

        // Get the current wilderness level
        final int wildernessLevel = getLevel();

        // Calculate the minimum and maximum attackable combat level
        final int minLevel = combatLevel - wildernessLevel;
        final int maxLevel = combatLevel + wildernessLevel;

        // Return true if the target combat level is in that range
        return targetCombatLevel >= minLevel && targetCombatLevel <= maxLevel;
    }

    /**
     * Checks whether the local player can be attack by a target player or not
     *
     * @param target the player that is checked
     * @return true if the local player can be attacked by the target player; false otherwise
     */
    public static boolean canAttackLocal(final Player target) {
        return canAttack(Players.getLocal(), target);
    }

    /**
     * Approves the wilderness warning
     *
     * @param remember click on remember so the warning doesn't pop up again
     * @return true if the click on "Enter Wilderness" is successful; false otherwise
     */
    public static boolean approveWarning(final boolean remember) {
        // Get first interface component the text "Enter Wilderness"
        final InterfaceComponent enterWildernessComponent = WILDERNESS_WARNING_ENTER_ADDRESS.resolve();

        // If the component is null return false
        if (enterWildernessComponent == null)
            return false;

        // If remember is true continue
        if (remember) {
            // Get first interface component the text "Disable warning"
            final InterfaceComponent disableWarningComponent = WILDERNESS_WARNING_DISABLE_ADDRESS.resolve();

            // If the component is not null click the checkbox
            if (disableWarningComponent != null)
                disableWarningComponent.click();
        }

        // Click the enter wilderness button
        return enterWildernessComponent.click();
    }

    /**
     * Enter the wilderness using a set SceneObject and action
     *
     * @param sceneObject the SceneObject to click
     * @param action      the action to use on the specified SceneObject
     * @param remember    click on remember so the warning doesn't pop up again
     * @return true if the player is in the wilderness; false otherwise
     */
    public static boolean enter(final SceneObject sceneObject, final String action, final boolean remember) {
        // If the local player is already in the wilderness; return true
        if (isInWilderness())
            return true;

        // If the scene object is null; return false
        if (sceneObject == null)
            return false;

        // Interact with the scene object using the specified action
        sceneObject.interact(action);

        // Sleep till the warning pops up or the local player is in the wilderness
        Time.sleepUntil(() -> isWarningOpen() || isInWilderness(), Random.polar(1000, 2000));

        // If the warning is open, approve the warning and sleep till it is no longer open
        if (isWarningOpen())
            Time.sleepUntil(() -> approveWarning(remember) && !isWarningOpen(), Random.polar(1000, 2000));

        // Sleep until the player is in the wilderness
        return Time.sleepUntil(ExWilderness::isInWilderness, Random.polar(1000, 2000));
    }

    /**
     * Enter the wilderness using a set SceneObject and action
     *
     * @param name     the name of the SceneObject to click
     * @param action   the action to use on the specified SceneObject
     * @param remember click on remember so the warning doesn't pop up again
     * @return true if the player is in the wilderness; false otherwise
     */
    public static boolean enter(final String name, final String action, final boolean remember) {
        // Get the nearest scene object by the specified name
        final SceneObject sceneObject = SceneObjects.getNearest(name);

        // Enter the wilderness using that scene object
        return enter(sceneObject, action, remember);
    }

    /**
     * Enter the wilderness using a set SceneObject and action
     *
     * @param predicate the predicate for the SceneObject to click
     * @param action    the action to use on the specified SceneObject
     * @param remember  click on remember so the warning doesn't pop up again
     * @return true if the player is in the wilderness; false otherwise
     */
    public static boolean enter(final Predicate<? super SceneObject> predicate, final String action, final boolean remember) {
        // Get the nearest scene object by the specified name
        final SceneObject sceneObject = SceneObjects.getNearest(predicate);

        // Enter the wilderness using that scene object
        return enter(sceneObject, action, remember);
    }

}
