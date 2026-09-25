# Releasing Protégé 6

This guide describes how maintainers publish the Protégé 6 Maven artifacts
and create the corresponding GitHub release. The downloadable desktop
applications are assembled and released separately from the
[`protege-distribution`](https://github.com/protegeproject/protege-distribution)
repository.

## How publication is triggered

Release publication is triggered automatically by pushing a Protégé 6 version
tag, such as `6.0.0-alpha-1`. The version in `pom.xml` must exactly match the
tag. If it does not, the publishing workflow stops without publishing anything.

The workflow does not change the project version. This ensures that the tagged
source is the exact source from which the Maven artifacts were built.

The workflow can also be run manually to publish a snapshot. Manual runs only
accept versions ending in `-SNAPSHOT`; they cannot publish a release.

## Prerequisites

Before making a release:

- Confirm that the intended release version has not already been published.
- Ensure the `protege-6` branch is passing CI.

## 1. Prepare the release version

Create a branch from the latest `protege-6` branch. For example:

```bash
git switch protege-6
git pull --ff-only
git switch -c protege6/release-6.0.0-alpha-1
```

The `--ff-only` option stops if the local and remote branches have different
commits, instead of creating an unexpected merge commit. This helps ensure the
release work starts from the exact `protege-6` branch currently on GitHub.

Set the release version in all Maven modules:

```bash
mvn --batch-mode versions:set \
  -DnewVersion=6.0.0-alpha-1 \
  -DgenerateBackupPoms=false
```

Review the changes made to the POM files:

```bash
git diff
```

Confirm that these are only the intended version changes. The release version
must not end in `-SNAPSHOT`. This quick check also ensures that no unrelated
local changes are included in the release commit.

Run the complete build to check that all modules compile and all tests pass.
The release profile also checks that the source and Javadoc artifacts can be
created without requiring the artifacts to be signed locally:

```bash
mvn --batch-mode --activate-profiles release \
  --define release.signing.disabled=true clean verify
```

## 2. Open and merge the release-version pull request

Commit the version changes and push the release branch:

```bash
git add pom.xml */pom.xml
git commit -m "Set version to 6.0.0-alpha-1"
git push --set-upstream origin protege6/release-6.0.0-alpha-1
```

Open a pull request from the release branch into `protege-6`. **Check the pull
request diff, wait for all CI checks to pass, and then merge it.**

## 3. Tag the release

Update the local `protege-6` branch after the release-version pull request has
been merged:

```bash
git switch protege-6
git pull --ff-only
```

Confirm that the Maven project version is the intended release version:

```bash
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```

Create a signed, annotated tag on that commit and push it. **The tag name must
be exactly the same string as the version in `pom.xml`.** For example, a POM
version of `6.0.0-alpha-1` requires the tag `6.0.0-alpha-1`. Do not prefix the
tag with `v`.

```bash
git tag -s 6.0.0-alpha-1 -m "Release Protégé 6.0.0-alpha-1"
git push origin 6.0.0-alpha-1
```

Pushing the tag automatically starts the `Publish packages to the Maven Central
Repository` workflow. Do not start the workflow manually for a release: manual
runs are reserved for snapshots.

## 4. Verify publication

Follow the publishing job in GitHub Actions. The workflow checks the tag and
project version, builds the project, creates source and Javadoc JARs, signs the
artifacts, and publishes them through the Maven Central Portal.

Wait until the workflow succeeds and confirm that the new version is available
from Maven Central before announcing the release.

## 5. Create the GitHub prerelease

After Maven Central publication succeeds, create a GitHub release using the
existing tag. Mark alpha, beta, and release-candidate versions as prereleases.
Do not create a new tag from the GitHub release form.

The GitHub release represents the core Protégé release. Build and publish the
downloadable desktop applications from the `protege-distribution` repository.

## 6. Start the next development version

Create another branch from `protege-6` for the next development version. For
example:

```bash
git switch protege-6
git pull --ff-only
git switch -c protege6/start-6.0.0-alpha-2-development
```

Set the next snapshot version:

```bash
mvn --batch-mode versions:set \
  -DnewVersion=6.0.0-alpha-2-SNAPSHOT \
  -DgenerateBackupPoms=false
```

Commit this change and merge it into `protege-6` through a pull request.

## Publishing a snapshot

To publish the current snapshot:

1. Open the `Publish packages to the Maven Central Repository` workflow in
   GitHub Actions.
2. Select **Run workflow**.
3. Select the branch containing the snapshot version, normally `protege-6`.
4. Start the workflow and wait for it to complete.

The selected branch must contain a version ending in `-SNAPSHOT`. If it does
not, the workflow fails and nothing is published.

## If publication fails

- If the failure is temporary, rerun the failed tag-triggered workflow.
- If validation fails, check that the tag and `pom.xml` contain exactly the
  same version.
- Never move or reuse a tag for a version that has been published. Maven
  Central will not replace the existing artifacts, and the moved tag would no
  longer identify the source code from which those artifacts were built.
- Maven Central versions cannot be overwritten. If any artifacts for the
  version were published, prepare a new version instead.
