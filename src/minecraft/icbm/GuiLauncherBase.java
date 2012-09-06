package icbm;

import icbm.ContainerLauncherBase;
import icbm.TileEntityLauncherBase;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiLauncherBase extends GuiContainer
{
    private TileEntityLauncherBase tileEntity;

    private int containerWidth;
    private int containerHeight;

    public GuiLauncherBase(InventoryPlayer par1InventoryPlayer, TileEntityLauncherBase tileEntity)
    {
        super(new ContainerLauncherBase(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString(tileEntity.getInvName(), 48, 6, 4210752);
        this.fontRenderer.drawString("Place Missile", 63, 28, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH+"MissileLauncher.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        containerWidth = (this.width - this.xSize) / 2;
        containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
    }
}