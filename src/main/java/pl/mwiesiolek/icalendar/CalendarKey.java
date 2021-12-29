package pl.mwiesiolek.icalendar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum CalendarKey {
    BEGIN(":"),
    PRODID(":"),
    VERSION(":"),
    ORGANIZER(";"),
    DTSTART(":"),
    DTEND(":"),
    SUMMARY(":"),
    SEQUENCE(":"),
    END(":");

    String delimiter;
}
