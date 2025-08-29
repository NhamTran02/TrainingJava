import java.io.*;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class ChunkProcessor implements Callable<Integer> {
    private final long start, end;
    private final String inputFile, tempFile, searchLevel, keyword;
    private final LocalDateTime fromTime, toTime;

    ChunkProcessor(long start, long end, String inputFile, String tempFile, String searchLevel,
                   String keyword, LocalDateTime fromTime, LocalDateTime toTime) {
        this.start = start;
        this.end = end;
        this.inputFile = inputFile;
        this.tempFile = tempFile;
        this.searchLevel = searchLevel;
        this.keyword = keyword;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    @Override
    public Integer call() throws Exception {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            br.skip(start); // Nhảy đến vị trí chunk
            if (start > 0) br.readLine(); // Bỏ dòng bị cắt
            String line;
            long currentPos = start;
            while (currentPos < end && (line = br.readLine()) != null) {
                currentPos += line.length() + 1; // Cộng độ dài dòng + \n
                LogEntry log = Main.parseLogLine(line);
                if (log == null) continue;
                boolean matches = true;
                if (searchLevel != null && !log.level.equalsIgnoreCase(searchLevel)) matches = false;
                if (keyword != null && !log.message.contains(keyword)) matches = false;
                if (fromTime != null && log.timestamp.isBefore(fromTime)) matches = false;
                if (toTime != null && log.timestamp.isAfter(toTime)) matches = false;
                if (matches) {
                    bw.write(line);
                    bw.newLine();
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi xử lý chunk: " + e.getMessage());
            throw e;
        }
        return count;
    }
}