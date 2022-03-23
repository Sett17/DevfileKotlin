# Devfile reference

## Overall Structure

```
***OPERATION_NAME|DESCRIPTION*OPTION_LETTER...*+ARGUMENT_NAME...
script line 1
script line 2
script line N

***OPERATION_NAME*OPTION_LETTER...*+ARGUMENT_NAME...
script line 1
{{ ARGUMENT_NAME }} line 2
${DEV_ARGUMENT_NAME:-default} line 3
script line N

...
```

## Explanations

### Declaration line
  - The declaration MUST be on one line
  - All `*` at the start of the line are trimmed

### OPERATION_NAME
  - MUST NOT include `*` character
  - SHOULD only include alphanumeric characters
  - SHOULD be all lowercase
  - CAN include ONE description
    - which MUST be seperated to the OPERTION_NAME by `|`
    - which SHOULD only include alphanumeric characters

### OPTION_LETTER
  - MUST be one of the letters under the `Operation Options` segment in the help output
  - MUST be preceded with `*`
  - SHOULD have a trailing `*`
  - CAN appear multiple times per operation

### ARGUMENT_NAME
  - MUST start with a single `+`
  - MUST have a corresponding placeholder in the script
  - SHOULD only include alphanumeric characters

  - the placeholder in the script MUST have at least 1 whitespace between the braces and the name
  - arguments are also places in the script as variables in the form of `DEV_VARIABLENAME` (all uppercase)

### Script
  - CAN have empty line/s at the end
  - SHOULD be a valid script file for the host operating system

## Example

```
***install|installs this thing*t*
echo -e '\e[32mInstalling Linux executable\e[0m'
URL=$(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.assets[] | select(.name=="dev.kexe") | .browser_download_url')
wget $URL -O ~/.local/bin/dev
chmod +x ~/.local/bin/dev
echo -e '\e[32mInstalling Windows executable\e[0m'
URL=$(curl -s https://api.github.com/repos/sett17/devfile/releases | jq -r 'first.assets[] | select(.name=="dev.exe") | .browser_download_url')
wget $URL -O /mnt/c/Users/sett/Documents/bins/dev.exe

***random*p*+COUNT*
curl https://ciprand.p3p.repl.co/api?len=20&count=${DEV_COUNT:-5} | \
jq -r '.'
```

