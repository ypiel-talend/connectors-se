#!/usr/bin/env bash

set -xe

# Generates the HTML documentation for the components
# $@: the extra parameters to be used in the maven command
main() {
  local extraBuildParams=("$@")

  local outputFolder=target/talend-component-kit_documentation/

  rm --recursive --force "${outputFolder}" || true
  mkdir --parents "${outputFolder}"

  local modules
  modules="$(
    readModulesFromPom \
      | grep 'content=' \
      | awk -F '=' '{print $2}' \
      | sort --unique \
      | grep --invert-match --extended-regexp 'common'
  )"

  mvn dependency:unpack@doc-html-theme asciidoctor:process-asciidoc@html \
    --batch-mode \
    --threads '1C' \
    --define "git.branch=${BRANCH_NAME:-master}" \
    --activate-profiles 'documentation-html' \
    --projects "$(tr '\n' ',' <<< "${modules}")" \
    "${extraBuildParams[@]}"

  local moduleBulletPoints
  moduleBulletPoints="$(
    while read -r module; do
      printf '      <li><a href="%s/documentation.html">%s</a></li>\n' "${module}" "${module}"
    done <<< "${modules}"
  )"

  printf '%s\n' \
    '<html>' \
    '  <head><title>Documentations</title></head>'           \
    '  <body>'                                               \
    '    <h1>Reports</h1>'                                   \
    '    <ul>'                                               \
    "${moduleBulletPoints}"                                  \
    '    </ul>'                                              \
    '  </body>'                                              \
    '</html>'                                                \
    > "${outputFolder}/index.html"
}

readModulesFromPom() {
  xmllint --shell pom.xml << __EOF
    setns ns=http://maven.apache.org/POM/4.0.0
    xpath /ns:project//ns:module/text()
__EOF
}

main "$@"
