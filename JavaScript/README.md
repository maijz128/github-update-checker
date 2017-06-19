

```javascript
var userOrOrgName = "maijz128";
var repoName = "github-update-checker";
var currentVersion = "0.0.1";
var checker = UpdateChecker.createNew(userOrOrgName, repoName, currentVersion);
checker.hasNewVersion(function (result) {
   if(result) {
       checker.openBrowserToReleases();
   }
});
```