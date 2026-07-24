# earlyeffect.rocks

Org hub for [early-effect](https://github.com/early-effect) libraries.
Built with [specular](https://github.com/early-effect/specular) from published micro-site
`metadata.json` URLs listed in [`catalog-urls.txt`](catalog-urls.txt).

The hub SSRs optional first-paint cards, then an Ascent Scala.js client
(`LiveCatalog.bootstrap`) re-fetches allowlisted manifests on each page load. **Library
version bumps appear on refresh**; rebuild the hub when you change the URL allowlist (or
hub chrome), not on every library tag.

## Brand mark

Header and hero art come from `early-effect-docs-theme`
(`EarlyEffectTheme.logoHref` / `heroImageHref`, written by `writeLogo`).

Local `images/` keeps hub-only rasters (favicon, etc.).

## Local build

```bash
sbt specularSite
# → target/site (includes assets/client.js)
```

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
