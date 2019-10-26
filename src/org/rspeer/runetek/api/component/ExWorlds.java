package org.rspeer.runetek.api.component;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.providers.RSWorld;

import java.util.function.Predicate;

/**
 * @author Typically
 */
public final class ExWorlds {

    /**
     * Get a random world from the loaded worlds (excludes the current world)
     *
     * @return the random world
     */
    public static RSWorld getRandom() {
        final int currentWorld = Game.getClient().getCurrentWorld();
        final RSWorld[] worlds = Worlds.getLoaded();

        RSWorld world = worlds[Random.nextInt(worlds.length)];

        while (world.getId() == currentWorld) {
            world = worlds[Random.nextInt(worlds.length)];
        }

        return world;
    }

    /**
     * Get a random world from the loaded worlds with a predicate (excludes the current world)
     *
     * @param predicate the world predicate
     * @return the random world
     */
    public static RSWorld getRandom(Predicate<? super RSWorld> predicate) {
        final int currentWorld = Game.getClient().getCurrentWorld();
        final RSWorld[] worlds = Worlds.getLoaded(predicate);

        RSWorld world = worlds[Random.nextInt(worlds.length)];

        while (world.getId() == currentWorld) {
            world = worlds[Random.nextInt(worlds.length)];
        }

        return world;
    }

}
