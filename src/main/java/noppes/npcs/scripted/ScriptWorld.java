package noppes.npcs.scripted;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.blocks.tiles.TileBigSign;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.scripted.interfaces.IBlock;
import noppes.npcs.scripted.interfaces.IPos;
import noppes.npcs.scripted.interfaces.IParticle;
import noppes.npcs.scripted.interfaces.ITileEntity;
import noppes.npcs.scripted.interfaces.entity.IEntity;
import noppes.npcs.scripted.interfaces.entity.IPlayer;
import noppes.npcs.scripted.interfaces.handler.data.ISound;
import noppes.npcs.scripted.interfaces.item.IItemStack;
import noppes.npcs.scripted.interfaces.IWorld;

public class ScriptWorld implements IWorld {
	private static Map<String,Object> tempData = new HashMap<String,Object>();
	public WorldServer world;
	public ScriptWorld(WorldServer world){
		this.world = world;
	}

	public static ScriptWorld createNew(int dimensionId) {
		WorldServer[] worlds = CustomNpcs.getServer().worldServers;

		WorldServer world = worlds[0];
		for (WorldServer w : worlds) {
			if (w.provider.dimensionId == dimensionId) {
				world = w;
			}
		}
		return new ScriptWorld(world);
	}

	/**
	 * @return The worlds time
	 */
	public long getTime(){
		return world.getWorldTime();
	}
	
	/**
	 * @return The total world time
	 */
	public long getTotalTime(){
		return world.getTotalWorldTime();
	}

	public boolean areAllPlayersAsleep(){
		return world.areAllPlayersAsleep();
	}

	/**
	 * @param x World position x
	 * @param y World position y
	 * @param z World position z
	 * @return The block at the given position. Returns null if there isn't a block
	 */
	public IBlock getBlock(int x, int y, int z){
		Block block = world.getBlock(x, y, z);
		if(block == null || block.isAir(world, x, y, z))
			return null;
		return NpcAPI.Instance().getIBlock(world, block, new BlockPos(x,y,z));
	}

	public boolean isBlockFreezable(int x, int y, int z){
		return world.isBlockFreezable(x,y,z);
	}

	public boolean isBlockFreezableNaturally(int x, int y, int z){
		return world.isBlockFreezableNaturally(x,y,z);
	}

	public boolean canBlockFreeze(int x, int y, int z, boolean adjacentToWater){
		return world.canBlockFreeze(x,y,z,adjacentToWater);
	}

	public boolean canBlockFreezeBody(int x, int y, int z, boolean adjacentToWater){
		return world.canBlockFreezeBody(x,y,z,adjacentToWater);
	}

	public boolean canSnowAt(int x, int y, int z, boolean checkLight){
		return world.func_147478_e(x,y,z,checkLight);
	}

	public boolean canSnowAtBody(int x, int y, int z, boolean checkLight){
		return world.canSnowAtBody(x,y,z,checkLight);
	}

	public IBlock getTopBlock(int x, int z){
		int y;
		for (y = 63; !world.isAirBlock(x, y + 1, z); ++y) {;}
		Block block = world.getBlock(x, y, z);

		return NpcAPI.Instance().getIBlock(world, block, new BlockPos(x,y,z));
	}

	public int getHeightValue(int x, int z){
		return world.getHeightValue(x,z);
	}

	public int getChunkHeightMapMinimum(int x, int z){
		return world.getChunkHeightMapMinimum(x,z);
	}

	public int getBlockMetadata(int x, int y, int z){
		return world.getBlockMetadata(x,y,z);
	}

	public boolean setBlockMetadataWithNotify(int x, int y, int z, int metadata, int flag){
		return world.setBlockMetadataWithNotify(x,y,z,metadata,flag);
	}

	public boolean canBlockSeeTheSky(int x, int y, int z){
		return world.canBlockSeeTheSky(x,y,z);
	}

	public int getFullBlockLightValue(int x, int y, int z){
		return world.getFullBlockLightValue(x,y,z);
	}

	public int getBlockLightValue(int x, int y, int z){
		return world.getBlockLightValue(x,y,z);
	}

	public void playSoundAtEntity(IEntity entity, String sound, float volume, float pitch){
		world.playSoundAtEntity(entity.getMCEntity(), sound, volume, pitch);
	}

	public void playSoundToNearExcept(IPlayer player, String sound, float volume, float pitch){
		world.playSoundToNearExcept((EntityPlayerMP) player.getMCEntity(), sound, volume, pitch);
	}

	public void playSound(int id, ISound sound) {
		IPlayer[] players = getAllServerPlayers();
		for (IPlayer player : players) {
			if (player.getDimension() == this.getDimensionID()) {
				player.playSound(id, sound);
			}
		}
	}

	public void stopSound(int id) {
		IPlayer[] players = getAllServerPlayers();
		for (IPlayer player : players) {
			if (player.getDimension() == this.getDimensionID()) {
				player.stopSound(id);
			}
		}
	}

	public void pauseSounds() {
		IPlayer[] players = getAllServerPlayers();
		for (IPlayer player : players) {
			if (player.getDimension() == this.getDimensionID()) {
				player.pauseSounds();
			}
		}
	}

	public void continueSounds() {
		IPlayer[] players = getAllServerPlayers();
		for (IPlayer player : players) {
			if (player.getDimension() == this.getDimensionID()) {
				player.continueSounds();
			}
		}
	}

	public void stopSounds() {
		IPlayer[] players = getAllServerPlayers();
		for (IPlayer player : players) {
			if (player.getDimension() == this.getDimensionID()) {
				player.stopSounds();
			}
		}
	}

	public IEntity getEntityByID(int id){
		return NpcAPI.Instance().getIEntity(world.getEntityByID(id));
	}

	public boolean spawnEntityInWorld(IEntity entity){
		return world.spawnEntityInWorld(entity.getMCEntity());
	}

	public IPlayer getClosestPlayerToEntity(IEntity entity, double range){
		return (IPlayer) NpcAPI.Instance().getIEntity(world.getClosestPlayerToEntity(entity.getMCEntity(), range));
	}

	public IPlayer getClosestPlayer(double x, double y, double z, double range){
		return (IPlayer) NpcAPI.Instance().getIEntity(world.getClosestPlayer(x,y,z, range));
	}

	public IPlayer getClosestVulnerablePlayerToEntity(IEntity entity, double range){
		return (IPlayer) NpcAPI.Instance().getIEntity(world.getClosestVulnerablePlayerToEntity(entity.getMCEntity(), range));
	}

	public IPlayer getClosestVulnerablePlayer(double x, double y, double z, double range){
		return (IPlayer) NpcAPI.Instance().getIEntity(world.getClosestVulnerablePlayer(x,y,z, range));
	}

	public int countEntities(IEntity entity){
		return world.countEntities(entity.getMCEntity().getClass());
	}

	public IEntity[] getLoadedEntities() {
		ArrayList<IEntity> list = new ArrayList<>();
		for (Object obj : world.loadedEntityList) {
			list.add(NpcAPI.Instance().getIEntity((Entity) obj));
		}

		return list.toArray(new IEntity[0]);
	}

	public IEntity[] getEntitiesNear(IPos position, double range) {
		ArrayList<IEntity> list = new ArrayList<>();

		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(
				position.getX() - range, position.getY() - range, position.getZ() - range,
				position.getX() + range, position.getY() + range, position.getZ() + range));
		for(Entity entity : entities){
			list.add(NpcAPI.Instance().getIEntity(entity));
		}

		list.sort((e1, e2) -> {
			double dist1 = e1.getPos().distanceTo(position);
			double dist2 = e2.getPos().distanceTo(position);

			if (dist1 > dist2) {
				return 1;
			} else if (dist1 < dist2) {
				return -1;
			}
			return 0;
		});

		return list.toArray(new IEntity[0]);
	}

	public IEntity[] getEntitiesNear(double x, double y, double z, double range) {
		return getEntitiesNear(new ScriptBlockPos(new BlockPos(x,y,z)), range);
	}

	public void setTileEntity(int x, int y, int z, ITileEntity tileEntity){
		world.setTileEntity(x,y,z,tileEntity.getMCTileEntity());
	}

	public void removeTileEntity(int x, int y, int z){
		world.removeTileEntity(x,y,z);
	}

	public boolean isBlockFullCube(int x, int y, int z){
		return world.func_147469_q(x,y,z);
	}

	public long getSeed(){
		return world.getSeed();
	}

	public void setSpawnLocation(int x, int y, int z){
		world.setSpawnLocation(x,y,z);
	}

	public boolean canLightningStrikeAt(int x, int y, int z){
		return world.canLightningStrikeAt(x,y,z);
	}

	public boolean isBlockHighHumidity(int x, int y, int z){
		return world.isBlockHighHumidity(x,y,z);
	}

	/**
	 * @param pos
	 * @return The block at the given position. Returns null if there isn't a block
	 */
	public IBlock getBlock(IPos pos){
		return getBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * @param x World position x
	 * @param y World position y
	 * @param z World position z
	 * @return Text from signs
	 * @since 1.7.10d
	 */
	public String getSignText(int x, int y, int z){
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile instanceof TileBigSign)
			return ((TileBigSign)tile).getText();
		
		if(tile instanceof TileEntitySign){
			TileEntitySign tileSign = (TileEntitySign)tile;
			String s = tileSign.signText[0] + "\n";
			s += tileSign.signText[1] + "\n";
			s += tileSign.signText[2] + "\n";
			s += tileSign.signText[3];
			return s;
		}
		
		return null;
	}
	
	public void setBlock(int x, int y, int z, IBlock block){
		if(block == null || block.getMCBlock().isAir(world, x, y, z)){
			removeBlock(x, y, z);
			return;
		}
		world.setBlock(x, y, z, block.getMCBlock());
	}

	/**
	 * @param x World position x
	 * @param y World position y
	 * @param z World position z
	 * @param item The block to be set
	 */
	public void setBlock(int x, int y, int z, IItemStack item){
		if(item == null){
			removeBlock(x, y, z);
			return;
		}
		Block block = Block.getBlockFromItem(item.getMCItemStack().getItem());
		if(block == null || block == Blocks.air)
			return;
		world.setBlock(x, y, z, block);
	}
	
	/**
	 * @param x World position x
	 * @param y World position y
	 * @param z World position z
	 */
	public void removeBlock(int x, int y, int z){
		world.setBlock(x, y, z, Blocks.air);
	}

	public IPos rayCastPos(double[] startPos, double[] lookVector, int maxDistance, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
		if (startPos.length != 3 || lookVector.length != 3) {
			return null;
		}

		IPos pos;
		Vec3 currentPos = Vec3.createVectorHelper(startPos[0], startPos[1], startPos[2]); int rep = 0;

		while (rep++ < maxDistance + 10) {
			currentPos = currentPos.addVector(lookVector[0], lookVector[1], lookVector[2]);
			pos = new ScriptBlockPos(new BlockPos(currentPos.xCoord, currentPos.yCoord, currentPos.zCoord));

			IBlock block = getBlock((int)currentPos.xCoord, (int)currentPos.yCoord, (int)currentPos.zCoord);
			if (block != null && stopOnBlock) {
				if ((!stopOnLiquid || block.getMCBlock() instanceof BlockLiquid)
					&& (!stopOnCollision || block.canCollide()))
				return pos;
			}

			double distance = Math.pow(
					Math.pow(currentPos.xCoord-startPos[0],2)
							+Math.pow(currentPos.yCoord-startPos[1],2)
							+Math.pow(currentPos.zCoord-startPos[2],2)
					, 0.5);
			if (distance > maxDistance) {
				return pos;
			}
		}

		return null;
	}

	public IPos rayCastPos(double[] startPos, double[] lookVector, int maxDistance) {
		return rayCastPos(startPos,lookVector,maxDistance,true, false, false);
	}

	public IPos rayCastPos(IPos startPos, IPos lookVector, int maxDistance, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
		return rayCastPos(new double[] {startPos.getX(), startPos.getY(), startPos.getZ()}, lookVector.normalize(), maxDistance, stopOnBlock, stopOnLiquid, stopOnCollision);
	}

	public IPos rayCastPos(IPos startPos, IPos lookVector, int maxDistance) {
		return rayCastPos(new double[] {startPos.getX(), startPos.getY(), startPos.getZ()}, lookVector.normalize(), maxDistance, true, false, false);
	}

	public IBlock rayCastBlock(double[] startPos, double[] lookVector, int maxDistance, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
		return getBlock(rayCastPos(startPos,lookVector,maxDistance,stopOnBlock,stopOnLiquid,stopOnCollision));
	}

	public IBlock rayCastBlock(double[] startPos, double[] lookVector, int maxDistance) {
		return rayCastBlock(startPos, lookVector, maxDistance, true, false, false);
	}

	public IBlock rayCastBlock(IPos startPos, IPos lookVector, int maxDistance, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
		return rayCastBlock(new double[] {startPos.getX(), startPos.getY(), startPos.getZ()}, lookVector.normalize(), maxDistance, stopOnBlock, stopOnLiquid, stopOnCollision);
	}

	public IBlock rayCastBlock(IPos startPos, IPos lookVector, int maxDistance) {
		return rayCastBlock(new double[] {startPos.getX(), startPos.getY(), startPos.getZ()}, lookVector.normalize(), maxDistance);
	}

	public IPos getNearestAir(IPos pos, int maxHeight) {
		if (pos == null) return null;
		IPos currentPos = pos;
		IBlock block = null; int rep = 0;
		while (rep++ < maxHeight) {
			//check +x
			currentPos = currentPos.add(1, 0, 0);
			block = getBlock(currentPos);
			if (block == null) break;
			//check -x
			currentPos = currentPos.add(-2, 0, 0);
			block = getBlock(currentPos);
			if (block == null) break;
			//check +z
			currentPos = currentPos.add(1, 0, 1);
			block = getBlock(currentPos);
			if (block == null) break;
			//check -z
			currentPos = currentPos.add(0, 0, -2);
			block = getBlock(currentPos);
			if (block == null) break;
			//check up 1
			currentPos = currentPos.add(0, 1, 1);
			block = getBlock(currentPos);
			if (block == null) break;
		}
		return currentPos;
	}

	public IEntity[] rayCastEntities(double[] startPos, double[] lookVector,
										  int maxDistance, double offset, double range,
										  boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
		ArrayList<IEntity> entities = new ArrayList<>();

		Vec3 currentPos = Vec3.createVectorHelper(startPos[0], startPos[1], startPos[2]); int rep = 0;
		currentPos = currentPos.addVector(lookVector[0]*offset, lookVector[1]*offset, lookVector[2]*offset);

		while (rep++ < maxDistance + 10) {
			currentPos = currentPos.addVector(lookVector[0], lookVector[1], lookVector[2]);
			IPos pos = new ScriptBlockPos(new BlockPos(currentPos.xCoord, currentPos.yCoord, currentPos.zCoord));
			IBlock block = getBlock(pos);

			if (block != null && stopOnBlock) {
				if ((!stopOnLiquid || block.getMCBlock() instanceof BlockLiquid)
						&& (!stopOnCollision || block.canCollide()))
					return entities.toArray(new IEntity[0]);
			}

			IEntity[] entitiesNear = getEntitiesNear(pos,range);
			for (IEntity entity : entitiesNear) {
				if (!entities.contains(entity)) {
					entities.add(entity);
				}
			}

			double distance = Math.pow(
					Math.pow(currentPos.xCoord-startPos[0],2)
							+Math.pow(currentPos.yCoord-startPos[1],2)
							+Math.pow(currentPos.zCoord-startPos[2],2)
					, 0.5);
			if (distance > maxDistance) {
				break;
			}
		}

		return entities.toArray(new IEntity[0]);
	}

	public IEntity[] rayCastEntities(IPos startPos, IPos lookVector,
									 int maxDistance, double offset, double range,
									 boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
		return rayCastEntities(new double[] {startPos.getX(), startPos.getY(), startPos.getZ()}, lookVector.normalize(), maxDistance, offset, range, stopOnBlock, stopOnLiquid, stopOnCollision);
	}

	public IEntity[] rayCastEntities(double[] startPos, double[] lookVector,
									 int maxDistance, double offset, double range) {
		return rayCastEntities(startPos, lookVector, maxDistance, offset, range, true, false, true);
	}

	public IEntity[] rayCastEntities(IPos startPos, IPos lookVector,
									 int maxDistance, double offset, double range) {
		return rayCastEntities(new double[] {startPos.getX(), startPos.getY(), startPos.getZ()}, lookVector.normalize(), maxDistance, offset, range, true, false, true);
	}

	public boolean canSeeSky(int x, int y, int z) {
		return world.canBlockSeeTheSky(x, y, z);
	}

	public boolean canSeeSky(IPos pos) {
		return canSeeSky(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * @param name The name of the player to be returned
	 * @return The Player with name. Null is returned when the player isnt found
	 */
	public IPlayer getPlayer(String name){
		EntityPlayer player = world.getPlayerEntityByName(name);
		if(player == null)
			return null;
		return (IPlayer) NpcAPI.Instance().getIEntity(player);
	}

	public IPlayer getPlayerByUUID(String uuid){
		return (IPlayer) NpcAPI.Instance().getIEntity(world.func_152378_a(UUID.fromString(uuid)));
	}
	
	/**
	 * @param time The world time to be set
	 */
	public void setTime(long time){
		world.setWorldTime(time);
	}

	/**
	 * @return Whether or not its daytime
	 */
	public boolean isDay(){
		return world.getWorldTime() % 24000 < 12000;
	}
	
	/**
	 * @return Whether or not its currently raining
	 */
	public boolean isRaining(){
		return world.getWorldInfo().isRaining();
	}
	
	/**
	 * @param bo Set if it's raining
	 */
	public void setRaining(boolean bo){
		world.getWorldInfo().setRaining(bo);
	}
	
	/**
	 * @param x The x position
	 * @param y The y position
	 * @param z The z position
	 */
	public void thunderStrike(double x, double y, double z){
        world.addWeatherEffect(new EntityLightningBolt(world, x, y, z));
	}
	
	/**
	 * Sends a packet from the server to the client everytime its called. Probably should not use this too much.
	 * @param particle Particle name. Particle name list: http://minecraft.gamepedia.com/Particles
	 * @param x The x position
	 * @param y The y position
	 * @param z The z position
	 * @param dx Usually used for the x motion
	 * @param dy Usually used for the y motion
	 * @param dz Usually used for the z motion
	 * @param speed Speed of the particles, usually between 0 and 1
	 * @param count Particle count
	 */
	public void spawnParticle(String particle, double x, double y, double z, double dx, double dy, double dz, double speed, int count){
		world.func_147487_a(particle, x, y, z, count, dx, dy, dz, speed);
	}
	
	/**
	 * @param id The items name
	 * @param damage The damage value
	 * @param size The number of items in the item
	 * @return Returns the item
	 */
	public IItemStack createItem(String id, int damage, int size){
		return NpcAPI.Instance().createItem(id,damage,size);
	}

	/**
	 * @param directory The particle's texture directory. Use only forward slashes when writing a directory. Example: "customnpcs:textures/particle/bubble.png"
	 * @return Returns ScriptParticle object
	 */
	@Deprecated
	public IParticle createEntityParticle(String directory){
		return NpcAPI.Instance().createEntityParticle(directory);
	}

	/**
	 * @param key Get temp data for this key
	 * @return Returns the stored temp data
	 */
	public Object getTempData(String key){
		return tempData.get(key);
	}
	
	/**
	 * Tempdata gets cleared when the server restarts. All worlds share the same temp data.
	 * @param key The key for the data stored
	 * @param value The data stored
	 */
	public void setTempData(String key, Object value){
		tempData.put(key, value);
	}
	
	/**
	 * @param key The key thats going to be tested against the temp data
	 * @return Whether or not temp data containes the key
	 */
	public boolean hasTempData(String key){
		return tempData.containsKey(key);
	}
	
	/**
	 * @param key The key for the temp data to be removed
	 */
	public void removeTempData(String key){
		tempData.remove(key);
	}
	
	/**
	 * Removes all tempdata
	 */
	public void clearTempData(){
		tempData.clear();
	}
	
	/**
	 * @param key The key of the data to be returned
	 * @return Returns the stored data
	 */
	public Object getStoredData(String key){
		NBTTagCompound compound = ScriptController.Instance.compound;
		if(!compound.hasKey(key))
			return null;
		NBTBase base = compound.getTag(key);
		if(base instanceof NBTPrimitive)
			return ((NBTPrimitive)base).func_150286_g();
		return ((NBTTagString)base).func_150285_a_();
	}
	
	/**
	 * Stored data persists through world restart. Unlike tempdata only Strings and Numbers can be saved
	 * @param key The key for the data stored
	 * @param value The data stored. This data can be either a Number or a String. Other data is not stored
	 */
	public void setStoredData(String key, Object value){
		NBTTagCompound compound = ScriptController.Instance.compound;
		if(value instanceof Number)
			compound.setDouble(key, ((Number) value).doubleValue());
		else if(value instanceof String)
			compound.setString(key, (String)value);
		ScriptController.Instance.shouldSave = true;
	}
	
	/**
	 * @param key The key of the data to be checked
	 * @return Returns whether or not the stored data contains the key
	 */
	public boolean hasStoredData(String key){
		return ScriptController.Instance.compound.hasKey(key);
	}
	
	/**
	 * @param key The key of the data to be removed
	 */
	public void removeStoredData(String key){
		ScriptController.Instance.compound.removeTag(key);
		ScriptController.Instance.shouldSave = true;
	}
	
	/**
	 * Remove all stored data
	 */
	public void clearStoredData(){
		ScriptController.Instance.compound = new NBTTagCompound();
		ScriptController.Instance.shouldSave = true;
	}
	
	/**
	 * @param x Position x
	 * @param y Position y
	 * @param z Position z
	 * @param range Range of the explosion
	 * @param fire Whether or not the explosion does fire damage
	 * @param grief Whether or not the explosion does damage to blocks
	 */
	public void explode(double x, double y, double z, float range, boolean fire, boolean grief){
		world.newExplosion(null, x, y, z, range, fire, grief);
	}

	@Deprecated
	public IPlayer[] getAllServerPlayers(){
		List<EntityPlayer> list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		IPlayer[] arr = new IPlayer[list.size()];
		for(int i = 0; i < list.size(); i++){
			arr[i] = (IPlayer) NpcAPI.Instance().getIEntity(list.get(i));
		}
		
		return arr;
	}

	@Deprecated
	public String[] getPlayerNames() {
		IPlayer[] players = getAllServerPlayers();
		String[] names = new String[players.length];
		for (int i = 0; i < names.length; ++i) names[i] = players[i].getDisplayName();
		return names;
	}

	/**
	 * @since 1.7.10c
	 * @param x Position x
	 * @param z Position z
	 * @return Returns the name of the biome
	 */
	public String getBiomeName(int x, int z){
		return world.getBiomeGenForCoords(x, z).biomeName;
	}
	
	/**
	 * Lets you spawn a server side cloned entity
	 * @param x The x position the clone will be spawned at
	 * @param y The y position the clone will be spawned at
	 * @param z The z position the clone will be spawned at
	 * @param tab The tab in which the clone is
	 * @param name Name of the cloned entity
	 * @return Returns the entity which was spawned
	 */
	public IEntity spawnClone(int x, int y, int z, int tab, String name, boolean ignoreProtection){
		NBTTagCompound compound = ServerCloneController.Instance.getCloneData(null, name, tab);
		if(compound == null)
			return null;
		Entity entity;
		if (!ignoreProtection) {
			entity = NoppesUtilServer.spawnCloneWithProtection(compound, x, y, z, world);
		} else {
			entity = NoppesUtilServer.spawnClone(compound, x, y, z, world);
		}
		return entity == null ? null : NpcAPI.Instance().getIEntity(entity);
	}

	public IEntity spawnClone(int x, int y, int z, int tab, String name) {
		return this.spawnClone(x,y,z,tab,name,true);
	}
	
	public ScriptScoreboard getScoreboard(){
		return new ScriptScoreboard();
	}

	/**
	 * @since 1.7.10c
	 * Expert use only
	 * @return Returns minecraft world object
	 */
	public WorldServer getMCWorld(){
		return this.world;
	}

	public int getDimensionID(){
		return world.provider.dimensionId;
	}
}
