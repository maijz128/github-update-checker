# GitHub Update Checker

检查 GitHub 上指定的仓库是否有新 Release

## C&#35;

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

## Java

```java
String userOrOrgName = "maijz128";
String repoName = "github-update-checker";
String currentVersion = "0.0.1";
UpdateChecker checker = new UpdateChecker(userOrOrgName, repoName, currentVersion);
checker.HasNewVersion((result)->{
    checker.OpenBrowserToReleases();
});
```

## JavaSctipt

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

# Python

```python
user_org_name = "maijz128"
repo_name = "github-update-checker"
current_version = "0.0.1"
checker = UpdateChecker(user_org_name, repo_name, current_version)

def callback(result):
    if result:
        checker.open_browser2releases()

checker.has_new_version(callback)
```


## GitHub API

### 订阅指定仓库的 Release 更新

```
https://github.com/{USER_OR_ORG}/{REPO_NAME}/releases.atom
```

示例：

```
https://github.com/maijz128/github-update-checker/releases.atom
```

[订阅](https://github.com/maijz128/github-update-checker/releases.atom)

### 获取指定仓库最新Release版本信息

```
https://api.github.com/repos/{USER_OR_ORG}/{REPO_NAME}/releases/latest
```
示例：

```
https://api.github.com/repos/maijz128/github-update-checker/releases/latest
```

[浏览JSON](https://api.github.com/repos/maijz128/github-update-checker/releases/latest)

## Demo

![image](https://github.com/maijz128/github-update-checker/raw/master/docs/images/Demo.gif)
