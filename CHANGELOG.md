# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [7.4.3 - 2022-03-07]
### Fixed
- Bugfix: Check if proxy is set for Jersey 3 client

## [7.4.2 - 2022-02-25]
### Fixed
- increased slf4j and jersey version in master

## [7.4.1 - 2022-02-25]
### Added
- Fix Proxy problems
- Refactoring in HttpConnector
- increased slf4j and jersey version

## [7.4.0 - 2022-01-03]
### Added
- Jersey 3

## [7.3.0 - 2021-09-27]
### Added
- HttpConnector now automatically enables proxyByPass for internal addresses then any "no proxy" host is set
- Support for Jersey 2.X. common-http can now generate both, Jersey 1.X and Jersey 2.X Clients based on the Apache HttpClient.
- Automated deploy to maven central for develop and master

## [7.2.0 - 2021-05-07]
### Added
- Following redirects can now be configured

## [7.1.1 - 2020-10-29]
### Changed
- Google Style Code

## [7.1.0 - 2020-10-29]
### Added
- Github Actions

## [7.0.0 - 2020-10-29]
- Github release
### Changed
- changed structure of pom.xml for maven central
- samply parent 11.1.0
## Added
- added plugins into pom.xml for maven central

## [6.1.0 - 2019-06-06]
### Added
- added timeout configuration

## [6.0.0 - 2019-04-08]
- removed some console output of exceptions
- fixed a hidden exception ignoring
- changed failed-but-retrying attempt to logger.debug instead console output
- new major due to method signature changes throwing exceptions now

## [5.0.0 - 2018-03-13]
### Changed
- samply parent 10.0 (Java 8)

## [4.0.0 - 2018-01-08]
### Added
- Add option to add custom default headers

### Changed

### Deprecated

### Removed
- Removed redundant throws clauses

### Fixed

### Security
