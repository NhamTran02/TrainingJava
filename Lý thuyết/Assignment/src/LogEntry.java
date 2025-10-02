import java.time.LocalDateTime;

public class LogEntry {
    LocalDateTime timestamp;
    String level;
    String service;
    String message;
    public LogEntry(LocalDateTime timestamp, String level, String service, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.service = service;
        this.message = message;

    }
}
