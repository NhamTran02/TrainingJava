import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputFile = getValidInputFile(scanner);
        if (inputFile == null) {
            System.out.println("Hủy thao tác!");
            return;
        }

        System.out.print("Nhập file kết quả xuất ra: ");
        String outputFile = "txt/" + scanner.nextLine().trim() + ".txt";
        File output = new File(outputFile);
        if (output.exists()) {
            System.out.print("File kết quả đã tồn tại. Bạn có muốn ghi đè? (y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("Hủy thao tác!");
                return;
            }
        }

        System.out.print("Nhập log level (INFO/WARN/ERROR) hoặc để trống tất cả: ");
        String searchLevel = scanner.nextLine().trim();
        if (searchLevel.isEmpty()) searchLevel = null;

        System.out.print("Nhập từ khóa tìm trong message hoặc để trống tất cả: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) keyword = null;

        LocalDateTime fromTime = getValidDateTime(scanner, "thời gian từ");
        LocalDateTime toTime = getValidDateTime(scanner, "thời gian đến");

        long startTime = System.currentTimeMillis();
        File file = new File(inputFile);
        long fileSize = file.length();
        int numThreads = Math.min(4, Runtime.getRuntime().availableProcessors()); // Giảm số thread
        long chunkSize = fileSize / numThreads;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Integer>> futures = new ArrayList<>();
        List<String> tempFiles = new ArrayList<>();
        long currentStart = 0;

        // Chia file thành chunks và xử lý song song
        for (int i = 0; i < numThreads; i++) {
            long end = (i == numThreads - 1) ? fileSize : currentStart + chunkSize;
            String tempFile = "temp_" + i + ".txt";
            tempFiles.add(tempFile);
            futures.add(executor.submit(new ChunkProcessor(currentStart, end, inputFile, tempFile,
                    searchLevel, keyword, fromTime, toTime)));
            currentStart = end;
        }

        // Thu thập kết quả
        int totalCount = 0;
        try {
            for (Future<Integer> future : futures) {
                try {
                    totalCount += future.get();
                } catch (InterruptedException e) {
                    System.err.println("Thread bị gián đoạn: " + e.getMessage());
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    System.err.println("Lỗi khi thực thi thread: " + e.getCause().getMessage());
                }
            }
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    System.err.println("ExecutorService không hoàn thành trong thời gian chờ.");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.println("Đợi executor kết thúc bị gián đoạn: " + e.getMessage());
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Merge file tạm vào output
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (String temp : tempFiles) {
                try (BufferedReader br = new BufferedReader(new FileReader(temp))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        bw.write(line);
                        bw.newLine();
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi khi merge file tạm " + temp + ": " + e.getMessage());
                }
                new File(temp).delete();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi merge file: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("Tìm thấy " + totalCount + " log phù hợp. Kết quả đã lưu vào " + outputFile);
        System.out.println("Thời gian: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static String getValidInputFile(Scanner scanner) {
        while (true) {
            System.out.print("Nhập file log nguồn: ");
            String inputFile = "txt/" + scanner.nextLine().trim()+".txt";
            if (inputFile.equalsIgnoreCase("txt/exit")) {
                return null;
            }
            File file = new File(inputFile);
            if (file.exists()) {
                return inputFile;
            } else {
                System.out.println("File nguồn không tồn tại: " + inputFile);
            }
        }
    }

    private static LocalDateTime getValidDateTime(Scanner scanner, String fieldName) {
        while (true) {
            System.out.print("Nhập " + fieldName + " (yyyy-MM-dd HH:mm:ss) hoặc để trống tất cả: ");
            String timeStr = scanner.nextLine().trim();
            if (timeStr.isEmpty()) return null;
            try {
                return LocalDateTime.parse(timeStr, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Định dạng " + fieldName + " không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-dd HH:mm:ss hoặc để trống.");
            }
        }
    }

    public static LogEntry parseLogLine(String line) {
        try {
            if (!line.startsWith("[") || line.indexOf(']') == -1) {
                System.out.println("Dòng log không hợp lệ: " + line);
                return null;
            }
            int idx1 = line.indexOf(']');
            String timestampStr = line.substring(1, idx1);
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);

            String rest = line.substring(idx1 + 2);
            int idx2 = rest.indexOf(']');
            String level = rest.substring(1, idx2);

            rest = rest.substring(idx2 + 2);
            int idx3 = rest.indexOf('-');
            String service = rest.substring(1, idx3 - 1);

            String message = rest.substring(idx3 + 2);

            return new LogEntry(timestamp, level, service, message);

        } catch (Exception e) {
            System.out.println("Lỗi phân tích dòng log: " + line + ". Chi tiết: " + e.getMessage());
            return null;
        }
    }
}