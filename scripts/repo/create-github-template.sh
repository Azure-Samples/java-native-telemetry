#!/usr/bin/env bash
##############################################################################
# Usage: ./create-github-template.sh [--local]
# Creates the project template and push it to GitHub.
##############################################################################

set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
cd ../..

GITHUB_REPOSITORY=${GITHUB_REPOSITORY:-}
TEMPLATE_HOME=/tmp/moaw-template

if [[ -z "${GITHUB_REPOSITORY}" ]]; then
  TEMPLATE_REPO=$(git remote get-url origin)
else
  TEMPLATE_REPO=https://x-access-token:${GITHUB_TOKEN}@github.com/${GITHUB_REPOSITORY}.git
fi

echo "Preparing GitHub project template..."
echo "(temp folder: $TEMPLATE_HOME)"
rm -rf "$TEMPLATE_HOME"

# Clone the template repo and start from the base branch to keep
# the contributors history in the main branch we'll overwrite
git clone "$TEMPLATE_REPO" "$TEMPLATE_HOME"
pushd "$TEMPLATE_HOME"
git reset --hard origin/base
git checkout -b main
popd

find . -type d -not -path '*node_modules*' -not -path '*.git/*' -not -path '*/dist' -not -path '*dist/*' -exec mkdir -p '{}' "$TEMPLATE_HOME/{}" ';'
find . -type f -not -path '*node_modules*' -not -path '*.git/*' -not -path '*dist/*' -not -path '*/.DS_Store' -exec cp -r '{}' "$TEMPLATE_HOME/{}" ';'
cd "$TEMPLATE_HOME"

##############################################################################
# TODO: Prepare the project template
##############################################################################

# Remove unnecessary files
rm -rf node_modules
rm -rf .github
rm -rf TODO
rm -rf package-lock.json
rm -rf scripts/repo
rm -rf docs
rm -rf .azure
rm -rf .env
rm -rf ./*.env

# Prepare files
echo -e "console.log('hello world!')" > index.js

##############################################################################

# Prepare the commit
git add .
git commit -m "chore: prepare project template"

if [[ ${1-} == "--local" ]] || [[ -z "${GITHUB_REPOSITORY}" ]]; then
  echo "Local mode: skipping GitHub push."
  open "$TEMPLATE_HOME"
else
  # Update git repo
  git push -u origin main --force

  rm -rf "$TEMPLATE_HOME"
fi

echo "Successfully updated project template."
