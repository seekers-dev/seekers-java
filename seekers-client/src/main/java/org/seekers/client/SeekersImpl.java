package org.seekers.client;

public interface SeekersImpl {
    Collection<Seeker> decide(String id, List<Seeker> seekers, List<Camp> camps, List<Player players);
}
