#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
from src.update_checker import *


def test():
    user_org_name = "maijz128"
    repo_name = "github-update-checker"
    current_version = "0.0.1"
    checker = UpdateChecker(user_org_name, repo_name, current_version)

    def callback(result):
        if result:
            checker.open_browser2releases()

    checker.has_new_version(callback)

    def callback2(latest):
        print("Name: " + latest.name)
        print("Tag：" + latest.tag_name)
        print("说明：\n" + latest.body)

        result = VersionComparer.compare_version(latest.tag_name, checker.current_version)
        if result > 0:
            print("有新版本。")
        elif result < 0:
            print("当前版本为最新。")
        else:
            print("没有新版本。")

    checker.check_update2(callback2)


if __name__ == '__main__':
    test()
