# Warn when there is a big PR
warn("Big PR") if git.lines_of_code > 500

# Notify ktlint warning
checkstyle_format.base_path = Dir.pwd
checkstyle_format.report 'app/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt'
