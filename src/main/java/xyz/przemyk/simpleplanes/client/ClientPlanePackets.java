package xyz.przemyk.simpleplanes.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Client-side bridge for player input / plane synchronization.
 * This class must be invoked only from client-side branches.
 * Keeping the client-only distributor out of common entity classes
 * avoids eager client class references during dedicated-server startup.
 */
public final class ClientPlanePackets {
    private ClientPlanePackets() {}

    public static void sendToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
