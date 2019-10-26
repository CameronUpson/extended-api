package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.providers.RSItemDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ADivorcedFork
 *
 * Description: ExTrade focuses on adding functionality similar to methods accessible in Inventory,
 *      as well as making access to other details in the trade windows easier.
 */
public class ExTrade {

    private static final int FIRST_TRADE_WINDOW_PARENT_INDEX = 335;
    private static final int SECOND_TRADE_WINDOW_PARENT_INDEX = 334;

    private static final String FIRST_SCREEN_TRADER_NAME_PREFIX = "Trading With: ";
    private static final InterfaceAddress FIRST_SCREEN_TRADER_NAME = new InterfaceAddress(() -> Interfaces.getFirst(FIRST_TRADE_WINDOW_PARENT_INDEX, comp -> {
        String text = comp.getText();
        return text != null && text.startsWith(FIRST_SCREEN_TRADER_NAME_PREFIX);
    }));

    private static final String SECOND_SCREEN_TRADER_NAME_PREFIX = "Trading with:<br>";
    private static final InterfaceAddress SECOND_SCREEN_TRADER_NAME = new InterfaceAddress(() -> Interfaces.getFirst(SECOND_TRADE_WINDOW_PARENT_INDEX, comp -> {
        String text = comp.getText();
        return text != null && text.startsWith(SECOND_SCREEN_TRADER_NAME_PREFIX);
    }));

    public static String getTraderName() {
        if (!Trade.isOpen()) {
            return null;
        }
        InterfaceComponent traderNameComp = Trade.isOpen(false) ? Interfaces.lookup(FIRST_SCREEN_TRADER_NAME) : Interfaces.lookup(SECOND_SCREEN_TRADER_NAME);
        String prefix = Trade.isOpen(false) ? FIRST_SCREEN_TRADER_NAME_PREFIX : SECOND_SCREEN_TRADER_NAME_PREFIX;
        if (traderNameComp != null && traderNameComp.isVisible() && traderNameComp.getText() != null) {
            String text = traderNameComp.getText();
            String traderNameStr = text.substring(text.indexOf(prefix) + prefix.length()).trim();
            return traderNameStr.replace("\u00A0","\u0020");
        }
        return null;
    }


    private static final String FIRST_SCREEN_MY_OFFER_PRICE_PREFIX_1 = "Your offer:";
    private static final String FIRST_SCREEN_MY_OFFER_PRICE_PREFIX_2 = "You offer:";
    private static final InterfaceAddress FIRST_SCREEN_MY_OFFER_PRICE = new InterfaceAddress(() -> Interfaces.getFirst(FIRST_TRADE_WINDOW_PARENT_INDEX, comp -> {
        String text = comp.getText();
        return text != null && (text.startsWith(FIRST_SCREEN_MY_OFFER_PRICE_PREFIX_1) || text.startsWith(FIRST_SCREEN_MY_OFFER_PRICE_PREFIX_2));
    }));

    private static final InterfaceAddress FIRST_SCREEN_THEIR_OFFER_PRICE = new InterfaceAddress(() -> Interfaces.getFirst(FIRST_TRADE_WINDOW_PARENT_INDEX, comp -> {
        String text = comp.getText();
        return text != null && text.startsWith(getTraderName() + " offers:");
    }));

    private static final String SECOND_SCREEN_MY_OFFER_PRICE_PREFIX = "You are about to give:";
    private static final InterfaceAddress SECOND_SCREEN_MY_OFFER_PRICE = new InterfaceAddress(() -> Interfaces.getFirst(SECOND_TRADE_WINDOW_PARENT_INDEX, comp -> {
        String text = comp.getText();
        return text != null && text.startsWith(SECOND_SCREEN_MY_OFFER_PRICE_PREFIX);
    }));

    private static final String SECOND_SCREEN_THEIR_OFFER_PRICE_PREFIX = "In return you will receive:";
    private static final InterfaceAddress SECOND_SCREEN_THEIR_OFFER_PRICE = new InterfaceAddress(() -> Interfaces.getFirst(SECOND_TRADE_WINDOW_PARENT_INDEX, comp -> {
        String text = comp.getText();
        return text != null && text.startsWith(SECOND_SCREEN_THEIR_OFFER_PRICE_PREFIX);
    }));

    public static int getOfferPrice(boolean theirOffer) {
        InterfaceComponent priceComp = null;
        if (Trade.isOpen(false)) {
            priceComp = theirOffer ? Interfaces.lookup(FIRST_SCREEN_THEIR_OFFER_PRICE) : Interfaces.lookup(FIRST_SCREEN_MY_OFFER_PRICE);
        }
        if (Trade.isOpen(true)) {
            priceComp = theirOffer ? Interfaces.lookup(SECOND_SCREEN_THEIR_OFFER_PRICE) : Interfaces.lookup(SECOND_SCREEN_MY_OFFER_PRICE);
        }
        if (priceComp != null && priceComp.isVisible()) {
            String priceText = priceComp.getText();
            if (priceText.contains(">One<")) {
                return 1;
            } else {
                return numbersFromString(priceText.substring(priceText.indexOf(':')));
            }
        }
        return -1;
    }


    public static ArrayList<RSItemDefinition> getAllItemDefinitions() {
        ArrayList<RSItemDefinition> itemDefinitions = new ArrayList<>();
        itemDefinitions.addAll(getAllItemDefinitions(false));
        itemDefinitions.addAll(getAllItemDefinitions(true));
        return itemDefinitions;
    }


    private static final int SECOND_SCREEN_MY_ITEMS_INDEX = 28;
    private static final int SECOND_SCREEN_THEIR_ITEMS_INDEX = 29;

    public static ArrayList<RSItemDefinition> getAllItemDefinitions(boolean theirItems) {
        ArrayList<RSItemDefinition> itemDefinitions = new ArrayList<>();
        if (Trade.isOpen(false)) {
            Item[] items = theirItems ? Trade.getTheirItems() : Trade.getMyItems();
            for (Item item : items) {
                RSItemDefinition itemDefinition = Definitions.getItem(item.getId());
                if (itemDefinition != null) {
                    itemDefinitions.add(itemDefinition);
                }
            }
            return itemDefinitions;
        }
        if (Trade.isOpen(true)) {
            int childIndex = theirItems ? SECOND_SCREEN_THEIR_ITEMS_INDEX : SECOND_SCREEN_MY_ITEMS_INDEX;
            InterfaceComponent itemContainerComp = Interfaces.getComponent(SECOND_TRADE_WINDOW_PARENT_INDEX, childIndex);
            if (itemContainerComp == null) {
                return itemDefinitions;
            }
            InterfaceComponent[] itemComps = itemContainerComp.getComponents();
            if (itemComps == null) {
                return itemDefinitions;
            }
            for (InterfaceComponent itemComp : itemComps) {
                String itemText = itemComp.getText();
                if (itemText == null) {
                    continue;
                }
                String itemName = (itemText.contains("<")) ? itemText.substring(0, itemText.indexOf('<')) : itemText;
                RSItemDefinition itemDefinition = Definitions.getItem(itemName, a->true);
                if (itemDefinition != null) {
                    itemDefinitions.add(itemDefinition);
                }
            }
            return itemDefinitions;
        }
        return itemDefinitions;
    }


    public static boolean contains(int itemId) {
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .contains(itemId);
    }


    public static boolean contains(boolean theirItems, int itemId) {
        return getAllItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .contains(itemId);
    }


    public static boolean contains(String itemName) {
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getName)
                .collect(Collectors.toList())
                .contains(itemName);
    }


    public static boolean contains(boolean theirItems, String itemName) {
        return getAllItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getName)
                .collect(Collectors.toList())
                .contains(itemName);
    }


    public static boolean containsAll(int... itemIds) {
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(itemIds));
    }


    public static boolean containsAll(boolean theirItems, int... itemIds) {
        return getAllItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(itemIds));
    }


    public static boolean containsAll(String... itemNames) {
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getName)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(itemNames));
    }


    public static boolean containsAll(boolean theirItems, String... itemNames) {
        return getAllItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getName)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList(itemNames));
    }


    public static boolean containsAnyExcept(String... itemNames) {
        List<String> itemNameList = Arrays.asList(itemNames);
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getName)
                .allMatch(itemNameList::contains);
    }


    public static boolean containsAnyExcept(boolean theirItems, String... itemNames) {
        List<String> itemNameList = Arrays.asList(itemNames);
        return getAllItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getName)
                .allMatch(itemNameList::contains);
    }


    private static int numbersFromString(String str) {
        String numStr = str.replaceAll("\\D+","");
        if (numStr.isEmpty()) {
            return -1;
        }
        return Integer.valueOf(numStr);
    }


//-----------------------------------------------------------------
//    // INTERFACE COMPONENT INDEXES
//    private static final int FIRST_TRADE_PLAYER_NAME = 31;
//    private static final int FIRST_TRADE_THEIR_OFFER = 27;
//    private static final int FIRST_TRADE_MY_OFFER = 24;
//
//    private static final int SECOND_TRADE_WINDOW = 334;
//    private static final int SECOND_TRADE_PLAYER_NAME = 31;
//    private static final int SECOND_TRADE_THEIR_OFFER = 24;
//    private static final int SECOND_TRADE_MY_OFFER = 23;
//
//    private static final int SECOND_TRADE_THEIR_COINS = 29;
//    private static final int SECOND_TRADE_MY_COINS = 28;
//-----------------------------------------------------------------
}

