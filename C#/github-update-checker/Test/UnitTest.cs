using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;


namespace Github_Update_Checker.Tests
{
    [TestClass]
    public class UnitTest
    {

        string HTML = @"
        {
            'html_url' : 'releases/tag/0.0.1',
            'id' : '123456',
            'tag_name': '0.0.1',
            'name' : '版本0.0.1',
            'assets' : [
            {
                'name' : '0.0.1.zip',
                'size' : '171',
                'download_count' : '1',
                'created_at' : '2016-12-09T06:49:37Z',
                'updated_at' : '2016-12-09T06:49:38Z',
                'browser_download_url' : '/releases/download/0.0.1/0.0.1.zip'
            }
            ],
            'tarball_url' : 'tarball/0.0.1',
            'zipball_url' : 'zipball/0.0.1',
            'body' : '第一个版本'
        }";


        class StubWebClient : UpdateChecker.IWebClient
        {
            public string DownloadHtmlResult { get; set; }
            public void DownloadHtml(string url, Action<string> callback)
            {
                callback(this.DownloadHtmlResult);
            }
        }


        public UpdateChecker GetChecker()
        {
            string userOrOrgName = "maijz128";
            string repoName = "github-update-checker";
            string currentVersion = "0.0.1";
            UpdateChecker result = new UpdateChecker(userOrOrgName, repoName, currentVersion);

            StubWebClient webclient = new StubWebClient();
            webclient.DownloadHtmlResult = HTML;
            result.WebClient = webclient;

            return result;
        }


        [TestMethod]
        public void TestCheckUpdate1()
        {
            UpdateChecker checker = GetChecker();

            checker.CheckUpdate((version, message) =>
            {
                Assert.AreEqual(version, "0.0.1");
                Assert.AreEqual(message, "第一个版本");
            });
        }


        [TestMethod]
        public void TestCheckUpdate2()
        {
            UpdateChecker checker = GetChecker();

            checker.CheckUpdate((latest) =>
            {
                Assert.AreEqual(latest.tag_name, "0.0.1");
                Assert.AreEqual(latest.body, "第一个版本");
            });
        }


        [TestMethod]
        public void TestHasNewVersion()
        {
            UpdateChecker checker = GetChecker();
            checker.CurrentVersion = "0.0.0.9";

            checker.HasNewVersion(result =>
            {
                Assert.IsTrue(result);
            });

            checker.CurrentVersion = "1.0.0";
            checker.HasNewVersion(result =>
            {
                Assert.IsFalse(result);
            });
        }


        //[TestMethod]
        //public void TestOpenBrowserToReleases()
        //{
        //    object result =  GetChecker().OpenBrowserToReleases();

        //    Assert.AreEqual(result, null);
        //}


        [TestMethod]
        public void TestGetUpdateURL()
        {
            UpdateChecker checker = GetChecker();
            string updateURL = "https://api.github.com/repos/maijz128/github-update-checker/releases/latest";

            string result = checker.GetUpdateURL();

            Assert.AreEqual(result, updateURL);
        }


        [TestMethod]
        public void TestLatestReleases_GetLatestReleases()
        {
            UpdateChecker.LatestReleases latest = UpdateChecker.LatestReleases.GetLatestReleases(HTML);

            Assert.AreEqual(latest.html_url, "releases/tag/0.0.1", "A1");
            Assert.AreEqual(latest.tag_name, "0.0.1", "A2");
            Assert.AreEqual(latest.assets.Count, 1, "A3");

            UpdateChecker.LatestReleases.Assets assets = latest.assets[0];
            Assert.AreEqual(assets.name, "0.0.1.zip", "A4");
            Assert.AreEqual(assets.browser_download_url, "/releases/download/0.0.1/0.0.1.zip", "A5");
        }


        [TestMethod]
        public void TestComparerVersionTests_Filter()
        {
            string ver1 = "0.0.1";
            string ver2 = "版本0.0.1";
            string ver3 = "版本00.001.08";

            string result1 = UpdateChecker.VersionComparer.Filter(ver1);
            string result2 = UpdateChecker.VersionComparer.Filter(ver2);
            string result3 = UpdateChecker.VersionComparer.Filter(ver3);

            Assert.AreEqual(result1, "0.0.1");
            Assert.AreEqual(result2, "0.0.1");
            Assert.AreEqual(result3, "00.001.08");
        }


        [TestMethod]
        public void TestComparerVersionTests_CompareVersion()
        {
            string ver1 = "0.0.1";
            string ver2 = "版本0.0.1";
            string ver3 = "版本00.001.08";
            string ver4 = "0.0.2";
            string ver5 = "1.0.1";
            string ver6 = "2.1.6";
            string ver7 = "2.2.5";

            int result1 = UpdateChecker.VersionComparer.CompareVersion(ver4, ver1);
            int result2 = UpdateChecker.VersionComparer.CompareVersion(ver2, ver1);
            int result3 = UpdateChecker.VersionComparer.CompareVersion(ver3, ver2);
            int result4 = UpdateChecker.VersionComparer.CompareVersion(ver4, ver5);
            int result5 = UpdateChecker.VersionComparer.CompareVersion(ver7, ver6);


            Assert.IsTrue(result1 > 0);
            Assert.IsTrue(result2 == 0);
            Assert.IsTrue(result3 > 0);
            Assert.IsTrue(result4 < 0);
            Assert.IsTrue(result5 > 0);

        }

    }

}
