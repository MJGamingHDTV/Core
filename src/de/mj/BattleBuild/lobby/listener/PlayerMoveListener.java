package de.mj.BattleBuild.lobby.listener;

import de.mj.BattleBuild.lobby.Lobby;
import de.mj.BattleBuild.lobby.utils.Particle;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {

    public PlayerMoveListener(Lobby lobby) {
        lobby.setListener(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent moveEvent) {
        Player player = moveEvent.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            if (SettingsListener.waterjump.contains(player)) {
                Location loc = player.getLocation();
                Block block = loc.getBlock();
                if (block.isLiquid()) {
                    player.setAllowFlight(true);
                    player.setVelocity(player.getLocation().getDirection().setY(4));
                    player.setAllowFlight(false);
                }
            }
            if (SettingsListener.doppelsprung.contains(player)) {
                if (player.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR || player.getLocation().add(0, -1, 0).getBlock().getType() != Material.WATER) {
                    if (player.isOnGround()) {
                        player.setAllowFlight(true);
                        player.setFlying(false);
                    }
                }
            }
            if ((player.getLocation().getBlock().getType() == Material.IRON_PLATE)) {
                if (SettingsListener.jumppads.contains(player)) {
                    Vector v = player.getLocation().getDirection().multiply(2.0D).setY(1.0D);
                    player.setVelocity(v);
                    player.playSound(player.getLocation(), Sound.DIG_SAND, 1, 1);
                    player.playEffect(player.getLocation(), Effect.LAVA_POP, null);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoubleJump(PlayerToggleFlightEvent flightEvent) {
        Player player = flightEvent.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && SettingsListener.doppelsprung.contains(player)) {
            flightEvent.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setVelocity(player.getLocation().getDirection().multiply(0.9D).add(new Vector(0, 0.9, 0)));
            player.playSound(player.getLocation(), Sound.LAVA_POP, 1, 1);
            Particle particle = new Particle(EnumParticle.FIREWORKS_SPARK, player.getLocation().add(0, 2.25, 0), true, 0.25f, 0.25f, 0.25f, 0, 100);
            particle.sendAll();
        }
    }
}