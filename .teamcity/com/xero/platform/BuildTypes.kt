package com.xero.platform

import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

open class BaseBuildType(block: BuildType.() -> Unit) : BuildType({
    name = "Base build type"

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
        }
    }

    features {
        feature {
            type = "teamcity.github.status"
            param("guthub_owner", "xero-jung")
            param("guthub_authentication_type", "token")
            param("guthub_repo", "TestRepo")
            param("github_report_on", "on start and finish")
            param("secure:github_access_token", "credentialsJSON:4a5cdcd3-38f8-4648-bc46-53a31388855f")
        }
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_200"
            }
        }
    }

    block()
})

object PullRequestBuildType : BaseBuildType({
    name = "Pull Request Build"

    features {
        pullRequests {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:4a5cdcd3-38f8-4648-bc46-53a31388855f"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }

    steps {
        gradle {
            name = "Run help task"
            useGradleWrapper = true
            dockerImage = "mobile-docker-common.artifactory.xero-support.com/android-build-agent:0.0.3.5"
            tasks = "help"
            dockerPull = true
        }
    }
})