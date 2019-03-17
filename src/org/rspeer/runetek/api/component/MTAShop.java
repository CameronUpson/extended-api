package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.providers.RSItemTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Man16
 * Date: 17/03/2019
 */
public final class MTAShop {

    private static final int TABLE_KEY = 347;
    private static final InterfaceAddress INTERFACE_COMP = new InterfaceAddress(197, 3);

    private MTAShop() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        return Interfaces.isOpen(INTERFACE_COMP.getRoot());
    }

    private static RSItemTable getItemTable() {
        return ItemTables.lookup(TABLE_KEY);
    }

    public static boolean contains(int... ids) {
        RSItemTable rsItemTable = getItemTable();
        if (rsItemTable == null) return false;

        return rsItemTable.contains(ids);
    }

    public static boolean containsAll(int... ids) {
        RSItemTable rsItemTable = getItemTable();
        if (rsItemTable == null) return false;

        return rsItemTable.containsAll(ids);
    }

    private static InterfaceComponent getItemContainer() {
        return INTERFACE_COMP.resolve();
    }

    public static Item[] getItems(Predicate<? super Item> predicate) {
        InterfaceComponent interfaceComponent = getItemContainer();
        if (interfaceComponent == null) return new Item[0];

        List<Item> list = new ArrayList<>();

        for (int i = 0; i < interfaceComponent.getItemIds().length; i++) {
            Item item = new Item(interfaceComponent, i);
            if (predicate.test(item))
                list.add(item);
        }

        return list.toArray(new Item[list.size()]);
    }

    public static Item[] getItems() {
        return getItems(x -> true);
    }

    public static Item getFirst(Predicate<? super Item> predicate) {
        return Arrays.stream(getItems(predicate)).findFirst().orElse(null);
    }

    public static Item getFirst(int id) {
        return getFirst(item -> item.getId() == id);
    }

    public static Item getFirst(String name) {
        return getFirst(item -> item.getName().equalsIgnoreCase(name));
    }

    public static boolean buy(Predicate<? super Item> predicate) {
        Item item = getFirst(predicate);
        return item != null && item.interact(ActionOpcodes.TABLE_ACTION_1);
    }

    public static boolean buy(int id) {
        return buy(item -> item.getId() == id);
    }

    public static boolean buy(String name) {
        return buy(item -> item.getName().equalsIgnoreCase(name));
    }

}
