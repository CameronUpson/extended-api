package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.providers.RSItemDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author ADivorcedFork
 *
 * Description: ExTrade focuses on adding functionality similar to methods accessible in Inventory,
 *      as well as making access to other details in the trade windows easier.
 */
public class ExTrade {

    private static final int MAX_INVENTORY_SLOTS = 28;

    private static final int FIRST_TRADE_WINDOW_PARENT_INDEX = 335;
    private static final int SECOND_TRADE_WINDOW_PARENT_INDEX = 334;


    // -----------------------------------------------------------//
    //                   NEW/EXTENDED METHODS                     //
    // -----------------------------------------------------------//

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
        itemDefinitions.addAll(getItemDefinitions(false));
        itemDefinitions.addAll(getItemDefinitions(true));
        return itemDefinitions;
    }


    public static ArrayList<RSItemDefinition> getItemDefinitions(boolean theirItems) {
        if (Trade.isOpen(false)) {
            return getFirstTradeScreenDefinitions(theirItems);
        }
        if (Trade.isOpen(true)) {
            return getSecondTradeScreenDefinitions(theirItems);
        }
        return new ArrayList<>();
    }


    private static ArrayList<RSItemDefinition> getFirstTradeScreenDefinitions(boolean theirItems) {
        ArrayList<RSItemDefinition> itemDefinitions = new ArrayList<>();
        Item[] items = theirItems ? Trade.getTheirItems() : Trade.getMyItems();
        for (Item item : items) {
            RSItemDefinition itemDefinition = Definitions.getItem(item.getId());
            if (itemDefinition != null) {
                itemDefinitions.add(itemDefinition);
            }
        }
        return itemDefinitions;
    }


    private static final int SECOND_SCREEN_MY_ITEMS_INDEX = 28;
    private static final int SECOND_SCREEN_THEIR_ITEMS_INDEX = 29;

    private static ArrayList<RSItemDefinition> getSecondTradeScreenDefinitions(boolean theirItems) {
        ArrayList<RSItemDefinition> itemDefinitions = new ArrayList<>();
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


    // -----------------------------------------------------------//
    //                INVENTORY-BASED METHODS                     //
    // -----------------------------------------------------------//

    public static boolean contains(int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getId)
                .anyMatch(idList::contains);
    }


    public static boolean contains(boolean theirItems, int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getId)
                .anyMatch(idList::contains);
    }


    public static boolean contains(String... names) {
        List<String> nameList = Arrays.asList(names);
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getName)
                .anyMatch(nameList::contains);
    }


    public static boolean contains(boolean theirItems, String... names) {
        List<String> nameList = Arrays.asList(names);
        return getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getName)
                .anyMatch(nameList::contains);
    }

    public static boolean contains(Predicate<? super RSItemDefinition> predicate) {
        return getAllItemDefinitions().stream()
                .anyMatch(predicate);
    }


    public static boolean contains(boolean theirItems, Predicate<? super RSItemDefinition> predicate) {
        return getItemDefinitions(theirItems).stream()
                .anyMatch(predicate);
    }


    public static boolean containsAll(int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .containsAll(idList);
    }


    public static boolean containsAll(boolean theirItems, int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .containsAll(idList);
    }


    public static boolean containsAll(String... names) {
        List<String> nameList = Arrays.asList(names);
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .containsAll(nameList);
    }


    public static boolean containsAll(boolean theirItems, String... names) {
        List<String> nameList = Arrays.asList(names);
        return getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getId)
                .collect(Collectors.toList())
                .containsAll(nameList);
    }


    public static boolean containsAnyExcept(int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return !getAllItemDefinitions().stream()
                .map(RSItemDefinition::getId)
                .allMatch(idList::contains);
    }


    public static boolean containsAnyExcept(boolean theirItems, int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return !getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getId)
                .allMatch(idList::contains);
    }


    public static boolean containsAnyExcept(String... names) {
        List<String> nameList = Arrays.asList(names);
        return !getAllItemDefinitions().stream()
                .map(RSItemDefinition::getName)
                .allMatch(nameList::contains);
    }


    public static boolean containsAnyExcept(boolean theirItems, String... names) {
        List<String> nameList = Arrays.asList(names);
        return !getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getName)
                .allMatch(nameList::contains);
    }


    public static boolean containsAnyExcept(Predicate<? super RSItemDefinition> predicate) {
        return !getAllItemDefinitions().stream()
                .allMatch(predicate);
    }


    public static boolean containsAnyExcept(boolean theirItems, Predicate<? super RSItemDefinition> predicate) {
        return !getItemDefinitions(theirItems).stream()
                .allMatch(predicate);
    }


    public static boolean containsOnly(int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getId)
                .allMatch(idList::contains);
    }


    public static boolean containsOnly(boolean theirItems, int... ids) {
        List<Integer> idList = toIntegerList(ids);
        return getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getId)
                .allMatch(idList::contains);
    }


    public static boolean containsOnly(String... names) {
        List<String> nameList = Arrays.asList(names);
        return getAllItemDefinitions().stream()
                .map(RSItemDefinition::getName)
                .allMatch(nameList::contains);
    }


    public static boolean containsOnly(boolean theirItems, String... names) {
        List<String> nameList = Arrays.asList(names);
        return getItemDefinitions(theirItems).stream()
                .map(RSItemDefinition::getName)
                .allMatch(nameList::contains);
    }


    public static boolean containsOnly(Predicate<? super RSItemDefinition> predicate) {
        return getAllItemDefinitions().stream()
                .allMatch(predicate);
    }


    public static boolean containsOnly(boolean theirItems, Predicate<? super RSItemDefinition> predicate) {
        return getItemDefinitions(theirItems).stream()
                .allMatch(predicate);
    }


    // Total number of items from both offers
    public static int getCount() {
        return getAllItemDefinitions().size();
    }


    public static int getCount(boolean theirOffer) {
        return getItemDefinitions(theirOffer).size();
    }


    // Total number of free slots from both offers
    public static int getFreeSlots() {
        return (MAX_INVENTORY_SLOTS * 2) - getAllItemDefinitions().size();
    }


    public static int getFreeSlots(boolean theirOffer) {
        return MAX_INVENTORY_SLOTS - getItemDefinitions(theirOffer).size();
    }


    public static boolean isEmpty() {
        return getAllItemDefinitions().size() == 0;
    }


    public static boolean isEmpty(boolean theirOffer) {
        return getItemDefinitions(theirOffer).size() == 0;
    }


    public static boolean isFull() {
        return getAllItemDefinitions().size() == (MAX_INVENTORY_SLOTS * 2);
    }


    public static boolean isFull(boolean theirOffer) {
        return getItemDefinitions(theirOffer).size() == MAX_INVENTORY_SLOTS;
    }


    // -----------------------------------------------------------//
    //                      HELPER METHODS                        //
    // -----------------------------------------------------------//
    private static int numbersFromString(String str) {
        String numStr = str.replaceAll("\\D+","");
        if (numStr.isEmpty()) {
            return -1;
        }
        return Integer.valueOf(numStr);
    }


    private static List<Integer> toIntegerList(int[] ints) {
        List<Integer> intList = new ArrayList<>(ints.length);
        for (int i : ints) intList.add(i);
        return intList;
    }

//   //-----------------------------------------------------------//
//   //                     COMPONENT INDICES                     //
//   //-----------------------------------------------------------//
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
//
}

