package com.maijz128.githubupdatechecker;

import org.junit.Test;
import java.util.function.*;
import static org.junit.Assert.*;


/****************************************************************
 *      MaiJZ                                                   *
 *      20161210                                                *
 *      https://github.com/maijz128/github-update-checker       *
 *                                                              *
 ****************************************************************/


public class UpdateCheckerTest {

    final String HTML = "{"
            + "     \"html_url\" : \"releases/tag/0.0.1\", "
            + "     \"id\" : \"123456\","
            + "     \"tag_name\": \"0.0.1\","
            + "     \"name\" : \"版本0.0.1\","
            + "     \"assets\" : ["
            + "       {"
            + "           \"name\" : \"0.0.1.zip\","
            + "           \"size\" : \"171\","
            + "           \"download_count\" : \"1\","
            + "           \"created_at\" : \"2016-12-09T06:49:37Z\","
            + "           \"updated_at\" : \"2016-12-09T06:49:38Z\","
            + "           \"browser_download_url\" : \"/releases/download/0.0.1/0.0.1.zip\""
            + "       }"
            + "     ],"
            + "     \"tarball_url\" : \"tarball/0.0.1\","
            + "     \"zipball_url\" : \"zipball/0.0.1\","
            + "     \"body\" : \"第一个版本\""
            + " }";


    class StubWebClient implements UpdateChecker.IWebClient {
        public String DownloadHtmlResult;

        public void DownloadHtml(String url, Consumer<String> callback) {
            callback.accept(this.DownloadHtmlResult);
        }
    }


    private UpdateChecker GetChecker() {
        String userOrOrgName = "maijz128";
        String repoName = "github-update-checker";
        String currentVersion = "0.0.1";
        UpdateChecker result = new UpdateChecker(userOrOrgName, repoName, currentVersion);

        StubWebClient webclient = new StubWebClient();
        webclient.DownloadHtmlResult = HTML;
        result.WebClient = webclient;

        return result;
    }


    @Test
    public void checkUpdate() throws Exception {
        UpdateChecker checker = GetChecker();

        checker.CheckUpdate((version, message) ->
        {
            assertEquals(version, "0.0.1");
            assertEquals(message, "第一个版本");
        });
    }


    @Test
    public void checkUpdate1() throws Exception {
        UpdateChecker checker = GetChecker();

        checker.CheckUpdate((latest) ->
        {
            assertEquals(latest.tag_name, "0.0.1");
            assertEquals(latest.body, "第一个版本");
        });
    }


    @Test
    public void hasNewVersion() throws Exception {
        UpdateChecker checker = GetChecker();
        checker.CurrentVersion = "0.0.0.9";

        checker.HasNewVersion(result -> {
            if (result) {
                assertTrue(result);
            }
        });
    }


//    @Test
//    public void openBrowserToReleases() throws Exception {
//        GetChecker().OpenBrowserToReleases();
//    }


    @Test
    public void getUpdateURL() throws Exception {
        UpdateChecker checker = GetChecker();
        String updateURL = "https://api.github.com/repos/maijz128/github-update-checker/releases/latest";

        String result = checker.GetUpdateURL();

        assertEquals(result, updateURL);
    }


    @Test
    public void getLatestReleases() throws Exception {
        UpdateChecker.LatestReleases latest = GetChecker().GetLatestReleases(HTML);

        assertEquals(latest.tag_name, "0.0.1");
        assertEquals(latest.body, "第一个版本");
    }


    @Test
    public void TestComparerVersionTests_Filter() {
        String ver1 = "0.0.1";
        String ver2 = "版本0.0.1";
        String ver3 = "版本00.001.08";

        String result1 = UpdateChecker.VersionComparer.Filter(ver1);
        String result2 = UpdateChecker.VersionComparer.Filter(ver2);
        String result3 = UpdateChecker.VersionComparer.Filter(ver3);

        assertEquals(result1, "0.0.1");
        assertEquals(result2, "0.0.1");
        assertEquals(result3, "00.001.08");
    }


    @Test
    public void TestComparerVersionTests_CompareVersion() {
        String ver1 = "0.0.1";
        String ver2 = "版本0.0.1";
        String ver3 = "版本00.001.08";
        String ver4 = "0.0.2";
        String ver5 = "1.0.1";
        String ver6 = "2.1.6";
        String ver7 = "2.2.5";

        int result1 = UpdateChecker.VersionComparer.CompareVersion(ver4, ver1);
        int result2 = UpdateChecker.VersionComparer.CompareVersion(ver2, ver1);
        int result3 = UpdateChecker.VersionComparer.CompareVersion(ver3, ver2);
        int result4 = UpdateChecker.VersionComparer.CompareVersion(ver4, ver5);
        int result5 = UpdateChecker.VersionComparer.CompareVersion(ver7, ver6);

        assertTrue((result1 > 0));
        assertTrue(result2 == 0);
        assertTrue(result3 > 0);
        assertTrue(result4 < 0);
        assertTrue(result5 > 0);

    }
}