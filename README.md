<div align="center">

  <h1>Devfile</h1>
  
  <p>
    Tired of the weird quirks of make? Annoyed of making typos in long chained commands, or getting to them via reverse-i-search?
    Well, here is a solution that comes as just an executable for each platform and with an extensive help command.
  </p>

  
<!-- Badges -->
<p>
  <a href="https://github.com/Sett17/Devfile/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/Sett17/Devfile" alt="contributors" />
  </a>
  <a href="">
    <img src="https://img.shields.io/github/last-commit/Sett17/Devfile" alt="last update" />
  </a>
  <a href="https://github.com/Sett17/Devfile/network/members">
    <img src="https://img.shields.io/github/forks/Sett17/Devfile" alt="forks" />
  </a>
  <a href="https://github.com/Sett17/Devfile/stargazers">
    <img src="https://img.shields.io/github/stars/Sett17/Devfile" alt="stars" />
  </a>
  <a href="https://github.com/Sett17/Devfile/issues/">
    <img src="https://img.shields.io/github/issues/Sett17/Devfile" alt="open issues" />
  </a>
  <a href="https://github.com/Sett17/Devfile/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/Sett17/Devfile.svg" alt="license" />
  </a>
</p>
</div>

<br />

<!-- Table of Contents -->
# Table of Contents

- [Table of Contents](#table-of-contents)
  - [About the Project](#about-the-project)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Build](#build)
  - [Usage](#usage)
    - [Example](#example)
  - [Contributing](#contributing)
  - [License](#license)
  - [Acknowledgements](#acknowledgements)
  

<!-- About the Project -->
## About the Project

<div align="center"> 
  <img src="https://i.imgur.com/4SoNkxb.png" alt="screenshot" />
</div>

<!-- Getting Started -->
## Getting Started

<!-- Prerequisites -->
### Prerequisites

To install with just one command, `jq` is needed

```bash
sudo apt-get install jq
```

<!-- Installation -->
### Installation

Either run

```bash
wget $(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.assets[] | select(.name|startswith("dev-linux")) | .browser_download_url') -O ~/.local/bin/dev && chmod +x ~/.local/bin/dev
```

or download the latest release executable for your system [here](https://github.com/Sett17/Devfile/releases) and put it somewhere in your PATH.

<!-- Run Locally -->
### Build

Clone the project

```bash
  git clone https://github.com/Sett17/Devfile.git
```

Go to the project directory

```bash
  cd Devfile
```

Build native Executables

```bash
  gradle nativeBinaries
```

<!-- Usage -->
## Usage

To get started, either create a file called `Defile` or `dev.file`, or run
```bash
dev --init
```
which creates the file with an example operation.

_Note that the example operation only works on Linux executions_

To add new operation, you can use your favorite text editor, or run
```bash
dev -e
```
which will open the default editor (or vim as fallback).

### Example

Devfile is used in the development of Devfile itself. I use it to update the version and create a new release.

```
***version*t*+TAG
if [ -z "$DEV_TAG" ]; then
  echo -e '\e[31mYou need to supply a TAG argument'
  exit 1
fi
echo "val version = \"$DEV_TAG\"" > ./src/nativeMain/kotlin/version.kt

git add src/nativeMain/kotlin/version.kt
git commit -m "Bumped version to ${DEV_TAG}"
git push

***release|Creates release with supplied tag*t*+TAG
if [ -z "$DEV_TAG" ]; then
  echo -e '\e[31mYou need to supply a TAG argument'
  exit 1
fi

cmd.exe /c gradle nativeBinaries
cp ./build/bin/native/releaseExecutable/dev.exe dev-windows-${DEV_TAG}.exe

gradle nativeBinaries
cp ./build/bin/native/releaseExecutable/dev.kexe dev-linux-${DEV_TAG}

LAST_RELEASE=$(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.tag_name')

echo "https://github.com/Sett17/Devfile/compare/${LAST_RELEASE}...${DEV_TAG}"

gh release create ${DEV_TAG} -n "https://github.com/Sett17/Devfile/compare/${LAST_RELEASE}...${DEV_TAG}" dev-windows-${DEV_TAG}.exe dev-linux-${DEV_TAG}

rm dev-windows-${DEV_TAG}.exe dev-linux-${DEV_TAG}
```

With this Devfile, I can run `dev version+v0.0.69 release+v0.0.69` to bump the version number up and create a new release with linux and windows executables with just one command.

<!-- Contributing -->
## Contributing

<a href="https://github.com/Sett17/Devfile/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Sett17/Devfile" />
</a>


Contributions are always welcome!

There are already issues for enhancements, and everyone is welcome to create new ones, or work on some existing issues.


<!-- License -->
## License

Distributed under the GNU General Public License v3.0. See LICENSE for more information.


<!-- Acknowledgments -->
## Acknowledgements

Use this section to mention useful resources and libraries that you have used in your projects.

 - [clikt](https://github.com/ajalt/clikt/)
 - [mordant](https://github.com/ajalt/mordant)
 - [Awesome README](https://github.com/matiassingers/awesome-readme)