package net.server.channel.packet.reader;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.server.PacketReader;
import net.server.channel.packet.pet.PetMovementPacket;
import server.movement.AbsoluteLifeMovement;
import server.movement.ChangeEquip;
import server.movement.JumpDownMovement;
import server.movement.LifeMovementFragment;
import server.movement.RelativeLifeMovement;
import server.movement.TeleportMovement;
import tools.data.input.LittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.exceptions.EmptyMovementException;

public class PetMovementReader implements PacketReader<PetMovementPacket> {
   @Override
   public PetMovementPacket read(SeekableLittleEndianAccessor accessor) {
      int petId = accessor.readInt();
      accessor.readLong();

      List<LifeMovementFragment> commands;
      try {
         commands = parseMovement(accessor);
      } catch (EmptyMovementException exception) {
         return null;
      }

      return new PetMovementPacket(petId, (byte) commands.size(), commands);
   }

   protected List<LifeMovementFragment> parseMovement(LittleEndianAccessor accessor) throws EmptyMovementException {
      List<LifeMovementFragment> results = new ArrayList<>();
      byte numCommands = accessor.readByte();
      for (byte i = 0; i < numCommands; i++) {
         byte command = accessor.readByte();
         switch (command) {
            case 0: // normal move
            case 5:
            case 17:
               absoluteLifeMovement(accessor, results, command);
               break;
            case 1:
            case 2:
            case 6: // fj
            case 12:
            case 13: // Shot-jump-back thing
            case 16: // Float
            case 18:
            case 19: // Springs on maps
            case 20: // Aran Combat Step
            case 22:
               relativeLifeMovement(accessor, results, command);
               break;
            case 3:
            case 4: // tele... -.-
            case 7: // assaulter
            case 8: // assassinate
            case 9: // rush
            case 11: //chair
//                case 14: {
               teleportMovement(accessor, results, command);
               break;
            case 14:
               accessor.skip(9); // jump down (?)
               break;
            case 10: // Change Equip
               results.add(new ChangeEquip(accessor.readByte()));
               break;
                /*case 11: { // Chair
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short fh = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate);
                    cm.setFh(fh);
                    res.add(cm);
                    break;
                }*/
            case 15:
               jumpDownMovement(accessor, results, command);
               break;
            case 21://Causes aran to do weird stuff when attacking o.o
                    /*byte newstate = lea.readByte();
                     short unk = lea.readShort();
                     AranMovement am = new AranMovement(command, null, unk, newstate);
                     res.add(am);*/
               accessor.skip(3);
               break;
            default:
               System.out.println("Unhandled Case:" + command);
               throw new EmptyMovementException(accessor);
         }
         if (results.isEmpty()) {
            throw new EmptyMovementException(accessor);
         }
      }
      return results;
   }

   private void jumpDownMovement(LittleEndianAccessor accessor, List<LifeMovementFragment> results, byte command) {
      short xpos = accessor.readShort();
      short ypos = accessor.readShort();
      short xwobble = accessor.readShort();
      short ywobble = accessor.readShort();
      short fh = accessor.readShort();
      short ofh = accessor.readShort();
      byte newstate = accessor.readByte();
      short duration = accessor.readShort();
      Point pixelsPerSecond = new Point(xwobble, ywobble);
      JumpDownMovement jdm = new JumpDownMovement(command, new Point(xpos, ypos), duration, newstate, pixelsPerSecond, fh, ofh);
      results.add(jdm);
   }

   private void teleportMovement(LittleEndianAccessor accessor, List<LifeMovementFragment> results, byte command) {
      short xpos = accessor.readShort();
      short ypos = accessor.readShort();
      short xwobble = accessor.readShort();
      short ywobble = accessor.readShort();
      byte newstate = accessor.readByte();
      Point pixelsPerSecond = new Point(xwobble, ywobble);
      TeleportMovement tm = new TeleportMovement(command, new Point(xpos, ypos), newstate, pixelsPerSecond);
      results.add(tm);
   }

   private void relativeLifeMovement(LittleEndianAccessor accessor, List<LifeMovementFragment> results, byte command) {
      short xpos = accessor.readShort();
      short ypos = accessor.readShort();
      byte newstate = accessor.readByte();
      short duration = accessor.readShort();
      RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xpos, ypos), duration, newstate);
      results.add(rlm);
   }

   private void absoluteLifeMovement(LittleEndianAccessor lea, List<LifeMovementFragment> res, byte command) {
      short xpos = lea.readShort();
      short ypos = lea.readShort();
      short xwobble = lea.readShort();
      short ywobble = lea.readShort();
      short fh = lea.readShort();
      byte newstate = lea.readByte();
      short duration = lea.readShort();
      Point pixelsPerSecond = new Point(xwobble, ywobble);
      AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate, pixelsPerSecond, fh);
      res.add(alm);
   }
}
