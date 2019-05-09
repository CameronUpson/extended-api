package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.chatter.ClanChat;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;

/**
 * @author 2baad4u2
 */
public class ExClanChat {

    private static final int CLAN_CHAT_INDEX;
    private static final int JOIN_LEAVE_CHAT_COMPONENT = 22;
    private static final int CLAN_SETUP_COMPONENT = 24;

    static {
        CLAN_CHAT_INDEX = InterfaceComposite.CLAN_CHAT.getGroup();
    }

    /**
     * Check if player is in a clan chat channel.
     *
     * @return true if in channel
     */
    public static boolean isInChannel() {
        return ClanChat.isInChannel();
    }

    /**
     * Check if player is not in a clan chat channel.
     *
     * @return true if not in channel
     */
    public static boolean isNotInChannel() {
        return !ClanChat.isInChannel();
    }

    /**
     * Check if clan chat tab is open.
     *
     * @return true if clan chat open
     */
    public static boolean isOpen() {
        return Tab.CLAN_CHAT.isOpen();
    }

    /**
     * Opens the clan chat tab.
     *
     * @return true if chat chat is open
     */
    public static boolean open() {
        return isOpen() || (Tabs.open(Tab.CLAN_CHAT) && Time.sleepUntil(ExClanChat::isOpen, 1200L));
    }

    /**
     * Joins a clan chat channel with specified name (will leave current clan chat if already in one).
     *
     * @param name of the clan chat to join
     * @return true if successfully tried to join clan chat
     */
    public static boolean join(String name) {
        if (name == null || name.isEmpty() || !Time.sleepUntil(ExClanChat::open, 1200L)) return false;
        if (!leave()) return false;
        Time.sleepUntil(ExClanChat::isNotInChannel, 1200L);
        InterfaceComponent joinChatButton = Interfaces.getComponent(CLAN_CHAT_INDEX, JOIN_LEAVE_CHAT_COMPONENT);
        if (joinChatButton != null && joinChatButton.click()) {
            Time.sleepUntil(EnterInput::isOpen, 1200L);
            return EnterInput.initiate(name);
        }
        return false;
    }

    /**
     * Leaves the current clan chat channel if player is in one.
     *
     * @return true if successfully left clan chat
     */
    public static boolean leave() {
        if (!isInChannel()) return true;
        if (!Time.sleepUntil(ExClanChat::open, 1200L)) return false;
        InterfaceComponent leaveChatButton = Interfaces.getComponent(CLAN_CHAT_INDEX, JOIN_LEAVE_CHAT_COMPONENT);
        return leaveChatButton != null && leaveChatButton.click();
    }
}
