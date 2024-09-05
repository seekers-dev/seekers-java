package org.seekers.client;

import org.seekers.core.*;

import java.util.List;

public interface SeekersImpl {
    Iterable<Seeker> decide(String id, List<Seeker> seekers, List<Goal> goals, List<Player> players, List<Camp> camps, Torus world);
}
