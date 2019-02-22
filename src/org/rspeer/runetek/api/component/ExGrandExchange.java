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
public class ExGrandExchange {
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

            //placeholder till GrandExchange.open() works again
            Npc clerk = Npcs.getNearest("Grand Exchange Clerk");
            if (clerk != null) {
                clerk.interact("Exchange");
            } else {
                Movement.walkToRandomized(BankLocation.GRAND_EXCHANGE.getPosition());
            }
        } else if (GrandExchange.getOffers(x -> x.getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED)).length > 0) {
            GrandExchange.collectAll(toBank);
        } else {
            if (!GrandExchangeSetup.isOpen() && GrandExchange.createOffer(type)) {
                Time.sleepUntil(GrandExchangeSetup::isOpen, TIMEOUT);
            } else {
                if (GrandExchangeSetup.getItem() != null) {
                    if (GrandExchangeSetup.getPricePerItem() != price && GrandExchangeSetup.setPrice(price)) {
                        Time.sleepUntil(() -> GrandExchangeSetup.getPricePerItem() == price, TIMEOUT);
                    } else if (GrandExchangeSetup.getQuantity() != quantity && quantity > SELL_ALL && GrandExchangeSetup.setQuantity(quantity)) {
                        Time.sleepUntil(() -> GrandExchangeSetup.getQuantity() == quantity, TIMEOUT);
                    } else {
                        return GrandExchangeSetup.confirm();
                    }
                } else {
                    if (itemIsSet) {
                        Time.sleepUntil(() -> GrandExchangeSetup.getItem() != null, TIMEOUT);
                    }
                }
            }
        }
        return false;
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
