import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Nhập file log nguồn: ");
        String inputFile = "txt/"+scanner.nextLine();



        System.out.print("Nhập file kết quả xuất ra: ");
        String outputFile = "txt/"+scanner.nextLine();

        System.out.print("Nhập log level (INFO/WARN/ERROR) hoặc để trống tất cả: ");
        String searchLevel = scanner.nextLine().trim();
        if (searchLevel.isEmpty()) searchLevel = null;

        System.out.print("Nhập từ khóa tìm trong message hoặc để trống tất cả: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) keyword = null;

        System.out.print("Nhập thời gian từ (yyyy-MM-dd HH:mm:ss) hoặc để trống tất cả: ");
        String fromTimeStr = scanner.nextLine().trim();
        LocalDateTime fromTime = fromTimeStr.isEmpty() ? null : LocalDateTime.parse(fromTimeStr, formatter);

        System.out.print("Nhập thời gian đến (yyyy-MM-dd HH:mm:ss) hoặc để trống tất cả: ");
        String toTimeStr = scanner.nextLine().trim();
        LocalDateTime toTime = toTimeStr.isEmpty() ? null : LocalDateTime.parse(toTimeStr, formatter);

        int count=0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                LogEntry log = parseLogLine(line);
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

            System.out.println("Tìm thấy " + count + " log phù hợp. Kết quả đã lưu vào " + outputFile);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static LogEntry parseLogLine(String line) {
        try {
            int idx1 = line.indexOf(']');
            String timestampStr = line.substring(1, idx1);
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

            String rest = line.substring(idx1 + 2);
            int idx2 = rest.indexOf(' ');
            String level = rest.substring(0, idx2);

            int idx3 = rest.indexOf("- ");
            String service = rest.substring(idx2 + 1, idx3);
            String message = rest.substring(idx3 + 2);

            return new LogEntry(timestamp, level, service, message);

        } catch (Exception e) {
            return null;
        }
    }
}