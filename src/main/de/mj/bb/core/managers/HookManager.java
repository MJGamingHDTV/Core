package main.de.mj.bb.core.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.clans.api.ClansManager;
import main.de.mj.bb.core.CoreSpigot;
import main.de.mj.bb.core.utils.Data;
import main.de.mj.bb.core.utils.ServerType;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.milkbowl.vault.economy.Economy;
import nl.chimpgamer.networkmanagerapi.NetworkManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public class HookManager {

    private final CoreSpigot coreSpigot;
    private final ConsoleCommandSender sender;
    private final String prefix = new Data().getPrefix();
    private Economy economy;
    private ProtocolManager protocolManager;
    private NetworkManagerPlugin networkManagerPlugin;
    private LuckPermsApi luckPermsApi;
    private PAFPlayerManager pafPlayerManager;
    private ClansManager clansManager;

    public HookManager(@NotNull CoreSpigot coreSpigot) {
        this.coreSpigot = coreSpigot;
        sender = coreSpigot.getSender();
    }

    public Economy getEconomy() {
        return economy;
    }

    public void hook(ServerType serverType) {
        if (serverType.equals(ServerType.LOBBY)) {
            sender.sendMessage(prefix + "§2try to hook in Vault...");
            if (!setupEconomy()) {
                sender.sendMessage(String.format("§c[%s] - Vault-Dependecy wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
                return;
            } else {
                sender.sendMessage(prefix + "§2hooked into: Vault");
                sender.sendMessage(prefix + "§2hooked into Economy-Plugin: " + economy.getName());
            }

            sender.sendMessage(prefix + "§etry to hook into TimoCloud...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("TimoCloud") != null) {
                sender.sendMessage(prefix + "§ehooked into: TimoCloud");
            } else {
                sender.sendMessage(String.format("§c[%s] - TimoCloud wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§dtry to hook into LuckPerms...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                this.luckPermsApi = LuckPerms.getApi();
                sender.sendMessage(prefix + "§dhooked into: LuckPerms");
            } else {
                sender.sendMessage(String.format("§c[%s] - LuckPerms wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§6try to hook into FriendsAPIForPartyAndFriends...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("FriendsAPIForPartyAndFriends") != null) {
                this.pafPlayerManager = PAFPlayerManager.getInstance();
                sender.sendMessage(prefix + "§6hooked into: FriendsAPIForPartyAndFriends");
            } else {
                sender.sendMessage(String.format("§c[%s] - FriendsAPIForPartyAndFriends wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§5try to hook into Clans-Spigot-Part...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("Clans-Spigot-Part") != null) {
                this.clansManager = ClansManager.getInstance();
                sender.sendMessage(prefix + "§5hooked into: Clans-Spigot-Part");
            } else {
                sender.sendMessage(String.format("§c[%s] - Clans-Spigot-Part wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§atry to hook into NetworkManager...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("NetworkManager") != null) {
                this.networkManagerPlugin = (NetworkManagerPlugin) coreSpigot.getServer().getPluginManager().getPlugin("NetworkManager");
                sender.sendMessage(prefix + "§ahooked into: NetworkManager");
            } else {
                sender.sendMessage(String.format("§c[%s] - NetworkManagerBridge wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§etry to hook into ProtocolLib...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                sender.sendMessage(prefix + "§ehooked into: ProtocolLib");
                blockTabComplete();
                crashFixer();
                sender.sendMessage(prefix + "§eBlockTabComplete-Module was successfully enabled!");
            } else {
                sender.sendMessage(String.format("§c[%s] - ProtocolLib wasn't found - disable TabComplete!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§dtry to hook into HolographicDisplays...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("HolographicDisplays") != null) {
                sender.sendMessage(prefix + "§dhooked into: HolographicDisplays");
            } else {
                sender.sendMessage(String.format("§c[%s] - HolographicDisplays wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
        }
        if (serverType.equals(ServerType.SKY_PVP)) {
            sender.sendMessage(prefix + "§6try to hook into FriendsAPIForPartyAndFriends...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("FriendsAPIForPartyAndFriends") != null) {
                this.pafPlayerManager = PAFPlayerManager.getInstance();
                sender.sendMessage(prefix + "§6hooked into: FriendsAPIForPartyAndFriends");
            } else {
                sender.sendMessage(String.format("§c[%s] - FriendsAPIForPartyAndFriends wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§5try to hook into Clans-Spigot-Part...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("Clans-Spigot-Part") != null) {
                this.clansManager = ClansManager.getInstance();
                sender.sendMessage(prefix + "§5hooked into: Clans-Spigot-Part");
            } else {
                sender.sendMessage(String.format("§c[%s] - Clans-Spigot-Part wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§dtry to hook into LuckPerms...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                this.luckPermsApi = LuckPerms.getApi();
                sender.sendMessage(prefix + "§dhooked into: LuckPerms");
            } else {
                sender.sendMessage(String.format("§c[%s] - LuckPerms wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§etry to hook into ProtocolLib...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                sender.sendMessage(prefix + "§ehooked into: ProtocolLib");
                blockTabComplete();
                sender.sendMessage(prefix + "§eBlockTabComplete-Module was successfully enabled!");
            } else {
                sender.sendMessage(String.format("§c[%s] - ProtocolLib wasn't found - disable TabComplete!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
        }
        if (serverType.equals(ServerType.CITY_BUILD)) {
            sender.sendMessage(prefix + "§6try to hook into FriendsAPIForPartyAndFriends...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("FriendsAPIForPartyAndFriends") != null) {
                this.pafPlayerManager = PAFPlayerManager.getInstance();
                sender.sendMessage(prefix + "§6hooked into: FriendsAPIForPartyAndFriends");
            } else {
                sender.sendMessage(String.format("§c[%s] - FriendsAPIForPartyAndFriends wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§5try to hook into Clans-Spigot-Part...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("Clans-Spigot-Part") != null) {
                this.clansManager = ClansManager.getInstance();
                sender.sendMessage(prefix + "§5hooked into: Clans-Spigot-Part");
            } else {
                sender.sendMessage(String.format("§c[%s] - Clans-Spigot-Part wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§dtry to hook into LuckPerms...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                this.luckPermsApi = LuckPerms.getApi();
                sender.sendMessage(prefix + "§dhooked into: LuckPerms");
            } else {
                sender.sendMessage(String.format("§c[%s] - LuckPerms wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§etry to hook into ProtocolLib...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                sender.sendMessage(prefix + "§ehooked into: ProtocolLib");
                blockTabComplete();
                sender.sendMessage(prefix + "§eBlockTabComplete-Module was successfully enabled!");
            } else {
                sender.sendMessage(String.format("§c[%s] - ProtocolLib wasn't found - disable TabComplete!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
        }
        if (serverType.equals(ServerType.DEFAULT)) {
            sender.sendMessage(prefix + "§6try to hook into FriendsAPIForPartyAndFriends...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("FriendsAPIForPartyAndFriends") != null) {
                this.pafPlayerManager = PAFPlayerManager.getInstance();
                sender.sendMessage(prefix + "§6hooked into: FriendsAPIForPartyAndFriends");
            } else {
                sender.sendMessage(String.format("§c[%s] - FriendsAPIForPartyAndFriends wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§5try to hook into Clans-Spigot-Part...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("Clans-Spigot-Part") != null) {
                this.clansManager = ClansManager.getInstance();
                sender.sendMessage(prefix + "§5hooked into: Clans-Spigot-Part");
            } else {
                sender.sendMessage(String.format("§c[%s] - Clans-Spigot-Part wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§dtry to hook into LuckPerms...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                this.luckPermsApi = LuckPerms.getApi();
                sender.sendMessage(prefix + "§dhooked into: LuckPerms");
            } else {
                sender.sendMessage(String.format("§c[%s] - LuckPerms wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§etry to hook into ProtocolLib...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                sender.sendMessage(prefix + "§ehooked into: ProtocolLib");
                blockTabComplete();
                sender.sendMessage(prefix + "§eBlockTabComplete-Module was successfully enabled!");
            } else {
                sender.sendMessage(String.format("§c[%s] - ProtocolLib wasn't found - disable TabComplete!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
        }
        if (serverType.equals(ServerType.BAU_SERVER)) {
            sender.sendMessage(prefix + "§6try to hook into FriendsAPIForPartyAndFriends...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("FriendsAPIForPartyAndFriends") != null) {
                this.pafPlayerManager = PAFPlayerManager.getInstance();
                sender.sendMessage(prefix + "§6hooked into: FriendsAPIForPartyAndFriends");
            } else {
                sender.sendMessage(String.format("§c[%s] - FriendsAPIForPartyAndFriends wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§5try to hook into Clans-Spigot-Part...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("Clans-Spigot-Part") != null) {
                this.clansManager = ClansManager.getInstance();
                sender.sendMessage(prefix + "§5hooked into: Clans-Spigot-Part");
            } else {
                sender.sendMessage(String.format("§c[%s] - Clans-Spigot-Part wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§dtry to hook into LuckPerms...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                this.luckPermsApi = LuckPerms.getApi();
                sender.sendMessage(prefix + "§dhooked into: LuckPerms");
            } else {
                sender.sendMessage(String.format("§c[%s] - LuckPerms wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§etry to hook into ProtocolLib...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                sender.sendMessage(prefix + "§ehooked into: ProtocolLib");
                blockTabComplete();
                sender.sendMessage(prefix + "§eBlockTabComplete-Module was successfully enabled!");
            } else {
                sender.sendMessage(String.format("§c[%s] - ProtocolLib wasn't found - disable TabComplete!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
        }
        if (serverType.equals(ServerType.BED_WARS)) {
            sender.sendMessage(prefix + "§6try to hook into FriendsAPIForPartyAndFriends...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("FriendsAPIForPartyAndFriends") != null) {
                this.pafPlayerManager = PAFPlayerManager.getInstance();
                sender.sendMessage(prefix + "§6hooked into: FriendsAPIForPartyAndFriends");
            } else {
                sender.sendMessage(String.format("§c[%s] - FriendsAPIForPartyAndFriends wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§5try to hook into Clans-Spigot-Part...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("Clans-Spigot-Part") != null) {
                this.clansManager = ClansManager.getInstance();
                sender.sendMessage(prefix + "§5hooked into: Clans-Spigot-Part");
            } else {
                sender.sendMessage(String.format("§c[%s] - Clans-Spigot-Part wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§dtry to hook into LuckPerms...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                this.luckPermsApi = LuckPerms.getApi();
                sender.sendMessage(prefix + "§dhooked into: LuckPerms");
            } else {
                sender.sendMessage(String.format("§c[%s] - LuckPerms wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§btry to hook into VIPHide...");
            sender.sendMessage(prefix + "§etry to hook into ProtocolLib...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                sender.sendMessage(prefix + "§ehooked into: ProtocolLib");
                blockTabComplete();
                sender.sendMessage(prefix + "§eBlockTabComplete-Module was successfully enabled!");
            } else {
                sender.sendMessage(String.format("§c[%s] - ProtocolLib wasn't found - disable TabComplete!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            return;
        }
        if (serverType.equals(ServerType.VORBAUEN)) {
            sender.sendMessage(prefix + "§6try to hook into FriendsAPIForPartyAndFriends...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("FriendsAPIForPartyAndFriends") != null) {
                this.pafPlayerManager = PAFPlayerManager.getInstance();
                sender.sendMessage(prefix + "§6hooked into: FriendsAPIForPartyAndFriends");
            } else {
                sender.sendMessage(String.format("§c[%s] - FriendsAPIForPartyAndFriends wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }

            sender.sendMessage(prefix + "§5try to hook into Clans-Spigot-Part...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("Clans-Spigot-Part") != null) {
                this.clansManager = ClansManager.getInstance();
                sender.sendMessage(prefix + "§5hooked into: Clans-Spigot-Part");
            } else {
                sender.sendMessage(String.format("§c[%s] - Clans-Spigot-Part wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§dtry to hook into PlotSquared...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("PlotSquared") != null) {
                this.protocolManager = ProtocolLibrary.getProtocolManager();
                sender.sendMessage(prefix + "§ehooked into: PlotSquared");
            } else {
                sender.sendMessage(String.format("§c[%s] - PlotSquared wasn't found - disable Core!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
            sender.sendMessage(prefix + "§dtry to hook into LuckPerms...");
            if (coreSpigot.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                this.luckPermsApi = LuckPerms.getApi();
                sender.sendMessage(prefix + "§dhooked into: LuckPerms");
            } else {
                sender.sendMessage(String.format("§c[%s] - LuckPerms wasn't found - disable Plugin!", coreSpigot.getDescription().getName()));
                coreSpigot.getServer().getPluginManager().disablePlugin(coreSpigot);
            }
        }
    }

    private void blockTabComplete() {
        this.protocolManager.addPacketListener(new PacketAdapter(coreSpigot, ListenerPriority.HIGHEST, PacketType.Play.Client.TAB_COMPLETE) {
            public void onPacketReceiving(PacketEvent packetEvent) {
                if (!packetEvent.getPlayer().hasPermission("player.team")) {
                    if (packetEvent.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
                        try {
                            PacketContainer packetContainer = packetEvent.getPacket();
                            String message = (packetContainer.getSpecificModifier(String.class).read(0)).toLowerCase();
                            if ((message.startsWith("")) && (!message.startsWith("  "))) {
                                packetEvent.setCancelled(true);
                            }
                        } catch (FieldAccessException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void crashFixer() {
        this.protocolManager.addPacketListener(new PacketAdapter(coreSpigot, ListenerPriority.HIGHEST, PacketType.Play.Client.CUSTOM_PAYLOAD) {

            public void onPacketReceiving(PacketEvent event) {
                coreSpigot.getModuleManager().getCrashFixer().checkPacket(event);
            }
        });
        Bukkit.getScheduler().runTaskTimer(coreSpigot, () -> {
            Iterator<Map.Entry<Player, Long>> iterator = coreSpigot.getModuleManager().getCrashFixer().getPACKET_USAGE().entrySet().iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next().getKey();
                if (player.isOnline() && player.isValid()) continue;
                iterator.remove();
            }
        }, 20L, 20L);
    }

    private boolean setupEconomy() {
        if (coreSpigot.getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("Vault nicht gefunden");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = coreSpigot.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            System.out.println("RegisteredServiceProvider Fehler");
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public CoreSpigot getCoreSpigot() {
        return this.coreSpigot;
    }

    public ConsoleCommandSender getSender() {
        return this.sender;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public ProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    public NetworkManagerPlugin getNetworkManagerPlugin() {
        return this.networkManagerPlugin;
    }

    public LuckPermsApi getLuckPermsApi() {
        return this.luckPermsApi;
    }

    public PAFPlayerManager getPafPlayerManager() {
        return this.pafPlayerManager;
    }

    public ClansManager getClansManager() {
        return this.clansManager;
    }
}
