package com.maijz128.githubupdatechecker;


public class Main {

    public static void main(String[] args) {
        String userOrOrgName = "maijz128";
        String repoName = "github-update-checker";
        String currentVersion = "0.0.1";
        UpdateChecker checker = new UpdateChecker(userOrOrgName, repoName, currentVersion);
        checker.HasNewVersion((result)->{
            checker.OpenBrowserToReleases();
        });

        checker.CheckUpdate((latest) ->
        {
            System.out.println("Name: " + latest.name);
            System.out.println("Tag：" + latest.tag_name);
            System.out.println("说明：\n" + latest.body);

            int result = UpdateChecker.VersionComparer.CompareVersion(latest.tag_name, checker.CurrentVersion);
            if (result > 0) {
                System.out.println("有新版本。");
            } else if (result < 0) {
                System.out.println("当前版本为最新。");
            } else {
                System.out.println("没有新版本。");
            }
        });
    }
}
