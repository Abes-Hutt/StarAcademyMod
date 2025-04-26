package abeshutt.staracademy.net;

import abeshutt.staracademy.StarAcademyMod;
import abeshutt.staracademy.block.entity.BetterStructureBlockEntity;
import abeshutt.staracademy.block.entity.BetterStructureBlockEntity.Action;
import abeshutt.staracademy.data.adapter.Adapters;
import abeshutt.staracademy.data.bit.BitBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import static abeshutt.staracademy.block.entity.BetterStructureBlockEntity.Action.*;
import static abeshutt.staracademy.data.adapter.basic.EnumAdapter.Mode.ORDINAL;

public class UpdateBetterStructureBlockC2SPacket extends ModPacket<ServerPlayNetworkHandler> {

    public static final Id<UpdateBetterStructureBlockC2SPacket> ID = new Id<>(StarAcademyMod.id("update_better_structure_block_c2s"));

    private BlockPos pos;
    private Action action;
    private StructureBlockMode mode;
    private String templateName;
    private BlockPos offset;
    private Vec3i size;
    private BlockMirror mirror;
    private BlockRotation rotation;
    private String metadata;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private float integrity;
    private long seed;

    public UpdateBetterStructureBlockC2SPacket() {

    }

    public UpdateBetterStructureBlockC2SPacket(BlockPos pos, Action action, StructureBlockMode mode,
                                               String templateName, BlockPos offset, Vec3i size, BlockMirror mirror,
                                               BlockRotation rotation, String metadata, boolean ignoreEntities, boolean showAir,
                                               boolean showBoundingBox, float integrity, long seed) {
        this.pos = pos;
        this.action = action;
        this.mode = mode;
        this.templateName = templateName;
        this.offset = offset;
        this.size = size;
        this.mirror = mirror;
        this.rotation = rotation;
        this.metadata = metadata;
        this.ignoreEntities = ignoreEntities;
        this.showAir = showAir;
        this.showBoundingBox = showBoundingBox;
        this.integrity = integrity;
        this.seed = seed;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void onReceive(ServerPlayNetworkHandler listener) {
        ServerPlayerEntity player = listener.getPlayer();

        if(player.isCreativeLevelTwoOp()) {
            BlockState blockState = player.getWorld().getBlockState(this.pos);

            if(player.getWorld().getBlockEntity(this.pos) instanceof BetterStructureBlockEntity structure) {
                structure.setMode(this.mode);
                structure.setTemplateName(this.templateName);
                structure.setOffset(this.offset);
                structure.setSize(this.size);
                structure.setMirror(this.mirror);
                structure.setRotation(this.rotation);
                structure.setMetadata(this.metadata);
                structure.setIgnoreEntities(this.ignoreEntities);
                structure.setShowAir(this.showAir);
                structure.setShowBoundingBox(this.showBoundingBox);
                structure.setIntegrity(this.integrity);
                structure.setSeed(this.seed);

                if(structure.hasStructureName()) {
                    String string = structure.getTemplateName();

                    if(this.action == SAVE_AREA) {
                        if(structure.saveStructure()) {
                            player.sendMessage(Text.translatable("structure_block.save_success", string), false);
                        } else {
                            player.sendMessage(Text.translatable("structure_block.save_failure", string), false);
                        }
                    } else if(this.action == LOAD_AREA) {
                        if(!structure.isStructureAvailable()) {
                            player.sendMessage(Text.translatable("structure_block.load_not_found", string), false);
                        } else if(structure.loadStructure(player.getServerWorld())) {
                            player.sendMessage(Text.translatable("structure_block.load_success", string), false);
                        } else {
                            player.sendMessage(Text.translatable("structure_block.load_prepare", string), false);
                        }
                    } else if(this.action == SCAN_AREA) {
                        if (structure.detectStructureSize()) {
                            player.sendMessage(Text.translatable("structure_block.size_success", string), false);
                        } else {
                            player.sendMessage(Text.translatable("structure_block.size_failure"), false);
                        }
                    }
                } else {
                    player.sendMessage(Text.translatable("structure_block.invalid_structure_name", this.templateName), false);
                }

                structure.markDirty();
                player.getWorld().updateListeners(this.pos, blockState, blockState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void writeBits(BitBuffer buffer) {
        Adapters.BLOCK_POS.writeBits(this.pos, buffer);
        Adapters.ofEnum(Action.class, ORDINAL).writeBits(this.action, buffer);
        Adapters.ofEnum(StructureBlockMode.class, ORDINAL).writeBits(this.mode, buffer);
        Adapters.UTF_8.writeBits(this.templateName, buffer);
        Adapters.INT_SEGMENTED_3.writeBits(this.offset.getX(), buffer);
        Adapters.INT_SEGMENTED_3.writeBits(this.offset.getY(), buffer);
        Adapters.INT_SEGMENTED_3.writeBits(this.offset.getZ(), buffer);
        Adapters.INT_SEGMENTED_7.writeBits(this.size.getX(), buffer);
        Adapters.INT_SEGMENTED_7.writeBits(this.size.getY(), buffer);
        Adapters.INT_SEGMENTED_7.writeBits(this.size.getZ(), buffer);
        Adapters.ofEnum(BlockMirror.class, ORDINAL).writeBits(this.mirror, buffer);
        Adapters.ofEnum(BlockRotation.class, ORDINAL).writeBits(this.rotation, buffer);
        Adapters.UTF_8.writeBits(this.metadata, buffer);
        Adapters.FLOAT.writeBits(this.integrity, buffer);
        Adapters.LONG.writeBits(this.seed, buffer);
        int state = (this.ignoreEntities ? 0b1 : 0) | (this.showAir ? 0b10 : 0) | (this.showBoundingBox ? 0b100 : 0);
        Adapters.ofBoundedInt(8).writeBits(state, buffer);
    }

    @Override
    public void readBits(BitBuffer buffer) {
        this.pos = Adapters.BLOCK_POS.readBits(buffer).orElseThrow();
        this.action = Adapters.ofEnum(Action.class, ORDINAL).readBits(buffer).orElseThrow();
        this.mode = Adapters.ofEnum(StructureBlockMode.class, ORDINAL).readBits(buffer).orElseThrow();
        this.templateName = Adapters.UTF_8.readBits(buffer).orElseThrow();

        this.offset = new BlockPos(
            Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow(),
            Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow(),
            Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow()
        );

        this.size = new BlockPos(
            Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow(),
            Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow(),
            Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow()
        );

        this.mirror = Adapters.ofEnum(BlockMirror.class, ORDINAL).readBits(buffer).orElseThrow();
        this.rotation = Adapters.ofEnum(BlockRotation.class, ORDINAL).readBits(buffer).orElseThrow();
        this.metadata = Adapters.UTF_8.readBits(buffer).orElseThrow();
        this.integrity = Adapters.FLOAT.readBits(buffer).orElseThrow();
        this.seed = Adapters.LONG.readBits(buffer).orElseThrow();

        int state = Adapters.ofBoundedInt(8).readBits(buffer).orElseThrow();
        this.ignoreEntities = (state & 1) > 0;
        this.showAir = (state & 2) > 0;
        this.showBoundingBox = (state & 4) > 0;
    }

}
