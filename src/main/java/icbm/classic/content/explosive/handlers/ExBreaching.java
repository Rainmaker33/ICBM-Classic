package icbm.classic.content.explosive.handlers;

import icbm.classic.content.explosive.blast.BlastBreech;
import icbm.classic.prefab.tile.EnumTier;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExBreaching extends Explosion
{
    public ExBreaching()
    {
        super("breaching", EnumTier.TWO);
        this.setFuseTime(40);
        //this.missileModelPath = "missiles/tier2/missile_head_breaching.obj";
    }

    @Override
    public void doCreateExplosion(World world, BlockPos pos, Entity entity, float scale)
    {
        new BlastBreech(world, entity, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 2.5f * scale, 7).explode();
    }
}
