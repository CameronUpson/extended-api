package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSItemDefinition;

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

        if (GrandExchange.getOffers(x -> x.getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED)).length > 0) {
            return GrandExchange.collectAll(toBank);
        }

        if (!GrandExchangeSetup.isOpen()) {
            return GrandExchange.createOffer(type) && Time.sleepUntil(GrandExchangeSetup::isOpen, TIMEOUT);
        }

        if (GrandExchangeSetup.getItem() == null) {
            return GrandExchangeSetup.setItem(item.getId()) && Time.sleepUntil(() -> GrandExchangeSetup.getItem() != null, TIMEOUT);
        }

        if (GrandExchangeSetup.getPricePerItem() != price) {
            return GrandExchangeSetup.setPrice(price) && Time.sleepUntil(() -> GrandExchangeSetup.getPricePerItem() == price, TIMEOUT);
        }

        if (GrandExchangeSetup.getQuantity() != quantity && quantity > SELL_ALL) {
            return GrandExchangeSetup.setQuantity(quantity) && Time.sleepUntil(() -> GrandExchangeSetup.getQuantity() == quantity, TIMEOUT);
        }

        return GrandExchangeSetup.isOpen() && GrandExchangeSetup.confirm() && Time.sleepUntil(() -> !GrandExchangeSetup.isOpen(), TIMEOUT);
    }

    public static boolean buy(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, Definitions.getItem(id), quantity, price, toBank);
    }

    public static boolean buy(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, Definitions.getItem(name, RSItemDefinition::isTradable), quantity, price, toBank);
    }

    public static boolean sell(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, Definitions.getItem(id), quantity, price, toBank);
    }

    public static boolean sell(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, Definitions.getItem(name, RSItemDefinition::isTradable), quantity, price, toBank);
    }
}
