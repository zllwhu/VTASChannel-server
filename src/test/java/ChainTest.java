import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChainTest {
    public static void main(String[] args) throws IOException {
        String urlString = "http://localhost:8080/asset/query?account=63ee5a52aa844759957e1dfecf3cd9b7";
        int iterations = 100; // 测试100次请求
        long totalNanoTime = 0;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime(); // 记录开始时间
            sendRequest(urlString); // 发送请求
            long endTime = System.nanoTime(); // 记录结束时间
            totalNanoTime += (endTime - startTime); // 累加消耗的时间
        }

        // 计算平均时间
        double averageTime = totalNanoTime / (double) iterations;
        System.out.println("Average response time for " + iterations + " requests: " + averageTime + " nanoseconds");
    }

    private static void sendRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // 设置请求方法为GET
        connection.setConnectTimeout(5000); // 设置连接超时时间
        connection.setReadTimeout(5000); // 设置读取超时时间

        // 发送请求并获取响应
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            // 如果响应成功，可以读取响应内容，但这里只是计算时间
            connection.getInputStream().close(); // 获取响应流
        } else {
            System.out.println("Request failed with response code: " + responseCode);
        }
        connection.disconnect(); // 关闭连接
    }
}
