

```csharp
string userOrOrgName = "maijz128";
string repoName = "github-update-checker";
string currentVersion = "0.0.1";

UpdateChecker checker = new UpdateChecker(userOrOrgName, repoName, currentVersion);

checker.HasNewVersion(result =>{
    if(result){
        checker.OpenBrowserToReleases();
    }
});
```