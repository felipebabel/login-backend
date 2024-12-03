package com.securityspring.service;

import com.securityspring.util.Major;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MajorService {

    Map<Long, Major> marathonMajors = new HashMap<>() {{
        put(1L, new Major(1L, "Tokyo Marathon", "Tokyo, Japan", "One of the six Abbott World Marathon Majors."));
        put(2L, new Major(2L, "Boston Marathon", "Boston, USA", "One of the oldest marathons in the world."));
        put(3L, new Major(3L, "London Marathon", "London, UK", "Known for its scenic route along the Thames River."));
        put(4L, new Major(4L, "Berlin Marathon", "Berlin, Germany", "Famous for world record-breaking performances."));
        put(5L, new Major(5L, "Chicago Marathon", "Chicago, USA", "Known for its flat course and fast times."));
        put(6L, new Major(6L, "New York City Marathon", "New York, USA", "The largest marathon in the world."));
    }};

    public Major getMajor(final Long id)  {
        return marathonMajors.get(id);
    }

    public List<Major> getAllMajors()  {
        return new ArrayList<>(marathonMajors.values());
    }
}
