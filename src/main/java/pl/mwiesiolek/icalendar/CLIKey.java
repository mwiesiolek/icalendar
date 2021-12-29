package pl.mwiesiolek.icalendar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum CLIKey {
    YEAR("y", "year"),
    FILE("f", "file"),
    COLUMN_SEPARATOR("s", "separator"),
    HEADERS("h", "headers");

    String shortKey;
    String longKey;
}
