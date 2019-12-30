package se.mickelus.tetra.blocks.hammer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.animation.TileEntityRendererAnimation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ObjectHolder;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.ToolTypes;
import se.mickelus.tetra.blocks.Materials;
import se.mickelus.tetra.blocks.TetraBlock;
import se.mickelus.tetra.capabilities.Capability;

public class HammerHeadBlock extends TetraBlock {
    public static final String unlocalizedName = "hammer_head";

    public static final VoxelShape shape = makeCuboidShape(2, 14, 2, 14, 16, 14);

    @ObjectHolder(TetraMod.MOD_ID + ":" + unlocalizedName)
    public static HammerHeadBlock instance;

    public HammerHeadBlock() {
        super(Properties.create(Materials.forgedBlock)
                .sound(SoundType.METAL)
                .harvestTool(ToolTypes.hammer)
                .harvestLevel(4)
                .hardnessAndResistance(10F, 25));

        setRegistryName(unlocalizedName);

        hasItem = true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(HammerHeadTile.class, new TileEntityRendererAnimation<>());
    }

    @Override
    public void addInformation(final ItemStack stack, @Nullable final IBlockReader world, final List<ITextComponent> tooltip,
            final ITooltipFlag advanced) {
        tooltip.add(new TranslationTextComponent("forged_description"));
    }

    @Override
    public Collection<Capability> getCapabilities(World world, BlockPos pos, BlockState blockState) {
        BlockPos basePos = pos.offset(Direction.UP);
        if (world.getBlockState(basePos).getBlock() instanceof HammerBaseBlock) {
            HammerBaseBlock baseBlock = (HammerBaseBlock) world.getBlockState(basePos).getBlock();

            if (baseBlock.isFueled(world, basePos)) {
                return Collections.singletonList(Capability.hammer);
            }
        }
        return super.getCapabilities(world, pos, blockState);
    }

    @Override
    public int getCapabilityLevel(World world, BlockPos pos, BlockState blockState, Capability capability) {
        BlockPos basePos = pos.offset(Direction.UP);
        if (Capability.hammer.equals(capability) && world.getBlockState(basePos).getBlock() instanceof HammerBaseBlock) {
            HammerBaseBlock baseBlock = (HammerBaseBlock) world.getBlockState(basePos).getBlock();

            if (baseBlock.isFueled(world, basePos)) {
                return baseBlock.getHammerLevel(world, basePos);
            }
        }
        return super.getCapabilityLevel(world, pos, blockState, capability);
    }

    @Override
    public ItemStack onCraftConsumeCapability(World world, BlockPos pos, BlockState blockState, ItemStack targetStack, PlayerEntity player,
            boolean consumeResources) {
        BlockPos basePos = pos.offset(Direction.UP);
        if (consumeResources && world.getBlockState(basePos).getBlock() instanceof HammerBaseBlock) {
            HammerBaseBlock baseBlock = (HammerBaseBlock) world.getBlockState(basePos).getBlock();
            baseBlock.consumeFuel(world, basePos);

            baseBlock.applyEffects(world, basePos, targetStack, player);

            ((HammerHeadTile) world.getTileEntity(pos)).activate();
            world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 3f, (float) (0.5 + Math.random() * 0.1));
        }
        return targetStack;
    }

    @Override
    public ItemStack onActionConsumeCapability(World world, BlockPos pos, BlockState blockState, ItemStack targetStack, PlayerEntity player,
            boolean consumeResources) {
        BlockPos basePos = pos.offset(Direction.UP);
        if (consumeResources && world.getBlockState(basePos).getBlock() instanceof HammerBaseBlock) {
            HammerBaseBlock baseBlock = (HammerBaseBlock) world.getBlockState(basePos).getBlock();
            baseBlock.consumeFuel(world, basePos);

            ((HammerHeadTile) world.getTileEntity(pos)).activate();
            world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 3f, (float) (0.5 + Math.random() * 0.1));
        }
        return targetStack;
    }

    @Override
    public VoxelShape getShape(final BlockState blockState, final IBlockReader world, final BlockPos pos, final ISelectionContext context) {
        return shape;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return new HammerHeadTile();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
