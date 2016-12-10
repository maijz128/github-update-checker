    user_org_name = "maijz128"
    repo_name = "github-update-checker"
    current_version = "0.0.1"
    checker = UpdateChecker(user_org_name, repo_name, current_version)

    def callback(result):
        if result:
            checker.open_browser2releases()

    checker.has_new_version(callback)