package gitBackuper.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class HttpClientTest {
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    @Before
    public void setErrStream() {
        System.setErr(new PrintStream(errStream));
    }

    @Test
    public void testHttpRequest() {
        Assert.assertTrue(HttpClient.makeRequest("https://api.github.com").length() > 0);
    }

    @Test
    public void httpRequestWillFail() {
        HttpClient.makeRequest("asfasfasf");
        Assert.assertTrue(errStream.toString().contains("Failed to get"));
    }
}
