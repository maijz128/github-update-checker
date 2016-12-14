

//Test power by Mocha

var UpdateChecker = require('../src/update-checker.js');
var assert = require('assert');

describe('UpdateChecker', function () {

    var StubWebClient = {
        HTML: '{ '
        + ' "html_url": "releases/tag/0.0.1",'
        + ' "id": "123456",'
        + ' "tag_name": "0.0.1",'
        + ' "name": "版本0.0.1",'
        + ' "assets": ['
        + '    {'
        + '        "name": "0.0.1.zip",'
        + '      "size": "171",'
        + '       "download_count": "1",'
        + '       "created_at": "2016-12-09T06:49:37Z",'
        + '        "updated_at": "2016-12-09T06:49:38Z",'
        + '        "browser_download_url": "/releases/download/0.0.1/0.0.1.zip"'
        + '     }'
        + '  ],'
        + '  "tarball_url": "tarball/0.0.1",'
        + '  "zipball_url": "zipball/0.0.1",'
        + '  "body": "第一个版本"'
        + ' }',

        DownloadHtml: function (url, callback) {
            callback(StubWebClient.HTML);
        }
    };


    function getChecker() {
        var userOrOrgName = "maijz128";
        var repoName = "github-update-checker";
        var currentVersion = "0.0.1";
        var result = UpdateChecker.createNew(userOrOrgName, repoName, currentVersion);
        result.webClient = StubWebClient;
        return result;
    }

    describe('checkUpdate', function () {
        it('检查更新，返回版本tag，和信息', function () {
            var checker = getChecker();
            checker.checkUpdate(function (version, message) {
                assert.equal(version, "0.0.1");
                assert.equal(message, "第一个版本");
            });
        });
    });

    describe('checkUpdate2', function () {
        it('检查更新，返回版本Json对象', function () {
            var checker = getChecker();
            checker.checkUpdate2(function (latest) {
                assert.equal(latest.tag_name, "0.0.1");
                assert.equal(latest.body, "第一个版本");
            });
        });
    });

    describe('hasNewVersion', function () {
       it('是否有新版本', function () {
           var checker = getChecker();

           checker.currentVersion = "0.0.0.9";
           checker.hasNewVersion(function (result) {
              assert.ok(result);
           });

           checker.currentVersion = "1.0.0";
           checker.hasNewVersion(function (result) {
               assert.ok(!result);
           });
       }) ;
    });

    // describe('openBrowserToReleases', function () {
    //    it('打开项目Releases页面', function () {
    //        var checker = getChecker();
    //         checker.openBrowserToReleases();
    //    }) ;
    // });

    describe('getUpdateURL', function () {
        it('返回更新的API', function () {
            var checker = getChecker();
            var updateURL = "https://api.github.com/repos/maijz128/github-update-checker/releases/latest";

            var result = checker.getUpdateURL();

            assert.equal(result, updateURL);
        });
    });
});


describe('VersionComparer', function () {
    describe('filter', function () {
        it('过滤掉除了数字和点号之外字符', function () {
            var ver1 = "0.0.1";
            var ver2 = "版本0.0.1";
            var ver3 = "版本00.001.08";

            var result1 = UpdateChecker.VersionComparer.filter(ver1);
            var result2 = UpdateChecker.VersionComparer.filter(ver2);
            var result3 = UpdateChecker.VersionComparer.filter(ver3);

            assert.equal(result1, '0.0.1');
            assert.equal(result2, '0.0.1');
            assert.equal(result3, '00.001.08');

        });
    });

    describe('compareVersion', function () {
        it('比较两个版本，返回值负数为小于，零为等于，正数为大于', function () {
            var ver1 = "0.0.1";
            var ver2 = "版本0.0.1";
            var ver3 = "版本00.001.08";
            var ver4 = "0.0.2";
            var ver5 = "1.0.1";
            var ver6 = "2.01.6";
            var ver7 = "2.2.50";

            var result1 = UpdateChecker.VersionComparer.compareVersion(ver4, ver1);
            var result2 = UpdateChecker.VersionComparer.compareVersion(ver2, ver1);
            var result3 = UpdateChecker.VersionComparer.compareVersion(ver3, ver2);
            var result4 = UpdateChecker.VersionComparer.compareVersion(ver4, ver5);
            var result5 = UpdateChecker.VersionComparer.compareVersion(ver7, ver6);


            assert.ok(result1 > 0);
            assert.ok(result2 == 0);
            assert.ok(result3 > 0);
            assert.ok(result4 < 0);
            assert.ok(result5 > 0);
        });
    });
});

