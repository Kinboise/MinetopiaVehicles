package nl.mtvehicles.core.Movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;
import nl.mtvehicles.core.Events.VehicleLeaveEvent;
import nl.mtvehicles.core.Infrastructure.Models.Vehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleMovement extends PacketAdapter {
    public static HashMap<String, Double> speed = new HashMap<>();
    float yaw;
    int w;

    public VehicleMovement(final Plugin main) {
        super(main, ListenerPriority.HIGHEST, new PacketType[]{PacketType.Play.Client.STEER_VEHICLE});
        this.yaw = 0.0f;
        this.w = 0;
    }

    public void onPacketReceiving(final PacketEvent event) {
        final PacketPlayInSteerVehicle ppisv = (PacketPlayInSteerVehicle) event.getPacket().getHandle();
        final Player p = event.getPlayer();
        if (p.getVehicle() == null) {
            return;
        }
        String ken = p.getVehicle().getCustomName().replace("MTVEHICLES_MAINSEAT_", "");


        ArmorStand as = VehicleLeaveEvent.autostand.get("MTVEHICLES_MAIN_"+ken);
        ArmorStand as2 = VehicleLeaveEvent.autostand.get("MTVEHICLES_SKIN_"+ken);
        ArmorStand as3 = VehicleLeaveEvent.autostand.get("MTVEHICLES_MAINSEAT_"+ken);
        final EntityArmorStand stand = ((CraftArmorStand)as2).getHandle();
        stand.setLocation(as.getLocation().getX(), as.getLocation().getY(), as.getLocation().getZ(), as.getLocation().getYaw(), as.getLocation().getPitch());
        mainSeat(as, as3, ken);
        seat(as, ken);
        Location loc = as.getLocation();
        Location location = new Location(loc.getWorld(), loc.getX(), loc.getY()-0.2, loc.getZ(), loc.getYaw(), loc.getPitch());
        if (location.getBlock().getType().equals(Material.AIR) || location.getBlock().getType().equals(Material.WATER)){
            KeyW(as, speed.get(ken), -0.8);
        } else {
            KeyW(as, speed.get(ken), 0.0);
        }

        final float forward = ppisv.b();
        final float side = ppisv.a();
        final boolean space = ppisv.c();
        boolean w;
        boolean s;
        if (forward > 0.0f) {

            if (speed.get(ken) > Vehicle.getByPlate(ken).getMaxSpeed()) {

            } else {
                speed.put(ken, speed.get(ken) + Vehicle.getByPlate(ken).getAcceleratieSpeed());
            }

            w = true;
            s = false;
        } else if (forward < 0.0f) {
            if (speed.get(ken) <= 0){
                speed.put(ken, 0.0);
            }else {

                speed.put(ken, speed.get(ken)-Vehicle.getByPlate(ken).getBrakingSpeed());
            }
            w = false;
            s = true;
        } else {

            if (speed.get(ken) <= 0){
                speed.put(ken, 0.0);
            }else {

                speed.put(ken, speed.get(ken)-Vehicle.getByPlate(ken).getAftrekkenSpeed());
            }

            w = false;
            s = false;
        }
        boolean a;
        boolean d;
        if (side > 0.0f) {
            KeyA(as, ken);

            a = true;
            d = false;
        } else if (side < 0.0f) {
            KeyD(as, ken);

            a = false;
            d = true;
        } else {
            a = false;
            d = false;
        }

    }

    public static void KeyW(ArmorStand as, double a, double b) {
        as.setVelocity(new Vector(as.getLocation().getDirection().multiply((double)a).getX(), b, as.getLocation().getDirection().multiply((double)a).getZ()));
    }

    public static void KeyD(ArmorStand a, String ken) {
        Location loc = a.getLocation();
        EntityArmorStand stand = ((CraftArmorStand)a).getHandle();
        int draai = Vehicle.getByPlate(ken).getRotateSpeed();
        stand.setLocation(a.getLocation().getX(), a.getLocation().getY(), a.getLocation().getZ(), loc.getYaw() + draai, loc.getPitch());
    }

    public static void KeyA(ArmorStand a, String ken) {
        Location loc = a.getLocation();
        EntityArmorStand stand = ((CraftArmorStand)a).getHandle();
        int draai = Vehicle.getByPlate(ken).getRotateSpeed();
        stand.setLocation(a.getLocation().getX(), a.getLocation().getY(), a.getLocation().getZ(), loc.getYaw() - draai, loc.getPitch());
    }

    public static void mainSeat(ArmorStand main, ArmorStand seatas, String ken){

            Vehicle vehicle = Vehicle.getByPlate(ken);
            List<Map<String, Double>> seats = (List<Map<String, Double>>) vehicle.getVehicleData().get("seats");

            for (int i = 1; i <= seats.size(); i++) {

                Map<String, Double> seat = seats.get(i - 1);
                if (i == 1) {

                    double xOffset = seat.get("x");
                    double yOffset = seat.get("y");
                    double zOffset = seat.get("z");
                    Location locvp = main.getLocation().clone();
                    Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
                    float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(fbvp.getYaw())));
                    float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(fbvp.getYaw())));
                    Location loc = new Location(main.getWorld(), (double) xvp, main.getLocation().getY() + yOffset, (double) zvp, fbvp.getYaw(), fbvp.getPitch());
                    EntityArmorStand stand = ((CraftArmorStand) seatas).getHandle();
                    stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), fbvp.getYaw(), loc.getPitch());
                }
        }

    }

    public static void seat(ArmorStand main, String ken){

        Vehicle vehicle = Vehicle.getByPlate(ken);
        List<Map<String, Double>> seats = (List<Map<String, Double>>) vehicle.getVehicleData().get("seats");

        for (int i = 1; i <= seats.size(); i++) {

            Map<String, Double> seat = seats.get(i - 1);
            if (i > 1) {
                ArmorStand seatas = VehicleLeaveEvent.autostand.get("MTVEHICLES_SEAT" + i + "_" + ken);
                double xOffset = seat.get("x");
                double yOffset = seat.get("y");
                double zOffset = seat.get("z");
                Location locvp = main.getLocation().clone();
                Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
                float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(fbvp.getYaw())));
                float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(fbvp.getYaw())));
                Location loc = new Location(main.getWorld(), (double) xvp, main.getLocation().getY() + yOffset, (double) zvp, fbvp.getYaw(), fbvp.getPitch());
                EntityArmorStand stand = ((CraftArmorStand) seatas).getHandle();
                stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), fbvp.getYaw(), loc.getPitch());
            }
        }

    }
}