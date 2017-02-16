package com.mac.litegrid.clustering.communication.channels;

import com.mac.libraries.concurrency.KeepRunning;
import com.mac.libraries.concurrency.QueueCloseNotification;
import com.mac.libraries.concurrency.Waiter;
import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.libraries.events.GenericCallBack;
import com.mac.libraries.generic.IOUtil;
import com.mac.libraries.sequences.Sequence;
import com.mac.libraries.statistics.GenericTimer;
import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;
import com.mac.litegrid.clustering.communication.messages.Member;
import com.mac.litegrid.clustering.communication.messages.Peer;
import com.mac.litegrid.clustering.communication.serialization.LiteGridSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 18/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */

public class TCPTransceiver extends Thread implements Comparable<TCPTransceiver> {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private KeepRunning keepRunning;
    private Socket socket;
    private BlockingQueue receiveQueue;
    private BlockingQueue sendQueue;
    private DataOutputStream ostream;
    private DataInputStream istream;
    private Member self;
    private Member remoteMember;
    private GenericCallBack onReadyCallback;
    private GenericCallBack onErrorCallback;
    private int instanceId = Sequence.nextInt();
    private boolean ready;
    private Waiter readWaiter;
    private long bufferWaitMs;
    private int bufferReadAttempts;

    public TCPTransceiver(Member self, Socket s, BlockingQueue q, GenericCallBack onReadyCallback, GenericCallBack onErrorCallback, ConfigurationSource config) {
        super("TCPTransceiver");
        this.setDaemon(true);
        this.socket = s;
        this.receiveQueue = q;
        this.self = self;
        this.onReadyCallback = onReadyCallback;
        this.onErrorCallback = onErrorCallback;
        this.keepRunning = new KeepRunning();
        this.ready = false;
        this.sendQueue = new ArrayBlockingQueue(50);
        this.readWaiter = new Waiter();
        this.bufferWaitMs = 500;
        this.bufferReadAttempts = 100;
    }

    public boolean matchesRemotePeer(Peer peer) {
        if (!isReady()) {
            log.info("not ready");
            return false;
        }
        // cant remember why I matched host and port?
        return peer.getMemberId().equals(remoteMember.getPeer().getMemberId());
//        return remoteMember.getPeer().doesHostAndPortMatch(peer);
    }

    public boolean matchesRemoteMember(Member member) {
        if (!isReady()) {
            return false;
        }
        return remoteMember.getId().equals(member.getId());
    }

    @Override
    public int compareTo(TCPTransceiver o) {
        if (this.equals(o)) {
            return 0;
        }
        return -1;
    }

    public Member getSelf() {
        return self;
    }

    public Member getRemoteMember() {
        return remoteMember;
    }

    private void writeInternal(Object o) {
        try {
            writeProtoc(o);
        } catch (Exception e) {
            log.warn("Exception writing packet " + e, e);
        }

    }

    public void write(Object o) {
        sendQueue.add(o);
    }

    public boolean isReady() {
        return ready && socket.isConnected();
    }

    public void close() {
        keepRunning.stopRunning();
        sendQueue.clear();
        sendQueue.add(new QueueCloseNotification());
        try {
            IOUtil.doClose(ostream);
            IOUtil.doClose(istream);
            log.debug("TCPTranceiver " + instanceId + "  closing");
            socket.close();
        } catch (Exception e) {
        }

    }

    private Object readProtoc() throws IOException {
        int startToken = istream.readInt();
        if (startToken != START_TOKEN) {
            log.warn("Start Token {} != {}", startToken, START_TOKEN);
            keepRunning.stopRunning();
            return null;
        }

        int dataLength = istream.readInt();
        if (dataLength == 0) {
            return null;
        }

        byte[] readBuffer = new byte[dataLength]; // no point trying to reuse as "parseFrom" needs correct size buffer
        int readLength = IOUtil.retryReadToBuffer(istream, readBuffer, 0, dataLength, bufferReadAttempts, readWaiter, bufferWaitMs);
//        log.debug("READ read {} vs len {}", readLength, dataLength);

        if (readLength != dataLength) {
            log.error("READ read {} != len {}", readLength, dataLength);
            return null;
        }

        int endToken = istream.readInt();
        if (endToken != END_TOKEN) {
            log.warn("End Token {} != {}", endToken, END_TOKEN);
            //todo: do some kind of reset
            return null;
        }

        if (readLength != dataLength) {
            log.error("READ blen {} != rlen {}", dataLength, readLength);
            return null;
        } else {

//            LiteGridProtoc.NetworkPacket packet = LiteGridProtoc.NetworkPacket.parseFrom(readBuffer);
//            Object o = LiteGridSerialization.getSerializer().deSerialize(packet.getData().toByteArray());
            Object o = LiteGridSerialization.getSerializer().deSerialize(readBuffer);
            return o;
        }
    }

    private static final int START_TOKEN = 66;
    private static final int END_TOKEN = 99;

    private int writeProtoc(Object o) throws IOException {
//        LiteGridProtoc.NetworkPacket packet = LiteGridProtoc.NetworkPacket.newBuilder()
//                .setSequence(Sequence.nextLong())
//                .setUid(UIDUtil.getUid())
//                .setData(ByteString.copyFrom(LiteGridSerialization.getSerializer().serialize(o)))
//                .build();

//        byte[] buff = packet.toByteArray();
        byte[] buff = LiteGridSerialization.getSerializer().serialize(o);
        ostream.writeInt(START_TOKEN);
        ostream.writeInt(buff.length);
        ostream.write(buff, 0, buff.length);
        //todo: write a checksum
        ostream.writeInt(END_TOKEN);
        ostream.flush();
        return buff.length;
    }

    public void run() {
        Object o;
        try {
            ostream = new DataOutputStream(socket.getOutputStream());
            istream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("IOException in connecting " + e.getMessage(), e);
        }

        Thread sender = new Thread() {
            @Override
            public void run() {
                while (keepRunning.shouldKeepRunning()) {
                    while (!isReady()) {
                        keepRunning.doWait(500);
                    }
                    try {
                        Object o = sendQueue.poll(30000, TimeUnit.MILLISECONDS);
                        if (o instanceof QueueCloseNotification) {
                            if (keepRunning.shouldKeepRunning()) {
                                log.error("Receive a QueueCloseNotification but keepRunning == true");
                            } else {
                                break;
                            }

                        }
                        if (o != null) {
                            if (isReady()) {
                                writeInternal(o);
                            } else {
                                sendQueue.put(o);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        };

        sender.start();

        GenericTimer timer = new GenericTimer();
        while (keepRunning.shouldKeepRunning()) {
            try {
                if (remoteMember == null) {
                    writeInternal(self);
                }

                timer.start("tcp.receive.event");

                timer.start("tcp.read.object");
                o = readProtoc();
                timer.stop("tcp.read.object");

                if (o == null) {
                    continue;
                }

                if (remoteMember == null) {
                    if (o instanceof Member) {
                        remoteMember = (Member) o;
                        log.debug("TCPTranceiver " + instanceId + "  received remoteMember " + remoteMember);
                        ready = true;
                        onReadyCallback.onCallBack(this);
                        keepRunning.doNotify();
                        continue;
                    } else {
                        log.debug("TCPTranceiver " + instanceId + "  writing for remoteMember to identify itself...");
                    }
                    continue;
                }

                if (!(o instanceof NetworkMessage)) {
                    continue;
                }


                timer.start("tcp.receive.enqueue");
                receiveQueue.add(o);
                timer.stop("tcp.receive.enqueue");

                timer.stop("tcp.receive.event");


            } catch (SocketException se) {
                log.info("TCPTranceiver " + instanceId + "  socketException " + se.getMessage());
                close();
            } catch (EOFException eof) {
                log.info("TCPTranceiver " + instanceId + "  eofException " + eof.getMessage());
                close();
            } catch (Exception e) {
                if (socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()) {
                    log.info("TCPTranceiver " + instanceId + "  socketClosed " + e.getMessage());
                    close();
                } else {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.debug("TCPTranceiver " + instanceId + "  stopped ");
        onErrorCallback.onCallBack(this);

    }


}
