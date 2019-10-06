package org.rspeer.runetek.api.component;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.providers.RSWorld;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Typically
 */
public final class ExWorldHopper {

    private static final Predicate<RSWorld> PURE_WORLD_PREDICATE =
            rsWorld -> !rsWorld.isDeadman()
                    && !rsWorld.isHighRisk()
                    && !rsWorld.isLastManStanding()
                    && !rsWorld.isSkillTotal()
                    && !rsWorld.isSeasonDeadman()
                    && !rsWorld.isTournament()
                    && !rsWorld.isPVP()
                    && !rsWorld.isBounty();

    private static final Predicate<RSWorld> PURE_MEMBER_WORLD_PREDICATE = PURE_WORLD_PREDICATE.and(RSWorld::isMembers);

    public static boolean instaHopTo(RSWorld world) {
        final int currentWorld = Game.getClient().getCurrentWorld();

        if (Game.isLoggedIn() && Game.logout()) {
            Game.getClient().setWorld(world);
        }

        return Time.sleepUntil(() -> Game.getClient().getCurrentWorld() != currentWorld, Random.high(2000, 3000));
    }

    public static boolean instaHopTo(Predicate<RSWorld> predicate) {
        return instaHopTo(Worlds.get(predicate));
    }

    public static boolean instaHopTo(int worldId) {
        return instaHopTo(Worlds.get(worldId));
    }

    public static boolean randomInstaHop(Predicate<RSWorld> predicate) {
        return instaHopTo(ExWorlds.getRandom(predicate));
    }

    public static boolean randomInstaHop(Predicate<RSWorld> predicate, Integer... excluded) {
        final List<Integer> excludedList = Arrays.asList(excluded);

        return randomInstaHop(predicate.and(rsWorld -> !excludedList.contains(rsWorld.getId())));
    }

    public static boolean randomInstaHopInF2p() {
        return randomInstaHop(rsWorld -> !rsWorld.isMembers());
    }

    public static boolean randomInstaHopInPureF2p() {
        return randomInstaHop(PURE_WORLD_PREDICATE);
    }

    public static boolean randomInstaHopInP2p() {
        return randomInstaHop(RSWorld::isMembers);
    }

    public static boolean randomInstaHopInPureP2p() {
        return randomInstaHop(PURE_MEMBER_WORLD_PREDICATE, 318, 319);
    }

}
