package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSItemDefinition;

import java.util.Arrays;

/**
 * @author burak
 */
public final class ExGrandExchange {
    private static final int SELL_ALL = 0;
    private static final int TIMEOUT = 3000;
    private static final String EXCHANGE_ACTION = "Exchange";
    private static final String GE_NPC_NAME = "Grand Exchange Clerk";

    private static boolean exchange(RSGrandExchangeOffer.Type type, RSItemDefinition item, int quantity, int price, boolean toBank) {
        return exchange(type, quantity, price, toBank, item);
    }

    private static boolean exchange(RSGrandExchangeOffer.Type type, int quantity, int price, boolean toBank, RSItemDefinition item) {
        if (!GrandExchange.isOpen()) {
            //GrandExchange.open();

            // placeholder
            Npc clerk = Npcs.getNearest(GE_NPC_NAME);
            return clerk != null ? clerk.interact(EXCHANGE_ACTION) : Movement.walkToRandomized(BankLocation.GRAND_EXCHANGE.getPosition());
        }

        return collectFinishedOffers(toBank)
                && createOffer(type)
                && setItem(item)
                && setItemPrice(price)
                && setItemQuantity(quantity)
                && GrandExchangeSetup.confirm();

    }

    private static boolean setItem(RSItemDefinition item) {
        return GrandExchangeSetup.setItem(item.getId());
    }

    private static boolean collectFinishedOffers(boolean toBank) {
        if (hasNotAnyFinishedOffers()) return true;
        GrandExchange.collectAll(toBank);
        return Time.sleepUntil(ExGrandExchange::hasNotAnyFinishedOffers, TIMEOUT);
    }

    private static boolean createOffer(RSGrandExchangeOffer.Type offerType) {
        if (GrandExchangeSetup.isOpen()) return true;
        return GrandExchange.createOffer(offerType)
                && Time.sleepUntil(GrandExchangeSetup::isOpen, TIMEOUT);
    }

    private static boolean hasNotAnyFinishedOffers() {
        return Arrays.stream(GrandExchange.getOffers())
                .noneMatch(it -> it.getProgress() == RSGrandExchangeOffer.Progress.FINISHED);
    }

    private static boolean isItemPriceSettled(int desired) {
        return GrandExchangeSetup.getPricePerItem() == desired;
    }

    private static boolean isItemQuantitySettled(int desired) {
        return GrandExchangeSetup.getQuantity() == desired;
    }

    private static boolean setItemPrice(int price) {
        return GrandExchangeSetup.setPrice(price)
                && Time.sleepUntil(() -> isItemPriceSettled(price), TIMEOUT);
    }

    private static boolean setItemQuantity(int quantity) {
        if (quantity == SELL_ALL) return true;

        return GrandExchangeSetup.setQuantity(quantity)
                && Time.sleepUntil(() -> isItemQuantitySettled(quantity), TIMEOUT);
    }

    public static boolean buy(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, Definitions.getItem(id), quantity, price, toBank);
    }

    public static boolean buy(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, Definitions.getItem(name, x -> x.isTradable() || x.isNoted()), quantity, price, toBank);
    }

    public static boolean sell(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, Definitions.getItem(id), quantity, price, toBank);
    }

    public static boolean sell(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, Definitions.getItem(name, x -> x.isTradable() || x.isNoted()), quantity, price, toBank);
    }
}
