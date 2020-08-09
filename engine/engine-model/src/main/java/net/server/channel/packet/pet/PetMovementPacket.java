package net.server.channel.packet.pet;

import java.util.List;

import net.server.MaplePacket;
import server.movement.LifeMovementFragment;

public record PetMovementPacket(int petId, byte numberOfCommands,
                                List<LifeMovementFragment> commands) implements MaplePacket {
}
