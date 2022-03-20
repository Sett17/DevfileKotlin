# Devfile

Tired of the weird quirks of make? Annoyed of making typos in long chained commands, or getting to them via reverse-i-search?

Well, here is a solution that comes as just an executable for each platform and with an extensive help command.

## Install

To install the latest release on Linux (this downloads the latest linux executable from github and needs jq installed)
```bash
wget $(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.assets[] | select(.name|startswith("dev-linux")) | .browser_download_url') -O ~/.local/bin/dev && chmod +x ~/.local/bin/dev
chmod +x ~/.local/bin/dev
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
