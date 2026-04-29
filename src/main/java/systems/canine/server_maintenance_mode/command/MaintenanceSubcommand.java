package systems.canine.server_maintenance_mode.command;

import net.minecraft.util.StringRepresentable;

public enum MaintenanceSubcommand implements StringRepresentable {
    ON(0, "on"),
    OFF(0, "off"),
    STATUS(0, "status");

    private final int id;
    private final String subcommand_name;

    private MaintenanceSubcommand(int id, String name) {
        this.id = id;
        this.subcommand_name = name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String getSerializedName() {
        return this.subcommand_name;
    }

    public static MaintenanceSubcommand fromString(String name) {
        return byName(name);
    }

    public static MaintenanceSubcommand byName(String name) {
        for (MaintenanceSubcommand cmd : MaintenanceSubcommand.values()) {
            if (cmd.subcommand_name.equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        return null;
    }
}