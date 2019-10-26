package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.*;

/**
 * @author qverkk and Typically
 */

public final class ExSpells {

    private static final int COMBAT_TAB_INTERFACE_ID = 593;

    private static final int AUTO_CAST_MENU_INTERFACE_ID = 201;
    private static final int AUTO_CAST_VARP = 108;
    private static final InterfaceAddress AUTO_CAST_ROOT_MENU = new InterfaceAddress(() -> {
        final InterfaceComponent[] components = Interfaces.get(AUTO_CAST_MENU_INTERFACE_ID);

        if (components.length == 0)
            return null;

        return components[0];
    });

    /**
     * Returns the best spell the local player can cast
     *
     * @return the best spell
     */
    public static ExSpell getBestSpell() {
        final ExSpell[] allSpells = ExSpell.values();

        ExSpell currentSpell = ExSpell.WIND_STRIKE;

        for (ExSpell currentlyChecked : allSpells) {
            if (currentlyChecked == currentSpell) {
                continue;
            }

            if (currentlyChecked.getLevelRequired() > currentSpell.getLevelRequired() && currentlyChecked.canCast()) {
                currentSpell = currentlyChecked;
            }
        }
        return currentSpell;
    }

    /**
     * Returns the currently selected spell
     *
     * @return the currently selected spell
     */
    public static ExSpell getSelectedSpell() {
        for (ExSpell spell : ExSpell.values()) {
            if (spell.isAutoCasted()) {
                return spell;
            }
        }
        return null;
    }

    /**
     * Checks if the target spell is auto-casted
     *
     * @param spell the spell
     * @return true if the spell is auto-casted; false otherwise
     */
    public static boolean isAutoCasted(ExSpell spell) {
        return spell.isAutoCasted();
    }

    /**
     * Checks if the target spell is auto-casted
     *
     * @param spell the spell
     * @return true if the spell is auto-casted; false otherwise
     */
    public static boolean isAutoCasted(Spell spell) {
        for (ExSpell exSpell : ExSpell.values()) {
            if (exSpell.getSpell().equals(spell)) {
                return isAutoCasted(exSpell);
            }
        }
        return false;
    }

    /**
     * Checks if the best spell is auto-casted
     *
     * @return true if the best spell is auto-casted; false otherwise
     */
    public static boolean isBestSpellAutoCasted() {
        return getBestSpell().isAutoCasted();
    }

    /**
     * Auto-casts a target spell
     *
     * @param spell the spell
     * @param defensive defensive auto-cast
     * @return true if auto-cast succeeded; false otherwise
     */
    public static boolean autoCast(Spell spell, boolean defensive) {
        for (ExSpell exSpell : ExSpell.values()) {
            if (exSpell.getSpell() == spell) {
                return autoCast(exSpell, defensive);
            }
        }
        return false;
    }

    /**
     * Auto-casts a target spell
     *
     * @param spell the spell
     * @return true if auto-cast succeeded; false otherwise
     */
    public static boolean autoCast(Spell spell) {
        return autoCast(spell, false);
    }

    /**
     * Auto-casts a target spell
     *
     * @param spell the spell
     * @param defensive defensive auto-cast
     * @return true if auto-cast succeeded; false otherwise
     */
    public static boolean autoCast(ExSpell spell, boolean defensive) {
        return openAutoCastSettings(defensive) && selectSpell(spell);
    }

    /**
     * Auto-casts a target spell
     *
     * @param spell the spell
     * @return true if auto-cast succeeded; false otherwise
     */
    public static boolean autoCast(ExSpell spell) {
        return autoCast(spell, false);
    }

    /**
     * Opens the auto-cast settings
     *
     * @param defensive defensive auto-cast
     * @return true if auto-cast settings is open; false otherwise
     */
    public static boolean openAutoCastSettings(boolean defensive) {
        if (AUTO_CAST_ROOT_MENU.resolve() != null)
            return true;

        InterfaceComponent chooseSpellComponent = getChooseSpellComponent(defensive);

        if (chooseSpellComponent == null)
            return false;

        return openCombatTab()
                && chooseSpellComponent.click()
                && Time.sleepUntil(() -> AUTO_CAST_ROOT_MENU.resolve() != null, Random.low(600, 1600));
    }

    /**
     * Opens the auto-cast settings
     *
     * @return true if auto-cast settings is open; false otherwise
     */
    public static boolean openAutoCastSettings() {
        return openAutoCastSettings(false);
    }
    /**
     * Selects a target spell
     *
     * @param spell the spell
     * @return true if the target spell is selected; false otherwise
     */

    public static boolean selectSpell(ExSpell spell) {
        final InterfaceComponent spellComponent = Interfaces.getComponent(AUTO_CAST_MENU_INTERFACE_ID, 1, spell.getSpellIndex());

        return spellComponent != null
                && spellComponent.interact(action -> action.toLowerCase().equals(spell.getName()))
                && Time.sleepUntil(spell::isAutoCasted, Random.low(600, 1600));
    }

    private static boolean openCombatTab() {
        if (Tabs.isOpen(Tab.COMBAT))
            return true;

        return Tabs.open(Tab.COMBAT)
                && Time.sleepUntil(() -> Tabs.isOpen(Tab.COMBAT), Random.low(600, 1600));
    }

    private static InterfaceComponent getChooseSpellComponent(boolean defensive) {
        final int index = defensive ? 0 : 1;

        final InterfaceComponent[] components =  Interfaces.get(COMBAT_TAB_INTERFACE_ID,
                interfaceComponent -> interfaceComponent.containsAction("Choose spell"));

        if (components.length < 2)
            return null;

        return components[index];
    }

    public enum ExSpell {
        WIND_STRIKE(Spell.Modern.WIND_STRIKE, 3, 1),
        WATER_STRIKE(Spell.Modern.WATER_STRIKE, 5, 2),
        EARTH_STRIKE(Spell.Modern.EARTH_STRIKE, 7, 3),
        FIRE_STRIKE(Spell.Modern.FIRE_STRIKE, 9, 4),
        WIND_BOLT(Spell.Modern.WIND_BOLT, 11, 5),
        WATER_BOLT(Spell.Modern.WATER_BOLT, 13, 6),
        EARTH_BOLT(Spell.Modern.EARTH_BOLT, 15, 7),
        FIRE_BOLT(Spell.Modern.FIRE_BOLT, 17, 8);

        private final Spell spell;
        private final int varpIndex;
        private final int spellIndex;

        ExSpell(Spell spell, int varpIndex, int spellIndex) {
            this.spell = spell;
            this.varpIndex = varpIndex;
            this.spellIndex = spellIndex;
        }

        public String getName() {
            return this.name().toLowerCase().replace("_", " ");
        }

        public boolean isAutoCasted() {
            return Varps.get(AUTO_CAST_VARP) == varpIndex;
        }

        public boolean canCast() {
            return getLevelRequired() <= Skills.getCurrentLevel(Skill.MAGIC);
        }

        public int getLevelRequired() {
            return spell.getLevelRequired();
        }

        public Spell getSpell() {
            return spell;
        }

        public int getVarpIndex() {
            return varpIndex;
        }

        public int getSpellIndex() {
            return spellIndex;
        }

        public boolean onCorrectBook() {
            return spell.getBook().equals(Magic.getBook());
        }
    }
}
