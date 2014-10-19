package darkjet.server.network.player;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import darkjet.server.Leader;
import darkjet.server.Utils;
import darkjet.server.entity.Entity;
import darkjet.server.level.Level;
import darkjet.server.level.chunk.Chunk;
import darkjet.server.math.Vector;
import darkjet.server.math.Vector2;
import darkjet.server.network.packets.minecraft.AddPlayerPacket;
import darkjet.server.network.packets.minecraft.AdventureSettingPacket;
import darkjet.server.network.packets.minecraft.AnimatePacket;
import darkjet.server.network.packets.minecraft.BaseMinecraftPacket;
import darkjet.server.network.packets.minecraft.ClientConnectPacket;
import darkjet.server.network.packets.minecraft.ClientHandshakePacket;
import darkjet.server.network.packets.minecraft.FullChunkDataPacket;
import darkjet.server.network.packets.minecraft.LoginPacket;
import darkjet.server.network.packets.minecraft.LoginStatusPacket;
import darkjet.server.network.packets.minecraft.MessagePacket;
import darkjet.server.network.packets.minecraft.MinecraftIDs;
import darkjet.server.network.packets.minecraft.MovePlayerPacket;
import darkjet.server.network.packets.minecraft.PingPacket;
import darkjet.server.network.packets.minecraft.PlayerEquipmentPacket;
import darkjet.server.network.packets.minecraft.PongPacket;
import darkjet.server.network.packets.minecraft.RemoveBlockPacket;
import darkjet.server.network.packets.minecraft.ServerHandshakePacket;
import darkjet.server.network.packets.minecraft.SetHealthPacket;
import darkjet.server.network.packets.minecraft.SetSpawnPositionPacket;
import darkjet.server.network.packets.minecraft.SetTimePacket;
import darkjet.server.network.packets.minecraft.StartGamePacket;
import darkjet.server.network.packets.minecraft.UpdateBlockPacket;
import darkjet.server.network.packets.minecraft.UseItemPacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.ACKPacket;
import darkjet.server.network.packets.raknet.AcknowledgePacket.NACKPacket;
import darkjet.server.network.packets.raknet.MinecraftDataPacket;
import darkjet.server.network.packets.raknet.RaknetIDs;
import darkjet.server.network.packets.raknet.MinecraftDataPacket.InternalDataPacket;

/**
 * Minecraft Packet Handler
 * @author Blue Electric
 */
public final class Player extends Entity {
	public final String IP;
	public final int port;
	public final short mtu;
	public final long clientID;
	
	public String name;
	
	private int lastSequenceNum = 0;
	
	private ArrayList<Integer> ACKQueue; // Received packet queue
	private ArrayList<Integer> NACKQueue; // Not received packet queue
	private HashMap<Integer, byte[]> recoveryQueue;
	private HashMap<Integer, InternalDataPacket> OftenrecoveryQueue;
	
	protected final InternalDataPacketQueue Queue;
	
	private final ChunkSender chunkSender;
	public final HashMap<Vector2, Chunk> getUsingChunks() {
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
		
		ACKQueue = new ArrayList<Integer>();
		NACKQueue = new ArrayList<Integer>();
		recoveryQueue = new HashMap<Integer, byte[]>();
		OftenrecoveryQueue = new HashMap<Integer, InternalDataPacket>();
		
		Queue = new InternalDataPacketQueue(3939);
		
		level = leader.level.getLoadedLevel("world");
		chunkSender = new ChunkSender();
	}
	
	//External Part
	public final void sendChat(String message) throws Exception {
		MessagePacket pak = new MessagePacket(message);
		Queue.addMinecraftPacket(pak);
	}
	
	public final void close() {
		
	}

	public final void close(String reason) {
		
	}
	
	//Internal Part
	private final class ChunkSender extends Thread {
		public final HashMap<Vector2, Chunk> useChunks = new HashMap<>();
		
		private final HashMap<Integer, ArrayList<Vector2>> MapOrder = new HashMap<>();
		private final HashMap<Vector2, Boolean> requestChunks = new HashMap<>();
		private final ArrayList<Integer> orders = new ArrayList<>();
		
		public boolean first = true, firstReloader = false;;
		public int lastCX = 0, lastCZ = 0;
		
		public boolean refreshAllUsed = false;
		
		@Override
		public final void run() {
			while ( !isInterrupted() ) {
				try {
					updateChunk();
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private final void updateChunk() throws Exception {
			int centerX = (int) Math.floor(x) >> 4;
			int centerZ = (int) Math.floor(z) >> 4;
			
			if( refreshAllUsed ) {
				for( Chunk chunk : useChunks.values() ) {
					Queue.addMinecraftPacket( new FullChunkDataPacket( useChunks.get(chunk) ) );
				}
				refreshAllUsed = false;
				return;
			}
			
			if( centerX == lastCX && centerZ == lastCZ && !first ) {
				Thread.sleep(100);
				return;
			}
			System.out.println("FullChunk for " + centerX + "," + centerZ);
			lastCX = centerX; lastCZ = centerZ;
			int radius = 4;
			
			MapOrder.clear(); requestChunks.clear(); orders.clear();
			
			for (int x = -radius; x <= radius; ++x) {
				for (int z = -radius; z <= radius; ++z) {
					int distance = (x*x) + (z*z);
					int chunkX = x + centerX;
					int chunkZ = z + centerZ;
					if( !MapOrder.containsKey( distance ) ) {
						MapOrder.put(distance, new ArrayList<Vector2>());
					}
					Vector2 v = new Vector2(chunkX, chunkZ);
					requestChunks.put(v, true);
					MapOrder.get(distance).add( v );
					if( !useChunks.containsKey( v ) ) {
						level.requestChunk( new Vector2(chunkX, chunkZ) );
					}
					orders.add(distance);
				}
			}
			Collections.sort(orders);

			synchronized (Queue) {
				for( Integer i : orders ) {
					for( Vector2 v : MapOrder.get(i) ) {
						try {
							if( useChunks.containsKey(v) ) { continue; }
							useChunks.put(v, level.getChunk(v.getX(), v.getZ()));
							Queue.addMinecraftPacket( new FullChunkDataPacket( useChunks.get(v) ) );
							sleep(1);
							//Resend in First Chunk Sending
							//TODO: Minecraft Error?
							if( first ) {
								useChunks.remove(v);
							} else if ( firstReloader ) {
								level.releaseChunk(v);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(first) {
				System.out.println( "Player " + name + " is ready to play!" );
				InitPlayer();
			}
			Vector2[] v2a = useChunks.keySet().toArray(new Vector2[useChunks.keySet().size()] );
			for( int i = 0; i < v2a.length; i++ ) {
				Vector2 v = v2a[i];
				if( !requestChunks.containsKey( v ) ) {
					level.releaseChunk(v);
					useChunks.remove(v);
				}
			}
			if( firstReloader ) { firstReloader = false; }
			if( first ) { firstReloader = true; }
			first = false;
		}
	}
	
	@Override
	public final void update() throws Exception {
		super.update();
		synchronized (ACKQueue) {
			if(this.ACKQueue.size() > 0){
				int[] array = new int[this.ACKQueue.size()];
				int offset = 0;
				for(Integer i: ACKQueue){
					array[offset++] = i;
				}
				ACKPacket pck = new ACKPacket();
				pck.sequenceNumbers = array;
				leader.network.server.sendTo( pck.getResponse() , IP, port);
			}
		}
		synchronized (NACKQueue) {
			if(NACKQueue.size() > 0){
				int[] array = new int[NACKQueue.size()];
				int offset = 0;
				for(Integer i: NACKQueue){
					array[offset++] = i;
				}
				NACKPacket pck = new NACKPacket();
				pck.sequenceNumbers = array;
				leader.network.server.sendTo( pck.getResponse() , IP, port);
			}
		}
		if( !Queue.isEmpty() ) {
			Queue.send();
		}
	}
	
	protected void InitPlayer() throws Exception {
		Queue.addMinecraftPacket( new SetTimePacket(0) );
		MovePlayerPacket player = new MovePlayerPacket();
		player.x = x; player.y = y; player.z = z;
		Queue.addMinecraftPacket(player);
		AdventureSettingPacket adp = new AdventureSettingPacket(0x20);
		Queue.addMinecraftPacket(adp);
		
		//Add player for other Player
		AddPlayerPacket app = new AddPlayerPacket(Player.this);
		leader.player.broadcastPacket(app, false, Player.this);
		
		sendChat("Welcome to DarkJET Server, " + name);
		
		//Take exist player for this
		for( Player p : leader.player.getPlayers() ) {
			if( p == this ) { continue; }
			p.sendChat( name + " is Connected!" );
			Queue.addMinecraftPacket( new AddPlayerPacket(p) );
		}
	}

	public final void handlePacket(MinecraftDataPacket MDP) throws Exception {
		if(MDP.sequenceNumber - this.lastSequenceNum == 1){
			lastSequenceNum = MDP.sequenceNumber;
		}
		else{
			for(int i = this.lastSequenceNum; i < MDP.sequenceNumber; ++i){
				synchronized (NACKQueue) {
					NACKQueue.add(i);
				}
			}
		}
		synchronized (ACKQueue) {
			ACKQueue.add(MDP.sequenceNumber);
		}
		for(InternalDataPacket ipck : MDP.packets){
			if(ipck.buffer.length == 0) { continue; }
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
				case MinecraftIDs.CLIENT_HANDSHAKE:
					ClientHandshakePacket clientshake = new ClientHandshakePacket();
					clientshake.parse( ipck.buffer );
					break;
				case MinecraftIDs.LOGIN:
					LoginPacket login = new LoginPacket();
					login.parse( ipck.buffer );
					
					if( chunkSender.isAlive() ) {
						break;
					}
					
					name = login.username;
					
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
					//TODO Check Another Location Player
					
					StartGamePacket startgame = new StartGamePacket(new Vector(128, 4, 128), new Vector(128, 4, 128), 1, 0L, 0);
					Queue.addMinecraftPacket(startgame);
					
					//TODO RealTime
					SetTimePacket stp = new SetTimePacket(0);
					Queue.addMinecraftPacket(stp);
					
					SetSpawnPositionPacket sspp = new SetSpawnPositionPacket( new Vector(128, 4, 128) );
					Queue.addMinecraftPacket(sspp);
					
					SetHealthPacket shp = new SetHealthPacket((byte) 0x20);
					Queue.addMinecraftPacket(shp);
					
					chunkSender.start();

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
					if( !(uip.face >= 0 && uip.face <= 5) ) {
						break;
					}
					System.out.println( uip.item );
					Vector Target = new Vector(uip.x, uip.y, uip.z).getSide((byte) uip.face, 1);
					byte TB = level.getBlock(Target);
					if( TB == 0x00 ) {
						UpdateBlockPacket uubp = new UpdateBlockPacket(Target.getX(), (byte) Target.getY(), Target.getZ(), (byte) uip.item, (byte) 0x00);
						level.setBlock(Target, (byte) uip.item, (byte) 0x00); 
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
	}
	
	public final void handleVerfiy(AcknowledgePacket ACK) throws Exception {
		if( ACK.getPID() == RaknetIDs.ACK ) {
			for(int i: ACK.sequenceNumbers){
				recoveryQueue.remove(i);
			}
		} else if( ACK.getPID() == RaknetIDs.NACK ) {
			for(int i: ACK.sequenceNumbers){
				System.err.println( i + " love you" );
				if( recoveryQueue.containsKey(i) ) {
					leader.network.server.sendTo( recoveryQueue.get(i) , IP, port);
				} else if( OftenrecoveryQueue.containsKey(i) ) { //Often Changed Movement Packet!
					InternalDataPacket idp = OftenrecoveryQueue.get(i);
					switch( idp.buffer[0] ) {
						case MinecraftIDs.MOVE_PLAYER:
							MovePlayerPacket mpp = new MovePlayerPacket(EID, x, y, z, yaw, pitch, bodyYaw, false);
							idp.buffer = mpp.getResponse();
							Queue.recoverOftenPacket(i, idp.toBinary());
					}
				}
			}
		} else {
			
		}
	}
	
	public final class InternalDataPacketQueue {
		public ByteBuffer buffer;
		public ByteBuffer directBuffer;
		public int sequenceNumber = 0;
		public int messageIndex = 0;
		public int orderIndex = 0;
		public final int mtu;
		
		public InternalDataPacketQueue(int mtu) {
			this.mtu = mtu;
			buffer = ByteBuffer.allocate(mtu);
			directBuffer = ByteBuffer.allocate(mtu);
			resetBuffer();
		}
		
		public final void resetBuffer() {
			buffer.clear();
			buffer.position(4);
		}
		
		public final boolean isEmpty() {
			return buffer.position() == 4;
		}
		
		public final void addMinecraftPacket(BaseMinecraftPacket bmp) throws Exception {
			addMinecraftPacket( bmp.getResponse() );
		}
		
		/**
		 * Add MinecraftPacket to Buffer, if Buffer is Full, send directly
		 * @param buf
		 */
		public final void addMinecraftPacket(byte[] buf) throws Exception {
			synchronized ( this ) {
				if( mtu < buffer.position() + buf.length + 6 ) {
					//Buffer is Empty = buf too big to send.
					if( isEmpty() ) {
						throw new RuntimeException("Unhandled Too Big Packet");
					} else {
						send();
						//retry
						addMinecraftPacket(buf);
					}
					return;
				}
				InternalDataPacket idp = new InternalDataPacket();
				idp.buffer = buf;
				idp.reliability = 2;
				idp.messageIndex = messageIndex++;
				buffer.put( idp.toBinary() );
			}
		}
		
		public final void sendOffenPacket(BaseMinecraftPacket pak) throws Exception {
			synchronized ( this ) {
				InternalDataPacket idp = InternalDataPacket.wrapMCPacket(pak.getResponse(), messageIndex++);
				directBuffer.clear(); directBuffer.position(4);
				directBuffer.put( idp.toBinary() );
				send(directBuffer);
				OftenrecoveryQueue.put(sequenceNumber, idp);
			}
		}
		
		protected final void recoverOftenPacket(int seq, byte[] buf) throws Exception {
			synchronized ( this ) {
				directBuffer.clear(); directBuffer.position(4);
				directBuffer.put( buf );
				send(seq, directBuffer);
			}
		}
		
		public final void send() throws Exception {
			synchronized ( this ) {
				recoveryQueue.put(sequenceNumber, send(buffer));
				resetBuffer();
			}
		}
		
		private final byte[] send(ByteBuffer buffer) throws Exception {
			return send(sequenceNumber++, buffer);
		}
		
		protected final byte[] send(int seq, ByteBuffer buffer) throws Exception {
			//System.out.println("seq:" + seq);
			int len = buffer.position();
			buffer.position(0);
			buffer.put( RaknetIDs.DATA_PACKET_4 );
			buffer.put( Utils.putLTriad(seq) );
			byte[] sendBuffer = new byte[4+len];
			buffer.position(0);
			buffer.get(sendBuffer);
			leader.network.server.sendTo(sendBuffer, IP, port);
			return sendBuffer;
		}
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
		leader.player.broadcastPacket(mpp, true, (Player) this);
	}
}
