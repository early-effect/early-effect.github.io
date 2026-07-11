# earlyeffect.rocks

Org hub for [early-effect](https://github.com/early-effect) libraries.
Built with [specular](https://github.com/early-effect/specular) from published micro-site
`metadata.json` URLs listed in [`catalog-urls.txt`](catalog-urls.txt).

## Local build

```bash
sbt specularSite
# → target/site
```

## Deploy

1. Enable **Settings → Pages → Source: GitHub Actions**
2. **Actions → Hub site → Run workflow** (manual)

Custom domain: `www.earlyeffect.rocks` (see `CNAME`).

## Adding a library

1. Ship docs via `early-effect/.github` → `specular-docs.yml` so
   `https://early-effect.github.io/<repo>/metadata.json` exists
2. Append that URL to `catalog-urls.txt`
3. Run **Hub site**
