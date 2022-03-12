# android-template

## setup

```bash
export PATH=$PATH:$(npm root)/.bin
export GOROOT=$HOME/sdk/go1.17.2
export PATH=$GOROOT/bin:$PATH
export GOBIN=$(pwd)/bin
export PATH=$GOBIN:$PATH
```

```bash
npm install
```

```bash
go install -v github.com/stormcat24/protodep@v0.1.6
```

```bash
npm-groovy-lint -f "**/*.gradle" --format
```

```bash
markdownlint -f README.md
```

```bash
protodep up -u
```
