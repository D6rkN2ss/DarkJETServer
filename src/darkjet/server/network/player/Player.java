package darkjet.server.network.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import darkjet.server.Leader;
import darkjet.server.Logger;
import darkjet.server.entity.Entity;
import darkjet.server.level.Level;
import darkjet.server.math.Vector;
import darkjet.server.math.Vector2;
import darkjet.server.network.packets.minecraft.AddPlayerPacket;
import darkjet.server.network.packets.minecraft.AdventureSettingPacket;
import darkjet.server.network.packets.minecraft.AnimatePacket;
import darkjet.server.network.packets.minecraft.ClientConnectPacket;
import darkjet.server.network.packets.minecraft.ClientHandshakePacket;
import darkjet.server.network.packets.minecraft.DisconnectPacket;
import darkjet.server.network.packets.minecraft.LoginPacket;
import darkjet.server.network.packets.minecraft.LoginStatusPacket;
import darkjet.server.network.packets.minecraft.MessagePacket;
import darkjet.server.network.packets.minecraft.MinecraftIDs;
import darkjet.server.network.packets.minecraft.MovePlayerPacket;
import darkjet.server.network.packets.minecraft.PingPacket;
import darkjet.server.network.packets.minecraft.PlayerEquipmentPacket;
import darkjet.server.network.packets.minecraft.PongPacket;
import darkjet.server.network.packets.minecraft.RemoveBlockPacket;
import darkjet.server.network.packets.minecraft.RemovePlayerPacket;
import darkjet.server.network.packets.minecraft.ServerHandshakePacket;
import darkjet.server.network.packets.minecraft.SetHealthPacket;
import darkjet.server.network.packets.minecraft.SetSpawnPositionPacket;
import darkjet.server.network.packets.minecraft.SetTimePacket;
import darkjet.server.network.packets.minecraft.StartGamePacket;
import darkjet.server.network.packets.minecraft.UpdateBlockPacket;
import darkjet.server.network.packets.minecraft.UseItemPacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket;
import darkjet.server.network.packets.raknet.MinecraftDataPacket.InternalDataPacket;
import darkjet.server.network.player.ChunkSender.ChunkCache;
import darkjet.server.tasker.TaskManager.TaskThread;

/**
 * Minecraft Packet Handler
 * @author Blue Electric
 */
public final class Player extends Entity {
	public final String IP;
	public final int port;
	public final short mtu;
	public final long clientID;
	
	public boolean isLogin = false;
	
	public String name;
	
	int lastSequenceNum = 0;
	
	public final InternalDataPacketQueue Queue;
	
	private long lastPacketReceived = System.currentTimeMillis();
	
	private final Worker worker;
	protected final ChunkSender chunkSender;
	public final HashMap<Vector2, ChunkCache> getUsingChunks() {
		return chunkSender.useChunks;
	}
	
	protected Level level;
	public final Level getLevel() {
		return level;
	}
	
	public Player(Leader leader, String IP, int port, short mtu, long clientID) throws Exception {
		super( leader, leader.entity.getNewEntityID() );
		this.IP = IP;
		this.port = port;
		this.mtu = mtu;
		this.clientID = clientID;
		
		x = 128F; y = 4F; z = 128F;
		
		Queue = new InternalDataPacketQueue(this, 3939);
		
		level = leader.level.getLoadedLevel("world");
		chunkSender = new ChunkSender(this);
		
		worker = new Worker();
		leader.task.addThread(worker);
	}
	
	public final class Worker extends TaskThread {
		private long lastChunkSender = -1;
		
		public Worker() {
			setName( "Player: " + IP);
		}
		
		@Override
		public final void launch() {
			while ( !isInterrupted() ) {
				try {
					if( lastChunkSender + 100 < System.currentTimeMillis() ) {
						if( name != null ) {
							if( !chunkSender.updateChunk() ) {
								lastChunkSender = System.currentTimeMillis();
							}
						}
					}
					if( name == null || isLogin ) {
						Queue.update();
					}
					checkTimeout();
					Thread.sleep(1);
				} catch (InterruptedException ie) { 
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Save
	 * Float X
	 * Float Y
	 * Float Z
	 */
	public final void load() throws Exception {
		if(name == null) { return; }
		File file = leader.player.getPlayerFile(name);
		if( !file.exists() ) { return; }
		DataInputStream dis = new DataInputStream( new FileInputStream( file ) );
		x = dis.readFloat();
		y = dis.readFloat();
		z = dis.readFloat();
		dis.close();
	}
	
	public final void save() throws Exception {
		if(name == null) { return; }
		DataOutputStream dos = new DataOutputStream( new FileOutputStream( leader.player.getPlayerFile(name) ) );
		dos.writeFloat(x);
		dos.writeFloat(y);
		dos.writeFloat(z);
		dos.close();
	}
	
	public final void checkTimeout() throws Exception {
		if( lastPacketReceived + 30000 < System.currentTimeMillis() ) {
			close("Timeout");
		}
	}
	
	//External Part
	public final void sendChat(String message) throws Exception {
		MessagePacket pak = new MessagePacket(message);
		Queue.addMinecraftPacket(pak);
	}
	
	@Override
	public final void close() throws Exception {
		close("No reason");
	}

	public final void close(String reason) throws Exception {
		leader.player.removePlayer(IP);
		save();
		worker.close();
		DisconnectPacket dp = new DisconnectPacket();
		Queue.addMinecraftPacket(dp); Queue.send();
		chunkSender.destroy();
		
		RemovePlayerPacket rpp = new RemovePlayerPacket();
		rpp.eid = EID; rpp.clientID = clientID;
		leader.player.broadcastPacket(rpp, false, this);
		Logger.print(Logger.INFO, "%s(%s) was disconnected, %s", name, IP, reason);
		leader.chat.broadcastChat(String.format("%s was disconnected, %s", name, reason), this);
		super.close();
	}
	
	//Internal Part
	
	protected void InitPlayer() throws Exception {
		leader.player.noticeLogin(this);
		isLogin = true;
		
		Queue.addMinecraftPacket( new SetTimePacket(0) );
		MovePlayerPacket player = new MovePlayerPacket(EID, x, y, z, yaw, pitch, bodyYaw, false);
		Queue.addMinecraftPacket(player);
		AdventureSettingPacket adp = new AdventureSettingPacket(0x20);
		Queue.addMinecraftPacket(adp);
		
		//Add player for other Player
		AddPlayerPacket app = new AddPlayerPacket(Player.this);
		leader.player.broadcastPacket(app, false, Player.this);
		
		sendChat( leader.config.getServerWelcomeMessage().replace("@name", name) );
		
		Logger.print(Logger.INFO, "%s(%s) is Connected to Server!", name, IP);
	}

	public final void handlePacket(InternalDataPacket ipck) throws Exception {
		lastPacketReceived = System.currentTimeMillis();
		switch( ipck.buffer[0] ) {
			case MinecraftIDs.PING:
				PingPacket ping = new PingPacket(); ping.parse(ipck.buffer);
				PongPacket pong = new PongPacket(ping.pingID);
				Queue.addMinecraftPacket(pong);
				break;
			case MinecraftIDs.CLIENT_CONNECT:
				ClientConnectPacket connect = new ClientConnectPacket();
				connect.parse( ipck.buffer );
				ServerHandshakePacket servershake = new ServerHandshakePacket(port, connect.session);
				Queue.addMinecraftPacket(servershake);
				break;
			case MinecraftIDs.DISCONNECT:
				close("Disconnect");
				break;
			case MinecraftIDs.CLIENT_HANDSHAKE:
				ClientHandshakePacket clientshake = new ClientHandshakePacket();
				clientshake.parse( ipck.buffer );
				break;
			case MinecraftIDs.LOGIN:
				LoginPacket login = new LoginPacket();
				login.parse( ipck.buffer );
				if(name != null) { break; }
				name = login.username;
				load();
				
				if(login.protocol != MinecraftIDs.CURRENT_PROTOCOL || login.protocol2 != MinecraftIDs.CURRENT_PROTOCOL){
					if(login.protocol < MinecraftIDs.CURRENT_PROTOCOL || login.protocol2 < MinecraftIDs.CURRENT_PROTOCOL){
						Queue.addMinecraftPacket(new LoginStatusPacket( LoginStatusPacket.CLIENT_OUTDATE ));
						close("Wrong Protocol: Client is outdated.");
					}
					if(login.protocol > MinecraftIDs.CURRENT_PROTOCOL || login.protocol2 > MinecraftIDs.CURRENT_PROTOCOL){
						Queue.addMinecraftPacket(new LoginStatusPacket( LoginStatusPacket.SERVER_OUTDATE ));
						close("Wrong Protocol: Server is outdated.");
					}
					break;
				}
				//TODO Player count limit
				Queue.addMinecraftPacket( new LoginStatusPacket(LoginStatusPacket.NORMAL) );
				//TODO Check Player Name is Valid?
				
				if( !leader.player.checkValid(this) ) {
					return;
				}
				
				StartGamePacket startgame = new StartGamePacket(new Vector(128, 4, 128), new Vector( (int) x, (int) y, (int) z ), 1, 0L, EID);
				Queue.addMinecraftPacket(startgame);
				
				//TODO RealTime
				SetTimePacket stp = new SetTimePacket(0x00);
				Queue.addMinecraftPacket(stp);
				
				SetSpawnPositionPacket sspp = new SetSpawnPositionPacket( new Vector(128, 4, 128) );
				Queue.addMinecraftPacket(sspp);
				
				SetHealthPacket shp = new SetHealthPacket((byte) 0x20);
				Queue.addMinecraftPacket(shp);

				worker.setName("Player: " + name);
				break;
			case MinecraftIDs.MESSAGE:
				MessagePacket message = new MessagePacket();
				message.parse( ipck.buffer );
				leader.chat.handleChat( this, message.getMessage() );
				break;
			case MinecraftIDs.MOVE_PLAYER:
				MovePlayerPacket movePlayer = new MovePlayerPacket();
				movePlayer.parse( ipck.buffer );
				x = movePlayer.x;
				y = movePlayer.y;
				z = movePlayer.z;
				yaw = movePlayer.yaw;
				pitch = movePlayer.pitch;
				bodyYaw = movePlayer.bodyYaw;
				updateMovement();
				break;
			case MinecraftIDs.ANIMATE:
				AnimatePacket ani = new AnimatePacket();
				ani.parse( ipck.buffer );
				ani.eid = getEID();
				leader.player.broadcastPacket(ani, true, this);
				break;
			case MinecraftIDs.REMOVE_BLOCK:
				RemoveBlockPacket rbp = new RemoveBlockPacket();
				rbp.parse( ipck.buffer );
				UpdateBlockPacket ubp = new UpdateBlockPacket(rbp.x, rbp.y, rbp.z, (byte) 0, (byte) 0);
				level.setBlock(rbp.x, rbp.y, rbp.z, (byte) 0x00, (byte) 0x00); 
				leader.player.broadcastPacket(ubp, false);
				break;
			case MinecraftIDs.USE_ITEM:
				UseItemPacket uip = new UseItemPacket();
				uip.parse( ipck.buffer );
				uip.eid = getEID();
				if( !(uip.face >= 0 && uip.face <= 5) ) {
					break;
				}
				Vector Target = new Vector(uip.x, uip.y, uip.z).getSide((byte) uip.face, 1);
				byte TB = level.getBlock(Target);
				if( TB == 0x00 ) {
					UpdateBlockPacket uubp = new UpdateBlockPacket(Target.getX(), (byte) Target.getY(), Target.getZ(), (byte) uip.item, (byte) uip.meta);
					level.setBlock(Target, (byte) uip.item, (byte) uip.meta);
					leader.player.broadcastPacket(uubp, false);
				}
				break;
			case MinecraftIDs.PLAYER_EQUIPMENT:
				PlayerEquipmentPacket pep = new PlayerEquipmentPacket();
				pep.parse( ipck.buffer );
				pep.eid = EID; pep.slot = 0;
				leader.player.broadcastPacket(pep, false, this);
				break;
		}
	}
	
	public final void handleVerfiy(AcknowledgePacket ACK) throws Exception {
		lastPacketReceived = System.currentTimeMillis();
		Queue.handleVerify(ACK);
	}
	
	public long getClientID() {
		return clientID;
	}

	public String getName() {
		return name;
	}

	@Override
	public void updateMovement() throws Exception {
		MovePlayerPacket mpp = new MovePlayerPacket(EID, x, y, z, yaw, pitch, bodyYaw, false);
		leader.player.broadcastPacket(mpp, true, this);
	}

	@Override
	protected boolean isNeedUpdate() {
		return false;
	}
}
