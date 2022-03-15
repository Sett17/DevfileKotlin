# Devfile reference

## Overall Structure

```
***OPERATION_NAME*OPTION_LETTER...
script line 1
script line 2
script line N

***OPERATION_NAME*OPTION_LETTER...
script line 1
script line N

...
```

## Explanations

### OPERATION_NAME
  - MUST NOT include `*` character
  - SHOULD only include alphanumeric characters
  - SHOULD be all lowercase

### OPTION_LETTER
  - MUST be one of the letters under the `Operation Options` segment in the help output
  - MUST be preceded with `*`
  - CAN have a trailing `*`
  - CAN appear multiple times per operation

### Script
  - CAN have empty line/s at the end
  - SHOULD be a valid script file for the host operating system

## Example

```
***install*t*
echo -e '\e[32mInstalling Linux executable\e[0m'
URL=$(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.assets[] | select(.name=="dev.kexe") | .browser_download_url')
wget $URL -O ~/.local/bin/dev
chmod +x ~/.local/bin/dev
echo -e '\e[32mInstalling Windows executable\e[0m'
URL=$(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.assets[] | select(.name=="dev.exe") | .browser_download_url')
wget $URL -O /mnt/c/Users/sett/Documents/bins/dev.exe

***random*
curl https://ciprand.p3p.repl.co/api?len=20&count=10 | \
jq -r '.'
```

