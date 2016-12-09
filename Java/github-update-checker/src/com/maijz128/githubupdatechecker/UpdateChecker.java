package com.maijz128.githubupdatechecker;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.function.*;
import com.google.gson.Gson;


/****************************************************************
 *      MaiJZ                                                   *
 *      20161209                                                *
 *      https://github.com/maijz128/github-update-checker       *
 *                                                              *
 ****************************************************************/


public class UpdateChecker {

    public interface IWebClient {
        void DownloadHtml(String url, Consumer<String> callback);
    }

    public IWebClient WebClient;
    public String UserOrOrgName;
    public String RepoName;
    public String CurrentVersion;

    private String _UpdateURL = "https://api.github.com/repos/{USER_OR_ORG}/{REPO_NAME}/releases/latest";
    private String _ReleasesURL = "https://github.com/{USER_OR_ORG}/{REPO_NAME}/releases";


    public UpdateChecker(String userOrOrgName, String repoName, String currentVersion) {
        this.UserOrOrgName = userOrOrgName;
        this.RepoName = repoName;
        this.CurrentVersion = currentVersion;
        this.WebClient = new MyWebClient();
    }


    public void CheckUpdate(BiConsumer<String, String> callback) {
        this.WebClient.DownloadHtml(GetUpdateURL(), (html) ->
        {
            LatestReleases latest = GetLatestReleases(html);
            callback.accept(latest.tag_name, latest.body);
        });
    }


    public void CheckUpdate(Consumer<LatestReleases> callback) {
        this.WebClient.DownloadHtml(GetUpdateURL(), (html) ->
        {
            LatestReleases latest = GetLatestReleases(html);
            callback.accept(latest);
        });
    }


    public void HasNewVersion(Consumer<Boolean> callback) {
        CheckUpdate((latest) ->
        {
            int result = VersionComparer.CompareVersion(latest.tag_name, this.CurrentVersion);
            callback.accept(result > 0);
        });
    }


    public void OpenBrowserToReleases() {
        String url = _ReleasesURL;
        url = url.replace("{USER_OR_ORG}", this.UserOrOrgName);
        url = url.replace("{REPO_NAME}", this.RepoName);
        URI uri = URI.create(url);
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String GetUpdateURL() {
        String result = _UpdateURL;
        result = result.replace("{USER_OR_ORG}", this.UserOrOrgName);
        result = result.replace("{REPO_NAME}", this.RepoName);
        return result;
    }


    public static LatestReleases GetLatestReleases(String html) {
        Gson g = new Gson();
        return g.fromJson(html, LatestReleases.class);
    }


    public static class VersionComparer {

        public static int CompareVersion(String target, String current) {
            String target_f = Filter(target);
            String current_f = Filter(current);
            String[] tsplit = target_f.split("\\.");
            String[] csplit = current_f.split("\\.");
            int len = (tsplit.length > csplit.length) ? tsplit.length : csplit.length;

            for (int i = 0; i < len; i++) {
                int tvalue = 0;
                int cvalue = 0;

                if (i < tsplit.length) {
                    tvalue = Integer.parseInt(tsplit[i]);
                }

                if (i < csplit.length) {
                    cvalue = Integer.parseInt(csplit[i]);
                }

                if (tvalue != cvalue) {
                    return tvalue - cvalue;
                }
            }
            return 0;
        }


        public static String Filter(String version) {
            StringBuilder sb = new StringBuilder();

            for (char c : version.toCharArray()) {
                Boolean condition = c >= '0' && c <= '9';
                if (condition || c == '.') {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

    }


    public class LatestReleases {

        public String html_url;
        public String tag_name;
        public String name;
        public String body;
        public List<Assets> assets;
        public String tarball_url;
        public String zipball_url;


        public class Assets {
            public String name;
            public String size;
            public String download_count;
            public String created_at;
            public String updated_at;
            public String browser_download_url;
        }
    }


    public class MyWebClient implements IWebClient {

        public void DownloadHtml(String url, Consumer<String> callback) {
            Thread thread = new Thread(() ->
            {
                callback.accept(HttpConnection(url));
            });
            thread.start();
        }


        public String HttpConnection(String surl) {
            try {
                URL url = new URL(surl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    InputStream stream = conn.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = stream.read(buffer)) != (-1)) {
                        baos.write(buffer, 0, len);
                    }
                    return baos.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "{\"name\" : \"获取Html错误!\"}";
        }

    }

}
