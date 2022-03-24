package nl.mtvehicles.core.commands.vehiclesubs;

import de.tr7zw.changeme.nbtapi.NBTItem;
import nl.mtvehicles.core.infrastructure.helpers.TextUtils;
import nl.mtvehicles.core.infrastructure.models.MTVehicleSubCommand;
import nl.mtvehicles.core.infrastructure.models.Vehicle;
import nl.mtvehicles.core.infrastructure.models.VehicleUtils;
import nl.mtvehicles.core.infrastructure.modules.ConfigModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VehicleRemoveRider extends MTVehicleSubCommand {
    public VehicleRemoveRider() {
        this.setPlayerCommand(true);
    }

    @Override
    public boolean execute(CommandSender sender, Command cmd, String s, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!item.hasItemMeta() || !(new NBTItem(item)).hasKey("mtvehicles.kenteken")) {
            sendMessage(TextUtils.colorize(ConfigModule.messagesConfig.getMessage("noVehicleInHand")));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(ConfigModule.messagesConfig.getMessage("useRemoveRider"));
            return true;
        }

        String ken = VehicleUtils.getLicensePlate(item);
        Player of = Bukkit.getPlayer(args[1]);
        Vehicle vehicle = VehicleUtils.getByLicensePlate(ken);

        if (of == null || !of.hasPlayedBefore()) {
            player.sendMessage(ConfigModule.messagesConfig.getMessage("playerNotFound"));
            return true;
        }

        assert vehicle != null;
        List<String> riders = vehicle.getRiders();
        riders.remove(of.getUniqueId().toString());
        vehicle.setRiders(riders);
        vehicle.save();
        player.sendMessage(ConfigModule.messagesConfig.getMessage("memberChange"));

        return true;
    }
}
