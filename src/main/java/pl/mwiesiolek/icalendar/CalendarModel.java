package pl.mwiesiolek.icalendar;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
class CalendarModel {

    @Builder.Default
    List<CalendarPair> pairs = new ArrayList<>();

    void addPair(CalendarPair pair) {
        pairs.add(pair);
    }

    @Value
    @Builder
    static class CalendarPair {
        CalendarKey key;
        String value;
    }
}
