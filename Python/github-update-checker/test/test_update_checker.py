#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import unittest
from src.update_checker import *

json_str = '{ ' \
           + ' "html_url": "releases/tag/0.0.1",' \
           + ' "id": "123456",' \
           + ' "tag_name": "0.0.1",' \
           + ' "name": "版本0.0.1",' \
           + ' "assets": [' \
           + '    {' \
           + '        "name": "0.0.1.zip",' \
           + '      "size": "171",' \
           + '       "download_count": "1",' \
           + '       "created_at": "2016-12-09T06:49:37Z",' \
           + '        "updated_at": "2016-12-09T06:49:38Z",' \
           + '        "browser_download_url": "/releases/download/0.0.1/0.0.1.zip"' \
           + '     }' \
           + '  ],' \
           + '  "tarball_url": "tarball/0.0.1",' \
           + '  "zipball_url": "zipball/0.0.1",' \
           + '  "body": "第一个版本"' \
           + ' }'


class StubWebClient(AbstractWebClient):
    def __init__(self):
        self.download_html_result = json_str

    def download_html(self, url, callback):
        callback(self.download_html_result)


class TestUpdateChecker(unittest.TestCase):
    def get_checker(self):
        user_org_name = "maijz128"
        repo_name = "github-update-checker"
        current_version = "0.0.1"
        result = UpdateChecker(user_org_name, repo_name, current_version)

        result.web_client = StubWebClient()

        return result

    def test_check_update(self):
        checker = self.get_checker()

        def callback(version, message):
            self.assertEqual(version, "0.0.1")
            self.assertEqual(message, "第一个版本")

        checker.check_update(callback)

    def test_check_update2(self):
        checker = self.get_checker()

        def callback(latest):
            self.assertEqual(latest.tag_name, "0.0.1")
            self.assertEqual(latest.body, "第一个版本")

        checker.check_update2(callback)

    def test_has_new_version(self):
        checker = self.get_checker()

        def callback1(result):
            self.assertTrue(result)

        checker.current_version = "0.0.0.9"
        checker.has_new_version(callback1)

        def callback2(result):
            self.assertFalse(result)

        checker.current_version = "1.0.0"
        checker.has_new_version(callback2)

    def test_get_update_url(self):
        checker = self.get_checker()
        update_url = "https://api.github.com/repos/maijz128/github-update-checker/releases/latest"

        result = checker.get_update_url()

        self.assertEqual(result, update_url)

    def test_open_browser2releases(self):
        # checker = self.get_checker()
        #
        # checker.open_browser2releases()
        pass


class TestVersionComparer(unittest.TestCase):
    def test_filter(self):
        ver1 = "0.0.1"
        ver2 = "版本0.0.1"
        ver3 = "版本00.001.08"

        result1 = VersionComparer.filter(ver1)
        result2 = VersionComparer.filter(ver2)
        result3 = VersionComparer.filter(ver3)

        self.assertEqual(result1, "0.0.1")
        self.assertEqual(result2, "0.0.1")
        self.assertEqual(result3, "00.001.08")

    def test_compare_version(self):
        ver1 = "0.0.1"
        ver2 = "版本0.0.1"
        ver3 = "版本00.001.08"
        ver4 = "0.0.2"
        ver5 = "1.0.1"
        ver6 = "2.1.6"
        ver7 = "2.2.5"

        result1 = VersionComparer.compare_version(ver4, ver1)
        result2 = VersionComparer.compare_version(ver2, ver1)
        result3 = VersionComparer.compare_version(ver3, ver2)
        result4 = VersionComparer.compare_version(ver4, ver5)
        result5 = VersionComparer.compare_version(ver7, ver6)

        self.assertTrue(result1 > 0)
        self.assertTrue(result2 == 0)
        self.assertTrue(result3 > 0)
        self.assertTrue(result4 < 0)
        self.assertTrue(result5 > 0)


class TestLatestReleases(unittest.TestCase):
    def test_get_latest_releases(self):
        latest = LatestReleases.get_latest_releases(json_str)

        self.assertEqual(latest.html_url, "releases/tag/0.0.1")
        self.assertEqual(latest.tag_name, "0.0.1")
