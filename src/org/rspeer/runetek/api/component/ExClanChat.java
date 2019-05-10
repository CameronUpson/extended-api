package org.rspeer.runetek.api.component;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.chatter.ClanChat;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;

/**
 * 
 *
 * @author 2baad4u2
 */
public class ExClanChat {

    private static final int CLAN_CHAT_INDEX;
    private static final int JOIN_LEAVE_CHAT_COMPONENT = 22;
    private static final int CLAN_SETUP_COMPONENT = 24;

    private static final int CLAN_CHAT_SETUP_INDEX = 94;
    private static final int NAME_COMPONENT = 10;
    private static final int ENTER_COMPONENT = 13;
    private static final int TALK_COMPONENT = 16;
    private static final int KICK_COMPONENT = 19;
    private static final int CLOSE_SETUP_COMPONENT = 3;
    private static final int CLOSE_SETUP_SUBCOMPONENT = 13;

    static {
        CLAN_CHAT_INDEX = InterfaceComposite.CLAN_CHAT.getGroup();
    }

    public static boolean isInChannel() {
        return ClanChat.isInChannel();
    }

    public static boolean isNotInChannel() {
        return !isInChannel();
    }

    public static boolean isOpen() {
        return Tab.CLAN_CHAT.isOpen();
    }

    public static boolean open() {
        return isOpen() || (Tabs.open(Tab.CLAN_CHAT) && Time.sleepUntil(ExClanChat::isOpen, 1200L));
    }

    /**
     * Join clan chat channel with specified username (will leave current clan chat if already in one).
     *
     * @param username of the clan chat to join
     * @return true if successfully tried to join clan chat
     */
    public static boolean join(String username) {
        if (username == null|| username.isEmpty() || !Time.sleepUntil(ExClanChat::open, 1200L) || !leave()) return false;
        Time.sleepUntil(ExClanChat::isNotInChannel, 1200L);
        InterfaceComponent joinChatButton = Interfaces.getComponent(CLAN_CHAT_INDEX, JOIN_LEAVE_CHAT_COMPONENT);
        if (joinChatButton != null && joinChatButton.click()) {
            Time.sleepUntil(EnterInput::isOpen, 1200L);
            return EnterInput.initiate(username);
        }
        return false;
    }

    /**
     * Join your own clan chat (will leave current clan chat if already in one).
     *
     * @return true if successfully tried to join clan chat
     */
    public static boolean join() {
        if (!Time.sleepUntil(ExClanChat::open, 1200L) || !setup() || !leave()) return false;
        Time.sleepUntil(ExClanChat::isNotInChannel, 1200L);
        InterfaceComponent joinChatButton = Interfaces.getComponent(CLAN_CHAT_INDEX, JOIN_LEAVE_CHAT_COMPONENT);
        if (joinChatButton != null && joinChatButton.click()) {
            Time.sleepUntil(EnterInput::isOpen, 1200L);
            InterfaceComponent usernameComp = Interfaces.getComponent(InterfaceComposite.CHATBOX.getGroup(), 57);
            return usernameComp != null && EnterInput.initiate(usernameComp.getText().split(":")[0]);
        }
        return false;
    }

    public static boolean leave() {
        if (!isInChannel()) return true;
        if (!Time.sleepUntil(ExClanChat::open, 1200L)) return false;
        InterfaceComponent leaveChatButton = Interfaces.getComponent(CLAN_CHAT_INDEX, JOIN_LEAVE_CHAT_COMPONENT);
        return leaveChatButton != null && leaveChatButton.click();
    }

    public static boolean isSetupOpen() {
        return Interfaces.isVisible(CLAN_CHAT_SETUP_INDEX, 0);
    }

    public static boolean openSetup() {
        if (isSetupOpen()) return true;
        if (!open()) return false;
        InterfaceComponent clanSetup = Interfaces.getComponent(CLAN_CHAT_INDEX, CLAN_SETUP_COMPONENT);
        return clanSetup != null && clanSetup.click() && Time.sleepUntil(ExClanChat::isSetupOpen, 1200L);
    }

    public static boolean closeSetup() {
        if (!isSetupOpen()) return true;
        InterfaceComponent closeSetup = Interfaces.getComponent(CLAN_CHAT_SETUP_INDEX, CLOSE_SETUP_COMPONENT, CLOSE_SETUP_SUBCOMPONENT);
        return closeSetup != null && closeSetup.click();
    }

    /**
     * Setup player's own clan chat with default values.
     *
     * @return true if clan chat is setup.
     */
    public static boolean setup() {
        return isChatEnabled() || setClanName("a");
    }

    /**
     * Setup player's own clan chat with specified params.
     *
     * @param name of the clan chat
     * @param enter minimum rank needed to enter chat
     * @param talk minimum rank needed to talk in chat
     * @param kick minimum rank needed to kick a player from chat
     * @return true if clan chat is setup.
     */
    public static boolean setup(String name, Permission enter, Permission talk, Permission kick) {
        return setClanName(name)
                && setEnterPermission(enter)
                && setTalkPermission(talk)
                && setKickPermission(kick);
    }

    private static Permission getClanPermission(int component) {
        if (!Time.sleepUntil(ExClanChat::openSetup, 1200L)) return null;
        InterfaceComponent permission = Interfaces.getComponent(CLAN_CHAT_SETUP_INDEX, component);
        return permission != null && permission.getText() != null ?
                Permission.getPermission(permission.getText()) : null;
    }

    private static boolean setClanPermission(Permission permission, int component) {
        if (permission == null || !Time.sleepUntil(ExClanChat::openSetup, 1200L)) return false;
        InterfaceComponent permissionComponent = Interfaces.getComponent(CLAN_CHAT_SETUP_INDEX, component);
        return permissionComponent != null && permissionComponent.interact(permission.value);
    }

    public static Permission getEnterPermission() {
        return getClanPermission(ENTER_COMPONENT);
    }

    public static boolean setEnterPermission(Permission permission) {
        return setClanPermission(permission, ENTER_COMPONENT);
    }

    public static Permission getTalkPermission() {
        return getClanPermission(TALK_COMPONENT);
    }

    public static boolean setTalkPermission(Permission permission) {
        return setClanPermission(permission, TALK_COMPONENT);
    }

    public static Permission getKickPermission() {
        return getClanPermission(KICK_COMPONENT);
    }

    public static boolean setKickPermission(Permission permission) {
        if (permission != null && permission.ordinal() < 3) return false;
        return setClanPermission(permission, KICK_COMPONENT);
    }

    public static String getSetupClanName() {
        if (!Time.sleepUntil(ExClanChat::openSetup, 1200L)) return "";
        InterfaceComponent nameComponent = Interfaces.getComponent(CLAN_CHAT_SETUP_INDEX, NAME_COMPONENT);
        return nameComponent != null ? nameComponent.getText() : "";
    }

    public static boolean setClanName(String name) {
        if (name == null || name.isEmpty() || !Time.sleepUntil(ExClanChat::openSetup, 1200L)) return false;
        InterfaceComponent nameComponent = Interfaces.getComponent(CLAN_CHAT_SETUP_INDEX, NAME_COMPONENT);
        if (nameComponent == null || !nameComponent.interact("Set prefix")) return false;
        Time.sleepUntil(EnterInput::isOpen, 1200L);
        return EnterInput.initiate(name);
    }

    public static boolean isChatEnabled() {
        return !isChatDisabled();
    }

    public static boolean isChatDisabled() {
        return getSetupClanName().equals("Chat disabled");
    }

    public static boolean disableChat() {
        if (!Time.sleepUntil(ExClanChat::openSetup, 1200L)) return false;
        InterfaceComponent nameComponent = Interfaces.getComponent(CLAN_CHAT_SETUP_INDEX, NAME_COMPONENT);
        return nameComponent != null && nameComponent.interact("Disable");
    }

    public enum Permission {
        ANYONE("Anyone"),
        ANY_FRIENDS("Any friends"),
        RECRUIT("Recruit+"),
        CORPORAL("Corporal+"),
        SERGEANT("Sergeant+"),
        LIEUTENANT("Lieutenant+"),
        CAPTAIN("Captain+"),
        GENERAL("General+"),
        ONLY_ME("Only me");

        private final String value;

        Permission(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        static Permission getPermission(String value) {
            for (Permission p : values())
                if (p.getValue().equals(value)) return p;
            return null;
        }
    }
}
