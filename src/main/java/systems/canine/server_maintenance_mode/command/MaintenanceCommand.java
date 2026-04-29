package systems.canine.server_maintenance_mode.command;

import java.util.function.Supplier;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import systems.canine.server_maintenance_mode.ServerMaintenanceMode;

public class MaintenanceCommand {
    private static final int OP_LEVEL = 4;
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister
        .create(Registries.COMMAND_ARGUMENT_TYPE, ServerMaintenanceMode.MODID);

    private static final Supplier<SingletonArgumentInfo<MaintenanceCommandArgumentType>> MAINTENANCE_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES
        .register("maintenance_subcommand",
            () -> ArgumentTypeInfos.registerByClass(MaintenanceCommandArgumentType.class,
                SingletonArgumentInfo.contextFree(MaintenanceCommandArgumentType::newInstance)));

    private MaintenanceCommand() {
    }

    public static void init(IEventBus modBus) {
        COMMAND_ARGUMENT_TYPES.register(modBus);

        NeoForge.EVENT_BUS.addListener(MaintenanceCommand::registerCommands);
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
            Commands.literal("maintenance")
                .requires(cs -> cs.hasPermission(OP_LEVEL))
                .executes(context -> handleSubcommand(context.getSource(), false, MaintenanceSubcommand.STATUS))
                .then(
                    Commands.argument("subcommand", MaintenanceCommandArgumentType.newInstance())
                        .executes(context -> handleSubcommand(context.getSource(), false,
                            context.getArgument("subcommand", MaintenanceSubcommand.class)))));
    }

    private static int handleSubcommand(CommandSourceStack source, boolean needs_update, MaintenanceSubcommand cmd) {
        switch (cmd) {
        case MaintenanceSubcommand.ON:
            ServerMaintenanceMode.enable();
            break;
        case MaintenanceSubcommand.OFF:
            ServerMaintenanceMode.disable();
            break;
        default:
            break;
        }

        if (ServerMaintenanceMode.isEnabled()) {
            source.sendSuccess(() -> Component.literal("Maintenance mode is enabled."), false);
        } else {
            source.sendSuccess(() -> Component.literal("Maintenance mode is disabled."), false);
        }

        return 0;
    }
}