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

    /**
     * Insta hops to a target world
     *
     * @param world the world
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean instaHopTo(RSWorld world) {
        final int currentWorld = Game.getClient().getCurrentWorld();

        if (Game.isLoggedIn() && Game.logout()) {
            Game.getClient().setWorld(world);
        }

        return Time.sleepUntil(() -> Game.getClient().getCurrentWorld() != currentWorld, Random.high(2000, 3000));
    }

    /**
     * Insta hops to a target world with a predicate
     *
     * @param predicate the predicate for the world
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean instaHopTo(Predicate<RSWorld> predicate) {
        return instaHopTo(Worlds.get(predicate));
    }

    /**
     * Insta hops to a target world
     *
     * @param worldId the worldId
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean instaHopTo(int worldId) {
        return instaHopTo(Worlds.get(worldId));
    }

    /**
     * Insta hops to a random world with a predicate
     *
     * @param predicate the predicate for the random world
     * @param excluded  the world id's to exclude from the predicate
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean randomInstaHop(Predicate<RSWorld> predicate, Integer... excluded) {
        if (excluded != null) {
            final List<Integer> excludedList = Arrays.asList(excluded);
            predicate = predicate.and(rsWorld -> !excludedList.contains(rsWorld.getId()));
        }

        return instaHopTo(ExWorlds.getRandom(predicate));
    }

    /**
     * Insta hops to a random free to play world
     *
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean randomInstaHopInF2p() {
        return randomInstaHop(rsWorld -> !rsWorld.isMembers());
    }

    /**
     * Insta hops to a random pure free to play world (excludes all flags)
     *
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean randomInstaHopInPureF2p() {
        return randomInstaHop(PURE_WORLD_PREDICATE);
    }


    /**
     * Insta hops to a random pay to play world
     *
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean randomInstaHopInP2p() {
        return randomInstaHop(RSWorld::isMembers);
    }

    /**
     * Insta hops to a random pure pay to play world (excludes all flags)
     *
     * @return true if it succeeds to insta hop; false otherwise
     */
    public static boolean randomInstaHopInPureP2p() {
        return randomInstaHop(PURE_MEMBER_WORLD_PREDICATE, 318, 319);
    }

}
