# Devfile

Tired of the weird quirks of make? Annoyed of making typos in long chained commands, or getting to them via reverse-i-search?

Well, here is a solution that comes as just an executable for each platform and with an extensive help command.

## Install

To install the latest release on Linux (this downloads the executable from github)
```bash
wget $(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.assets[] | select(.name=="dev.kexe") | .browser_download_url') -O ~/.local/bin/dev && chmod +x ~/.local/bin/dev
```
For this to work `~/.local/bin/` needs to be in your PATH

### Build
build with gradle >7.3
```bash
gradle nativeBinaries
```
the executable will be in `./build/bin/native/releaseExecutable/dev.kexe`

## How does this work?

A script file is created from the Script segment of the specified operation. This file is then executed.

# Planned Features

- [ ] Execute other operations from an operation
- [ ] support arguments with qutoes
- [ ] reuse script files if there are no changes
- [x] add timing option
- [x] pass arguments to ops
- [x] -e/--edit eager option to edit file with $editor
- [ ] scrolling region for commands (auto for long ones)
  - [ ] DSL for output?
- [x] info command
- [x] explanation support in Devfile
- [x] -c/--clean clean output for easy typing
