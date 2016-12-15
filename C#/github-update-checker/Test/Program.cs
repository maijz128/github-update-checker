using GithubUpdateChecker;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Test
{
    class Program
    {
        static void Main(string[] args)
        {
            TestMyWebClient();
            Console.WriteLine();

            UpdateChecker checker = GetChecker();
            checker.CheckUpdate((latest) =>
            {
                Console.WriteLine("Name: " + latest.name);
                Console.WriteLine("Tag：" + latest.tag_name);
                Console.WriteLine("说明：\n" + latest.body);

                int result = UpdateChecker.VersionComparer.CompareVersion(latest.tag_name, checker.CurrentVersion);
                if (result > 0)
                {
                    Console.WriteLine("有新版本。");
                }
                else if (result < 0)
                {
                    Console.WriteLine("当前版本为最新。");
                }
                else
                {
                    Console.WriteLine("没有新版本。");
                }
            });

            Console.ReadKey();
        }

        static void TestMyWebClient()
        {
            UpdateChecker.IWebClient webclient = new UpdateChecker.MyWebClient();
            string url = "https://api.github.com/repos/maijz128/github-update-checker/releases/latest";

            webclient.DownloadHtml(url, (html) =>
            {
                Console.WriteLine(html);
            });
        }

        static UpdateChecker GetChecker()
        {
            string userOrOrgName = "maijz128";
            string repoName = "github-update-checker";
            string currentVersion = "0.0.1";
            return new UpdateChecker(userOrOrgName, repoName, currentVersion);
        }
    }
}
