package pl.mwiesiolek.icalendar;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import static pl.mwiesiolek.icalendar.CLIKey.*;

public class Main {

    public static final CalendarModel.CalendarPair BEGIN_VCALENDAR = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.BEGIN)
            .value("VCALENDAR")
            .build();

    public static final CalendarModel.CalendarPair PRODID = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.PRODID)
            .value("-//Google Inc//Google Calendar 70.9054//EN")
            .build();

    public static final CalendarModel.CalendarPair VERSION = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.VERSION)
            .value("2.0")
            .build();

    public static final CalendarModel.CalendarPair BEGIN_VEVENT = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.BEGIN)
            .value("VEVENT")
            .build();

    public static final CalendarModel.CalendarPair ORGANIZER = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.ORGANIZER)
            .value("CN=Maksymilian Wiesiolek:MAILTO:maksymilian.wiesiolek@gmail.com")
            .build();

    public static final CalendarModel.CalendarPair SEQUENCE = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.SEQUENCE)
            .value("0")
            .build();

    public static final CalendarModel.CalendarPair END_VCALENDAR = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.END)
            .value("VCALENDAR")
            .build();

    public static final CalendarModel.CalendarPair END_VEVENT = CalendarModel.CalendarPair.builder()
            .key(CalendarKey.END)
            .value("VEVENT")
            .build();

    public static void main(String[] args) throws IOException, ParseException, URISyntaxException {
        final var options = new Options();
        options.addRequiredOption(YEAR.getShortKey(), YEAR.getLongKey(), true, "Planner year");
        options.addRequiredOption(FILE.getShortKey(), FILE.getLongKey(), true, "Relative path to CSV file");
        options.addRequiredOption(COLUMN_SEPARATOR.getShortKey(), COLUMN_SEPARATOR.getLongKey(), true, "Column separator");

        final var headersOption = new Option(HEADERS.getShortKey(), HEADERS.getLongKey(), false, "File headers (without months)");
        headersOption.setArgs(Option.UNLIMITED_VALUES);
        headersOption.setValueSeparator(',');
        options.addOption(headersOption);

        final var parser = new DefaultParser();
        final var cmd = parser.parse(options, args);

        final var calendarModel = CalendarModel.builder().build();
        calendarModel.addPair(BEGIN_VCALENDAR);
        calendarModel.addPair(PRODID);
        calendarModel.addPair(VERSION);

        final var year = cmd.getOptionValue(YEAR.getLongKey());
        final var in = new FileReader(getFileFromResource(cmd.getOptionValue(FILE.getLongKey())));

        final var records = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setDelimiter(cmd.getOptionValue(COLUMN_SEPARATOR.getLongKey()))
                .build()
                .parse(in);

        for (final var record : records) {

            for (final var header : cmd.getOptionValues(HEADERS.getLongKey())) {
                final var values = Arrays.stream(record.get(header).split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toList());
                for (String value : values) {

                    calendarModel.addPair(BEGIN_VEVENT);
                    calendarModel.addPair(ORGANIZER);

                    final var start = CalendarModel.CalendarPair.builder()
                            .key(CalendarKey.DTSTART)
                            .value(getDate(year, record.getRecordNumber(), value, "T090000Z"))
                            .build();
                    final var end = CalendarModel.CalendarPair.builder()
                            .key(CalendarKey.DTEND)
                            .value(getDate(year, record.getRecordNumber(), value, "T100000Z"))
                            .build();
                    final var summary = CalendarModel.CalendarPair.builder()
                            .key(CalendarKey.SUMMARY)
                            .value(header)
                            .build();

                    calendarModel.addPair(start);
                    calendarModel.addPair(end);
                    calendarModel.addPair(SEQUENCE);
                    calendarModel.addPair(summary);

                    calendarModel.addPair(END_VEVENT);
                }
            }
        }
        calendarModel.addPair(END_VCALENDAR);

        try (final var output = new FileWriter("calendar.ics")) {
            for (CalendarModel.CalendarPair pair : calendarModel.getPairs()) {
                output.write(String.format("%s%s%s%s", pair.getKey().name(), pair.getKey().getDelimiter(), pair.getValue(), System.lineSeparator()));
            }
        }

    }

    private static String getDate(String year, long nrRecord, String value, String suffix) {
        return String.format("%s%s%s%s", year, String.format("%02d", nrRecord), value, suffix);
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }
}
