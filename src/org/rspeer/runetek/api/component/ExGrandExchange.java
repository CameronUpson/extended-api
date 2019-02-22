package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;

/**
 * @author burak
 */
public final class ExGrandExchange {
    private static final int SELL_ALL = 0;
    private static final int TIMEOUT = 3000;

    private static boolean exchange(RSGrandExchangeOffer.Type type, int id, int quantity, int price, boolean toBank) {
        return exchange(type, quantity, price, toBank, GrandExchangeSetup.setItem(id));
    }

    private static boolean exchange(RSGrandExchangeOffer.Type type, String name, int quantity, int price, boolean toBank) {
        return exchange(type, quantity, price, toBank, GrandExchangeSetup.setItem(name));
    }

    private static boolean exchange(RSGrandExchangeOffer.Type type, int quantity, int price, boolean toBank, boolean itemIsSet) {
        if (!GrandExchange.isOpen()) {
            //GrandExchange.open();

            Npc clerk = Npcs.getNearest("Grand Exchange Clerk");
            return clerk != null ? clerk.interact("Exchange") : Movement.walkToRandomized(BankLocation.GRAND_EXCHANGE.getPosition());
        }

        if (GrandExchange.getOffers(x -> x.getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED)).length > 0) {
            return GrandExchange.collectAll(toBank);
        }

        if (!GrandExchangeSetup.isOpen() && GrandExchange.createOffer(type)) {
            return Time.sleepUntil(GrandExchangeSetup::isOpen, TIMEOUT);
        }

        if (GrandExchangeSetup.getItem() == null) {
            return itemIsSet;
        }

        if (GrandExchangeSetup.getPricePerItem() != price) {
            return GrandExchangeSetup.setPrice(price);
        }

        if (GrandExchangeSetup.getQuantity() != quantity && quantity > SELL_ALL) {
            return GrandExchangeSetup.setQuantity(quantity);
        }

        return GrandExchangeSetup.confirm();
    }

    public static boolean buy(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, id, quantity, price, toBank);
    }

    public static boolean buy(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, name, quantity, price, toBank);
    }

    public static boolean sell(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, id, quantity, price, toBank);
    }

    public static boolean sell(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, name, quantity, price, toBank);
    }
}
