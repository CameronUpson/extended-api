package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.*;

/**
 * @author qverkk
 */

public final class ExSpells {

    private static final int AUTOCAST_VARP = 108;
    private static final InterfaceAddress AUTO_CAST_MENU = new InterfaceAddress(() -> Interfaces.getComponent(201, 1, 1));

    public static boolean isBestSpellAutoCasted() {
        return getBestSpell().isAutoCasted();
    }

    public static ExSpell getBestSpell() {
        ExSpell currentSpell = ExSpell.WIND_STRIKE;
        ExSpell[] allSpells = ExSpell.values();
        for (ExSpell currentlyChecked : allSpells) {
            if (currentlyChecked == currentSpell)
                continue;
            if (currentlyChecked.getLevelRequired() > currentSpell.getLevelRequired() && currentlyChecked.canCast())
                currentSpell = currentlyChecked;
        }
        return currentSpell;
    }

    public static ExSpell getCurrentlySelected() {
        for (ExSpell spell : ExSpell.values()) {
            if (spell.isAutoCasted())
                return spell;
        }
        return null;
    }

    public static boolean isAutoCasted(ExSpell spell) {
        return spell.isAutoCasted();
    }

    public static boolean isAutoCasted(Spell spell) {
        for (ExSpell exSpell : ExSpell.values()) {
            if (exSpell.getSpell() == spell) {
                return isAutoCasted(exSpell);
            }
        }
        return false;
    }

    public static boolean selectAutoCast(Spell spell) {
        for (ExSpell exSpell : ExSpell.values()) {
            if (exSpell.getSpell() == spell)
                return selectAutoCast(exSpell);
        }
        return false;
    }

    public static boolean selectAutoCast(ExSpell spell) {
        return openAutoCastSettings() && selectSpell(spell);
    }

    private static boolean openAutoCastSettings() {
        if (AUTO_CAST_MENU.resolve() != null)
            return true;

        InterfaceComponent bookComponent = Interfaces.get(593, a -> a.containsAction("Choose spell"))[1];
        return openCombatTab() && bookComponent != null && bookComponent.interact("Choose spell") &&
                Time.sleepUntil(() -> AUTO_CAST_MENU.resolve() != null, 600, 1200);
    }

    private static boolean selectSpell(ExSpell spell) {
        InterfaceComponent spellComponent = Interfaces.getComponent(201, 1, spell.getSpellIndex());
        return spellComponent != null && spellComponent.interact(a -> a.toLowerCase().equals(spell.getName()))
                && Time.sleepUntil(spell::isAutoCasted, 600, 1200);
    }

    private static boolean openCombatTab() {
        if (Tab.COMBAT.isOpen())
            return true;

        InterfaceComponent combatComponent = Tab.COMBAT.getComponent();
        return combatComponent != null && combatComponent.interact("Combat Options") &&
                Time.sleepUntil(Tab.COMBAT::isOpen, 600, 1200);
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
            return Varps.get(AUTOCAST_VARP) == varpIndex;
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
