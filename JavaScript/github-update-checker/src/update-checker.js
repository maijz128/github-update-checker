/****************************************************************
 *      MaiJZ                                                   *
 *      20161214                                                *
 *      https://github.com/maijz128/github-update-checker       *
 *                                                              *
 ****************************************************************/

var UpdateChecker = {

    UPDATE_URL: "https://api.github.com/repos/{USER_OR_ORG}/{REPO_NAME}/releases/latest",
    RELEASES_URL: "https://github.com/{USER_OR_ORG}/{REPO_NAME}/releases",

    createNew: function (userOrOrgName, repoName, currentVersion) {
        var checker = {};
        checker.webClient = UpdateChecker.MyWebClient;
        checker.userOrOrgName = userOrOrgName;
        checker.repoName = repoName;
        checker.currentVersion = currentVersion;

        //callback(string, string)
        checker.checkUpdate = function (callback) {
            checker.webClient.DownloadHtml(checker.getUpdateURL(), function (html) {
                var latest = checker.getLatestReleases(html);
                callback(latest.tag_name, latest.body);
            });
        };

        //callback(latest)
        checker.checkUpdate2 = function (callback) {
            checker.webClient.DownloadHtml(checker.getUpdateURL(), function (html) {
                var latest = checker.getLatestReleases(html);
                callback(latest);
            });
        };

        //callback(bool)
        checker.hasNewVersion = function (callback) {
            checker.checkUpdate2(function (latest) {
                var result = UpdateChecker.VersionComparer.compareVersion(latest.tag_name, checker.currentVersion);
                callback(result > 0);
            })
        };

        checker.openBrowserToReleases = function () {
            var url = UpdateChecker.RELEASES_URL;
            url = url.replace("{USER_OR_ORG}", checker.userOrOrgName);
            url = url.replace("{REPO_NAME}", checker.repoName);
            window.open(url);
        };

        checker.getUpdateURL = function () {
            var result = UpdateChecker.UPDATE_URL;
            result = result.replace("{USER_OR_ORG}", checker.userOrOrgName);
            result = result.replace("{REPO_NAME}", checker.repoName);
            return result;
        };

        checker.getLatestReleases = function (html) {
            return JSON.parse(html);
        };

        return checker;
    },

    MyWebClient: {
        //callback(string)
        DownloadHtml: function (url, callback) {
            var xmlhttp = new XMLHttpRequest();
            xmlhttp.onreadystatechange = function () {
                if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                    callback(xmlhttp.responseText);
                }
            };
            xmlhttp.open('GET', url, false);
            xmlhttp.send();
        }
    },

    VersionComparer: {

        compareVersion: function (target, current) {
            var target_f = this.filter(target);
            var current_f = this.filter(current);
            var tsplit = target_f.split('.');
            var csplit = current_f.split('.');
            var tsplit_len = tsplit.length;
            var csplit_len = csplit.length;
            var len = (tsplit_len > csplit_len) ? tsplit_len : csplit_len;

            for (var i = 0; i < len; i++) {
                var tvalue = 0;
                var cvalue = 0;

                if (i < tsplit_len) {
                    tvalue = parseInt(tsplit[i]);
                }

                if (i < csplit_len) {
                    cvalue = parseInt(csplit[i]);
                }

                if (tvalue != cvalue) {
                    return tvalue - cvalue;
                }
            }
            return 0;
        }
        ,

        filter: function (version) {
            var result = '';
            for (var i = 0; i < version.length; i++) {
                var c = version.charAt(i);
                var condition = c >= '0' && c <= '9';
                if (condition || c == '.') {
                    result += c;
                }
            }
            return result;
        }
    }
};

module.exports = UpdateChecker;

