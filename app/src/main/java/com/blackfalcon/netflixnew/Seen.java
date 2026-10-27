package com.blackfalcon.netflixnew;

import java.util.List;
import java.util.Set;

/** Marks which titles are new compared with what was seen before. Pure Java. */
final class Seen {
    private Seen() {}

    /** Sets title.fresh for titles not in 'seen', adds every title to 'seen', returns the number of fresh ones. */
    static int mark(Set<String> seen, List<Tmdb.Title> titles, boolean baseline) {
        int n = 0;
        for (Tmdb.Title t : titles) {
            boolean isNew = !seen.contains(t.key());
            t.fresh = isNew && !baseline;
            if (t.fresh) n++;
            seen.add(t.key());
        }
        return n;
    }
}
