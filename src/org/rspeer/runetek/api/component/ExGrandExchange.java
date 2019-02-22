package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;

import java.util.Arrays;

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
        return openGrandExchange()
                && collectDanglingOffers(toBank)
                && openGrandExchangeSetup(type)
                && setItem(itemIsSet)
                && setItemPrice(price)
                && setItemQuantity(quantity)
                && GrandExchangeSetup.confirm();
    }

    private static boolean openGrandExchange() {
        //GrandExchange.open();

        //placeholder till GrandExchange.open() works again
        Npc clerk = findNearestClerk();

        if (clerk != null) {
            clerk.interact("Exchange");
        } else {
            walkToGe();
        }
        return GrandExchange.isOpen();
    }

    private static boolean collectDanglingOffers(boolean toBank) {
        if (hasNotAnyFinishedOffers()) return true;
        GrandExchange.collectAll(toBank);
        return Time.sleepUntil(ExGrandExchange::hasNotAnyFinishedOffers, TIMEOUT);
    }

    private static boolean openGrandExchangeSetup(RSGrandExchangeOffer.Type offerType) {
        if (GrandExchangeSetup.isOpen()) return true;
        return GrandExchange.createOffer(offerType)
                && Time.sleepUntil(GrandExchangeSetup::isOpen, TIMEOUT);

    }

    private static Npc findNearestClerk() {
        return Npcs.getNearest("Grand Exchange Clerk");
    }

    private static void walkToGe() {
        Movement.walkToRandomized(BankLocation.GRAND_EXCHANGE.getPosition());
    }

    private static boolean hasNotAnyFinishedOffers() {
        //TODO should use stream any / first
        return Arrays.stream(GrandExchange.getOffers())
                .noneMatch(it -> it.getProgress() == RSGrandExchangeOffer.Progress.FINISHED);
    }

    private static boolean setItem(boolean hasBeenSet) {
        return hasBeenSet &&
                //TODO what is this api lol
                Time.sleepUntil(() -> GrandExchangeSetup.getItem() != null, TIMEOUT);
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
