package gitBackuper.utils;

import org.junit.Test;

public class HttpClientTest {
    @Test
    public void testHttpRequest() {
        System.out.println(HttpClient.makeRequest("https://api.github.com"));
    }
}
