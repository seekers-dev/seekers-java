package org.seekers.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.seekers.api.Command;
import org.seekers.api.CommandRequest;
import org.seekers.api.JoinRequest;
import org.seekers.api.SeekersGrpc;

public class SeekersClient {
    private final ManagedChannel channel;
    private final SeekersGrpc.SeekersBlockingStub blockingStub;

    public SeekersClient(String address) {
        channel = ManagedChannelBuilder.forAddress(address, 4242).usePlaintext().build();
        blockingStub = SeekersGrpc.newBlockingStub(channel);
    }

    public void close() {
        channel.shutdown();
    }
    
    private String playerId;
    private String token;
    
    public void join() {
        var response = blockingStub.join(JoinRequest.newBuilder().build());
        playerId = response.getPlayerId();
        token = response.getToken();
    }

    public void command(Iterable<Command> commands) {
        var response = blockingStub.command(CommandRequest.newBuilder().setToken(token).addAllCommands(commands).build());
        
    }
}
