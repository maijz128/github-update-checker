#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import json
import webbrowser
import urllib
import urllib.request
import threading


 ################################################################
 #      MaiJZ                                                   #
 #      20161209                                                #
 #      https://github.com/maijz128/github-update-checker       #
 #                                                              #
 ################################################################


class AbstractWebClient:
    # def callback(str)
    def download_html(self, url, callback):
        callback('{}')


class UpdateChecker:
    _UpdateURL = "https://api.github.com/repos/{USER_OR_ORG}/{REPO_NAME}/releases/latest"
    _ReleasesURL = "https://github.com/{USER_OR_ORG}/{REPO_NAME}/releases"

    def __init__(self, user_org_name, repo_name, current_version):
        self.web_client = MyWebClient()
        self.user_org_name = user_org_name
        self.repo_name = repo_name
        self.current_version = current_version

    # def callback(str, str)
    def check_update(self, callback):
        def _callback(html):
            latest = LatestReleases.get_latest_releases(html)
            callback(latest.tag_name, latest.body)

        self.web_client.download_html(self.get_update_url(), _callback)

    # def callback(latest)
    def check_update2(self, callback):
        def _callback(html):
            latest = LatestReleases.get_latest_releases(html)
            callback(latest)

        self.web_client.download_html(self.get_update_url(), _callback)

    # def callback(bool)
    def has_new_version(self, callback):
        def _callback(latest):
            result = VersionComparer.compare_version(latest.tag_name, self.current_version)
            callback(result > 0)

        self.check_update2(_callback)

    def open_browser2releases(self):
        url = UpdateChecker._ReleasesURL
        url = url.replace("{USER_OR_ORG}", self.user_org_name)
        url = url.replace("{REPO_NAME}", self.repo_name)
        webbrowser.open(url)

    def get_update_url(self):
        result = UpdateChecker._UpdateURL
        result = result.replace("{USER_OR_ORG}", self.user_org_name)
        result = result.replace("{REPO_NAME}", self.repo_name)
        return result


class VersionComparer:
    @staticmethod
    def compare_version(target, current):

        target_f = VersionComparer.filter(target)
        current_f = VersionComparer.filter(current)
        tsplit = target_f.split('.')
        csplit = current_f.split('.')
        tsplit_len = len(tsplit)
        csplit_len = len(csplit)

        max_len = (tsplit_len > csplit_len) and tsplit_len or csplit_len

        for i in range(max_len):
            tvalue = (i < tsplit_len) and int(tsplit[i]) or 0
            cvalue = (i < csplit_len) and int(csplit[i]) or 0

            if tvalue != cvalue:
                return tvalue - cvalue
        return 0

    @staticmethod
    def filter(version):
        result = ''
        for c in version:
            condition = (c >= '0') and (c <= '9')
            if condition or c == '.':
                result += c

        return result


class LatestReleases:
    def __init__(self):
        self.tag_name = None
        self.name = None
        self.html_url = None
        self.body = None
        self.assets = None
        self.tarball_url = None
        self.tarball_url = None

    @staticmethod
    def get_latest_releases(json_str):
        return json.loads(json_str, object_hook=LatestReleases.dict2latest)

    @staticmethod
    def dict2latest(d):
        result = LatestReleases()
        result.__dict__.update(d)
        return result


class MyWebClient(AbstractWebClient):
    def download_html(self, url, callback):
        def _download():
            callback(self.download(url))

        thread = threading.Thread(target=_download)
        thread.start()
        thread.join()

    def download(self, url):
        user_agent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) " \
                     "AppleWebKit/537.36 (KHTML, like Gecko) " \
                     "Chrome/54.0.2840.87 Safari/537.36"
        headers = {"User-Agent": user_agent}
        request = urllib.request.Request(url, headers=headers)

        for i in range(5):
            try:
                respone = urllib.request.urlopen(request)
                html = respone.read()
                return html.decode('utf-8')
            except Exception as e:
                print(e)

        return '{"name": "获取信息错误！"}'
