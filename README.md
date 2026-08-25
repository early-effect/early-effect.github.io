# earlyeffect.rocks

Org hub for [early-effect](https://github.com/early-effect) libraries.
The landing is an [Ascent](https://github.com/early-effect/ascent) SPA: typed CSS, five-act
story, live catalog. [Specular](https://github.com/early-effect/specular) SSRs a readable
fallback and the `metadata.json` allowlist in [`catalog-urls.txt`](catalog-urls.txt).

On load the Scala.js client fetches allowlisted manifests and mounts the page with
`AscentApp.mountBody`. **Library version bumps appear on refresh**; rebuild the hub when
you change the URL allowlist or hub chrome, not on every library tag.

## Brand mark

Header and hero art come from `early-effect-docs-theme`
(`EarlyEffectTheme.logoHref` / `heroImageHref`, written by `writeLogo`).

Local `images/` keeps hub-only rasters (favicon, etc.).

## Local build

```bash
sbt specularSite
# → target/site (includes assets/client.js from hubJS/spliceFull)
```

Production JS is `spliceFull` (Closure). `hubJS/spliceFast` is the quicker local iterate.

## Deploy

1. Enable **Settings → Pages → Source: GitHub Actions**
2. **Actions → Hub site → Run workflow** (manual)

Custom domain: `www.earlyeffect.rocks` (see `CNAME`).

## Adding a library

1. Ship docs via `early-effect/.github` → `specular-docs.yml` so
   `https://www.earlyeffect.rocks/<repo>/metadata.json` exists
2. Append that URL to `catalog-urls.txt`
3. Run **Hub site** once (deploys the updated allowlist)
4. Later releases of that library update the card on refresh without another hub rebuild

## Development

```bash
./scripts/install-git-hooks  # once per clone: pre-commit runs scalafmtCheckAll
```
