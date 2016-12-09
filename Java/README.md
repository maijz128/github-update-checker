    String userOrOrgName = "maijz128";
    String repoName = "github-update-checker";
    String currentVersion = "0.0.1";
    UpdateChecker checker = new UpdateChecker(userOrOrgName, repoName, currentVersion);
    checker.HasNewVersion((result)->{
        checker.OpenBrowserToReleases();
    });