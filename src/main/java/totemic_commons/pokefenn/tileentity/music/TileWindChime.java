package totemic_commons.pokefenn.tileentity.music;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import totemic_commons.pokefenn.ModBlocks;
import totemic_commons.pokefenn.network.PacketHandler;
import totemic_commons.pokefenn.network.client.PacketWindChime;
import totemic_commons.pokefenn.recipe.HandlerInitiation;
import totemic_commons.pokefenn.tileentity.TileTotemic;
import totemic_commons.pokefenn.util.TotemUtil;

/**
 * Created by Pokefenn.
 * Licensed under MIT (If this is one of my Mods)
 */
public class TileWindChime extends TileTotemic
{
    public boolean isPlaying;
    public int currentTime;
    public int cooldownPassed;
    public boolean canPlay;
    public float currentRotation;

    public TileWindChime()
    {
        isPlaying = false;
        currentTime = 0;
        cooldownPassed = 0;
        canPlay = true;
        currentRotation = 0;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        World world = worldObj;

        if(worldObj.isRemote)
        {

            if(isPlaying)
                currentRotation += 0.03F;
            else
                currentRotation = 0F;
            if(currentRotation <= 0F)
                currentRotation = 0F;
            else if(currentRotation >= 4F)
                currentRotation = 4F;

            if(worldObj.getWorldTime() % 40L == 0)
                if(isPlaying)
                    world.playSound(xCoord, yCoord, zCoord, "totemic:windChime", 1.0F, 1.0F, false);
        }

        if(isPlaying)
        {
            currentTime++;
        }

        //This is for how long it can play
        if(currentTime > 20 * 12)
        {
            isPlaying = false;
            currentTime = 0;
        }

        if(!world.isRemote)
        {
            if(!canPlay)
                cooldownPassed++;
            if(cooldownPassed > 20)
            {
                canPlay = true;
                cooldownPassed = 0;
            }

            Random rand = new Random();

            if(world.getWorldTime() % 20L == 0 && rand.nextInt(60) == 0)
            {
                isPlaying = true;
                PacketHandler.sendAround(new PacketWindChime(xCoord, yCoord, zCoord, isPlaying), this);
                int radius = 5;

                for(int i = -radius; i <= radius; i++)
                    for(int j = -radius; j <= radius; j++)
                        for(int k = -radius; k <= radius; k++)
                            if(world.getBlock(xCoord + i, yCoord + j, zCoord + k) == ModBlocks.windChime)
                            {
                                TileWindChime tileWindChime = (TileWindChime) world.getTileEntity(xCoord + i, yCoord + j, zCoord + k);
                                tileWindChime.isPlaying = true;
                            }
            }

            if(isPlaying)
                if(world.getWorldTime() % 50L == 0 && rand.nextInt(2) == 0)
                {
                    int bonus = world.getBlock(xCoord, yCoord + 1, zCoord).isLeaves(world, xCoord, yCoord + 1, zCoord)
                            ? world.rand.nextInt(3) : 0;
                    TotemUtil.playMusicForCeremony(this, HandlerInitiation.windChime, 0, bonus);
                    ((WorldServer)world).func_147487_a("note", xCoord + 0.5D, yCoord - 0.8D, zCoord + 0.5D, 2, 0.0D, 0.0D, 0.0D, 0.0D);
                }


        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("currentTime", currentTime);
        nbtTagCompound.setBoolean("isPlaying", isPlaying);
        nbtTagCompound.setFloat("currentRotation", currentRotation);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);
        currentTime = nbtTagCompound.getInteger("currentTime");
        isPlaying = nbtTagCompound.getBoolean("isPlaying");
        currentRotation = nbtTagCompound.getFloat("currentRotation");
    }
}
