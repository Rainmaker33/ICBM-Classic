package icbm.wanyi.b;

import icbm.core.di.TIC2Runnable;
import icbm.wanyi.ItHuoLuanQi;
import icbm.zhapin.ZhuYaoZhaPin;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneProvider;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TYinGanQi extends TIC2Runnable implements IRedstoneProvider, IPacketReceiver
{
	private static final int MAX_DISTANCE = 30;

	public short frequency = 0;

	public boolean isDetect = false;

	public Vector3 minCoord = new Vector3(9, 9, 9);
	public Vector3 maxCoord = new Vector3(9, 9, 9);

	public byte mode = 0;

	private int yongZhe = 0;

	public boolean isInverted = false;

	@Override
	public void initiate()
	{
		super.initiate();
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 20 == 0)
			{
				if (this.yongZhe > 0)
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
				}

				if (!this.isDisabled())
				{
					boolean isDetectThisCheck = false;

					if (this.wattsReceived >= this.getRequest().getWatts())
					{
						AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord - minCoord.x, this.yCoord - minCoord.y, this.zCoord - minCoord.z, this.xCoord + maxCoord.x + 1D, this.yCoord + maxCoord.y + 1D, this.zCoord + maxCoord.z + 1D);
						List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);

						for (EntityLiving entity : entitiesNearby)
						{
							if (entity instanceof EntityPlayer && (this.mode == 0 || this.mode == 1))
							{
								boolean gotDisrupter = false;

								for (ItemStack inventory : ((EntityPlayer) entity).inventory.mainInventory)
								{
									if (inventory != null)
									{
										if (inventory.getItem() instanceof ItHuoLuanQi)
										{
											if (((ItHuoLuanQi) inventory.getItem()).getFrequency(inventory) == this.frequency)
											{
												gotDisrupter = true;
												break;
											}
										}
									}
								}

								if (gotDisrupter)
								{
									if (this.isInverted)
									{
										isDetectThisCheck = true;
										break;
									}

									continue;
								}

								if (!this.isInverted)
								{
									isDetectThisCheck = true;
								}
							}
							else if (!this.isInverted && !(entity instanceof EntityPlayer) && (this.mode == 0 || this.mode == 2))
							{
								isDetectThisCheck = true;
								break;
							}
						}

						if (!this.worldObj.isRemote)
						{
							this.wattsReceived = 0;
						}
					}

					if (isDetectThisCheck != this.isDetect)
					{
						this.isDetect = isDetectThisCheck;
						this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
					}
				}
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		double sendDian = this.wattsReceived;

		if (sendDian > 0)
		{
			sendDian = this.getRequest().getWatts();
		}

		return PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this, 1, sendDian, this.frequency, this.mode, this.isInverted, this.minCoord.intX(), this.minCoord.intY(), this.minCoord.intZ(), this.maxCoord.intX(), this.maxCoord.intY(), this.maxCoord.intZ());
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			final int ID = dataStream.readInt();

			if (ID == -1)
			{
				if (dataStream.readBoolean())
				{
					this.yongZhe++;
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
				}
				else
				{
					this.yongZhe--;
				}
			}
			else if (ID == 1)
			{
				this.wattsReceived = dataStream.readDouble();
				this.frequency = dataStream.readShort();
				this.mode = dataStream.readByte();
				this.isInverted = dataStream.readBoolean();
				this.minCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())));
				this.maxCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())));
			}
			else if (ID == 2)
			{
				this.mode = dataStream.readByte();
			}
			else if (ID == 3)
			{
				this.frequency = dataStream.readShort();
			}
			else if (ID == 4)
			{
				this.minCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())));
			}
			else if (ID == 5)
			{
				this.maxCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())), Math.max(0, Math.min(MAX_DISTANCE, dataStream.readInt())));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		this.mode = nbt.getByte("mode");
		this.frequency = nbt.getShort("frequency");
		this.isInverted = nbt.getBoolean("isInverted");

		this.minCoord = Vector3.readFromNBT(nbt.getCompoundTag("minCoord"));
		this.maxCoord = Vector3.readFromNBT(nbt.getCompoundTag("maxCoord"));
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setShort("frequency", this.frequency);
		nbt.setByte("mode", this.mode);
		nbt.setBoolean("isInverted", this.isInverted);

		nbt.setCompoundTag("minCoord", this.minCoord.writeToNBT(new NBTTagCompound()));
		nbt.setCompoundTag("maxCoord", this.maxCoord.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public boolean isPoweringTo(ForgeDirection side)
	{
		return this.isDetect;
	}

	@Override
	public boolean isIndirectlyPoweringTo(ForgeDirection side)
	{
		return this.isDetect;
	}

	@Override
	public ElectricityPack getRequest()
	{
		return new ElectricityPack(8 / this.getVoltage(), this.getVoltage());
	}
}