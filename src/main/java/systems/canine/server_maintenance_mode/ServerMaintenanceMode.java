package systems.canine.server_maintenance_mode;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import systems.canine.server_maintenance_mode.command.MaintenanceCommand;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = ServerMaintenanceMode.MODID)
public class ServerMaintenanceMode {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "server_maintenance_mode";

    private static Boolean enabled = false;

    public ServerMaintenanceMode(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            // Register ourselves for server and other game events we are interested in.
            NeoForge.EVENT_BUS.register(this);
        }

        MaintenanceCommand.init(modEventBus);
    }

    public static void enable() {
        ServerMaintenanceMode.enabled = true;
    }

    public static void disable() {
        ServerMaintenanceMode.enabled = false;
    }

    public static Boolean isEnabled() {
        return ServerMaintenanceMode.enabled;
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
        if (!ServerMaintenanceMode.enabled) {
            return;
        }

        Player player = event.getEntity();
        GameProfile profile = player.getGameProfile();

        MinecraftServer server = player.getServer();

        Boolean isOp = server.getPlayerList().isOp(profile);

        if (!isOp) {
            PlayerList playerList = server.getPlayerList();
            for (ServerPlayer serverPlayer : Lists.newArrayList(playerList.getPlayers())) {
                if (serverPlayer.getGameProfile().equals(profile)) {
                    Component reason = Component.translatable("server_maintenance_mode.kick_message");
                    serverPlayer.connection.disconnect(reason);
                }
            }
        }
    }
}